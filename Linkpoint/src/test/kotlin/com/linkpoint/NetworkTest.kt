package com.linkpoint

import com.linkpoint.network.SecondLifeConnection
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Second Life network connection
 */
class NetworkTest {
    
    private lateinit var connection: SecondLifeConnection
    
    @Before
    fun setup() {
        connection = SecondLifeConnection()
    }
    
    @Test
    fun `test grid URLs are defined`() {
        assertTrue("Should have Second Life grid", 
            SecondLifeConnection.GRIDS.containsKey("Second Life"))
        assertTrue("Should have OSGrid", 
            SecondLifeConnection.GRIDS.containsKey("OSGrid"))
        assertTrue("Should have Second Life Beta", 
            SecondLifeConnection.GRIDS.containsKey("Second Life Beta"))
    }
    
    @Test
    fun `test initial state is disconnected`() {
        assertFalse("Should not be connected initially", connection.isConnected())
        assertNull("Session ID should be null", connection.getSessionId())
        assertNull("Agent ID should be null", connection.getAgentId())
    }
    
    @Test
    fun `test sim port initial value`() {
        assertEquals("Sim port should be 0 initially", 0, connection.getSimPort())
    }
    
    @Test
    fun `test grid count`() {
        assertTrue("Should have at least 3 grids", SecondLifeConnection.GRIDS.size >= 3)
    }
    
    @Test
    fun `test grid URLs are valid`() {
        SecondLifeConnection.GRIDS.forEach { (name, url) ->
            assertTrue("$name URL should not be empty", url.isNotEmpty())
            assertTrue("$name URL should start with http", 
                url.startsWith("http://") || url.startsWith("https://"))
        }
    }
}

/**
 * Unit tests for utility functions
 */
class UtilityTest {
    
    @Test
    fun `test version code regex pattern`() {
        val content = """
            defaultConfig {
                versionCode = 42
                versionName = "1.0.0"
            }
        """.trimIndent()
        
        val regex = Regex("""versionCode\s*=\s*(\d+)""")
        val match = regex.find(content)
        
        assertNotNull("Should find versionCode", match)
        assertEquals("Should extract correct version", "42", match?.groupValues?.get(1))
    }
    
    @Test
    fun `test version name regex pattern`() {
        val content = """
            defaultConfig {
                versionCode = 1
                versionName = "1.2.3"
            }
        """.trimIndent()
        
        val regex = Regex("""versionName\s*=\s*"([^"]+)"""")
        val match = regex.find(content)
        
        assertNotNull("Should find versionName", match)
        assertEquals("Should extract correct version", "1.2.3", match?.groupValues?.get(1))
    }
}
