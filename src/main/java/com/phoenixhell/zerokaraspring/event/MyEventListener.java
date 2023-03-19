package com.phoenixhell.zerokaraspring.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author チヨウ　カツヒ
 * @date 2023-03-19 14:11
 *
 * 注意名字不要取EventListener  不然会重名不能import  @EventListener 会需要写全类名
 * 监听器 监听 发布的事件
 */

@Slf4j
@Component
public class MyEventListener {

    @EventListener
    public void  listenEvent(UserRegisteredEvent event){
        log.debug("{}",event);
        System.out.println("收到  UserRegisteredEvent 事件");
    }
}
