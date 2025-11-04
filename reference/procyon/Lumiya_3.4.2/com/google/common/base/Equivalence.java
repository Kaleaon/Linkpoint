// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.io.Serializable;
import com.google.common.annotations.Beta;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public abstract class Equivalence<T>
{
    protected Equivalence() {
    }
    
    public static Equivalence<Object> equals() {
        return Equals.INSTANCE;
    }
    
    public static Equivalence<Object> identity() {
        return Identity.INSTANCE;
    }
    
    protected abstract boolean doEquivalent(final T p0, final T p1);
    
    protected abstract int doHash(final T p0);
    
    public final boolean equivalent(@Nullable final T t, @Nullable final T t2) {
        return t == t2 || (t != null && t2 != null && this.doEquivalent(t, t2));
    }
    
    @Beta
    public final Predicate<T> equivalentTo(@Nullable final T t) {
        return new EquivalentToPredicate<T>(this, t);
    }
    
    public final int hash(@Nullable final T t) {
        if (t != null) {
            return this.doHash(t);
        }
        return 0;
    }
    
    public final <F> Equivalence<F> onResultOf(final Function<F, ? extends T> function) {
        return (Equivalence<F>)new FunctionalEquivalence((Function<Object, ?>)function, (Equivalence<Object>)this);
    }
    
    @GwtCompatible(serializable = true)
    public final <S extends T> Equivalence<Iterable<S>> pairwise() {
        return (Equivalence<Iterable<S>>)new PairwiseEquivalence((Equivalence<? super Object>)this);
    }
    
    public final <S extends T> Wrapper<S> wrap(@Nullable final S n) {
        return new Wrapper<S>(this, (Object)n);
    }
    
    static final class Equals extends Equivalence<Object> implements Serializable
    {
        static final Equals INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new Equals();
        }
        
        private Object readResolve() {
            return Equals.INSTANCE;
        }
        
        @Override
        protected boolean doEquivalent(final Object o, final Object obj) {
            return o.equals(obj);
        }
        
        @Override
        protected int doHash(final Object o) {
            return o.hashCode();
        }
    }
    
    private static final class EquivalentToPredicate<T> implements Predicate<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Equivalence<T> equivalence;
        @Nullable
        private final T target;
        
        EquivalentToPredicate(final Equivalence<T> equivalence, @Nullable final T target) {
            this.equivalence = Preconditions.checkNotNull(equivalence);
            this.target = target;
        }
        
        @Override
        public boolean apply(@Nullable final T t) {
            return this.equivalence.equivalent(t, this.target);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = true;
            if (this == o) {
                return true;
            }
            if (!(o instanceof EquivalentToPredicate)) {
                return false;
            }
            final EquivalentToPredicate equivalentToPredicate = (EquivalentToPredicate)o;
            if (!this.equivalence.equals(equivalentToPredicate.equivalence) || !Objects.equal(this.target, equivalentToPredicate.target)) {
                b = false;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.equivalence, this.target);
        }
        
        @Override
        public String toString() {
            return this.equivalence + ".equivalentTo(" + this.target + ")";
        }
    }
    
    static final class Identity extends Equivalence<Object> implements Serializable
    {
        static final Identity INSTANCE;
        private static final long serialVersionUID = 1L;
        
        static {
            INSTANCE = new Identity();
        }
        
        private Object readResolve() {
            return Identity.INSTANCE;
        }
        
        @Override
        protected boolean doEquivalent(final Object o, final Object o2) {
            return false;
        }
        
        @Override
        protected int doHash(final Object o) {
            return System.identityHashCode(o);
        }
    }
    
    public static final class Wrapper<T> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Equivalence<? super T> equivalence;
        @Nullable
        private final T reference;
        
        private Wrapper(final Equivalence<? super T> equivalence, @Nullable final T reference) {
            this.equivalence = Preconditions.checkNotNull(equivalence);
            this.reference = reference;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (o != this) {
                if (o instanceof Wrapper) {
                    final Wrapper wrapper = (Wrapper)o;
                    if (this.equivalence.equals(wrapper.equivalence)) {
                        return this.equivalence.equivalent((Object)this.reference, (Object)wrapper.reference);
                    }
                }
                return false;
            }
            return true;
        }
        
        @Nullable
        public T get() {
            return this.reference;
        }
        
        @Override
        public int hashCode() {
            return this.equivalence.hash(this.reference);
        }
        
        @Override
        public String toString() {
            return this.equivalence + ".wrap(" + this.reference + ")";
        }
    }
}
