package com.linkpoint.react;

public interface RequestHandlerLimits {
    int getMaxRequestsInFlight();

    long getRequestTimeout();

    boolean isRequestCancellable();
}
