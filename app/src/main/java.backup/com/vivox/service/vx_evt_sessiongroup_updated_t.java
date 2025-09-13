/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_sessiongroup_playback_mode;

public class vx_evt_sessiongroup_updated_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_sessiongroup_updated_t() {
        this(VxClientProxyJNI.new_vx_evt_sessiongroup_updated_t(), true);
    }

    protected vx_evt_sessiongroup_updated_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_sessiongroup_updated_t vx_evt_sessiongroup_updated_t2) {
        if (vx_evt_sessiongroup_updated_t2 != null) return vx_evt_sessiongroup_updated_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_evt_sessiongroup_updated_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public vx_sessiongroup_playback_mode getCurrent_playback_mode() {
        return vx_sessiongroup_playback_mode.swigToEnum(VxClientProxyJNI.vx_evt_sessiongroup_updated_t_current_playback_mode_get(this.swigCPtr, this));
    }

    public double getCurrent_playback_speed() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_current_playback_speed_get(this.swigCPtr, this);
    }

    public String getCurrent_recording_filename() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_current_recording_filename_get(this.swigCPtr, this);
    }

    public long getFirst_frame_timestamp_us() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_get(this.swigCPtr, this);
    }

    public int getFirst_loop_frame() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_first_loop_frame_get(this.swigCPtr, this);
    }

    public int getIn_delayed_playback() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_in_delayed_playback_get(this.swigCPtr, this);
    }

    public int getLast_loop_frame_played() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_last_loop_frame_played_get(this.swigCPtr, this);
    }

    public int getLoop_buffer_capacity() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_loop_buffer_capacity_get(this.swigCPtr, this);
    }

    public int getPlayback_paused() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_playback_paused_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public int getTotal_loop_frames_captured() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_total_loop_frames_captured_get(this.swigCPtr, this);
    }

    public int getTotal_recorded_frames() {
        return VxClientProxyJNI.vx_evt_sessiongroup_updated_t_total_recorded_frames_get(this.swigCPtr, this);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setCurrent_playback_mode(vx_sessiongroup_playback_mode vx_sessiongroup_playback_mode2) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_current_playback_mode_set(this.swigCPtr, this, vx_sessiongroup_playback_mode2.swigValue());
    }

    public void setCurrent_playback_speed(double d) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_current_playback_speed_set(this.swigCPtr, this, d);
    }

    public void setCurrent_recording_filename(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_current_recording_filename_set(this.swigCPtr, this, string2);
    }

    public void setFirst_frame_timestamp_us(long l) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_set(this.swigCPtr, this, l);
    }

    public void setFirst_loop_frame(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_first_loop_frame_set(this.swigCPtr, this, n);
    }

    public void setIn_delayed_playback(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_in_delayed_playback_set(this.swigCPtr, this, n);
    }

    public void setLast_loop_frame_played(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_last_loop_frame_played_set(this.swigCPtr, this, n);
    }

    public void setLoop_buffer_capacity(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_loop_buffer_capacity_set(this.swigCPtr, this, n);
    }

    public void setPlayback_paused(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_playback_paused_set(this.swigCPtr, this, n);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setTotal_loop_frames_captured(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_total_loop_frames_captured_set(this.swigCPtr, this, n);
    }

    public void setTotal_recorded_frames(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_updated_t_total_recorded_frames_set(this.swigCPtr, this, n);
    }
}

