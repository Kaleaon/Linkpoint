// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            LinkedTreeNode

public static class isFirst
    implements Iterator
{

    private boolean isFirst;
    private LinkedTreeNode node;

    public boolean hasNext()
    {
        if (isFirst)
        {
            return LinkedTreeNode._2D_get1(node) != null;
        }
        return LinkedTreeNode._2D_get2(node) != null;
    }

    public Object next()
    {
        if (node == null)
        {
            throw new NoSuchElementException();
        }
        if (isFirst)
        {
            node = LinkedTreeNode._2D_get1(node);
            isFirst = false;
        } else
        {
            node = LinkedTreeNode._2D_get2(node);
        }
        if (node == null)
        {
            throw new NoSuchElementException();
        } else
        {
            return LinkedTreeNode._2D_get0(node);
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException("remove() not supported by LinkedTreeNode");
    }

    public (LinkedTreeNode linkedtreenode)
    {
        node = linkedtreenode;
        isFirst = true;
    }
}
