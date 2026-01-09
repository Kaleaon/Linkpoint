/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_void
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_recording_frame_type_t

class vx_recording_frame_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_recording_frame_t() {
        this(VxClientProxyJNI.new_vx_recording_frame_t(), true)
    }

    protected vx_recording_frame_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_recording_frame_t vx_recording_frame_t2) {
        if (vx_recording_frame_t2 != null) return vx_recording_frame_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_recording_frame_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_void getFrame_data() {
        var l: Long = VxClientProxyJNI.vx_recording_frame_t_frame_data_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_void(l, false)
        return null
    }

    Int getFrame_size() {
        return VxClientProxyJNI.vx_recording_frame_t_frame_size_get(this.swigCPtr, this)
    }

    vx_recording_frame_type_t getFrame_type() {
        return vx_recording_frame_type_t.swigToEnum(VxClientProxyJNI.vx_recording_frame_t_frame_type_get(this.swigCPtr, this))
    }

    Unit setFrame_data(SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_recording_frame_t_frame_data_set(this.swigCPtr, this, SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Unit setFrame_size(Int n) {
        VxClientProxyJNI.vx_recording_frame_t_frame_size_set(this.swigCPtr, this, n)
    }

    Unit setFrame_type(vx_recording_frame_type_t vx_recording_frame_type_t2) {
        VxClientProxyJNI.vx_recording_frame_t_frame_type_set(this.swigCPtr, this, vx_recording_frame_type_t2.swigValue())
    }
}

