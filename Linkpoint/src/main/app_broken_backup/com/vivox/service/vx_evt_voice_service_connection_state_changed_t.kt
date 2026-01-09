/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_evt_base_t

class vx_evt_voice_service_connection_state_changed_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_evt_voice_service_connection_state_changed_t() {
        this(VxClientProxyJNI.new_vx_evt_voice_service_connection_state_changed_t(), true)
    }

    protected vx_evt_voice_service_connection_state_changed_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_evt_voice_service_connection_state_changed_t vx_evt_voice_service_connection_state_changed_t2) {
        if (vx_evt_voice_service_connection_state_changed_t2 != null) return vx_evt_voice_service_connection_state_changed_t2.swigCPtr
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
        var l: Long = VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_evt_base_t(l, false)
        return null
    }

    Int getConnected() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_connected_get(this.swigCPtr, this)
    }

    String getData_directory() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_data_directory_get(this.swigCPtr, this)
    }

    Int getNetwork_is_down() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_is_down_get(this.swigCPtr, this)
    }

    Int getNetwork_test_completed() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_test_completed_get(this.swigCPtr, this)
    }

    Int getNetwork_test_run() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_test_run_get(this.swigCPtr, this)
    }

    Int getNetwork_test_state() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_test_state_get(this.swigCPtr, this)
    }

    String getPlatform() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_platform_get(this.swigCPtr, this)
    }

    String getVersion() {
        return VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_version_get(this.swigCPtr, this)
    }

    Unit setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit setConnected(Int n) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_connected_set(this.swigCPtr, this, n)
    }

    Unit setData_directory(String string2) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_data_directory_set(this.swigCPtr, this, string2)
    }

    Unit setNetwork_is_down(Int n) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_is_down_set(this.swigCPtr, this, n)
    }

    Unit setNetwork_test_completed(Int n) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_test_completed_set(this.swigCPtr, this, n)
    }

    Unit setNetwork_test_run(Int n) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_test_run_set(this.swigCPtr, this, n)
    }

    Unit setNetwork_test_state(Int n) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_network_test_state_set(this.swigCPtr, this, n)
    }

    Unit setPlatform(String string2) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_platform_set(this.swigCPtr, this, string2)
    }

    Unit setVersion(String string2) {
        VxClientProxyJNI.vx_evt_voice_service_connection_state_changed_t_version_set(this.swigCPtr, this, string2)
    }
}

