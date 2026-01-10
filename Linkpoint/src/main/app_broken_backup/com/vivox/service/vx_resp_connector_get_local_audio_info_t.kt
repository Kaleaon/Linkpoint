/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_connector_get_local_audio_info_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_connector_get_local_audio_info_t() {
        this(VxClientProxyJNI.new_vx_resp_connector_get_local_audio_info_t(), true)
    }

    protected vx_resp_connector_get_local_audio_info_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_connector_get_local_audio_info_t vx_resp_connector_get_local_audio_info_t2) {
        if (vx_resp_connector_get_local_audio_info_t2 != null) return vx_resp_connector_get_local_audio_info_t2.swigCPtr
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
        var l: Long = VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    Int getIs_mic_muted() {
        return VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_is_mic_muted_get(this.swigCPtr, this)
    }

    Int getIs_speaker_muted() {
        return VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_is_speaker_muted_get(this.swigCPtr, this)
    }

    Int getMic_volume() {
        return VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_mic_volume_get(this.swigCPtr, this)
    }

    Int getSpeaker_volume() {
        return VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_speaker_volume_get(this.swigCPtr, this)
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setIs_mic_muted(Int n) {
        VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_is_mic_muted_set(this.swigCPtr, this, n)
    }

    Unit setIs_speaker_muted(Int n) {
        VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_is_speaker_muted_set(this.swigCPtr, this, n)
    }

    Unit setMic_volume(Int n) {
        VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_mic_volume_set(this.swigCPtr, this, n)
    }

    Unit setSpeaker_volume(Int n) {
        VxClientProxyJNI.vx_resp_connector_get_local_audio_info_t_speaker_volume_set(this.swigCPtr, this, n)
    }
}

