/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_aux_audio_properties_state;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_aux_audio_properties_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_aux_audio_properties_t() {
        this(VxClientProxyJNI.new_vx_evt_aux_audio_properties_t(), true);
    }

    protected vx_evt_aux_audio_properties_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_aux_audio_properties_t vx_evt_aux_audio_properties_t2) {
        if (vx_evt_aux_audio_properties_t2 != null) return vx_evt_aux_audio_properties_t2.swigCPtr;
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
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_aux_audio_properties_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public double getMic_energy() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_energy_get(this.swigCPtr, this);
    }

    public int getMic_is_active() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_is_active_get(this.swigCPtr, this);
    }

    public int getMic_volume() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_volume_get(this.swigCPtr, this);
    }

    public double getSpeaker_energy() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_energy_get(this.swigCPtr, this);
    }

    public int getSpeaker_is_active() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_is_active_get(this.swigCPtr, this);
    }

    public int getSpeaker_volume() {
        return VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_volume_get(this.swigCPtr, this);
    }

    public vx_aux_audio_properties_state getState() {
        return vx_aux_audio_properties_state.swigToEnum(VxClientProxyJNI.vx_evt_aux_audio_properties_t_state_get(this.swigCPtr, this));
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setMic_energy(double d) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_energy_set(this.swigCPtr, this, d);
    }

    public void setMic_is_active(int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_is_active_set(this.swigCPtr, this, n);
    }

    public void setMic_volume(int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_mic_volume_set(this.swigCPtr, this, n);
    }

    public void setSpeaker_energy(double d) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_energy_set(this.swigCPtr, this, d);
    }

    public void setSpeaker_is_active(int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_is_active_set(this.swigCPtr, this, n);
    }

    public void setSpeaker_volume(int n) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_speaker_volume_set(this.swigCPtr, this, n);
    }

    public void setState(vx_aux_audio_properties_state vx_aux_audio_properties_state2) {
        VxClientProxyJNI.vx_evt_aux_audio_properties_t_state_set(this.swigCPtr, this, vx_aux_audio_properties_state2.swigValue());
    }
}

