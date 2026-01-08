/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_subscription_type {
    vx_subscription_type subscription_presence = vx_subscription_type("subscription_presence", VxClientProxyJNI.subscription_presence_get())
    private Int swigNext = 0
    private vx_subscription_type[] swigValues = vx_subscription_type[]{subscription_presence}
    private String swigName
    private Int swigValue

    private vx_subscription_type(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_subscription_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_subscription_type(String string2, vx_subscription_type vx_subscription_type2) {
        this.swigName = string2
        this.swigValue = vx_subscription_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_subscription_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_subscription_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_subscription_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_subscription_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

