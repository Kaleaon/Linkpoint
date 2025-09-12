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

public class SimCrashed extends SLMessage
{
    public static class Data
    {

        public int RegionX;
        public int RegionY;

        public Data()
        {
        }
    }

    public static class Users
    {

        public UUID AgentID;

        public Users()
        {
        }
    }


    public Data Data_Field;
    public ArrayList Users_Fields;

    public SimCrashed()
    {
        Users_Fields = new ArrayList();
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Users_Fields.size() * 16 + 13;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimCrashed(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)72);
        packInt(bytebuffer, Data_Field.RegionX);
        packInt(bytebuffer, Data_Field.RegionY);
        bytebuffer.put((byte)Users_Fields.size());
        for (Iterator iterator = Users_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((Users)iterator.next()).AgentID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.RegionX = unpackInt(bytebuffer);
        Data_Field.RegionY = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Users users = new Users();
            users.AgentID = unpackUUID(bytebuffer);
            Users_Fields.add(users);
        }

    }
}
