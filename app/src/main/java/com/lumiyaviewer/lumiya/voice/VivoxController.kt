/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.media.AudioManager
 *  android.os.Build$VERSION
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.Messenger
 */
package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Messenger
import com.google.common.base.Objects
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.voice.Debug
import com.lumiyaviewer.lumiya.voice.VivoxMessageController
import com.lumiyaviewer.lumiya.voice.VoiceService
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessenger
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAcceptCall
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceChannelClosed
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceChannelStatus
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceEnableMic
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLoginStatus
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRejectCall
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSet3DPosition
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceTerminateCall
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceAccountConnection
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceConnector
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceException
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceSession
import com.vivox.service.VxClientProxy
import com.vivox.service.vx_event_type
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_evt_media_stream_updated_t
import com.vivox.service.vx_evt_participant_updated_t
import com.vivox.service.vx_evt_session_added_t
import com.vivox.service.vx_evt_session_removed_t
import com.vivox.service.vx_message_base_t
import com.vivox.service.vx_session_media_state
import com.vivox.service.vx_termination_status
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.Iterator
import java.util.Map
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nullable

class VivoxController
: VivoxMessageController.OnVivoxMessageListener {
    private VivoxController instance
    private Any instanceLock
    private AudioManager audioManager
    private Int bluetoothScoState = -1
    @Nullable
    private Messenger connectedMessenger = null
    private Handler controllerHandler
    private Boolean controllerReady = false
    private Any controllerReadyLock
    private Thread controllerThread
    private AtomicReference<Messenger> incomingMessengerRef = AtomicReference<Any>(null)
    private Boolean localMicEnabled = false
    private Handler mainThreadHandler
    private VivoxMessageController messageController
    private Map<String, VoiceChannelInfo> outgoingRequests
    @Nullable
    private VoiceAccountConnection voiceAccountConnection = null
    @Nullable
    private VoiceConnector voiceConnector = null
    private Map<String, VoiceSession> voiceSessions = Collections.synchronizedMap(HashMap())

    {
        instanceLock = Any()
        instance = null
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private VivoxController(Context object, Handler object2) throws VivoxInitException {
        this.outgoingRequests = Collections.synchronizedMap(HashMap())
        this.controllerReadyLock = Any()
        try {
            if (VxClientProxy.vx_initialize() != 0) {
                super("Failed to initialize voice subsystem")
                throw object
            }
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            throw VivoxInitException("Failed to initialize voice subsystem", unsatisfiedLinkError)
        }
        this.mainThreadHandler = object2
        this.audioManager = (AudioManager)object.getSystemService("audio")
        object2 = ControllerThread(this)
        this.controllerThread = Thread((Runnable)object2, "VivoxController")
        this.controllerThread.start()
        try {
            object = this.controllerReadyLock
            synchronized (object) {
            }
        }
        catch (InterruptedException interruptedException) {
            throw VivoxInitException("Failed to initialize voice subsystem", interruptedException)
        }
        {
            while (true) {
                if (this.controllerReady) {
                    this.controllerHandler = ((ControllerThread)object2).handler
                    this.messageController = ((ControllerThread)object2).messageController
                    return
                }
                this.controllerReadyLock.wait()
            }
        }
    }

    /* synthetic */ Boolean access$1002(VivoxController vivoxController, Boolean bl) {
        vivoxController.localMicEnabled = bl
        return bl
    }

    /* synthetic */ Messenger access$1102(VivoxController vivoxController, Messenger messenger) {
        vivoxController.connectedMessenger = messenger
        return messenger
    }

    /* synthetic */ Int access$1602(VivoxController vivoxController, Int n) {
        vivoxController.bluetoothScoState = n
        return n
    }

    /* synthetic */ Boolean access$402(VivoxController vivoxController, Boolean bl) {
        vivoxController.controllerReady = bl
        return bl
    }

    /* synthetic */ VoiceAccountConnection access$602(VivoxController vivoxController, VoiceAccountConnection voiceAccountConnection) {
        vivoxController.voiceAccountConnection = voiceAccountConnection
        return voiceAccountConnection
    }

    /* synthetic */ VoiceConnector access$802(VivoxController vivoxController, VoiceConnector voiceConnector) {
        vivoxController.voiceConnector = voiceConnector
        return voiceConnector
    }

    private Unit closeAllSessions() {
        Iterator iterator = ImmutableList.copyOf(this.voiceSessions.values()).iterator()
        while (iterator.hasNext()) {
            ((VoiceSession)iterator.next()).dispose()
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    VivoxController getInstance(Context context, Handler handler, Messenger messenger) throws VivoxInitException {
        Any object = instanceLock
        synchronized (object) {
            if (instance == null) {
                VivoxController vivoxController
                instance = vivoxController = VivoxController(context, handler)
            }
        }
        instance.setIncomingMessenger(messenger)
        return instance
    }

    private Boolean hasActiveSession() {
        Boolean bl
        block1: {
            Boolean bl2 = false
            Iterator<VoiceSession> iterator = this.voiceSessions.values().iterator()
            do {
                bl = bl2
                if (!iterator.hasNext()) break block1
            } while (iterator.next().getState() != VoiceChatInfo.VoiceChatState.Active)
            bl = true
        }
        return bl
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Unit setAudioVoiceMode(Boolean bl) {
        if (bl) {
            this.mainThreadHandler.post(Runnable(this){
                VivoxController this$0
                {
                    this.this$0 = vivoxController
                }

                /*
                 * Enabled aggressive block sorting
                 * Enabled unnecessary exception pruning
                 * Enabled aggressive exception aggregation
                 */
                @Override
                Unit run() {
                    VoiceService voiceService
                    if (this.this$0.audioManager != null && Build.VERSION.SDK_INT >= 11) {
                        try {
                            this.this$0.audioManager.setMode(3)
                        }
                        catch (Exception exception) {
                            Debug.Warning(exception)
                        }
                    }
                    if ((voiceService = VoiceService.getServiceInstance()) != null) {
                        voiceService.listenForVolumeChanges(true)
                    }
                }
            if (this.bluetoothScoState == 1) {
                this.setBluetoothEnable(true)
                return
            }
            this.tryStartingBluetooth()
            return
        }
        this.mainThreadHandler.post(Runnable(this){
            VivoxController this$0
            {
                this.this$0 = vivoxController
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            Unit run() {
                VoiceService voiceService
                if (this.this$0.audioManager != null && Build.VERSION.SDK_INT >= 11) {
                    try {
                        this.this$0.audioManager.setBluetoothScoOn(false)
                    }
                    catch (Exception exception) {
                        Debug.Warning(exception)
                    }
                    try {
                        this.this$0.audioManager.setSpeakerphoneOn(false)
                        this.this$0.audioManager.setMode(0)
                    }
                    catch (Exception exception) {
                        Debug.Warning(exception)
                    }
                }
                if ((voiceService = VoiceService.getServiceInstance()) != null) {
                    voiceService.listenForVolumeChanges(false)
                    voiceService.updateAudioProperties()
                }
            }
    }

    private Unit setBluetoothEnable(Boolean bl) {
        this.mainThreadHandler.post(Runnable(this, bl){
            VivoxController this$0
            Boolean val$enable
            {
                this.this$0 = vivoxController
                this.val$enable = bl
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            Unit run() {
                VoiceService voiceService
                try {
                    this.this$0.audioManager.setBluetoothScoOn(this.val$enable)
                }
                catch (Exception exception) {
                    Debug.Warning(exception)
                }
                if ((voiceService = VoiceService.getServiceInstance()) != null) {
                    voiceService.updateAudioProperties()
                }
            }
    }

    /*
     * Enabled aggressive block sorting
     */
    private Unit setLocalMicEnabled(Boolean bl) {
        if (this.voiceConnector != null) {
            VoiceConnector voiceConnector = this.voiceConnector
            Boolean bl2 = !bl
            voiceConnector.setMuteLocalMic(bl2)
            this.localMicEnabled = bl
            for (VoiceSession voiceSession : this.voiceSessions.values()) {
                if (!voiceSession.setLocalMicActive(bl)) continue
                this.updateSessionState(voiceSession)
            }
        }
    }

    private Unit tryStartingBluetooth() {
        this.mainThreadHandler.post(Runnable(this){
            VivoxController this$0
            {
                this.this$0 = vivoxController
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            Unit run() {
                VoiceService voiceService
                try {
                    this.this$0.audioManager.startBluetoothSco()
                }
                catch (Exception exception) {
                    Debug.Warning(exception)
                }
                if ((voiceService = VoiceService.getServiceInstance()) != null) {
                    voiceService.updateAudioProperties()
                }
            }
    }

    private Unit updateSessionState(VoiceSession voiceSession) {
        if (this.connectedMessenger != null) {
            VoiceChatInfo voiceChatInfo = voiceSession.getVoiceChatInfo()
            Debug.Printf("Voice: Updating session state: %s", voiceChatInfo)
            VoicePluginMessenger.sendMessage(this.connectedMessenger, VoicePluginMessageType.VoiceChannelStatus, VoiceChannelStatus(voiceSession.getVoiceChannelInfo(), voiceChatInfo, null), this.incomingMessengerRef.get())
        }
    }

    Unit AcceptCall(VoiceAcceptCall voiceAcceptCall) {
        this.controllerHandler.post(Runnable(this, voiceAcceptCall){
            VivoxController this$0
            VoiceAcceptCall val$message
            {
                this.this$0 = vivoxController
                this.val$message = voiceAcceptCall
            }

            /*
             * Enabled aggressive block sorting
             */
            @Override
            Unit run() {
                VoiceSession voiceSession
                Any var2_1 = null
                if (this.val$message.sessionHandle != null) {
                    Debug.Printf("Voice: trying to accept session with handle %s", this.val$message.sessionHandle)
                    voiceSession = (VoiceSession)this.this$0.voiceSessions.get(this.val$message.sessionHandle)
                } else {
                    Debug.Printf("Voice: trying to accept session with uri %s", this.val$message.voiceChannelInfo.voiceChannelURI)
                    Iterator iterator = this.this$0.voiceSessions.values().iterator()
                    do {
                        voiceSession = var2_1
                        if (!iterator.hasNext()) break
                        voiceSession = (VoiceSession)iterator.next()
                        Debug.Printf("Voice: existing session with uri %s", voiceSession.getVoiceChannelInfo().voiceChannelURI)
                    } while (!Objects.equal(voiceSession.getVoiceChannelInfo().voiceChannelURI, this.val$message.voiceChannelInfo.voiceChannelURI))
                }
                if (voiceSession != null) {
                    Debug.Printf("Voice: accepting session", Any[0])
                    voiceSession.mediaConnect()
                    return
                }
                Debug.Printf("Voice: no session to accept", Any[0])
            }
    }

    Unit ConnectChannel(VoiceChannelInfo voiceChannelInfo, @Nullable String string2, Messenger messenger) {
        this.controllerHandler.post(Runnable(this, voiceChannelInfo, string2, messenger){
            VivoxController this$0
            String val$channelCredentials
            Messenger val$replyTo
            VoiceChannelInfo val$voiceChannelInfo
            {
                this.this$0 = vivoxController
                this.val$voiceChannelInfo = voiceChannelInfo
                this.val$channelCredentials = string2
                this.val$replyTo = messenger
            }

            /*
             * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            Unit run() {
                try {
                    if (this.this$0.voiceAccountConnection == null) {
                        VoiceException voiceException = VoiceException("Not logged in")
                        throw voiceException
                    }
                }
                catch (VoiceException voiceException) {
                    VoicePluginMessenger.sendMessage(this.val$replyTo, VoicePluginMessageType.VoiceChannelStatus, VoiceChannelStatus(this.val$voiceChannelInfo, VoiceChatInfo.empty(), voiceException.getMessage()), null)
                    return
                }
                {
                    Any object3
                    ArrayList<Any> arrayList = ArrayList<Any>()
                    Any object2 = null
                    for (Any object3 : this.this$0.voiceSessions.values()) {
                        if (((VoiceSession)object3).getVoiceChannelInfo().equals(this.val$voiceChannelInfo)) {
                            object2 = object3
                            continue
                        }
                        arrayList.add(object3)
                    }
                    object3 = arrayList.iterator()
                    while (true) {
                        if (!object3.hasNext()) {
                            if (object2 != null) return
                            this.this$0.outgoingRequests.put(this.val$voiceChannelInfo.voiceChannelURI, this.val$voiceChannelInfo)
                            this.this$0.voiceAccountConnection.createVoiceSession(this.val$voiceChannelInfo, this.val$channelCredentials)
                            return
                        }
                        VoiceSession voiceSession = (VoiceSession)object3.next()
                        voiceSession.mediaDisconnect(vx_termination_status.termination_status_none)
                        voiceSession.dispose()
                    }
                }
            }
    }

    Unit EnableVoiceMic(VoiceEnableMic voiceEnableMic) {
        this.controllerHandler.post(Runnable(this, voiceEnableMic){
            VivoxController this$0
            VoiceEnableMic val$message
            {
                this.this$0 = vivoxController
                this.val$message = voiceEnableMic
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            Unit run() {
                block6: {
                    block5: {
                        if (this.this$0.voiceConnector == null) break block5
                        Debug.Printf("Voice: local mic enabling: %b", this.val$message.enableMic)
                        if (!this.val$message.enableMic) break block6
                        if (this.this$0.hasActiveSession()) {
                            this.this$0.setLocalMicEnabled(true)
                        }
                    }
                    return
                }
                this.this$0.setLocalMicEnabled(false)
            }
    }

    Unit Login(VoiceLoginInfo voiceLoginInfo, Messenger messenger) {
        this.controllerHandler.post(Runnable(this, voiceLoginInfo, messenger){
            VivoxController this$0
            Messenger val$replyTo
            VoiceLoginInfo val$voiceLoginInfo
            {
                this.this$0 = vivoxController
                this.val$voiceLoginInfo = voiceLoginInfo
                this.val$replyTo = messenger
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            Unit run() {
                try {
                    VoiceConnector voiceConnector
                    Debug.Printf("Voice: Logging in.", Any[0])
                    if (this.this$0.voiceAccountConnection != null && !Objects.equal(this.val$voiceLoginInfo, this.this$0.voiceAccountConnection.getVoiceLoginInfo())) {
                        this.this$0.closeAllSessions()
                        this.this$0.voiceAccountConnection.dispose()
                        VivoxController.access$602(this.this$0, null)
                    }
                    if (this.this$0.voiceConnector != null && !Objects.equal(this.val$voiceLoginInfo.voiceAccountServerName, this.this$0.voiceConnector.getVoiceAccountServerName())) {
                        this.this$0.closeAllSessions()
                        if (this.this$0.voiceAccountConnection != null) {
                            this.this$0.voiceAccountConnection.dispose()
                            VivoxController.access$602(this.this$0, null)
                        }
                        this.this$0.voiceConnector.dispose()
                        VivoxController.access$802(this.this$0, null)
                    }
                    if (this.this$0.voiceConnector == null) {
                        Debug.Printf("Voice: Creating voice connector.", Any[0])
                        VivoxController vivoxController = this.this$0
                        voiceConnector = VoiceConnector(this.this$0.messageController, this.val$voiceLoginInfo.voiceAccountServerName)
                        VivoxController.access$802(vivoxController, voiceConnector)
                        Debug.Printf("Voice: Voice connector created.", Any[0])
                    }
                    this.this$0.voiceConnector.setMuteLocalMic(true)
                    VivoxController.access$1002(this.this$0, false)
                    if (this.this$0.voiceAccountConnection == null) {
                        Debug.Printf("Voice: Creating voice account connection.", Any[0])
                        VivoxController.access$602(this.this$0, this.this$0.voiceConnector.createAccountConnection(this.val$voiceLoginInfo))
                        Debug.Printf("Voice: Voice account connection created.", Any[0])
                    }
                    VivoxController.access$1102(this.this$0, this.val$replyTo)
                    voiceConnector = this.val$replyTo
                    VoicePluginMessageType voicePluginMessageType = VoicePluginMessageType.VoiceLoginStatus
                    VoiceLoginStatus voiceLoginStatus = VoiceLoginStatus(this.val$voiceLoginInfo, true, null)
                    VoicePluginMessenger.sendMessage((Messenger)voiceConnector, voicePluginMessageType, voiceLoginStatus, null)
                    return
                }
                catch (VoiceException voiceException) {
                    Debug.Warning(voiceException)
                    VivoxController.access$1102(this.this$0, null)
                    VoicePluginMessenger.sendMessage(this.val$replyTo, VoicePluginMessageType.VoiceLoginStatus, VoiceLoginStatus(this.val$voiceLoginInfo, false, voiceException.getMessage()), null)
                    return
                }
            }
    }

    Unit Logout(Messenger messenger) {
        this.controllerHandler.post(Runnable(this, messenger){
            VivoxController this$0
            Messenger val$replyTo
            {
                this.this$0 = vivoxController
                this.val$replyTo = messenger
            }

            @Override
            Unit run() {
                Debug.Printf("Voice: logging out.", Any[0])
                this.this$0.setLocalMicEnabled(false)
                if (this.this$0.voiceAccountConnection != null) {
                    this.this$0.closeAllSessions()
                    this.this$0.voiceAccountConnection.dispose()
                    VivoxController.access$602(this.this$0, null)
                }
                if (this.this$0.voiceConnector != null) {
                    this.this$0.voiceConnector.dispose()
                    VivoxController.access$802(this.this$0, null)
                }
                Debug.Printf("Voice: logged out.", Any[0])
                VoicePluginMessenger.sendMessage(this.val$replyTo, VoicePluginMessageType.VoiceLoginStatus, VoiceLoginStatus(null, false, null), null)
            }
    }

    Unit RejectCall(VoiceRejectCall voiceRejectCall) {
        this.controllerHandler.post(Runnable(this, voiceRejectCall){
            VivoxController this$0
            VoiceRejectCall val$message
            {
                this.this$0 = vivoxController
                this.val$message = voiceRejectCall
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            Unit run() {
                Debug.Printf("Voice: requested to reject session with handle %s", this.val$message.sessionHandle)
                VoiceSession voiceSession = (VoiceSession)this.this$0.voiceSessions.get(this.val$message.sessionHandle)
                if (voiceSession != null) {
                    Debug.Printf("Voice: terminating session", Any[0])
                    voiceSession.mediaDisconnect(vx_termination_status.termination_status_busy)
                    return
                }
                Debug.Printf("Voice: no session to terminate", Any[0])
            }
    }

    Unit Set3DPosition(VoiceSet3DPosition voiceSet3DPosition) {
        this.controllerHandler.post(Runnable(this, voiceSet3DPosition){
            VivoxController this$0
            VoiceSet3DPosition val$message
            {
                this.this$0 = vivoxController
                this.val$message = voiceSet3DPosition
            }

            @Override
            Unit run() {
                for (VoiceSession voiceSession : this.this$0.voiceSessions.values()) {
                    VoiceChannelInfo voiceChannelInfo = voiceSession.getVoiceChannelInfo()
                    if (!voiceChannelInfo.isSpatial || !Objects.equal(voiceChannelInfo, this.val$message.voiceChannelInfo)) continue
                    voiceSession.set3DPosition(this.val$message.speakerPosition, this.val$message.listenerPosition)
                }
            }
    }

    Unit SetAudioProperties(VoiceSetAudioProperties voiceSetAudioProperties) {
        this.controllerHandler.post(Runnable(this, voiceSetAudioProperties){
            VivoxController this$0
            VoiceSetAudioProperties val$message
            {
                this.this$0 = vivoxController
                this.val$message = voiceSetAudioProperties
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            Unit run() {
                VoiceService voiceService
                if (this.this$0.audioManager != null && this.val$message.audioDevice != null && this.this$0.hasActiveSession()) {
                    switch (15.$SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice[this.val$message.audioDevice.ordinal()]) {
                        case 1: {
                            try {
                                this.this$0.audioManager.setSpeakerphoneOn(false)
                                this.this$0.audioManager.setBluetoothScoOn(false)
                            }
                            catch (Exception exception) {
                                Debug.Warning(exception)
                            }
                            break
                        }
                        case 2: {
                            try {
                                this.this$0.audioManager.setSpeakerphoneOn(true)
                                this.this$0.audioManager.setBluetoothScoOn(false)
                            }
                            catch (Exception exception) {
                                Debug.Warning(exception)
                            }
                            break
                        }
                        case 3: {
                            if (this.this$0.bluetoothScoState == 1) {
                                this.this$0.setBluetoothEnable(true)
                                break
                            }
                            this.this$0.tryStartingBluetooth()
                            break
                        }
                    }
                }
                if ((voiceService = VoiceService.getServiceInstance()) != null) {
                    if (this.val$message.speakerVolumeValid) {
                        voiceService.setVolume(this.val$message.speakerVolume)
                    }
                    voiceService.updateAudioProperties()
                }
            }
    }

    Unit TerminateCall(VoiceTerminateCall voiceTerminateCall) {
        this.controllerHandler.post(Runnable(this, voiceTerminateCall){
            VivoxController this$0
            VoiceTerminateCall val$message
            {
                this.this$0 = vivoxController
                this.val$message = voiceTerminateCall
            }

            @Override
            Unit run() {
                ArrayList<VoiceSession> arrayList = ArrayList<VoiceSession>()
                for (VoiceSession voiceSession : this.this$0.voiceSessions.values()) {
                    if (!Objects.equal(voiceSession.getVoiceChannelInfo().voiceChannelURI, this.val$message.channelInfo.voiceChannelURI)) continue
                    arrayList.add(voiceSession)
                }
                for (VoiceSession voiceSession : arrayList) {
                    voiceSession.mediaDisconnect(vx_termination_status.termination_status_none)
                    voiceSession.dispose()
                }
                this.this$0.setLocalMicEnabled(false)
            }
    }

    Messenger getIncomingMessenger() {
        return this.incomingMessengerRef.get()
    }

    Unit onBluetoothScoStateChanged(Int n) {
        this.controllerHandler.post(Runnable(this, n){
            VivoxController this$0
            Int val$state
            {
                this.this$0 = vivoxController
                this.val$state = n
            }

            @Override
            Unit run() {
                VivoxController.access$1602(this.this$0, this.val$state)
                if (this.this$0.bluetoothScoState == 1 && this.this$0.audioManager != null && this.this$0.hasActiveSession()) {
                    this.this$0.setBluetoothEnable(true)
                }
            }
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    Unit onVivoxEvent(vx_evt_base_t object) {
        Debug.Printf("Voice: got vivox event: %s", object)
        if (object == null) return
        Any object2 = ((vx_evt_base_t)object).getType()
        Debug.Printf("Voice: vx_event_type %s", object2)
        if (object2 == vx_event_type.evt_session_added) {
            if ((object = VxClientProxy.vx_message_base_t2vx_evt_session_added_t(((vx_evt_base_t)object).getMessage())) == null) return
            object2 = this.outgoingRequests.remove(((vx_evt_session_added_t)object).getUri())
            object = VoiceSession(this.messageController, (vx_evt_session_added_t)object, (VoiceChannelInfo)object2)
            this.voiceSessions.put(((VoiceSession)object).getHandle(), (VoiceSession)object)
            Debug.Printf("Voice: session added: %s (%s)", ((VoiceSession)object).getHandle(), ((VoiceSession)object).getVoiceChannelInfo().voiceChannelURI)
            this.updateSessionState((VoiceSession)object)
            return
        }
        if (object2 == vx_event_type.evt_session_removed) {
            if ((object = VxClientProxy.vx_message_base_t2vx_evt_session_removed_t(((vx_evt_base_t)object).getMessage())) == null) return
            if ((object = this.voiceSessions.remove(((vx_evt_session_removed_t)object).getSession_handle())) == null) return
            Debug.Printf("Voice: session removed: %s", ((VoiceSession)object).getVoiceChannelInfo().voiceChannelURI)
            if (((VoiceSession)object).setState(VoiceChatInfo.VoiceChatState.None)) {
                this.updateSessionState((VoiceSession)object)
            }
            VoicePluginMessenger.sendMessage(this.connectedMessenger, VoicePluginMessageType.VoiceChannelClosed, VoiceChannelClosed(((VoiceSession)object).getVoiceChannelInfo()), this.incomingMessengerRef.get())
            return
        }
        if (object2 == vx_event_type.evt_participant_updated) {
            Any object3 = VxClientProxy.vx_message_base_t2vx_evt_participant_updated_t(((vx_evt_base_t)object).getMessage())
            if (object3 == null) return
            object = this.voiceAccountConnection
            object2 = this.voiceSessions.get(((vx_evt_participant_updated_t)object3).getSession_handle())
            if (object2 == null) return
            if (object == null) return
            Boolean bl = ((vx_evt_participant_updated_t)object3).getIs_speaking() != 0
            object3 = VoiceChannelInfo.agentUUIDFromURI(((vx_evt_participant_updated_t)object3).getParticipant_uri())
            Boolean bl2 = Objects.equal(object3, ((VoiceAccountConnection)object).getVoiceLoginInfo().agentUUID)
            Debug.Printf("Voice: speaking %b, speakerID %s (mine: %b)", bl, object3, bl2)
            if (object3 == null) return
            if (bl2) return
            if (!((VoiceSession)object2).setSpeakerSpeaking((UUID)object3, bl)) return
            this.updateSessionState((VoiceSession)object2)
            return
        }
        if (object2 != vx_event_type.evt_media_stream_updated) return
        object2 = VxClientProxy.vx_message_base_t2vx_evt_media_stream_updated_t(((vx_evt_base_t)object).getMessage())
        if (object2 == null) return
        object = ((vx_evt_media_stream_updated_t)object2).getState()
        Debug.Printf("Voice: media stream updated, state %s", object)
        if (object == vx_session_media_state.session_media_ringing) {
            object = this.voiceSessions.get(((vx_evt_media_stream_updated_t)object2).getSession_handle())
            if (object == null) return
            if (!((VoiceSession)object).isIncoming()) return
            object2 = ((VoiceSession)object).getVoiceChannelInfo().getAgentUUID()
            if (object2 == null) return
            if (this.connectedMessenger == null) return
            if (((VoiceSession)object).setState(VoiceChatInfo.VoiceChatState.Ringing)) {
                this.updateSessionState((VoiceSession)object)
            }
            VoicePluginMessenger.sendMessage(this.connectedMessenger, VoicePluginMessageType.VoiceRinging, VoiceRinging(((VoiceSession)object).getHandle(), ((VoiceSession)object).getVoiceChannelInfo(), (UUID)object2), this.incomingMessengerRef.get())
            return
        }
        if (object != vx_session_media_state.session_media_connected) {
            if (object != vx_session_media_state.session_media_disconnected) return
            this.setAudioVoiceMode(false)
            return
        }
        object = this.voiceSessions.get(((vx_evt_media_stream_updated_t)object2).getSession_handle())
        if (object == null) return
        if (((VoiceSession)object).setState(VoiceChatInfo.VoiceChatState.Active)) {
            this.updateSessionState((VoiceSession)object)
        }
        if (!((VoiceSession)object).getVoiceChannelInfo().isConference) {
            this.setLocalMicEnabled(true)
        }
        this.setAudioVoiceMode(true)
    }

    @Override
    Unit onVivoxMessage(vx_message_base_t vx_message_base_t2) {
        Debug.Printf("Voice: got vivox message: %s", vx_message_base_t2)
    }

    Unit setIncomingMessenger(Messenger messenger) {
        this.incomingMessengerRef.set(messenger)
    }

    private class ControllerThread
    : Runnable {
        private Handler handler
        private VivoxMessageController messageController
        VivoxController this$0

        private ControllerThread(VivoxController vivoxController) {
            this.this$0 = vivoxController
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        Unit run() {
            Looper.prepare()
            this.handler = Handler()
            this.messageController = VivoxMessageController(this.this$0)
            Any object = this.this$0.controllerReadyLock
            synchronized (object) {
                VivoxController.access$402(this.this$0, true)
                this.this$0.controllerReadyLock.notifyAll()
            }
            Looper.loop()
        }
    }

    class VivoxInitException
    : Exception {
        VivoxInitException(String string2) {
            super(string2)
        }

        VivoxInitException(String string2, Throwable throwable) {
            super(string2, throwable)
        }
    }
}

