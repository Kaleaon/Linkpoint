/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_attempt_stun
import com.vivox.service.vx_connector_mode
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_session_handle_type

class vx_req_connector_create_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_connector_create_t() {
        this(VxClientProxyJNI.new_vx_req_connector_create_t(), true)
    }

    protected vx_req_connector_create_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_connector_create_t vx_req_connector_create_t2) {
        if (vx_req_connector_create_t2 != null) return vx_req_connector_create_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_connector_create_t_acct_mgmt_server_get(this.swigCPtr, this)
    }

    Int getAllow_cross_domain_logins() {
        return VxClientProxyJNI.vx_req_connector_create_t_allow_cross_domain_logins_get(this.swigCPtr, this)
    }

    String getApplication() {
        return VxClientProxyJNI.vx_req_connector_create_t_application_get(this.swigCPtr, this)
    }

    vx_attempt_stun getAttempt_stun() {
        return vx_attempt_stun.swigToEnum(VxClientProxyJNI.vx_req_connector_create_t_attempt_stun_get(this.swigCPtr, this))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_connector_create_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getClient_name() {
        return VxClientProxyJNI.vx_req_connector_create_t_client_name_get(this.swigCPtr, this)
    }

    Int getDefault_codec() {
        return VxClientProxyJNI.vx_req_connector_create_t_default_codec_get(this.swigCPtr, this)
    }

    String getHttp_proxy_server_name() {
        return VxClientProxyJNI.vx_req_connector_create_t_http_proxy_server_name_get(this.swigCPtr, this)
    }

    Int getHttp_proxy_server_port() {
        return VxClientProxyJNI.vx_req_connector_create_t_http_proxy_server_port_get(this.swigCPtr, this)
    }

    String getLog_filename_prefix() {
        return VxClientProxyJNI.vx_req_connector_create_t_log_filename_prefix_get(this.swigCPtr, this)
    }

    String getLog_filename_suffix() {
        return VxClientProxyJNI.vx_req_connector_create_t_log_filename_suffix_get(this.swigCPtr, this)
    }

    String getLog_folder() {
        return VxClientProxyJNI.vx_req_connector_create_t_log_folder_get(this.swigCPtr, this)
    }

    Int getLog_level() {
        return VxClientProxyJNI.vx_req_connector_create_t_log_level_get(this.swigCPtr, this)
    }

    Int getMax_calls() {
        return VxClientProxyJNI.vx_req_connector_create_t_max_calls_get(this.swigCPtr, this)
    }

    Int getMaximum_port() {
        return VxClientProxyJNI.vx_req_connector_create_t_maximum_port_get(this.swigCPtr, this)
    }

    String getMedia_probe_server() {
        return VxClientProxyJNI.vx_req_connector_create_t_media_probe_server_get(this.swigCPtr, this)
    }

    Int getMinimum_port() {
        return VxClientProxyJNI.vx_req_connector_create_t_minimum_port_get(this.swigCPtr, this)
    }

    vx_connector_mode getMode() {
        return vx_connector_mode.swigToEnum(VxClientProxyJNI.vx_req_connector_create_t_mode_get(this.swigCPtr, this))
    }

    vx_session_handle_type getSession_handle_type() {
        return vx_session_handle_type.swigToEnum(VxClientProxyJNI.vx_req_connector_create_t_session_handle_type_get(this.swigCPtr, this))
    }

    String getUser_agent_id() {
        return VxClientProxyJNI.vx_req_connector_create_t_user_agent_id_get(this.swigCPtr, this)
    }

    Unit setAcct_mgmt_server(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_acct_mgmt_server_set(this.swigCPtr, this, string2)
    }

    Unit setAllow_cross_domain_logins(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_allow_cross_domain_logins_set(this.swigCPtr, this, n)
    }

    Unit setApplication(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_application_set(this.swigCPtr, this, string2)
    }

    Unit setAttempt_stun(vx_attempt_stun vx_attempt_stun2) {
        VxClientProxyJNI.vx_req_connector_create_t_attempt_stun_set(this.swigCPtr, this, vx_attempt_stun2.swigValue())
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_connector_create_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setClient_name(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_client_name_set(this.swigCPtr, this, string2)
    }

    Unit setDefault_codec(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_default_codec_set(this.swigCPtr, this, n)
    }

    Unit setHttp_proxy_server_name(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_http_proxy_server_name_set(this.swigCPtr, this, string2)
    }

    Unit setHttp_proxy_server_port(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_http_proxy_server_port_set(this.swigCPtr, this, n)
    }

    Unit setLog_filename_prefix(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_log_filename_prefix_set(this.swigCPtr, this, string2)
    }

    Unit setLog_filename_suffix(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_log_filename_suffix_set(this.swigCPtr, this, string2)
    }

    Unit setLog_folder(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_log_folder_set(this.swigCPtr, this, string2)
    }

    Unit setLog_level(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_log_level_set(this.swigCPtr, this, n)
    }

    Unit setMax_calls(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_max_calls_set(this.swigCPtr, this, n)
    }

    Unit setMaximum_port(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_maximum_port_set(this.swigCPtr, this, n)
    }

    Unit setMedia_probe_server(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_media_probe_server_set(this.swigCPtr, this, string2)
    }

    Unit setMinimum_port(Int n) {
        VxClientProxyJNI.vx_req_connector_create_t_minimum_port_set(this.swigCPtr, this, n)
    }

    Unit setMode(vx_connector_mode vx_connector_mode2) {
        VxClientProxyJNI.vx_req_connector_create_t_mode_set(this.swigCPtr, this, vx_connector_mode2.swigValue())
    }

    Unit setSession_handle_type(vx_session_handle_type vx_session_handle_type2) {
        VxClientProxyJNI.vx_req_connector_create_t_session_handle_type_set(this.swigCPtr, this, vx_session_handle_type2.swigValue())
    }

    Unit setUser_agent_id(String string2) {
        VxClientProxyJNI.vx_req_connector_create_t_user_agent_id_set(this.swigCPtr, this, string2)
    }
}

