package com.phoenixhell.zerokaraspring.BeanProcessor;

import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

// AutowiredAnnotationBeanPostProcessor 运行分析
public class DigInAutowired {
    public static void main(String[] args) throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //这边不用  beanFactory.registerBeanDefinition();所以 创建过程,依赖注入,初始化 这几步就不会帮你做了
        //bean工厂会认为这是一个成品的bean 直接拿来用
        beanFactory.registerSingleton("bean2", new Bean2()); // 创建过程,依赖注入,初始化
        beanFactory.registerSingleton("bean3", new Bean3());
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver()); // @Value
        //不加这个解析器 就不能解释$符号 如果想创建类似spring的属性注入功能可以研究下这个类
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders); // ${} 的解析器

        // 1. 查找哪些属性、方法加了 @Autowired, 这称之为 InjectionMetadata
        AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
        //bean后处理器需要 根据bean的类型 从beanFactory 找到这些bean
        processor.setBeanFactory(beanFactory);

        Bean1 bean1 = new Bean1();
        System.out.println("手动创建的bean1 里面的注入都是不会调用的"+bean1);

        //第一个参数是自己手动指定属性的值，而我们用bean里面自动注入属性的值 所以为null
        //processor.postProcessProperties(null, bean1, "bean1"); // 执行依赖注入 @Autowired @Value
        //System.out.println(bean1);

        Method findAutowiringMetadata = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
        findAutowiringMetadata.setAccessible(true);
        InjectionMetadata metadata = (InjectionMetadata) findAutowiringMetadata.invoke(processor, "bean1", Bean1.class, null);// 获取 Bean1 上加了 @Value @Autowired 的成员变量，方法参数信息
        System.out.println(metadata);

        // 2. 调用 InjectionMetadata 来进行依赖注入, 注入时按类型查找值
        metadata.inject(bean1, "bean1", null);
        System.out.println(bean1);

        // 3. 如何按类型查找值
        Field bean3 = Bean1.class.getDeclaredField("bean3");
        //第二个 required 参数是 依赖注入的时候如何找不到这个属性对应的值 false的时候也不会报错
        DependencyDescriptor dd1 = new DependencyDescriptor(bean3, false);
        //doResolveDependency 根据属性的类型 从spring容器中找到要注入的对象
        Object o = beanFactory.doResolveDependency(dd1, null, null, null);
        System.out.println("bean3"+o);//bean3

        Method setBean2 = Bean1.class.getDeclaredMethod("setBean2", Bean2.class);
        DependencyDescriptor dd2 =
                new DependencyDescriptor(new MethodParameter(setBean2, 0), true);
        Object o1 = beanFactory.doResolveDependency(dd2, null, null, null);
        System.out.println(o1);

        Method setHome = Bean1.class.getDeclaredMethod("setHome", String.class);
        DependencyDescriptor dd3 = new DependencyDescriptor(new MethodParameter(setHome, 0), true);
        Object o2 = beanFactory.doResolveDependency(dd3, null, null, null);
        System.out.println(o2);

    }
}
