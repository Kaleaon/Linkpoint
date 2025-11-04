// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptAnswerYes;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.AgentSit;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarSitResponse;
import com.lumiyaviewer.lumiya.slproto.messages.AgentRequestSit;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import java.util.Timer;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import java.util.TimerTask;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import java.util.NoSuchElementException;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import java.util.UUID;

public class SLAvatarControl extends SLModule
{
    private static final int IDLE_AGENT_UPDATE_INTERVAL = 2000;
    public static final float MANUAL_FLY_SPEED = 1.0f;
    public static final float MANUAL_MOVE_SPEED = 1.0f;
    public static final float MANUAL_STRAFE_SPEED = 1.0f;
    public static final float MANUAL_TURN_SPEED = 45.0f;
    private static final int MIN_AGENT_UPDATE_INTERVAL = 200;
    private static final UUID animUUID_Falldown;
    private static final UUID animUUID_Land;
    private static final UUID animUUID_PreJump;
    private static final UUID animUUID_Run;
    private static final UUID animUUID_Softland;
    private static final UUID animUUID_Stand;
    private static final UUID animUUID_Standup;
    private static final UUID animUUID_Walk;
    private volatile int ActiveMotionMask;
    private volatile int AgentMotionMask;
    private volatile boolean AgentWantStand;
    private volatile float agentHeading;
    private final AgentPosition agentPosition;
    private final LLVector3 agentUpdateCameraCenter;
    private final Object agentUpdateScheduleLock;
    private volatile AgentUpdateTimerTask agentUpdateTask;
    private final RequestHandler<SubscriptionSingleKey> avatarStateRequestHandler;
    private final CameraParams cameraParams;
    private final Object cammingLock;
    private volatile boolean enableAgentUpdates;
    private volatile int initialAnimCount;
    private boolean isCamming;
    private volatile boolean isFlying;
    private boolean isManualCamming;
    private boolean isTurning;
    private float lastTurnedAngle;
    private final ResultHandler<SubscriptionSingleKey, MyAvatarState> myAvatarStateResultHandler;
    private volatile boolean needClearAnims;
    private volatile int needFastUpdates;
    private final SLParcelInfo parcelInfo;
    private final Object turningLock;
    private float turningSpeed;
    private long turningStartTime;
    private final UserManager userManager;
    
    static {
        animUUID_PreJump = UUID.fromString("7a4e87fe-de39-6fcb-6223-024b00893244");
        animUUID_Softland = UUID.fromString("f4f00d6e-b9fe-9292-f4cb-0ae06ea58d57");
        animUUID_Falldown = UUID.fromString("666307d9-a860-572d-6fd4-c3ab8865c094");
        animUUID_Land = UUID.fromString("7a17b059-12b2-41b1-570a-186368b6aa6f");
        animUUID_Run = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445");
        animUUID_Walk = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0");
        animUUID_Stand = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f");
        animUUID_Standup = UUID.fromString("3da1d753-028a-5446-24f3-9c9b856d9422");
    }
    
    SLAvatarControl(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.enableAgentUpdates = false;
        this.agentHeading = 0.0f;
        this.AgentMotionMask = 0;
        this.ActiveMotionMask = 0;
        this.AgentWantStand = true;
        this.needClearAnims = true;
        this.initialAnimCount = 5;
        this.needFastUpdates = 10;
        this.cammingLock = new Object();
        this.isCamming = false;
        this.isManualCamming = false;
        this.agentPosition = new AgentPosition();
        this.cameraParams = new CameraParams();
        this.turningLock = new Object();
        this.isTurning = false;
        this.turningSpeed = 0.0f;
        this.turningStartTime = 0L;
        this.lastTurnedAngle = 0.0f;
        this.isFlying = false;
        this.agentUpdateScheduleLock = new Object();
        this.avatarStateRequestHandler = new AsyncRequestHandler<SubscriptionSingleKey>(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                if (SLAvatarControl.this.myAvatarStateResultHandler != null) {
                    SLAvatarControl.this.myAvatarStateResultHandler.onResultData(subscriptionSingleKey, SLAvatarControl.this.getMyAvatarState());
                }
            }
        });
        this.agentUpdateCameraCenter = new LLVector3();
        this.parcelInfo = this.agentCircuit.getGridConnection().parcelInfo;
        this.userManager = UserManager.getUserManager(this.agentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.myAvatarStateResultHandler = this.userManager.getObjectsManager().myAvatarState().attachRequestHandler(this.avatarStateRequestHandler);
        }
        else {
            this.myAvatarStateResultHandler = null;
        }
    }
    
    private void SendAgentAnimation() {
        final AgentAnimation agentAnimation = new AgentAnimation();
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final AgentAnimation.AnimationList e = new AgentAnimation.AnimationList();
        e.AnimID = UUIDPool.ZeroUUID;
        e.StartAnim = false;
        agentAnimation.AnimationList_Fields.add(e);
        agentAnimation.isReliable = true;
        this.SendMessage(agentAnimation);
        final AgentAnimation agentAnimation2 = new AgentAnimation();
        agentAnimation2.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final AgentAnimation.AnimationList e2 = new AgentAnimation.AnimationList();
        e2.AnimID = SLAvatarControl.animUUID_Stand;
        e2.StartAnim = true;
        agentAnimation2.AnimationList_Fields.add(e2);
        agentAnimation2.isReliable = true;
        this.SendMessage(agentAnimation2);
    }
    
    private void SendAgentUpdate(final SLDrawDistance slDrawDistance) {
        if (this.agentPosition.getPosition(this.agentUpdateCameraCenter)) {
            this.ActiveMotionMask = this.AgentMotionMask;
            final AgentUpdate agentUpdate = new AgentUpdate();
            agentUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            if (!this.isCamming) {
                this.agentHeading = this.cameraParams.getHeading();
            }
            final double n = this.agentHeading * 3.141592653589793 / 180.0;
            Debug.Printf("AgentUpdate: agent heading %.2f", this.agentHeading);
            final float n2 = (float)Math.cos(n);
            final float n3 = (float)Math.sin(n);
            final LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, 0.0f, this.agentHeading, LLQuaternion.Order.YZX);
            agentUpdate.AgentData_Field.BodyRotation = mayaQ;
            agentUpdate.AgentData_Field.HeadRotation = mayaQ;
            agentUpdate.AgentData_Field.CameraCenter = this.agentUpdateCameraCenter;
            if (slDrawDistance.is3DViewEnabled()) {
                agentUpdate.AgentData_Field.CameraAtAxis = new LLVector3(n2, n3, 0.0f);
                agentUpdate.AgentData_Field.CameraLeftAxis = new LLVector3(-n3, n2, 0.0f);
                agentUpdate.AgentData_Field.CameraUpAxis = new LLVector3(0.0f, 0.0f, 1.0f);
            }
            else {
                agentUpdate.AgentData_Field.CameraAtAxis = new LLVector3(0.0f, 0.0f, 1.0f);
                agentUpdate.AgentData_Field.CameraLeftAxis = new LLVector3(1.0f, 0.0f, 0.0f);
                agentUpdate.AgentData_Field.CameraUpAxis = new LLVector3(0.0f, 1.0f, 0.0f);
            }
            agentUpdate.AgentData_Field.Far = slDrawDistance.getDrawDistanceForUpdate();
            if (this.needClearAnims) {
                final AgentUpdate.AgentData agentData_Field = agentUpdate.AgentData_Field;
                agentData_Field.ControlFlags |= 0xC000;
            }
            if (this.initialAnimCount > 0) {
                final AgentUpdate.AgentData agentData_Field2 = agentUpdate.AgentData_Field;
                agentData_Field2.ControlFlags |= 0xC000;
                --this.initialAnimCount;
            }
            if (this.AgentWantStand) {
                final AgentUpdate.AgentData agentData_Field3 = agentUpdate.AgentData_Field;
                agentData_Field3.ControlFlags |= 0x1C000;
                this.AgentWantStand = false;
                this.needClearAnims = true;
                this.needFastUpdates = 10;
            }
            else {
                if ((this.ActiveMotionMask & 0x2) != 0x0) {
                    final AgentUpdate.AgentData agentData_Field4 = agentUpdate.AgentData_Field;
                    agentData_Field4.ControlFlags |= 0x401;
                }
                if ((this.ActiveMotionMask & 0x4) != 0x0) {
                    final AgentUpdate.AgentData agentData_Field5 = agentUpdate.AgentData_Field;
                    agentData_Field5.ControlFlags |= 0x402;
                }
                if ((this.ActiveMotionMask & 0x20) != 0x0) {
                    final AgentUpdate.AgentData agentData_Field6 = agentUpdate.AgentData_Field;
                    agentData_Field6.ControlFlags |= 0x804;
                }
                if ((this.ActiveMotionMask & 0x40) != 0x0) {
                    final AgentUpdate.AgentData agentData_Field7 = agentUpdate.AgentData_Field;
                    agentData_Field7.ControlFlags |= 0x808;
                }
                if ((this.ActiveMotionMask & 0x8) != 0x0) {
                    final AgentUpdate.AgentData agentData_Field8 = agentUpdate.AgentData_Field;
                    agentData_Field8.ControlFlags |= 0x1010;
                }
                if ((this.ActiveMotionMask & 0x10) != 0x0) {
                    final AgentUpdate.AgentData agentData_Field9 = agentUpdate.AgentData_Field;
                    agentData_Field9.ControlFlags |= 0x1020;
                }
            }
            if (this.isFlying) {
                final AgentUpdate.AgentData agentData_Field10 = agentUpdate.AgentData_Field;
                agentData_Field10.ControlFlags |= 0x2000;
            }
            agentUpdate.isReliable = false;
            this.SendMessage(agentUpdate);
            if (this.needClearAnims) {
                this.SendAgentAnimation();
                this.needClearAnims = false;
            }
            if (this.needFastUpdates > 0) {
                --this.needFastUpdates;
            }
        }
        this.rescheduleAgentUpdate();
    }
    
    private boolean getIsFlying() {
        synchronized (this) {
            return this.isFlying;
        }
    }
    
    @Nonnull
    private MyAvatarState getMyAvatarState() {
        boolean b = false;
        final boolean isFlying = this.getIsFlying();
        final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
    Label_0130:
        while (true) {
            if (agentAvatar == null) {
                final int localID = 0;
                final boolean b2 = false;
                break Label_0130;
            }
            final SLObjectInfo parentObject = agentAvatar.getParentObject();
            int localID;
            boolean b2;
            if (parentObject != null) {
                localID = parentObject.localID;
                b2 = true;
            }
            else {
                localID = 0;
                b2 = false;
            }
            while (true) {
                Label_0150: {
                    try {
                        Block_9: {
                            for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                                if (!Strings.nullToEmpty(slObjectInfo.getName()).startsWith("#")) {
                                    final int attachmentID = slObjectInfo.attachmentID;
                                    if (attachmentID < 0 || attachmentID >= 56) {
                                        continue;
                                    }
                                    final SLAttachmentPoint slAttachmentPoint = SLAttachmentPoint.attachmentPoints[attachmentID];
                                    if (slAttachmentPoint != null && slAttachmentPoint.isHUD) {
                                        break Block_9;
                                    }
                                    continue;
                                }
                            }
                            break Label_0150;
                        }
                        b = true;
                        return MyAvatarState.create(b2, localID, isFlying, b);
                    }
                    catch (final NoSuchElementException ex) {
                        Debug.Warning(ex);
                        return MyAvatarState.create(b2, localID, isFlying, b);
                    }
                }
                b = false;
                continue Label_0130;
            }
            break;
        }
    }
    
    private void processStopAvatarAnimations() {
        final Iterable<Object> iterable = null;
        final AgentAnimation agentAnimation = new AgentAnimation();
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final AgentAnimation.AnimationList e = new AgentAnimation.AnimationList();
        e.AnimID = UUIDPool.ZeroUUID;
        e.StartAnim = false;
        agentAnimation.AnimationList_Fields.add(e);
        agentAnimation.isReliable = true;
        this.SendMessage(agentAnimation);
        final AgentAnimation agentAnimation2 = new AgentAnimation();
        agentAnimation2.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        Object runningAnimations = iterable;
        if (this.gridConn != null) {
            runningAnimations = iterable;
            if (this.gridConn.parcelInfo != null) {
                final SLObjectAvatarInfo agentAvatar = this.gridConn.parcelInfo.getAgentAvatar();
                runningAnimations = iterable;
                if (agentAvatar != null) {
                    runningAnimations = agentAvatar.getAvatarVisualState().getRunningAnimations();
                }
            }
        }
        if (runningAnimations != null) {
            for (final UUID animID : runningAnimations) {
                if (!animID.equals(SLAvatarControl.animUUID_Stand)) {
                    final AgentAnimation.AnimationList e2 = new AgentAnimation.AnimationList();
                    e2.AnimID = animID;
                    e2.StartAnim = false;
                    agentAnimation2.AnimationList_Fields.add(e2);
                }
            }
        }
        if (agentAnimation2.AnimationList_Fields.size() != 0) {
            agentAnimation2.isReliable = true;
            this.SendMessage(agentAnimation2);
        }
        final AgentAnimation agentAnimation3 = new AgentAnimation();
        agentAnimation3.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation3.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final AgentAnimation.AnimationList e3 = new AgentAnimation.AnimationList();
        e3.AnimID = UUIDPool.ZeroUUID;
        e3.StartAnim = false;
        agentAnimation3.AnimationList_Fields.add(e3);
        agentAnimation3.isReliable = true;
        this.SendMessage(agentAnimation3);
    }
    
    private void rescheduleAgentUpdate() {
        final boolean b = true;
        int n = 0;
        int n3;
        if (this.enableAgentUpdates) {
            if (this.agentPosition.isValid()) {
                final SLDrawDistance drawDistance = this.agentCircuit.getModules().drawDistance;
                int n2 = b ? 1 : 0;
                if (this.AgentMotionMask == this.ActiveMotionMask) {
                    n2 = (b ? 1 : 0);
                    if (!drawDistance.needUpdateDrawDistance()) {
                        n2 = (b ? 1 : 0);
                        if (!drawDistance.is3DViewEnabled()) {
                            n2 = (b ? 1 : 0);
                            if (!this.AgentWantStand) {
                                if (this.needFastUpdates > 0) {
                                    n2 = (b ? 1 : 0);
                                }
                                else {
                                    n2 = 0;
                                }
                            }
                        }
                    }
                }
                if (n2 != 0) {
                    n3 = 200;
                }
                else {
                    n3 = 2000;
                }
                if (this.AgentMotionMask != this.ActiveMotionMask || this.AgentWantStand) {
                    final int n4 = 0;
                    n = n3;
                    n3 = n4;
                }
                else {
                    n = n3;
                }
            }
            else {
                n3 = 0;
            }
        }
        else {
            n3 = 0;
        }
        this.scheduleAgentUpdate(n3, n);
    }
    
    private void scheduleAgentUpdate(final int n, final int n2) {
        int scheduledInterval = 0;
        if (this.agentCircuit == null) {
            return;
        }
        final SLGridConnection gridConnection = this.agentCircuit.getGridConnection();
        if (gridConnection == null) {
            return;
        }
        final Timer timer = gridConnection.getTimer();
        if (timer == null) {
            return;
        }
        synchronized (this.agentUpdateScheduleLock) {
            final AgentUpdateTimerTask agentUpdateTask = this.agentUpdateTask;
            if (agentUpdateTask != null) {
                scheduledInterval = agentUpdateTask.getScheduledInterval();
            }
            if (scheduledInterval != n2 || n < n2) {
                if (agentUpdateTask != null) {
                    agentUpdateTask.cancel();
                    this.agentUpdateTask = null;
                }
                if (n2 != 0) {
                    timer.schedule(this.agentUpdateTask = new AgentUpdateTimerTask(n2, (AgentUpdateTimerTask)null), n, n2);
                }
            }
        }
    }
    
    public void ApplyAvatarAnimation(final SLObjectAvatarInfo slObjectAvatarInfo, final AvatarAnimation avatarAnimation) {
        final HashSet set = new HashSet();
        synchronized (this) {
            for (final AvatarAnimation.AnimationList list : avatarAnimation.AnimationList_Fields) {
                final UUID animID = list.AnimID;
                Debug.Log("Own animation: " + animID.toString() + ", sequence ID = " + list.AnimSequenceID);
                if (animID.equals(SLAvatarControl.animUUID_PreJump) || animID.equals(SLAvatarControl.animUUID_Land) || animID.equals(SLAvatarControl.animUUID_Softland) || animID.equals(SLAvatarControl.animUUID_Standup)) {
                    this.needClearAnims = true;
                }
                set.add(animID);
            }
        }
        monitorexit(this);
        final Collection<? extends E> collection;
        final ImmutableSet<Object> copy = ImmutableSet.copyOf((Collection<?>)collection);
        if (this.userManager != null) {
            this.userManager.getObjectsManager().runningAnimations().setData(SubscriptionSingleKey.Value, copy);
        }
    }
    
    void DisableFastUpdates() {
        Debug.Log("AgentUpdate: Disabling fast updates.");
        this.rescheduleAgentUpdate();
    }
    
    void EnableFastUpdates() {
        Debug.Log("AgentUpdate: Enabling fast updates.");
        this.rescheduleAgentUpdate();
    }
    
    public void ForceSitOnObject(final UUID targetID) {
        if (targetID != null) {
            Debug.Log("AvatarSit: Attempting to sit on object " + targetID.toString());
            final AgentRequestSit agentRequestSit = new AgentRequestSit();
            agentRequestSit.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentRequestSit.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            agentRequestSit.TargetObject_Field.TargetID = targetID;
            agentRequestSit.TargetObject_Field.Offset = new LLVector3();
            agentRequestSit.isReliable = true;
            this.SendMessage(agentRequestSit);
        }
    }
    
    public void ForceStand() {
        synchronized (this) {
            this.AgentWantStand = true;
            this.rescheduleAgentUpdate();
        }
    }
    
    @SLMessageHandler
    public void HandleAvatarSitResponse(final AvatarSitResponse avatarSitResponse) {
        final UUID id = avatarSitResponse.SitObject_Field.ID;
        if (id.getLeastSignificantBits() != 0L || id.getMostSignificantBits() != 0L) {
            Debug.Log("AvatarSit: Got sit response for object " + id.toString());
            final AgentSit agentSit = new AgentSit();
            agentSit.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentSit.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            agentSit.isReliable = true;
            this.SendMessage(agentSit);
        }
        else {
            Debug.Log("AvatarSit: Got null sit response");
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().myAvatarState().detachRequestHandler(this.avatarStateRequestHandler);
        }
        this.scheduleAgentUpdate(0, 0);
    }
    
    @SLMessageHandler
    public void HandleScriptQuestion(final ScriptQuestion scriptQuestion) {
        Debug.Log("ScriptQuestion: ItemID = " + scriptQuestion.Data_Field.ItemID + ", questions = " + String.format("%08x", scriptQuestion.Data_Field.Questions));
        final SLChatPermissionRequestEvent slChatPermissionRequestEvent = new SLChatPermissionRequestEvent(scriptQuestion, this.agentCircuit.getAgentUUID());
        if (slChatPermissionRequestEvent.getQuestions() != 0) {
            this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), slChatPermissionRequestEvent, true);
        }
    }
    
    public void ScriptAnswerYes(final UUID itemID, final UUID taskID, final int questions) {
        final ScriptAnswerYes scriptAnswerYes = new ScriptAnswerYes();
        scriptAnswerYes.AgentData_Field.AgentID = this.circuitInfo.agentID;
        scriptAnswerYes.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        scriptAnswerYes.Data_Field.TaskID = taskID;
        scriptAnswerYes.Data_Field.ItemID = itemID;
        scriptAnswerYes.Data_Field.Questions = questions;
        scriptAnswerYes.isReliable = true;
        this.SendMessage(scriptAnswerYes);
    }
    
    public void SitOnObject(final UUID uuid) {
        if (this.agentCircuit.getModules().rlvController.canSit()) {
            try {
                if (this.parcelInfo != null) {
                    final SLObjectInfo slObjectInfo = this.parcelInfo.allObjectsNearby.get(uuid);
                    final ImmutableVector immutablePosition = this.agentPosition.getImmutablePosition();
                    if (slObjectInfo != null && immutablePosition != null) {
                        final float distanceTo = immutablePosition.getDistanceTo(slObjectInfo.getAbsolutePosition());
                        Debug.Printf("RLV: Distance to object for sitting: %f", distanceTo);
                        if (distanceTo > 1.5f && !this.gridConn.getModules().rlvController.canTeleportBySitting()) {
                            return;
                        }
                    }
                }
            }
            catch (final Exception ex) {
                Debug.Warning(ex);
            }
            this.ForceSitOnObject(uuid);
        }
    }
    
    public void Stand() {
        synchronized (this) {
            if (this.agentCircuit.getModules().rlvController.canStandUp()) {
                this.ForceStand();
            }
        }
    }
    
    public void StartAgentMotion(final int agentMotionMask) {
        final boolean b = false;
        monitorenter(this);
        Label_0036: {
            if ((agentMotionMask & 0x8) == 0x0) {
                final int n = b ? 1 : 0;
                if ((agentMotionMask & 0x10) == 0x0) {
                    break Label_0036;
                }
            }
            int n = b ? 1 : 0;
            try {
                if (!this.isFlying) {
                    this.isFlying = true;
                    n = 1;
                }
                this.AgentMotionMask = agentMotionMask;
                this.rescheduleAgentUpdate();
                monitorexit(this);
                if (n != 0) {
                    this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
                }
            }
            finally {
                monitorexit(this);
            }
        }
    }
    
    public void StopAgentMotion() {
        synchronized (this) {
            if (this.AgentMotionMask != 0) {
                this.AgentMotionMask = 0;
                this.needClearAnims = true;
            }
            this.rescheduleAgentUpdate();
        }
    }
    
    public void StopAvatarAnimations() {
        this.processStopAvatarAnimations();
        this.needClearAnims = true;
        this.needFastUpdates = 10;
        this.AgentMotionMask = 0;
        this.rescheduleAgentUpdate();
    }
    
    public boolean getAgentAndCameraPosition(@Nonnull final LLVector3 p0, @Nonnull final CameraParams p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.agentPosition:Lcom/lumiyaviewer/lumiya/slproto/types/AgentPosition;
        //     4: aload_1        
        //     5: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/AgentPosition.getInterpolatedPosition:(Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;)Z
        //     8: pop            
        //     9: aload_0        
        //    10: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.turningLock:Ljava/lang/Object;
        //    13: astore_3       
        //    14: aload_3        
        //    15: monitorenter   
        //    16: aload_0        
        //    17: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.isTurning:Z
        //    20: ifeq            157
        //    23: invokestatic    java/lang/System.currentTimeMillis:()J
        //    26: aload_0        
        //    27: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.turningStartTime:J
        //    30: lsub           
        //    31: l2f            
        //    32: ldc_w           1000.0
        //    35: fdiv           
        //    36: fstore          4
        //    38: aload_0        
        //    39: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.turningSpeed:F
        //    42: fload           4
        //    44: fmul           
        //    45: fstore          5
        //    47: fload           5
        //    49: aload_0        
        //    50: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.lastTurnedAngle:F
        //    53: fsub           
        //    54: fstore          4
        //    56: aload_0        
        //    57: fload           5
        //    59: putfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.lastTurnedAngle:F
        //    62: aload_3        
        //    63: monitorexit    
        //    64: aload_0        
        //    65: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.cammingLock:Ljava/lang/Object;
        //    68: astore_3       
        //    69: aload_3        
        //    70: monitorenter   
        //    71: aload_0        
        //    72: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.isCamming:Z
        //    75: ifne            135
        //    78: aload_0        
        //    79: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.isManualCamming:Z
        //    82: iconst_1       
        //    83: ixor           
        //    84: ifeq            135
        //    87: fload           4
        //    89: fconst_0       
        //    90: fcmpl          
        //    91: ifeq            104
        //    94: aload_0        
        //    95: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.cameraParams:Lcom/lumiyaviewer/lumiya/slproto/types/CameraParams;
        //    98: fload           4
        //   100: fconst_0       
        //   101: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/CameraParams.rotate:(FF)V
        //   104: aload_0        
        //   105: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.cameraParams:Lcom/lumiyaviewer/lumiya/slproto/types/CameraParams;
        //   108: aload_1        
        //   109: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/CameraParams.setPosition:(Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;)V
        //   112: aload_3        
        //   113: monitorexit    
        //   114: aload_2        
        //   115: aload_0        
        //   116: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.cameraParams:Lcom/lumiyaviewer/lumiya/slproto/types/CameraParams;
        //   119: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/CameraParams.copyFrom:(Lcom/lumiyaviewer/lumiya/slproto/types/CameraParams;)V
        //   122: aload_0        
        //   123: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.cameraParams:Lcom/lumiyaviewer/lumiya/slproto/types/CameraParams;
        //   126: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/CameraParams.isFlinging:()Z
        //   129: ireturn        
        //   130: astore_1       
        //   131: aload_3        
        //   132: monitorexit    
        //   133: aload_1        
        //   134: athrow         
        //   135: aload_0        
        //   136: fload           4
        //   138: aload_0        
        //   139: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.agentHeading:F
        //   142: fadd           
        //   143: invokestatic    com/lumiyaviewer/lumiya/slproto/types/CameraParams.wrapAngle:(F)F
        //   146: putfield        com/lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.agentHeading:F
        //   149: goto            112
        //   152: astore_1       
        //   153: aload_3        
        //   154: monitorexit    
        //   155: aload_1        
        //   156: athrow         
        //   157: fconst_0       
        //   158: fstore          4
        //   160: goto            62
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  16     62     130    135    Any
        //  71     87     152    157    Any
        //  94     104    152    157    Any
        //  104    112    152    157    Any
        //  135    149    152    157    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0104:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public float getAgentHeading() {
        return this.agentHeading;
    }
    
    @Nonnull
    public AgentPosition getAgentPosition() {
        return this.agentPosition;
    }
    
    public boolean getIsManualCamming() {
        synchronized (this.cammingLock) {
            return this.isManualCamming;
        }
    }
    
    public void getVRCamera(final HeadTransformCompat headTransformCompat, @Nonnull final LLVector3 position, @Nonnull final CameraParams cameraParams) {
        this.agentPosition.getInterpolatedPosition(position);
        synchronized (this.cammingLock) {
            if (!this.isManualCamming) {
                this.cameraParams.setPosition(position);
            }
            monitorexit(this.cammingLock);
            cameraParams.getVRCamera(this.cameraParams, headTransformCompat);
        }
    }
    
    public void playAnimation(final UUID animID, final boolean startAnim) {
        final AgentAnimation agentAnimation = new AgentAnimation();
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final AgentAnimation.AnimationList e = new AgentAnimation.AnimationList();
        e.AnimID = animID;
        e.StartAnim = startAnim;
        agentAnimation.AnimationList_Fields.add(e);
        agentAnimation.isReliable = true;
        this.SendMessage(agentAnimation);
    }
    
    public void processCameraFling(final float n, final float n2) {
        synchronized (this.cammingLock) {
            this.cameraParams.fling(n, n2);
        }
    }
    
    public void processCameraRotate(final float n, final float n2) {
        synchronized (this.cammingLock) {
            this.cameraParams.rotate(n, n2);
            if (!this.isCamming) {
                this.agentHeading = this.cameraParams.getHeading();
            }
        }
    }
    
    public void processCameraZoom(final float n, final float n2, final float n3, final float n4, final float n5) {
        synchronized (this.cammingLock) {
            this.isCamming = true;
            this.cameraParams.zoom(n, n2, n3, n4, n5);
        }
    }
    
    public void setAgentHeading(final float heading) {
        synchronized (this.cammingLock) {
            this.cameraParams.setHeading(heading);
            this.agentHeading = this.cameraParams.getHeading();
        }
    }
    
    public void setAgentPosition(@Nonnull final LLVector3 position, @Nullable final LLVector3 llVector3) {
        synchronized (this.cammingLock) {
            this.agentPosition.set(position, llVector3);
            if (!this.cameraParams.isValid() || (!this.isCamming && (this.isManualCamming ^ true))) {
                this.cameraParams.setPosition(position);
            }
            monitorexit(this.cammingLock);
            final SLModules modules = this.agentCircuit.getModules();
            if (modules != null) {
                modules.voice.updateSpatialVoicePosition();
            }
        }
    }
    
    public void setCameraManualControl(final boolean isManualCamming) {
        synchronized (this.cammingLock) {
            if (!(this.isManualCamming = isManualCamming)) {
                this.isCamming = false;
            }
            if (!this.isCamming && (isManualCamming ^ true)) {
                this.cameraParams.setPosition(this.agentPosition.getPosition(), this.agentHeading);
            }
        }
    }
    
    public void setEnableAgentUpdates(final boolean enableAgentUpdates) {
        this.enableAgentUpdates = enableAgentUpdates;
        if (enableAgentUpdates) {
            this.scheduleAgentUpdate(0, 1000);
        }
        else {
            this.scheduleAgentUpdate(0, 0);
        }
    }
    
    public void startCameraManualControl(final float n, final float n2, final float n3, final float n4) {
        synchronized (this.cammingLock) {
            this.isCamming = true;
            this.isManualCamming = true;
            this.cameraParams.startManualControl(n, n2, n3, n4);
        }
    }
    
    public void startTurning(final float turningSpeed) {
        synchronized (this.turningLock) {
            if (!this.isTurning || this.turningSpeed != turningSpeed) {
                this.isTurning = true;
                this.turningSpeed = turningSpeed;
                this.turningStartTime = System.currentTimeMillis();
                this.lastTurnedAngle = 0.0f;
            }
        }
    }
    
    public void stopCameraManualControl() {
        synchronized (this.cammingLock) {
            this.cameraParams.stopManualControl();
        }
    }
    
    public void stopCamming() {
        synchronized (this.cammingLock) {
            if (this.isCamming) {
                this.isCamming = false;
                if (!this.isManualCamming) {
                    this.cameraParams.setPosition(this.agentPosition.getPosition(), this.agentHeading);
                }
            }
        }
    }
    
    public void stopFlying() {
        while (true) {
            int n = 1;
            while (true) {
                synchronized (this) {
                    if (this.isFlying) {
                        this.isFlying = false;
                        this.AgentWantStand = true;
                        monitorexit(this);
                        if (n != 0) {
                            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
                        }
                        return;
                    }
                }
                n = 0;
                continue;
            }
        }
    }
    
    public void stopTurning() {
        synchronized (this.turningLock) {
            this.isTurning = false;
        }
    }
    
    private class AgentUpdateTimerTask extends TimerTask
    {
        private final int scheduledInterval;
        
        private AgentUpdateTimerTask(final int scheduledInterval) {
            this.scheduledInterval = scheduledInterval;
        }
        
        int getScheduledInterval() {
            return this.scheduledInterval;
        }
        
        @Override
        public void run() {
            if (SLAvatarControl.this.enableAgentUpdates) {
                SLAvatarControl.this.SendAgentUpdate(SLAvatarControl.this.agentCircuit.getModules().drawDistance);
            }
        }
    }
}
