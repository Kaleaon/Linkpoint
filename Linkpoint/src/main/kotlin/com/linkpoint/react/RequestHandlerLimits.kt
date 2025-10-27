package com.linkpoint.react

interface RequestHandlerLimits {
     fun getMaxRequestsInFlight(): Int)

     fun getRequestTimeout(): Long)

     fun isRequestCancellable(): Boolean)
}
