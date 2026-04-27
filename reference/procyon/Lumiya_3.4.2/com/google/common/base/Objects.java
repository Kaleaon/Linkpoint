// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.util.Arrays;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Objects
{
    private Objects() {
    }
    
    @CheckReturnValue
    public static boolean equal(@Nullable final Object o, @Nullable final Object obj) {
        final boolean b = false;
        if (o != obj) {
            boolean b2 = b;
            if (o == null) {
                return b2;
            }
            if (!o.equals(obj)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    @Deprecated
    @CheckReturnValue
    public static <T> T firstNonNull(@Nullable final T t, @Nullable final T t2) {
        return MoreObjects.firstNonNull(t, t2);
    }
    
    @CheckReturnValue
    public static int hashCode(@Nullable final Object... a) {
        return Arrays.hashCode(a);
    }
    
    @Deprecated
    @CheckReturnValue
    public static ToStringHelper toStringHelper(final Class<?> clazz) {
        return new ToStringHelper(clazz.getSimpleName());
    }
    
    @Deprecated
    @CheckReturnValue
    public static ToStringHelper toStringHelper(final Object o) {
        return new ToStringHelper(o.getClass().getSimpleName());
    }
    
    @Deprecated
    @CheckReturnValue
    public static ToStringHelper toStringHelper(final String s) {
        return new ToStringHelper(s);
    }
    
    @Deprecated
    public static final class ToStringHelper
    {
        private final String className;
        private ValueHolder holderHead;
        private ValueHolder holderTail;
        private boolean omitNullValues;
        
        private ToStringHelper(final String s) {
            this.holderHead = new ValueHolder();
            this.holderTail = this.holderHead;
            this.omitNullValues = false;
            this.className = Preconditions.checkNotNull(s);
        }
        
        private ValueHolder addHolder() {
            final ValueHolder valueHolder = new ValueHolder();
            this.holderTail.next = valueHolder;
            return this.holderTail = valueHolder;
        }
        
        private ToStringHelper addHolder(@Nullable final Object value) {
            this.addHolder().value = value;
            return this;
        }
        
        private ToStringHelper addHolder(final String s, @Nullable final Object value) {
            final ValueHolder addHolder = this.addHolder();
            addHolder.value = value;
            addHolder.name = Preconditions.checkNotNull(s);
            return this;
        }
        
        public ToStringHelper add(final String s, final char c) {
            return this.addHolder(s, String.valueOf(c));
        }
        
        public ToStringHelper add(final String s, final double d) {
            return this.addHolder(s, String.valueOf(d));
        }
        
        public ToStringHelper add(final String s, final float f) {
            return this.addHolder(s, String.valueOf(f));
        }
        
        public ToStringHelper add(final String s, final int i) {
            return this.addHolder(s, String.valueOf(i));
        }
        
        public ToStringHelper add(final String s, final long l) {
            return this.addHolder(s, String.valueOf(l));
        }
        
        public ToStringHelper add(final String s, @Nullable final Object o) {
            return this.addHolder(s, o);
        }
        
        public ToStringHelper add(final String s, final boolean b) {
            return this.addHolder(s, String.valueOf(b));
        }
        
        public ToStringHelper addValue(final char c) {
            return this.addHolder(String.valueOf(c));
        }
        
        public ToStringHelper addValue(final double d) {
            return this.addHolder(String.valueOf(d));
        }
        
        public ToStringHelper addValue(final float f) {
            return this.addHolder(String.valueOf(f));
        }
        
        public ToStringHelper addValue(final int i) {
            return this.addHolder(String.valueOf(i));
        }
        
        public ToStringHelper addValue(final long l) {
            return this.addHolder(String.valueOf(l));
        }
        
        public ToStringHelper addValue(@Nullable final Object o) {
            return this.addHolder(o);
        }
        
        public ToStringHelper addValue(final boolean b) {
            return this.addHolder(String.valueOf(b));
        }
        
        public ToStringHelper omitNullValues() {
            this.omitNullValues = true;
            return this;
        }
        
        @Override
        public String toString() {
            final boolean omitNullValues = this.omitNullValues;
            final StringBuilder append = new StringBuilder(32).append(this.className).append('{');
            ValueHolder valueHolder = this.holderHead.next;
            String str = "";
            while (valueHolder != null) {
                if (!omitNullValues || valueHolder.value != null) {
                    append.append(str);
                    str = ", ";
                    if (valueHolder.name != null) {
                        append.append(valueHolder.name).append('=');
                    }
                    append.append(valueHolder.value);
                }
                valueHolder = valueHolder.next;
            }
            return append.append('}').toString();
        }
        
        private static final class ValueHolder
        {
            String name;
            ValueHolder next;
            Object value;
        }
    }
}
