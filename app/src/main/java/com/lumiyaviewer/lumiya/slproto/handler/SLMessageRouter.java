package com.lumiyaviewer.lumiya.slproto.handler;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class SLMessageRouter {
    private Map<SLCapEventQueue.CapsEventType, HandlerList> eventQueueMessageHandlers = new HashMap();
    private Map<Class<?>, HandlerList> messageHandlers = new HashMap();

    private static class HandlerInfo {
        private final Method method;
        /* access modifiers changed from: private */
        public final WeakReference<?> subscriber;

        public HandlerInfo(Method method2, Object obj) {
            this.method = method2;
            this.subscriber = new WeakReference<>(obj);
        }

        public void invoke(Object obj) {
            try {
                Object obj2 = this.subscriber.get();
                if (obj2 != null) {
                    this.method.invoke(obj2, new Object[]{obj});
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                Debug.Log("InvocationTargetException in handler for " + obj.getClass().getSimpleName());
                Throwable cause = e3.getCause();
                if (cause != null) {
                    cause.printStackTrace();
                } else {
                    e3.printStackTrace();
                }
            }
        }
    }

    private static class HandlerList extends LinkedList<HandlerInfo> {
        private HandlerList() {
        }

        /* synthetic */ HandlerList(HandlerList handlerList) {
            this();
        }

        public void deleteAll(Object obj) {
            LinkedList linkedList = new LinkedList();
            Iterator it = iterator();
            while (it.hasNext()) {
                HandlerInfo handlerInfo = (HandlerInfo) it.next();
                Object obj2 = handlerInfo.subscriber.get();
                if (obj2 == null || obj2 == obj) {
                    linkedList.add(handlerInfo);
                }
            }
            removeAll(linkedList);
        }

        public void invokeAll(Object obj) {
            Iterator it = iterator();
            while (it.hasNext()) {
                ((HandlerInfo) it.next()).invoke(obj);
            }
        }
    }

    public synchronized boolean handleEventQueueMessage(SLCapEventQueue.CapsEventType capsEventType, LLSDNode lLSDNode) {
        HandlerList handlerList = this.eventQueueMessageHandlers.get(capsEventType);
        if (handlerList == null) {
            return false;
        }
        handlerList.invokeAll(lLSDNode);
        return true;
    }

    public synchronized boolean handleMessage(Object obj) {
        HandlerList handlerList = this.messageHandlers.get(obj.getClass());
        if (handlerList == null) {
            return false;
        }
        handlerList.invokeAll(obj);
        return true;
    }

    public synchronized void registerHandler(Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            if (((SLMessageHandler) method.getAnnotation(SLMessageHandler.class)) != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException("SLMessageHandler methods must specify a single SLMessage paramter.");
                }
                Class cls = parameterTypes[0];
                HandlerInfo handlerInfo = new HandlerInfo(method, obj);
                HandlerList handlerList = this.messageHandlers.get(cls);
                if (handlerList == null) {
                    handlerList = new HandlerList((HandlerList) null);
                    this.messageHandlers.put(cls, handlerList);
                }
                handlerList.add(handlerInfo);
            }
            SLEventQueueMessageHandler sLEventQueueMessageHandler = (SLEventQueueMessageHandler) method.getAnnotation(SLEventQueueMessageHandler.class);
            if (sLEventQueueMessageHandler != null) {
                if (method.getParameterTypes().length != 1) {
                    throw new IllegalArgumentException("SLMessageHandler methods must specify a single LLSDNode paramter.");
                }
                SLCapEventQueue.CapsEventType eventName = sLEventQueueMessageHandler.eventName();
                HandlerInfo handlerInfo2 = new HandlerInfo(method, obj);
                HandlerList handlerList2 = this.eventQueueMessageHandlers.get(eventName);
                if (handlerList2 == null) {
                    handlerList2 = new HandlerList((HandlerList) null);
                    this.eventQueueMessageHandlers.put(eventName, handlerList2);
                }
                handlerList2.add(handlerInfo2);
            }
        }
    }

    public synchronized void unregisterHandler(Object obj) {
        for (HandlerList deleteAll : this.messageHandlers.values()) {
            deleteAll.deleteAll(obj);
        }
        for (HandlerList deleteAll2 : this.eventQueueMessageHandlers.values()) {
            deleteAll2.deleteAll(obj);
        }
    }
}
