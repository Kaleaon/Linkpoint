/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_group_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_group_t() {
        this(VxClientProxyJNI.new_vx_group_t(), true);
    }

    protected vx_group_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_group_t vx_group_t2) {
        if (vx_group_t2 != null) return vx_group_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_group_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getGroup_data() {
        return VxClientProxyJNI.vx_group_t_group_data_get(this.swigCPtr, this);
    }

    public int getGroup_id() {
        return VxClientProxyJNI.vx_group_t_group_id_get(this.swigCPtr, this);
    }

    public String getGroup_name() {
        return VxClientProxyJNI.vx_group_t_group_name_get(this.swigCPtr, this);
    }

    public void setGroup_data(String string2) {
        VxClientProxyJNI.vx_group_t_group_data_set(this.swigCPtr, this, string2);
    }

    public void setGroup_id(int n) {
        VxClientProxyJNI.vx_group_t_group_id_set(this.swigCPtr, this, n);
    }

    public void setGroup_name(String string2) {
        VxClientProxyJNI.vx_group_t_group_name_set(this.swigCPtr, this, string2);
    }
}

