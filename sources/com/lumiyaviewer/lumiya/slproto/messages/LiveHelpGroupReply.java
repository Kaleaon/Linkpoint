// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LiveHelpGroupReply extends SLMessage
{
    public static class ReplyData
    {

        public UUID GroupID;
        public UUID RequestID;
        public byte Selection[];

        public ReplyData()
        {
        }
    }


    public ReplyData ReplyData_Field;

    public LiveHelpGroupReply()
    {
        zeroCoded = false;
        ReplyData_Field = new ReplyData();
    }

    public int CalcPayloadSize()
    {
        return ReplyData_Field.Selection.length + 33 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLiveHelpGroupReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)124);
        packUUID(bytebuffer, ReplyData_Field.RequestID);
        packUUID(bytebuffer, ReplyData_Field.GroupID);
        packVariable(bytebuffer, ReplyData_Field.Selection, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ReplyData_Field.RequestID = unpackUUID(bytebuffer);
        ReplyData_Field.GroupID = unpackUUID(bytebuffer);
        ReplyData_Field.Selection = unpackVariable(bytebuffer, 1);
    }
}
