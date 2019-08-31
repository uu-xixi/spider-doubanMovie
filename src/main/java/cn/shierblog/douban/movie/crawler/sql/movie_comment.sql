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

 Date: 01/09/2019 01:02:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for movie_comment
-- ----------------------------
DROP TABLE IF EXISTS `movie_comment`;
CREATE TABLE `movie_comment`  (
  `movie_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电影id',
  `movie_cids` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户评论id',
  `movie_evaluate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '电影评论',
  `movie_evaluate_user_recommend` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推荐指数',
  `movie_evaluate_user_comment_date` datetime(0) NULL DEFAULT NULL COMMENT '用户评论时间',
  `movie_evaluate_user_comment_like` int(11) NULL DEFAULT NULL COMMENT '用户评论点赞',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `movie_evaluate_user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户评论名称',
  PRIMARY KEY (`movie_cids`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
