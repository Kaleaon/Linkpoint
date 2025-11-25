package com.lumiyaviewer.lumiya.slproto.handler

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.EventQueueMessage
import java.lang.reflect.Method
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap

class SLMessageRouter {
    private val messageHandlers = ConcurrentHashMap<Class<out SLMessage>, HandlerList>()
    private val eventQueueMessageHandlers = ConcurrentHashMap<String, HandlerList>()

    private class HandlerInfo(val subscriber: Any, val method: Method)

    private class HandlerList {
        private val handlers = LinkedList<HandlerInfo>()

        @Synchronized
        fun add(subscriber: Any, method: Method) {
            handlers.add(HandlerInfo(subscriber, method))
        }

        @Synchronized
        fun remove(subscriber: Any) {
            val iterator = handlers.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().subscriber === subscriber) {
                    iterator.remove()
                }
            }
        }

        @Synchronized
        fun dispatch(message: SLMessage) {
            for (info in handlers) {
                try {
                    info.method.invoke(info.subscriber, message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        @Synchronized
        fun dispatchEvent(eventType: CapsEventType, node: LLSDNode): Boolean {
            var handled = false
            for (info in handlers) {
                try {
                    info.method.invoke(info.subscriber, eventType, node)
                    handled = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return handled
        }
    }

    fun registerHandler(messageClass: Class<out SLMessage>, subscriber: Any, method: Method) {
        val list = messageHandlers.getOrPut(messageClass) { HandlerList() }
        list.add(subscriber, method)
    }
    
    fun registerHandler(subscriber: Any) {
        val methods = subscriber.javaClass.methods
        for (method in methods) {
            if (method.isAnnotationPresent(SLMessageHandler::class.java)) {
                val params = method.parameterTypes
                if (params.size == 1 && SLMessage::class.java.isAssignableFrom(params[0])) {
                     @Suppress("UNCHECKED_CAST")
                     val messageClass = params[0] as Class<out SLMessage>
                     registerHandler(messageClass, subscriber, method)
                } else if (params.size == 2 && params[0] == CapsEventType::class.java && params[1] == LLSDNode::class.java) {
                    // Handle event queue handlers if annotated? 
                    // For now assuming this is how we register standard message handlers
                }
            }
        }
    }

    fun registerEventQueueHandler(capsEventType: String, subscriber: Any, method: Method) {
        val list = eventQueueMessageHandlers.getOrPut(capsEventType) { HandlerList() }
        list.add(subscriber, method)
    }

    fun unregisterHandler(subscriber: Any) {
        for (list in messageHandlers.values) {
            list.remove(subscriber)
        }
        for (list in eventQueueMessageHandlers.values) {
            list.remove(subscriber)
        }
    }
    
    fun handleMessage(message: SLMessage) {
        dispatch(message)
    }

    fun dispatch(message: SLMessage) {
        if (message is EventQueueMessage) {
            val body = message.Body_Field.Body
            // This is simplified; real implementation needs to parse LLSD from body to get event name
        }
        
        val list = messageHandlers[message::class.java]
        list?.dispatch(message)
    }
    
    fun handleEventQueueMessage(eventType: CapsEventType, node: LLSDNode): Boolean {
        val list = eventQueueMessageHandlers[eventType.name]
        return list?.dispatchEvent(eventType, node) ?: false
    }
}
