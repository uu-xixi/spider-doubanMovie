/*
 Navicat Premium Data Transfer

 Source Server         : Ali cloud
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : 47.96.97.87:3306
 Source Schema         : douban_crawler

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 01/09/2019 01:02:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for movie_info
-- ----------------------------
DROP TABLE IF EXISTS `movie_info`;
CREATE TABLE `movie_info`  (
  `movie_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电影id',
  `movie_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电影名称',
  `1star` double(11, 1) NULL DEFAULT NULL COMMENT '1颗小星星',
  `2star` double(11, 1) NULL DEFAULT NULL COMMENT '2颗小星星',
  `3star` double(11, 1) NULL DEFAULT NULL COMMENT '3颗小星星',
  `4star` double(11, 1) NULL DEFAULT NULL COMMENT '4颗小星星',
  `5star` double(11, 1) NULL DEFAULT NULL COMMENT '5颗小星星',
  `release_date` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上映日期',
  `movie_ratings` double(11, 1) UNSIGNED NULL DEFAULT NULL COMMENT '电影评分',
  `movie_ratings_num` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '影评总人数',
  `want_to_movie_number` int(11) NULL DEFAULT NULL COMMENT '想看电影的总人数',
  `watched_the_movie_number` int(11) NULL DEFAULT NULL COMMENT '看过电影的人数',
  `movie_director` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '导演',
  `country` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '制片国家',
  `language` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电影语言',
  `movie_actor` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '主演',
  `movie_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电影类型',
  `movie_image` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电影封面图片',
  `movie_imdb` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电影imdb',
  `movie_date_length` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电影时长',
  `movie_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '电影简介',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '保存时间',
  PRIMARY KEY (`movie_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
