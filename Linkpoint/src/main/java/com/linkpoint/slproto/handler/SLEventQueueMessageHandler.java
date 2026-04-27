package com.linkpoint.slproto.handler;

import com.linkpoint.slproto.caps.SLCapEventQueue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SLEventQueueMessageHandler {
    SLCapEventQueue.CapsEventType eventName();
}
