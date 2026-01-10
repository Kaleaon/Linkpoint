/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_audiosource_operation {
    vx_audiosource_operation op_delete = vx_audiosource_operation("op_delete")
    vx_audiosource_operation op_none = vx_audiosource_operation("op_none", VxClientProxyJNI.op_none_get())
    vx_audiosource_operation op_safeupdate = vx_audiosource_operation("op_safeupdate", VxClientProxyJNI.op_safeupdate_get())
    private Int swigNext = 0
    private vx_audiosource_operation[] swigValues = vx_audiosource_operation[]{op_none, op_safeupdate, op_delete}
    private String swigName
    private Int swigValue

    private vx_audiosource_operation(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_audiosource_operation(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_audiosource_operation(String string2, vx_audiosource_operation vx_audiosource_operation2) {
        this.swigName = string2
        this.swigValue = vx_audiosource_operation2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_audiosource_operation swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_audiosource_operation.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_audiosource_operation.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_audiosource_operation.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

