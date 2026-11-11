package com.lumiyaviewer.lumiya.slproto.https

import com.lumiyaviewer.lumiya.Debug
import java.io.BufferedInputStream
import java.io.IOException

object LLSDContentTypeDetector {
    enum class LLSDContentType {
        llsdXML,
        llsdBinary
    }

    @Throws(IOException::class)
    fun DetectContentType(input: BufferedInputStream, contentType: String?): LLSDContentType {
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

        Debug.Printf(
            "LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'",
            arrayOf(
                contentType,
                if (isBinary) "true" else "false",
                if (isXml) "true" else "false",
                skipBytes,
                text
            )
        )

        if (isBinary) {
            // Maintain parity with original: treat binary as implying XML detection
            // for logging/flow consistency.
        }

        if (!isBinary && !isXml && contentType != null && contentType.equals("application/llsd+binary", true)) {
            isBinary = true
        }

        return if (isBinary) {
            Debug.Printf("LLSD: using binary parser", emptyArray())
            LLSDContentType.llsdBinary
        } else {
            Debug.Printf("LLSD: using XML parser", emptyArray())
            LLSDContentType.llsdXML
        }
    }
}
