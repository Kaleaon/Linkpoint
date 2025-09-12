// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.utils.InlineList;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            SpatialTreeNode, MyAvatarTreeNode, DrawListAvatarEntry, DrawListEntry, 
//            SpatialObjectIndex, DrawList, FrustrumPlanes

class SpatialTree
{

    static final int INVALID_BIN = -1;
    private final SpatialTreeNode bins[];
    private final float depthBuf[] = new float[1];
    private float drawDistance;
    private boolean drawListChanged;
    private final MyAvatarTreeNode myAvatarTreeNode = new MyAvatarTreeNode(this);
    private final int numBins;
    private final SpatialTreeNode rootNode;
    final SpatialObjectIndex spatialObjectIndex;
    private boolean treeWalkNeeded;

    SpatialTree(int i, float f, float f1, float f2, SpatialObjectIndex spatialobjectindex)
    {
        drawDistance = 1.0F;
        treeWalkNeeded = false;
        drawListChanged = false;
        numBins = i;
        bins = new SpatialTreeNode[i];
        spatialObjectIndex = spatialobjectindex;
        rootNode = new SpatialTreeNode(this, f, f1, f2);
    }

    private InlineList getNodeForObject(DrawListEntry drawlistentry)
    {
        if ((drawlistentry instanceof DrawListAvatarEntry) && ((DrawListAvatarEntry)drawlistentry).getObjectAvatarInfo().isMyAvatar())
        {
            return myAvatarTreeNode;
        } else
        {
            return rootNode.findNode(drawlistentry.boundingBox);
        }
    }

    private void setEntryBin(SpatialTreeNode spatialtreenode, int i)
    {
        if (i != spatialtreenode.depthBin)
        {
            if (spatialtreenode.depthBin != -1)
            {
                if (spatialtreenode.prevDepth != null)
                {
                    spatialtreenode.prevDepth.nextDepth = spatialtreenode.nextDepth;
                } else
                {
                    bins[spatialtreenode.depthBin] = spatialtreenode.nextDepth;
                }
                if (spatialtreenode.nextDepth != null)
                {
                    spatialtreenode.nextDepth.prevDepth = spatialtreenode.prevDepth;
                }
                spatialtreenode.prevDepth = null;
                spatialtreenode.nextDepth = null;
            }
            if (i != -1)
            {
                spatialtreenode.nextDepth = bins[i];
                spatialtreenode.prevDepth = null;
                if (spatialtreenode.nextDepth != null)
                {
                    spatialtreenode.nextDepth.prevDepth = spatialtreenode;
                }
                bins[i] = spatialtreenode;
            }
            spatialtreenode.depthBin = i;
            if (spatialtreenode.getFirst() != null)
            {
                setDrawListChanged();
            }
        }
    }

    void addDrawables(DrawList drawlist)
    {
        Debug.Printf("SpatialTree: adding drawables.", new Object[0]);
        myAvatarTreeNode.addDrawables(drawlist);
        SpatialTreeNode aspatialtreenode[] = bins;
        int j = aspatialtreenode.length;
        for (int i = 0; i < j; i++)
        {
            for (SpatialTreeNode spatialtreenode = aspatialtreenode[i]; spatialtreenode != null; spatialtreenode = spatialtreenode.nextDepth)
            {
                spatialtreenode.addDrawables(drawlist);
            }

        }

        drawListChanged = false;
    }

    final boolean isDrawListChanged()
    {
        return drawListChanged;
    }

    final boolean isTreeWalkNeeded()
    {
        return treeWalkNeeded;
    }

    final void removeEntry(SpatialTreeNode spatialtreenode)
    {
        setEntryBin(spatialtreenode, -1);
    }

    void removeObject(DrawListEntry drawlistentry)
    {
        InlineList inlinelist = drawlistentry.getList();
        if (inlinelist != null)
        {
            inlinelist.removeEntry(drawlistentry);
        }
    }

    final void setDrawListChanged()
    {
        drawListChanged = true;
    }

    final void setEntryDepth(SpatialTreeNode spatialtreenode, float f)
    {
        int i = 0;
        int j = Math.round(((float)numBins * f) / drawDistance);
        if (j >= 0)
        {
            if (j >= numBins)
            {
                i = numBins - 1;
            } else
            {
                i = j;
            }
        }
        setEntryBin(spatialtreenode, i);
    }

    final void setTreeWalkNeeded()
    {
        treeWalkNeeded = true;
    }

    void updateObject(DrawListEntry drawlistentry)
    {
        InlineList inlinelist = getNodeForObject(drawlistentry);
        InlineList inlinelist1 = drawlistentry.getList();
        if (inlinelist != inlinelist1 && inlinelist1 != null)
        {
            inlinelist1.removeEntry(drawlistentry);
        }
        if (inlinelist != null)
        {
            inlinelist.addEntry(drawlistentry);
        }
    }

    void walkTree(FrustrumPlanes frustrumplanes, float f)
    {
        Debug.Printf("SpatialTree: walkTree: starting to walk.", new Object[0]);
        drawDistance = f;
        rootNode.walkTree(frustrumplanes, 1, depthBuf);
        treeWalkNeeded = false;
    }
}
