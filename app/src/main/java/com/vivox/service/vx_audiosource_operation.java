/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_audiosource_operation {
    public static final vx_audiosource_operation op_delete = new vx_audiosource_operation("op_delete");
    public static final vx_audiosource_operation op_none = new vx_audiosource_operation("op_none", VxClientProxyJNI.op_none_get());
    public static final vx_audiosource_operation op_safeupdate = new vx_audiosource_operation("op_safeupdate", VxClientProxyJNI.op_safeupdate_get());
    private static int swigNext = 0;
    private static vx_audiosource_operation[] swigValues = new vx_audiosource_operation[]{op_none, op_safeupdate, op_delete};
    private final String swigName;
    private final int swigValue;

    private vx_audiosource_operation(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_audiosource_operation(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_audiosource_operation(String string2, vx_audiosource_operation vx_audiosource_operation2) {
        this.swigName = string2;
        this.swigValue = vx_audiosource_operation2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_audiosource_operation swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_audiosource_operation.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_audiosource_operation.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_audiosource_operation.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

