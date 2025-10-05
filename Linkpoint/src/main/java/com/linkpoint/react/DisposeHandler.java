package com.linkpoint.react;

import javax.annotation.Nonnull;

public interface DisposeHandler<T> {
    void onDispose(@Nonnull T t);
}
