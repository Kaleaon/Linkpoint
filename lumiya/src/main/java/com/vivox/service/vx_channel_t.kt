/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_channel_mode

class vx_channel_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_channel_t() {
        this(VxClientProxyJNI.new_vx_channel_t(), true)
    }

    protected vx_channel_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_channel_t vx_channel_t2) {
        if (vx_channel_t2 != null) return vx_channel_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_channel_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    Int getActive_participants() {
        return VxClientProxyJNI.vx_channel_t_active_participants_get(this.swigCPtr, this)
    }

    Int getCapacity() {
        return VxClientProxyJNI.vx_channel_t_capacity_get(this.swigCPtr, this)
    }

    String getChannel_desc() {
        return VxClientProxyJNI.vx_channel_t_channel_desc_get(this.swigCPtr, this)
    }

    Int getChannel_id() {
        return VxClientProxyJNI.vx_channel_t_channel_id_get(this.swigCPtr, this)
    }

    String getChannel_name() {
        return VxClientProxyJNI.vx_channel_t_channel_name_get(this.swigCPtr, this)
    }

    String getChannel_uri() {
        return VxClientProxyJNI.vx_channel_t_channel_uri_get(this.swigCPtr, this)
    }

    Int getClamping_dist() {
        return VxClientProxyJNI.vx_channel_t_clamping_dist_get(this.swigCPtr, this)
    }

    Int getDist_model() {
        return VxClientProxyJNI.vx_channel_t_dist_model_get(this.swigCPtr, this)
    }

    Int getEncrypt_audio() {
        return VxClientProxyJNI.vx_channel_t_encrypt_audio_get(this.swigCPtr, this)
    }

    String getHost() {
        return VxClientProxyJNI.vx_channel_t_host_get(this.swigCPtr, this)
    }

    Int getIs_persistent() {
        return VxClientProxyJNI.vx_channel_t_is_persistent_get(this.swigCPtr, this)
    }

    Int getIs_protected() {
        return VxClientProxyJNI.vx_channel_t_is_protected_get(this.swigCPtr, this)
    }

    Int getLimit() {
        return VxClientProxyJNI.vx_channel_t_limit_get(this.swigCPtr, this)
    }

    Double getMax_gain() {
        return VxClientProxyJNI.vx_channel_t_max_gain_get(this.swigCPtr, this)
    }

    Int getMax_range() {
        return VxClientProxyJNI.vx_channel_t_max_range_get(this.swigCPtr, this)
    }

    vx_channel_mode getMode() {
        return vx_channel_mode.swigToEnum(VxClientProxyJNI.vx_channel_t_mode_get(this.swigCPtr, this))
    }

    String getModified() {
        return VxClientProxyJNI.vx_channel_t_modified_get(this.swigCPtr, this)
    }

    String getOwner() {
        return VxClientProxyJNI.vx_channel_t_owner_get(this.swigCPtr, this)
    }

    String getOwner_display_name() {
        return VxClientProxyJNI.vx_channel_t_owner_display_name_get(this.swigCPtr, this)
    }

    String getOwner_user_name() {
        return VxClientProxyJNI.vx_channel_t_owner_user_name_get(this.swigCPtr, this)
    }

    Double getRoll_off() {
        return VxClientProxyJNI.vx_channel_t_roll_off_get(this.swigCPtr, this)
    }

    Int getSize() {
        return VxClientProxyJNI.vx_channel_t_size_get(this.swigCPtr, this)
    }

    Int getType() {
        return VxClientProxyJNI.vx_channel_t_type_get(this.swigCPtr, this)
    }

    Unit setActive_participants(Int n) {
        VxClientProxyJNI.vx_channel_t_active_participants_set(this.swigCPtr, this, n)
    }

    Unit setCapacity(Int n) {
        VxClientProxyJNI.vx_channel_t_capacity_set(this.swigCPtr, this, n)
    }

    Unit setChannel_desc(String string2) {
        VxClientProxyJNI.vx_channel_t_channel_desc_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_id(Int n) {
        VxClientProxyJNI.vx_channel_t_channel_id_set(this.swigCPtr, this, n)
    }

    Unit setChannel_name(String string2) {
        VxClientProxyJNI.vx_channel_t_channel_name_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_uri(String string2) {
        VxClientProxyJNI.vx_channel_t_channel_uri_set(this.swigCPtr, this, string2)
    }

    Unit setClamping_dist(Int n) {
        VxClientProxyJNI.vx_channel_t_clamping_dist_set(this.swigCPtr, this, n)
    }

    Unit setDist_model(Int n) {
        VxClientProxyJNI.vx_channel_t_dist_model_set(this.swigCPtr, this, n)
    }

    Unit setEncrypt_audio(Int n) {
        VxClientProxyJNI.vx_channel_t_encrypt_audio_set(this.swigCPtr, this, n)
    }

    Unit setHost(String string2) {
        VxClientProxyJNI.vx_channel_t_host_set(this.swigCPtr, this, string2)
    }

    Unit setIs_persistent(Int n) {
        VxClientProxyJNI.vx_channel_t_is_persistent_set(this.swigCPtr, this, n)
    }

    Unit setIs_protected(Int n) {
        VxClientProxyJNI.vx_channel_t_is_protected_set(this.swigCPtr, this, n)
    }

    Unit setLimit(Int n) {
        VxClientProxyJNI.vx_channel_t_limit_set(this.swigCPtr, this, n)
    }

    Unit setMax_gain(Double d) {
        VxClientProxyJNI.vx_channel_t_max_gain_set(this.swigCPtr, this, d)
    }

    Unit setMax_range(Int n) {
        VxClientProxyJNI.vx_channel_t_max_range_set(this.swigCPtr, this, n)
    }

    Unit setMode(vx_channel_mode vx_channel_mode2) {
        VxClientProxyJNI.vx_channel_t_mode_set(this.swigCPtr, this, vx_channel_mode2.swigValue())
    }

    Unit setModified(String string2) {
        VxClientProxyJNI.vx_channel_t_modified_set(this.swigCPtr, this, string2)
    }

    Unit setOwner(String string2) {
        VxClientProxyJNI.vx_channel_t_owner_set(this.swigCPtr, this, string2)
    }

    Unit setOwner_display_name(String string2) {
        VxClientProxyJNI.vx_channel_t_owner_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setOwner_user_name(String string2) {
        VxClientProxyJNI.vx_channel_t_owner_user_name_set(this.swigCPtr, this, string2)
    }

    Unit setRoll_off(Double d) {
        VxClientProxyJNI.vx_channel_t_roll_off_set(this.swigCPtr, this, d)
    }

    Unit setSize(Int n) {
        VxClientProxyJNI.vx_channel_t_size_set(this.swigCPtr, this, n)
    }

    Unit setType(Int n) {
        VxClientProxyJNI.vx_channel_t_type_set(this.swigCPtr, this, n)
    }
}

