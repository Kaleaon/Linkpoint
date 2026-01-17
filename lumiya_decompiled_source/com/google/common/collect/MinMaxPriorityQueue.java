package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.j2objc.annotations.Weak;
import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
@Beta
/* loaded from: classes.dex */
public final class MinMaxPriorityQueue<E> extends AbstractQueue<E> {
    private static final int DEFAULT_CAPACITY = 11;
    private static final int EVEN_POWERS_OF_TWO = 1431655765;
    private static final int ODD_POWERS_OF_TWO = -1431655766;
    private final MinMaxPriorityQueue<E>.Heap maxHeap;
    @VisibleForTesting
    final int maximumSize;
    private final MinMaxPriorityQueue<E>.Heap minHeap;
    private int modCount;
    private Object[] queue;
    private int size;

    @Beta
    /* loaded from: classes.dex */
    public static final class Builder<B> {
        private static final int UNSET_EXPECTED_SIZE = -1;
        private final Comparator<B> comparator;
        private int expectedSize;
        private int maximumSize;

        private Builder(Comparator<B> comparator) {
            this.expectedSize = -1;
            this.maximumSize = Integer.MAX_VALUE;
            this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public <T extends B> Ordering<T> ordering() {
            return Ordering.from(this.comparator);
        }

        public <T extends B> MinMaxPriorityQueue<T> create() {
            return create(Collections.emptySet());
        }

        public <T extends B> MinMaxPriorityQueue<T> create(Iterable<? extends T> iterable) {
            MinMaxPriorityQueue<T> minMaxPriorityQueue = new MinMaxPriorityQueue<>(this, MinMaxPriorityQueue.initialQueueSize(this.expectedSize, this.maximumSize, iterable));
            for (T t : iterable) {
                minMaxPriorityQueue.offer(t);
            }
            return minMaxPriorityQueue;
        }

        public Builder<B> expectedSize(int i) {
            Preconditions.checkArgument(i >= 0);
            this.expectedSize = i;
            return this;
        }

        public Builder<B> maximumSize(int i) {
            Preconditions.checkArgument(i > 0);
            this.maximumSize = i;
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Heap {
        final Ordering<E> ordering;
        @Weak
        MinMaxPriorityQueue<E>.Heap otherHeap;

        Heap(Ordering<E> ordering) {
            this.ordering = ordering;
        }

        private int getGrandparentIndex(int i) {
            return getParentIndex(getParentIndex(i));
        }

        private int getLeftChildIndex(int i) {
            return (i * 2) + 1;
        }

        private int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        private int getRightChildIndex(int i) {
            return (i * 2) + 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean verifyIndex(int i) {
            if (getLeftChildIndex(i) < MinMaxPriorityQueue.this.size && compareElements(i, getLeftChildIndex(i)) > 0) {
                return false;
            }
            if (getRightChildIndex(i) < MinMaxPriorityQueue.this.size && compareElements(i, getRightChildIndex(i)) > 0) {
                return false;
            }
            if (i > 0 && compareElements(i, getParentIndex(i)) > 0) {
                return false;
            }
            return i <= 2 || compareElements(getGrandparentIndex(i), i) <= 0;
        }

        void bubbleUp(int i, E e) {
            int crossOverUp = crossOverUp(i, e);
            if (crossOverUp != i) {
                this = this.otherHeap;
                i = crossOverUp;
            }
            this.bubbleUpAlternatingLevels(i, e);
        }

        int bubbleUpAlternatingLevels(int i, E e) {
            while (i > 2) {
                int grandparentIndex = getGrandparentIndex(i);
                Object elementData = MinMaxPriorityQueue.this.elementData(grandparentIndex);
                if (((Ordering<E>) this.ordering).compare(elementData, e) <= 0) {
                    break;
                }
                MinMaxPriorityQueue.this.queue[i] = elementData;
                i = grandparentIndex;
            }
            MinMaxPriorityQueue.this.queue[i] = e;
            return i;
        }

        int compareElements(int i, int i2) {
            return ((Ordering<E>) this.ordering).compare(MinMaxPriorityQueue.this.elementData(i), MinMaxPriorityQueue.this.elementData(i2));
        }

        int crossOver(int i, E e) {
            int findMinChild = findMinChild(i);
            if (findMinChild > 0 && ((Ordering<E>) this.ordering).compare(MinMaxPriorityQueue.this.elementData(findMinChild), e) < 0) {
                MinMaxPriorityQueue.this.queue[i] = MinMaxPriorityQueue.this.elementData(findMinChild);
                MinMaxPriorityQueue.this.queue[findMinChild] = e;
                return findMinChild;
            }
            return crossOverUp(i, e);
        }

        int crossOverUp(int i, E e) {
            int i2;
            if (i == 0) {
                MinMaxPriorityQueue.this.queue[0] = e;
                return 0;
            }
            int parentIndex = getParentIndex(i);
            Object elementData = MinMaxPriorityQueue.this.elementData(parentIndex);
            if (parentIndex == 0) {
                i2 = parentIndex;
            } else {
                int rightChildIndex = getRightChildIndex(getParentIndex(parentIndex));
                if (rightChildIndex != parentIndex && getLeftChildIndex(rightChildIndex) >= MinMaxPriorityQueue.this.size) {
                    Object elementData2 = MinMaxPriorityQueue.this.elementData(rightChildIndex);
                    if (((Ordering<E>) this.ordering).compare(elementData2, elementData) >= 0) {
                        i2 = parentIndex;
                    } else {
                        elementData = elementData2;
                        i2 = rightChildIndex;
                    }
                } else {
                    i2 = parentIndex;
                }
            }
            if (((Ordering<E>) this.ordering).compare(elementData, e) >= 0) {
                MinMaxPriorityQueue.this.queue[i] = e;
                return i;
            }
            MinMaxPriorityQueue.this.queue[i] = elementData;
            MinMaxPriorityQueue.this.queue[i2] = e;
            return i2;
        }

        int fillHoleAt(int i) {
            while (true) {
                int findMinGrandChild = findMinGrandChild(i);
                if (findMinGrandChild <= 0) {
                    return i;
                }
                MinMaxPriorityQueue.this.queue[i] = MinMaxPriorityQueue.this.elementData(findMinGrandChild);
                i = findMinGrandChild;
            }
        }

        int findMin(int i, int i2) {
            if (i < MinMaxPriorityQueue.this.size) {
                Preconditions.checkState(i > 0);
                int min = Math.min(i, MinMaxPriorityQueue.this.size - i2) + i2;
                int i3 = i;
                for (int i4 = i + 1; i4 < min; i4++) {
                    if (compareElements(i4, i3) < 0) {
                        i3 = i4;
                    }
                }
                return i3;
            }
            return -1;
        }

        int findMinChild(int i) {
            return findMin(getLeftChildIndex(i), 2);
        }

        int findMinGrandChild(int i) {
            int leftChildIndex = getLeftChildIndex(i);
            if (leftChildIndex >= 0) {
                return findMin(getLeftChildIndex(leftChildIndex), 4);
            }
            return -1;
        }

        int getCorrectLastElement(E e) {
            int rightChildIndex;
            int parentIndex = getParentIndex(MinMaxPriorityQueue.this.size);
            if (parentIndex != 0 && (rightChildIndex = getRightChildIndex(getParentIndex(parentIndex))) != parentIndex && getLeftChildIndex(rightChildIndex) >= MinMaxPriorityQueue.this.size) {
                Object elementData = MinMaxPriorityQueue.this.elementData(rightChildIndex);
                if (((Ordering<E>) this.ordering).compare(elementData, e) < 0) {
                    MinMaxPriorityQueue.this.queue[rightChildIndex] = e;
                    MinMaxPriorityQueue.this.queue[MinMaxPriorityQueue.this.size] = elementData;
                    return rightChildIndex;
                }
            }
            return MinMaxPriorityQueue.this.size;
        }

        MoveDesc<E> tryCrossOverAndBubbleUp(int i, int i2, E e) {
            int crossOver = crossOver(i2, e);
            if (crossOver != i2) {
                Object elementData = crossOver >= i ? MinMaxPriorityQueue.this.elementData(getParentIndex(i)) : MinMaxPriorityQueue.this.elementData(i);
                if (this.otherHeap.bubbleUpAlternatingLevels(crossOver, e) >= i) {
                    return null;
                }
                return new MoveDesc<>(e, elementData);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MoveDesc<E> {
        final E replaced;
        final E toTrickle;

        MoveDesc(E e, E e2) {
            this.toTrickle = e;
            this.replaced = e2;
        }
    }

    /* loaded from: classes.dex */
    private class QueueIterator implements Iterator<E> {
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

        private boolean containsExact(Iterable<E> iterable, E e) {
            for (E e2 : iterable) {
                if (e2 == e) {
                    return true;
                }
            }
            return false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private int nextNotInSkipMe(int i) {
            if (this.skipMe != null) {
                while (i < MinMaxPriorityQueue.this.size() && containsExact(this.skipMe, MinMaxPriorityQueue.this.elementData(i))) {
                    i++;
                }
            }
            return i;
        }

        void checkModCount() {
            if (MinMaxPriorityQueue.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            checkModCount();
            return nextNotInSkipMe(this.cursor + 1) < MinMaxPriorityQueue.this.size() || !(this.forgetMeNot == null || this.forgetMeNot.isEmpty());
        }

        @Override // java.util.Iterator
        public E next() {
            checkModCount();
            int nextNotInSkipMe = nextNotInSkipMe(this.cursor + 1);
            if (nextNotInSkipMe < MinMaxPriorityQueue.this.size()) {
                this.cursor = nextNotInSkipMe;
                this.canRemove = true;
                return (E) MinMaxPriorityQueue.this.elementData(this.cursor);
            }
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

        @Override // java.util.Iterator
        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            checkModCount();
            this.canRemove = false;
            this.expectedModCount++;
            if (this.cursor >= MinMaxPriorityQueue.this.size()) {
                Preconditions.checkState(removeExact(this.lastFromForgetMeNot));
                this.lastFromForgetMeNot = null;
                return;
            }
            MoveDesc<E> removeAt = MinMaxPriorityQueue.this.removeAt(this.cursor);
            if (removeAt != null) {
                if (this.forgetMeNot == null) {
                    this.forgetMeNot = new ArrayDeque();
                    this.skipMe = new ArrayList(3);
                }
                this.forgetMeNot.add(removeAt.toTrickle);
                this.skipMe.add(removeAt.replaced);
            }
            this.cursor--;
        }

        boolean removeExact(Object obj) {
            for (int i = 0; i < MinMaxPriorityQueue.this.size; i++) {
                if (MinMaxPriorityQueue.this.queue[i] == obj) {
                    MinMaxPriorityQueue.this.removeAt(i);
                    return true;
                }
            }
            return false;
        }
    }

    private MinMaxPriorityQueue(Builder<? super E> builder, int i) {
        Ordering ordering = builder.ordering();
        this.minHeap = new Heap(ordering);
        this.maxHeap = new Heap(ordering.reverse());
        this.minHeap.otherHeap = this.maxHeap;
        this.maxHeap.otherHeap = this.minHeap;
        this.maximumSize = ((Builder) builder).maximumSize;
        this.queue = new Object[i];
    }

    private int calculateNewCapacity() {
        int length = this.queue.length;
        return capAtMaximumSize(length >= 64 ? IntMath.checkedMultiply(length / 2, 3) : (length + 1) * 2, this.maximumSize);
    }

    private static int capAtMaximumSize(int i, int i2) {
        return Math.min(i - 1, i2) + 1;
    }

    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create() {
        return new Builder(Ordering.natural()).create();
    }

    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create(Iterable<? extends E> iterable) {
        return new Builder(Ordering.natural()).create(iterable);
    }

    public static Builder<Comparable> expectedSize(int i) {
        return new Builder(Ordering.natural()).expectedSize(i);
    }

    private MoveDesc<E> fillHole(int i, E e) {
        MinMaxPriorityQueue<E>.Heap heapForIndex = heapForIndex(i);
        int fillHoleAt = heapForIndex.fillHoleAt(i);
        int bubbleUpAlternatingLevels = heapForIndex.bubbleUpAlternatingLevels(fillHoleAt, e);
        if (bubbleUpAlternatingLevels != fillHoleAt) {
            if (bubbleUpAlternatingLevels >= i) {
                return null;
            }
            return new MoveDesc<>(e, elementData(i));
        }
        return heapForIndex.tryCrossOverAndBubbleUp(i, fillHoleAt, e);
    }

    private int getMaxElementIndex() {
        switch (this.size) {
            case 1:
                return 0;
            case 2:
                return 1;
            default:
                return this.maxHeap.compareElements(1, 2) > 0 ? 2 : 1;
        }
    }

    private void growIfNeeded() {
        if (this.size <= this.queue.length) {
            return;
        }
        Object[] objArr = new Object[calculateNewCapacity()];
        System.arraycopy(this.queue, 0, objArr, 0, this.queue.length);
        this.queue = objArr;
    }

    private MinMaxPriorityQueue<E>.Heap heapForIndex(int i) {
        return !isEvenLevel(i) ? this.maxHeap : this.minHeap;
    }

    @VisibleForTesting
    static int initialQueueSize(int i, int i2, Iterable<?> iterable) {
        if (i == -1) {
            i = 11;
        }
        if (iterable instanceof Collection) {
            i = Math.max(i, ((Collection) iterable).size());
        }
        return capAtMaximumSize(i, i2);
    }

    @VisibleForTesting
    static boolean isEvenLevel(int i) {
        int i2 = i + 1;
        Preconditions.checkState(i2 > 0, "negative index");
        return (EVEN_POWERS_OF_TWO & i2) > (i2 & ODD_POWERS_OF_TWO);
    }

    public static Builder<Comparable> maximumSize(int i) {
        return new Builder(Ordering.natural()).maximumSize(i);
    }

    public static <B> Builder<B> orderedBy(Comparator<B> comparator) {
        return new Builder<>(comparator);
    }

    private E removeAndGet(int i) {
        E elementData = elementData(i);
        removeAt(i);
        return elementData;
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection, java.util.Queue
    public boolean add(E e) {
        offer(e);
        return true;
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        boolean z = false;
        for (E e : collection) {
            offer(e);
            z = true;
        }
        return z;
    }

    @VisibleForTesting
    int capacity() {
        return this.queue.length;
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        for (int i = 0; i < this.size; i++) {
            this.queue[i] = null;
        }
        this.size = 0;
    }

    public Comparator<? super E> comparator() {
        return this.minHeap.ordering;
    }

    E elementData(int i) {
        return (E) this.queue[i];
    }

    @VisibleForTesting
    boolean isIntact() {
        for (int i = 1; i < this.size; i++) {
            if (!heapForIndex(i).verifyIndex(i)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new QueueIterator();
    }

    @Override // java.util.Queue
    public boolean offer(E e) {
        Preconditions.checkNotNull(e);
        this.modCount++;
        int i = this.size;
        this.size = i + 1;
        growIfNeeded();
        heapForIndex(i).bubbleUp(i, e);
        return this.size <= this.maximumSize || pollLast() != e;
    }

    @Override // java.util.Queue
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return elementData(0);
    }

    public E peekFirst() {
        return peek();
    }

    public E peekLast() {
        if (isEmpty()) {
            return null;
        }
        return elementData(getMaxElementIndex());
    }

    @Override // java.util.Queue
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        return removeAndGet(0);
    }

    public E pollFirst() {
        return poll();
    }

    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        return removeAndGet(getMaxElementIndex());
    }

    @VisibleForTesting
    MoveDesc<E> removeAt(int i) {
        Preconditions.checkPositionIndex(i, this.size);
        this.modCount++;
        this.size--;
        if (this.size == i) {
            this.queue[this.size] = null;
            return null;
        }
        E elementData = elementData(this.size);
        int correctLastElement = heapForIndex(this.size).getCorrectLastElement(elementData);
        E elementData2 = elementData(this.size);
        this.queue[this.size] = null;
        MoveDesc<E> fillHole = fillHole(i, elementData2);
        return correctLastElement >= i ? fillHole : fillHole != null ? new MoveDesc<>(elementData, fillHole.replaced) : new MoveDesc<>(elementData, elementData2);
    }

    public E removeFirst() {
        return remove();
    }

    public E removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return removeAndGet(getMaxElementIndex());
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        Object[] objArr = new Object[this.size];
        System.arraycopy(this.queue, 0, objArr, 0, this.size);
        return objArr;
    }
}
