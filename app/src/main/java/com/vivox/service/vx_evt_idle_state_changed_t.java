/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_idle_state_changed_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_idle_state_changed_t() {
        this(VxClientProxyJNI.new_vx_evt_idle_state_changed_t(), true);
    }

    protected vx_evt_idle_state_changed_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_idle_state_changed_t vx_evt_idle_state_changed_t2) {
        if (vx_evt_idle_state_changed_t2 != null) return vx_evt_idle_state_changed_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_evt_idle_state_changed_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public int getIs_idle() {
        return VxClientProxyJNI.vx_evt_idle_state_changed_t_is_idle_get(this.swigCPtr, this);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_idle_state_changed_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setIs_idle(int n) {
        VxClientProxyJNI.vx_evt_idle_state_changed_t_is_idle_set(this.swigCPtr, this, n);
    }
}

