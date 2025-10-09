package com.linkpoint.voice.voicecon

import com.linkpoint.voice.Debug
import com.linkpoint.voice.VivoxMessageController
import com.linkpoint.voice.common.model.Voice3DPosition
import com.linkpoint.voice.common.model.VoiceChannelInfo
import com.linkpoint.voice.common.model.VoiceChatInfo
import com.vivox.service.VxClientProxy
import com.vivox.service.vx_evt_session_added_t
import com.vivox.service.vx_req_session_media_connect_t
import com.vivox.service.vx_req_session_media_disconnect_t
import com.vivox.service.vx_req_session_set_3d_position_t
import com.vivox.service.vx_req_session_terminate_t
import com.vivox.service.vx_termination_status
import java.util.*
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceSession(
    private val messageController: VivoxMessageController,
    sessionAddedEvent: vx_evt_session_added_t,
    voiceChannelInfoParam: VoiceChannelInfo?
) {
    private var disposed: Boolean = false
    private val handle: String
    private val isIncoming: Boolean
    private var localMicActive: Boolean = false
    private var previousState: VoiceChatInfo.VoiceChatState
    private val sessionGroupHandle: String
    private val speakers: MutableSet<UUID>
    private var state: VoiceChatInfo.VoiceChatState
    private val stateLock = Any()
    val voiceChannelInfo: VoiceChannelInfo

    init {
        this.previousState = VoiceChatInfo.VoiceChatState.Connecting
        this.state = VoiceChatInfo.VoiceChatState.Connecting
        this.speakers = HashSet()
        this.handle = sessionAddedEvent.session_handle
        this.sessionGroupHandle = sessionAddedEvent.sessiongroup_handle
        this.voiceChannelInfo = voiceChannelInfoParam 
            ?: VoiceChannelInfo(sessionAddedEvent.uri, false, false)
        this.isIncoming = sessionAddedEvent.incoming != 0
        Debug.Printf("Voice: created session: %s (uri %s)", this.handle, this.voiceChannelInfo.voiceChannelURI)
    }

    fun dispose() {
        if (!disposed) {
            disposed = true
            val terminateRequest = vx_req_session_terminate_t()
            terminateRequest.session_handle = handle
            messageController.sendRequestAndWait(terminateRequest.base)
        }
    }

    fun getHandle(): String = handle

    fun getState(): VoiceChatInfo.VoiceChatState {
        synchronized(stateLock) {
            return state
        }
    }

    fun getVoiceChannelInfo(): VoiceChannelInfo = voiceChannelInfo

    fun getVoiceChatInfo(): VoiceChatInfo {
        val result: VoiceChatInfo
        synchronized(stateLock) {
            Debug.Printf("Voice: got session state: %s (%s)", state, this)
            result = if (state == VoiceChatInfo.VoiceChatState.None && 
                        previousState == VoiceChatInfo.VoiceChatState.None) {
                Debug.Printf("Voice: returning empty session state", emptyArray<Any>())
                VoiceChatInfo.empty()
            } else {
                val firstSpeaker: UUID? = speakers.firstOrNull()
                VoiceChatInfo.create(
                    state, 
                    previousState, 
                    speakers.size, 
                    firstSpeaker, 
                    voiceChannelInfo.isConference, 
                    localMicActive
                )
            }
            previousState = state
        }
        Debug.Printf("Voice: returning session state: %s", result.state)
        return result
    }

    fun isIncoming(): Boolean = isIncoming

    fun mediaConnect() {
        val connectRequest = vx_req_session_media_connect_t()
        connectRequest.session_handle = handle
        connectRequest.sessiongroup_handle = sessionGroupHandle
        connectRequest.session_font_id = 0
        messageController.sendRequest(connectRequest.base)
    }

    fun mediaDisconnect(terminationStatus: vx_termination_status) {
        val disconnectRequest = vx_req_session_media_disconnect_t()
        disconnectRequest.session_handle = handle
        disconnectRequest.sessiongroup_handle = sessionGroupHandle
        disconnectRequest.termination_status = terminationStatus
        messageController.sendRequest(disconnectRequest.base)
    }

    fun set3DPosition(speakerPosition: Voice3DPosition, listenerPosition: Voice3DPosition) {
        Debug.Printf("Voice: set3D: speaker %s", speakerPosition.toString())
        Debug.Printf("Voice: set3D: listener %s", listenerPosition.toString())
        
        val positionRequest = vx_req_session_set_3d_position_t()
        positionRequest.session_handle = handle
        
        // Set speaker position
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(positionRequest, 0, speakerPosition.position.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(positionRequest, 1, speakerPosition.position.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(positionRequest, 2, speakerPosition.position.z)
        
        // Set speaker velocity
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(positionRequest, 0, speakerPosition.velocity.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(positionRequest, 1, speakerPosition.velocity.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(positionRequest, 2, speakerPosition.velocity.z)
        
        // Set speaker orientations
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(positionRequest, 0, speakerPosition.atOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(positionRequest, 1, speakerPosition.atOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(positionRequest, 2, speakerPosition.atOrientation.z)
        
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(positionRequest, 0, speakerPosition.upOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(positionRequest, 1, speakerPosition.upOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(positionRequest, 2, speakerPosition.upOrientation.z)
        
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(positionRequest, 0, speakerPosition.leftOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(positionRequest, 1, speakerPosition.leftOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(positionRequest, 2, speakerPosition.leftOrientation.z)
        
        // Set listener position
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(positionRequest, 0, listenerPosition.position.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(positionRequest, 1, listenerPosition.position.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(positionRequest, 2, listenerPosition.position.z)
        
        // Set listener velocity
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(positionRequest, 0, listenerPosition.velocity.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(positionRequest, 1, listenerPosition.velocity.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(positionRequest, 2, listenerPosition.velocity.z)
        
        // Set listener orientations
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(positionRequest, 0, listenerPosition.atOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(positionRequest, 1, listenerPosition.atOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(positionRequest, 2, listenerPosition.atOrientation.z)
        
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(positionRequest, 0, listenerPosition.upOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(positionRequest, 1, listenerPosition.upOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(positionRequest, 2, listenerPosition.upOrientation.z)
        
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(positionRequest, 0, listenerPosition.leftOrientation.x)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(positionRequest, 1, listenerPosition.leftOrientation.y)
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(positionRequest, 2, listenerPosition.leftOrientation.z)
        
        messageController.sendRequest(positionRequest.base)
    }

    fun setLocalMicActive(active: Boolean): Boolean {
        synchronized(stateLock) {
            val changed = active != localMicActive
            localMicActive = active
            return changed
        }
    }

    fun setSpeakerSpeaking(speakerId: UUID, isSpeaking: Boolean): Boolean {
        synchronized(stateLock) {
            return if (isSpeaking) {
                speakers.add(speakerId)
            } else {
                speakers.remove(speakerId)
            }
        }
    }

    fun setState(newState: VoiceChatInfo.VoiceChatState): Boolean {
        synchronized(stateLock) {
            val changed = state != newState
            previousState = state
            state = newState
            return changed
        }
    }

    companion object {
        private const val TAG = "VoiceSession"
    }
}
