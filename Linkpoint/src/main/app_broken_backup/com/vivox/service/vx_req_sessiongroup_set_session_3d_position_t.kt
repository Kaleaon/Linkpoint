/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_double
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t

class vx_req_sessiongroup_set_session_3d_position_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_sessiongroup_set_session_3d_position_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_set_session_3d_position_t(), true)
    }

    protected vx_req_sessiongroup_set_session_3d_position_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_sessiongroup_set_session_3d_position_t vx_req_sessiongroup_set_session_3d_position_t2) {
        if (vx_req_sessiongroup_set_session_3d_position_t2 != null) return vx_req_sessiongroup_set_session_3d_position_t2.swigCPtr
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
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_session_handle_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_at_orientation() {
        var l: Long = VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_position() {
        var l: Long = VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_position_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSpeaker_at_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setSpeaker_position(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_speaker_position_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }
}

