/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_sessiongroup_audio_injection_control_type

class vx_req_sessiongroup_control_audio_injection_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_sessiongroup_control_audio_injection_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_control_audio_injection_t(), true)
    }

    protected vx_req_sessiongroup_control_audio_injection_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_sessiongroup_control_audio_injection_t vx_req_sessiongroup_control_audio_injection_t2) {
        if (vx_req_sessiongroup_control_audio_injection_t2 != null) return vx_req_sessiongroup_control_audio_injection_t2.swigCPtr
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

    vx_sessiongroup_audio_injection_control_type getAudio_injection_control_type() {
        return vx_sessiongroup_audio_injection_control_type.swigToEnum(VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_audio_injection_control_type_get(this.swigCPtr, this))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getFilename() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_filename_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    Unit setAudio_injection_control_type(vx_sessiongroup_audio_injection_control_type vx_sessiongroup_audio_injection_control_type2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_audio_injection_control_type_set(this.swigCPtr, this, vx_sessiongroup_audio_injection_control_type2.swigValue())
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setFilename(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_filename_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }
}

