package com.lumiyaviewer.lumiya.slproto.llsd

import java.io.DataOutputStream
import java.io.IOException
import java.net.URI
import java.util.Date
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

/**
 * Base class for all LLSD (Linden Lab Structured Data) types.
 * 
 * LLSD is a flexible data system similar to JSON, supporting:
 * - Scalar types: Boolean, Integer, Real (Double), String, UUID, Date, URI, Binary
 * - Container types: Array, Map
 * - Special: Undefined
 * 
 * Based on Second Life LLSD specification and C++ implementation.
 */
abstract class LLSDNode {

    /**
     * Convert to binary (byte array).
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asBinary(): ByteArray {
        throw LLSDValueTypeException("binary", this)
    }

    /**
     * Convert to boolean.
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asBoolean(): Boolean {
        throw LLSDValueTypeException("Boolean", this)
    }

    /**
     * Convert to Date.
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asDate(): Date {
        throw LLSDValueTypeException("date", this)
    }

    /**
     * Convert to Double (real number).
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asDouble(): Double {
        throw LLSDValueTypeException("real", this)
    }

    /**
     * Convert to Int (integer).
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asInt(): Int {
        throw LLSDValueTypeException("integer", this)
    }

    /**
     * Convert to Long.
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asLong(): Long {
        throw LLSDValueTypeException("Long", this)
    }

    /**
     * Convert to String.
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asString(): String {
        throw LLSDValueTypeException("string", this)
    }

    /**
     * Convert to URI.
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asURI(): URI {
        throw LLSDValueTypeException("uri", this)
    }

    /**
     * Convert to UUID.
     * Default implementation throws exception - override in subclasses that support this.
     */
    open fun asUUID(): UUID {
        throw LLSDValueTypeException("uuid", this)
    }

    /**
     * Access array element by index.
     * Default implementation throws exception - override in Array subclass.
     */
    @Throws(LLSDException::class)
    open fun byIndex(i: Int): LLSDNode {
        throw LLSDValueTypeException("array", this)
    }

    /**
     * Access map element by key.
     * Default implementation throws exception - override in Map subclass.
     */
    @Throws(LLSDException::class)
    open fun byKey(str: String): LLSDNode {
        throw LLSDValueTypeException("map", this)
    }

    /**
     * Get count of elements (for arrays).
     * Default implementation throws exception - override in Array subclass.
     */
    @Throws(LLSDException::class)
    open fun getCount(): Int {
        throw LLSDValueTypeException("array", this)
    }

    /**
     * Check if key exists (for maps).
     * Default implementation throws exception - override in Map subclass.
     */
    @Throws(LLSDException::class)
    open fun keyExists(str: String): Boolean {
        throw LLSDValueTypeException("map", this)
    }

    /**
     * Serialize this node to binary format.
     * Must be implemented by all subclasses.
     */
    @Throws(IOException::class)
    abstract fun toBinary(dataOutputStream: DataOutputStream)

    /**
     * Deserialize to a typed object using reflection.
     * Default implementation throws exception - override in Map subclass for object mapping.
     */
    @Throws(LLSDException::class)
    open fun <T> toObject(cls: Class<out T>): T {
        throw LLSDException("Cannot deserialize ${javaClass.name}")
    }

    /**
     * Serialize this node to XML format.
     * Must be implemented by all subclasses.
     */
    @Throws(IOException::class)
    abstract fun toXML(xmlSerializer: XmlSerializer)

    /**
     * Serialize to XML string.
     */
    @Throws(IOException::class)
    fun serializeToXML(): String {
        val xmlSerializer = android.util.Xml.newSerializer()
        val stringWriter = java.io.StringWriter()
        xmlSerializer.setOutput(stringWriter)
        xmlSerializer.startTag("", "llsd")
        toXML(xmlSerializer)
        xmlSerializer.endTag("", "llsd")
        xmlSerializer.endDocument()
        return stringWriter.toString()
    }

    /**
     * Serialize to XML output stream.
     */
    @Throws(IOException::class)
    fun serializeToXML(outputStream: java.io.OutputStream, encoding: String) {
        val xmlSerializer = android.util.Xml.newSerializer()
        xmlSerializer.setOutput(outputStream, encoding)
        xmlSerializer.startTag("", "llsd")
        toXML(xmlSerializer)
        xmlSerializer.endTag("", "llsd")
        xmlSerializer.endDocument()
    }
}
