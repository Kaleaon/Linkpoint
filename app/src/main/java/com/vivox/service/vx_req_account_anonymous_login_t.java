/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_buddy_management_mode;
import com.vivox.service.vx_req_base_t;

public class vx_req_account_anonymous_login_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_account_anonymous_login_t() {
        this(VxClientProxyJNI.new_vx_req_account_anonymous_login_t(), true);
    }

    protected vx_req_account_anonymous_login_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_account_anonymous_login_t vx_req_account_anonymous_login_t2) {
        if (vx_req_account_anonymous_login_t2 != null) return vx_req_account_anonymous_login_t2.swigCPtr;
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

    public String getAcct_mgmt_server() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_acct_mgmt_server_get(this.swigCPtr, this);
    }

    public String getApplication_override() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_application_override_get(this.swigCPtr, this);
    }

    public String getApplication_token() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_application_token_get(this.swigCPtr, this);
    }

    public int getAutopost_crash_dumps() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_autopost_crash_dumps_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_account_anonymous_login_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public vx_buddy_management_mode getBuddy_management_mode() {
        return vx_buddy_management_mode.swigToEnum(VxClientProxyJNI.vx_req_account_anonymous_login_t_buddy_management_mode_get(this.swigCPtr, this));
    }

    public String getConnector_handle() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_connector_handle_get(this.swigCPtr, this);
    }

    public String getDisplayname() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_displayname_get(this.swigCPtr, this);
    }

    public int getEnable_buddies_and_presence() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_enable_buddies_and_presence_get(this.swigCPtr, this);
    }

    public int getParticipant_property_frequency() {
        return VxClientProxyJNI.vx_req_account_anonymous_login_t_participant_property_frequency_get(this.swigCPtr, this);
    }

    public void setAcct_mgmt_server(String string2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_acct_mgmt_server_set(this.swigCPtr, this, string2);
    }

    public void setApplication_override(String string2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_application_override_set(this.swigCPtr, this, string2);
    }

    public void setApplication_token(String string2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_application_token_set(this.swigCPtr, this, string2);
    }

    public void setAutopost_crash_dumps(int n) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_autopost_crash_dumps_set(this.swigCPtr, this, n);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setBuddy_management_mode(vx_buddy_management_mode vx_buddy_management_mode2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_buddy_management_mode_set(this.swigCPtr, this, vx_buddy_management_mode2.swigValue());
    }

    public void setConnector_handle(String string2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_connector_handle_set(this.swigCPtr, this, string2);
    }

    public void setDisplayname(String string2) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_displayname_set(this.swigCPtr, this, string2);
    }

    public void setEnable_buddies_and_presence(int n) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_enable_buddies_and_presence_set(this.swigCPtr, this, n);
    }

    public void setParticipant_property_frequency(int n) {
        VxClientProxyJNI.vx_req_account_anonymous_login_t_participant_property_frequency_set(this.swigCPtr, this, n);
    }
}

