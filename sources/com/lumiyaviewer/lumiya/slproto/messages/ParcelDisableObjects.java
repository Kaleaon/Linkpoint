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

public class ParcelDisableObjects extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class OwnerIDs
    {

        public UUID OwnerID;

        public OwnerIDs()
        {
        }
    }

    public static class ParcelData
    {

        public int LocalID;
        public int ReturnType;

        public ParcelData()
        {
        }
    }

    public static class TaskIDs
    {

        public UUID TaskID;

        public TaskIDs()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList OwnerIDs_Fields;
    public ParcelData ParcelData_Field;
    public ArrayList TaskIDs_Fields;

    public ParcelDisableObjects()
    {
        TaskIDs_Fields = new ArrayList();
        OwnerIDs_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return TaskIDs_Fields.size() * 16 + 45 + 1 + OwnerIDs_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelDisableObjects(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-55);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, ParcelData_Field.LocalID);
        packInt(bytebuffer, ParcelData_Field.ReturnType);
        bytebuffer.put((byte)TaskIDs_Fields.size());
        for (Iterator iterator = TaskIDs_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((TaskIDs)iterator.next()).TaskID)) { }
        bytebuffer.put((byte)OwnerIDs_Fields.size());
        for (Iterator iterator1 = OwnerIDs_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, ((OwnerIDs)iterator1.next()).OwnerID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ParcelData_Field.LocalID = unpackInt(bytebuffer);
        ParcelData_Field.ReturnType = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            TaskIDs taskids = new TaskIDs();
            taskids.TaskID = unpackUUID(bytebuffer);
            TaskIDs_Fields.add(taskids);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            OwnerIDs ownerids = new OwnerIDs();
            ownerids.OwnerID = unpackUUID(bytebuffer);
            OwnerIDs_Fields.add(ownerids);
        }

    }
}
