package com.phoenixhell.zerokaraspring.BeanFactoryProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.io.IOException;


public class ComponentScanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override // context.refresh 初始化 ApplicationContext的时候会调用
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //原先实现的是 BeanFactoryPostProcessor
        //只有configurableListableBeanFactory
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            //AnnotationUtils.findAnnotation 查找某个类上有没有加注解
            ComponentScan componentScan = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
            if (componentScan != null) {
                for (String p : componentScan.basePackages()) {
                    System.out.println(p);
                    // Springboot是通过路径来查找class类上的有没有加component注解的 不是通过反射

                    //第一步 找到jar component文件夹下中所有的类文件路径
                    // com.phoenixhell.zerokaraspring.BeanFactoryProcessor.component -> classpath*:com/phoenixhell/zerokaraspring/BeanFactoryProcessor/component/**/*.class
                    // classpath: 通配符只是到类路劲下找，jar包里面是找不到的
                    // 需要改成 classpath*: 就可以再jar包里面寻找
                    String path = "classpath*:" + p.replace(".", "/") + "/**/*.class";
                    //拿到文件路径 classes 文件目录下字节码文件
                    System.out.println(path);

                    //第二步 查看类文件上的注解 看看是否有component 注解（hasAnnotation）
                    //CachingMetadataReaderFactory 用来读取类的元信息 类上有啥注解什么的
                    CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
                    //这边没有  GenericApplicationContext  不能用 context.getResource(); 换成 PathMatchingResourcePatternResolver
                    //或者   new ClassPathResource(path); 效率高
                    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(path);
                    AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
                    for (Resource resource : resources) {
                        // System.out.println(resource);
                        MetadataReader reader = factory.getMetadataReader(resource);
                        // System.out.println("类名:" + reader.getClassMetadata().getClassName());
                        AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
                        // System.out.println("是否加了 @Component:" + annotationMetadata.hasAnnotation(Component.class.getName()));

                        //@Controller ,@repository @service  等属于 @Component的派生注解 用 hasMetaAnnotation
                        // System.out.println("是否加了 @Component 派生:" + annotationMetadata.hasMetaAnnotation(Component.class.getName()));
                        if (annotationMetadata.hasAnnotation(Component.class.getName())
                            || annotationMetadata.hasMetaAnnotation(Component.class.getName())) {
                            AbstractBeanDefinition bd = BeanDefinitionBuilder
                                    .genericBeanDefinition(reader.getClassMetadata().getClassName())
                                    .getBeanDefinition();
                            String name = generator.generateBeanName(bd, beanFactory);
                            beanFactory.registerBeanDefinition(name, bd);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
