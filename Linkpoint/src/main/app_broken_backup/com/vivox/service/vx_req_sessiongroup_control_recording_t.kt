/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_sessiongroup_recording_control_type

class vx_req_sessiongroup_control_recording_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_sessiongroup_control_recording_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_control_recording_t(), true)
    }

    protected vx_req_sessiongroup_control_recording_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_sessiongroup_control_recording_t vx_req_sessiongroup_control_recording_t2) {
        if (vx_req_sessiongroup_control_recording_t2 != null) return vx_req_sessiongroup_control_recording_t2.swigCPtr
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
        var l: Long = VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    Int getDelta_frames_per_control_frame() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_get(this.swigCPtr, this)
    }

    Int getEnable_audio_recording_events() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_get(this.swigCPtr, this)
    }

    String getFilename() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_filename_get(this.swigCPtr, this)
    }

    Int getLoop_mode_duration_seconds() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_get(this.swigCPtr, this)
    }

    vx_sessiongroup_recording_control_type getRecording_control_type() {
        return vx_sessiongroup_recording_control_type.swigToEnum(VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_recording_control_type_get(this.swigCPtr, this))
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setDelta_frames_per_control_frame(Int n) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_set(this.swigCPtr, this, n)
    }

    Unit setEnable_audio_recording_events(Int n) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_set(this.swigCPtr, this, n)
    }

    Unit setFilename(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_filename_set(this.swigCPtr, this, string2)
    }

    Unit setLoop_mode_duration_seconds(Int n) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_set(this.swigCPtr, this, n)
    }

    Unit setRecording_control_type(vx_sessiongroup_recording_control_type vx_sessiongroup_recording_control_type2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_recording_control_type_set(this.swigCPtr, this, vx_sessiongroup_recording_control_type2.swigValue())
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }
}

