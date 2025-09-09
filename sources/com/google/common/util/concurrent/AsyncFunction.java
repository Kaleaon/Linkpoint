package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public interface AsyncFunction<I, O> {
    ListenableFuture<O> apply(@Nullable I i) throws Exception;
}
