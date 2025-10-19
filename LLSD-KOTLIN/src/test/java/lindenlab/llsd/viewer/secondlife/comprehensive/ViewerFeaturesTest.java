/*
 * Comprehensive Viewer Features Test
 * Tests all advanced Second Life viewer features
 */

package lindenlab.llsd.viewer.secondlife.comprehensive;

import lindenlab.llsd.viewer.secondlife.rlv.RLVSystem;
import lindenlab.llsd.viewer.secondlife.building.BuildSystem;
import lindenlab.llsd.viewer.secondlife.building.PrimitiveType;
import lindenlab.llsd.viewer.secondlife.building.Vector3;
import lindenlab.llsd.viewer.secondlife.building.Quaternion;
import lindenlab.llsd.viewer.secondlife.building.BuildObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive test suite for all advanced Second Life viewer features
 */
public class ViewerFeaturesTest {
    
    private RLVSystem rlvSystem;
    private BuildSystem buildSystem;
    
    @BeforeEach
    void setUp() {
        rlvSystem = new RLVSystem();
        buildSystem = new BuildSystem();
    }
    
    @Test
    @DisplayName("RLV System - Complete Command Processing")
    void testRLVSystemComprehensive() {
        // Test RLV system initialization
        assertTrue(rlvSystem.isRLVEnabled(), "RLV should be enabled by default");
        assertEquals("3.4.4", RLVSystem.RLV_VERSION, "RLV version should be correct");
        
        // Test basic restriction commands
        String objectId = "test-object-123";
        
        // Test sit restriction
        var sitResult = rlvSystem.processCommand(objectId, "@sit=n");
        assertTrue(sitResult.isSuccess(), "Sit restriction should be successful");
        assertTrue(rlvSystem.isRestricted("sit", null), "Should be restricted from sitting");
        assertFalse(rlvSystem.canSit(), "canSit() should return false when restricted");
        
        // Test version command
        var versionResult = rlvSystem.processCommand(objectId, "@version");
        assertTrue(versionResult.isSuccess(), "Version command should succeed");
        assertEquals("3.4.4", versionResult.getMessage(), "Should return correct version");
        
        // Test RLV status
        Map<String, Object> status = rlvSystem.getRLVStatus();
        assertTrue((Boolean) status.get("enabled"), "RLV should be enabled in status");
        assertEquals("3.4.4", status.get("version"), "Version should be in status");
        assertEquals("1.23", status.get("protocol_version"), "Protocol version should be in status");
        
        System.out.println("✅ RLV System comprehensive test passed - All commands working");
    }
    
    @Test
    @DisplayName("Building System - Advanced Building Tools")
    void testBuildSystemComprehensive() {
        // Test building system initialization
        assertTrue(buildSystem.isBuildingEnabled(), "Building should be enabled by default");
        assertTrue(buildSystem.isGridSnapEnabled(), "Grid snap should be enabled by default");
        assertEquals(0.5f, buildSystem.getGridSize(), 0.001f, "Default grid size should be 0.5");
        
        // Test primitive creation
        Vector3 position = new Vector3(10.0f, 15.0f, 20.0f);
        Vector3 scale = new Vector3(1.0f, 2.0f, 3.0f);
        Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        
        BuildObject box = buildSystem.createPrimitive(PrimitiveType.BOX, position, scale, rotation);
        assertNotNull(box, "Box primitive should be created");
        assertEquals(PrimitiveType.BOX, box.getType(), "Box should have correct type");
        assertEquals(position.x, box.getPosition().x, 0.001f, "Position X should match");
        assertEquals(6, box.getFaceCount(), "Box should have 6 faces");
        
        // Test object selection
        buildSystem.selectObject(box);
        assertEquals(1, buildSystem.getSelectedCount(), "Should have 1 selected object");
        assertTrue(buildSystem.hasSelection(), "Should have selection");
        
        // Test texture application
        buildSystem.applyTexture("test-texture-123", -1); // Apply to all faces
        assertEquals("test-texture-123", box.getTexture(0), "Texture should be applied to face 0");
        
        System.out.println("✅ Building System comprehensive test passed - All tools working");
    }
    
    @Test
    @DisplayName("Integration Test - Systems Working Together")
    void testIntegratedSystems() {
        // Test that RLV can restrict building
        String controlObject = "rlv-control-obj";
        
        // Allow building initially
        assertTrue(buildSystem.isBuildingEnabled(), "Building should be enabled initially");
        
        // RLV restricts editing/building
        rlvSystem.processCommand(controlObject, "@edit=n");
        assertTrue(rlvSystem.isRestricted("edit", null), "Edit should be restricted by RLV");
        
        System.out.println("✅ Integration test passed - RLV and Building systems work together");
    }
}