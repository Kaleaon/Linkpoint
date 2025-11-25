package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.spatial.DrawListEntry
import com.lumiyaviewer.lumiya.utils.InlineList
import kotlin.math.abs

class SpatialTreeNode(
    val spatialTree: SpatialTree,
    xSize: Float,
    ySize: Float,
    zSize: Float
) : InlineList<DrawListEntry>() {
    
    var depthBin: Int = -1
    var nextDepth: SpatialTreeNode? = null
    var prevDepth: SpatialTreeNode? = null
    
    private var children: Array<SpatialTreeNode?>? = null
    private var indexInParent: Int = 0
    private var leaf: Boolean = false
    private var parent: SpatialTreeNode? = null
    val position: FloatArray
    private var singleChild: SpatialTreeNode? = null
    private var splitAxis: Int = 0

    companion object {
        private const val MIN_SIZE = 2.0f
    }

    init {
        this.position = floatArrayOf(
            0.0f, 0.0f, 0.0f, xSize, ySize, zSize, 
            0.0f, 0.0f, 0.0f, xSize, ySize, zSize
        )
        this.leaf = false
        this.parent = null
        this.indexInParent = 0
        this.splitAxis = longestAxis()
    }

    constructor(parent: SpatialTreeNode, index: Int) : this(parent.spatialTree, 0f, 0f, 0f) {
        this.parent = parent
        this.indexInParent = index
        
        // Recalculate position based on parent
        System.arraycopy(parent.position, 0, this.position, 0, 12)
        
        var isLeaf = true
        for (i in 0 until 3) {
            var min = parent.position[i + 6]
            var size = parent.position[i + 9] - min
            
            if (i == parent.splitAxis) {
                size /= MIN_SIZE
                min += (size / MIN_SIZE) * index.toFloat()
            }
            
            this.position[i + 6] = min
            this.position[i + 9] = min + size
            this.position[i] = (size / MIN_SIZE) + min
            this.position[i + 3] = min + (size / MIN_SIZE)
            
            if (size > MIN_SIZE) {
                isLeaf = false
            }
        }
        this.leaf = isLeaf
        this.splitAxis = longestAxis()
    }

    private fun longestAxis(): Int {
        var maxAxis = 0
        var maxSize = 0.0f
        
        for (i in 0 until 3) {
            val size = this.position[i + 9] - this.position[i + 6]
            if (size > maxSize) {
                maxSize = size
                maxAxis = i
            }
        }
        return maxAxis
    }

    fun addDrawables(drawList: DrawList) {
        var item = getFirst()
        while (item != null) {
            item.addToDrawList(drawList)
            item = item.getNext()
        }
        children?.forEach { child ->
            child?.addDrawables(drawList)
        }
    }

    override fun addEntry(entry: DrawListEntry) {
        val wasEmpty = getFirst() == null && children == null
        
        if (entry.getList() !== this) {
            super.addEntry(entry)
            enlargeForBoundingBox(wasEmpty, entry.boundingBox)
            
            if (wasEmpty || singleChild != null) {
                spatialTree.setTreeWalkNeeded()
            }
            
            if (depthBin != -1) {
                spatialTree.setDrawListChanged()
            }
        } else {
            shrinkBoundingBox()
        }
    }
    
    override fun removeEntry(entry: DrawListEntry) {
        super.removeEntry(entry)
        
        if (depthBin != -1) {
            spatialTree.setDrawListChanged()
        }
        
        if (getFirst() == null) {
            spatialTree.removeEntry(this)
            if (isEmpty()) {
                removeFromParent()
                return
            }
        }
        shrinkBoundingBox()
    }

    override fun requestEntryRemoval(entry: DrawListEntry) {
        spatialTree.spatialObjectIndex.requestEntryRemoval(entry)
    }

    fun walkTree(planes: FrustrumPlanes, parentResult: Int, depthBuf: FloatArray): Int {
        if (singleChild != null && getFirst() == null) {
            return singleChild!!.walkTree(planes, parentResult, depthBuf)
        }
        
        var result = parentResult
        if (result != -1) {
            result = planes.testBoundingBox(position, depthBuf)
        }
        
        var count: Int
        if (result == -1) {
            spatialTree.removeEntry(this)
            count = 0
        } else {
            count = 1
            if (getFirst() != null) {
                spatialTree.setEntryDepth(this, depthBuf[0])
            } else {
                spatialTree.removeEntry(this)
            }
        }
        
        children?.forEach { child ->
            if (child != null) {
                count += child.walkTree(planes, result, depthBuf)
            }
        }
        
        return count
    }
    
    private fun enlargeForBoundingBox(isEmpty: Boolean, bounds: FloatArray) {
        // Stub
    }
    
    private fun isEmpty(): Boolean = getFirst() == null && children == null
    
    private fun removeFromParent() {
        // Stub
    }
    
    private fun shrinkBoundingBox() {
        // Stub
    }
    
    protected fun findNode(bounds: FloatArray): SpatialTreeNode {
        if (leaf) return this
        // Simplified logic
        if (children == null) children = arrayOfNulls(3)
        if (children!![0] == null) children!![0] = SpatialTreeNode(this, 0)
        return children!![0]!!.findNode(bounds)
    }
}
