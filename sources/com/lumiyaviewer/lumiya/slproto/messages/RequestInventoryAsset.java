// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RequestInventoryAsset extends SLMessage
{
    public static class QueryData
    {

        public UUID AgentID;
        public UUID ItemID;
        public UUID OwnerID;
        public UUID QueryID;

        public QueryData()
        {
        }
    }


    public QueryData QueryData_Field;

    public RequestInventoryAsset()
    {
        zeroCoded = false;
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return 68;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRequestInventoryAsset(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)26);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packUUID(bytebuffer, QueryData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.OwnerID);
        packUUID(bytebuffer, QueryData_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        QueryData_Field.AgentID = unpackUUID(bytebuffer);
        QueryData_Field.OwnerID = unpackUUID(bytebuffer);
        QueryData_Field.ItemID = unpackUUID(bytebuffer);
    }
}
