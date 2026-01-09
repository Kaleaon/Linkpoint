/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

class vx_password_hash_algorithm_t {
    vx_password_hash_algorithm_t password_hash_algorithm_cleartext
    vx_password_hash_algorithm_t password_hash_algorithm_sha1_username_hash
    private Int swigNext
    private vx_password_hash_algorithm_t[] swigValues
    private String swigName
    private Int swigValue

    {
        swigNext = 0
        password_hash_algorithm_cleartext = vx_password_hash_algorithm_t("password_hash_algorithm_cleartext")
        password_hash_algorithm_sha1_username_hash = vx_password_hash_algorithm_t("password_hash_algorithm_sha1_username_hash")
        swigValues = vx_password_hash_algorithm_t[]{password_hash_algorithm_cleartext, password_hash_algorithm_sha1_username_hash}
    }

    private vx_password_hash_algorithm_t(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_password_hash_algorithm_t(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_password_hash_algorithm_t(String string2, vx_password_hash_algorithm_t vx_password_hash_algorithm_t2) {
        this.swigName = string2
        this.swigValue = vx_password_hash_algorithm_t2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_password_hash_algorithm_t swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_password_hash_algorithm_t.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_password_hash_algorithm_t.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_password_hash_algorithm_t.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

