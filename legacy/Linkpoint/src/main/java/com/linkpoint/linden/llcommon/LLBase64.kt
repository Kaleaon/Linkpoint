package com.linkpoint.linden.llcommon

import java.util.Base64

object Base64Codec {
    private val encoder: Base64.Encoder = Base64.getEncoder()
    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val urlEncoder: Base64.Encoder = Base64.getUrlEncoder()
    private val urlDecoder: Base64.Decoder = Base64.getUrlDecoder()

    fun encode(bytes: ByteArray): String =
        if (bytes.isEmpty()) "" else encoder.encodeToString(bytes)

    fun decode(s: String): ByteArray =
        if (s.isEmpty()) ByteArray(0) else decoder.decode(s)

    fun encodeURL(bytes: ByteArray): String =
        if (bytes.isEmpty()) "" else urlEncoder.encodeToString(bytes)

    fun decodeURL(s: String): ByteArray =
        if (s.isEmpty()) ByteArray(0) else urlDecoder.decode(s)
}
