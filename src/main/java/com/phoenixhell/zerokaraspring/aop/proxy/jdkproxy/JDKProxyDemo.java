package com.phoenixhell.zerokaraspring.aop.proxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxyDemo {

    interface Foo {
        void foo();
    }

    //要被增强的类实现了一个接口（jdk代理只能针对接口代理）
    //可以是final
     static final class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }
    }

    public static void main(String[] args) {

        Target target = new Target();

        /**
         * 为什么代理类需要一个类加载器喃，是因为普通类是从源码生成class字节码，经过类加载使用
         * 而代理类没有源码。是在运行期间直接生成代理类的字节码，而生成的这个字节码也要被类加载器加载后才能运行
         * 所以需要一个classLoader 来加载代理类内部生成的字节码
         */
        ClassLoader classLoader = JDKProxyDemo.class.getClassLoader(); //用来加载在运行期间动态生成的字节码

        //返回类型定义成接口类型（因为代理类实现了接口）
        Foo proxyInstance = (Foo) Proxy.newProxyInstance(classLoader, new Class[]{Foo.class}, new InvocationHandler() {
            /**
             * @param: proxy      代理对象自己
             * @param: method     正在执行的(被代理)方法对象
             * @param: args       方法传过来的参数
             * @return:
             * @throws
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("我在被代理的方法之前执行了。。。。。");
                //执行被代理的目标方法 有可能有返回结果
                Object result = method.invoke(target, args);
                System.out.println("后置增强。。。。。");
                return result;
            }
        });

        proxyInstance.foo();
    }
}