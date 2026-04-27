// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import java.util.Map;
import java.util.ConcurrentModificationException;

public class SimpleArrayMap<K, V>
{
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean CONCURRENT_MODIFICATION_EXCEPTIONS = true;
    private static final boolean DEBUG = false;
    private static final String TAG = "ArrayMap";
    static Object[] mBaseCache;
    static int mBaseCacheSize;
    static Object[] mTwiceBaseCache;
    static int mTwiceBaseCacheSize;
    Object[] mArray;
    int[] mHashes;
    int mSize;
    
    public SimpleArrayMap() {
        this.mHashes = ContainerHelpers.EMPTY_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
    }
    
    public SimpleArrayMap(final int n) {
        if (n != 0) {
            this.allocArrays(n);
        }
        else {
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        }
        this.mSize = 0;
    }
    
    public SimpleArrayMap(final SimpleArrayMap<K, V> simpleArrayMap) {
        this();
        if (simpleArrayMap != null) {
            this.putAll((SimpleArrayMap<? extends K, ? extends V>)simpleArrayMap);
        }
    }
    
    private void allocArrays(final int n) {
        Label_0011: {
            if (n == 8) {
                synchronized (ArrayMap.class) {
                    if (SimpleArrayMap.mTwiceBaseCache == null) {
                        break Label_0011;
                    }
                }
                final Object[] mTwiceBaseCache = SimpleArrayMap.mTwiceBaseCache;
                this.mArray = mTwiceBaseCache;
                SimpleArrayMap.mTwiceBaseCache = (Object[])mTwiceBaseCache[0];
                this.mHashes = (int[])mTwiceBaseCache[1];
                mTwiceBaseCache[0] = (mTwiceBaseCache[1] = null);
                --SimpleArrayMap.mTwiceBaseCacheSize;
                monitorexit(ArrayMap.class);
                return;
            }
            if (n == 4) {
                synchronized (ArrayMap.class) {
                    if (SimpleArrayMap.mBaseCache == null) {
                        break Label_0011;
                    }
                }
                final Object[] mBaseCache = SimpleArrayMap.mBaseCache;
                this.mArray = mBaseCache;
                SimpleArrayMap.mBaseCache = (Object[])mBaseCache[0];
                this.mHashes = (int[])mBaseCache[1];
                mBaseCache[0] = (mBaseCache[1] = null);
                --SimpleArrayMap.mBaseCacheSize;
                monitorexit(ArrayMap.class);
                return;
            }
        }
        this.mHashes = new int[n];
        this.mArray = new Object[n << 1];
    }
    
    private static int binarySearchHashes(final int[] array, int binarySearch, final int n) {
        try {
            binarySearch = ContainerHelpers.binarySearch(array, binarySearch, n);
            return binarySearch;
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
    
    private static void freeArrays(final int[] array, final Object[] array2, int i) {
        if (array.length != 8) {
            if (array.length == 4) {
                while (true) {
                    while (true) {
                        synchronized (ArrayMap.class) {
                            if (SimpleArrayMap.mBaseCacheSize >= 10) {
                                break;
                            }
                        }
                        array2[0] = SimpleArrayMap.mBaseCache;
                        final Throwable t;
                        array2[1] = t;
                        for (i = (i << 1) - 1; i >= 2; --i) {
                            array2[i] = null;
                        }
                        SimpleArrayMap.mBaseCache = array2;
                        ++SimpleArrayMap.mBaseCacheSize;
                        continue;
                    }
                }
            }
        }
        else {
            while (true) {
                while (true) {
                    synchronized (ArrayMap.class) {
                        if (SimpleArrayMap.mTwiceBaseCacheSize >= 10) {
                            break;
                        }
                    }
                    array2[0] = SimpleArrayMap.mTwiceBaseCache;
                    final Throwable t2;
                    array2[1] = t2;
                    for (i = (i << 1) - 1; i >= 2; --i) {
                        array2[i] = null;
                    }
                    SimpleArrayMap.mTwiceBaseCache = array2;
                    ++SimpleArrayMap.mTwiceBaseCacheSize;
                    continue;
                }
            }
        }
    }
    
    public void clear() {
        if (this.mSize > 0) {
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            final int mSize = this.mSize;
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
            freeArrays(mHashes, mArray, mSize);
        }
        if (this.mSize <= 0) {
            return;
        }
        throw new ConcurrentModificationException();
    }
    
    public boolean containsKey(final Object o) {
        boolean b = false;
        if (this.indexOfKey(o) >= 0) {
            b = true;
        }
        return b;
    }
    
    public boolean containsValue(final Object o) {
        boolean b = false;
        if (this.indexOfValue(o) >= 0) {
            b = true;
        }
        return b;
    }
    
    public void ensureCapacity(final int n) {
        final int mSize = this.mSize;
        if (this.mHashes.length < n) {
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            this.allocArrays(n);
            if (this.mSize > 0) {
                System.arraycopy(mHashes, 0, this.mHashes, 0, mSize);
                System.arraycopy(mArray, 0, this.mArray, 0, mSize << 1);
            }
            freeArrays(mHashes, mArray, mSize);
        }
        if (this.mSize == mSize) {
            return;
        }
        throw new ConcurrentModificationException();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleArrayMap)) {
            if (!(o instanceof Map)) {
                return false;
            }
        }
        else {
            final SimpleArrayMap simpleArrayMap = (SimpleArrayMap)o;
            if (this.size() != simpleArrayMap.size()) {
                return false;
            }
            int i = 0;
            try {
                while (i < this.mSize) {
                    final K key = this.keyAt(i);
                    final V value = this.valueAt(i);
                    final Object value2 = simpleArrayMap.get(key);
                    if (value != null) {
                        if (!value.equals(value2)) {
                            return false;
                        }
                    }
                    else if (value2 != null || !simpleArrayMap.containsKey(key)) {
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
        final Map map = (Map)o;
        if (this.size() != map.size()) {
            return false;
        }
        int j = 0;
        try {
            while (j < this.mSize) {
                final K key2 = this.keyAt(j);
                final V value3 = this.valueAt(j);
                final Object value4 = map.get(key2);
                if (value3 != null) {
                    if (!value3.equals(value4)) {
                        return false;
                    }
                }
                else if (value4 != null || !map.containsKey(key2)) {
                    return false;
                }
                ++j;
            }
            return true;
        }
        catch (final NullPointerException ex3) {
            return false;
        }
        catch (final ClassCastException ex4) {
            return false;
        }
    }
    
    public V get(Object o) {
        final int indexOfKey = this.indexOfKey(o);
        if (indexOfKey < 0) {
            o = null;
        }
        else {
            o = this.mArray[(indexOfKey << 1) + 1];
        }
        return (V)o;
    }
    
    @Override
    public int hashCode() {
        final int[] mHashes = this.mHashes;
        final Object[] mArray = this.mArray;
        final int mSize = this.mSize;
        int n = 1;
        int i = 0;
        int n2 = 0;
        while (i < mSize) {
            final Object o = mArray[n];
            final int n3 = mHashes[i];
            int hashCode;
            if (o != null) {
                hashCode = o.hashCode();
            }
            else {
                hashCode = 0;
            }
            n2 += (hashCode ^ n3);
            ++i;
            n += 2;
        }
        return n2;
    }
    
    int indexOf(final Object o, final int n) {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        final int binarySearchHashes = binarySearchHashes(this.mHashes, mSize, n);
        if (binarySearchHashes < 0) {
            return binarySearchHashes;
        }
        if (!o.equals(this.mArray[binarySearchHashes << 1])) {
            int n2;
            for (n2 = binarySearchHashes + 1; n2 < mSize && this.mHashes[n2] == n; ++n2) {
                if (o.equals(this.mArray[n2 << 1])) {
                    return n2;
                }
            }
            for (int n3 = binarySearchHashes - 1; n3 >= 0 && this.mHashes[n3] == n; --n3) {
                if (o.equals(this.mArray[n3 << 1])) {
                    return n3;
                }
            }
            return ~n2;
        }
        return binarySearchHashes;
    }
    
    public int indexOfKey(final Object o) {
        int n;
        if (o != null) {
            n = this.indexOf(o, o.hashCode());
        }
        else {
            n = this.indexOfNull();
        }
        return n;
    }
    
    int indexOfNull() {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        final int binarySearchHashes = binarySearchHashes(this.mHashes, mSize, 0);
        if (binarySearchHashes < 0) {
            return binarySearchHashes;
        }
        if (this.mArray[binarySearchHashes << 1] != null) {
            int n;
            for (n = binarySearchHashes + 1; n < mSize && this.mHashes[n] == 0; ++n) {
                if (this.mArray[n << 1] == null) {
                    return n;
                }
            }
            for (int n2 = binarySearchHashes - 1; n2 >= 0 && this.mHashes[n2] == 0; --n2) {
                if (this.mArray[n2 << 1] == null) {
                    return n2;
                }
            }
            return ~n;
        }
        return binarySearchHashes;
    }
    
    int indexOfValue(final Object o) {
        int i = 1;
        final int n = 1;
        final int n2 = this.mSize * 2;
        final Object[] mArray = this.mArray;
        if (o != null) {
            for (int j = n; j < n2; j += 2) {
                if (o.equals(mArray[j])) {
                    return j >> 1;
                }
            }
        }
        else {
            while (i < n2) {
                if (mArray[i] == null) {
                    return i >> 1;
                }
                i += 2;
            }
        }
        return -1;
    }
    
    public boolean isEmpty() {
        boolean b = false;
        if (this.mSize <= 0) {
            b = true;
        }
        return b;
    }
    
    public K keyAt(final int n) {
        return (K)this.mArray[n << 1];
    }
    
    public V put(final K k, final V v) {
        final int n = 4;
        final int mSize = this.mSize;
        int hashCode;
        int n2;
        if (k != null) {
            hashCode = k.hashCode();
            n2 = this.indexOf(k, hashCode);
        }
        else {
            n2 = this.indexOfNull();
            hashCode = 0;
        }
        if (n2 >= 0) {
            final int n3 = (n2 << 1) + 1;
            final Object o = this.mArray[n3];
            this.mArray[n3] = v;
            return (V)o;
        }
        final int n4 = ~n2;
        if (mSize >= this.mHashes.length) {
            int n5;
            if (mSize < 8) {
                if (mSize < 4) {
                    n5 = n;
                }
                else {
                    n5 = 8;
                }
            }
            else {
                n5 = (mSize >> 1) + mSize;
            }
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            this.allocArrays(n5);
            if (mSize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            if (this.mHashes.length > 0) {
                System.arraycopy(mHashes, 0, this.mHashes, 0, mHashes.length);
                System.arraycopy(mArray, 0, this.mArray, 0, mArray.length);
            }
            freeArrays(mHashes, mArray, mSize);
        }
        if (n4 < mSize) {
            System.arraycopy(this.mHashes, n4, this.mHashes, n4 + 1, mSize - n4);
            System.arraycopy(this.mArray, n4 << 1, this.mArray, n4 + 1 << 1, this.mSize - n4 << 1);
        }
        if (mSize == this.mSize && n4 < this.mHashes.length) {
            this.mHashes[n4] = hashCode;
            this.mArray[n4 << 1] = k;
            this.mArray[(n4 << 1) + 1] = v;
            ++this.mSize;
            return null;
        }
        throw new ConcurrentModificationException();
    }
    
    public void putAll(final SimpleArrayMap<? extends K, ? extends V> simpleArrayMap) {
        int i = 0;
        final int mSize = simpleArrayMap.mSize;
        this.ensureCapacity(this.mSize + mSize);
        if (this.mSize != 0) {
            while (i < mSize) {
                this.put(simpleArrayMap.keyAt(i), simpleArrayMap.valueAt(i));
                ++i;
            }
        }
        else if (mSize > 0) {
            System.arraycopy(simpleArrayMap.mHashes, 0, this.mHashes, 0, mSize);
            System.arraycopy(simpleArrayMap.mArray, 0, this.mArray, 0, mSize << 1);
            this.mSize = mSize;
        }
    }
    
    public V remove(final Object o) {
        final int indexOfKey = this.indexOfKey(o);
        if (indexOfKey < 0) {
            return null;
        }
        return this.removeAt(indexOfKey);
    }
    
    public V removeAt(int mSize) {
        int n = 8;
        final Object o = this.mArray[(mSize << 1) + 1];
        final int mSize2 = this.mSize;
        if (mSize2 > 1) {
            final int n2 = mSize2 - 1;
            if (this.mHashes.length > 8 && this.mSize < this.mHashes.length / 3) {
                if (mSize2 > 8) {
                    n = (mSize2 >> 1) + mSize2;
                }
                final int[] mHashes = this.mHashes;
                final Object[] mArray = this.mArray;
                this.allocArrays(n);
                if (mSize2 != this.mSize) {
                    throw new ConcurrentModificationException();
                }
                if (mSize > 0) {
                    System.arraycopy(mHashes, 0, this.mHashes, 0, mSize);
                    System.arraycopy(mArray, 0, this.mArray, 0, mSize << 1);
                }
                if (mSize < n2) {
                    System.arraycopy(mHashes, mSize + 1, this.mHashes, mSize, n2 - mSize);
                    System.arraycopy(mArray, mSize + 1 << 1, this.mArray, mSize << 1, n2 - mSize << 1);
                }
                mSize = n2;
            }
            else {
                if (mSize < n2) {
                    System.arraycopy(this.mHashes, mSize + 1, this.mHashes, mSize, n2 - mSize);
                    System.arraycopy(this.mArray, mSize + 1 << 1, this.mArray, mSize << 1, n2 - mSize << 1);
                }
                this.mArray[n2 << 1] = null;
                this.mArray[(n2 << 1) + 1] = null;
                mSize = n2;
            }
        }
        else {
            freeArrays(this.mHashes, this.mArray, mSize2);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            mSize = 0;
        }
        if (mSize2 == this.mSize) {
            this.mSize = mSize;
            return (V)o;
        }
        throw new ConcurrentModificationException();
    }
    
    public V setValueAt(int n, final V v) {
        n = (n << 1) + 1;
        final Object o = this.mArray[n];
        this.mArray[n] = v;
        return (V)o;
    }
    
    public int size() {
        return this.mSize;
    }
    
    @Override
    public String toString() {
        int i = 0;
        if (!this.isEmpty()) {
            final StringBuilder sb = new StringBuilder(this.mSize * 28);
            sb.append('{');
            while (i < this.mSize) {
                if (i > 0) {
                    sb.append(", ");
                }
                final K key = this.keyAt(i);
                if (key == this) {
                    sb.append("(this Map)");
                }
                else {
                    sb.append(key);
                }
                sb.append('=');
                final V value = this.valueAt(i);
                if (value == this) {
                    sb.append("(this Map)");
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
    
    public V valueAt(final int n) {
        return (V)this.mArray[(n << 1) + 1];
    }
}
