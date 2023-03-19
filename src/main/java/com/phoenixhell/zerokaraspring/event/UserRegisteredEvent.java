package com.phoenixhell.zerokaraspring.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author チヨウ　カツヒ
 * @date 2023-03-19 14:06
 *
 * Object source 事件源  谁发的事件
 */
public class UserRegisteredEvent extends ApplicationEvent {
    public UserRegisteredEvent(Object source) {
        super(source);
    }
}
