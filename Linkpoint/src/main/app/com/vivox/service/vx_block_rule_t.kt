/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_block_rule_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_block_rule_t() {
        this(VxClientProxyJNI.new_vx_block_rule_t(), true)
    }

    protected vx_block_rule_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_block_rule_t vx_block_rule_t2) {
        if (vx_block_rule_t2 != null) return vx_block_rule_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_block_rule_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    String getBlock_mask() {
        return VxClientProxyJNI.vx_block_rule_t_block_mask_get(this.swigCPtr, this)
    }

    Int getPresence_only() {
        return VxClientProxyJNI.vx_block_rule_t_presence_only_get(this.swigCPtr, this)
    }

    Unit setBlock_mask(String string2) {
        VxClientProxyJNI.vx_block_rule_t_block_mask_set(this.swigCPtr, this, string2)
    }

    Unit setPresence_only(Int n) {
        VxClientProxyJNI.vx_block_rule_t_presence_only_set(this.swigCPtr, this, n)
    }
}

