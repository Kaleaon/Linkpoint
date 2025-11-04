// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.InlineList;

class SpatialTree
{
    static final int INVALID_BIN = -1;
    private final SpatialTreeNode[] bins;
    private final float[] depthBuf;
    private float drawDistance;
    private boolean drawListChanged;
    private final MyAvatarTreeNode myAvatarTreeNode;
    private final int numBins;
    private final SpatialTreeNode rootNode;
    final SpatialObjectIndex spatialObjectIndex;
    private boolean treeWalkNeeded;
    
    SpatialTree(final int numBins, final float n, final float n2, final float n3, final SpatialObjectIndex spatialObjectIndex) {
        this.drawDistance = 1.0f;
        this.treeWalkNeeded = false;
        this.drawListChanged = false;
        this.depthBuf = new float[1];
        this.numBins = numBins;
        this.bins = new SpatialTreeNode[numBins];
        this.spatialObjectIndex = spatialObjectIndex;
        this.rootNode = new SpatialTreeNode(this, n, n2, n3);
        this.myAvatarTreeNode = new MyAvatarTreeNode(this);
    }
    
    private InlineList<DrawListEntry> getNodeForObject(final DrawListEntry drawListEntry) {
        if (drawListEntry instanceof DrawListAvatarEntry && ((DrawListAvatarEntry)drawListEntry).getObjectAvatarInfo().isMyAvatar()) {
            return this.myAvatarTreeNode;
        }
        return this.rootNode.findNode(drawListEntry.boundingBox);
    }
    
    private void setEntryBin(final SpatialTreeNode prevDepth, final int depthBin) {
        if (depthBin != prevDepth.depthBin) {
            if (prevDepth.depthBin != -1) {
                if (prevDepth.prevDepth != null) {
                    prevDepth.prevDepth.nextDepth = prevDepth.nextDepth;
                }
                else {
                    this.bins[prevDepth.depthBin] = prevDepth.nextDepth;
                }
                if (prevDepth.nextDepth != null) {
                    prevDepth.nextDepth.prevDepth = prevDepth.prevDepth;
                }
                prevDepth.prevDepth = null;
                prevDepth.nextDepth = null;
            }
            if (depthBin != -1) {
                prevDepth.nextDepth = this.bins[depthBin];
                prevDepth.prevDepth = null;
                if (prevDepth.nextDepth != null) {
                    prevDepth.nextDepth.prevDepth = prevDepth;
                }
                this.bins[depthBin] = prevDepth;
            }
            prevDepth.depthBin = depthBin;
            if (prevDepth.getFirst() != null) {
                this.setDrawListChanged();
            }
        }
    }
    
    void addDrawables(final DrawList list) {
        Debug.Printf("SpatialTree: adding drawables.", new Object[0]);
        this.myAvatarTreeNode.addDrawables(list);
        for (SpatialTreeNode nextDepth : this.bins) {
            while (nextDepth != null) {
                nextDepth.addDrawables(list);
                nextDepth = nextDepth.nextDepth;
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
    
    final void removeEntry(final SpatialTreeNode spatialTreeNode) {
        this.setEntryBin(spatialTreeNode, -1);
    }
    
    void removeObject(final DrawListEntry drawListEntry) {
        final InlineList<DrawListEntry> list = drawListEntry.getList();
        if (list != null) {
            list.removeEntry(drawListEntry);
        }
    }
    
    final void setDrawListChanged() {
        this.drawListChanged = true;
    }
    
    final void setEntryDepth(final SpatialTreeNode spatialTreeNode, final float n) {
        final int n2 = 0;
        int round = Math.round(this.numBins * n / this.drawDistance);
        if (round < 0) {
            round = n2;
        }
        else if (round >= this.numBins) {
            round = this.numBins - 1;
        }
        this.setEntryBin(spatialTreeNode, round);
    }
    
    final void setTreeWalkNeeded() {
        this.treeWalkNeeded = true;
    }
    
    void updateObject(final DrawListEntry drawListEntry) {
        final InlineList<DrawListEntry> nodeForObject = this.getNodeForObject(drawListEntry);
        final InlineList<DrawListEntry> list = drawListEntry.getList();
        if (nodeForObject != list && list != null) {
            list.removeEntry(drawListEntry);
        }
        if (nodeForObject != null) {
            nodeForObject.addEntry(drawListEntry);
        }
    }
    
    void walkTree(final FrustrumPlanes frustrumPlanes, final float drawDistance) {
        Debug.Printf("SpatialTree: walkTree: starting to walk.", new Object[0]);
        this.drawDistance = drawDistance;
        this.rootNode.walkTree(frustrumPlanes, 1, this.depthBuf);
        this.treeWalkNeeded = false;
    }
}
