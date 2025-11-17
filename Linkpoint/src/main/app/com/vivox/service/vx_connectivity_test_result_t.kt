/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.ND_ERROR
import com.vivox.service.ND_TEST_TYPE
import com.vivox.service.VxClientProxyJNI

class vx_connectivity_test_result_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_connectivity_test_result_t() {
        this(VxClientProxyJNI.new_vx_connectivity_test_result_t(), true)
    }

    protected vx_connectivity_test_result_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_connectivity_test_result_t vx_connectivity_test_result_t2) {
        if (vx_connectivity_test_result_t2 != null) return vx_connectivity_test_result_t2.swigCPtr
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

    String getTest_additional_info() {
        return VxClientProxyJNI.vx_connectivity_test_result_t_test_additional_info_get(this.swigCPtr, this)
    }

    ND_ERROR getTest_error_code() {
        return ND_ERROR.swigToEnum(VxClientProxyJNI.vx_connectivity_test_result_t_test_error_code_get(this.swigCPtr, this))
    }

    ND_TEST_TYPE getTest_type() {
        return ND_TEST_TYPE.swigToEnum(VxClientProxyJNI.vx_connectivity_test_result_t_test_type_get(this.swigCPtr, this))
    }

    Unit setTest_additional_info(String string2) {
        VxClientProxyJNI.vx_connectivity_test_result_t_test_additional_info_set(this.swigCPtr, this, string2)
    }

    Unit setTest_error_code(ND_ERROR nD_ERROR) {
        VxClientProxyJNI.vx_connectivity_test_result_t_test_error_code_set(this.swigCPtr, this, nD_ERROR.swigValue())
    }

    Unit setTest_type(ND_TEST_TYPE nD_TEST_TYPE) {
        VxClientProxyJNI.vx_connectivity_test_result_t_test_type_set(this.swigCPtr, this, nD_TEST_TYPE.swigValue())
    }
}

