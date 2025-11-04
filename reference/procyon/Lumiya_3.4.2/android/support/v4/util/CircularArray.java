// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

public final class CircularArray<E>
{
    private int mCapacityBitmask;
    private E[] mElements;
    private int mHead;
    private int mTail;
    
    public CircularArray() {
        this(8);
    }
    
    public CircularArray(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        if (i <= 1073741824) {
            if (Integer.bitCount(i) != 1) {
                i = Integer.highestOneBit(i - 1) << 1;
            }
            this.mCapacityBitmask = i - 1;
            this.mElements = (E[])new Object[i];
            return;
        }
        throw new IllegalArgumentException("capacity must be <= 2^30");
    }
    
    private void doubleCapacity() {
        final int length = this.mElements.length;
        final int n = length - this.mHead;
        final int n2 = length << 1;
        if (n2 >= 0) {
            final Object[] array = new Object[n2];
            System.arraycopy(this.mElements, this.mHead, array, 0, n);
            System.arraycopy(this.mElements, 0, array, n, this.mHead);
            this.mElements = (E[])array;
            this.mHead = 0;
            this.mTail = length;
            this.mCapacityBitmask = n2 - 1;
            return;
        }
        throw new RuntimeException("Max array capacity exceeded");
    }
    
    public void addFirst(final E e) {
        this.mHead = (this.mHead - 1 & this.mCapacityBitmask);
        this.mElements[this.mHead] = e;
        if (this.mHead == this.mTail) {
            this.doubleCapacity();
        }
    }
    
    public void addLast(final E e) {
        this.mElements[this.mTail] = e;
        this.mTail = (this.mTail + 1 & this.mCapacityBitmask);
        if (this.mTail == this.mHead) {
            this.doubleCapacity();
        }
    }
    
    public void clear() {
        this.removeFromStart(this.size());
    }
    
    public E get(final int n) {
        if (n >= 0 && n < this.size()) {
            return this.mElements[this.mHead + n & this.mCapacityBitmask];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public E getFirst() {
        if (this.mHead != this.mTail) {
            return this.mElements[this.mHead];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public E getLast() {
        if (this.mHead != this.mTail) {
            return this.mElements[this.mTail - 1 & this.mCapacityBitmask];
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public boolean isEmpty() {
        return this.mHead == this.mTail;
    }
    
    public E popFirst() {
        if (this.mHead != this.mTail) {
            final E e = this.mElements[this.mHead];
            this.mElements[this.mHead] = null;
            this.mHead = (this.mHead + 1 & this.mCapacityBitmask);
            return e;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public E popLast() {
        if (this.mHead != this.mTail) {
            final int mTail = this.mTail - 1 & this.mCapacityBitmask;
            final E e = this.mElements[mTail];
            this.mElements[mTail] = null;
            this.mTail = mTail;
            return e;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public void removeFromEnd(int i) {
        int n = 0;
        if (i <= 0) {
            return;
        }
        if (i <= this.size()) {
            if (i < this.mTail) {
                n = this.mTail - i;
            }
            for (int j = n; j < this.mTail; ++j) {
                this.mElements[j] = null;
            }
            final int n2 = this.mTail - n;
            i -= n2;
            this.mTail -= n2;
            if (i > 0) {
                this.mTail = this.mElements.length;
                int mTail;
                for (mTail = (i = this.mTail - i); i < this.mTail; ++i) {
                    this.mElements[i] = null;
                }
                this.mTail = mTail;
            }
            return;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public void removeFromStart(int i) {
        if (i <= 0) {
            return;
        }
        if (i <= this.size()) {
            int length = this.mElements.length;
            if (i < length - this.mHead) {
                length = this.mHead + i;
            }
            for (int j = this.mHead; j < length; ++j) {
                this.mElements[j] = null;
            }
            final int n = length - this.mHead;
            final int mHead = i - n;
            this.mHead = (n + this.mHead & this.mCapacityBitmask);
            if (mHead > 0) {
                for (i = 0; i < mHead; ++i) {
                    this.mElements[i] = null;
                }
                this.mHead = mHead;
            }
            return;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public int size() {
        return this.mTail - this.mHead & this.mCapacityBitmask;
    }
}
