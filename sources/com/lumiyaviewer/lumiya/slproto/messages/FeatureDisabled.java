// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class FeatureDisabled extends SLMessage
{
    public static class FailureInfo
    {

        public UUID AgentID;
        public byte ErrorMessage[];
        public UUID TransactionID;

        public FailureInfo()
        {
        }
    }


    public FailureInfo FailureInfo_Field;

    public FeatureDisabled()
    {
        zeroCoded = false;
        FailureInfo_Field = new FailureInfo();
    }

    public int CalcPayloadSize()
    {
        return FailureInfo_Field.ErrorMessage.length + 1 + 16 + 16 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleFeatureDisabled(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)19);
        packVariable(bytebuffer, FailureInfo_Field.ErrorMessage, 1);
        packUUID(bytebuffer, FailureInfo_Field.AgentID);
        packUUID(bytebuffer, FailureInfo_Field.TransactionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        FailureInfo_Field.ErrorMessage = unpackVariable(bytebuffer, 1);
        FailureInfo_Field.AgentID = unpackUUID(bytebuffer);
        FailureInfo_Field.TransactionID = unpackUUID(bytebuffer);
    }
}
