package com.linkpoint.network

import org.junit.Assert.*
import org.junit.Test
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

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
    
    // ============================================================
    // Tests for new detailed error reporting functionality
    // ============================================================
    
    @Test
    fun `test classifyException identifies EOF as CONNECTION_CLOSED`() {
        val e = EOFException("Connection closed")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("EOF should be classified as CONNECTION_CLOSED", 
            NetworkExceptionUtils.ErrorCategory.CONNECTION_CLOSED, category)
    }
    
    @Test
    fun `test classifyException identifies connection reset as CONNECTION_CLOSED`() {
        val e = IOException("Connection reset by peer")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("Connection reset should be classified as CONNECTION_CLOSED", 
            NetworkExceptionUtils.ErrorCategory.CONNECTION_CLOSED, category)
    }
    
    @Test
    fun `test classifyException identifies SSLException as SSL_ERROR`() {
        val e = SSLException("SSL handshake failed")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("SSLException should be classified as SSL_ERROR", 
            NetworkExceptionUtils.ErrorCategory.SSL_ERROR, category)
    }
    
    @Test
    fun `test classifyException identifies SocketTimeoutException as TIMEOUT`() {
        val e = SocketTimeoutException("Connection timed out")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("SocketTimeoutException should be classified as TIMEOUT", 
            NetworkExceptionUtils.ErrorCategory.TIMEOUT, category)
    }
    
    @Test
    fun `test classifyException identifies UnknownHostException as DNS_ERROR`() {
        val e = UnknownHostException("Unable to resolve host")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("UnknownHostException should be classified as DNS_ERROR", 
            NetworkExceptionUtils.ErrorCategory.DNS_ERROR, category)
    }
    
    @Test
    fun `test classifyException identifies ConnectException as CONNECTION_REFUSED`() {
        val e = ConnectException("Connection refused")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("ConnectException should be classified as CONNECTION_REFUSED", 
            NetworkExceptionUtils.ErrorCategory.CONNECTION_REFUSED, category)
    }
    
    @Test
    fun `test classifyException identifies generic IOException as IO_ERROR`() {
        val e = IOException("Some I/O error")
        val category = NetworkExceptionUtils.classifyException(e)
        assertEquals("Generic IOException should be classified as IO_ERROR", 
            NetworkExceptionUtils.ErrorCategory.IO_ERROR, category)
    }
    
    @Test
    fun `test getRootCause returns deepest cause`() {
        val rootCause = EOFException("Root EOF")
        val layer1 = IOException("Layer 1", rootCause)
        val layer2 = RuntimeException("Layer 2", layer1)
        
        val result = NetworkExceptionUtils.getRootCause(layer2)
        
        assertSame("Should return the root cause", rootCause, result)
    }
    
    @Test
    fun `test getRootCause returns self when no cause`() {
        val e = EOFException("No cause")
        
        val result = NetworkExceptionUtils.getRootCause(e)
        
        assertSame("Should return the exception itself when no cause", e, result)
    }
    
    @Test
    fun `test analyzeException returns DetailedErrorInfo for EOF`() {
        val e = EOFException("Connection closed by server")
        
        val info = NetworkExceptionUtils.analyzeException(
            e = e,
            loginUri = "https://login.test.com/cgi-bin/login.cgi",
            attemptNumber = 1,
            totalAttempts = 3,
            elapsedTimeMs = 1500
        )
        
        assertEquals("Category should be CONNECTION_CLOSED", 
            NetworkExceptionUtils.ErrorCategory.CONNECTION_CLOSED, info.category)
        assertEquals("Error code should be EOF_ERROR", "EOF_ERROR", info.errorCode)
        assertEquals("Root cause type should be EOFException", "EOFException", info.rootCauseType)
        assertTrue("Should be marked as transient", info.isTransient)
        assertTrue("User message should not be empty", info.userMessage.isNotEmpty())
        assertTrue("Technical details should contain exception info", 
            info.technicalDetails.contains("EOFException"))
        assertTrue("Recommendations should not be empty", info.recommendations.isNotEmpty())
    }
    
    @Test
    fun `test analyzeException returns DetailedErrorInfo for SSL error`() {
        val e = SSLHandshakeException("Handshake failed")
        
        val info = NetworkExceptionUtils.analyzeException(
            e = e,
            loginUri = "https://login.test.com/cgi-bin/login.cgi",
            attemptNumber = 2,
            totalAttempts = 3,
            elapsedTimeMs = 5000
        )
        
        assertEquals("Category should be SSL_ERROR", 
            NetworkExceptionUtils.ErrorCategory.SSL_ERROR, info.category)
        assertEquals("Error code should be SSL_ERROR", "SSL_ERROR", info.errorCode)
        assertTrue("User message should mention handshake or certificate", 
            info.userMessage.contains("handshake", ignoreCase = true) || 
            info.userMessage.contains("certificate", ignoreCase = true) ||
            info.userMessage.contains("secure", ignoreCase = true))
        assertTrue("Technical details should contain SSL analysis", 
            info.technicalDetails.contains("SSL", ignoreCase = true))
    }
    
    @Test
    fun `test analyzeException returns DetailedErrorInfo for timeout`() {
        val e = SocketTimeoutException("Read timed out")
        
        val info = NetworkExceptionUtils.analyzeException(
            e = e,
            loginUri = "https://login.test.com/cgi-bin/login.cgi",
            attemptNumber = 3,
            totalAttempts = 3,
            elapsedTimeMs = 90000
        )
        
        assertEquals("Category should be TIMEOUT", 
            NetworkExceptionUtils.ErrorCategory.TIMEOUT, info.category)
        assertEquals("Error code should be TIMEOUT", "TIMEOUT", info.errorCode)
        assertTrue("Should be marked as transient", info.isTransient)
        assertTrue("User message should mention timeout", 
            info.userMessage.contains("timeout", ignoreCase = true))
    }
    
    @Test
    fun `test analyzeException returns DetailedErrorInfo for DNS error`() {
        val e = UnknownHostException("Unable to resolve host \"login.test.com\"")
        
        val info = NetworkExceptionUtils.analyzeException(
            e = e,
            loginUri = "https://login.test.com/cgi-bin/login.cgi",
            attemptNumber = 1,
            totalAttempts = 1,
            elapsedTimeMs = 500
        )
        
        assertEquals("Category should be DNS_ERROR", 
            NetworkExceptionUtils.ErrorCategory.DNS_ERROR, info.category)
        assertEquals("Error code should be DNS_ERROR", "DNS_ERROR", info.errorCode)
        assertTrue("User message should mention resolve or DNS", 
            info.userMessage.contains("resolve", ignoreCase = true) || 
            info.userMessage.contains("DNS", ignoreCase = true))
    }
    
    @Test
    fun `test buildExceptionChain produces readable output`() {
        val rootCause = EOFException("Root cause")
        val layer1 = IOException("Layer 1", rootCause)
        val layer2 = RuntimeException("Layer 2", layer1)
        
        val chain = NetworkExceptionUtils.buildExceptionChain(layer2)
        
        assertTrue("Chain should contain RuntimeException", chain.contains("RuntimeException"))
        assertTrue("Chain should contain IOException", chain.contains("IOException"))
        assertTrue("Chain should contain EOFException", chain.contains("EOFException"))
        assertTrue("Chain should contain Layer 2", chain.contains("Layer 2"))
        assertTrue("Chain should contain Layer 1", chain.contains("Layer 1"))
        assertTrue("Chain should contain Root cause", chain.contains("Root cause"))
    }
    
    @Test
    fun `test formatForErrorDialog returns summary and details`() {
        val e = EOFException("Connection closed")
        val info = NetworkExceptionUtils.analyzeException(e)
        
        val (summary, details) = NetworkExceptionUtils.formatForErrorDialog(info)
        
        assertTrue("Summary should not be empty", summary.isNotEmpty())
        assertTrue("Details should not be empty", details.isNotEmpty())
        assertTrue("Details should contain error code", details.contains("Error Code"))
        assertTrue("Details should contain category", details.contains("Category"))
        assertTrue("Details should contain recommendations", details.contains("Recommended Actions"))
    }
    
    @Test
    fun `test suggestedRetryDelayMs increases with attempts`() {
        val e = EOFException("Connection closed")
        
        val info1 = NetworkExceptionUtils.analyzeException(e, attemptNumber = 1)
        val info2 = NetworkExceptionUtils.analyzeException(e, attemptNumber = 2)
        val info3 = NetworkExceptionUtils.analyzeException(e, attemptNumber = 3)
        
        assertTrue("Retry delay should increase with attempts", 
            info1.suggestedRetryDelayMs <= info2.suggestedRetryDelayMs)
        assertTrue("Retry delay should continue increasing", 
            info2.suggestedRetryDelayMs <= info3.suggestedRetryDelayMs)
    }
    
    @Test
    fun `test DetailedErrorInfo contains exception chain`() {
        val rootCause = EOFException("Root")
        val wrapped = IOException("Wrapped", rootCause)
        
        val info = NetworkExceptionUtils.analyzeException(wrapped)
        
        assertTrue("Exception chain should not be empty", info.exceptionChain.isNotEmpty())
        assertTrue("Exception chain should contain IOException", 
            info.exceptionChain.contains("IOException"))
        assertTrue("Exception chain should contain EOFException", 
            info.exceptionChain.contains("EOFException"))
    }
    
    @Test
    fun `test recommendations include actionable steps`() {
        val e = EOFException("Connection closed")
        val info = NetworkExceptionUtils.analyzeException(e)
        
        assertTrue("Should have at least 2 recommendations", info.recommendations.size >= 2)
        
        // Check that recommendations are actionable (contain verbs or specific actions)
        val hasActionableRecommendation = info.recommendations.any { rec ->
            rec.contains("try", ignoreCase = true) ||
            rec.contains("check", ignoreCase = true) ||
            rec.contains("wait", ignoreCase = true) ||
            rec.contains("switch", ignoreCase = true) ||
            rec.contains("disable", ignoreCase = true)
        }
        assertTrue("Recommendations should be actionable", hasActionableRecommendation)
    }
}
