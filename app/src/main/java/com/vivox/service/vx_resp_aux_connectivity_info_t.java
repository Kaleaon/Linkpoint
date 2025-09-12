/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_connectivity_test_result;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_resp_base_t;

public class vx_resp_aux_connectivity_info_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_resp_aux_connectivity_info_t() {
        this(VxClientProxyJNI.new_vx_resp_aux_connectivity_info_t(), true);
    }

    protected vx_resp_aux_connectivity_info_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_resp_aux_connectivity_info_t vx_resp_aux_connectivity_info_t2) {
        if (vx_resp_aux_connectivity_info_t2 != null) return vx_resp_aux_connectivity_info_t2.swigCPtr;
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
        long l = VxClientProxyJNI.vx_resp_aux_connectivity_info_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_resp_base_t(l, false);
        return null;
    }

    public int getCount() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_count_get(this.swigCPtr, this);
    }

    public int getEcho_port() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_echo_port_get(this.swigCPtr, this);
    }

    public String getEcho_server() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_echo_server_get(this.swigCPtr, this);
    }

    public int getFirst_sip_port() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_first_sip_port_get(this.swigCPtr, this);
    }

    public int getRtcp_port() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_rtcp_port_get(this.swigCPtr, this);
    }

    public int getRtp_port() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_rtp_port_get(this.swigCPtr, this);
    }

    public int getSecond_sip_port() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_second_sip_port_get(this.swigCPtr, this);
    }

    public String getStun_server() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_stun_server_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_connectivity_test_result getTest_results() {
        long l = VxClientProxyJNI.vx_resp_aux_connectivity_info_t_test_results_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_connectivity_test_result(l, false);
        return null;
    }

    public int getTimeout() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_timeout_get(this.swigCPtr, this);
    }

    public String getWell_known_ip() {
        return VxClientProxyJNI.vx_resp_aux_connectivity_info_t_well_known_ip_get(this.swigCPtr, this);
    }

    public void setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2);
    }

    public void setCount(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_count_set(this.swigCPtr, this, n);
    }

    public void setEcho_port(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_echo_port_set(this.swigCPtr, this, n);
    }

    public void setEcho_server(String string2) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_echo_server_set(this.swigCPtr, this, string2);
    }

    public void setFirst_sip_port(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_first_sip_port_set(this.swigCPtr, this, n);
    }

    public void setRtcp_port(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_rtcp_port_set(this.swigCPtr, this, n);
    }

    public void setRtp_port(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_rtp_port_set(this.swigCPtr, this, n);
    }

    public void setSecond_sip_port(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_second_sip_port_set(this.swigCPtr, this, n);
    }

    public void setStun_server(String string2) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_stun_server_set(this.swigCPtr, this, string2);
    }

    public void setTest_results(SWIGTYPE_p_p_vx_connectivity_test_result sWIGTYPE_p_p_vx_connectivity_test_result) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_test_results_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_connectivity_test_result.getCPtr(sWIGTYPE_p_p_vx_connectivity_test_result));
    }

    public void setTimeout(int n) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_timeout_set(this.swigCPtr, this, n);
    }

    public void setWell_known_ip(String string2) {
        VxClientProxyJNI.vx_resp_aux_connectivity_info_t_well_known_ip_set(this.swigCPtr, this, string2);
    }
}

