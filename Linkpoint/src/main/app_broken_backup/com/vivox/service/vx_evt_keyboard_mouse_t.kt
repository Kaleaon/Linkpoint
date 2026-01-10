/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t

class vx_evt_keyboard_mouse_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_keyboard_mouse_t() {
        this(VxClientProxyJNI.new_vx_evt_keyboard_mouse_t(), true)
    }

    protected vx_evt_keyboard_mouse_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_keyboard_mouse_t vx_evt_keyboard_mouse_t2) {
        if (vx_evt_keyboard_mouse_t2 != null) return vx_evt_keyboard_mouse_t2.swigCPtr
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
    vx_evt_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_evt_keyboard_mouse_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    Int getIs_down() {
        return VxClientProxyJNI.vx_evt_keyboard_mouse_t_is_down_get(this.swigCPtr, this)
    }

    String getName() {
        return VxClientProxyJNI.vx_evt_keyboard_mouse_t_name_get(this.swigCPtr, this)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_keyboard_mouse_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setIs_down(Int n) {
        VxClientProxyJNI.vx_evt_keyboard_mouse_t_is_down_set(this.swigCPtr, this, n)
    }

    Unit setName(String string2) {
        VxClientProxyJNI.vx_evt_keyboard_mouse_t_name_set(this.swigCPtr, this, string2)
    }
}

