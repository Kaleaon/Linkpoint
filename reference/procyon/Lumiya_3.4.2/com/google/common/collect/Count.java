// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible
final class Count implements Serializable
{
    private int value;
    
    Count(final int value) {
        this.value = value;
    }
    
    public int addAndGet(int value) {
        value += this.value;
        return this.value = value;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (o instanceof Count && ((Count)o).value == this.value) {
            b = true;
        }
        return b;
    }
    
    public int get() {
        return this.value;
    }
    
    public int getAndAdd(final int n) {
        final int value = this.value;
        this.value = value + n;
        return value;
    }
    
    public int getAndSet(final int value) {
        final int value2 = this.value;
        this.value = value;
        return value2;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    public void set(final int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
