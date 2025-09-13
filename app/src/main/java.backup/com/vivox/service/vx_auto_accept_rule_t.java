/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_auto_accept_rule_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_auto_accept_rule_t() {
        this(VxClientProxyJNI.new_vx_auto_accept_rule_t(), true);
    }

    protected vx_auto_accept_rule_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_auto_accept_rule_t vx_auto_accept_rule_t2) {
        if (vx_auto_accept_rule_t2 != null) return vx_auto_accept_rule_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_auto_accept_rule_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getAuto_accept_mask() {
        return VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_mask_get(this.swigCPtr, this);
    }

    public String getAuto_accept_nickname() {
        return VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_nickname_get(this.swigCPtr, this);
    }

    public int getAuto_add_as_buddy() {
        return VxClientProxyJNI.vx_auto_accept_rule_t_auto_add_as_buddy_get(this.swigCPtr, this);
    }

    public void setAuto_accept_mask(String string2) {
        VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_mask_set(this.swigCPtr, this, string2);
    }

    public void setAuto_accept_nickname(String string2) {
        VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_nickname_set(this.swigCPtr, this, string2);
    }

    public void setAuto_add_as_buddy(int n) {
        VxClientProxyJNI.vx_auto_accept_rule_t_auto_add_as_buddy_set(this.swigCPtr, this, n);
    }
}

