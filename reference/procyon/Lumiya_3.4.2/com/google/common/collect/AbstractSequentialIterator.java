// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T>
{
    private T nextOrNull;
    
    protected AbstractSequentialIterator(@Nullable final T nextOrNull) {
        this.nextOrNull = nextOrNull;
    }
    
    protected abstract T computeNext(final T p0);
    
    @Override
    public final boolean hasNext() {
        return this.nextOrNull != null;
    }
    
    @Override
    public final T next() {
        Label_0026: {
            if (!this.hasNext()) {
                break Label_0026;
            }
            try {
                return this.nextOrNull;
                throw new NoSuchElementException();
            }
            finally {
                this.nextOrNull = this.computeNext(this.nextOrNull);
            }
        }
    }
}
