-- ============================================================
-- Dummy Data: Sistem Penjadwalan Shift Karyawan
-- ============================================================

-- ============================================================
-- Data Karyawan (10 orang dengan berbagai posisi)
-- ============================================================
INSERT INTO karyawan (nama, posisi, batas_shift_mingguan) VALUES
('Ahmad Fauzi',    'Kasir',       5),
('Budi Santoso',   'Kasir',       5),
('Citra Dewi',     'Gudang',      5),
('Dian Pratama',   'Gudang',      6),
('Eka Putri',      'Supervisor',  5),
('Fajar Rahman',   'Kasir',       5),
('Gita Sari',      'Gudang',      4),
('Hadi Wijaya',    'Kasir',       6),
('Indah Lestari',  'Supervisor',  5),
('Joko Susilo',    'Gudang',      5);

-- ============================================================
-- Data Shift (3 shift standar operasional)
-- ============================================================
INSERT INTO shift (kode_shift, jam_mulai, jam_selesai, kuota_kebutuhan) VALUES
('P', '06:00', '14:00', 3),   -- Shift Pagi: butuh 3 orang
('S', '14:00', '22:00', 3),   -- Shift Siang: butuh 3 orang
('M', '22:00', '06:00', 2);   -- Shift Malam: butuh 2 orang

-- ============================================================
-- Data Cuti/Izin (beberapa record untuk testing)
-- ============================================================
INSERT INTO karyawan_cuti (id_karyawan, tanggal_mulai, tanggal_selesai, keterangan) VALUES
(1, '2026-06-20', '2026-06-22', 'Cuti tahunan'),
(3, '2026-06-18', '2026-06-19', 'Izin sakit'),
(5, '2026-06-21', '2026-06-25', 'Cuti melahirkan'),
(7, '2026-06-19', '2026-06-20', 'Urusan keluarga'),
(9, '2026-06-23', '2026-06-24', 'Cuti pribadi');
