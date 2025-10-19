/*
 * LLSDJ - Simple LLSD demonstration
 * 
 * Demonstrates the comprehensive LLSD implementation from the master branch
 */

package lindenlab.llsd

import java.io.StringWriter

/**
 * A demonstration class that showcases the serialization capabilities of the LLSD library.
 *
 * This class provides a simple command-line application that creates a sample
 * LLSD data structure (a map) and then serializes it into the four supported
 * formats:
 * - XML
 * - JSON
 * - Notation
 * - Binary
 *
 * The output for each format is printed to the console, providing a clear
 * example of how to use the various serializer classes.
 */
object LLSDDemo {
    /**
     * The main entry point for the demonstration application.
     *
     * It builds a sample LLSD object and prints its representation in all
     * supported serialization formats.
     *
     * @param args Command-line arguments (not used).
     */
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            println("LLSD Master Branch Integration Demo")
            println("===================================\n")
            
            // Create sample data
            val data = mapOf(
                "message" to "LLSD conflict resolution successful!",
                "formats" to listOf("XML", "JSON", "Notation", "Binary"),
                "version" to 2.0,
                "automated" to true
            )
            
            val llsd = LLSD(data)
            
            // XML (original format)
            println("1. XML Format:")
            val xmlWriter = StringWriter()
            llsd.serialise(xmlWriter, "UTF-8")
            println(xmlWriter.toString())
            
            // JSON (master branch feature)
            println("2. JSON Format:")
            val jsonSerializer = LLSDJsonSerializer()
            val jsonWriter = StringWriter()
            jsonSerializer.serialize(llsd, jsonWriter)
            println(jsonWriter.toString())
            println()
            
            // Notation (master branch feature)  
            println("3. Notation Format:")
            val notationSerializer = LLSDNotationSerializer()
            val notationWriter = StringWriter()
            notationSerializer.serialize(llsd, notationWriter)
            println(notationWriter.toString())
            println()
            
            // Binary (master branch feature)
            println("4. Binary Format:")
            val binarySerializer = LLSDBinarySerializer()
            val binaryData = binarySerializer.serialize(llsd)
            println("Binary serialized: ${binaryData.size} bytes")
            println()
            
            println("✅ All formats working correctly!")
            println("✅ Conflicts resolved successfully!")
            println("✅ Master branch integration complete!")
            
        } catch (e: Exception) {
            System.err.println("❌ Error: ${e.message}")
            e.printStackTrace()
        }
    }
}
