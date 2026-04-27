/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_buddy;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_account_buddy_search_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_account_buddy_search_t() {
        this(VxClientProxyJNI.new_vx_resp_account_buddy_search_t(), true);
    }

    protected vx_resp_account_buddy_search_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_account_buddy_search_t vx_resp_account_buddy_search_t2) {
        if (vx_resp_account_buddy_search_t2 != null) return vx_resp_account_buddy_search_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_resp_account_buddy_search_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_buddy getBuddies() {
        long l = VxClientProxyJNI.vx_resp_account_buddy_search_t_buddies_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_buddy(l, false);
        return null;
    }

    public int getBuddy_count() {
        return VxClientProxyJNI.vx_resp_account_buddy_search_t_buddy_count_get(this.swigCPtr, this);
    }

    public int getFrom() {
        return VxClientProxyJNI.vx_resp_account_buddy_search_t_from_get(this.swigCPtr, this);
    }

    public int getPage() {
        return VxClientProxyJNI.vx_resp_account_buddy_search_t_page_get(this.swigCPtr, this);
    }

    public int getTo() {
        return VxClientProxyJNI.vx_resp_account_buddy_search_t_to_get(this.swigCPtr, this);
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_buddy_search_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setBuddies(SWIGTYPE_p_p_vx_buddy sWIGTYPE_p_p_vx_buddy) {
        VxClientProxyJNI.vx_resp_account_buddy_search_t_buddies_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_buddy.getCPtr(sWIGTYPE_p_p_vx_buddy));
    }

    public void setBuddy_count(int n) {
        VxClientProxyJNI.vx_resp_account_buddy_search_t_buddy_count_set(this.swigCPtr, this, n);
    }

    public void setFrom(int n) {
        VxClientProxyJNI.vx_resp_account_buddy_search_t_from_set(this.swigCPtr, this, n);
    }

    public void setPage(int n) {
        VxClientProxyJNI.vx_resp_account_buddy_search_t_page_set(this.swigCPtr, this, n);
    }

    public void setTo(int n) {
        VxClientProxyJNI.vx_resp_account_buddy_search_t_to_set(this.swigCPtr, this, n);
    }
}

