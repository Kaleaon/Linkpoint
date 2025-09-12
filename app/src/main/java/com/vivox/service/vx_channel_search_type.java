/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_channel_search_type {
    public static final vx_channel_search_type channel_search_type_all = new vx_channel_search_type("channel_search_type_all", VxClientProxyJNI.channel_search_type_all_get());
    public static final vx_channel_search_type channel_search_type_non_positional = new vx_channel_search_type("channel_search_type_non_positional", VxClientProxyJNI.channel_search_type_non_positional_get());
    public static final vx_channel_search_type channel_search_type_positional = new vx_channel_search_type("channel_search_type_positional", VxClientProxyJNI.channel_search_type_positional_get());
    private static int swigNext = 0;
    private static vx_channel_search_type[] swigValues = new vx_channel_search_type[]{channel_search_type_all, channel_search_type_non_positional, channel_search_type_positional};
    private final String swigName;
    private final int swigValue;

    private vx_channel_search_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_channel_search_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_channel_search_type(String string2, vx_channel_search_type vx_channel_search_type2) {
        this.swigName = string2;
        this.swigValue = vx_channel_search_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_channel_search_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_search_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_channel_search_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_channel_search_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

