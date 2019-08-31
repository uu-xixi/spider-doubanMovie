package cn.shierblog.douban.movie.crawler.service;

import cn.shierblog.douban.movie.crawler.processor.LinkProcessor;
import cn.shierblog.douban.movie.crawler.processor.SubjectPageProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;
import us.codecraft.webmagic.handler.CompositePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;

@Service
public class CrawlerService {

    @Resource
    private SubjectPageProcessor subjectPageProcessor;
    @Resource
    private LinkProcessor linkProcessor;

//    @Autowired
//    private JedisPool jedisPool;

    private Spider spider;

    public Spider getSpider() {
        return spider;
    }

    /**
     * 启动
     */
    public Spider start() {
        if (spider != null && !Spider.Status.Running.equals(spider.getStatus())) {
            spider.start();
        }
        return spider;
    }

    /**
     * 停止
     */
    public Spider stop() {
        if (spider != null && Spider.Status.Running.equals(spider.getStatus())) {
            spider.stop();
        }
        return spider;
    }

    /**
     * 初始化
     * 以下数据受到爬虫随机爬取的连接和过滤连接所耗时不同，数据可能有所偏差
     * 先成为3条，抓取间隔为1000，获取50条记录时间平均为33秒
     * 线程：3，抓取间隔：2000 容易出现403
     *      爬取第一条记录的时间2018-10-31 11:04:02
     *      爬取50条数据所用的时间：2018-10-31 11:04:55
     * 线程：3，抓取间隔：3000  偶尔出现time out等   100条数据：2分52秒
     *      爬取第一条记录的时间2018-10-31 11:38:39
     *      爬取50条数据所用的时间：2018-10-31 11:39:54
     *      爬取100条数据所用的时间：2018-10-31 11:41:31
     *      爬取150条数据所用的时间：2018-10-31 11:46:10
     * 线程：3，不设置抓取间隔 爬取较慢，稳定性较好 100条数据中暂无出现time out等 100条数据：3分28秒
     *      爬取第一条记录的时间2018-10-31 14:37:04
     *      爬取50条数据所用的时间：2018-10-31 14:38:57
     *      爬取100条数据所用的时间：2018-10-31 14:41:32
     * 线程：3，抓取间隔：4000 爬取较慢，稳定性较好 100条数据中暂无出现time out等  100条数据：3分48秒
     *      爬取第一条记录的时间2018-10-31 14:21:41
     *      爬取50条数据所用的时间：2018-10-31 14:23:12
     *      爬取100条数据所用的时间：2018-10-31 14:25:19
     *      爬取150条数据所用的时间：2018-10-31 14:31:26
     */
    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        // 配置网站参数
        Site site = Site.me();
        site.setRetryTimes(1);//设置重试次数
        site.setSleepTime(4000);//设置抓取间隔


        // 页面处理程序
        CompositePageProcessor processor = new CompositePageProcessor(site);
        processor.addSubPageProcessor(linkProcessor);
        processor.addSubPageProcessor(subjectPageProcessor);

        // 结果处理
        CompositePipeline pipeline = new CompositePipeline();
        pipeline.addSubPipeline(subjectPageProcessor);

        // 初始化爬虫
        spider = Spider.create(processor)
            .addUrl("https://movie.douban.com/")//爬取地址
            .addPipeline(pipeline)
                .thread(3);//开始几个线程开始爬
       // spider.setScheduler(new RedisScheduler(System.getProperty("spring.redis.host")));

    }

}
