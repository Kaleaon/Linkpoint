package com.lumiyaviewer.lumiya.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedTreeNode<T> implements Iterable<T> {
    /* access modifiers changed from: private */
    public T dataObject;
    /* access modifiers changed from: private */
    public LinkedTreeNode<T> firstChild;
    /* access modifiers changed from: private */
    public LinkedTreeNode<T> nextChild;
    private LinkedTreeNode<T> parentObject;
    private LinkedTreeNode<T> prevChild;

    public static class LinkedTreeIterator<T> implements Iterator<T> {
        private boolean isFirst = true;
        private LinkedTreeNode<T> node;

        public LinkedTreeIterator(LinkedTreeNode<T> linkedTreeNode) {
            this.node = linkedTreeNode;
        }

        public boolean hasNext() {
            return this.isFirst ? this.node.firstChild != null : this.node.nextChild != null;
        }

        public T next() {
            if (this.node == null) {
                throw new NoSuchElementException();
            }
            if (this.isFirst) {
                this.node = this.node.firstChild;
                this.isFirst = false;
            } else {
                this.node = this.node.nextChild;
            }
            if (this.node != null) {
                return this.node.dataObject;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported by LinkedTreeNode");
        }
    }

    public LinkedTreeNode(T t) {
        this.dataObject = t;
    }

    public synchronized void addChild(LinkedTreeNode<T> linkedTreeNode) {
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

    public T getDataObject() {
        return this.dataObject;
    }

    public synchronized LinkedTreeNode<T> getFirstChild() {
        return this.firstChild;
    }

    public synchronized LinkedTreeNode<T> getNextChild() {
        return this.nextChild;
    }

    public synchronized T getParent() {
        if (this.parentObject == null) {
            return null;
        }
        return this.parentObject.getDataObject();
    }

    public synchronized boolean hasChild(LinkedTreeNode<?> linkedTreeNode) {
        return linkedTreeNode.parentObject == this;
    }

    public synchronized boolean hasChildren() {
        return this.firstChild != null;
    }

    public Iterator<T> iterator() {
        return new LinkedTreeIterator(this);
    }

    public synchronized void removeChild(LinkedTreeNode<T> linkedTreeNode) {
        if (linkedTreeNode.parentObject == this) {
            linkedTreeNode.unlinkFromParent();
        }
    }

    public synchronized void unlinkFromParent() {
        if (this.parentObject != null) {
            if (this.prevChild != null) {
                this.prevChild.nextChild = this.nextChild;
            } else {
                this.parentObject.firstChild = this.nextChild;
            }
            if (this.nextChild != null) {
                this.nextChild.prevChild = this.prevChild;
            }
            this.parentObject = null;
        }
    }
}
