package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ObjectLink extends SLMessage {
    private AgentData agentDataField;
    private final List<ObjectData> objectDataFields;

    public static class AgentData {
        private LLSDUUID agentID;
        private LLSDUUID sessionID;

        public AgentData() {
            this.agentID = new LLSDUUID();
            this.sessionID = new LLSDUUID();
        }

        // Getters with null safety
        public UUID getAgentID() {
            return agentID != null ? agentID.asUUID() : null;
        }

        public UUID getSessionID() {
            return sessionID != null ? sessionID.asUUID() : null;
        }

        // Setters with null safety  
        public void setAgentID(UUID id) {
            this.agentID = id != null ? new LLSDUUID(id) : new LLSDUUID();
        }

        public void setSessionID(UUID id) {
            this.sessionID = id != null ? new LLSDUUID(id) : new LLSDUUID();
        }
        
        // Legacy compatibility
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ObjectData {
        private LLSDInt objectLocalID;

        public ObjectData() {
            this.objectLocalID = new LLSDInt(0);
        }

        public ObjectData(int localID) {
            this.objectLocalID = new LLSDInt(localID);
        }

        // Getter with null safety
        public int getObjectLocalID() {
            return objectLocalID != null ? objectLocalID.asInt() : 0;
        }

        // Setter with null safety
        public void setObjectLocalID(int localID) {
            this.objectLocalID = new LLSDInt(localID);
        }
        
        // Legacy compatibility
        public int ObjectLocalID;
    }

    public ObjectLink() {
        this.zeroCoded = false;
        this.agentDataField = new AgentData();
        this.objectDataFields = new ArrayList<>();
    }

    // Getters with null safety
    public AgentData getAgentData() {
        if (agentDataField == null) {
            agentDataField = new AgentData();
        }
        return agentDataField;
    }

    public List<ObjectData> getObjectDataList() {
        return Collections.unmodifiableList(objectDataFields);
    }

    public void addObjectData(ObjectData objectData) {
        if (objectData != null) {
            objectDataFields.add(objectData);
        }
    }

    public void clearObjectData() {
        objectDataFields.clear();
    }

    // Legacy compatibility
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public int CalcPayloadSize() {
        return (objectDataFields.size() * 4) + 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        if (sLMessageHandler != null) {
            sLMessageHandler.HandleObjectLink(this);
        }
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        if (byteBuffer == null || agentDataField == null) {
            return;
        }

        // Sync LLSD data to legacy fields for compatibility
        if (AgentData_Field == null) {
            AgentData_Field = new AgentData();
        }
        
        AgentData_Field.AgentID = agentDataField.getAgentID();
        AgentData_Field.SessionID = agentDataField.getSessionID();
        
        ObjectData_Fields.clear();
        for (ObjectData objData : objectDataFields) {
            ObjectData legacyData = new ObjectData();
            legacyData.ObjectLocalID = objData.getObjectLocalID();
            ObjectData_Fields.add(legacyData);
        }

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
        
        // Sync to LLSD fields
        if (agentDataField == null) {
            agentDataField = new AgentData();
        }
        
        agentDataField.setAgentID(AgentData_Field.AgentID);
        agentDataField.setSessionID(AgentData_Field.SessionID);
        
        objectDataFields.clear();
        for (ObjectData legacyData : ObjectData_Fields) {
            ObjectData newData = new ObjectData(legacyData.ObjectLocalID);
            objectDataFields.add(newData);
        }
    }
}
