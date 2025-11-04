// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineListEntry;
import com.lumiyaviewer.lumiya.utils.InlineList;

public class MyAvatarTreeNode extends InlineList<DrawListEntry>
{
    private final SpatialTree spatialTree;
    
    public MyAvatarTreeNode(final SpatialTree spatialTree) {
        this.spatialTree = spatialTree;
    }
    
    public void addDrawables(final DrawList list) {
        for (DrawListEntry next = this.getFirst(); next != null; next = next.getNext()) {
            next.addToDrawList(list);
        }
    }
    
    @Override
    public void addEntry(final DrawListEntry drawListEntry) {
        super.addEntry(drawListEntry);
        this.spatialTree.setDrawListChanged();
    }
    
    @Override
    public void removeEntry(final DrawListEntry drawListEntry) {
        super.removeEntry(drawListEntry);
        this.spatialTree.setDrawListChanged();
    }
    
    @Override
    public void requestEntryRemoval(final DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry);
    }
}
