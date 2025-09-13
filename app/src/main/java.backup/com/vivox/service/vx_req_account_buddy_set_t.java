/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_req_base_t;

public class vx_req_account_buddy_set_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_account_buddy_set_t() {
        this(VxClientProxyJNI.new_vx_req_account_buddy_set_t(), true);
    }

    protected vx_req_account_buddy_set_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_account_buddy_set_t vx_req_account_buddy_set_t2) {
        if (vx_req_account_buddy_set_t2 != null) return vx_req_account_buddy_set_t2.swigCPtr;
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
        return VxClientProxyJNI.vx_req_account_buddy_set_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_account_buddy_set_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public String getBuddy_data() {
        return VxClientProxyJNI.vx_req_account_buddy_set_t_buddy_data_get(this.swigCPtr, this);
    }

    public String getBuddy_uri() {
        return VxClientProxyJNI.vx_req_account_buddy_set_t_buddy_uri_get(this.swigCPtr, this);
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_req_account_buddy_set_t_display_name_get(this.swigCPtr, this);
    }

    public int getGroup_id() {
        return VxClientProxyJNI.vx_req_account_buddy_set_t_group_id_get(this.swigCPtr, this);
    }

    public String getMessage() {
        return VxClientProxyJNI.vx_req_account_buddy_set_t_message_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setBuddy_data(String string2) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_buddy_data_set(this.swigCPtr, this, string2);
    }

    public void setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_buddy_uri_set(this.swigCPtr, this, string2);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setGroup_id(int n) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_group_id_set(this.swigCPtr, this, n);
    }

    public void setMessage(String string2) {
        VxClientProxyJNI.vx_req_account_buddy_set_t_message_set(this.swigCPtr, this, string2);
    }
}

