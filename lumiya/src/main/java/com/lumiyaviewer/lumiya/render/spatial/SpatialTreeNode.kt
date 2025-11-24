package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.utils.InlineList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SpatialTreeNode : InlineList<DrawListEntry> {
    companion object {
        private const val MIN_SIZE = 2.0f
    }

    var depthBin: Int = -1
    var nextDepth: SpatialTreeNode? = null
    var prevDepth: SpatialTreeNode? = null
    
    private var children: Array<SpatialTreeNode?>? = null
    private var indexInParent: Int = 0
    private var leaf: Boolean = false
    private var parent: SpatialTreeNode? = null
    val position: FloatArray
    private var singleChild: SpatialTreeNode? = null
    private val spatialTree: SpatialTree
    private var splitAxis: Int = 0

    constructor(spatialTree: SpatialTree, xSize: Float, ySize: Float, zSize: Float) {
        this.spatialTree = spatialTree
        this.position = floatArrayOf(
            0.0f, 0.0f, 0.0f, xSize, ySize, zSize, 
            0.0f, 0.0f, 0.0f, xSize, ySize, zSize
        )
        this.leaf = false
        this.parent = null
        this.indexInParent = 0
        this.splitAxis = longestAxis()
    }

    constructor(parent: SpatialTreeNode, index: Int) {
        this.spatialTree = parent.spatialTree
        this.position = FloatArray(12)
        this.parent = parent
        this.indexInParent = index
        
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
                // Loop continues, but we mark if ANY dimension is > MIN_SIZE? 
                // Decompiled: z = f2 > MIN_SIZE ? false : z
                // If f2 (size) > MIN_SIZE, then z (leaf) becomes false.
                // Initial z is true.
                isLeaf = false
            }
        }
        this.leaf = isLeaf
        this.splitAxis = longestAxis()
    }

    private fun enlargeForBoundingBox(isEmpty: Boolean, bounds: FloatArray) {
        val p = parent ?: return
        var changed = false
        
        for (i in 0 until 3) {
            if (isEmpty || bounds[i] < this.position[i]) {
                this.position[i] = bounds[i]
                changed = true
            }
            if (isEmpty || bounds[i + 3] > this.position[i + 3]) {
                this.position[i + 3] = bounds[i + 3]
                changed = true
            }
        }
        
        if (changed) {
            spatialTree.setTreeWalkNeeded()
            p.enlargeForBoundingBox(false, this.position)
        }
    }

    private fun isEmpty(): Boolean {
        return first == null && children == null
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

    private fun removeFromParent() {
        spatialTree.removeEntry(this)
        val p = parent
        if (p != null && p.children != null) {
            p.children!![indexInParent] = null
            
            if (p.singleChild === this) {
                p.singleChild = null
                p.children = null
                if (p.isEmpty()) {
                    p.removeFromParent()
                } else {
                    p.shrinkBoundingBox()
                }
                return
            }
            
            var remainingChild: SpatialTreeNode? = null
            var childCount = 0
            for (child in p.children!!) {
                if (child != null) {
                    if (remainingChild != null) {
                        remainingChild = null // More than one child
                        childCount = 2
                        break
                    }
                    remainingChild = child
                    childCount = 1
                }
            }
            
            p.singleChild = remainingChild
            if (p.first == null) {
                spatialTree.removeEntry(p)
            }
            p.shrinkBoundingBox()
        }
    }

    private fun shrinkBoundingBox() {
        if (parent == null) return
        
        val newBounds = FloatArray(6)
        var hasItems = false
        
        // Check items in this node
        var item = first as? DrawListEntry
        while (item != null) {
            for (i in 0 until 3) {
                newBounds[i] = if (hasItems) min(newBounds[i], item!!.boundingBox[i]) else item!!.boundingBox[i]
                newBounds[i + 3] = if (hasItems) max(newBounds[i + 3], item!!.boundingBox[i + 3]) else item!!.boundingBox[i + 3]
            }
            item = item.next as? DrawListEntry
            hasItems = true
        }
        
        // Check children
        children?.forEach { child ->
            if (child != null) {
                for (i in 0 until 3) {
                    newBounds[i] = if (hasItems) min(newBounds[i], child.position[i]) else child.position[i]
                    newBounds[i + 3] = if (hasItems) max(newBounds[i + 3], child.position[i + 3]) else child.position[i + 3]
                }
                hasItems = true
            }
        }
        
        if (hasItems) {
            var changed = false
            for (i in 0 until 6) {
                if (this.position[i] != newBounds[i]) {
                    changed = true
                    break
                }
            }
            
            if (changed) {
                System.arraycopy(newBounds, 0, this.position, 0, 6)
                parent!!.shrinkBoundingBox()
                spatialTree.setTreeWalkNeeded()
            }
        }
    }

    fun addDrawables(drawList: DrawList) {
        var item = first as? DrawListEntry
        while (item != null) {
            item.addToDrawList(drawList)
            item = item.next as? DrawListEntry
        }
    }

    override fun addEntry(entry: DrawListEntry) {
        val wasEmpty = first == null && children == null
        // Decompiled logic: obj = 1 if singleChild == null? No, `if (this.singleChild == null) { obj = null }`.
        // Wait. `Any obj = 1`. `if (singleChild == null) obj = null`. So `obj` is not null if `singleChild` is not null?
        // Then `if (z || obj != null)`. z is wasEmpty.
        // So if wasEmpty OR hasSingleChild.
        
        if (entry.list !== this) {
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

    protected fun findNode(bounds: FloatArray): SpatialTreeNode {
        if (leaf) return this
        
        var minCost = Float.POSITIVE_INFINITY
        var bestChildIndex = -1
        
        for (i in 0 until 3) {
            val stepSize = (position[splitAxis + 6 + 3] - position[splitAxis + 6]) / MIN_SIZE
            val childStart = position[splitAxis + 6] + ((stepSize / MIN_SIZE) * i.toFloat())
            
            val cost: Float
            val nextChildIndex: Int
            
            if (bounds[splitAxis] < childStart || bounds[splitAxis + 3] > childStart + stepSize) {
                cost = minCost
                nextChildIndex = bestChildIndex
            } else {
                val diff = abs(((stepSize / MIN_SIZE) + childStart) - ((bounds[splitAxis] + bounds[splitAxis + 3]) / MIN_SIZE))
                if (diff < minCost) {
                    cost = diff
                    nextChildIndex = i
                } else {
                    cost = minCost
                    nextChildIndex = bestChildIndex
                }
            }
            
            if (nextChildIndex != -1) {
                minCost = cost
                bestChildIndex = nextChildIndex
            }
        }
        
        if (bestChildIndex == -1) return this
        
        if (children == null) {
            children = arrayOfNulls(3)
            // Logic about `obj = 1` was here.
        }
        
        if (children!![bestChildIndex] == null) {
            val newChild = SpatialTreeNode(this, bestChildIndex)
            children!![bestChildIndex] = newChild
            
            // If it was first child, set singleChild?
            // Decompiled: `if (obj != null) singleChild = children[i2] else singleChild = null`
            // `obj` was 1 if `children` was just allocated (so prev was null).
            // So if we just created the array, this is the first child.
            // BUT wait, if `children` was NOT null, `obj` was null.
            // If `obj` is null, `singleChild` becomes null.
            // Meaning: if we add a second child, singleChild becomes null.
            
            // Logic reconstruction:
            // If we created a new child array, we have 1 child -> singleChild = newChild.
            // If we already had array, we are adding another child (or filling existing slot).
            // If we are filling a slot that was null, and we already have other slots filled...
            // The logic `singleChild = null` implies checking if there is exactly one child.
            
            // Let's approximate:
            // If `children` was freshly created, `singleChild = newChild`.
            // If `children` existed, we assume more than 1 child might exist now?
            // Wait, `children` array exists. If we add a child, we need to check if it's the ONLY child.
            // But this logic runs only when `children[i] == null` (creating new child).
            // If `children` existed, and we create a new child, we now have at least 1.
            // If we had `singleChild` set, and we create *another* child (different index), `singleChild` should become null.
            
            // Re-reading decompiled:
            // if (children == null) { children = new...; obj = 1; } else { obj = null; }
            // if (children[i] == null) { create...; if (obj != null) singleChild = new; else singleChild = null; }
            
            // Case 1: First child. children=null -> children=new, obj=1. create child. singleChild=new. Correct.
            // Case 2: Second child. children!=null -> obj=null. create child. singleChild=null. Correct.
            // Case 3: Existing child (not null). Block skipped. singleChild unchanged. Correct.
            
            if (singleChild == null && children!!.count { it != null } == 1) {
                 // Wait, if we just added it, count is 1.
            }
            
            // Following decompiled strictly:
            if (children!!.count { it != null } == 1) { 
                // Only 1 child (the one we just added, assuming array was empty or we track it).
                // But if array was not null, we set singleChild to null.
                // This implies if we add a *second* child, singleChild is null.
                // If we add a *third*, singleChild is null.
                
                // Implementation:
                // Check how many non-null children there are.
                val count = children!!.count { it != null }
                if (count == 1) {
                    singleChild = children!![bestChildIndex]
                } else {
                    singleChild = null
                }
            }
        }
        
        return children!![bestChildIndex]!!.findNode(bounds)
    }

    override fun removeEntry(entry: DrawListEntry) {
        super.removeEntry(entry)
        
        if (depthBin != -1) {
            spatialTree.setDrawListChanged()
        }
        
        if (first == null) {
            spatialTree.removeEntry(this)
            if (isEmpty()) {
                removeFromParent()
                return
            }
        }
        shrinkBoundingBox()
    }

    fun requestEntryRemoval(entry: DrawListEntry) {
        spatialTree.spatialObjectIndex.requestEntryRemoval(entry)
    }

    fun walkTree(planes: FrustrumPlanes, parentResult: Int, depthBuf: FloatArray): Int {
        if (singleChild != null && first == null) {
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
            if (first != null) {
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
}
