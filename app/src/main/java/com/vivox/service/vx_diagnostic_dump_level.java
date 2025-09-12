/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_diagnostic_dump_level {
    public static final vx_diagnostic_dump_level diagnostic_dump_level_all = new vx_diagnostic_dump_level("diagnostic_dump_level_all", VxClientProxyJNI.diagnostic_dump_level_all_get());
    public static final vx_diagnostic_dump_level diagnostic_dump_level_sessions = new vx_diagnostic_dump_level("diagnostic_dump_level_sessions");
    private static int swigNext = 0;
    private static vx_diagnostic_dump_level[] swigValues = new vx_diagnostic_dump_level[]{diagnostic_dump_level_all, diagnostic_dump_level_sessions};
    private final String swigName;
    private final int swigValue;

    private vx_diagnostic_dump_level(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_diagnostic_dump_level(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_diagnostic_dump_level(String string2, vx_diagnostic_dump_level vx_diagnostic_dump_level2) {
        this.swigName = string2;
        this.swigValue = vx_diagnostic_dump_level2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_diagnostic_dump_level swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_diagnostic_dump_level.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_diagnostic_dump_level.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_diagnostic_dump_level.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

