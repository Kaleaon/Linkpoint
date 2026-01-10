/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_channel_type {
    vx_channel_type channel_type_normal = vx_channel_type("channel_type_normal", VxClientProxyJNI.channel_type_normal_get())
    vx_channel_type channel_type_positional = vx_channel_type("channel_type_positional", VxClientProxyJNI.channel_type_positional_get())
    private Int swigNext = 0
    private vx_channel_type[] swigValues = vx_channel_type[]{channel_type_normal, channel_type_positional}
    private String swigName
    private Int swigValue

    private vx_channel_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_channel_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_channel_type(String string2, vx_channel_type vx_channel_type2) {
        this.swigName = string2
        this.swigValue = vx_channel_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_channel_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_channel_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

