/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

public final class vx_password_hash_algorithm_t {
    public static final vx_password_hash_algorithm_t password_hash_algorithm_cleartext;
    public static final vx_password_hash_algorithm_t password_hash_algorithm_sha1_username_hash;
    private static int swigNext;
    private static vx_password_hash_algorithm_t[] swigValues;
    private final String swigName;
    private final int swigValue;

    static {
        swigNext = 0;
        password_hash_algorithm_cleartext = new vx_password_hash_algorithm_t("password_hash_algorithm_cleartext");
        password_hash_algorithm_sha1_username_hash = new vx_password_hash_algorithm_t("password_hash_algorithm_sha1_username_hash");
        swigValues = new vx_password_hash_algorithm_t[]{password_hash_algorithm_cleartext, password_hash_algorithm_sha1_username_hash};
    }

    private vx_password_hash_algorithm_t(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_password_hash_algorithm_t(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_password_hash_algorithm_t(String string2, vx_password_hash_algorithm_t vx_password_hash_algorithm_t2) {
        this.swigName = string2;
        this.swigValue = vx_password_hash_algorithm_t2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_password_hash_algorithm_t swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_password_hash_algorithm_t.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_password_hash_algorithm_t.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_password_hash_algorithm_t.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

