// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineListEntry;
import com.lumiyaviewer.lumiya.utils.InlineList;

public class SpatialTreeNode extends InlineList<DrawListEntry>
{
    private static final float MIN_SIZE = 2.0f;
    private SpatialTreeNode[] children;
    int depthBin;
    private final int indexInParent;
    private final boolean leaf;
    SpatialTreeNode nextDepth;
    private final SpatialTreeNode parent;
    final float[] position;
    SpatialTreeNode prevDepth;
    private SpatialTreeNode singleChild;
    private final SpatialTree spatialTree;
    private final int splitAxis;
    
    public SpatialTreeNode(final SpatialTree spatialTree, final float n, final float n2, final float n3) {
        this.children = null;
        this.singleChild = null;
        this.depthBin = -1;
        this.prevDepth = null;
        this.nextDepth = null;
        this.spatialTree = spatialTree;
        this.position = new float[] { 0.0f, 0.0f, 0.0f, n, n2, n3, 0.0f, 0.0f, 0.0f, n, n2, n3 };
        this.leaf = false;
        this.parent = null;
        this.indexInParent = 0;
        this.splitAxis = this.longestAxis();
    }
    
    public SpatialTreeNode(final SpatialTreeNode parent, final int indexInParent) {
        this.children = null;
        this.singleChild = null;
        this.depthBin = -1;
        this.prevDepth = null;
        this.nextDepth = null;
        this.spatialTree = parent.spatialTree;
        this.position = new float[12];
        this.parent = parent;
        this.indexInParent = indexInParent;
        boolean leaf = true;
        for (int i = 0; i < 3; ++i) {
            float n = parent.position[i + 6];
            float n2 = parent.position[i + 9] - n;
            if (i == parent.splitAxis) {
                n2 /= 2.0f;
                n += n2 / 2.0f * indexInParent;
            }
            this.position[i + 6] = n;
            this.position[i + 9] = n + n2;
            this.position[i] = n2 / 2.0f + n;
            this.position[i + 3] = n + n2 / 2.0f;
            if (n2 > 2.0f) {
                leaf = false;
            }
        }
        this.leaf = leaf;
        this.splitAxis = this.longestAxis();
    }
    
    private void enlargeForBoundingBox(final boolean b, final float[] array) {
        if (this.parent != null) {
            int i = 0;
            boolean b2 = false;
            while (i < 3) {
                if (b || array[i] < this.position[i]) {
                    this.position[i] = array[i];
                    b2 = true;
                }
                if (b || array[i + 3] > this.position[i + 3]) {
                    this.position[i + 3] = array[i + 3];
                    b2 = true;
                }
                ++i;
            }
            if (b2) {
                this.spatialTree.setTreeWalkNeeded();
                this.parent.enlargeForBoundingBox(false, this.position);
            }
        }
    }
    
    private boolean isEmpty() {
        boolean b = false;
        if (this.getFirst() == null) {
            b = b;
            if (this.children == null) {
                b = true;
            }
        }
        return b;
    }
    
    private int longestAxis() {
        int i = 0;
        float n = 0.0f;
        int n2 = 0;
        while (i < 3) {
            final float n3 = this.position[i + 9] - this.position[i + 6];
            if (n3 > n) {
                n2 = i;
                n = n3;
            }
            ++i;
        }
        return n2;
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
                }
                else {
                    this.parent.shrinkBoundingBox();
                }
            }
            else {
                int n = 0;
                SpatialTreeNode spatialTreeNode = null;
                SpatialTreeNode singleChild;
                while (true) {
                    singleChild = spatialTreeNode;
                    if (n >= 3) {
                        break;
                    }
                    SpatialTreeNode spatialTreeNode2 = spatialTreeNode;
                    if (this.parent.children[n] != null) {
                        if (spatialTreeNode != null) {
                            singleChild = null;
                            break;
                        }
                        spatialTreeNode2 = this.parent.children[n];
                    }
                    ++n;
                    spatialTreeNode = spatialTreeNode2;
                }
                this.parent.singleChild = singleChild;
                if (this.parent.getFirst() == null) {
                    this.spatialTree.removeEntry(this.parent);
                }
                this.parent.shrinkBoundingBox();
            }
        }
    }
    
    private void shrinkBoundingBox() {
        final int n = 1;
        if (this.parent != null) {
            final float[] array = new float[6];
            DrawListEntry next = this.getFirst();
            int n2 = 0;
            while (next != null) {
                for (int i = 0; i < 3; ++i) {
                    float min;
                    if (n2 != 0) {
                        min = Math.min(array[i], next.boundingBox[i]);
                    }
                    else {
                        min = next.boundingBox[i];
                    }
                    array[i] = min;
                    float max;
                    if (n2 != 0) {
                        max = Math.max(array[i + 3], next.boundingBox[i + 3]);
                    }
                    else {
                        max = next.boundingBox[i + 3];
                    }
                    array[i + 3] = max;
                }
                next = next.getNext();
                n2 = 1;
            }
            int n3 = n2;
            if (this.children != null) {
                final SpatialTreeNode[] children = this.children;
                final int length = children.length;
                int n4 = 0;
                while (true) {
                    n3 = n2;
                    if (n4 >= length) {
                        break;
                    }
                    final SpatialTreeNode spatialTreeNode = children[n4];
                    if (spatialTreeNode != null) {
                        for (int j = 0; j < 3; ++j) {
                            float min2;
                            if (n2 != 0) {
                                min2 = Math.min(array[j], spatialTreeNode.position[j]);
                            }
                            else {
                                min2 = spatialTreeNode.position[j];
                            }
                            array[j] = min2;
                            float max2;
                            if (n2 != 0) {
                                max2 = Math.max(array[j + 3], spatialTreeNode.position[j + 3]);
                            }
                            else {
                                max2 = spatialTreeNode.position[j + 3];
                            }
                            array[j + 3] = max2;
                        }
                        n2 = 1;
                    }
                    ++n4;
                }
            }
            if (n3 != 0) {
                int k = 0;
                while (true) {
                    while (k < 6) {
                        if (this.position[k] != array[k]) {
                            final int n5 = n;
                            if (n5 != 0) {
                                System.arraycopy(array, 0, this.position, 0, 6);
                                this.parent.shrinkBoundingBox();
                                this.spatialTree.setTreeWalkNeeded();
                            }
                            return;
                        }
                        else {
                            ++k;
                        }
                    }
                    final int n5 = 0;
                    continue;
                }
            }
        }
    }
    
    public void addDrawables(final DrawList list) {
        for (DrawListEntry next = this.getFirst(); next != null; next = next.getNext()) {
            next.addToDrawList(list);
        }
    }
    
    @Override
    public void addEntry(final DrawListEntry drawListEntry) {
        boolean b = true;
        final boolean b2 = this.getFirst() == null && this.children == null;
        if (this.singleChild == null) {
            b = false;
        }
        if (drawListEntry.getList() != this) {
            super.addEntry(drawListEntry);
            this.enlargeForBoundingBox(b2, drawListEntry.boundingBox);
            if (b2 || b) {
                this.spatialTree.setTreeWalkNeeded();
            }
            if (this.depthBin != -1) {
                this.spatialTree.setDrawListChanged();
            }
        }
        else {
            this.shrinkBoundingBox();
        }
    }
    
    protected SpatialTreeNode findNode(final float[] array) {
        if (this.leaf) {
            return this;
        }
        float n = Float.POSITIVE_INFINITY;
        int i = 0;
        int n2 = -1;
        while (i < 3) {
            final float n3 = (this.position[this.splitAxis + 6 + 3] - this.position[this.splitAxis + 6]) / 2.0f;
            final float n4 = this.position[this.splitAxis + 6] + n3 / 2.0f * i;
            if (array[this.splitAxis] >= n4 && array[this.splitAxis + 3] <= n4 + n3) {
                final float abs = Math.abs(n3 / 2.0f + n4 - (array[this.splitAxis] + array[this.splitAxis + 3]) / 2.0f);
                if (abs < n) {
                    n2 = i;
                    n = abs;
                }
            }
            ++i;
        }
        if (n2 != -1) {
            int n5;
            if (this.children == null) {
                this.children = new SpatialTreeNode[3];
                n5 = 1;
            }
            else {
                n5 = 0;
            }
            if (this.children[n2] == null) {
                this.children[n2] = new SpatialTreeNode(this, n2);
                if (n5 != 0) {
                    this.singleChild = this.children[n2];
                }
                else {
                    this.singleChild = null;
                }
            }
            return this.children[n2].findNode(array);
        }
        return this;
    }
    
    @Override
    public void removeEntry(final DrawListEntry drawListEntry) {
        super.removeEntry(drawListEntry);
        if (this.depthBin != -1) {
            this.spatialTree.setDrawListChanged();
        }
        if (this.getFirst() == null) {
            this.spatialTree.removeEntry(this);
            if (this.isEmpty()) {
                this.removeFromParent();
                return;
            }
        }
        this.shrinkBoundingBox();
    }
    
    @Override
    public void requestEntryRemoval(final DrawListEntry drawListEntry) {
        this.spatialTree.spatialObjectIndex.requestEntryRemoval(drawListEntry);
    }
    
    public int walkTree(final FrustrumPlanes frustrumPlanes, int n, final float[] array) {
        int n2 = 0;
        if (this.singleChild != null && this.getFirst() == null) {
            return this.singleChild.walkTree(frustrumPlanes, n, array);
        }
        int testBoundingBox;
        if ((testBoundingBox = n) != -1) {
            testBoundingBox = frustrumPlanes.testBoundingBox(this.position, array);
        }
        if (testBoundingBox == -1) {
            this.spatialTree.removeEntry(this);
            n = 0;
        }
        else {
            n = 1;
            if (this.getFirst() != null) {
                this.spatialTree.setEntryDepth(this, array[0]);
            }
            else {
                this.spatialTree.removeEntry(this);
            }
        }
        int n3 = n;
        if (this.children != null) {
            final SpatialTreeNode[] children = this.children;
            final int length = children.length;
            while (true) {
                n3 = n;
                if (n2 >= length) {
                    break;
                }
                final SpatialTreeNode spatialTreeNode = children[n2];
                int n4 = n;
                if (spatialTreeNode != null) {
                    n4 = n + spatialTreeNode.walkTree(frustrumPlanes, testBoundingBox, array);
                }
                ++n2;
                n = n4;
            }
        }
        return n3;
    }
}
