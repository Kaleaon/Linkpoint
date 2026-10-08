package com.linkpoint.linden.llcorehttp

import com.linkpoint.linden.llcommon.LLURI

enum class HttpMethod { GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS }

class LLHttpRequest(
    var method: HttpMethod = HttpMethod.GET,
    var url: LLURI = LLURI(),
    val headers: LLHttpHeaders = LLHttpHeaders(),
    var body: ByteArray? = null,
    var timeoutMs: Long = 30_000L,
    var followRedirects: Boolean = true
) {
    fun setHeader(name: String, value: String): LLHttpRequest {
        headers.set(name, value)
        return this
    }

    fun setBody(data: ByteArray, contentType: String = "application/octet-stream"): LLHttpRequest {
        body = data
        headers.set("Content-Type", contentType)
        return this
    }

    fun setJsonBody(json: String): LLHttpRequest =
        setBody(json.toByteArray(Charsets.UTF_8), "application/json")

    companion object {
        fun get(url: String) = LLHttpRequest(HttpMethod.GET, LLURI.fromString(url))
        fun post(url: String, body: ByteArray? = null) =
            LLHttpRequest(HttpMethod.POST, LLURI.fromString(url), body = body)
        fun put(url: String, body: ByteArray? = null) =
            LLHttpRequest(HttpMethod.PUT, LLURI.fromString(url), body = body)
        fun delete(url: String) = LLHttpRequest(HttpMethod.DELETE, LLURI.fromString(url))
    }
}
