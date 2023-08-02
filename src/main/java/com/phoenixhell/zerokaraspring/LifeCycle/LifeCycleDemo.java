package com.phoenixhell.zerokaraspring.LifeCycle;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author チヨウ　カツヒ
 * @date 2023-08-02 22:19
 *
 *    bean 的生命周期, 以及 bean 后处理器
 *
 *     学到了什么
 *         a. Spring bean 生命周期各个阶段
 *         b. 模板设计模式, 大流程已经固定好了, 通过接口回调(bean 后处理器)扩展
 *
 *         注意 druid 会再次扫描数据源 一起排除 类似还有
 *         {@link org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration}
 *         {@link org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration}
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class LifeCycleDemo {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LifeCycleDemo.class, args);
        context.close();
    }
}
