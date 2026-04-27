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

public class AvatarAnimation extends SLMessage
{
    public static class AnimationList
    {

        public UUID AnimID;
        public int AnimSequenceID;

        public AnimationList()
        {
        }
    }

    public static class AnimationSourceList
    {

        public UUID ObjectID;

        public AnimationSourceList()
        {
        }
    }

    public static class PhysicalAvatarEventList
    {

        public byte TypeData[];

        public PhysicalAvatarEventList()
        {
        }
    }

    public static class Sender
    {

        public UUID ID;

        public Sender()
        {
        }
    }


    public ArrayList AnimationList_Fields;
    public ArrayList AnimationSourceList_Fields;
    public ArrayList PhysicalAvatarEventList_Fields;
    public Sender Sender_Field;

    public AvatarAnimation()
    {
        AnimationList_Fields = new ArrayList();
        AnimationSourceList_Fields = new ArrayList();
        PhysicalAvatarEventList_Fields = new ArrayList();
        zeroCoded = false;
        Sender_Field = new Sender();
    }

    public int CalcPayloadSize()
    {
        int i = AnimationList_Fields.size();
        int j = AnimationSourceList_Fields.size();
        Iterator iterator = PhysicalAvatarEventList_Fields.iterator();
        for (i = i * 20 + 18 + 1 + j * 16 + 1; iterator.hasNext(); i = ((PhysicalAvatarEventList)iterator.next()).TypeData.length + 1 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarAnimation(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)20);
        packUUID(bytebuffer, Sender_Field.ID);
        bytebuffer.put((byte)AnimationList_Fields.size());
        AnimationList animationlist;
        for (Iterator iterator = AnimationList_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, animationlist.AnimSequenceID))
        {
            animationlist = (AnimationList)iterator.next();
            packUUID(bytebuffer, animationlist.AnimID);
        }

        bytebuffer.put((byte)AnimationSourceList_Fields.size());
        for (Iterator iterator1 = AnimationSourceList_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, ((AnimationSourceList)iterator1.next()).ObjectID)) { }
        bytebuffer.put((byte)PhysicalAvatarEventList_Fields.size());
        for (Iterator iterator2 = PhysicalAvatarEventList_Fields.iterator(); iterator2.hasNext(); packVariable(bytebuffer, ((PhysicalAvatarEventList)iterator2.next()).TypeData, 1)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        Sender_Field.ID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AnimationList animationlist = new AnimationList();
            animationlist.AnimID = unpackUUID(bytebuffer);
            animationlist.AnimSequenceID = unpackInt(bytebuffer);
            AnimationList_Fields.add(animationlist);
        }

        byte0 = bytebuffer.get();
        for (int j = 0; j < (byte0 & 0xff); j++)
        {
            AnimationSourceList animationsourcelist = new AnimationSourceList();
            animationsourcelist.ObjectID = unpackUUID(bytebuffer);
            AnimationSourceList_Fields.add(animationsourcelist);
        }

        byte0 = bytebuffer.get();
        for (int k = ((flag) ? 1 : 0); k < (byte0 & 0xff); k++)
        {
            PhysicalAvatarEventList physicalavatareventlist = new PhysicalAvatarEventList();
            physicalavatareventlist.TypeData = unpackVariable(bytebuffer, 1);
            PhysicalAvatarEventList_Fields.add(physicalavatareventlist);
        }

    }
}
