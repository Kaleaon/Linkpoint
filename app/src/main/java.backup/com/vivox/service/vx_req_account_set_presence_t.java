/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_buddy_presence_state;
import com.vivox.service.vx_req_base_t;

public class vx_req_account_set_presence_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_account_set_presence_t() {
        this(VxClientProxyJNI.new_vx_req_account_set_presence_t(), true);
    }

    protected vx_req_account_set_presence_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_account_set_presence_t vx_req_account_set_presence_t2) {
        if (vx_req_account_set_presence_t2 != null) return vx_req_account_set_presence_t2.swigCPtr;
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

    public String getAccount_handle() {
        return VxClientProxyJNI.vx_req_account_set_presence_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_account_set_presence_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public String getCustom_message() {
        return VxClientProxyJNI.vx_req_account_set_presence_t_custom_message_get(this.swigCPtr, this);
    }

    public vx_buddy_presence_state getPresence() {
        return vx_buddy_presence_state.swigToEnum(VxClientProxyJNI.vx_req_account_set_presence_t_presence_get(this.swigCPtr, this));
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setCustom_message(String string2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_custom_message_set(this.swigCPtr, this, string2);
    }

    public void setPresence(vx_buddy_presence_state vx_buddy_presence_state2) {
        VxClientProxyJNI.vx_req_account_set_presence_t_presence_set(this.swigCPtr, this, vx_buddy_presence_state2.swigValue());
    }
}

