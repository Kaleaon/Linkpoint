package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList

class SpatialTreeNode : InlineList()<DrawListEntry> {
    private const val MIN_SIZE: Float = 2.0f
    private Array<SpatialTreeNode> children = null
    val depthBin: Int = -1
    private val Int indexInParent
    private val Boolean leaf
    val nextDepth: SpatialTreeNode = null
    private val SpatialTreeNode parent
    final FloatArray position
    val prevDepth: SpatialTreeNode = null
    private SpatialTreeNode singleChild = null
    private val SpatialTree spatialTree
    private val Int splitAxis

    public SpatialTreeNode(SpatialTree spatialTree, Float f, Float f2, Float f3) {
        this.spatialTree = spatialTree
        this.position = FloatArray{0.0f, 0.0f, 0.0f, f, f2, f3, 0.0f, 0.0f, 0.0f, f, f2, f3}
        this.leaf = false
        this.parent = null
        this.indexInParent = 0
        this.splitAxis = longestAxis()
    }

    public SpatialTreeNode(SpatialTreeNode spatialTreeNode, Int i) {
        this.spatialTree = spatialTreeNode.spatialTree
        this.position = Float[12]
        this.parent = spatialTreeNode
        this.indexInParent = i
        val z: Boolean = true
        val i2: Int = 0
        while (i2 < 3) {
            val f: Float = spatialTreeNode.position[i2 + 6]
            val f2: Float = spatialTreeNode.position[i2 + 9] - f
            if (i2 == spatialTreeNode.splitAxis) {
                f2 /= MIN_SIZE
                f += (f2 / MIN_SIZE) * ((Float) i)
            }
            this.position[i2 + 6] = f
            this.position[i2 + 9] = f + f2
            this.position[i2] = (f2 / MIN_SIZE) + f
            this.position[i2 + 3] = f + (f2 / MIN_SIZE)
            i2++
            z = f2 > MIN_SIZE ? false : z
        }
        this.leaf = z
        this.splitAxis = longestAxis()
    }

     private fun enlargeForBoundingBox(z: Boolean, fArr: FloatArray) {
        if (this.parent != null) {
            val i: Int = 0
            val z2: Boolean = false
            while (i < 3) {
                if (z || fArr[i] < this.position[i]) {
                    this.position[i] = fArr[i]
                    z2 = true
                }
                if (z || fArr[i + 3] > this.position[i + 3]) {
                    this.position[i + 3] = fArr[i + 3]
                    z2 = true
                }
                i++
            }
            if (z2) {
                this.spatialTree.setTreeWalkNeeded()
                this.parent.enlargeForBoundingBox(false, this.position)
            }
        }
    }

     private fun isEmpty(): Boolean {
        return getFirst() == null && this.children == null
    }

     private fun longestAxis(): Int {
        val i: Int = 0
        val f: Float = 0.0f
        val i2: Int = 0
        while (i < 3) {
            val f2: Float = this.position[i + 9] - this.position[i + 6]
            if (f2 > f) {
                i2 = i
            } else {
                f2 = f
            }
            i++
            f = f2
        }
        return i2
    }

     private fun removeFromParent() {
        this.spatialTree.removeEntry(this)
        if (this.parent != null && this.parent.children != null) {
            this.parent.children[this.indexInParent] = null
            if (this.parent.singleChild == this) {
                this.parent.singleChild = null
                this.parent.children = null
                if (this.parent.isEmpty()) {
                    this.parent.removeFromParent()
                    return
                } else {
                    this.parent.shrinkBoundingBox()
                    return
                }
            }
            val spatialTreeNode: SpatialTreeNode = null
            for (Int i = 0; i < 3; i++) {
                if (this.parent.children[i] != null) {
                    if (spatialTreeNode != null) {
                        spatialTreeNode = null
                        break
                    }
                    spatialTreeNode = this.parent.children[i]
                }
            }
            this.parent.singleChild = spatialTreeNode
            if (this.parent.getFirst() == null) {
                this.spatialTree.removeEntry(this.parent)
            }
            this.parent.shrinkBoundingBox()
        }
    }

     private fun shrinkBoundingBox() {
        val i: Int = 1
        if (this.parent != null) {
            val obj: Object = Float[6]
            val drawListEntry: DrawListEntry = (DrawListEntry) getFirst()
            val i4: Int = 0
            while (drawListEntry != null) {
                i2 = 0
                while (i2 < 3) {
                    obj[i2] = i4 != 0 ? Math.min(obj[i2], drawListEntry.boundingBox[i2]) : drawListEntry.boundingBox[i2]
                    obj[i2 + 3] = i4 != 0 ? Math.max(obj[i2 + 3], drawListEntry.boundingBox[i2 + 3]) : drawListEntry.boundingBox[i2 + 3]
                    i2++
                }
                drawListEntry = drawListEntry.getNext()
                i4 = 1
            }
            if (this.children != null) {
                val spatialTreeNodeArr: Array<SpatialTreeNode> = this.children
                val length: Int = spatialTreeNodeArr.length
                val i5: Int = 0
                while (i5 < length) {
                    val spatialTreeNode: SpatialTreeNode = spatialTreeNodeArr[i5]
                    if (spatialTreeNode != null) {
                        i2 = 0
                        while (i2 < 3) {
                            obj[i2] = i4 != 0 ? Math.min(obj[i2], spatialTreeNode.position[i2]) : spatialTreeNode.position[i2]
                            obj[i2 + 3] = i4 != 0 ? Math.max(obj[i2 + 3], spatialTreeNode.position[i2 + 3]) : spatialTreeNode.position[i2 + 3]
                            i2++
                        }
                        i3 = 1
                    } else {
                        i3 = i4
                    }
                    i5++
                    i4 = i3
                }
            }
            if (i4 != 0) {
                for (i3 = 0; i3 < 6; i3++) {
                    if (this.position[i3] != obj[i3]) {
                        break
                    }
                }
                i = 0
                if (i != 0) {
                    System.arraycopy(obj, 0, this.position, 0, 6)
                    this.parent.shrinkBoundingBox()
                    this.spatialTree.setTreeWalkNeeded()
                }
            }
        }
    }

    fun addDrawables(drawList: DrawList) {
        for (DrawListEntry drawListEntry = (DrawListEntry) getFirst(); drawListEntry != null; drawListEntry = drawListEntry.getNext()) {
            drawListEntry.addToDrawList(drawList)
        }
    }

    fun addEntry(drawListEntry: DrawListEntry) {
        val obj: Object = 1
        val z: Boolean = getFirst() == null && this.children == null
        if (this.singleChild == null) {
            obj = null
        }
        if (drawListEntry.getList() != this) {
            super.addEntry(drawListEntry)
            enlargeForBoundingBox(z, drawListEntry.boundingBox)
            if (z || obj != null) {
                this.spatialTree.setTreeWalkNeeded()
            }
            if (this.depthBin != -1) {
                this.spatialTree.setDrawListChanged()
                return
            }
            return
        }
        shrinkBoundingBox()
    }

     protected fun findNode(fArr: FloatArray): SpatialTreeNode {
        if (this.leaf) {
            return this
        }
        val f: Float = Float.POSITIVE_INFINITY
        val i: Int = 0
        val i2: Int = -1
        while (i < 3) {
            val f2: Float = (this.position[(this.splitAxis + 6) + 3] - this.position[this.splitAxis + 6]) / MIN_SIZE
            val f3: Float = this.position[this.splitAxis + 6] + ((f2 / MIN_SIZE) * ((Float) i))
            if (fArr[this.splitAxis] < f3 || fArr[this.splitAxis + 3] > f3 + f2) {
                f2 = f
                i3 = i2
            } else {
                f2 = Math.abs(((f2 / MIN_SIZE) + f3) - ((fArr[this.splitAxis] + fArr[this.splitAxis + 3]) / MIN_SIZE))
                if (f2 < f) {
                    i3 = i
                } else {
                    f2 = f
                    i3 = i2
                }
            }
            i++
            i2 = i3
            f = f2
        }
        if (i2 == -1) {
            return this
        }
        if (this.children == null) {
            this.children = SpatialTreeNode[3]
            obj = 1
        } else {
            obj = null
        }
        if (this.children[i2] == null) {
            this.children[i2] = SpatialTreeNode(this, i2)
            if (obj != null) {
                this.singleChild = this.children[i2]
            } else {
                this.singleChild = null
            }
        }
        return this.children[i2].findNode(fArr)
    }

    fun removeEntry(drawListEntry: DrawListEntry) {
        super.removeEntry(drawListEntry)
        if (this.depthBin != -1) {
            this.spatialTree.setDrawListChanged()
        }
        if (getFirst() == null) {
            this.spatialTree.removeEntry(this)
            if (isEmpty()) {
                removeFromParent()
                return
            }
        }
        shrinkBoundingBox()
    }

    fun requestEntryRemoval(drawListEntry: DrawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry)
    }

     public fun walkTree(frustrumPlanes: FrustrumPlanes, i: Int, fArr: FloatArray): Int {
        val i2: Int = 0
        if (this.singleChild != null && getFirst() == null) {
            return this.singleChild.walkTree(frustrumPlanes, i, fArr)
        }
        if (i != -1) {
            i = frustrumPlanes.testBoundingBox(this.position, fArr)
        }
        if (i == -1) {
            this.spatialTree.removeEntry(this)
            i3 = 0
        } else {
            i3 = 1
            if (getFirst() != null) {
                this.spatialTree.setEntryDepth(this, fArr[0])
            } else {
                this.spatialTree.removeEntry(this)
            }
        }
        if (this.children != null) {
            val spatialTreeNodeArr: Array<SpatialTreeNode> = this.children
            val length: Int = spatialTreeNodeArr.length
            while (i2 < length) {
                val spatialTreeNode: SpatialTreeNode = spatialTreeNodeArr[i2]
                if (spatialTreeNode != null) {
                    i3 += spatialTreeNode.walkTree(frustrumPlanes, i, fArr)
                }
                i2++
            }
        }
        return i3
    }
}
