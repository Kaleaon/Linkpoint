/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_aux_audio_properties_state
import com.vivox.service.vx_evt_base_t

class vx_evt_aux_audio_properties_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_aux_audio_properties_t() {
        this(VxClientProxyJNI.new_vx_evt_aux_audio_properties_t(), true)
    }

    protected vx_evt_aux_audio_properties_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_aux_audio_properties_t vx_evt_aux_audio_properties_t2) {
        if (vx_evt_aux_audio_properties_t2 != null) return vx_evt_aux_audio_properties_t2.swigCPtr
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
    vx_evt_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_evt_aux_audio_properties_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    Double getMic_energy() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_energy_get(this.swigCPtr, this)
    }

    Int getMic_is_active() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_is_active_get(this.swigCPtr, this)
    }

    Int getMic_volume() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_volume_get(this.swigCPtr, this)
    }

    Double getSpeaker_energy() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_energy_get(this.swigCPtr, this)
    }

    Int getSpeaker_is_active() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_is_active_get(this.swigCPtr, this)
    }

    Int getSpeaker_volume() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_volume_get(this.swigCPtr, this)
    }

    vx_aux_audio_properties_state getState() {
        return vx_aux_audio_properties_state.swigToEnum(VxClientProxyJNI.vx_evt_aux_audio_properties_t_state_get(this.swigCPtr, this))
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setMic_energy(Double d) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_energy_set(this.swigCPtr, this, d)
    }

    Unit setMic_is_active(Int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_is_active_set(this.swigCPtr, this, n)
    }

    Unit setMic_volume(Int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_volume_set(this.swigCPtr, this, n)
    }

    Unit setSpeaker_energy(Double d) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_energy_set(this.swigCPtr, this, d)
    }

    Unit setSpeaker_is_active(Int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_is_active_set(this.swigCPtr, this, n)
    }

    Unit setSpeaker_volume(Int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_volume_set(this.swigCPtr, this, n)
    }

    Unit setState(vx_aux_audio_properties_state vx_aux_audio_properties_state2) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_state_set(this.swigCPtr, this, vx_aux_audio_properties_state2.swigValue())
    }
}

