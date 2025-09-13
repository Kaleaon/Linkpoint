/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_user_app_data_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_user_app_data_t() {
        this(VxClientProxyJNI.new_vx_evt_user_app_data_t(), true);
    }

    protected vx_evt_user_app_data_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_user_app_data_t vx_evt_user_app_data_t2) {
        if (vx_evt_user_app_data_t2 != null) return vx_evt_user_app_data_t2.swigCPtr;
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
        return VxClientProxyJNI.vx_evt_user_app_data_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_user_app_data_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public String getContent() {
        return VxClientProxyJNI.vx_evt_user_app_data_t_content_get(this.swigCPtr, this);
    }

    public String getContent_type() {
        return VxClientProxyJNI.vx_evt_user_app_data_t_content_type_get(this.swigCPtr, this);
    }

    public String getFrom_uri() {
        return VxClientProxyJNI.vx_evt_user_app_data_t_from_uri_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_user_app_data_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_user_app_data_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setContent(String string2) {
        VxClientProxyJNI.vx_evt_user_app_data_t_content_set(this.swigCPtr, this, string2);
    }

    public void setContent_type(String string2) {
        VxClientProxyJNI.vx_evt_user_app_data_t_content_type_set(this.swigCPtr, this, string2);
    }

    public void setFrom_uri(String string2) {
        VxClientProxyJNI.vx_evt_user_app_data_t_from_uri_set(this.swigCPtr, this, string2);
    }
}

