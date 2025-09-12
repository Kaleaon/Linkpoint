// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedTreeNode
    implements Iterable
{
    public static class LinkedTreeIterator
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

        public LinkedTreeIterator(LinkedTreeNode linkedtreenode)
        {
            node = linkedtreenode;
            isFirst = true;
        }
    }


    private Object dataObject;
    private LinkedTreeNode firstChild;
    private LinkedTreeNode nextChild;
    private LinkedTreeNode parentObject;
    private LinkedTreeNode prevChild;

    static Object _2D_get0(LinkedTreeNode linkedtreenode)
    {
        return linkedtreenode.dataObject;
    }

    static LinkedTreeNode _2D_get1(LinkedTreeNode linkedtreenode)
    {
        return linkedtreenode.firstChild;
    }

    static LinkedTreeNode _2D_get2(LinkedTreeNode linkedtreenode)
    {
        return linkedtreenode.nextChild;
    }

    public LinkedTreeNode(Object obj)
    {
        dataObject = obj;
    }

    public void addChild(LinkedTreeNode linkedtreenode)
    {
        this;
        JVM INSTR monitorenter ;
        if (linkedtreenode.parentObject != this)
        {
            linkedtreenode.unlinkFromParent();
            linkedtreenode.parentObject = this;
            linkedtreenode.prevChild = null;
            linkedtreenode.nextChild = firstChild;
            firstChild = linkedtreenode;
            if (linkedtreenode.nextChild != null)
            {
                linkedtreenode.nextChild.prevChild = linkedtreenode;
            }
        }
        this;
        JVM INSTR monitorexit ;
        return;
        linkedtreenode;
        throw linkedtreenode;
    }

    public Object getDataObject()
    {
        return dataObject;
    }

    public LinkedTreeNode getFirstChild()
    {
        this;
        JVM INSTR monitorenter ;
        LinkedTreeNode linkedtreenode = firstChild;
        this;
        JVM INSTR monitorexit ;
        return linkedtreenode;
        Exception exception;
        exception;
        throw exception;
    }

    public LinkedTreeNode getNextChild()
    {
        this;
        JVM INSTR monitorenter ;
        LinkedTreeNode linkedtreenode = nextChild;
        this;
        JVM INSTR monitorexit ;
        return linkedtreenode;
        Exception exception;
        exception;
        throw exception;
    }

    public Object getParent()
    {
        this;
        JVM INSTR monitorenter ;
        Object obj;
        if (parentObject == null)
        {
            break MISSING_BLOCK_LABEL_21;
        }
        obj = parentObject.getDataObject();
        return obj;
        this;
        JVM INSTR monitorexit ;
        return null;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean hasChild(LinkedTreeNode linkedtreenode)
    {
        this;
        JVM INSTR monitorenter ;
        linkedtreenode = linkedtreenode.parentObject;
        boolean flag;
        if (linkedtreenode == this)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        this;
        JVM INSTR monitorexit ;
        return flag;
        linkedtreenode;
        throw linkedtreenode;
    }

    public boolean hasChildren()
    {
        this;
        JVM INSTR monitorenter ;
        LinkedTreeNode linkedtreenode = firstChild;
        boolean flag;
        if (linkedtreenode != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        this;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public Iterator iterator()
    {
        return new LinkedTreeIterator(this);
    }

    public void removeChild(LinkedTreeNode linkedtreenode)
    {
        this;
        JVM INSTR monitorenter ;
        if (linkedtreenode.parentObject == this)
        {
            linkedtreenode.unlinkFromParent();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        linkedtreenode;
        throw linkedtreenode;
    }

    public void unlinkFromParent()
    {
        this;
        JVM INSTR monitorenter ;
        if (parentObject == null) goto _L2; else goto _L1
_L1:
        if (prevChild == null)
        {
            break MISSING_BLOCK_LABEL_53;
        }
        prevChild.nextChild = nextChild;
_L3:
        if (nextChild != null)
        {
            nextChild.prevChild = prevChild;
        }
        parentObject = null;
_L2:
        this;
        JVM INSTR monitorexit ;
        return;
        parentObject.firstChild = nextChild;
          goto _L3
        Exception exception;
        exception;
        throw exception;
    }
}
