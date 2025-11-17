/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_sessiongroup_type {
    vx_sessiongroup_type sessiongroup_type_normal = vx_sessiongroup_type("sessiongroup_type_normal", VxClientProxyJNI.sessiongroup_type_normal_get())
    vx_sessiongroup_type sessiongroup_type_playback = vx_sessiongroup_type("sessiongroup_type_playback", VxClientProxyJNI.sessiongroup_type_playback_get())
    private Int swigNext = 0
    private vx_sessiongroup_type[] swigValues = vx_sessiongroup_type[]{sessiongroup_type_normal, sessiongroup_type_playback}
    private String swigName
    private Int swigValue

    private vx_sessiongroup_type(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_sessiongroup_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_sessiongroup_type(String string2, vx_sessiongroup_type vx_sessiongroup_type2) {
        this.swigName = string2
        this.swigValue = vx_sessiongroup_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_sessiongroup_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_sessiongroup_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_sessiongroup_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_sessiongroup_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

