package com.phoenixhell.zerokaraspring.BeanProcessor;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

/*
    bean 后处理器的作用
 */
public class BeanProcessorDemo {
    public static void main(String[] args) {
        // ⬇️GenericApplicationContext 是一个【干净】的容器  干净指没有在容器中额外的Bean工厂处理器和Bean后处理器
        GenericApplicationContext context = new GenericApplicationContext();

        // ⬇️用原始方法注册三个 bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);

        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class); // @Autowired @Value 注入时候解析

        context.registerBean(CommonAnnotationBeanPostProcessor.class); // @Resource @PostConstruct @PreDestroy 注入初始化和销毁前解析

        //这个和上面不一样注册 是还需要绑定一些其他信息
        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory()); //@ConfigurationProperties 初始化前解析

        // ⬇️初始化容器
        context.refresh(); // 执行beanFactory后处理器, 添加bean后处理器, 初始化所有单例

        //看下属性是否已经正确绑定
        System.out.println(context.getBean(Bean4.class));

        // ⬇️销毁容器
        context.close();

        /*
            学到了什么
                a. @Autowired 等注解的解析属于 bean 生命周期阶段(依赖注入, 初始化)的扩展功能
                b. 这些扩展功能由 bean 后处理器来完成
         */
    }
}
