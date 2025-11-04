// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LiveHelpGroupReply extends SLMessage
{
    public ReplyData ReplyData_Field;
    
    public LiveHelpGroupReply() {
        this.zeroCoded = false;
        this.ReplyData_Field = new ReplyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ReplyData_Field.Selection.length + 33 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLiveHelpGroupReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)124);
        this.packUUID(byteBuffer, this.ReplyData_Field.RequestID);
        this.packUUID(byteBuffer, this.ReplyData_Field.GroupID);
        this.packVariable(byteBuffer, this.ReplyData_Field.Selection, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ReplyData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.ReplyData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ReplyData_Field.Selection = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class ReplyData
    {
        public UUID GroupID;
        public UUID RequestID;
        public byte[] Selection;
    }
}
