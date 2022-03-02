CREATE TABLE `migration_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `migration_type` varchar(50) NOT NULL,
  `migration_status` varchar(20) NOT NULL,
  `details` text NULL,
  `created_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);