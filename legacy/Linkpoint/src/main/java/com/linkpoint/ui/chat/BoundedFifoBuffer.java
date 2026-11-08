package com.linkpoint.ui.chat;

import java.util.AbstractList;
import java.util.Arrays;

/**
 * A thread-safe, fixed-capacity FIFO ring buffer that automatically evicts
 * the oldest element when new elements are inserted at capacity.
 * Provides O(1) time complexity for additions, index access, and capacity management.
 */
public class BoundedFifoBuffer<E> extends AbstractList<E> {

    private Object[] elements;
    private int capacity;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    public BoundedFifoBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.elements = new Object[capacity];
    }

    public synchronized int getCapacity() {
        return capacity;
    }

    public synchronized void setCapacity(int newCapacity) {
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        if (this.capacity == newCapacity) {
            return;
        }

        Object[] newElements = new Object[newCapacity];
        int countToCopy = Math.min(size, newCapacity);
        int dropCount = size - countToCopy;

        for (int i = 0; i < countToCopy; i++) {
            int oldIdx = (head + dropCount + i) % capacity;
            newElements[i] = elements[oldIdx];
        }

        this.elements = newElements;
        this.capacity = newCapacity;
        this.head = 0;
        this.tail = countToCopy % newCapacity;
        this.size = countToCopy;
        this.modCount++;
    }

    @Override
    public synchronized boolean add(E element) {
        if (size == capacity) {
            // Evict oldest element (FIFO)
            elements[head] = null;
            head = (head + 1) % capacity;
            size--;
        }

        elements[tail] = element;
        tail = (tail + 1) % capacity;
        size++;
        this.modCount++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int actualIndex = (head + index) % capacity;
        return (E) elements[actualIndex];
    }

    @Override
    public synchronized int size() {
        return size;
    }

    @Override
    public synchronized void clear() {
        Arrays.fill(elements, null);
        head = 0;
        tail = 0;
        size = 0;
        this.modCount++;
    }
}
