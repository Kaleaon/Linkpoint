/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_user_channel_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_user_channel_t() {
        this(VxClientProxyJNI.new_vx_user_channel_t(), true);
    }

    protected vx_user_channel_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_user_channel_t vx_user_channel_t2) {
        if (vx_user_channel_t2 != null) return vx_user_channel_t2.swigCPtr;
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

    public String getName() {
        return VxClientProxyJNI.vx_user_channel_t_name_get(this.swigCPtr, this);
    }

    public String getUri() {
        return VxClientProxyJNI.vx_user_channel_t_uri_get(this.swigCPtr, this);
    }

    public void setName(String string2) {
        VxClientProxyJNI.vx_user_channel_t_name_set(this.swigCPtr, this, string2);
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_user_channel_t_uri_set(this.swigCPtr, this, string2);
    }
}

