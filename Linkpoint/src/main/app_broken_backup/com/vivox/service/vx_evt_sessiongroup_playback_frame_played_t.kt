/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t

class vx_evt_sessiongroup_playback_frame_played_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_sessiongroup_playback_frame_played_t() {
        this(VxClientProxyJNI.new_vx_evt_sessiongroup_playback_frame_played_t(), true)
    }

    protected vx_evt_sessiongroup_playback_frame_played_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_sessiongroup_playback_frame_played_t vx_evt_sessiongroup_playback_frame_played_t2) {
        if (vx_evt_sessiongroup_playback_frame_played_t2 != null) return vx_evt_sessiongroup_playback_frame_played_t2.swigCPtr
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
        var l: Long = VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    Int getCurrent_frame() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_current_frame_get(this.swigCPtr, this)
    }

    Int getFirst_frame() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_first_frame_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    Int getTotal_frames() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_total_frames_get(this.swigCPtr, this)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setCurrent_frame(Int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_current_frame_set(this.swigCPtr, this, n)
    }

    Unit setFirst_frame(Int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_first_frame_set(this.swigCPtr, this, n)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setTotal_frames(Int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_total_frames_set(this.swigCPtr, this, n)
    }
}

