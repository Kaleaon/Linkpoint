/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_mute_scope;
import com.vivox.service.vx_req_base_t;

public class vx_req_session_set_participant_mute_for_me_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_session_set_participant_mute_for_me_t() {
        this(VxClientProxyJNI.new_vx_req_session_set_participant_mute_for_me_t(), true);
    }

    protected vx_req_session_set_participant_mute_for_me_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_session_set_participant_mute_for_me_t vx_req_session_set_participant_mute_for_me_t2) {
        if (vx_req_session_set_participant_mute_for_me_t2 != null) return vx_req_session_set_participant_mute_for_me_t2.swigCPtr;
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
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getMute() {
        return VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_mute_get(this.swigCPtr, this);
    }

    public String getParticipant_uri() {
        return VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_participant_uri_get(this.swigCPtr, this);
    }

    public vx_mute_scope getScope() {
        return vx_mute_scope.swigToEnum(VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_scope_get(this.swigCPtr, this));
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_session_handle_get(this.swigCPtr, this);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setMute(int n) {
        VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_mute_set(this.swigCPtr, this, n);
    }

    public void setParticipant_uri(String string2) {
        VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_participant_uri_set(this.swigCPtr, this, string2);
    }

    public void setScope(vx_mute_scope vx_mute_scope2) {
        VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_scope_set(this.swigCPtr, this, vx_mute_scope2.swigValue());
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_t_session_handle_set(this.swigCPtr, this, string2);
    }
}

