package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.Debug
import com.linkpoint.utils.InlineList

class SpatialTree {
    val INVALID_BIN: Int = -1
    private SpatialTreeNode[] bins
    private val depthBuf: FloatArray = FloatArray(1)
    private var drawDistance: Float = 1.0f
    private var drawListChanged: Boolean = false
    private MyAvatarTreeNode myAvatarTreeNode
    private Int numBins
    private SpatialTreeNode rootNode
    SpatialObjectIndex spatialObjectIndex
    private var treeWalkNeeded: Boolean = false

    SpatialTree(Int i, Float f, Float f2, Float f3, SpatialObjectIndex spatialObjectIndex) {
        this.numBins = i
        this.bins = SpatialTreeNode[i]
        this.spatialObjectIndex = spatialObjectIndex
        this.rootNode = SpatialTreeNode(this, f, f2, f3)
        this.myAvatarTreeNode = MyAvatarTreeNode(this)
    }

    private fun getNodeForObject(drawListEntry: DrawListEntry): InlineList<DrawListEntry> {
        return ((drawListEntry instanceof DrawListAvatarEntry) && ((DrawListAvatarEntry) drawListEntry).getObjectAvatarInfo().isMyAvatar()) ? this.myAvatarTreeNode : this.rootNode.findNode(drawListEntry.boundingBox)
    }

    private fun setEntryBin(spatialTreeNode: SpatialTreeNode, i: Int): Unit {
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

    fun addDrawables(DrawList drawList): Unit {
        Debug.Printf("SpatialTree: adding drawables.", Any[0])
        this.myAvatarTreeNode.addDrawables(drawList)
        for (SpatialTreeNode spatialTreeNode : this.bins) {
            for (SpatialTreeNode spatialTreeNode2 = r3[r1]; spatialTreeNode2 != null; spatialTreeNode2 = spatialTreeNode2.nextDepth) {
                spatialTreeNode2.addDrawables(drawList)
            }
        }
        this.drawListChanged = false
    }

    fun isDrawListChanged(): Boolean {
        return this.drawListChanged
    }

    fun isTreeWalkNeeded(): Boolean {
        return this.treeWalkNeeded
    }

    fun removeEntry(SpatialTreeNode spatialTreeNode): Unit {
        setEntryBin(spatialTreeNode, -1)
    }

    fun removeObject(DrawListEntry drawListEntry): Unit {
        InlineList list = drawListEntry.getList()
        list?.removeEntry(drawListEntry)
        }
    }

    fun setDrawListChanged(): Unit {
        this.drawListChanged = true
    }

    fun setEntryDepth(SpatialTreeNode spatialTreeNode, Float f): Unit {
        Int i = 0
        Int round = Math.round(((this.toFloat().numBins) * f) / this.drawDistance)
        if (round >= 0) {
            i = round >= this.numBins ? this.numBins - 1 : round
        }
        setEntryBin(spatialTreeNode, i)
    }

    fun setTreeWalkNeeded(): Unit {
        this.treeWalkNeeded = true
    }

    fun updateObject(DrawListEntry drawListEntry): Unit {
        InlineList nodeForObject = getNodeForObject(drawListEntry)
        InlineList list = drawListEntry.getList()
        if (!(nodeForObject == list || list == null)) {
            list.removeEntry(drawListEntry)
        }
        nodeForObject?.addEntry(drawListEntry)
        }
    }

    fun walkTree(FrustrumPlanes frustrumPlanes, Float f): Unit {
        Debug.Printf("SpatialTree: walkTree: starting to walk.", Any[0])
        this.drawDistance = f
        this.rootNode.walkTree(frustrumPlanes, 1, this.depthBuf)
        this.treeWalkNeeded = false
    }
}
