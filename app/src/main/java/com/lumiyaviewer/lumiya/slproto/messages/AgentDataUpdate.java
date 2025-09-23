package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDUtils;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
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
    
    // Convenient getters using LLSD utilities
    public UUID getAgentID() {
        return LLSDUtils.getUUID(agentDataLLSD.getContent(), "AgentID", null);
    }
    
    public void setAgentID(UUID agentID) {
        updateLLSDField("AgentID", agentID);
    }
    
    public UUID getActiveGroupID() {
        return LLSDUtils.getUUID(agentDataLLSD.getContent(), "ActiveGroupID", null);
    }
    
    public void setActiveGroupID(UUID activeGroupID) {
        updateLLSDField("ActiveGroupID", activeGroupID);
    }
    
    public String getFirstName() {
        return LLSDUtils.getString(agentDataLLSD.getContent(), "FirstName", "");
    }
    
    public void setFirstName(String firstName) {
        updateLLSDField("FirstName", firstName != null ? firstName : "");
    }
    
    public String getLastName() {
        return LLSDUtils.getString(agentDataLLSD.getContent(), "LastName", "");
    }
    
    public void setLastName(String lastName) {
        updateLLSDField("LastName", lastName != null ? lastName : "");
    }
    
    public String getGroupTitle() {
        return LLSDUtils.getString(agentDataLLSD.getContent(), "GroupTitle", "");
    }
    
    public void setGroupTitle(String groupTitle) {
        updateLLSDField("GroupTitle", groupTitle != null ? groupTitle : "");
    }
    
    public long getGroupPowers() {
        return LLSDUtils.getLong(agentDataLLSD.getContent(), "GroupPowers", 0L);
    }
    
    public void setGroupPowers(long groupPowers) {
        updateLLSDField("GroupPowers", groupPowers);
    }
    
    public String getGroupName() {
        return LLSDUtils.getString(agentDataLLSD.getContent(), "GroupName", "");
    }
    
    public void setGroupName(String groupName) {
        updateLLSDField("GroupName", groupName != null ? groupName : "");
    }
    
    @SuppressWarnings("unchecked")
    private void updateLLSDField(String key, Object value) {
        if (agentDataLLSD.getContent() instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) agentDataLLSD.getContent();
            map.put(key, value);
            syncToLegacyFields();
        }
    }
    
    private void syncToLegacyFields() {
        AgentData_Field.AgentID = getAgentID();
        AgentData_Field.ActiveGroupID = getActiveGroupID();
        AgentData_Field.FirstName = getFirstName().getBytes();
        AgentData_Field.LastName = getLastName().getBytes();
        AgentData_Field.GroupTitle = getGroupTitle().getBytes();
        AgentData_Field.GroupPowers = getGroupPowers();
        AgentData_Field.GroupName = getGroupName().getBytes();
    }
    
    private void syncFromLegacyFields() {
        setAgentID(AgentData_Field.AgentID);
        setActiveGroupID(AgentData_Field.ActiveGroupID);
        setFirstName(AgentData_Field.FirstName != null ? new String(AgentData_Field.FirstName) : "");
        setLastName(AgentData_Field.LastName != null ? new String(AgentData_Field.LastName) : "");
        setGroupTitle(AgentData_Field.GroupTitle != null ? new String(AgentData_Field.GroupTitle) : "");
        setGroupPowers(AgentData_Field.GroupPowers);
        setGroupName(AgentData_Field.GroupName != null ? new String(AgentData_Field.GroupName) : "");
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
     * Create from LLSD XML string
     */
    public static AgentDataUpdate fromXMLString(String xml) {
        AgentDataUpdate update = new AgentDataUpdate();
        try {
            // Note: Would use LLSDParser here when available
            // For now, just return default instance
            return update;
        } catch (Exception e) {
            return update;
        }
    }
}
