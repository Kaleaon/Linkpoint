package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;
@Beta
@GwtCompatible
@Deprecated
/* loaded from: classes.dex */
public interface MapConstraint<K, V> {
    void checkKeyValue(@Nullable K k, @Nullable V v);

    String toString();
}
