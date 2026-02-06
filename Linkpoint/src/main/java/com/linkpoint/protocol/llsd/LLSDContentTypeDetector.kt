package com.linkpoint.protocol.llsd

import android.util.Log
import java.io.BufferedInputStream
import java.io.IOException

object LLSDContentTypeDetector {
    private const val TAG = "LLSDContentTypeDetector"

    enum class LLSDContentType {
        LLSD_XML,
        LLSD_BINARY
    }

    @Throws(IOException::class)
    fun detect(input: BufferedInputStream, contentType: String?): LLSDContentType {
        val bom = byteArrayOf(-17, -69, -65)
        input.mark(64)
        val buffer = ByteArray(32)
        val readCount = input.read(buffer, 0, buffer.size)
        var skipBytes = 0
        if (readCount >= bom.size) {
            var match = true
            for (i in bom.indices) {
                if (buffer[i] != bom[i]) {
                    match = false
                    break
                }
            }
            if (match) {
                skipBytes = bom.size
            }
        }
        val text = if (readCount > skipBytes) {
            String(buffer, skipBytes, readCount - skipBytes, Charsets.UTF_8)
        } else {
            ""
        }
        input.reset()
        input.skip(skipBytes.toLong())

        val isXml = text.startsWith("<llsd>") || text.startsWith("<?xml")
        var isBinary = text.startsWith("<? LLSD/Binary ?>") || text.startsWith("{") || text.startsWith("<?llsd/binary")

        Log.d(
            TAG,
            "contentType='$contentType', detectedBinary=$isBinary, detectedXml=$isXml, skipBytes=$skipBytes, preview='${text.take(40)}'"
        )

        if (!isBinary && !isXml && contentType != null && contentType.equals("application/llsd+binary", true)) {
            isBinary = true
        }

        return if (isBinary) LLSDContentType.LLSD_BINARY else LLSDContentType.LLSD_XML
    }
}
