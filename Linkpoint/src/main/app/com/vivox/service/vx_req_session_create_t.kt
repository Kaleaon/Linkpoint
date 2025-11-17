/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_password_hash_algorithm_t
import com.vivox.service.vx_req_base_t

class vx_req_session_create_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_session_create_t() {
        this(VxClientProxyJNI.new_vx_req_session_create_t(), true)
    }

    protected vx_req_session_create_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_session_create_t vx_req_session_create_t2) {
        if (vx_req_session_create_t2 != null) return vx_req_session_create_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_session_create_t_account_handle_get(this.swigCPtr, this)
    }

    String getAlias_username() {
        return VxClientProxyJNI.vx_req_session_create_t_alias_username_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_session_create_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    Int getConnect_audio() {
        return VxClientProxyJNI.vx_req_session_create_t_connect_audio_get(this.swigCPtr, this)
    }

    Int getConnect_text() {
        return VxClientProxyJNI.vx_req_session_create_t_connect_text_get(this.swigCPtr, this)
    }

    Int getJitter_compensation() {
        return VxClientProxyJNI.vx_req_session_create_t_jitter_compensation_get(this.swigCPtr, this)
    }

    Int getJoin_audio() {
        return VxClientProxyJNI.vx_req_session_create_t_join_audio_get(this.swigCPtr, this)
    }

    Int getJoin_text() {
        return VxClientProxyJNI.vx_req_session_create_t_join_text_get(this.swigCPtr, this)
    }

    String getName() {
        return VxClientProxyJNI.vx_req_session_create_t_name_get(this.swigCPtr, this)
    }

    String getPassword() {
        return VxClientProxyJNI.vx_req_session_create_t_password_get(this.swigCPtr, this)
    }

    vx_password_hash_algorithm_t getPassword_hash_algorithm() {
        return vx_password_hash_algorithm_t.swigToEnum(VxClientProxyJNI.vx_req_session_create_t_password_hash_algorithm_get(this.swigCPtr, this))
    }

    Int getSession_font_id() {
        return VxClientProxyJNI.vx_req_session_create_t_session_font_id_get(this.swigCPtr, this)
    }

    String getUri() {
        return VxClientProxyJNI.vx_req_session_create_t_uri_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_session_create_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAlias_username(String string2) {
        VxClientProxyJNI.vx_req_session_create_t_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_session_create_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setConnect_audio(Int n) {
        VxClientProxyJNI.vx_req_session_create_t_connect_audio_set(this.swigCPtr, this, n)
    }

    Unit setConnect_text(Int n) {
        VxClientProxyJNI.vx_req_session_create_t_connect_text_set(this.swigCPtr, this, n)
    }

    Unit setJitter_compensation(Int n) {
        VxClientProxyJNI.vx_req_session_create_t_jitter_compensation_set(this.swigCPtr, this, n)
    }

    Unit setJoin_audio(Int n) {
        VxClientProxyJNI.vx_req_session_create_t_join_audio_set(this.swigCPtr, this, n)
    }

    Unit setJoin_text(Int n) {
        VxClientProxyJNI.vx_req_session_create_t_join_text_set(this.swigCPtr, this, n)
    }

    Unit setName(String string2) {
        VxClientProxyJNI.vx_req_session_create_t_name_set(this.swigCPtr, this, string2)
    }

    Unit setPassword(String string2) {
        VxClientProxyJNI.vx_req_session_create_t_password_set(this.swigCPtr, this, string2)
    }

    Unit setPassword_hash_algorithm(vx_password_hash_algorithm_t vx_password_hash_algorithm_t2) {
        VxClientProxyJNI.vx_req_session_create_t_password_hash_algorithm_set(this.swigCPtr, this, vx_password_hash_algorithm_t2.swigValue())
    }

    Unit setSession_font_id(Int n) {
        VxClientProxyJNI.vx_req_session_create_t_session_font_id_set(this.swigCPtr, this, n)
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_req_session_create_t_uri_set(this.swigCPtr, this, string2)
    }
}

