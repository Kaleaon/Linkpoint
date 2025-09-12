/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

public final class req_disposition_type_t {
    public static final req_disposition_type_t req_disposition_no_reply_required = new req_disposition_type_t("req_disposition_no_reply_required");
    public static final req_disposition_type_t req_disposition_reply_required = new req_disposition_type_t("req_disposition_reply_required");
    private static int swigNext = 0;
    private static req_disposition_type_t[] swigValues = new req_disposition_type_t[]{req_disposition_reply_required, req_disposition_no_reply_required};
    private final String swigName;
    private final int swigValue;

    private req_disposition_type_t(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private req_disposition_type_t(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private req_disposition_type_t(String string2, req_disposition_type_t req_disposition_type_t2) {
        this.swigName = string2;
        this.swigValue = req_disposition_type_t2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static req_disposition_type_t swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && req_disposition_type_t.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (req_disposition_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + req_disposition_type_t.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

