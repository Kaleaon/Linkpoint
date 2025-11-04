// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Set;
import com.google.common.annotations.Beta;

@Beta
public interface RangeSet<C extends Comparable>
{
    void add(final Range<C> p0);
    
    void addAll(final RangeSet<C> p0);
    
    Set<Range<C>> asDescendingSetOfRanges();
    
    Set<Range<C>> asRanges();
    
    void clear();
    
    RangeSet<C> complement();
    
    boolean contains(final C p0);
    
    boolean encloses(final Range<C> p0);
    
    boolean enclosesAll(final RangeSet<C> p0);
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
    
    boolean isEmpty();
    
    Range<C> rangeContaining(final C p0);
    
    void remove(final Range<C> p0);
    
    void removeAll(final RangeSet<C> p0);
    
    Range<C> span();
    
    RangeSet<C> subRangeSet(final Range<C> p0);
    
    String toString();
}
