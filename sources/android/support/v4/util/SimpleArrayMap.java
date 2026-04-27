package android.support.v4.util;

import java.util.ConcurrentModificationException;
import java.util.Map;

public class SimpleArrayMap<K, V> {
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

    public SimpleArrayMap(int i) {
        if (i != 0) {
            allocArrays(i);
        } else {
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        }
        this.mSize = 0;
    }

    public SimpleArrayMap(SimpleArrayMap<K, V> simpleArrayMap) {
        this();
        if (simpleArrayMap != null) {
            putAll(simpleArrayMap);
        }
    }

    private void allocArrays(int i) {
        if (i == 8) {
            Class<ArrayMap> cls = ArrayMap.class;
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCache == null) {
                } else {
                    Object[] objArr = mTwiceBaseCache;
                    this.mArray = objArr;
                    mTwiceBaseCache = (Object[]) objArr[0];
                    this.mHashes = (int[]) objArr[1];
                    objArr[1] = null;
                    objArr[0] = null;
                    mTwiceBaseCacheSize--;
                    return;
                }
            }
        } else if (i == 4) {
            Class<ArrayMap> cls2 = ArrayMap.class;
            synchronized (ArrayMap.class) {
                if (mBaseCache == null) {
                } else {
                    Object[] objArr2 = mBaseCache;
                    this.mArray = objArr2;
                    mBaseCache = (Object[]) objArr2[0];
                    this.mHashes = (int[]) objArr2[1];
                    objArr2[1] = null;
                    objArr2[0] = null;
                    mBaseCacheSize--;
                    return;
                }
            }
        }
        this.mHashes = new int[i];
        this.mArray = new Object[(i << 1)];
    }

    private static int binarySearchHashes(int[] iArr, int i, int i2) {
        try {
            return ContainerHelpers.binarySearch(iArr, i, i2);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    private static void freeArrays(int[] iArr, Object[] objArr, int i) {
        if (iArr.length == 8) {
            Class<ArrayMap> cls = ArrayMap.class;
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCacheSize < 10) {
                    objArr[0] = mTwiceBaseCache;
                    objArr[1] = iArr;
                    for (int i2 = (i << 1) - 1; i2 >= 2; i2--) {
                        objArr[i2] = null;
                    }
                    mTwiceBaseCache = objArr;
                    mTwiceBaseCacheSize++;
                }
            }
        } else if (iArr.length == 4) {
            Class<ArrayMap> cls2 = ArrayMap.class;
            synchronized (ArrayMap.class) {
                if (mBaseCacheSize < 10) {
                    objArr[0] = mBaseCache;
                    objArr[1] = iArr;
                    for (int i3 = (i << 1) - 1; i3 >= 2; i3--) {
                        objArr[i3] = null;
                    }
                    mBaseCache = objArr;
                    mBaseCacheSize++;
                }
            }
        }
    }

    public void clear() {
        if (this.mSize > 0) {
            int[] iArr = this.mHashes;
            Object[] objArr = this.mArray;
            int i = this.mSize;
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
            freeArrays(iArr, objArr, i);
        }
        if (this.mSize > 0) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean containsKey(Object obj) {
        if (indexOfKey(obj) < 0) {
            return false;
        }
        return CONCURRENT_MODIFICATION_EXCEPTIONS;
    }

    public boolean containsValue(Object obj) {
        if (indexOfValue(obj) < 0) {
            return false;
        }
        return CONCURRENT_MODIFICATION_EXCEPTIONS;
    }

    public void ensureCapacity(int i) {
        int i2 = this.mSize;
        if (this.mHashes.length < i) {
            int[] iArr = this.mHashes;
            Object[] objArr = this.mArray;
            allocArrays(i);
            if (this.mSize > 0) {
                System.arraycopy(iArr, 0, this.mHashes, 0, i2);
                System.arraycopy(objArr, 0, this.mArray, 0, i2 << 1);
            }
            freeArrays(iArr, objArr, i2);
        }
        if (this.mSize != i2) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return CONCURRENT_MODIFICATION_EXCEPTIONS;
        }
        if (obj instanceof SimpleArrayMap) {
            SimpleArrayMap simpleArrayMap = (SimpleArrayMap) obj;
            if (size() != simpleArrayMap.size()) {
                return false;
            }
            int i = 0;
            while (i < this.mSize) {
                try {
                    Object keyAt = keyAt(i);
                    Object valueAt = valueAt(i);
                    Object obj2 = simpleArrayMap.get(keyAt);
                    if (valueAt != null) {
                        if (!valueAt.equals(obj2)) {
                            return false;
                        }
                    } else if (obj2 != null || !simpleArrayMap.containsKey(keyAt)) {
                        return false;
                    }
                    i++;
                } catch (NullPointerException e) {
                    return false;
                } catch (ClassCastException e2) {
                    return false;
                }
            }
            return CONCURRENT_MODIFICATION_EXCEPTIONS;
        } else if (!(obj instanceof Map)) {
            return false;
        } else {
            Map map = (Map) obj;
            if (size() != map.size()) {
                return false;
            }
            int i2 = 0;
            while (i2 < this.mSize) {
                try {
                    Object keyAt2 = keyAt(i2);
                    Object valueAt2 = valueAt(i2);
                    Object obj3 = map.get(keyAt2);
                    if (valueAt2 != null) {
                        if (!valueAt2.equals(obj3)) {
                            return false;
                        }
                    } else if (obj3 != null || !map.containsKey(keyAt2)) {
                        return false;
                    }
                    i2++;
                } catch (NullPointerException e3) {
                    return false;
                } catch (ClassCastException e4) {
                    return false;
                }
            }
            return CONCURRENT_MODIFICATION_EXCEPTIONS;
        }
    }

    public V get(Object obj) {
        int indexOfKey = indexOfKey(obj);
        if (indexOfKey < 0) {
            return null;
        }
        return this.mArray[(indexOfKey << 1) + 1];
    }

    public int hashCode() {
        int[] iArr = this.mHashes;
        Object[] objArr = this.mArray;
        int i = this.mSize;
        int i2 = 1;
        int i3 = 0;
        int i4 = 0;
        while (i3 < i) {
            Object obj = objArr[i2];
            i4 += (obj != null ? obj.hashCode() : 0) ^ iArr[i3];
            i3++;
            i2 += 2;
        }
        return i4;
    }

    /* access modifiers changed from: package-private */
    public int indexOf(Object obj, int i) {
        int i2 = this.mSize;
        if (i2 == 0) {
            return -1;
        }
        int binarySearchHashes = binarySearchHashes(this.mHashes, i2, i);
        if (binarySearchHashes < 0 || obj.equals(this.mArray[binarySearchHashes << 1])) {
            return binarySearchHashes;
        }
        int i3 = binarySearchHashes + 1;
        while (i3 < i2 && this.mHashes[i3] == i) {
            if (obj.equals(this.mArray[i3 << 1])) {
                return i3;
            }
            i3++;
        }
        int i4 = binarySearchHashes - 1;
        while (i4 >= 0 && this.mHashes[i4] == i) {
            if (obj.equals(this.mArray[i4 << 1])) {
                return i4;
            }
            i4--;
        }
        return i3 ^ -1;
    }

    public int indexOfKey(Object obj) {
        return obj != null ? indexOf(obj, obj.hashCode()) : indexOfNull();
    }

    /* access modifiers changed from: package-private */
    public int indexOfNull() {
        int i = this.mSize;
        if (i == 0) {
            return -1;
        }
        int binarySearchHashes = binarySearchHashes(this.mHashes, i, 0);
        if (binarySearchHashes < 0 || this.mArray[binarySearchHashes << 1] == null) {
            return binarySearchHashes;
        }
        int i2 = binarySearchHashes + 1;
        while (i2 < i && this.mHashes[i2] == 0) {
            if (this.mArray[i2 << 1] == null) {
                return i2;
            }
            i2++;
        }
        int i3 = binarySearchHashes - 1;
        while (i3 >= 0 && this.mHashes[i3] == 0) {
            if (this.mArray[i3 << 1] == null) {
                return i3;
            }
            i3--;
        }
        return i2 ^ -1;
    }

    /* access modifiers changed from: package-private */
    public int indexOfValue(Object obj) {
        int i = 1;
        int i2 = this.mSize * 2;
        Object[] objArr = this.mArray;
        if (obj != null) {
            while (i < i2) {
                if (obj.equals(objArr[i])) {
                    return i >> 1;
                }
                i += 2;
            }
            return -1;
        }
        while (i < i2) {
            if (objArr[i] == null) {
                return i >> 1;
            }
            i += 2;
        }
        return -1;
    }

    public boolean isEmpty() {
        if (this.mSize > 0) {
            return false;
        }
        return CONCURRENT_MODIFICATION_EXCEPTIONS;
    }

    public K keyAt(int i) {
        return this.mArray[i << 1];
    }

    public V put(K k, V v) {
        int indexOfNull;
        int i;
        int i2 = 4;
        int i3 = this.mSize;
        if (k != null) {
            i = k.hashCode();
            indexOfNull = indexOf(k, i);
        } else {
            indexOfNull = indexOfNull();
            i = 0;
        }
        if (indexOfNull < 0) {
            int i4 = indexOfNull ^ -1;
            if (i3 >= this.mHashes.length) {
                if (i3 >= 8) {
                    i2 = (i3 >> 1) + i3;
                } else if (i3 >= 4) {
                    i2 = 8;
                }
                int[] iArr = this.mHashes;
                Object[] objArr = this.mArray;
                allocArrays(i2);
                if (i3 == this.mSize) {
                    if (this.mHashes.length > 0) {
                        System.arraycopy(iArr, 0, this.mHashes, 0, iArr.length);
                        System.arraycopy(objArr, 0, this.mArray, 0, objArr.length);
                    }
                    freeArrays(iArr, objArr, i3);
                } else {
                    throw new ConcurrentModificationException();
                }
            }
            if (i4 < i3) {
                System.arraycopy(this.mHashes, i4, this.mHashes, i4 + 1, i3 - i4);
                System.arraycopy(this.mArray, i4 << 1, this.mArray, (i4 + 1) << 1, (this.mSize - i4) << 1);
            }
            if (i3 == this.mSize && i4 < this.mHashes.length) {
                this.mHashes[i4] = i;
                this.mArray[i4 << 1] = k;
                this.mArray[(i4 << 1) + 1] = v;
                this.mSize++;
                return null;
            }
            throw new ConcurrentModificationException();
        }
        int i5 = (indexOfNull << 1) + 1;
        V v2 = this.mArray[i5];
        this.mArray[i5] = v;
        return v2;
    }

    public void putAll(SimpleArrayMap<? extends K, ? extends V> simpleArrayMap) {
        int i = simpleArrayMap.mSize;
        ensureCapacity(this.mSize + i);
        if (this.mSize != 0) {
            for (int i2 = 0; i2 < i; i2++) {
                put(simpleArrayMap.keyAt(i2), simpleArrayMap.valueAt(i2));
            }
        } else if (i > 0) {
            System.arraycopy(simpleArrayMap.mHashes, 0, this.mHashes, 0, i);
            System.arraycopy(simpleArrayMap.mArray, 0, this.mArray, 0, i << 1);
            this.mSize = i;
        }
    }

    public V remove(Object obj) {
        int indexOfKey = indexOfKey(obj);
        if (indexOfKey < 0) {
            return null;
        }
        return removeAt(indexOfKey);
    }

    public V removeAt(int i) {
        int i2;
        int i3 = 8;
        V v = this.mArray[(i << 1) + 1];
        int i4 = this.mSize;
        if (i4 > 1) {
            int i5 = i4 - 1;
            if (this.mHashes.length > 8 && this.mSize < this.mHashes.length / 3) {
                if (i4 > 8) {
                    i3 = (i4 >> 1) + i4;
                }
                int[] iArr = this.mHashes;
                Object[] objArr = this.mArray;
                allocArrays(i3);
                if (i4 == this.mSize) {
                    if (i > 0) {
                        System.arraycopy(iArr, 0, this.mHashes, 0, i);
                        System.arraycopy(objArr, 0, this.mArray, 0, i << 1);
                    }
                    if (i < i5) {
                        System.arraycopy(iArr, i + 1, this.mHashes, i, i5 - i);
                        System.arraycopy(objArr, (i + 1) << 1, this.mArray, i << 1, (i5 - i) << 1);
                    }
                    i2 = i5;
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                if (i < i5) {
                    System.arraycopy(this.mHashes, i + 1, this.mHashes, i, i5 - i);
                    System.arraycopy(this.mArray, (i + 1) << 1, this.mArray, i << 1, (i5 - i) << 1);
                }
                this.mArray[i5 << 1] = null;
                this.mArray[(i5 << 1) + 1] = null;
                i2 = i5;
            }
        } else {
            freeArrays(this.mHashes, this.mArray, i4);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            i2 = 0;
        }
        if (i4 == this.mSize) {
            this.mSize = i2;
            return v;
        }
        throw new ConcurrentModificationException();
    }

    public V setValueAt(int i, V v) {
        int i2 = (i << 1) + 1;
        V v2 = this.mArray[i2];
        this.mArray[i2] = v;
        return v2;
    }

    public int size() {
        return this.mSize;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object keyAt = keyAt(i);
            if (keyAt == this) {
                sb.append("(this Map)");
            } else {
                sb.append(keyAt);
            }
            sb.append('=');
            Object valueAt = valueAt(i);
            if (valueAt == this) {
                sb.append("(this Map)");
            } else {
                sb.append(valueAt);
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public V valueAt(int i) {
        return this.mArray[(i << 1) + 1];
    }
}
