/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t

class vx_evt_session_added_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_session_added_t() {
        this(VxClientProxyJNI.new_vx_evt_session_added_t(), true)
    }

    protected vx_evt_session_added_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_session_added_t vx_evt_session_added_t2) {
        if (vx_evt_session_added_t2 != null) return vx_evt_session_added_t2.swigCPtr
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

    String getAlias_username() {
        return VxClientProxyJNI.vx_evt_session_added_t_alias_username_get(this.swigCPtr, this)
    }

    String getApplication() {
        return VxClientProxyJNI.vx_evt_session_added_t_application_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_evt_session_added_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    String getChannel_name() {
        return VxClientProxyJNI.vx_evt_session_added_t_channel_name_get(this.swigCPtr, this)
    }

    String getDisplayname() {
        return VxClientProxyJNI.vx_evt_session_added_t_displayname_get(this.swigCPtr, this)
    }

    Int getIncoming() {
        return VxClientProxyJNI.vx_evt_session_added_t_incoming_get(this.swigCPtr, this)
    }

    Int getIs_channel() {
        return VxClientProxyJNI.vx_evt_session_added_t_is_channel_get(this.swigCPtr, this)
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_evt_session_added_t_session_handle_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_session_added_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    String getUri() {
        return VxClientProxyJNI.vx_evt_session_added_t_uri_get(this.swigCPtr, this)
    }

    Unit setAlias_username(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setApplication(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_application_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_session_added_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setChannel_name(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_channel_name_set(this.swigCPtr, this, string2)
    }

    Unit setDisplayname(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_displayname_set(this.swigCPtr, this, string2)
    }

    Unit setIncoming(Int n) {
        VxClientProxyJNI.vx_evt_session_added_t_incoming_set(this.swigCPtr, this, n)
    }

    Unit setIs_channel(Int n) {
        VxClientProxyJNI.vx_evt_session_added_t_is_channel_set(this.swigCPtr, this, n)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_evt_session_added_t_uri_set(this.swigCPtr, this, string2)
    }
}

