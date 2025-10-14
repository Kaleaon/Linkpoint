/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_stat_sample_t
import com.vivox.service.vx_stat_thread_t

class vx_system_stats_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_system_stats_t() {
        this(VxClientProxyJNI.new_vx_system_stats_t(), true)
    }

    protected vx_system_stats_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_system_stats_t vx_system_stats_t2) {
        if (vx_system_stats_t2 != null) return vx_system_stats_t2.swigCPtr
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

    Int getAr_source_count() {
        return VxClientProxyJNI.vx_system_stats_t_ar_source_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_stat_sample_t getAr_source_free_buffers() {
        Long l = VxClientProxyJNI.vx_system_stats_t_ar_source_free_buffers_get(this.swigCPtr, this)
        if (l != 0L) return vx_stat_sample_t(l, false)
        return null
    }

    Int getAr_source_poll_count() {
        return VxClientProxyJNI.vx_system_stats_t_ar_source_poll_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_stat_sample_t getAr_source_queue_depth() {
        Long l = VxClientProxyJNI.vx_system_stats_t_ar_source_queue_depth_get(this.swigCPtr, this)
        if (l != 0L) return vx_stat_sample_t(l, false)
        return null
    }

    Int getAr_source_queue_limit() {
        return VxClientProxyJNI.vx_system_stats_t_ar_source_queue_limit_get(this.swigCPtr, this)
    }

    Int getAr_source_queue_overflows() {
        return VxClientProxyJNI.vx_system_stats_t_ar_source_queue_overflows_get(this.swigCPtr, this)
    }

    Int getSs_size() {
        return VxClientProxyJNI.vx_system_stats_t_ss_size_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_stat_thread_t getTicker_thread() {
        Long l = VxClientProxyJNI.vx_system_stats_t_ticker_thread_get(this.swigCPtr, this)
        if (l != 0L) return vx_stat_thread_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_stat_thread_t getVp_thread() {
        Long l = VxClientProxyJNI.vx_system_stats_t_vp_thread_get(this.swigCPtr, this)
        if (l != 0L) return vx_stat_thread_t(l, false)
        return null
    }

    Unit setAr_source_count(Int n) {
        VxClientProxyJNI.vx_system_stats_t_ar_source_count_set(this.swigCPtr, this, n)
    }

    Unit setAr_source_free_buffers(vx_stat_sample_t vx_stat_sample_t2) {
        VxClientProxyJNI.vx_system_stats_t_ar_source_free_buffers_set(this.swigCPtr, this, vx_stat_sample_t.getCPtr(vx_stat_sample_t2), vx_stat_sample_t2)
    }

    Unit setAr_source_poll_count(Int n) {
        VxClientProxyJNI.vx_system_stats_t_ar_source_poll_count_set(this.swigCPtr, this, n)
    }

    Unit setAr_source_queue_depth(vx_stat_sample_t vx_stat_sample_t2) {
        VxClientProxyJNI.vx_system_stats_t_ar_source_queue_depth_set(this.swigCPtr, this, vx_stat_sample_t.getCPtr(vx_stat_sample_t2), vx_stat_sample_t2)
    }

    Unit setAr_source_queue_limit(Int n) {
        VxClientProxyJNI.vx_system_stats_t_ar_source_queue_limit_set(this.swigCPtr, this, n)
    }

    Unit setAr_source_queue_overflows(Int n) {
        VxClientProxyJNI.vx_system_stats_t_ar_source_queue_overflows_set(this.swigCPtr, this, n)
    }

    Unit setSs_size(Int n) {
        VxClientProxyJNI.vx_system_stats_t_ss_size_set(this.swigCPtr, this, n)
    }

    Unit setTicker_thread(vx_stat_thread_t vx_stat_thread_t2) {
        VxClientProxyJNI.vx_system_stats_t_ticker_thread_set(this.swigCPtr, this, vx_stat_thread_t.getCPtr(vx_stat_thread_t2), vx_stat_thread_t2)
    }

    Unit setVp_thread(vx_stat_thread_t vx_stat_thread_t2) {
        VxClientProxyJNI.vx_system_stats_t_vp_thread_set(this.swigCPtr, this, vx_stat_thread_t.getCPtr(vx_stat_thread_t2), vx_stat_thread_t2)
    }
}

