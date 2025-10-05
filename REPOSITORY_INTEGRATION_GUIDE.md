# Repository Integration Guide

## Overview
This guide outlines how to integrate code and knowledge from the mentioned external repositories into the modernized Lumiya Viewer / Linkpoint project.

## Available Repositories

### 1. Second Life Repository
**Purpose**: Official Second Life client source code  
**Key Integration Areas**:

#### Protocol Specifications
- **Location**: `indra/llmessage/` - Network message definitions
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/slproto/`
- **Value**: Authoritative protocol message structures

#### Asset Pipeline
- **Location**: `indra/newview/llviewerassetupload.*` - Asset upload handling
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/modern/assets/`
- **Value**: Official asset format specifications and upload logic

#### Inventory System
- **Location**: `indra/newview/llinventory*` - Inventory management
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/modern/features/ModernInventoryManager.java`
- **Value**: Standard SL inventory structure and operations

#### Capabilities (CAPS)
- **Location**: `indra/llmessage/llcapabilitylistener.*` - HTTP capabilities
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/modern/protocol/HTTP2CapsClient.java`
- **Value**: Official CAPS endpoint specifications

**Integration Steps**:
```bash
# 1. Clone Second Life repo
git clone https://github.com/secondlife/viewer secondlife-viewer

# 2. Extract protocol definitions
# Convert C++ message templates to Java equivalents
# Example: indra/llmessage/message_prehash.cpp → Java enums

# 3. Port asset format handlers
# C++: LLViewerAssetUpload → Java: ModernAssetManager

# 4. Sync inventory folder types
# C++: LLFolderType → Java: ModernInventoryManager.InventoryType
```

---

### 2. Firestorm Repository
**Purpose**: Advanced Second Life viewer with enhanced features  
**Key Integration Areas**:

#### Advanced Inventory Management
- **Location**: `indra/newview/llinventorybridge.*` - Enhanced inventory UI
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/modern/features/ModernInventoryManager.java`
- **Value**: Advanced filtering, search, and batch operations

#### RLV (Restrained Love Viewer) Integration
- **Location**: `indra/newview/rlvhandler.*` - RLV command handling
- **Integration Point**: Create new `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/rlv/`
- **Value**: Full RLV protocol support for role-play applications

#### UI Enhancements
- **Location**: `indra/newview/fsfloater*` - Firestorm-specific UI components
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/ui/modern/`
- **Value**: Advanced UI patterns and user experience improvements

#### Performance Optimizations
- **Location**: `indra/newview/llmeshrepository.*` - Mesh loading optimizations
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/modern/assets/`
- **Value**: Efficient asset caching and loading strategies

**Integration Steps**:
```bash
# 1. Clone Firestorm repo
git clone https://github.com/FirestormViewer/phoenix-firestorm firestorm-viewer

# 2. Extract RLV implementation
# Port RLVHandler to Java
cp firestorm/indra/newview/rlv* analysis/
# Convert to Java: RLVHandler.cpp → RLVManager.java

# 3. Port advanced inventory features
# Example: Inventory favorites, recent items, worn items tracking

# 4. Integrate performance improvements
# Port mesh streaming optimizations
```

**Specific Features to Port**:
- **Inventory Favorites**: Quick access bookmarks
- **Outfit Management**: Enhanced outfit system
- **Asset Blacklist**: Performance optimization feature
- **Advanced Teleport**: Teleport history and favorites
- **Contact Sets**: Enhanced friends/contacts management

---

### 3. Restrained Love Repository
**Purpose**: RLV protocol implementation for role-play restrictions  
**Key Integration Areas**:

#### RLV Protocol
- **Location**: Core RLV command parser and handler
- **Integration Point**: Create `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/rlv/`
- **Value**: Complete RLV 3.x protocol support

#### Attachment Restrictions
- **Location**: Attachment point locking and restrictions
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/rlv/RLVAttachmentManager.java`
- **Value**: Enables RLV-compatible role-play experiences

#### Permission System
- **Location**: Object permission validation
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/rlv/RLVPermissionManager.java`
- **Value**: Secure permission checking for RLV commands

**RLV Commands to Implement**:
```java
// Core RLV Commands (priority order)
@detach           // Prevent attachment removal
@addoutfit        // Force wear items
@remoutfit        // Force remove items
@tpto             // Force teleport
@accepttp         // Auto-accept teleport
@shownames        // Hide avatar names
@sendim           // Restrict IM sending
@recvim           // Restrict IM receiving
@fartouch         // Restrict far touch
@sittp            // Auto-sit on teleport
```

**Integration Steps**:
```bash
# 1. Clone Restrained Love repo
git clone https://github.com/N3X15/restrained-love restrained-love-viewer

# 2. Port RLV command parser
# Create RLVCommandParser.java from C++ implementation

# 3. Implement attachment restrictions
# Create RLVAttachmentManager.java

# 4. Add RLV communication channel
# Integrate with chat system for @commands
```

**Architecture**:
```
RLVManager (Main Controller)
├── RLVCommandParser (Parse @commands)
├── RLVPermissionManager (Validate permissions)
├── RLVAttachmentManager (Handle attachment locks)
├── RLVTeleportManager (Handle forced TP)
└── RLVInventoryManager (Handle outfit restrictions)
```

---

### 4. Linkpoint-Kotlin Repository
**Purpose**: C++/C# to Kotlin conversion attempts  
**Key Integration Areas**:

#### Protocol Conversion
- **Location**: C++ protocol handlers → Kotlin implementations
- **Integration Point**: `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/slproto/`
- **Value**: Modern Kotlin idioms for protocol handling

#### Coroutine Patterns
- **Location**: Async/await patterns using Kotlin coroutines
- **Integration Point**: Convert CompletableFuture → Kotlin coroutines
- **Value**: More idiomatic async code

#### Data Classes
- **Location**: C++ structs → Kotlin data classes
- **Integration Point**: Throughout `/workspace/app/src/main/java/com/lumiyaviewer/lumiya/`
- **Value**: Concise, immutable data structures

**Migration Examples**:

#### Example 1: CompletableFuture → Coroutines
```kotlin
// From CompletableFuture (current)
public CompletableFuture<Boolean> connectAsync(SLAuthParams params) {
    return CompletableFuture.supplyAsync(() -> {
        // connection logic
        return true;
    }, executor);
}

// To Kotlin Coroutines
suspend fun connectAsync(params: SLAuthParams): Boolean {
    return withContext(Dispatchers.IO) {
        // connection logic
        true
    }
}
```

#### Example 2: Java Classes → Kotlin Data Classes
```kotlin
// From Java (verbose)
public class InventoryItem {
    private final UUID itemId;
    private final String name;
    private final String description;
    
    public InventoryItem(UUID itemId, String name, String description) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
    }
    
    // Getters...
}

// To Kotlin (concise)
data class InventoryItem(
    val itemId: UUID,
    val name: String,
    val description: String
)
```

#### Example 3: Protocol Messages
```kotlin
// Current Java approach
public class ChatMessage {
    public enum Type { LOCAL, PRIVATE, GROUP }
    private String content;
    private Type type;
    // Constructor, getters, setters...
}

// Kotlin sealed classes approach
sealed class ChatMessage {
    abstract val content: String
    abstract val timestamp: Long
    
    data class Local(
        override val content: String,
        override val timestamp: Long,
        val channel: Int = 0
    ) : ChatMessage()
    
    data class Private(
        override val content: String,
        override val timestamp: Long,
        val fromId: UUID,
        val toId: UUID
    ) : ChatMessage()
    
    data class Group(
        override val content: String,
        override val timestamp: Long,
        val groupId: UUID
    ) : ChatMessage()
}
```

**Integration Steps**:
```bash
# 1. Clone Linkpoint-Kotlin repo
git clone [linkpoint-kotlin-url] linkpoint-kotlin

# 2. Review Kotlin patterns
# Identify successful conversions

# 3. Gradual migration strategy
# Start with new features in Kotlin
# Gradually convert Java → Kotlin

# 4. Keep Java/Kotlin interop
# Ensure smooth interoperability
```

---

## Integration Priorities

### Phase 1: Critical Protocol Support (Immediate)
1. **Second Life Protocol**: Core message definitions
2. **CAPS System**: HTTP capability endpoints
3. **Asset Formats**: Texture, mesh, animation formats

### Phase 2: Enhanced Features (Short-term)
1. **Firestorm UI**: Advanced inventory management
2. **RLV Protocol**: Basic RLV command support
3. **Performance**: Mesh streaming optimizations

### Phase 3: Full Feature Parity (Medium-term)
1. **Complete RLV**: Full RLV 3.x support
2. **Firestorm Features**: All major Firestorm enhancements
3. **Kotlin Migration**: Convert critical components to Kotlin

### Phase 4: Next-Generation (Long-term)
1. **Kotlin Coroutines**: Full async/await migration
2. **Modern Architecture**: Jetpack Compose UI
3. **Cross-Platform**: Kotlin Multiplatform Mobile (KMM)

---

## Code Organization

### Suggested Directory Structure
```
app/src/main/java/com/lumiyaviewer/lumiya/
├── slproto/              [Second Life repo integration]
│   ├── messages/         Protocol messages
│   ├── caps/            CAPS endpoints
│   └── assets/          Asset formats
├── modern/              [Current modern components]
│   ├── features/        Feature managers
│   ├── protocol/        Protocol layer
│   └── graphics/        Graphics pipeline
├── rlv/                 [Restrained Love repo integration]
│   ├── RLVManager.java
│   ├── RLVCommandParser.java
│   └── RLVPermissionManager.java
├── firestorm/           [Firestorm repo integration]
│   ├── inventory/       Advanced inventory
│   ├── ui/             Enhanced UI components
│   └── performance/    Performance optimizations
└── kotlin/              [Linkpoint-Kotlin repo integration]
    ├── coroutines/      Coroutine-based async
    ├── models/         Kotlin data classes
    └── extensions/     Kotlin extension functions
```

---

## Integration Best Practices

### 1. Maintain Compatibility
- Keep Java implementations alongside Kotlin during migration
- Ensure all features work with existing Second Life infrastructure
- Test with multiple grids (Main, Beta, OpenSim)

### 2. Gradual Migration
- Don't rewrite everything at once
- Migrate subsystems incrementally
- Keep the app buildable and testable throughout

### 3. Document Everything
- Comment source repository for each integrated feature
- Document API changes and migration notes
- Keep integration guides updated

### 4. Testing Strategy
```java
// Test Second Life protocol integration
@Test
public void testSLProtocolMessages() {
    // Verify message serialization matches official client
}

// Test Firestorm feature compatibility
@Test
public void testInventoryFiltering() {
    // Verify Firestorm-style inventory filtering
}

// Test RLV command handling
@Test
public void testRLVCommands() {
    // Verify RLV @detach command
}

// Test Kotlin interop
@Test
public void testKotlinJavaInterop() {
    // Verify Java/Kotlin classes work together
}
```

### 5. Performance Monitoring
- Benchmark each integration
- Profile memory usage
- Monitor network performance
- Test on real devices

---

## Integration Checklist

### Second Life Repository
- [ ] Extract protocol message definitions
- [ ] Port CAPS endpoint handlers
- [ ] Integrate asset format specifications
- [ ] Sync inventory folder types
- [ ] Port avatar baking logic

### Firestorm Repository
- [ ] Integrate RLV handler
- [ ] Port advanced inventory features
- [ ] Add UI enhancements
- [ ] Integrate performance optimizations
- [ ] Add outfit management system

### Restrained Love Repository
- [ ] Implement RLV command parser
- [ ] Add attachment restriction system
- [ ] Create permission validation
- [ ] Integrate with chat system
- [ ] Support RLV 3.x protocol

### Linkpoint-Kotlin Repository
- [ ] Review Kotlin conversion patterns
- [ ] Migrate utilities to Kotlin
- [ ] Convert data models to data classes
- [ ] Implement coroutine-based async
- [ ] Plan full Kotlin migration

---

## Technical Considerations

### Memory Management
- C++ uses manual memory management
- Java/Kotlin use garbage collection
- Ensure proper resource cleanup in conversions

### Threading Model
- C++ may use different threading patterns
- Java uses ExecutorService/ThreadPools
- Kotlin coroutines use structured concurrency
- Maintain thread safety during integration

### Error Handling
- C++ uses exceptions and return codes
- Java uses try/catch exceptions
- Kotlin has nullable types and Result types
- Standardize error handling across integrations

---

## Resources

### Documentation
- **Second Life Wiki**: https://wiki.secondlife.com/
- **Firestorm Wiki**: https://wiki.firestormviewer.org/
- **RLV Specification**: https://wiki.secondlife.com/wiki/LSL_Protocol/RestrainedLoveAPI
- **Kotlin Docs**: https://kotlinlang.org/docs/

### Community
- **Second Life Developers**: https://community.secondlife.com/forums/forum/305-open-source-development/
- **Firestorm Support**: https://www.firestormviewer.org/support/
- **OpenSimulator**: http://opensimulator.org/

### Tools
- **Java to Kotlin Converter**: Built into IntelliJ IDEA
- **Protocol Analyzers**: Wireshark with SL dissectors
- **Diff Tools**: Meld, Beyond Compare for code comparison

---

## Success Metrics

### Integration Quality
- ✅ All features compile without errors
- ✅ All tests pass
- ✅ No performance regressions
- ✅ Compatible with all supported grids

### Code Quality
- ✅ Follows Android best practices
- ✅ Properly documented
- ✅ Maintains type safety
- ✅ Thread-safe implementations

### User Experience
- ✅ No crashes or ANR
- ✅ Smooth animations (60 FPS)
- ✅ Fast startup time (<3 seconds)
- ✅ Low battery consumption

---

## Conclusion

Integrating code from these four repositories will significantly enhance the Lumiya Viewer / Linkpoint project:

1. **Second Life repo**: Authoritative protocol and asset specifications
2. **Firestorm repo**: Proven advanced features and optimizations
3. **Restrained Love repo**: Complete RLV support for role-play
4. **Linkpoint-Kotlin repo**: Modern Kotlin idioms and patterns

The integration should be gradual, well-tested, and documented. Priority should be given to features that provide the most value to users while maintaining stability and compatibility with the Second Life ecosystem.

**Next Steps**:
1. Clone all four repositories
2. Begin with Phase 1 (Critical Protocol Support)
3. Set up integration tests
4. Document each integration milestone
5. Release incrementally with user feedback