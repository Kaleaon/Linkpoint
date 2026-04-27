package com.lumiyaviewer.lumiya.react;

public interface RequestHandlerLimits {
    int getMaxRequestsInFlight();

    long getRequestTimeout();

    boolean isRequestCancellable();
}
