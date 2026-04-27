/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_session_text_state;

public class vx_evt_text_stream_updated_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_text_stream_updated_t() {
        this(VxClientProxyJNI.new_vx_evt_text_stream_updated_t(), true);
    }

    protected vx_evt_text_stream_updated_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_text_stream_updated_t vx_evt_text_stream_updated_t2) {
        if (vx_evt_text_stream_updated_t2 != null) return vx_evt_text_stream_updated_t2.swigCPtr;
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
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_text_stream_updated_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public int getEnabled() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_enabled_get(this.swigCPtr, this);
    }

    public int getIncoming() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_incoming_get(this.swigCPtr, this);
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public vx_session_text_state getState() {
        return vx_session_text_state.swigToEnum(VxClientProxyJNI.vx_evt_text_stream_updated_t_state_get(this.swigCPtr, this));
    }

    public int getStatus_code() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_status_code_get(this.swigCPtr, this);
    }

    public String getStatus_string() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_status_string_get(this.swigCPtr, this);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setEnabled(int n) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_enabled_set(this.swigCPtr, this, n);
    }

    public void setIncoming(int n) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_incoming_set(this.swigCPtr, this, n);
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setState(vx_session_text_state vx_session_text_state2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_state_set(this.swigCPtr, this, vx_session_text_state2.swigValue());
    }

    public void setStatus_code(int n) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_status_code_set(this.swigCPtr, this, n);
    }

    public void setStatus_string(String string2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_status_string_set(this.swigCPtr, this, string2);
    }
}

