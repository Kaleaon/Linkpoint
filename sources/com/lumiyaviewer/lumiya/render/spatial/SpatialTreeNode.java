// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            SpatialTree, DrawListEntry, SpatialObjectIndex, FrustrumPlanes, 
//            DrawList

public class SpatialTreeNode extends InlineList
{

    private static final float MIN_SIZE = 2F;
    private SpatialTreeNode children[];
    int depthBin;
    private final int indexInParent;
    private final boolean leaf;
    SpatialTreeNode nextDepth;
    private final SpatialTreeNode parent;
    final float position[];
    SpatialTreeNode prevDepth;
    private SpatialTreeNode singleChild;
    private final SpatialTree spatialTree;
    private final int splitAxis;

    public SpatialTreeNode(SpatialTree spatialtree, float f, float f1, float f2)
    {
        children = null;
        singleChild = null;
        depthBin = -1;
        prevDepth = null;
        nextDepth = null;
        spatialTree = spatialtree;
        position = (new float[] {
            0.0F, 0.0F, 0.0F, f, f1, f2, 0.0F, 0.0F, 0.0F, f, 
            f1, f2
        });
        leaf = false;
        parent = null;
        indexInParent = 0;
        splitAxis = longestAxis();
    }

    public SpatialTreeNode(SpatialTreeNode spatialtreenode, int i)
    {
        children = null;
        singleChild = null;
        depthBin = -1;
        prevDepth = null;
        nextDepth = null;
        spatialTree = spatialtreenode.spatialTree;
        position = new float[12];
        parent = spatialtreenode;
        indexInParent = i;
        boolean flag = true;
        int j = 0;
        while (j < 3) 
        {
            float f1 = spatialtreenode.position[j + 6];
            float f = spatialtreenode.position[j + 9] - f1;
            if (j == spatialtreenode.splitAxis)
            {
                f /= 2.0F;
                f1 += (f / 2.0F) * (float)i;
            }
            position[j + 6] = f1;
            position[j + 9] = f1 + f;
            position[j] = f / 2.0F + f1;
            position[j + 3] = f1 + f / 2.0F;
            if (f > 2.0F)
            {
                flag = false;
            }
            j++;
        }
        leaf = flag;
        splitAxis = longestAxis();
    }

    private void enlargeForBoundingBox(boolean flag, float af[])
    {
        if (parent != null)
        {
            int i = 0;
            boolean flag1 = false;
            for (; i < 3; i++)
            {
                if (flag || af[i] < position[i])
                {
                    position[i] = af[i];
                    flag1 = true;
                }
                if (flag || af[i + 3] > position[i + 3])
                {
                    position[i + 3] = af[i + 3];
                    flag1 = true;
                }
            }

            if (flag1)
            {
                spatialTree.setTreeWalkNeeded();
                parent.enlargeForBoundingBox(false, position);
            }
        }
    }

    private boolean isEmpty()
    {
        boolean flag1 = false;
        boolean flag = flag1;
        if (getFirst() == null)
        {
            flag = flag1;
            if (children == null)
            {
                flag = true;
            }
        }
        return flag;
    }

    private int longestAxis()
    {
        int i = 0;
        float f = 0.0F;
        int j = 0;
        for (; i < 3; i++)
        {
            float f1 = position[i + 9] - position[i + 6];
            if (f1 > f)
            {
                j = i;
                f = f1;
            }
        }

        return j;
    }

    private void removeFromParent()
    {
label0:
        {
label1:
            {
                spatialTree.removeEntry(this);
                if (parent != null && parent.children != null)
                {
                    parent.children[indexInParent] = null;
                    if (parent.singleChild != this)
                    {
                        break label0;
                    }
                    parent.singleChild = null;
                    parent.children = null;
                    if (!parent.isEmpty())
                    {
                        break label1;
                    }
                    parent.removeFromParent();
                }
                return;
            }
            parent.shrinkBoundingBox();
            return;
        }
        SpatialTreeNode spatialtreenode1;
label2:
        {
            int i = 0;
            SpatialTreeNode spatialtreenode = null;
            do
            {
                spatialtreenode1 = spatialtreenode;
                if (i >= 3)
                {
                    break label2;
                }
                spatialtreenode1 = spatialtreenode;
                if (parent.children[i] != null)
                {
                    if (spatialtreenode != null)
                    {
                        break;
                    }
                    spatialtreenode1 = parent.children[i];
                }
                i++;
                spatialtreenode = spatialtreenode1;
            } while (true);
            spatialtreenode1 = null;
        }
        parent.singleChild = spatialtreenode1;
        if (parent.getFirst() == null)
        {
            spatialTree.removeEntry(parent);
        }
        parent.shrinkBoundingBox();
    }

    private void shrinkBoundingBox()
    {
        boolean flag1 = true;
        if (parent == null) goto _L2; else goto _L1
_L1:
        float af[];
        int l;
        af = new float[6];
        DrawListEntry drawlistentry = (DrawListEntry)getFirst();
        boolean flag;
        for (flag = false; drawlistentry != null; flag = true)
        {
            int j = 0;
            while (j < 3) 
            {
                float f;
                if (flag)
                {
                    f = Math.min(af[j], drawlistentry.boundingBox[j]);
                } else
                {
                    f = drawlistentry.boundingBox[j];
                }
                af[j] = f;
                if (flag)
                {
                    f = Math.max(af[j + 3], drawlistentry.boundingBox[j + 3]);
                } else
                {
                    f = drawlistentry.boundingBox[j + 3];
                }
                af[j + 3] = f;
                j++;
            }
            drawlistentry = drawlistentry.getNext();
        }

        l = ((flag) ? 1 : 0);
        if (children != null)
        {
            SpatialTreeNode aspatialtreenode[] = children;
            int i1 = aspatialtreenode.length;
            int k = 0;
            do
            {
                l = ((flag) ? 1 : 0);
                if (k >= i1)
                {
                    break;
                }
                SpatialTreeNode spatialtreenode = aspatialtreenode[k];
                if (spatialtreenode != null)
                {
                    l = 0;
                    while (l < 3) 
                    {
                        float f1;
                        if (flag)
                        {
                            f1 = Math.min(af[l], spatialtreenode.position[l]);
                        } else
                        {
                            f1 = spatialtreenode.position[l];
                        }
                        af[l] = f1;
                        if (flag)
                        {
                            f1 = Math.max(af[l + 3], spatialtreenode.position[l + 3]);
                        } else
                        {
                            f1 = spatialtreenode.position[l + 3];
                        }
                        af[l + 3] = f1;
                        l++;
                    }
                    flag = true;
                }
                k++;
            } while (true);
        }
        if (l == 0) goto _L2; else goto _L3
_L3:
        int i = 0;
_L6:
        if (i >= 6)
        {
            break MISSING_BLOCK_LABEL_373;
        }
        if (position[i] == af[i]) goto _L5; else goto _L4
_L4:
        i = ((flag1) ? 1 : 0);
_L7:
        if (i != 0)
        {
            System.arraycopy(af, 0, position, 0, 6);
            parent.shrinkBoundingBox();
            spatialTree.setTreeWalkNeeded();
        }
_L2:
        return;
_L5:
        i++;
          goto _L6
        i = 0;
          goto _L7
    }

    public void addDrawables(DrawList drawlist)
    {
        for (DrawListEntry drawlistentry = (DrawListEntry)getFirst(); drawlistentry != null; drawlistentry = drawlistentry.getNext())
        {
            drawlistentry.addToDrawList(drawlist);
        }

    }

    public void addEntry(DrawListEntry drawlistentry)
    {
        boolean flag = true;
        boolean flag1;
        if (getFirst() == null && children == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (singleChild == null)
        {
            flag = false;
        }
        if (drawlistentry.getList() != this)
        {
            super.addEntry(drawlistentry);
            enlargeForBoundingBox(flag1, drawlistentry.boundingBox);
            if (flag1 || flag)
            {
                spatialTree.setTreeWalkNeeded();
            }
            if (depthBin != -1)
            {
                spatialTree.setDrawListChanged();
            }
            return;
        } else
        {
            shrinkBoundingBox();
            return;
        }
    }

    public volatile void addEntry(InlineListEntry inlinelistentry)
    {
        addEntry((DrawListEntry)inlinelistentry);
    }

    protected SpatialTreeNode findNode(float af[])
    {
        if (leaf)
        {
            return this;
        }
        float f = (1.0F / 0.0F);
        int j = 0;
        int i = -1;
        for (; j < 3; j++)
        {
            float f1 = (position[splitAxis + 6 + 3] - position[splitAxis + 6]) / 2.0F;
            float f2 = position[splitAxis + 6] + (f1 / 2.0F) * (float)j;
            if (af[splitAxis] < f2 || af[splitAxis + 3] > f2 + f1)
            {
                continue;
            }
            f1 = Math.abs((f1 / 2.0F + f2) - (af[splitAxis] + af[splitAxis + 3]) / 2.0F);
            if (f1 < f)
            {
                i = j;
                f = f1;
            }
        }

        if (i != -1)
        {
            boolean flag;
            if (children == null)
            {
                children = new SpatialTreeNode[3];
                flag = true;
            } else
            {
                flag = false;
            }
            if (children[i] == null)
            {
                children[i] = new SpatialTreeNode(this, i);
                if (flag)
                {
                    singleChild = children[i];
                } else
                {
                    singleChild = null;
                }
            }
            return children[i].findNode(af);
        } else
        {
            return this;
        }
    }

    public void removeEntry(DrawListEntry drawlistentry)
    {
        super.removeEntry(drawlistentry);
        if (depthBin != -1)
        {
            spatialTree.setDrawListChanged();
        }
        if (getFirst() == null)
        {
            spatialTree.removeEntry(this);
            if (isEmpty())
            {
                removeFromParent();
                return;
            }
        }
        shrinkBoundingBox();
    }

    public volatile void removeEntry(InlineListEntry inlinelistentry)
    {
        removeEntry((DrawListEntry)inlinelistentry);
    }

    public void requestEntryRemoval(DrawListEntry drawlistentry)
    {
        spatialTree.spatialObjectIndex.requestEntryRemoval(drawlistentry);
    }

    public volatile void requestEntryRemoval(InlineListEntry inlinelistentry)
    {
        requestEntryRemoval((DrawListEntry)inlinelistentry);
    }

    public int walkTree(FrustrumPlanes frustrumplanes, int i, float af[])
    {
        int k = 0;
        if (singleChild != null && getFirst() == null)
        {
            return singleChild.walkTree(frustrumplanes, i, af);
        }
        int j = i;
        if (i != -1)
        {
            j = frustrumplanes.testBoundingBox(position, af);
        }
        int l;
        if (j == -1)
        {
            spatialTree.removeEntry(this);
            i = 0;
        } else
        {
            i = 1;
            if (getFirst() != null)
            {
                spatialTree.setEntryDepth(this, af[0]);
            } else
            {
                spatialTree.removeEntry(this);
            }
        }
        l = i;
        if (children != null)
        {
            SpatialTreeNode aspatialtreenode[] = children;
            int i1 = aspatialtreenode.length;
            do
            {
                l = i;
                if (k >= i1)
                {
                    break;
                }
                SpatialTreeNode spatialtreenode = aspatialtreenode[k];
                l = i;
                if (spatialtreenode != null)
                {
                    l = i + spatialtreenode.walkTree(frustrumplanes, j, af);
                }
                k++;
                i = l;
            } while (true);
        }
        return l;
    }
}
