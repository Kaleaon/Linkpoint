// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetSimStatusInDatabase extends SLMessage
{
    public static class Data
    {

        public int AgentCount;
        public byte HostName[];
        public int PID;
        public UUID RegionID;
        public byte Status[];
        public int TimeToLive;
        public int X;
        public int Y;

        public Data()
        {
        }
    }


    public Data Data_Field;

    public SetSimStatusInDatabase()
    {
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + Data_Field.Status.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetSimStatusInDatabase(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)22);
        packUUID(bytebuffer, Data_Field.RegionID);
        packVariable(bytebuffer, Data_Field.HostName, 1);
        packInt(bytebuffer, Data_Field.X);
        packInt(bytebuffer, Data_Field.Y);
        packInt(bytebuffer, Data_Field.PID);
        packInt(bytebuffer, Data_Field.AgentCount);
        packInt(bytebuffer, Data_Field.TimeToLive);
        packVariable(bytebuffer, Data_Field.Status, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.RegionID = unpackUUID(bytebuffer);
        Data_Field.HostName = unpackVariable(bytebuffer, 1);
        Data_Field.X = unpackInt(bytebuffer);
        Data_Field.Y = unpackInt(bytebuffer);
        Data_Field.PID = unpackInt(bytebuffer);
        Data_Field.AgentCount = unpackInt(bytebuffer);
        Data_Field.TimeToLive = unpackInt(bytebuffer);
        Data_Field.Status = unpackVariable(bytebuffer, 1);
    }
}
