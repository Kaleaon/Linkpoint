package com.linkpoint.connection

import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.io.File
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.DatagramChannel
import java.security.MessageDigest
import javax.net.ssl.HttpsURLConnection
import kotlin.system.measureTimeMillis

/**
 * Integration tests for real Second Life connections
 * 
 * IMPORTANT: These tests require valid credentials and network access.
 * They will be SKIPPED if credentials are not available.
 * 
 * To run these tests:
 * 1. Create a file: Linkpoint/test-credentials.properties
 * 2. Add these lines:
 *    firstName=YourFirstName
 *    lastName=YourLastName
 *    password=YourPassword
 *    grid=main  (or "beta" for Aditi)
 * 
 * Then run: ./gradlew :Linkpoint:test --tests "com.linkpoint.connection.ConnectionIntegrationTest"
 * 
 * NOTE: test-credentials.properties is gitignored and should NEVER be committed!
 */
class ConnectionIntegrationTest {

    companion object {
        // Grid endpoints
        const val MAIN_GRID_LOGIN = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        const val BETA_GRID_LOGIN = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Test parameters
        const val CONNECTION_TIMEOUT_MS = 30000
        const val READ_TIMEOUT_MS = 60000
        
        private const val TAG = "ConnectionIntegrationTest"
    }

    private var firstName: String = ""
    private var lastName: String = ""
    private var password: String = ""
    private var loginUri: String = ""
    private var credentialsAvailable = false

    @Before
    fun loadCredentials() {
        // Try multiple locations for credentials file
        val homeDir = System.getProperty("user.home") ?: ""
        val possiblePaths = listOf(
            "test-credentials.properties",
            "Linkpoint/test-credentials.properties",
            "../test-credentials.properties",
            "$homeDir/.linkpoint/test-credentials.properties"
        )
        
        var propsFile: File? = null
        for (path in possiblePaths) {
            val file = File(path)
            if (file.exists()) {
                propsFile = file
                break
            }
        }
        
        if (propsFile != null) {
            try {
                val props = java.util.Properties()
                propsFile.inputStream().use { props.load(it) }
                
                firstName = props.getProperty("firstName", "")
                lastName = props.getProperty("lastName", "")
                password = props.getProperty("password", "")
                val grid = props.getProperty("grid", "main")
                
                loginUri = if (grid.lowercase() == "beta") BETA_GRID_LOGIN else MAIN_GRID_LOGIN
                
                credentialsAvailable = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank()
                
                if (credentialsAvailable) {
                    println("✅ Credentials loaded for $firstName $lastName on ${if (grid == "beta") "Beta Grid" else "Main Grid"}")
                }
            } catch (e: Exception) {
                println("⚠️ Failed to load credentials: ${e.message}")
            }
        }
        
        if (!credentialsAvailable) {
            println("""
                ╔════════════════════════════════════════════════════════════════╗
                ║ Integration tests SKIPPED - No credentials found               ║
                ╠════════════════════════════════════════════════════════════════╣
                ║ To enable these tests, create: test-credentials.properties     ║
                ║ with contents:                                                 ║
                ║   firstName=YourFirstName                                      ║
                ║   lastName=YourLastName                                        ║
                ║   password=YourPassword                                        ║
                ║   grid=main                                                    ║
                ╚════════════════════════════════════════════════════════════════╝
            """.trimIndent())
        }
    }

    // ============================================================
    // Server Reachability Tests (no credentials needed)
    // ============================================================

    @Test
    fun `test Main Grid login server is reachable`() {
        val result = testServerReachability(MAIN_GRID_LOGIN)
        println("Main Grid Reachability: $result")
        Assume.assumeTrue(
            "Main Grid login server is unreachable in this environment: ${result.errorMessage}",
            result.isReachable
        )
    }

    @Test
    fun `test Beta Grid login server is reachable`() {
        val result = testServerReachability(BETA_GRID_LOGIN)
        println("Beta Grid Reachability: $result")
        // Beta grid may not always be available, so just log
        if (!result.isReachable) {
            println("⚠️ Beta Grid is not reachable (this is sometimes expected): ${result.errorMessage}")
        }
    }

    @Test
    fun `test DNS resolution for Second Life domains`() {
        val domains = listOf(
            "login.agni.lindenlab.com",
            "secondlife.com",
            "status.secondlifegrid.net"
        )
        
        for (domain in domains) {
            try {
                val startTime = System.currentTimeMillis()
                val address = InetAddress.getByName(domain)
                val dnsTime = System.currentTimeMillis() - startTime
                println("✅ DNS: $domain → ${address.hostAddress} (${dnsTime}ms)")
            } catch (e: Exception) {
                println("❌ DNS FAILED: $domain - ${e.message}")
            }
        }
    }

    @Test
    fun `test HTTP latency to login server`() {
        val measurements = mutableListOf<Long>()
        
        repeat(5) { i ->
            val latency = measureHttpLatency(MAIN_GRID_LOGIN)
            if (latency > 0) {
                measurements.add(latency)
                println("Latency test ${i + 1}: ${latency}ms")
            }
            Thread.sleep(500) // Small delay between tests
        }
        
        if (measurements.isNotEmpty()) {
            val avg = measurements.average().toLong()
            val min = measurements.minOrNull() ?: 0
            val max = measurements.maxOrNull() ?: 0
            println("\n📊 HTTP Latency Summary:")
            println("   Average: ${avg}ms")
            println("   Min: ${min}ms")
            println("   Max: ${max}ms")
            println("   Quality: ${classifyLatency(avg.toInt())}")
        }
    }

    // ============================================================
    // Login Tests (require credentials)
    // ============================================================

    @Test
    fun `test login request format is valid`() {
        Assume.assumeTrue("Credentials not available", credentialsAvailable)
        
        // Build the login XML
        val passwordHash = createPasswordHash(password)
        val xml = buildLoginXml(firstName, lastName, passwordHash)
        
        println("Login XML Request (password redacted):")
        println(xml.replace(passwordHash, "***HASH***"))
        
        // Validate XML structure
        assert(xml.contains("<methodCall>")) { "Should have methodCall tag" }
        assert(xml.contains("<methodName>login_to_simulator</methodName>")) { "Should have method name" }
        assert(xml.contains("<name>first</name>")) { "Should have first name param" }
        assert(xml.contains("<name>last</name>")) { "Should have last name param" }
        assert(xml.contains("<name>passwd</name>")) { "Should have passwd param" }
    }

    @Test
    fun `test actual login to grid`() {
        Assume.assumeTrue("Credentials not available", credentialsAvailable)
        
        println("\n╔═══════════════════════════════════════════════════════════════╗")
        println("║ REAL LOGIN TEST - Connecting to Second Life                   ║")
        println("╚═══════════════════════════════════════════════════════════════╝\n")
        
        val passwordHash = createPasswordHash(password)
        val xml = buildLoginXml(firstName, lastName, passwordHash)
        
        val startTime = System.currentTimeMillis()
        val result = performLogin(loginUri, xml)
        val duration = System.currentTimeMillis() - startTime
        
        println("\n📋 Login Result:")
        println("   Duration: ${duration}ms")
        println("   Success: ${result.success}")
        
        if (result.success) {
            println("   Agent ID: ${result.agentId}")
            println("   Session ID: ***REDACTED***")
            println("   Sim IP: ${result.simIp}")
            println("   Sim Port: ${result.simPort}")
            println("   Region: ${result.regionName}")
            println("   Circuit Code: ${result.circuitCode}")
            
            // If login succeeded, try UDP connection
            if (result.simIp.isNotEmpty() && result.simPort > 0 && result.circuitCode > 0) {
                println("\n🔌 Testing UDP connection to simulator...")
                testUdpConnection(result.simIp, result.simPort, result.circuitCode, result.sessionId, result.agentId)
            }
        } else {
            println("   Error: ${result.errorMessage}")
            if (result.errorMessage.contains("mfa", ignoreCase = true)) {
                println("   ⚠️ MFA required - this account has 2FA enabled")
            }
        }
        
        assert(result.success || result.errorMessage.contains("mfa", ignoreCase = true)) {
            "Login should succeed or require MFA: ${result.errorMessage}"
        }
    }

    // ============================================================
    // UDP Connection Tests (require successful login)
    // ============================================================

    private fun testUdpConnection(simIp: String, simPort: Int, circuitCode: Int, sessionId: String, agentId: String) {
        try {
            val channel = DatagramChannel.open()
            channel.configureBlocking(false)
            
            // Bind to any available local port
            channel.bind(InetSocketAddress(0))
            val localPort = (channel.localAddress as InetSocketAddress).port
            println("   Local UDP port: $localPort")
            
            // Connect to simulator
            val simAddress = InetSocketAddress(InetAddress.getByName(simIp), simPort)
            channel.connect(simAddress)
            println("   Connected to: $simIp:$simPort")
            
            // Send UseCircuitCode
            val useCircuitCodePacket = buildUseCircuitCodePacket(circuitCode, sessionId, agentId)
            val sendBuffer = ByteBuffer.wrap(useCircuitCodePacket)
            channel.write(sendBuffer)
            println("   Sent UseCircuitCode packet (${useCircuitCodePacket.size} bytes)")
            
            // Wait for response
            val receiveBuffer = ByteBuffer.allocate(4096)
            val startTime = System.currentTimeMillis()
            var received = false
            
            while (System.currentTimeMillis() - startTime < 5000) { // 5 second timeout
                receiveBuffer.clear()
                val bytesRead = channel.read(receiveBuffer)
                
                if (bytesRead > 0) {
                    received = true
                    receiveBuffer.flip()
                    val packetData = ByteArray(bytesRead)
                    receiveBuffer.get(packetData)
                    
                    val messageId = extractMessageId(packetData)
                    println("   ✅ Received response: $bytesRead bytes, MessageID: $messageId")
                    break
                }
                
                Thread.sleep(100)
            }
            
            if (!received) {
                println("   ⚠️ No UDP response received within 5 seconds")
                println("   This may indicate NAT/firewall issues blocking UDP")
            }
            
            channel.close()
            
        } catch (e: Exception) {
            println("   ❌ UDP connection failed: ${e.message}")
        }
    }

    // ============================================================
    // Helper Functions
    // ============================================================

    data class ReachabilityResult(
        val isReachable: Boolean,
        val latencyMs: Long,
        val errorMessage: String = ""
    )

    private fun testServerReachability(urlString: String): ReachabilityResult {
        return try {
            val url = URL(urlString)
            val startTime = System.currentTimeMillis()
            
            val connection = url.openConnection() as HttpsURLConnection
            connection.connectTimeout = CONNECTION_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.requestMethod = "HEAD"
            
            val responseCode = connection.responseCode
            val latency = System.currentTimeMillis() - startTime
            
            connection.disconnect()
            
            ReachabilityResult(
                isReachable = responseCode in 200..499, // Any response means server is up
                latencyMs = latency
            )
        } catch (e: Exception) {
            ReachabilityResult(
                isReachable = false,
                latencyMs = -1,
                errorMessage = "${e.javaClass.simpleName}: ${e.message}"
            )
        }
    }

    private fun measureHttpLatency(urlString: String): Long {
        return try {
            val url = URL(urlString)
            val startTime = System.currentTimeMillis()
            
            val connection = url.openConnection() as HttpsURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "HEAD"
            
            connection.responseCode
            val latency = System.currentTimeMillis() - startTime
            
            connection.disconnect()
            latency
        } catch (e: Exception) {
            -1
        }
    }

    private fun classifyLatency(latencyMs: Int): String {
        return when {
            latencyMs < 50 -> "EXCELLENT 🟢"
            latencyMs < 150 -> "GOOD 🟡"
            latencyMs < 300 -> "FAIR 🟠"
            else -> "POOR 🔴"
        }
    }

    private fun createPasswordHash(password: String): String {
        // Second Life requires password to be truncated to 16 chars, then MD5 hashed
        val truncated = password.trim().take(16)
        val md5 = MessageDigest.getInstance("MD5")
        val hash = md5.digest(truncated.toByteArray())
        val hexHash = hash.joinToString("") { "%02x".format(it) }
        return "\$1\$$hexHash"
    }

    private fun buildLoginXml(firstName: String, lastName: String, passwordHash: String): String {
        return """<?xml version="1.0"?>
<methodCall>
<methodName>login_to_simulator</methodName>
<params>
<param>
<value>
<struct>
<member><name>first</name><value><string>$firstName</string></value></member>
<member><name>last</name><value><string>$lastName</string></value></member>
<member><name>passwd</name><value><string>$passwordHash</string></value></member>
<member><name>start</name><value><string>last</string></value></member>
<member><name>channel</name><value><string>Linkpoint Test</string></value></member>
<member><name>version</name><value><string>1.0.0</string></value></member>
<member><name>platform</name><value><string>Lin</string></value></member>
<member><name>agree_to_tos</name><value><boolean>1</boolean></value></member>
<member><name>read_critical</name><value><boolean>1</boolean></value></member>
<member><name>viewer_digest</name><value><string></string></value></member>
<member><name>options</name><value><array><data>
<value><string>inventory-root</string></value>
<value><string>inventory-skeleton</string></value>
<value><string>inventory-lib-root</string></value>
<value><string>inventory-lib-owner</string></value>
<value><string>inventory-skel-lib</string></value>
<value><string>initial-outfit</string></value>
<value><string>gestures</string></value>
<value><string>event_categories</string></value>
<value><string>event_notifications</string></value>
<value><string>classified_categories</string></value>
<value><string>buddy-list</string></value>
<value><string>ui-config</string></value>
<value><string>login-flags</string></value>
<value><string>global-textures</string></value>
</data></array></value></member>
</struct>
</value>
</param>
</params>
</methodCall>"""
    }

    data class LoginResult(
        val success: Boolean,
        val agentId: String = "",
        val sessionId: String = "",
        val simIp: String = "",
        val simPort: Int = 0,
        val circuitCode: Int = 0,
        val regionName: String = "",
        val errorMessage: String = ""
    )

    private fun performLogin(loginUri: String, xml: String): LoginResult {
        return try {
            val url = URL(loginUri)
            val connection = url.openConnection() as HttpsURLConnection
            connection.connectTimeout = CONNECTION_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "text/xml")
            connection.doOutput = true
            
            connection.outputStream.use { it.write(xml.toByteArray()) }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            parseLoginResponse(response)
        } catch (e: Exception) {
            LoginResult(success = false, errorMessage = "${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun parseLoginResponse(response: String): LoginResult {
        // Simple XML parsing for test purposes
        fun extractValue(tag: String): String {
            val pattern = "<name>$tag</name>\\s*<value>\\s*<[^>]+>([^<]*)</".toRegex()
            return pattern.find(response)?.groupValues?.get(1) ?: ""
        }
        
        val loginSuccess = extractValue("login")
        
        return if (loginSuccess == "true") {
            LoginResult(
                success = true,
                agentId = extractValue("agent_id"),
                sessionId = extractValue("session_id"),
                simIp = extractValue("sim_ip"),
                simPort = extractValue("sim_port").toIntOrNull() ?: 0,
                circuitCode = extractValue("circuit_code").toIntOrNull() ?: 0,
                regionName = extractValue("region_x") + "," + extractValue("region_y")
            )
        } else {
            LoginResult(
                success = false,
                errorMessage = extractValue("message").ifEmpty { extractValue("reason") }
            )
        }
    }

    private fun buildUseCircuitCodePacket(circuitCode: Int, sessionId: String, agentId: String): ByteArray {
        val buffer = ByteBuffer.allocate(64)
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        // Flags: Reliable
        buffer.put(0x40.toByte())
        
        // Sequence number: 0
        buffer.putInt(0)
        
        // Extra header: 0
        buffer.put(0)
        
        // Message ID: UseCircuitCode (0xFF 0xFF 0x00 0x03)
        buffer.put(0xFF.toByte())
        buffer.put(0xFF.toByte())
        buffer.put(0x00.toByte())
        buffer.put(0x03.toByte())
        
        // Circuit code
        buffer.putInt(circuitCode)
        
        // Session ID (16 bytes)
        val sessionUuid = try { 
            java.util.UUID.fromString(sessionId) 
        } catch (e: Exception) { 
            println("Warning: Failed to parse sessionId '$sessionId', using random UUID: ${e.message}")
            java.util.UUID.randomUUID() 
        }
        buffer.putLong(sessionUuid.mostSignificantBits)
        buffer.putLong(sessionUuid.leastSignificantBits)
        
        // Agent ID (16 bytes)
        val agentUuid = try { 
            java.util.UUID.fromString(agentId) 
        } catch (e: Exception) { 
            println("Warning: Failed to parse agentId '$agentId', using random UUID: ${e.message}")
            java.util.UUID.randomUUID() 
        }
        buffer.putLong(agentUuid.mostSignificantBits)
        buffer.putLong(agentUuid.leastSignificantBits)
        
        return buffer.array().copyOf(buffer.position())
    }

    private fun extractMessageId(packet: ByteArray): Int {
        if (packet.size < 8) return -1
        
        val buffer = ByteBuffer.wrap(packet)
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.get() // Skip flags
        buffer.int // Skip sequence
        buffer.get() // Skip extra header
        
        val firstByte = buffer.get().toInt() and 0xFF
        
        return if (firstByte == 0xFF && buffer.remaining() > 0) {
            val secondByte = buffer.get().toInt() and 0xFF
            if (secondByte == 0xFF && buffer.remaining() >= 2) {
                buffer.short.toInt()
            } else {
                (firstByte shl 8) or secondByte
            }
        } else {
            firstByte
        }
    }
}
