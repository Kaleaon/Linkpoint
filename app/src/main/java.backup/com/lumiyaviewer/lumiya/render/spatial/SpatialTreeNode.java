package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;

public class SpatialTreeNode extends InlineList<DrawListEntry> {
    private static final float MIN_SIZE = 2.0f;
    private SpatialTreeNode[] children = null;
    int depthBin = -1;
    private final int indexInParent;
    private final boolean leaf;
    SpatialTreeNode nextDepth = null;
    private final SpatialTreeNode parent;
    final float[] position;
    SpatialTreeNode prevDepth = null;
    private SpatialTreeNode singleChild = null;
    private final SpatialTree spatialTree;
    private final int splitAxis;

    public SpatialTreeNode(SpatialTree spatialTree, float f, float f2, float f3) {
        this.spatialTree = spatialTree;
        this.position = new float[]{0.0f, 0.0f, 0.0f, f, f2, f3, 0.0f, 0.0f, 0.0f, f, f2, f3};
        this.leaf = false;
        this.parent = null;
        this.indexInParent = 0;
        this.splitAxis = longestAxis();
    }

    public SpatialTreeNode(SpatialTreeNode spatialTreeNode, int i) {
        this.spatialTree = spatialTreeNode.spatialTree;
        this.position = new float[12];
        this.parent = spatialTreeNode;
        this.indexInParent = i;
        boolean z = true;
        int i2 = 0;
        while (i2 < 3) {
            float f = spatialTreeNode.position[i2 + 6];
            float f2 = spatialTreeNode.position[i2 + 9] - f;
            if (i2 == spatialTreeNode.splitAxis) {
                f2 /= MIN_SIZE;
                f += (f2 / MIN_SIZE) * ((float) i);
            }
            this.position[i2 + 6] = f;
            this.position[i2 + 9] = f + f2;
            this.position[i2] = (f2 / MIN_SIZE) + f;
            this.position[i2 + 3] = f + (f2 / MIN_SIZE);
            i2++;
            z = f2 > MIN_SIZE ? false : z;
        }
        this.leaf = z;
        this.splitAxis = longestAxis();
    }

    private void enlargeForBoundingBox(boolean z, float[] fArr) {
        if (this.parent != null) {
            int i = 0;
            boolean z2 = false;
            while (i < 3) {
                if (z || fArr[i] < this.position[i]) {
                    this.position[i] = fArr[i];
                    z2 = true;
                }
                if (z || fArr[i + 3] > this.position[i + 3]) {
                    this.position[i + 3] = fArr[i + 3];
                    z2 = true;
                }
                i++;
            }
            if (z2) {
                this.spatialTree.setTreeWalkNeeded();
                this.parent.enlargeForBoundingBox(false, this.position);
            }
        }
    }

    private boolean isEmpty() {
        return getFirst() == null && this.children == null;
    }

    private int longestAxis() {
        int i = 0;
        float f = 0.0f;
        int i2 = 0;
        while (i < 3) {
            float f2 = this.position[i + 9] - this.position[i + 6];
            if (f2 > f) {
                i2 = i;
            } else {
                f2 = f;
            }
            i++;
            f = f2;
        }
        return i2;
    }

    private void removeFromParent() {
        this.spatialTree.removeEntry(this);
        if (this.parent != null && this.parent.children != null) {
            this.parent.children[this.indexInParent] = null;
            if (this.parent.singleChild == this) {
                this.parent.singleChild = null;
                this.parent.children = null;
                if (this.parent.isEmpty()) {
                    this.parent.removeFromParent();
                    return;
                } else {
                    this.parent.shrinkBoundingBox();
                    return;
                }
            }
            SpatialTreeNode spatialTreeNode = null;
            for (int i = 0; i < 3; i++) {
                if (this.parent.children[i] != null) {
                    if (spatialTreeNode != null) {
                        spatialTreeNode = null;
                        break;
                    }
                    spatialTreeNode = this.parent.children[i];
                }
            }
            this.parent.singleChild = spatialTreeNode;
            if (this.parent.getFirst() == null) {
                this.spatialTree.removeEntry(this.parent);
            }
            this.parent.shrinkBoundingBox();
        }
    }

    private void shrinkBoundingBox() {
        int i = 1;
        if (this.parent != null) {
            int i2;
            int i3;
            Object obj = new float[6];
            DrawListEntry drawListEntry = (DrawListEntry) getFirst();
            int i4 = 0;
            while (drawListEntry != null) {
                i2 = 0;
                while (i2 < 3) {
                    obj[i2] = i4 != 0 ? Math.min(obj[i2], drawListEntry.boundingBox[i2]) : drawListEntry.boundingBox[i2];
                    obj[i2 + 3] = i4 != 0 ? Math.max(obj[i2 + 3], drawListEntry.boundingBox[i2 + 3]) : drawListEntry.boundingBox[i2 + 3];
                    i2++;
                }
                drawListEntry = drawListEntry.getNext();
                i4 = 1;
            }
            if (this.children != null) {
                SpatialTreeNode[] spatialTreeNodeArr = this.children;
                int length = spatialTreeNodeArr.length;
                int i5 = 0;
                while (i5 < length) {
                    SpatialTreeNode spatialTreeNode = spatialTreeNodeArr[i5];
                    if (spatialTreeNode != null) {
                        i2 = 0;
                        while (i2 < 3) {
                            obj[i2] = i4 != 0 ? Math.min(obj[i2], spatialTreeNode.position[i2]) : spatialTreeNode.position[i2];
                            obj[i2 + 3] = i4 != 0 ? Math.max(obj[i2 + 3], spatialTreeNode.position[i2 + 3]) : spatialTreeNode.position[i2 + 3];
                            i2++;
                        }
                        i3 = 1;
                    } else {
                        i3 = i4;
                    }
                    i5++;
                    i4 = i3;
                }
            }
            if (i4 != 0) {
                for (i3 = 0; i3 < 6; i3++) {
                    if (this.position[i3] != obj[i3]) {
                        break;
                    }
                }
                i = 0;
                if (i != 0) {
                    System.arraycopy(obj, 0, this.position, 0, 6);
                    this.parent.shrinkBoundingBox();
                    this.spatialTree.setTreeWalkNeeded();
                }
            }
        }
    }

    public void addDrawables(DrawList drawList) {
        for (DrawListEntry drawListEntry = (DrawListEntry) getFirst(); drawListEntry != null; drawListEntry = drawListEntry.getNext()) {
            drawListEntry.addToDrawList(drawList);
        }
    }

    public void addEntry(DrawListEntry drawListEntry) {
        Object obj = 1;
        boolean z = getFirst() == null && this.children == null;
        if (this.singleChild == null) {
            obj = null;
        }
        if (drawListEntry.getList() != this) {
            super.addEntry(drawListEntry);
            enlargeForBoundingBox(z, drawListEntry.boundingBox);
            if (z || obj != null) {
                this.spatialTree.setTreeWalkNeeded();
            }
            if (this.depthBin != -1) {
                this.spatialTree.setDrawListChanged();
                return;
            }
            return;
        }
        shrinkBoundingBox();
    }

    protected SpatialTreeNode findNode(float[] fArr) {
        if (this.leaf) {
            return this;
        }
        float f = Float.POSITIVE_INFINITY;
        int i = 0;
        int i2 = -1;
        while (i < 3) {
            int i3;
            float f2 = (this.position[(this.splitAxis + 6) + 3] - this.position[this.splitAxis + 6]) / MIN_SIZE;
            float f3 = this.position[this.splitAxis + 6] + ((f2 / MIN_SIZE) * ((float) i));
            if (fArr[this.splitAxis] < f3 || fArr[this.splitAxis + 3] > f3 + f2) {
                f2 = f;
                i3 = i2;
            } else {
                f2 = Math.abs(((f2 / MIN_SIZE) + f3) - ((fArr[this.splitAxis] + fArr[this.splitAxis + 3]) / MIN_SIZE));
                if (f2 < f) {
                    i3 = i;
                } else {
                    f2 = f;
                    i3 = i2;
                }
            }
            i++;
            i2 = i3;
            f = f2;
        }
        if (i2 == -1) {
            return this;
        }
        Object obj;
        if (this.children == null) {
            this.children = new SpatialTreeNode[3];
            obj = 1;
        } else {
            obj = null;
        }
        if (this.children[i2] == null) {
            this.children[i2] = new SpatialTreeNode(this, i2);
            if (obj != null) {
                this.singleChild = this.children[i2];
            } else {
                this.singleChild = null;
            }
        }
        return this.children[i2].findNode(fArr);
    }

    public void removeEntry(DrawListEntry drawListEntry) {
        super.removeEntry(drawListEntry);
        if (this.depthBin != -1) {
            this.spatialTree.setDrawListChanged();
        }
        if (getFirst() == null) {
            this.spatialTree.removeEntry(this);
            if (isEmpty()) {
                removeFromParent();
                return;
            }
        }
        shrinkBoundingBox();
    }

    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry);
    }

    public int walkTree(FrustrumPlanes frustrumPlanes, int i, float[] fArr) {
        int i2 = 0;
        if (this.singleChild != null && getFirst() == null) {
            return this.singleChild.walkTree(frustrumPlanes, i, fArr);
        }
        int i3;
        if (i != -1) {
            i = frustrumPlanes.testBoundingBox(this.position, fArr);
        }
        if (i == -1) {
            this.spatialTree.removeEntry(this);
            i3 = 0;
        } else {
            i3 = 1;
            if (getFirst() != null) {
                this.spatialTree.setEntryDepth(this, fArr[0]);
            } else {
                this.spatialTree.removeEntry(this);
            }
        }
        if (this.children != null) {
            SpatialTreeNode[] spatialTreeNodeArr = this.children;
            int length = spatialTreeNodeArr.length;
            while (i2 < length) {
                SpatialTreeNode spatialTreeNode = spatialTreeNodeArr[i2];
                if (spatialTreeNode != null) {
                    i3 += spatialTreeNode.walkTree(frustrumPlanes, i, fArr);
                }
                i2++;
            }
        }
        return i3;
    }
}
