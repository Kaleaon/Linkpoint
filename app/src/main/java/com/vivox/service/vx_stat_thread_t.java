/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_stat_thread_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_stat_thread_t() {
        this(VxClientProxyJNI.new_vx_stat_thread_t(), true);
    }

    protected vx_stat_thread_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_stat_thread_t vx_stat_thread_t2) {
        if (vx_stat_thread_t2 != null) return vx_stat_thread_t2.swigCPtr;
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

    public int getCount_poll_gte_25ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_gte_25ms_get(this.swigCPtr, this);
    }

    public int getCount_poll_lt_10ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_10ms_get(this.swigCPtr, this);
    }

    public int getCount_poll_lt_16ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_16ms_get(this.swigCPtr, this);
    }

    public int getCount_poll_lt_1ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_1ms_get(this.swigCPtr, this);
    }

    public int getCount_poll_lt_20ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_20ms_get(this.swigCPtr, this);
    }

    public int getCount_poll_lt_25ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_25ms_get(this.swigCPtr, this);
    }

    public int getCount_poll_lt_5ms() {
        return VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_5ms_get(this.swigCPtr, this);
    }

    public int getInterval() {
        return VxClientProxyJNI.vx_stat_thread_t_interval_get(this.swigCPtr, this);
    }

    public void setCount_poll_gte_25ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_gte_25ms_set(this.swigCPtr, this, n);
    }

    public void setCount_poll_lt_10ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_10ms_set(this.swigCPtr, this, n);
    }

    public void setCount_poll_lt_16ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_16ms_set(this.swigCPtr, this, n);
    }

    public void setCount_poll_lt_1ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_1ms_set(this.swigCPtr, this, n);
    }

    public void setCount_poll_lt_20ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_20ms_set(this.swigCPtr, this, n);
    }

    public void setCount_poll_lt_25ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_25ms_set(this.swigCPtr, this, n);
    }

    public void setCount_poll_lt_5ms(int n) {
        VxClientProxyJNI.vx_stat_thread_t_count_poll_lt_5ms_set(this.swigCPtr, this, n);
    }

    public void setInterval(int n) {
        VxClientProxyJNI.vx_stat_thread_t_interval_set(this.swigCPtr, this, n);
    }
}

