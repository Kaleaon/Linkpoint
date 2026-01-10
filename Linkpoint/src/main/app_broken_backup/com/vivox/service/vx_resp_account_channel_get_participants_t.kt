/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_participant
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_account_channel_get_participants_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_account_channel_get_participants_t() {
        this(VxClientProxyJNI.new_vx_resp_account_channel_get_participants_t(), true)
    }

    protected vx_resp_account_channel_get_participants_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_account_channel_get_participants_t vx_resp_account_channel_get_participants_t2) {
        if (vx_resp_account_channel_get_participants_t2 != null) return vx_resp_account_channel_get_participants_t2.swigCPtr
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
    vx_resp_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_get_participants_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    Int getFrom() {
        return VxClientProxyJNI.vx_resp_account_channel_get_participants_t_from_get(this.swigCPtr, this)
    }

    Int getPage() {
        return VxClientProxyJNI.vx_resp_account_channel_get_participants_t_page_get(this.swigCPtr, this)
    }

    Int getParticipant_count() {
        return VxClientProxyJNI.vx_resp_account_channel_get_participants_t_participant_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_participant getParticipants() {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_get_participants_t_participants_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_participant(l, false)
        return null
    }

    Int getTo() {
        return VxClientProxyJNI.vx_resp_account_channel_get_participants_t_to_get(this.swigCPtr, this)
    }

    Int getTotal() {
        return VxClientProxyJNI.vx_resp_account_channel_get_participants_t_total_get(this.swigCPtr, this)
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setFrom(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_from_set(this.swigCPtr, this, n)
    }

    Unit setPage(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_page_set(this.swigCPtr, this, n)
    }

    Unit setParticipant_count(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_participant_count_set(this.swigCPtr, this, n)
    }

    Unit setParticipants(SWIGTYPE_p_p_vx_participant sWIGTYPE_p_p_vx_participant) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_participants_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_participant.getCPtr(sWIGTYPE_p_p_vx_participant))
    }

    Unit setTo(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_to_set(this.swigCPtr, this, n)
    }

    Unit setTotal(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_get_participants_t_total_set(this.swigCPtr, this, n)
    }
}

