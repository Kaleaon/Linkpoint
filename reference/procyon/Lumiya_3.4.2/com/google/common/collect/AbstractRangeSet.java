// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Iterator;

abstract class AbstractRangeSet<C extends Comparable> implements RangeSet<C>
{
    @Override
    public void add(final Range<C> range) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addAll(final RangeSet<C> set) {
        final Iterator<Range<C>> iterator = set.asRanges().iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
    }
    
    @Override
    public void clear() {
        this.remove(Range.all());
    }
    
    @Override
    public boolean contains(final C c) {
        return this.rangeContaining(c) != null;
    }
    
    @Override
    public abstract boolean encloses(final Range<C> p0);
    
    @Override
    public boolean enclosesAll(final RangeSet<C> set) {
        final Iterator<Range<C>> iterator = set.asRanges().iterator();
        while (iterator.hasNext()) {
            if (!this.encloses(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o == this || (o instanceof RangeSet && this.asRanges().equals(((RangeSet)o).asRanges()));
    }
    
    @Override
    public final int hashCode() {
        return this.asRanges().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.asRanges().isEmpty();
    }
    
    @Override
    public abstract Range<C> rangeContaining(final C p0);
    
    @Override
    public void remove(final Range<C> range) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeAll(final RangeSet<C> set) {
        final Iterator<Range<C>> iterator = set.asRanges().iterator();
        while (iterator.hasNext()) {
            this.remove(iterator.next());
        }
    }
    
    @Override
    public final String toString() {
        return this.asRanges().toString();
    }
}
