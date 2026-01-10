/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_channel_search_type {
    vx_channel_search_type channel_search_type_all = vx_channel_search_type("channel_search_type_all", VxClientProxyJNI.channel_search_type_all_get())
    vx_channel_search_type channel_search_type_non_positional = vx_channel_search_type("channel_search_type_non_positional", VxClientProxyJNI.channel_search_type_non_positional_get())
    vx_channel_search_type channel_search_type_positional = vx_channel_search_type("channel_search_type_positional", VxClientProxyJNI.channel_search_type_positional_get())
    private Int swigNext = 0
    private vx_channel_search_type[] swigValues = vx_channel_search_type[]{channel_search_type_all, channel_search_type_non_positional, channel_search_type_positional}
    private String swigName
    private Int swigValue

    private vx_channel_search_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_channel_search_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_channel_search_type(String string2, vx_channel_search_type vx_channel_search_type2) {
        this.swigName = string2
        this.swigValue = vx_channel_search_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_search_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_search_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_channel_search_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_channel_search_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

