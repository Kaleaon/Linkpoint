/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 * Updated 2024 based on Second Life viewer and LibreMetaverse implementations
 */

package lindenlab.llsd;

/**
 * Represents the different serialization formats available for LLSD.
 * <p>
 * This enum is used to specify which format to use when serializing or
 * parsing LLSD data. Each format has distinct characteristics regarding
 * readability, size, and performance.
 */
public enum LLSDFormat {
    /**
     * The original XML-based format. It is human-readable but can be verbose.
     * It is the most widely compatible format.
     * @see LLSDParser
     * @see LLSD
     */
    XML,
    /**
     * A compact, human-readable text format that resembles programming language
     * literals (e.g., {@code i42, s'hello', [1, 2, 3]}). It is less verbose
     * than XML but not as compact as binary.
     * @see LLSDNotationParser
     * @see LLSDNotationSerializer
     */
    NOTATION,
    /**
     * A highly efficient and compact binary format designed for high-performance
     * applications and network protocols. It is not human-readable.
     * @see LLSDBinaryParser
     * @see LLSDBinarySerializer
     */
    BINARY
}