// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelBuy extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public boolean Final;
        public UUID GroupID;
        public boolean IsGroupOwned;
        public int LocalID;
        public boolean RemoveContribution;

        public Data()
        {
        }
    }

    public static class ParcelData
    {

        public int Area;
        public int Price;

        public ParcelData()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;
    public ParcelData ParcelData_Field;

    public ParcelBuy()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return 67;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelBuy(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-43);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, Data_Field.GroupID);
        packBoolean(bytebuffer, Data_Field.IsGroupOwned);
        packBoolean(bytebuffer, Data_Field.RemoveContribution);
        packInt(bytebuffer, Data_Field.LocalID);
        packBoolean(bytebuffer, Data_Field.Final);
        packInt(bytebuffer, ParcelData_Field.Price);
        packInt(bytebuffer, ParcelData_Field.Area);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Data_Field.GroupID = unpackUUID(bytebuffer);
        Data_Field.IsGroupOwned = unpackBoolean(bytebuffer);
        Data_Field.RemoveContribution = unpackBoolean(bytebuffer);
        Data_Field.LocalID = unpackInt(bytebuffer);
        Data_Field.Final = unpackBoolean(bytebuffer);
        ParcelData_Field.Price = unpackInt(bytebuffer);
        ParcelData_Field.Area = unpackInt(bytebuffer);
    }
}
