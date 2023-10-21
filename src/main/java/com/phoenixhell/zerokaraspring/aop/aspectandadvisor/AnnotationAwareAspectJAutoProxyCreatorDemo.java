package com.phoenixhell.zerokaraspring.aop.aspectandadvisor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

public class AnnotationAwareAspectJAutoProxyCreatorDemo {
    public static void main(String[] args) {
        //GenericApplicationContext 不带后处理器的比较干净的处理器，见前面章节
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("aspect1", Aspect1.class);
        context.registerBean("config", Config.class);
        //target1 必须在容器中 才会为你创建代理 切面才会生效 自己new的是没有代理的
        context.registerBean("target1", Target1.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);
        // BeanPostProcessor  * : AnnotationAwareAspectJAutoProxyCreator的扩展点
        // 创建 -> (*) 依赖注入 -> 初始化 (*)

        context.refresh();
//        for (String name : context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }

        /*
            第一个重要方法 findEligibleAdvisors 找到有【资格】的 Advisors
                a. 有【资格】的 Advisor 一部分是低级的, 可以由自己编写, 如下例中的 advisor3
                b. 有【资格】的 Advisor 另一部分是高级的, 由本章的主角解析 @Aspect 后获得
         */
        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);

        //从容器中找是到否有advisor 能应用到 Target2.class 并返回
        //另外 这个方法只和对象类型有关，更实例无关，target 并没有在容器中。
        //List<Advisor> advisors = creator.findEligibleAdvisors(Target2.class, "target2");

        /*for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }*/

        /*
            第二个重要方法 wrapIfNecessary
                a. 它内部调用 findEligibleAdvisors, 只要返回集合不空, 则表示需要创建代理
         */
        //Object o1 = creator.wrapIfNecessary(new Target1(), "target1", "target1");
        //System.out.println(o1.getClass());
        //Object o2 = creator.wrapIfNecessary(new Target2(), "target2", "target2");
        //System.out.println(o2.getClass());

        //((Target1) o1).foo();
        /*
            学到了什么
                a. 自动代理后处理器 AnnotationAwareAspectJAutoProxyCreator 会帮我们创建代理
                b. 通常代理创建的活在原始对象初始化后执行, 但碰到循环依赖会提前至依赖注入之前执行
                c. 高级的 @Aspect 切面会转换为低级的 Advisor 切面, 理解原理, 大道至简
         */

        //观察执行的顺序
        //target1 必须在容器中 才会为你创建代理 切面才会生效 自己new的是没有代理的
        Target1 target1 = (Target1) context.getBean("target1");

        target1.foo();
    }

    static class Target1 {
        public void foo() {
            System.out.println("target1 foo");
        }
    }

    static class Target2 {
        public void bar() {
            System.out.println("target2 bar");
        }
    }

    @Aspect // 高级切面类
    @Order(1) //数字小优先级高
    static class Aspect1 {
        @Before("execution(* foo())")
        public void before1() {
            System.out.println("aspect1 before1...");
        }

        @Before("execution(* foo())")
        public void before2() {
            System.out.println("aspect1 before2...");
        }
    }

    @Configuration
    static class Config {
        @Bean // 低级切面
        //@Order(2) //加在这是没有效果的 原因视频没说
        public Advisor advisor3(MethodInterceptor advice3) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice3);
            advisor.setOrder(2);
            return advisor;
        }
        @Bean
        public MethodInterceptor advice3() {
            return invocation -> {
                System.out.println("advice3 before...");
                Object result = invocation.proceed();
                System.out.println("advice3 after...");
                return result;
            };
        }
    }
}
