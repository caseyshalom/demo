package com.shiftscheduler.model;

import java.time.LocalTime;

/**
 * POJO untuk entitas Shift.
 * Merepresentasikan satu baris data di tabel 'shift'.
 */
public class Shift {

    private int id;
    private String kodeShift;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private int kuotaKebutuhan;

    /** Constructor default */
    public Shift() {
    }

    /** Constructor tanpa ID (untuk INSERT) */
    public Shift(String kodeShift, LocalTime jamMulai, LocalTime jamSelesai, int kuotaKebutuhan) {
        this.kodeShift = kodeShift;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.kuotaKebutuhan = kuotaKebutuhan;
    }

    /** Constructor lengkap dengan ID (untuk SELECT) */
    public Shift(int id, String kodeShift, LocalTime jamMulai, LocalTime jamSelesai, int kuotaKebutuhan) {
        this.id = id;
        this.kodeShift = kodeShift;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.kuotaKebutuhan = kuotaKebutuhan;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeShift() {
        return kodeShift;
    }

    public void setKodeShift(String kodeShift) {
        this.kodeShift = kodeShift;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public int getKuotaKebutuhan() {
        return kuotaKebutuhan;
    }

    public void setKuotaKebutuhan(int kuotaKebutuhan) {
        this.kuotaKebutuhan = kuotaKebutuhan;
    }

    @Override
    public String toString() {
        return kodeShift + " (" + jamMulai + " - " + jamSelesai + ")";
    }
}
