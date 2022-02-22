CREATE TABLE `query_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `team_id` bigint(20) NOT NULL,
  `warehouse_id` varchar(255) NOT NULL,
  `model_name` varchar(50) NOT NULL,
  `model_type` varchar(20) NOT NULL,
  `model_details` text NOT NULL,
  `query_pk` text NOT NULL,
  `created_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);