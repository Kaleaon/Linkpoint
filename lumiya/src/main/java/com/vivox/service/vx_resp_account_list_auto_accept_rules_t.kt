/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_auto_accept_rule
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_account_list_auto_accept_rules_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_account_list_auto_accept_rules_t() {
        this(VxClientProxyJNI.new_vx_resp_account_list_auto_accept_rules_t(), true)
    }

    protected vx_resp_account_list_auto_accept_rules_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_account_list_auto_accept_rules_t vx_resp_account_list_auto_accept_rules_t2) {
        if (vx_resp_account_list_auto_accept_rules_t2 != null) return vx_resp_account_list_auto_accept_rules_t2.swigCPtr
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_auto_accept_rule getAuto_accept_rules() {
        Long l = VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_auto_accept_rules_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_auto_accept_rule(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_base_t getBase() {
        Long l = VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    Int getRule_count() {
        return VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_rule_count_get(this.swigCPtr, this)
    }

    Unit setAuto_accept_rules(SWIGTYPE_p_p_vx_auto_accept_rule sWIGTYPE_p_p_vx_auto_accept_rule) {
        VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_auto_accept_rules_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_auto_accept_rule.getCPtr(sWIGTYPE_p_p_vx_auto_accept_rule))
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setRule_count(Int n) {
        VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_rule_count_set(this.swigCPtr, this, n)
    }
}

