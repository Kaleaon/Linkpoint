/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_subscription_type {
    public static final vx_subscription_type subscription_presence = new vx_subscription_type("subscription_presence", VxClientProxyJNI.subscription_presence_get());
    private static int swigNext = 0;
    private static vx_subscription_type[] swigValues = new vx_subscription_type[]{subscription_presence};
    private final String swigName;
    private final int swigValue;

    private vx_subscription_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_subscription_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_subscription_type(String string2, vx_subscription_type vx_subscription_type2) {
        this.swigName = string2;
        this.swigValue = vx_subscription_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_subscription_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_subscription_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_subscription_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_subscription_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

