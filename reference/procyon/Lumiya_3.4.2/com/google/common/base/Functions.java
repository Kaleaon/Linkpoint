// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.io.Serializable;
import com.google.common.annotations.Beta;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class Functions
{
    private Functions() {
    }
    
    public static <A, B, C> Function<A, C> compose(final Function<B, C> function, final Function<A, ? extends B> function2) {
        return new FunctionComposition<A, Object, C>(function, function2);
    }
    
    public static <E> Function<Object, E> constant(@Nullable final E e) {
        return new ConstantFunction<E>(e);
    }
    
    public static <K, V> Function<K, V> forMap(final Map<K, V> map) {
        return new FunctionForMapNoDefault<K, V>(map);
    }
    
    public static <K, V> Function<K, V> forMap(final Map<K, ? extends V> map, @Nullable final V v) {
        return new ForMapWithDefault<K, V>(map, v);
    }
    
    public static <T> Function<T, Boolean> forPredicate(final Predicate<T> predicate) {
        return new PredicateFunction<T>((Predicate)predicate);
    }
    
    @Beta
    public static <T> Function<Object, T> forSupplier(final Supplier<T> supplier) {
        return new SupplierFunction<T>((Supplier)supplier);
    }
    
    public static <E> Function<E, E> identity() {
        return (Function<E, E>)IdentityFunction.INSTANCE;
    }
    
    public static Function<Object, String> toStringFunction() {
        return ToStringFunction.INSTANCE;
    }
    
    private static class ConstantFunction<E> implements Function<Object, E>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final E value;
        
        public ConstantFunction(@Nullable final E value) {
            this.value = value;
        }
        
        @Override
        public E apply(@Nullable final Object o) {
            return this.value;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof ConstantFunction && Objects.equal(this.value, ((ConstantFunction)o).value);
        }
        
        @Override
        public int hashCode() {
            int hashCode;
            if (this.value != null) {
                hashCode = this.value.hashCode();
            }
            else {
                hashCode = 0;
            }
            return hashCode;
        }
        
        @Override
        public String toString() {
            return "Functions.constant(" + this.value + ")";
        }
    }
    
    private static class ForMapWithDefault<K, V> implements Function<K, V>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final V defaultValue;
        final Map<K, ? extends V> map;
        
        ForMapWithDefault(final Map<K, ? extends V> map, @Nullable final V defaultValue) {
            this.map = Preconditions.checkNotNull(map);
            this.defaultValue = defaultValue;
        }
        
        @Override
        public V apply(@Nullable final K k) {
            V v = (V)this.map.get(k);
            if (v == null && !this.map.containsKey(k)) {
                v = this.defaultValue;
            }
            return v;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof ForMapWithDefault)) {
                return false;
            }
            final ForMapWithDefault forMapWithDefault = (ForMapWithDefault)o;
            if (this.map.equals(forMapWithDefault.map) && Objects.equal(this.defaultValue, forMapWithDefault.defaultValue)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.map, this.defaultValue);
        }
        
        @Override
        public String toString() {
            return "Functions.forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
        }
    }
    
    private static class FunctionComposition<A, B, C> implements Function<A, C>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Function<A, ? extends B> f;
        private final Function<B, C> g;
        
        public FunctionComposition(final Function<B, C> function, final Function<A, ? extends B> function2) {
            this.g = Preconditions.checkNotNull(function);
            this.f = Preconditions.checkNotNull(function2);
        }
        
        @Override
        public C apply(@Nullable final A a) {
            return this.g.apply((B)this.f.apply(a));
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof FunctionComposition)) {
                return false;
            }
            final FunctionComposition functionComposition = (FunctionComposition)o;
            if (this.f.equals(functionComposition.f) && this.g.equals(functionComposition.g)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return this.f.hashCode() ^ this.g.hashCode();
        }
        
        @Override
        public String toString() {
            return this.g + "(" + this.f + ")";
        }
    }
    
    private static class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Map<K, V> map;
        
        FunctionForMapNoDefault(final Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }
        
        @Override
        public V apply(@Nullable final K k) {
            final V value = this.map.get(k);
            Preconditions.checkArgument(value != null || this.map.containsKey(k), "Key '%s' not present in map", k);
            return value;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof FunctionForMapNoDefault && this.map.equals(((FunctionForMapNoDefault)o).map);
        }
        
        @Override
        public int hashCode() {
            return this.map.hashCode();
        }
        
        @Override
        public String toString() {
            return "Functions.forMap(" + this.map + ")";
        }
    }
    
    private enum IdentityFunction implements Function<Object, Object>
    {
        INSTANCE;
        
        @Nullable
        @Override
        public Object apply(@Nullable final Object o) {
            return o;
        }
        
        @Override
        public String toString() {
            return "Functions.identity()";
        }
    }
    
    private static class PredicateFunction<T> implements Function<T, Boolean>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Predicate<T> predicate;
        
        private PredicateFunction(final Predicate<T> predicate) {
            this.predicate = Preconditions.checkNotNull(predicate);
        }
        
        @Override
        public Boolean apply(@Nullable final T t) {
            return this.predicate.apply(t);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof PredicateFunction && this.predicate.equals(((PredicateFunction)o).predicate);
        }
        
        @Override
        public int hashCode() {
            return this.predicate.hashCode();
        }
        
        @Override
        public String toString() {
            return "Functions.forPredicate(" + this.predicate + ")";
        }
    }
    
    private static class SupplierFunction<T> implements Function<Object, T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Supplier<T> supplier;
        
        private SupplierFunction(final Supplier<T> supplier) {
            this.supplier = Preconditions.checkNotNull(supplier);
        }
        
        @Override
        public T apply(@Nullable final Object o) {
            return this.supplier.get();
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof SupplierFunction && this.supplier.equals(((SupplierFunction)o).supplier);
        }
        
        @Override
        public int hashCode() {
            return this.supplier.hashCode();
        }
        
        @Override
        public String toString() {
            return "Functions.forSupplier(" + this.supplier + ")";
        }
    }
    
    private enum ToStringFunction implements Function<Object, String>
    {
        INSTANCE;
        
        @Override
        public String apply(final Object o) {
            Preconditions.checkNotNull(o);
            return o.toString();
        }
        
        @Override
        public String toString() {
            return "Functions.toStringFunction()";
        }
    }
}
