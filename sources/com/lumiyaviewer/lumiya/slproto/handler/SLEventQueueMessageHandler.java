// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.handler;

import java.lang.annotation.Annotation;

public interface SLEventQueueMessageHandler
    extends Annotation
{

    public abstract com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType eventName();
}
