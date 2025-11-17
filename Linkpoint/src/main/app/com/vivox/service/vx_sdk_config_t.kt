/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_sdk_config_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_sdk_config_t() {
        this(VxClientProxyJNI.new_vx_sdk_config_t(), true)
    }

    protected vx_sdk_config_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_sdk_config_t vx_sdk_config_t2) {
        if (vx_sdk_config_t2 != null) return vx_sdk_config_t2.swigCPtr
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

    Int getAllow_shared_capture_devices() {
        return VxClientProxyJNI.vx_sdk_config_t_allow_shared_capture_devices_get(this.swigCPtr, this)
    }

    String getApp_id() {
        return VxClientProxyJNI.vx_sdk_config_t_app_id_get(this.swigCPtr, this)
    }

    String getCert_data_dir() {
        return VxClientProxyJNI.vx_sdk_config_t_cert_data_dir_get(this.swigCPtr, this)
    }

    Int getMax_logins_per_user() {
        return VxClientProxyJNI.vx_sdk_config_t_max_logins_per_user_get(this.swigCPtr, this)
    }

    Int getNum_codec_threads() {
        return VxClientProxyJNI.vx_sdk_config_t_num_codec_threads_get(this.swigCPtr, this)
    }

    Int getNum_voice_threads() {
        return VxClientProxyJNI.vx_sdk_config_t_num_voice_threads_get(this.swigCPtr, this)
    }

    Int getNum_web_threads() {
        return VxClientProxyJNI.vx_sdk_config_t_num_web_threads_get(this.swigCPtr, this)
    }

    Int getRender_source_initial_buffer_count() {
        return VxClientProxyJNI.vx_sdk_config_t_render_source_initial_buffer_count_get(this.swigCPtr, this)
    }

    Int getRender_source_queue_depth_max() {
        return VxClientProxyJNI.vx_sdk_config_t_render_source_queue_depth_max_get(this.swigCPtr, this)
    }

    Int getUpstream_jitter_frame_count() {
        return VxClientProxyJNI.vx_sdk_config_t_upstream_jitter_frame_count_get(this.swigCPtr, this)
    }

    Unit setAllow_shared_capture_devices(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_allow_shared_capture_devices_set(this.swigCPtr, this, n)
    }

    Unit setApp_id(String string2) {
        VxClientProxyJNI.vx_sdk_config_t_app_id_set(this.swigCPtr, this, string2)
    }

    Unit setCert_data_dir(String string2) {
        VxClientProxyJNI.vx_sdk_config_t_cert_data_dir_set(this.swigCPtr, this, string2)
    }

    Unit setMax_logins_per_user(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_max_logins_per_user_set(this.swigCPtr, this, n)
    }

    Unit setNum_codec_threads(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_num_codec_threads_set(this.swigCPtr, this, n)
    }

    Unit setNum_voice_threads(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_num_voice_threads_set(this.swigCPtr, this, n)
    }

    Unit setNum_web_threads(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_num_web_threads_set(this.swigCPtr, this, n)
    }

    Unit setRender_source_initial_buffer_count(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_render_source_initial_buffer_count_set(this.swigCPtr, this, n)
    }

    Unit setRender_source_queue_depth_max(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_render_source_queue_depth_max_set(this.swigCPtr, this, n)
    }

    Unit setUpstream_jitter_frame_count(Int n) {
        VxClientProxyJNI.vx_sdk_config_t_upstream_jitter_frame_count_set(this.swigCPtr, this, n)
    }
}

