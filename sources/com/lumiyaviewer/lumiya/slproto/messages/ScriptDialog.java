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

public class ScriptDialog extends SLMessage
{
    public static class Buttons
    {

        public byte ButtonLabel[];

        public Buttons()
        {
        }
    }

    public static class Data
    {

        public int ChatChannel;
        public byte FirstName[];
        public UUID ImageID;
        public byte LastName[];
        public byte Message[];
        public UUID ObjectID;
        public byte ObjectName[];

        public Data()
        {
        }
    }

    public static class OwnerData
    {

        public UUID OwnerID;

        public OwnerData()
        {
        }
    }


    public ArrayList Buttons_Fields;
    public Data Data_Field;
    public ArrayList OwnerData_Fields;

    public ScriptDialog()
    {
        Buttons_Fields = new ArrayList();
        OwnerData_Fields = new ArrayList();
        zeroCoded = true;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        int i = Data_Field.FirstName.length;
        int j = Data_Field.LastName.length;
        int k = Data_Field.ObjectName.length;
        int l = Data_Field.Message.length;
        Iterator iterator = Buttons_Fields.iterator();
        for (i = i + 17 + 1 + j + 1 + k + 2 + l + 4 + 16 + 4 + 1; iterator.hasNext(); i = ((Buttons)iterator.next()).ButtonLabel.length + 1 + i) { }
        return i + 1 + OwnerData_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptDialog(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-66);
        packUUID(bytebuffer, Data_Field.ObjectID);
        packVariable(bytebuffer, Data_Field.FirstName, 1);
        packVariable(bytebuffer, Data_Field.LastName, 1);
        packVariable(bytebuffer, Data_Field.ObjectName, 1);
        packVariable(bytebuffer, Data_Field.Message, 2);
        packInt(bytebuffer, Data_Field.ChatChannel);
        packUUID(bytebuffer, Data_Field.ImageID);
        bytebuffer.put((byte)Buttons_Fields.size());
        for (Iterator iterator = Buttons_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, ((Buttons)iterator.next()).ButtonLabel, 1)) { }
        bytebuffer.put((byte)OwnerData_Fields.size());
        for (Iterator iterator1 = OwnerData_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, ((OwnerData)iterator1.next()).OwnerID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        Data_Field.ObjectID = unpackUUID(bytebuffer);
        Data_Field.FirstName = unpackVariable(bytebuffer, 1);
        Data_Field.LastName = unpackVariable(bytebuffer, 1);
        Data_Field.ObjectName = unpackVariable(bytebuffer, 1);
        Data_Field.Message = unpackVariable(bytebuffer, 2);
        Data_Field.ChatChannel = unpackInt(bytebuffer);
        Data_Field.ImageID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Buttons buttons = new Buttons();
            buttons.ButtonLabel = unpackVariable(bytebuffer, 1);
            Buttons_Fields.add(buttons);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            OwnerData ownerdata = new OwnerData();
            ownerdata.OwnerID = unpackUUID(bytebuffer);
            OwnerData_Fields.add(ownerdata);
        }

    }
}
