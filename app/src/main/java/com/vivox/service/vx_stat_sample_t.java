/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_stat_sample_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_stat_sample_t() {
        this(VxClientProxyJNI.new_vx_stat_sample_t(), true);
    }

    protected vx_stat_sample_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_stat_sample_t vx_stat_sample_t2) {
        if (vx_stat_sample_t2 != null) return vx_stat_sample_t2.swigCPtr;
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

    public double getLast() {
        return VxClientProxyJNI.vx_stat_sample_t_last_get(this.swigCPtr, this);
    }

    public double getMax() {
        return VxClientProxyJNI.vx_stat_sample_t_max_get(this.swigCPtr, this);
    }

    public double getMean() {
        return VxClientProxyJNI.vx_stat_sample_t_mean_get(this.swigCPtr, this);
    }

    public double getMin() {
        return VxClientProxyJNI.vx_stat_sample_t_min_get(this.swigCPtr, this);
    }

    public double getSample_count() {
        return VxClientProxyJNI.vx_stat_sample_t_sample_count_get(this.swigCPtr, this);
    }

    public double getStddev() {
        return VxClientProxyJNI.vx_stat_sample_t_stddev_get(this.swigCPtr, this);
    }

    public double getSum() {
        return VxClientProxyJNI.vx_stat_sample_t_sum_get(this.swigCPtr, this);
    }

    public double getSum_of_squares() {
        return VxClientProxyJNI.vx_stat_sample_t_sum_of_squares_get(this.swigCPtr, this);
    }

    public void setLast(double d) {
        VxClientProxyJNI.vx_stat_sample_t_last_set(this.swigCPtr, this, d);
    }

    public void setMax(double d) {
        VxClientProxyJNI.vx_stat_sample_t_max_set(this.swigCPtr, this, d);
    }

    public void setMean(double d) {
        VxClientProxyJNI.vx_stat_sample_t_mean_set(this.swigCPtr, this, d);
    }

    public void setMin(double d) {
        VxClientProxyJNI.vx_stat_sample_t_min_set(this.swigCPtr, this, d);
    }

    public void setSample_count(double d) {
        VxClientProxyJNI.vx_stat_sample_t_sample_count_set(this.swigCPtr, this, d);
    }

    public void setStddev(double d) {
        VxClientProxyJNI.vx_stat_sample_t_stddev_set(this.swigCPtr, this, d);
    }

    public void setSum(double d) {
        VxClientProxyJNI.vx_stat_sample_t_sum_set(this.swigCPtr, this, d);
    }

    public void setSum_of_squares(double d) {
        VxClientProxyJNI.vx_stat_sample_t_sum_of_squares_set(this.swigCPtr, this, d);
    }
}

