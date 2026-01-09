/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_req_base_t

class vx_req_aux_connectivity_info_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_aux_connectivity_info_t() {
        this(VxClientProxyJNI.new_vx_req_aux_connectivity_info_t(), true)
    }

    protected vx_req_aux_connectivity_info_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_aux_connectivity_info_t vx_req_aux_connectivity_info_t2) {
        if (vx_req_aux_connectivity_info_t2 != null) return vx_req_aux_connectivity_info_t2.swigCPtr
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

    String getAcct_mgmt_server() {
        return VxClientProxyJNI.vx_req_aux_connectivity_info_t_acct_mgmt_server_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_req_aux_connectivity_info_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    Int getEcho_port() {
        return VxClientProxyJNI.vx_req_aux_connectivity_info_t_echo_port_get(this.swigCPtr, this)
    }

    String getEcho_server() {
        return VxClientProxyJNI.vx_req_aux_connectivity_info_t_echo_server_get(this.swigCPtr, this)
    }

    String getStun_server() {
        return VxClientProxyJNI.vx_req_aux_connectivity_info_t_stun_server_get(this.swigCPtr, this)
    }

    Int getTimeout() {
        return VxClientProxyJNI.vx_req_aux_connectivity_info_t_timeout_get(this.swigCPtr, this)
    }

    String getWell_known_ip() {
        return VxClientProxyJNI.vx_req_aux_connectivity_info_t_well_known_ip_get(this.swigCPtr, this)
    }

    Unit setAcct_mgmt_server(String string2) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_acct_mgmt_server_set(this.swigCPtr, this, string2)
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setEcho_port(Int n) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_echo_port_set(this.swigCPtr, this, n)
    }

    Unit setEcho_server(String string2) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_echo_server_set(this.swigCPtr, this, string2)
    }

    Unit setStun_server(String string2) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_stun_server_set(this.swigCPtr, this, string2)
    }

    Unit setTimeout(Int n) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_timeout_set(this.swigCPtr, this, n)
    }

    Unit setWell_known_ip(String string2) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_t_well_known_ip_set(this.swigCPtr, this, string2)
    }
}

