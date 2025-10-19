/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd;

/**
 * A base exception class for errors related to Second Life protocols or data structures.
 * <p>
 * This class serves as a common superclass for more specific exceptions, such as
 * {@link LLSDException}. It allows calling code to catch a single exception type
 * to handle a broad category of errors originating from this library.
 *
 * @see LLSDException
 */
public class SecondLifeException extends Exception {
    /**
     * Constructs a new {@code SecondLifeException} with the specified detail message.
     *
     * @param message The detail message, which is saved for later retrieval
     *                by the {@link #getMessage()} method.
     */
    public  SecondLifeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code SecondLifeException} with the specified detail message
     * and cause.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or unknown.
     */
    public  SecondLifeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
