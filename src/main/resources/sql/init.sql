drop database if exists OnlineDriveSource;
create database OnlineDriveSource;
use OnlineDriveSource;
drop table if exists t_file;
create table t_file
(
    file_id        int auto_increment comment '编号',
    file_name      nvarchar(30) not null comment '文件名',
    contributor    nvarchar(30) not null comment '贡献者',
    file_type      nvarchar(30) not null comment '文件类型(后缀名)',
    file_size      nvarchar(30) not null comment '文件大小',
    upload_time    nvarchar(30) not null comment '上传时间',
    download_times int     default 0 comment '下载次数',
    deleted        boolean default false comment '逻辑删除',
    remarks        text,
    primary key (file_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
create index index_account on t_file (file_name ASC);

drop table if exists t_user;
create table t_user
(
    account   varchar(30)   not null comment '账号',
    password  varchar(30)   not null comment '密码',
    username  varchar(30)   not null comment '用户名',
    status    int default 0 not null comment '身份', /* 1表示管理员，0表示普通用户*/
    telephone varchar(30)   not null comment '手机号码',
    email     varchar(50)   not null comment '邮箱',
    primary key (account)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
create index index_account on t_user (account ASC);


