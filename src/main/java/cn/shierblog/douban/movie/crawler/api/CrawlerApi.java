package cn.shierblog.douban.movie.crawler.api;

import cn.shierblog.douban.movie.crawler.service.CrawlerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CrawlerApi {

    @Resource
    private CrawlerService crawlerService;

    @RequestMapping("/start")
    public Object start() {
        return crawlerService.start().getStatus();
    }

    @RequestMapping("/stop")
    public Object stop() {
        return crawlerService.stop().getStatus();
    }

}
