package com.linkpoint.linden.llcorehttp

class LLHttpResponse(
    val status: Int,
    val headers: LLHttpHeaders = LLHttpHeaders(),
    val body: ByteArray = ByteArray(0)
) {
    val isSuccess: Boolean get() = status in 200..299
    val isRedirect: Boolean get() = status in 300..399
    val isClientError: Boolean get() = status in 400..499
    val isServerError: Boolean get() = status in 500..599

    fun bodyAsString(charset: String = "UTF-8"): String = String(body, charset(charset))

    fun contentType(): String? = headers.find("Content-Type")

    fun contentLength(): Long = headers.find("Content-Length")?.toLongOrNull() ?: body.size.toLong()

    override fun toString(): String = "HTTP $status (${body.size} bytes)"
}
