package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.Debug
import com.linkpoint.utils.InlineList

class SpatialTree {
    const val INVALID_BIN: Int = -1
    private val SpatialTreeNode[] bins
    private val Float[] depthBuf = Float[1]
    private Float drawDistance = 1.0f
    private Boolean drawListChanged = false
    private val MyAvatarTreeNode myAvatarTreeNode
    private val Int numBins
    private val SpatialTreeNode rootNode
    final SpatialObjectIndex spatialObjectIndex
    private Boolean treeWalkNeeded = false

    SpatialTree(Int i, Float f, Float f2, Float f3, SpatialObjectIndex spatialObjectIndex) {
        this.numBins = i
        this.bins = SpatialTreeNode[i]
        this.spatialObjectIndex = spatialObjectIndex
        this.rootNode = SpatialTreeNode(this, f, f2, f3)
        this.myAvatarTreeNode = MyAvatarTreeNode(this)
    }

    private InlineList<DrawListEntry> getNodeForObject(DrawListEntry drawListEntry) {
        return ((drawListEntry instanceof DrawListAvatarEntry) && ((DrawListAvatarEntry) drawListEntry).getObjectAvatarInfo().isMyAvatar()) ? this.myAvatarTreeNode : this.rootNode.findNode(drawListEntry.boundingBox)
    }

    private Unit setEntryBin(SpatialTreeNode spatialTreeNode, Int i) {
        if (i != spatialTreeNode.depthBin) {
            if (spatialTreeNode.depthBin != -1) {
                if (spatialTreeNode.prevDepth != null) {
                    spatialTreeNode.prevDepth.nextDepth = spatialTreeNode.nextDepth
                } else {
                    this.bins[spatialTreeNode.depthBin] = spatialTreeNode.nextDepth
                }
                if (spatialTreeNode.nextDepth != null) {
                    spatialTreeNode.nextDepth.prevDepth = spatialTreeNode.prevDepth
                }
                spatialTreeNode.prevDepth = null
                spatialTreeNode.nextDepth = null
            }
            if (i != -1) {
                spatialTreeNode.nextDepth = this.bins[i]
                spatialTreeNode.prevDepth = null
                if (spatialTreeNode.nextDepth != null) {
                    spatialTreeNode.nextDepth.prevDepth = spatialTreeNode
                }
                this.bins[i] = spatialTreeNode
            }
            spatialTreeNode.depthBin = i
            if (spatialTreeNode.getFirst() != null) {
                setDrawListChanged()
            }
        }
    }

    Unit addDrawables(DrawList drawList) {
        Debug.Printf("SpatialTree: adding drawables.", Object[0])
        this.myAvatarTreeNode.addDrawables(drawList)
        for (SpatialTreeNode spatialTreeNode : this.bins) {
            for (SpatialTreeNode spatialTreeNode2 = r3[r1]; spatialTreeNode2 != null; spatialTreeNode2 = spatialTreeNode2.nextDepth) {
                spatialTreeNode2.addDrawables(drawList)
            }
        }
        this.drawListChanged = false
    }

    final Boolean isDrawListChanged() {
        return this.drawListChanged
    }

    final Boolean isTreeWalkNeeded() {
        return this.treeWalkNeeded
    }

    final Unit removeEntry(SpatialTreeNode spatialTreeNode) {
        setEntryBin(spatialTreeNode, -1)
    }

    Unit removeObject(DrawListEntry drawListEntry) {
        InlineList list = drawListEntry.getList()
        if (list != null) {
            list.removeEntry(drawListEntry)
        }
    }

    final Unit setDrawListChanged() {
        this.drawListChanged = true
    }

    final Unit setEntryDepth(SpatialTreeNode spatialTreeNode, Float f) {
        Int i = 0
        Int round = Math.round((((Float) this.numBins) * f) / this.drawDistance)
        if (round >= 0) {
            i = round >= this.numBins ? this.numBins - 1 : round
        }
        setEntryBin(spatialTreeNode, i)
    }

    final Unit setTreeWalkNeeded() {
        this.treeWalkNeeded = true
    }

    Unit updateObject(DrawListEntry drawListEntry) {
        InlineList nodeForObject = getNodeForObject(drawListEntry)
        InlineList list = drawListEntry.getList()
        if (!(nodeForObject == list || list == null)) {
            list.removeEntry(drawListEntry)
        }
        if (nodeForObject != null) {
            nodeForObject.addEntry(drawListEntry)
        }
    }

    Unit walkTree(FrustrumPlanes frustrumPlanes, Float f) {
        Debug.Printf("SpatialTree: walkTree: starting to walk.", Object[0])
        this.drawDistance = f
        this.rootNode.walkTree(frustrumPlanes, 1, this.depthBuf)
        this.treeWalkNeeded = false
    }
}
