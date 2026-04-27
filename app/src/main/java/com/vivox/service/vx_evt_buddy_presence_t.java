/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_buddy_presence_state;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_buddy_presence_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_buddy_presence_t() {
        this(VxClientProxyJNI.new_vx_evt_buddy_presence_t(), true);
    }

    protected vx_evt_buddy_presence_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_buddy_presence_t vx_evt_buddy_presence_t2) {
        if (vx_evt_buddy_presence_t2 != null) return vx_evt_buddy_presence_t2.swigCPtr;
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
        return VxClientProxyJNI.vx_evt_buddy_presence_t_account_handle_get(this.swigCPtr, this);
    }

    public String getApplication() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_application_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_buddy_presence_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public String getBuddy_uri() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_buddy_uri_get(this.swigCPtr, this);
    }

    public String getContact() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_contact_get(this.swigCPtr, this);
    }

    public String getCustom_message() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_custom_message_get(this.swigCPtr, this);
    }

    public String getDisplayname() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_displayname_get(this.swigCPtr, this);
    }

    public String getId() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_id_get(this.swigCPtr, this);
    }

    public vx_buddy_presence_state getPresence() {
        return vx_buddy_presence_state.swigToEnum(VxClientProxyJNI.vx_evt_buddy_presence_t_presence_get(this.swigCPtr, this));
    }

    public String getPriority() {
        return VxClientProxyJNI.vx_evt_buddy_presence_t_priority_get(this.swigCPtr, this);
    }

    public vx_buddy_presence_state getState() {
        return vx_buddy_presence_state.swigToEnum(VxClientProxyJNI.vx_evt_buddy_presence_t_state_get(this.swigCPtr, this));
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setApplication(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_application_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_buddy_uri_set(this.swigCPtr, this, string2);
    }

    public void setContact(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_contact_set(this.swigCPtr, this, string2);
    }

    public void setCustom_message(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_custom_message_set(this.swigCPtr, this, string2);
    }

    public void setDisplayname(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_displayname_set(this.swigCPtr, this, string2);
    }

    public void setId(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_id_set(this.swigCPtr, this, string2);
    }

    public void setPresence(vx_buddy_presence_state vx_buddy_presence_state2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_presence_set(this.swigCPtr, this, vx_buddy_presence_state2.swigValue());
    }

    public void setPriority(String string2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_priority_set(this.swigCPtr, this, string2);
    }

    public void setState(vx_buddy_presence_state vx_buddy_presence_state2) {
        VxClientProxyJNI.vx_evt_buddy_presence_t_state_set(this.swigCPtr, this, vx_buddy_presence_state2.swigValue());
    }
}

