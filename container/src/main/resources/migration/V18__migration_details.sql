CREATE TABLE `data_migrations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL,
  `failure_message` text,
  `status` varchar(50) NULL,
  `created_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);