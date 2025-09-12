// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class InitiateDownload extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class FileData
    {

        public byte SimFilename[];
        public byte ViewerFilename[];

        public FileData()
        {
        }
    }


    public AgentData AgentData_Field;
    public FileData FileData_Field;

    public InitiateDownload()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        FileData_Field = new FileData();
    }

    public int CalcPayloadSize()
    {
        return FileData_Field.SimFilename.length + 1 + 1 + FileData_Field.ViewerFilename.length + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleInitiateDownload(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-109);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packVariable(bytebuffer, FileData_Field.SimFilename, 1);
        packVariable(bytebuffer, FileData_Field.ViewerFilename, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        FileData_Field.SimFilename = unpackVariable(bytebuffer, 1);
        FileData_Field.ViewerFilename = unpackVariable(bytebuffer, 1);
    }
}
