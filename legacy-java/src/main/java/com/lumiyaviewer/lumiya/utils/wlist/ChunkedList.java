package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class ChunkedList<E> extends AbstractList<E> implements RandomAccess {
    private final List<List<E>> chunks = new ArrayList();
    private int count = 0;
    private List<E> lastChunk = null;
    private int lastChunkIndex = 0;
    private int lastChunkStart = 0;
    private int lastChunkSize = 0;

    /* loaded from: classes.dex */
    public interface ChunkFactory<E> {
        List<E> createEmptyChunk();
    }

    private void checkConsistency() {
        Iterator<T> it = this.chunks.iterator();
        int i = 0;
        while (it.hasNext()) {
            i = ((List) it.next()).size() + i;
        }
        if (i != this.count) {
            throw new IllegalStateException(String.format("newCount %d, count %d", Integer.valueOf(i), Integer.valueOf(this.count)));
        }
    }

    private int replaceElementInChunk(List<E> list, @Nonnull E e, @Nonnull Comparator<E> comparator) {
        int binarySearch;
        if (list.isEmpty() || (binarySearch = Collections.binarySearch(list, e, comparator)) < 0) {
            return -1;
        }
        return replaceFoundElement(list, binarySearch, e);
    }

    private int replaceFoundElement(List<E> list, int i, @Nonnull E e) {
        list.set(i, e);
        int i2 = 0;
        Iterator<T> it = this.chunks.iterator();
        while (true) {
            int i3 = i2;
            if (!it.hasNext()) {
                return -1;
            }
            List<E> list2 = (List) it.next();
            if (list2 == list) {
                return i3 + i;
            }
            i2 = list2.size() + i3;
        }
    }

    private void resetLastPosition() {
        this.lastChunk = null;
        checkConsistency();
    }

    private void setLastChunk(int i) {
        if (i < 0 || i >= this.count) {
            throw new IndexOutOfBoundsException(String.format("index %d, count %d", Integer.valueOf(i), Integer.valueOf(this.count)));
        }
        checkConsistency();
        if (this.lastChunk == null) {
            this.lastChunkIndex = 0;
            this.lastChunkStart = 0;
            this.lastChunk = this.chunks.get(this.lastChunkIndex);
            this.lastChunkSize = this.lastChunk.size();
        }
        while (i < this.lastChunkStart) {
            this.lastChunkIndex--;
            this.lastChunk = this.chunks.get(this.lastChunkIndex);
            this.lastChunkSize = this.lastChunk.size();
            this.lastChunkStart -= this.lastChunkSize;
        }
        while (i >= this.lastChunkStart + this.lastChunkSize) {
            this.lastChunkIndex++;
            this.lastChunkStart += this.lastChunkSize;
            if (this.lastChunkIndex >= this.chunks.size()) {
                throw new IllegalStateException(String.format("lastChunkIndex runaway, position %d, count %d, lastChunkStart %d", Integer.valueOf(i), Integer.valueOf(this.count), Integer.valueOf(this.lastChunkStart)));
            }
            this.lastChunk = this.chunks.get(this.lastChunkIndex);
            this.lastChunkSize = this.lastChunk.size();
        }
    }

    public void addChunkAtEnd(List<E> list) {
        this.chunks.add(list);
        this.count += list.size();
        resetLastPosition();
    }

    public void addChunkAtStart(List<E> list) {
        this.chunks.add(0, list);
        this.count += list.size();
        resetLastPosition();
    }

    public void addElement(E e, int i, ChunkFactory<E> chunkFactory) {
        List<E> list = this.chunks.size() > 0 ? this.chunks.get(this.chunks.size() - 1) : null;
        if (list == null || list.size() >= i) {
            List<E> createEmptyChunk = chunkFactory.createEmptyChunk();
            createEmptyChunk.add(e);
            this.chunks.add(createEmptyChunk);
            this.count++;
        } else {
            list.add(e);
            this.count++;
            if (this.lastChunk == list) {
                this.lastChunkSize++;
            }
        }
        checkConsistency();
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.chunks.clear();
        this.count = 0;
        resetLastPosition();
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int i) {
        setLastChunk(i);
        if (i < this.lastChunkStart || i >= this.lastChunkStart + this.lastChunkSize) {
            throw new IndexOutOfBoundsException(String.format("index %d, count %d", Integer.valueOf(i), Integer.valueOf(this.count)));
        }
        return this.lastChunk.get(i - this.lastChunkStart);
    }

    public int removeChunkAtEnd() {
        if (this.chunks.size() <= 0) {
            return 0;
        }
        List<E> remove = this.chunks.remove(this.chunks.size() - 1);
        int size = remove != null ? remove.size() : 0;
        this.count -= size;
        resetLastPosition();
        return size;
    }

    public int removeChunkAtStart() {
        if (this.chunks.size() <= 0) {
            return 0;
        }
        List<E> remove = this.chunks.remove(0);
        int size = remove != null ? remove.size() : 0;
        this.count -= size;
        resetLastPosition();
        return size;
    }

    public int removeElementsAfter(int i) {
        checkConsistency();
        if (i < 0 || i >= this.count) {
            return 0;
        }
        setLastChunk(i);
        if (i < this.lastChunkStart || i >= this.lastChunkStart + this.lastChunkSize) {
            return 0;
        }
        int i2 = this.lastChunkIndex + 2;
        int i3 = 0;
        for (int size = this.chunks.size() - 1; size >= i2; size--) {
            i3 += this.chunks.get(size).size();
            this.chunks.remove(size);
        }
        this.count -= i3;
        checkConsistency();
        return i3;
    }

    public int removeElementsBefore(int i) {
        int i2;
        int i3;
        checkConsistency();
        if (i < 0 || i >= this.count) {
            return 0;
        }
        setLastChunk(i);
        if (i < this.lastChunkStart || i >= this.lastChunkStart + this.lastChunkSize) {
            return 0;
        }
        int i4 = this.lastChunkIndex - 2;
        if (i4 >= 0) {
            i2 = i4 + 1;
            i3 = 0;
        } else {
            i2 = 0;
            i3 = 0;
        }
        while (i2 > 0) {
            i3 += this.chunks.get(0).size();
            this.chunks.remove(0);
            i2--;
        }
        this.count -= i3;
        resetLastPosition();
        return i3;
    }

    public int replaceElement(@Nonnull E e, @Nonnull Comparator<E> comparator) {
        int i;
        int i2;
        char c;
        if (this.chunks.isEmpty()) {
            return -1;
        }
        char c2 = 0;
        int size = this.chunks.size() / 2;
        while (true) {
            List<E> list = this.chunks.get(size);
            if (!list.isEmpty()) {
                E e2 = list.get(0);
                E e3 = list.get(list.size() - 1);
                int compare = comparator.compare(e, e2);
                if (compare == 0) {
                    return replaceFoundElement(list, 0, e);
                }
                if (compare < 0) {
                    i2 = size - 1;
                    if (i2 < 0) {
                        return -1;
                    }
                    c = 65535;
                    size = i2;
                    c2 = c;
                } else {
                    int compare2 = comparator.compare(e, e3);
                    if (compare2 == 0) {
                        return replaceFoundElement(list, list.size() - 1, e);
                    }
                    if (compare2 <= 0) {
                        return replaceElementInChunk(list, e, comparator);
                    }
                    i = size + 1;
                    c2 = 1;
                    if (i >= this.chunks.size()) {
                        return -1;
                    }
                }
            } else if (c2 < 0) {
                i = size - 1;
                if (i < 0) {
                    return -1;
                }
            } else if (c2 > 0) {
                i = size + 1;
                if (i >= this.chunks.size()) {
                    return -1;
                }
            } else {
                int i3 = 0;
                while (true) {
                    if (i3 >= this.chunks.size()) {
                        i = -1;
                        break;
                    }
                    if (!this.chunks.get(i3).isEmpty()) {
                        i = i3;
                        break;
                    }
                    i3++;
                }
                if (i == -1) {
                    return -1;
                }
            }
            char c3 = c2;
            i2 = i;
            c = c3;
            size = i2;
            c2 = c;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.count;
    }
}
