package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = "com.example.entity") // 显式扫描实体类
@EnableJpaRepositories(basePackages = {"com.example.repository",  // 指定包路径
})
@ComponentScan(basePackages = {"com.example",        // 默认包
})
public class AiChatApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AiChatApplication.class);

    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    static StopWatch stopWatch = new StopWatch();


    public static void main(String[] args) {
        stopWatch.start();
        SpringApplication.run(AiChatApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("====================================");
//        System.out.println("应用启动成功！欢迎使用 AI Chat 系统");
//        System.out.println("====================================");

        // 模拟应用初始化逻辑（实际是你的业务代码）

        stopWatch.stop();
        printStartupMessage(stopWatch.getTotalTimeSeconds());
    }

    private void printStartupMessage(double startupTime) {
        // ASCII 艺术字（生成工具：https://patorjk.com/software/taag/）
        String asciiArt = " \"\"\"\n" +
                "        ┏━━━┓┏━━━┓┏━━━┓┏━━━┓\n" +
                "        ┃恭  ┃喜  ┃启   ┃动 ┃\n" +
                "        ┗━━━┛┗━━━┛┗━━━┛┗━━━┛\n" +
                "        \"\"\"";

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 使用 SLF4J 日志输出
        logger.info("\n" + asciiArt);
        logger.info("✅ AI Chat 服务启动成功！版本: v1.0.0");
        logger.info("🕒 启动耗时: {} 秒", startupTime);
        logger.info("🌐 运行环境: {}", activeProfile);
        logger.info("📅 当前时间: {}", currentTime);
        logger.info("====================================");
    }
}