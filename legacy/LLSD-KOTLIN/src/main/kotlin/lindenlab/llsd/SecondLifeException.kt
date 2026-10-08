/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd

/**
 * A base exception class for errors related to Second Life protocols or data structures.
 *
 * This class serves as a common superclass for more specific exceptions, such as
 * [LLSDException]. It allows calling code to catch a single exception type
 * to handle a broad category of errors originating from this library.
 *
 * @see LLSDException
 */
open class SecondLifeException : Exception {
    /**
     * Constructs a new [SecondLifeException] with the specified detail message.
     *
     * @param message The detail message, which is saved for later retrieval
     *                by the [message] property.
     */
    constructor(message: String) : super(message)

    /**
     * Constructs a new [SecondLifeException] with the specified detail message
     * and cause.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the [message] property).
     * @param cause   The cause (which is saved for later retrieval by the
     *                [cause] property). A `null` value is
     *                permitted, and indicates that the cause is nonexistent or unknown.
     */
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
