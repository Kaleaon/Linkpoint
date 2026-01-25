package com.linkpoint.util

import com.linkpoint.network.EOFIOException

/**
 * Utility class for null safety operations in the Linkpoint app.
 * 
 * Provides common null-safe patterns and utilities that follow
 * Second Life naming conventions and idiomatic Kotlin practices.
 * 
 * Design Philosophy:
 * - Never throw NullPointerException - throw meaningful exceptions
 * - Provide clear error messages for debugging
 * - Use Kotlin's null-safety features effectively
 * - Follow functional programming principles where applicable
 * 
 * @see requireNotNull
 * @see checkNotNull
 * 
 * Usage Example:
 * ```kotlin
 * val response = NullSafetyUtil.getOrThrow(
 *     value = apiResponse,
 *     exception = EOFIOException("No response received")
 * )
 * ```
 * 
 * @author SuperNinja AI
 * @since 1.0
 */
object NullSafetyUtil {
    
    /**
     * Default error message template for null values.
     */
    private const val DEFAULT_NULL_ERROR_MESSAGE = "Required value cannot be null"
    
    /**
     * Returns the non-null value or throws a specified exception if null.
     * 
     * This is a type-safe alternative to the `!!` operator that allows
     * custom exception types and error messages.
     * 
     * Use Case: When you need to provide a specific exception type
     * for API contracts or error handling strategies.
     * 
     * @param value The nullable value to check
     * @param exception The exception to throw if value is null
     * @return The non-null value
     * @throws T if value is null
     * 
     * Example:
     * ```kotlin
     * val body = NullSafetyUtil.getOrThrow(
     *     response.body,
     *     EOFIOException("Server returned empty response")
     * )
     * ```
     */
    inline fun <reified T : Throwable> getOrThrow(
        value: Any?,
        exception: T
    ): Any {
        if (value == null) {
            throw exception
        }
        return value
    }
    
    /**
     * Returns the non-null value or throws an exception with a lazy message.
     * 
     * The message lambda is only evaluated if the value is null, making
     * this efficient for complex message construction.
     * 
     * Use Case: When you want to throw IllegalArgumentException with
     * a detailed error message that's expensive to construct.
     * 
     * @param value The nullable value to check
     * @param lazyMessage Lambda that generates the error message if value is null
     * @return The non-null value
     * @throws IllegalArgumentException if value is null
     * 
     * Example:
     * ```kotlin
     * val sessionId = NullSafetyUtil.getOrThrowLazy(
     *     session.id,
     *     { "Session ID is null for session: ${session.name}" }
     * )
     * ```
     */
    fun <T : Any> getOrThrowLazy(
        value: T?,
        lazyMessage: () -> String
    ): T {
        if (value == null) {
            throw IllegalArgumentException(lazyMessage())
        }
        return value
    }
    
    /**
     * Returns the non-null value or throws EOFIOException.
     * 
     * Specialized version for network operations where EOF exceptions
     * are the expected error type.
     * 
     * Use Case: HTTP response body handling, stream operations, and
     * other network I/O scenarios.
     * 
     * @param value The nullable value to check
     * @param message Error message if value is null
     * @return The non-null value
     * @throws EOFIOException if value is null
     * 
     * Example:
     * ```kotlin
     * val responseBody = NullSafetyUtil.getOrThrowEOF(
     *     response.body,
     *     "Server returned empty response body"
     * )
     * ```
     */
    fun <T : Any> getOrThrowEOF(
        value: T?,
        message: String = DEFAULT_NULL_ERROR_MESSAGE
    ): T {
        if (value == null) {
            throw EOFIOException(message)
        }
        return value
    }
    
    /**
     * Returns the non-null value or a default value if null.
     * 
     * This is a functional alternative to the Elvis operator that's
     * more explicit about the intent of providing a default.
     * 
     * Use Case: When you want to be explicit about default values
     * rather than using the elvis operator inline.
     * 
     * @param value The nullable value to check
     * @param defaultValue The default value to return if value is null
     * @return The non-null value or default value
     * 
     * Example:
     * ```kotlin
     * val regionName = NullSafetyUtil.getOrDefault(
     *     region.name,
     *     "Unknown Region"
     * )
     * ```
     */
    fun <T : Any> getOrDefault(
        value: T?,
        defaultValue: T
    ): T {
        return value ?: defaultValue
    }
    
    /**
     * Returns the non-null value or a default value from a lazy supplier.
     * 
     * The default supplier is only called if the value is null, making
     * this efficient for expensive default value construction.
     * 
     * Use Case: When the default value is expensive to compute
     * and should only be computed when needed.
     * 
     * @param value The nullable value to check
     * @param defaultValueSupplier Lambda that provides the default value
     * @return The non-null value or default value
     * 
     * Example:
     * ```kotlin
     * val avatarList = NullSafetyUtil.getOrDefaultLazy(
     *     scene.avatars,
     *     { loadDefaultAvatars() }
     * )
     * ```
     */
    fun <T : Any> getOrDefaultLazy(
        value: T?,
        defaultValueSupplier: () -> T
    ): T {
        return value ?: defaultValueSupplier()
    }
    
    /**
     * Executes an action if the value is non-null.
     * 
     * Functional alternative to if-null checks with side effects.
     * 
     * Use Case: When you want to perform an action only if a value
     * is present, without returning anything.
     * 
     * @param value The nullable value to check
     * @param action The action to perform if value is non-null
     * 
     * Example:
     * ```kotlin
     * NullSafetyUtil.ifNotNull(session) {
     *     startSession(it)
     * }
     * ```
     */
    inline fun <T : Any> ifNotNull(value: T?, action: (T) -> Unit) {
        value?.let(action)
    }
    
    /**
     * Executes an action if the value is null.
     * 
     * Useful for initialization or cleanup operations when a value
     * is expected to be null.
     * 
     * Use Case: When you need to initialize or clean up when a value
     * is absent.
     * 
     * @param value The nullable value to check
     * @param action The action to perform if value is null
     * 
     * Example:
     * ```kotlin
     * NullSafetyUtil.ifNull(session) {
     *     initializeDefaultSession()
     * }
     * ```
     */
    inline fun <T : Any> ifNull(value: T?, action: () -> Unit) {
        if (value == null) {
            action()
        }
    }
    
    /**
     * Transforms a nullable value using a mapper, returning null if input is null.
     * 
     * Functional alternative to safe call operator for complex transformations.
     * 
     * Use Case: When you need to transform a value but want to maintain
     * nullability through the transformation.
     * 
     * @param value The nullable value to transform
     * @param mapper The transformation function
     * @return The transformed value or null if input was null
     * 
     * Example:
     * ```kotlin
     * val regionName = NullSafetyUtil.mapOrNull(
     *     simulator.region,
     *     { it.name }
     * )
     * ```
     */
    inline fun <T : Any, R : Any> mapOrNull(
        value: T?,
        mapper: (T) -> R
    ): R? {
        return value?.let(mapper)
    }
    
    /**
     * Validates that all values in a collection are non-null.
     * 
     * Throws IllegalArgumentException if any value in the collection is null.
     * 
     * Use Case: When you need to validate that a collection has no null
     * elements, such as before passing to a non-null-typed API.
     * 
     * @param values The collection of nullable values to validate
     * @param collectionName Name of the collection for error message
     * @return The collection of non-null values
     * @throws IllegalArgumentException if any value is null
     * 
     * Example:
     * ```kotlin
     * val nonNullTextures = NullSafetyUtil.requireAllNonNull(
     *     textures,
     *     "texture list"
     * )
     * ```
     */
    fun <T : Any> requireAllNonNull(
        values: List<T?>,
        collectionName: String = "collection"
    ): List<T> {
        val nullIndex = values.indexOfFirst { it == null }
        if (nullIndex != -1) {
            throw IllegalArgumentException(
                "$collectionName contains null at index $nullIndex"
            )
        }
        @Suppress("UNCHECKED_CAST")
        return values as List<T>
    }
    
    /**
     * Returns the first non-null value from a sequence of values.
     * 
     * Useful for fallback chains or priority-based value selection.
     * 
     * Use Case: When you have multiple possible sources for a value
     * and want to use the first one that's available.
     * 
     * @param values Variable number of nullable values to check
     * @return The first non-null value, or null if all are null
     * 
     * Example:
     * ```kotlin
     * val regionName = NullSafetyUtil.coalesce(
     *     customRegionName,
     *     region.displayName,
     *     region.name,
     *     "Unknown"
     * )
     * ```
     */
    fun <T : Any> coalesce(vararg values: T?): T? {
        return values.firstOrNull { it != null }
    }
    
    /**
     * Converts a nullable value to an Optional-like wrapper for functional operations.
     * 
     * This provides a more functional approach to null handling when you
     * need to chain multiple operations.
     * 
     * Use Case: When you want to perform multiple operations on a nullable
     * value in a functional style.
     * 
     * @param value The nullable value to wrap
     * @return An Optional wrapper that can be used for functional operations
     * 
     * Example:
     * ```kotlin
     * val result = NullSafetyUtil.optional(session)
     *     .map { it.agentId }
     *     .map { it.toString() }
     *     .orElse("unknown")
     * ```
     */
    fun <T : Any> optional(value: T?): Optional<T> {
        return Optional(value)
    }
    
    /**
     * Optional wrapper class for functional null handling.
     * 
     * Provides methods for functional composition and transformation
     * of nullable values without explicit null checks.
     * 
     * @param T The type of the wrapped value
     * @property value The nullable value being wrapped
     */
    class Optional<T : Any>(@PublishedApi internal val value: T?) {
        
        /**
         * Returns true if the wrapped value is non-null.
         */
        fun isPresent(): Boolean = value != null
        
        /**
         * Returns true if the wrapped value is null.
         */
        fun isEmpty(): Boolean = value == null
        
        /**
         * Returns the wrapped value if non-null, or the default value otherwise.
         * 
         * @param defaultValue The default value to return if wrapped value is null
         * @return The wrapped value or default value
         */
        fun orElse(defaultValue: T): T {
            return value ?: defaultValue
        }
        
        /**
         * Returns the wrapped value if non-null, or null otherwise.
         * 
         * Useful when you need to maintain nullability.
         * 
         * @return The wrapped value or null
         */
        fun orNull(): T? {
            return value
        }
        
        /**
         * Transforms the wrapped value using the mapper, if non-null.
         * 
         * @param mapper The transformation function
         * @return An Optional wrapping the transformed value, or empty Optional if input was null
         */
        inline fun <R : Any> map(mapper: (T) -> R): Optional<R> {
            return Optional(value?.let(mapper))
        }
        
        /**
         * Transforms the wrapped value using the mapper that returns an Optional.
         * 
         * Useful for chaining transformations that may fail.
         * 
         * @param mapper The transformation function that returns an Optional
         * @return The result of the mapper, or empty Optional if input was null
         */
        inline fun <R : Any> flatMap(mapper: (T) -> Optional<R>): Optional<R> {
            return value?.let(mapper) ?: Optional(null)
        }
        
        /**
         * Executes an action if the wrapped value is non-null.
         * 
         * @param action The action to perform if value is present
         */
        inline fun ifPresent(action: (T) -> Unit) {
            value?.let(action)
        }
        
        /**
         * Executes an action if the wrapped value is null.
         * 
         * @param action The action to perform if value is absent
         */
        inline fun ifEmpty(action: () -> Unit) {
            if (value == null) {
                action()
            }
        }
        
        /**
         * Returns the wrapped value or throws an exception if null.
         * 
         * @param exception The exception to throw if value is null
         * @return The non-null wrapped value
         * @throws T if wrapped value is null
         */
        inline fun <reified E : Throwable> orThrow(exception: E): T {
            return value ?: throw exception
        }
    }
}

/**
 * Extension function to convert any nullable value to Optional.
 * 
 * Provides a more convenient way to use the Optional wrapper.
 * 
 * Usage Example:
 * ```kotlin
 * val result = session?.agentId
 *     .toOptional()
 *     .map { it.toString() }
 *     .orElse("unknown")
 * ```
 * 
 * @receiver The nullable value to wrap
 * @return An Optional wrapping the value
 */
fun <T : Any> T?.toOptional(): NullSafetyUtil.Optional<T> {
    return NullSafetyUtil.Optional(this)
}