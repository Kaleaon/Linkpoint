package com.linkpoint.slproto.modules

import androidx.core.view.InputDeviceCompat
import com.google.common.base.Strings
import com.google.common.collect.ImmutableSet
import com.linkpoint.Debug
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.render.HeadTransformCompat
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.SLParcelInfo
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.chat.SLChatPermissionRequestEvent
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.AgentAnimation
import com.linkpoint.slproto.messages.AgentRequestSit
import com.linkpoint.slproto.messages.AgentSit
import com.linkpoint.slproto.messages.AgentUpdate
import com.linkpoint.slproto.messages.AvatarAnimation
import com.linkpoint.slproto.messages.AvatarSitResponse
import com.linkpoint.slproto.messages.ScriptAnswerYes
import com.linkpoint.slproto.messages.ScriptQuestion
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.types.AgentPosition
import com.linkpoint.slproto.types.CameraParams
import com.linkpoint.slproto.types.ImmutableVector
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.manager.MyAvatarState
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.util.HashSet
import java.util.Iterator
import java.util.NoSuchElementException
import java.util.Set
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SLAvatarControl : SLModule {
    private val IDLE_AGENT_UPDATE_INTERVAL: Int = 2000
    val MANUAL_FLY_SPEED: Float = 1.0f
    val MANUAL_MOVE_SPEED: Float = 1.0f
    val MANUAL_STRAFE_SPEED: Float = 1.0f
    val MANUAL_TURN_SPEED: Float = 45.0f
    private val MIN_AGENT_UPDATE_INTERVAL: Int = 200
    private val animUUID_Falldown: UUID = UUID.fromString("666307d9-a860-572d-6fd4-c3ab8865c094")
    private val animUUID_Land: UUID = UUID.fromString("7a17b059-12b2-41b1-570a-186368b6aa6f")
    private val animUUID_PreJump: UUID = UUID.fromString("7a4e87fe-de39-6fcb-6223-024b00893244")
    private val animUUID_Run: UUID = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")
    private val animUUID_Softland: UUID = UUID.fromString("f4f00d6e-b9fe-9292-f4cb-0ae06ea58d57")
    private val animUUID_Stand: UUID = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")
    private val animUUID_Standup: UUID = UUID.fromString("3da1d753-028a-5446-24f3-9c9b856d9422")
    private val animUUID_Walk: UUID = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")
    private volatile Int ActiveMotionMask = 0
    private volatile Int AgentMotionMask = 0
    private volatile Boolean AgentWantStand = true
    private volatile Float agentHeading = 0.0f
    private AgentPosition agentPosition = AgentPosition()
    private LLVector3 agentUpdateCameraCenter = LLVector3()
    private Any agentUpdateScheduleLock = Any()
    private volatile AgentUpdateTimerTask agentUpdateTask
    private RequestHandler<SubscriptionSingleKey> avatarStateRequestHandler = AsyncRequestHandler(this.agentCircuit, SimpleRequestHandler<SubscriptionSingleKey>() {
        fun onRequest(@NonNull SubscriptionSingleKey subscriptionSingleKey)  {
            if (SLAvatarControl.this.myAvatarStateResultHandler != null) {
                SLAvatarControl.this.myAvatarStateResultHandler.onResultData(subscriptionSingleKey, SLAvatarControl.this.getMyAvatarState())
            }
        }
    private CameraParams cameraParams = CameraParams()
    private Any cammingLock = Any()
    /* access modifiers changed from: private */
    volatile Boolean enableAgentUpdates = false
    private volatile Int initialAnimCount = 5
    private Boolean isCamming = false
    private volatile Boolean isFlying = false
    private Boolean isManualCamming = false
    private Boolean isTurning = false
    private Float lastTurnedAngle = 0.0f
    /* access modifiers changed from: private */
    ResultHandler<SubscriptionSingleKey, MyAvatarState> myAvatarStateResultHandler
    private volatile Boolean needClearAnims = true
    private volatile Int needFastUpdates = 10
    private SLParcelInfo parcelInfo = this.agentCircuit.getGridConnection().parcelInfo
    private Any turningLock = Any()
    private Float turningSpeed = 0.0f
    private Long turningStartTime = 0
    private UserManager userManager = UserManager.getUserManager(this.agentCircuit.getAgentUUID())

    private class AgentUpdateTimerTask : TimerTask {
        private Int scheduledInterval

        private AgentUpdateTimerTask(Int i) {
            this.scheduledInterval = i
        }

        /* synthetic */ AgentUpdateTimerTask(SLAvatarControl sLAvatarControl, Int i, AgentUpdateTimerTask agentUpdateTimerTask) {
            this(i)
        }

        /* access modifiers changed from: package-private */
        fun getScheduledInterval(): Int {
            return this.scheduledInterval
        }

        fun run()  {
            if (SLAvatarControl.this.enableAgentUpdates) {
                SLAvatarControl.this.SendAgentUpdate(SLAvatarControl.this.agentCircuit.getModules().drawDistance)
            }
        }
    }

    SLAvatarControl(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        if (this.userManager != null) {
            this.myAvatarStateResultHandler = this.userManager.getObjectsManager().myAvatarState().attachRequestHandler(this.avatarStateRequestHandler)
        } else {
            this.myAvatarStateResultHandler = null
        }
    }

    private Unit SendAgentAnimation() {
        AgentAnimation agentAnimation = AgentAnimation()
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentAnimation.AnimationList animationList = AgentAnimation.AnimationList()
        animationList.AnimID = UUIDPool.ZeroUUID
        animationList.StartAnim = false
        agentAnimation.AnimationList_Fields.add(animationList)
        agentAnimation.isReliable = true
        SendMessage(agentAnimation)
        AgentAnimation agentAnimation2 = AgentAnimation()
        agentAnimation2.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentAnimation2.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentAnimation.AnimationList animationList2 = AgentAnimation.AnimationList()
        animationList2.AnimID = animUUID_Stand
        animationList2.StartAnim = true
        agentAnimation2.AnimationList_Fields.add(animationList2)
        agentAnimation2.isReliable = true
        SendMessage(agentAnimation2)
    }

    /* access modifiers changed from: private */
    fun SendAgentUpdate(SLDrawDistance sLDrawDistance)  {
        if (this.agentPosition.getPosition(this.agentUpdateCameraCenter)) {
            this.ActiveMotionMask = this.AgentMotionMask
            AgentUpdate agentUpdate = AgentUpdate()
            agentUpdate.AgentData_Field.AgentID = this.circuitInfo.agentID
            agentUpdate.AgentData_Field.SessionID = this.circuitInfo.sessionID
            if (!this.isCamming) {
                this.agentHeading = this.cameraParams.getHeading()
            }
            Double d = ((this.toDouble().agentHeading) * 3.141592653589793d) / 180.0d
            Debug.Printf("AgentUpdate: agent heading %.2f", Float.valueOf(this.agentHeading))
            var cos: Float = Math.toFloat().cos(d)
            var sin: Float = Math.toFloat().sin(d)
            LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, 0.0f, this.agentHeading, LLQuaternion.Order.YZX)
            agentUpdate.AgentData_Field.BodyRotation = mayaQ
            agentUpdate.AgentData_Field.HeadRotation = mayaQ
            agentUpdate.AgentData_Field.CameraCenter = this.agentUpdateCameraCenter
            if (sLDrawDistance.is3DViewEnabled()) {
                agentUpdate.AgentData_Field.CameraAtAxis = LLVector3(cos, sin, 0.0f)
                agentUpdate.AgentData_Field.CameraLeftAxis = LLVector3(-sin, cos, 0.0f)
                agentUpdate.AgentData_Field.CameraUpAxis = LLVector3(0.0f, 0.0f, 1.0f)
            } else {
                agentUpdate.AgentData_Field.CameraAtAxis = LLVector3(0.0f, 0.0f, 1.0f)
                agentUpdate.AgentData_Field.CameraLeftAxis = LLVector3(1.0f, 0.0f, 0.0f)
                agentUpdate.AgentData_Field.CameraUpAxis = LLVector3(0.0f, 1.0f, 0.0f)
            }
            agentUpdate.AgentData_Field.Far = sLDrawDistance.getDrawDistanceForUpdate()
            if (this.needClearAnims) {
                agentUpdate.AgentData_Field.ControlFlags |= 49152
            }
            if (this.initialAnimCount > 0) {
                agentUpdate.AgentData_Field.ControlFlags |= 49152
                this.initialAnimCount--
            }
            if (this.AgentWantStand) {
                agentUpdate.AgentData_Field.ControlFlags |= 114688
                this.AgentWantStand = false
                this.needClearAnims = true
                this.needFastUpdates = 10
            } else {
                if ((this.ActiveMotionMask & 2) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= InputDeviceCompat.SOURCE_GAMEPAD
                }
                if ((this.ActiveMotionMask & 4) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 1026
                }
                if ((this.ActiveMotionMask & 32) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 2052
                }
                if ((this.ActiveMotionMask & 64) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 2056
                }
                if ((this.ActiveMotionMask & 8) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 4112
                }
                if ((this.ActiveMotionMask & 16) != 0) {
                    agentUpdate.AgentData_Field.ControlFlags |= 4128
                }
            }
            if (this.isFlying) {
                agentUpdate.AgentData_Field.ControlFlags |= 8192
            }
            agentUpdate.isReliable = false
            SendMessage(agentUpdate)
            if (this.needClearAnims) {
                SendAgentAnimation()
                this.needClearAnims = false
            }
            if (this.needFastUpdates > 0) {
                this.needFastUpdates--
            }
        }
        rescheduleAgentUpdate()
    }

    private synchronized Boolean getIsFlying() {
        return this.isFlying
    }

    /* access modifiers changed from: private */
    @NonNull
    fun getMyAvatarState(): MyAvatarState {
        SLAttachmentPoint sLAttachmentPoint
        var z4: Boolean = false
        var isFlying2: Boolean = getIsFlying()
        SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar()
        if (agentAvatar != null) {
            SLObjectInfo parentObject = agentAvatar.getParentObject()
            if (parentObject != null) {
                i2 = parentObject.localID
                z2 = true
            } else {
                i2 = 0
                z2 = false
            }
            try {
                Iterator it = agentAvatar.treeNode.iterator()
                while (true) {
                    if (!it.hasNext()) {
                        z3 = false
                        break
                    }
                    SLObjectInfo sLObjectInfo = (SLObjectInfo) it.next()
                    if (!Strings.nullToEmpty(sLObjectInfo.getName()).startsWith("#") && (i3 = sLObjectInfo.attachmentID) >= 0 && i3 < 56 && (sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i3]) != null && sLAttachmentPoint.isHUD) {
                        z3 = true
                        break
                    }
                }
                z4 = z3
                z = z2
                i = i2
            } catch (NoSuchElementException e) {
                Debug.Warning(e)
                i = i2
                z = z2
            }
        } else {
            i = 0
            z = false
        }
        return MyAvatarState.create(z, i, isFlying2, z4)
    }

    private Unit processStopAvatarAnimations() {
        SLObjectAvatarInfo agentAvatar
        Set<UUID> set = null
        AgentAnimation agentAnimation = AgentAnimation()
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentAnimation.AnimationList animationList = AgentAnimation.AnimationList()
        animationList.AnimID = UUIDPool.ZeroUUID
        animationList.StartAnim = false
        agentAnimation.AnimationList_Fields.add(animationList)
        agentAnimation.isReliable = true
        SendMessage(agentAnimation)
        AgentAnimation agentAnimation2 = AgentAnimation()
        agentAnimation2.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentAnimation2.AgentData_Field.SessionID = this.circuitInfo.sessionID
        if (!(this.gridConn == null || this.gridConn.parcelInfo == null || (agentAvatar = this.gridConn.parcelInfo.getAgentAvatar()) == null)) {
            set = agentAvatar.getAvatarVisualState().getRunningAnimations()
        }
        if (set != null) {
            for (UUID uuid : set) {
                if (!uuid.equals(animUUID_Stand)) {
                    AgentAnimation.AnimationList animationList2 = AgentAnimation.AnimationList()
                    animationList2.AnimID = uuid
                    animationList2.StartAnim = false
                    agentAnimation2.AnimationList_Fields.add(animationList2)
                }
            }
        }
        if (agentAnimation2.AnimationList_Fields.size() != 0) {
            agentAnimation2.isReliable = true
            SendMessage(agentAnimation2)
        }
        AgentAnimation agentAnimation3 = AgentAnimation()
        agentAnimation3.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentAnimation3.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentAnimation.AnimationList animationList3 = AgentAnimation.AnimationList()
        animationList3.AnimID = UUIDPool.ZeroUUID
        animationList3.StartAnim = false
        agentAnimation3.AnimationList_Fields.add(animationList3)
        agentAnimation3.isReliable = true
        SendMessage(agentAnimation3)
    }

    private Unit rescheduleAgentUpdate() {
        var z: Boolean = true
        var i2: Int = 0
        if (!this.enableAgentUpdates) {
            i = 0
        } else if (this.agentPosition.isValid()) {
            SLDrawDistance sLDrawDistance = this.agentCircuit.getModules().drawDistance
            if (this.AgentMotionMask == this.ActiveMotionMask && !sLDrawDistance.needUpdateDrawDistance() && !sLDrawDistance.is3DViewEnabled() && !this.AgentWantStand && this.needFastUpdates <= 0) {
                z = false
            }
            i = z ? 200 : 2000
            if (this.AgentMotionMask != this.ActiveMotionMask || this.AgentWantStand) {
                i2 = i
                i = 0
            } else {
                i2 = i
            }
        } else {
            i = 0
        }
        scheduleAgentUpdate(i, i2)
    }

    private Unit scheduleAgentUpdate(Int i, Int i2) {
        SLGridConnection gridConnection
        Timer timer
        var i3: Int = 0
        if (this.agentCircuit != null && (gridConnection = this.agentCircuit.getGridConnection()) != null && (timer = gridConnection.getTimer()) != null) {
            synchronized (this.agentUpdateScheduleLock) {
                AgentUpdateTimerTask agentUpdateTimerTask = this.agentUpdateTask
                if (agentUpdateTimerTask != null) {
                    i3 = agentUpdateTimerTask.getScheduledInterval()
                }
                if (i3 != i2 || i < i2) {
                    if (agentUpdateTimerTask != null) {
                        agentUpdateTimerTask.cancel()
                        this.agentUpdateTask = null
                    }
                    if (i2 != 0) {
                        this.agentUpdateTask = AgentUpdateTimerTask(this, i2, (AgentUpdateTimerTask) null)
                        timer.schedule(this.agentUpdateTask, (Long) i, (Long) i2)
                    }
                }
            }
        }
    }

    fun ApplyAvatarAnimation(SLObjectAvatarInfo sLObjectAvatarInfo, AvatarAnimation avatarAnimation)  {
        HashSet hashSet = HashSet()
        synchronized (this) {
            for (AvatarAnimation.AnimationList animationList : avatarAnimation.AnimationList_Fields) {
                UUID uuid = animationList.AnimID
                Debug.Log("Own animation: " + uuid.toString() + ", sequence ID = " + animationList.AnimSequenceID)
                if (uuid.equals(animUUID_PreJump) || uuid.equals(animUUID_Land) || uuid.equals(animUUID_Softland) || uuid.equals(animUUID_Standup)) {
                    this.needClearAnims = true
                }
                hashSet.add(uuid)
            }
        }
        ImmutableSet copyOf = ImmutableSet.copyOf(hashSet)
        if (this.userManager != null) {
            this.userManager.getObjectsManager().runningAnimations().setData(SubscriptionSingleKey.Value, copyOf)
        }
    }

    /* access modifiers changed from: package-private */
    fun DisableFastUpdates()  {
        Debug.Log("AgentUpdate: Disabling fast updates.")
        rescheduleAgentUpdate()
    }

    /* access modifiers changed from: package-private */
    fun EnableFastUpdates()  {
        Debug.Log("AgentUpdate: Enabling fast updates.")
        rescheduleAgentUpdate()
    }

    fun ForceSitOnObject(UUID uuid)  {
        if (uuid != null) {
            Debug.Log("AvatarSit: Attempting to sit on object " + uuid.toString())
            AgentRequestSit agentRequestSit = AgentRequestSit()
            agentRequestSit.AgentData_Field.AgentID = this.circuitInfo.agentID
            agentRequestSit.AgentData_Field.SessionID = this.circuitInfo.sessionID
            agentRequestSit.TargetObject_Field.TargetID = uuid
            agentRequestSit.TargetObject_Field.Offset = LLVector3()
            agentRequestSit.isReliable = true
            SendMessage(agentRequestSit)
        }
    }

    synchronized Unit ForceStand() {
        this.AgentWantStand = true
        rescheduleAgentUpdate()
    }

    @SLMessageHandler
    fun HandleAvatarSitResponse(AvatarSitResponse avatarSitResponse)  {
        UUID uuid = avatarSitResponse.SitObject_Field.ID
        if (uuid.getLeastSignificantBits() == 0 && uuid.getMostSignificantBits() == 0) {
            Debug.Log("AvatarSit: Got null sit response")
            return
        }
        Debug.Log("AvatarSit: Got sit response for object " + uuid.toString())
        AgentSit agentSit = AgentSit()
        agentSit.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentSit.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentSit.isReliable = true
        SendMessage(agentSit)
    }

    fun HandleCloseCircuit()  {
        if (this.userManager != null) {
            this.userManager.getObjectsManager().myAvatarState().detachRequestHandler(this.avatarStateRequestHandler)
        }
        scheduleAgentUpdate(0, 0)
    }

    @SLMessageHandler
    fun HandleScriptQuestion(ScriptQuestion scriptQuestion)  {
        Debug.Log("ScriptQuestion: ItemID = " + scriptQuestion.Data_Field.ItemID + ", questions = " + String.format("%08x", Any[]{Int.valueOf(scriptQuestion.Data_Field.Questions)}))
        SLChatPermissionRequestEvent sLChatPermissionRequestEvent = SLChatPermissionRequestEvent(scriptQuestion, this.agentCircuit.getAgentUUID())
        if (sLChatPermissionRequestEvent.getQuestions() != 0) {
            this.agentCircuit.HandleChatEvent(this.agentCircuit.getLocalChatterID(), sLChatPermissionRequestEvent, true)
        }
    }

    fun ScriptAnswerYes(UUID uuid, UUID uuid2, Int i)  {
        ScriptAnswerYes scriptAnswerYes = ScriptAnswerYes()
        scriptAnswerYes.AgentData_Field.AgentID = this.circuitInfo.agentID
        scriptAnswerYes.AgentData_Field.SessionID = this.circuitInfo.sessionID
        scriptAnswerYes.Data_Field.TaskID = uuid2
        scriptAnswerYes.Data_Field.ItemID = uuid
        scriptAnswerYes.Data_Field.Questions = i
        scriptAnswerYes.isReliable = true
        SendMessage(scriptAnswerYes)
    }

    fun SitOnObject(UUID uuid)  {
        if (this.agentCircuit.getModules().rlvController.canSit()) {
            try {
                if (this.parcelInfo != null) {
                    SLObjectInfo sLObjectInfo = this.parcelInfo.allObjectsNearby.get(uuid)
                    ImmutableVector immutablePosition = this.agentPosition.getImmutablePosition()
                    if (!(sLObjectInfo == null || immutablePosition == null)) {
                        var distanceTo: Float = immutablePosition.getDistanceTo(sLObjectInfo.getAbsolutePosition())
                        Debug.Printf("RLV: Distance to object for sitting: %f", Float.valueOf(distanceTo))
                        if (distanceTo > 1.5f && !this.gridConn.getModules().rlvController.canTeleportBySitting()) {
                            return
                        }
                    }
                }
            } catch (Exception e) {
                Debug.Warning(e)
            }
            ForceSitOnObject(uuid)
        }
    }

    synchronized Unit Stand() {
        if (this.agentCircuit.getModules().rlvController.canStandUp()) {
            ForceStand()
        }
    }

    fun StartAgentMotion(Int i)  {
        var z: Boolean = false
        synchronized (this) {
            if (!((i & 8) == 0 && (i & 16) == 0)) {
                if (!this.isFlying) {
                    this.isFlying = true
                    z = true
                }
            }
            this.AgentMotionMask = i
            rescheduleAgentUpdate()
        }
        if (z) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
        }
    }

    synchronized Unit StopAgentMotion() {
        if (this.AgentMotionMask != 0) {
            this.AgentMotionMask = 0
            this.needClearAnims = true
        }
        rescheduleAgentUpdate()
    }

    fun StopAvatarAnimations()  {
        processStopAvatarAnimations()
        this.needClearAnims = true
        this.needFastUpdates = 10
        this.AgentMotionMask = 0
        rescheduleAgentUpdate()
    }

    fun getAgentAndCameraPosition(@NonNull LLVector3 lLVector3, @NonNull CameraParams cameraParams2): Boolean {
        Float f
        this.agentPosition.getInterpolatedPosition(lLVector3)
        synchronized (this.turningLock) {
            if (this.isTurning) {
                var currentTimeMillis: Float = this.turningSpeed * (((Float) (System.currentTimeMillis() - this.turningStartTime)) / 1000.0f)
                f = currentTimeMillis - this.lastTurnedAngle
                this.lastTurnedAngle = currentTimeMillis
            } else {
                f = 0.0f
            }
        }
        synchronized (this.cammingLock) {
            if (this.isCamming || !(!this.isManualCamming)) {
                this.agentHeading = CameraParams.wrapAngle(f + this.agentHeading)
            } else {
                if (f != 0.0f) {
                    this.cameraParams.rotate(f, 0.0f)
                }
                this.cameraParams.setPosition(lLVector3)
            }
        }
        cameraParams2.copyFrom(this.cameraParams)
        return this.cameraParams.isFlinging()
    }

    fun getAgentHeading(): Float {
        return this.agentHeading
    }

    @NonNull
    fun getAgentPosition(): AgentPosition {
        return this.agentPosition
    }

    fun getIsManualCamming(): Boolean {
        synchronized (this.cammingLock) {
            z = this.isManualCamming
        }
        return z
    }

    fun getVRCamera(HeadTransformCompat headTransformCompat, @NonNull LLVector3 lLVector3, @NonNull CameraParams cameraParams2)  {
        this.agentPosition.getInterpolatedPosition(lLVector3)
        synchronized (this.cammingLock) {
            if (!this.isManualCamming) {
                this.cameraParams.setPosition(lLVector3)
            }
        }
        cameraParams2.getVRCamera(this.cameraParams, headTransformCompat)
    }

    fun playAnimation(UUID uuid, Boolean z)  {
        AgentAnimation agentAnimation = AgentAnimation()
        agentAnimation.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentAnimation.AgentData_Field.SessionID = this.circuitInfo.sessionID
        AgentAnimation.AnimationList animationList = AgentAnimation.AnimationList()
        animationList.AnimID = uuid
        animationList.StartAnim = z
        agentAnimation.AnimationList_Fields.add(animationList)
        agentAnimation.isReliable = true
        SendMessage(agentAnimation)
    }

    fun processCameraFling(Float f, Float f2)  {
        synchronized (this.cammingLock) {
            this.cameraParams.fling(f, f2)
        }
    }

    fun processCameraRotate(Float f, Float f2)  {
        synchronized (this.cammingLock) {
            this.cameraParams.rotate(f, f2)
            if (!this.isCamming) {
                this.agentHeading = this.cameraParams.getHeading()
            }
        }
    }

    fun processCameraZoom(Float f, Float f2, Float f3, Float f4, Float f5)  {
        synchronized (this.cammingLock) {
            this.isCamming = true
            this.cameraParams.zoom(f, f2, f3, f4, f5)
        }
    }

    fun setAgentHeading(Float f)  {
        synchronized (this.cammingLock) {
            this.cameraParams.setHeading(f)
            this.agentHeading = this.cameraParams.getHeading()
        }
    }

    fun setAgentPosition(@NonNull LLVector3 lLVector3, @Nullable LLVector3 lLVector32)  {
        synchronized (this.cammingLock) {
            this.agentPosition.set(lLVector3, lLVector32)
            if (!this.cameraParams.isValid() || (!this.isCamming && (!this.isManualCamming))) {
                this.cameraParams.setPosition(lLVector3)
            }
        }
        SLModules modules = this.agentCircuit.getModules()
        if (modules != null) {
            modules.voice.updateSpatialVoicePosition()
        }
    }

    fun setCameraManualControl(Boolean z)  {
        synchronized (this.cammingLock) {
            this.isManualCamming = z
            if (!z) {
                this.isCamming = false
            }
            if (!this.isCamming && (!z)) {
                this.cameraParams.setPosition(this.agentPosition.getPosition(), this.agentHeading)
            }
        }
    }

    fun setEnableAgentUpdates(Boolean z)  {
        this.enableAgentUpdates = z
        if (z) {
            scheduleAgentUpdate(0, 1000)
        } else {
            scheduleAgentUpdate(0, 0)
        }
    }

    fun startCameraManualControl(Float f, Float f2, Float f3, Float f4)  {
        synchronized (this.cammingLock) {
            this.isCamming = true
            this.isManualCamming = true
            this.cameraParams.startManualControl(f, f2, f3, f4)
        }
    }

    fun startTurning(Float f)  {
        synchronized (this.turningLock) {
            if (!this.isTurning || this.turningSpeed != f) {
                this.isTurning = true
                this.turningSpeed = f
                this.turningStartTime = System.currentTimeMillis()
                this.lastTurnedAngle = 0.0f
            }
        }
    }

    fun stopCameraManualControl()  {
        synchronized (this.cammingLock) {
            this.cameraParams.stopManualControl()
        }
    }

    fun stopCamming()  {
        synchronized (this.cammingLock) {
            if (this.isCamming) {
                this.isCamming = false
                if (!this.isManualCamming) {
                    this.cameraParams.setPosition(this.agentPosition.getPosition(), this.agentHeading)
                }
            }
        }
    }

    fun stopFlying()  {
        var z: Boolean = true
        synchronized (this) {
            if (this.isFlying) {
                this.isFlying = false
                this.AgentWantStand = true
            } else {
                z = false
            }
        }
        if (z) {
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
        }
    }

    fun stopTurning()  {
        synchronized (this.turningLock) {
            this.isTurning = false
        }
    }
}
