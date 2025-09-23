package com.lumiyaviewer.lumiya.test;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDUtils;
import com.lumiyaviewer.lumiya.slproto.llsd.EnhancedLLSDUtils;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectLink;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;

/**
 * Demo class showcasing Kaleaon's LLSD-Java library integration
 * Demonstrates advanced LLSD features and Second Life message handling
 */
public class KaleaonLLSDDemo {
    
    public static void demonstrateBasicLLSD() {
        System.out.println("=== Basic LLSD Operations with Kaleaon's Library ===");
        
        // Create LLSD using builder pattern
        LLSD document = EnhancedLLSDUtils.builder()
            .put("name", "Test Agent")
            .put("id", UUID.randomUUID())
            .put("active", true)
            .put("score", 98.5)
            .put("items", Arrays.asList(1, 2, 3))
            .build();
        
        System.out.println("Created LLSD document:");
        System.out.println(EnhancedLLSDUtils.toXMLString(document));
        
        // Safe value extraction
        String name = EnhancedLLSDUtils.safeGetString(document, "name", "Unknown");
        UUID id = EnhancedLLSDUtils.safeGetUUID(document, "id", null);
        boolean active = EnhancedLLSDUtils.safeGetBoolean(document, "active", false);
        
        System.out.println("Extracted values: " + name + ", " + id + ", " + active);
    }
    
    public static void demonstrateAgentDataUpdate() {
        System.out.println("\n=== Enhanced AgentDataUpdate with LLSD ===");
        
        AgentDataUpdate update = new AgentDataUpdate();
        
        // Set values using modern LLSD API
        update.setAgentID(UUID.randomUUID());
        update.setActiveGroupID(UUID.randomUUID());
        update.setFirstName("John");
        update.setLastName("Doe");
        update.setGroupTitle("Member");
        update.setGroupName("Test Group");
        update.setGroupPowers(42);
        
        System.out.println("Agent Data as LLSD XML:");
        System.out.println(update.toXMLString());
        
        // Validate required fields
        LLSD agentLLSD = update.getAgentDataLLSD();
        List<String> missing = EnhancedLLSDUtils.validateRequiredFields(
            agentLLSD, "AgentID", "FirstName", "LastName"
        );
        
        if (missing.isEmpty()) {
            System.out.println("✓ All required fields present");
        } else {
            System.out.println("✗ Missing fields: " + missing);
        }
    }
    
    public static void demonstrateObjectLink() {
        System.out.println("\n=== Enhanced ObjectLink with LLSD ===");
        
        ObjectLink link = new ObjectLink();
        
        // Set agent data
        link.setAgentID(UUID.randomUUID());
        link.setSessionID(UUID.randomUUID());
        
        // Add object IDs
        link.addObjectLocalID(101);
        link.addObjectLocalID(102);
        link.addObjectLocalID(103);
        
        System.out.println("Object Link as LLSD XML:");
        System.out.println(link.toJSONString());
        
        System.out.println("Object Link as Notation:");
        System.out.println(link.toNotationString());
        
        System.out.println("Validation result: " + (link.isValid() ? "✓ Valid" : "✗ Invalid"));
        
        // Demonstrate object ID manipulation
        List<Integer> objectIDs = link.getObjectLocalIDs();
        System.out.println("Object IDs: " + objectIDs);
    }
    
    public static void demonstrateAdvancedFeatures() {
        System.out.println("\n=== Advanced LLSD Features ===");
        
        // Create SL message templates
        LLSD chatMessage = EnhancedLLSDUtils.SLMessageTemplates.createChatMessage(
            UUID.randomUUID(),
            "TestBot",
            "Hello Second Life!",
            0, // Say
            0, // Public channel
            new double[]{128.0, 128.0, 23.0}
        );
        
        System.out.println("Chat Message LLSD:");
        System.out.println(EnhancedLLSDUtils.prettyPrint(chatMessage));
        
        // Create teleport message
        LLSD teleportMessage = EnhancedLLSDUtils.SLMessageTemplates.createTeleportMessage(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Sandbox",
            new double[]{128.0, 128.0, 23.0},
            new double[]{1.0, 0.0, 0.0}
        );
        
        System.out.println("Teleport Message LLSD:");
        System.out.println(EnhancedLLSDUtils.prettyPrint(teleportMessage));
        
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
        System.out.println("Merged LLSD:");
        System.out.println(EnhancedLLSDUtils.prettyPrint(merged));
    }
    
    public static void demonstratePerformanceFeatures() {
        System.out.println("\n=== Performance Features ===");
        
        // Demonstrate caching
        LLSD cached1 = EnhancedLLSDUtils.builder()
            .put("test", "value")
            .buildCached("test-key");
            
        LLSD cached2 = EnhancedLLSDUtils.builder()
            .put("test", "value")  
            .buildCached("test-key");  // Should hit cache
            
        System.out.println("Cache statistics:");
        System.out.println("  Cache size: " + EnhancedLLSDUtils.getCacheSize());
        System.out.println("  Cache hits: " + EnhancedLLSDUtils.getCacheHits());
        System.out.println("  Parse operations: " + EnhancedLLSDUtils.getParseOperations());
        System.out.println("  Cache hit ratio: " + String.format("%.2f%%", 
            EnhancedLLSDUtils.getCacheHitRatio() * 100));
    }
    
    public static void runAllDemos() {
        try {
            demonstrateBasicLLSD();
            demonstrateAgentDataUpdate();
            demonstrateObjectLink();
            demonstrateAdvancedFeatures();
            demonstratePerformanceFeatures();
            
            System.out.println("\n=== All demos completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("Demo failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}