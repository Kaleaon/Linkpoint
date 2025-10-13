package com.linkpoint.utils

/**
 * String utility functions converted to Kotlin with null safety.
 * Leverages Kotlin's built-in string functions where possible.
 */
object StringUtils {
    
    /**
     * Count occurrences of a character in a string
     */
    @JvmStatic
    fun countOccurrences(str: String?, ch: Char): Int {
        return str?.count { it == ch } ?: 0
    }
    
    /**
     * Check if string is null or blank (empty/whitespace)
     */
    @JvmStatic
    fun isNullOrEmpty(str: String?): Boolean {
        return str.isNullOrBlank()
    }
    
    /**
     * Safe string comparison - handles nulls properly
     */
    @JvmStatic
    fun equals(str1: String?, str2: String?): Boolean {
        return str1 == str2
    }
    
    /**
     * Get string or default value if null/empty
     */
    @JvmStatic
    fun getOrDefault(str: String?, defaultValue: String): String {
        return if (str.isNullOrBlank()) defaultValue else str
    }
}

/**
 * Kotlin extension functions for more idiomatic usage
 */
fun String?.isNullOrEmpty(): Boolean = StringUtils.isNullOrEmpty(this)
fun String?.orDefault(defaultValue: String): String = StringUtils.getOrDefault(this, defaultValue)
fun String?.countChar(ch: Char): Int = StringUtils.countOccurrences(this, ch)