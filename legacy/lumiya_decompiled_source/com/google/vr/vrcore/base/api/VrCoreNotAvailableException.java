package com.google.vr.vrcore.base.api;
/* loaded from: classes.dex */
public final class VrCoreNotAvailableException extends Exception {
    public final int errorCode;

    public VrCoreNotAvailableException(int i) {
        super(VrCoreUtils.getConnectionResultString(i));
        this.errorCode = i;
    }
}
