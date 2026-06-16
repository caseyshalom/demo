package com.shiftscheduler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class untuk mengelola koneksi ke database PostgreSQL.
 * Menggunakan pattern Singleton agar hanya ada satu instance koneksi aktif.
 */
public class DatabaseConnection {

    // Konfigurasi koneksi database
    private static final String URL = "jdbc:postgresql://localhost:5432/shift_scheduler";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * Private constructor — hanya bisa dipanggil dari getInstance().
     * Memuat driver JDBC dan membuat koneksi awal.
     */
    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Koneksi ke database berhasil.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver PostgreSQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Gagal terhubung ke database: " + e.getMessage());
        }
    }

    /**
     * Mendapatkan instance tunggal DatabaseConnection (Singleton).
     * Thread-safe dengan synchronized.
     * 
     * @return instance DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Mendapatkan objek Connection untuk operasi database.
     * Jika koneksi terputus (closed), akan membuat koneksi baru (auto-reconnect).
     * 
     * @return objek Connection yang aktif
     * @throws SQLException jika gagal membuat koneksi baru
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("[DB] Koneksi terputus, melakukan reconnect...");
            try {
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Reconnect berhasil.");
            } catch (SQLException e) {
                System.err.println("[DB] Gagal reconnect: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Menutup koneksi database. Dipanggil saat aplikasi ditutup.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error saat menutup koneksi: " + e.getMessage());
        }
    }
}
