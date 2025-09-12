// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TelehubInfo extends SLMessage
{
    public static class SpawnPointBlock
    {

        public LLVector3 SpawnPointPos;

        public SpawnPointBlock()
        {
        }
    }

    public static class TelehubBlock
    {

        public UUID ObjectID;
        public byte ObjectName[];
        public LLVector3 TelehubPos;
        public LLQuaternion TelehubRot;

        public TelehubBlock()
        {
        }
    }


    public ArrayList SpawnPointBlock_Fields;
    public TelehubBlock TelehubBlock_Field;

    public TelehubInfo()
    {
        SpawnPointBlock_Fields = new ArrayList();
        zeroCoded = false;
        TelehubBlock_Field = new TelehubBlock();
    }

    public int CalcPayloadSize()
    {
        return TelehubBlock_Field.ObjectName.length + 17 + 12 + 12 + 4 + 1 + SpawnPointBlock_Fields.size() * 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTelehubInfo(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)10);
        packUUID(bytebuffer, TelehubBlock_Field.ObjectID);
        packVariable(bytebuffer, TelehubBlock_Field.ObjectName, 1);
        packLLVector3(bytebuffer, TelehubBlock_Field.TelehubPos);
        packLLQuaternion(bytebuffer, TelehubBlock_Field.TelehubRot);
        bytebuffer.put((byte)SpawnPointBlock_Fields.size());
        for (Iterator iterator = SpawnPointBlock_Fields.iterator(); iterator.hasNext(); packLLVector3(bytebuffer, ((SpawnPointBlock)iterator.next()).SpawnPointPos)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TelehubBlock_Field.ObjectID = unpackUUID(bytebuffer);
        TelehubBlock_Field.ObjectName = unpackVariable(bytebuffer, 1);
        TelehubBlock_Field.TelehubPos = unpackLLVector3(bytebuffer);
        TelehubBlock_Field.TelehubRot = unpackLLQuaternion(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            SpawnPointBlock spawnpointblock = new SpawnPointBlock();
            spawnpointblock.SpawnPointPos = unpackLLVector3(bytebuffer);
            SpawnPointBlock_Fields.add(spawnpointblock);
        }

    }
}
