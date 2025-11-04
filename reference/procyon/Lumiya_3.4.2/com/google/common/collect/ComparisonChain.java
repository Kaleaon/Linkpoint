// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.primitives.Booleans;
import java.util.Comparator;
import javax.annotation.Nullable;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Ints;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public abstract class ComparisonChain
{
    private static final ComparisonChain ACTIVE;
    private static final ComparisonChain GREATER;
    private static final ComparisonChain LESS;
    
    static {
        ACTIVE = new ComparisonChain() {
            ComparisonChain classify(final int n) {
                ComparisonChain comparisonChain;
                if (n >= 0) {
                    if (n <= 0) {
                        comparisonChain = ComparisonChain.ACTIVE;
                    }
                    else {
                        comparisonChain = ComparisonChain.GREATER;
                    }
                }
                else {
                    comparisonChain = ComparisonChain.LESS;
                }
                return comparisonChain;
            }
            
            @Override
            public ComparisonChain compare(final double d1, final double d2) {
                return this.classify(Double.compare(d1, d2));
            }
            
            @Override
            public ComparisonChain compare(final float f1, final float f2) {
                return this.classify(Float.compare(f1, f2));
            }
            
            @Override
            public ComparisonChain compare(final int n, final int n2) {
                return this.classify(Ints.compare(n, n2));
            }
            
            @Override
            public ComparisonChain compare(final long n, final long n2) {
                return this.classify(Longs.compare(n, n2));
            }
            
            @Override
            public ComparisonChain compare(final Comparable comparable, final Comparable comparable2) {
                return this.classify(comparable.compareTo(comparable2));
            }
            
            @Override
            public <T> ComparisonChain compare(@Nullable final T t, @Nullable final T t2, final Comparator<T> comparator) {
                return this.classify(comparator.compare(t, t2));
            }
            
            @Override
            public ComparisonChain compareFalseFirst(final boolean b, final boolean b2) {
                return this.classify(Booleans.compare(b, b2));
            }
            
            @Override
            public ComparisonChain compareTrueFirst(final boolean b, final boolean b2) {
                return this.classify(Booleans.compare(b2, b));
            }
            
            @Override
            public int result() {
                return 0;
            }
        };
        LESS = new InactiveComparisonChain(-1);
        GREATER = new InactiveComparisonChain(1);
    }
    
    private ComparisonChain() {
    }
    
    public static ComparisonChain start() {
        return ComparisonChain.ACTIVE;
    }
    
    public abstract ComparisonChain compare(final double p0, final double p1);
    
    public abstract ComparisonChain compare(final float p0, final float p1);
    
    public abstract ComparisonChain compare(final int p0, final int p1);
    
    public abstract ComparisonChain compare(final long p0, final long p1);
    
    @Deprecated
    public final ComparisonChain compare(final Boolean b, final Boolean b2) {
        return this.compareFalseFirst(b, b2);
    }
    
    public abstract ComparisonChain compare(final Comparable<?> p0, final Comparable<?> p1);
    
    public abstract <T> ComparisonChain compare(@Nullable final T p0, @Nullable final T p1, final Comparator<T> p2);
    
    public abstract ComparisonChain compareFalseFirst(final boolean p0, final boolean p1);
    
    public abstract ComparisonChain compareTrueFirst(final boolean p0, final boolean p1);
    
    public abstract int result();
    
    private static final class InactiveComparisonChain extends ComparisonChain
    {
        final int result;
        
        InactiveComparisonChain(final int result) {
            super(null);
            this.result = result;
        }
        
        @Override
        public ComparisonChain compare(final double n, final double n2) {
            return this;
        }
        
        @Override
        public ComparisonChain compare(final float n, final float n2) {
            return this;
        }
        
        @Override
        public ComparisonChain compare(final int n, final int n2) {
            return this;
        }
        
        @Override
        public ComparisonChain compare(final long n, final long n2) {
            return this;
        }
        
        @Override
        public ComparisonChain compare(@Nullable final Comparable comparable, @Nullable final Comparable comparable2) {
            return this;
        }
        
        @Override
        public <T> ComparisonChain compare(@Nullable final T t, @Nullable final T t2, @Nullable final Comparator<T> comparator) {
            return this;
        }
        
        @Override
        public ComparisonChain compareFalseFirst(final boolean b, final boolean b2) {
            return this;
        }
        
        @Override
        public ComparisonChain compareTrueFirst(final boolean b, final boolean b2) {
            return this;
        }
        
        @Override
        public int result() {
            return this.result;
        }
    }
}
