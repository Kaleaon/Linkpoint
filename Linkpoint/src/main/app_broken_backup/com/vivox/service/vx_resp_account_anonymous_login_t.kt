/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_account_anonymous_login_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_account_anonymous_login_t() {
        this(VxClientProxyJNI.new_vx_resp_account_anonymous_login_t(), true)
    }

    protected vx_resp_account_anonymous_login_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_account_anonymous_login_t vx_resp_account_anonymous_login_t2) {
        if (vx_resp_account_anonymous_login_t2 != null) return vx_resp_account_anonymous_login_t2.swigCPtr
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

    String getAccount_handle() {
        return VxClientProxyJNI.vx_resp_account_anonymous_login_t_account_handle_get(this.swigCPtr, this)
    }

    Int getAccount_id() {
        return VxClientProxyJNI.vx_resp_account_anonymous_login_t_account_id_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_resp_account_anonymous_login_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    String getDisplayname() {
        return VxClientProxyJNI.vx_resp_account_anonymous_login_t_displayname_get(this.swigCPtr, this)
    }

    String getUri() {
        return VxClientProxyJNI.vx_resp_account_anonymous_login_t_uri_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_resp_account_anonymous_login_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAccount_id(Int n) {
        VxClientProxyJNI.vx_resp_account_anonymous_login_t_account_id_set(this.swigCPtr, this, n)
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_anonymous_login_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setDisplayname(String string2) {
        VxClientProxyJNI.vx_resp_account_anonymous_login_t_displayname_set(this.swigCPtr, this, string2)
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_resp_account_anonymous_login_t_uri_set(this.swigCPtr, this, string2)
    }
}

