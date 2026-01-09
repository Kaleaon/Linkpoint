# LLSD (Linden Lab Structured Data) Implementation

## Overview

The LLSD module provides comprehensive support for Linden Lab Structured Data format, which is the primary data serialization format used by Second Life protocols. This implementation includes both traditional Java classes and modern Kotlin DSL support for type-safe data manipulation.

## What is LLSD?

LLSD (Linden Lab Structured Data) is a data serialization format designed for Second Life's virtual world protocols. It supports multiple encoding formats and provides a rich type system for representing complex data structures used in virtual world communications.

### Supported Data Types

| Type | Description | Example |
|------|-------------|---------|
| `undefined` | Null/undefined value | `null` |
| `boolean` | Boolean value | `true`, `false` |
| `integer` | 32-bit signed integer | `42`, `-123` |
| `real` | Double-precision float | `3.14159`, `-2.5` |
| `string` | UTF-8 text string | `"Hello, World!"` |
| `uuid` | Universally unique identifier | `"550e8400-e29b-41d4-a716-446655440000"` |
| `uri` | Uniform resource identifier | `"http://example.com/resource"` |
| `date` | ISO 8601 timestamp | `"2023-12-25T10:30:00Z"` |
| `binary` | Base64-encoded binary data | `"SGVsbG8gV29ybGQ="` |
| `array` | Ordered list of values | `[1, 2, 3]` |
| `map` | Key-value dictionary | `{"name": "Avatar", "level": 5}` |

## Architecture

### Core Components

```
llsd/
├── types/              # Core LLSD data type implementations
│   ├── LLSDBoolean.java
│   ├── LLSDInt.java
│   ├── LLSDString.java
│   ├── LLSDArray.java
│   ├── LLSDMap.java
│   └── ... (other types)
├── integration/        # Modern integration layer
│   ├── LLSDIntegrationBridge.java
│   ├── LLSDIntegrationDemo.java
│   ├── KALEAON_KOTLIN_ANALYSIS.md
│   └── KALEAON_LLSD_MIGRATION_PLAN.md
└── kotlin/            # Modern Kotlin DSL implementation
    ├── KotlinLLSDValue.kt
    └── KotlinLLSDDemo.kt
```

### Integration Strategy

The LLSD module follows a three-tier architecture:

1. **Legacy Layer**: Traditional Java LLSD classes (compatibility)
2. **Bridge Layer**: Integration bridge for seamless interoperability
3. **Modern Layer**: Kotlin DSL with type safety and modern patterns

## Traditional Java API

### Basic Usage

```java
// Creating LLSD data structures
LLSDMap agentData = new LLSDMap();
agentData.put("agent_id", new LLSDUUID(UUID.randomUUID()));
agentData.put("name", new LLSDString("Avatar Name"));
agentData.put("level", new LLSDInt(25));

LLSDArray position = new LLSDArray();
position.add(new LLSDDouble(128.0));
position.add(new LLSDDouble(128.0));
position.add(new LLSDDouble(23.0));
agentData.put("position", position);

// Nested structures
LLSDMap status = new LLSDMap();
status.put("online", new LLSDBoolean(true));
status.put("typing", new LLSDBoolean(false));
agentData.put("status", status);
```

### Serialization

```java
// XML serialization (most common)
String xml = agentData.toXML();

// Binary serialization (compact)
byte[] binary = agentData.toBinary();

// Notation format (human-readable)
String notation = agentData.toNotation();
```

## Modern Kotlin DSL

### Enhanced Type Safety

The Kotlin DSL provides compile-time type safety and elegant syntax inspired by @Kaleaon's enhanced LLSD library:

```kotlin
val agentData = kotlinLlsdMap {
    "agent_id" to UUID.randomUUID()
    "name" to "Avatar Name"
    "level" to 25
    
    "position" to kotlinLlsdArray {
        +128.0  // x coordinate
        +128.0  // y coordinate  
        +23.0   // z coordinate
    }
    
    "status" to kotlinLlsdMap {
        "online" to true
        "typing" to false
        "away" to false
    }
    
    "equipment" to kotlinLlsdArray {
        // Equipment items would go here
    }
    
    "metadata" to kotlinLlsdMap {
        "created" to Date()
        "version" to "linkpoint-3.4.3"
        "client" to "Linkpoint Android"
    }
}
```

### Safe Data Access

```kotlin
// Safe value extraction with defaults
val agentId = agentData["agent_id"].asUUID()
val agentName = agentData["name"].asString("Unknown")
val isOnline = agentData["status"]["online"].asBoolean(false)
val level = agentData["level"].asInt(1)

// Safe array access
val positionArray = agentData["position"].asArray()
val xPos = if (positionArray.isNotEmpty()) positionArray[0].asDouble(0.0) else 0.0

// Type checking
when (val statusValue = agentData["status"]) {
    is KotlinLLSDValue.Map -> {
        println("Status has ${statusValue.size} properties")
    }
    is KotlinLLSDValue.Undefined -> {
        println("Status not available")
    }
    else -> {
        println("Unexpected status type")
    }
}
```

## Integration Bridge

The integration bridge provides seamless interoperability between Java and Kotlin implementations:

### Bridge Features

```java
public class LLSDIntegrationBridge {
    // Get current integration status
    public static String getIntegrationStatus() {
        return "LLSD Integration Status:\n" +
               "- Current: jacobilinden/llsd-java (base functionality)\n" + 
               "- Enhanced: @Kaleaon's Kotlin LLSD features (Android-compatible)\n" +
               "- Features available: Kotlin DSL, type safety, sealed classes\n" +
               "- Status: Ready for production use";
    }
    
    // Demonstrate Kotlin features from Java code
    public static void demonstrateKotlinFeatures() {
        Log.i(TAG, "Kotlin LLSD features now available!");
        Log.i(TAG, "Enhanced capabilities: Type-safe DSL, sealed classes, extension functions");
        
        // Example of Kotlin DSL usage:
        // val data = kotlinLlsdMap {
        //     "name" to "Agent"
        //     "position" to kotlinLlsdArray { +128.0; +128.0; +23.0 }
        //     "active" to true
        // }
    }
}
```

### Conversion Between Formats

```java
// Parse traditional LLSD from XML
LLSDNode traditionalLlsd = LLSDParser.parseXML(xmlString);

// Convert to Kotlin LLSD for modern processing
KotlinLLSDValue modernLlsd = traditionalLlsd.toKotlinLLSD();

// Process with type safety
if (modernLlsd instanceof KotlinLLSDValue.Map) {
    KotlinLLSDValue.Map map = (KotlinLLSDValue.Map) modernLlsd;
    String name = map.get("name").asString("Unknown");
}

// Convert back to traditional format for legacy code
LLSDNode backToTraditional = modernLlsd.toLinkpointLLSD();
```

## Performance Optimizations

### Memory Efficiency

```kotlin
// Efficient LLSD creation with pre-sized collections
val largeDataSet = kotlinLlsdMap {
    // Pre-allocate capacity for better performance
    "users" to kotlinLlsdArray {
        repeat(1000) { index ->
            +kotlinLlsdMap {
                "id" to index
                "name" to "User$index"
                "active" to (index % 2 == 0)
            }
        }
    }
}
```

### Caching and Reuse

```java
public class LLSDCache {
    private final LRUCache<String, LLSDNode> parseCache = new LRUCache<>(100);
    
    public LLSDNode parseWithCache(String xml) {
        String key = DigestUtils.md5Hex(xml);
        LLSDNode cached = parseCache.get(key);
        if (cached != null) {
            return cached;
        }
        
        LLSDNode parsed = LLSDParser.parseXML(xml);
        parseCache.put(key, parsed);
        return parsed;
    }
}
```

## Advanced Features

### Custom Serializers

```kotlin
// Custom serialization for specific types
class AvatarDataSerializer {
    fun serialize(avatar: Avatar): KotlinLLSDValue.Map {
        return kotlinLlsdMap {
            "basic_info" to kotlinLlsdMap {
                "name" to avatar.displayName
                "uuid" to avatar.id
                "legacy_name" to avatar.legacyName
            }
            
            "appearance" to kotlinLlsdMap {
                "height" to avatar.height
                "skin_color" to avatar.skinColor.toHex()
                "attachments" to kotlinLlsdArray {
                    avatar.attachments.forEach { attachment ->
                        +kotlinLlsdMap {
                            "id" to attachment.assetId
                            "point" to attachment.attachmentPoint
                        }
                    }
                }
            }
            
            "location" to kotlinLlsdMap {
                "region" to avatar.currentRegion
                "position" to kotlinLlsdArray {
                    +avatar.position.x
                    +avatar.position.y
                    +avatar.position.z
                }
                "rotation" to kotlinLlsdArray {
                    +avatar.rotation.x
                    +avatar.rotation.y
                    +avatar.rotation.z
                    +avatar.rotation.w
                }
            }
        }
    }
}
```

### Validation and Schema

```kotlin
// LLSD data validation
class LLSDValidator {
    fun validateAgentData(data: KotlinLLSDValue.Map): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Check required fields
        if (data["agent_id"].asUUID() == null) {
            errors.add("Missing or invalid agent_id")
        }
        
        if (data["name"].asString().isEmpty()) {
            errors.add("Agent name cannot be empty")
        }
        
        // Validate position array
        val position = data["position"].asArray()
        if (position.size != 3) {
            errors.add("Position must have exactly 3 coordinates")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}
```

## Real-World Usage Examples

### Chat Message Processing

```kotlin
// Incoming chat message from Second Life
fun processChatMessage(llsdData: KotlinLLSDValue.Map) {
    val messageData = ChatMessage(
        text = llsdData["message"].asString(),
        fromName = llsdData["from_name"].asString("Unknown"),
        fromId = llsdData["from_id"].asUUID(),
        timestamp = llsdData["timestamp"].asString(),
        channel = llsdData["channel"].asInt(0),
        type = ChatType.fromInt(llsdData["type"].asInt(0))
    )
    
    chatManager.displayMessage(messageData)
}

// Outgoing chat message to Second Life
fun sendChatMessage(text: String, channel: Int): KotlinLLSDValue.Map {
    return kotlinLlsdMap {
        "message" to text
        "channel" to channel
        "type" to ChatType.NORMAL.value
        "from_id" to currentUser.agentId
        "session_id" to currentSession.id
        "timestamp" to Date().time
    }
}
```

### Inventory Management

```kotlin
// Process inventory folder data
fun processInventoryFolder(folderData: KotlinLLSDValue.Map): InventoryFolder {
    return InventoryFolder(
        id = folderData["folder_id"].asUUID()!!,
        name = folderData["name"].asString("Untitled"),
        parentId = folderData["parent_id"].asUUID(),
        type = FolderType.fromInt(folderData["type_default"].asInt(-1)),
        items = folderData["items"].asArray().map { item ->
            processInventoryItem(item as KotlinLLSDValue.Map)
        }
    )
}

// Create inventory update request
fun createInventoryUpdate(item: InventoryItem): KotlinLLSDValue.Map {
    return kotlinLlsdMap {
        "item_id" to item.id
        "parent_id" to item.parentFolder
        "name" to item.name
        "description" to item.description
        "asset_id" to item.assetId
        "type" to item.type.value
        "inventory_type" to item.inventoryType.value
        "permissions" to kotlinLlsdMap {
            "owner" to item.permissions.owner
            "group" to item.permissions.group
            "everyone" to item.permissions.everyone
            "next_owner" to item.permissions.nextOwner
        }
    }
}
```

## Testing

### Unit Testing

```kotlin
class KotlinLLSDTest {
    @Test
    fun testBasicDataTypes() {
        val data = kotlinLlsdMap {
            "string" to "test"
            "integer" to 42
            "boolean" to true
            "real" to 3.14159
        }
        
        assertEquals("test", data["string"].asString())
        assertEquals(42, data["integer"].asInt())
        assertEquals(true, data["boolean"].asBoolean())
        assertEquals(3.14159, data["real"].asDouble(), 0.00001)
    }
    
    @Test
    fun testNestedStructures() {
        val data = kotlinLlsdMap {
            "nested" to kotlinLlsdMap {
                "array" to kotlinLlsdArray {
                    +1; +2; +3
                }
            }
        }
        
        val nestedArray = data["nested"]["array"].asArray()
        assertEquals(3, nestedArray.size)
        assertEquals(2, nestedArray[1].asInt())
    }
}
```

### Performance Testing

```kotlin
class LLSDPerformanceTest {
    @Test
    fun testLargeDataStructureCreation() {
        val startTime = System.currentTimeMillis()
        
        val largeData = kotlinLlsdMap {
            repeat(1000) { i ->
                "item$i" to kotlinLlsdMap {
                    "id" to i
                    "name" to "Item $i"
                    "data" to kotlinLlsdArray {
                        repeat(10) { j -> +j }
                    }
                }
            }
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        assertTrue("Large structure creation took too long: ${duration}ms", duration < 1000)
        assertEquals(1000, largeData.size)
    }
}
```

## Configuration

### LLSD Settings
```xml
<resources>
    <!-- Enable modern Kotlin LLSD features -->
    <bool name="enable_kotlin_llsd">true</bool>
    <bool name="enable_llsd_caching">true</bool>
    <bool name="enable_llsd_validation">true</bool>
    
    <!-- Performance tuning -->
    <integer name="llsd_cache_size">100</integer>
    <integer name="max_llsd_depth">10</integer>
    <integer name="max_llsd_array_size">10000</integer>
</resources>
```

### Feature Flags
```java
public class LLSDConfig {
    public static final boolean ENABLE_KOTLIN_DSL = BuildConfig.DEBUG || 
        getBoolean("enable_kotlin_llsd", true);
    
    public static final boolean ENABLE_VALIDATION = 
        getBoolean("enable_llsd_validation", false);
        
    public static final int CACHE_SIZE = 
        getInt("llsd_cache_size", 100);
}
```

## Migration Guide

### From Traditional LLSD to Kotlin DSL

1. **Gradual Adoption**: Start using Kotlin DSL for new code
2. **Bridge Integration**: Use compatibility layer for existing code
3. **Type Safety**: Leverage compile-time type checking
4. **Performance**: Monitor memory usage and performance impact

### Best Practices

- Use Kotlin DSL for new development
- Maintain Java compatibility for existing code
- Validate data structures in debug builds
- Cache frequently used LLSD structures
- Monitor memory usage with large data sets

## Future Enhancements

### Planned Features
- **JSON Support**: Native JSON serialization/deserialization
- **Binary Format**: Optimized binary encoding for mobile
- **Schema Validation**: Runtime schema validation
- **Streaming Parser**: Memory-efficient parsing for large data

### Integration Roadmap
- **Full @Kaleaon Library Integration**: Complete migration to enhanced features
- **Advanced Serialization**: Custom serializers for complex types
- **Performance Optimization**: Zero-copy parsing and serialization
- **Extended Validation**: Schema-based validation system

## Related Documentation

- [@Kaleaon's Kotlin Analysis](integration/KALEAON_KOTLIN_ANALYSIS.md)
- [LLSD Migration Plan](integration/KALEAON_LLSD_MIGRATION_PLAN.md)
- [Second Life Protocol Integration](../README.md)
- [Modern Components Overview](../../modern/README.md)