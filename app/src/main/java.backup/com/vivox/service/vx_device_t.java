/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_device_type_t;

public class vx_device_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_device_t() {
        this(VxClientProxyJNI.new_vx_device_t(), true);
    }

    protected vx_device_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_device_t vx_device_t2) {
        if (vx_device_t2 != null) return vx_device_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_device_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getDevice() {
        return VxClientProxyJNI.vx_device_t_device_get(this.swigCPtr, this);
    }

    public vx_device_type_t getDevice_type() {
        return vx_device_type_t.swigToEnum(VxClientProxyJNI.vx_device_t_device_type_get(this.swigCPtr, this));
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_device_t_display_name_get(this.swigCPtr, this);
    }

    public void setDevice(String string2) {
        VxClientProxyJNI.vx_device_t_device_set(this.swigCPtr, this, string2);
    }

    public void setDevice_type(vx_device_type_t vx_device_type_t2) {
        VxClientProxyJNI.vx_device_t_device_type_set(this.swigCPtr, this, vx_device_type_t2.swigValue());
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_device_t_display_name_set(this.swigCPtr, this, string2);
    }
}

