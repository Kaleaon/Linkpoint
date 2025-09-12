/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_buddy_management_mode {
    public static final vx_buddy_management_mode mode_application = new vx_buddy_management_mode("mode_application");
    public static final vx_buddy_management_mode mode_auto_accept = new vx_buddy_management_mode("mode_auto_accept", VxClientProxyJNI.mode_auto_accept_get());
    public static final vx_buddy_management_mode mode_auto_add = new vx_buddy_management_mode("mode_auto_add", VxClientProxyJNI.mode_auto_add_get());
    public static final vx_buddy_management_mode mode_block = new vx_buddy_management_mode("mode_block");
    public static final vx_buddy_management_mode mode_hide = new vx_buddy_management_mode("mode_hide");
    private static int swigNext = 0;
    private static vx_buddy_management_mode[] swigValues = new vx_buddy_management_mode[]{mode_auto_accept, mode_auto_add, mode_block, mode_hide, mode_application};
    private final String swigName;
    private final int swigValue;

    private vx_buddy_management_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_buddy_management_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_buddy_management_mode(String string2, vx_buddy_management_mode vx_buddy_management_mode2) {
        this.swigName = string2;
        this.swigValue = vx_buddy_management_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_buddy_management_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_buddy_management_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_buddy_management_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_buddy_management_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

