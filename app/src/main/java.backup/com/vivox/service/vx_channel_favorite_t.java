/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_channel_favorite_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_channel_favorite_t() {
        this(VxClientProxyJNI.new_vx_channel_favorite_t(), true);
    }

    protected vx_channel_favorite_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_channel_favorite_t vx_channel_favorite_t2) {
        if (vx_channel_favorite_t2 != null) return vx_channel_favorite_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_channel_favorite_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public int getChannel_active_participants() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_active_participants_get(this.swigCPtr, this);
    }

    public int getChannel_capacity() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_capacity_get(this.swigCPtr, this);
    }

    public String getChannel_description() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_description_get(this.swigCPtr, this);
    }

    public int getChannel_is_persistent() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_is_persistent_get(this.swigCPtr, this);
    }

    public int getChannel_is_protected() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_is_protected_get(this.swigCPtr, this);
    }

    public int getChannel_limit() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_limit_get(this.swigCPtr, this);
    }

    public String getChannel_modified() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_modified_get(this.swigCPtr, this);
    }

    public String getChannel_owner() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_owner_get(this.swigCPtr, this);
    }

    public String getChannel_owner_display_name() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_owner_display_name_get(this.swigCPtr, this);
    }

    public String getChannel_owner_user_name() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_owner_user_name_get(this.swigCPtr, this);
    }

    public int getChannel_size() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_size_get(this.swigCPtr, this);
    }

    public String getChannel_uri() {
        return VxClientProxyJNI.vx_channel_favorite_t_channel_uri_get(this.swigCPtr, this);
    }

    public String getFavorite_data() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_data_get(this.swigCPtr, this);
    }

    public String getFavorite_display_name() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_display_name_get(this.swigCPtr, this);
    }

    public int getFavorite_group_id() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_group_id_get(this.swigCPtr, this);
    }

    public int getFavorite_id() {
        return VxClientProxyJNI.vx_channel_favorite_t_favorite_id_get(this.swigCPtr, this);
    }

    public void setChannel_active_participants(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_active_participants_set(this.swigCPtr, this, n);
    }

    public void setChannel_capacity(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_capacity_set(this.swigCPtr, this, n);
    }

    public void setChannel_description(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_description_set(this.swigCPtr, this, string2);
    }

    public void setChannel_is_persistent(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_is_persistent_set(this.swigCPtr, this, n);
    }

    public void setChannel_is_protected(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_is_protected_set(this.swigCPtr, this, n);
    }

    public void setChannel_limit(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_limit_set(this.swigCPtr, this, n);
    }

    public void setChannel_modified(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_modified_set(this.swigCPtr, this, string2);
    }

    public void setChannel_owner(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_owner_set(this.swigCPtr, this, string2);
    }

    public void setChannel_owner_display_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_owner_display_name_set(this.swigCPtr, this, string2);
    }

    public void setChannel_owner_user_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_owner_user_name_set(this.swigCPtr, this, string2);
    }

    public void setChannel_size(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_size_set(this.swigCPtr, this, n);
    }

    public void setChannel_uri(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_channel_uri_set(this.swigCPtr, this, string2);
    }

    public void setFavorite_data(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_data_set(this.swigCPtr, this, string2);
    }

    public void setFavorite_display_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_display_name_set(this.swigCPtr, this, string2);
    }

    public void setFavorite_group_id(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_group_id_set(this.swigCPtr, this, n);
    }

    public void setFavorite_id(int n) {
        VxClientProxyJNI.vx_channel_favorite_t_favorite_id_set(this.swigCPtr, this, n);
    }
}

