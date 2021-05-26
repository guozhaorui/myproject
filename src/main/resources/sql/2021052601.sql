CREATE TABLE `person` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `person_id` bigint(20) unsigned NOT NULL COMMENT '人员ID',
   person_name VARCHAR(50) NOT NULL COMMENT '人员姓名',
   person_sex  TINYINT(4)  COMMENT '人员性别',
   person_age  TINYINT(4)  COMMENT '人员年龄',
   person_work TINYINT(4)  COMMENT '人员工作',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'gmt_create',
  `gmt_modified` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_person_id` (`person_id`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='人员主表';