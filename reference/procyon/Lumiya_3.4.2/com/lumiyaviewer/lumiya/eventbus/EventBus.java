// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.eventbus;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import android.os.Handler;
import android.app.Activity;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EventBus
{
    private final List<HandlerInfo> handlers;
    
    private EventBus() {
        this.handlers = new LinkedList<HandlerInfo>();
    }
    
    public static EventBus getInstance() {
        return InstanceHolder.Instance;
    }
    
    public void publish(final Object o) {
        LinkedList list;
        while (true) {
            while (true) {
                HandlerInfo handlerInfo = null;
                Object subscriber = null;
                Activity activity = null;
                Label_0085: {
                    synchronized (this) {
                        list = new LinkedList();
                        final Iterator<Object> iterator = this.handlers.iterator();
                        while (iterator.hasNext()) {
                            handlerInfo = iterator.next();
                            if (handlerInfo.matchesEvent(o)) {
                                subscriber = handlerInfo.getSubscriber();
                                activity = handlerInfo.getActivity();
                                if (subscriber != null) {
                                    break Label_0085;
                                }
                                list.add(handlerInfo);
                            }
                        }
                        break;
                    }
                }
                new EventInvocation(o, activity, subscriber, handlerInfo.getMethod(), handlerInfo.getHandler()).runOnUIThread();
                continue;
            }
        }
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            this.handlers.remove(iterator2.next());
        }
        monitorexit(this);
    }
    
    public void subscribe(final Activity activity) {
        synchronized (this) {
            this.subscribe(activity, activity);
        }
    }
    
    public void subscribe(final Object o) {
        synchronized (this) {
            if (o instanceof Activity) {
                this.subscribe(o, (Activity)o);
            }
            else {
                this.subscribe(o, null);
            }
        }
    }
    
    public void subscribe(final Object o, final Activity activity) {
        synchronized (this) {
            this.subscribe(o, activity, null);
        }
    }
    
    public void subscribe(Object o, final Activity activity, final Handler handler) {
        final LinkedList list;
        synchronized (this) {
            list = new LinkedList();
            for (final HandlerInfo handlerInfo : this.handlers) {
                final Object subscriber = handlerInfo.getSubscriber();
                if (subscriber == o) {
                    return;
                }
                if (subscriber != null) {
                    continue;
                }
                list.add(handlerInfo);
            }
        }
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            this.handlers.remove(iterator2.next());
        }
        final Throwable t;
        for (final Method method : t.getClass().getMethods()) {
            if (method.getAnnotation(EventHandler.class) != null) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    o = new IllegalArgumentException("EventHandler methods must specify a single Object paramter.");
                    throw o;
                }
                this.handlers.add(new HandlerInfo(parameterTypes[0], method, t, activity, handler));
            }
        }
        monitorexit(this);
    }
    
    public void unsubscribe(final Object o) {
        final LinkedList list;
        synchronized (this) {
            list = new LinkedList();
            for (final HandlerInfo handlerInfo : this.handlers) {
                final Object subscriber = handlerInfo.getSubscriber();
                if (subscriber == null || subscriber == o) {
                    list.add(handlerInfo);
                }
            }
        }
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            this.handlers.remove(iterator2.next());
        }
        monitorexit(this);
    }
    
    public void unsubscribeActivity(final Activity activity) {
        final LinkedList list;
        synchronized (this) {
            list = new LinkedList();
            for (final HandlerInfo handlerInfo : this.handlers) {
                if (handlerInfo.getActivity() == activity) {
                    list.add(handlerInfo);
                }
            }
        }
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            this.handlers.remove(iterator2.next());
        }
        monitorexit(this);
    }
    
    private static class EventInvocation implements Runnable
    {
        private final Activity activity;
        private final Object event;
        private final Handler handler;
        private final Method method;
        private final Object subscriber;
        
        public EventInvocation(final Object event, final Activity activity, final Object subscriber, final Method method, final Handler handler) {
            this.event = event;
            this.activity = activity;
            this.subscriber = subscriber;
            this.method = method;
            this.handler = handler;
        }
        
        @Override
        public void run() {
            try {
                this.method.invoke(this.subscriber, this.event);
            }
            catch (final Exception ex) {}
        }
        
        public void runOnUIThread() {
            if (this.activity != null) {
                this.activity.runOnUiThread((Runnable)this);
            }
            else if (this.handler != null) {
                this.handler.post((Runnable)this);
            }
            else {
                this.run();
            }
        }
    }
    
    private static class HandlerInfo
    {
        private final WeakReference<Activity> activity;
        private final Class<?> eventClass;
        private final WeakReference<Handler> handler;
        private final Method method;
        private final WeakReference<?> subscriber;
        
        public HandlerInfo(final Class<?> eventClass, final Method method, final Object referent, final Activity referent2, final Handler referent3) {
            this.eventClass = eventClass;
            this.method = method;
            this.subscriber = new WeakReference<Object>(referent);
            this.activity = new WeakReference<Activity>(referent2);
            this.handler = new WeakReference<Handler>(referent3);
        }
        
        public Activity getActivity() {
            return this.activity.get();
        }
        
        public Handler getHandler() {
            return this.handler.get();
        }
        
        public Method getMethod() {
            return this.method;
        }
        
        public Object getSubscriber() {
            return this.subscriber.get();
        }
        
        public boolean matchesEvent(final Object o) {
            return o != null && o.getClass().equals(this.eventClass);
        }
    }
    
    private static class InstanceHolder
    {
        private static final EventBus Instance;
        
        static {
            Instance = new EventBus(null);
        }
    }
}
