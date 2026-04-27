# Linkpoint AI Knowledge Base & Development Guide

## Project Overview

**Linkpoint** is a modern Second Life viewer based on the Lumiya codebase, implementing the full Second Life protocol stack including LLSD (Linden Lab Structured Data), RLV (Restrained Love Viewer), and modern Android development practices.

**Current Status:**
- Total Java files: 1,141 (to be converted)
- Total Kotlin files: 175 (13.3% complete)
- Target: 100% Kotlin conversion with modern Android architecture

---

## 1. LLSD (Linden Lab Structured Data) Specification

### 1.1 Overview
LLSD is Second Life's flexible data serialization format, similar to JSON but with additional types specific to virtual world operations.

### 1.2 Core Data Types

#### Atomic Types
1. **undefined** - Placeholder value (null equivalent)
2. **boolean** - true/false
3. **integer** - 32-bit signed integer (-2,147,483,648 to 2,147,483,647)
4. **real** - 64-bit IEEE 754 floating point
5. **uuid** - 128-bit unique identifier (format: 8-4-4-4-12)
6. **string** - Unicode character sequence (UTF-8)
7. **date** - ISO-8601 UTC timestamp (e.g., "2006-02-01T14:29:53.43Z")
8. **uri** - RFC 2396 compliant URI
9. **binary** - Byte sequence (base64/base16/base85 encoded)

#### Container Types
1. **map** - Key-value pairs (keys are strings, values are any LLSD type)
2. **array** - Ordered collection of LLSD values

### 1.3 Serialization Formats

#### XML Format (Primary)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<llsd>
<map>
  <key>agent_id</key>
  <uuid>93c73b16-cd86-434d-8b4a-76e12eee950a</uuid>
  <key>name</key>
  <string>testtest tester</string>
  <key>position</key>
  <array>
    <real>70.9247</real>
    <real>254.378</real>
    <real>38.7304</real>
  </array>
</map>
</llsd>
```

#### Binary Format (Performance)
- Prefix: `<? LLSD/Binary ?>`
- Type markers: '!', '1', '0', 'i', 'r', 'u', 'b', 's', 'l', 'd', '[', ']', '{', '}'
- Network byte order (big-endian) for integers and doubles

#### Notation Format (Human-Readable)
```
<?llsd/notation?>
{
  'agent_id':u93c73b16-cd86-434d-8b4a-76e12eee950a,
  'name':'testtest tester',
  'position':[r70.9247,r254.378,r38.7304]
}
```

### 1.4 Type Conversion Rules

| From → To | Boolean | Integer | Real | String | UUID | Binary |
|-----------|---------|---------|------|--------|------|--------|
| Boolean | unity | 1/0 | 1.0/0.0 | "true"/"false" | n/a | 0x01/0x00 |
| Integer | 0→false, else→true | unity | cast | string repr | n/a | 4-byte network order |
| Real | 0.0→false, else→true | round | unity | string repr | n/a | 8-byte network order |
| String | ""→false, else→true | parse | parse | unity | parse 8-4-4-4-12 | UTF-8 bytes |
| UUID | null→false, else→true | n/a | n/a | 8-4-4-4-12 format | unity | 16 bytes |
| Binary | empty→false, else→true | first 4 bytes | first 8 bytes | UTF-8 decode | first 16 bytes | unity |

### 1.5 Kotlin Implementation Guidelines

```kotlin
// LLSD Base Interface
sealed class LLSD {
    object Undefined : LLSD()
    data class Boolean(val value: kotlin.Boolean) : LLSD()
    data class Integer(val value: Int) : LLSD()
    data class Real(val value: Double) : LLSD()
    data class UUID(val value: java.util.UUID) : LLSD()
    data class String(val value: kotlin.String) : LLSD()
    data class Date(val value: java.time.Instant) : LLSD()
    data class URI(val value: java.net.URI) : LLSD()
    data class Binary(val value: ByteArray) : LLSD()
    data class Map(val value: kotlin.collections.Map<kotlin.String, LLSD>) : LLSD()
    data class Array(val value: List<LLSD>) : LLSD()
}

// Type-safe accessors
fun LLSD.asBoolean(): Boolean = when (this) {
    is LLSD.Boolean -> value
    is LLSD.Integer -> value != 0
    is LLSD.Real -> value != 0.0
    is LLSD.String -> value.isNotEmpty()
    else -> false
}

fun LLSD.asInteger(): Int = when (this) {
    is LLSD.Integer -> value
    is LLSD.Boolean -> if (value) 1 else 0
    is LLSD.Real -> value.toInt()
    is LLSD.String -> value.toIntOrNull() ?: 0
    else -> 0
}
```

---

## 2. RLV (Restrained Love Viewer) Protocol

### 2.1 Overview
RLV is a protocol extension for Second Life viewers that allows in-world objects to control viewer behavior through special commands sent via `llOwnerSay()`.

### 2.2 Command Structure

```
@<command1>[:option1]=<param1>,<command2>[:option2]=<param2>,...
```

**Key Points:**
- Commands start with `@` symbol
- Multiple commands separated by commas
- Options separated by colons
- Parameters: `n` (restrict), `y` (allow), `force` (immediate action), `add`/`rem` (exceptions)

### 2.3 Command Categories

#### Movement & Camera
```kotlin
// Movement restrictions
@fly=n                    // Prevent flying
@temprun=n               // Prevent double-tap running
@alwaysrun=n             // Prevent always-run mode
@setrot:<angle>=force    // Force rotation

// Camera controls
@camzoommax:<mult>=n     // Limit zoom in
@camzoommin:<mult>=n     // Limit zoom out
@camdistmax:<dist>=n     // Max camera distance
@camdistmin:<dist>=n     // Min camera distance (0=force mouselook)
@camunlock=n             // Lock camera to avatar
@camtextures=n           // Hide all textures
```

#### Chat & Communication
```kotlin
// Chat restrictions
@sendchat=n              // Prevent public chat
@recvchat=n              // Block incoming chat
@recvchat:<uuid>=add     // Exception for specific avatar
@redirchat:<channel>=add // Redirect chat to private channel

// Instant Messages
@sendim=n                // Prevent sending IMs
@recvim=n                // Block incoming IMs
@sendim:<uuid>=add       // Exception for specific avatar
@startim=n               // Prevent starting IM sessions
```

#### Teleportation
```kotlin
@tplocal=n               // Prevent local teleport
@tplm=n                  // Prevent landmark teleport
@tploc=n                 // Prevent map teleport
@tplure=n                // Prevent friend teleport
@tplure:<uuid>=add       // Exception for specific friend
@tpto:<x>/<y>/<z>=force  // Force teleport (global coords)
@sittp=n                 // Prevent sit-teleport
```

#### Inventory & Attachments
```kotlin
@showinv=n               // Hide inventory
@edit=n                  // Prevent editing objects
@rez=n                   // Prevent rezzing objects
@detach=n                // Lock current attachment
@detach:<point>=n        // Lock attachment point
@addattach:<point>=n     // Prevent attaching to point
@remattach:<point>=n     // Prevent detaching from point
```

#### Clothing
```kotlin
@addoutfit=n             // Prevent wearing clothes
@remoutfit=n             // Prevent removing clothes
@addoutfit:<layer>=n     // Prevent wearing specific layer
@remoutfit:<layer>=n     // Prevent removing specific layer

// Layers: gloves, jacket, pants, shirt, shoes, skirt, socks,
//         underpants, undershirt, skin, eyes, hair, shape,
//         alpha, tattoo, physics
```

### 2.4 Query Commands

```kotlin
// Version checking
@version=<channel>           // Get RLV version
@versionnew=<channel>        // Get version (new format)
@versionnum=<channel>        // Get version number

// Status queries
@getstatus=<channel>         // Get restrictions from this object
@getstatusall=<channel>      // Get all restrictions
@getoutfit=<channel>         // Get worn clothing
@getattach=<channel>         // Get worn attachments
@getinv=<channel>            // Get shared folders
@getsitid=<channel>          // Get UUID of sit object
```

### 2.5 Kotlin Implementation Pattern

```kotlin
// RLV Command Parser
data class RLVCommand(
    val command: String,
    val option: String? = null,
    val param: String
)

fun parseRLVMessage(message: String): List<RLVCommand> {
    if (!message.startsWith("@")) return emptyList()
    
    return message.substring(1).split(",").mapNotNull { part ->
        val (cmdOpt, param) = part.split("=")
        val (command, option) = if (":" in cmdOpt) {
            cmdOpt.split(":", limit = 2)
        } else {
            listOf(cmdOpt, null)
        }
        RLVCommand(command, option, param)
    }
}

// RLV Command Handler
interface RLVCommandHandler {
    fun handleCommand(command: RLVCommand): Boolean
    fun isRestricted(command: String): Boolean
    fun getRestrictions(): List<String>
}

// Example implementation
class RLVManager : RLVCommandHandler {
    private val restrictions = mutableMapOf<String, MutableSet<UUID>>()
    
    override fun handleCommand(command: RLVCommand): Boolean {
        return when (command.param) {
            "n", "add" -> addRestriction(command)
            "y", "rem" -> removeRestriction(command)
            "force" -> executeForce(command)
            else -> false
        }
    }
    
    private fun addRestriction(command: RLVCommand): Boolean {
        val key = if (command.option != null) {
            "${command.command}:${command.option}"
        } else {
            command.command
        }
        restrictions.getOrPut(key) { mutableSetOf() }.add(getCurrentObjectUUID())
        return true
    }
}
```

---

## 3. Second Life Protocol Types

### 3.1 Core Vector Types

```kotlin
// 3D Vector (32-bit floats)
data class LLVector3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {
    operator fun plus(other: LLVector3) = LLVector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: LLVector3) = LLVector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = LLVector3(x * scalar, y * scalar, z * scalar)
    
    fun length(): Float = sqrt(x * x + y * y + z * z)
    fun normalize(): LLVector3 {
        val len = length()
        return if (len > 0f) this * (1f / len) else this
    }
}

// 3D Vector (64-bit doubles) for global coordinates
data class LLVector3d(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
)

// 4D Vector for colors/quaternions
data class LLVector4(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 0f
)

// Quaternion for rotations
data class LLQuaternion(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 1f
) {
    fun toEuler(): LLVector3 {
        // Convert quaternion to Euler angles
        val sinr_cosp = 2 * (w * x + y * z)
        val cosr_cosp = 1 - 2 * (x * x + y * y)
        val roll = atan2(sinr_cosp, cosr_cosp)
        
        val sinp = 2 * (w * y - z * x)
        val pitch = if (abs(sinp) >= 1) {
            (PI / 2).toFloat() * sign(sinp)
        } else {
            asin(sinp)
        }
        
        val siny_cosp = 2 * (w * z + x * y)
        val cosy_cosp = 1 - 2 * (y * y + z * z)
        val yaw = atan2(siny_cosp, cosy_cosp)
        
        return LLVector3(roll, pitch, yaw)
    }
}
```

### 3.2 UUID Handling

```kotlin
// UUID utilities for Second Life
object SLUUIDUtils {
    val NULL_UUID = UUID(0L, 0L)
    
    fun isValid(uuid: UUID): Boolean = uuid != NULL_UUID
    
    fun fromString(str: String): UUID? = try {
        UUID.fromString(str)
    } catch (e: IllegalArgumentException) {
        null
    }
    
    // Generate UUID from name (for consistent object IDs)
    fun generateFromName(name: String): UUID {
        val bytes = name.toByteArray(Charsets.UTF_8)
        val md5 = MessageDigest.getInstance("MD5")
        val hash = md5.digest(bytes)
        return UUID.nameUUIDFromBytes(hash)
    }
}
```

---

## 4. Kotlin Conversion Guidelines

### 4.1 General Principles

1. **Null Safety**: Use Kotlin's null safety features
   ```kotlin
   // Java
   String name = user.getName();
   if (name != null) {
       // use name
   }
   
   // Kotlin
   val name = user.name
   name?.let {
       // use it
   }
   ```

2. **Data Classes**: Convert POJOs to data classes
   ```kotlin
   // Java
   public class User {
       private UUID id;
       private String name;
       // getters, setters, equals, hashCode, toString
   }
   
   // Kotlin
   data class User(
       val id: UUID,
       val name: String
   )
   ```

3. **Default Parameters**: Use instead of overloads
   ```kotlin
   // Java
   public void teleport(Vector3 pos) {
       teleport(pos, null);
   }
   public void teleport(Vector3 pos, Quaternion rot) {
       // implementation
   }
   
   // Kotlin
   fun teleport(
       pos: Vector3,
       rot: Quaternion? = null
   ) {
       // implementation
   }
   ```

4. **Extension Functions**: Add utility methods
   ```kotlin
   // Instead of utility classes
   fun UUID.isNull(): Boolean = this == SLUUIDUtils.NULL_UUID
   fun String.toUUID(): UUID? = SLUUIDUtils.fromString(this)
   ```

5. **Sealed Classes**: For type hierarchies
   ```kotlin
   sealed class SLEvent {
       data class ChatMessage(val text: String, val sender: UUID) : SLEvent()
       data class Teleport(val destination: Vector3d) : SLEvent()
       object Logout : SLEvent()
   }
   ```

### 4.2 Common Patterns

#### Singleton Objects
```kotlin
// Java
public class Manager {
    private static Manager instance;
    private Manager() {}
    public static Manager getInstance() {
        if (instance == null) instance = new Manager();
        return instance;
    }
}

// Kotlin
object Manager {
    // automatically singleton
}
```

#### Companion Objects
```kotlin
// Java
public class User {
    public static final int MAX_NAME_LENGTH = 64;
    public static User create(String name) { ... }
}

// Kotlin
class User {
    companion object {
        const val MAX_NAME_LENGTH = 64
        fun create(name: String): User = ...
    }
}
```

#### Property Delegation
```kotlin
// Lazy initialization
val expensiveObject by lazy {
    // computed only once, on first access
    ExpensiveObject()
}

// Observable properties
var name: String by Delegates.observable("") { _, old, new ->
    println("Name changed from $old to $new")
}
```

### 4.3 Android-Specific Patterns

#### ViewBinding
```kotlin
// Replace findViewById
class MyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.myButton.setOnClickListener {
            // handle click
        }
    }
}
```

#### Coroutines for Async
```kotlin
// Replace AsyncTask
class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                // background work
                repository.fetchData()
            }
            // update UI with data
            _uiState.value = UiState.Success(data)
        }
    }
}
```

---

## 5. Code Quality Standards

### 5.1 Naming Conventions

```kotlin
// Classes: PascalCase
class UserManager

// Functions/Properties: camelCase
fun getUserName()
val userName: String

// Constants: SCREAMING_SNAKE_CASE
const val MAX_RETRY_COUNT = 3

// Private properties: _camelCase (optional)
private val _users = mutableListOf<User>()
val users: List<User> get() = _users
```

### 5.2 Documentation

```kotlin
/**
 * Manages RLV (Restrained Love Viewer) commands and restrictions.
 *
 * This class handles parsing and executing RLV commands sent from in-world
 * objects via llOwnerSay(). It maintains the current restriction state and
 * enforces viewer behavior modifications.
 *
 * @property restrictions Current active restrictions mapped by command
 * @see RLVCommand
 * @see parseRLVMessage
 */
class RLVManager {
    /**
     * Handles an RLV command and updates restrictions accordingly.
     *
     * @param command The parsed RLV command to execute
     * @return true if the command was successfully handled, false otherwise
     * @throws IllegalArgumentException if the command format is invalid
     */
    fun handleCommand(command: RLVCommand): Boolean {
        // implementation
    }
}
```

### 5.3 Error Handling

```kotlin
// Use sealed classes for results
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

// Use runCatching for exception handling
fun parseUUID(str: String): Result<UUID> = runCatching {
    UUID.fromString(str)
}.fold(
    onSuccess = { Result.Success(it) },
    onFailure = { Result.Error(it as? Exception ?: Exception(it)) }
)
```

---

## 6. Testing Guidelines

### 6.1 Unit Tests

```kotlin
class RLVManagerTest {
    private lateinit var manager: RLVManager
    
    @Before
    fun setup() {
        manager = RLVManager()
    }
    
    @Test
    fun `handleCommand should add restriction when param is n`() {
        val command = RLVCommand("sendchat", null, "n")
        val result = manager.handleCommand(command)
        
        assertTrue(result)
        assertTrue(manager.isRestricted("sendchat"))
    }
    
    @Test
    fun `parseRLVMessage should handle multiple commands`() {
        val message = "@sendchat=n,recvim=n,tplure:uuid=add"
        val commands = parseRLVMessage(message)
        
        assertEquals(3, commands.size)
        assertEquals("sendchat", commands[0].command)
        assertEquals("recvim", commands[1].command)
        assertEquals("tplure", commands[2].command)
        assertEquals("uuid", commands[2].option)
    }
}
```

### 6.2 Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class RLVIntegrationTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testRLVCommandExecution() {
        // Test full RLV command flow
        onView(withId(R.id.chat_input))
            .perform(typeText("@sendchat=n"))
        
        // Verify restriction is applied
        onView(withId(R.id.chat_input))
            .check(matches(not(isEnabled())))
    }
}
```

---

## 7. Common Conversion Patterns

### 7.1 Event Bus Pattern

```kotlin
// Java EventBus
@Subscribe
public void onEvent(ChatEvent event) {
    // handle event
}

// Kotlin Flow
class EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()
    
    suspend fun emit(event: Event) {
        _events.emit(event)
    }
}

// Usage
viewModelScope.launch {
    eventBus.events.collect { event ->
        when (event) {
            is ChatEvent -> handleChat(event)
            is TeleportEvent -> handleTeleport(event)
        }
    }
}
```

### 7.2 Callback Pattern

```kotlin
// Java callback
interface Callback {
    void onSuccess(Data data);
    void onError(Exception e);
}

// Kotlin suspend function
suspend fun fetchData(): Result<Data> = withContext(Dispatchers.IO) {
    try {
        Result.Success(api.getData())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

### 7.3 Builder Pattern

```kotlin
// Java builder
User user = new User.Builder()
    .setName("John")
    .setAge(30)
    .build();

// Kotlin DSL
fun user(block: UserBuilder.() -> Unit): User {
    return UserBuilder().apply(block).build()
}

val user = user {
    name = "John"
    age = 30
}
```

---

## 8. Resources & References

### 8.1 Official Documentation
- [Second Life Wiki - LLSD](https://wiki.secondlife.com/wiki/LLSD)
- [Second Life Wiki - RLV API](https://wiki.secondlife.com/wiki/LSL_Protocol/RestrainedLoveAPI)
- [IETF LLSD Draft](https://datatracker.ietf.org/doc/html/draft-hamrick-llsd-00)

### 8.2 Open Source Implementations
- [LibreMetaverse](https://github.com/cinderblocks/libremetaverse) - C# implementation
- [Python LLSD](https://github.com/secondlife/python-llsd) - Python implementation
- [Firestorm Viewer](https://www.firestormviewer.org/) - Reference viewer

### 8.3 Kotlin Resources
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Android Kotlin Guide](https://developer.android.com/kotlin)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## 9. AI Assistant Instructions

When assisting with Linkpoint development:

1. **Always prioritize type safety** - Use Kotlin's null safety and type system
2. **Follow LLSD specifications exactly** - Data format compatibility is critical
3. **Implement RLV commands precisely** - User safety depends on correct restrictions
4. **Use modern Android patterns** - ViewBinding, Coroutines, Flow, ViewModel
5. **Write comprehensive tests** - Protocol implementations must be reliable
6. **Document protocol details** - Future developers need clear references
7. **Maintain backward compatibility** - Second Life protocol is stable
8. **Optimize for mobile** - Battery life and performance matter

### Common Tasks

#### Converting a Java class to Kotlin
1. Identify the class purpose (data, service, utility, UI)
2. Choose appropriate Kotlin construct (data class, object, sealed class)
3. Convert fields to properties with appropriate visibility
4. Replace getters/setters with property access
5. Use default parameters instead of overloads
6. Add extension functions for utilities
7. Implement proper null safety
8. Add KDoc documentation
9. Write unit tests
10. Run ktlint for formatting

#### Implementing an LLSD type
1. Create sealed class hierarchy
2. Implement type conversion methods
3. Add serialization/deserialization
4. Write comprehensive tests for all conversions
5. Document format specifications

#### Adding an RLV command
1. Document command specification from RLV wiki
2. Add command to parser
3. Implement restriction logic
4. Add exception handling
5. Implement query command if applicable
6. Write integration tests
7. Update documentation

---

**Last Updated:** 2025-10-14
**Version:** 1.0
**Maintainer:** SuperNinja AI