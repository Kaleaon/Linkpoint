/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_login_state_change_state {
    vx_login_state_change_state login_state_error = vx_login_state_change_state("login_state_error", VxClientProxyJNI.login_state_error_get())
    vx_login_state_change_state login_state_logged_in = vx_login_state_change_state("login_state_logged_in", VxClientProxyJNI.login_state_logged_in_get())
    vx_login_state_change_state login_state_logged_out = vx_login_state_change_state("login_state_logged_out", VxClientProxyJNI.login_state_logged_out_get())
    vx_login_state_change_state login_state_logging_in = vx_login_state_change_state("login_state_logging_in", VxClientProxyJNI.login_state_logging_in_get())
    vx_login_state_change_state login_state_logging_out = vx_login_state_change_state("login_state_logging_out", VxClientProxyJNI.login_state_logging_out_get())
    vx_login_state_change_state login_state_resetting = vx_login_state_change_state("login_state_resetting", VxClientProxyJNI.login_state_resetting_get())
    private Int swigNext = 0
    private vx_login_state_change_state[] swigValues = vx_login_state_change_state[]{login_state_logged_out, login_state_logged_in, login_state_logging_in, login_state_logging_out, login_state_resetting, login_state_error}
    private String swigName
    private Int swigValue

    private vx_login_state_change_state(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_login_state_change_state(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_login_state_change_state(String string2, vx_login_state_change_state vx_login_state_change_state2) {
        this.swigName = string2
        this.swigValue = vx_login_state_change_state2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_login_state_change_state swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_login_state_change_state.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_login_state_change_state.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_login_state_change_state.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

