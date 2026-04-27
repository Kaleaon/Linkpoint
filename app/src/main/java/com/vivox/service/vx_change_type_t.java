/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_change_type_t {
    public static final vx_change_type_t change_type_delete = new vx_change_type_t("change_type_delete", VxClientProxyJNI.change_type_delete_get());
    public static final vx_change_type_t change_type_set = new vx_change_type_t("change_type_set", VxClientProxyJNI.change_type_set_get());
    private static int swigNext = 0;
    private static vx_change_type_t[] swigValues = new vx_change_type_t[]{change_type_set, change_type_delete};
    private final String swigName;
    private final int swigValue;

    private vx_change_type_t(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_change_type_t(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_change_type_t(String string2, vx_change_type_t vx_change_type_t2) {
        this.swigName = string2;
        this.swigValue = vx_change_type_t2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_change_type_t swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_change_type_t.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_change_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_change_type_t.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

