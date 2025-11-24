package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.utils.InlineList

class SpatialTree(
    val numBins: Int,
    xSize: Float,
    ySize: Float,
    zSize: Float,
    val spatialObjectIndex: SpatialObjectIndex
) {
    val INVALID_BIN: Int = -1
    private val bins: Array<SpatialTreeNode?> = arrayOfNulls(numBins)
    private val depthBuf = FloatArray(1)
    private var drawDistance: Float = 1.0f
    private var drawListChanged: Boolean = false
    private val myAvatarTreeNode: MyAvatarTreeNode = MyAvatarTreeNode(this)
    private val rootNode: SpatialTreeNode = SpatialTreeNode(this, xSize, ySize, zSize)
    private var treeWalkNeeded: Boolean = false

    private fun getNodeForObject(entry: DrawListEntry): InlineList<DrawListEntry> {
        return if (entry is DrawListAvatarEntry && entry.getObjectAvatarInfo().isMyAvatar()) {
            myAvatarTreeNode
        } else {
            rootNode.findNode(entry.boundingBox)
        }
    }

    private fun setEntryBin(node: SpatialTreeNode, binIndex: Int) {
        if (binIndex != node.depthBin) {
            if (node.depthBin != -1) {
                if (node.prevDepth != null) {
                    node.prevDepth?.nextDepth = node.nextDepth
                } else {
                    bins[node.depthBin] = node.nextDepth
                }
                if (node.nextDepth != null) {
                    node.nextDepth?.prevDepth = node.prevDepth
                }
                node.prevDepth = null
                node.nextDepth = null
            }
            if (binIndex != -1) {
                node.nextDepth = bins[binIndex]
                node.prevDepth = null
                if (node.nextDepth != null) {
                    node.nextDepth?.prevDepth = node
                }
                bins[binIndex] = node
            }
            node.depthBin = binIndex
            if (node.first != null) {
                setDrawListChanged()
            }
        }
    }

    fun addDrawables(drawList: DrawList) {
        Debug.Printf("SpatialTree: adding drawables.")
        myAvatarTreeNode.addDrawables(drawList)
        for (node in bins) {
            var current = node
            while (current != null) {
                current.addDrawables(drawList)
                current = current.nextDepth
            }
        }
        drawListChanged = false
    }

    fun isDrawListChanged(): Boolean {
        return drawListChanged
    }

    fun isTreeWalkNeeded(): Boolean {
        return treeWalkNeeded
    }

    fun removeEntry(node: SpatialTreeNode) {
        setEntryBin(node, -1)
    }

    fun removeObject(entry: DrawListEntry) {
        val list = entry.list
        list?.removeEntry(entry)
    }

    fun setDrawListChanged() {
        drawListChanged = true
    }

    fun setEntryDepth(node: SpatialTreeNode, depth: Float) {
        var binIndex = 0
        val round = Math.round((numBins.toFloat() * depth) / drawDistance)
        if (round >= 0) {
            binIndex = if (round >= numBins) numBins - 1 else round
        }
        setEntryBin(node, binIndex)
    }

    fun setTreeWalkNeeded() {
        treeWalkNeeded = true
    }

    fun updateObject(entry: DrawListEntry) {
        val nodeForObject = getNodeForObject(entry)
        val currentList = entry.list
        if (nodeForObject !== currentList && currentList != null) {
            currentList.removeEntry(entry)
        }
        nodeForObject.addEntry(entry)
    }

    fun walkTree(frustrumPlanes: FrustrumPlanes, distance: Float) {
        Debug.Printf("SpatialTree: walkTree: starting to walk.")
        drawDistance = distance
        rootNode.walkTree(frustrumPlanes, 1, depthBuf)
        treeWalkNeeded = false
    }
}
