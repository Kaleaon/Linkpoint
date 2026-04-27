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

public class ChildAgentUpdate extends SLMessage
{
    public static class AgentAccess
    {

        public int AgentLegacyAccess;
        public int AgentMaxAccess;

        public AgentAccess()
        {
        }
    }

    public static class AgentData
    {

        public UUID ActiveGroupID;
        public int AgentAccess;
        public UUID AgentID;
        public LLVector3 AgentPos;
        public byte AgentTextures[];
        public LLVector3 AgentVel;
        public boolean AlwaysRun;
        public float Aspect;
        public LLVector3 AtAxis;
        public LLQuaternion BodyRotation;
        public LLVector3 Center;
        public boolean ChangedGrid;
        public int ControlFlags;
        public float EnergyLevel;
        public float Far;
        public int GodLevel;
        public LLQuaternion HeadRotation;
        public LLVector3 LeftAxis;
        public int LocomotionState;
        public UUID PreyAgent;
        public long RegionHandle;
        public UUID SessionID;
        public LLVector3 Size;
        public byte Throttles[];
        public LLVector3 UpAxis;
        public int ViewerCircuitCode;

        public AgentData()
        {
        }
    }

    public static class AgentInfo
    {

        public int Flags;

        public AgentInfo()
        {
        }
    }

    public static class AnimationData
    {

        public UUID Animation;
        public UUID ObjectID;

        public AnimationData()
        {
        }
    }

    public static class GranterBlock
    {

        public UUID GranterID;

        public GranterBlock()
        {
        }
    }

    public static class GroupData
    {

        public boolean AcceptNotices;
        public UUID GroupID;
        public long GroupPowers;

        public GroupData()
        {
        }
    }

    public static class NVPairData
    {

        public byte NVPairs[];

        public NVPairData()
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


    public ArrayList AgentAccess_Fields;
    public AgentData AgentData_Field;
    public ArrayList AgentInfo_Fields;
    public ArrayList AnimationData_Fields;
    public ArrayList GranterBlock_Fields;
    public ArrayList GroupData_Fields;
    public ArrayList NVPairData_Fields;
    public ArrayList VisualParam_Fields;

    public ChildAgentUpdate()
    {
        GroupData_Fields = new ArrayList();
        AnimationData_Fields = new ArrayList();
        GranterBlock_Fields = new ArrayList();
        NVPairData_Fields = new ArrayList();
        VisualParam_Fields = new ArrayList();
        AgentAccess_Fields = new ArrayList();
        AgentInfo_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        int i = AgentData_Field.Throttles.length;
        int j = AgentData_Field.AgentTextures.length;
        int k = GroupData_Fields.size();
        int l = AnimationData_Fields.size();
        int i1 = GranterBlock_Fields.size();
        Iterator iterator = NVPairData_Fields.iterator();
        for (i = i + 138 + 4 + 12 + 12 + 4 + 4 + 1 + 1 + 16 + 1 + 2 + j + 16 + 1 + 1 + k * 25 + 1 + l * 32 + 1 + i1 * 16 + 1; iterator.hasNext(); i = ((NVPairData)iterator.next()).NVPairs.length + 2 + i) { }
        return i + 1 + VisualParam_Fields.size() * 1 + 1 + AgentAccess_Fields.size() * 2 + 1 + AgentInfo_Fields.size() * 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleChildAgentUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)25);
        packLong(bytebuffer, AgentData_Field.RegionHandle);
        packInt(bytebuffer, AgentData_Field.ViewerCircuitCode);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packLLVector3(bytebuffer, AgentData_Field.AgentPos);
        packLLVector3(bytebuffer, AgentData_Field.AgentVel);
        packLLVector3(bytebuffer, AgentData_Field.Center);
        packLLVector3(bytebuffer, AgentData_Field.Size);
        packLLVector3(bytebuffer, AgentData_Field.AtAxis);
        packLLVector3(bytebuffer, AgentData_Field.LeftAxis);
        packLLVector3(bytebuffer, AgentData_Field.UpAxis);
        packBoolean(bytebuffer, AgentData_Field.ChangedGrid);
        packFloat(bytebuffer, AgentData_Field.Far);
        packFloat(bytebuffer, AgentData_Field.Aspect);
        packVariable(bytebuffer, AgentData_Field.Throttles, 1);
        packInt(bytebuffer, AgentData_Field.LocomotionState);
        packLLQuaternion(bytebuffer, AgentData_Field.HeadRotation);
        packLLQuaternion(bytebuffer, AgentData_Field.BodyRotation);
        packInt(bytebuffer, AgentData_Field.ControlFlags);
        packFloat(bytebuffer, AgentData_Field.EnergyLevel);
        packByte(bytebuffer, (byte)AgentData_Field.GodLevel);
        packBoolean(bytebuffer, AgentData_Field.AlwaysRun);
        packUUID(bytebuffer, AgentData_Field.PreyAgent);
        packByte(bytebuffer, (byte)AgentData_Field.AgentAccess);
        packVariable(bytebuffer, AgentData_Field.AgentTextures, 2);
        packUUID(bytebuffer, AgentData_Field.ActiveGroupID);
        bytebuffer.put((byte)GroupData_Fields.size());
        GroupData groupdata;
        for (Iterator iterator = GroupData_Fields.iterator(); iterator.hasNext(); packBoolean(bytebuffer, groupdata.AcceptNotices))
        {
            groupdata = (GroupData)iterator.next();
            packUUID(bytebuffer, groupdata.GroupID);
            packLong(bytebuffer, groupdata.GroupPowers);
        }

        bytebuffer.put((byte)AnimationData_Fields.size());
        AnimationData animationdata;
        for (Iterator iterator1 = AnimationData_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, animationdata.ObjectID))
        {
            animationdata = (AnimationData)iterator1.next();
            packUUID(bytebuffer, animationdata.Animation);
        }

        bytebuffer.put((byte)GranterBlock_Fields.size());
        for (Iterator iterator2 = GranterBlock_Fields.iterator(); iterator2.hasNext(); packUUID(bytebuffer, ((GranterBlock)iterator2.next()).GranterID)) { }
        bytebuffer.put((byte)NVPairData_Fields.size());
        for (Iterator iterator3 = NVPairData_Fields.iterator(); iterator3.hasNext(); packVariable(bytebuffer, ((NVPairData)iterator3.next()).NVPairs, 2)) { }
        bytebuffer.put((byte)VisualParam_Fields.size());
        for (Iterator iterator4 = VisualParam_Fields.iterator(); iterator4.hasNext(); packByte(bytebuffer, (byte)((VisualParam)iterator4.next()).ParamValue)) { }
        bytebuffer.put((byte)AgentAccess_Fields.size());
        AgentAccess agentaccess;
        for (Iterator iterator5 = AgentAccess_Fields.iterator(); iterator5.hasNext(); packByte(bytebuffer, (byte)agentaccess.AgentMaxAccess))
        {
            agentaccess = (AgentAccess)iterator5.next();
            packByte(bytebuffer, (byte)agentaccess.AgentLegacyAccess);
        }

        bytebuffer.put((byte)AgentInfo_Fields.size());
        for (Iterator iterator6 = AgentInfo_Fields.iterator(); iterator6.hasNext(); packInt(bytebuffer, ((AgentInfo)iterator6.next()).Flags)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.RegionHandle = unpackLong(bytebuffer);
        AgentData_Field.ViewerCircuitCode = unpackInt(bytebuffer);
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.AgentPos = unpackLLVector3(bytebuffer);
        AgentData_Field.AgentVel = unpackLLVector3(bytebuffer);
        AgentData_Field.Center = unpackLLVector3(bytebuffer);
        AgentData_Field.Size = unpackLLVector3(bytebuffer);
        AgentData_Field.AtAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.LeftAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.UpAxis = unpackLLVector3(bytebuffer);
        AgentData_Field.ChangedGrid = unpackBoolean(bytebuffer);
        AgentData_Field.Far = unpackFloat(bytebuffer);
        AgentData_Field.Aspect = unpackFloat(bytebuffer);
        AgentData_Field.Throttles = unpackVariable(bytebuffer, 1);
        AgentData_Field.LocomotionState = unpackInt(bytebuffer);
        AgentData_Field.HeadRotation = unpackLLQuaternion(bytebuffer);
        AgentData_Field.BodyRotation = unpackLLQuaternion(bytebuffer);
        AgentData_Field.ControlFlags = unpackInt(bytebuffer);
        AgentData_Field.EnergyLevel = unpackFloat(bytebuffer);
        AgentData_Field.GodLevel = unpackByte(bytebuffer) & 0xff;
        AgentData_Field.AlwaysRun = unpackBoolean(bytebuffer);
        AgentData_Field.PreyAgent = unpackUUID(bytebuffer);
        AgentData_Field.AgentAccess = unpackByte(bytebuffer) & 0xff;
        AgentData_Field.AgentTextures = unpackVariable(bytebuffer, 2);
        AgentData_Field.ActiveGroupID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            GroupData groupdata = new GroupData();
            groupdata.GroupID = unpackUUID(bytebuffer);
            groupdata.GroupPowers = unpackLong(bytebuffer);
            groupdata.AcceptNotices = unpackBoolean(bytebuffer);
            GroupData_Fields.add(groupdata);
        }

        byte0 = bytebuffer.get();
        for (int j = 0; j < (byte0 & 0xff); j++)
        {
            AnimationData animationdata = new AnimationData();
            animationdata.Animation = unpackUUID(bytebuffer);
            animationdata.ObjectID = unpackUUID(bytebuffer);
            AnimationData_Fields.add(animationdata);
        }

        byte0 = bytebuffer.get();
        for (int k = 0; k < (byte0 & 0xff); k++)
        {
            GranterBlock granterblock = new GranterBlock();
            granterblock.GranterID = unpackUUID(bytebuffer);
            GranterBlock_Fields.add(granterblock);
        }

        byte0 = bytebuffer.get();
        for (int l = 0; l < (byte0 & 0xff); l++)
        {
            NVPairData nvpairdata = new NVPairData();
            nvpairdata.NVPairs = unpackVariable(bytebuffer, 2);
            NVPairData_Fields.add(nvpairdata);
        }

        byte0 = bytebuffer.get();
        for (int i1 = 0; i1 < (byte0 & 0xff); i1++)
        {
            VisualParam visualparam = new VisualParam();
            visualparam.ParamValue = unpackByte(bytebuffer) & 0xff;
            VisualParam_Fields.add(visualparam);
        }

        byte0 = bytebuffer.get();
        for (int j1 = 0; j1 < (byte0 & 0xff); j1++)
        {
            AgentAccess agentaccess = new AgentAccess();
            agentaccess.AgentLegacyAccess = unpackByte(bytebuffer) & 0xff;
            agentaccess.AgentMaxAccess = unpackByte(bytebuffer) & 0xff;
            AgentAccess_Fields.add(agentaccess);
        }

        byte0 = bytebuffer.get();
        for (int k1 = ((flag) ? 1 : 0); k1 < (byte0 & 0xff); k1++)
        {
            AgentInfo agentinfo = new AgentInfo();
            agentinfo.Flags = unpackInt(bytebuffer);
            AgentInfo_Fields.add(agentinfo);
        }

    }
}
