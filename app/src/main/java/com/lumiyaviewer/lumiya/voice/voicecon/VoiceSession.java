/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice.voicecon;

import com.lumiyaviewer.lumiya.voice.Debug;
import com.lumiyaviewer.lumiya.voice.VivoxMessageController;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.vivox.service.VxClientProxy;
import com.vivox.service.vx_evt_session_added_t;
import com.vivox.service.vx_req_session_media_connect_t;
import com.vivox.service.vx_req_session_media_disconnect_t;
import com.vivox.service.vx_req_session_set_3d_position_t;
import com.vivox.service.vx_req_session_terminate_t;
import com.vivox.service.vx_termination_status;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceSession {
    private boolean disposed = false;
    private final String handle;
    private final boolean isIncoming;
    private boolean localMicActive = false;
    private final VivoxMessageController messageController;
    private VoiceChatInfo.VoiceChatState previousState;
    private final String sessionGroupHandle;
    private final Set<UUID> speakers;
    private VoiceChatInfo.VoiceChatState state;
    private final Object stateLock = new Object();
    private final VoiceChannelInfo voiceChannelInfo;

    /*
     * Enabled aggressive block sorting
     */
    public VoiceSession(VivoxMessageController vivoxMessageController, vx_evt_session_added_t vx_evt_session_added_t2, @Nullable VoiceChannelInfo voiceChannelInfo) {
        this.previousState = VoiceChatInfo.VoiceChatState.Connecting;
        this.state = VoiceChatInfo.VoiceChatState.Connecting;
        this.speakers = new HashSet<UUID>();
        this.messageController = vivoxMessageController;
        this.handle = vx_evt_session_added_t2.getSession_handle();
        this.sessionGroupHandle = vx_evt_session_added_t2.getSessiongroup_handle();
        if (voiceChannelInfo == null) {
            voiceChannelInfo = new VoiceChannelInfo(vx_evt_session_added_t2.getUri(), false, false);
        }
        this.voiceChannelInfo = voiceChannelInfo;
        boolean bl = vx_evt_session_added_t2.getIncoming() != 0;
        this.isIncoming = bl;
        Debug.Printf("Voice: created session: %s (uri %s)", this.handle, this.voiceChannelInfo.voiceChannelURI);
    }

    public void dispose() {
        if (!this.disposed) {
            this.disposed = true;
            vx_req_session_terminate_t vx_req_session_terminate_t2 = new vx_req_session_terminate_t();
            vx_req_session_terminate_t2.setSession_handle(this.handle);
            this.messageController.sendRequestAndWait(vx_req_session_terminate_t2.getBase());
        }
    }

    public String getHandle() {
        return this.handle;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public VoiceChatInfo.VoiceChatState getState() {
        Object object = this.stateLock;
        synchronized (object) {
            return this.state;
        }
    }

    public VoiceChannelInfo getVoiceChannelInfo() {
        return this.voiceChannelInfo;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nonnull
    public VoiceChatInfo getVoiceChatInfo() {
        Object object;
        Object object2 = this.stateLock;
        synchronized (object2) {
            Debug.Printf("Voice: got session state: %s (%s)", new Object[]{this.state, this});
            if (this.state == VoiceChatInfo.VoiceChatState.None && this.previousState == VoiceChatInfo.VoiceChatState.None) {
                object = VoiceChatInfo.empty();
                Debug.Printf("Voice: returning empty session state", new Object[0]);
            } else {
                VoiceChatInfo voiceChatInfo = null;
                object = voiceChatInfo;
                if (!this.speakers.isEmpty()) {
                    Iterator<UUID> iterator = this.speakers.iterator();
                    object = voiceChatInfo;
                    if (iterator.hasNext()) {
                        object = iterator.next();
                    }
                }
                object = VoiceChatInfo.create(this.state, this.previousState, this.speakers.size(), (UUID)object, this.voiceChannelInfo.isConference, this.localMicActive);
            }
            this.previousState = this.state;
        }
        Debug.Printf("Voice: returning session state: %s", new Object[]{((VoiceChatInfo)object).state});
        return object;
    }

    public boolean isIncoming() {
        return this.isIncoming;
    }

    public void mediaConnect() {
        vx_req_session_media_connect_t vx_req_session_media_connect_t2 = new vx_req_session_media_connect_t();
        vx_req_session_media_connect_t2.setSession_handle(this.handle);
        vx_req_session_media_connect_t2.setSessiongroup_handle(this.sessionGroupHandle);
        vx_req_session_media_connect_t2.setSession_font_id(0);
        this.messageController.sendRequest(vx_req_session_media_connect_t2.getBase());
    }

    public void mediaDisconnect(vx_termination_status vx_termination_status2) {
        vx_req_session_media_disconnect_t vx_req_session_media_disconnect_t2 = new vx_req_session_media_disconnect_t();
        vx_req_session_media_disconnect_t2.setSession_handle(this.handle);
        vx_req_session_media_disconnect_t2.setSessiongroup_handle(this.sessionGroupHandle);
        vx_req_session_media_disconnect_t2.setTermination_status(vx_termination_status2);
        this.messageController.sendRequest(vx_req_session_media_disconnect_t2.getBase());
    }

    public void set3DPosition(Voice3DPosition voice3DPosition, Voice3DPosition voice3DPosition2) {
        Debug.Printf("Voice: set3D: speaker %s", voice3DPosition.toString());
        Debug.Printf("Voice: set3D: listener %s", voice3DPosition2.toString());
        vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2 = new vx_req_session_set_3d_position_t();
        vx_req_session_set_3d_position_t2.setSession_handle(this.handle);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.position.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.position.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.position.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.velocity.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.velocity.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.velocity.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.atOrientation.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.atOrientation.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.atOrientation.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.upOrientation.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.upOrientation.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.upOrientation.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition.leftOrientation.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition.leftOrientation.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition.leftOrientation.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.position.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.position.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.position.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.velocity.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.velocity.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.velocity.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.atOrientation.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.atOrientation.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.atOrientation.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.upOrientation.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.upOrientation.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.upOrientation.z);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t2, 0, voice3DPosition2.leftOrientation.x);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t2, 1, voice3DPosition2.leftOrientation.y);
        VxClientProxy.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t2, 2, voice3DPosition2.leftOrientation.z);
        this.messageController.sendRequest(vx_req_session_set_3d_position_t2.getBase());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean setLocalMicActive(boolean bl) {
        Object object = this.stateLock;
        synchronized (object) {
            boolean bl2 = bl != this.localMicActive;
            this.localMicActive = bl;
            return bl2;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean setSpeakerSpeaking(UUID uUID, boolean bl) {
        Object object = this.stateLock;
        synchronized (object) {
            if (!bl) return this.speakers.remove(uUID);
            return this.speakers.add(uUID);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean setState(VoiceChatInfo.VoiceChatState voiceChatState) {
        boolean bl = false;
        Object object = this.stateLock;
        synchronized (object) {
            if (this.state != voiceChatState) {
                this.previousState = this.state;
                this.state = voiceChatState;
                bl = true;
                Debug.Printf("Voice: new session state: %s (%s)", new Object[]{this.state, this});
            }
            return bl;
        }
    }
}

