-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 02, 2026 at 12:24 PM
-- Server version: 8.0.43
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inventaris_barang`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id_barang` int NOT NULL,
  `nama_barang` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `id_instansi` int NOT NULL,
  `jumlah_total` int NOT NULL,
  `jumlah_tersedia` int DEFAULT NULL,
  `status` enum('tersedia','dipinjam','rusak','hilang') NOT NULL DEFAULT 'tersedia',
  `foto_barang` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`id_barang`, `nama_barang`, `id_instansi`, `jumlah_total`, `jumlah_tersedia`, `status`, `foto_barang`, `created_at`, `updated_at`) VALUES
(1, 'Proyektor Epson EB-X400', 2, 5, 5, 'tersedia', 'proyektor_epson.jpg', '2025-12-27 00:55:04', '2025-12-27 00:55:04'),
(2, 'Kabel HDMI 10 Meter', 2, 10, 7, 'tersedia', 'kabel_hdmi.jpg', '2025-12-27 00:55:04', '2025-12-27 06:35:29'),
(3, 'Sound System Portable', 3, 2, 2, 'tersedia', 'sound_portable.jpg', '2025-12-27 00:55:04', '2025-12-27 00:55:04'),
(4, 'Megaphone TOA', 3, 4, 2, 'tersedia', 'megaphone_toa.jpg', '2025-12-27 00:55:04', '2025-12-27 06:37:32'),
(5, 'Printer HP Laserjet', 4, 3, 3, 'tersedia', 'printer_hp.jpg', '2025-12-27 00:55:04', '2025-12-27 00:55:04'),
(6, 'Laptop Asus ROG', 2, 2, 2, 'tersedia', 'laptop_rog.jpg', '2026-01-02 05:44:14', '2026-01-02 05:44:14'),
(7, 'Tripod Takara', 3, 5, 5, 'tersedia', 'tripod_takara.jpg', '2026-01-02 05:44:14', '2026-01-02 05:44:14'),
(8, 'Kamera Canon EOS 200D', 3, 2, 1, 'tersedia', 'canon_200d.jpg', '2026-01-02 05:44:14', '2026-01-02 05:44:14'),
(9, 'Pointer Logitech', 2, 4, 4, 'tersedia', 'pointer_logitech.jpg', '2026-01-02 05:44:14', '2026-01-02 05:44:14'),
(10, 'Kabel Roll 15m', 4, 6, 6, 'tersedia', 'kabel_roll.jpg', '2026-01-02 05:44:14', '2026-01-02 05:44:14');

-- --------------------------------------------------------

--
-- Table structure for table `berita`
--

CREATE TABLE `berita` (
  `id_berita` int NOT NULL,
  `judul` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `deskripsi` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `warna_background` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#D9696F',
  `created_by` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `berita`
--

INSERT INTO `berita` (`id_berita`, `judul`, `deskripsi`, `warna_background`, `created_by`, `created_at`) VALUES
(1, 'mic', 'wkwkkwwk', '#D9696F', 1, '2025-12-27 06:39:59'),
(2, 'Pemeliharaan Alat', 'Diberitahukan bahwa seluruh proyektor akan dilakukan maintenance pada tanggal 5 Januari.', '#E74C3C', 1, '2026-01-02 05:44:40'),
(3, 'Prosedur Peminjaman', 'Pastikan mengunggah SPM yang sudah ditandatangani saat melakukan pengajuan.', '#3498DB', 1, '2026-01-02 05:44:40');

-- --------------------------------------------------------

--
-- Table structure for table `borrow`
--

CREATE TABLE `borrow` (
  `id_borrow` int NOT NULL,
  `id_peminjam` int NOT NULL,
  `id_barang` int NOT NULL,
  `jumlah_pinjam` int NOT NULL,
  `tgl_pinjam` datetime NOT NULL,
  `dl_kembali` date DEFAULT NULL,
  `link_spm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `status` enum('menunggu','disetujui','ditolak') NOT NULL DEFAULT 'menunggu',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `borrow`
--

INSERT INTO `borrow` (`id_borrow`, `id_peminjam`, `id_barang`, `jumlah_pinjam`, `tgl_pinjam`, `dl_kembali`, `link_spm`, `status`, `created_at`, `updated_at`) VALUES
(1, 5, 2, 1, '2025-12-27 00:00:00', '2026-01-03', 'hjkl', 'menunggu', '2025-12-27 06:35:29', '2025-12-27 06:35:29'),
(2, 5, 4, 1, '2025-12-27 00:00:00', '2026-01-03', 'hjkl;', 'menunggu', '2025-12-27 06:37:32', '2025-12-27 06:37:32'),
(3, 6, 1, 1, '2026-01-02 08:00:00', '2026-01-05', 'spm_nafila_01.pdf', 'disetujui', '2026-01-02 05:44:51', '2026-01-02 05:44:51'),
(4, 7, 3, 1, '2026-01-02 09:00:00', '2026-01-04', 'spm_annisa_02.pdf', 'menunggu', '2026-01-02 05:44:51', '2026-01-02 05:44:51'),
(5, 8, 6, 1, '2026-01-02 10:00:00', '2026-01-03', 'spm_reifana_03.pdf', 'disetujui', '2026-01-02 05:44:51', '2026-01-02 05:44:51'),
(6, 9, 2, 2, '2026-01-02 11:00:00', '2026-01-06', 'spm_yasir_04.pdf', 'ditolak', '2026-01-02 05:44:51', '2026-01-02 05:44:51');

-- --------------------------------------------------------

--
-- Table structure for table `laporan`
--

CREATE TABLE `laporan` (
  `id_laporan` int NOT NULL,
  `id_admin` int NOT NULL,
  `id_peminjaman` int NOT NULL,
  `tgl_lapor` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('diproses','verify','selesai') NOT NULL DEFAULT 'diproses',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `laporan`
--

INSERT INTO `laporan` (`id_laporan`, `id_admin`, `id_peminjaman`, `tgl_lapor`, `status`, `updated_at`) VALUES
(1, 1, 1, '2026-01-02 05:45:18', 'selesai', '2026-01-02 05:45:18'),
(2, 1, 2, '2026-01-02 05:45:18', 'diproses', '2026-01-02 05:45:18'),
(3, 1, 3, '2026-01-02 05:45:18', 'verify', '2026-01-02 05:45:18');

-- --------------------------------------------------------

--
-- Table structure for table `log_activity`
--

CREATE TABLE `log_activity` (
  `id_log` int NOT NULL,
  `id_user` int DEFAULT NULL,
  `aktivitas` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `log_activity`
--

INSERT INTO `log_activity` (`id_log`, `id_user`, `aktivitas`, `created_at`) VALUES
(1, 1, 'Admin menyetujui peminjaman ID #1', '2026-01-02 05:45:32'),
(2, 5, 'User Kholistiyani melakukan login', '2026-01-02 05:45:32'),
(3, 6, 'User Nafila mengajukan peminjaman Laptop', '2026-01-02 05:45:32'),
(4, 2, 'Instansi MKB menambahkan barang baru: Laptop Asus ROG', '2026-01-02 05:45:32'),
(5, 1, 'Admin memperbarui pengumuman pemeliharaan alat', '2026-01-02 05:45:32');

-- --------------------------------------------------------

--
-- Table structure for table `return`
--

CREATE TABLE `return` (
  `id_return` int NOT NULL,
  `id_borrow` int NOT NULL,
  `jumlah` int NOT NULL,
  `kondisi` enum('baik','rusak','hilang') NOT NULL,
  `status` enum('pending','verified','rejected') NOT NULL DEFAULT 'pending',
  `keterangan` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `return`
--

INSERT INTO `return` (`id_return`, `id_borrow`, `jumlah`, `kondisi`, `status`, `keterangan`, `created_at`) VALUES
(1, 1, 1, 'baik', 'verified', 'Dikembalikan dalam kondisi mulus', '2026-01-02 05:46:54'),
(2, 3, 1, 'baik', 'pending', 'Sedang dicek oleh admin', '2026-01-02 05:46:54');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int NOT NULL,
  `username` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `nama` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('peminjam','instansi','admin') NOT NULL,
  `status` enum('aktif','nonaktif') NOT NULL DEFAULT 'aktif',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `username`, `password`, `nama`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin', '123', 'Admin Upi PWK', 'admin', 'aktif', '2025-12-15 15:18:47', '2025-12-27 00:57:14'),
(2, 'mkb', '123', 'HIMATRONIKA-AI', 'instansi', 'aktif', '2025-12-15 15:18:47', '2025-12-27 00:56:50'),
(3, '567Bempwk', '45AWRema', 'BEM REMA UPI PWK', 'instansi', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(4, '1256Leppim', 'Leppim1920', 'LEPPIM UPI PWK', 'instansi', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(5, '123', '123', 'Kholistiyani Zulfi', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-27 00:57:01'),
(6, '456', '456', 'Nafila Ajmal', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2026-01-02 11:07:47'),
(7, '2503117', '0FG3208', 'Annisa Zulfa', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(8, '2203217', '068320P', 'Reifana Al-Kindi', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(9, '2203117', '06GF208', 'Yasir Ahmadin', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(10, '2303117', '0783208', 'Arya Adimanggala', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(11, '2403457', '0683248', 'Dita Karang', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(12, '2503111', '0HI8208', 'Muhammad Zayyan', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(13, '2243117', '0676890', 'Mark Lee', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(14, '2583117', '0623HJ8', 'Lee Seunghyun', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(15, '2456789', 'WJ89008', 'Hwang Renjun', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(16, '2403167', 'JK83208', 'Dilraba Dilmurat', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(17, '2356781', '06KL008', 'Martin Edwards', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(18, '2403223', 'HJ90208', 'Zhou Yufan James', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(19, '2402113', 'GH67208', 'Dasha Taran', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(20, '2567890', 'KL90208', 'Pharita Boonpakdeethaveeyod', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47'),
(21, '2478907', '56FG208', 'Roseanne Park', 'peminjam', 'aktif', '2025-12-15 15:18:47', '2025-12-15 15:18:47');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_barang`),
  ADD UNIQUE KEY `id_barang_UNIQUE` (`id_barang`),
  ADD KEY `idx_barang_id_instansi` (`id_instansi`);

--
-- Indexes for table `berita`
--
ALTER TABLE `berita`
  ADD PRIMARY KEY (`id_berita`),
  ADD KEY `idx_created_at` (`created_at` DESC),
  ADD KEY `idx_created_by` (`created_by`);

--
-- Indexes for table `borrow`
--
ALTER TABLE `borrow`
  ADD PRIMARY KEY (`id_borrow`),
  ADD UNIQUE KEY `id_borrow_UNIQUE` (`id_borrow`),
  ADD KEY `fk_borrow_id_peminjam` (`id_peminjam`),
  ADD KEY `fk_borrow_id_barang_idx` (`id_barang`);

--
-- Indexes for table `laporan`
--
ALTER TABLE `laporan`
  ADD PRIMARY KEY (`id_laporan`),
  ADD UNIQUE KEY `id_laporan_UNIQUE` (`id_laporan`),
  ADD KEY `fk_laporan_id_admin` (`id_admin`),
  ADD KEY `fk_laporan_id_peminjaman_idx` (`id_peminjaman`);

--
-- Indexes for table `log_activity`
--
ALTER TABLE `log_activity`
  ADD PRIMARY KEY (`id_log`),
  ADD KEY `fk_log_user` (`id_user`);

--
-- Indexes for table `return`
--
ALTER TABLE `return`
  ADD PRIMARY KEY (`id_return`),
  ADD UNIQUE KEY `id_return_UNIQUE` (`id_return`),
  ADD KEY `idx_return_id_borrow` (`id_borrow`) INVISIBLE;

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username_UNIQUE` (`username`),
  ADD UNIQUE KEY `id_user_UNIQUE` (`id_user`),
  ADD KEY `idx_user_role` (`role`),
  ADD KEY `idx_user_status` (`status`),
  ADD KEY `idx_user_created_at` (`created_at`),
  ADD KEY `idx_user_update_at` (`updated_at`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `barang`
--
ALTER TABLE `barang`
  MODIFY `id_barang` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `berita`
--
ALTER TABLE `berita`
  MODIFY `id_berita` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `borrow`
--
ALTER TABLE `borrow`
  MODIFY `id_borrow` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `laporan`
--
ALTER TABLE `laporan`
  MODIFY `id_laporan` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `log_activity`
--
ALTER TABLE `log_activity`
  MODIFY `id_log` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `return`
--
ALTER TABLE `return`
  MODIFY `id_return` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `barang`
--
ALTER TABLE `barang`
  ADD CONSTRAINT `fk_barang_id_instansi` FOREIGN KEY (`id_instansi`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `berita`
--
ALTER TABLE `berita`
  ADD CONSTRAINT `berita_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `borrow`
--
ALTER TABLE `borrow`
  ADD CONSTRAINT `fk_borrow_id_barang` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_borrow_id_peminjam` FOREIGN KEY (`id_peminjam`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `laporan`
--
ALTER TABLE `laporan`
  ADD CONSTRAINT `fk_laporan_id_admin` FOREIGN KEY (`id_admin`) REFERENCES `user` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_laporan_id_peminjaman` FOREIGN KEY (`id_peminjaman`) REFERENCES `borrow` (`id_borrow`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `log_activity`
--
ALTER TABLE `log_activity`
  ADD CONSTRAINT `fk_log_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `return`
--
ALTER TABLE `return`
  ADD CONSTRAINT `fk_return_id_borrow` FOREIGN KEY (`id_borrow`) REFERENCES `borrow` (`id_borrow`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
