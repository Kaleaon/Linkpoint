package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;
import javax.annotation.Nonnull;

public abstract class DrawListEntry implements InlineListEntry<DrawListEntry> {
    @Nonnull
    final float[] boundingBox = new float[6];
    private volatile InlineList<DrawListEntry> list;
    private DrawListEntry next;
    private DrawListEntry prev;

    public abstract void addToDrawList(@Nonnull DrawList drawList);

    public InlineList<DrawListEntry> getList() {
        return this.list;
    }

    public DrawListEntry getNext() {
        return this.next;
    }

    public DrawListEntry getPrev() {
        return this.prev;
    }

    public void requestEntryRemoval() {
        InlineList inlineList = this.list;
        if (inlineList != null) {
            inlineList.requestEntryRemoval(this);
        }
    }

    public void setList(InlineList<DrawListEntry> inlineList) {
        this.list = inlineList;
    }

    public void setNext(DrawListEntry drawListEntry) {
        this.next = drawListEntry;
    }

    public void setPrev(DrawListEntry drawListEntry) {
        this.prev = drawListEntry;
    }
}
