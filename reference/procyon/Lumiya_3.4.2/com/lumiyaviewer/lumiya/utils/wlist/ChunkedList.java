// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.Collections;
import java.util.Comparator;
import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.AbstractList;

public class ChunkedList<E> extends AbstractList<E> implements RandomAccess
{
    private final List<List<E>> chunks;
    private int count;
    private List<E> lastChunk;
    private int lastChunkIndex;
    private int lastChunkSize;
    private int lastChunkStart;
    
    public ChunkedList() {
        this.chunks = new ArrayList<List<E>>();
        this.count = 0;
        this.lastChunk = null;
        this.lastChunkIndex = 0;
        this.lastChunkStart = 0;
        this.lastChunkSize = 0;
    }
    
    private void checkConsistency() {
        final Iterator<Object> iterator = this.chunks.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            i += iterator.next().size();
        }
        if (i != this.count) {
            throw new IllegalStateException(String.format("newCount %d, count %d", i, this.count));
        }
    }
    
    private int replaceElementInChunk(final List<E> list, @Nonnull final E key, @Nonnull final Comparator<E> c) {
        if (list.isEmpty()) {
            return -1;
        }
        final int binarySearch = Collections.binarySearch((List<? extends E>)list, key, c);
        if (binarySearch >= 0) {
            return this.replaceFoundElement(list, binarySearch, key);
        }
        return -1;
    }
    
    private int replaceFoundElement(final List<E> list, final int n, @Nonnull final E e) {
        list.set(n, e);
        final Iterator<Object> iterator = this.chunks.iterator();
        int n2 = 0;
        while (iterator.hasNext()) {
            final List list2 = iterator.next();
            if (list2 == list) {
                return n2 + n;
            }
            n2 += list2.size();
        }
        return -1;
    }
    
    private void resetLastPosition() {
        this.lastChunk = null;
        this.checkConsistency();
    }
    
    private void setLastChunk(final int i) {
        if (i < 0 || i >= this.count) {
            throw new IndexOutOfBoundsException(String.format("index %d, count %d", i, this.count));
        }
        this.checkConsistency();
        if (this.lastChunk == null) {
            this.lastChunkIndex = 0;
            this.lastChunkStart = 0;
            this.lastChunk = this.chunks.get(this.lastChunkIndex);
            this.lastChunkSize = this.lastChunk.size();
        }
        while (i < this.lastChunkStart) {
            --this.lastChunkIndex;
            this.lastChunk = this.chunks.get(this.lastChunkIndex);
            this.lastChunkSize = this.lastChunk.size();
            this.lastChunkStart -= this.lastChunkSize;
        }
        while (i >= this.lastChunkStart + this.lastChunkSize) {
            ++this.lastChunkIndex;
            this.lastChunkStart += this.lastChunkSize;
            if (this.lastChunkIndex >= this.chunks.size()) {
                throw new IllegalStateException(String.format("lastChunkIndex runaway, position %d, count %d, lastChunkStart %d", i, this.count, this.lastChunkStart));
            }
            this.lastChunk = this.chunks.get(this.lastChunkIndex);
            this.lastChunkSize = this.lastChunk.size();
        }
    }
    
    public void addChunkAtEnd(final List<E> list) {
        this.chunks.add(list);
        this.count += list.size();
        this.resetLastPosition();
    }
    
    public void addChunkAtStart(final List<E> list) {
        this.chunks.add(0, list);
        this.count += list.size();
        this.resetLastPosition();
    }
    
    public void addElement(final E e, final int n, final ChunkFactory<E> chunkFactory) {
        List list = null;
        if (this.chunks.size() > 0) {
            list = this.chunks.get(this.chunks.size() - 1);
        }
        if (list != null && list.size() < n) {
            list.add(e);
            ++this.count;
            if (this.lastChunk == list) {
                ++this.lastChunkSize;
            }
        }
        else {
            final List<E> emptyChunk = chunkFactory.createEmptyChunk();
            emptyChunk.add(e);
            this.chunks.add(emptyChunk);
            ++this.count;
        }
        this.checkConsistency();
    }
    
    @Override
    public void clear() {
        this.chunks.clear();
        this.count = 0;
        this.resetLastPosition();
    }
    
    @Override
    public E get(final int n) {
        this.setLastChunk(n);
        if (n >= this.lastChunkStart && n < this.lastChunkStart + this.lastChunkSize) {
            return this.lastChunk.get(n - this.lastChunkStart);
        }
        throw new IndexOutOfBoundsException(String.format("index %d, count %d", n, this.count));
    }
    
    public int removeChunkAtEnd() {
        if (this.chunks.size() > 0) {
            final List list = this.chunks.remove(this.chunks.size() - 1);
            int size;
            if (list != null) {
                size = list.size();
            }
            else {
                size = 0;
            }
            this.count -= size;
            this.resetLastPosition();
            return size;
        }
        return 0;
    }
    
    public int removeChunkAtStart() {
        if (this.chunks.size() > 0) {
            final List list = this.chunks.remove(0);
            int size;
            if (list != null) {
                size = list.size();
            }
            else {
                size = 0;
            }
            this.count -= size;
            this.resetLastPosition();
            return size;
        }
        return 0;
    }
    
    public int removeElementsAfter(int i) {
        this.checkConsistency();
        if (i < 0 || i >= this.count) {
            return 0;
        }
        this.setLastChunk(i);
        if (i >= this.lastChunkStart && i < this.lastChunkStart + this.lastChunkSize) {
            final int lastChunkIndex = this.lastChunkIndex;
            i = this.chunks.size();
            int n = 0;
            --i;
            while (i >= lastChunkIndex + 2) {
                n += this.chunks.get(i).size();
                this.chunks.remove(i);
                --i;
            }
            this.count -= n;
            this.checkConsistency();
            return n;
        }
        return 0;
    }
    
    public int removeElementsBefore(int i) {
        this.checkConsistency();
        if (i < 0 || i >= this.count) {
            return 0;
        }
        this.setLastChunk(i);
        if (i >= this.lastChunkStart && i < this.lastChunkStart + this.lastChunkSize) {
            i = this.lastChunkIndex - 2;
            int n;
            if (i >= 0) {
                ++i;
                n = 0;
            }
            else {
                i = 0;
                n = 0;
            }
            while (i > 0) {
                n += this.chunks.get(0).size();
                this.chunks.remove(0);
                --i;
            }
            this.count -= n;
            this.resetLastPosition();
            return n;
        }
        return 0;
    }
    
    public int replaceElement(@Nonnull final E e, @Nonnull final Comparator<E> comparator) {
        if (this.chunks.isEmpty()) {
            return -1;
        }
        int n = this.chunks.size() / 2;
        int n2 = 0;
        while (true) {
            final List list = this.chunks.get(n);
            int n3 = 0;
            int n4 = 0;
            Label_0311: {
                int n6 = 0;
                Label_0301: {
                    if (!list.isEmpty()) {
                        final Object value = list.get(0);
                        final Object value2 = list.get(list.size() - 1);
                        final int compare = comparator.compare(e, (E)value);
                        if (compare == 0) {
                            return this.replaceFoundElement(list, 0, e);
                        }
                        if (compare < 0) {
                            n3 = n - 1;
                            if (n3 < 0) {
                                return -1;
                            }
                            n4 = -1;
                            break Label_0311;
                        }
                        else {
                            final int compare2 = comparator.compare(e, (E)value2);
                            if (compare2 == 0) {
                                return this.replaceFoundElement(list, list.size() - 1, e);
                            }
                            if (compare2 <= 0) {
                                return this.replaceElementInChunk(list, e, comparator);
                            }
                            final int n5 = n + 1;
                            n2 = 1;
                            if ((n6 = n5) >= this.chunks.size()) {
                                return -1;
                            }
                        }
                    }
                    else if (n2 < 0) {
                        if ((n6 = n - 1) < 0) {
                            return -1;
                        }
                    }
                    else {
                        if (n2 <= 0) {
                            int i = 0;
                            while (true) {
                                while (i < this.chunks.size()) {
                                    if (!this.chunks.get(i).isEmpty()) {
                                        final int n7 = i;
                                        n6 = n7;
                                        if (n7 == -1) {
                                            return -1;
                                        }
                                        break Label_0301;
                                    }
                                    else {
                                        ++i;
                                    }
                                }
                                final int n7 = -1;
                                continue;
                            }
                        }
                        if ((n6 = n + 1) >= this.chunks.size()) {
                            return -1;
                        }
                    }
                }
                final int n8 = n2;
                n3 = n6;
                n4 = n8;
            }
            final int n9 = n3;
            n2 = n4;
            n = n9;
        }
    }
    
    @Override
    public int size() {
        return this.count;
    }
    
    public interface ChunkFactory<E>
    {
        List<E> createEmptyChunk();
    }
}
