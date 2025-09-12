// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.eventbus;

import android.app.Activity;
import android.os.Handler;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

// Referenced classes of package com.lumiyaviewer.lumiya.eventbus:
//            EventBus

private static class handler
{

    private final WeakReference activity;
    private final Class eventClass;
    private final WeakReference handler;
    private final Method method;
    private final WeakReference subscriber;

    public Activity getActivity()
    {
        return (Activity)activity.get();
    }

    public Handler getHandler()
    {
        return (Handler)handler.get();
    }

    public Method getMethod()
    {
        return method;
    }

    public Object getSubscriber()
    {
        return subscriber.get();
    }

    public boolean matchesEvent(Object obj)
    {
        if (obj != null)
        {
            return obj.getClass().equals(eventClass);
        } else
        {
            return false;
        }
    }

    public (Class class1, Method method1, Object obj, Activity activity1, Handler handler1)
    {
        eventClass = class1;
        method = method1;
        subscriber = new WeakReference(obj);
        activity = new WeakReference(activity1);
        handler = new WeakReference(handler1);
    }
}
