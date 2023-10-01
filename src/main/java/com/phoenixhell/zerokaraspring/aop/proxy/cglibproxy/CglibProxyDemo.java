package com.phoenixhell.zerokaraspring.aop.proxy.cglibproxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author チヨウ　カツヒ
 * @date 2023-09-23 20:27
 */
public class CglibProxyDemo {

    //很明显 cglib 是要继承被代理类的 被代理类不能是final
    //同理 方法 也不能是final 的 既然是继承来代理 肯定是通过重写方法来实现的
    static class Target {
        public void foo() {
            System.out.println("target foo");
        }
    }

    public static void main(String[] args) {
        // 目标对象
        Target target = new Target();
        // 代理对象

        /**
         * cglib通过Enhancer 创建代理  和jdk代理的兄弟关系不同 它是继承目标被代理对象的方式
         * 参数1  被代理的目标对象
         * 参数2  callback（一般用其子接口MethodInterceptor） 类似jdk代理的 invocationHandler
         *
         */

        //因为和被代理类是继承关系 所以强转为被代理类的类型
        Target proxy = (Target) Enhancer.create(Target.class,
                new MethodInterceptor() {
                    @Override
                    public Object intercept(Object p, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                        System.out.println("proxy before...");
                        //Object result = methodProxy.invoke(target, args); //用反射调用目标方法 性能会弱一些
                        //MethodProxy 参数的作用 不需要目标对象实例 避免用反射调用目标方法
                        Object result = methodProxy.invokeSuper(p, args);
                        //也可以传入实例对象  但是内部也不是反射方法 spring用的是这种
                        //Object result = methodProxy.invoke(target, args);
                        System.out.println("proxy after...");
                        return result;
                    }
                });
        // 调用代理
        proxy.foo();
    }
}
