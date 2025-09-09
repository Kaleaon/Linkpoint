package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.utils.InlineListEntry;

public interface InlineListEntry<T extends InlineListEntry<T>> {
    InlineList<T> getList();

    T getNext();

    T getPrev();

    void requestEntryRemoval();

    void setList(InlineList<T> inlineList);

    void setNext(T t);

    void setPrev(T t);
}
