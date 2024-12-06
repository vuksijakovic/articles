-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 13, 2024 at 02:51 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `webtabela`
--

-- --------------------------------------------------------

--
-- Table structure for table `aktivnost`
--

CREATE TABLE `aktivnost` (
  `id` int(11) NOT NULL,
  `naziv` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `aktivnost`
--

INSERT INTO `aktivnost` (`id`, `naziv`) VALUES
(16, 'planinarenje'),
(17, 'krstarenje'),
(18, 'trcanje'),
(20, 'posjeta visegradu');

-- --------------------------------------------------------

--
-- Table structure for table `clanak`
--

CREATE TABLE `clanak` (
  `id` int(11) NOT NULL,
  `naslov` varchar(255) NOT NULL,
  `tekst` text NOT NULL,
  `vreme_kreiranja` datetime NOT NULL,
  `broj_poseta` int(11) DEFAULT 0,
  `autor_id` int(11) DEFAULT NULL,
  `destinacija_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `clanak`
--

INSERT INTO `clanak` (`id`, `naslov`, `tekst`, `vreme_kreiranja`, `broj_poseta`, `autor_id`, `destinacija_id`) VALUES
(35, 'Clanak o srbiji', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. In eros orci, pharetra quis consequat et, ullamcorper ut sapien. Quisque et dolor sit amet erat vulputate vehicula vitae at magna. Praesent consequat, lectus eget maximus fermentum, lacus nulla iaculis lorem, vel tempor sapien quam consequat velit. Fusce aliquet nisl eu tortor cursus, at placerat mauris consectetur. Duis sit amet diam porta, euismod tortor ac, ultrices ligula. Pellentesque nunc nibh, lacinia vitae porta in, iaculis vitae est. Curabitur aliquet venenatis aliquam.', '2024-06-13 14:33:16', 5, 4, 16),
(36, 'Clanak o makedoniji', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. In eros orci, pharetra quis consequat et, ullamcorper ut sapien. Quisque et dolor sit amet erat vulputate vehicula vitae at magna. Praesent consequat, lectus eget maximus fermentum, lacus nulla iaculis lorem, vel tempor sapien quam consequat velit. Fusce aliquet nisl eu tortor cursus, at placerat mauris consectetur. Duis sit amet diam porta, euismod tortor ac, ultrices ligula. Pellentesque nunc nibh, lacinia vitae porta in, iaculis vitae est. Curabitur aliquet venenatis aliquam.', '2024-06-13 14:34:21', 2, 2, 17),
(37, 'Visegrad', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. In eros orci, pharetra quis consequat et, ullamcorper ut sapien. Quisque et dolor sit amet erat vulputate vehicula vitae at magna. Praesent consequat, lectus eget maximus fermentum, lacus nulla iaculis lorem, vel tempor sapien quam consequat velit. Fusce aliquet nisl eu tortor cursus, at placerat mauris consectetur. Duis sit amet diam porta, euismod tortor ac, ultrices ligula. Pellentesque nunc nibh, lacinia vitae porta in, iaculis vitae est. Curabitur aliquet venenatis aliquam.', '2024-06-13 14:38:49', 7, 3, 18);

-- --------------------------------------------------------

--
-- Table structure for table `clanak_aktivnost`
--

CREATE TABLE `clanak_aktivnost` (
  `clanak_id` int(11) NOT NULL,
  `aktivnost_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `clanak_aktivnost`
--

INSERT INTO `clanak_aktivnost` (`clanak_id`, `aktivnost_id`) VALUES
(35, 16),
(35, 17),
(36, 18),
(37, 16),
(37, 18),
(37, 20);

-- --------------------------------------------------------

--
-- Table structure for table `destinacija`
--

CREATE TABLE `destinacija` (
  `id` int(11) NOT NULL,
  `ime` varchar(255) NOT NULL,
  `opis` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `destinacija`
--

INSERT INTO `destinacija` (`id`, `ime`, `opis`) VALUES
(16, 'Srbija', 'Opis destinacije'),
(17, 'Makedonija', 'Makedonski opis destinacije'),
(18, 'Bosna i Hercegovina', 'Republika srpska\n');

-- --------------------------------------------------------

--
-- Table structure for table `komentar`
--

CREATE TABLE `komentar` (
  `id` int(11) NOT NULL,
  `tekst` text NOT NULL,
  `datum_kreiranja` datetime NOT NULL,
  `clanak_id` int(11) DEFAULT NULL,
  `autor_ime` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `komentar`
--

INSERT INTO `komentar` (`id`, `tekst`, `datum_kreiranja`, `clanak_id`, `autor_ime`) VALUES
(14, 'komentar\n', '2024-06-13 14:34:45', 35, 'moje ime');

-- --------------------------------------------------------

--
-- Table structure for table `korisnik`
--

CREATE TABLE `korisnik` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `ime` varchar(255) NOT NULL,
  `prezime` varchar(255) NOT NULL,
  `tip_korisnika` enum('uredjivac','admin') NOT NULL,
  `status` enum('aktivan','neaktivan') NOT NULL,
  `lozinka` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `korisnik`
--

INSERT INTO `korisnik` (`id`, `email`, `ime`, `prezime`, `tip_korisnika`, `status`, `lozinka`) VALUES
(2, 'vuk123@gmail.com', 'vuk', 'sijakovic', 'admin', 'aktivan', 'f21ba6785827859f2d614029d3794bedab05966d3b7c83fb0371a406b9a5933e'),
(3, 'email@email.com', 'vuk', 'milos', 'uredjivac', 'aktivan', '2c5819121fff961907a6235cba5b60371a3c01cc939b1bc22b9445f7ddc24bf5'),
(4, 'test@test.com', 'test', 'test', 'admin', 'aktivan', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b'),
(5, 'vuk@vuk.com', 'neki', 'mail', 'uredjivac', 'aktivan', '2c5819121fff961907a6235cba5b60371a3c01cc939b1bc22b9445f7ddc24bf5');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aktivnost`
--
ALTER TABLE `aktivnost`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `clanak`
--
ALTER TABLE `clanak`
  ADD PRIMARY KEY (`id`),
  ADD KEY `autor_id` (`autor_id`),
  ADD KEY `destinacija_id` (`destinacija_id`);

--
-- Indexes for table `clanak_aktivnost`
--
ALTER TABLE `clanak_aktivnost`
  ADD PRIMARY KEY (`clanak_id`,`aktivnost_id`),
  ADD KEY `aktivnost_id` (`aktivnost_id`);

--
-- Indexes for table `destinacija`
--
ALTER TABLE `destinacija`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `komentar`
--
ALTER TABLE `komentar`
  ADD PRIMARY KEY (`id`),
  ADD KEY `clanak_id` (`clanak_id`);

--
-- Indexes for table `korisnik`
--
ALTER TABLE `korisnik`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aktivnost`
--
ALTER TABLE `aktivnost`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `clanak`
--
ALTER TABLE `clanak`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `destinacija`
--
ALTER TABLE `destinacija`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `komentar`
--
ALTER TABLE `komentar`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `korisnik`
--
ALTER TABLE `korisnik`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `clanak`
--
ALTER TABLE `clanak`
  ADD CONSTRAINT `clanak_ibfk_1` FOREIGN KEY (`autor_id`) REFERENCES `korisnik` (`id`),
  ADD CONSTRAINT `clanak_ibfk_2` FOREIGN KEY (`destinacija_id`) REFERENCES `destinacija` (`id`);

--
-- Constraints for table `clanak_aktivnost`
--
ALTER TABLE `clanak_aktivnost`
  ADD CONSTRAINT `clanak_aktivnost_ibfk_1` FOREIGN KEY (`clanak_id`) REFERENCES `clanak` (`id`),
  ADD CONSTRAINT `clanak_aktivnost_ibfk_2` FOREIGN KEY (`aktivnost_id`) REFERENCES `aktivnost` (`id`);

--
-- Constraints for table `komentar`
--
ALTER TABLE `komentar`
  ADD CONSTRAINT `komentar_ibfk_1` FOREIGN KEY (`clanak_id`) REFERENCES `clanak` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
