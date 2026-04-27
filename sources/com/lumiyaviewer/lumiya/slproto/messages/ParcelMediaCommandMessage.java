// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelMediaCommandMessage extends SLMessage
{
    public static class CommandBlock
    {

        public int Command;
        public int Flags;
        public float Time;

        public CommandBlock()
        {
        }
    }


    public CommandBlock CommandBlock_Field;

    public ParcelMediaCommandMessage()
    {
        zeroCoded = false;
        CommandBlock_Field = new CommandBlock();
    }

    public int CalcPayloadSize()
    {
        return 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelMediaCommandMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-93);
        packInt(bytebuffer, CommandBlock_Field.Flags);
        packInt(bytebuffer, CommandBlock_Field.Command);
        packFloat(bytebuffer, CommandBlock_Field.Time);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        CommandBlock_Field.Flags = unpackInt(bytebuffer);
        CommandBlock_Field.Command = unpackInt(bytebuffer);
        CommandBlock_Field.Time = unpackFloat(bytebuffer);
    }
}
