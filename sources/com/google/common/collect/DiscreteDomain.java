package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.lang.Comparable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@GwtCompatible
@Beta
public abstract class DiscreteDomain<C extends Comparable> {

    private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {
        /* access modifiers changed from: private */
        public static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
        private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        private static final long serialVersionUID = 0;

        private BigIntegerDomain() {
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public long distance(BigInteger bigInteger, BigInteger bigInteger2) {
            return bigInteger2.subtract(bigInteger).max(MIN_LONG).min(MAX_LONG).longValue();
        }

        public BigInteger next(BigInteger bigInteger) {
            return bigInteger.add(BigInteger.ONE);
        }

        public BigInteger previous(BigInteger bigInteger) {
            return bigInteger.subtract(BigInteger.ONE);
        }

        public String toString() {
            return "DiscreteDomain.bigIntegers()";
        }
    }

    private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {
        /* access modifiers changed from: private */
        public static final IntegerDomain INSTANCE = new IntegerDomain();
        private static final long serialVersionUID = 0;

        private IntegerDomain() {
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public long distance(Integer num, Integer num2) {
            return ((long) num2.intValue()) - ((long) num.intValue());
        }

        public Integer maxValue() {
            return Integer.MAX_VALUE;
        }

        public Integer minValue() {
            return Integer.MIN_VALUE;
        }

        public Integer next(Integer num) {
            int intValue = num.intValue();
            if (intValue != Integer.MAX_VALUE) {
                return Integer.valueOf(intValue + 1);
            }
            return null;
        }

        public Integer previous(Integer num) {
            int intValue = num.intValue();
            if (intValue != Integer.MIN_VALUE) {
                return Integer.valueOf(intValue - 1);
            }
            return null;
        }

        public String toString() {
            return "DiscreteDomain.integers()";
        }
    }

    private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {
        /* access modifiers changed from: private */
        public static final LongDomain INSTANCE = new LongDomain();
        private static final long serialVersionUID = 0;

        private LongDomain() {
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public long distance(Long l, Long l2) {
            boolean z = true;
            long longValue = l2.longValue() - l.longValue();
            if (!(l2.longValue() <= l.longValue())) {
                if (!(longValue >= 0)) {
                    return Long.MAX_VALUE;
                }
            }
            if (!(l2.longValue() >= l.longValue())) {
                if (longValue > 0) {
                    z = false;
                }
                if (!z) {
                    return Long.MIN_VALUE;
                }
            }
            return longValue;
        }

        public Long maxValue() {
            return Long.MAX_VALUE;
        }

        public Long minValue() {
            return Long.MIN_VALUE;
        }

        public Long next(Long l) {
            long longValue = l.longValue();
            if (longValue == Long.MAX_VALUE) {
                return null;
            }
            return Long.valueOf(longValue + 1);
        }

        public Long previous(Long l) {
            long longValue = l.longValue();
            if (longValue == Long.MIN_VALUE) {
                return null;
            }
            return Long.valueOf(longValue - 1);
        }

        public String toString() {
            return "DiscreteDomain.longs()";
        }
    }

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

    public abstract long distance(C c, C c2);

    public C maxValue() {
        throw new NoSuchElementException();
    }

    public C minValue() {
        throw new NoSuchElementException();
    }

    public abstract C next(C c);

    public abstract C previous(C c);
}
