// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference<T> extends PhantomReference<T> implements FinalizableReference
{
    protected FinalizablePhantomReference(final T referent, final FinalizableReferenceQueue finalizableReferenceQueue) {
        super(referent, finalizableReferenceQueue.queue);
        finalizableReferenceQueue.cleanUp();
    }
}
