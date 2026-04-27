/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_participant;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_account_channel_get_acl_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_account_channel_get_acl_t() {
        this(VxClientProxyJNI.new_vx_resp_account_channel_get_acl_t(), true);
    }

    protected vx_resp_account_channel_get_acl_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_account_channel_get_acl_t vx_resp_account_channel_get_acl_t2) {
        if (vx_resp_account_channel_get_acl_t2 != null) return vx_resp_account_channel_get_acl_t2.swigCPtr;
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_resp_base_t getBase() {
        long l = VxClientProxyJNI.vx_resp_account_channel_get_acl_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_participant getParticipants() {
        long l = VxClientProxyJNI.vx_resp_account_channel_get_acl_t_participants_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_participant(l, false);
        return null;
    }

    public int getParticipants_size() {
        return VxClientProxyJNI.vx_resp_account_channel_get_acl_t_participants_size_get(this.swigCPtr, this);
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_channel_get_acl_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setParticipants(SWIGTYPE_p_p_vx_participant sWIGTYPE_p_p_vx_participant) {
        VxClientProxyJNI.vx_resp_account_channel_get_acl_t_participants_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_participant.getCPtr(sWIGTYPE_p_p_vx_participant));
    }

    public void setParticipants_size(int n) {
        VxClientProxyJNI.vx_resp_account_channel_get_acl_t_participants_size_set(this.swigCPtr, this, n);
    }
}

