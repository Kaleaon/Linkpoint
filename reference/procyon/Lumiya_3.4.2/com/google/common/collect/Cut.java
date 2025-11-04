// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible
abstract class Cut<C extends Comparable> implements Comparable<Cut<C>>, Serializable
{
    private static final long serialVersionUID = 0L;
    final C endpoint;
    
    Cut(@Nullable final C endpoint) {
        this.endpoint = endpoint;
    }
    
    static <C extends Comparable> Cut<C> aboveAll() {
        return (Cut<C>)AboveAll.INSTANCE;
    }
    
    static <C extends Comparable> Cut<C> aboveValue(final C c) {
        return new AboveValue<C>(c);
    }
    
    static <C extends Comparable> Cut<C> belowAll() {
        return (Cut<C>)BelowAll.INSTANCE;
    }
    
    static <C extends Comparable> Cut<C> belowValue(final C c) {
        return new BelowValue<C>(c);
    }
    
    Cut<C> canonical(final DiscreteDomain<C> discreteDomain) {
        return this;
    }
    
    @Override
    public int compareTo(final Cut<C> cut) {
        if (cut == belowAll()) {
            return 1;
        }
        if (cut == aboveAll()) {
            return -1;
        }
        final int compareOrThrow = Range.compareOrThrow(this.endpoint, cut.endpoint);
        if (compareOrThrow == 0) {
            return Booleans.compare(this instanceof AboveValue, cut instanceof AboveValue);
        }
        return compareOrThrow;
    }
    
    abstract void describeAsLowerBound(final StringBuilder p0);
    
    abstract void describeAsUpperBound(final StringBuilder p0);
    
    C endpoint() {
        return this.endpoint;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof Cut) {
            final Cut cut = (Cut)o;
            try {
                if (this.compareTo(cut) == 0) {
                    b = true;
                }
                return b;
            }
            catch (final ClassCastException ex) {}
        }
        return false;
    }
    
    abstract C greatestValueBelow(final DiscreteDomain<C> p0);
    
    abstract boolean isLessThan(final C p0);
    
    abstract C leastValueAbove(final DiscreteDomain<C> p0);
    
    abstract BoundType typeAsLowerBound();
    
    abstract BoundType typeAsUpperBound();
    
    abstract Cut<C> withLowerBoundType(final BoundType p0, final DiscreteDomain<C> p1);
    
    abstract Cut<C> withUpperBoundType(final BoundType p0, final DiscreteDomain<C> p1);
    
    private static final class AboveAll extends Cut<Comparable<?>>
    {
        private static final AboveAll INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new AboveAll();
        }
        
        private AboveAll() {
            super(null);
        }
        
        private Object readResolve() {
            return AboveAll.INSTANCE;
        }
        
        @Override
        public int compareTo(final Cut<Comparable<?>> cut) {
            int n;
            if (cut != this) {
                n = 1;
            }
            else {
                n = 0;
            }
            return n;
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            throw new AssertionError();
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            sb.append("+\u221e)");
        }
        
        @Override
        Comparable<?> endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }
        
        @Override
        Comparable<?> greatestValueBelow(final DiscreteDomain<Comparable<?>> discreteDomain) {
            return discreteDomain.maxValue();
        }
        
        @Override
        boolean isLessThan(final Comparable<?> comparable) {
            return false;
        }
        
        @Override
        Comparable<?> leastValueAbove(final DiscreteDomain<Comparable<?>> discreteDomain) {
            throw new AssertionError();
        }
        
        @Override
        public String toString() {
            return "+\u221e";
        }
        
        @Override
        BoundType typeAsLowerBound() {
            throw new AssertionError((Object)"this statement should be unreachable");
        }
        
        @Override
        BoundType typeAsUpperBound() {
            throw new IllegalStateException();
        }
        
        @Override
        Cut<Comparable<?>> withLowerBoundType(final BoundType boundType, final DiscreteDomain<Comparable<?>> discreteDomain) {
            throw new AssertionError((Object)"this statement should be unreachable");
        }
        
        @Override
        Cut<Comparable<?>> withUpperBoundType(final BoundType boundType, final DiscreteDomain<Comparable<?>> discreteDomain) {
            throw new IllegalStateException();
        }
    }
    
    private static final class AboveValue<C extends Comparable> extends Cut<C>
    {
        private static final long serialVersionUID = 0L;
        
        AboveValue(final C c) {
            super(Preconditions.checkNotNull((C)c));
        }
        
        @Override
        Cut<C> canonical(final DiscreteDomain<C> discreteDomain) {
            final Comparable leastValueAbove = this.leastValueAbove(discreteDomain);
            Cut<Comparable> cut;
            if (leastValueAbove == null) {
                cut = Cut.aboveAll();
            }
            else {
                cut = Cut.belowValue(leastValueAbove);
            }
            return (Cut<C>)cut;
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            sb.append('(').append(this.endpoint);
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            sb.append(this.endpoint).append(']');
        }
        
        @Override
        C greatestValueBelow(final DiscreteDomain<C> discreteDomain) {
            return this.endpoint;
        }
        
        @Override
        public int hashCode() {
            return ~this.endpoint.hashCode();
        }
        
        @Override
        boolean isLessThan(final C c) {
            boolean b = false;
            if (Range.compareOrThrow(this.endpoint, c) < 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        C leastValueAbove(final DiscreteDomain<C> discreteDomain) {
            return discreteDomain.next(this.endpoint);
        }
        
        @Override
        public String toString() {
            return "/" + this.endpoint + "\\";
        }
        
        @Override
        BoundType typeAsLowerBound() {
            return BoundType.OPEN;
        }
        
        @Override
        BoundType typeAsUpperBound() {
            return BoundType.CLOSED;
        }
        
        @Override
        Cut<C> withLowerBoundType(final BoundType boundType, final DiscreteDomain<C> discreteDomain) {
            switch (boundType) {
                default: {
                    throw new AssertionError();
                }
                case OPEN: {
                    return this;
                }
                case CLOSED: {
                    final Comparable next = discreteDomain.next(this.endpoint);
                    Cut<Comparable> cut;
                    if (next != null) {
                        cut = Cut.belowValue(next);
                    }
                    else {
                        cut = Cut.belowAll();
                    }
                    return (Cut<C>)cut;
                }
            }
        }
        
        @Override
        Cut<C> withUpperBoundType(final BoundType boundType, final DiscreteDomain<C> discreteDomain) {
            switch (boundType) {
                default: {
                    throw new AssertionError();
                }
                case OPEN: {
                    final Comparable next = discreteDomain.next(this.endpoint);
                    Cut<Comparable> cut;
                    if (next != null) {
                        cut = Cut.belowValue(next);
                    }
                    else {
                        cut = Cut.aboveAll();
                    }
                    return (Cut<C>)cut;
                }
                case CLOSED: {
                    return this;
                }
            }
        }
    }
    
    private static final class BelowAll extends Cut<Comparable<?>>
    {
        private static final BelowAll INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new BelowAll();
        }
        
        private BelowAll() {
            super(null);
        }
        
        private Object readResolve() {
            return BelowAll.INSTANCE;
        }
        
        @Override
        Cut<Comparable<?>> canonical(final DiscreteDomain<Comparable<?>> discreteDomain) {
            try {
                return Cut.belowValue((Comparable<?>)discreteDomain.minValue());
            }
            catch (final NoSuchElementException ex) {
                return this;
            }
        }
        
        @Override
        public int compareTo(final Cut<Comparable<?>> cut) {
            int n;
            if (cut != this) {
                n = -1;
            }
            else {
                n = 0;
            }
            return n;
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            sb.append("(-\u221e");
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            throw new AssertionError();
        }
        
        @Override
        Comparable<?> endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }
        
        @Override
        Comparable<?> greatestValueBelow(final DiscreteDomain<Comparable<?>> discreteDomain) {
            throw new AssertionError();
        }
        
        @Override
        boolean isLessThan(final Comparable<?> comparable) {
            return true;
        }
        
        @Override
        Comparable<?> leastValueAbove(final DiscreteDomain<Comparable<?>> discreteDomain) {
            return discreteDomain.minValue();
        }
        
        @Override
        public String toString() {
            return "-\u221e";
        }
        
        @Override
        BoundType typeAsLowerBound() {
            throw new IllegalStateException();
        }
        
        @Override
        BoundType typeAsUpperBound() {
            throw new AssertionError((Object)"this statement should be unreachable");
        }
        
        @Override
        Cut<Comparable<?>> withLowerBoundType(final BoundType boundType, final DiscreteDomain<Comparable<?>> discreteDomain) {
            throw new IllegalStateException();
        }
        
        @Override
        Cut<Comparable<?>> withUpperBoundType(final BoundType boundType, final DiscreteDomain<Comparable<?>> discreteDomain) {
            throw new AssertionError((Object)"this statement should be unreachable");
        }
    }
    
    private static final class BelowValue<C extends Comparable> extends Cut<C>
    {
        private static final long serialVersionUID = 0L;
        
        BelowValue(final C c) {
            super(Preconditions.checkNotNull((C)c));
        }
        
        @Override
        void describeAsLowerBound(final StringBuilder sb) {
            sb.append('[').append(this.endpoint);
        }
        
        @Override
        void describeAsUpperBound(final StringBuilder sb) {
            sb.append(this.endpoint).append(')');
        }
        
        @Override
        C greatestValueBelow(final DiscreteDomain<C> discreteDomain) {
            return discreteDomain.previous(this.endpoint);
        }
        
        @Override
        public int hashCode() {
            return this.endpoint.hashCode();
        }
        
        @Override
        boolean isLessThan(final C c) {
            boolean b = false;
            if (Range.compareOrThrow(this.endpoint, c) <= 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        C leastValueAbove(final DiscreteDomain<C> discreteDomain) {
            return this.endpoint;
        }
        
        @Override
        public String toString() {
            return "\\" + this.endpoint + "/";
        }
        
        @Override
        BoundType typeAsLowerBound() {
            return BoundType.CLOSED;
        }
        
        @Override
        BoundType typeAsUpperBound() {
            return BoundType.OPEN;
        }
        
        @Override
        Cut<C> withLowerBoundType(final BoundType boundType, final DiscreteDomain<C> discreteDomain) {
            switch (boundType) {
                default: {
                    throw new AssertionError();
                }
                case CLOSED: {
                    return this;
                }
                case OPEN: {
                    final Comparable previous = discreteDomain.previous(this.endpoint);
                    Cut<Comparable> belowAll;
                    if (previous != null) {
                        belowAll = new AboveValue<Comparable>(previous);
                    }
                    else {
                        belowAll = Cut.belowAll();
                    }
                    return (Cut<C>)belowAll;
                }
            }
        }
        
        @Override
        Cut<C> withUpperBoundType(final BoundType boundType, final DiscreteDomain<C> discreteDomain) {
            switch (boundType) {
                default: {
                    throw new AssertionError();
                }
                case CLOSED: {
                    final Comparable previous = discreteDomain.previous(this.endpoint);
                    Cut<Comparable> aboveAll;
                    if (previous != null) {
                        aboveAll = new AboveValue<Comparable>(previous);
                    }
                    else {
                        aboveAll = Cut.aboveAll();
                    }
                    return (Cut<C>)aboveAll;
                }
                case OPEN: {
                    return this;
                }
            }
        }
    }
}
