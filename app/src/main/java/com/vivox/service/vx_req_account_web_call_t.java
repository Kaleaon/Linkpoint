/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_name_value_pair;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_req_base_t;

public class vx_req_account_web_call_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_account_web_call_t() {
        this(VxClientProxyJNI.new_vx_req_account_web_call_t(), true);
    }

    protected vx_req_account_web_call_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_account_web_call_t vx_req_account_web_call_t2) {
        if (vx_req_account_web_call_t2 != null) return vx_req_account_web_call_t2.swigCPtr;
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

    public String getAccount_handle() {
        return VxClientProxyJNI.vx_req_account_web_call_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_account_web_call_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getParameter_count() {
        return VxClientProxyJNI.vx_req_account_web_call_t_parameter_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_name_value_pair getParameters() {
        long l = VxClientProxyJNI.vx_req_account_web_call_t_parameters_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_name_value_pair(l, false);
        return null;
    }

    public String getRelative_path() {
        return VxClientProxyJNI.vx_req_account_web_call_t_relative_path_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_web_call_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_web_call_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setParameter_count(int n) {
        VxClientProxyJNI.vx_req_account_web_call_t_parameter_count_set(this.swigCPtr, this, n);
    }

    public void setParameters(SWIGTYPE_p_p_vx_name_value_pair sWIGTYPE_p_p_vx_name_value_pair) {
        VxClientProxyJNI.vx_req_account_web_call_t_parameters_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_name_value_pair.getCPtr(sWIGTYPE_p_p_vx_name_value_pair));
    }

    public void setRelative_path(String string2) {
        VxClientProxyJNI.vx_req_account_web_call_t_relative_path_set(this.swigCPtr, this, string2);
    }
}

