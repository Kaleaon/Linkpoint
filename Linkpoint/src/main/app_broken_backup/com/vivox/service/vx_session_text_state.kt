/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_session_text_state {
    vx_session_text_state session_text_connected = vx_session_text_state("session_text_connected")
    vx_session_text_state session_text_connecting = vx_session_text_state("session_text_connecting")
    vx_session_text_state session_text_disconnected = vx_session_text_state("session_text_disconnected", VxClientProxyJNI.session_text_disconnected_get())
    vx_session_text_state session_text_disconnecting = vx_session_text_state("session_text_disconnecting")
    private Int swigNext = 0
    private vx_session_text_state[] swigValues = vx_session_text_state[]{session_text_disconnected, session_text_connected, session_text_connecting, session_text_disconnecting}
    private String swigName
    private Int swigValue

    private vx_session_text_state(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_session_text_state(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_session_text_state(String string2, vx_session_text_state vx_session_text_state2) {
        this.swigName = string2
        this.swigValue = vx_session_text_state2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_session_text_state swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_session_text_state.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_session_text_state.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_session_text_state.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

