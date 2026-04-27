/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_participant_type;

public class vx_state_participant_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_state_participant_t() {
        this(VxClientProxyJNI.new_vx_state_participant_t(), true);
    }

    protected vx_state_participant_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_state_participant_t vx_state_participant_t2) {
        if (vx_state_participant_t2 != null) return vx_state_participant_t2.swigCPtr;
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

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_state_participant_t_display_name_get(this.swigCPtr, this);
    }

    public double getEnergy() {
        return VxClientProxyJNI.vx_state_participant_t_energy_get(this.swigCPtr, this);
    }

    public int getIs_anonymous_login() {
        return VxClientProxyJNI.vx_state_participant_t_is_anonymous_login_get(this.swigCPtr, this);
    }

    public int getIs_audio_enabled() {
        return VxClientProxyJNI.vx_state_participant_t_is_audio_enabled_get(this.swigCPtr, this);
    }

    public int getIs_audio_moderator_muted() {
        return VxClientProxyJNI.vx_state_participant_t_is_audio_moderator_muted_get(this.swigCPtr, this);
    }

    public int getIs_audio_muted_for_me() {
        return VxClientProxyJNI.vx_state_participant_t_is_audio_muted_for_me_get(this.swigCPtr, this);
    }

    public int getIs_hand_raised() {
        return VxClientProxyJNI.vx_state_participant_t_is_hand_raised_get(this.swigCPtr, this);
    }

    public int getIs_speaking() {
        return VxClientProxyJNI.vx_state_participant_t_is_speaking_get(this.swigCPtr, this);
    }

    public int getIs_text_enabled() {
        return VxClientProxyJNI.vx_state_participant_t_is_text_enabled_get(this.swigCPtr, this);
    }

    public int getIs_text_moderator_muted() {
        return VxClientProxyJNI.vx_state_participant_t_is_text_moderator_muted_get(this.swigCPtr, this);
    }

    public int getIs_text_muted_for_me() {
        return VxClientProxyJNI.vx_state_participant_t_is_text_muted_for_me_get(this.swigCPtr, this);
    }

    public int getIs_typing() {
        return VxClientProxyJNI.vx_state_participant_t_is_typing_get(this.swigCPtr, this);
    }

    public vx_participant_type getType() {
        return vx_participant_type.swigToEnum(VxClientProxyJNI.vx_state_participant_t_type_get(this.swigCPtr, this));
    }

    public String getUri() {
        return VxClientProxyJNI.vx_state_participant_t_uri_get(this.swigCPtr, this);
    }

    public int getVolume() {
        return VxClientProxyJNI.vx_state_participant_t_volume_get(this.swigCPtr, this);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_participant_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setEnergy(double d) {
        VxClientProxyJNI.vx_state_participant_t_energy_set(this.swigCPtr, this, d);
    }

    public void setIs_anonymous_login(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_anonymous_login_set(this.swigCPtr, this, n);
    }

    public void setIs_audio_enabled(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_audio_enabled_set(this.swigCPtr, this, n);
    }

    public void setIs_audio_moderator_muted(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_audio_moderator_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_audio_muted_for_me(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_audio_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setIs_hand_raised(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_hand_raised_set(this.swigCPtr, this, n);
    }

    public void setIs_speaking(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_speaking_set(this.swigCPtr, this, n);
    }

    public void setIs_text_enabled(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_text_enabled_set(this.swigCPtr, this, n);
    }

    public void setIs_text_moderator_muted(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_text_moderator_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_text_muted_for_me(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_text_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setIs_typing(int n) {
        VxClientProxyJNI.vx_state_participant_t_is_typing_set(this.swigCPtr, this, n);
    }

    public void setType(vx_participant_type vx_participant_type2) {
        VxClientProxyJNI.vx_state_participant_t_type_set(this.swigCPtr, this, vx_participant_type2.swigValue());
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_state_participant_t_uri_set(this.swigCPtr, this, string2);
    }

    public void setVolume(int n) {
        VxClientProxyJNI.vx_state_participant_t_volume_set(this.swigCPtr, this, n);
    }
}

