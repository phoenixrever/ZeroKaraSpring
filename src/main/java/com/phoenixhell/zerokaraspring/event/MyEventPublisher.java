package com.phoenixhell.zerokaraspring.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author チヨウ　カツヒ
 * @date 2023-03-19 14:20
 * 事件发布 实现组件之间的解耦
 */
@Component
public class MyEventPublisher {
    @Autowired
    private ApplicationEventPublisher publisher;

    //不把注册完成的通知方法写死 比如发邮件  发短信 我只告诉你完成了 具体实现方法自己实现
    //这样就实现了 用户注册和发送短信之间的解耦
    public void register(){
        System.out.println("用户注册完成");
        publisher.publishEvent(new UserRegisteredEvent(this));
    }
}
