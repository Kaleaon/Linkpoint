package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {
    private static final int CUTOFF = 751619276;
    private static final double DESIRED_LOAD_FACTOR = 0.7d;
    static final int MAX_TABLE_SIZE = 1073741824;

    public static class Builder<E> extends ImmutableCollection.ArrayBasedBuilder<E> {
        public Builder() {
            this(4);
        }

        Builder(int i) {
            super(i);
        }

        public Builder<E> add(E e) {
            super.add(e);
            return this;
        }

        public Builder<E> add(E... eArr) {
            super.add(eArr);
            return this;
        }

        public Builder<E> addAll(Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }

        public Builder<E> addAll(Iterator<? extends E> it) {
            super.addAll(it);
            return this;
        }

        public ImmutableSet<E> build() {
            ImmutableSet<E> access$000 = ImmutableSet.construct(this.size, this.contents);
            this.size = access$000.size();
            return access$000;
        }
    }

    static abstract class Indexed<E> extends ImmutableSet<E> {
        Indexed() {
        }

        /* access modifiers changed from: package-private */
        public ImmutableList<E> createAsList() {
            return new ImmutableAsList<E>() {
                /* access modifiers changed from: package-private */
                public Indexed<E> delegateCollection() {
                    return Indexed.this;
                }

                public E get(int i) {
                    return Indexed.this.get(i);
                }
            };
        }

        /* access modifiers changed from: package-private */
        public abstract E get(int i);

        public UnmodifiableIterator<E> iterator() {
            return asList().iterator();
        }
    }

    private static class SerializedForm implements Serializable {
        private static final long serialVersionUID = 0;
        final Object[] elements;

        SerializedForm(Object[] objArr) {
            this.elements = objArr;
        }

        /* access modifiers changed from: package-private */
        public Object readResolve() {
            return ImmutableSet.copyOf((E[]) this.elements);
        }
    }

    ImmutableSet() {
    }

    public static <E> Builder<E> builder() {
        return new Builder<>();
    }

    @VisibleForTesting
    static int chooseTableSize(int i) {
        if (i >= CUTOFF) {
            Preconditions.checkArgument(i < 1073741824, "collection too large");
            return 1073741824;
        }
        int highestOneBit = Integer.highestOneBit(i - 1);
        while (true) {
            highestOneBit <<= 1;
            if (((double) highestOneBit) * DESIRED_LOAD_FACTOR >= ((double) i)) {
                return highestOneBit;
            }
        }
    }

    /* access modifiers changed from: private */
    public static <E> ImmutableSet<E> construct(int i, Object... objArr) {
        int i2;
        int i3;
        switch (i) {
            case 0:
                return of();
            case 1:
                return of(objArr[0]);
            default:
                int chooseTableSize = chooseTableSize(i);
                Object[] objArr2 = new Object[chooseTableSize];
                int i4 = chooseTableSize - 1;
                int i5 = 0;
                int i6 = 0;
                int i7 = 0;
                while (i5 < i) {
                    Object checkElementNotNull = ObjectArrays.checkElementNotNull(objArr[i5], i5);
                    int hashCode = checkElementNotNull.hashCode();
                    int smear = Hashing.smear(hashCode);
                    while (true) {
                        int i8 = smear & i4;
                        Object obj = objArr2[i8];
                        if (obj == null) {
                            i2 = i6 + 1;
                            objArr[i6] = checkElementNotNull;
                            objArr2[i8] = checkElementNotNull;
                            i3 = i7 + hashCode;
                        } else if (!obj.equals(checkElementNotNull)) {
                            smear++;
                        } else {
                            i2 = i6;
                            i3 = i7;
                        }
                    }
                    i5++;
                    i7 = i3;
                    i6 = i2;
                }
                Arrays.fill(objArr, i6, i, (Object) null);
                if (i6 == 1) {
                    return new SingletonImmutableSet(objArr[0], i7);
                }
                if (chooseTableSize != chooseTableSize(i6)) {
                    return construct(i6, objArr);
                }
                if (i6 < objArr.length) {
                    objArr = ObjectArrays.arraysCopyOf(objArr, i6);
                }
                return new RegularImmutableSet(objArr, i7, objArr2, i4);
        }
    }

    public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> iterable) {
        return !(iterable instanceof Collection) ? copyOf(iterable.iterator()) : copyOf((Collection) iterable);
    }

    public static <E> ImmutableSet<E> copyOf(Collection<? extends E> collection) {
        if ((collection instanceof ImmutableSet) && !(collection instanceof ImmutableSortedSet)) {
            ImmutableSet<E> immutableSet = (ImmutableSet) collection;
            if (!immutableSet.isPartialView()) {
                return immutableSet;
            }
        } else if (collection instanceof EnumSet) {
            return copyOfEnumSet((EnumSet) collection);
        }
        Object[] array = collection.toArray();
        return construct(array.length, array);
    }

    public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> it) {
        if (!it.hasNext()) {
            return of();
        }
        Object next = it.next();
        return it.hasNext() ? new Builder().add((Object) next).addAll((Iterator) it).build() : of(next);
    }

    public static <E> ImmutableSet<E> copyOf(E[] eArr) {
        switch (eArr.length) {
            case 0:
                return of();
            case 1:
                return of(eArr[0]);
            default:
                return construct(eArr.length, (Object[]) eArr.clone());
        }
    }

    private static ImmutableSet copyOfEnumSet(EnumSet enumSet) {
        return ImmutableEnumSet.asImmutable(EnumSet.copyOf(enumSet));
    }

    public static <E> ImmutableSet<E> of() {
        return RegularImmutableSet.EMPTY;
    }

    public static <E> ImmutableSet<E> of(E e) {
        return new SingletonImmutableSet(e);
    }

    public static <E> ImmutableSet<E> of(E e, E e2) {
        return construct(2, e, e2);
    }

    public static <E> ImmutableSet<E> of(E e, E e2, E e3) {
        return construct(3, e, e2, e3);
    }

    public static <E> ImmutableSet<E> of(E e, E e2, E e3, E e4) {
        return construct(4, e, e2, e3, e4);
    }

    public static <E> ImmutableSet<E> of(E e, E e2, E e3, E e4, E e5) {
        return construct(5, e, e2, e3, e4, e5);
    }

    public static <E> ImmutableSet<E> of(E e, E e2, E e3, E e4, E e5, E e6, E... eArr) {
        Object[] objArr = new Object[(eArr.length + 6)];
        objArr[0] = e;
        objArr[1] = e2;
        objArr[2] = e3;
        objArr[3] = e4;
        objArr[4] = e5;
        objArr[5] = e6;
        System.arraycopy(eArr, 0, objArr, 6, eArr.length);
        return construct(objArr.length, objArr);
    }

    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof ImmutableSet) && isHashCodeFast() && ((ImmutableSet) obj).isHashCodeFast() && hashCode() != obj.hashCode()) {
            return false;
        }
        return Sets.equalsImpl(this, obj);
    }

    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }

    /* access modifiers changed from: package-private */
    public boolean isHashCodeFast() {
        return false;
    }

    public abstract UnmodifiableIterator<E> iterator();

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return new SerializedForm(toArray());
    }
}
