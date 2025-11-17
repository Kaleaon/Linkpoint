package com.linkpoint.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Utility object for hash functions used in Second Life protocol.
 * Converted from Java to Kotlin with improved error handling and extension functions.
 */
object HashUtils {
    /**
     * Generate MD5 hash of input string
     * Used for Second Life authentication
     */
    @JvmStatic
    fun MD5_Hash(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            messageDigest.joinToString("") {
                "%02x".format(it)
            }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("MD5 algorithm not available", e)
        }
    }

    /**
     * Generate SHA1 hash of input string
     */
    @JvmStatic
    fun SHA1_Hash(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-1")
            val messageDigest = md.digest(input.toByteArray())
            messageDigest.joinToString("") {
                "%02x".format(it)
            }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-1 algorithm not available", e)
        }
    }
}

/**
 * Kotlin extension functions for more idiomatic usage
 */
fun String.md5(): String = HashUtils.MD5_Hash(this)

fun String.sha1(): String = HashUtils.SHA1_Hash(this)
