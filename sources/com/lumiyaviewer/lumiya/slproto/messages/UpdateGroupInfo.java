package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UpdateGroupInfo extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public GroupData GroupData_Field = new GroupData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class GroupData {
        public boolean AllowPublish;
        public byte[] Charter;
        public UUID GroupID;
        public UUID InsigniaID;
        public boolean MaturePublish;
        public int MembershipFee;
        public boolean OpenEnrollment;
        public boolean ShowInList;
    }

    public UpdateGroupInfo() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.GroupData_Field.Charter.length + 18 + 1 + 16 + 4 + 1 + 1 + 1 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateGroupInfo(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 85);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        packVariable(byteBuffer, this.GroupData_Field.Charter, 2);
        packBoolean(byteBuffer, this.GroupData_Field.ShowInList);
        packUUID(byteBuffer, this.GroupData_Field.InsigniaID);
        packInt(byteBuffer, this.GroupData_Field.MembershipFee);
        packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment);
        packBoolean(byteBuffer, this.GroupData_Field.AllowPublish);
        packBoolean(byteBuffer, this.GroupData_Field.MaturePublish);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        this.GroupData_Field.Charter = unpackVariable(byteBuffer, 2);
        this.GroupData_Field.ShowInList = unpackBoolean(byteBuffer);
        this.GroupData_Field.InsigniaID = unpackUUID(byteBuffer);
        this.GroupData_Field.MembershipFee = unpackInt(byteBuffer);
        this.GroupData_Field.OpenEnrollment = unpackBoolean(byteBuffer);
        this.GroupData_Field.AllowPublish = unpackBoolean(byteBuffer);
        this.GroupData_Field.MaturePublish = unpackBoolean(byteBuffer);
    }
}
