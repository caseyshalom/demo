package com.shiftscheduler.dao;

import com.shiftscheduler.db.DatabaseConnection;
import com.shiftscheduler.model.JadwalHasil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel 'jadwal_hasil'.
 * Semua query menggunakan PreparedStatement untuk mencegah SQL Injection.
 */
public class JadwalDAO {

    private final DatabaseConnection dbConnection;

    public JadwalDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Mengambil semua data jadwal dengan JOIN ke tabel karyawan dan shift.
     * 
     * @return List seluruh jadwal
     * @throws SQLException jika terjadi error query
     */
    public List<JadwalHasil> getAll() throws SQLException {
        return executeJoinQuery(
            "SELECT j.id, j.tanggal, j.id_karyawan, j.id_shift, " +
            "k.nama AS nama_karyawan, k.posisi AS posisi_karyawan, " +
            "s.kode_shift, s.jam_mulai, s.jam_selesai " +
            "FROM jadwal_hasil j " +
            "JOIN karyawan k ON j.id_karyawan = k.id " +
            "JOIN shift s ON j.id_shift = s.id " +
            "ORDER BY j.tanggal, s.jam_mulai, k.nama",
            null
        );
    }

    /**
     * Mengambil data jadwal berdasarkan rentang tanggal.
     * 
     * @param mulai tanggal awal (inclusive)
     * @param selesai tanggal akhir (inclusive)
     * @return List jadwal dalam rentang tanggal
     * @throws SQLException jika terjadi error query
     */
    public List<JadwalHasil> getByDateRange(LocalDate mulai, LocalDate selesai) throws SQLException {
        return executeJoinQuery(
            "SELECT j.id, j.tanggal, j.id_karyawan, j.id_shift, " +
            "k.nama AS nama_karyawan, k.posisi AS posisi_karyawan, " +
            "s.kode_shift, s.jam_mulai, s.jam_selesai " +
            "FROM jadwal_hasil j " +
            "JOIN karyawan k ON j.id_karyawan = k.id " +
            "JOIN shift s ON j.id_shift = s.id " +
            "WHERE j.tanggal BETWEEN ? AND ? " +
            "ORDER BY j.tanggal, s.jam_mulai, k.nama",
            stmt -> {
                stmt.setDate(1, Date.valueOf(mulai));
                stmt.setDate(2, Date.valueOf(selesai));
            }
        );
    }

    /**
     * Mengambil data jadwal untuk tanggal tertentu.
     * Digunakan oleh Dashboard untuk menampilkan jadwal hari ini.
     * 
     * @param tanggal tanggal yang diminta
     * @return List jadwal pada tanggal tersebut
     * @throws SQLException jika terjadi error query
     */
    public List<JadwalHasil> getByDate(LocalDate tanggal) throws SQLException {
        return executeJoinQuery(
            "SELECT j.id, j.tanggal, j.id_karyawan, j.id_shift, " +
            "k.nama AS nama_karyawan, k.posisi AS posisi_karyawan, " +
            "s.kode_shift, s.jam_mulai, s.jam_selesai " +
            "FROM jadwal_hasil j " +
            "JOIN karyawan k ON j.id_karyawan = k.id " +
            "JOIN shift s ON j.id_shift = s.id " +
            "WHERE j.tanggal = ? " +
            "ORDER BY s.jam_mulai, k.nama",
            stmt -> stmt.setDate(1, Date.valueOf(tanggal))
        );
    }

    /**
     * Menyisipkan batch jadwal hasil ke database.
     * Menggunakan batch execution untuk efisiensi.
     * 
     * @param jadwalList List jadwal yang akan disisipkan
     * @return jumlah baris yang berhasil disisipkan
     * @throws SQLException jika terjadi error query
     */
    public int insertBatch(List<JadwalHasil> jadwalList) throws SQLException {
        String sql = "INSERT INTO jadwal_hasil (tanggal, id_karyawan, id_shift) VALUES (?, ?, ?) " +
                     "ON CONFLICT (tanggal, id_karyawan) DO UPDATE SET id_shift = EXCLUDED.id_shift";

        Connection conn = dbConnection.getConnection();
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (JadwalHasil j : jadwalList) {
                stmt.setDate(1, Date.valueOf(j.getTanggal()));
                stmt.setInt(2, j.getIdKaryawan());
                stmt.setInt(3, j.getIdShift());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            conn.commit();

            int total = 0;
            for (int r : results) {
                if (r >= 0) total += r;
                else if (r == Statement.SUCCESS_NO_INFO) total++;
            }
            return total;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    /**
     * Memperbarui shift untuk satu jadwal (untuk editor manual).
     * 
     * @param jadwal objek JadwalHasil dengan id_shift baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean update(JadwalHasil jadwal) throws SQLException {
        String sql = "UPDATE jadwal_hasil SET id_shift = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, jadwal.getIdShift());
            stmt.setInt(2, jadwal.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus satu jadwal berdasarkan ID.
     * 
     * @param id ID jadwal
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM jadwal_hasil WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus semua jadwal dalam rentang tanggal tertentu.
     * Digunakan sebelum re-generate jadwal untuk periode yang sama.
     * 
     * @param mulai tanggal awal (inclusive)
     * @param selesai tanggal akhir (inclusive)
     * @return jumlah baris yang dihapus
     * @throws SQLException jika terjadi error query
     */
    public int deleteByDateRange(LocalDate mulai, LocalDate selesai) throws SQLException {
        String sql = "DELETE FROM jadwal_hasil WHERE tanggal BETWEEN ? AND ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(mulai));
            stmt.setDate(2, Date.valueOf(selesai));
            return stmt.executeUpdate();
        }
    }

    /**
     * Menghitung jumlah shift karyawan dalam satu minggu (Senin-Minggu yang mencakup tanggal tertentu).
     * Digunakan oleh JadwalEngine untuk constraint batas_shift_mingguan.
     * 
     * @param idKaryawan ID karyawan
     * @param tanggalDalamMinggu salah satu tanggal dalam minggu tersebut
     * @return jumlah shift karyawan dalam minggu tersebut
     * @throws SQLException jika terjadi error query
     */
    public int countShiftMingguIni(int idKaryawan, LocalDate tanggalDalamMinggu) throws SQLException {
        // Menghitung awal dan akhir minggu (Senin - Minggu)
        LocalDate senin = tanggalDalamMinggu.minusDays(tanggalDalamMinggu.getDayOfWeek().getValue() - 1);
        LocalDate minggu = senin.plusDays(6);

        String sql = "SELECT COUNT(*) FROM jadwal_hasil WHERE id_karyawan = ? AND tanggal BETWEEN ? AND ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idKaryawan);
            stmt.setDate(2, Date.valueOf(senin));
            stmt.setDate(3, Date.valueOf(minggu));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // ==================== HELPER METHOD ====================

    /**
     * Helper method untuk menjalankan query JOIN dan mapping hasil ke List<JadwalHasil>.
     * Mengurangi duplikasi kode antar method getAll, getByDateRange, getByDate.
     */
    private List<JadwalHasil> executeJoinQuery(String sql, ParameterSetter setter) throws SQLException {
        List<JadwalHasil> list = new ArrayList<>();

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            if (setter != null) {
                setter.setParameters(stmt);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JadwalHasil j = new JadwalHasil(
                        rs.getInt("id"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getInt("id_karyawan"),
                        rs.getInt("id_shift")
                    );
                    j.setNamaKaryawan(rs.getString("nama_karyawan"));
                    j.setPosisiKaryawan(rs.getString("posisi_karyawan"));
                    j.setKodeShift(rs.getString("kode_shift"));
                    j.setJamMulai(rs.getTime("jam_mulai").toLocalTime().toString());
                    j.setJamSelesai(rs.getTime("jam_selesai").toLocalTime().toString());
                    list.add(j);
                }
            }
        }
        return list;
    }

    /**
     * Functional interface untuk mengatur parameter PreparedStatement.
     * Digunakan oleh executeJoinQuery untuk menghindari duplikasi.
     */
    @FunctionalInterface
    private interface ParameterSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }
}
