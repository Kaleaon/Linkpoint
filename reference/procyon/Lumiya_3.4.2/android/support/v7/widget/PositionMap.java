// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import java.util.ArrayList;

class PositionMap<E> implements Cloneable
{
    private static final Object DELETED;
    private boolean mGarbage;
    private int[] mKeys;
    private int mSize;
    private Object[] mValues;
    
    static {
        DELETED = new Object();
    }
    
    PositionMap() {
        this(10);
    }
    
    PositionMap(int idealIntArraySize) {
        this.mGarbage = false;
        if (idealIntArraySize != 0) {
            idealIntArraySize = idealIntArraySize(idealIntArraySize);
            this.mKeys = new int[idealIntArraySize];
            this.mValues = new Object[idealIntArraySize];
        }
        else {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_OBJECTS;
        }
        this.mSize = 0;
    }
    
    private void gc() {
        final int mSize = this.mSize;
        final int[] mKeys = this.mKeys;
        final Object[] mValues = this.mValues;
        int i = 0;
        int mSize2 = 0;
        while (i < mSize) {
            final Object o = mValues[i];
            if (o != PositionMap.DELETED) {
                if (i != mSize2) {
                    mKeys[mSize2] = mKeys[i];
                    mValues[mSize2] = o;
                    mValues[i] = null;
                }
                ++mSize2;
            }
            ++i;
        }
        this.mGarbage = false;
        this.mSize = mSize2;
    }
    
    static int idealBooleanArraySize(final int n) {
        return idealByteArraySize(n);
    }
    
    static int idealByteArraySize(final int n) {
        for (int i = 4; i < 32; ++i) {
            if (n <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return n;
    }
    
    static int idealCharArraySize(final int n) {
        return idealByteArraySize(n * 2) / 2;
    }
    
    static int idealFloatArraySize(final int n) {
        return idealByteArraySize(n * 4) / 4;
    }
    
    static int idealIntArraySize(final int n) {
        return idealByteArraySize(n * 4) / 4;
    }
    
    static int idealLongArraySize(final int n) {
        return idealByteArraySize(n * 8) / 8;
    }
    
    static int idealObjectArraySize(final int n) {
        return idealByteArraySize(n * 4) / 4;
    }
    
    static int idealShortArraySize(final int n) {
        return idealByteArraySize(n * 2) / 2;
    }
    
    public void append(final int n, final E e) {
        if (this.mSize != 0 && n <= this.mKeys[this.mSize - 1]) {
            this.put(n, e);
            return;
        }
        if (this.mGarbage && this.mSize >= this.mKeys.length) {
            this.gc();
        }
        final int mSize = this.mSize;
        if (mSize >= this.mKeys.length) {
            final int idealIntArraySize = idealIntArraySize(mSize + 1);
            final int[] mKeys = new int[idealIntArraySize];
            final Object[] mValues = new Object[idealIntArraySize];
            System.arraycopy(this.mKeys, 0, mKeys, 0, this.mKeys.length);
            System.arraycopy(this.mValues, 0, mValues, 0, this.mValues.length);
            this.mKeys = mKeys;
            this.mValues = mValues;
        }
        this.mKeys[mSize] = n;
        this.mValues[mSize] = e;
        this.mSize = mSize + 1;
    }
    
    public void clear() {
        final int mSize = this.mSize;
        final Object[] mValues = this.mValues;
        for (int i = 0; i < mSize; ++i) {
            mValues[i] = null;
        }
        this.mSize = 0;
        this.mGarbage = false;
    }
    
    public PositionMap<E> clone() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   java/lang/Object.clone:()Ljava/lang/Object;
        //     4: checkcast       Landroid/support/v7/widget/PositionMap;
        //     7: astore_1       
        //     8: aload_1        
        //     9: aload_0        
        //    10: getfield        android/support/v7/widget/PositionMap.mKeys:[I
        //    13: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
        //    16: checkcast       [I
        //    19: putfield        android/support/v7/widget/PositionMap.mKeys:[I
        //    22: aload_1        
        //    23: aload_0        
        //    24: getfield        android/support/v7/widget/PositionMap.mValues:[Ljava/lang/Object;
        //    27: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
        //    30: checkcast       [Ljava/lang/Object;
        //    33: putfield        android/support/v7/widget/PositionMap.mValues:[Ljava/lang/Object;
        //    36: aload_1        
        //    37: areturn        
        //    38: astore_1       
        //    39: aconst_null    
        //    40: astore_1       
        //    41: goto            36
        //    44: astore_2       
        //    45: goto            36
        //    Signature:
        //  ()Landroid/support/v7/widget/PositionMap<TE;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                  
        //  -----  -----  -----  -----  --------------------------------------
        //  0      8      38     44     Ljava/lang/CloneNotSupportedException;
        //  8      36     44     48     Ljava/lang/CloneNotSupportedException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0036:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void delete(int binarySearch) {
        binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, binarySearch);
        if (binarySearch >= 0 && this.mValues[binarySearch] != PositionMap.DELETED) {
            this.mValues[binarySearch] = PositionMap.DELETED;
            this.mGarbage = true;
        }
    }
    
    public E get(final int n) {
        return this.get(n, null);
    }
    
    public E get(int binarySearch, final E e) {
        binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, binarySearch);
        if (binarySearch >= 0 && this.mValues[binarySearch] != PositionMap.DELETED) {
            return (E)this.mValues[binarySearch];
        }
        return e;
    }
    
    public int indexOfKey(final int n) {
        if (this.mGarbage) {
            this.gc();
        }
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, n);
    }
    
    public int indexOfValue(final E e) {
        int i = 0;
        if (this.mGarbage) {
            this.gc();
        }
        while (i < this.mSize) {
            if (this.mValues[i] == e) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public void insertKeyRange(final int n, final int n2) {
    }
    
    public int keyAt(final int n) {
        if (this.mGarbage) {
            this.gc();
        }
        return this.mKeys[n];
    }
    
    public void put(final int n, final E e) {
        final int binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, n);
        if (binarySearch < 0) {
            int n2 = ~binarySearch;
            if (n2 < this.mSize && this.mValues[n2] == PositionMap.DELETED) {
                this.mKeys[n2] = n;
                this.mValues[n2] = e;
                return;
            }
            if (this.mGarbage && this.mSize >= this.mKeys.length) {
                this.gc();
                n2 = ~ContainerHelpers.binarySearch(this.mKeys, this.mSize, n);
            }
            if (this.mSize >= this.mKeys.length) {
                final int idealIntArraySize = idealIntArraySize(this.mSize + 1);
                final int[] mKeys = new int[idealIntArraySize];
                final Object[] mValues = new Object[idealIntArraySize];
                System.arraycopy(this.mKeys, 0, mKeys, 0, this.mKeys.length);
                System.arraycopy(this.mValues, 0, mValues, 0, this.mValues.length);
                this.mKeys = mKeys;
                this.mValues = mValues;
            }
            if (this.mSize - n2 != 0) {
                System.arraycopy(this.mKeys, n2, this.mKeys, n2 + 1, this.mSize - n2);
                System.arraycopy(this.mValues, n2, this.mValues, n2 + 1, this.mSize - n2);
            }
            this.mKeys[n2] = n;
            this.mValues[n2] = e;
            ++this.mSize;
        }
        else {
            this.mValues[binarySearch] = e;
        }
    }
    
    public void remove(final int n) {
        this.delete(n);
    }
    
    public void removeAt(final int n) {
        if (this.mValues[n] != PositionMap.DELETED) {
            this.mValues[n] = PositionMap.DELETED;
            this.mGarbage = true;
        }
    }
    
    public void removeAtRange(int i, int min) {
        for (min = Math.min(this.mSize, i + min); i < min; ++i) {
            this.removeAt(i);
        }
    }
    
    public void removeKeyRange(final ArrayList<E> list, final int n, final int n2) {
    }
    
    public void setValueAt(final int n, final E e) {
        if (this.mGarbage) {
            this.gc();
        }
        this.mValues[n] = e;
    }
    
    public int size() {
        if (this.mGarbage) {
            this.gc();
        }
        return this.mSize;
    }
    
    @Override
    public String toString() {
        int i = 0;
        if (this.size() > 0) {
            final StringBuilder sb = new StringBuilder(this.mSize * 28);
            sb.append('{');
            while (i < this.mSize) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.keyAt(i));
                sb.append('=');
                final E value = this.valueAt(i);
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
    
    public E valueAt(final int n) {
        if (this.mGarbage) {
            this.gc();
        }
        return (E)this.mValues[n];
    }
    
    static class ContainerHelpers
    {
        static final boolean[] EMPTY_BOOLEANS;
        static final int[] EMPTY_INTS;
        static final long[] EMPTY_LONGS;
        static final Object[] EMPTY_OBJECTS;
        
        static {
            EMPTY_BOOLEANS = new boolean[0];
            EMPTY_INTS = new int[0];
            EMPTY_LONGS = new long[0];
            EMPTY_OBJECTS = new Object[0];
        }
        
        static int binarySearch(final int[] array, int i, final int n) {
            final int n2 = 0;
            final int n3 = i - 1;
            i = n2;
            int n4 = n3;
            while (i <= n4) {
                final int n5 = i + n4 >>> 1;
                final int n6 = array[n5];
                if (n6 >= n) {
                    if (n6 <= n) {
                        return n5;
                    }
                    n4 = n5 - 1;
                }
                else {
                    i = n5 + 1;
                }
            }
            return ~i;
        }
    }
}
