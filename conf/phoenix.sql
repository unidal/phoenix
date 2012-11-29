CREATE TABLE `version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain` varchar(32) NOT NULL COMMENT '架构项目名称，目前只有: kernel',
  `version` varchar(32) NOT NULL COMMENT '版本号',
  `description` varchar(200) NOT NULL COMMENT '版本描述',
  `release_notes` varchar(200) NOT NULL COMMENT 'Release Notes',
  `status` int(1) NOT NULL COMMENT '状态，可选值: 0 - active, 1 - inactive',
  `created_by` varchar(64) NOT NULL COMMENT '创建者',
  `terminated_by` varchar(64) NULL COMMENT '终止者',
  `creation_date` datetime NOT NULL COMMENT '创建时间',
  `last_modified_date` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='版本信息表';

CREATE TABLE `deployment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain` varchar(32) NOT NULL COMMENT '业务项目名称，如: shop-web',
  `war_type` int(1) NOT NULL COMMENT '类型，0 - kernel, 1 - application',
  `war_version` varchar(32) NOT NULL COMMENT '版本，如: 0.1',
  `strategy` varchar(32) NOT NULL COMMENT '部署策略',
  `error_policy` varchar(32) NOT NULL COMMENT '出错处理策略',
  `status` int(1) NOT NULL COMMENT '部署状态，可选值: 1 - created, 2 - deploying, 3 - completed all successful, 4 - completed with partial failures, 5 - failed, 8 - aborted, 9 - cancelled',
  `deployed_by` varchar(64) NOT NULL COMMENT '部署者',
  `begin_date` datetime NULL COMMENT '部署开始时间',
  `end_date` datetime NULL COMMENT '部署结束时间',
  `creation_date` datetime NOT NULL COMMENT '创建时间',
  `last_modified_date` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务部署主表';

CREATE TABLE `deployment_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deployment_id` int(11) NOT NULL COMMENT '部署ID',
  `ip_address` varchar(32) DEFAULT NULL COMMENT '业务主机IP地址',
  `kernel_version` varchar(32) NOT NULL COMMENT '版本，如: 0.1',
  `app_version` varchar(32) NOT NULL COMMENT '版本，如: 1.0.1',
  `status` int(1) NOT NULL COMMENT '部署状态，可选值: 1 - created, 2 - deploying, 3 - successful, 5 - failed, 8 - aborted, 9 - cancelled',
  `begin_date` datetime NULL COMMENT '部署开始时间',
  `end_date` datetime NULL COMMENT '部署结束时间',
  `raw_log` MediumText NULL COMMENT '原始日志',
  `creation_date` datetime NOT NULL COMMENT '创建时间',
  `last_modified_date` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务部署详细表';


