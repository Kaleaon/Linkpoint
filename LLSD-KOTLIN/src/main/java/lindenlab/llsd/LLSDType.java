/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * An enumeration representing the fundamental data types supported by LLSD.
 * <p>
 * This enum provides a clear, type-safe way to identify the type of an LLSD
 * value, which is useful for type checking and dispatching logic.
 */
public enum LLSDType {
    /** The type is unknown or undefined. */
    UNKNOWN,
    /** A boolean value (true or false). */
    BOOLEAN,
    /** A 32-bit signed integer. */
    INTEGER,
    /** A 64-bit double-precision floating-point number. */
    REAL,
    /** A sequence of characters (a string). */
    STRING,
    /** A 128-bit Universally Unique Identifier. */
    UUID,
    /** An instant in time, typically represented in UTC. */
    DATE,
    /** A Uniform Resource Identifier. */
    URI,
    /** A block of arbitrary binary data. */
    BINARY,
    /** An unordered collection of key-value pairs, where keys are strings. */
    MAP,
    /** An ordered sequence of values. */
    ARRAY
}