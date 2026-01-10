/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

class vx_attempt_stun {
    vx_attempt_stun attempt_stun_off = vx_attempt_stun("attempt_stun_off")
    vx_attempt_stun attempt_stun_on = vx_attempt_stun("attempt_stun_on")
    vx_attempt_stun attempt_stun_unspecified = vx_attempt_stun("attempt_stun_unspecified")
    private Int swigNext = 0
    private vx_attempt_stun[] swigValues = vx_attempt_stun[]{attempt_stun_unspecified, attempt_stun_on, attempt_stun_off}
    private String swigName
    private Int swigValue

    private vx_attempt_stun(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_attempt_stun(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_attempt_stun(String string2, vx_attempt_stun vx_attempt_stun2) {
        this.swigName = string2
        this.swigValue = vx_attempt_stun2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_attempt_stun swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_attempt_stun.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_attempt_stun.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_attempt_stun.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

