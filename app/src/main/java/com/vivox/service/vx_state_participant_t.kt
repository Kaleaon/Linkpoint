/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_participant_type

class vx_state_participant_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_participant_t() {
        this(VxClientProxyJNI.new_vx_state_participant_t(), true)
    }

    protected vx_state_participant_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_participant_t vx_state_participant_t2) {
        if (vx_state_participant_t2 != null) return vx_state_participant_t2.swigCPtr
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

    String getDisplay_name() {
        return VxClientProxyJNI.vx_state_participant_t_display_name_get(this.swigCPtr, this)
    }

    Double getEnergy() {
        return VxClientProxyJNI.vx_state_participant_t_energy_get(this.swigCPtr, this)
    }

    Int getIs_anonymous_login() {
        return VxClientProxyJNI.vx_state_participant_t_is_anonymous_login_get(this.swigCPtr, this)
    }

    Int getIs_audio_enabled() {
        return VxClientProxyJNI.vx_state_participant_t_is_audio_enabled_get(this.swigCPtr, this)
    }

    Int getIs_audio_moderator_muted() {
        return VxClientProxyJNI.vx_state_participant_t_is_audio_moderator_muted_get(this.swigCPtr, this)
    }

    Int getIs_audio_muted_for_me() {
        return VxClientProxyJNI.vx_state_participant_t_is_audio_muted_for_me_get(this.swigCPtr, this)
    }

    Int getIs_hand_raised() {
        return VxClientProxyJNI.vx_state_participant_t_is_hand_raised_get(this.swigCPtr, this)
    }

    Int getIs_speaking() {
        return VxClientProxyJNI.vx_state_participant_t_is_speaking_get(this.swigCPtr, this)
    }

    Int getIs_text_enabled() {
        return VxClientProxyJNI.vx_state_participant_t_is_text_enabled_get(this.swigCPtr, this)
    }

    Int getIs_text_moderator_muted() {
        return VxClientProxyJNI.vx_state_participant_t_is_text_moderator_muted_get(this.swigCPtr, this)
    }

    Int getIs_text_muted_for_me() {
        return VxClientProxyJNI.vx_state_participant_t_is_text_muted_for_me_get(this.swigCPtr, this)
    }

    Int getIs_typing() {
        return VxClientProxyJNI.vx_state_participant_t_is_typing_get(this.swigCPtr, this)
    }

    vx_participant_type getType() {
        return vx_participant_type.swigToEnum(VxClientProxyJNI.vx_state_participant_t_type_get(this.swigCPtr, this))
    }

    String getUri() {
        return VxClientProxyJNI.vx_state_participant_t_uri_get(this.swigCPtr, this)
    }

    Int getVolume() {
        return VxClientProxyJNI.vx_state_participant_t_volume_get(this.swigCPtr, this)
    }

    Unit setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_participant_t_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setEnergy(Double d) {
        VxClientProxyJNI.vx_state_participant_t_energy_set(this.swigCPtr, this, d)
    }

    Unit setIs_anonymous_login(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_anonymous_login_set(this.swigCPtr, this, n)
    }

    Unit setIs_audio_enabled(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_audio_enabled_set(this.swigCPtr, this, n)
    }

    Unit setIs_audio_moderator_muted(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_audio_moderator_muted_set(this.swigCPtr, this, n)
    }

    Unit setIs_audio_muted_for_me(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_audio_muted_for_me_set(this.swigCPtr, this, n)
    }

    Unit setIs_hand_raised(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_hand_raised_set(this.swigCPtr, this, n)
    }

    Unit setIs_speaking(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_speaking_set(this.swigCPtr, this, n)
    }

    Unit setIs_text_enabled(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_text_enabled_set(this.swigCPtr, this, n)
    }

    Unit setIs_text_moderator_muted(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_text_moderator_muted_set(this.swigCPtr, this, n)
    }

    Unit setIs_text_muted_for_me(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_text_muted_for_me_set(this.swigCPtr, this, n)
    }

    Unit setIs_typing(Int n) {
        VxClientProxyJNI.vx_state_participant_t_is_typing_set(this.swigCPtr, this, n)
    }

    Unit setType(vx_participant_type vx_participant_type2) {
        VxClientProxyJNI.vx_state_participant_t_type_set(this.swigCPtr, this, vx_participant_type2.swigValue())
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_state_participant_t_uri_set(this.swigCPtr, this, string2)
    }

    Unit setVolume(Int n) {
        VxClientProxyJNI.vx_state_participant_t_volume_set(this.swigCPtr, this, n)
    }
}

