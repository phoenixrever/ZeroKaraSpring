package com.phoenixhell.zerokaraspring.aop.ajc;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect // ⬅️注意此切面并未被 Spring 管理
public class MyAspect {

    private static final Logger log = LoggerFactory.getLogger(MyAspect.class);

    @Before("execution(* com.phoenixhell.zerokaraspring.aop.ajc.MyService.foo())")
    public void before() {
        log.debug("before()");
    }
}
