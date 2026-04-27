/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_device;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_device_t;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_aux_get_capture_devices_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_aux_get_capture_devices_t() {
        this(VxClientProxyJNI.new_vx_resp_aux_get_capture_devices_t(), true);
    }

    protected vx_resp_aux_get_capture_devices_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_aux_get_capture_devices_t vx_resp_aux_get_capture_devices_t2) {
        if (vx_resp_aux_get_capture_devices_t2 != null) return vx_resp_aux_get_capture_devices_t2.swigCPtr;
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
    public vx_resp_base_t getBase() {
        long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_device getCapture_devices() {
        long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_capture_devices_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_device(l, false);
        return null;
    }

    public int getCount() {
        return VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_device_t getCurrent_capture_device() {
        long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_current_capture_device_get(this.swigCPtr, this);
        if (l != 0L) return new vx_device_t(l, false);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_device_t getEffective_capture_device() {
        long l = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_effective_capture_device_get(this.swigCPtr, this);
        if (l != 0L) return new vx_device_t(l, false);
        return null;
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setCapture_devices(SWIGTYPE_p_p_vx_device sWIGTYPE_p_p_vx_device) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_capture_devices_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_device.getCPtr(sWIGTYPE_p_p_vx_device));
    }

    public void setCount(int n) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_count_set(this.swigCPtr, this, n);
    }

    public void setCurrent_capture_device(vx_device_t vx_device_t2) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_current_capture_device_set(this.swigCPtr, this, vx_device_t.getCPtr(vx_device_t2), vx_device_t2);
    }

    public void setEffective_capture_device(vx_device_t vx_device_t2) {
        VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_effective_capture_device_set(this.swigCPtr, this, vx_device_t.getCPtr(vx_device_t2), vx_device_t2);
    }
}

