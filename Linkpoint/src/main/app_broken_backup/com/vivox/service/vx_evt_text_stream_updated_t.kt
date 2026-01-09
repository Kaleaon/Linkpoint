/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_session_text_state

class vx_evt_text_stream_updated_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_text_stream_updated_t() {
        this(VxClientProxyJNI.new_vx_evt_text_stream_updated_t(), true)
    }

    protected vx_evt_text_stream_updated_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_text_stream_updated_t vx_evt_text_stream_updated_t2) {
        if (vx_evt_text_stream_updated_t2 != null) return vx_evt_text_stream_updated_t2.swigCPtr
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
        var l: Long = VxClientProxyJNI.vx_evt_text_stream_updated_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    Int getEnabled() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_enabled_get(this.swigCPtr, this)
    }

    Int getIncoming() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_incoming_get(this.swigCPtr, this)
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_session_handle_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    vx_session_text_state getState() {
        return vx_session_text_state.swigToEnum(VxClientProxyJNI.vx_evt_text_stream_updated_t_state_get(this.swigCPtr, this))
    }

    Int getStatus_code() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_status_code_get(this.swigCPtr, this)
    }

    String getStatus_string() {
        return VxClientProxyJNI.vx_evt_text_stream_updated_t_status_string_get(this.swigCPtr, this)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setEnabled(Int n) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_enabled_set(this.swigCPtr, this, n)
    }

    Unit setIncoming(Int n) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_incoming_set(this.swigCPtr, this, n)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setState(vx_session_text_state vx_session_text_state2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_state_set(this.swigCPtr, this, vx_session_text_state2.swigValue())
    }

    Unit setStatus_code(Int n) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_status_code_set(this.swigCPtr, this, n)
    }

    Unit setStatus_string(String string2) {
        VxClientProxyJNI.vx_evt_text_stream_updated_t_status_string_set(this.swigCPtr, this, string2)
    }
}

