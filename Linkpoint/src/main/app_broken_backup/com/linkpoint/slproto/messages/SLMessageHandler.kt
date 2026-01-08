package com.linkpoint.slproto.messages

import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * Base message handler that dispatches protocol messages to statically typed handler methods.
 *
 * Kotlin-friendly replacement for the original Java `SLMessageHandler`. Subclasses can declare
 * methods named `Handle<ClassName>(message: ClassName)` (or the camelCase variant
 * `handle<ClassName>`) to receive specific messages. If no specific method is present, the
 * `DefaultMessageHandler` hook is invoked.
 */
open class SLMessageHandler {

    private val handlerCache = ConcurrentHashMap<Class<*>, Method?>()

    /**
     * Fallback invoked when no specific handler exists for a message type.
     */
    open fun DefaultMessageHandler(message: SLMessage) {}

    /**
     * Dispatch a message to the appropriate handler.
     */
    fun dispatch(message: SLMessage) {
        val handlerMethod = handlerCache.getOrPut(message.javaClass) {
            resolveHandlerMethod(message.javaClass)
        }

        if (handlerMethod != null) {
            try {
                handlerMethod.invoke(this, message)
            } catch (invocation: InvocationTargetException) {
                val cause = invocation.cause ?: invocation
                Debug.Warning(cause)
            } catch (ex: IllegalAccessException) {
                Debug.Warning(ex)
                DefaultMessageHandler(message)
            }
        } else {
            DefaultMessageHandler(message)
        }
    }

    /**
     * Compatibility wrapper mirroring the legacy Java signature.
     */
    fun Handle(message: SLMessage) = dispatch(message)

    private fun resolveHandlerMethod(messageClass: Class<*>): Method? {
        val exact = findCompatibleMethod("Handle${messageClass.simpleName}", messageClass)
        if (exact != null) {
            return exact
        }

        return findCompatibleMethod("handle${messageClass.simpleName}", messageClass)
    }

    private fun findCompatibleMethod(name: String, messageClass: Class<*>): Method? {
        return this::class.java.methods.firstOrNull { method ->
            method.name == name && method.parameterTypes.size == 1 &&
                method.parameterTypes[0].isAssignableFrom(messageClass)
        }
    }
}
