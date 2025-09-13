/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_sdk_config_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_sdk_config_t() {
        this(VxClientProxyJNI.new_vx_sdk_config_t(), true);
    }

    protected vx_sdk_config_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_sdk_config_t vx_sdk_config_t2) {
        if (vx_sdk_config_t2 != null) return vx_sdk_config_t2.swigCPtr;
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

    public int getAllow_shared_capture_devices() {
        return VxClientProxyJNI.vx_sdk_config_t_allow_shared_capture_devices_get(this.swigCPtr, this);
    }

    public String getApp_id() {
        return VxClientProxyJNI.vx_sdk_config_t_app_id_get(this.swigCPtr, this);
    }

    public String getCert_data_dir() {
        return VxClientProxyJNI.vx_sdk_config_t_cert_data_dir_get(this.swigCPtr, this);
    }

    public int getMax_logins_per_user() {
        return VxClientProxyJNI.vx_sdk_config_t_max_logins_per_user_get(this.swigCPtr, this);
    }

    public int getNum_codec_threads() {
        return VxClientProxyJNI.vx_sdk_config_t_num_codec_threads_get(this.swigCPtr, this);
    }

    public int getNum_voice_threads() {
        return VxClientProxyJNI.vx_sdk_config_t_num_voice_threads_get(this.swigCPtr, this);
    }

    public int getNum_web_threads() {
        return VxClientProxyJNI.vx_sdk_config_t_num_web_threads_get(this.swigCPtr, this);
    }

    public int getRender_source_initial_buffer_count() {
        return VxClientProxyJNI.vx_sdk_config_t_render_source_initial_buffer_count_get(this.swigCPtr, this);
    }

    public int getRender_source_queue_depth_max() {
        return VxClientProxyJNI.vx_sdk_config_t_render_source_queue_depth_max_get(this.swigCPtr, this);
    }

    public int getUpstream_jitter_frame_count() {
        return VxClientProxyJNI.vx_sdk_config_t_upstream_jitter_frame_count_get(this.swigCPtr, this);
    }

    public void setAllow_shared_capture_devices(int n) {
        VxClientProxyJNI.vx_sdk_config_t_allow_shared_capture_devices_set(this.swigCPtr, this, n);
    }

    public void setApp_id(String string2) {
        VxClientProxyJNI.vx_sdk_config_t_app_id_set(this.swigCPtr, this, string2);
    }

    public void setCert_data_dir(String string2) {
        VxClientProxyJNI.vx_sdk_config_t_cert_data_dir_set(this.swigCPtr, this, string2);
    }

    public void setMax_logins_per_user(int n) {
        VxClientProxyJNI.vx_sdk_config_t_max_logins_per_user_set(this.swigCPtr, this, n);
    }

    public void setNum_codec_threads(int n) {
        VxClientProxyJNI.vx_sdk_config_t_num_codec_threads_set(this.swigCPtr, this, n);
    }

    public void setNum_voice_threads(int n) {
        VxClientProxyJNI.vx_sdk_config_t_num_voice_threads_set(this.swigCPtr, this, n);
    }

    public void setNum_web_threads(int n) {
        VxClientProxyJNI.vx_sdk_config_t_num_web_threads_set(this.swigCPtr, this, n);
    }

    public void setRender_source_initial_buffer_count(int n) {
        VxClientProxyJNI.vx_sdk_config_t_render_source_initial_buffer_count_set(this.swigCPtr, this, n);
    }

    public void setRender_source_queue_depth_max(int n) {
        VxClientProxyJNI.vx_sdk_config_t_render_source_queue_depth_max_set(this.swigCPtr, this, n);
    }

    public void setUpstream_jitter_frame_count(int n) {
        VxClientProxyJNI.vx_sdk_config_t_upstream_jitter_frame_count_set(this.swigCPtr, this, n);
    }
}

