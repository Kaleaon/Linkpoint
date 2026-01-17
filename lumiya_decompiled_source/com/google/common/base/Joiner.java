package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
@GwtCompatible
/* loaded from: classes.dex */
public class Joiner {
    private final String separator;

    /* loaded from: classes.dex */
    public static final class MapJoiner {
        private final Joiner joiner;
        private final String keyValueSeparator;

        private MapJoiner(Joiner joiner, String str) {
            this.joiner = joiner;
            this.keyValueSeparator = (String) Preconditions.checkNotNull(str);
        }

        @Beta
        public <A extends Appendable> A appendTo(A a, Iterable<? extends Map.Entry<?, ?>> iterable) throws IOException {
            return (A) appendTo((MapJoiner) a, iterable.iterator());
        }

        @Beta
        public <A extends Appendable> A appendTo(A a, Iterator<? extends Map.Entry<?, ?>> it) throws IOException {
            Preconditions.checkNotNull(a);
            if (it.hasNext()) {
                Map.Entry<?, ?> next = it.next();
                a.append(this.joiner.toString(next.getKey()));
                a.append(this.keyValueSeparator);
                a.append(this.joiner.toString(next.getValue()));
                while (it.hasNext()) {
                    a.append(this.joiner.separator);
                    Map.Entry<?, ?> next2 = it.next();
                    a.append(this.joiner.toString(next2.getKey()));
                    a.append(this.keyValueSeparator);
                    a.append(this.joiner.toString(next2.getValue()));
                }
            }
            return a;
        }

        public <A extends Appendable> A appendTo(A a, Map<?, ?> map) throws IOException {
            return (A) appendTo((MapJoiner) a, (Iterable<? extends Map.Entry<?, ?>>) map.entrySet());
        }

        @Beta
        public StringBuilder appendTo(StringBuilder sb, Iterable<? extends Map.Entry<?, ?>> iterable) {
            return appendTo(sb, iterable.iterator());
        }

        @Beta
        public StringBuilder appendTo(StringBuilder sb, Iterator<? extends Map.Entry<?, ?>> it) {
            try {
                appendTo((MapJoiner) sb, it);
                return sb;
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }

        public StringBuilder appendTo(StringBuilder sb, Map<?, ?> map) {
            return appendTo(sb, (Iterable<? extends Map.Entry<?, ?>>) map.entrySet());
        }

        @Beta
        @CheckReturnValue
        public String join(Iterable<? extends Map.Entry<?, ?>> iterable) {
            return join(iterable.iterator());
        }

        @Beta
        @CheckReturnValue
        public String join(Iterator<? extends Map.Entry<?, ?>> it) {
            return appendTo(new StringBuilder(), it).toString();
        }

        @CheckReturnValue
        public String join(Map<?, ?> map) {
            return join(map.entrySet());
        }

        @CheckReturnValue
        public MapJoiner useForNull(String str) {
            return new MapJoiner(this.joiner.useForNull(str), this.keyValueSeparator);
        }
    }

    private Joiner(Joiner joiner) {
        this.separator = joiner.separator;
    }

    private Joiner(String str) {
        this.separator = (String) Preconditions.checkNotNull(str);
    }

    private static Iterable<Object> iterable(final Object obj, final Object obj2, final Object[] objArr) {
        Preconditions.checkNotNull(objArr);
        return new AbstractList<Object>() { // from class: com.google.common.base.Joiner.3
            @Override // java.util.AbstractList, java.util.List
            public Object get(int i) {
                switch (i) {
                    case 0:
                        return obj;
                    case 1:
                        return obj2;
                    default:
                        return objArr[i - 2];
                }
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return objArr.length + 2;
            }
        };
    }

    @CheckReturnValue
    public static Joiner on(char c) {
        return new Joiner(String.valueOf(c));
    }

    @CheckReturnValue
    public static Joiner on(String str) {
        return new Joiner(str);
    }

    public <A extends Appendable> A appendTo(A a, Iterable<?> iterable) throws IOException {
        return (A) appendTo((Joiner) a, iterable.iterator());
    }

    public final <A extends Appendable> A appendTo(A a, @Nullable Object obj, @Nullable Object obj2, Object... objArr) throws IOException {
        return (A) appendTo((Joiner) a, iterable(obj, obj2, objArr));
    }

    public <A extends Appendable> A appendTo(A a, Iterator<?> it) throws IOException {
        Preconditions.checkNotNull(a);
        if (it.hasNext()) {
            a.append(toString(it.next()));
            while (it.hasNext()) {
                a.append(this.separator);
                a.append(toString(it.next()));
            }
        }
        return a;
    }

    public final <A extends Appendable> A appendTo(A a, Object[] objArr) throws IOException {
        return (A) appendTo((Joiner) a, (Iterable<?>) Arrays.asList(objArr));
    }

    public final StringBuilder appendTo(StringBuilder sb, Iterable<?> iterable) {
        return appendTo(sb, iterable.iterator());
    }

    public final StringBuilder appendTo(StringBuilder sb, @Nullable Object obj, @Nullable Object obj2, Object... objArr) {
        return appendTo(sb, iterable(obj, obj2, objArr));
    }

    public final StringBuilder appendTo(StringBuilder sb, Iterator<?> it) {
        try {
            appendTo((Joiner) sb, it);
            return sb;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public final StringBuilder appendTo(StringBuilder sb, Object[] objArr) {
        return appendTo(sb, (Iterable<?>) Arrays.asList(objArr));
    }

    @CheckReturnValue
    public final String join(Iterable<?> iterable) {
        return join(iterable.iterator());
    }

    @CheckReturnValue
    public final String join(@Nullable Object obj, @Nullable Object obj2, Object... objArr) {
        return join(iterable(obj, obj2, objArr));
    }

    @CheckReturnValue
    public final String join(Iterator<?> it) {
        return appendTo(new StringBuilder(), it).toString();
    }

    @CheckReturnValue
    public final String join(Object[] objArr) {
        return join(Arrays.asList(objArr));
    }

    @CheckReturnValue
    public Joiner skipNulls() {
        return new Joiner(this) { // from class: com.google.common.base.Joiner.2
            @Override // com.google.common.base.Joiner
            public <A extends Appendable> A appendTo(A a, Iterator<?> it) throws IOException {
                Preconditions.checkNotNull(a, "appendable");
                Preconditions.checkNotNull(it, "parts");
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Object next = it.next();
                    if (next != null) {
                        a.append(Joiner.this.toString(next));
                        break;
                    }
                }
                while (it.hasNext()) {
                    Object next2 = it.next();
                    if (next2 != null) {
                        a.append(Joiner.this.separator);
                        a.append(Joiner.this.toString(next2));
                    }
                }
                return a;
            }

            @Override // com.google.common.base.Joiner
            public Joiner useForNull(String str) {
                throw new UnsupportedOperationException("already specified skipNulls");
            }

            @Override // com.google.common.base.Joiner
            public MapJoiner withKeyValueSeparator(String str) {
                throw new UnsupportedOperationException("can't use .skipNulls() with maps");
            }
        };
    }

    CharSequence toString(Object obj) {
        Preconditions.checkNotNull(obj);
        return !(obj instanceof CharSequence) ? obj.toString() : (CharSequence) obj;
    }

    @CheckReturnValue
    public Joiner useForNull(final String str) {
        Preconditions.checkNotNull(str);
        return new Joiner(this) { // from class: com.google.common.base.Joiner.1
            @Override // com.google.common.base.Joiner
            public Joiner skipNulls() {
                throw new UnsupportedOperationException("already specified useForNull");
            }

            @Override // com.google.common.base.Joiner
            CharSequence toString(@Nullable Object obj) {
                return obj != null ? Joiner.this.toString(obj) : str;
            }

            @Override // com.google.common.base.Joiner
            public Joiner useForNull(String str2) {
                throw new UnsupportedOperationException("already specified useForNull");
            }
        };
    }

    @CheckReturnValue
    public MapJoiner withKeyValueSeparator(String str) {
        return new MapJoiner(str);
    }
}
