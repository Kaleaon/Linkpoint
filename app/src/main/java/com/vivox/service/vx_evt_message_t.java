/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_message_state;

public class vx_evt_message_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_message_t() {
        this(VxClientProxyJNI.new_vx_evt_message_t(), true);
    }

    protected vx_evt_message_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_message_t vx_evt_message_t2) {
        if (vx_evt_message_t2 != null) return vx_evt_message_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L;
                return;
            }
            this.swigCMemOwn = false;
            UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("C++ destructor does not have public access");
            throw unsupportedOperationException;
        }
    }

    public String getAlias_username() {
        return VxClientProxyJNI.vx_evt_message_t_alias_username_get(this.swigCPtr, this);
    }

    public String getApplication() {
        return VxClientProxyJNI.vx_evt_message_t_application_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_message_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public String getMessage_body() {
        return VxClientProxyJNI.vx_evt_message_t_message_body_get(this.swigCPtr, this);
    }

    public String getMessage_header() {
        return VxClientProxyJNI.vx_evt_message_t_message_header_get(this.swigCPtr, this);
    }

    public String getParticipant_displayname() {
        return VxClientProxyJNI.vx_evt_message_t_participant_displayname_get(this.swigCPtr, this);
    }

    public String getParticipant_uri() {
        return VxClientProxyJNI.vx_evt_message_t_participant_uri_get(this.swigCPtr, this);
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_evt_message_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_message_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public vx_message_state getState() {
        return vx_message_state.swigToEnum(VxClientProxyJNI.vx_evt_message_t_state_get(this.swigCPtr, this));
    }

    public void setAlias_username(String string2) {
        VxClientProxyJNI.vx_evt_message_t_alias_username_set(this.swigCPtr, this, string2);
    }

    public void setApplication(String string2) {
        VxClientProxyJNI.vx_evt_message_t_application_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_message_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setMessage_body(String string2) {
        VxClientProxyJNI.vx_evt_message_t_message_body_set(this.swigCPtr, this, string2);
    }

    public void setMessage_header(String string2) {
        VxClientProxyJNI.vx_evt_message_t_message_header_set(this.swigCPtr, this, string2);
    }

    public void setParticipant_displayname(String string2) {
        VxClientProxyJNI.vx_evt_message_t_participant_displayname_set(this.swigCPtr, this, string2);
    }

    public void setParticipant_uri(String string2) {
        VxClientProxyJNI.vx_evt_message_t_participant_uri_set(this.swigCPtr, this, string2);
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_message_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_message_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setState(vx_message_state vx_message_state2) {
        VxClientProxyJNI.vx_evt_message_t_state_set(this.swigCPtr, this, vx_message_state2.swigValue());
    }
}

