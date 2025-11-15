/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_stat_thread_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_stat_thread_t() {
        this(VxClientProxyJNI.new_vx_stat_thread_t(), true)
    }

    protected vx_stat_thread_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_stat_thread_t vx_stat_thread_t2) {
        if (vx_stat_thread_t2 != null) return vx_stat_thread_t2.swigCPtr
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

    Int getCount_poll_gte_25ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_gte_25ms_get(this.swigCPtr, this)
    }

    Int getCount_poll_lt_10ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_10ms_get(this.swigCPtr, this)
    }

    Int getCount_poll_lt_16ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_16ms_get(this.swigCPtr, this)
    }

    Int getCount_poll_lt_1ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_1ms_get(this.swigCPtr, this)
    }

    Int getCount_poll_lt_20ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_20ms_get(this.swigCPtr, this)
    }

    Int getCount_poll_lt_25ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_25ms_get(this.swigCPtr, this)
    }

    Int getCount_poll_lt_5ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_5ms_get(this.swigCPtr, this)
    }

    Int getInterval() {
        return VxClientProxyJNI.vx_stat_thread_t_interval_get(this.swigCPtr, this)
    }

    Unit setCount_poll_gte_25ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_gte_25ms_set(this.swigCPtr, this, n)
    }

    Unit setCount_poll_lt_10ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_10ms_set(this.swigCPtr, this, n)
    }

    Unit setCount_poll_lt_16ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_16ms_set(this.swigCPtr, this, n)
    }

    Unit setCount_poll_lt_1ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_1ms_set(this.swigCPtr, this, n)
    }

    Unit setCount_poll_lt_20ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_20ms_set(this.swigCPtr, this, n)
    }

    Unit setCount_poll_lt_25ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_25ms_set(this.swigCPtr, this, n)
    }

    Unit setCount_poll_lt_5ms(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_5ms_set(this.swigCPtr, this, n)
    }

    Unit setInterval(Int n) {
        VxClientProxyJNI.vx_stat_thread_t_interval_set(this.swigCPtr, this, n)
    }
}

