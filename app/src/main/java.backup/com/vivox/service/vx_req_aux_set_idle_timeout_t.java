/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_req_base_t;

public class vx_req_aux_set_idle_timeout_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_aux_set_idle_timeout_t() {
        this(VxClientProxyJNI.new_vx_req_aux_set_idle_timeout_t(), true);
    }

    protected vx_req_aux_set_idle_timeout_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_aux_set_idle_timeout_t vx_req_aux_set_idle_timeout_t2) {
        if (vx_req_aux_set_idle_timeout_t2 != null) return vx_req_aux_set_idle_timeout_t2.swigCPtr;
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
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_aux_set_idle_timeout_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getSeconds() {
        return VxClientProxyJNI.vx_req_aux_set_idle_timeout_t_seconds_get(this.swigCPtr, this);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_aux_set_idle_timeout_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setSeconds(int n) {
        VxClientProxyJNI.vx_req_aux_set_idle_timeout_t_seconds_set(this.swigCPtr, this, n);
    }
}

