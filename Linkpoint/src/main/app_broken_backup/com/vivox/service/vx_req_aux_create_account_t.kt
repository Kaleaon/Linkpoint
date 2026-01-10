/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_generic_credentials
import com.vivox.service.vx_req_base_t

class vx_req_aux_create_account_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_aux_create_account_t() {
        this(VxClientProxyJNI.new_vx_req_aux_create_account_t(), true)
    }

    protected vx_req_aux_create_account_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_aux_create_account_t vx_req_aux_create_account_t2) {
        if (vx_req_aux_create_account_t2 != null) return vx_req_aux_create_account_t2.swigCPtr
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

    String getAge() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_age_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_aux_create_account_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_generic_credentials getCredentials() {
        var l: Long = VxClientProxyJNI.vx_req_aux_create_account_t_credentials_get(this.swigCPtr, this)
        if (l != 0L) return vx_generic_credentials(l, false)
        return null
    }

    String getDisplayname() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_displayname_get(this.swigCPtr, this)
    }

    String getEmail() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_email_get(this.swigCPtr, this)
    }

    String getExt_id() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_ext_id_get(this.swigCPtr, this)
    }

    String getExt_profile() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_ext_profile_get(this.swigCPtr, this)
    }

    String getFirstname() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_firstname_get(this.swigCPtr, this)
    }

    String getGender() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_gender_get(this.swigCPtr, this)
    }

    String getLang() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_lang_get(this.swigCPtr, this)
    }

    String getLastname() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_lastname_get(this.swigCPtr, this)
    }

    String getNumber() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_number_get(this.swigCPtr, this)
    }

    String getPassword() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_password_get(this.swigCPtr, this)
    }

    String getPhone() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_phone_get(this.swigCPtr, this)
    }

    String getTimezone() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_timezone_get(this.swigCPtr, this)
    }

    String getUser_name() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_user_name_get(this.swigCPtr, this)
    }

    Unit setAge(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_age_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setCredentials(vx_generic_credentials vx_generic_credentials2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_credentials_set(this.swigCPtr, this, vx_generic_credentials.getCPtr(vx_generic_credentials2), vx_generic_credentials2)
    }

    Unit setDisplayname(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_displayname_set(this.swigCPtr, this, string2)
    }

    Unit setEmail(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_email_set(this.swigCPtr, this, string2)
    }

    Unit setExt_id(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_ext_id_set(this.swigCPtr, this, string2)
    }

    Unit setExt_profile(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_ext_profile_set(this.swigCPtr, this, string2)
    }

    Unit setFirstname(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_firstname_set(this.swigCPtr, this, string2)
    }

    Unit setGender(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_gender_set(this.swigCPtr, this, string2)
    }

    Unit setLang(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_lang_set(this.swigCPtr, this, string2)
    }

    Unit setLastname(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_lastname_set(this.swigCPtr, this, string2)
    }

    Unit setNumber(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_number_set(this.swigCPtr, this, string2)
    }

    Unit setPassword(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_password_set(this.swigCPtr, this, string2)
    }

    Unit setPhone(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_phone_set(this.swigCPtr, this, string2)
    }

    Unit setTimezone(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_timezone_set(this.swigCPtr, this, string2)
    }

    Unit setUser_name(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_user_name_set(this.swigCPtr, this, string2)
    }
}

