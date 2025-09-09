package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface LongAddable {
    void add(long j);

    void increment();

    long sum();
}
