package com.linkpoint.react

interface RequestHandlerLimits {
    Int getMaxRequestsInFlight()

    Long getRequestTimeout()

    Boolean isRequestCancellable()
}
