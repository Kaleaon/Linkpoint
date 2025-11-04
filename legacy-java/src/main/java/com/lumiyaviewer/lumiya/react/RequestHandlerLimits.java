package com.lumiyaviewer.lumiya.react;

/* loaded from: classes.dex */
public interface RequestHandlerLimits {
    int getMaxRequestsInFlight();

    long getRequestTimeout();

    boolean isRequestCancellable();
}
