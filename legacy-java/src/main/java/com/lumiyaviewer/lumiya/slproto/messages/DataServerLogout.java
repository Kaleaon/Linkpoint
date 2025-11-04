package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class DataServerLogout extends SLMessage {
    public UserData UserData_Field;

    /* loaded from: classes.dex */
    public static class UserData {
        public UUID AgentID;
        public boolean Disconnect;
        public UUID SessionID;
        public Inet4Address ViewerIP;
    }

    public DataServerLogout() {
        this.zeroCoded = false;
        this.UserData_Field = new UserData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 41;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataServerLogout(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -5);
        packUUID(byteBuffer, this.UserData_Field.AgentID);
        packIPAddress(byteBuffer, this.UserData_Field.ViewerIP);
        packBoolean(byteBuffer, this.UserData_Field.Disconnect);
        packUUID(byteBuffer, this.UserData_Field.SessionID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.UserData_Field.AgentID = unpackUUID(byteBuffer);
        this.UserData_Field.ViewerIP = unpackIPAddress(byteBuffer);
        this.UserData_Field.Disconnect = unpackBoolean(byteBuffer);
        this.UserData_Field.SessionID = unpackUUID(byteBuffer);
    }
}
