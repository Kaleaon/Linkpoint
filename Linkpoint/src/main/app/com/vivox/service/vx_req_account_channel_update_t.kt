/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_channel_mode
import com.vivox.service.vx_req_base_t

class vx_req_account_channel_update_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_account_channel_update_t() {
        this(VxClientProxyJNI.new_vx_req_account_channel_update_t(), true)
    }

    protected vx_req_account_channel_update_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_account_channel_update_t vx_req_account_channel_update_t2) {
        if (vx_req_account_channel_update_t2 != null) return vx_req_account_channel_update_t2.swigCPtr
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

    String getAccount_handle() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_account_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_account_channel_update_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    Int getCapacity() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_capacity_get(this.swigCPtr, this)
    }

    String getChannel_desc() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_channel_desc_get(this.swigCPtr, this)
    }

    vx_channel_mode getChannel_mode() {
        return vx_channel_mode.swigToEnum(VxClientProxyJNI.vx_req_account_channel_update_t_channel_mode_get(this.swigCPtr, this))
    }

    String getChannel_name() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_channel_name_get(this.swigCPtr, this)
    }

    String getChannel_uri() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_channel_uri_get(this.swigCPtr, this)
    }

    Int getClamping_dist() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_clamping_dist_get(this.swigCPtr, this)
    }

    Int getDist_model() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_dist_model_get(this.swigCPtr, this)
    }

    Int getEncrypt_audio() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_encrypt_audio_get(this.swigCPtr, this)
    }

    Double getMax_gain() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_max_gain_get(this.swigCPtr, this)
    }

    Int getMax_participants() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_max_participants_get(this.swigCPtr, this)
    }

    Int getMax_range() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_max_range_get(this.swigCPtr, this)
    }

    String getProtected_password() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_protected_password_get(this.swigCPtr, this)
    }

    Double getRoll_off() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_roll_off_get(this.swigCPtr, this)
    }

    Int getSet_persistent() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_set_persistent_get(this.swigCPtr, this)
    }

    Int getSet_protected() {
        return VxClientProxyJNI.vx_req_account_channel_update_t_set_protected_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setCapacity(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_capacity_set(this.swigCPtr, this, n)
    }

    Unit setChannel_desc(String string2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_channel_desc_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_mode(vx_channel_mode vx_channel_mode2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_channel_mode_set(this.swigCPtr, this, vx_channel_mode2.swigValue())
    }

    Unit setChannel_name(String string2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_channel_name_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_uri(String string2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_channel_uri_set(this.swigCPtr, this, string2)
    }

    Unit setClamping_dist(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_clamping_dist_set(this.swigCPtr, this, n)
    }

    Unit setDist_model(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_dist_model_set(this.swigCPtr, this, n)
    }

    Unit setEncrypt_audio(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_encrypt_audio_set(this.swigCPtr, this, n)
    }

    Unit setMax_gain(Double d) {
        VxClientProxyJNI.vx_req_account_channel_update_t_max_gain_set(this.swigCPtr, this, d)
    }

    Unit setMax_participants(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_max_participants_set(this.swigCPtr, this, n)
    }

    Unit setMax_range(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_max_range_set(this.swigCPtr, this, n)
    }

    Unit setProtected_password(String string2) {
        VxClientProxyJNI.vx_req_account_channel_update_t_protected_password_set(this.swigCPtr, this, string2)
    }

    Unit setRoll_off(Double d) {
        VxClientProxyJNI.vx_req_account_channel_update_t_roll_off_set(this.swigCPtr, this, d)
    }

    Unit setSet_persistent(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_set_persistent_set(this.swigCPtr, this, n)
    }

    Unit setSet_protected(Int n) {
        VxClientProxyJNI.vx_req_account_channel_update_t_set_protected_set(this.swigCPtr, this, n)
    }
}

