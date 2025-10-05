package com.linkpoint.react

import javax.annotation.Nonnull
import javax.annotation.Nullable

interface RequestQueue<K, T> : RequestSource<K, T> {
    K getNextRequest()

    ResultHandler<K, T> getResultHandler()

    Unit returnRequest(K k)

    K waitForRequest() throws InterruptedException
}
