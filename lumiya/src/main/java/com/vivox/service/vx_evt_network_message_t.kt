/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_evt_network_message_type

class vx_evt_network_message_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_network_message_t() {
        this(VxClientProxyJNI.new_vx_evt_network_message_t(), true)
    }

    protected vx_evt_network_message_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_network_message_t vx_evt_network_message_t2) {
        if (vx_evt_network_message_t2 != null) return vx_evt_network_message_t2.swigCPtr
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
        return VxClientProxyJNI.vx_evt_network_message_t_account_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        Long l = VxClientProxyJNI.vx_evt_network_message_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    String getContent() {
        return VxClientProxyJNI.vx_evt_network_message_t_content_get(this.swigCPtr, this)
    }

    String getContent_type() {
        return VxClientProxyJNI.vx_evt_network_message_t_content_type_get(this.swigCPtr, this)
    }

    vx_evt_network_message_type getNetwork_message_type() {
        return vx_evt_network_message_type.swigToEnum(VxClientProxyJNI.vx_evt_network_message_t_network_message_type_get(this.swigCPtr, this))
    }

    String getReceiver_alias_username() {
        return VxClientProxyJNI.vx_evt_network_message_t_receiver_alias_username_get(this.swigCPtr, this)
    }

    String getSender_alias_username() {
        return VxClientProxyJNI.vx_evt_network_message_t_sender_alias_username_get(this.swigCPtr, this)
    }

    String getSender_display_name() {
        return VxClientProxyJNI.vx_evt_network_message_t_sender_display_name_get(this.swigCPtr, this)
    }

    String getSender_uri() {
        return VxClientProxyJNI.vx_evt_network_message_t_sender_uri_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_network_message_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setContent(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_content_set(this.swigCPtr, this, string2)
    }

    Unit setContent_type(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_content_type_set(this.swigCPtr, this, string2)
    }

    Unit setNetwork_message_type(vx_evt_network_message_type vx_evt_network_message_type2) {
        VxClientProxyJNI.vx_evt_network_message_t_network_message_type_set(this.swigCPtr, this, vx_evt_network_message_type2.swigValue())
    }

    Unit setReceiver_alias_username(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_receiver_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setSender_alias_username(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_sender_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setSender_display_name(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_sender_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setSender_uri(String string2) {
        VxClientProxyJNI.vx_evt_network_message_t_sender_uri_set(this.swigCPtr, this, string2)
    }
}

