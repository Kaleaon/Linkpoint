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

    public SpatialTreeNode(SpatialTree spatialTree2, float f, float f2, float f3) {
        this.spatialTree = spatialTree2;
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
            boolean z2 = false;
            for (int i = 0; i < 3; i++) {
                if (z || fArr[i] < this.position[i]) {
                    this.position[i] = fArr[i];
                    z2 = true;
                }
                if (z || fArr[i + 3] > this.position[i + 3]) {
                    this.position[i + 3] = fArr[i + 3];
                    z2 = true;
                }
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
                } else {
                    this.parent.shrinkBoundingBox();
                }
            } else {
                int i = 0;
                SpatialTreeNode spatialTreeNode = null;
                while (true) {
                    if (i >= 3) {
                        break;
                    }
                    if (this.parent.children[i] != null) {
                        if (spatialTreeNode != null) {
                            spatialTreeNode = null;
                            break;
                        }
                        spatialTreeNode = this.parent.children[i];
                    }
                    i++;
                }
                this.parent.singleChild = spatialTreeNode;
                if (this.parent.getFirst() == null) {
                    this.spatialTree.removeEntry(this.parent);
                }
                this.parent.shrinkBoundingBox();
            }
        }
    }

    private void shrinkBoundingBox() {
        boolean z;
        boolean z2 = true;
        if (this.parent != null) {
            float[] fArr = new float[6];
            DrawListEntry drawListEntry = (DrawListEntry) getFirst();
            boolean z3 = false;
            while (drawListEntry != null) {
                for (int i = 0; i < 3; i++) {
                    fArr[i] = z3 ? Math.min(fArr[i], drawListEntry.boundingBox[i]) : drawListEntry.boundingBox[i];
                    fArr[i + 3] = z3 ? Math.max(fArr[i + 3], drawListEntry.boundingBox[i + 3]) : drawListEntry.boundingBox[i + 3];
                }
                drawListEntry = drawListEntry.getNext();
                z3 = true;
            }
            if (this.children != null) {
                SpatialTreeNode[] spatialTreeNodeArr = this.children;
                int length = spatialTreeNodeArr.length;
                int i2 = 0;
                while (i2 < length) {
                    SpatialTreeNode spatialTreeNode = spatialTreeNodeArr[i2];
                    if (spatialTreeNode != null) {
                        for (int i3 = 0; i3 < 3; i3++) {
                            fArr[i3] = z3 ? Math.min(fArr[i3], spatialTreeNode.position[i3]) : spatialTreeNode.position[i3];
                            fArr[i3 + 3] = z3 ? Math.max(fArr[i3 + 3], spatialTreeNode.position[i3 + 3]) : spatialTreeNode.position[i3 + 3];
                        }
                        z = true;
                    } else {
                        z = z3;
                    }
                    i2++;
                    z3 = z;
                }
            }
            if (z3) {
                int i4 = 0;
                while (true) {
                    if (i4 >= 6) {
                        z2 = false;
                        break;
                    } else if (this.position[i4] != fArr[i4]) {
                        break;
                    } else {
                        i4++;
                    }
                }
                if (z2) {
                    System.arraycopy(fArr, 0, this.position, 0, 6);
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
        boolean z = true;
        boolean z2 = getFirst() == null && this.children == null;
        if (this.singleChild == null) {
            z = false;
        }
        if (drawListEntry.getList() != this) {
            super.addEntry(drawListEntry);
            enlargeForBoundingBox(z2, drawListEntry.boundingBox);
            if (z2 || z) {
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

    /* access modifiers changed from: protected */
    public SpatialTreeNode findNode(float[] fArr) {
        boolean z;
        float f;
        int i;
        if (this.leaf) {
            return this;
        }
        float f2 = Float.POSITIVE_INFINITY;
        int i2 = 0;
        int i3 = -1;
        while (i2 < 3) {
            float f3 = (this.position[(this.splitAxis + 6) + 3] - this.position[this.splitAxis + 6]) / MIN_SIZE;
            float f4 = this.position[this.splitAxis + 6] + ((f3 / MIN_SIZE) * ((float) i2));
            if (fArr[this.splitAxis] < f4 || fArr[this.splitAxis + 3] > f4 + f3) {
                f = f2;
                i = i3;
            } else {
                f = Math.abs(((f3 / MIN_SIZE) + f4) - ((fArr[this.splitAxis] + fArr[this.splitAxis + 3]) / MIN_SIZE));
                if (f < f2) {
                    i = i2;
                } else {
                    f = f2;
                    i = i3;
                }
            }
            i2++;
            i3 = i;
            f2 = f;
        }
        if (i3 == -1) {
            return this;
        }
        if (this.children == null) {
            this.children = new SpatialTreeNode[3];
            z = true;
        } else {
            z = false;
        }
        if (this.children[i3] == null) {
            this.children[i3] = new SpatialTreeNode(this, i3);
            if (z) {
                this.singleChild = this.children[i3];
            } else {
                this.singleChild = null;
            }
        }
        return this.children[i3].findNode(fArr);
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
        int i2;
        if (this.singleChild != null && getFirst() == null) {
            return this.singleChild.walkTree(frustrumPlanes, i, fArr);
        }
        if (i != -1) {
            i = frustrumPlanes.testBoundingBox(this.position, fArr);
        }
        if (i == -1) {
            this.spatialTree.removeEntry(this);
            i2 = 0;
        } else {
            i2 = 1;
            if (getFirst() != null) {
                this.spatialTree.setEntryDepth(this, fArr[0]);
            } else {
                this.spatialTree.removeEntry(this);
            }
        }
        if (this.children != null) {
            for (SpatialTreeNode spatialTreeNode : this.children) {
                if (spatialTreeNode != null) {
                    i2 += spatialTreeNode.walkTree(frustrumPlanes, i, fArr);
                }
            }
        }
        return i2;
    }
}
