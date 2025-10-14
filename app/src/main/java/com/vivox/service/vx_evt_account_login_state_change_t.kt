/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_void
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_login_state_change_state

class vx_evt_account_login_state_change_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_account_login_state_change_t() {
        this(VxClientProxyJNI.new_vx_evt_account_login_state_change_t(), true)
    }

    protected vx_evt_account_login_state_change_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_account_login_state_change_t vx_evt_account_login_state_change_t2) {
        if (vx_evt_account_login_state_change_t2 != null) return vx_evt_account_login_state_change_t2.swigCPtr
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
        return VxClientProxyJNI.vx_evt_account_login_state_change_t_account_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        Long l = VxClientProxyJNI.vx_evt_account_login_state_change_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    String getCookie() {
        return VxClientProxyJNI.vx_evt_account_login_state_change_t_cookie_get(this.swigCPtr, this)
    }

    vx_login_state_change_state getState() {
        return vx_login_state_change_state.swigToEnum(VxClientProxyJNI.vx_evt_account_login_state_change_t_state_get(this.swigCPtr, this))
    }

    Int getStatus_code() {
        return VxClientProxyJNI.vx_evt_account_login_state_change_t_status_code_get(this.swigCPtr, this)
    }

    String getStatus_string() {
        return VxClientProxyJNI.vx_evt_account_login_state_change_t_status_string_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_void getVcookie() {
        Long l = VxClientProxyJNI.vx_evt_account_login_state_change_t_vcookie_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_void(l, false)
        return null
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setCookie(String string2) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_cookie_set(this.swigCPtr, this, string2)
    }

    Unit setState(vx_login_state_change_state vx_login_state_change_state2) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_state_set(this.swigCPtr, this, vx_login_state_change_state2.swigValue())
    }

    Unit setStatus_code(Int n) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_status_code_set(this.swigCPtr, this, n)
    }

    Unit setStatus_string(String string2) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_status_string_set(this.swigCPtr, this, string2)
    }

    Unit setVcookie(SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_evt_account_login_state_change_t_vcookie_set(this.swigCPtr, this, SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }
}

