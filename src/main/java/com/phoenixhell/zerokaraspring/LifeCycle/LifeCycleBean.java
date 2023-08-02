package com.phoenixhell.zerokaraspring.LifeCycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class LifeCycleBean {
    private static final Logger log = LoggerFactory.getLogger(LifeCycleBean.class);

    public LifeCycleBean() {
        log.debug("构造");
    }

    /**
     *   @Autowired
     *   public void autowire(UserService userService){}
     *   方法上的AutoWired 会自动到容器中寻找userService注入
     *   autowire(String beanName) 会找这个名字的bean注入
     *
     *   autowire(@Value("${JAVA_HOME}") String home) 就是去property文件，或者环境变量等找到值注入到参数中
     */
    @Autowired
    public void autowire(@Value("${JAVA_HOME}") String home) {
        log.debug("依赖注入: {}", home);
    }

    @PostConstruct
    public void init() {
        log.debug("初始化");
    }

    @PreDestroy
    public void destroy() {
        log.debug("销毁");
    }
}
