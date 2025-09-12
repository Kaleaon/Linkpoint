// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptSensorRequest extends SLMessage
{
    public static class Requester
    {

        public float Arc;
        public float Range;
        public long RegionHandle;
        public UUID RequestID;
        public LLQuaternion SearchDir;
        public UUID SearchID;
        public byte SearchName[];
        public LLVector3 SearchPos;
        public int SearchRegions;
        public UUID SourceID;
        public int Type;

        public Requester()
        {
        }
    }


    public Requester Requester_Field;

    public ScriptSensorRequest()
    {
        zeroCoded = true;
        Requester_Field = new Requester();
    }

    public int CalcPayloadSize()
    {
        return Requester_Field.SearchName.length + 73 + 4 + 4 + 4 + 8 + 1 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptSensorRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-9);
        packUUID(bytebuffer, Requester_Field.SourceID);
        packUUID(bytebuffer, Requester_Field.RequestID);
        packUUID(bytebuffer, Requester_Field.SearchID);
        packLLVector3(bytebuffer, Requester_Field.SearchPos);
        packLLQuaternion(bytebuffer, Requester_Field.SearchDir);
        packVariable(bytebuffer, Requester_Field.SearchName, 1);
        packInt(bytebuffer, Requester_Field.Type);
        packFloat(bytebuffer, Requester_Field.Range);
        packFloat(bytebuffer, Requester_Field.Arc);
        packLong(bytebuffer, Requester_Field.RegionHandle);
        packByte(bytebuffer, (byte)Requester_Field.SearchRegions);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Requester_Field.SourceID = unpackUUID(bytebuffer);
        Requester_Field.RequestID = unpackUUID(bytebuffer);
        Requester_Field.SearchID = unpackUUID(bytebuffer);
        Requester_Field.SearchPos = unpackLLVector3(bytebuffer);
        Requester_Field.SearchDir = unpackLLQuaternion(bytebuffer);
        Requester_Field.SearchName = unpackVariable(bytebuffer, 1);
        Requester_Field.Type = unpackInt(bytebuffer);
        Requester_Field.Range = unpackFloat(bytebuffer);
        Requester_Field.Arc = unpackFloat(bytebuffer);
        Requester_Field.RegionHandle = unpackLong(bytebuffer);
        Requester_Field.SearchRegions = unpackByte(bytebuffer) & 0xff;
    }
}
