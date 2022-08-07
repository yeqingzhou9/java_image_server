-- 先删除，再创建数据库
drop database if exists image_system;
create database image_system character set utf8mb4;

use image_system;

-- 准备表
-- 注：图片表的字段，转为实体类的成员变量（名称会关联前后端接口）
create table image_info(
    image_id int primary key auto_increment comment '主键id',
    image_name varchar(50) comment '图片名称',
    size bigint comment '图片大小',
    upload_time datetime comment '图片上传日期',
    md5 varchar(128) comment 'md5值，用于校验图片唯一',
    content_type varchar(50) comment '数据类型，上传图片时，请求数据就包含了Content_Type',
    path varchar(1024) comment '图片的路径：相对路径'
);
