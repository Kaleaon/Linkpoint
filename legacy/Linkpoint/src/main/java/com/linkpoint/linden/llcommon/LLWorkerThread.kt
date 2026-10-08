package com.linkpoint.linden.llcommon

abstract class LLWorkerClass {
    var requestHandle: Long = -1L
    var priority: Int = 0

    abstract fun doWork(param: Float): Boolean
    open fun finishWork() {}
    open fun abortWork() {}
}

class LLWorkerThread(name: String) : LLQueuedThread(name) {
    private val workers: MutableMap<Long, LLWorkerClass> = mutableMapOf()

    fun addWorkRequest(worker: LLWorkerClass, priority: Int = 0): Long {
        val id = addRequest(priority, worker)
        worker.requestHandle = id
        synchronized(workers) { workers[id] = worker }
        return id
    }

    fun completeWorkRequest(id: Long) {
        synchronized(workers) { workers[id] }?.finishWork()
        synchronized(workers) { workers.remove(id) }
    }

    fun abortWorkRequest(id: Long) {
        synchronized(workers) { workers[id] }?.abortWork()
        abortRequest(id)
        synchronized(workers) { workers.remove(id) }
    }

    override fun processRequest(request: Request) {
        val worker = request.payload as? LLWorkerClass ?: return
        var param = 0f
        while (shouldRun()) {
            if (worker.doWork(param)) break
            param += 0.1f
        }
        worker.finishWork()
        synchronized(workers) { workers.remove(request.id) }
    }
}
