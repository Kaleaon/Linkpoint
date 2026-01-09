/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_change_type_t {
    vx_change_type_t change_type_delete = vx_change_type_t("change_type_delete", VxClientProxyJNI.change_type_delete_get())
    vx_change_type_t change_type_set = vx_change_type_t("change_type_set", VxClientProxyJNI.change_type_set_get())
    private Int swigNext = 0
    private vx_change_type_t[] swigValues = vx_change_type_t[]{change_type_set, change_type_delete}
    private String swigName
    private Int swigValue

    private vx_change_type_t(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_change_type_t(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_change_type_t(String string2, vx_change_type_t vx_change_type_t2) {
        this.swigName = string2
        this.swigValue = vx_change_type_t2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_change_type_t swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_change_type_t.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_change_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_change_type_t.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

