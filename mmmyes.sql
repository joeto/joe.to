CREATE TABLE IF NOT EXISTS `blocks_0` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `player` varchar(32) NOT NULL DEFAULT '-',
  `replaced` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `z` int(11) NOT NULL DEFAULT '0',
  `extra` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `coords` (`y`,`x`,`z`),
  KEY `type` (`type`),
  KEY `replaced` (`replaced`),
  KEY `player` (`player`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=282 ;

-- --------------------------------------------------------

--
-- Table structure for table `j2bans`
--

CREATE TABLE IF NOT EXISTS `j2bans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `reason` text NOT NULL,
  `admin` varchar(64) NOT NULL,
  `unbantime` bigint(20) DEFAULT '0',
  `timeofban` bigint(20) DEFAULT '0',
  `unbanned` tinyint(1) DEFAULT '0',
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
(0, 'regular', 'f');

-- --------------------------------------------------------

--
-- Table structure for table `j2users`
--

CREATE TABLE IF NOT EXISTS `j2users` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `group` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `color` tinyint(4) NOT NULL,
  `flags` varchar(26) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
