/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_sessiongroup_type

class vx_req_sessiongroup_create_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_sessiongroup_create_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_create_t(), true)
    }

    protected vx_req_sessiongroup_create_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_sessiongroup_create_t vx_req_sessiongroup_create_t2) {
        if (vx_req_sessiongroup_create_t2 != null) return vx_req_sessiongroup_create_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_sessiongroup_create_t_account_handle_get(this.swigCPtr, this)
    }

    String getAlias_username() {
        return VxClientProxyJNI.vx_req_sessiongroup_create_t_alias_username_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_sessiongroup_create_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getCapture_device_id() {
        return VxClientProxyJNI.vx_req_sessiongroup_create_t_capture_device_id_get(this.swigCPtr, this)
    }

    Int getLoop_mode_duration_seconds() {
        return VxClientProxyJNI.vx_req_sessiongroup_create_t_loop_mode_duration_seconds_get(this.swigCPtr, this)
    }

    String getRender_device_id() {
        return VxClientProxyJNI.vx_req_sessiongroup_create_t_render_device_id_get(this.swigCPtr, this)
    }

    vx_sessiongroup_type getType() {
        return vx_sessiongroup_type.swigToEnum(VxClientProxyJNI.vx_req_sessiongroup_create_t_type_get(this.swigCPtr, this))
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAlias_username(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setCapture_device_id(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_capture_device_id_set(this.swigCPtr, this, string2)
    }

    Unit setLoop_mode_duration_seconds(Int n) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_loop_mode_duration_seconds_set(this.swigCPtr, this, n)
    }

    Unit setRender_device_id(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_render_device_id_set(this.swigCPtr, this, string2)
    }

    Unit setType(vx_sessiongroup_type vx_sessiongroup_type2) {
        VxClientProxyJNI.vx_req_sessiongroup_create_t_type_set(this.swigCPtr, this, vx_sessiongroup_type2.swigValue())
    }
}

