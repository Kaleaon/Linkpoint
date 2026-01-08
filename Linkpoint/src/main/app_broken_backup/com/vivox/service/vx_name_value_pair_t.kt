/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_name_value_pair_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_name_value_pair_t() {
        this(VxClientProxyJNI.new_vx_name_value_pair_t(), true)
    }

    protected vx_name_value_pair_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_name_value_pair_t vx_name_value_pair_t2) {
        if (vx_name_value_pair_t2 != null) return vx_name_value_pair_t2.swigCPtr
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

    String getName() {
        return VxClientProxyJNI.vx_name_value_pair_t_name_get(this.swigCPtr, this)
    }

    String getValue() {
        return VxClientProxyJNI.vx_name_value_pair_t_value_get(this.swigCPtr, this)
    }

    Unit setName(String string2) {
        VxClientProxyJNI.vx_name_value_pair_t_name_set(this.swigCPtr, this, string2)
    }

    Unit setValue(String string2) {
        VxClientProxyJNI.vx_name_value_pair_t_value_set(this.swigCPtr, this, string2)
    }
}

