/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_state_participant;
import com.vivox.service.VxClientProxyJNI;

public class vx_state_session_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_state_session_t() {
        this(VxClientProxyJNI.new_vx_state_session_t(), true);
    }

    protected vx_state_session_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_state_session_t vx_state_session_t2) {
        if (vx_state_session_t2 != null) return vx_state_session_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L;
                return;
            }
            this.swigCMemOwn = false;
            UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("C++ destructor does not have public access");
            throw unsupportedOperationException;
        }
    }

    public String getDurable_media_id() {
        return VxClientProxyJNI.vx_state_session_t_durable_media_id_get(this.swigCPtr, this);
    }

    public int getHas_audio() {
        return VxClientProxyJNI.vx_state_session_t_has_audio_get(this.swigCPtr, this);
    }

    public int getHas_text() {
        return VxClientProxyJNI.vx_state_session_t_has_text_get(this.swigCPtr, this);
    }

    public int getIs_audio_muted_for_me() {
        return VxClientProxyJNI.vx_state_session_t_is_audio_muted_for_me_get(this.swigCPtr, this);
    }

    public int getIs_connected() {
        return VxClientProxyJNI.vx_state_session_t_is_connected_get(this.swigCPtr, this);
    }

    public int getIs_focused() {
        return VxClientProxyJNI.vx_state_session_t_is_focused_get(this.swigCPtr, this);
    }

    public int getIs_incoming() {
        return VxClientProxyJNI.vx_state_session_t_is_incoming_get(this.swigCPtr, this);
    }

    public int getIs_positional() {
        return VxClientProxyJNI.vx_state_session_t_is_positional_get(this.swigCPtr, this);
    }

    public int getIs_text_muted_for_me() {
        return VxClientProxyJNI.vx_state_session_t_is_text_muted_for_me_get(this.swigCPtr, this);
    }

    public int getIs_transmitting() {
        return VxClientProxyJNI.vx_state_session_t_is_transmitting_get(this.swigCPtr, this);
    }

    public String getName() {
        return VxClientProxyJNI.vx_state_session_t_name_get(this.swigCPtr, this);
    }

    public int getSession_font_id() {
        return VxClientProxyJNI.vx_state_session_t_session_font_id_get(this.swigCPtr, this);
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_state_session_t_session_handle_get(this.swigCPtr, this);
    }

    public int getState_participant_count() {
        return VxClientProxyJNI.vx_state_session_t_state_participant_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_state_participant getState_participants() {
        long l = VxClientProxyJNI.vx_state_session_t_state_participants_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_state_participant(l, false);
        return null;
    }

    public String getUri() {
        return VxClientProxyJNI.vx_state_session_t_uri_get(this.swigCPtr, this);
    }

    public int getVolume() {
        return VxClientProxyJNI.vx_state_session_t_volume_get(this.swigCPtr, this);
    }

    public void setDurable_media_id(String string2) {
        VxClientProxyJNI.vx_state_session_t_durable_media_id_set(this.swigCPtr, this, string2);
    }

    public void setHas_audio(int n) {
        VxClientProxyJNI.vx_state_session_t_has_audio_set(this.swigCPtr, this, n);
    }

    public void setHas_text(int n) {
        VxClientProxyJNI.vx_state_session_t_has_text_set(this.swigCPtr, this, n);
    }

    public void setIs_audio_muted_for_me(int n) {
        VxClientProxyJNI.vx_state_session_t_is_audio_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setIs_connected(int n) {
        VxClientProxyJNI.vx_state_session_t_is_connected_set(this.swigCPtr, this, n);
    }

    public void setIs_focused(int n) {
        VxClientProxyJNI.vx_state_session_t_is_focused_set(this.swigCPtr, this, n);
    }

    public void setIs_incoming(int n) {
        VxClientProxyJNI.vx_state_session_t_is_incoming_set(this.swigCPtr, this, n);
    }

    public void setIs_positional(int n) {
        VxClientProxyJNI.vx_state_session_t_is_positional_set(this.swigCPtr, this, n);
    }

    public void setIs_text_muted_for_me(int n) {
        VxClientProxyJNI.vx_state_session_t_is_text_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setIs_transmitting(int n) {
        VxClientProxyJNI.vx_state_session_t_is_transmitting_set(this.swigCPtr, this, n);
    }

    public void setName(String string2) {
        VxClientProxyJNI.vx_state_session_t_name_set(this.swigCPtr, this, string2);
    }

    public void setSession_font_id(int n) {
        VxClientProxyJNI.vx_state_session_t_session_font_id_set(this.swigCPtr, this, n);
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_state_session_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setState_participant_count(int n) {
        VxClientProxyJNI.vx_state_session_t_state_participant_count_set(this.swigCPtr, this, n);
    }

    public void setState_participants(SWIGTYPE_p_p_vx_state_participant sWIGTYPE_p_p_vx_state_participant) {
        VxClientProxyJNI.vx_state_session_t_state_participants_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_participant.getCPtr(sWIGTYPE_p_p_vx_state_participant));
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_state_session_t_uri_set(this.swigCPtr, this, string2);
    }

    public void setVolume(int n) {
        VxClientProxyJNI.vx_state_session_t_volume_set(this.swigCPtr, this, n);
    }
}

