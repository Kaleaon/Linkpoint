package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;

/* loaded from: classes.dex */
public class MyAvatarTreeNode extends InlineList<DrawListEntry> {
    private final SpatialTree spatialTree;

    public MyAvatarTreeNode(SpatialTree spatialTree) {
        this.spatialTree = spatialTree;
    }

    public void addDrawables(DrawList drawList) {
        for (DrawListEntry first = getFirst(); first != null; first = first.getNext()) {
            first.addToDrawList(drawList);
        }
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineList
    public void addEntry(DrawListEntry drawListEntry) {
        super.addEntry((MyAvatarTreeNode) drawListEntry);
        this.spatialTree.setDrawListChanged();
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineList
    public void removeEntry(DrawListEntry drawListEntry) {
        super.removeEntry((MyAvatarTreeNode) drawListEntry);
        this.spatialTree.setDrawListChanged();
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineList
    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry);
    }
}
