--
-- Database: `sastix_cms`
--

-- --------------------------------------------------------

--
-- Table structure for table `resource`
--
DROP TABLE IF EXISTS `resource`;

CREATE TABLE IF NOT EXISTS `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author` varchar(255) NOT NULL,
  `media_type` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `path` longtext NOT NULL,
  `resource_tenant_id` varchar(255) NOT NULL,
  `uid` varchar(255) NOT NULL,
  `uri` varchar(255) NOT NULL,
  `resource_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8aryy958k6ueg24n3ufyuksfh` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `revision`
--
DROP TABLE IF EXISTS `revision`;

CREATE TABLE IF NOT EXISTS `revision` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` date DEFAULT NULL,
  `deleted_at` date DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `archived_resource_id` int(11) DEFAULT NULL,
  `parent_resource_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_86g13py41me59mnb4yvx9kteg` (`archived_resource_id`),
  KEY `FK_na89w0f2b0qse62s8r6u2xnhb` (`parent_resource_id`),
  KEY `FK_ryvmivnoxgv3uvrwq4cgls75u` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `tenant`
--
DROP TABLE IF EXISTS `tenant`;

CREATE TABLE IF NOT EXISTS `tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `checksum` text,
  `tenant_id` varchar(255) DEFAULT NULL,
  `volume` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `resource`
--
ALTER TABLE `resource`
  ADD CONSTRAINT `FK_8aryy958k6ueg24n3ufyuksfh` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`);

--
-- Constraints for table `revision`
--
ALTER TABLE `revision`
  ADD CONSTRAINT `FK_ryvmivnoxgv3uvrwq4cgls75u` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`),
  ADD CONSTRAINT `FK_86g13py41me59mnb4yvx9kteg` FOREIGN KEY (`archived_resource_id`) REFERENCES `resource` (`id`),
  ADD CONSTRAINT `FK_na89w0f2b0qse62s8r6u2xnhb` FOREIGN KEY (`parent_resource_id`) REFERENCES `resource` (`id`);
