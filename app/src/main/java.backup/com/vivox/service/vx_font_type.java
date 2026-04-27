/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_font_type {
    private static int swigNext = 0;
    private static vx_font_type[] swigValues;
    public static final vx_font_type type_none;
    public static final vx_font_type type_root;
    public static final vx_font_type type_user;
    private final String swigName;
    private final int swigValue;

    static {
        type_none = new vx_font_type("type_none", VxClientProxyJNI.type_none_get());
        type_root = new vx_font_type("type_root", VxClientProxyJNI.type_root_get());
        type_user = new vx_font_type("type_user", VxClientProxyJNI.type_user_get());
        swigValues = new vx_font_type[]{type_none, type_root, type_user};
    }

    private vx_font_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_font_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_font_type(String string2, vx_font_type vx_font_type2) {
        this.swigName = string2;
        this.swigValue = vx_font_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_font_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_font_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_font_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_font_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

