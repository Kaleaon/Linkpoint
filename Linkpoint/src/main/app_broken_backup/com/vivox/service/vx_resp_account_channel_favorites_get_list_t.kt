/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_channel_favorite
import com.vivox.service.SWIGTYPE_p_p_vx_channel_favorite_group
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_account_channel_favorites_get_list_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_account_channel_favorites_get_list_t() {
        this(VxClientProxyJNI.new_vx_resp_account_channel_favorites_get_list_t(), true)
    }

    protected vx_resp_account_channel_favorites_get_list_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_account_channel_favorites_get_list_t vx_resp_account_channel_favorites_get_list_t2) {
        if (vx_resp_account_channel_favorites_get_list_t2 != null) return vx_resp_account_channel_favorites_get_list_t2.swigCPtr
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
    vx_resp_base_t getBase() {
        Long l = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    Int getFavorite_count() {
        return VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorite_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_channel_favorite getFavorites() {
        Long l = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorites_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_channel_favorite(l, false)
        return null
    }

    Int getGroup_count() {
        return VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_group_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_channel_favorite_group getGroups() {
        Long l = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_groups_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_channel_favorite_group(l, false)
        return null
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setFavorite_count(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorite_count_set(this.swigCPtr, this, n)
    }

    Unit setFavorites(SWIGTYPE_p_p_vx_channel_favorite sWIGTYPE_p_p_vx_channel_favorite) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorites_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_channel_favorite.getCPtr(sWIGTYPE_p_p_vx_channel_favorite))
    }

    Unit setGroup_count(Int n) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_group_count_set(this.swigCPtr, this, n)
    }

    Unit setGroups(SWIGTYPE_p_p_vx_channel_favorite_group sWIGTYPE_p_p_vx_channel_favorite_group) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_groups_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_channel_favorite_group.getCPtr(sWIGTYPE_p_p_vx_channel_favorite_group))
    }
}

