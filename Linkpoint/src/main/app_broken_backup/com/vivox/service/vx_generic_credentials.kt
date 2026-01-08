/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_generic_credentials {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_generic_credentials() {
        this(VxClientProxyJNI.new_vx_generic_credentials(), true)
    }

    protected vx_generic_credentials(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_generic_credentials vx_generic_credentials2) {
        if (vx_generic_credentials2 != null) return vx_generic_credentials2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L
                return
            }
            this.swigCMemOwn = false
            UnsupportedOperationException unsupportedOperationException = UnsupportedOperationException("C++ destructor does not have access")
            throw unsupportedOperationException
        }
    }

    String getAdmin_password() {
        return VxClientProxyJNI.vx_generic_credentials_admin_password_get(this.swigCPtr, this)
    }

    String getAdmin_username() {
        return VxClientProxyJNI.vx_generic_credentials_admin_username_get(this.swigCPtr, this)
    }

    String getGrant_document() {
        return VxClientProxyJNI.vx_generic_credentials_grant_document_get(this.swigCPtr, this)
    }

    String getServer_url() {
        return VxClientProxyJNI.vx_generic_credentials_server_url_get(this.swigCPtr, this)
    }

    Unit setAdmin_password(String string2) {
        VxClientProxyJNI.vx_generic_credentials_admin_password_set(this.swigCPtr, this, string2)
    }

    Unit setAdmin_username(String string2) {
        VxClientProxyJNI.vx_generic_credentials_admin_username_set(this.swigCPtr, this, string2)
    }

    Unit setGrant_document(String string2) {
        VxClientProxyJNI.vx_generic_credentials_grant_document_set(this.swigCPtr, this, string2)
    }

    Unit setServer_url(String string2) {
        VxClientProxyJNI.vx_generic_credentials_server_url_set(this.swigCPtr, this, string2)
    }
}

