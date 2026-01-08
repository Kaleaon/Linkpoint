package com.lumiyaviewer.lumiya.slproto.https

import com.lumiyaviewer.lumiya.Debug
import java.io.BufferedInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object LLSDContentTypeDetector {

    enum class LLSDContentType {
        llsdXML,
        llsdBinary
    }

    @Throws(IOException::class)
    fun DetectContentType(bis: BufferedInputStream, contentType: String?): LLSDContentType {
        bis.mark(64)
        val buffer = ByteArray(64)
        val bytesRead = bis.read(buffer, 0, 64)
        
        if (bytesRead < 0) {
             bis.reset()
             return LLSDContentType.llsdXML 
        }

        var skipBytes = 0
        // Check for UTF-8 BOM: EF BB BF
        if (bytesRead >= 3 && 
            (buffer[0].toInt() and 0xFF) == 0xEF && 
            (buffer[1].toInt() and 0xFF) == 0xBB && 
            (buffer[2].toInt() and 0xFF) == 0xBF) {
            skipBytes = 3
        }

        val contentStr = String(buffer, skipBytes, bytesRead - skipBytes, StandardCharsets.UTF_8)
        
        bis.reset()
        if (skipBytes > 0) {
            bis.skip(skipBytes.toLong())
        }

        var isXml = false
        var isBinary = false

        if (contentStr.startsWith("<llsd>") || contentStr.startsWith("<?xml")) {
            isXml = true
        } else if (contentStr.startsWith("<? LLSD/Binary ?>") || 
                   contentStr.startsWith("<?llsd/binary") || 
                   contentStr.startsWith("{")) { 
             isBinary = true
        }

        Debug.Printf("LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'",
            contentType ?: "null", if(isBinary) "true" else "false", if(isXml) "true" else "false", skipBytes, contentStr)

        if (!isXml && !isBinary) {
             if (contentType != null && contentType.equals("application/llsd+binary", ignoreCase = true)) {
                 isBinary = true
             }
        }
        
        if (isBinary) {
             Debug.Printf("LLSD: using binary parser")
             return LLSDContentType.llsdBinary
        }
        
        Debug.Printf("LLSD: using XML parser")
        return LLSDContentType.llsdXML
    }
}
