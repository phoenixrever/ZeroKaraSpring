package com.phoenixhell.zerokaraspring.aop.springproxy;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

public class AspectJPointcutDemo {
    public static void main(String[] args) throws NoSuchMethodException {
        //用execution表达式来代理增强
        //    AspectJExpressionPointcut pt1 = new AspectJExpressionPointcut();
        //    pt1.setExpression("execution(* bar())");
        //    System.out.println(pt1.matches(T1.class.getMethod("foo"), T1.class));
        //    System.out.println(pt1.matches(T1.class.getMethod("bar"), T1.class));

        //用注解代理增强
        //AspectJExpressionPointcut pt2 = new AspectJExpressionPointcut();
        //pt2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        //System.out.println(pt2.matches(T1.class.getMethod("foo"), T1.class));
        //System.out.println(pt2.matches(T1.class.getMethod("bar"), T1.class));

        //模拟spring 采用的注解增强方案
        StaticMethodMatcherPointcut pt3 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                //spring annotation包中的方法 找到对象的全部注解信息

                //1 检查方法上是否加了 Transactional 注解
                MergedAnnotations annotations = MergedAnnotations.from(method);
                if (annotations.isPresent(Transactional.class)) {
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxx");
                    return true;
                }
                //2 查看类上是否加了 Transactional 注解
                // 默认的from 方法使用 SearchStrategy.DIRECT 只会查找本类有没有注解 这种情况T3就不会被增强
                // TYPE_HIERARCHY 策略会查找父类以及接口上有没注解
                annotations = MergedAnnotations.from(targetClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }
                return false;
            }
        };

        //System.out.println(pt3.matches(T1.class.getMethod("foo"), T1.class)); //true
        //System.out.println(pt3.matches(T1.class.getMethod("bar"), T1.class)); //false
        //System.out.println(pt3.matches(T2.class.getMethod("foo"), T2.class)); //true
        //System.out.println(pt3.matches(T3.class.getMethod("foo"), T3.class)); //true
        T3 t3 = new T3();
        t3.foo();

        /*
            学到了什么
                a. 底层切点实现是如何匹配的: 调用了 aspectj 的匹配方法
                b. 比较关键的是它实现了 MethodMatcher 接口, 用来执行方法的匹配
         */
    }


    static class T1 {
        @Transactional
        public void foo() {
        }
        public void bar() {
        }
    }

    @Transactional
    static class T2 {
        public void foo() {
        }
    }

    @Transactional
    interface I3 {
        void foo();
    }
    static class T3 implements I3 {
        public void foo() {
            System.out.println("t3-----------");
        }
    }
}
