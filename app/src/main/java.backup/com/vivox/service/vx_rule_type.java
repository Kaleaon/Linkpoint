/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_rule_type {
    public static final vx_rule_type rule_allow = new vx_rule_type("rule_allow");
    public static final vx_rule_type rule_block = new vx_rule_type("rule_block");
    public static final vx_rule_type rule_hide = new vx_rule_type("rule_hide");
    public static final vx_rule_type rule_none = new vx_rule_type("rule_none", VxClientProxyJNI.rule_none_get());
    private static int swigNext = 0;
    private static vx_rule_type[] swigValues = new vx_rule_type[]{rule_none, rule_allow, rule_block, rule_hide};
    private final String swigName;
    private final int swigValue;

    private vx_rule_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_rule_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_rule_type(String string2, vx_rule_type vx_rule_type2) {
        this.swigName = string2;
        this.swigValue = vx_rule_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_rule_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_rule_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_rule_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_rule_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

