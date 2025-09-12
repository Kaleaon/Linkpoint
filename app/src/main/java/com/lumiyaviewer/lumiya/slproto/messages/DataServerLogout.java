package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DataServerLogout extends SLMessage {
    public UserData UserData_Field = new UserData();

    public static class UserData {
        public UUID AgentID;
        public boolean Disconnect;
        public UUID SessionID;
        public Inet4Address ViewerIP;
    }

    public DataServerLogout() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 41;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDataServerLogout(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -5);
        packUUID(byteBuffer, this.UserData_Field.AgentID);
        packIPAddress(byteBuffer, this.UserData_Field.ViewerIP);
        packBoolean(byteBuffer, this.UserData_Field.Disconnect);
        packUUID(byteBuffer, this.UserData_Field.SessionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.UserData_Field.AgentID = unpackUUID(byteBuffer);
        this.UserData_Field.ViewerIP = unpackIPAddress(byteBuffer);
        this.UserData_Field.Disconnect = unpackBoolean(byteBuffer);
        this.UserData_Field.SessionID = unpackUUID(byteBuffer);
    }
}
