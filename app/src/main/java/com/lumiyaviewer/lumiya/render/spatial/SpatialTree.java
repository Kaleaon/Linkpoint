package com.lumiyaviewer.lumiya.render.spatial;
import java.util.*;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.InlineList;

class SpatialTree {
    static final int INVALID_BIN = -1;
    private final SpatialTreeNode[] bins;
    private final float[] depthBuf = new float[1];
    private float drawDistance = 1.0f;
    private boolean drawListChanged = false;
    private final MyAvatarTreeNode myAvatarTreeNode;
    private final int numBins;
    private final SpatialTreeNode rootNode;
    final SpatialObjectIndex spatialObjectIndex;
    private boolean treeWalkNeeded = false;

    SpatialTree(int i, float f, float f2, float f3, SpatialObjectIndex spatialObjectIndex) {
        this.numBins = i;
        this.bins = new SpatialTreeNode[i];
        this.spatialObjectIndex = spatialObjectIndex;
        this.rootNode = new SpatialTreeNode(this, f, f2, f3);
        this.myAvatarTreeNode = new MyAvatarTreeNode(this);
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

    void addDrawables(DrawList drawList) {
        Debug.Printf("SpatialTree: adding drawables.", new Object[0]);
        this.myAvatarTreeNode.addDrawables(drawList);
        for (SpatialTreeNode spatialTreeNode : this.bins) {
            for (SpatialTreeNode spatialTreeNode2 = r3[r1]; spatialTreeNode2 != null; spatialTreeNode2 = spatialTreeNode2.nextDepth) {
                spatialTreeNode2.addDrawables(drawList);
            }
        }
        this.drawListChanged = false;
    }

    final boolean isDrawListChanged() {
        return this.drawListChanged;
    }

    final boolean isTreeWalkNeeded() {
        return this.treeWalkNeeded;
    }

    final void removeEntry(SpatialTreeNode spatialTreeNode) {
        setEntryBin(spatialTreeNode, -1);
    }

    void removeObject(DrawListEntry drawListEntry) {
        InlineList list = drawListEntry.getList();
        if (list != null) {
            list.removeEntry(drawListEntry);
        }
    }

    final void setDrawListChanged() {
        this.drawListChanged = true;
    }

    final void setEntryDepth(SpatialTreeNode spatialTreeNode, float f) {
        int i = 0;
        int round = Math.round((((float) this.numBins) * f) / this.drawDistance);
        if (round >= 0) {
            i = round >= this.numBins ? this.numBins - 1 : round;
        }
        setEntryBin(spatialTreeNode, i);
    }

    final void setTreeWalkNeeded() {
        this.treeWalkNeeded = true;
    }

    void updateObject(DrawListEntry drawListEntry) {
        InlineList nodeForObject = getNodeForObject(drawListEntry);
        InlineList list = drawListEntry.getList();
        if (!(nodeForObject == list || list == null)) {
            list.removeEntry(drawListEntry);
        }
        if (nodeForObject != null) {
            nodeForObject.addEntry(drawListEntry);
        }
    }

    void walkTree(FrustrumPlanes frustrumPlanes, float f) {
        Debug.Printf("SpatialTree: walkTree: starting to walk.", new Object[0]);
        this.drawDistance = f;
        this.rootNode.walkTree(frustrumPlanes, 1, this.depthBuf);
        this.treeWalkNeeded = false;
    }
}
