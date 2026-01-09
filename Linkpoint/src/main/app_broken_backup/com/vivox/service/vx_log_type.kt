/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_log_type {
    vx_log_type log_to_callback = vx_log_type("log_to_callback")
    vx_log_type log_to_file = vx_log_type("log_to_file")
    vx_log_type log_to_file_and_callback = vx_log_type("log_to_file_and_callback")
    vx_log_type log_to_none = vx_log_type("log_to_none", VxClientProxyJNI.log_to_none_get())
    private Int swigNext = 0
    private vx_log_type[] swigValues = vx_log_type[]{log_to_none, log_to_file, log_to_callback, log_to_file_and_callback}
    private String swigName
    private Int swigValue

    private vx_log_type(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_log_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_log_type(String string2, vx_log_type vx_log_type2) {
        this.swigName = string2
        this.swigValue = vx_log_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_log_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_log_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_log_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_log_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

