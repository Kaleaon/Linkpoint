/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_double;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_req_base_t;

public class vx_req_sessiongroup_set_session_3d_position_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_sessiongroup_set_session_3d_position_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_set_session_3d_position_t(), true);
    }

    protected vx_req_sessiongroup_set_session_3d_position_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_sessiongroup_set_session_3d_position_t vx_req_sessiongroup_set_session_3d_position_t2) {
        if (vx_req_sessiongroup_set_session_3d_position_t2 != null) return vx_req_sessiongroup_set_session_3d_position_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_double getSpeaker_at_orientation() {
        long l = VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_double(l, false);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_double getSpeaker_position() {
        long l = VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_position_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_double(l, false);
        return null;
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setSpeaker_at_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double));
    }

    public void setSpeaker_position(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_position_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double));
    }
}

