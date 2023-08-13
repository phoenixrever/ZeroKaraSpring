package com.phoenixhell.zerokaraspring.AwareInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
/**
 a. BeanNameAware 注入 bean 的名字
 b. BeanFactoryAware 注入 BeanFactory 容器
 c. ApplicationContextAware 注入 ApplicationContext 容器
 d. InitializingBean 接口提供了一种【内置】的初始化手段
 */
public class MyBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MyBean.class);

    @Override
    public void setBeanName(String name) {
        log.debug("当前bean " + this + " 名字叫:" + name);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.debug("当前bean " + this + " 工厂是:" + beanFactory);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.debug("当前bean " + this + " 容器是:" + applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("当前bean " + this + " 初始化");
    }

    // @Autowired 注入 ApplicationContext 失败
    @Autowired
    public void aaa(ApplicationContext applicationContext) {
        log.debug("当前bean " + this + " 使用@Autowired 容器是:" + applicationContext);
    }

    //@PostConstruct 的初始化失败
    @PostConstruct
    public void init() {
        log.debug("当前bean " + this + " 使用@PostConstruct 初始化");
    }
}
