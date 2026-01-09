/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_participant
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_channel_get_banned_users_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_channel_get_banned_users_t() {
        this(VxClientProxyJNI.new_vx_resp_channel_get_banned_users_t(), true)
    }

    protected vx_resp_channel_get_banned_users_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_channel_get_banned_users_t vx_resp_channel_get_banned_users_t2) {
        if (vx_resp_channel_get_banned_users_t2 != null) return vx_resp_channel_get_banned_users_t2.swigCPtr
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_participant getBanned_users() {
        var l: Long = VxClientProxyJNI.vx_resp_channel_get_banned_users_t_banned_users_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_participant(l, false)
        return null
    }

    Int getBanned_users_count() {
        return VxClientProxyJNI.vx_resp_channel_get_banned_users_t_banned_users_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_resp_channel_get_banned_users_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    Unit setBanned_users(SWIGTYPE_p_p_vx_participant sWIGTYPE_p_p_vx_participant) {
        VxClientProxyJNI.vx_resp_channel_get_banned_users_t_banned_users_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_participant.getCPtr(sWIGTYPE_p_p_vx_participant))
    }

    Unit setBanned_users_count(Int n) {
        VxClientProxyJNI.vx_resp_channel_get_banned_users_t_banned_users_count_set(this.swigCPtr, this, n)
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_channel_get_banned_users_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }
}

