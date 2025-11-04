// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

public class InlineList<T extends InlineListEntry<T>>
{
    private T first;
    
    public InlineList() {
        this.first = null;
    }
    
    public void addEntry(final T t) {
        final InlineList<T> list = t.getList();
        if (list != this) {
            if (list != null) {
                list.removeEntry((InlineListEntry<InlineListEntry<InlineListEntry<T>>>)t);
            }
            t.setNext(this.first);
            t.setPrev(null);
            if (this.first != null) {
                this.first.setPrev(t);
            }
            (this.first = t).setList(this);
        }
    }
    
    public final T getFirst() {
        return this.first;
    }
    
    public void removeEntry(final T t) {
        if (t.getList() == this) {
            final InlineListEntry<T> next = ((InlineListEntry<InlineListEntry<T>>)t).getNext();
            final InlineListEntry<T> prev = ((InlineListEntry<InlineListEntry<T>>)t).getPrev();
            if (prev != null) {
                prev.setNext((T)next);
            }
            else {
                this.first = (T)next;
            }
            if (next != null) {
                next.setPrev((T)prev);
            }
            t.setPrev(null);
            t.setNext(null);
            t.setList(null);
        }
    }
    
    public void requestEntryRemoval(final T t) {
    }
}
