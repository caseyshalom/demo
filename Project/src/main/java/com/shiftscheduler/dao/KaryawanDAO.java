package com.shiftscheduler.dao;

import com.shiftscheduler.db.DatabaseConnection;
import com.shiftscheduler.model.Karyawan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel 'karyawan'.
 * Semua query menggunakan PreparedStatement untuk mencegah SQL Injection.
 */
public class KaryawanDAO {

    private final DatabaseConnection dbConnection;

    public KaryawanDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Mengambil semua data karyawan dari database.
     * 
     * @return List seluruh karyawan
     * @throws SQLException jika terjadi error query
     */
    public List<Karyawan> getAll() throws SQLException {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT id, nama, posisi, batas_shift_mingguan FROM karyawan ORDER BY id";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Karyawan k = new Karyawan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("posisi"),
                    rs.getInt("batas_shift_mingguan")
                );
                list.add(k);
            }
        }
        return list;
    }

    /**
     * Mengambil data karyawan berdasarkan ID.
     * 
     * @param id ID karyawan
     * @return objek Karyawan, atau null jika tidak ditemukan
     * @throws SQLException jika terjadi error query
     */
    public Karyawan getById(int id) throws SQLException {
        String sql = "SELECT id, nama, posisi, batas_shift_mingguan FROM karyawan WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Karyawan(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("posisi"),
                        rs.getInt("batas_shift_mingguan")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Menambahkan karyawan baru ke database.
     * 
     * @param karyawan objek Karyawan yang akan ditambahkan
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean insert(Karyawan karyawan) throws SQLException {
        String sql = "INSERT INTO karyawan (nama, posisi, batas_shift_mingguan) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, karyawan.getNama());
            stmt.setString(2, karyawan.getPosisi());
            stmt.setInt(3, karyawan.getBatasShiftMingguan());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Memperbarui data karyawan di database.
     * 
     * @param karyawan objek Karyawan dengan data baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean update(Karyawan karyawan) throws SQLException {
        String sql = "UPDATE karyawan SET nama = ?, posisi = ?, batas_shift_mingguan = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, karyawan.getNama());
            stmt.setString(2, karyawan.getPosisi());
            stmt.setInt(3, karyawan.getBatasShiftMingguan());
            stmt.setInt(4, karyawan.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus karyawan dari database berdasarkan ID.
     * Cascade delete akan otomatis menghapus cuti dan jadwal terkait.
     * 
     * @param id ID karyawan yang akan dihapus
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM karyawan WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghitung jumlah total karyawan aktif (terdaftar di database).
     * Digunakan oleh Dashboard untuk metrik.
     * 
     * @return jumlah karyawan
     * @throws SQLException jika terjadi error query
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM karyawan";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
