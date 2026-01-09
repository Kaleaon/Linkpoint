/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_session_media_state {
    vx_session_media_state session_media_connected
    vx_session_media_state session_media_connecting
    vx_session_media_state session_media_disconnected
    vx_session_media_state session_media_disconnecting
    vx_session_media_state session_media_hold
    vx_session_media_state session_media_none
    vx_session_media_state session_media_refer
    vx_session_media_state session_media_ringing
    private Int swigNext
    private vx_session_media_state[] swigValues
    private String swigName
    private Int swigValue

    {
        swigNext = 0
        session_media_none = vx_session_media_state("session_media_none", VxClientProxyJNI.session_media_none_get())
        session_media_disconnected = vx_session_media_state("session_media_disconnected")
        session_media_connected = vx_session_media_state("session_media_connected")
        session_media_ringing = vx_session_media_state("session_media_ringing")
        session_media_hold = vx_session_media_state("session_media_hold")
        session_media_refer = vx_session_media_state("session_media_refer")
        session_media_connecting = vx_session_media_state("session_media_connecting")
        session_media_disconnecting = vx_session_media_state("session_media_disconnecting")
        swigValues = vx_session_media_state[]{session_media_none, session_media_disconnected, session_media_connected, session_media_ringing, session_media_hold, session_media_refer, session_media_connecting, session_media_disconnecting}
    }

    private vx_session_media_state(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_session_media_state(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_session_media_state(String string2, vx_session_media_state vx_session_media_state2) {
        this.swigName = string2
        this.swigValue = vx_session_media_state2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_session_media_state swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_session_media_state.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_session_media_state.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_session_media_state.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

