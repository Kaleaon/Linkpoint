// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;

public abstract class DrawListEntry implements InlineListEntry<DrawListEntry>
{
    @Nonnull
    final float[] boundingBox;
    private volatile InlineList<DrawListEntry> list;
    private DrawListEntry next;
    private DrawListEntry prev;
    
    public DrawListEntry() {
        this.boundingBox = new float[6];
    }
    
    public abstract void addToDrawList(@Nonnull final DrawList p0);
    
    @Override
    public InlineList<DrawListEntry> getList() {
        return this.list;
    }
    
    @Override
    public DrawListEntry getNext() {
        return this.next;
    }
    
    @Override
    public DrawListEntry getPrev() {
        return this.prev;
    }
    
    @Override
    public void requestEntryRemoval() {
        final InlineList<DrawListEntry> list = this.list;
        if (list != null) {
            list.requestEntryRemoval(this);
        }
    }
    
    @Override
    public void setList(final InlineList<DrawListEntry> list) {
        this.list = list;
    }
    
    @Override
    public void setNext(final DrawListEntry next) {
        this.next = next;
    }
    
    @Override
    public void setPrev(final DrawListEntry prev) {
        this.prev = prev;
    }
}
