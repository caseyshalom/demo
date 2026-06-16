package com.shiftscheduler.model;

import java.time.LocalDate;

/**
 * POJO untuk entitas Jadwal Hasil.
 * Merepresentasikan satu baris data di tabel 'jadwal_hasil'.
 */
public class JadwalHasil {

    private int id;
    private LocalDate tanggal;
    private int idKaryawan;
    private int idShift;

    // Field transient — tidak disimpan di DB, hanya untuk tampilan UI dan ekspor
    private String namaKaryawan;
    private String posisiKaryawan;
    private String kodeShift;
    private String jamMulai;
    private String jamSelesai;

    /** Constructor default */
    public JadwalHasil() {
    }

    /** Constructor tanpa ID (untuk INSERT) */
    public JadwalHasil(LocalDate tanggal, int idKaryawan, int idShift) {
        this.tanggal = tanggal;
        this.idKaryawan = idKaryawan;
        this.idShift = idShift;
    }

    /** Constructor lengkap dengan ID (untuk SELECT) */
    public JadwalHasil(int id, LocalDate tanggal, int idKaryawan, int idShift) {
        this.id = id;
        this.tanggal = tanggal;
        this.idKaryawan = idKaryawan;
        this.idShift = idShift;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public String getPosisiKaryawan() {
        return posisiKaryawan;
    }

    public void setPosisiKaryawan(String posisiKaryawan) {
        this.posisiKaryawan = posisiKaryawan;
    }

    public String getKodeShift() {
        return kodeShift;
    }

    public void setKodeShift(String kodeShift) {
        this.kodeShift = kodeShift;
    }

    public String getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(String jamMulai) {
        this.jamMulai = jamMulai;
    }

    public String getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(String jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    @Override
    public String toString() {
        return tanggal + " | " + namaKaryawan + " → " + kodeShift;
    }
}
