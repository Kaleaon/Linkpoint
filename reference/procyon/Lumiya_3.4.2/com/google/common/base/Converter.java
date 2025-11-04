// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class Converter<A, B> implements Function<A, B>
{
    private final boolean handleNullAutomatically;
    private transient Converter<B, A> reverse;
    
    protected Converter() {
        this(true);
    }
    
    Converter(final boolean handleNullAutomatically) {
        this.handleNullAutomatically = handleNullAutomatically;
    }
    
    public static <A, B> Converter<A, B> from(final Function<? super A, ? extends B> function, final Function<? super B, ? extends A> function2) {
        return new FunctionBasedConverter<A, B>((Function)function, (Function)function2);
    }
    
    public static <T> Converter<T, T> identity() {
        return IdentityConverter.INSTANCE;
    }
    
    public final <C> Converter<A, C> andThen(final Converter<B, C> converter) {
        return (Converter<A, C>)this.doAndThen((Converter<B, Object>)converter);
    }
    
    @Deprecated
    @Nullable
    @Override
    public final B apply(@Nullable final A a) {
        return this.convert(a);
    }
    
    @Nullable
    public final B convert(@Nullable final A a) {
        return this.correctedDoForward(a);
    }
    
    public Iterable<B> convertAll(final Iterable<? extends A> iterable) {
        Preconditions.checkNotNull(iterable, (Object)"fromIterable");
        return new Iterable<B>() {
            @Override
            public Iterator<B> iterator() {
                return new Iterator<B>() {
                    private final Iterator<? extends A> fromIterator = iterable.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.fromIterator.hasNext();
                    }
                    
                    @Override
                    public B next() {
                        return Converter.this.convert(this.fromIterator.next());
                    }
                    
                    @Override
                    public void remove() {
                        this.fromIterator.remove();
                    }
                };
            }
        };
    }
    
    @Nullable
    A correctedDoBackward(@Nullable final B b) {
        A checkNotNull = null;
        if (!this.handleNullAutomatically) {
            return this.doBackward(b);
        }
        if (b != null) {
            checkNotNull = Preconditions.checkNotNull(this.doBackward(b));
        }
        return checkNotNull;
    }
    
    @Nullable
    B correctedDoForward(@Nullable final A a) {
        B checkNotNull = null;
        if (!this.handleNullAutomatically) {
            return this.doForward(a);
        }
        if (a != null) {
            checkNotNull = Preconditions.checkNotNull(this.doForward(a));
        }
        return checkNotNull;
    }
    
     <C> Converter<A, C> doAndThen(final Converter<B, C> converter) {
        return (Converter<A, C>)new ConverterComposition((Converter<Object, Object>)this, (Converter<Object, Object>)Preconditions.checkNotNull(converter));
    }
    
    protected abstract A doBackward(final B p0);
    
    protected abstract B doForward(final A p0);
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        return super.equals(obj);
    }
    
    public Converter<B, A> reverse() {
        Converter<B, A> reverse = this.reverse;
        if (reverse == null) {
            reverse = (Converter<B, A>)new ReverseConverter((Converter<Object, Object>)this);
            this.reverse = reverse;
        }
        return reverse;
    }
    
    private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Converter<A, B> first;
        final Converter<B, C> second;
        
        ConverterComposition(final Converter<A, B> first, final Converter<B, C> second) {
            this.first = first;
            this.second = second;
        }
        
        @Nullable
        @Override
        A correctedDoBackward(@Nullable final C c) {
            return this.first.correctedDoBackward(this.second.correctedDoBackward(c));
        }
        
        @Nullable
        @Override
        C correctedDoForward(@Nullable final A a) {
            return this.second.correctedDoForward(this.first.correctedDoForward(a));
        }
        
        @Override
        protected A doBackward(final C c) {
            throw new AssertionError();
        }
        
        @Override
        protected C doForward(final A a) {
            throw new AssertionError();
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof ConverterComposition)) {
                return false;
            }
            final ConverterComposition converterComposition = (ConverterComposition)o;
            if (this.first.equals(converterComposition.first) && this.second.equals(converterComposition.second)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return this.first.hashCode() * 31 + this.second.hashCode();
        }
        
        @Override
        public String toString() {
            return this.first + ".andThen(" + this.second + ")";
        }
    }
    
    private static final class FunctionBasedConverter<A, B> extends Converter<A, B> implements Serializable
    {
        private final Function<? super B, ? extends A> backwardFunction;
        private final Function<? super A, ? extends B> forwardFunction;
        
        private FunctionBasedConverter(final Function<? super A, ? extends B> function, final Function<? super B, ? extends A> function2) {
            this.forwardFunction = Preconditions.checkNotNull(function);
            this.backwardFunction = Preconditions.checkNotNull(function2);
        }
        
        @Override
        protected A doBackward(final B b) {
            return (A)this.backwardFunction.apply(b);
        }
        
        @Override
        protected B doForward(final A a) {
            return (B)this.forwardFunction.apply(a);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof FunctionBasedConverter)) {
                return false;
            }
            final FunctionBasedConverter functionBasedConverter = (FunctionBasedConverter)o;
            if (this.forwardFunction.equals(functionBasedConverter.forwardFunction) && this.backwardFunction.equals(functionBasedConverter.backwardFunction)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
        }
        
        @Override
        public String toString() {
            return "Converter.from(" + this.forwardFunction + ", " + this.backwardFunction + ")";
        }
    }
    
    private static final class IdentityConverter<T> extends Converter<T, T> implements Serializable
    {
        static final IdentityConverter INSTANCE;
        private static final long serialVersionUID = 0L;
        
        static {
            INSTANCE = new IdentityConverter();
        }
        
        private Object readResolve() {
            return IdentityConverter.INSTANCE;
        }
        
        @Override
         <S> Converter<T, S> doAndThen(final Converter<T, S> converter) {
            return Preconditions.checkNotNull(converter, (Object)"otherConverter");
        }
        
        @Override
        protected T doBackward(final T t) {
            return t;
        }
        
        @Override
        protected T doForward(final T t) {
            return t;
        }
        
        @Override
        public IdentityConverter<T> reverse() {
            return this;
        }
        
        @Override
        public String toString() {
            return "Converter.identity()";
        }
    }
    
    private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Converter<A, B> original;
        
        ReverseConverter(final Converter<A, B> original) {
            this.original = original;
        }
        
        @Nullable
        @Override
        B correctedDoBackward(@Nullable final A a) {
            return this.original.correctedDoForward(a);
        }
        
        @Nullable
        @Override
        A correctedDoForward(@Nullable final B b) {
            return this.original.correctedDoBackward(b);
        }
        
        @Override
        protected B doBackward(final A a) {
            throw new AssertionError();
        }
        
        @Override
        protected A doForward(final B b) {
            throw new AssertionError();
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof ReverseConverter && this.original.equals(((ReverseConverter)o).original);
        }
        
        @Override
        public int hashCode() {
            return ~this.original.hashCode();
        }
        
        @Override
        public Converter<A, B> reverse() {
            return this.original;
        }
        
        @Override
        public String toString() {
            return this.original + ".reverse()";
        }
    }
}
