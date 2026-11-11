package com.linkpoint.linden.llcorehttp

import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

typealias HttpHandler = (response: LLHttpResponse?, error: Throwable?) -> Unit

object LLCoreHttp {
    private val executor = Executors.newCachedThreadPool { runnable ->
        Thread(runnable, "LLCoreHttp").also { it.isDaemon = true }
    }

    fun request(req: LLHttpRequest, handler: HttpHandler): CompletableFuture<Void> {
        return CompletableFuture.runAsync({
            try {
                handler(executeRequest(req), null)
            } catch (t: Throwable) {
                handler(null, t)
            }
        }, executor)
    }

    fun requestSync(req: LLHttpRequest): LLHttpResponse = executeRequest(req)

    private fun executeRequest(req: LLHttpRequest): LLHttpResponse {
        val connection = (URL(req.url.toString()).openConnection() as HttpURLConnection).apply {
            connectTimeout = req.timeoutMs.toInt().coerceAtLeast(0)
            readTimeout = req.timeoutMs.toInt().coerceAtLeast(0)
            instanceFollowRedirects = true
            requestMethod = req.method.name
            req.headers.toMap().forEach { (k, v) -> setRequestProperty(k, v) }
            req.body?.let {
                doOutput = true
                outputStream.use { os -> os.write(it) }
            }
        }
        return try {
            val responseHeaders = LLHttpHeaders()
            connection.headerFields.forEach { (k, vs) ->
                if (k != null) vs.forEach { v -> responseHeaders.append(k, v) }
            }
            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..399) connection.inputStream else connection.errorStream
            val body = stream?.use { it.readBytes() } ?: ByteArray(0)
            LLHttpResponse(statusCode, responseHeaders, body)
        } finally {
            connection.disconnect()
        }
    }
}
