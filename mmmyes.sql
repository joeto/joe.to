-- Generation Time: Jun 07, 2011 at 10:07 PM

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `bukkit`
--

-- --------------------------------------------------------

--
-- Table structure for table `alias`
--

CREATE TABLE IF NOT EXISTS `alias` (
  `Name` varchar(64) NOT NULL,
  `IP` varchar(64) NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Logins` int(11) NOT NULL,
  KEY `Name` (`Name`),
  KEY `IP` (`IP`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `j2bans`
--

CREATE TABLE IF NOT EXISTS `j2bans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `reason` text NOT NULL,
  `admin` varchar(64) NOT NULL,
  `unbantime` bigint(20) DEFAULT '0',
  `timeofban` bigint(20) DEFAULT '0',
  `unbanned` tinyint(1) DEFAULT '0',
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL,
  `world` varchar(32) NOT NULL,
  `server` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `j2groups`
--

CREATE TABLE IF NOT EXISTS `j2groups` (
  `server` tinyint(4) NOT NULL,
  `name` varchar(16) NOT NULL,
  `flags` varchar(26) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `j2groups` (`server`, `name`, `flags`) VALUES
(0, 'regular', 'fm'),
(0, 'admins', 'asftm'),
;


-- --------------------------------------------------------

--
-- Table structure for table `j2users`
--

CREATE TABLE IF NOT EXISTS `j2users` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `group` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `color` tinyint(4) NOT NULL,
  `flags` varchar(26) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `safeword` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `perms`
--

CREATE TABLE IF NOT EXISTS `perms` (
  `permission` varchar(255) NOT NULL,
  `flag` char(1) NOT NULL,
  `server` tinyint(4) NOT NULL,
  KEY `permission` (`permission`,`flag`,`server`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE IF NOT EXISTS `reports` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user` varchar(32) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `message` text COLLATE utf8_bin NOT NULL,
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `admin` varchar(32) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `reason` text COLLATE utf8_bin,
  `world` varchar(32) COLLATE utf8_bin NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL,
  `server` tinyint(1) NOT NULL,
  `time` int(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `warps`
--

CREATE TABLE IF NOT EXISTS `warps` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  `player` varchar(32) NOT NULL,
  `server` tinyint(1) NOT NULL,
  `flag` varchar(1) NOT NULL,
  `world` varchar(32) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `pitch` float NOT NULL,
  `yaw` float NOT NULL,
  PRIMARY KEY (`id`),
  KEY `server` (`server`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

