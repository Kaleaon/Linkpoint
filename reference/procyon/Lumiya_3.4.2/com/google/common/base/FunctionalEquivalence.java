// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
@GwtCompatible
final class FunctionalEquivalence<F, T> extends Equivalence<F> implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final Function<F, ? extends T> function;
    private final Equivalence<T> resultEquivalence;
    
    FunctionalEquivalence(final Function<F, ? extends T> function, final Equivalence<T> equivalence) {
        this.function = Preconditions.checkNotNull(function);
        this.resultEquivalence = Preconditions.checkNotNull(equivalence);
    }
    
    @Override
    protected boolean doEquivalent(final F n, final F n2) {
        return this.resultEquivalence.equivalent((T)this.function.apply(n), (T)this.function.apply(n2));
    }
    
    @Override
    protected int doHash(final F n) {
        return this.resultEquivalence.hash((T)this.function.apply(n));
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof FunctionalEquivalence)) {
            return false;
        }
        final FunctionalEquivalence functionalEquivalence = (FunctionalEquivalence)o;
        if (!this.function.equals(functionalEquivalence.function) || !this.resultEquivalence.equals(functionalEquivalence.resultEquivalence)) {
            b = false;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.function, this.resultEquivalence);
    }
    
    @Override
    public String toString() {
        return this.resultEquivalence + ".onResultOf(" + this.function + ")";
    }
}
