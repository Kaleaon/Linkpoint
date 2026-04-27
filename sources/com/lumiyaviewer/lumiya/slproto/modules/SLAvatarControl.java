// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import com.lumiyaviewer.lumiya.render.avatar.AvatarVisualState;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLConnection;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AgentRequestSit;
import com.lumiyaviewer.lumiya.slproto.messages.AgentSit;
import com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarSitResponse;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptAnswerYes;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLDrawDistance, SLModules

public class SLAvatarControl extends SLModule
{
    private class AgentUpdateTimerTask extends TimerTask
    {

        private final int scheduledInterval;
        final SLAvatarControl this$0;

        int getScheduledInterval()
        {
            return scheduledInterval;
        }

        public void run()
        {
            if (SLAvatarControl._2D_get0(SLAvatarControl.this))
            {
                SLAvatarControl._2D_wrap1(SLAvatarControl.this, agentCircuit.getModules().drawDistance);
            }
        }

        private AgentUpdateTimerTask(int i)
        {
            this$0 = SLAvatarControl.this;
            super();
            scheduledInterval = i;
        }

        AgentUpdateTimerTask(int i, AgentUpdateTimerTask agentupdatetimertask)
        {
            this(i);
        }
    }


    private static final int IDLE_AGENT_UPDATE_INTERVAL = 2000;
    public static final float MANUAL_FLY_SPEED = 1F;
    public static final float MANUAL_MOVE_SPEED = 1F;
    public static final float MANUAL_STRAFE_SPEED = 1F;
    public static final float MANUAL_TURN_SPEED = 45F;
    private static final int MIN_AGENT_UPDATE_INTERVAL = 200;
    private static final UUID animUUID_Falldown = UUID.fromString("666307d9-a860-572d-6fd4-c3ab8865c094");
    private static final UUID animUUID_Land = UUID.fromString("7a17b059-12b2-41b1-570a-186368b6aa6f");
    private static final UUID animUUID_PreJump = UUID.fromString("7a4e87fe-de39-6fcb-6223-024b00893244");
    private static final UUID animUUID_Run = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445");
    private static final UUID animUUID_Softland = UUID.fromString("f4f00d6e-b9fe-9292-f4cb-0ae06ea58d57");
    private static final UUID animUUID_Stand = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f");
    private static final UUID animUUID_Standup = UUID.fromString("3da1d753-028a-5446-24f3-9c9b856d9422");
    private static final UUID animUUID_Walk = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0");
    private volatile int ActiveMotionMask;
    private volatile int AgentMotionMask;
    private volatile boolean AgentWantStand;
    private volatile float agentHeading;
    private final AgentPosition agentPosition = new AgentPosition();
    private final LLVector3 agentUpdateCameraCenter = new LLVector3();
    private final Object agentUpdateScheduleLock = new Object();
    private volatile AgentUpdateTimerTask agentUpdateTask;
    private final RequestHandler avatarStateRequestHandler;
    private final CameraParams cameraParams = new CameraParams();
    private final Object cammingLock = new Object();
    private volatile boolean enableAgentUpdates;
    private volatile int initialAnimCount;
    private boolean isCamming;
    private volatile boolean isFlying;
    private boolean isManualCamming;
    private boolean isTurning;
    private float lastTurnedAngle;
    private final ResultHandler myAvatarStateResultHandler;
    private volatile boolean needClearAnims;
    private volatile int needFastUpdates;
    private final SLParcelInfo parcelInfo;
    private final Object turningLock = new Object();
    private float turningSpeed;
    private long turningStartTime;
    private final UserManager userManager;

    static boolean _2D_get0(SLAvatarControl slavatarcontrol)
    {
        return slavatarcontrol.enableAgentUpdates;
    }

    static ResultHandler _2D_get1(SLAvatarControl slavatarcontrol)
    {
        return slavatarcontrol.myAvatarStateResultHandler;
    }

    static MyAvatarState _2D_wrap0(SLAvatarControl slavatarcontrol)
    {
        return slavatarcontrol.getMyAvatarState();
    }

    static void _2D_wrap1(SLAvatarControl slavatarcontrol, SLDrawDistance sldrawdistance)
    {
        slavatarcontrol.SendAgentUpdate(sldrawdistance);
    }

    SLAvatarControl(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        enableAgentUpdates = false;
        agentHeading = 0.0F;
        AgentMotionMask = 0;
        ActiveMotionMask = 0;
        AgentWantStand = true;
        needClearAnims = true;
        initialAnimCount = 5;
        needFastUpdates = 10;
        isCamming = false;
        isManualCamming = false;
        isTurning = false;
        turningSpeed = 0.0F;
        turningStartTime = 0L;
        lastTurnedAngle = 0.0F;
        isFlying = false;
        avatarStateRequestHandler = new AsyncRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLAvatarControl this$0;

            public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
            {
                if (SLAvatarControl._2D_get1(SLAvatarControl.this) != null)
                {
                    SLAvatarControl._2D_get1(SLAvatarControl.this).onResultData(subscriptionsinglekey, SLAvatarControl._2D_wrap0(SLAvatarControl.this));
                }
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((SubscriptionSingleKey)obj);
            }

            
            {
                this$0 = SLAvatarControl.this;
                super();
            }
        });
        parcelInfo = agentCircuit.getGridConnection().parcelInfo;
        userManager = UserManager.getUserManager(agentCircuit.getAgentUUID());
        if (userManager != null)
        {
            myAvatarStateResultHandler = userManager.getObjectsManager().myAvatarState().attachRequestHandler(avatarStateRequestHandler);
            return;
        } else
        {
            myAvatarStateResultHandler = null;
            return;
        }
    }

    private void SendAgentAnimation()
    {
        AgentAnimation agentanimation = new AgentAnimation();
        agentanimation.AgentData_Field.AgentID = circuitInfo.agentID;
        agentanimation.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList animationlist = new com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList();
        animationlist.AnimID = UUIDPool.ZeroUUID;
        animationlist.StartAnim = false;
        agentanimation.AnimationList_Fields.add(animationlist);
        agentanimation.isReliable = true;
        SendMessage(agentanimation);
        agentanimation = new AgentAnimation();
        agentanimation.AgentData_Field.AgentID = circuitInfo.agentID;
        agentanimation.AgentData_Field.SessionID = circuitInfo.sessionID;
        animationlist = new com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList();
        animationlist.AnimID = animUUID_Stand;
        animationlist.StartAnim = true;
        agentanimation.AnimationList_Fields.add(animationlist);
        agentanimation.isReliable = true;
        SendMessage(agentanimation);
    }

    private void SendAgentUpdate(SLDrawDistance sldrawdistance)
    {
        if (!agentPosition.getPosition(agentUpdateCameraCenter)) goto _L2; else goto _L1
_L1:
        AgentUpdate agentupdate;
        ActiveMotionMask = AgentMotionMask;
        agentupdate = new AgentUpdate();
        agentupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        agentupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        if (!isCamming)
        {
            agentHeading = cameraParams.getHeading();
        }
        double d = ((double)agentHeading * 3.1415926535897931D) / 180D;
        Debug.Printf("AgentUpdate: agent heading %.2f", new Object[] {
            Float.valueOf(agentHeading)
        });
        float f = (float)Math.cos(d);
        float f1 = (float)Math.sin(d);
        LLQuaternion llquaternion = LLQuaternion.mayaQ(0.0F, 0.0F, agentHeading, com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.Order.YZX);
        agentupdate.AgentData_Field.BodyRotation = llquaternion;
        agentupdate.AgentData_Field.HeadRotation = llquaternion;
        agentupdate.AgentData_Field.CameraCenter = agentUpdateCameraCenter;
        if (sldrawdistance.is3DViewEnabled())
        {
            agentupdate.AgentData_Field.CameraAtAxis = new LLVector3(f, f1, 0.0F);
            agentupdate.AgentData_Field.CameraLeftAxis = new LLVector3(-f1, f, 0.0F);
            agentupdate.AgentData_Field.CameraUpAxis = new LLVector3(0.0F, 0.0F, 1.0F);
        } else
        {
            agentupdate.AgentData_Field.CameraAtAxis = new LLVector3(0.0F, 0.0F, 1.0F);
            agentupdate.AgentData_Field.CameraLeftAxis = new LLVector3(1.0F, 0.0F, 0.0F);
            agentupdate.AgentData_Field.CameraUpAxis = new LLVector3(0.0F, 1.0F, 0.0F);
        }
        agentupdate.AgentData_Field.Far = sldrawdistance.getDrawDistanceForUpdate();
        if (needClearAnims)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0xc000;
        }
        if (initialAnimCount > 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0xc000;
            initialAnimCount = initialAnimCount - 1;
        }
        if (!AgentWantStand) goto _L4; else goto _L3
_L3:
        sldrawdistance = agentupdate.AgentData_Field;
        sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x1c000;
        AgentWantStand = false;
        needClearAnims = true;
        needFastUpdates = 10;
_L6:
        if (isFlying)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x2000;
        }
        agentupdate.isReliable = false;
        SendMessage(agentupdate);
        if (needClearAnims)
        {
            SendAgentAnimation();
            needClearAnims = false;
        }
        if (needFastUpdates > 0)
        {
            needFastUpdates = needFastUpdates - 1;
        }
_L2:
        rescheduleAgentUpdate();
        return;
_L4:
        if ((ActiveMotionMask & 2) != 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x401;
        }
        if ((ActiveMotionMask & 4) != 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x402;
        }
        if ((ActiveMotionMask & 0x20) != 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x804;
        }
        if ((ActiveMotionMask & 0x40) != 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x808;
        }
        if ((ActiveMotionMask & 8) != 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x1010;
        }
        if ((ActiveMotionMask & 0x10) != 0)
        {
            sldrawdistance = agentupdate.AgentData_Field;
            sldrawdistance.ControlFlags = ((com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate.AgentData) (sldrawdistance)).ControlFlags | 0x1020;
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    private boolean getIsFlying()
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = isFlying;
        this;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    private MyAvatarState getMyAvatarState()
    {
        Object obj;
        boolean flag1;
        boolean flag2;
        flag1 = false;
        flag2 = getIsFlying();
        obj = parcelInfo.getAgentAvatar();
        if (obj == null) goto _L2; else goto _L1
_L1:
        int i;
        boolean flag;
        Object obj1 = ((SLObjectAvatarInfo) (obj)).getParentObject();
        NoSuchElementException nosuchelementexception;
        int j;
        boolean flag3;
        if (obj1 != null)
        {
            i = ((SLObjectInfo) (obj1)).localID;
            flag = true;
        } else
        {
            i = 0;
            flag = false;
        }
        obj = ((SLObjectAvatarInfo) (obj)).treeNode.iterator();
_L6:
        if (!((Iterator) (obj)).hasNext()) goto _L4; else goto _L3
_L3:
        obj1 = (SLObjectInfo)((Iterator) (obj)).next();
        if (Strings.nullToEmpty(((SLObjectInfo) (obj1)).getName()).startsWith("#")) goto _L6; else goto _L5
_L5:
        j = ((SLObjectInfo) (obj1)).attachmentID;
        if (j < 0 || j >= 56) goto _L6; else goto _L7
_L7:
        try
        {
            obj1 = SLAttachmentPoint.attachmentPoints[j];
        }
        // Misplaced declaration of an exception variable
        catch (NoSuchElementException nosuchelementexception)
        {
            Debug.Warning(nosuchelementexception);
            continue; /* Loop/switch isn't completed */
        }
        if (obj1 == null) goto _L6; else goto _L8
_L8:
        flag3 = ((SLAttachmentPoint) (obj1)).isHUD;
        if (!flag3) goto _L6; else goto _L9
_L9:
        flag1 = true;
_L11:
        return MyAvatarState.create(flag, i, flag2, flag1);
_L4:
        flag1 = false;
        continue; /* Loop/switch isn't completed */
_L2:
        i = 0;
        flag = false;
        if (true) goto _L11; else goto _L10
_L10:
    }

    private void processStopAvatarAnimations()
    {
        Object obj1 = null;
        Object obj = new AgentAnimation();
        ((AgentAnimation) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((AgentAnimation) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        Object obj2 = new com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList();
        obj2.AnimID = UUIDPool.ZeroUUID;
        obj2.StartAnim = false;
        ((AgentAnimation) (obj)).AnimationList_Fields.add(obj2);
        obj.isReliable = true;
        SendMessage(((com.lumiyaviewer.lumiya.slproto.SLMessage) (obj)));
        obj2 = new AgentAnimation();
        ((AgentAnimation) (obj2)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((AgentAnimation) (obj2)).AgentData_Field.SessionID = circuitInfo.sessionID;
        obj = obj1;
        if (gridConn != null)
        {
            obj = obj1;
            if (gridConn.parcelInfo != null)
            {
                SLObjectAvatarInfo slobjectavatarinfo = gridConn.parcelInfo.getAgentAvatar();
                obj = obj1;
                if (slobjectavatarinfo != null)
                {
                    obj = slobjectavatarinfo.getAvatarVisualState().getRunningAnimations();
                }
            }
        }
        if (obj != null)
        {
            obj = ((Iterable) (obj)).iterator();
            do
            {
                if (!((Iterator) (obj)).hasNext())
                {
                    break;
                }
                obj1 = (UUID)((Iterator) (obj)).next();
                if (!((UUID) (obj1)).equals(animUUID_Stand))
                {
                    com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList animationlist = new com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList();
                    animationlist.AnimID = ((UUID) (obj1));
                    animationlist.StartAnim = false;
                    ((AgentAnimation) (obj2)).AnimationList_Fields.add(animationlist);
                }
            } while (true);
        }
        if (((AgentAnimation) (obj2)).AnimationList_Fields.size() != 0)
        {
            obj2.isReliable = true;
            SendMessage(((com.lumiyaviewer.lumiya.slproto.SLMessage) (obj2)));
        }
        obj = new AgentAnimation();
        ((AgentAnimation) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((AgentAnimation) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        obj1 = new com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList();
        obj1.AnimID = UUIDPool.ZeroUUID;
        obj1.StartAnim = false;
        ((AgentAnimation) (obj)).AnimationList_Fields.add(obj1);
        obj.isReliable = true;
        SendMessage(((com.lumiyaviewer.lumiya.slproto.SLMessage) (obj)));
    }

    private void rescheduleAgentUpdate()
    {
        boolean flag = true;
        char c1 = '\0';
        char c;
        if (enableAgentUpdates)
        {
            if (agentPosition.isValid())
            {
                SLDrawDistance sldrawdistance = agentCircuit.getModules().drawDistance;
                c = flag;
                if (AgentMotionMask == ActiveMotionMask)
                {
                    c = flag;
                    if (!sldrawdistance.needUpdateDrawDistance())
                    {
                        c = flag;
                        if (!sldrawdistance.is3DViewEnabled())
                        {
                            c = flag;
                            if (!AgentWantStand)
                            {
                                if (needFastUpdates > 0)
                                {
                                    c = flag;
                                } else
                                {
                                    c = '\0';
                                }
                            }
                        }
                    }
                }
                if (c != 0)
                {
                    c = '\310';
                } else
                {
                    c = '\u07D0';
                }
                if (AgentMotionMask != ActiveMotionMask || AgentWantStand)
                {
                    flag = false;
                    c1 = c;
                    c = flag;
                } else
                {
                    c1 = c;
                }
            } else
            {
                c = '\0';
            }
        } else
        {
            c = '\0';
        }
        scheduleAgentUpdate(c, c1);
    }

    private void scheduleAgentUpdate(int i, int j)
    {
        int k = 0;
        if (agentCircuit == null) goto _L2; else goto _L1
_L1:
        Object obj = agentCircuit.getGridConnection();
        if (obj == null) goto _L2; else goto _L3
_L3:
        Timer timer = ((SLConnection) (obj)).getTimer();
        if (timer == null) goto _L2; else goto _L4
_L4:
        obj = agentUpdateScheduleLock;
        obj;
        JVM INSTR monitorenter ;
        AgentUpdateTimerTask agentupdatetimertask = agentUpdateTask;
        if (agentupdatetimertask == null) goto _L6; else goto _L5
_L5:
        k = agentupdatetimertask.getScheduledInterval();
          goto _L6
_L7:
        if (agentupdatetimertask == null)
        {
            break MISSING_BLOCK_LABEL_77;
        }
        agentupdatetimertask.cancel();
        agentUpdateTask = null;
        if (j == 0)
        {
            break MISSING_BLOCK_LABEL_108;
        }
        agentUpdateTask = new AgentUpdateTimerTask(j, null);
        timer.schedule(agentUpdateTask, i, j);
_L8:
        obj;
        JVM INSTR monitorexit ;
_L2:
        return;
        Exception exception;
        exception;
        throw exception;
_L6:
        if (k == j && i >= j) goto _L8; else goto _L7
    }

    public void ApplyAvatarAnimation(SLObjectAvatarInfo slobjectavatarinfo, AvatarAnimation avataranimation)
    {
        slobjectavatarinfo = new HashSet();
        this;
        JVM INSTR monitorenter ;
        UUID uuid;
        for (avataranimation = avataranimation.AnimationList_Fields.iterator(); avataranimation.hasNext(); slobjectavatarinfo.add(uuid))
        {
            com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation.AnimationList animationlist = (com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation.AnimationList)avataranimation.next();
            uuid = animationlist.AnimID;
            Debug.Log((new StringBuilder()).append("Own animation: ").append(uuid.toString()).append(", sequence ID = ").append(animationlist.AnimSequenceID).toString());
            if (uuid.equals(animUUID_PreJump) || uuid.equals(animUUID_Land) || uuid.equals(animUUID_Softland) || uuid.equals(animUUID_Standup))
            {
                needClearAnims = true;
            }
        }

        break MISSING_BLOCK_LABEL_151;
        slobjectavatarinfo;
        throw slobjectavatarinfo;
        this;
        JVM INSTR monitorexit ;
        slobjectavatarinfo = ImmutableSet.copyOf(slobjectavatarinfo);
        if (userManager != null)
        {
            userManager.getObjectsManager().runningAnimations().setData(SubscriptionSingleKey.Value, slobjectavatarinfo);
        }
        return;
    }

    void DisableFastUpdates()
    {
        Debug.Log("AgentUpdate: Disabling fast updates.");
        rescheduleAgentUpdate();
    }

    void EnableFastUpdates()
    {
        Debug.Log("AgentUpdate: Enabling fast updates.");
        rescheduleAgentUpdate();
    }

    public void ForceSitOnObject(UUID uuid)
    {
        if (uuid != null)
        {
            Debug.Log((new StringBuilder()).append("AvatarSit: Attempting to sit on object ").append(uuid.toString()).toString());
            AgentRequestSit agentrequestsit = new AgentRequestSit();
            agentrequestsit.AgentData_Field.AgentID = circuitInfo.agentID;
            agentrequestsit.AgentData_Field.SessionID = circuitInfo.sessionID;
            agentrequestsit.TargetObject_Field.TargetID = uuid;
            agentrequestsit.TargetObject_Field.Offset = new LLVector3();
            agentrequestsit.isReliable = true;
            SendMessage(agentrequestsit);
        }
    }

    public void ForceStand()
    {
        this;
        JVM INSTR monitorenter ;
        AgentWantStand = true;
        rescheduleAgentUpdate();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void HandleAvatarSitResponse(AvatarSitResponse avatarsitresponse)
    {
        avatarsitresponse = avatarsitresponse.SitObject_Field.ID;
        if (avatarsitresponse.getLeastSignificantBits() != 0L || avatarsitresponse.getMostSignificantBits() != 0L)
        {
            Debug.Log((new StringBuilder()).append("AvatarSit: Got sit response for object ").append(avatarsitresponse.toString()).toString());
            avatarsitresponse = new AgentSit();
            ((AgentSit) (avatarsitresponse)).AgentData_Field.AgentID = circuitInfo.agentID;
            ((AgentSit) (avatarsitresponse)).AgentData_Field.SessionID = circuitInfo.sessionID;
            avatarsitresponse.isReliable = true;
            SendMessage(avatarsitresponse);
            return;
        } else
        {
            Debug.Log("AvatarSit: Got null sit response");
            return;
        }
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getObjectsManager().myAvatarState().detachRequestHandler(avatarStateRequestHandler);
        }
        scheduleAgentUpdate(0, 0);
    }

    public void HandleScriptQuestion(ScriptQuestion scriptquestion)
    {
        Debug.Log((new StringBuilder()).append("ScriptQuestion: ItemID = ").append(scriptquestion.Data_Field.ItemID).append(", questions = ").append(String.format("%08x", new Object[] {
            Integer.valueOf(scriptquestion.Data_Field.Questions)
        })).toString());
        scriptquestion = new SLChatPermissionRequestEvent(scriptquestion, agentCircuit.getAgentUUID());
        if (scriptquestion.getQuestions() != 0)
        {
            agentCircuit.HandleChatEvent(agentCircuit.getLocalChatterID(), scriptquestion, true);
        }
    }

    public void ScriptAnswerYes(UUID uuid, UUID uuid1, int i)
    {
        ScriptAnswerYes scriptansweryes = new ScriptAnswerYes();
        scriptansweryes.AgentData_Field.AgentID = circuitInfo.agentID;
        scriptansweryes.AgentData_Field.SessionID = circuitInfo.sessionID;
        scriptansweryes.Data_Field.TaskID = uuid1;
        scriptansweryes.Data_Field.ItemID = uuid;
        scriptansweryes.Data_Field.Questions = i;
        scriptansweryes.isReliable = true;
        SendMessage(scriptansweryes);
    }

    public void SitOnObject(UUID uuid)
    {
        if (!agentCircuit.getModules().rlvController.canSit())
        {
            break MISSING_BLOCK_LABEL_124;
        }
        SLObjectInfo slobjectinfo;
        ImmutableVector immutablevector;
        if (parcelInfo == null)
        {
            break MISSING_BLOCK_LABEL_119;
        }
        slobjectinfo = (SLObjectInfo)parcelInfo.allObjectsNearby.get(uuid);
        immutablevector = agentPosition.getImmutablePosition();
        if (slobjectinfo == null || immutablevector == null)
        {
            break MISSING_BLOCK_LABEL_119;
        }
        float f;
        f = immutablevector.getDistanceTo(slobjectinfo.getAbsolutePosition());
        Debug.Printf("RLV: Distance to object for sitting: %f", new Object[] {
            Float.valueOf(f)
        });
        if (f <= 1.5F)
        {
            break MISSING_BLOCK_LABEL_119;
        }
        boolean flag = gridConn.getModules().rlvController.canTeleportBySitting();
        if (!flag)
        {
            return;
        }
        break MISSING_BLOCK_LABEL_119;
        Exception exception;
        exception;
        Debug.Warning(exception);
        ForceSitOnObject(uuid);
    }

    public void Stand()
    {
        this;
        JVM INSTR monitorenter ;
        if (agentCircuit.getModules().rlvController.canStandUp())
        {
            ForceStand();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void StartAgentMotion(int i)
    {
        boolean flag1 = false;
        this;
        JVM INSTR monitorenter ;
        boolean flag;
        if ((i & 8) == 0)
        {
            flag = flag1;
            if ((i & 0x10) == 0)
            {
                break MISSING_BLOCK_LABEL_39;
            }
        }
        flag = flag1;
        if (isFlying)
        {
            break MISSING_BLOCK_LABEL_39;
        }
        isFlying = true;
        flag = true;
        AgentMotionMask = i;
        rescheduleAgentUpdate();
        this;
        JVM INSTR monitorexit ;
        if (flag)
        {
            userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void StopAgentMotion()
    {
        this;
        JVM INSTR monitorenter ;
        if (AgentMotionMask != 0)
        {
            AgentMotionMask = 0;
            needClearAnims = true;
        }
        rescheduleAgentUpdate();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void StopAvatarAnimations()
    {
        processStopAvatarAnimations();
        needClearAnims = true;
        needFastUpdates = 10;
        AgentMotionMask = 0;
        rescheduleAgentUpdate();
    }

    public boolean getAgentAndCameraPosition(LLVector3 llvector3, CameraParams cameraparams)
    {
        agentPosition.getInterpolatedPosition(llvector3);
        Object obj = turningLock;
        obj;
        JVM INSTR monitorenter ;
        if (!isTurning) goto _L2; else goto _L1
_L1:
        float f;
        f = (float)(System.currentTimeMillis() - turningStartTime) / 1000F;
        float f1 = turningSpeed * f;
        f = f1 - lastTurnedAngle;
        lastTurnedAngle = f1;
_L8:
        obj;
        JVM INSTR monitorexit ;
        obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        if (isCamming || !(isManualCamming ^ true)) goto _L4; else goto _L3
_L3:
        if (f == 0.0F)
        {
            break MISSING_BLOCK_LABEL_104;
        }
        cameraParams.rotate(f, 0.0F);
        cameraParams.setPosition(llvector3);
_L6:
        obj;
        JVM INSTR monitorexit ;
        cameraparams.copyFrom(cameraParams);
        return cameraParams.isFlinging();
        llvector3;
        throw llvector3;
_L4:
        agentHeading = CameraParams.wrapAngle(f + agentHeading);
        if (true) goto _L6; else goto _L5
_L5:
        llvector3;
        throw llvector3;
_L2:
        f = 0.0F;
        if (true) goto _L8; else goto _L7
_L7:
    }

    public float getAgentHeading()
    {
        return agentHeading;
    }

    public AgentPosition getAgentPosition()
    {
        return agentPosition;
    }

    public boolean getIsManualCamming()
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = isManualCamming;
        obj;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public void getVRCamera(HeadTransformCompat headtransformcompat, LLVector3 llvector3, CameraParams cameraparams)
    {
        agentPosition.getInterpolatedPosition(llvector3);
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        if (!isManualCamming)
        {
            cameraParams.setPosition(llvector3);
        }
        obj;
        JVM INSTR monitorexit ;
        cameraparams.getVRCamera(cameraParams, headtransformcompat);
        return;
        headtransformcompat;
        throw headtransformcompat;
    }

    public void playAnimation(UUID uuid, boolean flag)
    {
        AgentAnimation agentanimation = new AgentAnimation();
        agentanimation.AgentData_Field.AgentID = circuitInfo.agentID;
        agentanimation.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList animationlist = new com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation.AnimationList();
        animationlist.AnimID = uuid;
        animationlist.StartAnim = flag;
        agentanimation.AnimationList_Fields.add(animationlist);
        agentanimation.isReliable = true;
        SendMessage(agentanimation);
    }

    public void processCameraFling(float f, float f1)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        cameraParams.fling(f, f1);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void processCameraRotate(float f, float f1)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        cameraParams.rotate(f, f1);
        if (!isCamming)
        {
            agentHeading = cameraParams.getHeading();
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void processCameraZoom(float f, float f1, float f2, float f3, float f4)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        isCamming = true;
        cameraParams.zoom(f, f1, f2, f3, f4);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void setAgentHeading(float f)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        cameraParams.setHeading(f);
        agentHeading = cameraParams.getHeading();
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void setAgentPosition(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        agentPosition.set(llvector3, llvector3_1);
        if (!cameraParams.isValid() || !isCamming && isManualCamming ^ true)
        {
            cameraParams.setPosition(llvector3);
        }
        obj;
        JVM INSTR monitorexit ;
        llvector3 = agentCircuit.getModules();
        if (llvector3 != null)
        {
            ((SLModules) (llvector3)).voice.updateSpatialVoicePosition();
        }
        return;
        llvector3;
        throw llvector3;
    }

    public void setCameraManualControl(boolean flag)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        isManualCamming = flag;
        if (flag)
        {
            break MISSING_BLOCK_LABEL_21;
        }
        isCamming = false;
        if (isCamming || !(flag ^ true))
        {
            break MISSING_BLOCK_LABEL_52;
        }
        cameraParams.setPosition(agentPosition.getPosition(), agentHeading);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void setEnableAgentUpdates(boolean flag)
    {
        enableAgentUpdates = flag;
        if (flag)
        {
            scheduleAgentUpdate(0, 1000);
            return;
        } else
        {
            scheduleAgentUpdate(0, 0);
            return;
        }
    }

    public void startCameraManualControl(float f, float f1, float f2, float f3)
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        isCamming = true;
        isManualCamming = true;
        cameraParams.startManualControl(f, f1, f2, f3);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void startTurning(float f)
    {
        Object obj = turningLock;
        obj;
        JVM INSTR monitorenter ;
        if (!isTurning || turningSpeed != f)
        {
            isTurning = true;
            turningSpeed = f;
            turningStartTime = System.currentTimeMillis();
            lastTurnedAngle = 0.0F;
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void stopCameraManualControl()
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        cameraParams.stopManualControl();
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void stopCamming()
    {
        Object obj = cammingLock;
        obj;
        JVM INSTR monitorenter ;
        if (isCamming)
        {
            isCamming = false;
            if (!isManualCamming)
            {
                cameraParams.setPosition(agentPosition.getPosition(), agentHeading);
            }
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void stopFlying()
    {
        boolean flag = true;
        this;
        JVM INSTR monitorenter ;
        if (!isFlying) goto _L2; else goto _L1
_L1:
        isFlying = false;
        AgentWantStand = true;
_L4:
        this;
        JVM INSTR monitorexit ;
        if (flag)
        {
            userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
        return;
        Exception exception;
        exception;
        throw exception;
_L2:
        flag = false;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void stopTurning()
    {
        Object obj = turningLock;
        obj;
        JVM INSTR monitorenter ;
        isTurning = false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

}
