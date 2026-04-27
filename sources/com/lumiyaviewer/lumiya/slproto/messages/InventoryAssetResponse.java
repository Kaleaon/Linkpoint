// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class InventoryAssetResponse extends SLMessage
{
    public static class QueryData
    {

        public UUID AssetID;
        public boolean IsReadable;
        public UUID QueryID;

        public QueryData()
        {
        }
    }


    public QueryData QueryData_Field;

    public InventoryAssetResponse()
    {
        zeroCoded = false;
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleInventoryAssetResponse(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)27);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packUUID(bytebuffer, QueryData_Field.AssetID);
        packBoolean(bytebuffer, QueryData_Field.IsReadable);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        QueryData_Field.AssetID = unpackUUID(bytebuffer);
        QueryData_Field.IsReadable = unpackBoolean(bytebuffer);
    }
}
