package com.linkpoint.linden.llaudio

import com.linkpoint.linden.llcommon.LLUUID
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

typealias DecodeCallback = (id: LLUUID, buffer: LLAudioBuffer?) -> Unit

class LLAudioDecodeMgr {
    private data class DecodeRequest(val id: LLUUID, val data: ByteArray, val callback: DecodeCallback)

    private val queue = LinkedBlockingQueue<DecodeRequest>()
    private val running = AtomicBoolean(false)
    private var thread: Thread? = null

    fun start() {
        if (running.compareAndSet(false, true)) {
            thread = Thread({
                while (running.get()) {
                    val req = queue.poll(100L, java.util.concurrent.TimeUnit.MILLISECONDS) ?: continue
                    val buffer = LLAudioBuffer(req.id)
                    val loaded = buffer.loadWAV(req.data)
                    req.callback(req.id, if (loaded) buffer else null)
                }
            }, "LLAudioDecodeMgr").also { it.isDaemon = true; it.start() }
        }
    }

    fun stop() {
        running.set(false)
        thread?.interrupt()
        thread = null
    }

    fun addDecodeRequest(id: LLUUID, data: ByteArray, callback: DecodeCallback) {
        queue.put(DecodeRequest(id, data, callback))
    }

    fun pendingCount(): Int = queue.size
}
