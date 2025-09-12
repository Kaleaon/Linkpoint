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

public class AgentAnimation extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class AnimationList
    {

        public UUID AnimID;
        public boolean StartAnim;

        public AnimationList()
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


    public AgentData AgentData_Field;
    public ArrayList AnimationList_Fields;
    public ArrayList PhysicalAvatarEventList_Fields;

    public AgentAnimation()
    {
        AnimationList_Fields = new ArrayList();
        PhysicalAvatarEventList_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        int i = AnimationList_Fields.size();
        Iterator iterator = PhysicalAvatarEventList_Fields.iterator();
        for (i = i * 17 + 34 + 1; iterator.hasNext(); i = ((PhysicalAvatarEventList)iterator.next()).TypeData.length + 1 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentAnimation(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)5);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)AnimationList_Fields.size());
        AnimationList animationlist;
        for (Iterator iterator = AnimationList_Fields.iterator(); iterator.hasNext(); packBoolean(bytebuffer, animationlist.StartAnim))
        {
            animationlist = (AnimationList)iterator.next();
            packUUID(bytebuffer, animationlist.AnimID);
        }

        bytebuffer.put((byte)PhysicalAvatarEventList_Fields.size());
        for (Iterator iterator1 = PhysicalAvatarEventList_Fields.iterator(); iterator1.hasNext(); packVariable(bytebuffer, ((PhysicalAvatarEventList)iterator1.next()).TypeData, 1)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AnimationList animationlist = new AnimationList();
            animationlist.AnimID = unpackUUID(bytebuffer);
            animationlist.StartAnim = unpackBoolean(bytebuffer);
            AnimationList_Fields.add(animationlist);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            PhysicalAvatarEventList physicalavatareventlist = new PhysicalAvatarEventList();
            physicalavatareventlist.TypeData = unpackVariable(bytebuffer, 1);
            PhysicalAvatarEventList_Fields.add(physicalavatareventlist);
        }

    }
}
