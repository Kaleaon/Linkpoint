// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class GeneralRange<T> implements Serializable
{
    private final Comparator<? super T> comparator;
    private final boolean hasLowerBound;
    private final boolean hasUpperBound;
    private final BoundType lowerBoundType;
    @Nullable
    private final T lowerEndpoint;
    private transient GeneralRange<T> reverse;
    private final BoundType upperBoundType;
    @Nullable
    private final T upperEndpoint;
    
    private GeneralRange(final Comparator<? super T> comparator, final boolean hasLowerBound, @Nullable final T lowerEndpoint, final BoundType boundType, final boolean hasUpperBound, @Nullable final T upperEndpoint, final BoundType boundType2) {
        boolean b = false;
        this.comparator = Preconditions.checkNotNull(comparator);
        this.hasLowerBound = hasLowerBound;
        this.hasUpperBound = hasUpperBound;
        this.lowerEndpoint = lowerEndpoint;
        this.lowerBoundType = Preconditions.checkNotNull(boundType);
        this.upperEndpoint = upperEndpoint;
        this.upperBoundType = Preconditions.checkNotNull(boundType2);
        if (hasLowerBound) {
            comparator.compare(lowerEndpoint, lowerEndpoint);
        }
        if (hasUpperBound) {
            comparator.compare(upperEndpoint, upperEndpoint);
        }
        if (hasLowerBound && hasUpperBound) {
            final int compare = comparator.compare(lowerEndpoint, upperEndpoint);
            Preconditions.checkArgument(compare <= 0, "lowerEndpoint (%s) > upperEndpoint (%s)", lowerEndpoint, upperEndpoint);
            if (compare == 0) {
                boolean b2;
                if (boundType == BoundType.OPEN) {
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                if (boundType2 != BoundType.OPEN) {
                    b = true;
                }
                Preconditions.checkArgument(b2 | b);
            }
        }
    }
    
    static <T> GeneralRange<T> all(final Comparator<? super T> comparator) {
        return new GeneralRange<T>(comparator, false, null, BoundType.OPEN, false, null, BoundType.OPEN);
    }
    
    static <T> GeneralRange<T> downTo(final Comparator<? super T> comparator, @Nullable final T t, final BoundType boundType) {
        return new GeneralRange<T>(comparator, true, t, boundType, false, null, BoundType.OPEN);
    }
    
    static <T extends Comparable> GeneralRange<T> from(final Range<T> range) {
        Comparable lowerEndpoint;
        if (!range.hasLowerBound()) {
            lowerEndpoint = null;
        }
        else {
            lowerEndpoint = range.lowerEndpoint();
        }
        BoundType boundType;
        if (!range.hasLowerBound()) {
            boundType = BoundType.OPEN;
        }
        else {
            boundType = range.lowerBoundType();
        }
        Comparable upperEndpoint;
        if (!range.hasUpperBound()) {
            upperEndpoint = null;
        }
        else {
            upperEndpoint = range.upperEndpoint();
        }
        BoundType boundType2;
        if (!range.hasUpperBound()) {
            boundType2 = BoundType.OPEN;
        }
        else {
            boundType2 = range.upperBoundType();
        }
        return new GeneralRange<T>((Comparator<? super Object>)Ordering.natural(), range.hasLowerBound(), lowerEndpoint, boundType, range.hasUpperBound(), upperEndpoint, boundType2);
    }
    
    static <T> GeneralRange<T> range(final Comparator<? super T> comparator, @Nullable final T t, final BoundType boundType, @Nullable final T t2, final BoundType boundType2) {
        return new GeneralRange<T>(comparator, true, t, boundType, true, t2, boundType2);
    }
    
    static <T> GeneralRange<T> upTo(final Comparator<? super T> comparator, @Nullable final T t, final BoundType boundType) {
        return new GeneralRange<T>(comparator, false, null, BoundType.OPEN, true, t, boundType);
    }
    
    Comparator<? super T> comparator() {
        return this.comparator;
    }
    
    boolean contains(@Nullable final T t) {
        boolean b = false;
        if (!this.tooLow(t) && !this.tooHigh(t)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        final boolean b = false;
        if (!(o instanceof GeneralRange)) {
            return false;
        }
        final GeneralRange generalRange = (GeneralRange)o;
        boolean b2;
        if (!this.comparator.equals(generalRange.comparator)) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.hasLowerBound == generalRange.hasLowerBound) {
                b2 = b;
                if (this.hasUpperBound == generalRange.hasUpperBound) {
                    b2 = b;
                    if (this.getLowerBoundType().equals(generalRange.getLowerBoundType())) {
                        b2 = b;
                        if (this.getUpperBoundType().equals(generalRange.getUpperBoundType())) {
                            b2 = b;
                            if (Objects.equal(this.getLowerEndpoint(), generalRange.getLowerEndpoint())) {
                                b2 = b;
                                if (Objects.equal(this.getUpperEndpoint(), generalRange.getUpperEndpoint())) {
                                    b2 = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    BoundType getLowerBoundType() {
        return this.lowerBoundType;
    }
    
    T getLowerEndpoint() {
        return this.lowerEndpoint;
    }
    
    BoundType getUpperBoundType() {
        return this.upperBoundType;
    }
    
    T getUpperEndpoint() {
        return this.upperEndpoint;
    }
    
    boolean hasLowerBound() {
        return this.hasLowerBound;
    }
    
    boolean hasUpperBound() {
        return this.hasUpperBound;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.comparator, this.getLowerEndpoint(), this.getLowerBoundType(), this.getUpperEndpoint(), this.getUpperBoundType());
    }
    
    GeneralRange<T> intersect(final GeneralRange<T> generalRange) {
        Preconditions.checkNotNull(generalRange);
        Preconditions.checkArgument(this.comparator.equals(generalRange.comparator));
        final boolean hasLowerBound = this.hasLowerBound;
        final T lowerEndpoint = this.getLowerEndpoint();
        final BoundType lowerBoundType = this.getLowerBoundType();
        boolean hasLowerBound2 = false;
        Object o = null;
        BoundType boundType = null;
        Label_0061: {
            if (this.hasLowerBound()) {
                if (!generalRange.hasLowerBound()) {
                    hasLowerBound2 = hasLowerBound;
                    o = lowerEndpoint;
                    boundType = lowerBoundType;
                }
                else {
                    final int compare = this.comparator.compare(this.getLowerEndpoint(), generalRange.getLowerEndpoint());
                    if (compare >= 0) {
                        boundType = lowerBoundType;
                        o = lowerEndpoint;
                        hasLowerBound2 = hasLowerBound;
                        if (compare != 0) {
                            break Label_0061;
                        }
                        if (generalRange.getLowerBoundType() != BoundType.OPEN) {
                            boundType = lowerBoundType;
                            o = lowerEndpoint;
                            hasLowerBound2 = hasLowerBound;
                            break Label_0061;
                        }
                    }
                    o = generalRange.getLowerEndpoint();
                    boundType = generalRange.getLowerBoundType();
                    hasLowerBound2 = hasLowerBound;
                }
            }
            else {
                hasLowerBound2 = generalRange.hasLowerBound;
                o = generalRange.getLowerEndpoint();
                boundType = generalRange.getLowerBoundType();
            }
        }
        final boolean hasUpperBound = this.hasUpperBound;
        final T upperEndpoint = this.getUpperEndpoint();
        final BoundType upperBoundType = this.getUpperBoundType();
        BoundType boundType2 = null;
        Object o2 = null;
        boolean hasUpperBound2 = false;
        Label_0103: {
            if (this.hasUpperBound()) {
                if (!generalRange.hasUpperBound()) {
                    boundType2 = upperBoundType;
                    o2 = upperEndpoint;
                    hasUpperBound2 = hasUpperBound;
                }
                else {
                    final int compare2 = this.comparator.compare(this.getUpperEndpoint(), generalRange.getUpperEndpoint());
                    if (compare2 <= 0) {
                        hasUpperBound2 = hasUpperBound;
                        o2 = upperEndpoint;
                        boundType2 = upperBoundType;
                        if (compare2 != 0) {
                            break Label_0103;
                        }
                        if (generalRange.getUpperBoundType() != BoundType.OPEN) {
                            hasUpperBound2 = hasUpperBound;
                            o2 = upperEndpoint;
                            boundType2 = upperBoundType;
                            break Label_0103;
                        }
                    }
                    o2 = generalRange.getUpperEndpoint();
                    boundType2 = generalRange.getUpperBoundType();
                    hasUpperBound2 = hasUpperBound;
                }
            }
            else {
                hasUpperBound2 = generalRange.hasUpperBound;
                o2 = generalRange.getUpperEndpoint();
                boundType2 = generalRange.getUpperBoundType();
            }
        }
        Object o3;
        BoundType open;
        if (hasLowerBound2 && hasUpperBound2) {
            final int compare3 = this.comparator.compare((Object)o, (Object)o2);
            if (compare3 <= 0 && (compare3 != 0 || boundType != BoundType.OPEN || boundType2 != BoundType.OPEN)) {
                o3 = o;
                open = boundType;
            }
            else {
                open = BoundType.OPEN;
                boundType2 = BoundType.CLOSED;
                o3 = o2;
            }
        }
        else {
            o3 = o;
            open = boundType;
        }
        return new GeneralRange<T>((Comparator<? super Object>)this.comparator, hasLowerBound2, o3, open, hasUpperBound2, o2, boundType2);
    }
    
    boolean isEmpty() {
        boolean b = false;
        if (this.hasUpperBound() && this.tooLow(this.getUpperEndpoint())) {
            return true;
        }
        if (this.hasLowerBound()) {
            if (this.tooHigh(this.getLowerEndpoint())) {
                return true;
            }
        }
        return b;
        b = true;
        return b;
    }
    
    GeneralRange<T> reverse() {
        final GeneralRange<T> reverse = this.reverse;
        if (reverse != null) {
            return reverse;
        }
        final GeneralRange reverse2 = new GeneralRange(Ordering.from(this.comparator).reverse(), this.hasUpperBound, this.getUpperEndpoint(), this.getUpperBoundType(), this.hasLowerBound, this.getLowerEndpoint(), this.getLowerBoundType());
        reverse2.reverse = this;
        return this.reverse = reverse2;
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append(this.comparator).append(":");
        char c;
        if (this.lowerBoundType != BoundType.CLOSED) {
            c = '(';
        }
        else {
            c = '[';
        }
        final StringBuilder append2 = append.append(c);
        Object lowerEndpoint;
        if (!this.hasLowerBound) {
            lowerEndpoint = "-\u221e";
        }
        else {
            lowerEndpoint = this.lowerEndpoint;
        }
        final StringBuilder append3 = append2.append(lowerEndpoint).append(',');
        Object upperEndpoint;
        if (!this.hasUpperBound) {
            upperEndpoint = "\u221e";
        }
        else {
            upperEndpoint = this.upperEndpoint;
        }
        final StringBuilder append4 = append3.append(upperEndpoint);
        char c2;
        if (this.upperBoundType != BoundType.CLOSED) {
            c2 = ')';
        }
        else {
            c2 = ']';
        }
        return append4.append(c2).toString();
    }
    
    boolean tooHigh(@Nullable final T t) {
        boolean b = false;
        if (this.hasUpperBound()) {
            final int compare = this.comparator.compare(t, this.getUpperEndpoint());
            boolean b2;
            if (compare <= 0) {
                b2 = false;
            }
            else {
                b2 = true;
            }
            boolean b3;
            if (compare != 0) {
                b3 = false;
            }
            else {
                b3 = true;
            }
            if (this.getUpperBoundType() == BoundType.OPEN) {
                b = true;
            }
            return (b3 & b) | b2;
        }
        return false;
    }
    
    boolean tooLow(@Nullable final T t) {
        boolean b = false;
        if (this.hasLowerBound()) {
            final int compare = this.comparator.compare(t, this.getLowerEndpoint());
            boolean b2;
            if (compare >= 0) {
                b2 = false;
            }
            else {
                b2 = true;
            }
            boolean b3;
            if (compare != 0) {
                b3 = false;
            }
            else {
                b3 = true;
            }
            if (this.getLowerBoundType() == BoundType.OPEN) {
                b = true;
            }
            return (b3 & b) | b2;
        }
        return false;
    }
}
