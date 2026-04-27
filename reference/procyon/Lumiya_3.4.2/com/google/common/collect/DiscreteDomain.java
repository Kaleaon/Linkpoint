// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.math.BigInteger;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class DiscreteDomain<C extends Comparable>
{
    protected DiscreteDomain() {
    }
    
    public static DiscreteDomain<BigInteger> bigIntegers() {
        return BigIntegerDomain.INSTANCE;
    }
    
    public static DiscreteDomain<Integer> integers() {
        return IntegerDomain.INSTANCE;
    }
    
    public static DiscreteDomain<Long> longs() {
        return LongDomain.INSTANCE;
    }
    
    public abstract long distance(final C p0, final C p1);
    
    public C maxValue() {
        throw new NoSuchElementException();
    }
    
    public C minValue() {
        throw new NoSuchElementException();
    }
    
    public abstract C next(final C p0);
    
    public abstract C previous(final C p0);
    
    private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable
    {
        private static final BigIntegerDomain INSTANCE;
        private static final BigInteger MAX_LONG;
        private static final BigInteger MIN_LONG;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new BigIntegerDomain();
            MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
            MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        }
        
        private Object readResolve() {
            return BigIntegerDomain.INSTANCE;
        }
        
        @Override
        public long distance(final BigInteger val, final BigInteger bigInteger) {
            return bigInteger.subtract(val).max(BigIntegerDomain.MIN_LONG).min(BigIntegerDomain.MAX_LONG).longValue();
        }
        
        @Override
        public BigInteger next(final BigInteger bigInteger) {
            return bigInteger.add(BigInteger.ONE);
        }
        
        @Override
        public BigInteger previous(final BigInteger bigInteger) {
            return bigInteger.subtract(BigInteger.ONE);
        }
        
        @Override
        public String toString() {
            return "DiscreteDomain.bigIntegers()";
        }
    }
    
    private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable
    {
        private static final IntegerDomain INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new IntegerDomain();
        }
        
        private Object readResolve() {
            return IntegerDomain.INSTANCE;
        }
        
        @Override
        public long distance(final Integer n, final Integer n2) {
            return n2 - (long)n;
        }
        
        @Override
        public Integer maxValue() {
            return Integer.MAX_VALUE;
        }
        
        @Override
        public Integer minValue() {
            return Integer.MIN_VALUE;
        }
        
        @Override
        public Integer next(Integer value) {
            final int intValue = value;
            if (intValue != Integer.MAX_VALUE) {
                value = intValue + 1;
            }
            else {
                value = null;
            }
            return value;
        }
        
        @Override
        public Integer previous(Integer value) {
            final int intValue = value;
            if (intValue != Integer.MIN_VALUE) {
                value = intValue - 1;
            }
            else {
                value = null;
            }
            return value;
        }
        
        @Override
        public String toString() {
            return "DiscreteDomain.integers()";
        }
    }
    
    private static final class LongDomain extends DiscreteDomain<Long> implements Serializable
    {
        private static final LongDomain INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new LongDomain();
        }
        
        private Object readResolve() {
            return LongDomain.INSTANCE;
        }
        
        @Override
        public long distance(final Long n, final Long n2) {
            final int n3 = 1;
            final long n4 = n2 - n;
            int n5;
            if (n2 <= n) {
                n5 = 1;
            }
            else {
                n5 = 0;
            }
            if (n5 == 0) {
                int n6;
                if (n4 >= 0L) {
                    n6 = 1;
                }
                else {
                    n6 = 0;
                }
                if (n6 == 0) {
                    return Long.MAX_VALUE;
                }
            }
            int n7;
            if (n2 >= n) {
                n7 = 1;
            }
            else {
                n7 = 0;
            }
            if (n7 == 0) {
                int n8;
                if (n4 <= 0L) {
                    n8 = n3;
                }
                else {
                    n8 = 0;
                }
                if (n8 == 0) {
                    return Long.MIN_VALUE;
                }
            }
            return n4;
        }
        
        @Override
        public Long maxValue() {
            return Long.MAX_VALUE;
        }
        
        @Override
        public Long minValue() {
            return Long.MIN_VALUE;
        }
        
        @Override
        public Long next(Long value) {
            final long longValue = value;
            if (longValue == Long.MAX_VALUE) {
                value = null;
            }
            else {
                value = longValue + 1L;
            }
            return value;
        }
        
        @Override
        public Long previous(Long value) {
            final long longValue = value;
            if (longValue == Long.MIN_VALUE) {
                value = null;
            }
            else {
                value = longValue - 1L;
            }
            return value;
        }
        
        @Override
        public String toString() {
            return "DiscreteDomain.longs()";
        }
    }
}
