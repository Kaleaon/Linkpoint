// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.ConcurrentMap;
import com.google.common.base.Preconditions;
import com.google.common.base.Function;
import com.google.common.annotations.Beta;

@Beta
public final class Interners
{
    private Interners() {
    }
    
    public static <E> Function<E, E> asFunction(final Interner<E> interner) {
        return new InternerFunction<E>(Preconditions.checkNotNull(interner));
    }
    
    public static <E> Interner<E> newStrongInterner() {
        return new Interner<E>() {
            final /* synthetic */ ConcurrentMap val$map = new MapMaker().makeMap();
            
            @Override
            public E intern(E e) {
                final E putIfAbsent = this.val$map.putIfAbsent(Preconditions.checkNotNull(e), e);
                if (putIfAbsent != null) {
                    e = putIfAbsent;
                }
                return e;
            }
        };
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    public static <E> Interner<E> newWeakInterner() {
        return new WeakInterner<E>();
    }
    
    private static class InternerFunction<E> implements Function<E, E>
    {
        private final Interner<E> interner;
        
        public InternerFunction(final Interner<E> interner) {
            this.interner = interner;
        }
        
        @Override
        public E apply(final E e) {
            return this.interner.intern(e);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof InternerFunction && this.interner.equals(((InternerFunction)o).interner);
        }
        
        @Override
        public int hashCode() {
            return this.interner.hashCode();
        }
    }
    
    private static class WeakInterner<E> implements Interner<E>
    {
        private final MapMakerInternalMap<E, Dummy> map;
        
        private WeakInterner() {
            this.map = new MapMaker().weakKeys().keyEquivalence(Equivalence.equals()).makeCustomMap();
        }
        
        @Override
        public E intern(final E e) {
            while (true) {
                final MapMakerInternalMap.ReferenceEntry<E, Dummy> entry = this.map.getEntry(e);
                if (entry != null) {
                    final E key = entry.getKey();
                    if (key != null) {
                        return key;
                    }
                }
                if (this.map.putIfAbsent(e, Dummy.VALUE) == null) {
                    return e;
                }
            }
        }
        
        private enum Dummy
        {
            VALUE;
        }
    }
}
