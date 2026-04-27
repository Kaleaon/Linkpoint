/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_auto_accept_rule_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_auto_accept_rule_t() {
        this(VxClientProxyJNI.new_vx_auto_accept_rule_t(), true)
    }

    protected vx_auto_accept_rule_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_auto_accept_rule_t vx_auto_accept_rule_t2) {
        if (vx_auto_accept_rule_t2 != null) return vx_auto_accept_rule_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_auto_accept_rule_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    String getAuto_accept_mask() {
        return VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_mask_get(this.swigCPtr, this)
    }

    String getAuto_accept_nickname() {
        return VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_nickname_get(this.swigCPtr, this)
    }

    Int getAuto_add_as_buddy() {
        return VxClientProxyJNI.vx_auto_accept_rule_t_auto_add_as_buddy_get(this.swigCPtr, this)
    }

    Unit setAuto_accept_mask(String string2) {
        VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_mask_set(this.swigCPtr, this, string2)
    }

    Unit setAuto_accept_nickname(String string2) {
        VxClientProxyJNI.vx_auto_accept_rule_t_auto_accept_nickname_set(this.swigCPtr, this, string2)
    }

    Unit setAuto_add_as_buddy(Int n) {
        VxClientProxyJNI.vx_auto_accept_rule_t_auto_add_as_buddy_set(this.swigCPtr, this, n)
    }
}

