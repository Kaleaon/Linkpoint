/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_media_type
import com.vivox.service.vx_req_base_t

class vx_req_session_media_connect_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_session_media_connect_t() {
        this(VxClientProxyJNI.new_vx_req_session_media_connect_t(), true)
    }

    protected vx_req_session_media_connect_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_session_media_connect_t vx_req_session_media_connect_t2) {
        if (vx_req_session_media_connect_t2 != null) return vx_req_session_media_connect_t2.swigCPtr
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
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_session_media_connect_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    String getCapture_device_id() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_capture_device_id_get(this.swigCPtr, this)
    }

    Int getJitter_compensation() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_jitter_compensation_get(this.swigCPtr, this)
    }

    Int getLoop_mode_duration_seconds() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_loop_mode_duration_seconds_get(this.swigCPtr, this)
    }

    vx_media_type getMedia() {
        return vx_media_type.swigToEnum(VxClientProxyJNI.vx_req_session_media_connect_t_media_get(this.swigCPtr, this))
    }

    String getRender_device_id() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_render_device_id_get(this.swigCPtr, this)
    }

    Int getSession_font_id() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_session_font_id_get(this.swigCPtr, this)
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_session_handle_get(this.swigCPtr, this)
    }

    String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_req_session_media_connect_t_sessiongroup_handle_get(this.swigCPtr, this)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_session_media_connect_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setCapture_device_id(String string2) {
        VxClientProxyJNI.vx_req_session_media_connect_t_capture_device_id_set(this.swigCPtr, this, string2)
    }

    Unit setJitter_compensation(Int n) {
        VxClientProxyJNI.vx_req_session_media_connect_t_jitter_compensation_set(this.swigCPtr, this, n)
    }

    Unit setLoop_mode_duration_seconds(Int n) {
        VxClientProxyJNI.vx_req_session_media_connect_t_loop_mode_duration_seconds_set(this.swigCPtr, this, n)
    }

    Unit setMedia(vx_media_type vx_media_type2) {
        VxClientProxyJNI.vx_req_session_media_connect_t_media_set(this.swigCPtr, this, vx_media_type2.swigValue())
    }

    Unit setRender_device_id(String string2) {
        VxClientProxyJNI.vx_req_session_media_connect_t_render_device_id_set(this.swigCPtr, this, string2)
    }

    Unit setSession_font_id(Int n) {
        VxClientProxyJNI.vx_req_session_media_connect_t_session_font_id_set(this.swigCPtr, this, n)
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_req_session_media_connect_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_req_session_media_connect_t_sessiongroup_handle_set(this.swigCPtr, this, string2)
    }
}

