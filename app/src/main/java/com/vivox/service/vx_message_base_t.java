/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_message_type;
import java.math.BigInteger;

public class vx_message_base_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_message_base_t() {
        this(VxClientProxyJNI.new_vx_message_base_t(), true);
    }

    protected vx_message_base_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static long getCPtr(vx_message_base_t vx_message_base_t2) {
        if (vx_message_base_t2 != null) return vx_message_base_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L;
                return;
            }
            this.swigCMemOwn = false;
            UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("C++ destructor does not have public access");
            throw unsupportedOperationException;
        }
    }

    public BigInteger getCreate_time_ms() {
        return VxClientProxyJNI.vx_message_base_t_create_time_ms_get(this.swigCPtr, this);
    }

    public BigInteger getLast_step_ms() {
        return VxClientProxyJNI.vx_message_base_t_last_step_ms_get(this.swigCPtr, this);
    }

    public long getSdk_handle() {
        return VxClientProxyJNI.vx_message_base_t_sdk_handle_get(this.swigCPtr, this);
    }

    public vx_message_type getType() {
        return vx_message_type.swigToEnum(VxClientProxyJNI.vx_message_base_t_type_get(this.swigCPtr, this));
    }

    public void setCreate_time_ms(BigInteger bigInteger) {
        VxClientProxyJNI.vx_message_base_t_create_time_ms_set(this.swigCPtr, this, bigInteger);
    }

    public void setLast_step_ms(BigInteger bigInteger) {
        VxClientProxyJNI.vx_message_base_t_last_step_ms_set(this.swigCPtr, this, bigInteger);
    }

    public void setSdk_handle(long l) {
        VxClientProxyJNI.vx_message_base_t_sdk_handle_set(this.swigCPtr, this, l);
    }

    public void setType(vx_message_type vx_message_type2) {
        VxClientProxyJNI.vx_message_base_t_type_set(this.swigCPtr, this, vx_message_type2.swigValue());
    }
}

