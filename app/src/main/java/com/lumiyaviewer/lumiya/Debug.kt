package com.lumiyaviewer.lumiya

import android.util.Log
import java.nio.ByteBuffer

/**
 * Modern Kotlin Debug utility class
 * Provides logging and debugging functionality for the Lumiya application
 */
object Debug {
    private const val LOG_TAG = "Lumiya"

    /**
     * Always prints a formatted log message with caller information
     * @param format The format string
     * @param args Optional format arguments
     */
    @JvmStatic
    fun AlwaysPrintf(
        format: String,
        vararg args: Any?,
    ) {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size > 3) {
            val element = stackTrace[3]
            val className = element.className
            val simpleClassName = className.substring(className.lastIndexOf('.') + 1)
            val message = "[$simpleClassName::${element.methodName}] ${String.format(format, *args)}"
            Log.d(LOG_TAG, message)
        }
    }

    /**
     * Dumps a ByteBuffer for debugging (no-op in release builds)
     * @param label Label for the dump
     * @param buffer The ByteBuffer to dump
     */
    @JvmStatic
    fun DumpBuffer(
        label: String,
        buffer: ByteBuffer,
    ) {
        // No-op in release builds
    }

    /**
     * Dumps a byte array for debugging (no-op in release builds)
     * @param label Label for the dump
     * @param data The byte array to dump
     */
    @JvmStatic
    fun DumpBuffer(
        label: String,
        data: ByteArray,
    ) {
        // No-op in release builds
    }

    /**
     * Dumps a portion of a byte array for debugging (no-op in release builds)
     * @param label Label for the dump
     * @param data The byte array to dump
     * @param length Number of bytes to dump
     */
    @JvmStatic
    fun DumpBuffer(
        label: String,
        data: ByteArray,
        length: Int,
    ) {
        // No-op in release builds
    }

    /**
     * Logs a simple message (no-op in release builds)
     * @param message The message to log
     */
    @JvmStatic
    fun Log(message: String) {
        // No-op in release builds
    }

    /**
     * Prints a formatted log message
     * @param format The format string
     * @param args Optional format arguments
     */
    @JvmStatic
    fun Printf(
        format: String,
        vararg args: Any?,
    ) {
        try {
            val message =
                if (args.isEmpty()) {
                    format
                } else {
                    String.format(format, *args)
                }
            Log.d(LOG_TAG, message)
        } catch (e: Exception) {
            Log.d(LOG_TAG, "$format (format error: ${e.message})")
        }
    }

    /**
     * Logs a warning from a Throwable (no-op in release builds)
     * @param throwable The throwable to log
     */
    @JvmStatic
    fun Warning(throwable: Throwable) {
        // No-op in release builds
    }

    /**
     * Checks if this is a debug build
     * @return false in release builds
     */
    @JvmStatic
    fun isDebugBuild(): Boolean = false
}
