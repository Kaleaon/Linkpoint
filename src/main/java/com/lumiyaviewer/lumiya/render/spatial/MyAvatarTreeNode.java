package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;

public class MyAvatarTreeNode extends InlineList<DrawListEntry> {
    private final SpatialTree spatialTree;

    public MyAvatarTreeNode(SpatialTree spatialTree) {
        this.spatialTree = spatialTree;
    }

    public void addDrawables(DrawList drawList) {
        for (DrawListEntry drawListEntry = (DrawListEntry) getFirst(); drawListEntry != null; drawListEntry = drawListEntry.getNext()) {
            drawListEntry.addToDrawList(drawList);
        }
    }

    public void addEntry(DrawListEntry drawListEntry) {
        super.addEntry(drawListEntry);
        this.spatialTree.setDrawListChanged();
    }

    public void removeEntry(DrawListEntry drawListEntry) {
        super.removeEntry(drawListEntry);
        this.spatialTree.setDrawListChanged();
    }

    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry);
    }
}
