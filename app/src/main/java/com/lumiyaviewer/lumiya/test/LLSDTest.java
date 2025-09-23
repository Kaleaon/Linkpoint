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
     * Demonstrate basic LLSD operations with proper error handling
     */
    public static void demonstrateBasicLLSD() {
        LOGGER.info("Starting basic LLSD operations demonstration");
        
        try {
            // Create LLSD using builder pattern
            LLSD document = EnhancedLLSDUtils.builder()
                .put("name", "Test Agent")
                .put("id", UUID.randomUUID())
                .put("active", true)
                .put("score", 98.5)
                .put("items", Arrays.asList(1, 2, 3))
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
     * Demonstrate enhanced AgentDataUpdate with LLSD integration
     */
    public static void demonstrateAgentDataUpdate() {
        LOGGER.info("Starting AgentDataUpdate demonstration");
        
        try {
            AgentDataUpdate update = new AgentDataUpdate();
            
            // Set values using modern LLSD API
            UUID agentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            
            update.setAgentID(agentId);
            update.setActiveGroupID(groupId);
            update.setFirstName("John");
            update.setLastName("Doe");
            update.setGroupTitle("Member");
            update.setGroupName("Test Group");
            update.setGroupPowers(42);
            
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
     * Demonstrate enhanced ObjectLink with LLSD integration
     */
    public static void demonstrateObjectLink() {
        LOGGER.info("Starting ObjectLink demonstration");
        
        try {
            ObjectLink link = new ObjectLink();
            
            // Set agent data
            link.setAgentID(UUID.randomUUID());
            link.setSessionID(UUID.randomUUID());
            
            // Add object IDs
            link.addObjectLocalID(101);
            link.addObjectLocalID(102);
            link.addObjectLocalID(103);
            
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
     * Demonstrate advanced LLSD features with error handling
     */
    public static void demonstrateAdvancedFeatures() {
        LOGGER.info("Starting advanced LLSD features demonstration");
        
        try {
            // Create SL message templates
            LLSD chatMessage = EnhancedLLSDUtils.SLMessageTemplates.createChatMessage(
                UUID.randomUUID(),
                "TestBot",
                "Hello Second Life!",
                0, // Say
                0, // Public channel
                new double[]{128.0, 128.0, 23.0}
            );
            
            LOGGER.info("Created chat message template");
            String chatPretty = EnhancedLLSDUtils.prettyPrint(chatMessage);
            LOGGER.fine("Chat Message LLSD: " + chatPretty);
            
            // Create teleport message
            LLSD teleportMessage = EnhancedLLSDUtils.SLMessageTemplates.createTeleportMessage(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Sandbox",
                new double[]{128.0, 128.0, 23.0},
                new double[]{1.0, 0.0, 0.0}
            );
            
            LOGGER.info("Created teleport message template");
            String teleportPretty = EnhancedLLSDUtils.prettyPrint(teleportMessage);
            LOGGER.fine("Teleport Message LLSD: " + teleportPretty);
            
            // Demonstrate merging
            LLSD base = EnhancedLLSDUtils.builder()
                .put("name", "Default")
                .put("version", 1)
                .build();
                
            LLSD overlay = EnhancedLLSDUtils.builder()
                .put("name", "Custom")
                .put("enabled", true)
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
     * Demonstrate performance features with metrics
     */
    public static void demonstratePerformanceFeatures() {
        LOGGER.info("Starting performance features demonstration");
        
        try {
            // Demonstrate caching
            LLSD cached1 = EnhancedLLSDUtils.builder()
                .put("test", "value")
                .buildCached("test-key");
                
            LLSD cached2 = EnhancedLLSDUtils.builder()
                .put("test", "value")  
                .buildCached("test-key");  // Should hit cache
                
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