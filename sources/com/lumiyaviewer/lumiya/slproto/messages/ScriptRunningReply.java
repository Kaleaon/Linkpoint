// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptRunningReply extends SLMessage
{
    public static class Script
    {

        public UUID ItemID;
        public UUID ObjectID;
        public boolean Running;

        public Script()
        {
        }
    }


    public Script Script_Field;

    public ScriptRunningReply()
    {
        zeroCoded = false;
        Script_Field = new Script();
    }

    public int CalcPayloadSize()
    {
        return 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptRunningReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-12);
        packUUID(bytebuffer, Script_Field.ObjectID);
        packUUID(bytebuffer, Script_Field.ItemID);
        packBoolean(bytebuffer, Script_Field.Running);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Script_Field.ObjectID = unpackUUID(bytebuffer);
        Script_Field.ItemID = unpackUUID(bytebuffer);
        Script_Field.Running = unpackBoolean(bytebuffer);
    }
}
