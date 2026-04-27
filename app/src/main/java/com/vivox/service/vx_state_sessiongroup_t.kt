/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_state_session
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_sessiongroup_playback_mode

class vx_state_sessiongroup_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_sessiongroup_t() {
        this(VxClientProxyJNI.new_vx_state_sessiongroup_t(), true)
    }

    protected vx_state_sessiongroup_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_sessiongroup_t vx_state_sessiongroup_t2) {
        if (vx_state_sessiongroup_t2 != null) return vx_state_sessiongroup_t2.swigCPtr
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

    vx_sessiongroup_playback_mode getCurrent_playback_mode() {
        return vx_sessiongroup_playback_mode.swigToEnum(VxClientProxyJNI.vx_state_sessiongroup_t_current_playback_mode_get(this.swigCPtr, this))
    }

    Double getCurrent_playback_speed() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_current_playback_speed_get(this.swigCPtr, this)
    }

    String getCurrent_recording_filename() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_current_recording_filename_get(this.swigCPtr, this)
    }

    Int getFirst_loop_frame() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_first_loop_frame_get(this.swigCPtr, this)
    }

    Int getIn_delayed_playback() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_in_delayed_playback_get(this.swigCPtr, this)
    }

    Int getLast_loop_frame_played() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_last_loop_frame_played_get(this.swigCPtr, this)
    }

    Int getLoop_buffer_capacity() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_loop_buffer_capacity_get(this.swigCPtr, this)
    }

    Int getPlayback_paused() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_playback_paused_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_session getState_sessions() {
        Long l = VxClientProxyJNI.vx_state_sessiongroup_t_state_sessions_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_session(l, false)
        return null
    }

    Int getState_sessions_count() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_state_sessions_count_get(this.swigCPtr, this)
    }

    Int getTotal_loop_frames_captured() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_total_loop_frames_captured_get(this.swigCPtr, this)
    }

    Int getTotal_recorded_frames() {
        return VxClientProxyJNI.vx_state_sessiongroup_t_total_recorded_frames_get(this.swigCPtr, this)
    }

    Unit setCurrent_playback_mode(vx_sessiongroup_playback_mode vx_sessiongroup_playback_mode2) {
        VxClientProxyJNI.vx_state_sessiongroup_t_current_playback_mode_set(this.swigCPtr, this, vx_sessiongroup_playback_mode2.swigValue())
    }

    Unit setCurrent_playback_speed(Double d) {
        VxClientProxyJNI.vx_state_sessiongroup_t_current_playback_speed_set(this.swigCPtr, this, d)
    }

    Unit setCurrent_recording_filename(String string2) {
        VxClientProxyJNI.vx_state_sessiongroup_t_current_recording_filename_set(this.swigCPtr, this, string2)
    }

    Unit setFirst_loop_frame(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_first_loop_frame_set(this.swigCPtr, this, n)
    }

    Unit setIn_delayed_playback(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_in_delayed_playback_set(this.swigCPtr, this, n)
    }

    Unit setLast_loop_frame_played(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_last_loop_frame_played_set(this.swigCPtr, this, n)
    }

    Unit setLoop_buffer_capacity(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_loop_buffer_capacity_set(this.swigCPtr, this, n)
    }

    Unit setPlayback_paused(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_playback_paused_set(this.swigCPtr, this, n)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_state_sessiongroup_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setState_sessions(SWIGTYPE_p_p_vx_state_session sWIGTYPE_p_p_vx_state_session) {
        VxClientProxyJNI.vx_state_sessiongroup_t_state_sessions_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_session.getCPtr(sWIGTYPE_p_p_vx_state_session))
    }

    Unit setState_sessions_count(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_state_sessions_count_set(this.swigCPtr, this, n)
    }

    Unit setTotal_loop_frames_captured(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_total_loop_frames_captured_set(this.swigCPtr, this, n)
    }

    Unit setTotal_recorded_frames(Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_t_total_recorded_frames_set(this.swigCPtr, this, n)
    }
}

