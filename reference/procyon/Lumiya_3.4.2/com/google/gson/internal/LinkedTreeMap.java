// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Set;
import java.io.ObjectStreamException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.io.Serializable;
import java.util.AbstractMap;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable
{
    private static final Comparator<Comparable> NATURAL_ORDER;
    Comparator<? super K> comparator;
    private EntrySet entrySet;
    final Node<K, V> header;
    private KeySet keySet;
    int modCount;
    Node<K, V> root;
    int size;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!LinkedTreeMap.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
        NATURAL_ORDER = new Comparator<Comparable>() {
            @Override
            public int compare(final Comparable comparable, final Comparable comparable2) {
                return comparable.compareTo(comparable2);
            }
        };
    }
    
    public LinkedTreeMap() {
        this((Comparator)LinkedTreeMap.NATURAL_ORDER);
    }
    
    public LinkedTreeMap(final Comparator<? super K> comparator) {
        this.size = 0;
        this.modCount = 0;
        this.header = new Node<K, V>();
        Object natural_ORDER = comparator;
        if (comparator == null) {
            natural_ORDER = LinkedTreeMap.NATURAL_ORDER;
        }
        this.comparator = (Comparator<? super K>)natural_ORDER;
    }
    
    private boolean equal(final Object o, final Object obj) {
        final boolean b = false;
        if (o != obj) {
            boolean b2 = b;
            if (o == null) {
                return b2;
            }
            if (!o.equals(obj)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    private void rebalance(Node<K, V> parent, final boolean b) {
        while (parent != null) {
            final Node<K, V> left = parent.left;
            final Node<K, V> right = parent.right;
            int height;
            if (left == null) {
                height = 0;
            }
            else {
                height = left.height;
            }
            int height2;
            if (right == null) {
                height2 = 0;
            }
            else {
                height2 = right.height;
            }
            final int n = height - height2;
            if (n != -2) {
                if (n != 2) {
                    if (n != 0) {
                        assert n == 1;
                        parent.height = Math.max(height, height2) + 1;
                        if (!b) {
                            break;
                        }
                    }
                    else {
                        parent.height = height + 1;
                        if (b) {
                            break;
                        }
                    }
                }
                else {
                    final Node<K, V> left2 = left.left;
                    final Node<K, V> right2 = left.right;
                    int height3;
                    if (right2 == null) {
                        height3 = 0;
                    }
                    else {
                        height3 = right2.height;
                    }
                    int height4;
                    if (left2 == null) {
                        height4 = 0;
                    }
                    else {
                        height4 = left2.height;
                    }
                    final int n2 = height4 - height3;
                    if (n2 != 1 && (n2 != 0 || b)) {
                        assert n2 == -1;
                        this.rotateLeft(left);
                        this.rotateRight(parent);
                    }
                    else {
                        this.rotateRight(parent);
                    }
                    if (b) {
                        break;
                    }
                }
            }
            else {
                final Node<K, V> left3 = right.left;
                final Node<K, V> right3 = right.right;
                int height5;
                if (right3 == null) {
                    height5 = 0;
                }
                else {
                    height5 = right3.height;
                }
                int height6;
                if (left3 == null) {
                    height6 = 0;
                }
                else {
                    height6 = left3.height;
                }
                final int n3 = height6 - height5;
                if (n3 != -1 && (n3 != 0 || b)) {
                    assert n3 == 1;
                    this.rotateRight(right);
                    this.rotateLeft(parent);
                }
                else {
                    this.rotateLeft(parent);
                }
                if (b) {
                    break;
                }
            }
            parent = parent.parent;
        }
    }
    
    private void replaceInParent(final Node<K, V> node, final Node<K, V> left) {
        final Node<K, V> parent = node.parent;
        node.parent = null;
        if (left != null) {
            left.parent = parent;
        }
        if (parent == null) {
            this.root = left;
        }
        else if (parent.left != node) {
            assert parent.right == node;
            parent.right = left;
        }
        else {
            parent.left = left;
        }
    }
    
    private void rotateLeft(final Node<K, V> node) {
        final int n = 0;
        final Node<K, V> left = node.left;
        final Node<K, V> right = node.right;
        final Node<K, V> left2 = right.left;
        final Node<K, V> right2 = right.right;
        node.right = left2;
        if (left2 != null) {
            left2.parent = node;
        }
        this.replaceInParent(node, right);
        right.left = node;
        node.parent = right;
        int height;
        if (left == null) {
            height = 0;
        }
        else {
            height = left.height;
        }
        int height2;
        if (left2 == null) {
            height2 = 0;
        }
        else {
            height2 = left2.height;
        }
        node.height = Math.max(height, height2) + 1;
        final int height3 = node.height;
        int height4;
        if (right2 == null) {
            height4 = n;
        }
        else {
            height4 = right2.height;
        }
        right.height = Math.max(height3, height4) + 1;
    }
    
    private void rotateRight(final Node<K, V> node) {
        final int n = 0;
        final Node<K, V> left = node.left;
        final Node<K, V> right = node.right;
        final Node<K, V> left2 = left.left;
        final Node<K, V> right2 = left.right;
        node.left = right2;
        if (right2 != null) {
            right2.parent = node;
        }
        this.replaceInParent(node, left);
        left.right = node;
        node.parent = left;
        int height;
        if (right == null) {
            height = 0;
        }
        else {
            height = right.height;
        }
        int height2;
        if (right2 == null) {
            height2 = 0;
        }
        else {
            height2 = right2.height;
        }
        node.height = Math.max(height, height2) + 1;
        final int height3 = node.height;
        int height4;
        if (left2 == null) {
            height4 = n;
        }
        else {
            height4 = left2.height;
        }
        left.height = Math.max(height3, height4) + 1;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new LinkedHashMap(this);
    }
    
    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
        ++this.modCount;
        final Node<K, V> header = this.header;
        header.prev = header;
        header.next = header;
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.findByObject(o) != null;
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        EntrySet entrySet;
        if ((entrySet = this.entrySet) == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    Node<K, V> find(final K k, final boolean b) {
        int n = 0;
        final Comparator<? super K> comparator = this.comparator;
        Node<K, V> root = this.root;
        if (root != null) {
            Comparable<K> comparable;
            if (comparator != LinkedTreeMap.NATURAL_ORDER) {
                comparable = null;
            }
            else {
                comparable = (Comparable)k;
            }
            while (true) {
                if (comparable == null) {
                    n = comparator.compare(k, root.key);
                }
                else {
                    n = comparable.compareTo((K)root.key);
                }
                if (n == 0) {
                    return root;
                }
                Node<K, V> node;
                if (n >= 0) {
                    node = root.right;
                }
                else {
                    node = root.left;
                }
                if (node == null) {
                    break;
                }
                root = node;
            }
        }
        if (b) {
            final Node<K, V> header = this.header;
            Node root2;
            if (root != null) {
                root2 = new Node<K, V>(root, k, header, header.prev);
                if (n >= 0) {
                    root.right = (Node<K, V>)root2;
                }
                else {
                    root.left = (Node<K, V>)root2;
                }
                this.rebalance(root, true);
            }
            else {
                if (comparator == LinkedTreeMap.NATURAL_ORDER && !(k instanceof Comparable)) {
                    throw new ClassCastException(k.getClass().getName() + " is not Comparable");
                }
                root2 = new Node<K, V>(root, k, header, header.prev);
                this.root = (Node<K, V>)root2;
            }
            ++this.size;
            ++this.modCount;
            return (Node<K, V>)root2;
        }
        return null;
    }
    
    Node<K, V> findByEntry(final Entry<?, ?> entry) {
        int n = 0;
        final Node<K, V> byObject = this.findByObject(entry.getKey());
        if (byObject != null && this.equal(byObject.value, entry.getValue())) {
            n = 1;
        }
        Node<K, V> node;
        if (n == 0) {
            node = null;
        }
        else {
            node = byObject;
        }
        return node;
    }
    
    Node<K, V> findByObject(final Object o) {
        final Node<K, V> node = null;
        Entry<K, V> find;
        if (o == null) {
            find = (Entry<K, V>)node;
        }
        else {
            try {
                find = (Entry<K, V>)this.find(o, false);
            }
            catch (final ClassCastException ex) {
                return null;
            }
        }
        return (Node<K, V>)find;
    }
    
    @Override
    public V get(Object value) {
        final Object o = null;
        final Node<K, V> byObject = this.findByObject(value);
        if (byObject == null) {
            value = o;
        }
        else {
            value = byObject.value;
        }
        return (V)value;
    }
    
    @Override
    public Set<K> keySet() {
        KeySet keySet;
        if ((keySet = this.keySet) == null) {
            keySet = new KeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @Override
    public V put(final K k, final V value) {
        if (k != null) {
            final Node<K, V> find = this.find(k, true);
            final V value2 = find.value;
            find.value = value;
            return value2;
        }
        throw new NullPointerException("key == null");
    }
    
    @Override
    public V remove(Object value) {
        final Object o = null;
        final Node<K, V> removeInternalByKey = this.removeInternalByKey(value);
        if (removeInternalByKey == null) {
            value = o;
        }
        else {
            value = removeInternalByKey.value;
        }
        return (V)value;
    }
    
    void removeInternal(final Node<K, V> node, final boolean b) {
        int height = 0;
        if (b) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        final Node<K, V> left = node.left;
        final Node<K, V> right = node.right;
        final Node<K, V> parent = node.parent;
        if (left != null && right != null) {
            Node<K, V> node2;
            if (left.height <= right.height) {
                node2 = right.first();
            }
            else {
                node2 = left.last();
            }
            this.removeInternal(node2, false);
            final Node<K, V> left2 = node.left;
            int height2;
            if (left2 == null) {
                height2 = 0;
            }
            else {
                height2 = left2.height;
                node2.left = left2;
                left2.parent = node2;
                node.left = null;
            }
            final Node<K, V> right2 = node.right;
            if (right2 != null) {
                height = right2.height;
                node2.right = right2;
                right2.parent = node2;
                node.right = null;
            }
            node2.height = Math.max(height2, height) + 1;
            this.replaceInParent(node, node2);
            return;
        }
        if (left == null) {
            if (right == null) {
                this.replaceInParent(node, null);
            }
            else {
                this.replaceInParent(node, right);
                node.right = null;
            }
        }
        else {
            this.replaceInParent(node, left);
            node.left = null;
        }
        this.rebalance(parent, false);
        --this.size;
        ++this.modCount;
    }
    
    Node<K, V> removeInternalByKey(final Object o) {
        final Node<K, V> byObject = this.findByObject(o);
        if (byObject != null) {
            this.removeInternal(byObject, true);
        }
        return byObject;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    class EntrySet extends AbstractSet<Entry<K, V>>
    {
        @Override
        public void clear() {
            LinkedTreeMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (o instanceof Entry && LinkedTreeMap.this.findByEntry((Entry<?, ?>)o) != null) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new LinkedTreeMapIterator<Entry<K, V>>() {
                @Override
                public Entry<K, V> next() {
                    return ((LinkedTreeMapIterator)this).nextNode();
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Node<K, V> byEntry = LinkedTreeMap.this.findByEntry((Entry<?, ?>)o);
            if (byEntry != null) {
                LinkedTreeMap.this.removeInternal(byEntry, true);
                return true;
            }
            return false;
        }
        
        @Override
        public int size() {
            return LinkedTreeMap.this.size;
        }
    }
    
    private abstract class LinkedTreeMapIterator<T> implements Iterator<T>
    {
        int expectedModCount;
        Node<K, V> lastReturned;
        Node<K, V> next;
        
        LinkedTreeMapIterator() {
            this.next = LinkedTreeMap.this.header.next;
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
        }
        
        @Override
        public final boolean hasNext() {
            return this.next != LinkedTreeMap.this.header;
        }
        
        final Node<K, V> nextNode() {
            final Node<K, V> next = this.next;
            if (next == LinkedTreeMap.this.header) {
                throw new NoSuchElementException();
            }
            if (LinkedTreeMap.this.modCount == this.expectedModCount) {
                this.next = next.next;
                return this.lastReturned = next;
            }
            throw new ConcurrentModificationException();
        }
        
        @Override
        public final void remove() {
            if (this.lastReturned != null) {
                LinkedTreeMap.this.removeInternal(this.lastReturned, true);
                this.lastReturned = null;
                this.expectedModCount = LinkedTreeMap.this.modCount;
                return;
            }
            throw new IllegalStateException();
        }
    }
    
    final class KeySet extends AbstractSet<K>
    {
        @Override
        public void clear() {
            LinkedTreeMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return LinkedTreeMap.this.containsKey(o);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new LinkedTreeMapIterator<K>() {
                @Override
                public K next() {
                    return ((LinkedTreeMapIterator)this).nextNode().key;
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            return LinkedTreeMap.this.removeInternalByKey(o) != null;
        }
        
        @Override
        public int size() {
            return LinkedTreeMap.this.size;
        }
    }
    
    static final class Node<K, V> implements Entry<K, V>
    {
        int height;
        final K key;
        Node<K, V> left;
        Node<K, V> next;
        Node<K, V> parent;
        Node<K, V> prev;
        Node<K, V> right;
        V value;
        
        Node() {
            this.key = null;
            this.prev = this;
            this.next = this;
        }
        
        Node(final Node<K, V> parent, final K key, final Node<K, V> next, final Node<K, V> prev) {
            this.parent = parent;
            this.key = key;
            this.height = 1;
            this.next = next;
            this.prev = prev;
            prev.next = this;
            next.prev = this;
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean b = false;
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            Label_0054: {
                boolean b2;
                if (this.key != null) {
                    if (this.key.equals(entry.getKey())) {
                        break Label_0054;
                    }
                    b2 = b;
                }
                else {
                    b2 = b;
                    if (entry.getKey() == null) {
                        break Label_0054;
                    }
                }
                return b2;
            }
            if (this.value != null) {
                final boolean b2 = b;
                if (!this.value.equals(entry.getValue())) {
                    return b2;
                }
            }
            else if (entry.getValue() != null) {
                return b;
            }
            return true;
        }
        
        public Node<K, V> first() {
            Node<K, V> left = this.left;
            Node node = this;
            while (left != null) {
                final Node<K, V> left2 = left.left;
                node = left;
                left = left2;
            }
            return node;
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
        public int hashCode() {
            int hashCode = 0;
            int hashCode2;
            if (this.key != null) {
                hashCode2 = this.key.hashCode();
            }
            else {
                hashCode2 = 0;
            }
            if (this.value != null) {
                hashCode = this.value.hashCode();
            }
            return hashCode2 ^ hashCode;
        }
        
        public Node<K, V> last() {
            Node<K, V> right = this.right;
            Node node = this;
            while (right != null) {
                final Node<K, V> right2 = right.right;
                node = right;
                right = right2;
            }
            return node;
        }
        
        @Override
        public V setValue(final V value) {
            final V value2 = this.value;
            this.value = value;
            return value2;
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
