package com.lumiyaviewer.lumiya.base64

import android.util.Base64 as AndroidBase64

/**
 * Modern Kotlin Base64 utility
 * Provides encoding/decoding functionality using Android's built-in Base64
 */
object Base64 {
    
    /**
     * Decodes a Base64 encoded string to bytes
     * @param input The Base64 encoded string
     * @return Decoded byte array, or empty array if input is null
     */
    @JvmStatic
    fun decode(input: String?): ByteArray {
        return input?.let { 
            AndroidBase64.decode(it, AndroidBase64.DEFAULT) 
        } ?: byteArrayOf()
    }
    
    /**
     * Encodes bytes to a Base64 string
     * @param input The byte array to encode
     * @return Base64 encoded string, or empty string if input is null
     */
    @JvmStatic
    fun encode(input: ByteArray?): String {
        return input?.let { 
            AndroidBase64.encodeToString(it, AndroidBase64.DEFAULT) 
        } ?: ""
    }
    
    /**
     * Decodes a Base64 encoded string to bytes with specified flags
     * @param input The Base64 encoded string
     * @param flags Android Base64 flags
     * @return Decoded byte array
     */
    @JvmStatic
    fun decode(input: String?, flags: Int): ByteArray {
        return input?.let { 
            AndroidBase64.decode(it, flags) 
        } ?: byteArrayOf()
    }
    
    /**
     * Encodes bytes to a Base64 string with specified flags
     * @param input The byte array to encode
     * @param flags Android Base64 flags
     * @return Base64 encoded string
     */
    @JvmStatic
    fun encode(input: ByteArray?, flags: Int): String {
        return input?.let { 
            AndroidBase64.encodeToString(it, flags) 
        } ?: ""
    }
}