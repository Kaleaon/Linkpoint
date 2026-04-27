/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_channel_moderation_type;
import com.vivox.service.vx_channel_search_type;
import com.vivox.service.vx_req_base_t;

public class vx_req_account_channel_search_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_account_channel_search_t() {
        this(VxClientProxyJNI.new_vx_req_account_channel_search_t(), true);
    }

    protected vx_req_account_channel_search_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_account_channel_search_t vx_req_account_channel_search_t2) {
        if (vx_req_account_channel_search_t2 != null) return vx_req_account_channel_search_t2.swigCPtr;
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
        return VxClientProxyJNI.vx_req_account_channel_search_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_account_channel_search_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getBegins_with() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_begins_with_get(this.swigCPtr, this);
    }

    public int getChannel_active() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_channel_active_get(this.swigCPtr, this);
    }

    public String getChannel_description() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_channel_description_get(this.swigCPtr, this);
    }

    public String getChannel_name() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_channel_name_get(this.swigCPtr, this);
    }

    public vx_channel_search_type getChannel_type() {
        return vx_channel_search_type.swigToEnum(VxClientProxyJNI.vx_req_account_channel_search_t_channel_type_get(this.swigCPtr, this));
    }

    public vx_channel_moderation_type getModeration_type() {
        return vx_channel_moderation_type.swigToEnum(VxClientProxyJNI.vx_req_account_channel_search_t_moderation_type_get(this.swigCPtr, this));
    }

    public int getPage_number() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_page_number_get(this.swigCPtr, this);
    }

    public int getPage_size() {
        return VxClientProxyJNI.vx_req_account_channel_search_t_page_size_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setBegins_with(int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_begins_with_set(this.swigCPtr, this, n);
    }

    public void setChannel_active(int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_active_set(this.swigCPtr, this, n);
    }

    public void setChannel_description(String string2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_description_set(this.swigCPtr, this, string2);
    }

    public void setChannel_name(String string2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_name_set(this.swigCPtr, this, string2);
    }

    public void setChannel_type(vx_channel_search_type vx_channel_search_type2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_channel_type_set(this.swigCPtr, this, vx_channel_search_type2.swigValue());
    }

    public void setModeration_type(vx_channel_moderation_type vx_channel_moderation_type2) {
        VxClientProxyJNI.vx_req_account_channel_search_t_moderation_type_set(this.swigCPtr, this, vx_channel_moderation_type2.swigValue());
    }

    public void setPage_number(int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_page_number_set(this.swigCPtr, this, n);
    }

    public void setPage_size(int n) {
        VxClientProxyJNI.vx_req_account_channel_search_t_page_size_set(this.swigCPtr, this, n);
    }
}

