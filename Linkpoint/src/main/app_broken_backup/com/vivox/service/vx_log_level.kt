/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_log_level {
    vx_log_level log_debug = vx_log_level("log_debug")
    vx_log_level log_error = vx_log_level("log_error", VxClientProxyJNI.log_error_get())
    vx_log_level log_info = vx_log_level("log_info")
    vx_log_level log_trace = vx_log_level("log_trace")
    vx_log_level log_warning = vx_log_level("log_warning")
    private Int swigNext = 0
    private vx_log_level[] swigValues = vx_log_level[]{log_error, log_warning, log_info, log_debug, log_trace}
    private String swigName
    private Int swigValue

    private vx_log_level(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_log_level(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_log_level(String string2, vx_log_level vx_log_level2) {
        this.swigName = string2
        this.swigValue = vx_log_level2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_log_level swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_log_level.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_log_level.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_log_level.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

