/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_evt_network_message_type {
    private Int swigNext = 0
    private vx_evt_network_message_type[] swigValues
    vx_evt_network_message_type vx_evt_network_message_type_admin_message
    vx_evt_network_message_type vx_evt_network_message_type_offline_message
    vx_evt_network_message_type vx_evt_network_message_type_sessionless_message
    private String swigName
    private Int swigValue

    {
        vx_evt_network_message_type_admin_message = vx_evt_network_message_type("vx_evt_network_message_type_admin_message", VxClientProxyJNI.vx_evt_network_message_type_admin_message_get())
        vx_evt_network_message_type_offline_message = vx_evt_network_message_type("vx_evt_network_message_type_offline_message", VxClientProxyJNI.vx_evt_network_message_type_offline_message_get())
        vx_evt_network_message_type_sessionless_message = vx_evt_network_message_type("vx_evt_network_message_type_sessionless_message", VxClientProxyJNI.vx_evt_network_message_type_sessionless_message_get())
        swigValues = vx_evt_network_message_type[]{vx_evt_network_message_type_offline_message, vx_evt_network_message_type_admin_message, vx_evt_network_message_type_sessionless_message}
    }

    private vx_evt_network_message_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_evt_network_message_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_evt_network_message_type(String string2, vx_evt_network_message_type vx_evt_network_message_type2) {
        this.swigName = string2
        this.swigValue = vx_evt_network_message_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_network_message_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_evt_network_message_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_evt_network_message_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_evt_network_message_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

