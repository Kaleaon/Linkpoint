/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_channel_moderation_type
import com.vivox.service.vx_channel_search_type
import com.vivox.service.vx_req_base_t

class vx_req_account_channel_search_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_account_channel_search_t() {
        this(VxClientProxyJNI.new_vx_req_account_channel_search_t(), true)
    }

    protected vx_req_account_channel_search_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_account_channel_search_t vx_req_account_channel_search_t2) {
        if (vx_req_account_channel_search_t2 != null) return vx_req_account_channel_search_t2.swigCPtr
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
        return VxClientProxyJNI.vx_req_account_channel_search_t_account_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_account_channel_search_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    Int getBegins_with() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_begins_with_get(this.swigCPtr, this)
    }

    Int getChannel_active() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_channel_active_get(this.swigCPtr, this)
    }

    String getChannel_description() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_channel_description_get(this.swigCPtr, this)
    }

    String getChannel_name() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_channel_name_get(this.swigCPtr, this)
    }

    vx_channel_search_type getChannel_type() {
        return vx_channel_search_type.swigToEnum(VxClientProxyJNI.vx_req_account_channel_search_t_channel_type_get(this.swigCPtr, this))
    }

    vx_channel_moderation_type getModeration_type() {
        return vx_channel_moderation_type.swigToEnum(VxClientProxyJNI.vx_req_account_channel_search_t_moderation_type_get(this.swigCPtr, this))
    }

    Int getPage_number() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_page_number_get(this.swigCPtr, this)
    }

    Int getPage_size() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_page_size_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setBegins_with(Int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_begins_with_set(this.swigCPtr, this, n)
    }

    Unit setChannel_active(Int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_active_set(this.swigCPtr, this, n)
    }

    Unit setChannel_description(String string2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_description_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_name(String string2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_name_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_type(vx_channel_search_type vx_channel_search_type2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_type_set(this.swigCPtr, this, vx_channel_search_type2.swigValue())
    }

    Unit setModeration_type(vx_channel_moderation_type vx_channel_moderation_type2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_moderation_type_set(this.swigCPtr, this, vx_channel_moderation_type2.swigValue())
    }

    Unit setPage_number(Int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_page_number_set(this.swigCPtr, this, n)
    }

    Unit setPage_size(Int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_page_size_set(this.swigCPtr, this, n)
    }
}

