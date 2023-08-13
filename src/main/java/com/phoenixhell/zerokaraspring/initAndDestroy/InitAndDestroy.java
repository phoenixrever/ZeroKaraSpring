package com.phoenixhell.zerokaraspring.initAndDestroy;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/*
    初始化和销毁的执行顺序
 */
@SpringBootApplication(exclude ={ DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class InitAndDestroy {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(InitAndDestroy.class, args);
        context.close();
        /*
            学到了什么
                a. Spring 提供了多种初始化和销毁手段
                b. Spring 的面试有多么地卷
         */
    }

    @Bean(initMethod = "init3")
    public Bean1 bean1() {
        return new Bean1();
    }

    @Bean(destroyMethod = "destroy3")
    public Bean2 bean2() {
        return new Bean2();
    }
}
