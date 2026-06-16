package com.shiftscheduler.dao;

import com.shiftscheduler.db.DatabaseConnection;
import com.shiftscheduler.model.KaryawanCuti;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel 'karyawan_cuti'.
 * Semua query menggunakan PreparedStatement untuk mencegah SQL Injection.
 */
public class CutiDAO {

    private final DatabaseConnection dbConnection;

    public CutiDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Mengambil semua data cuti dengan JOIN ke tabel karyawan untuk mendapatkan nama.
     * 
     * @return List seluruh record cuti
     * @throws SQLException jika terjadi error query
     */
    public List<KaryawanCuti> getAll() throws SQLException {
        List<KaryawanCuti> list = new ArrayList<>();
        String sql = "SELECT c.id, c.id_karyawan, c.tanggal_mulai, c.tanggal_selesai, c.keterangan, " +
                     "k.nama AS nama_karyawan " +
                     "FROM karyawan_cuti c " +
                     "JOIN karyawan k ON c.id_karyawan = k.id " +
                     "ORDER BY c.tanggal_mulai DESC";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                KaryawanCuti cuti = new KaryawanCuti(
                    rs.getInt("id"),
                    rs.getInt("id_karyawan"),
                    rs.getDate("tanggal_mulai").toLocalDate(),
                    rs.getDate("tanggal_selesai").toLocalDate(),
                    rs.getString("keterangan")
                );
                cuti.setNamaKaryawan(rs.getString("nama_karyawan"));
                list.add(cuti);
            }
        }
        return list;
    }

    /**
     * Menambahkan record cuti baru ke database.
     * 
     * @param cuti objek KaryawanCuti yang akan ditambahkan
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean insert(KaryawanCuti cuti) throws SQLException {
        String sql = "INSERT INTO karyawan_cuti (id_karyawan, tanggal_mulai, tanggal_selesai, keterangan) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, cuti.getIdKaryawan());
            stmt.setDate(2, Date.valueOf(cuti.getTanggalMulai()));
            stmt.setDate(3, Date.valueOf(cuti.getTanggalSelesai()));
            stmt.setString(4, cuti.getKeterangan());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Memperbarui data cuti di database.
     * 
     * @param cuti objek KaryawanCuti dengan data baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean update(KaryawanCuti cuti) throws SQLException {
        String sql = "UPDATE karyawan_cuti SET id_karyawan = ?, tanggal_mulai = ?, tanggal_selesai = ?, keterangan = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, cuti.getIdKaryawan());
            stmt.setDate(2, Date.valueOf(cuti.getTanggalMulai()));
            stmt.setDate(3, Date.valueOf(cuti.getTanggalSelesai()));
            stmt.setString(4, cuti.getKeterangan());
            stmt.setInt(5, cuti.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus record cuti dari database berdasarkan ID.
     * 
     * @param id ID record cuti yang akan dihapus
     * @return true jika berhasil
     * @throws SQLException jika terjadi error query
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM karyawan_cuti WHERE id = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Memeriksa apakah karyawan tertentu sedang cuti pada tanggal tertentu.
     * Digunakan oleh JadwalEngine sebagai constraint check.
     * 
     * @param idKaryawan ID karyawan
     * @param tanggal tanggal yang akan dicek
     * @return true jika karyawan sedang cuti pada tanggal tersebut
     * @throws SQLException jika terjadi error query
     */
    public boolean isKaryawanCuti(int idKaryawan, LocalDate tanggal) throws SQLException {
        String sql = "SELECT COUNT(*) FROM karyawan_cuti WHERE id_karyawan = ? AND ? BETWEEN tanggal_mulai AND tanggal_selesai";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idKaryawan);
            stmt.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Mendapatkan daftar ID karyawan yang sedang cuti pada tanggal tertentu.
     * Digunakan oleh JadwalEngine untuk filtering batch.
     * 
     * @param tanggal tanggal yang akan dicek
     * @return List ID karyawan yang cuti
     * @throws SQLException jika terjadi error query
     */
    public List<Integer> getIdKaryawanCutiPadaTanggal(LocalDate tanggal) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_karyawan FROM karyawan_cuti WHERE ? BETWEEN tanggal_mulai AND tanggal_selesai";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(tanggal));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_karyawan"));
                }
            }
        }
        return ids;
    }

    /**
     * Menghitung jumlah karyawan yang sedang cuti hari ini.
     * Digunakan oleh Dashboard untuk metrik.
     * 
     * @return jumlah karyawan cuti hari ini
     * @throws SQLException jika terjadi error query
     */
    public int countCutiHariIni() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT id_karyawan) FROM karyawan_cuti WHERE CURRENT_DATE BETWEEN tanggal_mulai AND tanggal_selesai";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
