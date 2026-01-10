/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_state_account
import com.vivox.service.VxClientProxyJNI

class vx_state_connector_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_connector_t() {
        this(VxClientProxyJNI.new_vx_state_connector_t(), true)
    }

    protected vx_state_connector_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_connector_t vx_state_connector_t2) {
        if (vx_state_connector_t2 != null) return vx_state_connector_t2.swigCPtr
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

    String getConnector_handle() {
        return VxClientProxyJNI.vx_state_connector_t_connector_handle_get(this.swigCPtr, this)
    }

    Int getMic_mute() {
        return VxClientProxyJNI.vx_state_connector_t_mic_mute_get(this.swigCPtr, this)
    }

    Int getMic_vol() {
        return VxClientProxyJNI.vx_state_connector_t_mic_vol_get(this.swigCPtr, this)
    }

    Int getSpeaker_mute() {
        return VxClientProxyJNI.vx_state_connector_t_speaker_mute_get(this.swigCPtr, this)
    }

    Int getSpeaker_vol() {
        return VxClientProxyJNI.vx_state_connector_t_speaker_vol_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_account getState_accounts() {
        var l: Long = VxClientProxyJNI.vx_state_connector_t_state_accounts_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_account(l, false)
        return null
    }

    Int getState_accounts_count() {
        return VxClientProxyJNI.vx_state_connector_t_state_accounts_count_get(this.swigCPtr, this)
    }

    Unit setConnector_handle(String string2) {
        VxClientProxyJNI.vx_state_connector_t_connector_handle_set(this.swigCPtr, this, string2)
    }

    Unit setMic_mute(Int n) {
        VxClientProxyJNI.vx_state_connector_t_mic_mute_set(this.swigCPtr, this, n)
    }

    Unit setMic_vol(Int n) {
        VxClientProxyJNI.vx_state_connector_t_mic_vol_set(this.swigCPtr, this, n)
    }

    Unit setSpeaker_mute(Int n) {
        VxClientProxyJNI.vx_state_connector_t_speaker_mute_set(this.swigCPtr, this, n)
    }

    Unit setSpeaker_vol(Int n) {
        VxClientProxyJNI.vx_state_connector_t_speaker_vol_set(this.swigCPtr, this, n)
    }

    Unit setState_accounts(SWIGTYPE_p_p_vx_state_account sWIGTYPE_p_p_vx_state_account) {
        VxClientProxyJNI.vx_state_connector_t_state_accounts_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_account.getCPtr(sWIGTYPE_p_p_vx_state_account))
    }

    Unit setState_accounts_count(Int n) {
        VxClientProxyJNI.vx_state_connector_t_state_accounts_count_set(this.swigCPtr, this, n)
    }
}

