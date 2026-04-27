/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_account_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_account_t() {
        this(VxClientProxyJNI.new_vx_account_t(), true);
    }

    protected vx_account_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_account_t vx_account_t2) {
        if (vx_account_t2 != null) return vx_account_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_account_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getCarrier() {
        return VxClientProxyJNI.vx_account_t_carrier_get(this.swigCPtr, this);
    }

    public String getCreated_date() {
        return VxClientProxyJNI.vx_account_t_created_date_get(this.swigCPtr, this);
    }

    public String getDisplayname() {
        return VxClientProxyJNI.vx_account_t_displayname_get(this.swigCPtr, this);
    }

    public String getEmail() {
        return VxClientProxyJNI.vx_account_t_email_get(this.swigCPtr, this);
    }

    public String getFirstname() {
        return VxClientProxyJNI.vx_account_t_firstname_get(this.swigCPtr, this);
    }

    public String getLastname() {
        return VxClientProxyJNI.vx_account_t_lastname_get(this.swigCPtr, this);
    }

    public String getPhone() {
        return VxClientProxyJNI.vx_account_t_phone_get(this.swigCPtr, this);
    }

    public String getUri() {
        return VxClientProxyJNI.vx_account_t_uri_get(this.swigCPtr, this);
    }

    public String getUsername() {
        return VxClientProxyJNI.vx_account_t_username_get(this.swigCPtr, this);
    }

    public void setCarrier(String string2) {
        VxClientProxyJNI.vx_account_t_carrier_set(this.swigCPtr, this, string2);
    }

    public void setCreated_date(String string2) {
        VxClientProxyJNI.vx_account_t_created_date_set(this.swigCPtr, this, string2);
    }

    public void setDisplayname(String string2) {
        VxClientProxyJNI.vx_account_t_displayname_set(this.swigCPtr, this, string2);
    }

    public void setEmail(String string2) {
        VxClientProxyJNI.vx_account_t_email_set(this.swigCPtr, this, string2);
    }

    public void setFirstname(String string2) {
        VxClientProxyJNI.vx_account_t_firstname_set(this.swigCPtr, this, string2);
    }

    public void setLastname(String string2) {
        VxClientProxyJNI.vx_account_t_lastname_set(this.swigCPtr, this, string2);
    }

    public void setPhone(String string2) {
        VxClientProxyJNI.vx_account_t_phone_set(this.swigCPtr, this, string2);
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_account_t_uri_set(this.swigCPtr, this, string2);
    }

    public void setUsername(String string2) {
        VxClientProxyJNI.vx_account_t_username_set(this.swigCPtr, this, string2);
    }
}

