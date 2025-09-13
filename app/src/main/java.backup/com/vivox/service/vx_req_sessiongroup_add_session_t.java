/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_password_hash_algorithm_t;
import com.vivox.service.vx_req_base_t;

public class vx_req_sessiongroup_add_session_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_sessiongroup_add_session_t() {
        this(VxClientProxyJNI.new_vx_req_sessiongroup_add_session_t(), true);
    }

    protected vx_req_sessiongroup_add_session_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_sessiongroup_add_session_t vx_req_sessiongroup_add_session_t2) {
        if (vx_req_sessiongroup_add_session_t2 != null) return vx_req_sessiongroup_add_session_t2.swigCPtr;
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_sessiongroup_add_session_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getConnect_audio() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_connect_audio_get(this.swigCPtr, this);
    }

    public int getConnect_text() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_connect_text_get(this.swigCPtr, this);
    }

    public int getJitter_compensation() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_jitter_compensation_get(this.swigCPtr, this);
    }

    public String getName() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_name_get(this.swigCPtr, this);
    }

    public String getPassword() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_password_get(this.swigCPtr, this);
    }

    public vx_password_hash_algorithm_t getPassword_hash_algorithm() {
        return vx_password_hash_algorithm_t.swigToEnum(VxClientProxyJNI.vx_req_sessiongroup_add_session_t_password_hash_algorithm_get(this.swigCPtr, this));
    }

    public int getSession_font_id() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_session_font_id_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public String getUri() {
        return VxClientProxyJNI.vx_req_sessiongroup_add_session_t_uri_get(this.swigCPtr, this);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setConnect_audio(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_connect_audio_set(this.swigCPtr, this, n);
    }

    public void setConnect_text(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_connect_text_set(this.swigCPtr, this, n);
    }

    public void setJitter_compensation(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_jitter_compensation_set(this.swigCPtr, this, n);
    }

    public void setName(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_name_set(this.swigCPtr, this, string2);
    }

    public void setPassword(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_password_set(this.swigCPtr, this, string2);
    }

    public void setPassword_hash_algorithm(vx_password_hash_algorithm_t vx_password_hash_algorithm_t2) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_password_hash_algorithm_set(this.swigCPtr, this, vx_password_hash_algorithm_t2.swigValue());
    }

    public void setSession_font_id(int n) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_session_font_id_set(this.swigCPtr, this, n);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_t_uri_set(this.swigCPtr, this, string2);
    }
}

