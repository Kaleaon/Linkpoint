/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_sessiongroup_get_stats_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_sessiongroup_get_stats_t() {
        this(VxClientProxyJNI.new_vx_resp_sessiongroup_get_stats_t(), true);
    }

    protected vx_resp_sessiongroup_get_stats_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_sessiongroup_get_stats_t vx_resp_sessiongroup_get_stats_t2) {
        if (vx_resp_sessiongroup_get_stats_t2 != null) return vx_resp_sessiongroup_get_stats_t2.swigCPtr;
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_resp_base_t getBase() {
        long l = VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    public String getCall_id() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_call_id_get(this.swigCPtr, this);
    }

    public int getCurrent_bars() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_current_bars_get(this.swigCPtr, this);
    }

    public int getIncoming_discarded() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_discarded_get(this.swigCPtr, this);
    }

    public int getIncoming_expected() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_expected_get(this.swigCPtr, this);
    }

    public int getIncoming_out_of_time() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_get(this.swigCPtr, this);
    }

    public int getIncoming_packetloss() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_packetloss_get(this.swigCPtr, this);
    }

    public int getIncoming_received() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_received_get(this.swigCPtr, this);
    }

    public int getInsufficient_bandwidth() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_get(this.swigCPtr, this);
    }

    public int getMax_bars() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_max_bars_get(this.swigCPtr, this);
    }

    public int getMin_bars() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_min_bars_get(this.swigCPtr, this);
    }

    public int getOutgoing_sent() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_outgoing_sent_get(this.swigCPtr, this);
    }

    public int getPk_loss() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_pk_loss_get(this.swigCPtr, this);
    }

    public int getRender_device_errors() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_errors_get(this.swigCPtr, this);
    }

    public int getRender_device_overruns() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_overruns_get(this.swigCPtr, this);
    }

    public int getRender_device_underruns() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_underruns_get(this.swigCPtr, this);
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setCall_id(String string2) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_call_id_set(this.swigCPtr, this, string2);
    }

    public void setCurrent_bars(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_current_bars_set(this.swigCPtr, this, n);
    }

    public void setIncoming_discarded(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_discarded_set(this.swigCPtr, this, n);
    }

    public void setIncoming_expected(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_expected_set(this.swigCPtr, this, n);
    }

    public void setIncoming_out_of_time(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_set(this.swigCPtr, this, n);
    }

    public void setIncoming_packetloss(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_packetloss_set(this.swigCPtr, this, n);
    }

    public void setIncoming_received(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_received_set(this.swigCPtr, this, n);
    }

    public void setInsufficient_bandwidth(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_set(this.swigCPtr, this, n);
    }

    public void setMax_bars(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_max_bars_set(this.swigCPtr, this, n);
    }

    public void setMin_bars(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_min_bars_set(this.swigCPtr, this, n);
    }

    public void setOutgoing_sent(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_outgoing_sent_set(this.swigCPtr, this, n);
    }

    public void setPk_loss(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_pk_loss_set(this.swigCPtr, this, n);
    }

    public void setRender_device_errors(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_errors_set(this.swigCPtr, this, n);
    }

    public void setRender_device_overruns(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_overruns_set(this.swigCPtr, this, n);
    }

    public void setRender_device_underruns(int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_underruns_set(this.swigCPtr, this, n);
    }
}

