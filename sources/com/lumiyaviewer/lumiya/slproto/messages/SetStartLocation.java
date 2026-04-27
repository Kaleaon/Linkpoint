// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetStartLocation extends SLMessage
{
    public static class StartLocationData
    {

        public UUID AgentID;
        public int LocationID;
        public LLVector3 LocationLookAt;
        public LLVector3 LocationPos;
        public long RegionHandle;
        public UUID RegionID;

        public StartLocationData()
        {
        }
    }


    public StartLocationData StartLocationData_Field;

    public SetStartLocation()
    {
        zeroCoded = true;
        StartLocationData_Field = new StartLocationData();
    }

    public int CalcPayloadSize()
    {
        return 72;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetStartLocation(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)69);
        packUUID(bytebuffer, StartLocationData_Field.AgentID);
        packUUID(bytebuffer, StartLocationData_Field.RegionID);
        packInt(bytebuffer, StartLocationData_Field.LocationID);
        packLong(bytebuffer, StartLocationData_Field.RegionHandle);
        packLLVector3(bytebuffer, StartLocationData_Field.LocationPos);
        packLLVector3(bytebuffer, StartLocationData_Field.LocationLookAt);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        StartLocationData_Field.AgentID = unpackUUID(bytebuffer);
        StartLocationData_Field.RegionID = unpackUUID(bytebuffer);
        StartLocationData_Field.LocationID = unpackInt(bytebuffer);
        StartLocationData_Field.RegionHandle = unpackLong(bytebuffer);
        StartLocationData_Field.LocationPos = unpackLLVector3(bytebuffer);
        StartLocationData_Field.LocationLookAt = unpackLLVector3(bytebuffer);
    }
}
