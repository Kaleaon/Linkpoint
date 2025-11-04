// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.Beta;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.io.IOException;
import javax.annotation.CheckReturnValue;
import java.util.AbstractList;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class Joiner
{
    private final String separator;
    
    private Joiner(final Joiner joiner) {
        this.separator = joiner.separator;
    }
    
    private Joiner(final String s) {
        this.separator = Preconditions.checkNotNull(s);
    }
    
    private static Iterable<Object> iterable(final Object o, final Object o2, final Object[] array) {
        Preconditions.checkNotNull(array);
        return new AbstractList<Object>() {
            @Override
            public Object get(final int n) {
                switch (n) {
                    default: {
                        return array[n - 2];
                    }
                    case 0: {
                        return o;
                    }
                    case 1: {
                        return o2;
                    }
                }
            }
            
            @Override
            public int size() {
                return array.length + 2;
            }
        };
    }
    
    @CheckReturnValue
    public static Joiner on(final char c) {
        return new Joiner(String.valueOf(c));
    }
    
    @CheckReturnValue
    public static Joiner on(final String s) {
        return new Joiner(s);
    }
    
    public <A extends Appendable> A appendTo(final A a, final Iterable<?> iterable) throws IOException {
        return this.appendTo(a, iterable.iterator());
    }
    
    public final <A extends Appendable> A appendTo(final A a, @Nullable final Object o, @Nullable final Object o2, final Object... array) throws IOException {
        return this.appendTo(a, iterable(o, o2, array));
    }
    
    public <A extends Appendable> A appendTo(final A a, final Iterator<?> iterator) throws IOException {
        Preconditions.checkNotNull(a);
        if (iterator.hasNext()) {
            a.append(this.toString(iterator.next()));
            while (iterator.hasNext()) {
                a.append(this.separator);
                a.append(this.toString(iterator.next()));
            }
        }
        return a;
    }
    
    public final <A extends Appendable> A appendTo(final A a, final Object[] a2) throws IOException {
        return this.appendTo(a, Arrays.asList(a2));
    }
    
    public final StringBuilder appendTo(final StringBuilder sb, final Iterable<?> iterable) {
        return this.appendTo(sb, iterable.iterator());
    }
    
    public final StringBuilder appendTo(final StringBuilder sb, @Nullable final Object o, @Nullable final Object o2, final Object... array) {
        return this.appendTo(sb, (Iterable<?>)iterable(o, o2, array));
    }
    
    public final StringBuilder appendTo(final StringBuilder sb, final Iterator<?> iterator) {
        try {
            this.appendTo(sb, iterator);
            return sb;
        }
        catch (final IOException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    public final StringBuilder appendTo(final StringBuilder sb, final Object[] a) {
        return this.appendTo(sb, (Iterable<?>)Arrays.asList(a));
    }
    
    @CheckReturnValue
    public final String join(final Iterable<?> iterable) {
        return this.join(iterable.iterator());
    }
    
    @CheckReturnValue
    public final String join(@Nullable final Object o, @Nullable final Object o2, final Object... array) {
        return this.join(iterable(o, o2, array));
    }
    
    @CheckReturnValue
    public final String join(final Iterator<?> iterator) {
        return this.appendTo(new StringBuilder(), iterator).toString();
    }
    
    @CheckReturnValue
    public final String join(final Object[] a) {
        return this.join(Arrays.asList(a));
    }
    
    @CheckReturnValue
    public Joiner skipNulls() {
        return new Joiner(this) {
            @Override
            public <A extends Appendable> A appendTo(final A a, final Iterator<?> iterator) throws IOException {
                Preconditions.checkNotNull(a, (Object)"appendable");
                Preconditions.checkNotNull(iterator, (Object)"parts");
                while (iterator.hasNext()) {
                    final Object next = iterator.next();
                    if (next != null) {
                        a.append(Joiner.this.toString(next));
                        break;
                    }
                }
                while (iterator.hasNext()) {
                    final Object next2 = iterator.next();
                    if (next2 != null) {
                        a.append(Joiner.this.separator);
                        a.append(Joiner.this.toString(next2));
                    }
                }
                return a;
            }
            
            @Override
            public Joiner useForNull(final String s) {
                throw new UnsupportedOperationException("already specified skipNulls");
            }
            
            @Override
            public MapJoiner withKeyValueSeparator(final String s) {
                throw new UnsupportedOperationException("can't use .skipNulls() with maps");
            }
        };
    }
    
    CharSequence toString(final Object o) {
        Preconditions.checkNotNull(o);
        CharSequence string;
        if (!(o instanceof CharSequence)) {
            string = o.toString();
        }
        else {
            string = (CharSequence)o;
        }
        return string;
    }
    
    @CheckReturnValue
    public Joiner useForNull(final String s) {
        Preconditions.checkNotNull(s);
        return new Joiner(this) {
            @Override
            public Joiner skipNulls() {
                throw new UnsupportedOperationException("already specified useForNull");
            }
            
            @Override
            CharSequence toString(@Nullable final Object o) {
                CharSequence charSequence;
                if (o != null) {
                    charSequence = Joiner.this.toString(o);
                }
                else {
                    charSequence = s;
                }
                return charSequence;
            }
            
            @Override
            public Joiner useForNull(final String s) {
                throw new UnsupportedOperationException("already specified useForNull");
            }
        };
    }
    
    @CheckReturnValue
    public MapJoiner withKeyValueSeparator(final String s) {
        return new MapJoiner(this, s);
    }
    
    public static final class MapJoiner
    {
        private final Joiner joiner;
        private final String keyValueSeparator;
        
        private MapJoiner(final Joiner joiner, final String s) {
            this.joiner = joiner;
            this.keyValueSeparator = Preconditions.checkNotNull(s);
        }
        
        @Beta
        public <A extends Appendable> A appendTo(final A a, final Iterable<? extends Map.Entry<?, ?>> iterable) throws IOException {
            return this.appendTo(a, iterable.iterator());
        }
        
        @Beta
        public <A extends Appendable> A appendTo(final A a, final Iterator<? extends Map.Entry<?, ?>> iterator) throws IOException {
            Preconditions.checkNotNull(a);
            if (iterator.hasNext()) {
                final Map.Entry<Object, V> entry = iterator.next();
                a.append(this.joiner.toString(entry.getKey()));
                a.append(this.keyValueSeparator);
                a.append(this.joiner.toString(entry.getValue()));
                while (iterator.hasNext()) {
                    a.append(this.joiner.separator);
                    final Map.Entry<Object, V> entry2 = iterator.next();
                    a.append(this.joiner.toString(entry2.getKey()));
                    a.append(this.keyValueSeparator);
                    a.append(this.joiner.toString(entry2.getValue()));
                }
            }
            return a;
        }
        
        public <A extends Appendable> A appendTo(final A a, final Map<?, ?> map) throws IOException {
            return this.appendTo(a, (Iterable<? extends Map.Entry<?, ?>>)map.entrySet());
        }
        
        @Beta
        public StringBuilder appendTo(final StringBuilder sb, final Iterable<? extends Map.Entry<?, ?>> iterable) {
            return this.appendTo(sb, iterable.iterator());
        }
        
        @Beta
        public StringBuilder appendTo(final StringBuilder sb, final Iterator<? extends Map.Entry<?, ?>> iterator) {
            try {
                this.appendTo(sb, iterator);
                return sb;
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        public StringBuilder appendTo(final StringBuilder sb, final Map<?, ?> map) {
            return this.appendTo(sb, (Iterable<? extends Map.Entry<?, ?>>)map.entrySet());
        }
        
        @CheckReturnValue
        @Beta
        public String join(final Iterable<? extends Map.Entry<?, ?>> iterable) {
            return this.join(iterable.iterator());
        }
        
        @CheckReturnValue
        @Beta
        public String join(final Iterator<? extends Map.Entry<?, ?>> iterator) {
            return this.appendTo(new StringBuilder(), iterator).toString();
        }
        
        @CheckReturnValue
        public String join(final Map<?, ?> map) {
            return this.join((Iterable<? extends Map.Entry<?, ?>>)map.entrySet());
        }
        
        @CheckReturnValue
        public MapJoiner useForNull(final String s) {
            return new MapJoiner(this.joiner.useForNull(s), this.keyValueSeparator);
        }
    }
}
