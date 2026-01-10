/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_dtmf_type {
    vx_dtmf_type dtmf_0 = vx_dtmf_type("dtmf_0", VxClientProxyJNI.dtmf_0_get())
    vx_dtmf_type dtmf_1 = vx_dtmf_type("dtmf_1", VxClientProxyJNI.dtmf_1_get())
    vx_dtmf_type dtmf_2 = vx_dtmf_type("dtmf_2", VxClientProxyJNI.dtmf_2_get())
    vx_dtmf_type dtmf_3 = vx_dtmf_type("dtmf_3", VxClientProxyJNI.dtmf_3_get())
    vx_dtmf_type dtmf_4 = vx_dtmf_type("dtmf_4", VxClientProxyJNI.dtmf_4_get())
    vx_dtmf_type dtmf_5 = vx_dtmf_type("dtmf_5", VxClientProxyJNI.dtmf_5_get())
    vx_dtmf_type dtmf_6 = vx_dtmf_type("dtmf_6", VxClientProxyJNI.dtmf_6_get())
    vx_dtmf_type dtmf_7 = vx_dtmf_type("dtmf_7", VxClientProxyJNI.dtmf_7_get())
    vx_dtmf_type dtmf_8 = vx_dtmf_type("dtmf_8", VxClientProxyJNI.dtmf_8_get())
    vx_dtmf_type dtmf_9 = vx_dtmf_type("dtmf_9", VxClientProxyJNI.dtmf_9_get())
    vx_dtmf_type dtmf_A = vx_dtmf_type("dtmf_A", VxClientProxyJNI.dtmf_A_get())
    vx_dtmf_type dtmf_B = vx_dtmf_type("dtmf_B", VxClientProxyJNI.dtmf_B_get())
    vx_dtmf_type dtmf_C = vx_dtmf_type("dtmf_C", VxClientProxyJNI.dtmf_C_get())
    vx_dtmf_type dtmf_D = vx_dtmf_type("dtmf_D", VxClientProxyJNI.dtmf_D_get())
    vx_dtmf_type dtmf_max = vx_dtmf_type("dtmf_max", VxClientProxyJNI.dtmf_max_get())
    vx_dtmf_type dtmf_pound = vx_dtmf_type("dtmf_pound", VxClientProxyJNI.dtmf_pound_get())
    vx_dtmf_type dtmf_star = vx_dtmf_type("dtmf_star", VxClientProxyJNI.dtmf_star_get())
    private Int swigNext = 0
    private vx_dtmf_type[] swigValues = vx_dtmf_type[]{dtmf_0, dtmf_1, dtmf_2, dtmf_3, dtmf_4, dtmf_5, dtmf_6, dtmf_7, dtmf_8, dtmf_9, dtmf_pound, dtmf_star, dtmf_A, dtmf_B, dtmf_C, dtmf_D, dtmf_max}
    private String swigName
    private Int swigValue

    private vx_dtmf_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_dtmf_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_dtmf_type(String string2, vx_dtmf_type vx_dtmf_type2) {
        this.swigName = string2
        this.swigValue = vx_dtmf_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_dtmf_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_dtmf_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_dtmf_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_dtmf_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

