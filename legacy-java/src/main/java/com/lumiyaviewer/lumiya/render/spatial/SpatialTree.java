package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.InlineList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SpatialTree {
    static final int INVALID_BIN = -1;
    private final SpatialTreeNode[] bins;
    private final int numBins;
    private final SpatialTreeNode rootNode;
    final SpatialObjectIndex spatialObjectIndex;
    private float drawDistance = 1.0f;
    private boolean treeWalkNeeded = false;
    private boolean drawListChanged = false;
    private final float[] depthBuf = new float[1];
    private final MyAvatarTreeNode myAvatarTreeNode = new MyAvatarTreeNode(this);

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpatialTree(int i, float f, float f2, float f3, SpatialObjectIndex spatialObjectIndex) {
        this.numBins = i;
        this.bins = new SpatialTreeNode[i];
        this.spatialObjectIndex = spatialObjectIndex;
        this.rootNode = new SpatialTreeNode(this, f, f2, f3);
    }

    private InlineList<DrawListEntry> getNodeForObject(DrawListEntry drawListEntry) {
        return ((drawListEntry instanceof DrawListAvatarEntry) && ((DrawListAvatarEntry) drawListEntry).getObjectAvatarInfo().isMyAvatar()) ? this.myAvatarTreeNode : this.rootNode.findNode(drawListEntry.boundingBox);
    }

    private void setEntryBin(SpatialTreeNode spatialTreeNode, int i) {
        if (i != spatialTreeNode.depthBin) {
            if (spatialTreeNode.depthBin != -1) {
                if (spatialTreeNode.prevDepth != null) {
                    spatialTreeNode.prevDepth.nextDepth = spatialTreeNode.nextDepth;
                } else {
                    this.bins[spatialTreeNode.depthBin] = spatialTreeNode.nextDepth;
                }
                if (spatialTreeNode.nextDepth != null) {
                    spatialTreeNode.nextDepth.prevDepth = spatialTreeNode.prevDepth;
                }
                spatialTreeNode.prevDepth = null;
                spatialTreeNode.nextDepth = null;
            }
            if (i != -1) {
                spatialTreeNode.nextDepth = this.bins[i];
                spatialTreeNode.prevDepth = null;
                if (spatialTreeNode.nextDepth != null) {
                    spatialTreeNode.nextDepth.prevDepth = spatialTreeNode;
                }
                this.bins[i] = spatialTreeNode;
            }
            spatialTreeNode.depthBin = i;
            if (spatialTreeNode.getFirst() != null) {
                setDrawListChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addDrawables(DrawList drawList) {
        Debug.Printf("SpatialTree: adding drawables.", new Object[0]);
        this.myAvatarTreeNode.addDrawables(drawList);
        for (SpatialTreeNode spatialTreeNode : this.bins) {
            for (; spatialTreeNode != null; spatialTreeNode = spatialTreeNode.nextDepth) {
                spatialTreeNode.addDrawables(drawList);
            }
        }
        this.drawListChanged = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isDrawListChanged() {
        return this.drawListChanged;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTreeWalkNeeded() {
        return this.treeWalkNeeded;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void removeEntry(SpatialTreeNode spatialTreeNode) {
        setEntryBin(spatialTreeNode, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeObject(DrawListEntry drawListEntry) {
        InlineList<DrawListEntry> list = drawListEntry.getList();
        if (list != null) {
            list.removeEntry(drawListEntry);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setDrawListChanged() {
        this.drawListChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setEntryDepth(SpatialTreeNode spatialTreeNode, float f) {
        int round = Math.round((this.numBins * f) / this.drawDistance);
        setEntryBin(spatialTreeNode, round >= 0 ? round >= this.numBins ? this.numBins - 1 : round : 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setTreeWalkNeeded() {
        this.treeWalkNeeded = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateObject(DrawListEntry drawListEntry) {
        InlineList<DrawListEntry> nodeForObject = getNodeForObject(drawListEntry);
        InlineList<DrawListEntry> list = drawListEntry.getList();
        if (nodeForObject != list && list != null) {
            list.removeEntry(drawListEntry);
        }
        if (nodeForObject != null) {
            nodeForObject.addEntry(drawListEntry);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void walkTree(FrustrumPlanes frustrumPlanes, float f) {
        Debug.Printf("SpatialTree: walkTree: starting to walk.", new Object[0]);
        this.drawDistance = f;
        this.rootNode.walkTree(frustrumPlanes, 1, this.depthBuf);
        this.treeWalkNeeded = false;
    }
}
