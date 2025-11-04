package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class AvatarGroupsReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<GroupData> GroupData_Fields = new ArrayList<>();
    public NewGroupData NewGroupData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID AvatarID;
    }

    /* loaded from: classes.dex */
    public static class GroupData {
        public boolean AcceptNotices;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte[] GroupName;
        public long GroupPowers;
        public byte[] GroupTitle;
    }

    /* loaded from: classes.dex */
    public static class NewGroupData {
        public boolean ListInProfile;
    }

    public AvatarGroupsReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.NewGroupData_Field = new NewGroupData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarGroupsReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
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
