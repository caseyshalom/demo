-- ============================================================
-- DDL: Sistem Penjadwalan Shift Karyawan
-- Database: shift_scheduler (PostgreSQL 16)
-- Normalisasi: 3NF
-- ============================================================

-- Hapus tabel jika sudah ada (urutan sesuai dependensi)
DROP TABLE IF EXISTS jadwal_hasil CASCADE;
DROP TABLE IF EXISTS karyawan_cuti CASCADE;
DROP TABLE IF EXISTS shift CASCADE;
DROP TABLE IF EXISTS karyawan CASCADE;

-- ============================================================
-- 1. Tabel Karyawan
-- Menyimpan data identitas dan batasan kerja karyawan
-- ============================================================
CREATE TABLE karyawan (
    id SERIAL PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    posisi VARCHAR(50) NOT NULL,
    batas_shift_mingguan INT NOT NULL DEFAULT 5,
    CONSTRAINT chk_batas_shift CHECK (batas_shift_mingguan > 0 AND batas_shift_mingguan <= 7)
);

-- ============================================================
-- 2. Tabel Shift
-- Mendefinisikan parameter operasional setiap shift
-- ============================================================
CREATE TABLE shift (
    id SERIAL PRIMARY KEY,
    kode_shift VARCHAR(10) UNIQUE NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    kuota_kebutuhan INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_kuota CHECK (kuota_kebutuhan > 0)
);

-- ============================================================
-- 3. Tabel Karyawan Cuti
-- Mencatat blokade tanggal di mana karyawan tidak bisa dijadwalkan
-- ============================================================
CREATE TABLE karyawan_cuti (
    id SERIAL PRIMARY KEY,
    id_karyawan INT NOT NULL,
    tanggal_mulai DATE NOT NULL,
    tanggal_selesai DATE NOT NULL,
    keterangan VARCHAR(200),
    CONSTRAINT fk_cuti_karyawan FOREIGN KEY (id_karyawan) 
        REFERENCES karyawan(id) ON DELETE CASCADE,
    CONSTRAINT chk_tanggal_cuti CHECK (tanggal_selesai >= tanggal_mulai)
);

-- Index untuk mempercepat query cuti berdasarkan tanggal
CREATE INDEX idx_cuti_tanggal ON karyawan_cuti(tanggal_mulai, tanggal_selesai);
CREATE INDEX idx_cuti_karyawan ON karyawan_cuti(id_karyawan);

-- ============================================================
-- 4. Tabel Jadwal Hasil
-- Menyimpan hasil penjadwalan (matriks Karyawan x Tanggal x Shift)
-- ============================================================
CREATE TABLE jadwal_hasil (
    id SERIAL PRIMARY KEY,
    tanggal DATE NOT NULL,
    id_karyawan INT NOT NULL,
    id_shift INT NOT NULL,
    CONSTRAINT fk_jadwal_karyawan FOREIGN KEY (id_karyawan) 
        REFERENCES karyawan(id) ON DELETE CASCADE,
    CONSTRAINT fk_jadwal_shift FOREIGN KEY (id_shift) 
        REFERENCES shift(id) ON DELETE CASCADE,
    CONSTRAINT uq_jadwal_karyawan_tanggal UNIQUE (tanggal, id_karyawan)
);

-- Index untuk mempercepat query jadwal berdasarkan tanggal
CREATE INDEX idx_jadwal_tanggal ON jadwal_hasil(tanggal);
CREATE INDEX idx_jadwal_karyawan ON jadwal_hasil(id_karyawan);
