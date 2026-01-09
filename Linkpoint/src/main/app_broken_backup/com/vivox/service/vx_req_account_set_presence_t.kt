/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_buddy_presence_state
import com.vivox.service.vx_req_base_t

class vx_req_account_set_presence_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_account_set_presence_t() {
        this(VxClientProxyJNI.new_vx_req_account_set_presence_t(), true)
    }

    protected vx_req_account_set_presence_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_account_set_presence_t vx_req_account_set_presence_t2) {
        if (vx_req_account_set_presence_t2 != null) return vx_req_account_set_presence_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_account_set_presence_t_account_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_account_set_presence_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getCustom_message() {
        return VxClientProxyJNI.vx_req_account_set_presence_t_custom_message_get(this.swigCPtr, this)
    }

    vx_buddy_presence_state getPresence() {
        return vx_buddy_presence_state.swigToEnum(VxClientProxyJNI.vx_req_account_set_presence_t_presence_get(this.swigCPtr, this))
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setCustom_message(String string2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_custom_message_set(this.swigCPtr, this, string2)
    }

    Unit setPresence(vx_buddy_presence_state vx_buddy_presence_state2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_presence_set(this.swigCPtr, this, vx_buddy_presence_state2.swigValue())
    }
}

