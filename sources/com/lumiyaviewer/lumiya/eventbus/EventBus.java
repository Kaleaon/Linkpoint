// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.eventbus;

import android.app.Activity;
import android.os.Handler;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.eventbus:
//            EventHandler

public class EventBus
{
    private static class EventInvocation
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

        public EventInvocation(Object obj, Activity activity1, Object obj1, Method method1, Handler handler1)
        {
            event = obj;
            activity = activity1;
            subscriber = obj1;
            method = method1;
            handler = handler1;
        }
    }

    private static class HandlerInfo
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

        public HandlerInfo(Class class1, Method method1, Object obj, Activity activity1, Handler handler1)
        {
            eventClass = class1;
            method = method1;
            subscriber = new WeakReference(obj);
            activity = new WeakReference(activity1);
            handler = new WeakReference(handler1);
        }
    }

    private static class InstanceHolder
    {

        private static final EventBus Instance = new EventBus(null);

        static EventBus _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private final List handlers;

    private EventBus()
    {
        handlers = new LinkedList();
    }

    EventBus(EventBus eventbus)
    {
        this();
    }

    public static EventBus getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    public void publish(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj1;
        Iterator iterator;
        obj1 = new LinkedList();
        iterator = handlers.iterator();
_L1:
        HandlerInfo handlerinfo;
        Object obj2;
        Activity activity;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_113;
            }
            handlerinfo = (HandlerInfo)iterator.next();
        } while (!handlerinfo.matchesEvent(obj));
        obj2 = handlerinfo.getSubscriber();
        activity = handlerinfo.getActivity();
        if (obj2 != null)
        {
            break MISSING_BLOCK_LABEL_85;
        }
        ((List) (obj1)).add(handlerinfo);
          goto _L1
        obj;
        throw obj;
        (new EventInvocation(obj, activity, obj2, handlerinfo.getMethod(), handlerinfo.getHandler())).runOnUIThread();
          goto _L1
        for (obj = ((Iterable) (obj1)).iterator(); ((Iterator) (obj)).hasNext(); handlers.remove(obj1))
        {
            obj1 = (HandlerInfo)((Iterator) (obj)).next();
        }

        this;
        JVM INSTR monitorexit ;
    }

    public void subscribe(Activity activity)
    {
        this;
        JVM INSTR monitorenter ;
        subscribe(activity, activity);
        this;
        JVM INSTR monitorexit ;
        return;
        activity;
        throw activity;
    }

    public void subscribe(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        if (!(obj instanceof Activity)) goto _L2; else goto _L1
_L1:
        subscribe(obj, (Activity)obj);
_L4:
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        subscribe(obj, null);
        if (true) goto _L4; else goto _L3
_L3:
        obj;
        throw obj;
    }

    public void subscribe(Object obj, Activity activity)
    {
        this;
        JVM INSTR monitorenter ;
        subscribe(obj, activity, null);
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public void subscribe(Object obj, Activity activity, Handler handler)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj1;
        Object obj2;
        obj1 = new LinkedList();
        obj2 = handlers.iterator();
_L4:
        HandlerInfo handlerinfo;
        Object obj3;
        if (!((Iterator) (obj2)).hasNext())
        {
            break MISSING_BLOCK_LABEL_83;
        }
        handlerinfo = (HandlerInfo)((Iterator) (obj2)).next();
        obj3 = handlerinfo.getSubscriber();
        if (obj3 != obj) goto _L2; else goto _L1
_L1:
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        if (obj3 != null) goto _L4; else goto _L3
_L3:
        ((List) (obj1)).add(handlerinfo);
          goto _L4
        obj;
        throw obj;
        for (obj1 = ((Iterable) (obj1)).iterator(); ((Iterator) (obj1)).hasNext(); handlers.remove(obj2))
        {
            obj2 = (HandlerInfo)((Iterator) (obj1)).next();
        }

        Method amethod[];
        int j;
        amethod = obj.getClass().getMethods();
        j = amethod.length;
        int i = 0;
_L6:
        if (i >= j)
        {
            break MISSING_BLOCK_LABEL_234;
        }
        obj2 = amethod[i];
        if ((EventHandler)((Method) (obj2)).getAnnotation(com/lumiyaviewer/lumiya/eventbus/EventHandler) == null)
        {
            break MISSING_BLOCK_LABEL_236;
        }
        Class aclass[] = ((Method) (obj2)).getParameterTypes();
        if (aclass.length != 1)
        {
            throw new IllegalArgumentException("EventHandler methods must specify a single Object paramter.");
        }
        obj2 = new HandlerInfo(aclass[0], ((Method) (obj2)), obj, activity, handler);
        handlers.add(obj2);
        break MISSING_BLOCK_LABEL_236;
        return;
        i++;
        if (true) goto _L6; else goto _L5
_L5:
    }

    public void unsubscribe(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj1;
        Iterator iterator;
        obj1 = new LinkedList();
        iterator = handlers.iterator();
_L2:
        HandlerInfo handlerinfo;
        Object obj2;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_75;
            }
            handlerinfo = (HandlerInfo)iterator.next();
            obj2 = handlerinfo.getSubscriber();
        } while (obj2 != null && obj2 != obj);
        ((List) (obj1)).add(handlerinfo);
        if (true) goto _L2; else goto _L1
_L1:
        obj;
        throw obj;
        for (obj = ((Iterable) (obj1)).iterator(); ((Iterator) (obj)).hasNext(); handlers.remove(obj1))
        {
            obj1 = (HandlerInfo)((Iterator) (obj)).next();
        }

        this;
        JVM INSTR monitorexit ;
    }

    public void unsubscribeActivity(Activity activity)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj;
        obj = new LinkedList();
        Iterator iterator = handlers.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            HandlerInfo handlerinfo = (HandlerInfo)iterator.next();
            if (handlerinfo.getActivity() == activity)
            {
                ((List) (obj)).add(handlerinfo);
            }
        } while (true);
        break MISSING_BLOCK_LABEL_66;
        activity;
        throw activity;
        for (activity = ((Iterable) (obj)).iterator(); activity.hasNext(); handlers.remove(obj))
        {
            obj = (HandlerInfo)activity.next();
        }

        this;
        JVM INSTR monitorexit ;
    }
}
