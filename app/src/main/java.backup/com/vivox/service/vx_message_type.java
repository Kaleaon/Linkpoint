/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_message_type {
    public static final vx_message_type msg_event;
    public static final vx_message_type msg_none;
    public static final vx_message_type msg_request;
    public static final vx_message_type msg_response;
    private static int swigNext;
    private static vx_message_type[] swigValues;
    private final String swigName;
    private final int swigValue;

    static {
        swigNext = 0;
        msg_none = new vx_message_type("msg_none", VxClientProxyJNI.msg_none_get());
        msg_request = new vx_message_type("msg_request", VxClientProxyJNI.msg_request_get());
        msg_response = new vx_message_type("msg_response");
        msg_event = new vx_message_type("msg_event");
        swigValues = new vx_message_type[]{msg_none, msg_request, msg_response, msg_event};
    }

    private vx_message_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_message_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_message_type(String string2, vx_message_type vx_message_type2) {
        this.swigName = string2;
        this.swigValue = vx_message_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_message_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_message_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_message_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_message_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

