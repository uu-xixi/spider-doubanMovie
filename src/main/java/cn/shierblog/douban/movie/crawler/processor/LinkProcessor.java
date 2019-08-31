package cn.shierblog.douban.movie.crawler.processor;
import cn.shierblog.douban.movie.crawler.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 处理页面，获取其他链接
 */
@Component
public class LinkProcessor implements SubPageProcessor {
    @Autowired
    private RedisUtil redis;
    @Override
    public MatchOther processPage(Page page) {

            //将所有找到的列表页也加到待下载的URL
        List<String> links =
            page.getHtml().links().regex("(https://movie\\.douban\\.com/.*)").all();
        Stream<String> stream = links.stream();
        links =
            // 排重
            stream.distinct()
                // 过滤跳转链接
                    .filter(link -> !link.matches("https://movie.douban.com/ticket/redirect/?(\\?.*)?"))
                    //.collect (Collectors.toList());
                    .collect(ArrayList::new,ArrayList::add,ArrayList::addAll);
        page.addTargetRequests(links);//从页面发现后续的url地址来抓取



        return MatchOther.YES;
    }

    @Override
    public boolean match(Request page) {
        // 所有页面都匹配
        return true;
    }
}
