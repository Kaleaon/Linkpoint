// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import java.util.NoSuchElementException;
import java.util.ListIterator;
import java.util.Iterator;
import de.greenrobot.dao.DaoException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import de.greenrobot.dao.InternalQueryDaoAccess;
import android.database.Cursor;
import java.io.Closeable;
import java.util.List;

public class LazyList<E> implements List<E>, Closeable
{
    private final Cursor cursor;
    private final InternalQueryDaoAccess<E> daoAccess;
    private final List<E> entities;
    private volatile int loadedCount;
    private final ReentrantLock lock;
    private final int size;
    
    LazyList(final InternalQueryDaoAccess<E> daoAccess, final Cursor cursor, final boolean b) {
        int i = 0;
        this.cursor = cursor;
        this.daoAccess = daoAccess;
        this.size = cursor.getCount();
        if (!b) {
            this.entities = null;
        }
        else {
            this.entities = new ArrayList<E>(this.size);
            while (i < this.size) {
                this.entities.add(null);
                ++i;
            }
        }
        if (this.size == 0) {
            cursor.close();
        }
        this.lock = new ReentrantLock();
    }
    
    @Override
    public void add(final int n, final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean add(final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }
    
    protected void checkCached() {
        if (this.entities != null) {
            return;
        }
        throw new DaoException("This operation only works with cached lazy lists");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() {
        this.cursor.close();
    }
    
    @Override
    public boolean contains(final Object o) {
        this.loadRemaining();
        return this.entities.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        this.loadRemaining();
        return this.entities.containsAll(collection);
    }
    
    @Override
    public E get(final int n) {
        Label_0029: {
            if (this.entities != null) {
                break Label_0029;
            }
            this.lock.lock();
            try {
                return this.loadEntity(n);
                Label_0046: {
                    this.lock.lock();
                }
                try {
                    if (this.entities.get(n) == null) {
                        this.entities.set(n, this.loadEntity(n));
                        ++this.loadedCount;
                        if (this.loadedCount == this.size) {
                            this.cursor.close();
                        }
                    }
                }
                finally {
                    this.lock.unlock();
                }
                final E value = this.entities.get(n);
                iftrue(Label_0046:)(value == null);
                return value;
            }
            finally {
                this.lock.unlock();
            }
        }
    }
    
    public int getLoadedCount() {
        return this.loadedCount;
    }
    
    @Override
    public int indexOf(final Object o) {
        this.loadRemaining();
        return this.entities.indexOf(o);
    }
    
    public boolean isClosed() {
        return this.cursor.isClosed();
    }
    
    @Override
    public boolean isEmpty() {
        boolean b = false;
        if (this.size == 0) {
            b = true;
        }
        return b;
    }
    
    public boolean isLoadedCompletely() {
        return this.loadedCount == this.size;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new LazyIterator(0, false);
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        this.loadRemaining();
        return this.entities.lastIndexOf(o);
    }
    
    @Override
    public CloseableListIterator<E> listIterator() {
        return new LazyIterator(0, false);
    }
    
    @Override
    public ListIterator<E> listIterator(final int n) {
        return new LazyIterator(n, false);
    }
    
    public CloseableListIterator<E> listIteratorAutoClose() {
        return new LazyIterator(0, true);
    }
    
    protected E loadEntity(final int n) {
        if (!this.cursor.moveToPosition(n)) {
            throw new DaoException("Could not move to cursor location " + n);
        }
        final E loadCurrent = this.daoAccess.loadCurrent(this.cursor, 0, true);
        if (loadCurrent != null) {
            return loadCurrent;
        }
        throw new DaoException("Loading of entity failed (null) at position " + n);
    }
    
    public void loadRemaining() {
        this.checkCached();
        for (int size = this.entities.size(), i = 0; i < size; ++i) {
            this.get(i);
        }
    }
    
    public E peak(final int n) {
        if (this.entities == null) {
            return null;
        }
        return this.entities.get(n);
    }
    
    @Override
    public E remove(final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E set(final int n, final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public List<E> subList(final int n, final int n2) {
        this.checkCached();
        for (int i = n; i < n2; ++i) {
            this.entities.get(i);
        }
        return this.entities.subList(n, n2);
    }
    
    @Override
    public Object[] toArray() {
        this.loadRemaining();
        return this.entities.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        this.loadRemaining();
        return this.entities.toArray(array);
    }
    
    protected class LazyIterator implements CloseableListIterator<E>
    {
        private final boolean closeWhenDone;
        private int index;
        
        public LazyIterator(final int index, final boolean closeWhenDone) {
            this.index = index;
            this.closeWhenDone = closeWhenDone;
        }
        
        @Override
        public void add(final E e) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void close() {
            LazyList.this.close();
        }
        
        @Override
        public boolean hasNext() {
            return this.index < LazyList.this.size;
        }
        
        @Override
        public boolean hasPrevious() {
            boolean b = false;
            if (this.index > 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public E next() {
            if (this.index < LazyList.this.size) {
                final E value = LazyList.this.get(this.index);
                ++this.index;
                if (this.index == LazyList.this.size && this.closeWhenDone) {
                    this.close();
                }
                return value;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public int nextIndex() {
            return this.index;
        }
        
        @Override
        public E previous() {
            if (this.index > 0) {
                --this.index;
                return LazyList.this.get(this.index);
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public int previousIndex() {
            return this.index - 1;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void set(final E e) {
            throw new UnsupportedOperationException();
        }
    }
}
