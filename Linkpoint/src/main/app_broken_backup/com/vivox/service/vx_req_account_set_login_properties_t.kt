/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_session_answer_mode

class vx_req_account_set_login_properties_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_account_set_login_properties_t() {
        this(VxClientProxyJNI.new_vx_req_account_set_login_properties_t(), true)
    }

    protected vx_req_account_set_login_properties_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_account_set_login_properties_t vx_req_account_set_login_properties_t2) {
        if (vx_req_account_set_login_properties_t2 != null) return vx_req_account_set_login_properties_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_account_set_login_properties_t_account_handle_get(this.swigCPtr, this)
    }

    vx_session_answer_mode getAnswer_mode() {
        return vx_session_answer_mode.swigToEnum(VxClientProxyJNI.vx_req_account_set_login_properties_t_answer_mode_get(this.swigCPtr, this))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_account_set_login_properties_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    Int getParticipant_property_frequency() {
        return VxClientProxyJNI.vx_req_account_set_login_properties_t_participant_property_frequency_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_set_login_properties_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAnswer_mode(vx_session_answer_mode vx_session_answer_mode2) {
        VxClientProxyJNI.vx_req_account_set_login_properties_t_answer_mode_set(this.swigCPtr, this, vx_session_answer_mode2.swigValue())
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_set_login_properties_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setParticipant_property_frequency(Int n) {
        VxClientProxyJNI.vx_req_account_set_login_properties_t_participant_property_frequency_set(this.swigCPtr, this, n)
    }
}

