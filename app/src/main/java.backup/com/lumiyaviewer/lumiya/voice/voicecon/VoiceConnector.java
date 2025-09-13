/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice.voicecon;

import com.lumiyaviewer.lumiya.voice.Debug;
import com.lumiyaviewer.lumiya.voice.VivoxMessageController;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceAccountConnection;
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceException;
import com.vivox.service.VxClientProxy;
import com.vivox.service.vx_connector_mode;
import com.vivox.service.vx_req_connector_create_t;
import com.vivox.service.vx_req_connector_initiate_shutdown_t;
import com.vivox.service.vx_req_connector_mute_local_mic_t;
import com.vivox.service.vx_req_connector_mute_local_speaker_t;
import com.vivox.service.vx_req_connector_set_local_mic_volume_t;
import com.vivox.service.vx_req_connector_set_local_speaker_volume_t;
import com.vivox.service.vx_resp_base_t;
import com.vivox.service.vx_resp_connector_create_t;
import com.vivox.service.vx_session_handle_type;

public class VoiceConnector {
    private boolean disposed = false;
    private final String handle;
    private final VivoxMessageController messageController;
    private final String voiceAccountServerName;

    public VoiceConnector(VivoxMessageController object, String string2) throws VoiceException {
        this.messageController = object;
        this.voiceAccountServerName = string2;
        vx_req_connector_create_t vx_req_connector_create_t2 = new vx_req_connector_create_t();
        vx_req_connector_create_t2.setMode(vx_connector_mode.connector_mode_normal);
        vx_req_connector_create_t2.setAcct_mgmt_server(string2);
        vx_req_connector_create_t2.setMinimum_port(12000);
        vx_req_connector_create_t2.setMaximum_port(65000);
        vx_req_connector_create_t2.setClient_name("V2 SDK");
        vx_req_connector_create_t2.setSession_handle_type(vx_session_handle_type.session_handle_type_legacy);
        vx_req_connector_create_t2.setApplication("");
        vx_req_connector_create_t2.setMax_calls(12);
        object = ((VivoxMessageController)object).sendRequestAndWait(vx_req_connector_create_t2.getBase());
        if (object == null) {
            throw new VoiceException("Failed to create voice connector");
        }
        if ((object = VxClientProxy.vx_message_base_t2vx_resp_connector_create_t(((vx_resp_base_t)object).getMessage())) == null) {
            throw new VoiceException("Failed to create voice connector");
        }
        Debug.Printf("Voice: status code %d", ((vx_resp_connector_create_t)object).getBase().getStatus_code());
        Debug.Printf("Voice: return code %d", ((vx_resp_connector_create_t)object).getBase().getReturn_code());
        Debug.Printf("Voice: status string %s", ((vx_resp_connector_create_t)object).getBase().getStatus_string());
        Debug.Printf("Voice: ext status %s", ((vx_resp_connector_create_t)object).getBase().getExtended_status_info());
        object = ((vx_resp_connector_create_t)object).getConnector_handle();
        Debug.Printf("Voice: got connector handle '%s'", object);
        if (object == null) {
            throw new VoiceException("Failed to create voice connector");
        }
        this.handle = object;
    }

    public VoiceAccountConnection createAccountConnection(VoiceLoginInfo voiceLoginInfo) throws VoiceException {
        return new VoiceAccountConnection(this.messageController, this, voiceLoginInfo);
    }

    public void dispose() {
        if (!this.disposed) {
            this.disposed = true;
            vx_req_connector_initiate_shutdown_t vx_req_connector_initiate_shutdown_t2 = new vx_req_connector_initiate_shutdown_t();
            vx_req_connector_initiate_shutdown_t2.setConnector_handle(this.handle);
            vx_req_connector_initiate_shutdown_t2.setClient_name("V2 SDK");
            this.messageController.sendRequestAndWait(vx_req_connector_initiate_shutdown_t2.getBase());
        }
    }

    public String getHandle() {
        return this.handle;
    }

    public String getVoiceAccountServerName() {
        return this.voiceAccountServerName;
    }

    public void setLocalMicVolume(int n) {
        vx_req_connector_set_local_mic_volume_t vx_req_connector_set_local_mic_volume_t2 = new vx_req_connector_set_local_mic_volume_t();
        vx_req_connector_set_local_mic_volume_t2.setConnector_handle(this.handle);
        vx_req_connector_set_local_mic_volume_t2.setVolume(n);
        this.messageController.sendRequest(vx_req_connector_set_local_mic_volume_t2.getBase());
    }

    public void setLocalSpeakerVolume(int n) {
        vx_req_connector_set_local_speaker_volume_t vx_req_connector_set_local_speaker_volume_t2 = new vx_req_connector_set_local_speaker_volume_t();
        vx_req_connector_set_local_speaker_volume_t2.setConnector_handle(this.handle);
        vx_req_connector_set_local_speaker_volume_t2.setVolume(n);
        this.messageController.sendRequest(vx_req_connector_set_local_speaker_volume_t2.getBase());
    }

    /*
     * Enabled aggressive block sorting
     */
    public void setMuteLocalMic(boolean bl) {
        vx_req_connector_mute_local_mic_t vx_req_connector_mute_local_mic_t2 = new vx_req_connector_mute_local_mic_t();
        vx_req_connector_mute_local_mic_t2.setConnector_handle(this.handle);
        int n = bl ? 1 : 0;
        vx_req_connector_mute_local_mic_t2.setMute_level(n);
        this.messageController.sendRequest(vx_req_connector_mute_local_mic_t2.getBase());
    }

    /*
     * Enabled aggressive block sorting
     */
    public void setMuteLocalSpeaker(boolean bl) {
        vx_req_connector_mute_local_speaker_t vx_req_connector_mute_local_speaker_t2 = new vx_req_connector_mute_local_speaker_t();
        vx_req_connector_mute_local_speaker_t2.setConnector_handle(this.handle);
        int n = bl ? 1 : 0;
        vx_req_connector_mute_local_speaker_t2.setMute_level(n);
        this.messageController.sendRequest(vx_req_connector_mute_local_speaker_t2.getBase());
    }
}

