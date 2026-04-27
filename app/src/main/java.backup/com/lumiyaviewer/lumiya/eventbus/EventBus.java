package com.lumiyaviewer.lumiya.eventbus;

import android.app.Activity;
import android.os.Handler;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class EventBus {
    private final List<HandlerInfo> handlers;

    private static class EventInvocation implements Runnable {
        private final Activity activity;
        private final Object event;
        private final Handler handler;
        private final Method method;
        private final Object subscriber;

        public EventInvocation(Object obj, Activity activity, Object obj2, Method method, Handler handler) {
            this.event = obj;
            this.activity = activity;
            this.subscriber = obj2;
            this.method = method;
            this.handler = handler;
        }

        public void run() {
            try {
                this.method.invoke(this.subscriber, new Object[]{this.event});
            } catch (Exception e) {
                // Log reflection errors for debugging
                android.util.Log.e("EventBus", "Failed to invoke event handler method", e);
            }
        }

        public void runOnUIThread() {
            if (this.activity != null) {
                this.activity.runOnUiThread(this);
            } else if (this.handler != null) {
                this.handler.post(this);
            } else {
                run();
            }
        }
    }

    private static class HandlerInfo {
        private final WeakReference<Activity> activity;
        private final Class<?> eventClass;
        private final WeakReference<Handler> handler;
        private final Method method;
        private final WeakReference<?> subscriber;

        public HandlerInfo(Class<?> cls, Method method, Object obj, Activity activity, Handler handler) {
            this.eventClass = cls;
            this.method = method;
            this.subscriber = new WeakReference(obj);
            this.activity = new WeakReference(activity);
            this.handler = new WeakReference(handler);
        }

        public Activity getActivity() {
            return (Activity) this.activity.get();
        }

        public Handler getHandler() {
            return (Handler) this.handler.get();
        }

        public Method getMethod() {
            return this.method;
        }

        public Object getSubscriber() {
            return this.subscriber.get();
        }

        public boolean matchesEvent(Object obj) {
            return obj != null ? obj.getClass().equals(this.eventClass) : false;
        }
    }

    private static class InstanceHolder {
        private static final EventBus Instance = new EventBus();

        private InstanceHolder() {
        }
    }

    private EventBus() {
        this.handlers = new LinkedList();
    }

    /* synthetic */ EventBus(EventBus eventBus) {
        this();
    }

    public static EventBus getInstance() {
        return InstanceHolder.Instance;
    }

    public synchronized void publish(Object obj) {
        Iterable<HandlerInfo> linkedList = new LinkedList();
        for (HandlerInfo handlerInfo : this.handlers) {
            if (handlerInfo.matchesEvent(obj)) {
                Object subscriber = handlerInfo.getSubscriber();
                Activity activity = handlerInfo.getActivity();
                if (subscriber == null) {
                    linkedList.add(handlerInfo);
                } else {
                    new EventInvocation(obj, activity, subscriber, handlerInfo.getMethod(), handlerInfo.getHandler()).runOnUIThread();
                }
            }
        }
        for (HandlerInfo remove : linkedList) {
            this.handlers.remove(remove);
        }
    }

    public synchronized void subscribe(Activity activity) {
        subscribe(activity, activity);
    }

    public synchronized void subscribe(Object obj) {
        if (obj instanceof Activity) {
            subscribe(obj, (Activity) obj);
        } else {
            subscribe(obj, null);
        }
    }

    public synchronized void subscribe(Object obj, Activity activity) {
        subscribe(obj, activity, null);
    }

    public synchronized void subscribe(Object obj, Activity activity, Handler handler) {
        Iterable<HandlerInfo> linkedList = new LinkedList();
        for (HandlerInfo handlerInfo : this.handlers) {
            Object subscriber = handlerInfo.getSubscriber();
            if (subscriber != obj) {
                if (subscriber == null) {
                    linkedList.add(handlerInfo);
                }
            } else {
                return;
            }
        }
        for (HandlerInfo handlerInfo2 : linkedList) {
            this.handlers.remove(handlerInfo2);
        }
        for (Method method : obj.getClass().getMethods()) {
            if (((EventHandler) method.getAnnotation(EventHandler.class)) != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException("EventHandler methods must specify a single Object paramter.");
                }
                this.handlers.add(new HandlerInfo(parameterTypes[0], method, obj, activity, handler));
            }
        }
    }

    public synchronized void unsubscribe(Object obj) {
        Iterable<HandlerInfo> linkedList = new LinkedList();
        for (HandlerInfo handlerInfo : this.handlers) {
            Object subscriber = handlerInfo.getSubscriber();
            if (subscriber == null || subscriber == obj) {
                linkedList.add(handlerInfo);
            }
        }
        for (HandlerInfo handlerInfo2 : linkedList) {
            this.handlers.remove(handlerInfo2);
        }
    }

    public synchronized void unsubscribeActivity(Activity activity) {
        Iterable<HandlerInfo> linkedList = new LinkedList();
        for (HandlerInfo handlerInfo : this.handlers) {
            if (handlerInfo.getActivity() == activity) {
                linkedList.add(handlerInfo);
            }
        }
        for (HandlerInfo handlerInfo2 : linkedList) {
            this.handlers.remove(handlerInfo2);
        }
    }
}
