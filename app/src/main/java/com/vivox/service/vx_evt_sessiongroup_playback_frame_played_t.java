/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_sessiongroup_playback_frame_played_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_sessiongroup_playback_frame_played_t() {
        this(VxClientProxyJNI.new_vx_evt_sessiongroup_playback_frame_played_t(), true);
    }

    protected vx_evt_sessiongroup_playback_frame_played_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_sessiongroup_playback_frame_played_t vx_evt_sessiongroup_playback_frame_played_t2) {
        if (vx_evt_sessiongroup_playback_frame_played_t2 != null) return vx_evt_sessiongroup_playback_frame_played_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public int getCurrent_frame() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_current_frame_get(this.swigCPtr, this);
    }

    public int getFirst_frame() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_first_frame_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public int getTotal_frames() {
        return VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_total_frames_get(this.swigCPtr, this);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setCurrent_frame(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_current_frame_set(this.swigCPtr, this, n);
    }

    public void setFirst_frame(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_first_frame_set(this.swigCPtr, this, n);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setTotal_frames(int n) {
        VxClientProxyJNI.vx_evt_sessiongroup_playback_frame_played_t_total_frames_set(this.swigCPtr, this, n);
    }
}

