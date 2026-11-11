package com.linkpoint.linden.llmessage

fun interface LLMessageHandler {
    fun handle(message: Map<String, Any>): Boolean
}

class LLDispatcher {
    private val handlers: MutableMap<String, LLMessageHandler> = linkedMapOf()
    private var defaultHandler: LLMessageHandler? = null
    private var dispatchCount: Long = 0L

    fun addHandler(name: String, handler: LLMessageHandler) {
        handlers[name] = handler
    }

    fun removeHandler(name: String) = handlers.remove(name)

    fun setDefaultHandler(handler: LLMessageHandler?) { defaultHandler = handler }

    fun dispatch(messageName: String, message: Map<String, Any>): Boolean {
        dispatchCount++
        val handler = handlers[messageName] ?: defaultHandler ?: return false
        return runCatching { handler.handle(message) }.getOrDefault(false)
    }

    fun hasHandler(name: String): Boolean = name in handlers

    fun getRegisteredNames(): Set<String> = handlers.keys.toSet()

    fun getDispatchCount(): Long = dispatchCount

    fun clear() = handlers.clear()
}
