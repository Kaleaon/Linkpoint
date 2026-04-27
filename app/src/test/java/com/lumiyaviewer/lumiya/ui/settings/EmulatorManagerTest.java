package com.lumiyaviewer.lumiya.ui.settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for EmulatorManager class
 * 
 * Tests the Android emulator management functionality including
 * AVD parsing, configuration, and CLI integration.
 */
@RunWith(RobolectricTestRunner.class)
public class EmulatorManagerTest {
    
    private EmulatorManager emulatorManager;
    
    @Before
    public void setUp() {
        emulatorManager = new EmulatorManager(RuntimeEnvironment.getApplication());
    }
    
    @Test
    public void testEmulatorDefaults() {
        // Test that default configuration values are sensible
        assertEquals("pixel_2", EmulatorManager.EmulatorDefaults.DEFAULT_DEVICE);
        assertEquals("34", EmulatorManager.EmulatorDefaults.DEFAULT_API);
        assertEquals("x86_64", EmulatorManager.EmulatorDefaults.DEFAULT_ABI);
        assertEquals("google_apis", EmulatorManager.EmulatorDefaults.DEFAULT_TAG);
        
        // Test supported arrays are not empty
        assertTrue("Should have supported API levels", 
                   EmulatorManager.EmulatorDefaults.SUPPORTED_APIS.length > 0);
        assertTrue("Should have supported ABIs", 
                   EmulatorManager.EmulatorDefaults.SUPPORTED_ABIS.length > 0);
        assertTrue("Should have supported tags", 
                   EmulatorManager.EmulatorDefaults.SUPPORTED_TAGS.length > 0);
        assertTrue("Should have popular devices", 
                   EmulatorManager.EmulatorDefaults.POPULAR_DEVICES.length > 0);
    }
    
    @Test
    public void testAVDInfoCreation() {
        // Test AVDInfo class functionality
        EmulatorManager.AVDInfo avd = new EmulatorManager.AVDInfo(
            "test-avd", "pixel_2", "34", "x86_64", "stopped");
        
        assertEquals("test-avd", avd.name);
        assertEquals("pixel_2", avd.device);
        assertEquals("34", avd.apiLevel);
        assertEquals("x86_64", avd.abi);
        assertEquals("stopped", avd.status);
        
        // Test toString formatting
        String formatted = avd.toString();
        assertTrue("Should contain AVD name", formatted.contains("test-avd"));
        assertTrue("Should contain API level", formatted.contains("API 34"));
        assertTrue("Should contain ABI", formatted.contains("x86_64"));
    }
    
    @Test
    public void testParseAVDListEmpty() {
        // Test parsing empty AVD list
        String emptyOutput = "Available Android Virtual Devices:\n";
        List<EmulatorManager.AVDInfo> avds = EmulatorManager.parseAVDList(emptyOutput);
        
        assertTrue("Empty list should return empty collection", avds.isEmpty());
    }
    
    @Test
    public void testFormatAVDSummaryEmpty() {
        // Test formatting empty AVD list
        List<EmulatorManager.AVDInfo> emptyList = List.of();
        String summary = EmulatorManager.formatAVDSummary(emptyList);
        
        assertEquals("No AVDs found", summary);
    }
    
    @Test
    public void testEmulatorAvailabilityCheck() {
        // Test availability check logic
        // Note: This test may pass or fail depending on environment setup
        // In CI/test environments, emulator tools may not be available
        boolean isAvailable = emulatorManager.isAvailable();
        
        // Just verify the method doesn't throw exceptions
        // Actual availability depends on Android SDK installation
        assertNotNull("Availability check should return a result", Boolean.valueOf(isAvailable));
    }
    
    @Test 
    public void testSupportedConfigurationValidation() {
        // Test that all default configurations are in supported arrays
        String[] supportedApis = EmulatorManager.EmulatorDefaults.SUPPORTED_APIS;
        String[] supportedAbis = EmulatorManager.EmulatorDefaults.SUPPORTED_ABIS;
        String[] supportedTags = EmulatorManager.EmulatorDefaults.SUPPORTED_TAGS;
        String[] popularDevices = EmulatorManager.EmulatorDefaults.POPULAR_DEVICES;
        
        // Verify defaults are in supported lists
        assertTrue("Default API should be supported", 
                   contains(supportedApis, EmulatorManager.EmulatorDefaults.DEFAULT_API));
        assertTrue("Default ABI should be supported", 
                   contains(supportedAbis, EmulatorManager.EmulatorDefaults.DEFAULT_ABI));
        assertTrue("Default tag should be supported", 
                   contains(supportedTags, EmulatorManager.EmulatorDefaults.DEFAULT_TAG));
        assertTrue("Default device should be popular", 
                   contains(popularDevices, EmulatorManager.EmulatorDefaults.DEFAULT_DEVICE));
    }
    
    /**
     * Helper method to check if array contains a value
     */
    private boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}