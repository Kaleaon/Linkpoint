/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_channel_favorite_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_channel_favorite_t() {
        this(VxClientProxyJNI.new_vx_channel_favorite_t(), true)
    }

    protected vx_channel_favorite_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_channel_favorite_t vx_channel_favorite_t2) {
        if (vx_channel_favorite_t2 != null) return vx_channel_favorite_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_channel_favorite_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    Int getChannel_active_participants() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_active_participants_get(this.swigCPtr, this)
    }

    Int getChannel_capacity() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_capacity_get(this.swigCPtr, this)
    }

    String getChannel_description() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_description_get(this.swigCPtr, this)
    }

    Int getChannel_is_persistent() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_is_persistent_get(this.swigCPtr, this)
    }

    Int getChannel_is_protected() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_is_protected_get(this.swigCPtr, this)
    }

    Int getChannel_limit() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_limit_get(this.swigCPtr, this)
    }

    String getChannel_modified() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_modified_get(this.swigCPtr, this)
    }

    String getChannel_owner() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_owner_get(this.swigCPtr, this)
    }

    String getChannel_owner_display_name() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_owner_display_name_get(this.swigCPtr, this)
    }

    String getChannel_owner_user_name() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_owner_user_name_get(this.swigCPtr, this)
    }

    Int getChannel_size() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_size_get(this.swigCPtr, this)
    }

    String getChannel_uri() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_uri_get(this.swigCPtr, this)
    }

    String getFavorite_data() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_data_get(this.swigCPtr, this)
    }

    String getFavorite_display_name() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_display_name_get(this.swigCPtr, this)
    }

    Int getFavorite_group_id() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_group_id_get(this.swigCPtr, this)
    }

    Int getFavorite_id() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_id_get(this.swigCPtr, this)
    }

    Unit setChannel_active_participants(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_active_participants_set(this.swigCPtr, this, n)
    }

    Unit setChannel_capacity(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_capacity_set(this.swigCPtr, this, n)
    }

    Unit setChannel_description(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_description_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_is_persistent(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_is_persistent_set(this.swigCPtr, this, n)
    }

    Unit setChannel_is_protected(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_is_protected_set(this.swigCPtr, this, n)
    }

    Unit setChannel_limit(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_limit_set(this.swigCPtr, this, n)
    }

    Unit setChannel_modified(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_modified_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_owner(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_owner_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_owner_display_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_owner_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_owner_user_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_owner_user_name_set(this.swigCPtr, this, string2)
    }

    Unit setChannel_size(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_size_set(this.swigCPtr, this, n)
    }

    Unit setChannel_uri(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_uri_set(this.swigCPtr, this, string2)
    }

    Unit setFavorite_data(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_data_set(this.swigCPtr, this, string2)
    }

    Unit setFavorite_display_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setFavorite_group_id(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_group_id_set(this.swigCPtr, this, n)
    }

    Unit setFavorite_id(Int n) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_id_set(this.swigCPtr, this, n)
    }
}

