// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TransferInfo extends SLMessage
{
    public static class TransferInfoData
    {

        public int ChannelType;
        public byte Params[];
        public int Size;
        public int Status;
        public int TargetType;
        public UUID TransferID;

        public TransferInfoData()
        {
        }
    }


    public TransferInfoData TransferInfoData_Field;

    public TransferInfo()
    {
        zeroCoded = true;
        TransferInfoData_Field = new TransferInfoData();
    }

    public int CalcPayloadSize()
    {
        return TransferInfoData_Field.Params.length + 34 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTransferInfo(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-102);
        packUUID(bytebuffer, TransferInfoData_Field.TransferID);
        packInt(bytebuffer, TransferInfoData_Field.ChannelType);
        packInt(bytebuffer, TransferInfoData_Field.TargetType);
        packInt(bytebuffer, TransferInfoData_Field.Status);
        packInt(bytebuffer, TransferInfoData_Field.Size);
        packVariable(bytebuffer, TransferInfoData_Field.Params, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TransferInfoData_Field.TransferID = unpackUUID(bytebuffer);
        TransferInfoData_Field.ChannelType = unpackInt(bytebuffer);
        TransferInfoData_Field.TargetType = unpackInt(bytebuffer);
        TransferInfoData_Field.Status = unpackInt(bytebuffer);
        TransferInfoData_Field.Size = unpackInt(bytebuffer);
        TransferInfoData_Field.Params = unpackVariable(bytebuffer, 2);
    }
}
