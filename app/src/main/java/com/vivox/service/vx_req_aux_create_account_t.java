/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_generic_credentials;
import com.vivox.service.vx_req_base_t;

public class vx_req_aux_create_account_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_aux_create_account_t() {
        this(VxClientProxyJNI.new_vx_req_aux_create_account_t(), true);
    }

    protected vx_req_aux_create_account_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_aux_create_account_t vx_req_aux_create_account_t2) {
        if (vx_req_aux_create_account_t2 != null) return vx_req_aux_create_account_t2.swigCPtr;
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

    public String getAge() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_age_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_aux_create_account_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_generic_credentials getCredentials() {
        long l = VxClientProxyJNI.vx_req_aux_create_account_t_credentials_get(this.swigCPtr, this);
        if (l != 0L) return new vx_generic_credentials(l, false);
        return null;
    }

    public String getDisplayname() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_displayname_get(this.swigCPtr, this);
    }

    public String getEmail() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_email_get(this.swigCPtr, this);
    }

    public String getExt_id() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_ext_id_get(this.swigCPtr, this);
    }

    public String getExt_profile() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_ext_profile_get(this.swigCPtr, this);
    }

    public String getFirstname() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_firstname_get(this.swigCPtr, this);
    }

    public String getGender() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_gender_get(this.swigCPtr, this);
    }

    public String getLang() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_lang_get(this.swigCPtr, this);
    }

    public String getLastname() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_lastname_get(this.swigCPtr, this);
    }

    public String getNumber() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_number_get(this.swigCPtr, this);
    }

    public String getPassword() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_password_get(this.swigCPtr, this);
    }

    public String getPhone() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_phone_get(this.swigCPtr, this);
    }

    public String getTimezone() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_timezone_get(this.swigCPtr, this);
    }

    public String getUser_name() {
        return VxClientProxyJNI.vx_req_aux_create_account_t_user_name_get(this.swigCPtr, this);
    }

    public void setAge(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_age_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setCredentials(vx_generic_credentials vx_generic_credentials2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_credentials_set(this.swigCPtr, this, vx_generic_credentials.getCPtr(vx_generic_credentials2), vx_generic_credentials2);
    }

    public void setDisplayname(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_displayname_set(this.swigCPtr, this, string2);
    }

    public void setEmail(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_email_set(this.swigCPtr, this, string2);
    }

    public void setExt_id(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_ext_id_set(this.swigCPtr, this, string2);
    }

    public void setExt_profile(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_ext_profile_set(this.swigCPtr, this, string2);
    }

    public void setFirstname(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_firstname_set(this.swigCPtr, this, string2);
    }

    public void setGender(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_gender_set(this.swigCPtr, this, string2);
    }

    public void setLang(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_lang_set(this.swigCPtr, this, string2);
    }

    public void setLastname(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_lastname_set(this.swigCPtr, this, string2);
    }

    public void setNumber(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_number_set(this.swigCPtr, this, string2);
    }

    public void setPassword(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_password_set(this.swigCPtr, this, string2);
    }

    public void setPhone(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_phone_set(this.swigCPtr, this, string2);
    }

    public void setTimezone(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_timezone_set(this.swigCPtr, this, string2);
    }

    public void setUser_name(String string2) {
        VxClientProxyJNI.vx_req_aux_create_account_t_user_name_set(this.swigCPtr, this, string2);
    }
}

