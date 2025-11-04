package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public abstract class DrawListEntry implements InlineListEntry<DrawListEntry> {

    @Nonnull
    final float[] boundingBox = new float[6];
    private volatile InlineList<DrawListEntry> list;
    private DrawListEntry next;
    private DrawListEntry prev;

    public abstract void addToDrawList(@Nonnull DrawList drawList);

    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public InlineList<DrawListEntry> getList() {
        return this.list;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public DrawListEntry getNext() {
        return this.next;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public DrawListEntry getPrev() {
        return this.prev;
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public void requestEntryRemoval() {
        InlineList<DrawListEntry> inlineList = this.list;
        if (inlineList != null) {
            inlineList.requestEntryRemoval(this);
        }
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public void setList(InlineList<DrawListEntry> inlineList) {
        this.list = inlineList;
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public void setNext(DrawListEntry drawListEntry) {
        this.next = drawListEntry;
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineListEntry
    public void setPrev(DrawListEntry drawListEntry) {
        this.prev = drawListEntry;
    }
}
