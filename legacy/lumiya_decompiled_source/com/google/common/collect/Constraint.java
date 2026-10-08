package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
/* loaded from: classes.dex */
public interface Constraint<E> {
    E checkElement(E e);

    String toString();
}
