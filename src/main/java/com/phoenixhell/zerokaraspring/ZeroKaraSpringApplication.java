package com.phoenixhell.zerokaraspring;

import com.phoenixhell.zerokaraspring.event.MyEventPublisher;
import com.phoenixhell.zerokaraspring.event.UserRegisteredEvent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

/**
 * BeanFactory 与 ApplicationContext 的区别
 *
 *
 */
@SpringBootApplication
public class ZeroKaraSpringApplication {

    @SneakyThrows
    public static void main(String[] args) {

        /**
         * 1.BeanFactory是什么
         * BeanFactory 是 ApplicationContext的父接口
         *      BeanFactory才是spring的核心容器，主要的ApplicationContext的实现都【组合】了它的功能
         */
        //run方法返回的就是 applicationContext 对象 （就是spring容器）
        //java class diagrams
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ZeroKaraSpringApplication.class, args);
        //getBean 功能是 BeanFactory 接口提供的功能
        //ctrl + alt + 单击跳到类的实现
        //applicationContext.getBean("aaa");

        System.out.println(applicationContext);


        /**
         * 2  BeanFactory能干什么
         * Ctrl + F12 列出所有接口方法定义
         *   - 表面上只有getBean，但是看它的功能得看它的实现类 DefaultListableBeanFactory。
         *   - 实际上控制反转，基本的依赖注入，直至Bean 的生命周期的各种功能，都由它的实现类提供。
         *
         */

        // 反射 拿到 DefaultListableBeanFactory 单例集合的 singletonObjects 集合 再从集合中拿到BeanFactory的 单例对象set
        try {
            //getDeclaredFields(): 获取当前运行时类中声明的所属性。（不包含父类中声明的属性)
            //getDeclaredField(String fieldName):获取运行时类中指定变量名的属性
            Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
            //私有成员变量的需要setAccessible 保证当前属性是可访问的
            singletonObjects.setAccessible(true);
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            // set get 获取、设置指定对象的此属性值
            //这里不要理解查了 这不是map获取key get 参数是一个实例对象
            //下面这段 比如   name.set(p,"Tom"); 是设置 p对象上name属性为tom  name.get(p); 是获取p对象上name属性的值
            //同理  singletonObjects.get(beanFactory); 是获取 beanFactory 对象上 singletonObjects 的属性
            //这个属性也就是也给 concurrentMap也就可以把这
            ConcurrentMap<String, Object> concurrentMap = (ConcurrentMap<String, Object>) singletonObjects.get(beanFactory);
            concurrentMap.forEach((key, value) -> {
                System.out.println(key+":======>"+value);
            });
        }catch (Exception e) {
            throw new RuntimeException(e);
        }


        /**
         * ApplicationContext  多的  主要就体现再 继承的这个4个接口上
         *
         * - *`MessageSource`  :*   国际化
         * - *`ResourcePatternResolver`* 通配符方式获取一组 Resource 资源
         * - *`EnvironmentCapable`* 整合 Environment 环境（能通过它获取各种来源的配置信息）
         * - *`ApplicationEventPublisher`* 事件发布与监听，实现组件之间的解耦
         */

        /**
         * 国际化
         *
         * getMessage的作用据说根据不同的key 找到不同语言的翻译结果
         *  LocaleContextHolder.getLocale()  是选择当前机器语言 也可用指定
         */

        System.out.println("-----------------------------------国际化------------------------------------");
        System.out.println(applicationContext.getMessage("user.username", null, Locale.JAPAN));
        System.out.println(applicationContext.getMessage("user.username", null, Locale.CHINA));
        System.out.println(applicationContext.getMessage("user.username", null,  Locale.ENGLISH));
        System.out.println(applicationContext.getMessage("user.username", null, LocaleContextHolder.getLocale()));
        System.out.println(applicationContext.getMessage("user.username", null, Locale.getDefault()));


        /**
         * 获取Resource 资源
         * classpath： 是类路径（也就是resource下的资源）
         * file： 是磁盘路径
         * 注意
         *    classpath: 通配符只是到类路劲下找，jar包里面是找不到的
         *    需要改成 classpath*: 就可以再jar包里面寻找
         *
         */
        System.out.println("-----------------------------------获取Resource资源------------------------------------");
        Resource[] resources = applicationContext.getResources("classpath*:META-INF/spring.factories");
        for (Resource resource : resources) {
            System.out.println(resource);
        }


        /**
         * Environment 环境变量
         * getProperty 不区分大小写
         * 可用取到系统环境变量 也可用取到spring中的变量 比如application.yaml中的值
         *
         * 注意不要和 System环境变量 搞混了
         */
        System.out.println("-----------------------------------Environment环境变量------------------------------------");
        System.out.println(applicationContext.getEnvironment().getProperty("java_home"));
        System.out.println(applicationContext.getEnvironment().getProperty("server.port"));
        System.out.println(applicationContext.getEnvironment().getProperty("server.port"));

        System.out.println("-----------------------------------System环境变量------------------------------------");
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.arch"));
        System.out.println(System.getProperty("os.version"));
        System.out.println(System.getProperty("user.name"));


        /**
         * ApplicationEventPublisher  事件发布与监听，实现组件之间的解耦
         *
         * publishEvent 方法 来自 ApplicationEventPublisher 接口
         * 需要写一个event类来publish  继承ApplicationEvent接口就行  就是 event 包中的 UserRegisteredEvent
         *
         * 有Publisher 就必须有receiver 监听器 listener
         * 任何一个类都可用作为监听器
         *
         */
        System.out.println("-----------------------------------ApplicationEventPublisher------------------------------------");
        //applicationContext.publishEvent(new UserRegisteredEvent(applicationContext));

        MyEventPublisher myEventPublisher = applicationContext.getBean(MyEventPublisher.class);
        myEventPublisher.register();

    }
}
