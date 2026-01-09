/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_buddy_management_mode
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_session_answer_mode
import com.vivox.service.vx_text_mode

class vx_req_account_authtoken_login_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_account_authtoken_login_t() {
        this(VxClientProxyJNI.new_vx_req_account_authtoken_login_t(), true)
    }

    protected vx_req_account_authtoken_login_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_account_authtoken_login_t vx_req_account_authtoken_login_t2) {
        if (vx_req_account_authtoken_login_t2 != null) return vx_req_account_authtoken_login_t2.swigCPtr
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

    String getAcct_mgmt_server() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_acct_mgmt_server_get(this.swigCPtr, this)
    }

    vx_session_answer_mode getAnswer_mode() {
        return vx_session_answer_mode.swigToEnum(VxClientProxyJNI.vx_req_account_authtoken_login_t_answer_mode_get(this.swigCPtr, this))
    }

    String getApplication_override() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_application_override_get(this.swigCPtr, this)
    }

    String getApplication_token() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_application_token_get(this.swigCPtr, this)
    }

    String getAuthtoken() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_authtoken_get(this.swigCPtr, this)
    }

    Int getAutopost_crash_dumps() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_autopost_crash_dumps_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_account_authtoken_login_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    vx_buddy_management_mode getBuddy_management_mode() {
        return vx_buddy_management_mode.swigToEnum(VxClientProxyJNI.vx_req_account_authtoken_login_t_buddy_management_mode_get(this.swigCPtr, this))
    }

    String getConnector_handle() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_connector_handle_get(this.swigCPtr, this)
    }

    Int getEnable_buddies_and_presence() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_enable_buddies_and_presence_get(this.swigCPtr, this)
    }

    vx_text_mode getEnable_text() {
        return vx_text_mode.swigToEnum(VxClientProxyJNI.vx_req_account_authtoken_login_t_enable_text_get(this.swigCPtr, this))
    }

    Int getParticipant_property_frequency() {
        return VxClientProxyJNI.vx_req_account_authtoken_login_t_participant_property_frequency_get(this.swigCPtr, this)
    }

    Unit setAcct_mgmt_server(String string2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_acct_mgmt_server_set(this.swigCPtr, this, string2)
    }

    Unit setAnswer_mode(vx_session_answer_mode vx_session_answer_mode2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_answer_mode_set(this.swigCPtr, this, vx_session_answer_mode2.swigValue())
    }

    Unit setApplication_override(String string2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_application_override_set(this.swigCPtr, this, string2)
    }

    Unit setApplication_token(String string2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_application_token_set(this.swigCPtr, this, string2)
    }

    Unit setAuthtoken(String string2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_authtoken_set(this.swigCPtr, this, string2)
    }

    Unit setAutopost_crash_dumps(Int n) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_autopost_crash_dumps_set(this.swigCPtr, this, n)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setBuddy_management_mode(vx_buddy_management_mode vx_buddy_management_mode2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_buddy_management_mode_set(this.swigCPtr, this, vx_buddy_management_mode2.swigValue())
    }

    Unit setConnector_handle(String string2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_connector_handle_set(this.swigCPtr, this, string2)
    }

    Unit setEnable_buddies_and_presence(Int n) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_enable_buddies_and_presence_set(this.swigCPtr, this, n)
    }

    Unit setEnable_text(vx_text_mode vx_text_mode2) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_enable_text_set(this.swigCPtr, this, vx_text_mode2.swigValue())
    }

    Unit setParticipant_property_frequency(Int n) {
        VxClientProxyJNI.vx_req_account_authtoken_login_t_participant_property_frequency_set(this.swigCPtr, this, n)
    }
}

