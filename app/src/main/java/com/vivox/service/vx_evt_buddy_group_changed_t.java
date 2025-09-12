/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_change_type_t;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_buddy_group_changed_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_buddy_group_changed_t() {
        this(VxClientProxyJNI.new_vx_evt_buddy_group_changed_t(), true);
    }

    protected vx_evt_buddy_group_changed_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_buddy_group_changed_t vx_evt_buddy_group_changed_t2) {
        if (vx_evt_buddy_group_changed_t2 != null) return vx_evt_buddy_group_changed_t2.swigCPtr;
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
        return VxClientProxyJNI.vx_evt_buddy_group_changed_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_buddy_group_changed_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public vx_change_type_t getChange_type() {
        return vx_change_type_t.swigToEnum(VxClientProxyJNI.vx_evt_buddy_group_changed_t_change_type_get(this.swigCPtr, this));
    }

    public String getGroup_data() {
        return VxClientProxyJNI.vx_evt_buddy_group_changed_t_group_data_get(this.swigCPtr, this);
    }

    public int getGroup_id() {
        return VxClientProxyJNI.vx_evt_buddy_group_changed_t_group_id_get(this.swigCPtr, this);
    }

    public String getGroup_name() {
        return VxClientProxyJNI.vx_evt_buddy_group_changed_t_group_name_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_buddy_group_changed_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_buddy_group_changed_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setChange_type(vx_change_type_t vx_change_type_t2) {
        VxClientProxyJNI.vx_evt_buddy_group_changed_t_change_type_set(this.swigCPtr, this, vx_change_type_t2.swigValue());
    }

    public void setGroup_data(String string2) {
        VxClientProxyJNI.vx_evt_buddy_group_changed_t_group_data_set(this.swigCPtr, this, string2);
    }

    public void setGroup_id(int n) {
        VxClientProxyJNI.vx_evt_buddy_group_changed_t_group_id_set(this.swigCPtr, this, n);
    }

    public void setGroup_name(String string2) {
        VxClientProxyJNI.vx_evt_buddy_group_changed_t_group_name_set(this.swigCPtr, this, string2);
    }
}

