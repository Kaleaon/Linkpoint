/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_message_state

class vx_evt_message_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_message_t() {
        this(VxClientProxyJNI.new_vx_evt_message_t(), true)
    }

    protected vx_evt_message_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_message_t vx_evt_message_t2) {
        if (vx_evt_message_t2 != null) return vx_evt_message_t2.swigCPtr
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
        return VxClientProxyJNI.vx_evt_message_t_alias_username_get(this.swigCPtr, this)
    }

    String getApplication() {
        return VxClientProxyJNI.vx_evt_message_t_application_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_evt_message_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    String getMessage_body() {
        return VxClientProxyJNI.vx_evt_message_t_message_body_get(this.swigCPtr, this)
    }

    String getMessage_header() {
        return VxClientProxyJNI.vx_evt_message_t_message_header_get(this.swigCPtr, this)
    }

    String getParticipant_displayname() {
        return VxClientProxyJNI.vx_evt_message_t_participant_displayname_get(this.swigCPtr, this)
    }

    String getParticipant_uri() {
        return VxClientProxyJNI.vx_evt_message_t_participant_uri_get(this.swigCPtr, this)
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_evt_message_t_session_handle_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_message_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    vx_message_state getState() {
        return vx_message_state.swigToEnum(VxClientProxyJNI.vx_evt_message_t_state_get(this.swigCPtr, this))
    }

    Unit setAlias_username(String string2) {
        VxClientProxyJNI.vx_evt_message_t_alias_username_set(this.swigCPtr, this, string2)
    }

    Unit setApplication(String string2) {
        VxClientProxyJNI.vx_evt_message_t_application_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_message_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setMessage_body(String string2) {
        VxClientProxyJNI.vx_evt_message_t_message_body_set(this.swigCPtr, this, string2)
    }

    Unit setMessage_header(String string2) {
        VxClientProxyJNI.vx_evt_message_t_message_header_set(this.swigCPtr, this, string2)
    }

    Unit setParticipant_displayname(String string2) {
        VxClientProxyJNI.vx_evt_message_t_participant_displayname_set(this.swigCPtr, this, string2)
    }

    Unit setParticipant_uri(String string2) {
        VxClientProxyJNI.vx_evt_message_t_participant_uri_set(this.swigCPtr, this, string2)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_message_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_message_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }

    Unit setState(vx_message_state vx_message_state2) {
        VxClientProxyJNI.vx_evt_message_t_state_set(this.swigCPtr, this, vx_message_state2.swigValue())
    }
}

