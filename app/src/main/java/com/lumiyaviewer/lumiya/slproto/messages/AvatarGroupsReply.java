package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AvatarGroupsReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<GroupData> GroupData_Fields = new ArrayList<>();
    public NewGroupData NewGroupData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID AvatarID;
    }

    public static class GroupData {
        public boolean AcceptNotices;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte[] GroupName;
        public long GroupPowers;
        public byte[] GroupTitle;
    }

    public static class NewGroupData {
        public boolean ListInProfile;
    }

    public AvatarGroupsReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.NewGroupData_Field = new NewGroupData();
    }

    public int CalcPayloadSize() {
        int i = 37;
        Iterator<T> it = this.GroupData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2 + 1;
            }
            GroupData groupData = (GroupData) it.next();
            i = groupData.GroupName.length + groupData.GroupTitle.length + 10 + 16 + 1 + 16 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarGroupsReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -83);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.AvatarID);
        byteBuffer.put((byte) this.GroupData_Fields.size());
        for (GroupData groupData : this.GroupData_Fields) {
            packLong(byteBuffer, groupData.GroupPowers);
            packBoolean(byteBuffer, groupData.AcceptNotices);
            packVariable(byteBuffer, groupData.GroupTitle, 1);
            packUUID(byteBuffer, groupData.GroupID);
            packVariable(byteBuffer, groupData.GroupName, 1);
            packUUID(byteBuffer, groupData.GroupInsigniaID);
        }
        packBoolean(byteBuffer, this.NewGroupData_Field.ListInProfile);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            GroupData groupData = new GroupData();
            groupData.GroupPowers = unpackLong(byteBuffer);
            groupData.AcceptNotices = unpackBoolean(byteBuffer);
            groupData.GroupTitle = unpackVariable(byteBuffer, 1);
            groupData.GroupID = unpackUUID(byteBuffer);
            groupData.GroupName = unpackVariable(byteBuffer, 1);
            groupData.GroupInsigniaID = unpackUUID(byteBuffer);
            this.GroupData_Fields.add(groupData);
        }
        this.NewGroupData_Field.ListInProfile = unpackBoolean(byteBuffer);
    }
}
