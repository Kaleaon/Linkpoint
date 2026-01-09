/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_message_type {
    vx_message_type msg_event
    vx_message_type msg_none
    vx_message_type msg_request
    vx_message_type msg_response
    private Int swigNext
    private vx_message_type[] swigValues
    private String swigName
    private Int swigValue

    {
        swigNext = 0
        msg_none = vx_message_type("msg_none", VxClientProxyJNI.msg_none_get())
        msg_request = vx_message_type("msg_request", VxClientProxyJNI.msg_request_get())
        msg_response = vx_message_type("msg_response")
        msg_event = vx_message_type("msg_event")
        swigValues = vx_message_type[]{msg_none, msg_request, msg_response, msg_event}
    }

    private vx_message_type(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_message_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_message_type(String string2, vx_message_type vx_message_type2) {
        this.swigName = string2
        this.swigValue = vx_message_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_message_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_message_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_message_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_message_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

