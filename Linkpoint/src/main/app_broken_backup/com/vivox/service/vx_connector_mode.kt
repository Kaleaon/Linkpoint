/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_connector_mode {
    vx_connector_mode connector_mode_legacy = vx_connector_mode("connector_mode_legacy")
    vx_connector_mode connector_mode_normal = vx_connector_mode("connector_mode_normal", VxClientProxyJNI.connector_mode_normal_get())
    private Int swigNext = 0
    private vx_connector_mode[] swigValues = vx_connector_mode[]{connector_mode_normal, connector_mode_legacy}
    private String swigName
    private Int swigValue

    private vx_connector_mode(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_connector_mode(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_connector_mode(String string2, vx_connector_mode vx_connector_mode2) {
        this.swigName = string2
        this.swigValue = vx_connector_mode2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_connector_mode swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_connector_mode.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_connector_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_connector_mode.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

