/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_font_type {
    private Int swigNext = 0
    private vx_font_type[] swigValues
    vx_font_type type_none
    vx_font_type type_root
    vx_font_type type_user
    private String swigName
    private Int swigValue

    {
        type_none = vx_font_type("type_none", VxClientProxyJNI.type_none_get())
        type_root = vx_font_type("type_root", VxClientProxyJNI.type_root_get())
        type_user = vx_font_type("type_user", VxClientProxyJNI.type_user_get())
        swigValues = vx_font_type[]{type_none, type_root, type_user}
    }

    private vx_font_type(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_font_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_font_type(String string2, vx_font_type vx_font_type2) {
        this.swigName = string2
        this.swigValue = vx_font_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_font_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_font_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_font_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_font_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

