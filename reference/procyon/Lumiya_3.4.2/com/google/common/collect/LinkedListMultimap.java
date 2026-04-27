// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.AbstractSequentialList;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(emulated = true, serializable = true)
public class LinkedListMultimap<K, V> extends AbstractMultimap<K, V> implements ListMultimap<K, V>, Serializable
{
    @GwtIncompatible("java serialization not supported")
    private static final long serialVersionUID = 0L;
    private transient Node<K, V> head;
    private transient Map<K, KeyList<K, V>> keyToKeyList;
    private transient int modCount;
    private transient int size;
    private transient Node<K, V> tail;
    
    LinkedListMultimap() {
        this.keyToKeyList = (Map<K, KeyList<K, V>>)Maps.newHashMap();
    }
    
    private LinkedListMultimap(final int initialCapacity) {
        this.keyToKeyList = new HashMap<K, KeyList<K, V>>(initialCapacity);
    }
    
    private LinkedListMultimap(final Multimap<? extends K, ? extends V> multimap) {
        this(multimap.keySet().size());
        this.putAll(multimap);
    }
    
    private Node<K, V> addNode(@Nullable final K k, @Nullable final V v, @Nullable final Node<K, V> node) {
        final Node node2 = new Node(k, v);
        if (this.head != null) {
            if (node != null) {
                final KeyList list = this.keyToKeyList.get(k);
                ++list.count;
                node2.previous = (Node<K, V>)node.previous;
                node2.previousSibling = (Node<K, V>)node.previousSibling;
                node2.next = (Node<K, V>)node;
                node2.nextSibling = (Node<K, V>)node;
                if (node.previousSibling != null) {
                    node.previousSibling.nextSibling = (Node<K, V>)node2;
                }
                else {
                    this.keyToKeyList.get(k).head = (Node<K, V>)node2;
                }
                if (node.previous != null) {
                    node.previous.next = (Node<K, V>)node2;
                }
                else {
                    this.head = (Node<K, V>)node2;
                }
                node.previous = (Node<K, V>)node2;
                node.previousSibling = (Node<K, V>)node2;
            }
            else {
                this.tail.next = (Node<K, V>)node2;
                node2.previous = (Node<K, V>)this.tail;
                this.tail = (Node<K, V>)node2;
                final KeyList list2 = this.keyToKeyList.get(k);
                if (list2 != null) {
                    ++list2.count;
                    final Node<K, V> tail = list2.tail;
                    tail.nextSibling = (Node<K, V>)node2;
                    node2.previousSibling = (Node<K, V>)tail;
                    list2.tail = (Node<K, V>)node2;
                }
                else {
                    this.keyToKeyList.put(k, new KeyList<K, V>((Node<K, V>)node2));
                    ++this.modCount;
                }
            }
        }
        else {
            this.tail = (Node<K, V>)node2;
            this.head = (Node<K, V>)node2;
            this.keyToKeyList.put(k, new KeyList<K, V>((Node<K, V>)node2));
            ++this.modCount;
        }
        ++this.size;
        return (Node<K, V>)node2;
    }
    
    private static void checkElement(@Nullable final Object o) {
        if (o != null) {
            return;
        }
        throw new NoSuchElementException();
    }
    
    public static <K, V> LinkedListMultimap<K, V> create() {
        return new LinkedListMultimap<K, V>();
    }
    
    public static <K, V> LinkedListMultimap<K, V> create(final int n) {
        return new LinkedListMultimap<K, V>(n);
    }
    
    public static <K, V> LinkedListMultimap<K, V> create(final Multimap<? extends K, ? extends V> multimap) {
        return new LinkedListMultimap<K, V>(multimap);
    }
    
    private List<V> getCopy(@Nullable final Object o) {
        return Collections.unmodifiableList((List<? extends V>)Lists.newArrayList((Iterator<?>)new ValueForKeyIterator(o)));
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.keyToKeyList = (Map<K, KeyList<K, V>>)Maps.newLinkedHashMap();
        for (int int1 = objectInputStream.readInt(), i = 0; i < int1; ++i) {
            this.put(objectInputStream.readObject(), objectInputStream.readObject());
        }
    }
    
    private void removeAllNodes(@Nullable final Object o) {
        Iterators.clear(new ValueForKeyIterator(o));
    }
    
    private void removeNode(final Node<K, V> node) {
        if (node.previous == null) {
            this.head = node.next;
        }
        else {
            node.previous.next = node.next;
        }
        if (node.next == null) {
            this.tail = node.previous;
        }
        else {
            node.next.previous = node.previous;
        }
        if (node.previousSibling == null && node.nextSibling == null) {
            this.keyToKeyList.remove(node.key).count = 0;
            ++this.modCount;
        }
        else {
            final KeyList list = this.keyToKeyList.get(node.key);
            --list.count;
            if (node.previousSibling != null) {
                node.previousSibling.nextSibling = node.nextSibling;
            }
            else {
                list.head = (Node<K, V>)node.nextSibling;
            }
            if (node.nextSibling != null) {
                node.nextSibling.previousSibling = node.previousSibling;
            }
            else {
                list.tail = (Node<K, V>)node.previousSibling;
            }
        }
        --this.size;
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.size());
        for (final Map.Entry<Object, V> entry : this.entries()) {
            objectOutputStream.writeObject(entry.getKey());
            objectOutputStream.writeObject(entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        this.head = null;
        this.tail = null;
        this.keyToKeyList.clear();
        this.size = 0;
        ++this.modCount;
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.keyToKeyList.containsKey(o);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return this.values().contains(o);
    }
    
    @Override
    Map<K, Collection<V>> createAsMap() {
        return (Map<K, Collection<V>>)new Multimaps.AsMap((Multimap<Object, Object>)this);
    }
    
    @Override
    List<Map.Entry<K, V>> createEntries() {
        return new EntriesImpl();
    }
    
    @Override
    Set<K> createKeySet() {
        return new KeySetImpl();
    }
    
    @Override
    List<V> createValues() {
        return new ValuesImpl();
    }
    
    @Override
    public List<Map.Entry<K, V>> entries() {
        return (List)super.entries();
    }
    
    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    public List<V> get(@Nullable final K k) {
        return new AbstractSequentialList<V>() {
            @Override
            public ListIterator<V> listIterator(final int n) {
                return new ValueForKeyIterator(k, n);
            }
            
            @Override
            public int size() {
                final KeyList list = (KeyList)LinkedListMultimap.this.keyToKeyList.get(k);
                int count;
                if (list != null) {
                    count = list.count;
                }
                else {
                    count = 0;
                }
                return count;
            }
        };
    }
    
    @Override
    public boolean isEmpty() {
        return this.head == null;
    }
    
    @Override
    public boolean put(@Nullable final K k, @Nullable final V v) {
        this.addNode(k, v, null);
        return true;
    }
    
    @Override
    public List<V> removeAll(@Nullable final Object o) {
        final List<V> copy = this.getCopy(o);
        this.removeAllNodes(o);
        return copy;
    }
    
    @Override
    public List<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        final List<V> copy = this.getCopy(k);
        final ValueForKeyIterator valueForKeyIterator = new ValueForKeyIterator(k);
        final Iterator<? extends V> iterator = iterable.iterator();
        while (valueForKeyIterator.hasNext() && iterator.hasNext()) {
            valueForKeyIterator.next();
            valueForKeyIterator.set(iterator.next());
        }
        while (valueForKeyIterator.hasNext()) {
            valueForKeyIterator.next();
            valueForKeyIterator.remove();
        }
        while (iterator.hasNext()) {
            valueForKeyIterator.add(iterator.next());
        }
        return copy;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public List<V> values() {
        return (List)super.values();
    }
    
    class EntriesImpl extends AbstractSequentialList<Map.Entry<K, V>>
    {
        @Override
        public ListIterator<Map.Entry<K, V>> listIterator(final int n) {
            return new NodeIterator(n);
        }
        
        @Override
        public int size() {
            return LinkedListMultimap.this.size;
        }
    }
    
    class KeySetImpl extends ImprovedAbstractSet<K>
    {
        @Override
        public boolean contains(final Object o) {
            return LinkedListMultimap.this.containsKey(o);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new DistinctKeyIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            boolean b = false;
            if (!LinkedListMultimap.this.removeAll(o).isEmpty()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int size() {
            return LinkedListMultimap.this.keyToKeyList.size();
        }
    }
    
    class ValuesImpl extends AbstractSequentialList<V>
    {
        @Override
        public ListIterator<V> listIterator(final int n) {
            final NodeIterator nodeIterator = new NodeIterator(n);
            return new TransformedListIterator<Map.Entry<K, V>, V>(nodeIterator) {
                @Override
                public void set(final V value) {
                    nodeIterator.setValue(value);
                }
                
                @Override
                V transform(final Map.Entry<K, V> entry) {
                    return entry.getValue();
                }
            };
        }
        
        @Override
        public int size() {
            return LinkedListMultimap.this.size;
        }
    }
    
    private class DistinctKeyIterator implements Iterator<K>
    {
        Node<K, V> current;
        int expectedModCount;
        Node<K, V> next;
        final Set<K> seenKeys;
        
        private DistinctKeyIterator() {
            this.seenKeys = (Set<K>)Sets.newHashSetWithExpectedSize(LinkedListMultimap.this.keySet().size());
            this.next = LinkedListMultimap.this.head;
            this.expectedModCount = LinkedListMultimap.this.modCount;
        }
        
        private void checkForConcurrentModification() {
            if (LinkedListMultimap.this.modCount == this.expectedModCount) {
                return;
            }
            throw new ConcurrentModificationException();
        }
        
        @Override
        public boolean hasNext() {
            this.checkForConcurrentModification();
            return this.next != null;
        }
        
        @Override
        public K next() {
            this.checkForConcurrentModification();
            checkElement(this.next);
            this.current = this.next;
            this.seenKeys.add(this.current.key);
            do {
                this.next = this.next.next;
            } while (this.next != null && !this.seenKeys.add(this.next.key));
            return this.current.key;
        }
        
        @Override
        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.current != null);
            LinkedListMultimap.this.removeAllNodes(this.current.key);
            this.current = null;
            this.expectedModCount = LinkedListMultimap.this.modCount;
        }
    }
    
    private static class KeyList<K, V>
    {
        int count;
        Node<K, V> head;
        Node<K, V> tail;
        
        KeyList(final Node<K, V> node) {
            this.head = node;
            this.tail = node;
            node.previousSibling = null;
            node.nextSibling = null;
            this.count = 1;
        }
    }
    
    private static final class Node<K, V> extends AbstractMapEntry<K, V>
    {
        final K key;
        Node<K, V> next;
        Node<K, V> nextSibling;
        Node<K, V> previous;
        Node<K, V> previousSibling;
        V value;
        
        Node(@Nullable final K key, @Nullable final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(@Nullable final V value) {
            final V value2 = this.value;
            this.value = value;
            return value2;
        }
    }
    
    private class NodeIterator implements ListIterator<Map.Entry<K, V>>
    {
        Node<K, V> current;
        int expectedModCount;
        Node<K, V> next;
        int nextIndex;
        Node<K, V> previous;
        
        NodeIterator(int i) {
            this.expectedModCount = LinkedListMultimap.this.modCount;
            final int size = LinkedListMultimap.this.size();
            Preconditions.checkPositionIndex(i, size);
            if (i < size / 2) {
                this.next = LinkedListMultimap.this.head;
                while (i > 0) {
                    this.next();
                    --i;
                }
            }
            else {
                this.previous = LinkedListMultimap.this.tail;
                this.nextIndex = size;
                while (i < size) {
                    this.previous();
                    ++i;
                }
            }
            this.current = null;
        }
        
        private void checkForConcurrentModification() {
            if (LinkedListMultimap.this.modCount == this.expectedModCount) {
                return;
            }
            throw new ConcurrentModificationException();
        }
        
        @Override
        public void add(final Map.Entry<K, V> entry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasNext() {
            this.checkForConcurrentModification();
            return this.next != null;
        }
        
        @Override
        public boolean hasPrevious() {
            this.checkForConcurrentModification();
            return this.previous != null;
        }
        
        @Override
        public Node<K, V> next() {
            this.checkForConcurrentModification();
            checkElement(this.next);
            final Node<K, V> next = this.next;
            this.current = next;
            this.previous = next;
            this.next = this.next.next;
            ++this.nextIndex;
            return this.current;
        }
        
        @Override
        public int nextIndex() {
            return this.nextIndex;
        }
        
        @Override
        public Node<K, V> previous() {
            this.checkForConcurrentModification();
            checkElement(this.previous);
            final Node<K, V> previous = this.previous;
            this.current = previous;
            this.next = previous;
            this.previous = this.previous.previous;
            --this.nextIndex;
            return this.current;
        }
        
        @Override
        public int previousIndex() {
            return this.nextIndex - 1;
        }
        
        @Override
        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.current != null);
            if (this.current == this.next) {
                this.next = this.current.next;
            }
            else {
                this.previous = this.current.previous;
                --this.nextIndex;
            }
            LinkedListMultimap.this.removeNode((Node)this.current);
            this.current = null;
            this.expectedModCount = LinkedListMultimap.this.modCount;
        }
        
        @Override
        public void set(final Map.Entry<K, V> entry) {
            throw new UnsupportedOperationException();
        }
        
        void setValue(final V value) {
            Preconditions.checkState(this.current != null);
            this.current.value = value;
        }
    }
    
    private class ValueForKeyIterator implements ListIterator<V>
    {
        Node<K, V> current;
        final Object key;
        Node<K, V> next;
        int nextIndex;
        Node<K, V> previous;
        
        ValueForKeyIterator(@Nullable final Object key) {
            this.key = key;
            final KeyList list = (KeyList)LinkedListMultimap.this.keyToKeyList.get(key);
            Node<K, V> head;
            if (list != null) {
                head = list.head;
            }
            else {
                head = null;
            }
            this.next = (Node<K, V>)head;
        }
        
        public ValueForKeyIterator(@Nullable final Object key, int i) {
            int count = 0;
            final KeyList list = (KeyList)LinkedListMultimap.this.keyToKeyList.get(key);
            if (list != null) {
                count = list.count;
            }
            Preconditions.checkPositionIndex(i, count);
            if (i < count / 2) {
                Node<K, V> head;
                if (list != null) {
                    head = list.head;
                }
                else {
                    head = null;
                }
                this.next = (Node<K, V>)head;
                while (i > 0) {
                    this.next();
                    --i;
                }
            }
            else {
                Node<K, V> tail;
                if (list != null) {
                    tail = list.tail;
                }
                else {
                    tail = null;
                }
                this.previous = (Node<K, V>)tail;
                this.nextIndex = count;
                while (i < count) {
                    this.previous();
                    ++i;
                }
            }
            this.key = key;
            this.current = null;
        }
        
        @Override
        public void add(final V v) {
            this.previous = (Node<K, V>)LinkedListMultimap.this.addNode(this.key, v, (Node)this.next);
            ++this.nextIndex;
            this.current = null;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.previous != null;
        }
        
        @Override
        public V next() {
            checkElement(this.next);
            final Node<K, V> next = this.next;
            this.current = next;
            this.previous = next;
            this.next = this.next.nextSibling;
            ++this.nextIndex;
            return this.current.value;
        }
        
        @Override
        public int nextIndex() {
            return this.nextIndex;
        }
        
        @Override
        public V previous() {
            checkElement(this.previous);
            final Node<K, V> previous = this.previous;
            this.current = previous;
            this.next = previous;
            this.previous = this.previous.previousSibling;
            --this.nextIndex;
            return this.current.value;
        }
        
        @Override
        public int previousIndex() {
            return this.nextIndex - 1;
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(this.current != null);
            if (this.current == this.next) {
                this.next = this.current.nextSibling;
            }
            else {
                this.previous = this.current.previousSibling;
                --this.nextIndex;
            }
            LinkedListMultimap.this.removeNode((Node)this.current);
            this.current = null;
        }
        
        @Override
        public void set(final V value) {
            Preconditions.checkState(this.current != null);
            this.current.value = value;
        }
    }
}
