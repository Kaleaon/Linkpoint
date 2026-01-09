/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_subscription_type

class vx_evt_subscription_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_subscription_t() {
        this(VxClientProxyJNI.new_vx_evt_subscription_t(), true)
    }

    protected vx_evt_subscription_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_subscription_t vx_evt_subscription_t2) {
        if (vx_evt_subscription_t2 != null) return vx_evt_subscription_t2.swigCPtr
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
        return VxClientProxyJNI.vx_evt_subscription_t_account_handle_get(this.swigCPtr, this)
    }

    String getApplication() {
        return VxClientProxyJNI.vx_evt_subscription_t_application_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_evt_subscription_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    String getBuddy_uri() {
        return VxClientProxyJNI.vx_evt_subscription_t_buddy_uri_get(this.swigCPtr, this)
    }

    String getDisplayname() {
        return VxClientProxyJNI.vx_evt_subscription_t_displayname_get(this.swigCPtr, this)
    }

    String getMessage() {
        return VxClientProxyJNI.vx_evt_subscription_t_message_get(this.swigCPtr, this)
    }

    String getSubscription_handle() {
        return VxClientProxyJNI.vx_evt_subscription_t_subscription_handle_get(this.swigCPtr, this)
    }

    vx_subscription_type getSubscription_type() {
        return vx_subscription_type.swigToEnum(VxClientProxyJNI.vx_evt_subscription_t_subscription_type_get(this.swigCPtr, this))
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setApplication(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_application_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_subscription_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_buddy_uri_set(this.swigCPtr, this, string2)
    }

    Unit setDisplayname(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_displayname_set(this.swigCPtr, this, string2)
    }

    Unit setMessage(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_message_set(this.swigCPtr, this, string2)
    }

    Unit setSubscription_handle(String string2) {
        VxClientProxyJNI.vx_evt_subscription_t_subscription_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSubscription_type(vx_subscription_type vx_subscription_type2) {
        VxClientProxyJNI.vx_evt_subscription_t_subscription_type_set(this.swigCPtr, this, vx_subscription_type2.swigValue())
    }
}

