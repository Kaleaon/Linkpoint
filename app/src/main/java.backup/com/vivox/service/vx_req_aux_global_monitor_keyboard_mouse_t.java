/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_int;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_req_base_t;

public class vx_req_aux_global_monitor_keyboard_mouse_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_aux_global_monitor_keyboard_mouse_t() {
        this(VxClientProxyJNI.new_vx_req_aux_global_monitor_keyboard_mouse_t(), true);
    }

    protected vx_req_aux_global_monitor_keyboard_mouse_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_aux_global_monitor_keyboard_mouse_t vx_req_aux_global_monitor_keyboard_mouse_t2) {
        if (vx_req_aux_global_monitor_keyboard_mouse_t2 != null) return vx_req_aux_global_monitor_keyboard_mouse_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getCode_count() {
        return VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_code_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_int getCodes() {
        long l = VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_codes_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_int(l, false);
        return null;
    }

    public String getName() {
        return VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_name_get(this.swigCPtr, this);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setCode_count(int n) {
        VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_code_count_set(this.swigCPtr, this, n);
    }

    public void setCodes(SWIGTYPE_p_int sWIGTYPE_p_int) {
        VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_codes_set(this.swigCPtr, this, SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int));
    }

    public void setName(String string2) {
        VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_name_set(this.swigCPtr, this, string2);
    }
}

