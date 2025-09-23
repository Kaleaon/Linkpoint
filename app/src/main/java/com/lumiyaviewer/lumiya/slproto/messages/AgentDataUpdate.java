package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentDataUpdate extends SLMessage {
    private AgentData agentDataField;

    public static class AgentData {
        private LLSDUUID activeGroupID;
        private LLSDUUID agentID;
        private LLSDString firstName;
        private LLSDString groupName;
        private LLSDInt groupPowers;
        private LLSDString groupTitle;
        private LLSDString lastName;
        
        public AgentData() {
            this.activeGroupID = new LLSDUUID();
            this.agentID = new LLSDUUID();
            this.firstName = new LLSDString("");
            this.groupName = new LLSDString("");
            this.groupPowers = new LLSDInt(0);
            this.groupTitle = new LLSDString("");
            this.lastName = new LLSDString("");
        }

        // Getters with null safety
        public UUID getActiveGroupID() {
            return activeGroupID != null ? activeGroupID.asUUID() : null;
        }

        public UUID getAgentID() {
            return agentID != null ? agentID.asUUID() : null;
        }

        public String getFirstName() {
            return firstName != null ? firstName.asString() : "";
        }

        public String getGroupName() {
            return groupName != null ? groupName.asString() : "";
        }

        public long getGroupPowers() {
            return groupPowers != null ? groupPowers.asInt() : 0L;
        }

        public String getGroupTitle() {
            return groupTitle != null ? groupTitle.asString() : "";
        }

        public String getLastName() {
            return lastName != null ? lastName.asString() : "";
        }

        // Setters with null safety
        public void setActiveGroupID(UUID id) {
            this.activeGroupID = id != null ? new LLSDUUID(id) : new LLSDUUID();
        }

        public void setAgentID(UUID id) {
            this.agentID = id != null ? new LLSDUUID(id) : new LLSDUUID();
        }

        public void setFirstName(String name) {
            this.firstName = new LLSDString(name != null ? name : "");
        }

        public void setGroupName(String name) {
            this.groupName = new LLSDString(name != null ? name : "");
        }

        public void setGroupPowers(long powers) {
            this.groupPowers = new LLSDInt((int) powers);
        }

        public void setGroupTitle(String title) {
            this.groupTitle = new LLSDString(title != null ? title : "");
        }

        public void setLastName(String name) {
            this.lastName = new LLSDString(name != null ? name : "");
        }
        
        // Legacy compatibility - internal use only
        UUID ActiveGroupID;
        UUID AgentID;
        byte[] FirstName;
        byte[] GroupName;
        long GroupPowers;
        byte[] GroupTitle;
        byte[] LastName;
    }

    public AgentDataUpdate() {
        this.zeroCoded = true;
        this.agentDataField = new AgentData();
    }

    // Getter for agent data with null safety
    public AgentData getAgentData() {
        if (agentDataField == null) {
            agentDataField = new AgentData();
        }
        return agentDataField;
    }

    // Legacy compatibility
    public AgentData AgentData_Field = new AgentData();

    public int CalcPayloadSize() {
        if (agentDataField == null) {
            return 0;
        }
        
        String firstName = agentDataField.getFirstName();
        String lastName = agentDataField.getLastName();
        String groupTitle = agentDataField.getGroupTitle();
        String groupName = agentDataField.getGroupName();
        
        return (firstName != null ? firstName.getBytes().length : 0) + 17 + 1 + 
               (lastName != null ? lastName.getBytes().length : 0) + 1 + 
               (groupTitle != null ? groupTitle.getBytes().length : 0) + 16 + 8 + 1 + 
               (groupName != null ? groupName.getBytes().length : 0) + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        if (sLMessageHandler != null) {
            sLMessageHandler.HandleAgentDataUpdate(this);
        }
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        if (byteBuffer == null || agentDataField == null) {
            return;
        }

        // Sync LLSD data to legacy fields for compatibility
        AgentData_Field.AgentID = agentDataField.getAgentID();
        AgentData_Field.ActiveGroupID = agentDataField.getActiveGroupID();
        AgentData_Field.FirstName = agentDataField.getFirstName().getBytes();
        AgentData_Field.LastName = agentDataField.getLastName().getBytes();
        AgentData_Field.GroupTitle = agentDataField.getGroupTitle().getBytes();
        AgentData_Field.GroupPowers = agentDataField.getGroupPowers();
        AgentData_Field.GroupName = agentDataField.getGroupName().getBytes();

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
        
        // Sync to LLSD fields
        if (agentDataField == null) {
            agentDataField = new AgentData();
        }
        
        agentDataField.setAgentID(AgentData_Field.AgentID);
        agentDataField.setActiveGroupID(AgentData_Field.ActiveGroupID);
        agentDataField.setFirstName(AgentData_Field.FirstName != null ? new String(AgentData_Field.FirstName) : "");
        agentDataField.setLastName(AgentData_Field.LastName != null ? new String(AgentData_Field.LastName) : "");
        agentDataField.setGroupTitle(AgentData_Field.GroupTitle != null ? new String(AgentData_Field.GroupTitle) : "");
        agentDataField.setGroupPowers(AgentData_Field.GroupPowers);
        agentDataField.setGroupName(AgentData_Field.GroupName != null ? new String(AgentData_Field.GroupName) : "");
    }
}
