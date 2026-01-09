/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_double
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t

class vx_evt_session_updated_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_session_updated_t() {
        this(VxClientProxyJNI.new_vx_evt_session_updated_t(), true)
    }

    protected vx_evt_session_updated_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_session_updated_t vx_evt_session_updated_t2) {
        if (vx_evt_session_updated_t2 != null) return vx_evt_session_updated_t2.swigCPtr
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_evt_session_updated_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    Int getIs_ad_playing() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_ad_playing_get(this.swigCPtr, this)
    }

    Int getIs_focused() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_focused_get(this.swigCPtr, this)
    }

    Int getIs_muted() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_muted_get(this.swigCPtr, this)
    }

    Int getIs_text_muted() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_text_muted_get(this.swigCPtr, this)
    }

    Int getSession_font_id() {
        return VxClientProxyJNI.vx_evt_session_updated_t_session_font_id_get(this.swigCPtr, this)
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_evt_session_updated_t_session_handle_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_session_updated_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_position() {
        var l: Long = VxClientProxyJNI.vx_evt_session_updated_t_speaker_position_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    Int getTransmit_enabled() {
        return VxClientProxyJNI.vx_evt_session_updated_t_transmit_enabled_get(this.swigCPtr, this)
    }

    String getUri() {
        return VxClientProxyJNI.vx_evt_session_updated_t_uri_get(this.swigCPtr, this)
    }

    Int getVolume() {
        return VxClientProxyJNI.vx_evt_session_updated_t_volume_get(this.swigCPtr, this)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_session_updated_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setIs_ad_playing(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_ad_playing_set(this.swigCPtr, this, n)
    }

    Unit setIs_focused(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_focused_set(this.swigCPtr, this, n)
    }

    Unit setIs_muted(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_muted_set(this.swigCPtr, this, n)
    }

    Unit setIs_text_muted(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_text_muted_set(this.swigCPtr, this, n)
    }

    Unit setSession_font_id(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_session_font_id_set(this.swigCPtr, this, n)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_session_updated_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_session_updated_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSpeaker_position(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_evt_session_updated_t_speaker_position_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setTransmit_enabled(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_transmit_enabled_set(this.swigCPtr, this, n)
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_evt_session_updated_t_uri_set(this.swigCPtr, this, string2)
    }

    Unit setVolume(Int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_volume_set(this.swigCPtr, this, n)
    }
}

