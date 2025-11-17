/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_channel_favorite_group_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_channel_favorite_group_t() {
        this(VxClientProxyJNI.new_vx_channel_favorite_group_t(), true)
    }

    protected vx_channel_favorite_group_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_channel_favorite_group_t vx_channel_favorite_group_t2) {
        if (vx_channel_favorite_group_t2 != null) return vx_channel_favorite_group_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_channel_favorite_group_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    String getFavorite_group_data() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_data_get(this.swigCPtr, this)
    }

    Int getFavorite_group_id() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_id_get(this.swigCPtr, this)
    }

    String getFavorite_group_modified() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_modified_get(this.swigCPtr, this)
    }

    String getFavorite_group_name() {
        return VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_name_get(this.swigCPtr, this)
    }

    Unit setFavorite_group_data(String string2) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_data_set(this.swigCPtr, this, string2)
    }

    Unit setFavorite_group_id(Int n) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_id_set(this.swigCPtr, this, n)
    }

    Unit setFavorite_group_modified(String string2) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_modified_set(this.swigCPtr, this, string2)
    }

    Unit setFavorite_group_name(String string2) {
        VxClientProxyJNI.vx_channel_favorite_group_t_favorite_group_name_set(this.swigCPtr, this, string2)
    }
}

