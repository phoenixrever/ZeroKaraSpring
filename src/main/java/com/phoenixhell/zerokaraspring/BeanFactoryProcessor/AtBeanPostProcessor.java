package com.phoenixhell.zerokaraspring.BeanFactoryProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.Set;

public class AtBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //原先实现的是 BeanFactoryPostProcessor
        //只有configurableListableBeanFactory 没有registerBeanDefinition 方法
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
            MetadataReader reader = factory.getMetadataReader(new ClassPathResource("com/phoenixhell/zerokaraspring/BeanFactoryProcessor/Config.class"));
            Set<MethodMetadata> methods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
            for (MethodMetadata method : methods) {
                System.out.println(method);
                //拿到@Bean(initMethod = "init")  属性名字
                String initMethod = method.getAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();

                //根据这些方法信息生成对应的beanDefinition
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
                //定义config的工厂方法 我的理解就是这个bean 实在config里面声明的 config负责创建这些bean 所以config就是bean 工厂
                builder.setFactoryMethodOnBean(method.getMethodName(), "config");

                //上面的代码不能助理需要自动注入有的bean 比如config里面的sqlSessionFactoryBean(DataSource dataSource)  需要自动注入dataSource bean
                //需要指定自动装配模式 默认是AUTOWIRE_NO  对于构造方法和工厂方法的参数用 CONSTRUCTOR 具体以后用到在了解
                builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

                //解析@Bean(initMethod = "init") 的属性
                if (initMethod.length() > 0) {
                    builder.setInitMethodName(initMethod);
                }
                AbstractBeanDefinition bd = builder.getBeanDefinition();
                beanFactory.registerBeanDefinition(method.getMethodName(), bd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
