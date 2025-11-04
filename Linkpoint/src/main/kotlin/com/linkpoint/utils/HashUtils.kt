package com.linkpoint.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Hash helper utilities mirrored from the original Lumiya implementation.
 */
object HashUtils {

    @JvmStatic
    fun md5(input: String): String = hash(input, "MD5")

    @JvmStatic
    fun sha1(input: String): String = hash(input, "SHA-1")

    @JvmStatic
    fun MD5_Hash(input: String): String = md5(input)

    @JvmStatic
    fun SHA1_Hash(input: String): String = sha1(input)

    private fun hash(input: String, algorithm: String): String {
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            val bytes = digest.digest(input.toByteArray())
            bytes.joinToString(separator = "") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Hash algorithm $algorithm not available", e)
        }
    }
}

fun String.md5(): String = HashUtils.md5(this)

fun String.sha1(): String = HashUtils.sha1(this)
