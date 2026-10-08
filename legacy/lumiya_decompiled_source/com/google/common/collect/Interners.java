package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMakerInternalMap;
import java.util.concurrent.ConcurrentMap;
@Beta
/* loaded from: classes.dex */
public final class Interners {

    /* loaded from: classes.dex */
    private static class InternerFunction<E> implements Function<E, E> {
        private final Interner<E> interner;

        public InternerFunction(Interner<E> interner) {
            this.interner = interner;
        }

        @Override // com.google.common.base.Function
        public E apply(E e) {
            return this.interner.intern(e);
        }

        @Override // com.google.common.base.Function
        public boolean equals(Object obj) {
            if (obj instanceof InternerFunction) {
                return this.interner.equals(((InternerFunction) obj).interner);
            }
            return false;
        }

        public int hashCode() {
            return this.interner.hashCode();
        }
    }

    /* loaded from: classes.dex */
    private static class WeakInterner<E> implements Interner<E> {
        private final MapMakerInternalMap<E, Dummy> map;

        /* loaded from: classes.dex */
        private enum Dummy {
            VALUE
        }

        private WeakInterner() {
            this.map = (MapMakerInternalMap<E, Dummy>) new MapMaker().weakKeys().keyEquivalence2(Equivalence.equals()).makeCustomMap();
        }

        @Override // com.google.common.collect.Interner
        public E intern(E e) {
            E key;
            do {
                MapMakerInternalMap.ReferenceEntry<E, Dummy> entry = this.map.getEntry(e);
                if (entry != null && (key = entry.getKey()) != null) {
                    return key;
                }
            } while (this.map.putIfAbsent(e, Dummy.VALUE) != null);
            return e;
        }
    }

    private Interners() {
    }

    public static <E> Function<E, E> asFunction(Interner<E> interner) {
        return new InternerFunction((Interner) Preconditions.checkNotNull(interner));
    }

    public static <E> Interner<E> newStrongInterner() {
        final ConcurrentMap makeMap = new MapMaker().makeMap();
        return new Interner<E>() { // from class: com.google.common.collect.Interners.1
            @Override // com.google.common.collect.Interner
            public E intern(E e) {
                E e2 = (E) makeMap.putIfAbsent(Preconditions.checkNotNull(e), e);
                return e2 != null ? e2 : e;
            }
        };
    }

    @GwtIncompatible("java.lang.ref.WeakReference")
    public static <E> Interner<E> newWeakInterner() {
        return new WeakInterner();
    }
}
