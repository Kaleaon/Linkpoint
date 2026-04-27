/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_block_rule_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_block_rule_t() {
        this(VxClientProxyJNI.new_vx_block_rule_t(), true);
    }

    protected vx_block_rule_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_block_rule_t vx_block_rule_t2) {
        if (vx_block_rule_t2 != null) return vx_block_rule_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_block_rule_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getBlock_mask() {
        return VxClientProxyJNI.vx_block_rule_t_block_mask_get(this.swigCPtr, this);
    }

    public int getPresence_only() {
        return VxClientProxyJNI.vx_block_rule_t_presence_only_get(this.swigCPtr, this);
    }

    public void setBlock_mask(String string2) {
        VxClientProxyJNI.vx_block_rule_t_block_mask_set(this.swigCPtr, this, string2);
    }

    public void setPresence_only(int n) {
        VxClientProxyJNI.vx_block_rule_t_presence_only_set(this.swigCPtr, this, n);
    }
}

