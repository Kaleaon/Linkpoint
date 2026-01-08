/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_stat_sample_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_stat_sample_t() {
        this(VxClientProxyJNI.new_vx_stat_sample_t(), true)
    }

    protected vx_stat_sample_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_stat_sample_t vx_stat_sample_t2) {
        if (vx_stat_sample_t2 != null) return vx_stat_sample_t2.swigCPtr
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

    Double getLast() {
        return VxClientProxyJNI.vx_stat_sample_t_last_get(this.swigCPtr, this)
    }

    Double getMax() {
        return VxClientProxyJNI.vx_stat_sample_t_max_get(this.swigCPtr, this)
    }

    Double getMean() {
        return VxClientProxyJNI.vx_stat_sample_t_mean_get(this.swigCPtr, this)
    }

    Double getMin() {
        return VxClientProxyJNI.vx_stat_sample_t_min_get(this.swigCPtr, this)
    }

    Double getSample_count() {
        return VxClientProxyJNI.vx_stat_sample_t_sample_count_get(this.swigCPtr, this)
    }

    Double getStddev() {
        return VxClientProxyJNI.vx_stat_sample_t_stddev_get(this.swigCPtr, this)
    }

    Double getSum() {
        return VxClientProxyJNI.vx_stat_sample_t_sum_get(this.swigCPtr, this)
    }

    Double getSum_of_squares() {
        return VxClientProxyJNI.vx_stat_sample_t_sum_of_squares_get(this.swigCPtr, this)
    }

    Unit setLast(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_last_set(this.swigCPtr, this, d)
    }

    Unit setMax(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_max_set(this.swigCPtr, this, d)
    }

    Unit setMean(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_mean_set(this.swigCPtr, this, d)
    }

    Unit setMin(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_min_set(this.swigCPtr, this, d)
    }

    Unit setSample_count(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_sample_count_set(this.swigCPtr, this, d)
    }

    Unit setStddev(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_stddev_set(this.swigCPtr, this, d)
    }

    Unit setSum(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_sum_set(this.swigCPtr, this, d)
    }

    Unit setSum_of_squares(Double d) {
        VxClientProxyJNI.vx_stat_sample_t_sum_of_squares_set(this.swigCPtr, this, d)
    }
}

