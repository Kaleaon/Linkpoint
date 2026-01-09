/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_participant_removed_reason {
    vx_participant_removed_reason participant_banned = vx_participant_removed_reason("participant_banned", VxClientProxyJNI.participant_banned_get())
    vx_participant_removed_reason participant_kicked = vx_participant_removed_reason("participant_kicked", VxClientProxyJNI.participant_kicked_get())
    vx_participant_removed_reason participant_left = vx_participant_removed_reason("participant_left", VxClientProxyJNI.participant_left_get())
    vx_participant_removed_reason participant_timeout = vx_participant_removed_reason("participant_timeout", VxClientProxyJNI.participant_timeout_get())
    private Int swigNext = 0
    private vx_participant_removed_reason[] swigValues = vx_participant_removed_reason[]{participant_left, participant_timeout, participant_kicked, participant_banned}
    private String swigName
    private Int swigValue

    private vx_participant_removed_reason(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_participant_removed_reason(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_participant_removed_reason(String string2, vx_participant_removed_reason vx_participant_removed_reason2) {
        this.swigName = string2
        this.swigValue = vx_participant_removed_reason2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_participant_removed_reason swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_participant_removed_reason.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_participant_removed_reason.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_participant_removed_reason.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

