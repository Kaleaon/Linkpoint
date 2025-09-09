package com.google.common.collect;

import com.google.common.annotations.Beta;

@Beta
public interface Interner<E> {
    E intern(E e);
}
