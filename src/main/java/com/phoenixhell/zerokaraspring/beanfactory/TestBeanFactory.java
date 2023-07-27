package com.phoenixhell.zerokaraspring.beanfactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

/**
 * 模拟spring beanFactory的执行过程
 */
public class TestBeanFactory {

    public static void main(String[] args) {

        //刚建好的beanFactory 里面是没有东西的
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 添加bean 的定义（class, scope单例还是多了, 初始化, 销毁）  注意不是对象
        //定义就是描述一个bean的的样子特征 你告诉DefaultListableBeanFactory 怎么根据bean的定义创建对象对象是beanFactory创建的

        //下面的的Config 类在beanFactory 中加了bean1 和bean2的定义 并且bean1 需要注入（依赖）bean2
        //创建beanDefinition（bean定义对象） 将config 这个类交给beanFactory 管理（创建）
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();

        //注册这个bean定义对象 （beanFactory中添加bean config定义）
        beanFactory.registerBeanDefinition("config", beanDefinition);


        /**
         * config类 里面 有@Bean 但是 beanFactory 打印出来 里面只有config
         * 说明 beanFactory 缺少解析  @Configuration  @@Bean 注解的能力
         */

        // 给 BeanFactory 添加一些常用的后处理器 就是功能扩展 使其拥有解析 注解的能力
        //此方法也会添加一个默认的比较器，注意只是添加，还没有使用
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        System.out.println("----------------------获取默认的比较器------------------------------");
        System.out.println(beanFactory.getDependencyComparator());

        System.out.println("----------------------添加后处理器------------------------------");
        //这些后处理器只是加入了beanFactory ，还没有被运行
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        //通过getBeansOfType 拿到所有BeanFactoryPostProcessor 类型的后处理器遍历
        //org.springframework.context.annotation.ConfigurationClassPostProcessor@6ebc05a6
        //org.springframework.context.event.EventListenerMethodProcessor@130161f7
        //调用它的postProcessBeanFactory 方法 对beanFactory 功能进行扩展
        // BeanFactory 后处理器主要功能，补充了一些 bean 定义
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(beanFactoryPostProcessor -> {
            System.out.println("BeanFactoryPostProcessor-------->"+beanFactoryPostProcessor);
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        });


        //解析之后
        System.out.println("----------------------添加后处理器解析器------------------------------");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

         //bean1 , bean2 的定义已经加入到beanFactory 的定义里面去了
        //但是getBean 由定义创建bean1  bean1 的Autowired 的bean2 有没有没注入
        //也就是说 前面的后置处理器并不能处理@Autowired
        System.out.println("----------------------Autowired  依赖注入的bean2------------------------------");
        //System.out.println(beanFactory.getBean(Bean1.class).getBean2());//null

        System.out.println();
        System.out.println("----------------------BeanPostProcessor  Bean制后处理器------------------------------");
        // Bean 后处理器, 针对 bean 的生命周期的各个阶段提供扩展, 例如 @Autowired @Resource ...
        //在调用getBean方法的时候就进入了bean的生命周期（懒加载） 就会调用BeanPostProcessor 后置处理器
        //org.springframework.context.annotation.CommonAnnotationBeanPostProcessor@223d2c72
        //org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor@8f4ea7c
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream()
                .sorted(beanFactory.getDependencyComparator())
                .forEach(beanPostProcessor -> {
            System.out.println("BeanPostProcessor-------->>>>" + beanPostProcessor);
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        });

        //在调用getBean方法的时候就进入了bean的生命周期 就会调用BeanPostProcessor 后置处理器
        // 这样我们就能获取到bean1自动注入的bean2对象了

        //特别注意 如果上面在BeanPostProcessor 调用前面已经尝试getBean2了 这边还是会输出null
        //原因不明  我猜测第一次调用之后 spring会记住null 这个结果之间输出了。
        System.out.println("----------------------BeanPostProcessor 解析后  依赖注入的bean2------------------------------");
        //System.out.println("BeanPostProcessor=====>"+beanFactory.getBean(Bean1.class).getBean2());


        System.out.println("----------------------提前创建单例Bean------------------------------");
        //getBean 用到时候才会创建bean 但是单例模式我希望提前创建
        beanFactory.preInstantiateSingletons(); // 准备好所有单例
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        //System.out.println(beanFactory.getBean(Bean1.class).getBean2());
        System.out.println(beanFactory.getBean(Bean1.class).getInter());
        /*
            学到了什么:
            a. beanFactory 不会做的事
                   1. 不会主动调用 BeanFactory 后处理器
                   2. 不会主动添加 Bean 后处理器
                   3. 不会主动初始化单例
                   4. 不会解析beanFactory 还不会解析 ${ } 与 #{ }
            b. bean 后处理器会有排序的逻辑
         */

        System.out.println("Common:" + (Ordered.LOWEST_PRECEDENCE - 3));
        System.out.println("Autowired:" + (Ordered.LOWEST_PRECEDENCE - 2));
    }

    /**
     * beanFactory 中添加bean的定义
     */
    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }

    interface Inter {

    }

    static class Bean3 implements Inter {

    }

    static class Bean4 implements Inter {

    }

    static class Bean1 {
        private static final Logger log = LoggerFactory.getLogger(Bean1.class);

        public Bean1() {
            log.debug("构造 Bean1()");
        }

        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }

        //bean1 依赖bean2
        @Autowired
        @Resource(name = "bean4")
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }
    }

    static class Bean2 {
        private static final Logger log = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            log.debug("构造 Bean2()");
        }
    }
}
