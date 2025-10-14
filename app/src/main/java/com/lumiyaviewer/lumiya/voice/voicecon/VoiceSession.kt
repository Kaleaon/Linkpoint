/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice.voicecon

import com.lumiyaviewer.lumiya.voice.Debug
import com.lumiyaviewer.lumiya.voice.VivoxMessageController
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
import com.vivox.service.VxClientProxy
import com.vivox.service.vx_evt_session_added_t
import com.vivox.service.vx_req_session_media_connect_t
import com.vivox.service.vx_req_session_media_disconnect_t
import com.vivox.service.vx_req_session_set_3d_position_t
import com.vivox.service.vx_req_session_terminate_t
import com.vivox.service.vx_termination_status
import java.util.HashSet
import java.util.Iterator
import java.util.Set
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceSession {
    private Boolean disposed = false
    private String handle
    private Boolean isIncoming
    private Boolean localMicActive = false
    private VivoxMessageController messageController
    private VoiceChatInfo.VoiceChatState previousState
    private String sessionGroupHandle
    private Set<UUID> speakers
    private VoiceChatInfo.VoiceChatState state
    private Any stateLock = Any()
    private VoiceChannelInfo voiceChannelInfo

    /*
     * Enabled aggressive block sorting
     */
    VoiceSession(VivoxMessageController vivoxMessageController, vx_evt_session_added_t vx_evt_session_added_t2, @Nullable VoiceChannelInfo voiceChannelInfo) {
        this.previousState = VoiceChatInfo.VoiceChatState.Connecting
        this.state = VoiceChatInfo.VoiceChatState.Connecting
        this.speakers = HashSet<UUID>()
        this.messageController = vivoxMessageController
        this.handle = vx_evt_session_added_t2.getSession_handle()
        this.sessionGroupHandle = vx_evt_session_added_t2.getSessiongroup_handle()
        if (voiceChannelInfo == null) {
            voiceChannelInfo = VoiceChannelInfo(vx_evt_session_added_t2.getUri(), false, false)
        }
        this.voiceChannelInfo = voiceChannelInfo
        Boolean bl = vx_evt_session_added_t2.getIncoming() != 0
        this.isIncoming = bl
        Debug.Printf("Voice: created session: %s (uri %s)", this.handle, this.voiceChannelInfo.voiceChannelURI)
    }

    Unit dispose() {
        if (!this.disposed) {
            this.disposed = true
            vx_req_session_terminate_t vx_req_session_terminate_t2 = vx_req_session_terminate_t()
            vx_req_session_terminate_t2.setSession_handle(this.handle)
            this.messageController.sendRequestAndWait(vx_req_session_terminate_t2.getBase())
        }
    }

    String getHandle() {
        return this.handle
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    VoiceChatInfo.VoiceChatState getState() {
        Any object = this.stateLock
        synchronized (object) {
            return this.state
        }
    }

    VoiceChannelInfo getVoiceChannelInfo() {
        return this.voiceChannelInfo
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nonnull
    VoiceChatInfo getVoiceChatInfo() {
        Any object
        Any object2 = this.stateLock
        synchronized (object2) {
            Debug.Printf("Voice: got session state: %s (%s)", Any[]{this.state, this})
            if (this.state == VoiceChatInfo.VoiceChatState.None && this.previousState == VoiceChatInfo.VoiceChatState.None) {
                object = VoiceChatInfo.empty()
                Debug.Printf("Voice: returning empty session state", Any[0])
            } else {
                VoiceChatInfo voiceChatInfo = null
                object = voiceChatInfo
                if (!this.speakers.isEmpty()) {
                    Iterator<UUID> iterator = this.speakers.iterator()
                    object = voiceChatInfo
                    if (iterator.hasNext()) {
                        object = iterator.next()
                    }
                }
                object = VoiceChatInfo.create(this.state, this.previousState, this.speakers.size(), (UUID)object, this.voiceChannelInfo.isConference, this.localMicActive)
            }
            this.previousState = this.state
        }
        Debug.Printf("Voice: returning session state: %s", Any[]{((VoiceChatInfo)object).state})
        return object
    }

    Boolean isIncoming() {
        return this.isIncoming
    }

    Unit mediaConnect() {
        vx_req_session_media_connect_t vx_req_session_media_connect_t2 = vx_req_session_media_connect_t()
        vx_req_session_media_connect_t2.setSession_handle(this.handle)
        vx_req_session_media_connect_t2.setSessiongroup_handle(this.sessionGroupHandle)
        vx_req_session_media_connect_t2.setSession_font_id(0)
        this.messageController.sendRequest(vx_req_session_media_connect_t2.getBase())
    }

    Unit mediaDisconnect(vx_termination_status vx_termination_status2) {
        vx_req_session_media_disconnect_t vx_req_session_media_disconnect_t2 = vx_req_session_media_disconnect_t()
        vx_req_session_media_disconnect_t2.setSession_handle(this.handle)
        vx_req_session_media_disconnect_t2.setSessiongroup_handle(this.sessionGroupHandle)
        vx_req_session_media_disconnect_t2.setTermination_status(vx_termination_status2)
        this.messageController.sendRequest(vx_req_session_media_disconnect_t2.getBase())
    }

    Unit set3DPosition(Voice3DPosition voice3DPosition, Voice3DPosition voice3DPosition2) {
        Debug.Printf("Voice: set3D: speaker %s", voice3DPosition.toString())
        Debug.Printf("Voice: set3D: listener %s", voice3DPosition2.toString())
        vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2 = vx_req_session_set_3d_position_t()
        vx_req_session_set_3d_position_t2.setSession_handle(this.handle)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.position.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.position.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.position.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.velocity.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.velocity.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.velocity.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.atOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.atOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.atOrientation.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.upOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.upOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.upOrientation.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.leftOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.leftOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.leftOrientation.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.position.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.position.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.position.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.velocity.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.velocity.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.velocity.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.atOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.atOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.atOrientation.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.upOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.upOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.upOrientation.z)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.leftOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.leftOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.leftOrientation.z)
        this.messageController.sendRequest(vx_req_session_set_3d_position_t2.getBase())
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Boolean setLocalMicActive(Boolean bl) {
        Any object = this.stateLock
        synchronized (object) {
            Boolean bl2 = bl != this.localMicActive
            this.localMicActive = bl
            return bl2
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Boolean setSpeakerSpeaking(UUID uUID, Boolean bl) {
        Any object = this.stateLock
        synchronized (object) {
            if (!bl) return this.speakers.remove(uUID)
            return this.speakers.add(uUID)
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Boolean setState(VoiceChatInfo.VoiceChatState voiceChatState) {
        Boolean bl = false
        Any object = this.stateLock
        synchronized (object) {
            if (this.state != voiceChatState) {
                this.previousState = this.state
                this.state = voiceChatState
                bl = true
                Debug.Printf("Voice: session state: %s (%s)", Any[]{this.state, this})
            }
            return bl
        }
    }
}

