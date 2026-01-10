/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_session_answer_mode {
    vx_session_answer_mode mode_auto_answer = vx_session_answer_mode("mode_auto_answer", VxClientProxyJNI.mode_auto_answer_get())
    vx_session_answer_mode mode_busy_answer = vx_session_answer_mode("mode_busy_answer", VxClientProxyJNI.mode_busy_answer_get())
    vx_session_answer_mode mode_none = vx_session_answer_mode("mode_none", VxClientProxyJNI.mode_none_get())
    vx_session_answer_mode mode_verify_answer = vx_session_answer_mode("mode_verify_answer", VxClientProxyJNI.mode_verify_answer_get())
    private Int swigNext = 0
    private vx_session_answer_mode[] swigValues = vx_session_answer_mode[]{mode_none, mode_auto_answer, mode_verify_answer, mode_busy_answer}
    private String swigName
    private Int swigValue

    private vx_session_answer_mode(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_session_answer_mode(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_session_answer_mode(String string2, vx_session_answer_mode vx_session_answer_mode2) {
        this.swigName = string2
        this.swigValue = vx_session_answer_mode2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_session_answer_mode swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_session_answer_mode.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_session_answer_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_session_answer_mode.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

