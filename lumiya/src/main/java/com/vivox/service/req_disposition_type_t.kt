/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

class req_disposition_type_t {
    req_disposition_type_t req_disposition_no_reply_required = req_disposition_type_t("req_disposition_no_reply_required")
    req_disposition_type_t req_disposition_reply_required = req_disposition_type_t("req_disposition_reply_required")
    private Int swigNext = 0
    private req_disposition_type_t[] swigValues = req_disposition_type_t[]{req_disposition_reply_required, req_disposition_no_reply_required}
    private String swigName
    private Int swigValue

    private req_disposition_type_t(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private req_disposition_type_t(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private req_disposition_type_t(String string2, req_disposition_type_t req_disposition_type_t2) {
        this.swigName = string2
        this.swigValue = req_disposition_type_t2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    req_disposition_type_t swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && req_disposition_type_t.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (req_disposition_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + req_disposition_type_t.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

