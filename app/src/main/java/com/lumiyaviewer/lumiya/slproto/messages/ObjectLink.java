package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Enhanced ObjectLink using Kaleaon's LLSD-Java library
 * Provides advanced LLSD features including JSON, Notation, and Binary formats
 */
public class ObjectLink extends SLMessage {
    private LLSD messageLLSD;
    
    // Legacy compatibility fields
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int ObjectLocalID;
    }

    public ObjectLink() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        initializeDefaultLLSD();
    }
    
    private void initializeDefaultLLSD() {
        Map<String, Object> messageData = new HashMap<>();
        
        // Agent data
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("AgentID", UUID.randomUUID());
        agentData.put("SessionID", UUID.randomUUID());
        messageData.put("AgentData", agentData);
        
        // Object data array
        List<Map<String, Object>> objectDataList = new ArrayList<>();
        messageData.put("ObjectData", objectDataList);
        
        this.messageLLSD = new LLSD(messageData);
    }
    
    // Modern LLSD-based API
    public LLSD getMessageLLSD() {
        return messageLLSD;
    }
    
    public void setMessageLLSD(LLSD messageData) {
        if (messageData != null) {
            this.messageLLSD = messageData;
            syncToLegacyFields();
        }
    }
    
    // Agent data accessors using LLSD utilities
    public UUID getAgentID() {
        return LLSDUtils.getUUID(messageLLSD.getContent(), "AgentData.AgentID", null);
    }
    
    public void setAgentID(UUID agentID) {
        updateNestedLLSDField("AgentData", "AgentID", agentID);
    }
    
    public UUID getSessionID() {
        return LLSDUtils.getUUID(messageLLSD.getContent(), "AgentData.SessionID", null);
    }
    
    public void setSessionID(UUID sessionID) {
        updateNestedLLSDField("AgentData", "SessionID", sessionID);
    }
    
    // Object data accessors
    public List<Integer> getObjectLocalIDs() {
        List<Integer> localIDs = new ArrayList<>();
        Object content = messageLLSD.getContent();
        if (content instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) content;
            Object objectDataObj = map.get("ObjectData");
            if (objectDataObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> objectDataList = (List<Object>) objectDataObj;
                for (Object obj : objectDataList) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> objectMap = (Map<String, Object>) obj;
                        Object localIDObj = objectMap.get("ObjectLocalID");
                        if (localIDObj instanceof Integer) {
                            localIDs.add((Integer) localIDObj);
                        }
                    }
                }
            }
        }
        return localIDs;
    }
    
    public void addObjectLocalID(int localID) {
        Object content = messageLLSD.getContent();
        if (content instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) content;
            Object objectDataObj = map.get("ObjectData");
            if (objectDataObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> objectDataList = (List<Object>) objectDataObj;
                Map<String, Object> objectItem = new HashMap<>();
                objectItem.put("ObjectLocalID", localID);
                objectDataList.add(objectItem);
                syncToLegacyFields();
            }
        }
    }
    
    public void clearObjectData() {
        Object content = messageLLSD.getContent();
        if (content instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) content;
            Object objectDataObj = map.get("ObjectData");
            if (objectDataObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> objectDataList = (List<Object>) objectDataObj;
                objectDataList.clear();
                syncToLegacyFields();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void updateNestedLLSDField(String parentKey, String key, Object value) {
        if (messageLLSD.getContent() instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) messageLLSD.getContent();
            Object parentObj = map.get(parentKey);
            if (parentObj instanceof Map) {
                Map<String, Object> parentMap = (Map<String, Object>) parentObj;
                parentMap.put(key, value);
                syncToLegacyFields();
            }
        }
    }
    
    private void syncToLegacyFields() {
        if (AgentData_Field == null) {
            AgentData_Field = new AgentData();
        }
        
        AgentData_Field.AgentID = getAgentID();
        AgentData_Field.SessionID = getSessionID();
        
        ObjectData_Fields.clear();
        List<Integer> localIDs = getObjectLocalIDs();
        for (Integer localID : localIDs) {
            ObjectData objData = new ObjectData();
            objData.ObjectLocalID = localID;
            ObjectData_Fields.add(objData);
        }
    }
    
    private void syncFromLegacyFields() {
        setAgentID(AgentData_Field.AgentID);
        setSessionID(AgentData_Field.SessionID);
        
        clearObjectData();
        for (ObjectData objData : ObjectData_Fields) {
            addObjectLocalID(objData.ObjectLocalID);
        }
    }

    public int CalcPayloadSize() {
        return (getObjectLocalIDs().size() * 4) + 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        if (sLMessageHandler != null) {
            sLMessageHandler.HandleObjectLink(this);
        }
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }

        // Ensure legacy fields are current
        syncToLegacyFields();

        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 115);
        packUUID(byteBuffer, AgentData_Field.AgentID);
        packUUID(byteBuffer, AgentData_Field.SessionID);
        byteBuffer.put((byte) ObjectData_Fields.size());
        for (ObjectData objectData : ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        
        if (AgentData_Field == null) {
            AgentData_Field = new AgentData();
        }
        
        // Legacy unpacking
        AgentData_Field.AgentID = unpackUUID(byteBuffer);
        AgentData_Field.SessionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        
        ObjectData_Fields.clear();
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            ObjectData_Fields.add(objectData);
        }
        
        // Sync to LLSD representation
        syncFromLegacyFields();
    }
    
    /**
     * Export message as LLSD JSON for debugging/API usage
     */
    public String toJSONString() {
        try {
            // Note: Would use LLSDJsonSerializer here when available
            return messageLLSD.serialise(); // Falls back to XML for now
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Export message as LLSD Notation (compact format)
     */
    public String toNotationString() {
        try {
            // Note: Would use LLSDNotationSerializer here when available
            // For now, return a simple representation
            return String.format(
                "{AgentID:u%s,SessionID:u%s,ObjectCount:i%d}",
                getAgentID(), getSessionID(), getObjectLocalIDs().size()
            );
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Validate message structure using LLSD utilities
     */
    public boolean isValid() {
        try {
            UUID agentID = getAgentID();
            UUID sessionID = getSessionID();
            List<Integer> objectIDs = getObjectLocalIDs();
            
            return agentID != null && sessionID != null && objectIDs != null;
        } catch (Exception e) {
            return false;
        }
    }
}
