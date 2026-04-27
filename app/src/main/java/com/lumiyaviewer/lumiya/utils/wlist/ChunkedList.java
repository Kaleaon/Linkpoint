package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nonnull;

public class ChunkedList<E> extends AbstractList<E> implements RandomAccess {
    private final List<List<E>> chunks = new ArrayList();
    private int count = 0;
    private List<E> lastChunk = null;
    private int lastChunkIndex = 0;
    private int lastChunkSize = 0;
    private int lastChunkStart = 0;

    public interface ChunkFactory<E> {
        List<E> createEmptyChunk();
    }

    private void checkConsistency() {
        int i = 0;
        for (List size : this.chunks) {
            i = size.size() + i;
        }
        if (i != this.count) {
            throw new IllegalStateException(String.format("newCount %d, count %d", new Object[]{Integer.valueOf(i), Integer.valueOf(this.count)}));
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
            throw new IndexOutOfBoundsException(String.format("index %d, count %d", new Object[]{Integer.valueOf(i), Integer.valueOf(this.count)}));
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
                throw new IllegalStateException(String.format("lastChunkIndex runaway, position %d, count %d, lastChunkStart %d", new Object[]{Integer.valueOf(i), Integer.valueOf(this.count), Integer.valueOf(this.lastChunkStart)}));
            } else {
                this.lastChunk = this.chunks.get(this.lastChunkIndex);
                this.lastChunkSize = this.lastChunk.size();
            }
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
        List<E> list = null;
        if (this.chunks.size() > 0) {
            list = this.chunks.get(this.chunks.size() - 1);
        }
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

    public void clear() {
        this.chunks.clear();
        this.count = 0;
        resetLastPosition();
    }

    public E get(int i) {
        setLastChunk(i);
        if (i >= this.lastChunkStart && i < this.lastChunkStart + this.lastChunkSize) {
            return this.lastChunk.get(i - this.lastChunkStart);
        }
        throw new IndexOutOfBoundsException(String.format("index %d, count %d", new Object[]{Integer.valueOf(i), Integer.valueOf(this.count)}));
    }

    public int removeChunkAtEnd() {
        if (this.chunks.size() <= 0) {
            return 0;
        }
        List remove = this.chunks.remove(this.chunks.size() - 1);
        int size = remove != null ? remove.size() : 0;
        this.count -= size;
        resetLastPosition();
        return size;
    }

    public int removeChunkAtStart() {
        if (this.chunks.size() <= 0) {
            return 0;
        }
        List remove = this.chunks.remove(0);
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
            i3 = i4 + 1;
            i2 = 0;
        } else {
            i3 = 0;
            i2 = 0;
        }
        while (i3 > 0) {
            i2 += this.chunks.get(0).size();
            this.chunks.remove(0);
            i3--;
        }
        this.count -= i2;
        resetLastPosition();
        return i2;
    }

    public int replaceElement(@Nonnull E e, @Nonnull Comparator<E> comparator) {
        char c;
        if (this.chunks.isEmpty()) {
            return -1;
        }
        char c2 = 0;
        int size = this.chunks.size() / 2;
        while (true) {
            List list = this.chunks.get(size);
            if (!list.isEmpty()) {
                Object obj = list.get(0);
                Object obj2 = list.get(list.size() - 1);
                int compare = comparator.compare(e, obj);
                if (compare == 0) {
                    return replaceFoundElement(list, 0, e);
                }
                if (compare < 0) {
                    i = size - 1;
                    if (i < 0) {
                        return -1;
                    }
                    c = 65535;
                    size = i;
                    c2 = c;
                } else {
                    int compare2 = comparator.compare(e, obj2);
                    if (compare2 == 0) {
                        return replaceFoundElement(list, list.size() - 1, e);
                    }
                    if (compare2 <= 0) {
                        return replaceElementInChunk(list, e, comparator);
                    }
                    i2 = size + 1;
                    c2 = 1;
                    if (i2 >= this.chunks.size()) {
                        return -1;
                    }
                }
            } else if (c2 < 0) {
                i2 = size - 1;
                if (i2 < 0) {
                    return -1;
                }
            } else if (c2 > 0) {
                i2 = size + 1;
                if (i2 >= this.chunks.size()) {
                    return -1;
                }
            } else {
                int i3 = 0;
                while (true) {
                    if (i3 >= this.chunks.size()) {
                        i2 = -1;
                        break;
                    } else if (!this.chunks.get(i3).isEmpty()) {
                        i2 = i3;
                        break;
                    } else {
                        i3++;
                    }
                }
                if (i2 == -1) {
                    return -1;
                }
            }
            char c3 = c2;
            i = i2;
            c = c3;
            size = i;
            c2 = c;
        }
    }

    public int size() {
        return this.count;
    }
}
