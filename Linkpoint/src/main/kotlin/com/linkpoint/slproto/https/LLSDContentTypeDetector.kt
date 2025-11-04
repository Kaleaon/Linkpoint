package com.linkpoint.slproto.https

import com.linkpoint.Debug
import java.io.BufferedInputStream
import java.io.IOException

object LLSDContentTypeDetector {

    enum class LLSDContentType { llsdXML, llsdBinary }

    private val UTF8_BOM = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())

    @Throws(IOException::class)
    fun detectContentType(stream: BufferedInputStream, contentType: String?): LLSDContentType {
        stream.mark(64)
        val header = ByteArray(32)
        val read = stream.read(header)
        var skip = 0

        if (read >= UTF8_BOM.size && header.sliceArray(0 until UTF8_BOM.size).contentEquals(UTF8_BOM)) {
            skip = UTF8_BOM.size
        }

        val preview = if (read > skip) {
            String(header, skip, read - skip, Charsets.UTF_8).trimStart()
        } else ""

        val xmlHint = preview.startsWith("<llsd", ignoreCase = true) || preview.startsWith("<?xml", ignoreCase = true)
        val binaryHint = preview.startsWith("<? llSD/Binary", ignoreCase = true) ||
            preview.startsWith("<?llsd/binary", ignoreCase = true) ||
            preview.startsWith("{")

        stream.reset()
        if (skip > 0) {
            stream.skip(skip.toLong())
        }

        val headerHintBinary = contentType.equals("application/llsd+binary", ignoreCase = true)

        Debug.Printf(
            "LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'",
            contentType,
            binaryHint || headerHintBinary,
            xmlHint,
            skip,
            preview
        )

        return when {
            binaryHint || headerHintBinary -> LLSDContentType.llsdBinary
            xmlHint -> LLSDContentType.llsdXML
            else -> LLSDContentType.llsdXML
        }
    }
}
