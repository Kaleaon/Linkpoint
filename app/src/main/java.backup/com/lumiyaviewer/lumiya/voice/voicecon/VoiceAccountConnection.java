/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice.voicecon;

import com.lumiyaviewer.lumiya.voice.Debug;
import com.lumiyaviewer.lumiya.voice.VivoxMessageController;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceConnector;
import com.lumiyaviewer.lumiya.voice.voicecon.VoiceException;
import com.vivox.service.VxClientProxy;
import com.vivox.service.vx_buddy_management_mode;
import com.vivox.service.vx_password_hash_algorithm_t;
import com.vivox.service.vx_req_account_login_t;
import com.vivox.service.vx_req_account_logout_t;
import com.vivox.service.vx_req_session_create_t;
import com.vivox.service.vx_resp_account_login_t;
import com.vivox.service.vx_resp_base_t;
import com.vivox.service.vx_resp_session_create_t;
import com.vivox.service.vx_session_answer_mode;
import javax.annotation.Nullable;

public class VoiceAccountConnection {
    private boolean disposed = false;
    private final String handle;
    private final VivoxMessageController messageController;
    private final VoiceLoginInfo voiceLoginInfo;

    public VoiceAccountConnection(VivoxMessageController object, VoiceConnector voiceConnector, VoiceLoginInfo voiceLoginInfo) throws VoiceException {
        this.messageController = object;
        this.voiceLoginInfo = voiceLoginInfo;
        vx_req_account_login_t vx_req_account_login_t2 = new vx_req_account_login_t();
        vx_req_account_login_t2.setAcct_mgmt_server(voiceLoginInfo.voiceAccountServerName);
        vx_req_account_login_t2.setConnector_handle(voiceConnector.getHandle());
        vx_req_account_login_t2.setAcct_name(voiceLoginInfo.userName);
        vx_req_account_login_t2.setAcct_password(voiceLoginInfo.password);
        vx_req_account_login_t2.setAnswer_mode(vx_session_answer_mode.mode_verify_answer);
        vx_req_account_login_t2.setEnable_buddies_and_presence(0);
        vx_req_account_login_t2.setBuddy_management_mode(vx_buddy_management_mode.mode_application);
        vx_req_account_login_t2.setParticipant_property_frequency(10);
        Debug.Printf("Voice: sending login request", new Object[0]);
        object = ((VivoxMessageController)object).sendRequestAndWait(vx_req_account_login_t2.getBase());
        if (object == null) {
            throw new VoiceException("Failed to login");
        }
        Debug.Printf("Voice: got login response %s", object);
        object = VxClientProxy.vx_message_base_t2vx_resp_account_login_t(((vx_resp_base_t)object).getMessage());
        if (object == null) {
            throw new VoiceException("Failed to login");
        }
        Debug.Printf("Voice: status code %d", ((vx_resp_account_login_t)object).getBase().getStatus_code());
        Debug.Printf("Voice: return code %d", ((vx_resp_account_login_t)object).getBase().getReturn_code());
        Debug.Printf("Voice: status string %s", ((vx_resp_account_login_t)object).getBase().getStatus_string());
        Debug.Printf("Voice: ext status %s", ((vx_resp_account_login_t)object).getBase().getExtended_status_info());
        object = ((vx_resp_account_login_t)object).getAccount_handle();
        if (object == null) {
            throw new VoiceException("Failed to login");
        }
        this.handle = object;
    }

    public void createVoiceSession(VoiceChannelInfo object, @Nullable String string2) throws VoiceException {
        vx_req_session_create_t vx_req_session_create_t2 = new vx_req_session_create_t();
        vx_req_session_create_t2.setAccount_handle(this.handle);
        if (string2 != null) {
            vx_req_session_create_t2.setPassword(string2);
            vx_req_session_create_t2.setPassword_hash_algorithm(vx_password_hash_algorithm_t.password_hash_algorithm_sha1_username_hash);
        }
        vx_req_session_create_t2.setUri(((VoiceChannelInfo)object).voiceChannelURI);
        vx_req_session_create_t2.setConnect_audio(1);
        vx_req_session_create_t2.setConnect_text(0);
        vx_req_session_create_t2.setSession_font_id(0);
        Debug.Printf("Voice: sending session create request", new Object[0]);
        object = this.messageController.sendRequestAndWait(vx_req_session_create_t2.getBase());
        Debug.Printf("Voice: got session create response: %s", object);
        if (object == null) {
            throw new VoiceException("Failed to create voice session");
        }
        if ((object = VxClientProxy.vx_message_base_t2vx_resp_session_create_t(((vx_resp_base_t)object).getMessage())) == null) {
            throw new VoiceException("Failed to create voice session");
        }
        if (((vx_resp_session_create_t)object).getSession_handle() == null) {
            throw new VoiceException("Failed to create voice session");
        }
    }

    public void dispose() {
        if (!this.disposed) {
            this.disposed = true;
            vx_req_account_logout_t vx_req_account_logout_t2 = new vx_req_account_logout_t();
            vx_req_account_logout_t2.setAccount_handle(this.handle);
            vx_req_account_logout_t2.setLogout_reason("");
            this.messageController.sendRequestAndWait(vx_req_account_logout_t2.getBase());
        }
    }

    public String getHandle() {
        return this.handle;
    }

    public VoiceLoginInfo getVoiceLoginInfo() {
        return this.voiceLoginInfo;
    }
}

