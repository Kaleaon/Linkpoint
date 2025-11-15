/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_font_status {
    vx_font_status status_free = vx_font_status("status_free", VxClientProxyJNI.status_free_get())
    vx_font_status status_none = vx_font_status("status_none", VxClientProxyJNI.status_none_get())
    vx_font_status status_not_free = vx_font_status("status_not_free", VxClientProxyJNI.status_not_free_get())
    private Int swigNext = 0
    private vx_font_status[] swigValues = vx_font_status[]{status_none, status_free, status_not_free}
    private String swigName
    private Int swigValue

    private vx_font_status(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_font_status(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_font_status(String string2, vx_font_status vx_font_status2) {
        this.swigName = string2
        this.swigValue = vx_font_status2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_font_status swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_font_status.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_font_status.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_font_status.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

