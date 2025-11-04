// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import java.util.RandomAccess;
import java.util.AbstractList;

final class ImmutableList<T> extends AbstractList<T> implements RandomAccess
{
    private final T[] views;
    
    ImmutableList(final T[] views) {
        this.views = views;
    }
    
    @Override
    public boolean contains(final Object o) {
        final T[] views = this.views;
        for (int length = views.length, i = 0; i < length; ++i) {
            if (views[i] == o) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public T get(final int n) {
        return this.views[n];
    }
    
    @Override
    public int size() {
        return this.views.length;
    }
}
