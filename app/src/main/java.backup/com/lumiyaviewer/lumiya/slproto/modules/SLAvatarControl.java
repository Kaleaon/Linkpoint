package com.lumiyaviewer.lumiya.slproto.modules;

import android.support.v4.view.InputDeviceCompat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AgentRequestSit;
import com.lumiyaviewer.lumiya.slproto.messages.AgentSit;
import com.lumiyaviewer.lumiya.slproto.messages.AgentUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarSitResponse;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptAnswerYes;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SLAvatarControl extends SLModule {
    private static final int IDLE_AGENT_UPDATE_INTERVAL = 2000;
    public static final float MANUAL_FLY_SPEED = 1.0f;
    public static final float MANUAL_MOVE_SPEED = 1.0f;
    public static final float MANUAL_STRAFE_SPEED = 1.0f;
    public static final float MANUAL_TURN_SPEED = 45.0f;
    private static final int MIN_AGENT_UPDATE_INTERVAL = 200;
    private static final UUID animUUID_Falldown = UUID.fromString("666307d9-a860-572d-6fd4-c3ab8865c094");
    private static final UUID animUUID_Land = UUID.fromString("7a17b059-12b2-41b1-570a-186368b6aa6f");
    private static final UUID animUUID_PreJump = UUID.fromString("7a4e87fe-de39-6fcb-6223-024b00893244");
    private static final UUID animUUID_Run = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445");
    private static final UUID animUUID_Softland = UUID.fromString("f4f00d6e-b9fe-9292-f4cb-0ae06ea58d57");
    private static final UUID animUUID_Stand = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f");
    private static final UUID animUUID_Standup = UUID.fromString("3da1d753-028a-5446-24f3-9c9b856d9422");
    private static final UUID animUUID_Walk = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0");
    private volatile int ActiveMotionMask = 0;
    private volatile int AgentMotionMask = 0;
    private volatile boolean AgentWantStand = true;
    private volatile float agentHeading = 0.0f;
    private final AgentPosition agentPosition = new AgentPosition();
    private final LLVector3 agentUpdateCameraCenter = new LLVector3();
    private final Object agentUpdateScheduleLock = new Object();
    private volatile AgentUpdateTimerTask agentUpdateTask;
    private final RequestHandler<SubscriptionSingleKey> avatarStateRequestHandler = new AsyncRequestHandler(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() {
        public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            if (SLAvatarControl.this.myAvatarStateResultHandler != null) {
                SLAvatarControl.this.myAvatarStateResultHandler.onResultData(subscriptionSingleKey, SLAvatarControl.this.getMyAvatarState());
            }
        }
    });
    private final CameraParams cameraParams = new CameraParams();
    private final Object cammingLock = new Object();
    /* access modifiers changed from: private */
    public volatile boolean enableAgentUpdates = false;
    private volatile int initialAnimCount = 5;
    private boolean isCamming = false;
    private volatile boolean isFlying = false;
    private boolean isManualCamming = false;
    private boolean isTurning = false;
    private float lastTurnedAngle = 0.0f;
    /* access modifiers changed from: private */
    public final ResultHandler<SubscriptionSingleKey, MyAvatarState> myAvatarStateResultHandler;
    private volatile boolean needClearAnims = true;
    private volatile int needFastUpdates = 10;
    private final SLParcelInfo parcelInfo = this.agentCircuit.getGridConnection().parcelInfo;
    private final Object turningLock = new Object();
    private float turningSpeed = 0.0f;
    private long turningStartTime = 0;
    private final UserManager userManager = UserManager.getUserManager(this.agentCircuit.getAgentUUID());

    private class AgentUpdateTimerTask extends TimerTask {
        private final int scheduledInterval;

        private AgentUpdateTimerTask(int i) {
            this.scheduledInterval = i;
        }

        /* synthetic */ AgentUpdateTimerTask(SLAvatarControl sLAvatarControl, int i, AgentUpdateTimerTask agentUpdateTimerTask) {
            this(i);
        }

        /* access modifiers changed from: package-private */
        public int getScheduledInterval() {
            return this.scheduledInterval;
        }

        public void run() {
            if (SLAvatarControl.this.enableAgentUpdates) {
                SLAvatarControl.this.SendAgentUpdate(SLAvatarControl.this.agentCircuit.getModules().drawDistance);
            }
        }
    }

    SLAvatarControl(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        if (this.userManager != null) {
            this.myAvatarStateResultHandler = this.userManager.getObjectsManager().myAvatarState().attachRequestHandler(this.avatarStateRequestHandler);
        } else {
            this.myAvatarStateResultHandler = null;
        }
    }

    private void SendAgentAnimation() {
        AgentAnimation agentAnimation = new AgentAnimation();
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentAnimation.AnimationList animationList = new AgentAnimation.AnimationList();
        animationList.AnimID = UUIDPool.ZeroUUID;
        animationList.StartAnim = false;
        agentAnimation.AnimationList_Fields.add(animationList);
        agentAnimation.isReliable = true;
        SendMessage(agentAnimation);
        AgentAnimation agentAnimation2 = new AgentAnimation();
        agentAnimation2.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentAnimation.AnimationList animationList2 = new AgentAnimation.AnimationList();
        animationList2.AnimID = animUUID_Stand;
        animationList2.StartAnim = true;
        agentAnimation2.AnimationList_Fields.add(animationList2);
        agentAnimation2.isReliable = true;
        SendMessage(agentAnimation2);
    }

    /* access modifiers changed from: private */
    public void SendAgentUpdate(SLDrawDistance sLDrawDistance) {
        if (this.agentPosition.getPosition(this.agentUpdateCameraCenter)) {
            this.ActiveMotionMask = this.AgentMotionMask;
            AgentUpdate agentUpdate = new AgentUpdate();
            agentUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            if (!this.isCamming) {
                this.agentHeading = this.cameraParams.getHeading();
            }
            double d = (((double) this.agentHeading) * 3.141592653589793d) / 180.0d;
            Debug.Printf("AgentUpdate: agent heading %.2f", Float.valueOf(this.agentHeading));
            float cos = (float) Math.cos(d);
            float sin = (float) Math.sin(d);
            LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, 0.0f, this.agentHeading, LLQuaternion.Order.YZX);
            agentUpdate.AgentData_Field.BodyRotation = mayaQ;
            agentUpdate.AgentData_Field.HeadRotation = mayaQ;
            agentUpdate.AgentData_Field.CameraCenter = this.agentUpdateCameraCenter;
            if (sLDrawDistance.is3DViewEnabled()) {
                agentUpdate.AgentData_Field.CameraAtAxis = new LLVector3(cos, sin, 0.0f);
                agentUpdate.AgentData_Field.CameraLeftAxis = new LLVector3(-sin, cos, 0.0f);
                agentUpdate.AgentData_Field.CameraUpAxis = new LLVector3(0.0f, 0.0f, 1.0f);
            } else {
                agentUpdate.AgentData_Field.CameraAtAxis = new LLVector3(0.0f, 0.0f, 1.0f);
                agentUpdate.AgentData_Field.CameraLeftAxis = new LLVector3(1.0f, 0.0f, 0.0f);
                agentUpdate.AgentData_Field.CameraUpAxis = new LLVector3(0.0f, 1.0f, 0.0f);
            }
            agentUpdate.AgentData_Field.Far = sLDrawDistance.getDrawDistanceForUpdate();
            if (this.needClearAnims) {
                agentUpdate.AgentData_Field.ControlFlags |= 49152;
            }
            if (this.initialAnimCount > 0) {
                agentUpdate.AgentData_Field.ControlFlags |= 49152;
                this.initialAnimCount--;
            }
            if (this.AgentWantStand) {
                agentUpdate.AgentData_Field.ControlFlags |= 114688;
                this.AgentWantStand = false;
                this.needClearAnims = true;
                this.needFastUpdates = 10;
            } else {
                if ((this.ActiveMotionMask & 2) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= InputDeviceCompat.SOURCE_GAMEPAD;
                }
                if ((this.ActiveMotionMask & 4) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 1026;
                }
                if ((this.ActiveMotionMask & 32) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 2052;
                }
                if ((this.ActiveMotionMask & 64) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 2056;
                }
                if ((this.ActiveMotionMask & 8) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 4112;
                }
                if ((this.ActiveMotionMask & 16) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 4128;
                }
            }
            if (this.isFlying) {
                agentUpdate.AgentData_Field.ControlFlags |= 8192;
            }
            agentUpdate.isReliable = false;
            SendMessage(agentUpdate);
            if (this.needClearAnims) {
                SendAgentAnimation();
                this.needClearAnims = false;
            }
            if (this.needFastUpdates > 0) {
                this.needFastUpdates--;
            }
        }
        rescheduleAgentUpdate();
    }

    private synchronized boolean getIsFlying() {
        return this.isFlying;
    }

    /* access modifiers changed from: private */
    @Nonnull
    public MyAvatarState getMyAvatarState() {
        int i;
        boolean z;
        int i2;
        boolean z2;
        boolean z3;
        int i3;
        SLAttachmentPoint sLAttachmentPoint;
        boolean z4 = false;
        boolean isFlying2 = getIsFlying();
        SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
        if (agentAvatar != null) {
            SLObjectInfo parentObject = agentAvatar.getParentObject();
            if (parentObject != null) {
                i2 = parentObject.localID;
                z2 = true;
            } else {
                i2 = 0;
                z2 = false;
            }
            try {
                Iterator it = agentAvatar.treeNode.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z3 = false;
                        break;
                    }
                    SLObjectInfo sLObjectInfo = (SLObjectInfo) it.next();
                    if (!Strings.nullToEmpty(sLObjectInfo.getName()).startsWith("#") && (i3 = sLObjectInfo.attachmentID) >= 0 && i3 < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i3]) != null && sLAttachmentPoint.isHUD) {
                        z3 = true;
                        break;
                    }
                }
                z4 = z3;
                z = z2;
                i = i2;
            } catch (NoSuchElementException e) {
                Debug.Warning(e);
                i = i2;
                z = z2;
            }
        } else {
            i = 0;
            z = false;
        }
        return MyAvatarState.create(z, i, isFlying2, z4);
    }

    private void processStopAvatarAnimations() {
        SLObjectAvatarInfo agentAvatar;
        Set<UUID> set = null;
        AgentAnimation agentAnimation = new AgentAnimation();
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentAnimation.AnimationList animationList = new AgentAnimation.AnimationList();
        animationList.AnimID = UUIDPool.ZeroUUID;
        animationList.StartAnim = false;
        agentAnimation.AnimationList_Fields.add(animationList);
        agentAnimation.isReliable = true;
        SendMessage(agentAnimation);
        AgentAnimation agentAnimation2 = new AgentAnimation();
        agentAnimation2.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        if (!(this.gridConn == null || this.gridConn.parcelInfo == null || (agentAvatar = this.gridConn.parcelInfo.getAgentAvatar()) == null)) {
            set = agentAvatar.getAvatarVisualState().getRunningAnimations();
        }
        if (set != null) {
            for (UUID uuid : set) {
                if (!uuid.equals(animUUID_Stand)) {
                    AgentAnimation.AnimationList animationList2 = new AgentAnimation.AnimationList();
                    animationList2.AnimID = uuid;
                    animationList2.StartAnim = false;
                    agentAnimation2.AnimationList_Fields.add(animationList2);
                }
            }
        }
        if (agentAnimation2.AnimationList_Fields.size() != 0) {
            agentAnimation2.isReliable = true;
            SendMessage(agentAnimation2);
        }
        AgentAnimation agentAnimation3 = new AgentAnimation();
        agentAnimation3.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation3.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentAnimation.AnimationList animationList3 = new AgentAnimation.AnimationList();
        animationList3.AnimID = UUIDPool.ZeroUUID;
        animationList3.StartAnim = false;
        agentAnimation3.AnimationList_Fields.add(animationList3);
        agentAnimation3.isReliable = true;
        SendMessage(agentAnimation3);
    }

    private void rescheduleAgentUpdate() {
        int i;
        boolean z = true;
        int i2 = 0;
        if (!this.enableAgentUpdates) {
            i = 0;
        } else if (this.agentPosition.isValid()) {
            SLDrawDistance sLDrawDistance = this.agentCircuit.getModules().drawDistance;
            if (this.AgentMotionMask == this.ActiveMotionMask && !sLDrawDistance.needUpdateDrawDistance() && !sLDrawDistance.is3DViewEnabled() && !this.AgentWantStand && this.needFastUpdates <= 0) {
                z = false;
            }
            i = z ? 200 : 2000;
            if (this.AgentMotionMask != this.ActiveMotionMask || this.AgentWantStand) {
                i2 = i;
                i = 0;
            } else {
                i2 = i;
            }
        } else {
            i = 0;
        }
        scheduleAgentUpdate(i, i2);
    }

    private void scheduleAgentUpdate(int i, int i2) {
        SLGridConnection gridConnection;
        Timer timer;
        int i3 = 0;
        if (this.agentCircuit != null && (gridConnection = this.agentCircuit.getGridConnection()) != null && (timer = gridConnection.getTimer()) != null) {
            synchronized (this.agentUpdateScheduleLock) {
                AgentUpdateTimerTask agentUpdateTimerTask = this.agentUpdateTask;
                if (agentUpdateTimerTask != null) {
                    i3 = agentUpdateTimerTask.getScheduledInterval();
                }
                if (i3 != i2 || i < i2) {
                    if (agentUpdateTimerTask != null) {
                        agentUpdateTimerTask.cancel();
                        this.agentUpdateTask = null;
                    }
                    if (i2 != 0) {
                        this.agentUpdateTask = new AgentUpdateTimerTask(this, i2, (AgentUpdateTimerTask) null);
                        timer.schedule(this.agentUpdateTask, (long) i, (long) i2);
                    }
                }
            }
        }
    }

    public void ApplyAvatarAnimation(SLObjectAvatarInfo sLObjectAvatarInfo, AvatarAnimation avatarAnimation) {
        HashSet hashSet = new HashSet();
        synchronized (this) {
            for (AvatarAnimation.AnimationList animationList : avatarAnimation.AnimationList_Fields) {
                UUID uuid = animationList.AnimID;
                Debug.Log("Own animation: " + uuid.toString() + ", sequence ID = " + animationList.AnimSequenceID);
                if (uuid.equals(animUUID_PreJump) || uuid.equals(animUUID_Land) || uuid.equals(animUUID_Softland) || uuid.equals(animUUID_Standup)) {
                    this.needClearAnims = true;
                }
                hashSet.add(uuid);
            }
        }
        ImmutableSet copyOf = ImmutableSet.copyOf(hashSet);
        if (this.userManager != null) {
            this.userManager.getObjectsManager().runningAnimations().setData(SubscriptionSingleKey.Value, copyOf);
        }
    }

    /* access modifiers changed from: package-private */
    public void DisableFastUpdates() {
        Debug.Log("AgentUpdate: Disabling fast updates.");
        rescheduleAgentUpdate();
    }

    /* access modifiers changed from: package-private */
    public void EnableFastUpdates() {
        Debug.Log("AgentUpdate: Enabling fast updates.");
        rescheduleAgentUpdate();
    }

    public void ForceSitOnObject(UUID uuid) {
        if (uuid != null) {
            Debug.Log("AvatarSit: Attempting to sit on object " + uuid.toString());
            AgentRequestSit agentRequestSit = new AgentRequestSit();
            agentRequestSit.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentRequestSit.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            agentRequestSit.TargetObject_Field.TargetID = uuid;
            agentRequestSit.TargetObject_Field.Offset = new LLVector3();
            agentRequestSit.isReliable = true;
            SendMessage(agentRequestSit);
        }
    }

    public synchronized void ForceStand() {
        this.AgentWantStand = true;
        rescheduleAgentUpdate();
    }

    @SLMessageHandler
    public void HandleAvatarSitResponse(AvatarSitResponse avatarSitResponse) {
        UUID uuid = avatarSitResponse.SitObject_Field.ID;
        if (uuid.getLeastSignificantBits() == 0 && uuid.getMostSignificantBits() == 0) {
            Debug.Log("AvatarSit: Got null sit response");
            return;
        }
        Debug.Log("AvatarSit: Got sit response for object " + uuid.toString());
        AgentSit agentSit = new AgentSit();
        agentSit.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentSit.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentSit.isReliable = true;
        SendMessage(agentSit);
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().myAvatarState().detachRequestHandler(this.avatarStateRequestHandler);
        }
        scheduleAgentUpdate(0, 0);
    }

    @SLMessageHandler
    public void HandleScriptQuestion(ScriptQuestion scriptQuestion) {
        Debug.Log("ScriptQuestion: ItemID = " + scriptQuestion.Data_Field.ItemID + ", questions = " + String.format("%08x", new Object[]{Integer.valueOf(scriptQuestion.Data_Field.Questions)}));
        SLChatPermissionRequestEvent sLChatPermissionRequestEvent = new SLChatPermissionRequestEvent(scriptQuestion, this.agentCircuit.getAgentUUID());
        if (sLChatPermissionRequestEvent.getQuestions() != 0) {
            this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), sLChatPermissionRequestEvent, true);
        }
    }

    public void ScriptAnswerYes(UUID uuid, UUID uuid2, int i) {
        ScriptAnswerYes scriptAnswerYes = new ScriptAnswerYes();
        scriptAnswerYes.AgentData_Field.AgentID = this.circuitInfo.agentID;
        scriptAnswerYes.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        scriptAnswerYes.Data_Field.TaskID = uuid2;
        scriptAnswerYes.Data_Field.ItemID = uuid;
        scriptAnswerYes.Data_Field.Questions = i;
        scriptAnswerYes.isReliable = true;
        SendMessage(scriptAnswerYes);
    }

    public void SitOnObject(UUID uuid) {
        if (this.agentCircuit.getModules().rlvController.canSit()) {
            try {
                if (this.parcelInfo != null) {
                    SLObjectInfo sLObjectInfo = this.parcelInfo.allObjectsNearby.get(uuid);
                    ImmutableVector immutablePosition = this.agentPosition.getImmutablePosition();
                    if (!(sLObjectInfo == null || immutablePosition == null)) {
                        float distanceTo = immutablePosition.getDistanceTo(sLObjectInfo.getAbsolutePosition());
                        Debug.Printf("RLV: Distance to object for sitting: %f", Float.valueOf(distanceTo));
                        if (distanceTo > 1.5f && !this.gridConn.getModules().rlvController.canTeleportBySitting()) {
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                Debug.Warning(e);
            }
            ForceSitOnObject(uuid);
        }
    }

    public synchronized void Stand() {
        if (this.agentCircuit.getModules().rlvController.canStandUp()) {
            ForceStand();
        }
    }

    public void StartAgentMotion(int i) {
        boolean z = false;
        synchronized (this) {
            if (!((i & 8) == 0 && (i & 16) == 0)) {
                if (!this.isFlying) {
                    this.isFlying = true;
                    z = true;
                }
            }
            this.AgentMotionMask = i;
            rescheduleAgentUpdate();
        }
        if (z) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
    }

    public synchronized void StopAgentMotion() {
        if (this.AgentMotionMask != 0) {
            this.AgentMotionMask = 0;
            this.needClearAnims = true;
        }
        rescheduleAgentUpdate();
    }

    public void StopAvatarAnimations() {
        processStopAvatarAnimations();
        this.needClearAnims = true;
        this.needFastUpdates = 10;
        this.AgentMotionMask = 0;
        rescheduleAgentUpdate();
    }

    public boolean getAgentAndCameraPosition(@Nonnull LLVector3 lLVector3, @Nonnull CameraParams cameraParams2) {
        float f;
        this.agentPosition.getInterpolatedPosition(lLVector3);
        synchronized (this.turningLock) {
            if (this.isTurning) {
                float currentTimeMillis = this.turningSpeed * (((float) (System.currentTimeMillis() - this.turningStartTime)) / 1000.0f);
                f = currentTimeMillis - this.lastTurnedAngle;
                this.lastTurnedAngle = currentTimeMillis;
            } else {
                f = 0.0f;
            }
        }
        synchronized (this.cammingLock) {
            if (this.isCamming || !(!this.isManualCamming)) {
                this.agentHeading = CameraParams.wrapAngle(f + this.agentHeading);
            } else {
                if (f != 0.0f) {
                    this.cameraParams.rotate(f, 0.0f);
                }
                this.cameraParams.setPosition(lLVector3);
            }
        }
        cameraParams2.copyFrom(this.cameraParams);
        return this.cameraParams.isFlinging();
    }

    public float getAgentHeading() {
        return this.agentHeading;
    }

    @Nonnull
    public AgentPosition getAgentPosition() {
        return this.agentPosition;
    }

    public boolean getIsManualCamming() {
        boolean z;
        synchronized (this.cammingLock) {
            z = this.isManualCamming;
        }
        return z;
    }

    public void getVRCamera(HeadTransformCompat headTransformCompat, @Nonnull LLVector3 lLVector3, @Nonnull CameraParams cameraParams2) {
        this.agentPosition.getInterpolatedPosition(lLVector3);
        synchronized (this.cammingLock) {
            if (!this.isManualCamming) {
                this.cameraParams.setPosition(lLVector3);
            }
        }
        cameraParams2.getVRCamera(this.cameraParams, headTransformCompat);
    }

    public void playAnimation(UUID uuid, boolean z) {
        AgentAnimation agentAnimation = new AgentAnimation();
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        AgentAnimation.AnimationList animationList = new AgentAnimation.AnimationList();
        animationList.AnimID = uuid;
        animationList.StartAnim = z;
        agentAnimation.AnimationList_Fields.add(animationList);
        agentAnimation.isReliable = true;
        SendMessage(agentAnimation);
    }

    public void processCameraFling(float f, float f2) {
        synchronized (this.cammingLock) {
            this.cameraParams.fling(f, f2);
        }
    }

    public void processCameraRotate(float f, float f2) {
        synchronized (this.cammingLock) {
            this.cameraParams.rotate(f, f2);
            if (!this.isCamming) {
                this.agentHeading = this.cameraParams.getHeading();
            }
        }
    }

    public void processCameraZoom(float f, float f2, float f3, float f4, float f5) {
        synchronized (this.cammingLock) {
            this.isCamming = true;
            this.cameraParams.zoom(f, f2, f3, f4, f5);
        }
    }

    public void setAgentHeading(float f) {
        synchronized (this.cammingLock) {
            this.cameraParams.setHeading(f);
            this.agentHeading = this.cameraParams.getHeading();
        }
    }

    public void setAgentPosition(@Nonnull LLVector3 lLVector3, @Nullable LLVector3 lLVector32) {
        synchronized (this.cammingLock) {
            this.agentPosition.set(lLVector3, lLVector32);
            if (!this.cameraParams.isValid() || (!this.isCamming && (!this.isManualCamming))) {
                this.cameraParams.setPosition(lLVector3);
            }
        }
        SLModules modules = this.agentCircuit.getModules();
        if (modules != null) {
            modules.voice.updateSpatialVoicePosition();
        }
    }

    public void setCameraManualControl(boolean z) {
        synchronized (this.cammingLock) {
            this.isManualCamming = z;
            if (!z) {
                this.isCamming = false;
            }
            if (!this.isCamming && (!z)) {
                this.cameraParams.setPosition(this.agentPosition.getPosition(), this.agentHeading);
            }
        }
    }

    public void setEnableAgentUpdates(boolean z) {
        this.enableAgentUpdates = z;
        if (z) {
            scheduleAgentUpdate(0, 1000);
        } else {
            scheduleAgentUpdate(0, 0);
        }
    }

    public void startCameraManualControl(float f, float f2, float f3, float f4) {
        synchronized (this.cammingLock) {
            this.isCamming = true;
            this.isManualCamming = true;
            this.cameraParams.startManualControl(f, f2, f3, f4);
        }
    }

    public void startTurning(float f) {
        synchronized (this.turningLock) {
            if (!this.isTurning || this.turningSpeed != f) {
                this.isTurning = true;
                this.turningSpeed = f;
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
        boolean z = true;
        synchronized (this) {
            if (this.isFlying) {
                this.isFlying = false;
                this.AgentWantStand = true;
            } else {
                z = false;
            }
        }
        if (z) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
        }
    }

    public void stopTurning() {
        synchronized (this.turningLock) {
            this.isTurning = false;
        }
    }
}
