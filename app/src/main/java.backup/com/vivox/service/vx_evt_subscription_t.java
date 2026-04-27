/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_subscription_type;

public class vx_evt_subscription_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_subscription_t() {
        this(VxClientProxyJNI.new_vx_evt_subscription_t(), true);
    }

    protected vx_evt_subscription_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_subscription_t vx_evt_subscription_t2) {
        if (vx_evt_subscription_t2 != null) return vx_evt_subscription_t2.swigCPtr;
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

    public String getAccount_handle() {
        return VxClientProxyJNI.vx_evt_subscription_t_account_handle_get(this.swigCPtr, this);
    }

    public String getApplication() {
        return VxClientProxyJNI.vx_evt_subscription_t_application_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_subscription_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public String getBuddy_uri() {
        return VxClientProxyJNI.vx_evt_subscription_t_buddy_uri_get(this.swigCPtr, this);
    }

    public String getDisplayname() {
        return VxClientProxyJNI.vx_evt_subscription_t_displayname_get(this.swigCPtr, this);
    }

    public String getMessage() {
        return VxClientProxyJNI.vx_evt_subscription_t_message_get(this.swigCPtr, this);
    }

    public String getSubscription_handle() {
        return VxClientProxyJNI.vx_evt_subscription_t_subscription_handle_get(this.swigCPtr, this);
    }

    public vx_subscription_type getSubscription_type() {
        return vx_subscription_type.swigToEnum(VxClientProxyJNI.vx_evt_subscription_t_subscription_type_get(this.swigCPtr, this));
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setApplication(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_application_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_subscription_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_buddy_uri_set(this.swigCPtr, this, string2);
    }

    public void setDisplayname(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_displayname_set(this.swigCPtr, this, string2);
    }

    public void setMessage(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_message_set(this.swigCPtr, this, string2);
    }

    public void setSubscription_handle(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_subscription_handle_set(this.swigCPtr, this, string2);
    }

    public void setSubscription_type(vx_subscription_type vx_subscription_type2) {
        VxClientProxyJNI.vx_evt_subscription_t_subscription_type_set(this.swigCPtr, this, vx_subscription_type2.swigValue());
    }
}

