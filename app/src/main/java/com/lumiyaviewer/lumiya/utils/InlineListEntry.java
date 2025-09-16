package com.lumiyaviewer.lumiya.utils;
import java.util.*;

public interface InlineListEntry<T extends InlineListEntry<T>> {
    InlineList<T> getList();

    T getNext();

    T getPrev();

    void requestEntryRemoval();

    void setList(InlineList<T> inlineList);

    void setNext(T t);

    void setPrev(T t);
}
