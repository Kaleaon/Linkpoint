// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptQuestion extends SLMessage
{
    public static class Data
    {

        public UUID ItemID;
        public byte ObjectName[];
        public byte ObjectOwner[];
        public int Questions;
        public UUID TaskID;

        public Data()
        {
        }
    }


    public Data Data_Field;

    public ScriptQuestion()
    {
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.ObjectName.length + 33 + 1 + Data_Field.ObjectOwner.length + 4 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptQuestion(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-68);
        packUUID(bytebuffer, Data_Field.TaskID);
        packUUID(bytebuffer, Data_Field.ItemID);
        packVariable(bytebuffer, Data_Field.ObjectName, 1);
        packVariable(bytebuffer, Data_Field.ObjectOwner, 1);
        packInt(bytebuffer, Data_Field.Questions);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.TaskID = unpackUUID(bytebuffer);
        Data_Field.ItemID = unpackUUID(bytebuffer);
        Data_Field.ObjectName = unpackVariable(bytebuffer, 1);
        Data_Field.ObjectOwner = unpackVariable(bytebuffer, 1);
        Data_Field.Questions = unpackInt(bytebuffer);
    }
}
