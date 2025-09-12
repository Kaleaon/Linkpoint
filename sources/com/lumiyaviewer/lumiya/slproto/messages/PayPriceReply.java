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

public class PayPriceReply extends SLMessage
{
    public static class ButtonData
    {

        public int PayButton;

        public ButtonData()
        {
        }
    }

    public static class ObjectData
    {

        public int DefaultPayPrice;
        public UUID ObjectID;

        public ObjectData()
        {
        }
    }


    public ArrayList ButtonData_Fields;
    public ObjectData ObjectData_Field;

    public PayPriceReply()
    {
        ButtonData_Fields = new ArrayList();
        zeroCoded = false;
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return ButtonData_Fields.size() * 4 + 25;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandlePayPriceReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-94);
        packUUID(bytebuffer, ObjectData_Field.ObjectID);
        packInt(bytebuffer, ObjectData_Field.DefaultPayPrice);
        bytebuffer.put((byte)ButtonData_Fields.size());
        for (Iterator iterator = ButtonData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((ButtonData)iterator.next()).PayButton)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ObjectData_Field.ObjectID = unpackUUID(bytebuffer);
        ObjectData_Field.DefaultPayPrice = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ButtonData buttondata = new ButtonData();
            buttondata.PayButton = unpackInt(bytebuffer);
            ButtonData_Fields.add(buttondata);
        }

    }
}
