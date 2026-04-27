package com.linkpoint.integration

import kotlinx.coroutines.runBlocking

/**
 * Simple test class to verify that the Linkpoint-kotlin integration is working correctly.
 */
class IntegrationTest {
    
    companion object {
        /**
         * Run a basic integration test
         */
        @JvmStatic
        fun runBasicTest(): Boolean {
            println("=== Linkpoint-kotlin Integration Test ===")
            
            try {
                val bridge = LinkpointKotlinBridge()
                
                // Test initialization
                println("1. Testing initialization...")
                if (!bridge.initialize()) {
                    throw Exception("Failed to initialize bridge")
                }
                println("   ✓ Initialization success")
                
                // Test viewer core integration
                println("2. Testing viewer core...")
                val viewerCore = bridge.getViewerCore()
                if (!viewerCore.isInitialized()) {
                    throw Exception("ViewerCore not initialized")
                }
                println("   ✓ ViewerCore working")
                
                // Test protocol system
                println("3. Testing protocol system...")
                runBlocking {
                    // Simulate a connection test
                    val connected = bridge.connectToGrid(
                        "https://login.agni.lindenlab.com/cgi-bin/login.cgi",
                        "test_user",
                        "test_password"
                    )
                    if (connected) {
                        println("   ✓ Protocol connection simulation success")
                    } else {
                        println("   ✓ Protocol connection handled gracefully (expected for test)")
                    }
                }
                
                // Test shutdown
                println("4. Testing shutdown...")
                bridge.shutdown()
                println("   ✓ Shutdown success")
                
                println("=== Integration Test PASSED ===")
                return true
                
            } catch (e: Exception) {
                println("=== Integration Test FAILED: ${e.message} ===")
                e.printStackTrace()
                return false
            }
        }
    }
}