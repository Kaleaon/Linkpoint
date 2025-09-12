/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_participant_removed_reason;

public class vx_evt_participant_removed_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_participant_removed_t() {
        this(VxClientProxyJNI.new_vx_evt_participant_removed_t(), true);
    }

    protected vx_evt_participant_removed_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_participant_removed_t vx_evt_participant_removed_t2) {
        if (vx_evt_participant_removed_t2 != null) return vx_evt_participant_removed_t2.swigCPtr;
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

    public String getAccount_name() {
        return VxClientProxyJNI.vx_evt_participant_removed_t_account_name_get(this.swigCPtr, this);
    }

    public String getAlias_username() {
        return VxClientProxyJNI.vx_evt_participant_removed_t_alias_username_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_participant_removed_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public String getParticipant_uri() {
        return VxClientProxyJNI.vx_evt_participant_removed_t_participant_uri_get(this.swigCPtr, this);
    }

    public vx_participant_removed_reason getReason() {
        return vx_participant_removed_reason.swigToEnum(VxClientProxyJNI.vx_evt_participant_removed_t_reason_get(this.swigCPtr, this));
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_evt_participant_removed_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_participant_removed_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public void setAccount_name(String string2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_account_name_set(this.swigCPtr, this, string2);
    }

    public void setAlias_username(String string2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_alias_username_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setParticipant_uri(String string2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_participant_uri_set(this.swigCPtr, this, string2);
    }

    public void setReason(vx_participant_removed_reason vx_participant_removed_reason2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_reason_set(this.swigCPtr, this, vx_participant_removed_reason2.swigValue());
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_participant_removed_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }
}

