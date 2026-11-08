package com.linkpoint.linden.llcommon

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

abstract class LLQueuedThread(name: String) : LLThread(name) {
    enum class RequestStatus { PENDING, COMPLETE, ABORTED }

    inner class Request(
        val id: Long,
        val priority: Int = 0,
        var status: RequestStatus = RequestStatus.PENDING,
        val payload: Any? = null
    )

    private val queue = LinkedBlockingQueue<Request>()
    private val idGen = AtomicLong(1L)

    fun addRequest(priority: Int = 0, payload: Any? = null): Long {
        val req = Request(idGen.getAndIncrement(), priority, payload = payload)
        queue.put(req)
        return req.id
    }

    fun abortRequest(id: Long) {
        queue.removeIf { it.id == id }
    }

    protected abstract fun processRequest(request: Request)

    override fun run() {
        while (shouldRun()) {
            val req = queue.poll(100L, TimeUnit.MILLISECONDS) ?: continue
            if (req.status == RequestStatus.PENDING) {
                processRequest(req)
                req.status = RequestStatus.COMPLETE
            }
        }
        queue.clear()
    }
}
