/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_diagnostic_dump_level {
    vx_diagnostic_dump_level diagnostic_dump_level_all = vx_diagnostic_dump_level("diagnostic_dump_level_all", VxClientProxyJNI.diagnostic_dump_level_all_get())
    vx_diagnostic_dump_level diagnostic_dump_level_sessions = vx_diagnostic_dump_level("diagnostic_dump_level_sessions")
    private Int swigNext = 0
    private vx_diagnostic_dump_level[] swigValues = vx_diagnostic_dump_level[]{diagnostic_dump_level_all, diagnostic_dump_level_sessions}
    private String swigName
    private Int swigValue

    private vx_diagnostic_dump_level(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_diagnostic_dump_level(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_diagnostic_dump_level(String string2, vx_diagnostic_dump_level vx_diagnostic_dump_level2) {
        this.swigName = string2
        this.swigValue = vx_diagnostic_dump_level2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_diagnostic_dump_level swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_diagnostic_dump_level.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_diagnostic_dump_level.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_diagnostic_dump_level.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

