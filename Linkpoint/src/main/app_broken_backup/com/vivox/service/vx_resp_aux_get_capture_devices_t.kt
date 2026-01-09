/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_device
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_device_t
import com.vivox.service.vx_resp_base_t

class vx_resp_aux_get_capture_devices_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_aux_get_capture_devices_t() {
        this(VxClientProxyJNI.new_vx_resp_aux_get_capture_devices_t(), true)
    }

    protected vx_resp_aux_get_capture_devices_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_aux_get_capture_devices_t vx_resp_aux_get_capture_devices_t2) {
        if (vx_resp_aux_get_capture_devices_t2 != null) return vx_resp_aux_get_capture_devices_t2.swigCPtr
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
    vx_resp_base_t getBase() {
        Long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_device getCapture_devices() {
        Long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_capture_devices_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_device(l, false)
        return null
    }

    Int getCount() {
        return VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_device_t getCurrent_capture_device() {
        Long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_current_capture_device_get(this.swigCPtr, this)
        if (l != 0L) return vx_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_device_t getEffective_capture_device() {
        Long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_effective_capture_device_get(this.swigCPtr, this)
        if (l != 0L) return vx_device_t(l, false)
        return null
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setCapture_devices(SWIGTYPE_p_p_vx_device sWIGTYPE_p_p_vx_device) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_capture_devices_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_device.getCPtr(sWIGTYPE_p_p_vx_device))
    }

    Unit setCount(Int n) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_count_set(this.swigCPtr, this, n)
    }

    Unit setCurrent_capture_device(vx_device_t vx_device_t2) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_current_capture_device_set(this.swigCPtr, this, vx_device_t.getCPtr(vx_device_t2), vx_device_t2)
    }

    Unit setEffective_capture_device(vx_device_t vx_device_t2) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_effective_capture_device_set(this.swigCPtr, this, vx_device_t.getCPtr(vx_device_t2), vx_device_t2)
    }
}

