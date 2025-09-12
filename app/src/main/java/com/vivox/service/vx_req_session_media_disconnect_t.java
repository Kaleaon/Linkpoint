/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_media_type;
import com.vivox.service.vx_req_base_t;
import com.vivox.service.vx_termination_status;

public class vx_req_session_media_disconnect_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_session_media_disconnect_t() {
        this(VxClientProxyJNI.new_vx_req_session_media_disconnect_t(), true);
    }

    protected vx_req_session_media_disconnect_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_session_media_disconnect_t vx_req_session_media_disconnect_t2) {
        if (vx_req_session_media_disconnect_t2 != null) return vx_req_session_media_disconnect_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_req_session_media_disconnect_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public vx_media_type getMedia() {
        return vx_media_type.swigToEnum(VxClientProxyJNI.vx_req_session_media_disconnect_t_media_get(this.swigCPtr, this));
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_req_session_media_disconnect_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_session_media_disconnect_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public vx_termination_status getTermination_status() {
        return vx_termination_status.swigToEnum(VxClientProxyJNI.vx_req_session_media_disconnect_t_termination_status_get(this.swigCPtr, this));
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_session_media_disconnect_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setMedia(vx_media_type vx_media_type2) {
        VxClientProxyJNI.vx_req_session_media_disconnect_t_media_set(this.swigCPtr, this, vx_media_type2.swigValue());
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_req_session_media_disconnect_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_session_media_disconnect_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setTermination_status(vx_termination_status vx_termination_status2) {
        VxClientProxyJNI.vx_req_session_media_disconnect_t_termination_status_set(this.swigCPtr, this, vx_termination_status2.swigValue());
    }
}

