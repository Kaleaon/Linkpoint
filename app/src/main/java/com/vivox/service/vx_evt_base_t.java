/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_event_type;
import com.vivox.service.vx_message_base_t;

public class vx_evt_base_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_base_t() {
        this(VxClientProxyJNI.new_vx_evt_base_t(), true);
    }

    public vx_evt_base_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_base_t vx_evt_base_t2) {
        if (vx_evt_base_t2 != null) return vx_evt_base_t2.swigCPtr;
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

    public String getExtended_status_info() {
        return VxClientProxyJNI.vx_evt_base_t_extended_status_info_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_message_base_t getMessage() {
        long l = VxClientProxyJNI.vx_evt_base_t_message_get(this.swigCPtr, this);
        if (l != 0L) return new vx_message_base_t(l, false);
        return null;
    }

    public vx_event_type getType() {
        return vx_event_type.swigToEnum(VxClientProxyJNI.vx_evt_base_t_type_get(this.swigCPtr, this));
    }

    public void setExtended_status_info(String string2) {
        VxClientProxyJNI.vx_evt_base_t_extended_status_info_set(this.swigCPtr, this, string2);
    }

    public void setMessage(vx_message_base_t vx_message_base_t2) {
        VxClientProxyJNI.vx_evt_base_t_message_set(this.swigCPtr, this, vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2);
    }

    public void setType(vx_event_type vx_event_type2) {
        VxClientProxyJNI.vx_evt_base_t_type_set(this.swigCPtr, this, vx_event_type2.swigValue());
    }
}

