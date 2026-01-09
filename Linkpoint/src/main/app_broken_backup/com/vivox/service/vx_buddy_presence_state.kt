/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_buddy_presence_state {
    vx_buddy_presence_state buddy_presence_away = vx_buddy_presence_state("buddy_presence_away", VxClientProxyJNI.buddy_presence_away_get())
    vx_buddy_presence_state buddy_presence_brb = vx_buddy_presence_state("buddy_presence_brb", VxClientProxyJNI.buddy_presence_brb_get())
    vx_buddy_presence_state buddy_presence_busy = vx_buddy_presence_state("buddy_presence_busy", VxClientProxyJNI.buddy_presence_busy_get())
    vx_buddy_presence_state buddy_presence_closed = vx_buddy_presence_state("buddy_presence_closed", VxClientProxyJNI.buddy_presence_closed_get())
    vx_buddy_presence_state buddy_presence_custom = vx_buddy_presence_state("buddy_presence_custom", VxClientProxyJNI.buddy_presence_custom_get())
    vx_buddy_presence_state buddy_presence_offline = vx_buddy_presence_state("buddy_presence_offline", VxClientProxyJNI.buddy_presence_offline_get())
    vx_buddy_presence_state buddy_presence_online = vx_buddy_presence_state("buddy_presence_online", VxClientProxyJNI.buddy_presence_online_get())
    vx_buddy_presence_state buddy_presence_online_slc = vx_buddy_presence_state("buddy_presence_online_slc", VxClientProxyJNI.buddy_presence_online_slc_get())
    vx_buddy_presence_state buddy_presence_onthephone = vx_buddy_presence_state("buddy_presence_onthephone", VxClientProxyJNI.buddy_presence_onthephone_get())
    vx_buddy_presence_state buddy_presence_outtolunch = vx_buddy_presence_state("buddy_presence_outtolunch", VxClientProxyJNI.buddy_presence_outtolunch_get())
    vx_buddy_presence_state buddy_presence_pending = vx_buddy_presence_state("buddy_presence_pending", VxClientProxyJNI.buddy_presence_pending_get())
    vx_buddy_presence_state buddy_presence_unknown = vx_buddy_presence_state("buddy_presence_unknown", VxClientProxyJNI.buddy_presence_unknown_get())
    private Int swigNext = 0
    private vx_buddy_presence_state[] swigValues = vx_buddy_presence_state[]{buddy_presence_unknown, buddy_presence_pending, buddy_presence_online, buddy_presence_busy, buddy_presence_brb, buddy_presence_away, buddy_presence_onthephone, buddy_presence_outtolunch, buddy_presence_custom, buddy_presence_online_slc, buddy_presence_closed, buddy_presence_offline}
    private String swigName
    private Int swigValue

    private vx_buddy_presence_state(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_buddy_presence_state(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_buddy_presence_state(String string2, vx_buddy_presence_state vx_buddy_presence_state2) {
        this.swigName = string2
        this.swigValue = vx_buddy_presence_state2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_buddy_presence_state swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_buddy_presence_state.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_buddy_presence_state.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_buddy_presence_state.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

