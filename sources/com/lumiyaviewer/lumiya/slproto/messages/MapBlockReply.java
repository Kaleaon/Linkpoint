// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MapBlockReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int Flags;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public int Access;
        public int Agents;
        public UUID MapImageID;
        public byte Name[];
        public int RegionFlags;
        public int WaterHeight;
        public int X;
        public int Y;

        public Data()
        {
        }
    }

    public static class Size
    {

        public int SizeX;
        public int SizeY;

        public Size()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Data_Fields;
    public ArrayList Size_Fields;

    public MapBlockReply()
    {
        Data_Fields = new ArrayList();
        Size_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = Data_Fields.iterator();
        int i;
        for (i = 25; iterator.hasNext(); i = ((Data)iterator.next()).Name.length + 5 + 1 + 4 + 1 + 1 + 16 + i) { }
        return i + 1 + Size_Fields.size() * 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMapBlockReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-103);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, AgentData_Field.Flags);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, data.MapImageID))
        {
            data = (Data)iterator.next();
            packShort(bytebuffer, (short)data.X);
            packShort(bytebuffer, (short)data.Y);
            packVariable(bytebuffer, data.Name, 1);
            packByte(bytebuffer, (byte)data.Access);
            packInt(bytebuffer, data.RegionFlags);
            packByte(bytebuffer, (byte)data.WaterHeight);
            packByte(bytebuffer, (byte)data.Agents);
        }

        bytebuffer.put((byte)Size_Fields.size());
        Size size;
        for (Iterator iterator1 = Size_Fields.iterator(); iterator1.hasNext(); packShort(bytebuffer, (short)size.SizeY))
        {
            size = (Size)iterator1.next();
            packShort(bytebuffer, (short)size.SizeX);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.X = unpackShort(bytebuffer) & 0xffff;
            data.Y = unpackShort(bytebuffer) & 0xffff;
            data.Name = unpackVariable(bytebuffer, 1);
            data.Access = unpackByte(bytebuffer) & 0xff;
            data.RegionFlags = unpackInt(bytebuffer);
            data.WaterHeight = unpackByte(bytebuffer) & 0xff;
            data.Agents = unpackByte(bytebuffer) & 0xff;
            data.MapImageID = unpackUUID(bytebuffer);
            Data_Fields.add(data);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            Size size = new Size();
            size.SizeX = unpackShort(bytebuffer) & 0xffff;
            size.SizeY = unpackShort(bytebuffer) & 0xffff;
            Size_Fields.add(size);
        }

    }
}
