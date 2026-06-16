package com.shiftscheduler.model;

/**
 * POJO (Plain Old Java Object) untuk entitas Karyawan.
 * Merepresentasikan satu baris data di tabel 'karyawan'.
 */
public class Karyawan {

    private int id;
    private String nama;
    private String posisi;
    private int batasShiftMingguan;

    /** Constructor default (diperlukan untuk operasi DAO) */
    public Karyawan() {
    }

    /** Constructor lengkap tanpa ID (untuk INSERT — ID di-generate oleh database) */
    public Karyawan(String nama, String posisi, int batasShiftMingguan) {
        this.nama = nama;
        this.posisi = posisi;
        this.batasShiftMingguan = batasShiftMingguan;
    }

    /** Constructor lengkap dengan ID (untuk hasil SELECT dari database) */
    public Karyawan(int id, String nama, String posisi, int batasShiftMingguan) {
        this.id = id;
        this.nama = nama;
        this.posisi = posisi;
        this.batasShiftMingguan = batasShiftMingguan;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public int getBatasShiftMingguan() {
        return batasShiftMingguan;
    }

    public void setBatasShiftMingguan(int batasShiftMingguan) {
        this.batasShiftMingguan = batasShiftMingguan;
    }

    @Override
    public String toString() {
        return nama + " (" + posisi + ")";
    }
}
