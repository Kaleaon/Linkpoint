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

public class AvatarTextureUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public boolean TexturesChanged;

        public AgentData()
        {
        }
    }

    public static class TextureData
    {

        public UUID TextureID;

        public TextureData()
        {
        }
    }

    public static class WearableData
    {

        public UUID CacheID;
        public byte HostName[];
        public int TextureIndex;

        public WearableData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList TextureData_Fields;
    public ArrayList WearableData_Fields;

    public AvatarTextureUpdate()
    {
        WearableData_Fields = new ArrayList();
        TextureData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = WearableData_Fields.iterator();
        int i;
        for (i = 22; iterator.hasNext(); i = ((WearableData)iterator.next()).HostName.length + 18 + i) { }
        return i + 1 + TextureData_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarTextureUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)4);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packBoolean(bytebuffer, AgentData_Field.TexturesChanged);
        bytebuffer.put((byte)WearableData_Fields.size());
        WearableData wearabledata;
        for (Iterator iterator = WearableData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, wearabledata.HostName, 1))
        {
            wearabledata = (WearableData)iterator.next();
            packUUID(bytebuffer, wearabledata.CacheID);
            packByte(bytebuffer, (byte)wearabledata.TextureIndex);
        }

        bytebuffer.put((byte)TextureData_Fields.size());
        for (Iterator iterator1 = TextureData_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, ((TextureData)iterator1.next()).TextureID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.TexturesChanged = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            WearableData wearabledata = new WearableData();
            wearabledata.CacheID = unpackUUID(bytebuffer);
            wearabledata.TextureIndex = unpackByte(bytebuffer) & 0xff;
            wearabledata.HostName = unpackVariable(bytebuffer, 1);
            WearableData_Fields.add(wearabledata);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            TextureData texturedata = new TextureData();
            texturedata.TextureID = unpackUUID(bytebuffer);
            TextureData_Fields.add(texturedata);
        }

    }
}
