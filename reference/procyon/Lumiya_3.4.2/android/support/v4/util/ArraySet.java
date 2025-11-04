// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import java.lang.reflect.Array;
import android.support.annotation.RestrictTo;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

public final class ArraySet<E> implements Collection<E>, Set<E>
{
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    private static final int[] INT;
    private static final Object[] OBJECT;
    private static final String TAG = "ArraySet";
    static Object[] sBaseCache;
    static int sBaseCacheSize;
    static Object[] sTwiceBaseCache;
    static int sTwiceBaseCacheSize;
    Object[] mArray;
    MapCollections<E, E> mCollections;
    int[] mHashes;
    final boolean mIdentityHashCode;
    int mSize;
    
    static {
        INT = new int[0];
        OBJECT = new Object[0];
    }
    
    public ArraySet() {
        this(0, false);
    }
    
    public ArraySet(final int n) {
        this(n, false);
    }
    
    public ArraySet(final int n, final boolean mIdentityHashCode) {
        this.mIdentityHashCode = mIdentityHashCode;
        if (n != 0) {
            this.allocArrays(n);
        }
        else {
            this.mHashes = ArraySet.INT;
            this.mArray = ArraySet.OBJECT;
        }
        this.mSize = 0;
    }
    
    public ArraySet(final ArraySet<E> set) {
        this();
        if (set != null) {
            this.addAll((ArraySet<? extends E>)set);
        }
    }
    
    public ArraySet(final Collection<E> collection) {
        this();
        if (collection != null) {
            this.addAll((Collection<? extends E>)collection);
        }
    }
    
    private void allocArrays(final int n) {
        Label_0011: {
            if (n == 8) {
                synchronized (ArraySet.class) {
                    if (ArraySet.sTwiceBaseCache == null) {
                        break Label_0011;
                    }
                }
                final Object[] sTwiceBaseCache = ArraySet.sTwiceBaseCache;
                this.mArray = sTwiceBaseCache;
                ArraySet.sTwiceBaseCache = (Object[])sTwiceBaseCache[0];
                this.mHashes = (int[])sTwiceBaseCache[1];
                sTwiceBaseCache[0] = (sTwiceBaseCache[1] = null);
                --ArraySet.sTwiceBaseCacheSize;
                monitorexit(ArraySet.class);
                return;
            }
            if (n == 4) {
                synchronized (ArraySet.class) {
                    if (ArraySet.sBaseCache == null) {
                        break Label_0011;
                    }
                }
                final Object[] sBaseCache = ArraySet.sBaseCache;
                this.mArray = sBaseCache;
                ArraySet.sBaseCache = (Object[])sBaseCache[0];
                this.mHashes = (int[])sBaseCache[1];
                sBaseCache[0] = (sBaseCache[1] = null);
                --ArraySet.sBaseCacheSize;
                monitorexit(ArraySet.class);
                return;
            }
        }
        this.mHashes = new int[n];
        this.mArray = new Object[n];
    }
    
    private static void freeArrays(final int[] array, final Object[] array2, int i) {
        if (array.length != 8) {
            if (array.length == 4) {
                while (true) {
                    while (true) {
                        synchronized (ArraySet.class) {
                            if (ArraySet.sBaseCacheSize >= 10) {
                                break;
                            }
                        }
                        array2[0] = ArraySet.sBaseCache;
                        final Throwable t;
                        array2[1] = t;
                        --i;
                        while (i >= 2) {
                            array2[i] = null;
                            --i;
                        }
                        ArraySet.sBaseCache = array2;
                        ++ArraySet.sBaseCacheSize;
                        continue;
                    }
                }
            }
        }
        else {
            while (true) {
                while (true) {
                    synchronized (ArraySet.class) {
                        if (ArraySet.sTwiceBaseCacheSize >= 10) {
                            break;
                        }
                    }
                    array2[0] = ArraySet.sTwiceBaseCache;
                    final Throwable t2;
                    array2[1] = t2;
                    --i;
                    while (i >= 2) {
                        array2[i] = null;
                        --i;
                    }
                    ArraySet.sTwiceBaseCache = array2;
                    ++ArraySet.sTwiceBaseCacheSize;
                    continue;
                }
            }
        }
    }
    
    private MapCollections<E, E> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<E, E>() {
                @Override
                protected void colClear() {
                    ArraySet.this.clear();
                }
                
                @Override
                protected Object colGetEntry(final int n, final int n2) {
                    return ArraySet.this.mArray[n];
                }
                
                @Override
                protected Map<E, E> colGetMap() {
                    throw new UnsupportedOperationException("not a map");
                }
                
                @Override
                protected int colGetSize() {
                    return ArraySet.this.mSize;
                }
                
                @Override
                protected int colIndexOfKey(final Object o) {
                    return ArraySet.this.indexOf(o);
                }
                
                @Override
                protected int colIndexOfValue(final Object o) {
                    return ArraySet.this.indexOf(o);
                }
                
                @Override
                protected void colPut(final E e, final E e2) {
                    ArraySet.this.add(e);
                }
                
                @Override
                protected void colRemoveAt(final int n) {
                    ArraySet.this.removeAt(n);
                }
                
                @Override
                protected E colSetValue(final int n, final E e) {
                    throw new UnsupportedOperationException("not a map");
                }
            };
        }
        return this.mCollections;
    }
    
    private int indexOf(final Object o, final int n) {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        final int binarySearch = ContainerHelpers.binarySearch(this.mHashes, mSize, n);
        if (binarySearch < 0) {
            return binarySearch;
        }
        if (!o.equals(this.mArray[binarySearch])) {
            int n2;
            for (n2 = binarySearch + 1; n2 < mSize && this.mHashes[n2] == n; ++n2) {
                if (o.equals(this.mArray[n2])) {
                    return n2;
                }
            }
            for (int n3 = binarySearch - 1; n3 >= 0 && this.mHashes[n3] == n; --n3) {
                if (o.equals(this.mArray[n3])) {
                    return n3;
                }
            }
            return ~n2;
        }
        return binarySearch;
    }
    
    private int indexOfNull() {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        final int binarySearch = ContainerHelpers.binarySearch(this.mHashes, mSize, 0);
        if (binarySearch < 0) {
            return binarySearch;
        }
        if (this.mArray[binarySearch] != null) {
            int n;
            for (n = binarySearch + 1; n < mSize && this.mHashes[n] == 0; ++n) {
                if (this.mArray[n] == null) {
                    return n;
                }
            }
            for (int n2 = binarySearch - 1; n2 >= 0 && this.mHashes[n2] == 0; --n2) {
                if (this.mArray[n2] == null) {
                    return n2;
                }
            }
            return ~n;
        }
        return binarySearch;
    }
    
    @Override
    public boolean add(final E e) {
        int n2;
        int indexOfNull;
        if (e != null) {
            int n;
            if (!this.mIdentityHashCode) {
                n = e.hashCode();
            }
            else {
                n = System.identityHashCode(e);
            }
            final int index = this.indexOf(e, n);
            n2 = n;
            indexOfNull = index;
        }
        else {
            indexOfNull = this.indexOfNull();
            n2 = 0;
        }
        if (indexOfNull < 0) {
            final int n3 = ~indexOfNull;
            if (this.mSize >= this.mHashes.length) {
                int n4;
                if (this.mSize < 8) {
                    if (this.mSize < 4) {
                        n4 = 4;
                    }
                    else {
                        n4 = 8;
                    }
                }
                else {
                    n4 = this.mSize + (this.mSize >> 1);
                }
                final int[] mHashes = this.mHashes;
                final Object[] mArray = this.mArray;
                this.allocArrays(n4);
                if (this.mHashes.length > 0) {
                    System.arraycopy(mHashes, 0, this.mHashes, 0, mHashes.length);
                    System.arraycopy(mArray, 0, this.mArray, 0, mArray.length);
                }
                freeArrays(mHashes, mArray, this.mSize);
            }
            if (n3 < this.mSize) {
                System.arraycopy(this.mHashes, n3, this.mHashes, n3 + 1, this.mSize - n3);
                System.arraycopy(this.mArray, n3, this.mArray, n3 + 1, this.mSize - n3);
            }
            this.mHashes[n3] = n2;
            this.mArray[n3] = e;
            ++this.mSize;
            return true;
        }
        return false;
    }
    
    public void addAll(final ArraySet<? extends E> set) {
        int i = 0;
        final int mSize = set.mSize;
        this.ensureCapacity(this.mSize + mSize);
        if (this.mSize != 0) {
            while (i < mSize) {
                this.add(set.valueAt(i));
                ++i;
            }
        }
        else if (mSize > 0) {
            System.arraycopy(set.mHashes, 0, this.mHashes, 0, mSize);
            System.arraycopy(set.mArray, 0, this.mArray, 0, mSize);
            this.mSize = mSize;
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        boolean b = false;
        this.ensureCapacity(this.mSize + collection.size());
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            b |= this.add(iterator.next());
        }
        return b;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void append(final E e) {
        int n = 0;
        final int mSize = this.mSize;
        if (e != null) {
            if (!this.mIdentityHashCode) {
                n = e.hashCode();
            }
            else {
                n = System.identityHashCode(e);
            }
        }
        if (mSize >= this.mHashes.length) {
            throw new IllegalStateException("Array is full");
        }
        if (mSize > 0 && this.mHashes[mSize - 1] > n) {
            this.add(e);
            return;
        }
        this.mSize = mSize + 1;
        this.mHashes[mSize] = n;
        this.mArray[mSize] = e;
    }
    
    @Override
    public void clear() {
        if (this.mSize != 0) {
            freeArrays(this.mHashes, this.mArray, this.mSize);
            this.mHashes = ArraySet.INT;
            this.mArray = ArraySet.OBJECT;
            this.mSize = 0;
        }
    }
    
    @Override
    public boolean contains(final Object o) {
        boolean b = false;
        if (this.indexOf(o) >= 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        final Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (!this.contains(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    public void ensureCapacity(final int n) {
        if (this.mHashes.length < n) {
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            this.allocArrays(n);
            if (this.mSize > 0) {
                System.arraycopy(mHashes, 0, this.mHashes, 0, this.mSize);
                System.arraycopy(mArray, 0, this.mArray, 0, this.mSize);
            }
            freeArrays(mHashes, mArray, this.mSize);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set set = (Set)o;
        if (this.size() != set.size()) {
            return false;
        }
        int i = 0;
        try {
            while (i < this.mSize) {
                if (!set.contains(this.valueAt(i))) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        catch (final NullPointerException ex) {
            return false;
        }
        catch (final ClassCastException ex2) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int i = 0;
        final int[] mHashes = this.mHashes;
        final int mSize = this.mSize;
        int n = 0;
        while (i < mSize) {
            n += mHashes[i];
            ++i;
        }
        return n;
    }
    
    public int indexOf(final Object o) {
        int n2;
        if (o != null) {
            int n;
            if (!this.mIdentityHashCode) {
                n = o.hashCode();
            }
            else {
                n = System.identityHashCode(o);
            }
            n2 = this.indexOf(o, n);
        }
        else {
            n2 = this.indexOfNull();
        }
        return n2;
    }
    
    @Override
    public boolean isEmpty() {
        boolean b = false;
        if (this.mSize <= 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.getCollection().getKeySet().iterator();
    }
    
    @Override
    public boolean remove(final Object o) {
        final int index = this.indexOf(o);
        if (index < 0) {
            return false;
        }
        this.removeAt(index);
        return true;
    }
    
    public boolean removeAll(final ArraySet<? extends E> set) {
        boolean b = false;
        final int mSize = set.mSize;
        final int mSize2 = this.mSize;
        for (int i = 0; i < mSize; ++i) {
            this.remove(set.valueAt(i));
        }
        if (mSize2 != this.mSize) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        boolean b = false;
        final Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            b |= this.remove(iterator.next());
        }
        return b;
    }
    
    public E removeAt(final int n) {
        int n2 = 8;
        final Object o = this.mArray[n];
        if (this.mSize > 1) {
            if (this.mHashes.length > 8 && this.mSize < this.mHashes.length / 3) {
                if (this.mSize > 8) {
                    n2 = this.mSize + (this.mSize >> 1);
                }
                final int[] mHashes = this.mHashes;
                final Object[] mArray = this.mArray;
                this.allocArrays(n2);
                --this.mSize;
                if (n > 0) {
                    System.arraycopy(mHashes, 0, this.mHashes, 0, n);
                    System.arraycopy(mArray, 0, this.mArray, 0, n);
                }
                if (n < this.mSize) {
                    System.arraycopy(mHashes, n + 1, this.mHashes, n, this.mSize - n);
                    System.arraycopy(mArray, n + 1, this.mArray, n, this.mSize - n);
                }
            }
            else {
                --this.mSize;
                if (n < this.mSize) {
                    System.arraycopy(this.mHashes, n + 1, this.mHashes, n, this.mSize - n);
                    System.arraycopy(this.mArray, n + 1, this.mArray, n, this.mSize - n);
                }
                this.mArray[this.mSize] = null;
            }
        }
        else {
            freeArrays(this.mHashes, this.mArray, this.mSize);
            this.mHashes = ArraySet.INT;
            this.mArray = ArraySet.OBJECT;
            this.mSize = 0;
        }
        return (E)o;
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        int i = this.mSize;
        boolean b = false;
        --i;
        while (i >= 0) {
            if (!collection.contains(this.mArray[i])) {
                this.removeAt(i);
                b = true;
            }
            --i;
        }
        return b;
    }
    
    @Override
    public int size() {
        return this.mSize;
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[this.mSize];
        System.arraycopy(this.mArray, 0, array, 0, this.mSize);
        return array;
    }
    
    @Override
    public <T> T[] toArray(T[] array) {
        if (array.length < this.mSize) {
            array = (T[])Array.newInstance(array.getClass().getComponentType(), this.mSize);
        }
        System.arraycopy(this.mArray, 0, array, 0, this.mSize);
        if (array.length > this.mSize) {
            array[this.mSize] = null;
        }
        return array;
    }
    
    @Override
    public String toString() {
        int i = 0;
        if (!this.isEmpty()) {
            final StringBuilder sb = new StringBuilder(this.mSize * 14);
            sb.append('{');
            while (i < this.mSize) {
                if (i > 0) {
                    sb.append(", ");
                }
                final E value = this.valueAt(i);
                if (value == this) {
                    sb.append("(this Set)");
                }
                else {
                    sb.append(value);
                }
                ++i;
            }
            sb.append('}');
            return sb.toString();
        }
        return "{}";
    }
    
    public E valueAt(final int n) {
        return (E)this.mArray[n];
    }
}
