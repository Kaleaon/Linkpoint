/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_rule_type

class vx_req_account_send_subscription_reply_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_account_send_subscription_reply_t() {
        this(VxClientProxyJNI.new_vx_req_account_send_subscription_reply_t(), true)
    }

    protected vx_req_account_send_subscription_reply_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_account_send_subscription_reply_t vx_req_account_send_subscription_reply_t2) {
        if (vx_req_account_send_subscription_reply_t2 != null) return vx_req_account_send_subscription_reply_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_account_send_subscription_reply_t_account_handle_get(this.swigCPtr, this)
    }

    Int getAuto_accept() {
        return VxClientProxyJNI.vx_req_account_send_subscription_reply_t_auto_accept_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_account_send_subscription_reply_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getBuddy_uri() {
        return VxClientProxyJNI.vx_req_account_send_subscription_reply_t_buddy_uri_get(this.swigCPtr, this)
    }

    vx_rule_type getRule_type() {
        return vx_rule_type.swigToEnum(VxClientProxyJNI.vx_req_account_send_subscription_reply_t_rule_type_get(this.swigCPtr, this))
    }

    String getSubscription_handle() {
        return VxClientProxyJNI.vx_req_account_send_subscription_reply_t_subscription_handle_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAuto_accept(Int n) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_t_auto_accept_set(this.swigCPtr, this, n)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_t_buddy_uri_set(this.swigCPtr, this, string2)
    }

    Unit setRule_type(vx_rule_type vx_rule_type2) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_t_rule_type_set(this.swigCPtr, this, vx_rule_type2.swigValue())
    }

    Unit setSubscription_handle(String string2) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_t_subscription_handle_set(this.swigCPtr, this, string2)
    }
}

