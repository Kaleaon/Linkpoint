/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd

/**
 * An exception that indicates an error during the parsing or serialization of
 * LLSD (Linden Lab Structured Data).
 *
 * This exception is thrown when the LLSD data is malformed, contains invalid
 * or unexpected elements, or when a data type cannot be serialized. It extends
 * [SecondLifeException], providing a common base for exceptions related
 * to Second Life protocols.
 *
 * @see LLSDParser
 * @see LLSDJsonSerializer
 * @see LLSDBinaryParser
 * @see LLSDNotationParser
 */
class LLSDException : SecondLifeException {
    /**
     * Constructs a new [LLSDException] with the specified detail message.
     *
     * @param message The detail message, which is saved for later retrieval
     *                by the [message] property.
     */
    constructor(message: String) : super(message)

    /**
     * Constructs a new [LLSDException] with the specified detail message
     * and cause.
     *
     * Note that the detail message associated with [cause] is not
     * automatically incorporated into this exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the [message] property).
     * @param cause   The cause (which is saved for later retrieval by the
     *                [cause] property). A `null` value is
     *                permitted, and indicates that the cause is nonexistent or unknown.
     */
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
