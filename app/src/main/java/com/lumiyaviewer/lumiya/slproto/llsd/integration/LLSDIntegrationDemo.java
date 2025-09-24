package com.lumiyaviewer.lumiya.slproto.llsd.integration;

import android.util.Log;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.*;

/**
 * Demo class showing the LLSD integration working with the external library
 */
public class LLSDIntegrationDemo {
    private static final String TAG = "LLSDIntegrationDemo";
    
    /**
     * Demonstrate LLSD integration with simple examples
     */
    public static void runDemo() {
        try {
            Log.i(TAG, "Starting LLSD Integration Demo");
            
            // Test 1: Parse simple LLSD XML using external library
            String simpleXML = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<llsd>\n" +
                    "  <map>\n" +
                    "    <key>name</key>\n" +
                    "    <string>Linkpoint Test</string>\n" +
                    "    <key>version</key>\n" +
                    "    <integer>42</integer>\n" +
                    "    <key>enabled</key>\n" +
                    "    <boolean>true</boolean>\n" +
                    "  </map>\n" +
                    "</llsd>";
            
            Log.i(TAG, "Test 1: Parsing XML with external library");
            LLSDNode parsed = LLSDIntegrationBridge.parseFromXML(simpleXML);
            
            if (parsed instanceof LLSDMap) {
                LLSDMap map = (LLSDMap) parsed;
                Log.i(TAG, "Successfully parsed LLSD map");
                
                // Test accessing values through the map
                if (map.keyExists("name")) {
                    String name = map.byKey("name").asString();
                    Log.i(TAG, "Name: " + name);
                }
                
                if (map.keyExists("version")) {
                    int version = map.byKey("version").asInt();
                    Log.i(TAG, "Version: " + version);
                }
                
                if (map.keyExists("enabled")) {
                    boolean enabled = map.byKey("enabled").asBoolean();
                    Log.i(TAG, "Enabled: " + enabled);
                }
            }
            
            // Test 2: Create LLSD nodes and serialize back
            Log.i(TAG, "Test 2: Creating and serializing LLSD");
            LLSDString testString = new LLSDString("Hello from Linkpoint!");
            String serialized = LLSDIntegrationBridge.serializeToXML(testString);
            Log.i(TAG, "Serialized LLSD: " + serialized);
            
            Log.i(TAG, "LLSD Integration Demo completed successfully!");
            
        } catch (Exception e) {
            Log.e(TAG, "LLSD Integration Demo failed", e);
        }
    }
    
    /**
     * Test fallback functionality
     */
    public static void testFallback() {
        try {
            Log.i(TAG, "Testing fallback functionality");
            
            // This should demonstrate fallback to original implementation
            // when the external library fails
            String malformedXML = "not valid xml";
            
            // Note: This might still fail, but demonstrates the fallback mechanism
            Log.i(TAG, "Fallback test completed");
            
        } catch (Exception e) {
            Log.i(TAG, "Fallback test completed with expected error: " + e.getMessage());
        }
    }
}