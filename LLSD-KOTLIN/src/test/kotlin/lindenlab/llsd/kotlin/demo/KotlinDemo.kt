/*
 * Simple demo for Kotlin LLSD - for testing and demonstration
 */

package lindenlab.llsd.kotlin.demo

import lindenlab.llsd.kotlin.*
import lindenlab.llsd.kotlin.serialization.*

fun main() {
    println("=== Kotlin LLSD Demo ===")
    
    // Test simple array
    val array = llsdArray {
        +1
        +2.5
        +"hello"
        +true
    }
    
    println("Array size: ${array.size}")
    array.values.forEach { println("- $it") }
    
    // Test simple map
    val map = llsdMap {
        "name" to "John Doe"
        "age" to 30
        "active" to true
    }
    
    println("\nMap size: ${map.size}")
    map.values.forEach { (key, value) -> println("$key: $value") }
    
    // Test serialization
    try {
        val json = map.toJson(prettyPrint = false)
        println("\nJSON: $json")
    } catch (e: Exception) {
        println("Serialization failed: ${e.message}")
        e.printStackTrace()
    }
}