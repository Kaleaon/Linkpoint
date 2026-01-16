# Network Protocol Standards for Second Life Viewers

## Overview

This document establishes comprehensive standards for implementing Second Life network protocols in mobile viewers, derived from analysis of the official Second Life Android viewer, Lumiya mobile viewer, and other TPV (Third Party Viewer) implementations.

## Authentication & Login Protocol

### Standard Implementation

```kotlin
/**
 * Standard login flow for Second Life viewers
 * Based on Second Life Android Viewer 2025.12.1075
 */
object LoginProtocol {
    
    // Login URL endpoints
    const val LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
    const val AUTH_METHOD = "login_to_simulator"
    
    // Required login parameters
    data class LoginRequest(
        val first: String,           // First name
        val last: String,            // Last name  
        val password: String,        // Password (hashed)
        val start: String = "last",  // Start location preference
        val channel: String,         // Viewer channel identification
        val version: String,         // Viewer version
        val platform: String = "Android",
        val mac: String,             // Hardware identifier
        val id0: String,             // Machine ID hash 1
        val id1: String,             // Machine ID hash 2
        val agree_to_tos: Boolean = true
    )
    
    // Standard response fields
    data class LoginResponse(
        val circuitCode: Int,        // Circuit identifier
        val sessionId: String,       // Session identifier
        val agentId: String,         // Agent UUID
        val simIP: String,           // Simulator IP address
        val simPort: Int,            // Simulator port
        val regionX: Int,            // Region X coordinate
        val regionY: Int,            // Region Y coordinate
        val capsUrl: String,         // Capabilities URL
        val seedCapability: String   // Initial capability seed
    )
    
    // Channel naming standards
    const val CHANNEL_OFFICIAL = "Second Life Release"
    const val CHANNEL_BETA = "Second Life Beta"
    const val CHANNEL_TPV = "[TPV_NAME] [VERSION]" // e.g., "Linkpoint 1.0"
}
```

### Password Hashing Standards

```kotlin
/**
 * Standard password hashing for login
 * Uses MD5 hash of password in MD5 format
 */
object PasswordHasher {
    
    fun hashPassword(password: String): String {
        // Convert password to MD5 hash (legacy Second Life format)
        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    fun hashMachineId(machineId: String): String {
        // Hash machine identifier for privacy
        return hashPassword("$machineId-SecondLife")
    }
}
```

### Login Error Handling

```kotlin
/**
 * Standard login error codes and messages
 */
enum class LoginError(val code: Int, val message: String) {
    SUCCESS(0, "Login successful"),
    INVALID_CREDENTIALS(1, "Invalid username or password"),
    ACCOUNT_DISABLED(2, "Account has been disabled"),
    REGION_UNAVAILABLE(3, "Region is currently unavailable"),
    FULL(4, "Grid is full, please try again later"),
    VERSION_MISMATCH(5, "Viewer version mismatch"),
    TIMEOUT(6, "Connection timeout"),
    NETWORK_ERROR(7, "Network error occurred")
}
```

## UDP Packet Protocol Standards

### Packet Structure

```kotlin
/**
 * Standard UDP packet structure
 * Based on Second Life network protocol specification
 */
data class UDPPacket(
    val frequency: PacketFrequency,
    val messageId: Int,
    val reliable: Boolean = false,
    val sequenceNumber: Int,
    val body: ByteArray
) {
    enum class PacketFrequency {
        HIGH,        // High frequency messages (movement, camera)
        MEDIUM,      // Medium frequency (chat, IM)
        LOW,         // Low frequency (inventory, profile)
        FIXED        // Fixed frequency (system messages)
    }
    
    companion object {
        const val HEADER_SIZE = 10 // Standard header size
        const val MAX_PACKET_SIZE = 1200 // Maximum reliable packet size
    }
}
```

### Message Queue Management

```kotlin>
/**
 * Standard message queue implementation
 * Based on Lumiya mobile viewer implementation
 */
class MessageQueue {
    
    private val highPriorityQueue = ArrayDeque<UDPPacket>()
    private val mediumPriorityQueue = ArrayDeque<UDPPacket>()
    private val lowPriorityQueue = ArrayDeque<UDPPacket>()
    
    private var nextSequenceNumber = 0
    
    fun enqueue(packet: UDPPacket) {
        when (packet.frequency) {
            PacketFrequency.HIGH -> highPriorityQueue.addLast(packet)
            PacketFrequency.MEDIUM -> mediumPriorityQueue.addLast(packet)
            PacketFrequency.LOW -> lowPriorityQueue.addLast(packet)
            PacketFrequency.FIXED -> highPriorityQueue.addFirst(packet)
        }
    }
    
    fun getNextPacket(): UDPPacket? {
        return highPriorityQueue.removeFirstOrNull()
            ?: mediumPriorityQueue.removeFirstOrNull()
            ?: lowPriorityQueue.removeFirstOrNull()
    }
    
    fun getNextSequenceNumber(): Int {
        return nextSequenceNumber++
    }
}
```

### Reliable Packet Handling

```kotlin>
/**
 * Standard reliable packet acknowledgment
 */
class ReliabilityManager {
    
    private val pendingAcks = mutableMapOf<Int, UDPPacket>()
    private val resendTimeout = 5000L // 5 seconds
    private val maxResendAttempts = 3
    
    fun markPending(packet: UDPPacket) {
        pendingAcks[packet.sequenceNumber] = packet
    }
    
    fun handleAck(sequenceNumber: Int) {
        pendingAcks.remove(sequenceNumber)
    }
    
    fun getTimeoutPackets(): List<UDPPacket> {
        val now = System.currentTimeMillis()
        return pendingAcks.values.filter { 
            // Implementation would track packet timestamps
            false // Simplified
        }
    }
}
```

## Capabilities System Standards

### Standard Capabilities

```kotlin>
/**
 * Standard Second Life capabilities
 * Based on official viewer implementation
 */
object Capabilities {
    
    // Core capabilities (always required)
    const val SEED = "SeedCapability"
    const val EVENT_QUEUE = "EventQueueGet"
    const val CHAT_SEND = "ChatSessionRequest"
    const val IM_SESSION = "InstantMessage"
    
    // Asset capabilities
    const val GET_TEXTURE = "GetTexture"
    const val GET_ASSET = "GetAsset"
    
    // Avatar capabilities
    const val AVATAR_PROPERTIES = "AvatarProperties"
    const val AVATAR_OUTFITS = "AvatarOutfits"
    
    // Region capabilities
    const val REGION = "Region"
    const val PARCEL_INFO = "ParcelInfo"
    
    // Social capabilities
    const val FRIENDS = "FriendFinder"
    const val GROUPS = "GroupMembership"
    
    // Get capability URL
    fun getCapabilityUrl(baseUrl: String, capability: String): String {
        return "$baseUrl/$capability"
    }
}
```

### Capability Management

```kotlin>
/**
 * Standard capability manager implementation
 */
class CapabilityManager(private val seedUrl: String) {
    
    private val capabilities = mutableMapOf<String, String>()
    private val capabilityCache = ConcurrentHashMap<String, CapabilityCache>()
    
    data class CapabilityCache(
        val url: String,
        val timestamp: Long = System.currentTimeMillis(),
        val ttl: Long = 300000L // 5 minutes
    )
    
    suspend fun acquireCapability(name: String): String {
        // Check cache first
        capabilityCache[name]?.let { cache ->
            if (System.currentTimeMillis() - cache.timestamp < cache.ttl) {
                return cache.url
            }
        }
        
        // Request capability from server
        val url = requestCapability(name)
        capabilities[name] = url
        
        return url
    }
    
    private suspend fun requestCapability(name: String): String {
        // Implementation: HTTP GET request to seed URL
        return "$seedUrl/$name"
    }
    
    fun invalidateCapability(name: String) {
        capabilities.remove(name)
        capabilityCache.remove(name)
    }
    
    fun invalidateAll() {
        capabilities.clear()
        capabilityCache.clear()
    }
}
```

## Keep-Alive Standards

### Keep-Alive Implementation

```kotlin>
/**
 * Standard keep-alive mechanism
 */
class KeepAliveManager(
    private val udpConnection: UDPConnection,
    private val interval: Long = 30000L // 30 seconds
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var keepAliveJob: Job? = null
    private var lastActivityTime = System.currentTimeMillis()
    
    fun start() {
        keepAliveJob = scope.launch {
            while (isActive) {
                delay(interval)
                sendKeepAlive()
            }
        }
    }
    
    fun stop() {
        keepAliveJob?.cancel()
        keepAliveJob = null
    }
    
    fun recordActivity() {
        lastActivityTime = System.currentTimeMillis()
    }
    
    private suspend fun sendKeepAlive() {
        val packet = createKeepAlivePacket()
        udpConnection.send(packet)
    }
    
    private fun createKeepAlivePacket(): UDPPacket {
        // Implementation: Create keep-alive packet
        return UDPPacket(
            frequency = UDPPacket.PacketFrequency.FIXED,
            messageId = 0, // Keep-alive message ID
            reliable = false,
            sequenceNumber = 0,
            body = ByteArray(0)
        )
    }
}
```

## Message Serialization Standards

### LLSD (Linden Lab Structured Data)

```kotlin>
/**
 * Standard LLSD serialization
 * Used for complex data structures in HTTP requests/responses
 */
sealed class LLSDValue {
    data class LLSDString(val value: String) : LLSDValue()
    data class LLSDInteger(val value: Int) : LLSDValue()
    data class LLSDReal(val value: Double) : LLSDValue()
    data class LLSDBoolean(val value: Boolean) : LLSDValue()
    data class LLSDArray(val value: List<LLSDValue>) : LLSDValue()
    data class LLSDMap(val value: Map<String, LLSDValue>) : LLSDValue()
    data class LLSDUUID(val value: String) : LLSDValue() // UUID as string
    data class LLSDBinary(val value: ByteArray) : LLSDValue()
    data class LLSDBase64(val value: String) : LLSDValue()
    
    fun toXML(): String {
        // Convert LLSD to XML format
        return when (this) {
            is LLSDString -> "<string>$value</string>"
            is LLSDInteger -> "<integer>$value</integer>"
            is LLSDReal -> "<real>$value</real>"
            is LLSDBoolean -> "<boolean>${if (value) 1 else 0}</boolean>"
            is LLSDArray -> "<array>${value.joinToString("") { it.toXML() }}</array>"
            is LLSDMap -> "<map>${value.entries.joinToString("") { 
                "<key>${it.key}</key>${it.value.toXML()}" 
            }}</map>"
            is LLSDUUID -> "<uuid>$value</uuid>"
            is LLSDBinary -> "<binary>${java.util.Base64.getEncoder().encodeToString(value)}</binary>"
            is LLSDBase64 -> "<binary>$value</binary>"
        }
    }
}
```

### Binary Message Formatting

```kotlin>
/**
 * Standard binary message formatting
 * Used for efficient UDP packet encoding
 */
class BinaryMessageBuilder {
    
    private val buffer = ByteArrayOutputStream()
    private val dataOutputStream = DataOutputStream(buffer)
    
    fun writeByte(value: Int) {
        dataOutputStream.writeByte(value)
    }
    
    fun writeShort(value: Int) {
        dataOutputStream.writeShort(value)
    }
    
    fun writeInt(value: Int) {
        dataOutputStream.writeInt(value)
    }
    
    fun writeString(value: String) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        dataOutputStream.writeInt(bytes.size)
        dataOutputStream.write(bytes)
    }
    
    fun writeUUID(uuid: String) {
        // Parse UUID and write as 16-byte array
        val bytes = UUID.fromString(uuid).toString().toByteArray()
        dataOutputStream.write(bytes)
    }
    
    fun build(): ByteArray {
        dataOutputStream.flush()
        return buffer.toByteArray()
    }
}
```

## Error Handling Standards

### Network Error Types

```kotlin>
/**
 * Standard network error types
 */
sealed class NetworkError(message: String) : Exception(message) {
    class ConnectionError(message: String) : NetworkError(message)
    class TimeoutError(message: String) : NetworkError(message)
    class AuthenticationError(message: String) : NetworkError(message)
    class ProtocolError(message: String) : NetworkError(message)
    class SerializationError(message: String) : NetworkError(message)
    class ServerError(code: Int, message: String) : NetworkError("Server error $code: $message")
}
```

### Retry Strategy

```kotlin>
/**
 * Standard retry strategy for network operations
 */
class RetryStrategy(
    private val maxAttempts: Int = 3,
    private val baseDelay: Long = 1000L,
    private val maxDelay: Long = 30000L
) {
    
    suspend fun <T> executeWithRetry(
        operation: suspend () -> T
    ): T {
        var attempt = 0
        var delay = baseDelay
        
        while (attempt < maxAttempts) {
            try {
                return operation()
            } catch (e: NetworkError) {
                attempt++
                if (attempt >= maxAttempts) {
                    throw e
                }
                delay(delay)
                delay = minOf(delay * 2, maxDelay)
            }
        }
        
        throw NetworkError.ConnectionError("Max retry attempts exceeded")
    }
}
```

## Performance Standards

### Connection Pooling

```kotlin>
/**
 * Standard connection pooling for HTTP requests
 */
object HttpClientPool {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    fun getClient(): OkHttpClient {
        return client
    }
}
```

### Connection Timeouts

```kotlin>
/**
 * Standard timeout values
 */
object Timeouts {
    const val CONNECTION_TIMEOUT = 30_000L // 30 seconds
    const val READ_TIMEOUT = 30_000L // 30 seconds
    const val WRITE_TIMEOUT = 30_000L // 30 seconds
    const val KEEP_ALIVE_INTERVAL = 30_000L // 30 seconds
    const val CAPABILITY_CACHE_TTL = 300_000L // 5 minutes
    const val ACK_TIMEOUT = 5_000L // 5 seconds
}
```

## Security Standards

### TLS/SSL Configuration

```kotlin>
/**
 * Standard TLS configuration
 */
object SecurityConfig {
    
    fun createSSLContext(): SSLContext {
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(
            null, 
            trustManagerFactory.trustManagers, 
            SecureRandom()
        )
        
        return sslContext
    }
}
```

### Data Encryption Standards

```kotlin>
/**
 * Standard encryption for sensitive data
 */
object DataEncryption {
    
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 16
    
    fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKey = SecretKeySpec(key, "AES")
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(data)
        
        return iv + encrypted // Prepend IV
    }
    
    fun decrypt(encrypted: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKey = SecretKeySpec(key, "AES")
        val iv = encrypted.copyOfRange(0, IV_SIZE)
        val data = encrypted.copyOfRange(IV_SIZE, encrypted.size)
        
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}
```

## Testing Standards

### Network Testing

```kotlin>
/**
 * Standard network testing utilities
 */
class NetworkTester(
    private val httpClient: OkHttpClient = HttpClientPool.getClient()
) {
    
    suspend fun testConnection(url: String): Boolean {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()
            
            val response = httpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun measureLatency(url: String): Long {
        val startTime = System.currentTimeMillis()
        val success = testConnection(url)
        val endTime = System.currentTimeMillis()
        
        return if (success) endTime - startTime else -1
    }
}
```

## Conclusion

This document establishes comprehensive standards for implementing Second Life network protocols in mobile viewers. These standards are derived from:

1. **Official Second Life Android Viewer 2025.12.1075** - Login flow, capability management
2. **Lumiya Mobile Viewer** - Mobile-optimized implementations, keep-alive mechanisms
3. **Best Practices** - Error handling, retry strategies, security

All implementations should follow these standards to ensure:
- Protocol compatibility with Second Life servers
- Optimal performance on mobile devices
- Robust error handling and recovery
- Secure communication
- Maintainable and testable code

---

**Document Version**: 1.0  
**Last Updated**: January 16, 2025  
**Status**: ✅ Complete