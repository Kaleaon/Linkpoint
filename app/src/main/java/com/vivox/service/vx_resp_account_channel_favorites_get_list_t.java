/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_channel_favorite;
import com.vivox.service.SWIGTYPE_p_p_vx_channel_favorite_group;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_account_channel_favorites_get_list_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_account_channel_favorites_get_list_t() {
        this(VxClientProxyJNI.new_vx_resp_account_channel_favorites_get_list_t(), true);
    }

    protected vx_resp_account_channel_favorites_get_list_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_account_channel_favorites_get_list_t vx_resp_account_channel_favorites_get_list_t2) {
        if (vx_resp_account_channel_favorites_get_list_t2 != null) return vx_resp_account_channel_favorites_get_list_t2.swigCPtr;
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
    public vx_resp_base_t getBase() {
        long l = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    public int getFavorite_count() {
        return VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorite_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_channel_favorite getFavorites() {
        long l = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorites_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_channel_favorite(l, false);
        return null;
    }

    public int getGroup_count() {
        return VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_group_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_channel_favorite_group getGroups() {
        long l = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_groups_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_channel_favorite_group(l, false);
        return null;
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setFavorite_count(int n) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorite_count_set(this.swigCPtr, this, n);
    }

    public void setFavorites(SWIGTYPE_p_p_vx_channel_favorite sWIGTYPE_p_p_vx_channel_favorite) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_favorites_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_channel_favorite.getCPtr(sWIGTYPE_p_p_vx_channel_favorite));
    }

    public void setGroup_count(int n) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_group_count_set(this.swigCPtr, this, n);
    }

    public void setGroups(SWIGTYPE_p_p_vx_channel_favorite_group sWIGTYPE_p_p_vx_channel_favorite_group) {
        VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_groups_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_channel_favorite_group.getCPtr(sWIGTYPE_p_p_vx_channel_favorite_group));
    }
}

