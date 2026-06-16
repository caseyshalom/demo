package com.shiftscheduler.dao;

import com.shiftscheduler.db.DatabaseConnection;
import com.shiftscheduler.model.Shift;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel 'shift'.
 * Semua query menggunakan PreparedStatement untuk mencegah SQL Injection.
 */
public class ShiftDAO {

    private final DatabaseConnection dbConnection;

    public ShiftDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Mengambil semua data shift dari database.
     * 
     * @return List seluruh shift
     * @throws SQLException jika terjadi error query
     */
    public List<Shift> getAll() throws SQLException {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT id, kode_shift, jam_mulai, jam_selesai, kuota_kebutuhan FROM shift ORDER BY jam_mulai";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Shift s = new Shift(
                    rs.getInt("id"),
                    rs.getString("kode_shift"),
                    rs.getTime("jam_mulai").toLocalTime(),
                    rs.getTime("jam_selesai").toLocalTime(),
                    rs.getInt("kuota_kebutuhan")
                );
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Mengambil data shift berdasarkan ID.
     * 
     * @param id ID shift
     * @return objek Shift, atau null jika tidak ditemukan
     * @throws SQLException jika terjadi error query
     */
    public Shift getById(int id) throws SQLException {
        String sql = "SELECT id, kode_shift, jam_mulai, jam_selesai, kuota_kebutuhan FROM shift WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Shift(
                        rs.getInt("id"),
                        rs.getString("kode_shift"),
                        rs.getTime("jam_mulai").toLocalTime(),
                        rs.getTime("jam_selesai").toLocalTime(),
                        rs.getInt("kuota_kebutuhan")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Menambahkan shift baru ke database.
     * 
     * @param shift objek Shift yang akan ditambahkan
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean insert(Shift shift) throws SQLException {
        String sql = "INSERT INTO shift (kode_shift, jam_mulai, jam_selesai, kuota_kebutuhan) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, shift.getKodeShift());
            stmt.setTime(2, Time.valueOf(shift.getJamMulai()));
            stmt.setTime(3, Time.valueOf(shift.getJamSelesai()));
            stmt.setInt(4, shift.getKuotaKebutuhan());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Memperbarui data shift di database.
     * 
     * @param shift objek Shift dengan data baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean update(Shift shift) throws SQLException {
        String sql = "UPDATE shift SET kode_shift = ?, jam_mulai = ?, jam_selesai = ?, kuota_kebutuhan = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, shift.getKodeShift());
            stmt.setTime(2, Time.valueOf(shift.getJamMulai()));
            stmt.setTime(3, Time.valueOf(shift.getJamSelesai()));
            stmt.setInt(4, shift.getKuotaKebutuhan());
            stmt.setInt(5, shift.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus shift dari database berdasarkan ID.
     * 
     * @param id ID shift yang akan dihapus
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM shift WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghitung total kuota kebutuhan personel untuk semua shift.
     * Digunakan oleh Dashboard untuk metrik "Total Kuota Shift Hari Ini".
     * 
     * @return total kuota kebutuhan
     * @throws SQLException jika terjadi error query
     */
    public int getTotalKuota() throws SQLException {
        String sql = "SELECT COALESCE(SUM(kuota_kebutuhan), 0) FROM shift";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
