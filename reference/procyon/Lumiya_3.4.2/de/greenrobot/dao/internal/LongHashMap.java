// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.internal;

import de.greenrobot.dao.DaoLog;
import java.util.Arrays;

public final class LongHashMap<T>
{
    private int capacity;
    private int size;
    private Entry<T>[] table;
    private int threshold;
    
    public LongHashMap() {
        this(16);
    }
    
    public LongHashMap(final int capacity) {
        this.capacity = capacity;
        this.threshold = capacity * 4 / 3;
        this.table = new Entry[capacity];
    }
    
    public void clear() {
        this.size = 0;
        Arrays.fill(this.table, null);
    }
    
    public boolean containsKey(final long n) {
        for (Entry<T> next = this.table[(((int)(n >>> 32) ^ (int)n) & Integer.MAX_VALUE) % this.capacity]; next != null; next = next.next) {
            if (next.key == n) {
                return true;
            }
        }
        return false;
    }
    
    public T get(final long n) {
        for (Entry<T> next = this.table[(((int)(n >>> 32) ^ (int)n) & Integer.MAX_VALUE) % this.capacity]; next != null; next = next.next) {
            if (next.key == n) {
                return next.value;
            }
        }
        return null;
    }
    
    public void logStats() {
        final Entry<T>[] table = this.table;
        final int length = table.length;
        int i = 0;
        int j = 0;
        while (i < length) {
            for (Entry<T> next = table[i]; next != null && next.next != null; next = next.next) {
                ++j;
            }
            ++i;
        }
        DaoLog.d("load: " + this.size / (float)this.capacity + ", size: " + this.size + ", capa: " + this.capacity + ", collisions: " + j + ", collision ratio: " + j / (float)this.size);
    }
    
    public T put(final long n, final T value) {
        final int n2 = (((int)(n >>> 32) ^ (int)n) & Integer.MAX_VALUE) % this.capacity;
        Entry<T> next;
        Entry<T> entry;
        for (entry = (next = this.table[n2]); next != null; next = next.next) {
            if (next.key == n) {
                final T value2 = next.value;
                next.value = value;
                return value2;
            }
        }
        this.table[n2] = new Entry<T>(n, value, (Entry<Object>)entry);
        ++this.size;
        if (this.size > this.threshold) {
            this.setCapacity(this.capacity * 2);
        }
        return null;
    }
    
    public T remove(final long n) {
        final int n2 = (((int)(n >>> 32) ^ (int)n) & Integer.MAX_VALUE) % this.capacity;
        Entry<T> entry = this.table[n2];
        Entry<T> entry2 = null;
        while (entry != null) {
            final Entry<T> next = entry.next;
            if (entry.key == n) {
                if (entry2 != null) {
                    entry2.next = next;
                }
                else {
                    this.table[n2] = next;
                }
                --this.size;
                return entry.value;
            }
            entry2 = entry;
            entry = next;
        }
        return null;
    }
    
    public void reserveRoom(final int n) {
        this.setCapacity(n * 5 / 3);
    }
    
    public void setCapacity(final int capacity) {
        final Entry[] table = new Entry[capacity];
        for (int length = this.table.length, i = 0; i < length; ++i) {
            Entry<T> next;
            for (Entry<T> entry = this.table[i]; entry != null; entry = next) {
                final long key = entry.key;
                final int n = (((int)(key >>> 32) ^ (int)key) & Integer.MAX_VALUE) % capacity;
                next = entry.next;
                entry.next = table[n];
                table[n] = entry;
            }
        }
        this.table = table;
        this.capacity = capacity;
        this.threshold = capacity * 4 / 3;
    }
    
    public int size() {
        return this.size;
    }
    
    static final class Entry<T>
    {
        final long key;
        Entry<T> next;
        T value;
        
        Entry(final long key, final T value, final Entry<T> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
