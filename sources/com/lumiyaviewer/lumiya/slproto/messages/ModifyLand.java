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

public class ModifyLand extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ModifyBlock
    {

        public int Action;
        public int BrushSize;
        public float Height;
        public float Seconds;

        public ModifyBlock()
        {
        }
    }

    public static class ModifyBlockExtended
    {

        public float BrushSize;

        public ModifyBlockExtended()
        {
        }
    }

    public static class ParcelData
    {

        public float East;
        public int LocalID;
        public float North;
        public float South;
        public float West;

        public ParcelData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ModifyBlockExtended_Fields;
    public ModifyBlock ModifyBlock_Field;
    public ArrayList ParcelData_Fields;

    public ModifyLand()
    {
        ParcelData_Fields = new ArrayList();
        ModifyBlockExtended_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ModifyBlock_Field = new ModifyBlock();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Fields.size() * 20 + 47 + 1 + ModifyBlockExtended_Fields.size() * 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleModifyLand(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)124);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packByte(bytebuffer, (byte)ModifyBlock_Field.Action);
        packByte(bytebuffer, (byte)ModifyBlock_Field.BrushSize);
        packFloat(bytebuffer, ModifyBlock_Field.Seconds);
        packFloat(bytebuffer, ModifyBlock_Field.Height);
        bytebuffer.put((byte)ParcelData_Fields.size());
        ParcelData parceldata;
        for (Iterator iterator = ParcelData_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, parceldata.North))
        {
            parceldata = (ParcelData)iterator.next();
            packInt(bytebuffer, parceldata.LocalID);
            packFloat(bytebuffer, parceldata.West);
            packFloat(bytebuffer, parceldata.South);
            packFloat(bytebuffer, parceldata.East);
        }

        bytebuffer.put((byte)ModifyBlockExtended_Fields.size());
        for (Iterator iterator1 = ModifyBlockExtended_Fields.iterator(); iterator1.hasNext(); packFloat(bytebuffer, ((ModifyBlockExtended)iterator1.next()).BrushSize)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ModifyBlock_Field.Action = unpackByte(bytebuffer) & 0xff;
        ModifyBlock_Field.BrushSize = unpackByte(bytebuffer) & 0xff;
        ModifyBlock_Field.Seconds = unpackFloat(bytebuffer);
        ModifyBlock_Field.Height = unpackFloat(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ParcelData parceldata = new ParcelData();
            parceldata.LocalID = unpackInt(bytebuffer);
            parceldata.West = unpackFloat(bytebuffer);
            parceldata.South = unpackFloat(bytebuffer);
            parceldata.East = unpackFloat(bytebuffer);
            parceldata.North = unpackFloat(bytebuffer);
            ParcelData_Fields.add(parceldata);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            ModifyBlockExtended modifyblockextended = new ModifyBlockExtended();
            modifyblockextended.BrushSize = unpackFloat(bytebuffer);
            ModifyBlockExtended_Fields.add(modifyblockextended);
        }

    }
}
