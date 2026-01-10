/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_participant_type {
    vx_participant_type part_focus = vx_participant_type("part_focus", VxClientProxyJNI.part_focus_get())
    vx_participant_type part_moderator = vx_participant_type("part_moderator", VxClientProxyJNI.part_moderator_get())
    vx_participant_type part_user = vx_participant_type("part_user", VxClientProxyJNI.part_user_get())
    vx_participant_type participant_moderator = vx_participant_type("participant_moderator", VxClientProxyJNI.participant_moderator_get())
    vx_participant_type participant_owner = vx_participant_type("participant_owner", VxClientProxyJNI.participant_owner_get())
    vx_participant_type participant_user = vx_participant_type("participant_user", VxClientProxyJNI.participant_user_get())
    private Int swigNext = 0
    private vx_participant_type[] swigValues = vx_participant_type[]{participant_user, part_user, participant_moderator, part_moderator, participant_owner, part_focus}
    private String swigName
    private Int swigValue

    private vx_participant_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_participant_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_participant_type(String string2, vx_participant_type vx_participant_type2) {
        this.swigName = string2
        this.swigValue = vx_participant_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_participant_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_participant_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_participant_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_participant_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

