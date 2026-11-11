package com.lumiyaviewer.lumiya.test;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDUtils;
import com.lumiyaviewer.lumiya.slproto.llsd.EnhancedLLSDUtils;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectLink;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Demo class showcasing Kaleaon's LLSD-Java library integration
 * Demonstrates advanced LLSD features and Second Life message handling
 * 
 * This class provides comprehensive examples of LLSD usage with proper
 * error handling and logging practices.
 */
public class KaleaonLLSDDemo {
    
    private static final Logger LOGGER = Logger.getLogger(KaleaonLLSDDemo.class.getName());
    
    /**
     * Demonstrate basic LLSD operations with proper error handling using Second Life values
     */
    public static void demonstrateBasicLLSD() {
        LOGGER.info("Starting basic LLSD operations demonstration");
        
        try {
            // Create LLSD using builder pattern with Second Life avatar data
            // Using realistic SL UUID format and avatar properties
            LLSD document = EnhancedLLSDUtils.builder()
                .put("name", "Philip Linden")  // Second Life founder's avatar name
                .put("id", UUID.fromString("3d6181b0-6a4b-97ef-18d8-722652995cf1"))  // Example SL avatar UUID
                .put("active", true)
                .put("online_time", 3600.0)  // Online time in seconds
                .put("groups", Arrays.asList("Linden Lab", "Developers", "Builders"))  // SL groups
                .build();
            
            LOGGER.info("Created LLSD document successfully");
            String xmlRepresentation = EnhancedLLSDUtils.toXMLString(document);
            LOGGER.fine("LLSD XML: " + xmlRepresentation);
            
            // Safe value extraction
            String name = EnhancedLLSDUtils.safeGetString(document, "name", "Unknown");
            UUID id = EnhancedLLSDUtils.safeGetUUID(document, "id", null);
            boolean active = EnhancedLLSDUtils.safeGetBoolean(document, "active", false);
            
            LOGGER.info(String.format("Extracted values - Name: %s, ID: %s, Active: %s", 
                name, id, active));
                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to demonstrate basic LLSD operations", e);
        }
    }
    
    /**
     * Demonstrate enhanced AgentDataUpdate with LLSD integration using Second Life values
     */
    public static void demonstrateAgentDataUpdate() {
        LOGGER.info("Starting AgentDataUpdate demonstration");
        
        try {
            AgentDataUpdate update = new AgentDataUpdate();
            
            // Set values using realistic Second Life data
            // Using format matching actual SL avatar and group UUIDs
            UUID agentId = UUID.fromString("8955375f-7c44-4b6f-9e2f-dc2f66e9e6eb");  // Example SL avatar UUID
            UUID groupId = UUID.fromString("dfc8ff7c-9c2e-4da4-bed8-7be1087a84d6");  // Example SL group UUID
            
            update.setAgentID(agentId);
            update.setActiveGroupID(groupId);
            update.setFirstName("Philip");  // Second Life founder's first name
            update.setLastName("Linden");   // Second Life founder's last name  
            update.setGroupTitle("Founder"); // Realistic group title
            update.setGroupName("Linden Lab"); // Actual Second Life company/group
            update.setGroupPowers(8191);  // SL group powers bitmask (all powers = 2^13 - 1)
            
            LOGGER.info("Agent data configured successfully");
            String xmlData = update.toXMLString();
            LOGGER.fine("Agent Data LLSD XML: " + xmlData);
            
            // Validate required fields
            LLSD agentLLSD = update.getAgentDataLLSD();
            List<String> missing = EnhancedLLSDUtils.validateRequiredFields(
                agentLLSD, "AgentID", "FirstName", "LastName"
            );
            
            if (missing.isEmpty()) {
                LOGGER.info("✓ All required fields present in agent data");
            } else {
                LOGGER.warning("✗ Missing required fields: " + missing);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to demonstrate AgentDataUpdate", e);
        }
    }
    
    /**
     * Demonstrate enhanced ObjectLink with LLSD integration using Second Life values
     */
    public static void demonstrateObjectLink() {
        LOGGER.info("Starting ObjectLink demonstration");
        
        try {
            ObjectLink link = new ObjectLink();
            
            // Set agent data with realistic Second Life session UUIDs
            link.setAgentID(UUID.fromString("8955375f-7c44-4b6f-9e2f-dc2f66e9e6eb"));
            link.setSessionID(UUID.fromString("f6c58a8d-d6c6-4b1a-8db5-7e9f0a1b2c3d"));  // SL session UUID format
            
            // Add realistic object local IDs (SL objects typically use positive integers)
            // These would be local IDs for prims in a region
            link.addObjectLocalID(2147483647);  // Max int32 local ID
            link.addObjectLocalID(1048576);     // 2^20 typical range
            link.addObjectLocalID(524288);      // 2^19 typical range
            
            LOGGER.info("Object link configured with 3 objects");
            
            String jsonData = link.toJSONString();
            String notationData = link.toNotationString();
            boolean isValid = link.isValid();
            
            LOGGER.fine("Object Link JSON: " + jsonData);
            LOGGER.fine("Object Link Notation: " + notationData);
            LOGGER.info("Validation result: " + (isValid ? "✓ Valid" : "✗ Invalid"));
            
            // Demonstrate object ID manipulation
            List<Integer> objectIDs = link.getObjectLocalIDs();
            LOGGER.info("Object IDs: " + objectIDs);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to demonstrate ObjectLink", e);
        }
    }
    
    /**
     * Demonstrate advanced LLSD features with error handling using Second Life values
     */
    public static void demonstrateAdvancedFeatures() {
        LOGGER.info("Starting advanced LLSD features demonstration");
        
        try {
            // Create SL chat message template with realistic values
            // Chat type 0 = Say (normal chat), Channel 0 = public channel
            // Position in region coordinates (typically 0-256 for x/y, variable for z)
            LLSD chatMessage = EnhancedLLSDUtils.SLMessageTemplates.createChatMessage(
                UUID.fromString("3d6181b0-6a4b-97ef-18d8-722652995cf1"),  // Philip Linden UUID
                "Philip Linden",
                "Welcome to Second Life!",  // Classic SL greeting
                0, // ChatType: 0=Whisper, 1=Say, 2=Shout
                0, // PUBLIC_CHANNEL (channel 0)
                new double[]{128.0, 128.0, 23.5}  // Center of region (256x256), typical avatar height
            );
            
            LOGGER.info("Created chat message template");
            String chatPretty = EnhancedLLSDUtils.prettyPrint(chatMessage);
            LOGGER.fine("Chat Message LLSD: " + chatPretty);
            
            // Create teleport message to actual Second Life region
            // Using coordinates for landing point in a typical SL region
            LLSD teleportMessage = EnhancedLLSDUtils.SLMessageTemplates.createTeleportMessage(
                UUID.fromString("8955375f-7c44-4b6f-9e2f-dc2f66e9e6eb"),  // Agent UUID
                UUID.fromString("f6c58a8d-d6c6-4b1a-8db5-7e9f0a1b2c3d"),  // Session UUID
                "Ahern",  // Classic Second Life welcome area
                new double[]{128.0, 128.0, 24.0},  // Landing point coordinates
                new double[]{1.0, 0.0, 0.0}  // Look direction (facing east)
            );
            
            LOGGER.info("Created teleport message template");
            String teleportPretty = EnhancedLLSDUtils.prettyPrint(teleportMessage);
            LOGGER.fine("Teleport Message LLSD: " + teleportPretty);
            
            // Demonstrate merging with Second Life configuration data
            LLSD base = EnhancedLLSDUtils.builder()
                .put("viewer_name", "Lumiya")
                .put("protocol_version", 2)
                .put("grid", "agni")  // SL main grid codename
                .build();
                
            LLSD overlay = EnhancedLLSDUtils.builder()
                .put("viewer_name", "Lumiya Modern")
                .put("webrtc_enabled", true)
                .put("grid", "agni")  // Keep main grid
                .build();
                
            LLSD merged = EnhancedLLSDUtils.merge(base, overlay);
            LOGGER.info("Successfully merged LLSD objects");
            String mergedPretty = EnhancedLLSDUtils.prettyPrint(merged);
            LOGGER.fine("Merged LLSD: " + mergedPretty);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to demonstrate advanced features", e);
        }
    }
    
    /**
     * Demonstrate performance features with metrics using Second Life data
     */
    public static void demonstratePerformanceFeatures() {
        LOGGER.info("Starting performance features demonstration");
        
        try {
            // Demonstrate caching with Second Life region data
            // Using actual SL region configuration format
            LLSD cached1 = EnhancedLLSDUtils.builder()
                .put("region_name", "Ahern")
                .put("region_size", 256)  // Standard SL region size (256x256 meters)
                .put("water_height", 20.0)  // Default SL water height
                .put("sun_hour", 12.0)  // Noon in SL time
                .buildCached("region-ahern");
                
            LLSD cached2 = EnhancedLLSDUtils.builder()
                .put("region_name", "Ahern")
                .put("region_size", 256)
                .put("water_height", 20.0)
                .put("sun_hour", 12.0)
                .buildCached("region-ahern");  // Should hit cache
                
            // Log cache statistics
            int cacheSize = EnhancedLLSDUtils.getCacheSize();
            long cacheHits = EnhancedLLSDUtils.getCacheHits();
            long parseOps = EnhancedLLSDUtils.getParseOperations();
            double hitRatio = EnhancedLLSDUtils.getCacheHitRatio();
            
            LOGGER.info(String.format("Cache Performance Metrics - Size: %d, Hits: %d, Operations: %d, Hit Ratio: %.2f%%",
                cacheSize, cacheHits, parseOps, hitRatio * 100));
                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to demonstrate performance features", e);
        }
    }
    
    /**
     * Run all demonstration methods with comprehensive error handling
     */
    public static void runAllDemos() {
        LOGGER.info("Starting comprehensive LLSD library demonstration");
        
        try {
            demonstrateBasicLLSD();
            demonstrateAgentDataUpdate();
            demonstrateObjectLink();
            demonstrateAdvancedFeatures();
            demonstratePerformanceFeatures();
            
            LOGGER.info("All demonstrations completed successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Demo execution failed with unexpected error", e);
        } finally {
            // Cleanup
            EnhancedLLSDUtils.clearCache();
            LOGGER.info("Demo cleanup completed");
        }
    }
}