/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_session_handle_type {
    vx_session_handle_type session_handle_type_heirarchical_numeric = vx_session_handle_type("session_handle_type_heirarchical_numeric")
    vx_session_handle_type session_handle_type_heirarchical_unique = vx_session_handle_type("session_handle_type_heirarchical_unique")
    vx_session_handle_type session_handle_type_legacy = vx_session_handle_type("session_handle_type_legacy")
    vx_session_handle_type session_handle_type_unique = vx_session_handle_type("session_handle_type_unique", VxClientProxyJNI.session_handle_type_unique_get())
    private Int swigNext = 0
    private vx_session_handle_type[] swigValues = vx_session_handle_type[]{session_handle_type_unique, session_handle_type_legacy, session_handle_type_heirarchical_numeric, session_handle_type_heirarchical_unique}
    private String swigName
    private Int swigValue

    private vx_session_handle_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_session_handle_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_session_handle_type(String string2, vx_session_handle_type vx_session_handle_type2) {
        this.swigName = string2
        this.swigValue = vx_session_handle_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_session_handle_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_session_handle_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_session_handle_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_session_handle_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

