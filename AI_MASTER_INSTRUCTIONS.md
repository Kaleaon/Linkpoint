# Linkpoint AI Master Instructions
## Automated Code Generation & Repair Guide

This document provides step-by-step instructions for AI systems to generate, convert, and repair code in the Linkpoint project.

---

## Table of Contents
1. [Java to Kotlin Conversion](#java-to-kotlin-conversion)
2. [LLSD Implementation](#llsd-implementation)
3. [RLV Command Implementation](#rlv-command-implementation)
4. [Code Repair Procedures](#code-repair-procedures)
5. [Quality Assurance Checklist](#quality-assurance-checklist)

---

## Java to Kotlin Conversion

### Step 1: Analyze the Java File

```
INPUT: Java source file path
OUTPUT: Classification and conversion strategy
```

**Classification Categories:**
1. **Simple Interface** (< 10 lines, no logic)
2. **Data Class** (POJO with fields, getters, setters)
3. **Utility Class** (static methods only)
4. **Service Class** (business logic, dependencies)
5. **Android Component** (Activity, Fragment, Service)
6. **Protocol Handler** (LLSD, RLV, network)

**Analysis Checklist:**
- [ ] Count lines of code
- [ ] Identify class type (interface, abstract, concrete, enum)
- [ ] List dependencies (imports)
- [ ] Identify Android framework usage
- [ ] Check for static members
- [ ] Note any annotations
- [ ] Identify design patterns used

### Step 2: Apply Conversion Pattern

#### Pattern A: Simple Interface
```kotlin
// INPUT: Java interface
public interface HasPriority {
    int getPriority();
}

// OUTPUT: Kotlin interface
package com.lumiyaviewer.lumiya.utils

interface HasPriority {
    fun getPriority(): Int
}
```

**Conversion Rules:**
1. Remove `public` keyword (default in Kotlin)
2. Convert method signature: `ReturnType methodName(params)` → `fun methodName(params): ReturnType`
3. Remove semicolons
4. Add newline at end of file

#### Pattern B: Data Class
```kotlin
// INPUT: Java POJO
public class User {
    private UUID id;
    private String name;
    private boolean isOnline;
    
    public User(UUID id, String name, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.isOnline = isOnline;
    }
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    // ... more getters/setters
    
    @Override
    public boolean equals(Object o) { /* ... */ }
    @Override
    public int hashCode() { /* ... */ }
    @Override
    public String toString() { /* ... */ }
}

// OUTPUT: Kotlin data class
package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class User(
    var id: UUID,
    var name: String,
    var isOnline: Boolean = false,
)
```

**Conversion Rules:**
1. Replace class with `data class`
2. Move fields to primary constructor
3. Use `var` for mutable, `val` for immutable
4. Add default values where appropriate
5. Remove getters/setters (automatic)
6. Remove equals/hashCode/toString (automatic)
7. Add trailing comma after last parameter
8. Add newline at end of file

#### Pattern C: Enum Class
```kotlin
// INPUT: Java enum
public enum MessageType {
    LogSyncStart,
    LogSyncStatus,
    LogMessageBatch;
    
    public static final int CLOUD_PLUGIN_MESSAGE = 100;
    public static final int CLOUD_PLUGIN_RETRY = 101;
}

// OUTPUT: Kotlin enum class
package com.lumiyaviewer.lumiya.cloud.common

enum class MessageType {
    LogSyncStart,
    LogSyncStatus,
    LogMessageBatch;
    
    companion object {
        const val CLOUD_PLUGIN_MESSAGE = 100
        const val CLOUD_PLUGIN_RETRY = 101
    }
}
```

**Conversion Rules:**
1. Replace `enum` with `enum class`
2. Keep enum values as-is
3. Move static constants to `companion object`
4. Use `const val` for compile-time constants
5. Add newline at end of file

#### Pattern D: Utility Class with Static Methods
```kotlin
// INPUT: Java utility class
public class StringUtils {
    private StringUtils() {}
    
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

// OUTPUT: Kotlin object with extension functions
package com.lumiyaviewer.lumiya.utils

object StringUtils {
    fun isEmpty(str: String?): Boolean = str.isNullOrEmpty()
}

// Extension functions (preferred)
fun String?.isEmpty(): Boolean = this.isNullOrEmpty()
fun String.capitalize(): String = 
    if (isEmpty()) this else this[0].uppercase() + substring(1)
```

**Conversion Rules:**
1. Convert to `object` for singleton
2. OR convert static methods to extension functions
3. Use Kotlin stdlib functions where available
4. Replace null checks with safe calls (`?.`)
5. Use expression body for simple functions

#### Pattern E: Abstract Class
```kotlin
// INPUT: Java abstract class
public abstract class EventRateLimiter {
    private final EventBus bus;
    private volatile boolean isPending = false;
    private final long minInterval;
    
    protected EventRateLimiter(EventBus bus, long minInterval) {
        this.bus = bus;
        this.minInterval = minInterval;
    }
    
    public void fire() {
        synchronized (this.lock) {
            this.isPending = true;
        }
        firePending();
    }
    
    protected abstract Object getEventToFire();
    protected void onActualFire() {}
}

// OUTPUT: Kotlin abstract class
package com.lumiyaviewer.lumiya.eventbus

abstract class EventRateLimiter(
    private val bus: EventBus?,
    private val minInterval: Long,
) {
    @Volatile
    private var isPending = false
    
    private val lock = Any()
    
    fun fire() {
        synchronized(lock) {
            isPending = true
        }
        firePending()
    }
    
    protected abstract fun getEventToFire(): Any?
    
    protected open fun onActualFire() {
        // Default implementation
    }
}
```

**Conversion Rules:**
1. Move constructor parameters to primary constructor
2. Use `@Volatile` annotation for volatile fields
3. Replace `synchronized` blocks with `synchronized()` function
4. Use `Any()` for lock objects
5. Use `open` for overridable methods
6. Provide default implementation for optional overrides

### Step 3: Handle Special Cases

#### Null Safety Conversion
```kotlin
// Java with @Nullable/@NonNull
public void process(@Nullable String input, @NonNull User user) {
    if (input != null) {
        // use input
    }
}

// Kotlin with null safety
fun process(input: String?, user: User) {
    input?.let {
        // use it
    }
}
```

#### Collection Conversion
```kotlin
// Java
List<String> list = new ArrayList<>();
Map<String, Integer> map = new HashMap<>();

// Kotlin
val list = mutableListOf<String>()
val map = mutableMapOf<String, Int>()

// Or immutable
val list = listOf<String>()
val map = mapOf<String, Int>()
```

#### Exception Handling
```kotlin
// Java
try {
    riskyOperation();
} catch (IOException e) {
    handleError(e);
} finally {
    cleanup();
}

// Kotlin
try {
    riskyOperation()
} catch (e: IOException) {
    handleError(e)
} finally {
    cleanup()
}

// Or use runCatching
runCatching {
    riskyOperation()
}.onFailure { e ->
    handleError(e)
}
```

### Step 4: Format and Validate

```bash
# Run ktlint formatter
./tools/ktlint --format path/to/file.kt

# Verify no errors
./tools/ktlint path/to/file.kt
```

**Validation Checklist:**
- [ ] File ends with newline
- [ ] No trailing whitespace
- [ ] Proper indentation (4 spaces)
- [ ] Imports organized
- [ ] No unused imports
- [ ] Trailing commas in multi-line constructs
- [ ] Consistent naming conventions

---

## LLSD Implementation

### Creating LLSD Types

```kotlin
// Step 1: Define sealed class hierarchy
sealed class LLSD {
    object Undefined : LLSD()
    data class Boolean(val value: kotlin.Boolean) : LLSD()
    data class Integer(val value: Int) : LLSD()
    data class Real(val value: Double) : LLSD()
    data class UUID(val value: java.util.UUID) : LLSD()
    data class String(val value: kotlin.String) : LLSD()
    data class Date(val value: java.time.Instant) : LLSD()
    data class URI(val value: java.net.URI) : LLSD()
    data class Binary(val value: ByteArray) : LLSD() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Binary) return false
            return value.contentEquals(other.value)
        }
        
        override fun hashCode(): Int = value.contentHashCode()
    }
    data class Map(val value: kotlin.collections.Map<kotlin.String, LLSD>) : LLSD()
    data class Array(val value: List<LLSD>) : LLSD()
}

// Step 2: Add type conversion extensions
fun LLSD.asBoolean(): kotlin.Boolean = when (this) {
    is LLSD.Boolean -> value
    is LLSD.Integer -> value != 0
    is LLSD.Real -> value != 0.0
    is LLSD.String -> value.isNotEmpty()
    is LLSD.UUID -> value != java.util.UUID(0L, 0L)
    is LLSD.Binary -> value.isNotEmpty()
    else -> false
}

fun LLSD.asInteger(): Int = when (this) {
    is LLSD.Integer -> value
    is LLSD.Boolean -> if (value) 1 else 0
    is LLSD.Real -> value.toInt()
    is LLSD.String -> value.toIntOrNull() ?: 0
    is LLSD.Date -> (value.epochSecond).toInt()
    else -> 0
}

// Step 3: Implement serialization
interface LLSDSerializer {
    fun serialize(llsd: LLSD): String
    fun deserialize(data: String): LLSD
}

class LLSDXMLSerializer : LLSDSerializer {
    override fun serialize(llsd: LLSD): String {
        return buildString {
            append("<?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?>\n")
            append("<llsd>\n")
            serializeValue(llsd, this)
            append("</llsd>")
        }
    }
    
    private fun serializeValue(llsd: LLSD, builder: StringBuilder) {
        when (llsd) {
            is LLSD.Undefined -> builder.append("<undef />")
            is LLSD.Boolean -> builder.append("<boolean>${if (llsd.value) 1 else 0}</boolean>")
            is LLSD.Integer -> builder.append("<integer>${llsd.value}</integer>")
            is LLSD.Real -> builder.append("<real>${llsd.value}</real>")
            is LLSD.UUID -> builder.append("<uuid>${llsd.value}</uuid>")
            is LLSD.String -> builder.append("<string>${escapeXml(llsd.value)}</string>")
            is LLSD.Date -> builder.append("<date>${llsd.value}</date>")
            is LLSD.URI -> builder.append("<uri>${llsd.value}</uri>")
            is LLSD.Binary -> {
                val encoded = Base64.getEncoder().encodeToString(llsd.value)
                builder.append("<binary encoding=&quot;base64&quot;>$encoded</binary>")
            }
            is LLSD.Map -> {
                builder.append("<map>\n")
                llsd.value.forEach { (key, value) ->
                    builder.append("  <key>$key</key>\n")
                    builder.append("  ")
                    serializeValue(value, builder)
                    builder.append("\n")
                }
                builder.append("</map>")
            }
            is LLSD.Array -> {
                builder.append("<array>\n")
                llsd.value.forEach { value ->
                    builder.append("  ")
                    serializeValue(value, builder)
                    builder.append("\n")
                }
                builder.append("</array>")
            }
        }
    }
    
    private fun escapeXml(str: kotlin.String): kotlin.String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("&quot;", "&quot;")
            .replace("'", "&apos;")
    }
    
    override fun deserialize(data: String): LLSD {
        // XML parsing implementation
        TODO("Implement XML deserialization")
    }
}
```

---

## RLV Command Implementation

### Adding a New RLV Command

```kotlin
// Step 1: Define command in enum
enum class RLVCommand(val command: String) {
    // Movement
    FLY("fly"),
    TEMP_RUN("temprun"),
    ALWAYS_RUN("alwaysrun"),
    SET_ROT("setrot"),
    
    // Chat
    SEND_CHAT("sendchat"),
    RECV_CHAT("recvchat"),
    REDIR_CHAT("redirchat"),
    
    // Teleport
    TP_LOCAL("tplocal"),
    TP_LM("tplm"),
    TP_LOC("tploc"),
    TP_LURE("tplure"),
    TP_TO("tpto"),
    
    // Add new command here
    NEW_COMMAND("newcomm");
    
    companion object {
        fun fromString(str: String): RLVCommand? {
            return values().find { it.command == str }
        }
    }
}

// Step 2: Implement command handler
class RLVCommandHandler {
    private val restrictions = mutableMapOf<String, MutableSet<UUID>>()
    
    fun handleCommand(
        command: RLVCommand,
        option: String?,
        param: String,
        objectUUID: UUID
    ): Boolean {
        return when (param) {
            "n", "add" -> addRestriction(command, option, objectUUID)
            "y", "rem" -> removeRestriction(command, option, objectUUID)
            "force" -> executeForce(command, option)
            else -> false
        }
    }
    
    private fun addRestriction(
        command: RLVCommand,
        option: String?,
        objectUUID: UUID
    ): Boolean {
        val key = buildKey(command, option)
        restrictions.getOrPut(key) { mutableSetOf() }.add(objectUUID)
        
        // Apply the restriction
        return when (command) {
            RLVCommand.SEND_CHAT -> applySendChatRestriction(option)
            RLVCommand.FLY -> applyFlyRestriction()
            // Add new command handler here
            RLVCommand.NEW_COMMAND -> applyNewCommandRestriction(option)
            else -> false
        }
    }
    
    private fun applyNewCommandRestriction(option: String?): Boolean {
        // Implementation for new command
        // 1. Validate option if required
        // 2. Apply viewer behavior change
        // 3. Store state if needed
        // 4. Return success/failure
        return true
    }
    
    private fun buildKey(command: RLVCommand, option: String?): String {
        return if (option != null) {
            "${command.command}:$option"
        } else {
            command.command
        }
    }
}

// Step 3: Add query support if needed
fun getRestrictionStatus(command: RLVCommand, option: String?): Boolean {
    val key = buildKey(command, option)
    return restrictions.containsKey(key) && restrictions[key]?.isNotEmpty() == true
}

// Step 4: Add notification support
fun notifyRestrictionChange(command: RLVCommand, option: String?, added: Boolean) {
    val message = if (added) {
        "/${command.command}${option?.let { ":$it" } ?: ""}=n"
    } else {
        "/${command.command}${option?.let { ":$it" } ?: ""}=y"
    }
    
    // Send to notification channels
    notificationChannels.forEach { channel ->
        if (shouldNotify(channel, command.command)) {
            sendToChannel(channel, message)
        }
    }
}

// Step 5: Write tests
class RLVCommandTest {
    private lateinit var handler: RLVCommandHandler
    
    @Before
    fun setup() {
        handler = RLVCommandHandler()
    }
    
    @Test
    fun `new command should restrict when param is n`() {
        val objectUUID = UUID.randomUUID()
        val result = handler.handleCommand(
            RLVCommand.NEW_COMMAND,
            null,
            "n",
            objectUUID
        )
        
        assertTrue(result)
        assertTrue(handler.getRestrictionStatus(RLVCommand.NEW_COMMAND, null))
    }
    
    @Test
    fun `new command should allow when param is y`() {
        val objectUUID = UUID.randomUUID()
        
        // First restrict
        handler.handleCommand(RLVCommand.NEW_COMMAND, null, "n", objectUUID)
        
        // Then allow
        val result = handler.handleCommand(
            RLVCommand.NEW_COMMAND,
            null,
            "y",
            objectUUID
        )
        
        assertTrue(result)
        assertFalse(handler.getRestrictionStatus(RLVCommand.NEW_COMMAND, null))
    }
}
```

---

## Code Repair Procedures

### Procedure 1: Fix Compilation Errors

```kotlin
// Common Error 1: Unresolved reference
// ERROR: Unresolved reference: UUID
// FIX: Add import
import java.util.UUID

// Common Error 2: Type mismatch
// ERROR: Type mismatch: inferred type is String? but String was expected
// FIX: Add null check or safe call
val name: String = user.name ?: "Unknown"
// OR
val name: String? = user.name

// Common Error 3: Val cannot be reassigned
// ERROR: Val cannot be reassigned
// FIX: Change to var
var count = 0  // was: val count = 0
count++

// Common Error 4: Platform declaration clash
// ERROR: Platform declaration clash
// FIX: Use @JvmName annotation
@JvmName("getUserById")
fun getUser(id: Int): User { ... }

@JvmName("getUserByName")
fun getUser(name: String): User { ... }
```

### Procedure 2: Fix Runtime Errors

```kotlin
// Common Error 1: NullPointerException
// ERROR: Attempt to invoke method on null object reference
// FIX: Add null safety
fun process(user: User?) {
    user?.let {
        // Safe to use user here
        println(it.name)
    }
}

// Common Error 2: ClassCastException
// ERROR: Cannot cast X to Y
// FIX: Use safe cast
val user = obj as? User
if (user != null) {
    // Use user
}

// Common Error 3: ConcurrentModificationException
// ERROR: Collection was modified during iteration
// FIX: Use toList() or iterator
val itemsToRemove = list.filter { shouldRemove(it) }
list.removeAll(itemsToRemove)

// OR use iterator
val iterator = list.iterator()
while (iterator.hasNext()) {
    if (shouldRemove(iterator.next())) {
        iterator.remove()
    }
}
```

### Procedure 3: Fix Memory Leaks

```kotlin
// Common Leak 1: Activity/Fragment reference in callback
// PROBLEM: Callback holds reference to Activity
class MyActivity : AppCompatActivity() {
    fun startAsyncTask() {
        asyncTask.execute(object : Callback {
            override fun onComplete(result: Result) {
                // This holds reference to Activity
                updateUI(result)
            }
        })
    }
}

// FIX: Use WeakReference or lifecycle-aware components
class MyActivity : AppCompatActivity() {
    fun startAsyncTask() {
        val weakRef = WeakReference(this)
        asyncTask.execute(object : Callback {
            override fun onComplete(result: Result) {
                weakRef.get()?.updateUI(result)
            }
        })
    }
}

// BETTER: Use ViewModel and LiveData/Flow
class MyViewModel : ViewModel() {
    private val _result = MutableLiveData<Result>()
    val result: LiveData<Result> = _result
    
    fun startAsyncTask() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                performTask()
            }
            _result.value = result
        }
    }
}
```

---

## Quality Assurance Checklist

### Pre-Commit Checklist

- [ ] **Code compiles without errors**
  ```bash
  ./gradlew compileDebugKotlin
  ```

- [ ] **All tests pass**
  ```bash
  ./gradlew test
  ```

- [ ] **Code formatted with ktlint**
  ```bash
  ./tools/ktlint --format "**/*.kt"
  ```

- [ ] **No ktlint violations**
  ```bash
  ./tools/ktlint "**/*.kt"
  ```

- [ ] **Documentation updated**
  - KDoc comments for public APIs
  - README updated if needed
  - CHANGELOG entry added

- [ ] **No TODO/FIXME without issue reference**
  ```kotlin
  // BAD
  // TODO: Fix this later
  
  // GOOD
  // TODO(#123): Implement proper error handling
  ```

### Code Review Checklist

- [ ] **Null safety properly handled**
  - No `!!` operators without justification
  - Proper use of `?` and `?.`
  - Default values provided where appropriate

- [ ] **Proper error handling**
  - Exceptions caught and handled
  - User-friendly error messages
  - Logging for debugging

- [ ] **Performance considerations**
  - No blocking operations on main thread
  - Efficient algorithms used
  - Proper use of coroutines/async

- [ ] **Security considerations**
  - No hardcoded credentials
  - Proper input validation
  - Safe data handling

- [ ] **Accessibility**
  - Content descriptions for UI elements
  - Proper focus handling
  - Screen reader support

### Testing Checklist

- [ ] **Unit tests written**
  - Test happy path
  - Test error cases
  - Test edge cases
  - Test null inputs

- [ ] **Integration tests written**
  - Test component interactions
  - Test data flow
  - Test UI behavior

- [ ] **Test coverage > 80%**
  ```bash
  ./gradlew jacocoTestReport
  ```

---

## Emergency Procedures

### Procedure: Rollback Bad Conversion

```bash
# 1. Identify the bad commit
git log --oneline

# 2. Revert the commit
git revert <commit-hash>

# 3. Or reset to previous state (if not pushed)
git reset --hard HEAD~1

# 4. Restore Java file from backup
cp app/src/main/java.backup/path/to/File.java app/src/main/java/path/to/File.java

# 5. Remove bad Kotlin file
rm app/src/main/java/path/to/File.kt

# 6. Commit the fix
git add .
git commit -m "Revert bad conversion of File.java"
```

### Procedure: Fix Broken Build

```bash
# 1. Clean build
./gradlew clean

# 2. Invalidate caches
rm -rf .gradle
rm -rf build
rm -rf app/build

# 3. Sync Gradle
./gradlew --refresh-dependencies

# 4. Rebuild
./gradlew assembleDebug

# 5. If still failing, check for:
# - Missing dependencies in build.gradle
# - Incompatible library versions
# - Corrupted local Maven cache
```

---

**Last Updated:** 2025-10-14
**Version:** 1.0
**Maintainer:** SuperNinja AI