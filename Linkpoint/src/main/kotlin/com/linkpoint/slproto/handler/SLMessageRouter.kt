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

    @JvmStatic
private class HandlerInfo {
        private val Method method
        /* access modifiers changed from: private */
        val WeakReference<?> subscriber

        public HandlerInfo(Method method2, Object obj) {
            this.method = method2
            this.subscriber = WeakReference<>(obj)
        }

        fun invoke(obj: Object) {
            try {
                val obj2: Object = this.subscriber.get()
                if (obj2 != null) {
                    this.method.invoke(obj2, Array<Any>{obj})
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace()
            } catch (IllegalAccessException e2) {
                e2.printStackTrace()
            } catch (InvocationTargetException e3) {
                Debug.Log("InvocationTargetException in handler for " + obj.getClass().getSimpleName())
                val cause: Throwable = e3.getCause()
                if (cause != null) {
                    cause.printStackTrace()
                } else {
                    e3.printStackTrace()
                }
            }
        }
    }

    @JvmStatic
private class HandlerList : LinkedList()<HandlerInfo> {
        private HandlerList() {
        }

        /* synthetic */ HandlerList(HandlerList handlerList) {
            this()
        }

        fun deleteAll(obj: Object) {
            val linkedList: LinkedList = LinkedList()
            val it: Iterator = iterator()
            while (it.hasNext()) {
                val handlerInfo: HandlerInfo = (HandlerInfo) it.next()
                val obj2: Object = handlerInfo.subscriber.get()
                if (obj2 == null || obj2 == obj) {
                    linkedList.add(handlerInfo)
                }
            }
            removeAll(linkedList)
        }

        fun invokeAll(obj: Object) {
            val it: Iterator = iterator()
            while (it.hasNext()) {
                ((HandlerInfo) it.next()).invoke(obj)
            }
        }
    }

    public synchronized Boolean handleEventQueueMessage(SLCapEventQueue.CapsEventType capsEventType, LLSDNode lLSDNode) {
        val handlerList: HandlerList = this.eventQueueMessageHandlers.get(capsEventType)
        if (handlerList == null) {
            return false
        }
        handlerList.invokeAll(lLSDNode)
        return true
    }

    public synchronized Boolean handleMessage(Object obj) {
        val handlerList: HandlerList = this.messageHandlers.get(obj.getClass())
        if (handlerList == null) {
            return false
        }
        handlerList.invokeAll(obj)
        return true
    }

    public synchronized Unit registerHandler(Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            if (((SLMessageHandler) method.getAnnotation(SLMessageHandler.class)) != null) {
                val parameterTypes: Array<Class> = method.getParameterTypes()
                if (parameterTypes.length != 1) {
                    throw IllegalArgumentException("SLMessageHandler methods must specify a single SLMessage paramter.")
                }
                val cls: Class = parameterTypes[0]
                val handlerInfo: HandlerInfo = HandlerInfo(method, obj)
                val handlerList: HandlerList = this.messageHandlers.get(cls)
                if (handlerList == null) {
                    handlerList = HandlerList((HandlerList) null)
                    this.messageHandlers.put(cls, handlerList)
                }
                handlerList.add(handlerInfo)
            }
            val sLEventQueueMessageHandler: SLEventQueueMessageHandler = (SLEventQueueMessageHandler) method.getAnnotation(SLEventQueueMessageHandler.class)
            if (sLEventQueueMessageHandler != null) {
                if (method.getParameterTypes().length != 1) {
                    throw IllegalArgumentException("SLMessageHandler methods must specify a single LLSDNode paramter.")
                }
                SLCapEventQueue.CapsEventType eventName = sLEventQueueMessageHandler.eventName()
                val handlerInfo2: HandlerInfo = HandlerInfo(method, obj)
                val handlerList2: HandlerList = this.eventQueueMessageHandlers.get(eventName)
                if (handlerList2 == null) {
                    handlerList2 = HandlerList((HandlerList) null)
                    this.eventQueueMessageHandlers.put(eventName, handlerList2)
                }
                handlerList2.add(handlerInfo2)
            }
        }
    }

    public synchronized Unit unregisterHandler(Object obj) {
        for (HandlerList deleteAll : this.messageHandlers.values()) {
            deleteAll.deleteAll(obj)
        }
        for (HandlerList deleteAll2 : this.eventQueueMessageHandlers.values()) {
            deleteAll2.deleteAll(obj)
        }
    }
}
