package com.lumiyaviewer.lumiya.ui.settings

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit tests for EmulatorManager class
 * 
 * Tests the Android emulator management functionality including
 * AVD parsing, configuration, and CLI integration.
 */
@RunWith(RobolectricTestRunner::class)
class EmulatorManagerTest {
    
    private lateinit var emulatorManager: EmulatorManager
    
    @Before
    fun setUp() {
        emulatorManager = EmulatorManager(RuntimeEnvironment.getApplication())
    }
    
    @Test
    fun testEmulatorDefaults() {
        // Test that default configuration values are sensible
        assertEquals("pixel_2", EmulatorManager.EmulatorDefaults.DEFAULT_DEVICE)
        assertEquals("34", EmulatorManager.EmulatorDefaults.DEFAULT_API)
        assertEquals("x86_64", EmulatorManager.EmulatorDefaults.DEFAULT_ABI)
        assertEquals("google_apis", EmulatorManager.EmulatorDefaults.DEFAULT_TAG)
        
        // Test supported arrays are not empty
        assertTrue("Should have supported API levels", 
                   EmulatorManager.EmulatorDefaults.SUPPORTED_APIS.isNotEmpty())
        assertTrue("Should have supported ABIs", 
                   EmulatorManager.EmulatorDefaults.SUPPORTED_ABIS.isNotEmpty())
        assertTrue("Should have supported tags", 
                   EmulatorManager.EmulatorDefaults.SUPPORTED_TAGS.isNotEmpty())
        assertTrue("Should have popular devices", 
                   EmulatorManager.EmulatorDefaults.POPULAR_DEVICES.isNotEmpty())
    }
    
    @Test
    fun testAVDInfoCreation() {
        // Test AVDInfo class functionality
        val avd = EmulatorManager.AVDInfo(
            "test-avd", "pixel_2", "34", "x86_64", "stopped")
        
        assertEquals("test-avd", avd.name)
        assertEquals("pixel_2", avd.device)
        assertEquals("34", avd.apiLevel)
        assertEquals("x86_64", avd.abi)
        assertEquals("stopped", avd.status)
        
        // Test toString formatting
        val formatted = avd.toString()
        assertTrue("Should contain AVD name", formatted.contains("test-avd"))
        assertTrue("Should contain API level", formatted.contains("API 34"))
        assertTrue("Should contain ABI", formatted.contains("x86_64"))
    }
    
    @Test
    fun testParseAVDListEmpty() {
        // Test parsing empty AVD list
        val emptyOutput = "Available Android Virtual Devices:\n"
        val avds = EmulatorManager.parseAVDList(emptyOutput)
        
        assertTrue("Empty list should return empty collection", avds.isEmpty())
    }
    
    @Test
    fun testFormatAVDSummaryEmpty() {
        // Test formatting empty AVD list
        val emptyList = emptyList<EmulatorManager.AVDInfo>()
        val summary = EmulatorManager.formatAVDSummary(emptyList)
        
        assertEquals("No AVDs found", summary)
    }
    
    @Test
    fun testEmulatorAvailabilityCheck() {
        // Test availability check logic
        // Note: This test may pass or fail depending on environment setup
        // In CI/test environments, emulator tools may not be available
        val isAvailable = emulatorManager.isAvailable()
        
        // Just verify the method doesn't throw exceptions
        // Actual availability depends on Android SDK installation
        assertNotNull("Availability check should return a result", isAvailable)
    }
    
    @Test 
    fun testSupportedConfigurationValidation() {
        // Test that all default configurations are in supported arrays
        val supportedApis = EmulatorManager.EmulatorDefaults.SUPPORTED_APIS
        val supportedAbis = EmulatorManager.EmulatorDefaults.SUPPORTED_ABIS
        val supportedTags = EmulatorManager.EmulatorDefaults.SUPPORTED_TAGS
        val popularDevices = EmulatorManager.EmulatorDefaults.POPULAR_DEVICES
        
        // Verify defaults are in supported lists
        assertTrue("Default API should be supported", 
                   supportedApis.contains(EmulatorManager.EmulatorDefaults.DEFAULT_API))
        assertTrue("Default ABI should be supported", 
                   supportedAbis.contains(EmulatorManager.EmulatorDefaults.DEFAULT_ABI))
        assertTrue("Default tag should be supported", 
                   supportedTags.contains(EmulatorManager.EmulatorDefaults.DEFAULT_TAG))
        assertTrue("Default device should be popular", 
                   popularDevices.contains(EmulatorManager.EmulatorDefaults.DEFAULT_DEVICE))
    }
}
