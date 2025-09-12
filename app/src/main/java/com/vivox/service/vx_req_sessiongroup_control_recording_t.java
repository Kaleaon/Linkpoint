/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_req_base_t;
import com.vivox.service.vx_sessiongroup_recording_control_type;

public class vx_req_sessiongroup_control_recording_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_sessiongroup_control_recording_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_control_recording_t(), true);
    }

    protected vx_req_sessiongroup_control_recording_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_sessiongroup_control_recording_t vx_req_sessiongroup_control_recording_t2) {
        if (vx_req_sessiongroup_control_recording_t2 != null) return vx_req_sessiongroup_control_recording_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getDelta_frames_per_control_frame() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_get(this.swigCPtr, this);
    }

    public int getEnable_audio_recording_events() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_get(this.swigCPtr, this);
    }

    public String getFilename() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_filename_get(this.swigCPtr, this);
    }

    public int getLoop_mode_duration_seconds() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_get(this.swigCPtr, this);
    }

    public vx_sessiongroup_recording_control_type getRecording_control_type() {
        return vx_sessiongroup_recording_control_type.swigToEnum(VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_recording_control_type_get(this.swigCPtr, this));
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setDelta_frames_per_control_frame(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_set(this.swigCPtr, this, n);
    }

    public void setEnable_audio_recording_events(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_set(this.swigCPtr, this, n);
    }

    public void setFilename(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_filename_set(this.swigCPtr, this, string2);
    }

    public void setLoop_mode_duration_seconds(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_set(this.swigCPtr, this, n);
    }

    public void setRecording_control_type(vx_sessiongroup_recording_control_type vx_sessiongroup_recording_control_type2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_recording_control_type_set(this.swigCPtr, this, vx_sessiongroup_recording_control_type2.swigValue());
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }
}

