/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class orientation_type {
    orientation_type orientation_default = orientation_type("orientation_default", VxClientProxyJNI.orientation_default_get())
    orientation_type orientation_legacy = orientation_type("orientation_legacy", VxClientProxyJNI.orientation_legacy_get())
    orientation_type orientation_vivox = orientation_type("orientation_vivox", VxClientProxyJNI.orientation_vivox_get())
    private Int swigNext = 0
    private orientation_type[] swigValues = orientation_type[]{orientation_default, orientation_legacy, orientation_vivox}
    private String swigName
    private Int swigValue

    private orientation_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private orientation_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private orientation_type(String string2, orientation_type orientation_type2) {
        this.swigName = string2
        this.swigValue = orientation_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    orientation_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && orientation_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (orientation_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + orientation_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

