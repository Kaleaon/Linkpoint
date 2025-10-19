/*
 * LLSD Kotlin Serialization - Modern serialization with type safety
 *
 * Copyright (C) 2024
 */

package lindenlab.llsd.kotlin.serialization

import lindenlab.llsd.kotlin.*
import lindenlab.llsd.*
import java.io.*
import java.net.URI
import java.time.Instant
import java.util.*

/**
 * Kotlin-native LLSD serialization with type safety.
 * 
 * <p>This class provides modern Kotlin serialization capabilities including
 * streaming and type-safe serialization.</p>
 * 
 * @since 1.0
 */
class LLSDKotlinSerializer {
    
    /**
     * Serialization formats
     */
    enum class Format {
        JSON,
        NOTATION,
        XML,
        BINARY
    }
    
    /**
     * Serialization options
     */
    data class SerializationOptions(
        val format: Format = Format.JSON,
        val prettyPrint: Boolean = true,
        val indent: String = "  ",
        val bufferSize: Int = 8192,
        val charset: java.nio.charset.Charset = Charsets.UTF_8
    )
    
    /**
     * Serialize LLSD value to string
     */
    fun serialize(
        value: LLSDValue, 
        options: SerializationOptions = SerializationOptions()
    ): String {
        val javaLLSD = value.toJavaLLSD()
        
        return when (options.format) {
            Format.JSON -> {
                val serializer = LLSDJsonSerializer()
                val writer = StringWriter()
                serializer.serialize(javaLLSD, writer)
                writer.toString()
            }
            Format.NOTATION -> {
                val serializer = LLSDNotationSerializer()
                val writer = StringWriter()
                serializer.serialize(javaLLSD, writer)
                writer.toString()
            }
            Format.XML -> javaLLSD.toString()
            Format.BINARY -> {
                val serializer = LLSDBinarySerializer()
                val baos = ByteArrayOutputStream()
                serializer.serialize(javaLLSD, baos)
                Base64.getEncoder().encodeToString(baos.toByteArray())
            }
        }
    }
    
    /**
     * Serialize LLSD value to OutputStream
     */
    fun serialize(
        value: LLSDValue,
        output: OutputStream,
        options: SerializationOptions = SerializationOptions()
    ) {
        when (options.format) {
            Format.BINARY -> {
                val serializer = LLSDBinarySerializer()
                val javaLLSD = value.toJavaLLSD()
                serializer.serialize(javaLLSD, output)
            }
            else -> {
                val serialized = serialize(value, options)
                output.write(serialized.toByteArray(options.charset))
            }
        }
    }
    
    /**
     * Parse LLSD from string
     */
    fun parse(
        input: String,
        format: Format = Format.JSON
    ): LLSDValue {
        val javaLLSD = when (format) {
            Format.JSON -> {
                val parser = LLSDJsonParser()
                parser.parse(ByteArrayInputStream(input.toByteArray()))
            }
            Format.NOTATION -> {
                val parser = LLSDNotationParser()
                parser.parse(ByteArrayInputStream(input.toByteArray()))
            }
            Format.XML -> {
                val parser = LLSDParser()
                parser.parse(ByteArrayInputStream(input.toByteArray()))
            }
            Format.BINARY -> {
                val parser = LLSDBinaryParser()
                val data = Base64.getDecoder().decode(input)
                parser.parse(ByteArrayInputStream(data))
            }
        }
        
        return javaLLSD.toKotlinLLSD()
    }
    
    /**
     * Parse LLSD from InputStream
     */
    fun parse(
        input: InputStream,
        format: Format = Format.JSON
    ): LLSDValue {
        val javaLLSD = when (format) {
            Format.JSON -> {
                val parser = LLSDJsonParser()
                parser.parse(input)
            }
            Format.NOTATION -> {
                val parser = LLSDNotationParser()
                parser.parse(input)
            }
            Format.XML -> {
                val parser = LLSDParser()
                parser.parse(input)
            }
            Format.BINARY -> {
                val parser = LLSDBinaryParser()
                parser.parse(input)
            }
        }
        
        return javaLLSD.toKotlinLLSD()
    }
    
    /**
     * Auto-detect format from content
     */
    fun detectFormat(content: String): Format {
        return when {
            content.trim().startsWith('{') || content.trim().startsWith('[') -> Format.JSON
            content.trim().startsWith('<') -> Format.XML
            content.contains('\'') || content.contains('i') || content.contains('r') -> Format.NOTATION
            else -> Format.BINARY
        }
    }
}

/**
 * Extension functions for easier serialization
 */

/**
 * Serialize to JSON string
 */
fun LLSDValue.toJson(prettyPrint: Boolean = true): String {
    val serializer = LLSDKotlinSerializer()
    return serializer.serialize(this, LLSDKotlinSerializer.SerializationOptions(
        format = LLSDKotlinSerializer.Format.JSON,
        prettyPrint = prettyPrint
    ))
}

/**
 * Serialize to Notation string
 */
fun LLSDValue.toNotation(): String {
    val serializer = LLSDKotlinSerializer()
    return serializer.serialize(this, LLSDKotlinSerializer.SerializationOptions(
        format = LLSDKotlinSerializer.Format.NOTATION
    ))
}

/**
 * Serialize to XML string
 */
fun LLSDValue.toXml(): String {
    val serializer = LLSDKotlinSerializer()
    return serializer.serialize(this, LLSDKotlinSerializer.SerializationOptions(
        format = LLSDKotlinSerializer.Format.XML
    ))
}

/**
 * Serialize to Binary (Base64 encoded)
 */
fun LLSDValue.toBinary(): String {
    val serializer = LLSDKotlinSerializer()
    return serializer.serialize(this, LLSDKotlinSerializer.SerializationOptions(
        format = LLSDKotlinSerializer.Format.BINARY
    ))
}

/**
 * Parse LLSD from JSON string
 */
fun String.parseLLSDFromJson(): LLSDValue {
    val serializer = LLSDKotlinSerializer()
    return serializer.parse(this, LLSDKotlinSerializer.Format.JSON)
}

/**
 * Parse LLSD from Notation string
 */
fun String.parseLLSDFromNotation(): LLSDValue {
    val serializer = LLSDKotlinSerializer()
    return serializer.parse(this, LLSDKotlinSerializer.Format.NOTATION)
}

/**
 * Parse LLSD from XML string
 */
fun String.parseLLSDFromXml(): LLSDValue {
    val serializer = LLSDKotlinSerializer()
    return serializer.parse(this, LLSDKotlinSerializer.Format.XML)
}

/**
 * Parse LLSD from Binary (Base64 encoded)
 */
fun String.parseLLSDFromBinary(): LLSDValue {
    val serializer = LLSDKotlinSerializer()
    return serializer.parse(this, LLSDKotlinSerializer.Format.BINARY)
}

/**
 * Auto-detect format and parse
 */
fun String.parseLLSD(): LLSDValue {
    val serializer = LLSDKotlinSerializer()
    val format = serializer.detectFormat(this)
    return serializer.parse(this, format)
}

/**
 * Type-safe LLSD builders with validation
 */

/**
 * Build validated LLSD with schema
 */
inline fun <reified T> buildValidatedLLSD(
    schema: LLSDSchema<T>,
    builder: LLSDMapBuilder.() -> Unit
): Result<LLSDValue.Map> = runCatching {
    val mapBuilder = LLSDMapBuilder()
    mapBuilder.builder()
    val result = mapBuilder.build()
    
    // Validate against schema
    schema.validate(result)
    result
}

/**
 * Simple schema validation
 */
interface LLSDSchema<T> {
    fun validate(value: LLSDValue.Map): T
}

/**
 * Example schema implementation
 */
class PersonSchema : LLSDSchema<Person> {
    override fun validate(value: LLSDValue.Map): Person {
        val name = value["name"].asString()
        val age = value["age"].asInt()
        val email = value["email"].asString()
        
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(age >= 0) { "Age must be non-negative" }
        require(email.contains("@")) { "Email must contain @" }
        
        return Person(name, age, email)
    }
}

data class Person(val name: String, val age: Int, val email: String)

// Extension to convert back to LLSD
fun Person.toLLSD(): LLSDValue.Map = LLSDValue.Map().apply {
    set("name", LLSDValue.String(name))
    set("age", LLSDValue.Integer(age))
    set("email", LLSDValue.String(email))
}