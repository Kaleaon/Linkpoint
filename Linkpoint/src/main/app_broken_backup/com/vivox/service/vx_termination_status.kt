/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_termination_status {
    private Int swigNext = 0
    private vx_termination_status[] swigValues
    vx_termination_status termination_status_busy
    vx_termination_status termination_status_decline
    vx_termination_status termination_status_none
    private String swigName
    private Int swigValue

    {
        termination_status_none = vx_termination_status("termination_status_none", VxClientProxyJNI.termination_status_none_get())
        termination_status_busy = vx_termination_status("termination_status_busy")
        termination_status_decline = vx_termination_status("termination_status_decline")
        swigValues = vx_termination_status[]{termination_status_none, termination_status_busy, termination_status_decline}
    }

    private vx_termination_status(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_termination_status(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_termination_status(String string2, vx_termination_status vx_termination_status2) {
        this.swigName = string2
        this.swigValue = vx_termination_status2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_termination_status swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_termination_status.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_termination_status.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_termination_status.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

