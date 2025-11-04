// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class LinkedTreeNode<T> implements Iterable<T>
{
    private T dataObject;
    private LinkedTreeNode<T> firstChild;
    private LinkedTreeNode<T> nextChild;
    private LinkedTreeNode<T> parentObject;
    private LinkedTreeNode<T> prevChild;
    
    public LinkedTreeNode(final T dataObject) {
        this.dataObject = dataObject;
    }
    
    public void addChild(final LinkedTreeNode<T> linkedTreeNode) {
        synchronized (this) {
            if (linkedTreeNode.parentObject != this) {
                linkedTreeNode.unlinkFromParent();
                linkedTreeNode.parentObject = this;
                linkedTreeNode.prevChild = null;
                linkedTreeNode.nextChild = this.firstChild;
                this.firstChild = linkedTreeNode;
                if (linkedTreeNode.nextChild != null) {
                    linkedTreeNode.nextChild.prevChild = linkedTreeNode;
                }
            }
        }
    }
    
    public T getDataObject() {
        return this.dataObject;
    }
    
    public LinkedTreeNode<T> getFirstChild() {
        synchronized (this) {
            return this.firstChild;
        }
    }
    
    public LinkedTreeNode<T> getNextChild() {
        synchronized (this) {
            return this.nextChild;
        }
    }
    
    public T getParent() {
        synchronized (this) {
            if (this.parentObject != null) {
                return this.parentObject.getDataObject();
            }
            return null;
        }
    }
    
    public boolean hasChild(final LinkedTreeNode<?> linkedTreeNode) {
        synchronized (this) {
            return linkedTreeNode.parentObject == this;
        }
    }
    
    public boolean hasChildren() {
        synchronized (this) {
            return this.firstChild != null;
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        return new LinkedTreeIterator<T>(this);
    }
    
    public void removeChild(final LinkedTreeNode<T> linkedTreeNode) {
        synchronized (this) {
            if (linkedTreeNode.parentObject == this) {
                linkedTreeNode.unlinkFromParent();
            }
        }
    }
    
    public void unlinkFromParent() {
        synchronized (this) {
            if (this.parentObject != null) {
                if (this.prevChild != null) {
                    this.prevChild.nextChild = this.nextChild;
                }
                else {
                    this.parentObject.firstChild = this.nextChild;
                }
                if (this.nextChild != null) {
                    this.nextChild.prevChild = this.prevChild;
                }
                this.parentObject = null;
            }
        }
    }
    
    public static class LinkedTreeIterator<T> implements Iterator<T>
    {
        private boolean isFirst;
        private LinkedTreeNode<T> node;
        
        public LinkedTreeIterator(final LinkedTreeNode<T> node) {
            this.node = node;
            this.isFirst = true;
        }
        
        @Override
        public boolean hasNext() {
            final boolean b = true;
            boolean b2 = true;
            if (this.isFirst) {
                if (((LinkedTreeNode<Object>)this.node).firstChild == null) {
                    b2 = false;
                }
                return b2;
            }
            return ((LinkedTreeNode<Object>)this.node).nextChild != null && b;
        }
        
        @Override
        public T next() {
            if (this.node == null) {
                throw new NoSuchElementException();
            }
            if (this.isFirst) {
                this.node = (LinkedTreeNode<T>)((LinkedTreeNode<Object>)this.node).firstChild;
                this.isFirst = false;
            }
            else {
                this.node = (LinkedTreeNode<T>)((LinkedTreeNode<Object>)this.node).nextChild;
            }
            if (this.node == null) {
                throw new NoSuchElementException();
            }
            return (T)((LinkedTreeNode<Object>)this.node).dataObject;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() not supported by LinkedTreeNode");
        }
    }
}
