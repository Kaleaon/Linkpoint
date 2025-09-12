// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RequestXfer extends SLMessage
{
    public static class XferID
    {

        public boolean DeleteOnCompletion;
        public int FilePath;
        public byte Filename[];
        public long ID;
        public boolean UseBigPackets;
        public UUID VFileID;
        public int VFileType;

        public XferID()
        {
        }
    }


    public XferID XferID_Field;

    public RequestXfer()
    {
        zeroCoded = true;
        XferID_Field = new XferID();
    }

    public int CalcPayloadSize()
    {
        return XferID_Field.Filename.length + 9 + 1 + 1 + 1 + 16 + 2 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRequestXfer(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-100);
        packLong(bytebuffer, XferID_Field.ID);
        packVariable(bytebuffer, XferID_Field.Filename, 1);
        packByte(bytebuffer, (byte)XferID_Field.FilePath);
        packBoolean(bytebuffer, XferID_Field.DeleteOnCompletion);
        packBoolean(bytebuffer, XferID_Field.UseBigPackets);
        packUUID(bytebuffer, XferID_Field.VFileID);
        packShort(bytebuffer, (short)XferID_Field.VFileType);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        XferID_Field.ID = unpackLong(bytebuffer);
        XferID_Field.Filename = unpackVariable(bytebuffer, 1);
        XferID_Field.FilePath = unpackByte(bytebuffer) & 0xff;
        XferID_Field.DeleteOnCompletion = unpackBoolean(bytebuffer);
        XferID_Field.UseBigPackets = unpackBoolean(bytebuffer);
        XferID_Field.VFileID = unpackUUID(bytebuffer);
        XferID_Field.VFileType = unpackShort(bytebuffer);
    }
}
