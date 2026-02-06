/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd;

/**
 * Represents a typed undefined value in LLSD.
 * <p>
 * In some LLSD contexts, particularly in the XML format, it is possible to have
 * a value that is explicitly "undefined" but still carries a type. For example,
 * {@code <real><undef /></real>} represents an undefined real number. This is
 * distinct from a null or zero value.
 * <p>
 * This enum is used to represent such typed undefined values during serialization
 * and parsing. In other formats like JSON or Notation, this concept typically
 * maps to a simple {@code null} or {@code !} value.
 *
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#undefined">LLSD Undefined Specification</a>
 */
public enum LLSDUndefined {
    /** An undefined boolean value. */
    BOOLEAN,
    /** An undefined binary value. */
    BINARY,
    /** An undefined date value. */
    DATE,
    /** An undefined integer value. */
    INTEGER,
    /** An undefined real value. */
    REAL,
    /** An undefined string value. */
    STRING,
    /** An undefined URI value. */
    URI,
    /** An undefined UUID value. */
    UUID
}
