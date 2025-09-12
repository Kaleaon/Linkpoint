/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_buddy_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_buddy_t() {
        this(VxClientProxyJNI.new_vx_buddy_t(), true);
    }

    protected vx_buddy_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_buddy_t vx_buddy_t2) {
        if (vx_buddy_t2 != null) return vx_buddy_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_buddy_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public int getAccount_id() {
        return VxClientProxyJNI.vx_buddy_t_account_id_get(this.swigCPtr, this);
    }

    public String getAccount_name() {
        return VxClientProxyJNI.vx_buddy_t_account_name_get(this.swigCPtr, this);
    }

    public String getBuddy_data() {
        return VxClientProxyJNI.vx_buddy_t_buddy_data_get(this.swigCPtr, this);
    }

    public String getBuddy_uri() {
        return VxClientProxyJNI.vx_buddy_t_buddy_uri_get(this.swigCPtr, this);
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_buddy_t_display_name_get(this.swigCPtr, this);
    }

    public int getParent_group_id() {
        return VxClientProxyJNI.vx_buddy_t_parent_group_id_get(this.swigCPtr, this);
    }

    public void setAccount_id(int n) {
        VxClientProxyJNI.vx_buddy_t_account_id_set(this.swigCPtr, this, n);
    }

    public void setAccount_name(String string2) {
        VxClientProxyJNI.vx_buddy_t_account_name_set(this.swigCPtr, this, string2);
    }

    public void setBuddy_data(String string2) {
        VxClientProxyJNI.vx_buddy_t_buddy_data_set(this.swigCPtr, this, string2);
    }

    public void setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_buddy_t_buddy_uri_set(this.swigCPtr, this, string2);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_buddy_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setParent_group_id(int n) {
        VxClientProxyJNI.vx_buddy_t_parent_group_id_set(this.swigCPtr, this, n);
    }
}

