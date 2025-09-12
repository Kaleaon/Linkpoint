/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_channel_favorite_group_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_channel_favorite_group_t() {
        this(VxClientProxyJNI.new_vx_channel_favorite_group_t(), true);
    }

    protected vx_channel_favorite_group_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_channel_favorite_group_t vx_channel_favorite_group_t2) {
        if (vx_channel_favorite_group_t2 != null) return vx_channel_favorite_group_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_channel_favorite_group_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getFavorite_group_data() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_data_get(this.swigCPtr, this);
    }

    public int getFavorite_group_id() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_id_get(this.swigCPtr, this);
    }

    public String getFavorite_group_modified() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_modified_get(this.swigCPtr, this);
    }

    public String getFavorite_group_name() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_name_get(this.swigCPtr, this);
    }

    public void setFavorite_group_data(String string2) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_data_set(this.swigCPtr, this, string2);
    }

    public void setFavorite_group_id(int n) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_id_set(this.swigCPtr, this, n);
    }

    public void setFavorite_group_modified(String string2) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_modified_set(this.swigCPtr, this, string2);
    }

    public void setFavorite_group_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_name_set(this.swigCPtr, this, string2);
    }
}

