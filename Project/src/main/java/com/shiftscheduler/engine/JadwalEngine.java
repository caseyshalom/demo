package com.shiftscheduler.engine;

import com.shiftscheduler.dao.CutiDAO;
import com.shiftscheduler.dao.JadwalDAO;
import com.shiftscheduler.model.JadwalHasil;
import com.shiftscheduler.model.Karyawan;
import com.shiftscheduler.model.Shift;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Mesin Generator Jadwal Otomatis menggunakan algoritma Backtracking (CSP).
 * 
 * Constraint Satisfaction Problem (CSP):
 * - Variabel: Setiap slot (tanggal × shift × posisi_kuota)
 * - Domain: Daftar karyawan yang tersedia
 * - Constraints:
 *   1. Karyawan tidak sedang cuti pada tanggal tersebut
 *   2. Karyawan belum dijadwalkan pada tanggal tersebut (UNIQUE)
 *   3. Karyawan belum melebihi batas_shift_mingguan
 * 
 * Optimasi:
 * - Karyawan diurutkan berdasarkan jumlah shift yang sudah dijadwalkan (ascending)
 *   untuk distribusi jam kerja yang adil (Least Constraining Value heuristic)
 * - Forward Checking: Skip karyawan yang sudah pasti melanggar constraint
 */
public class JadwalEngine {

    private final List<Karyawan> daftarKaryawan;
    private final List<Shift> daftarShift;
    private final CutiDAO cutiDAO;
    private final JadwalDAO jadwalDAO;

    // Cache cuti per tanggal untuk menghindari query berulang
    private final Map<LocalDate, Set<Integer>> cacheCuti;

    // Tracking jumlah shift per karyawan per minggu
    // Key: "idKaryawan-mingguKe" → Value: jumlah shift
    private final Map<String, Integer> shiftCountPerWeek;

    // Tracking karyawan yang sudah dijadwalkan per tanggal
    // Key: tanggal → Value: Set ID karyawan
    private final Map<LocalDate, Set<Integer>> assignedPerDay;

    // Hasil jadwal yang dihasilkan
    private final List<JadwalHasil> hasilJadwal;

    // Callback untuk melaporkan progress ke UI
    private EngineCallback callback;

    // Total slot yang harus diisi (untuk kalkulasi progress)
    private int totalSlots;
    private int filledSlots;

    /**
     * Constructor JadwalEngine.
     * 
     * @param daftarKaryawan daftar semua karyawan yang tersedia
     * @param daftarShift daftar semua shift yang harus diisi
     */
    public JadwalEngine(List<Karyawan> daftarKaryawan, List<Shift> daftarShift) {
        this.daftarKaryawan = daftarKaryawan;
        this.daftarShift = daftarShift;
        this.cutiDAO = new CutiDAO();
        this.jadwalDAO = new JadwalDAO();
        this.cacheCuti = new HashMap<>();
        this.shiftCountPerWeek = new HashMap<>();
        this.assignedPerDay = new HashMap<>();
        this.hasilJadwal = new ArrayList<>();
    }

    /**
     * Mengatur callback untuk komunikasi progress ke UI.
     * 
     * @param callback implementasi EngineCallback
     */
    public void setCallback(EngineCallback callback) {
        this.callback = callback;
    }

    /**
     * Mengeksekusi algoritma backtracking untuk menghasilkan jadwal.
     * 
     * @param tanggalMulai tanggal awal jadwal (inclusive)
     * @param tanggalSelesai tanggal akhir jadwal (inclusive)
     * @return List jadwal yang dihasilkan, atau empty list jika infeasible
     */
    public List<JadwalHasil> generate(LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        hasilJadwal.clear();
        cacheCuti.clear();
        shiftCountPerWeek.clear();
        assignedPerDay.clear();
        filledSlots = 0;

        // Pre-load: Cache data cuti untuk seluruh rentang tanggal
        try {
            preloadCutiCache(tanggalMulai, tanggalSelesai);
        } catch (SQLException e) {
            reportFailed("Gagal memuat data cuti: " + e.getMessage());
            return Collections.emptyList();
        }

        // Pre-load: Hitung shift yang sudah ada di database untuk minggu-minggu terkait
        try {
            preloadExistingShiftCounts(tanggalMulai, tanggalSelesai);
        } catch (SQLException e) {
            reportFailed("Gagal memuat data jadwal existing: " + e.getMessage());
            return Collections.emptyList();
        }

        // Bangun daftar tanggal
        List<LocalDate> daftarTanggal = new ArrayList<>();
        LocalDate current = tanggalMulai;
        while (!current.isAfter(tanggalSelesai)) {
            daftarTanggal.add(current);
            current = current.plusDays(1);
        }

        // Hitung total slot
        totalSlots = 0;
        for (Shift shift : daftarShift) {
            totalSlots += shift.getKuotaKebutuhan();
        }
        totalSlots *= daftarTanggal.size();

        reportProgress(0, "Memulai penjadwalan untuk " + daftarTanggal.size() + " hari...");

        // Mulai backtracking dari hari pertama
        boolean berhasil = backtrack(daftarTanggal, 0);

        if (berhasil) {
            reportProgress(100, "Penjadwalan selesai! Total " + hasilJadwal.size() + " jadwal dibuat.");
            return hasilJadwal;
        } else {
            reportFailed("Tidak dapat menemukan solusi penjadwalan yang valid. " +
                         "Kemungkinan penyebab: terlalu banyak karyawan cuti atau kuota shift melebihi jumlah karyawan tersedia.");
            return Collections.emptyList();
        }
    }

    /**
     * Algoritma Backtracking rekursif.
     * Iterasi: Tanggal → Shift → Slot (kuota) → Pilih Karyawan
     * 
     * @param daftarTanggal daftar semua tanggal yang harus dijadwalkan
     * @param indexTanggal index tanggal saat ini dalam daftarTanggal
     * @return true jika solusi ditemukan
     */
    private boolean backtrack(List<LocalDate> daftarTanggal, int indexTanggal) {
        // Base case: Semua tanggal sudah dijadwalkan
        if (indexTanggal >= daftarTanggal.size()) {
            return true;
        }

        LocalDate tanggal = daftarTanggal.get(indexTanggal);
        assignedPerDay.putIfAbsent(tanggal, new HashSet<>());

        // Untuk setiap shift pada tanggal ini
        return backtrackShift(daftarTanggal, indexTanggal, tanggal, 0);
    }

    /**
     * Backtracking level shift: iterasi setiap shift dalam satu tanggal.
     */
    private boolean backtrackShift(List<LocalDate> daftarTanggal, int indexTanggal,
                                    LocalDate tanggal, int indexShift) {
        // Base case: Semua shift pada tanggal ini sudah diisi
        if (indexShift >= daftarShift.size()) {
            // Lanjut ke tanggal berikutnya
            return backtrack(daftarTanggal, indexTanggal + 1);
        }

        Shift shift = daftarShift.get(indexShift);
        int kuota = shift.getKuotaKebutuhan();

        // Isi slot sebanyak kuota untuk shift ini
        return backtrackSlot(daftarTanggal, indexTanggal, tanggal, indexShift, shift, 0, kuota);
    }

    /**
     * Backtracking level slot: mengisi setiap posisi kuota dalam satu shift.
     */
    private boolean backtrackSlot(List<LocalDate> daftarTanggal, int indexTanggal,
                                   LocalDate tanggal, int indexShift, Shift shift,
                                   int slotTerisi, int kuota) {
        // Base case: Semua slot untuk shift ini sudah terisi
        if (slotTerisi >= kuota) {
            // Lanjut ke shift berikutnya
            return backtrackShift(daftarTanggal, indexTanggal, tanggal, indexShift + 1);
        }

        // Dapatkan karyawan yang tersedia, diurutkan berdasarkan beban kerja (ascending)
        List<Karyawan> kandidat = getKandidatTersedia(tanggal);

        // Forward Checking: Jika tidak ada kandidat, backtrack
        if (kandidat.isEmpty()) {
            return false;
        }

        // Coba setiap kandidat
        for (Karyawan karyawan : kandidat) {
            // ASSIGN: Tempatkan karyawan pada slot ini
            assign(tanggal, karyawan, shift);

            // Rekursi: coba isi slot berikutnya
            if (backtrackSlot(daftarTanggal, indexTanggal, tanggal, indexShift, shift,
                              slotTerisi + 1, kuota)) {
                return true;
            }

            // BACKTRACK: Batalkan assignment, coba karyawan lain
            unassign(tanggal, karyawan, shift);
        }

        // Tidak ada kandidat yang cocok → backtrack ke level atas
        return false;
    }

    /**
     * Mendapatkan daftar karyawan yang tersedia untuk tanggal tertentu.
     * Menerapkan filtering constraint dan sorting berdasarkan beban kerja.
     * 
     * @param tanggal tanggal yang akan dicek
     * @return List karyawan yang memenuhi semua constraint, terurut ascending berdasarkan beban
     */
    private List<Karyawan> getKandidatTersedia(LocalDate tanggal) {
        List<Karyawan> kandidat = new ArrayList<>();
        Set<Integer> cutiIds = cacheCuti.getOrDefault(tanggal, Collections.emptySet());
        Set<Integer> assignedIds = assignedPerDay.getOrDefault(tanggal, Collections.emptySet());

        for (Karyawan k : daftarKaryawan) {
            // Constraint 1: Karyawan tidak sedang cuti
            if (cutiIds.contains(k.getId())) continue;

            // Constraint 2: Karyawan belum dijadwalkan pada tanggal ini
            if (assignedIds.contains(k.getId())) continue;

            // Constraint 3: Karyawan belum melebihi batas shift mingguan
            String weekKey = getWeekKey(k.getId(), tanggal);
            int currentCount = shiftCountPerWeek.getOrDefault(weekKey, 0);
            if (currentCount >= k.getBatasShiftMingguan()) continue;

            kandidat.add(k);
        }

        // Optimasi: Urutkan berdasarkan jumlah shift yang sudah dijadwalkan (ascending)
        // → Prioritaskan karyawan dengan beban paling ringan (distribusi adil)
        kandidat.sort((a, b) -> {
            int countA = shiftCountPerWeek.getOrDefault(getWeekKey(a.getId(), tanggal), 0);
            int countB = shiftCountPerWeek.getOrDefault(getWeekKey(b.getId(), tanggal), 0);
            return Integer.compare(countA, countB);
        });

        return kandidat;
    }

    /**
     * ASSIGN: Menempatkan karyawan pada slot jadwal.
     * Update semua tracking data structure.
     */
    private void assign(LocalDate tanggal, Karyawan karyawan, Shift shift) {
        // Tambah ke hasil jadwal
        JadwalHasil jadwal = new JadwalHasil(tanggal, karyawan.getId(), shift.getId());
        hasilJadwal.add(jadwal);

        // Update tracking: karyawan sudah dijadwalkan pada tanggal ini
        assignedPerDay.get(tanggal).add(karyawan.getId());

        // Update tracking: increment jumlah shift mingguan
        String weekKey = getWeekKey(karyawan.getId(), tanggal);
        shiftCountPerWeek.merge(weekKey, 1, Integer::sum);

        // Update progress
        filledSlots++;
        if (totalSlots > 0) {
            int persen = (int) ((filledSlots * 100.0) / totalSlots);
            reportProgress(Math.min(persen, 99),
                "Menjadwalkan " + karyawan.getNama() + " → " + shift.getKodeShift() + " pada " + tanggal);
        }
    }

    /**
     * UNASSIGN (Backtrack): Membatalkan penempatan karyawan.
     * Rollback semua tracking data structure.
     */
    private void unassign(LocalDate tanggal, Karyawan karyawan, Shift shift) {
        // Hapus dari hasil jadwal (item terakhir yang ditambahkan)
        if (!hasilJadwal.isEmpty()) {
            hasilJadwal.remove(hasilJadwal.size() - 1);
        }

        // Rollback tracking
        assignedPerDay.get(tanggal).remove(karyawan.getId());

        String weekKey = getWeekKey(karyawan.getId(), tanggal);
        shiftCountPerWeek.merge(weekKey, -1, Integer::sum);

        filledSlots--;
    }

    /**
     * Pre-load cache cuti untuk seluruh rentang tanggal.
     * Menghindari query database berulang kali saat backtracking.
     */
    private void preloadCutiCache(LocalDate mulai, LocalDate selesai) throws SQLException {
        LocalDate current = mulai;
        while (!current.isAfter(selesai)) {
            List<Integer> ids = cutiDAO.getIdKaryawanCutiPadaTanggal(current);
            cacheCuti.put(current, new HashSet<>(ids));
            current = current.plusDays(1);
        }
    }

    /**
     * Pre-load jumlah shift existing dari database.
     * Ini diperlukan jika ada jadwal yang sudah dibuat sebelumnya.
     */
    private void preloadExistingShiftCounts(LocalDate mulai, LocalDate selesai) throws SQLException {
        for (Karyawan k : daftarKaryawan) {
            LocalDate current = mulai;
            Set<String> processedWeeks = new HashSet<>();
            while (!current.isAfter(selesai)) {
                String weekKey = getWeekKey(k.getId(), current);
                if (!processedWeeks.contains(weekKey)) {
                    int count = jadwalDAO.countShiftMingguIni(k.getId(), current);
                    if (count > 0) {
                        shiftCountPerWeek.put(weekKey, count);
                    }
                    processedWeeks.add(weekKey);
                }
                current = current.plusDays(1);
            }
        }
    }

    /**
     * Membuat key unik untuk tracking shift per minggu.
     * Format: "idKaryawan-tahun-mingguKe"
     */
    private String getWeekKey(int idKaryawan, LocalDate tanggal) {
        // Hitung awal minggu (Senin)
        LocalDate senin = tanggal.minusDays(tanggal.getDayOfWeek().getValue() - 1);
        return idKaryawan + "-" + senin.toString();
    }

    // ==================== CALLBACK METHODS ====================

    private void reportProgress(int persen, String pesan) {
        if (callback != null) {
            callback.onProgress(persen, pesan);
        }
    }

    private void reportFailed(String alasan) {
        if (callback != null) {
            callback.onFailed(alasan);
        }
    }

    // ==================== CALLBACK INTERFACE ====================

    /**
     * Interface callback untuk komunikasi antara JadwalEngine dan UI.
     * Diimplementasikan oleh JadwalPanel untuk menerima update progress.
     */
    public interface EngineCallback {
        /** Dipanggil saat ada progres penjadwalan */
        void onProgress(int persen, String pesan);

        /** Dipanggil saat penjadwalan berhasil selesai */
        void onComplete(List<JadwalHasil> hasil);

        /** Dipanggil saat penjadwalan gagal (infeasible atau error) */
        void onFailed(String alasan);
    }
}
