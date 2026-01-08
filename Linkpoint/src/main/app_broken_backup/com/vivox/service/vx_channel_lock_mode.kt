/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_channel_lock_mode {
    vx_channel_lock_mode channel_lock = vx_channel_lock_mode("channel_lock")
    vx_channel_lock_mode channel_unlock = vx_channel_lock_mode("channel_unlock", VxClientProxyJNI.channel_unlock_get())
    private Int swigNext = 0
    private vx_channel_lock_mode[] swigValues = vx_channel_lock_mode[]{channel_unlock, channel_lock}
    private String swigName
    private Int swigValue

    private vx_channel_lock_mode(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_channel_lock_mode(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_channel_lock_mode(String string2, vx_channel_lock_mode vx_channel_lock_mode2) {
        this.swigName = string2
        this.swigValue = vx_channel_lock_mode2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_lock_mode swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_lock_mode.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_channel_lock_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_channel_lock_mode.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

