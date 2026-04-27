// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.handler;

import com.lumiyaviewer.lumiya.Debug;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.handler:
//            SLMessageRouter

private static class subscriber
{

    private final Method method;
    private final WeakReference subscriber;

    static WeakReference _2D_get0(subscriber subscriber1)
    {
        return subscriber1.subscriber;
    }

    public void invoke(Object obj)
    {
        Object obj1;
        try
        {
            obj1 = subscriber.get();
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            ((IllegalArgumentException) (obj)).printStackTrace();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            ((IllegalAccessException) (obj)).printStackTrace();
            return;
        }
        catch (InvocationTargetException invocationtargetexception)
        {
            Debug.Log((new StringBuilder()).append("InvocationTargetException in handler for ").append(obj.getClass().getSimpleName()).toString());
            obj = invocationtargetexception.getCause();
            if (obj != null)
            {
                ((Throwable) (obj)).printStackTrace();
                return;
            } else
            {
                invocationtargetexception.printStackTrace();
                return;
            }
        }
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_29;
        }
        method.invoke(obj1, new Object[] {
            obj
        });
    }

    public I(Method method1, Object obj)
    {
        method = method1;
        subscriber = new WeakReference(obj);
    }
}
