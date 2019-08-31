package cn.shierblog.douban.movie.crawler.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MovieInfoDao {
    /**
     * 保存电影信息
     * @param movieInfo
     * @return
     */
    int saveMovieInfo(Map<String, Object> movieInfo);

    int saveUserComment(List userCommentlist);
}
