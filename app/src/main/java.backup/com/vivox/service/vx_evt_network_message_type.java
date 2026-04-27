/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_evt_network_message_type {
    private static int swigNext = 0;
    private static vx_evt_network_message_type[] swigValues;
    public static final vx_evt_network_message_type vx_evt_network_message_type_admin_message;
    public static final vx_evt_network_message_type vx_evt_network_message_type_offline_message;
    public static final vx_evt_network_message_type vx_evt_network_message_type_sessionless_message;
    private final String swigName;
    private final int swigValue;

    static {
        vx_evt_network_message_type_admin_message = new vx_evt_network_message_type("vx_evt_network_message_type_admin_message", VxClientProxyJNI.vx_evt_network_message_type_admin_message_get());
        vx_evt_network_message_type_offline_message = new vx_evt_network_message_type("vx_evt_network_message_type_offline_message", VxClientProxyJNI.vx_evt_network_message_type_offline_message_get());
        vx_evt_network_message_type_sessionless_message = new vx_evt_network_message_type("vx_evt_network_message_type_sessionless_message", VxClientProxyJNI.vx_evt_network_message_type_sessionless_message_get());
        swigValues = new vx_evt_network_message_type[]{vx_evt_network_message_type_offline_message, vx_evt_network_message_type_admin_message, vx_evt_network_message_type_sessionless_message};
    }

    private vx_evt_network_message_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_evt_network_message_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_evt_network_message_type(String string2, vx_evt_network_message_type vx_evt_network_message_type2) {
        this.swigName = string2;
        this.swigValue = vx_evt_network_message_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_evt_network_message_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_evt_network_message_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_evt_network_message_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_evt_network_message_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

