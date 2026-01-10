/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_rule_type {
    vx_rule_type rule_allow = vx_rule_type("rule_allow")
    vx_rule_type rule_block = vx_rule_type("rule_block")
    vx_rule_type rule_hide = vx_rule_type("rule_hide")
    vx_rule_type rule_none = vx_rule_type("rule_none", VxClientProxyJNI.rule_none_get())
    private Int swigNext = 0
    private vx_rule_type[] swigValues = vx_rule_type[]{rule_none, rule_allow, rule_block, rule_hide}
    private String swigName
    private Int swigValue

    private vx_rule_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_rule_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_rule_type(String string2, vx_rule_type vx_rule_type2) {
        this.swigName = string2
        this.swigValue = vx_rule_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_rule_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_rule_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_rule_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_rule_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

