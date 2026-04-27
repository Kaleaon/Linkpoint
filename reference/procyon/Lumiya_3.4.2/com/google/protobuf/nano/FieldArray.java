// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

public final class FieldArray implements Cloneable
{
    private static final FieldData DELETED;
    private FieldData[] mData;
    private int[] mFieldNumbers;
    private boolean mGarbage;
    private int mSize;
    
    static {
        DELETED = new FieldData();
    }
    
    FieldArray() {
        this(10);
    }
    
    FieldArray(int idealIntArraySize) {
        this.mGarbage = false;
        idealIntArraySize = this.idealIntArraySize(idealIntArraySize);
        this.mFieldNumbers = new int[idealIntArraySize];
        this.mData = new FieldData[idealIntArraySize];
        this.mSize = 0;
    }
    
    private boolean arrayEquals(final int[] array, final int[] array2, final int n) {
        for (int i = 0; i < n; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    private boolean arrayEquals(final FieldData[] array, final FieldData[] array2, final int n) {
        for (int i = 0; i < n; ++i) {
            if (!array[i].equals(array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    private int binarySearch(final int n) {
        int i = 0;
        int n2 = this.mSize - 1;
        while (i <= n2) {
            final int n3 = i + n2 >>> 1;
            final int n4 = this.mFieldNumbers[n3];
            if (n4 >= n) {
                if (n4 <= n) {
                    return n3;
                }
                n2 = n3 - 1;
            }
            else {
                i = n3 + 1;
            }
        }
        return ~i;
    }
    
    private void gc() {
        final int mSize = this.mSize;
        final int[] mFieldNumbers = this.mFieldNumbers;
        final FieldData[] mData = this.mData;
        int i = 0;
        int mSize2 = 0;
        while (i < mSize) {
            final FieldData fieldData = mData[i];
            if (fieldData != FieldArray.DELETED) {
                if (i != mSize2) {
                    mFieldNumbers[mSize2] = mFieldNumbers[i];
                    mData[mSize2] = fieldData;
                    mData[i] = null;
                }
                ++mSize2;
            }
            ++i;
        }
        this.mGarbage = false;
        this.mSize = mSize2;
    }
    
    private int idealByteArraySize(final int n) {
        for (int i = 4; i < 32; ++i) {
            if (n <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return n;
    }
    
    private int idealIntArraySize(final int n) {
        return this.idealByteArraySize(n * 4) / 4;
    }
    
    public final FieldArray clone() {
        int i = 0;
        final int size = this.size();
        final FieldArray fieldArray = new FieldArray(size);
        System.arraycopy(this.mFieldNumbers, 0, fieldArray.mFieldNumbers, 0, size);
        while (i < size) {
            if (this.mData[i] != null) {
                fieldArray.mData[i] = this.mData[i].clone();
            }
            ++i;
        }
        fieldArray.mSize = size;
        return fieldArray;
    }
    
    FieldData dataAt(final int n) {
        if (this.mGarbage) {
            this.gc();
        }
        return this.mData[n];
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof FieldArray)) {
            return false;
        }
        final FieldArray fieldArray = (FieldArray)o;
        if (this.size() == fieldArray.size()) {
            if (!this.arrayEquals(this.mFieldNumbers, fieldArray.mFieldNumbers, this.mSize) || !this.arrayEquals(this.mData, fieldArray.mData, this.mSize)) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    FieldData get(int binarySearch) {
        binarySearch = this.binarySearch(binarySearch);
        if (binarySearch >= 0 && this.mData[binarySearch] != FieldArray.DELETED) {
            return this.mData[binarySearch];
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        int i = 0;
        if (this.mGarbage) {
            this.gc();
        }
        int n = 17;
        while (i < this.mSize) {
            n = (n * 31 + this.mFieldNumbers[i]) * 31 + this.mData[i].hashCode();
            ++i;
        }
        return n;
    }
    
    public boolean isEmpty() {
        boolean b = false;
        if (this.size() == 0) {
            b = true;
        }
        return b;
    }
    
    void put(final int n, final FieldData fieldData) {
        final int binarySearch = this.binarySearch(n);
        if (binarySearch < 0) {
            int n2 = ~binarySearch;
            if (n2 < this.mSize && this.mData[n2] == FieldArray.DELETED) {
                this.mFieldNumbers[n2] = n;
                this.mData[n2] = fieldData;
                return;
            }
            if (this.mGarbage && this.mSize >= this.mFieldNumbers.length) {
                this.gc();
                n2 = ~this.binarySearch(n);
            }
            if (this.mSize >= this.mFieldNumbers.length) {
                final int idealIntArraySize = this.idealIntArraySize(this.mSize + 1);
                final int[] mFieldNumbers = new int[idealIntArraySize];
                final FieldData[] mData = new FieldData[idealIntArraySize];
                System.arraycopy(this.mFieldNumbers, 0, mFieldNumbers, 0, this.mFieldNumbers.length);
                System.arraycopy(this.mData, 0, mData, 0, this.mData.length);
                this.mFieldNumbers = mFieldNumbers;
                this.mData = mData;
            }
            if (this.mSize - n2 != 0) {
                System.arraycopy(this.mFieldNumbers, n2, this.mFieldNumbers, n2 + 1, this.mSize - n2);
                System.arraycopy(this.mData, n2, this.mData, n2 + 1, this.mSize - n2);
            }
            this.mFieldNumbers[n2] = n;
            this.mData[n2] = fieldData;
            ++this.mSize;
        }
        else {
            this.mData[binarySearch] = fieldData;
        }
    }
    
    void remove(int binarySearch) {
        binarySearch = this.binarySearch(binarySearch);
        if (binarySearch >= 0 && this.mData[binarySearch] != FieldArray.DELETED) {
            this.mData[binarySearch] = FieldArray.DELETED;
            this.mGarbage = true;
        }
    }
    
    int size() {
        if (this.mGarbage) {
            this.gc();
        }
        return this.mSize;
    }
}
