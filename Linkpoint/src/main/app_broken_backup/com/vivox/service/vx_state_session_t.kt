/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_state_participant
import com.vivox.service.VxClientProxyJNI

class vx_state_session_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_session_t() {
        this(VxClientProxyJNI.new_vx_state_session_t(), true)
    }

    protected vx_state_session_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_session_t vx_state_session_t2) {
        if (vx_state_session_t2 != null) return vx_state_session_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L
                return
            }
            this.swigCMemOwn = false
            UnsupportedOperationException unsupportedOperationException = UnsupportedOperationException("C++ destructor does not have access")
            throw unsupportedOperationException
        }
    }

    String getDurable_media_id() {
        return VxClientProxyJNI.vx_state_session_t_durable_media_id_get(this.swigCPtr, this)
    }

    Int getHas_audio() {
        return VxClientProxyJNI.vx_state_session_t_has_audio_get(this.swigCPtr, this)
    }

    Int getHas_text() {
        return VxClientProxyJNI.vx_state_session_t_has_text_get(this.swigCPtr, this)
    }

    Int getIs_audio_muted_for_me() {
        return VxClientProxyJNI.vx_state_session_t_is_audio_muted_for_me_get(this.swigCPtr, this)
    }

    Int getIs_connected() {
        return VxClientProxyJNI.vx_state_session_t_is_connected_get(this.swigCPtr, this)
    }

    Int getIs_focused() {
        return VxClientProxyJNI.vx_state_session_t_is_focused_get(this.swigCPtr, this)
    }

    Int getIs_incoming() {
        return VxClientProxyJNI.vx_state_session_t_is_incoming_get(this.swigCPtr, this)
    }

    Int getIs_positional() {
        return VxClientProxyJNI.vx_state_session_t_is_positional_get(this.swigCPtr, this)
    }

    Int getIs_text_muted_for_me() {
        return VxClientProxyJNI.vx_state_session_t_is_text_muted_for_me_get(this.swigCPtr, this)
    }

    Int getIs_transmitting() {
        return VxClientProxyJNI.vx_state_session_t_is_transmitting_get(this.swigCPtr, this)
    }

    String getName() {
        return VxClientProxyJNI.vx_state_session_t_name_get(this.swigCPtr, this)
    }

    Int getSession_font_id() {
        return VxClientProxyJNI.vx_state_session_t_session_font_id_get(this.swigCPtr, this)
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_state_session_t_session_handle_get(this.swigCPtr, this)
    }

    Int getState_participant_count() {
        return VxClientProxyJNI.vx_state_session_t_state_participant_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_participant getState_participants() {
        var l: Long = VxClientProxyJNI.vx_state_session_t_state_participants_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_participant(l, false)
        return null
    }

    String getUri() {
        return VxClientProxyJNI.vx_state_session_t_uri_get(this.swigCPtr, this)
    }

    Int getVolume() {
        return VxClientProxyJNI.vx_state_session_t_volume_get(this.swigCPtr, this)
    }

    Unit setDurable_media_id(String string2) {
        VxClientProxyJNI.vx_state_session_t_durable_media_id_set(this.swigCPtr, this, string2)
    }

    Unit setHas_audio(Int n) {
        VxClientProxyJNI.vx_state_session_t_has_audio_set(this.swigCPtr, this, n)
    }

    Unit setHas_text(Int n) {
        VxClientProxyJNI.vx_state_session_t_has_text_set(this.swigCPtr, this, n)
    }

    Unit setIs_audio_muted_for_me(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_audio_muted_for_me_set(this.swigCPtr, this, n)
    }

    Unit setIs_connected(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_connected_set(this.swigCPtr, this, n)
    }

    Unit setIs_focused(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_focused_set(this.swigCPtr, this, n)
    }

    Unit setIs_incoming(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_incoming_set(this.swigCPtr, this, n)
    }

    Unit setIs_positional(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_positional_set(this.swigCPtr, this, n)
    }

    Unit setIs_text_muted_for_me(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_text_muted_for_me_set(this.swigCPtr, this, n)
    }

    Unit setIs_transmitting(Int n) {
        VxClientProxyJNI.vx_state_session_t_is_transmitting_set(this.swigCPtr, this, n)
    }

    Unit setName(String string2) {
        VxClientProxyJNI.vx_state_session_t_name_set(this.swigCPtr, this, string2)
    }

    Unit setSession_font_id(Int n) {
        VxClientProxyJNI.vx_state_session_t_session_font_id_set(this.swigCPtr, this, n)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_state_session_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setState_participant_count(Int n) {
        VxClientProxyJNI.vx_state_session_t_state_participant_count_set(this.swigCPtr, this, n)
    }

    Unit setState_participants(SWIGTYPE_p_p_vx_state_participant sWIGTYPE_p_p_vx_state_participant) {
        VxClientProxyJNI.vx_state_session_t_state_participants_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_participant.getCPtr(sWIGTYPE_p_p_vx_state_participant))
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_state_session_t_uri_set(this.swigCPtr, this, string2)
    }

    Unit setVolume(Int n) {
        VxClientProxyJNI.vx_state_session_t_volume_set(this.swigCPtr, this, n)
    }
}

