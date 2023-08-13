package com.phoenixhell.zerokaraspring.initAndDestroy;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 好像主要就是   beanFactory销毁bean实例之后, getBean仍可创建新的单例
 */
public class InitAndDestroyAdditional {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition(
                "myBean",
                BeanDefinitionBuilder.genericBeanDefinition(MyBean.class)
                        .setDestroyMethodName("destroy")
                        .getBeanDefinition()
        );

        System.out.println(beanFactory.getBean(MyBean.class));
        beanFactory.destroySingletons(); // 销毁之后, 仍可创建新的单例
        System.out.println(beanFactory.getBean(MyBean.class));

    }

    static class MyBean {
        public MyBean() {
            System.out.println("MyBean()");
        }

        public void destroy() {
            System.out.println("destroy()");
        }
    }
}
