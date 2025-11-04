package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.utils.InlineListEntry;

/* loaded from: classes.dex */
public class InlineList<T extends InlineListEntry<T>> {
    private T first = null;

    public void addEntry(T t) {
        InlineList<T> list = t.getList();
        if (list != this) {
            if (list != null) {
                list.removeEntry(t);
            }
            t.setNext(this.first);
            t.setPrev(null);
            if (this.first != null) {
                this.first.setPrev(t);
            }
            this.first = t;
            t.setList(this);
        }
    }

    public final T getFirst() {
        return this.first;
    }

    public void removeEntry(T t) {
        if (t.getList() == this) {
            T t2 = (T) t.getNext();
            InlineListEntry prev = t.getPrev();
            if (prev != null) {
                prev.setNext(t2);
            } else {
                this.first = t2;
            }
            if (t2 != null) {
                t2.setPrev(prev);
            }
            t.setPrev(null);
            t.setNext(null);
            t.setList(null);
        }
    }

    public void requestEntryRemoval(T t) {
    }
}
