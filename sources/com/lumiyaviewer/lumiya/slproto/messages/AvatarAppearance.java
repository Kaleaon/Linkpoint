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

public class AvatarAppearance extends SLMessage
{
    public static class AppearanceData
    {

        public int AppearanceVersion;
        public int CofVersion;
        public int Flags;

        public AppearanceData()
        {
        }
    }

    public static class ObjectData
    {

        public byte TextureEntry[];

        public ObjectData()
        {
        }
    }

    public static class Sender
    {

        public UUID ID;
        public boolean IsTrial;

        public Sender()
        {
        }
    }

    public static class VisualParam
    {

        public int ParamValue;

        public VisualParam()
        {
        }
    }


    public ArrayList AppearanceData_Fields;
    public ObjectData ObjectData_Field;
    public Sender Sender_Field;
    public ArrayList VisualParam_Fields;

    public AvatarAppearance()
    {
        VisualParam_Fields = new ArrayList();
        AppearanceData_Fields = new ArrayList();
        zeroCoded = true;
        Sender_Field = new Sender();
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Field.TextureEntry.length + 2 + 21 + 1 + VisualParam_Fields.size() * 1 + 1 + AppearanceData_Fields.size() * 9;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarAppearance(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-98);
        packUUID(bytebuffer, Sender_Field.ID);
        packBoolean(bytebuffer, Sender_Field.IsTrial);
        packVariable(bytebuffer, ObjectData_Field.TextureEntry, 2);
        bytebuffer.put((byte)VisualParam_Fields.size());
        for (Iterator iterator = VisualParam_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)((VisualParam)iterator.next()).ParamValue)) { }
        bytebuffer.put((byte)AppearanceData_Fields.size());
        AppearanceData appearancedata;
        for (Iterator iterator1 = AppearanceData_Fields.iterator(); iterator1.hasNext(); packInt(bytebuffer, appearancedata.Flags))
        {
            appearancedata = (AppearanceData)iterator1.next();
            packByte(bytebuffer, (byte)appearancedata.AppearanceVersion);
            packInt(bytebuffer, appearancedata.CofVersion);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        Sender_Field.ID = unpackUUID(bytebuffer);
        Sender_Field.IsTrial = unpackBoolean(bytebuffer);
        ObjectData_Field.TextureEntry = unpackVariable(bytebuffer, 2);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            VisualParam visualparam = new VisualParam();
            visualparam.ParamValue = unpackByte(bytebuffer) & 0xff;
            VisualParam_Fields.add(visualparam);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            AppearanceData appearancedata = new AppearanceData();
            appearancedata.AppearanceVersion = unpackByte(bytebuffer) & 0xff;
            appearancedata.CofVersion = unpackInt(bytebuffer);
            appearancedata.Flags = unpackInt(bytebuffer);
            AppearanceData_Fields.add(appearancedata);
        }

    }
}
