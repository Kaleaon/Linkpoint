# LLSD Java Library - Enhanced Version

This is an enhanced version of the LLSD Java library, updated with modern features from Second Life viewer and LibreMetaverse implementations.

## New Features

### Multiple Serialization Formats

The library now supports three LLSD serialization formats:

- **XML Format** - The original XML-based format
- **Notation Format** - A compact text-based format (similar to JSON)
- **Binary Format** - A compact binary format for efficient transmission

### Usage Examples

#### Basic Usage with Factory

```java
import lindenlab.llsd.*;
import java.util.*;

// Create LLSD data
Map<String, Object> data = new HashMap<>();
data.put("name", "Test Object");
data.put("count", 42);
data.put("active", true);

LLSD llsd = new LLSD(data);

// Serialize to different formats
String xml = LLSDSerializationFactory.serialize(llsd, LLSDFormat.XML);
String notation = LLSDSerializationFactory.serialize(llsd, LLSDFormat.NOTATION);
String binary = LLSDSerializationFactory.serialize(llsd, LLSDFormat.BINARY);

// Deserialize (auto-detect format)
LLSD parsed = LLSDSerializationFactory.deserialize(xml);
```

#### Format-Specific Serializers

```java
// Use specific serializers
LLSDNotationSerializer notationSerializer = new LLSDNotationSerializer();
String notationData = notationSerializer.serializeToString(llsd);
LLSD parsedNotation = notationSerializer.deserializeFromString(notationData);

LLSDBinarySerializer binarySerializer = new LLSDBinarySerializer();
// Binary serializer works with streams for efficiency
ByteArrayOutputStream baos = new ByteArrayOutputStream();
binarySerializer.serialize(llsd, baos);
byte[] binaryData = baos.toByteArray();
```

### LLSD Notation Format Examples

```java
// Boolean values
't' -> true
'f' -> false

// Numbers  
'i42' -> 42 (integer)
'r3.14159' -> 3.14159 (real)

// Strings
"'hello world'" -> "hello world"
'"quoted string"' -> "quoted string"

// Arrays
"[i1,i2,i3]" -> [1, 2, 3]

// Maps
"{'key':'value','count':i42}" -> {"key": "value", "count": 42}

// Complex nested structure
"{'users':[{'name':'Alice','age':i30},{'name':'Bob','age':i25}],'total':i2}"
```

### Vector and Color Types

The library now includes support for common Second Life data types:

```java
// Vector types (though not directly serializable yet, they're available for future use)
Vector2 pos2d = new Vector2(1.0f, 2.0f);
Vector3 pos3d = new Vector3(1.0f, 2.0f, 3.0f);  
Vector4 pos4d = new Vector4(1.0f, 2.0f, 3.0f, 4.0f);
Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
Color4 color = new Color4(1.0f, 0.0f, 0.0f, 1.0f); // Red
```

### Enhanced Type System

```java
// Type checking
LLSDType type = LLSDType.STRING;

// Format detection
LLSDFormat detectedFormat = LLSDSerializationFactory.detectFormat(data);
```

## Compatibility

This enhanced version maintains backward compatibility with existing code using the original LLSD and LLSDParser classes. The new serialization features are additive and don't break existing functionality.

### Migration Guide

Existing code will continue to work as before:

```java
// Existing code still works
LLSDParser parser = new LLSDParser();
LLSD llsd = parser.parse(inputStream);
String xmlOutput = llsd.toString();
```

To use new features, upgrade to the factory approach:

```java
// New approach with more formats
LLSD llsd = LLSDSerializationFactory.deserialize(inputStream);
String notationOutput = LLSDSerializationFactory.serialize(llsd, LLSDFormat.NOTATION);
```

## Performance Improvements

- **Streaming Support**: Binary and notation formats support streaming for large data
- **Auto-Detection**: Automatic format detection reduces guesswork
- **Memory Efficient**: Binary format reduces memory usage for large datasets
- **Base64 Handling**: Proper binary data encoding/decoding

## Error Handling

Enhanced error handling with specific exception information:

```java
try {
    LLSD result = LLSDSerializationFactory.deserialize(malformedData);
} catch (LLSDException e) {
    System.err.println("LLSD parsing failed: " + e.getMessage());
}
```

## Building

```bash
mvn compile  # Compile the library
mvn test     # Run tests
mvn package  # Create JAR file
```

## Dependencies

- Java 8 or higher
- JUnit 3.8.1 (for tests)

## License

Same as original - University of St. Andrews license, with updates noted as 2024 enhancements based on Second Life viewer and LibreMetaverse implementations.