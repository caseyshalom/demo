package com.shiftscheduler.model;

import java.time.LocalDate;

/**
 * POJO untuk entitas Karyawan Cuti/Izin.
 * Merepresentasikan satu baris data di tabel 'karyawan_cuti'.
 */
public class KaryawanCuti {

    private int id;
    private int idKaryawan;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private String keterangan;

    // Field transient — tidak disimpan di DB, hanya untuk tampilan UI
    private String namaKaryawan;

    /** Constructor default */
    public KaryawanCuti() {
    }

    /** Constructor tanpa ID (untuk INSERT) */
    public KaryawanCuti(int idKaryawan, LocalDate tanggalMulai, LocalDate tanggalSelesai, String keterangan) {
        this.idKaryawan = idKaryawan;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.keterangan = keterangan;
    }

    /** Constructor lengkap dengan ID (untuk SELECT) */
    public KaryawanCuti(int id, int idKaryawan, LocalDate tanggalMulai, LocalDate tanggalSelesai, String keterangan) {
        this.id = id;
        this.idKaryawan = idKaryawan;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.keterangan = keterangan;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDate getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(LocalDate tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    @Override
    public String toString() {
        return "Cuti: " + namaKaryawan + " (" + tanggalMulai + " s/d " + tanggalSelesai + ")";
    }
}
