/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_account_authtoken_login_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_account_authtoken_login_t() {
        this(VxClientProxyJNI.new_vx_resp_account_authtoken_login_t(), true);
    }

    protected vx_resp_account_authtoken_login_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_account_authtoken_login_t vx_resp_account_authtoken_login_t2) {
        if (vx_resp_account_authtoken_login_t2 != null) return vx_resp_account_authtoken_login_t2.swigCPtr;
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
        return VxClientProxyJNI.vx_resp_account_authtoken_login_t_account_handle_get(this.swigCPtr, this);
    }

    public int getAccount_id() {
        return VxClientProxyJNI.vx_resp_account_authtoken_login_t_account_id_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_resp_base_t getBase() {
        long l = VxClientProxyJNI.vx_resp_account_authtoken_login_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_resp_account_authtoken_login_t_display_name_get(this.swigCPtr, this);
    }

    public int getNum_aliases() {
        return VxClientProxyJNI.vx_resp_account_authtoken_login_t_num_aliases_get(this.swigCPtr, this);
    }

    public String getUri() {
        return VxClientProxyJNI.vx_resp_account_authtoken_login_t_uri_get(this.swigCPtr, this);
    }

    public String getUser_name() {
        return VxClientProxyJNI.vx_resp_account_authtoken_login_t_user_name_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setAccount_id(int n) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_account_id_set(this.swigCPtr, this, n);
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setNum_aliases(int n) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_num_aliases_set(this.swigCPtr, this, n);
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_uri_set(this.swigCPtr, this, string2);
    }

    public void setUser_name(String string2) {
        VxClientProxyJNI.vx_resp_account_authtoken_login_t_user_name_set(this.swigCPtr, this, string2);
    }
}

