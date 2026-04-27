/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_sessiongroup_type

class vx_evt_sessiongroup_added_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_sessiongroup_added_t() {
        this(VxClientProxyJNI.new_vx_evt_sessiongroup_added_t(), true)
    }

    protected vx_evt_sessiongroup_added_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_sessiongroup_added_t vx_evt_sessiongroup_added_t2) {
        if (vx_evt_sessiongroup_added_t2 != null) return vx_evt_sessiongroup_added_t2.swigCPtr
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
        return VxClientProxyJNI.vx_evt_sessiongroup_added_t_account_handle_get(this.swigCPtr, this)
    }

    String getAlias_username() {
        return VxClientProxyJNI.vx_evt_sessiongroup_added_t_alias_username_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        Long l = VxClientProxyJNI.vx_evt_sessiongroup_added_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_sessiongroup_added_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    vx_sessiongroup_type getType() {
        return vx_sessiongroup_type.swigToEnum(VxClientProxyJNI.vx_evt_sessiongroup_added_t_type_get(this.swigCPtr, this))
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_added_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAlias_username(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_added_t_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_sessiongroup_added_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_sessiongroup_added_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setType(vx_sessiongroup_type vx_sessiongroup_type2) {
        VxClientProxyJNI.vx_evt_sessiongroup_added_t_type_set(this.swigCPtr, this, vx_sessiongroup_type2.swigValue())
    }
}

