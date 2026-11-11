package com.linkpoint.linden.llimage

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

typealias RequestHandle = UInt

interface DecodeResponder {
    fun completed(success: Boolean, errorMessage: String, raw: ImageRaw?, aux: ImageRaw?, requestId: RequestHandle)
}

data class DecodeRequest(
    val handle: RequestHandle,
    val encodedData: ByteArray,
    val extension: String,
    val discard: Int,
    val needsAux: Boolean,
    val responder: DecodeResponder
)

open class ImageWorker(private val threaded: Boolean = true) {

    private val executor: ExecutorService = if (threaded)
        Executors.newFixedThreadPool(DECODE_THREAD_COUNT)
    else
        Executors.newSingleThreadExecutor()

    private val nextHandle = AtomicInteger(1)
    private val pending: ConcurrentHashMap<RequestHandle, Future<*>> = ConcurrentHashMap()
    private val decodeCount = AtomicInteger(0)

    fun decodeImage(
        encodedData: ByteArray,
        extension: String,
        discard: Int,
        needsAux: Boolean,
        responder: DecodeResponder
    ): RequestHandle {
        val handle = nextHandle.getAndIncrement().toUInt()
        val request = DecodeRequest(handle, encodedData, extension, discard, needsAux, responder)
        val future = executor.submit { processRequest(request) }
        pending[handle] = future
        return handle
    }

    private fun processRequest(request: DecodeRequest) {
        var raw: ImageRaw? = null
        var aux: ImageRaw? = null
        var success = false
        var errorMessage = ""

        try {
            val codec = ImageCodecRegistry.get(request.extension)
            if (codec == null) {
                errorMessage = "No codec for extension: ${request.extension}"
            } else {
                raw = codec.decode(request.encodedData)
                if (raw == null) {
                    errorMessage = "Failed to decode image"
                } else {
                    success = true
                    if (request.needsAux && raw.components >= 4) {
                        aux = raw.convertToType(1)
                    }
                    if (request.discard > 0) {
                        val scale = 1 shl request.discard
                        val newW = (raw.width / scale).coerceAtLeast(1)
                        val newH = (raw.height / scale).coerceAtLeast(1)
                        raw = raw.scale(newW, newH)
                    }
                    decodeCount.incrementAndGet()
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error during decode"
            success = false
        } finally {
            pending.remove(request.handle)
            request.responder.completed(success, errorMessage, raw, aux, request.handle)
        }
    }

    fun getPending(): Int = pending.size

    fun getTotalDecodeCount(): Int = decodeCount.get()

    fun update(maxTimeMs: Float): Int {
        val deadline = System.currentTimeMillis() + maxTimeMs.toLong()
        var processed = 0
        val iter = pending.iterator()
        while (iter.hasNext() && System.currentTimeMillis() < deadline) {
            val entry = iter.next()
            if (entry.value.isDone) {
                iter.remove()
                processed++
            }
        }
        return processed
    }

    fun shutdown() {
        executor.shutdown()
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    companion object {
        private const val DECODE_THREAD_COUNT = 4
        private const val SHUTDOWN_TIMEOUT_SECONDS = 5L
    }
}

class SyncImageWorker : ImageWorker(threaded = false)
