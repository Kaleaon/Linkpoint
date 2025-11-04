// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Queue;
import com.google.j2objc.annotations.Weak;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import com.google.common.math.IntMath;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.Beta;
import java.util.AbstractQueue;

@Beta
public final class MinMaxPriorityQueue<E> extends AbstractQueue<E>
{
    private static final int DEFAULT_CAPACITY = 11;
    private static final int EVEN_POWERS_OF_TWO = 1431655765;
    private static final int ODD_POWERS_OF_TWO = -1431655766;
    private final Heap maxHeap;
    @VisibleForTesting
    final int maximumSize;
    private final Heap minHeap;
    private int modCount;
    private Object[] queue;
    private int size;
    
    private MinMaxPriorityQueue(final Builder<? super E> builder, final int n) {
        final Ordering access$200 = ((Builder<Object>)builder).ordering();
        this.minHeap = new Heap(access$200);
        this.maxHeap = new Heap(access$200.reverse());
        this.minHeap.otherHeap = this.maxHeap;
        this.maxHeap.otherHeap = this.minHeap;
        this.maximumSize = ((Builder<Object>)builder).maximumSize;
        this.queue = new Object[n];
    }
    
    private int calculateNewCapacity() {
        final int length = this.queue.length;
        int checkedMultiply;
        if (length >= 64) {
            checkedMultiply = IntMath.checkedMultiply(length / 2, 3);
        }
        else {
            checkedMultiply = (length + 1) * 2;
        }
        return capAtMaximumSize(checkedMultiply, this.maximumSize);
    }
    
    private static int capAtMaximumSize(final int n, final int b) {
        return Math.min(n - 1, b) + 1;
    }
    
    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create() {
        return new Builder((Comparator)Ordering.natural()).create();
    }
    
    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create(final Iterable<? extends E> iterable) {
        return new Builder((Comparator)Ordering.natural()).create((Iterable)iterable);
    }
    
    public static Builder<Comparable> expectedSize(final int n) {
        return (Builder<Comparable>)new Builder<Comparable>((Comparator)Ordering.natural()).expectedSize(n);
    }
    
    private MoveDesc<E> fillHole(final int n, final E e) {
        final Heap heapForIndex = this.heapForIndex(n);
        final int fillHole = heapForIndex.fillHoleAt(n);
        final int bubbleUpAlternatingLevels = heapForIndex.bubbleUpAlternatingLevels(fillHole, e);
        if (bubbleUpAlternatingLevels != fillHole) {
            MoveDesc<E> moveDesc;
            if (bubbleUpAlternatingLevels >= n) {
                moveDesc = null;
            }
            else {
                moveDesc = new MoveDesc<E>(e, this.elementData(n));
            }
            return moveDesc;
        }
        return heapForIndex.tryCrossOverAndBubbleUp(n, fillHole, e);
    }
    
    private int getMaxElementIndex() {
        int n = 2;
        switch (this.size) {
            default: {
                if (this.maxHeap.compareElements(1, 2) <= 0) {
                    n = 1;
                }
                return n;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 1;
            }
        }
    }
    
    private void growIfNeeded() {
        if (this.size > this.queue.length) {
            final Object[] queue = new Object[this.calculateNewCapacity()];
            System.arraycopy(this.queue, 0, queue, 0, this.queue.length);
            this.queue = queue;
        }
    }
    
    private Heap heapForIndex(final int n) {
        Heap heap;
        if (!isEvenLevel(n)) {
            heap = this.maxHeap;
        }
        else {
            heap = this.minHeap;
        }
        return heap;
    }
    
    @VisibleForTesting
    static int initialQueueSize(int max, final int n, final Iterable<?> iterable) {
        if (max == -1) {
            max = 11;
        }
        if (iterable instanceof Collection) {
            max = Math.max(max, ((Collection)iterable).size());
        }
        return capAtMaximumSize(max, n);
    }
    
    @VisibleForTesting
    static boolean isEvenLevel(int n) {
        final boolean b = false;
        Preconditions.checkState(++n > 0, (Object)"negative index");
        return (0x55555555 & n) > (n & 0xAAAAAAAA) || b;
    }
    
    public static Builder<Comparable> maximumSize(final int n) {
        return (Builder<Comparable>)new Builder<Comparable>((Comparator)Ordering.natural()).maximumSize(n);
    }
    
    public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
        return new Builder<B>((Comparator)comparator);
    }
    
    private E removeAndGet(final int n) {
        final E elementData = this.elementData(n);
        this.removeAt(n);
        return elementData;
    }
    
    @Override
    public boolean add(final E e) {
        this.offer(e);
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        boolean b = false;
        final Iterator<? extends E> iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.offer(iterator.next());
            b = true;
        }
        return b;
    }
    
    @VisibleForTesting
    int capacity() {
        return this.queue.length;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.size; ++i) {
            this.queue[i] = null;
        }
        this.size = 0;
    }
    
    public Comparator<? super E> comparator() {
        return this.minHeap.ordering;
    }
    
    E elementData(final int n) {
        return (E)this.queue[n];
    }
    
    @VisibleForTesting
    boolean isIntact() {
        for (int i = 1; i < this.size; ++i) {
            if (!this.heapForIndex(i).verifyIndex(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new QueueIterator();
    }
    
    @Override
    public boolean offer(final E e) {
        Preconditions.checkNotNull(e);
        ++this.modCount;
        final int n = this.size++;
        this.growIfNeeded();
        this.heapForIndex(n).bubbleUp(n, e);
        return this.size <= this.maximumSize || this.pollLast() != e;
    }
    
    @Override
    public E peek() {
        E elementData;
        if (!this.isEmpty()) {
            elementData = this.elementData(0);
        }
        else {
            elementData = null;
        }
        return elementData;
    }
    
    public E peekFirst() {
        return this.peek();
    }
    
    public E peekLast() {
        E elementData;
        if (!this.isEmpty()) {
            elementData = this.elementData(this.getMaxElementIndex());
        }
        else {
            elementData = null;
        }
        return elementData;
    }
    
    @Override
    public E poll() {
        E removeAndGet;
        if (!this.isEmpty()) {
            removeAndGet = this.removeAndGet(0);
        }
        else {
            removeAndGet = null;
        }
        return removeAndGet;
    }
    
    public E pollFirst() {
        return this.poll();
    }
    
    public E pollLast() {
        E removeAndGet;
        if (!this.isEmpty()) {
            removeAndGet = this.removeAndGet(this.getMaxElementIndex());
        }
        else {
            removeAndGet = null;
        }
        return removeAndGet;
    }
    
    @VisibleForTesting
    MoveDesc<E> removeAt(final int n) {
        Preconditions.checkPositionIndex(n, this.size);
        ++this.modCount;
        --this.size;
        if (this.size == n) {
            this.queue[this.size] = null;
            return null;
        }
        final E elementData = this.elementData(this.size);
        final int correctLastElement = this.heapForIndex(this.size).getCorrectLastElement(elementData);
        final E elementData2 = this.elementData(this.size);
        this.queue[this.size] = null;
        final MoveDesc<E> fillHole = this.fillHole(n, elementData2);
        if (correctLastElement >= n) {
            return fillHole;
        }
        if (fillHole != null) {
            return new MoveDesc<E>(elementData, fillHole.replaced);
        }
        return new MoveDesc<E>(elementData, elementData2);
    }
    
    public E removeFirst() {
        return this.remove();
    }
    
    public E removeLast() {
        if (!this.isEmpty()) {
            return this.removeAndGet(this.getMaxElementIndex());
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[this.size];
        System.arraycopy(this.queue, 0, array, 0, this.size);
        return array;
    }
    
    @Beta
    public static final class Builder<B>
    {
        private static final int UNSET_EXPECTED_SIZE = -1;
        private final Comparator<B> comparator;
        private int expectedSize;
        private int maximumSize;
        
        private Builder(final Comparator<B> comparator) {
            this.expectedSize = -1;
            this.maximumSize = Integer.MAX_VALUE;
            this.comparator = Preconditions.checkNotNull(comparator);
        }
        
        private <T extends B> Ordering<T> ordering() {
            return Ordering.from((Comparator<T>)this.comparator);
        }
        
        public <T extends B> MinMaxPriorityQueue<T> create() {
            return this.create((Iterable<? extends T>)Collections.emptySet());
        }
        
        public <T extends B> MinMaxPriorityQueue<T> create(final Iterable<? extends T> iterable) {
            final MinMaxPriorityQueue minMaxPriorityQueue = new MinMaxPriorityQueue(this, MinMaxPriorityQueue.initialQueueSize(this.expectedSize, this.maximumSize, iterable), null);
            final Iterator<? extends T> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                minMaxPriorityQueue.offer(iterator.next());
            }
            return minMaxPriorityQueue;
        }
        
        public Builder<B> expectedSize(final int expectedSize) {
            boolean b = false;
            if (expectedSize >= 0) {
                b = true;
            }
            Preconditions.checkArgument(b);
            this.expectedSize = expectedSize;
            return this;
        }
        
        public Builder<B> maximumSize(final int maximumSize) {
            boolean b = false;
            if (maximumSize > 0) {
                b = true;
            }
            Preconditions.checkArgument(b);
            this.maximumSize = maximumSize;
            return this;
        }
    }
    
    private class Heap
    {
        final Ordering<E> ordering;
        @Weak
        Heap otherHeap;
        
        Heap(final Ordering<E> ordering) {
            this.ordering = ordering;
        }
        
        private int getGrandparentIndex(final int n) {
            return this.getParentIndex(this.getParentIndex(n));
        }
        
        private int getLeftChildIndex(final int n) {
            return n * 2 + 1;
        }
        
        private int getParentIndex(final int n) {
            return (n - 1) / 2;
        }
        
        private int getRightChildIndex(final int n) {
            return n * 2 + 2;
        }
        
        private boolean verifyIndex(final int n) {
            return (this.getLeftChildIndex(n) >= MinMaxPriorityQueue.this.size || this.compareElements(n, this.getLeftChildIndex(n)) <= 0) && (this.getRightChildIndex(n) >= MinMaxPriorityQueue.this.size || this.compareElements(n, this.getRightChildIndex(n)) <= 0) && (n <= 0 || this.compareElements(n, this.getParentIndex(n)) <= 0) && (n <= 2 || this.compareElements(this.getGrandparentIndex(n), n) <= 0);
        }
        
        void bubbleUp(final int n, final E e) {
            final int crossOverUp = this.crossOverUp(n, e);
            Heap otherHeap = this;
            int n2 = n;
            if (crossOverUp != n) {
                otherHeap = this.otherHeap;
                n2 = crossOverUp;
            }
            otherHeap.bubbleUpAlternatingLevels(n2, e);
        }
        
        int bubbleUpAlternatingLevels(int i, final E e) {
            while (i > 2) {
                final int grandparentIndex = this.getGrandparentIndex(i);
                final E elementData = MinMaxPriorityQueue.this.elementData(grandparentIndex);
                if (this.ordering.compare(elementData, e) <= 0) {
                    break;
                }
                MinMaxPriorityQueue.this.queue[i] = elementData;
                i = grandparentIndex;
            }
            MinMaxPriorityQueue.this.queue[i] = e;
            return i;
        }
        
        int compareElements(final int n, final int n2) {
            return this.ordering.compare(MinMaxPriorityQueue.this.elementData(n), MinMaxPriorityQueue.this.elementData(n2));
        }
        
        int crossOver(final int n, final E e) {
            final int minChild = this.findMinChild(n);
            if (minChild > 0 && this.ordering.compare(MinMaxPriorityQueue.this.elementData(minChild), e) < 0) {
                MinMaxPriorityQueue.this.queue[n] = MinMaxPriorityQueue.this.elementData(minChild);
                MinMaxPriorityQueue.this.queue[minChild] = e;
                return minChild;
            }
            return this.crossOverUp(n, e);
        }
        
        int crossOverUp(final int n, final E e) {
            if (n == 0) {
                MinMaxPriorityQueue.this.queue[0] = e;
                return 0;
            }
            int parentIndex = this.getParentIndex(n);
            E elementData = MinMaxPriorityQueue.this.elementData(parentIndex);
            if (parentIndex != 0) {
                final int rightChildIndex = this.getRightChildIndex(this.getParentIndex(parentIndex));
                if (rightChildIndex != parentIndex && this.getLeftChildIndex(rightChildIndex) >= MinMaxPriorityQueue.this.size) {
                    final E elementData2 = MinMaxPriorityQueue.this.elementData(rightChildIndex);
                    if (this.ordering.compare(elementData2, elementData) < 0) {
                        elementData = elementData2;
                        parentIndex = rightChildIndex;
                    }
                }
            }
            if (this.ordering.compare(elementData, e) >= 0) {
                MinMaxPriorityQueue.this.queue[n] = e;
                return n;
            }
            MinMaxPriorityQueue.this.queue[n] = elementData;
            MinMaxPriorityQueue.this.queue[parentIndex] = e;
            return parentIndex;
        }
        
        int fillHoleAt(int n) {
            while (true) {
                final int minGrandChild = this.findMinGrandChild(n);
                if (minGrandChild <= 0) {
                    break;
                }
                MinMaxPriorityQueue.this.queue[n] = MinMaxPriorityQueue.this.elementData(minGrandChild);
                n = minGrandChild;
            }
            return n;
        }
        
        int findMin(int i, final int n) {
            boolean b = false;
            if (i < MinMaxPriorityQueue.this.size) {
                if (i > 0) {
                    b = true;
                }
                Preconditions.checkState(b);
                final int min = Math.min(i, MinMaxPriorityQueue.this.size - n);
                final int n2 = i + 1;
                int n3 = i;
                for (i = n2; i < min + n; ++i) {
                    if (this.compareElements(i, n3) < 0) {
                        n3 = i;
                    }
                }
                return n3;
            }
            return -1;
        }
        
        int findMinChild(final int n) {
            return this.findMin(this.getLeftChildIndex(n), 2);
        }
        
        int findMinGrandChild(int leftChildIndex) {
            leftChildIndex = this.getLeftChildIndex(leftChildIndex);
            if (leftChildIndex >= 0) {
                return this.findMin(this.getLeftChildIndex(leftChildIndex), 4);
            }
            return -1;
        }
        
        int getCorrectLastElement(final E e) {
            final int parentIndex = this.getParentIndex(MinMaxPriorityQueue.this.size);
            if (parentIndex != 0) {
                final int rightChildIndex = this.getRightChildIndex(this.getParentIndex(parentIndex));
                if (rightChildIndex != parentIndex && this.getLeftChildIndex(rightChildIndex) >= MinMaxPriorityQueue.this.size) {
                    final E elementData = MinMaxPriorityQueue.this.elementData(rightChildIndex);
                    if (this.ordering.compare(elementData, e) < 0) {
                        MinMaxPriorityQueue.this.queue[rightChildIndex] = e;
                        MinMaxPriorityQueue.this.queue[MinMaxPriorityQueue.this.size] = elementData;
                        return rightChildIndex;
                    }
                }
            }
            return MinMaxPriorityQueue.this.size;
        }
        
        MoveDesc<E> tryCrossOverAndBubbleUp(final int n, final int n2, final E e) {
            final int crossOver = this.crossOver(n2, e);
            if (crossOver == n2) {
                return null;
            }
            Object o;
            if (crossOver >= n) {
                o = MinMaxPriorityQueue.this.elementData(this.getParentIndex(n));
            }
            else {
                o = MinMaxPriorityQueue.this.elementData(n);
            }
            if (this.otherHeap.bubbleUpAlternatingLevels(crossOver, e) >= n) {
                return null;
            }
            return (MoveDesc<E>)new MoveDesc(e, o);
        }
    }
    
    static class MoveDesc<E>
    {
        final E replaced;
        final E toTrickle;
        
        MoveDesc(final E toTrickle, final E replaced) {
            this.toTrickle = toTrickle;
            this.replaced = replaced;
        }
    }
    
    private class QueueIterator implements Iterator<E>
    {
        private boolean canRemove;
        private int cursor;
        private int expectedModCount;
        private Queue<E> forgetMeNot;
        private E lastFromForgetMeNot;
        private List<E> skipMe;
        
        private QueueIterator() {
            this.cursor = -1;
            this.expectedModCount = MinMaxPriorityQueue.this.modCount;
        }
        
        private boolean containsExact(final Iterable<E> iterable, final E e) {
            final Iterator<E> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == e) {
                    return true;
                }
            }
            return false;
        }
        
        private int nextNotInSkipMe(int n) {
            int n2 = n;
            if (this.skipMe != null) {
                while ((n = n2) < MinMaxPriorityQueue.this.size()) {
                    if (!this.containsExact(this.skipMe, MinMaxPriorityQueue.this.elementData(n2))) {
                        n = n2;
                        break;
                    }
                    ++n2;
                }
            }
            return n;
        }
        
        void checkModCount() {
            if (MinMaxPriorityQueue.this.modCount == this.expectedModCount) {
                return;
            }
            throw new ConcurrentModificationException();
        }
        
        @Override
        public boolean hasNext() {
            final boolean b = false;
            this.checkModCount();
            if (this.nextNotInSkipMe(this.cursor + 1) >= MinMaxPriorityQueue.this.size()) {
                boolean b2 = b;
                if (this.forgetMeNot == null) {
                    return b2;
                }
                if (this.forgetMeNot.isEmpty()) {
                    b2 = b;
                    return b2;
                }
            }
            return true;
        }
        
        @Override
        public E next() {
            this.checkModCount();
            final int nextNotInSkipMe = this.nextNotInSkipMe(this.cursor + 1);
            if (nextNotInSkipMe >= MinMaxPriorityQueue.this.size()) {
                if (this.forgetMeNot != null) {
                    this.cursor = MinMaxPriorityQueue.this.size();
                    this.lastFromForgetMeNot = this.forgetMeNot.poll();
                    if (this.lastFromForgetMeNot != null) {
                        this.canRemove = true;
                        return this.lastFromForgetMeNot;
                    }
                }
                throw new NoSuchElementException("iterator moved past last element in queue.");
            }
            this.cursor = nextNotInSkipMe;
            this.canRemove = true;
            return MinMaxPriorityQueue.this.elementData(this.cursor);
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            this.checkModCount();
            this.canRemove = false;
            ++this.expectedModCount;
            if (this.cursor >= MinMaxPriorityQueue.this.size()) {
                Preconditions.checkState(this.removeExact(this.lastFromForgetMeNot));
                this.lastFromForgetMeNot = null;
            }
            else {
                final MoveDesc<E> remove = MinMaxPriorityQueue.this.removeAt(this.cursor);
                if (remove != null) {
                    if (this.forgetMeNot == null) {
                        this.forgetMeNot = new ArrayDeque<E>();
                        this.skipMe = new ArrayList<E>(3);
                    }
                    this.forgetMeNot.add(remove.toTrickle);
                    this.skipMe.add(remove.replaced);
                }
                --this.cursor;
            }
        }
        
        boolean removeExact(final Object o) {
            for (int i = 0; i < MinMaxPriorityQueue.this.size; ++i) {
                if (MinMaxPriorityQueue.this.queue[i] == o) {
                    MinMaxPriorityQueue.this.removeAt(i);
                    return true;
                }
            }
            return false;
        }
    }
}
