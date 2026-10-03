package com.linkpoint.network

import java.io.IOException

/**
 * IOException subclass specifically for EOF errors.
 * This makes it easier to detect and handle connection EOF issues distinctly.
 * 
 * EOF errors typically occur when:
 * - The server closes the connection before sending a complete response
 * - HTTP/2 connection issues with certain servers
 * - Server-side load balancing or timeout issues
 * - Network interruption during data transfer
 */
class EOFIOException(message: String, cause: Throwable? = null) : IOException(message, cause)
