/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_state_account;
import com.vivox.service.VxClientProxyJNI;

public class vx_state_connector_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_state_connector_t() {
        this(VxClientProxyJNI.new_vx_state_connector_t(), true);
    }

    protected vx_state_connector_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_state_connector_t vx_state_connector_t2) {
        if (vx_state_connector_t2 != null) return vx_state_connector_t2.swigCPtr;
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

    public String getConnector_handle() {
        return VxClientProxyJNI.vx_state_connector_t_connector_handle_get(this.swigCPtr, this);
    }

    public int getMic_mute() {
        return VxClientProxyJNI.vx_state_connector_t_mic_mute_get(this.swigCPtr, this);
    }

    public int getMic_vol() {
        return VxClientProxyJNI.vx_state_connector_t_mic_vol_get(this.swigCPtr, this);
    }

    public int getSpeaker_mute() {
        return VxClientProxyJNI.vx_state_connector_t_speaker_mute_get(this.swigCPtr, this);
    }

    public int getSpeaker_vol() {
        return VxClientProxyJNI.vx_state_connector_t_speaker_vol_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_state_account getState_accounts() {
        long l = VxClientProxyJNI.vx_state_connector_t_state_accounts_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_state_account(l, false);
        return null;
    }

    public int getState_accounts_count() {
        return VxClientProxyJNI.vx_state_connector_t_state_accounts_count_get(this.swigCPtr, this);
    }

    public void setConnector_handle(String string2) {
        VxClientProxyJNI.vx_state_connector_t_connector_handle_set(this.swigCPtr, this, string2);
    }

    public void setMic_mute(int n) {
        VxClientProxyJNI.vx_state_connector_t_mic_mute_set(this.swigCPtr, this, n);
    }

    public void setMic_vol(int n) {
        VxClientProxyJNI.vx_state_connector_t_mic_vol_set(this.swigCPtr, this, n);
    }

    public void setSpeaker_mute(int n) {
        VxClientProxyJNI.vx_state_connector_t_speaker_mute_set(this.swigCPtr, this, n);
    }

    public void setSpeaker_vol(int n) {
        VxClientProxyJNI.vx_state_connector_t_speaker_vol_set(this.swigCPtr, this, n);
    }

    public void setState_accounts(SWIGTYPE_p_p_vx_state_account sWIGTYPE_p_p_vx_state_account) {
        VxClientProxyJNI.vx_state_connector_t_state_accounts_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_account.getCPtr(sWIGTYPE_p_p_vx_state_account));
    }

    public void setState_accounts_count(int n) {
        VxClientProxyJNI.vx_state_connector_t_state_accounts_count_set(this.swigCPtr, this, n);
    }
}

