// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

public final class CircularIntArray
{
    private int mCapacityBitmask;
    private int[] mElements;
    private int mHead;
    private int mTail;
    
    public CircularIntArray() {
        this(8);
    }
    
    public CircularIntArray(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        if (i <= 1073741824) {
            if (Integer.bitCount(i) != 1) {
                i = Integer.highestOneBit(i - 1) << 1;
            }
            this.mCapacityBitmask = i - 1;
            this.mElements = new int[i];
            return;
        }
        throw new IllegalArgumentException("capacity must be <= 2^30");
    }
    
    private void doubleCapacity() {
        final int length = this.mElements.length;
        final int n = length - this.mHead;
        final int n2 = length << 1;
        if (n2 >= 0) {
            final int[] mElements = new int[n2];
            System.arraycopy(this.mElements, this.mHead, mElements, 0, n);
            System.arraycopy(this.mElements, 0, mElements, n, this.mHead);
            this.mElements = mElements;
            this.mHead = 0;
            this.mTail = length;
            this.mCapacityBitmask = n2 - 1;
            return;
        }
        throw new RuntimeException("Max array capacity exceeded");
    }
    
    public void addFirst(final int n) {
        this.mHead = (this.mHead - 1 & this.mCapacityBitmask);
        this.mElements[this.mHead] = n;
        if (this.mHead == this.mTail) {
            this.doubleCapacity();
        }
    }
    
    public void addLast(final int n) {
        this.mElements[this.mTail] = n;
        this.mTail = (this.mTail + 1 & this.mCapacityBitmask);
        if (this.mTail == this.mHead) {
            this.doubleCapacity();
        }
    }
    
    public void clear() {
        this.mTail = this.mHead;
    }
    
    public int get(final int n) {
        if (n >= 0 && n < this.size()) {
            return this.mElements[this.mHead + n & this.mCapacityBitmask];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int getFirst() {
        if (this.mHead != this.mTail) {
            return this.mElements[this.mHead];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int getLast() {
        if (this.mHead != this.mTail) {
            return this.mElements[this.mTail - 1 & this.mCapacityBitmask];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public boolean isEmpty() {
        return this.mHead == this.mTail;
    }
    
    public int popFirst() {
        if (this.mHead != this.mTail) {
            final int n = this.mElements[this.mHead];
            this.mHead = (this.mHead + 1 & this.mCapacityBitmask);
            return n;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int popLast() {
        if (this.mHead != this.mTail) {
            final int mTail = this.mTail - 1 & this.mCapacityBitmask;
            final int n = this.mElements[mTail];
            this.mTail = mTail;
            return n;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public void removeFromEnd(final int n) {
        if (n <= 0) {
            return;
        }
        if (n <= this.size()) {
            this.mTail = (this.mTail - n & this.mCapacityBitmask);
            return;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public void removeFromStart(final int n) {
        if (n <= 0) {
            return;
        }
        if (n <= this.size()) {
            this.mHead = (this.mHead + n & this.mCapacityBitmask);
            return;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int size() {
        return this.mTail - this.mHead & this.mCapacityBitmask;
    }
}
