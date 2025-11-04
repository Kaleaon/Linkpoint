// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

public interface InlineListEntry<T extends InlineListEntry<T>>
{
    InlineList<T> getList();
    
    T getNext();
    
    T getPrev();
    
    void requestEntryRemoval();
    
    void setList(final InlineList<T> p0);
    
    void setNext(final T p0);
    
    void setPrev(final T p0);
}
