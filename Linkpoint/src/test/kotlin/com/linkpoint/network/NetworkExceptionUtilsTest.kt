package com.linkpoint.network

import org.junit.Assert.*
import org.junit.Test
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

/**
 * Unit tests for NetworkExceptionUtils
 */
class NetworkExceptionUtilsTest {
    
    @Test
    fun `test direct EOFException is detected`() {
        val e = EOFException("End of file")
        assertTrue("Should detect direct EOFException", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test EOFIOException is detected`() {
        val e = EOFIOException("EOF while reading response body")
        assertTrue("Should detect EOFIOException", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test wrapped EOFException is detected`() {
        val eof = EOFException("Unexpected end of stream")
        val wrapped = IOException("Network error", eof)
        assertTrue("Should detect wrapped EOFException", NetworkExceptionUtils.isEOFException(wrapped))
    }
    
    @Test
    fun `test EOF message in IOException is detected`() {
        val e = IOException("EOF while reading response")
        assertTrue("Should detect EOF in message", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test connection reset is detected as EOF-related`() {
        val e = IOException("Connection reset by peer")
        assertTrue("Should detect connection reset as EOF", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test ECONNRESET is detected as EOF-related`() {
        val e = IOException("ECONNRESET")
        assertTrue("Should detect ECONNRESET as EOF", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test SSLException with reset is detected`() {
        val e = SSLException("Connection reset during SSL handshake")
        assertTrue("Should detect SSL reset as EOF", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test SocketException with closed is detected`() {
        val e = SocketException("Socket closed")
        assertTrue("Should detect socket closed as EOF", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test normal IOException is not detected as EOF`() {
        val e = IOException("Some other network error")
        assertFalse("Should not detect normal IOException as EOF", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test timeout is not detected as EOF`() {
        val e = SocketTimeoutException("Connection timed out")
        assertFalse("Should not detect timeout as EOF", NetworkExceptionUtils.isEOFException(e))
    }
    
    @Test
    fun `test isConnectionResetException detects reset`() {
        val e = IOException("Connection reset")
        assertTrue("Should detect connection reset", NetworkExceptionUtils.isConnectionResetException(e))
    }
    
    @Test
    fun `test isConnectionResetException detects ECONNRESET`() {
        val e = SocketException("ECONNRESET")
        assertTrue("Should detect ECONNRESET", NetworkExceptionUtils.isConnectionResetException(e))
    }
    
    @Test
    fun `test isTransientError detects EOF`() {
        val e = EOFException("EOF")
        assertTrue("EOF should be transient", NetworkExceptionUtils.isTransientError(e))
    }
    
    @Test
    fun `test isTransientError detects timeout`() {
        val e = SocketTimeoutException("Timeout")
        assertTrue("Timeout should be transient", NetworkExceptionUtils.isTransientError(e))
    }
    
    @Test
    fun `test isTransientError detects connection reset`() {
        val e = IOException("Connection reset")
        assertTrue("Connection reset should be transient", NetworkExceptionUtils.isTransientError(e))
    }
    
    @Test
    fun `test EOF_EXTRA_DELAY_MS value`() {
        assertEquals("EOF extra delay should be 500ms", 500L, NetworkExceptionUtils.EOF_EXTRA_DELAY_MS)
    }
    
    @Test
    fun `test getEOFErrorDescription returns non-empty string`() {
        val description = NetworkExceptionUtils.getEOFErrorDescription()
        assertTrue("Description should not be empty", description.isNotEmpty())
        assertTrue("Description should mention 'server'", description.contains("server", ignoreCase = true))
        assertTrue("Description should mention 'try again'", description.contains("try again", ignoreCase = true))
    }
    
    @Test
    fun `test deeply nested EOFException is detected`() {
        val eof = EOFException("EOF")
        val layer1 = IOException("Layer 1", eof)
        val layer2 = RuntimeException("Layer 2", layer1)
        val layer3 = Exception("Layer 3", layer2)
        assertTrue("Should detect deeply nested EOFException", NetworkExceptionUtils.isEOFException(layer3))
    }
    
    @Test
    fun `test very deeply nested exception stops at depth limit`() {
        // Create a chain deeper than 10 levels
        var current: Throwable = IOException("Root cause")
        repeat(15) { i ->
            current = Exception("Layer $i", current)
        }
        // The root cause is NOT an EOF error, so should return false
        assertFalse("Should not find EOF in deep non-EOF chain", NetworkExceptionUtils.isEOFException(current))
    }
}
