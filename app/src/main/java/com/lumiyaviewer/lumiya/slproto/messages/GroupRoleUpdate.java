package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GroupRoleUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<RoleData> RoleData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class RoleData {
        public byte[] Description;
        public byte[] Name;
        public long Powers;
        public UUID RoleID;
        public byte[] Title;
        public int UpdateType;
    }

    public GroupRoleUpdate() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i = 53;
        Iterator<T> it = this.RoleData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            RoleData roleData = (RoleData) it.next();
            i = roleData.Title.length + roleData.Name.length + 17 + 1 + roleData.Description.length + 1 + 8 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 122);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        byteBuffer.put((byte) this.RoleData_Fields.size());
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID);
            packVariable(byteBuffer, roleData.Name, 1);
            packVariable(byteBuffer, roleData.Description, 1);
            packVariable(byteBuffer, roleData.Title, 1);
            packLong(byteBuffer, roleData.Powers);
            packByte(byteBuffer, (byte) roleData.UpdateType);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RoleData roleData = new RoleData();
            roleData.RoleID = unpackUUID(byteBuffer);
            roleData.Name = unpackVariable(byteBuffer, 1);
            roleData.Description = unpackVariable(byteBuffer, 1);
            roleData.Title = unpackVariable(byteBuffer, 1);
            roleData.Powers = unpackLong(byteBuffer);
            roleData.UpdateType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.RoleData_Fields.add(roleData);
        }
    }
}
