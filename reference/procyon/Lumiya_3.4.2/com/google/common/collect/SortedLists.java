// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.RandomAccess;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import java.util.List;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
final class SortedLists
{
    private SortedLists() {
    }
    
    public static <E, K extends Comparable> int binarySearch(final List<E> list, final Function<? super E, K> function, @Nullable final K k, final KeyPresentBehavior keyPresentBehavior, final KeyAbsentBehavior keyAbsentBehavior) {
        return binarySearch(list, function, k, Ordering.natural(), keyPresentBehavior, keyAbsentBehavior);
    }
    
    public static <E, K> int binarySearch(final List<E> list, final Function<? super E, K> function, @Nullable final K k, final Comparator<? super K> comparator, final KeyPresentBehavior keyPresentBehavior, final KeyAbsentBehavior keyAbsentBehavior) {
        return binarySearch(Lists.transform(list, (Function<? super E, ? extends K>)function), k, comparator, keyPresentBehavior, keyAbsentBehavior);
    }
    
    public static <E extends Comparable> int binarySearch(final List<? extends E> list, final E e, final KeyPresentBehavior keyPresentBehavior, final KeyAbsentBehavior keyAbsentBehavior) {
        Preconditions.checkNotNull(e);
        return binarySearch(list, e, Ordering.natural(), keyPresentBehavior, keyAbsentBehavior);
    }
    
    public static <E> int binarySearch(List<? extends E> arrayList, @Nullable final E e, final Comparator<? super E> comparator, final KeyPresentBehavior keyPresentBehavior, final KeyAbsentBehavior keyAbsentBehavior) {
        int i = 0;
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull((List<? extends E>)arrayList);
        Preconditions.checkNotNull(keyPresentBehavior);
        Preconditions.checkNotNull(keyAbsentBehavior);
        if (!(arrayList instanceof RandomAccess)) {
            arrayList = Lists.newArrayList((Iterable<?>)arrayList);
        }
        int n = arrayList.size() - 1;
        while (i <= n) {
            final int n2 = i + n >>> 1;
            final int compare = comparator.compare(e, (E)arrayList.get(n2));
            if (compare >= 0) {
                if (compare <= 0) {
                    return keyPresentBehavior.resultIndex(comparator, e, arrayList.subList(i, n + 1), n2 - i) + i;
                }
                i = n2 + 1;
            }
            else {
                n = n2 - 1;
            }
        }
        return keyAbsentBehavior.resultIndex(i);
    }
    
    public enum KeyAbsentBehavior
    {
        INVERTED_INSERTION_INDEX {
            public int resultIndex(final int n) {
                return ~n;
            }
        }, 
        NEXT_HIGHER {
            public int resultIndex(final int n) {
                return n;
            }
        }, 
        NEXT_LOWER {
            @Override
            int resultIndex(final int n) {
                return n - 1;
            }
        };
        
        abstract int resultIndex(final int p0);
    }
    
    public enum KeyPresentBehavior
    {
        ANY_PRESENT {
            @Override
             <E> int resultIndex(final Comparator<? super E> comparator, final E e, final List<? extends E> list, final int n) {
                return n;
            }
        }, 
        FIRST_AFTER {
            public <E> int resultIndex(final Comparator<? super E> comparator, final E e, final List<? extends E> list, final int n) {
                return SortedLists$KeyPresentBehavior$4.LAST_PRESENT.resultIndex(comparator, e, list, n) + 1;
            }
        }, 
        FIRST_PRESENT {
            @Override
             <E> int resultIndex(final Comparator<? super E> comparator, final E e, final List<? extends E> list, int n) {
                int i = 0;
                while (i < n) {
                    final int n2 = i + n >>> 1;
                    if (comparator.compare((E)list.get(n2), e) >= 0) {
                        n = n2;
                    }
                    else {
                        i = n2 + 1;
                    }
                }
                return i;
            }
        }, 
        LAST_BEFORE {
            public <E> int resultIndex(final Comparator<? super E> comparator, final E e, final List<? extends E> list, final int n) {
                return SortedLists$KeyPresentBehavior$5.FIRST_PRESENT.resultIndex(comparator, e, list, n) - 1;
            }
        }, 
        LAST_PRESENT {
            @Override
             <E> int resultIndex(final Comparator<? super E> comparator, final E e, final List<? extends E> list, int i) {
                int n = list.size() - 1;
                while (i < n) {
                    final int n2 = i + n + 1 >>> 1;
                    if (comparator.compare((E)list.get(n2), e) <= 0) {
                        i = n2;
                    }
                    else {
                        n = n2 - 1;
                    }
                }
                return i;
            }
        };
        
        abstract <E> int resultIndex(final Comparator<? super E> p0, final E p1, final List<? extends E> p2, final int p3);
    }
}
