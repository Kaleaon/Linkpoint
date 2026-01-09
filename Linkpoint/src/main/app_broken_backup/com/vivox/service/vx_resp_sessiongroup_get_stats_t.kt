/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_sessiongroup_get_stats_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_sessiongroup_get_stats_t() {
        this(VxClientProxyJNI.new_vx_resp_sessiongroup_get_stats_t(), true)
    }

    protected vx_resp_sessiongroup_get_stats_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_sessiongroup_get_stats_t vx_resp_sessiongroup_get_stats_t2) {
        if (vx_resp_sessiongroup_get_stats_t2 != null) return vx_resp_sessiongroup_get_stats_t2.swigCPtr
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
    vx_resp_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    String getCall_id() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_call_id_get(this.swigCPtr, this)
    }

    Int getCurrent_bars() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_current_bars_get(this.swigCPtr, this)
    }

    Int getIncoming_discarded() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_discarded_get(this.swigCPtr, this)
    }

    Int getIncoming_expected() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_expected_get(this.swigCPtr, this)
    }

    Int getIncoming_out_of_time() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_get(this.swigCPtr, this)
    }

    Int getIncoming_packetloss() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_packetloss_get(this.swigCPtr, this)
    }

    Int getIncoming_received() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_received_get(this.swigCPtr, this)
    }

    Int getInsufficient_bandwidth() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_get(this.swigCPtr, this)
    }

    Int getMax_bars() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_max_bars_get(this.swigCPtr, this)
    }

    Int getMin_bars() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_min_bars_get(this.swigCPtr, this)
    }

    Int getOutgoing_sent() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_outgoing_sent_get(this.swigCPtr, this)
    }

    Int getPk_loss() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_pk_loss_get(this.swigCPtr, this)
    }

    Int getRender_device_errors() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_errors_get(this.swigCPtr, this)
    }

    Int getRender_device_overruns() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_overruns_get(this.swigCPtr, this)
    }

    Int getRender_device_underruns() {
        return VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_underruns_get(this.swigCPtr, this)
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setCall_id(String string2) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_call_id_set(this.swigCPtr, this, string2)
    }

    Unit setCurrent_bars(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_current_bars_set(this.swigCPtr, this, n)
    }

    Unit setIncoming_discarded(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_discarded_set(this.swigCPtr, this, n)
    }

    Unit setIncoming_expected(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_expected_set(this.swigCPtr, this, n)
    }

    Unit setIncoming_out_of_time(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_set(this.swigCPtr, this, n)
    }

    Unit setIncoming_packetloss(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_packetloss_set(this.swigCPtr, this, n)
    }

    Unit setIncoming_received(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_incoming_received_set(this.swigCPtr, this, n)
    }

    Unit setInsufficient_bandwidth(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_set(this.swigCPtr, this, n)
    }

    Unit setMax_bars(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_max_bars_set(this.swigCPtr, this, n)
    }

    Unit setMin_bars(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_min_bars_set(this.swigCPtr, this, n)
    }

    Unit setOutgoing_sent(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_outgoing_sent_set(this.swigCPtr, this, n)
    }

    Unit setPk_loss(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_pk_loss_set(this.swigCPtr, this, n)
    }

    Unit setRender_device_errors(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_errors_set(this.swigCPtr, this, n)
    }

    Unit setRender_device_overruns(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_overruns_set(this.swigCPtr, this, n)
    }

    Unit setRender_device_underruns(Int n) {
        VxClientProxyJNI.vx_resp_sessiongroup_get_stats_t_render_device_underruns_set(this.swigCPtr, this, n)
    }
}

