package cn.shierblog.douban.movie.crawler.processor;

import cn.shierblog.douban.movie.crawler.service.MovieInfoService;
import cn.shierblog.douban.movie.crawler.utils.RedisUtil;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: 2018/10/29  wantToMovieNumber 这玩意超出字段 这个是String类型的数据库是int类型的 回家改一下

/**
 * 处理 https://movie.douban.com/subject/xxx/ 这种页面
 * <p>
 * 例如：https://movie.douban.com/subject/1866479/
 *
 */
@Component
public class SubjectPageProcessor extends PatternProcessor {
    @Autowired
    private MovieInfoService movieInfoService;
    @Autowired
    private RedisUtil redis;
    private static int selectNumber = 1;
    private static  int stopNumber =1;
    private static String time;
   // private  Map<String, Object> movieInfo = new HashMap<>();
    //获取爬虫启动线程数目

    //首先从这个页面开始抓取数据
    public  SubjectPageProcessor() {
        super("https://movie\\.douban\\.com/subject/[0-9]+/?(\\?.*)?");
    }
    @Override
    public MatchOther processPage(Page page) {
        Selectable url = page.getUrl();
        String pageUrl = url.toString();
        Html html = page.getHtml();
        // 标签页
        if (url.toString().equals("https://movie.douban.com/tag/?view=type")) {
            List<String> TagList = html.xpath("//div[@class='article']").links().all();
            page.addTargetRequests(TagList);
            // 电影列表页
        } else if (pageUrl.contains("tag") && !pageUrl.contains("cloud")) {
            List<String> PagList = html.xpath("//div[@class='paginator']").links().all();
            page.addTargetRequests(PagList);
            List<String> FilmList = html.xpath("//div[@class='pl2']").links().all();
            page.addTargetRequests(FilmList);
            //电影详情页面
        } else if (url.regex("https://movie\\.douban\\.com/subject/[0-9]+/?(\\?.*)?").match()) {
            // 抽取数据
            //xpath:解析html语言   jsonpath：解析json
            //获取电影Id值
            String movieId = url.toString().split("/")[4];
            page.putField("movieId", StringUtils.isBlank(movieId) ? "" : movieId);
            //获取电影名称
            page.putField("movieName", html.xpath("//h1/span/text()").toString());
            // TODO 抽取其他字段
            //获取电影导演非空判断
            String movieDirector = html.xpath("//div[@id='info']//a[@rel='v:directedBy']/text()").toString();
            page.putField("movieDirector", StringUtils.isBlank(movieDirector) ? "" : movieDirector);
            //获取该电影想看人数并进行非空判断(截取：人想看)
            String wantToMovieNumber = html.xpath("//div[@class='subject-others-interests-ft']/a[2]/text()").toString();
            page.putField("wantToMovieNumber", StringUtils.isBlank(wantToMovieNumber) ? "0" : wantToMovieNumber.substring(0, wantToMovieNumber.length() - 3));
            //获取该电影看过的人数，并进行非空判断（截取：人看过）
            String watchedTheMovieNumber = html.xpath("//div[@class='subject-others-interests-ft']/a[1]/text()").toString();
            page.putField("watchedTheMovieNumber", StringUtils.isBlank(watchedTheMovieNumber) ? "0" : watchedTheMovieNumber.substring(0, watchedTheMovieNumber.length() - 3));
            //获取上映日期并进行非空判断
            String releaseDate = html.xpath("//div[@id='info']/span[@property='v:initialReleaseDate']/text()").toString();
            page.putField("releaseDate", StringUtils.isBlank(releaseDate) ? "" : releaseDate);
            //获取电影封面图片地址
            page.putField("movieImage", html.xpath("//div[@id='mainpic']/a/img/@src").toString());
            String a = html.xpath("//div[@id='mainpic']/a/img/@src").toString();
            //获取电影IMDb链接
            String movieIMDb = html.xpath("//div[@id='info']/a[@rel='nofollow']/text()").toString();
            page.putField("movieIMDb", StringUtil.isBlank(movieIMDb) ? "" : movieIMDb);
            //获取电影片长
            String movieDateLength = html.xpath("//div[@id='info']/span[@property='v:runtime']/text()").toString();
            page.putField("movieDateLength", StringUtil.isBlank(movieDateLength) ? "" : movieDateLength);
            //获取电影简介
            String movieIntroduction = html.xpath("//div[@id='link-report']/span/text()").toString();
            page.putField("movieIntroduction", StringUtil.isBlank(movieIntroduction) ? "" : movieIntroduction);
            //获取豆瓣评分
            String movieRatings = html.xpath("//strong[@class='ll rating_num']/tidyText()").toString();
            if (StringUtils.isBlank(movieRatings)) {
                page.putField("movieRatings", "0.0");
                // 获取电影观影评价总人数
                page.putField("movieRatingsNum", "0");
                // 5星
                page.putField("5Star", "0");
                // 4星
                page.putField("4Star", "0");
                // 3星
                page.putField("3Star", "0");
                // 2星
                page.putField("2Star", "0");
                // 1星
                page.putField("1Star", "0");
            } else {

                page.putField("movieRatings", movieRatings);
                // 评分人数
                int movieRatingsNum = Integer.parseInt(html.xpath("//span[@property='v:votes']/tidyText()").toString());
                page.putField("movieRatingsNum", movieRatingsNum);
                List<String> starNums = html.xpath("//div[@class='ratings-on-weight']//span[@class='rating_per']/text()").all();
                // 5星
                page.putField("5Star", starNums.get(0).substring(0, starNums.get(0).indexOf("%")));
                // 4星
                page.putField("4Star", starNums.get(1).substring(0, starNums.get(1).indexOf("%")));
                // 3星
                page.putField("3Star", starNums.get(2).substring(0, starNums.get(2).indexOf("%")));
                // 2星
                page.putField("2Star", starNums.get(3).substring(0, starNums.get(3).indexOf("%")));
                // 1星
                page.putField("1Star", starNums.get(4).substring(0, starNums.get(4).indexOf("%")));
            }
            //获取电影短评
            boolean movieEvaluateJudge = StringUtils.isBlank(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='short']/text()").toString());
            //进行非空判断
            if (!movieEvaluateJudge) {
                String movieEvaluate = StringUtils
                        .join(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='short']/text()")
                                .all().toArray(), "&#&#"); //此处使用&#&#将评论分隔开，防止有些评论有特殊符号，程序分组错误造成数组下标越界
                page.putField("movieEvaluate", movieEvaluate);
            } else {
                page.putField("movieEvaluate", "");
            }
            //获取电影短评，评论用户名称
            boolean movieEvaluateUserNameJudge = StringUtils.isBlank(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='comment-info']/a/text()").toString());
            //进行非空判断
            if (!movieEvaluateUserNameJudge) {
                String movieEvaluateUserName = StringUtils
                        .join(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='comment-info']/a/text()")
                                .all().toArray(), "&#&#");//此处使用&#&#将评论分隔开，防止有些昵称有特殊符号，程序分组错误造成数组下标越界
                page.putField("movieEvaluateUserName", movieEvaluateUserName);
            } else {
                page.putField("movieEvaluateUserName", "");

            }
            //获取电影短评，评论用户推荐
            boolean movieEvaluateUserRecommendJudge = StringUtils.isBlank(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='comment-info']/span[2]/@class").toString());
            //进行非空判断
            if (!movieEvaluateUserRecommendJudge) {
                String movieEvaluateUserRecommend = StringUtils
                        .join(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='comment-info']/span[2]/@class")
                                .all().toArray(), "/");
                page.putField("movieEvaluateUserRecommend", movieEvaluateUserRecommend);
            } else {
                page.putField("movieEvaluateUserRecommend", "");

            }
            //获取电影短评，评论用户评价时间
            boolean movieEvaluateUserCommentDateJudge = StringUtils.isBlank(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='comment-info']//span[@class='comment-time']/@title").toString());
            //进行非空判断
            if (!movieEvaluateUserCommentDateJudge) {
                String movieEvaluateUserCommentDate = StringUtils
                        .join(html.xpath("//div[@class='tab-bd']//div[@id='hot-comments']//span[@class='comment-info']//span[@class='comment-time']/@title")
                                .all().toArray(), "/");
                page.putField("movieEvaluateUserCommentDate", movieEvaluateUserCommentDate);
            } else {
                page.putField("movieEvaluateUserCommentDate", "");

            }
            //获取电影短评，用户评论点赞数
            boolean movieEvaluateUserCommentLikeJudge = StringUtils.isBlank(html.xpath("//div[@class='tab-bd']//div[@class='comment-item']//span[@class='comment-vote']/span[@class='votes']/text()").toString());
            //进行非空判断
            if (!movieEvaluateUserCommentLikeJudge) {
                String movieEvaluateUserCommentLike = StringUtils
                        .join(html.xpath("//div[@class='tab-bd']//div[@class='comment-item']//span[@class='comment-vote']/span[@class='votes']/text()")
                                .all().toArray(), "/");
                page.putField("movieEvaluateUserCommentLike", movieEvaluateUserCommentLike);
            } else {
                page.putField("movieEvaluateUserCommentLike", "0");

            }
            //获取电影主演并进行非空判断
            boolean movieActorJudge = StringUtils.isBlank(html.xpath("//div[@id='info']//a[@rel='v:starring']/text()").toString());
            //进行非空判断
            if (!movieActorJudge) {
                String movieActor = StringUtils
                        .join(html.xpath("//div[@id='info']//a[@rel='v:starring']/text()")
                                .all().toArray(), "/");
                page.putField("movieActor", movieActor);
            } else {
                page.putField("movieActor", "");
            }
            //获取电影类型
            // 进行非空判断
            boolean movieTypeJudge = StringUtil.isBlank(html.xpath(
                    "//div[@id='info']//span[@property='v:genre']/text()")
                    .toString());
            if (!movieTypeJudge) {
                String movieType = StringUtils
                        .join(html.xpath("//div[@id='info']//span[@property='v:genre']/text()")
                                .all().toArray(), "/");
                page.putField("movieType", movieType);
            } else {
                page.putField("movieType", "");
            }
            //获取制片国家
            String filmInfoStr = html.xpath("//div[@id='info']/text()")
                    .toString();
            String[] filmInfo = {"", ""};
            filmInfo = StringUtil.isBlank(filmInfoStr) ? filmInfo : filmInfoStr
                    .replaceAll(" / ", "").trim().split("  ");
            page.putField("country", filmInfo.length > 0 ? filmInfo[0] : " ");
            //获取电影语言
                if (filmInfo.length >1&&filmInfo[1]!= null) {
                        page.putField("language", filmInfo.length > 1 ? filmInfo[1] : " ");
                }else {
                    page.putField("language", " ");
                }
          }
        // 继续匹配其他处理器
        return MatchOther.YES;
    }

    @Override
    public  synchronized MatchOther  processResult(ResultItems resultItems, Task task) {
            Map<String, Object> movieInfo = new HashMap<>();
            String movieId = resultItems.get("movieId").toString();
            movieInfo.put("movieId", movieId);
            movieInfo.put("movieName", resultItems.get("movieName").toString());
            movieInfo.put("movieDirector", resultItems.get("movieDirector").toString());
            //对想看电影人数数据处理保存入库
            Integer wantToMovieNumber = Integer.parseInt(resultItems.get("wantToMovieNumber").toString());
            movieInfo.put("wantToMovieNumber", wantToMovieNumber);
            //对看过该电影人数数据处理保存入库
            Integer watchedTheMovieNumber = Integer.parseInt(resultItems.get("watchedTheMovieNumber").toString());
            movieInfo.put("watchedTheMovieNumber", watchedTheMovieNumber);
            movieInfo.put("releaseDate", resultItems.get("releaseDate").toString());
            movieInfo.put("movieImage", resultItems.get("movieImage").toString());
            movieInfo.put("movieIMDb", resultItems.get("movieIMDb").toString());
            movieInfo.put("movieDirector", resultItems.get("movieDirector").toString());
            movieInfo.put("movieDateLength", resultItems.get("movieDateLength").toString());
            movieInfo.put("movieIntroduction", resultItems.get("movieIntroduction").toString());
            //将电影评分转换为Double类型保存入库
            double movieRatings = Double.parseDouble(resultItems.get("movieRatings").toString());
            movieInfo.put("movieRatings", movieRatings);
            movieInfo.put("movieRatingsNum", Integer.parseInt(resultItems.get("movieRatingsNum").toString()));
            movieInfo.put("1Star", resultItems.get("1Star").toString());
            movieInfo.put("2Star", resultItems.get("2Star").toString());
            movieInfo.put("3Star", resultItems.get("3Star").toString());
            movieInfo.put("4Star", resultItems.get("4Star").toString());
            movieInfo.put("5Star", resultItems.get("5Star").toString());
            movieInfo.put("movieActor", resultItems.get("movieActor").toString());
            movieInfo.put("movieType", resultItems.get("movieType").toString());
            movieInfo.put("country", resultItems.get("country").toString());
            movieInfo.put("language", resultItems.get("language").toString());

            //用户评论(数组)
            String[] movieEvaluate = resultItems.get("movieEvaluate").toString().split("&#&#");
            //评论的用户名称(数组)
            String[] movieEvaluateUserName = resultItems.get("movieEvaluateUserName").toString().split("&#&#");
            //用户推荐指数(数组)
            String[] movieEvaluateUserRecommend = resultItems.get("movieEvaluateUserRecommend").toString().split("/");
            //用户评论时间(数组)
            String[] movieEvaluateUserCommentDate = resultItems.get("movieEvaluateUserCommentDate").toString().split("/");
            //用户评论点赞数(数组) 转换为int型好对数据进行筛选
            String[] strMovieEvaluateUserCommentLike = resultItems.get("movieEvaluateUserCommentLike").toString().split("/");
            int[] movieEvaluateUserCommentLike = new int[movieEvaluate.length];
            for (int i = 0; i <movieEvaluateUserCommentLike.length; i++) {
                movieEvaluateUserCommentLike[i] = Integer.parseInt(strMovieEvaluateUserCommentLike[i]);
            }
            //将用户评论等相关信息添加到Map中，进行动态SQL插入
            List userCommentlist = new ArrayList();
            for (int i = 0; i < movieEvaluate.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("movieId", movieId);
                item.put("movieEvaluateUserName", movieEvaluateUserName[i]);
                item.put("movieEvaluate", movieEvaluate[i]);
                item.put("movieEvaluateUserRecommend", movieEvaluateUserRecommend[i].substring(7, 10));
                item.put("movieEvaluateUserCommentDate", movieEvaluateUserCommentDate[i]);
                item.put("movieEvaluateUserCommentLike", movieEvaluateUserCommentLike[i]);
                userCommentlist.add(item);
            }


            //保存电影相关信息
             movieInfoService.saveMovieInfo(movieInfo);
            //保存电影评论相关信息
             movieInfoService.saveUserComment(userCommentlist);






            System.out.println("电影Id：" + resultItems.get("movieId").toString());
            System.out.println("电影名称：" + resultItems.get("movieName").toString());
            System.out.println("电影评分：" + resultItems.get("movieRatings").toString());
            System.out.println("电影影评人数：" + resultItems.get("movieRatingsNum").toString());
            System.out.println("想看该电影的人数：" + wantToMovieNumber);
            System.out.println("看过该电影人数：" + watchedTheMovieNumber);
            System.out.println("上映日期：" + resultItems.get("releaseDate").toString());
            System.out.println("电影类型：" + resultItems.get("movieType").toString());
            System.out.println("语言：" + resultItems.get("language").toString());
            System.out.println("制片国家：" + resultItems.get("country").toString());
            System.out.println("主演：" + resultItems.get("movieActor").toString());
            System.out.println("导演：" + resultItems.get("movieDirector").toString());
            System.out.println("电影IMDb：" + resultItems.get("movieIMDb").toString());
            System.out.println("封面图片地址：" + resultItems.get("movieImage").toString());
            System.out.println("电影片长：" + resultItems.get("movieDateLength").toString());
            System.out.println("电影简介：" + resultItems.get("movieIntroduction").toString());
            System.out.println("电影评论内容：" + resultItems.get("movieEvaluate").toString());
            System.out.println("电影评论时间：" + resultItems.get("movieEvaluateUserCommentDate").toString());
            System.out.println("电影评论点用户名：" + resultItems.get("movieEvaluateUserName").toString());
            System.out.println("电影评论点赞数：" + resultItems.get("movieEvaluateUserCommentLike").toString());
            System.out.println("电影评论推荐指数：" + resultItems.get("movieEvaluateUserRecommend").toString().substring(7, 10));
            System.out.println("-------------------------------------------------------------------------");
            // 继续匹配其他处理器
            return MatchOther.YES;
        }


}
