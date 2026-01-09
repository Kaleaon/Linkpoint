/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_void
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_message_base_t
import com.vivox.service.vx_request_type

class vx_req_base_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_base_t() {
        this(VxClientProxyJNI.new_vx_req_base_t(), true)
    }

    protected vx_req_base_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_base_t vx_req_base_t2) {
        if (vx_req_base_t2 != null) return vx_req_base_t2.swigCPtr
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

    String getCookie() {
        return VxClientProxyJNI.vx_req_base_t_cookie_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_message_base_t getMessage() {
        Long l = VxClientProxyJNI.vx_req_base_t_message_get(this.swigCPtr, this)
        if (l != 0L) return vx_message_base_t(l, false)
        return null
    }

    vx_request_type getType() {
        return vx_request_type.swigToEnum(VxClientProxyJNI.vx_req_base_t_type_get(this.swigCPtr, this))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_void getVcookie() {
        Long l = VxClientProxyJNI.vx_req_base_t_vcookie_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_void(l, false)
        return null
    }

    Unit setCookie(String string2) {
        VxClientProxyJNI.vx_req_base_t_cookie_set(this.swigCPtr, this, string2)
    }

    Unit setMessage(vx_message_base_t vx_message_base_t2) {
        VxClientProxyJNI.vx_req_base_t_message_set(this.swigCPtr, this, vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2)
    }

    Unit setType(vx_request_type vx_request_type2) {
        VxClientProxyJNI.vx_req_base_t_type_set(this.swigCPtr, this, vx_request_type2.swigValue())
    }

    Unit setVcookie(SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_req_base_t_vcookie_set(this.swigCPtr, this, SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }
}

