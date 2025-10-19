/*
 * Tests for Firestorm LLSD utilities
 * 
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.firestorm;

import lindenlab.llsd.LLSDException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Firestorm LLSD utilities.
 */
public class FirestormLLSDUtilsTest {

    @Nested
    @DisplayName("RLV Command Tests")
    class RLVCommandTests {

        @Test
        @DisplayName("Should create RLV command with valid parameters")
        void testRLVCommandCreation() {
            UUID sourceId = UUID.randomUUID();
            FirestormLLSDUtils.RLVCommand cmd = new FirestormLLSDUtils.RLVCommand(
                "detach", "chest", "=y", sourceId);
            
            assertEquals("detach", cmd.getCommand());
            assertEquals("chest", cmd.getParam());
            assertEquals("=y", cmd.getOption());
            assertEquals(sourceId, cmd.getSourceId());
        }

        @Test
        @DisplayName("Should reject null command")
        void testRLVCommandNullCommand() {
            UUID sourceId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () ->
                new FirestormLLSDUtils.RLVCommand(null, "param", "option", sourceId));
        }

        @Test
        @DisplayName("Should reject empty command")
        void testRLVCommandEmptyCommand() {
            UUID sourceId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () ->
                new FirestormLLSDUtils.RLVCommand("", "param", "option", sourceId));
        }

        @Test
        @DisplayName("Should reject null source ID")
        void testRLVCommandNullSourceId() {
            assertThrows(IllegalArgumentException.class, () ->
                new FirestormLLSDUtils.RLVCommand("command", "param", "option", null));
        }

        @Test
        @DisplayName("Should handle null parameters gracefully")
        void testRLVCommandNullParams() {
            UUID sourceId = UUID.randomUUID();
            FirestormLLSDUtils.RLVCommand cmd = new FirestormLLSDUtils.RLVCommand(
                "command", null, null, sourceId);
            
            assertEquals("command", cmd.getCommand());
            assertEquals("", cmd.getParam());
            assertEquals("", cmd.getOption());
        }

        @Test
        @DisplayName("Should convert to LLSD correctly")
        void testRLVCommandToLLSD() {
            UUID sourceId = UUID.randomUUID();
            FirestormLLSDUtils.RLVCommand cmd = new FirestormLLSDUtils.RLVCommand(
                "detach", "chest", "=y", sourceId);
            
            Map<String, Object> llsd = cmd.toLLSD();
            
            assertEquals("detach", llsd.get("Command"));
            assertEquals("chest", llsd.get("Parameter"));
            assertEquals("=y", llsd.get("Option"));
            assertEquals(sourceId.toString(), llsd.get("SourceID"));
            assertTrue(llsd.containsKey("Timestamp"));
        }

        @Test
        @DisplayName("Should parse from LLSD correctly")
        void testRLVCommandFromLLSD() throws LLSDException {
            UUID sourceId = UUID.randomUUID();
            Map<String, Object> llsdData = new HashMap<>();
            llsdData.put("Command", "detach");
            llsdData.put("Parameter", "chest");
            llsdData.put("Option", "=y");
            llsdData.put("SourceID", sourceId.toString());
            
            FirestormLLSDUtils.RLVCommand cmd = FirestormLLSDUtils.RLVCommand.fromLLSD(llsdData);
            
            assertEquals("detach", cmd.getCommand());
            assertEquals("chest", cmd.getParam());
            assertEquals("=y", cmd.getOption());
            assertEquals(sourceId, cmd.getSourceId());
        }

        @Test
        @DisplayName("Should reject invalid LLSD data")
        void testRLVCommandFromInvalidLLSD() {
            assertThrows(LLSDException.class, () ->
                FirestormLLSDUtils.RLVCommand.fromLLSD(null));
            
            assertThrows(LLSDException.class, () ->
                FirestormLLSDUtils.RLVCommand.fromLLSD("not a map"));
            
            Map<String, Object> incomplete = new HashMap<>();
            incomplete.put("Command", "test");
            // Missing SourceID
            assertThrows(LLSDException.class, () ->
                FirestormLLSDUtils.RLVCommand.fromLLSD(incomplete));
        }
    }

    @Nested
    @DisplayName("Radar Data Tests")
    class RadarDataTests {

        @Test
        @DisplayName("Should create radar data with valid parameters")
        void testCreateRadarData() {
            UUID agentId = UUID.randomUUID();
            double[] position = {100.5, 200.5, 50.0};
            
            Map<String, Object> radarData = FirestormLLSDUtils.createRadarData(
                agentId, "TestAgent", "test.user", position, 25.5, true, null);
            
            assertEquals(agentId.toString(), radarData.get("AgentID"));
            assertEquals("TestAgent", radarData.get("DisplayName"));
            assertEquals("test.user", radarData.get("UserName"));
            assertEquals(25.5, radarData.get("Distance"));
            assertTrue((Boolean) radarData.get("IsTyping"));
            assertTrue(radarData.containsKey("LastSeen"));
        }

        @Test
        @DisplayName("Should reject null agent ID")
        void testRadarDataNullAgentId() {
            assertThrows(IllegalArgumentException.class, () ->
                FirestormLLSDUtils.createRadarData(null, "name", "user", null, 0, false, null));
        }

        @Test
        @DisplayName("Should reject negative distance")
        void testRadarDataNegativeDistance() {
            UUID agentId = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () ->
                FirestormLLSDUtils.createRadarData(agentId, "name", "user", null, -1.0, false, null));
        }

        @Test
        @DisplayName("Should validate position array length")
        void testRadarDataInvalidPosition() {
            UUID agentId = UUID.randomUUID();
            double[] invalidPosition = {1.0, 2.0}; // Only 2 components
            
            assertThrows(IllegalArgumentException.class, () ->
                FirestormLLSDUtils.createRadarData(agentId, "name", "user", invalidPosition, 0, false, null));
        }
    }

    @Nested
    @DisplayName("Cache Tests")
    class CacheTests {

        @Test
        @DisplayName("Should create cache with valid max age")
        void testCacheCreation() {
            FirestormLLSDUtils.FSLLSDCache cache = new FirestormLLSDUtils.FSLLSDCache(1000);
            assertEquals(1000, cache.getMaxAge());
        }

        @Test
        @DisplayName("Should reject invalid max age")
        void testCacheInvalidMaxAge() {
            assertThrows(IllegalArgumentException.class, () ->
                new FirestormLLSDUtils.FSLLSDCache(0));
            
            assertThrows(IllegalArgumentException.class, () ->
                new FirestormLLSDUtils.FSLLSDCache(-1));
        }

        @Test
        @DisplayName("Should store and retrieve values")
        void testCachePutGet() {
            FirestormLLSDUtils.FSLLSDCache cache = new FirestormLLSDUtils.FSLLSDCache(10000);
            
            cache.put("key1", "value1");
            assertEquals("value1", cache.get("key1"));
        }

        @Test
        @DisplayName("Should reject null keys")
        void testCacheNullKey() {
            FirestormLLSDUtils.FSLLSDCache cache = new FirestormLLSDUtils.FSLLSDCache(1000);
            
            assertThrows(IllegalArgumentException.class, () ->
                cache.put(null, "value"));
            
            assertThrows(IllegalArgumentException.class, () ->
                cache.get(null));
        }

        @Test
        @DisplayName("Should expire old entries")
        void testCacheExpiration() throws InterruptedException {
            FirestormLLSDUtils.FSLLSDCache cache = new FirestormLLSDUtils.FSLLSDCache(50);
            
            cache.put("key1", "value1");
            assertEquals("value1", cache.get("key1"));
            
            Thread.sleep(100); // Wait for expiration
            
            assertNull(cache.get("key1"));
        }
    }
}