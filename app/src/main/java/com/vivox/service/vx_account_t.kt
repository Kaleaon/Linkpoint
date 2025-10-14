/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_account_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_account_t() {
        this(VxClientProxyJNI.new_vx_account_t(), true)
    }

    protected vx_account_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_account_t vx_account_t2) {
        if (vx_account_t2 != null) return vx_account_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_account_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    String getCarrier() {
        return VxClientProxyJNI.vx_account_t_carrier_get(this.swigCPtr, this)
    }

    String getCreated_date() {
        return VxClientProxyJNI.vx_account_t_created_date_get(this.swigCPtr, this)
    }

    String getDisplayname() {
        return VxClientProxyJNI.vx_account_t_displayname_get(this.swigCPtr, this)
    }

    String getEmail() {
        return VxClientProxyJNI.vx_account_t_email_get(this.swigCPtr, this)
    }

    String getFirstname() {
        return VxClientProxyJNI.vx_account_t_firstname_get(this.swigCPtr, this)
    }

    String getLastname() {
        return VxClientProxyJNI.vx_account_t_lastname_get(this.swigCPtr, this)
    }

    String getPhone() {
        return VxClientProxyJNI.vx_account_t_phone_get(this.swigCPtr, this)
    }

    String getUri() {
        return VxClientProxyJNI.vx_account_t_uri_get(this.swigCPtr, this)
    }

    String getUsername() {
        return VxClientProxyJNI.vx_account_t_username_get(this.swigCPtr, this)
    }

    Unit setCarrier(String string2) {
        VxClientProxyJNI.vx_account_t_carrier_set(this.swigCPtr, this, string2)
    }

    Unit setCreated_date(String string2) {
        VxClientProxyJNI.vx_account_t_created_date_set(this.swigCPtr, this, string2)
    }

    Unit setDisplayname(String string2) {
        VxClientProxyJNI.vx_account_t_displayname_set(this.swigCPtr, this, string2)
    }

    Unit setEmail(String string2) {
        VxClientProxyJNI.vx_account_t_email_set(this.swigCPtr, this, string2)
    }

    Unit setFirstname(String string2) {
        VxClientProxyJNI.vx_account_t_firstname_set(this.swigCPtr, this, string2)
    }

    Unit setLastname(String string2) {
        VxClientProxyJNI.vx_account_t_lastname_set(this.swigCPtr, this, string2)
    }

    Unit setPhone(String string2) {
        VxClientProxyJNI.vx_account_t_phone_set(this.swigCPtr, this, string2)
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_account_t_uri_set(this.swigCPtr, this, string2)
    }

    Unit setUsername(String string2) {
        VxClientProxyJNI.vx_account_t_username_set(this.swigCPtr, this, string2)
    }
}

