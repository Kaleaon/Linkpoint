/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy_contact;
import com.vivox.service.VxClientProxyJNI;

public class vx_state_buddy_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_state_buddy_t() {
        this(VxClientProxyJNI.new_vx_state_buddy_t(), true);
    }

    protected vx_state_buddy_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_state_buddy_t vx_state_buddy_t2) {
        if (vx_state_buddy_t2 != null) return vx_state_buddy_t2.swigCPtr;
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

    public String getBuddy_data() {
        return VxClientProxyJNI.vx_state_buddy_t_buddy_data_get(this.swigCPtr, this);
    }

    public String getBuddy_uri() {
        return VxClientProxyJNI.vx_state_buddy_t_buddy_uri_get(this.swigCPtr, this);
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_state_buddy_t_display_name_get(this.swigCPtr, this);
    }

    public int getParent_group_id() {
        return VxClientProxyJNI.vx_state_buddy_t_parent_group_id_get(this.swigCPtr, this);
    }

    public int getState_buddy_contact_count() {
        return VxClientProxyJNI.vx_state_buddy_t_state_buddy_contact_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_p_vx_state_buddy_contact getState_buddy_contacts() {
        long l = VxClientProxyJNI.vx_state_buddy_t_state_buddy_contacts_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_p_vx_state_buddy_contact(l, false);
        return null;
    }

    public void setBuddy_data(String string2) {
        VxClientProxyJNI.vx_state_buddy_t_buddy_data_set(this.swigCPtr, this, string2);
    }

    public void setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_state_buddy_t_buddy_uri_set(this.swigCPtr, this, string2);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_buddy_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setParent_group_id(int n) {
        VxClientProxyJNI.vx_state_buddy_t_parent_group_id_set(this.swigCPtr, this, n);
    }

    public void setState_buddy_contact_count(int n) {
        VxClientProxyJNI.vx_state_buddy_t_state_buddy_contact_count_set(this.swigCPtr, this, n);
    }

    public void setState_buddy_contacts(SWIGTYPE_p_p_vx_state_buddy_contact sWIGTYPE_p_p_vx_state_buddy_contact) {
        VxClientProxyJNI.vx_state_buddy_t_state_buddy_contacts_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_buddy_contact.getCPtr(sWIGTYPE_p_p_vx_state_buddy_contact));
    }
}

