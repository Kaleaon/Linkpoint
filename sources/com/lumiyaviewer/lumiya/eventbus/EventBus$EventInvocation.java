// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.eventbus;

import android.app.Activity;
import android.os.Handler;
import java.lang.reflect.Method;

// Referenced classes of package com.lumiyaviewer.lumiya.eventbus:
//            EventBus

private static class handler
    implements Runnable
{

    private final Activity activity;
    private final Object event;
    private final Handler handler;
    private final Method method;
    private final Object subscriber;

    public void run()
    {
        try
        {
            method.invoke(subscriber, new Object[] {
                event
            });
            return;
        }
        catch (Exception exception)
        {
            return;
        }
    }

    public void runOnUIThread()
    {
        if (activity != null)
        {
            activity.runOnUiThread(this);
            return;
        }
        if (handler != null)
        {
            handler.post(this);
            return;
        } else
        {
            run();
            return;
        }
    }

    public (Object obj, Activity activity1, Object obj1, Method method1, Handler handler1)
    {
        event = obj;
        activity = activity1;
        subscriber = obj1;
        method = method1;
        handler = handler1;
    }
}
