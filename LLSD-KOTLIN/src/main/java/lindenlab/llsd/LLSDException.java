/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd;

/**
 * An exception that indicates an error during the parsing or serialization of
 * LLSD (Linden Lab Structured Data).
 * <p>
 * This exception is thrown when the LLSD data is malformed, contains invalid
 * or unexpected elements, or when a data type cannot be serialized. It extends
 * {@link SecondLifeException}, providing a common base for exceptions related
 * to Second Life protocols.
 *
 * @see LLSDParser
 * @see LLSDJsonSerializer
 * @see LLSDBinaryParser
 * @see LLSDNotationParser
 */
public class LLSDException extends SecondLifeException {

    /**
     * Constructs a new {@code LLSDException} with the specified detail message.
     *
     * @param message The detail message, which is saved for later retrieval
     *                by the {@link #getMessage()} method.
     */
    public  LLSDException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code LLSDException} with the specified detail message
     * and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is not
     * automatically incorporated into this exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or unknown.
     */
    public  LLSDException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
