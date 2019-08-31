package cn.shierblog.douban.movie.crawler.service;

import cn.shierblog.douban.movie.crawler.dao.MovieInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @auther zyb
 * @date 2018/10/24 22:28
 * @Comment TODO
 * 电影信息类
 */
@Service
public class MovieInfoService {
 @Autowired
 private MovieInfoDao movieInfoDao;
    private static  int size = 0;
    /**
     * 保存电影信息
     * @param
     * @return movieMap
     */
    public int saveMovieInfo(Map<String, Object> movieInfo) {
    return  movieInfoDao.saveMovieInfo(movieInfo);
    }

    /**
     * 保存电影评论
     */
    public int saveUserComment(List userCommentlist) {
        return movieInfoDao.saveUserComment(userCommentlist);
    }
}
