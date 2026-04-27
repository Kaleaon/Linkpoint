/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_void;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_recording_frame_type_t;

public class vx_recording_frame_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_recording_frame_t() {
        this(VxClientProxyJNI.new_vx_recording_frame_t(), true);
    }

    protected vx_recording_frame_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_recording_frame_t vx_recording_frame_t2) {
        if (vx_recording_frame_t2 != null) return vx_recording_frame_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_recording_frame_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_void getFrame_data() {
        long l = VxClientProxyJNI.vx_recording_frame_t_frame_data_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_void(l, false);
        return null;
    }

    public int getFrame_size() {
        return VxClientProxyJNI.vx_recording_frame_t_frame_size_get(this.swigCPtr, this);
    }

    public vx_recording_frame_type_t getFrame_type() {
        return vx_recording_frame_type_t.swigToEnum(VxClientProxyJNI.vx_recording_frame_t_frame_type_get(this.swigCPtr, this));
    }

    public void setFrame_data(SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_recording_frame_t_frame_data_set(this.swigCPtr, this, SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void));
    }

    public void setFrame_size(int n) {
        VxClientProxyJNI.vx_recording_frame_t_frame_size_set(this.swigCPtr, this, n);
    }

    public void setFrame_type(vx_recording_frame_type_t vx_recording_frame_type_t2) {
        VxClientProxyJNI.vx_recording_frame_t_frame_type_set(this.swigCPtr, this, vx_recording_frame_type_t2.swigValue());
    }
}

