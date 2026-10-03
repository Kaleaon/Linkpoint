package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.EnhancedLLSDUtils;
import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDUtils;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Enhanced AgentDataUpdate using Kaleaon's LLSD-Java library
 * Provides modern LLSD handling with better type safety and utilities
 */
public class AgentDataUpdate extends SLMessage {
    private LLSD agentDataLLSD;
    
    // Legacy compatibility field
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
        public UUID ActiveGroupID;
        public UUID AgentID;
        public byte[] FirstName;
        public byte[] GroupName;
        public long GroupPowers;
        public byte[] GroupTitle;
        public byte[] LastName;
    }

    public AgentDataUpdate() {
        this.zeroCoded = true;
        initializeDefaultLLSD();
    }
    
    private void initializeDefaultLLSD() {
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("AgentID", UUID.randomUUID());
        agentData.put("ActiveGroupID", UUID.randomUUID());
        agentData.put("FirstName", "");
        agentData.put("LastName", "");
        agentData.put("GroupTitle", "");
        agentData.put("GroupPowers", 0L);
        agentData.put("GroupName", "");
        
        this.agentDataLLSD = new LLSD(agentData);
    }
    
    // Modern LLSD-based API
    public LLSD getAgentDataLLSD() {
        return agentDataLLSD;
    }
    
    public void setAgentDataLLSD(LLSD agentData) {
        if (agentData != null) {
            this.agentDataLLSD = agentData;
            syncToLegacyFields();
        }
    }
    
    // Convenient getters using enhanced LLSD utilities with proper navigation
    public UUID getAgentID() {
        return EnhancedLLSDUtils.safeGetUUID(agentDataLLSD, "AgentID", null);
    }
    
    public void setAgentID(UUID agentID) {
        updateLLSDField("AgentID", agentID);
    }
    
    public UUID getActiveGroupID() {
        return EnhancedLLSDUtils.safeGetUUID(agentDataLLSD, "ActiveGroupID", null);
    }
    
    public void setActiveGroupID(UUID activeGroupID) {
        updateLLSDField("ActiveGroupID", activeGroupID);
    }
    
    public String getFirstName() {
        return EnhancedLLSDUtils.safeGetString(agentDataLLSD, "FirstName", "");
    }
    
    public void setFirstName(String firstName) {
        updateLLSDField("FirstName", firstName != null ? firstName : "");
    }
    
    public String getLastName() {
        return EnhancedLLSDUtils.safeGetString(agentDataLLSD, "LastName", "");
    }
    
    public void setLastName(String lastName) {
        updateLLSDField("LastName", lastName != null ? lastName : "");
    }
    
    public String getGroupTitle() {
        return EnhancedLLSDUtils.safeGetString(agentDataLLSD, "GroupTitle", "");
    }
    
    public void setGroupTitle(String groupTitle) {
        updateLLSDField("GroupTitle", groupTitle != null ? groupTitle : "");
    }
    
    public long getGroupPowers() {
        return EnhancedLLSDUtils.safeGetLong(agentDataLLSD, "GroupPowers", 0L);
    }
    
    public void setGroupPowers(long groupPowers) {
        updateLLSDField("GroupPowers", groupPowers);
    }
    
    public String getGroupName() {
        return EnhancedLLSDUtils.safeGetString(agentDataLLSD, "GroupName", "");
    }
    
    public void setGroupName(String groupName) {
        updateLLSDField("GroupName", groupName != null ? groupName : "");
    }
    
    /**
     * Update an LLSD field with proper error handling and validation
     * @param key The field key to update
     * @param value The new value to set
     */
    @SuppressWarnings("unchecked")
    private void updateLLSDField(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Field key cannot be null or empty");
        }
        
        if (agentDataLLSD == null) {
            initializeDefaultLLSD();
        }
        
        try {
            if (agentDataLLSD.getContent() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) agentDataLLSD.getContent();
                map.put(key, value);
                syncToLegacyFields();
            } else {
                throw new IllegalStateException("LLSD content is not a Map");
            }
        } catch (ClassCastException e) {
            throw new IllegalStateException("LLSD content cannot be cast to Map<String, Object>", e);
        }
    }
    
    /**
     * Synchronize LLSD data to legacy fields for wire protocol compatibility
     */
    private void syncToLegacyFields() {
        if (AgentData_Field == null) {
            AgentData_Field = new AgentData();
        }
        
        try {
            AgentData_Field.AgentID = getAgentID();
            AgentData_Field.ActiveGroupID = getActiveGroupID();
            AgentData_Field.FirstName = safeStringToBytes(getFirstName());
            AgentData_Field.LastName = safeStringToBytes(getLastName());
            AgentData_Field.GroupTitle = safeStringToBytes(getGroupTitle());
            AgentData_Field.GroupPowers = getGroupPowers();
            AgentData_Field.GroupName = safeStringToBytes(getGroupName());
        } catch (Exception e) {
            // Log error but don't fail - maintain robustness
            System.err.println("Warning: Failed to sync to legacy fields: " + e.getMessage());
        }
    }
    
    /**
     * Synchronize legacy fields to LLSD data
     */
    private void syncFromLegacyFields() {
        if (AgentData_Field == null) {
            return;
        }
        
        try {
            setAgentID(AgentData_Field.AgentID);
            setActiveGroupID(AgentData_Field.ActiveGroupID);
            setFirstName(safeBytesToString(AgentData_Field.FirstName));
            setLastName(safeBytesToString(AgentData_Field.LastName));
            setGroupTitle(safeBytesToString(AgentData_Field.GroupTitle));
            setGroupPowers(AgentData_Field.GroupPowers);
            setGroupName(safeBytesToString(AgentData_Field.GroupName));
        } catch (Exception e) {
            // Log error but don't fail - maintain robustness
            System.err.println("Warning: Failed to sync from legacy fields: " + e.getMessage());
        }
    }
    
    /**
     * Safely convert string to bytes with UTF-8 encoding
     */
    private byte[] safeStringToBytes(String str) {
        if (str == null) {
            return new byte[0];
        }
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            return str.getBytes(); // Fallback to default encoding
        }
    }
    
    /**
     * Safely convert bytes to string with UTF-8 encoding
     */
    private String safeBytesToString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            return new String(bytes); // Fallback to default encoding
        }
    }

    public int CalcPayloadSize() {
        String firstName = getFirstName();
        String lastName = getLastName();
        String groupTitle = getGroupTitle();
        String groupName = getGroupName();
        
        return (firstName.getBytes().length) + 17 + 1 + 
               (lastName.getBytes().length) + 1 + 
               (groupTitle.getBytes().length) + 16 + 8 + 1 + 
               (groupName.getBytes().length) + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        if (sLMessageHandler != null) {
            sLMessageHandler.HandleAgentDataUpdate(this);
        }
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }

        // Ensure legacy fields are current
        syncToLegacyFields();

        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -125);
        packUUID(byteBuffer, AgentData_Field.AgentID);
        packVariable(byteBuffer, AgentData_Field.FirstName, 1);
        packVariable(byteBuffer, AgentData_Field.LastName, 1);
        packVariable(byteBuffer, AgentData_Field.GroupTitle, 1);
        packUUID(byteBuffer, AgentData_Field.ActiveGroupID);
        packLong(byteBuffer, AgentData_Field.GroupPowers);
        packVariable(byteBuffer, AgentData_Field.GroupName, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        
        // Legacy unpacking
        AgentData_Field.AgentID = unpackUUID(byteBuffer);
        AgentData_Field.FirstName = unpackVariable(byteBuffer, 1);
        AgentData_Field.LastName = unpackVariable(byteBuffer, 1);
        AgentData_Field.GroupTitle = unpackVariable(byteBuffer, 1);
        AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer);
        AgentData_Field.GroupPowers = unpackLong(byteBuffer);
        AgentData_Field.GroupName = unpackVariable(byteBuffer, 1);
        
        // Sync to LLSD representation
        syncFromLegacyFields();
    }
    
    /**
     * Get LLSD representation as XML string for debugging/serialization
     */
    public String toXMLString() {
        try {
            return agentDataLLSD.serialise();
        } catch (Exception e) {
            return "<llsd><undef /></llsd>";
        }
    }
    
    /**
     * Create AgentDataUpdate from LLSD XML string
     * Uses proper parsing with error handling
     */
    public static AgentDataUpdate fromXMLString(String xml) {
        AgentDataUpdate update = new AgentDataUpdate();
        
        if (xml == null || xml.trim().isEmpty()) {
            return update; // Return default instance
        }
        
        try {
            Optional<LLSD> parsedLLSD = EnhancedLLSDUtils.parseFromXMLString(xml);
            if (parsedLLSD.isPresent()) {
                update.setAgentDataLLSD(parsedLLSD.get());
            }
        } catch (Exception e) {
            // Log error but return default instance for robustness
            System.err.println("Warning: Failed to parse AgentDataUpdate from XML: " + e.getMessage());
        }
        
        return update;
    }
}
