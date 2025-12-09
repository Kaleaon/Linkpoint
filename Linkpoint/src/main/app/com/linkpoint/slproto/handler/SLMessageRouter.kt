package com.linkpoint.slproto.handler

import com.linkpoint.Debug
import com.linkpoint.slproto.caps.SLCapEventQueue
import com.linkpoint.slproto.llsd.LLSDNode
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.HashMap
import java.util.Iterator
import java.util.LinkedList
import java.util.Map

class SLMessageRouter {
    private Map<SLCapEventQueue.CapsEventType, HandlerList> eventQueueMessageHandlers = HashMap()
    private Map<Class<?>, HandlerList> messageHandlers = HashMap()

    private class HandlerInfo {
        private Method method
        /* access modifiers changed from: private */
        WeakReference<?> subscriber

        HandlerInfo(Method method2, Object obj) {
            this.method = method2
            this.subscriber = WeakReference<>(obj)
        }

        fun invoke(Object obj): Unit {
            try {
                Object obj2 = this.subscriber.get()
                if (obj2 != null) {
                    this.method.invoke(obj2, Array<Any>{obj})
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace()
            } catch (IllegalAccessException e2) {
                e2.printStackTrace()
            } catch (InvocationTargetException e3) {
                Debug.Log("InvocationTargetException in handler for " + obj.getClass().getSimpleName())
                Throwable cause = e3.getCause()
                if (cause != null) {
                    cause.printStackTrace()
                } else {
                    e3.printStackTrace()
                }
            }
        }
    }

    private class HandlerList : LinkedList<HandlerInfo> {
        private HandlerList() {
        }

        /* synthetic */ HandlerList(HandlerList handlerList) {
            this()
        }

        fun deleteAll(Object obj): Unit {
            LinkedList linkedList = LinkedList()
            Iterator it = iterator()
            while (it.hasNext()) {
                HandlerInfo handlerInfo = (it as HandlerInfo).next()
                Object obj2 = handlerInfo.subscriber.get()
                if (obj2 == null || obj2 == obj) {
                    linkedList.add(handlerInfo)
                }
            }
            removeAll(linkedList)
        }

        fun invokeAll(Object obj): Unit {
            Iterator it = iterator()
            while (it.hasNext()) {
                ((it as HandlerInfo).next()).invoke(obj)
            }
        }
    }

    synchronized Boolean handleEventQueueMessage(SLCapEventQueue.CapsEventType capsEventType, LLSDNode lLSDNode) {
        HandlerList handlerList = this.eventQueueMessageHandlers.get(capsEventType)
        if (handlerList == null) {
            return false
        }
        handlerList.invokeAll(lLSDNode)
        return true
    }

    synchronized Boolean handleMessage(Object obj) {
        HandlerList handlerList = this.messageHandlers.get(obj.getClass())
        if (handlerList == null) {
            return false
        }
        handlerList.invokeAll(obj)
        return true
    }

    synchronized Unit registerHandler(Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            if (((method as SLMessageHandler).getAnnotation(SLMessageHandler.class)) != null) {
                Class[] parameterTypes = method.getParameterTypes()
                if (parameterTypes.size != 1) {
                    throw IllegalArgumentException("SLMessageHandler methods must specify a single SLMessage paramter.")
                }
                Class cls = parameterTypes[0]
                HandlerInfo handlerInfo = HandlerInfo(method, obj)
                HandlerList handlerList = this.messageHandlers.get(cls)
                if (handlerList == null) {
                    handlerList = HandlerList((HandlerList) null)
                    this.messageHandlers.put(cls, handlerList)
                }
                handlerList.add(handlerInfo)
            }
            SLEventQueueMessageHandler sLEventQueueMessageHandler = (method as SLEventQueueMessageHandler).getAnnotation(SLEventQueueMessageHandler.class)
            if (sLEventQueueMessageHandler != null) {
                if (method.getParameterTypes().size != 1) {
                    throw IllegalArgumentException("SLMessageHandler methods must specify a single LLSDNode paramter.")
                }
                SLCapEventQueue.CapsEventType eventName = sLEventQueueMessageHandler.eventName()
                HandlerInfo handlerInfo2 = HandlerInfo(method, obj)
                HandlerList handlerList2 = this.eventQueueMessageHandlers.get(eventName)
                if (handlerList2 == null) {
                    handlerList2 = HandlerList((HandlerList) null)
                    this.eventQueueMessageHandlers.put(eventName, handlerList2)
                }
                handlerList2.add(handlerInfo2)
            }
        }
    }

    synchronized Unit unregisterHandler(Object obj) {
        for (HandlerList deleteAll : this.messageHandlers.values()) {
            deleteAll.deleteAll(obj)
        }
        for (HandlerList deleteAll2 : this.eventQueueMessageHandlers.values()) {
            deleteAll2.deleteAll(obj)
        }
    }
}
