package cn.shierblog.douban.movie.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("cn.shierblog.douban.movie.crawler")
public class Appilcation {

    public static void main(String[] args) {
        SpringApplication.run(Appilcation.class, args);
    }
}
