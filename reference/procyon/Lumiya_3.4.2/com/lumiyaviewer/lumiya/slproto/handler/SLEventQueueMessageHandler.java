// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.handler;

import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SLEventQueueMessageHandler {
    SLCapEventQueue.CapsEventType eventName();
}
