// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public class ChunkedList extends AbstractList
    implements RandomAccess
{
    public static interface ChunkFactory
    {

        public abstract List createEmptyChunk();
    }


    private final List chunks = new ArrayList();
    private int count;
    private List lastChunk;
    private int lastChunkIndex;
    private int lastChunkSize;
    private int lastChunkStart;

    public ChunkedList()
    {
        count = 0;
        lastChunk = null;
        lastChunkIndex = 0;
        lastChunkStart = 0;
        lastChunkSize = 0;
    }

    private void checkConsistency()
    {
        Iterator iterator = chunks.iterator();
        int i;
        for (i = 0; iterator.hasNext(); i = ((List)iterator.next()).size() + i) { }
        if (i != count)
        {
            throw new IllegalStateException(String.format("newCount %d, count %d", new Object[] {
                Integer.valueOf(i), Integer.valueOf(count)
            }));
        } else
        {
            return;
        }
    }

    private int replaceElementInChunk(List list, Object obj, Comparator comparator)
    {
        if (!list.isEmpty())
        {
            int i = Collections.binarySearch(list, obj, comparator);
            if (i >= 0)
            {
                return replaceFoundElement(list, i, obj);
            } else
            {
                return -1;
            }
        } else
        {
            return -1;
        }
    }

    private int replaceFoundElement(List list, int i, Object obj)
    {
        list.set(i, obj);
        obj = chunks.iterator();
        List list1;
        for (int j = 0; ((Iterator) (obj)).hasNext(); j = list1.size() + j)
        {
            list1 = (List)((Iterator) (obj)).next();
            if (list1 == list)
            {
                return j + i;
            }
        }

        return -1;
    }

    private void resetLastPosition()
    {
        lastChunk = null;
        checkConsistency();
    }

    private void setLastChunk(int i)
    {
        if (i < 0 || i >= count)
        {
            throw new IndexOutOfBoundsException(String.format("index %d, count %d", new Object[] {
                Integer.valueOf(i), Integer.valueOf(count)
            }));
        }
        checkConsistency();
        if (lastChunk == null)
        {
            lastChunkIndex = 0;
            lastChunkStart = 0;
            lastChunk = (List)chunks.get(lastChunkIndex);
            lastChunkSize = lastChunk.size();
        }
        for (; i < lastChunkStart; lastChunkStart = lastChunkStart - lastChunkSize)
        {
            lastChunkIndex = lastChunkIndex - 1;
            lastChunk = (List)chunks.get(lastChunkIndex);
            lastChunkSize = lastChunk.size();
        }

_L3:
        if (i >= lastChunkStart + lastChunkSize)
        {
            lastChunkIndex = lastChunkIndex + 1;
            lastChunkStart = lastChunkStart + lastChunkSize;
            if (lastChunkIndex >= chunks.size())
            {
                throw new IllegalStateException(String.format("lastChunkIndex runaway, position %d, count %d, lastChunkStart %d", new Object[] {
                    Integer.valueOf(i), Integer.valueOf(count), Integer.valueOf(lastChunkStart)
                }));
            }
        } else
        {
            return;
        }
        if (true) goto _L2; else goto _L1
_L2:
        lastChunk = (List)chunks.get(lastChunkIndex);
        lastChunkSize = lastChunk.size();
        if (true) goto _L3; else goto _L1
_L1:
    }

    public void addChunkAtEnd(List list)
    {
        chunks.add(list);
        count = count + list.size();
        resetLastPosition();
    }

    public void addChunkAtStart(List list)
    {
        chunks.add(0, list);
        count = count + list.size();
        resetLastPosition();
    }

    public void addElement(Object obj, int i, ChunkFactory chunkfactory)
    {
        List list = null;
        if (chunks.size() > 0)
        {
            list = (List)chunks.get(chunks.size() - 1);
        }
        if (list != null && list.size() < i)
        {
            list.add(obj);
            count = count + 1;
            if (lastChunk == list)
            {
                lastChunkSize = lastChunkSize + 1;
            }
        } else
        {
            chunkfactory = chunkfactory.createEmptyChunk();
            chunkfactory.add(obj);
            chunks.add(chunkfactory);
            count = count + 1;
        }
        checkConsistency();
    }

    public void clear()
    {
        chunks.clear();
        count = 0;
        resetLastPosition();
    }

    public Object get(int i)
    {
        setLastChunk(i);
        if (i >= lastChunkStart && i < lastChunkStart + lastChunkSize)
        {
            return lastChunk.get(i - lastChunkStart);
        } else
        {
            throw new IndexOutOfBoundsException(String.format("index %d, count %d", new Object[] {
                Integer.valueOf(i), Integer.valueOf(count)
            }));
        }
    }

    public int removeChunkAtEnd()
    {
        if (chunks.size() > 0)
        {
            List list = (List)chunks.remove(chunks.size() - 1);
            int i;
            if (list != null)
            {
                i = list.size();
            } else
            {
                i = 0;
            }
            count = count - i;
            resetLastPosition();
            return i;
        } else
        {
            return 0;
        }
    }

    public int removeChunkAtStart()
    {
        if (chunks.size() > 0)
        {
            List list = (List)chunks.remove(0);
            int i;
            if (list != null)
            {
                i = list.size();
            } else
            {
                i = 0;
            }
            count = count - i;
            resetLastPosition();
            return i;
        } else
        {
            return 0;
        }
    }

    public int removeElementsAfter(int i)
    {
        checkConsistency();
        if (i >= 0 && i < count)
        {
            setLastChunk(i);
            if (i >= lastChunkStart && i < lastChunkStart + lastChunkSize)
            {
                int k = lastChunkIndex;
                i = chunks.size();
                int j = 0;
                for (i--; i >= k + 2; i--)
                {
                    j += ((List)chunks.get(i)).size();
                    chunks.remove(i);
                }

                count = count - j;
                checkConsistency();
                return j;
            } else
            {
                return 0;
            }
        } else
        {
            return 0;
        }
    }

    public int removeElementsBefore(int i)
    {
        checkConsistency();
        if (i >= 0 && i < count)
        {
            setLastChunk(i);
            if (i >= lastChunkStart && i < lastChunkStart + lastChunkSize)
            {
                i = lastChunkIndex - 2;
                int j;
                if (i >= 0)
                {
                    i++;
                    j = 0;
                } else
                {
                    i = 0;
                    j = 0;
                }
                for (; i > 0; i--)
                {
                    j += ((List)chunks.get(0)).size();
                    chunks.remove(0);
                }

                count = count - j;
                resetLastPosition();
                return j;
            } else
            {
                return 0;
            }
        } else
        {
            return 0;
        }
    }

    public int replaceElement(Object obj, Comparator comparator)
    {
        if (chunks.isEmpty()) goto _L2; else goto _L1
_L1:
        int i;
        int j;
        i = chunks.size() / 2;
        j = 0;
_L15:
        List list = (List)chunks.get(i);
        if (list.isEmpty()) goto _L4; else goto _L3
_L3:
        Object obj1 = list.get(0);
        Object obj2 = list.get(list.size() - 1);
        j = comparator.compare(obj, obj1);
        if (j == 0)
        {
            return replaceFoundElement(list, 0, obj);
        }
        if (j < 0)
        {
            j = i - 1;
            if (j < 0)
            {
                return -1;
            }
            break MISSING_BLOCK_LABEL_344;
        }
        j = comparator.compare(obj, obj2);
        if (j == 0)
        {
            return replaceFoundElement(list, list.size() - 1, obj);
        }
        if (j > 0)
        {
            int k = i + 1;
            j = 1;
            i = k;
            if (k >= chunks.size())
            {
                return -1;
            }
        } else
        {
            return replaceElementInChunk(list, obj, comparator);
        }
          goto _L5
_L4:
        if (j >= 0) goto _L7; else goto _L6
_L6:
        int l = i - 1;
        i = l;
        if (l < 0)
        {
            return -1;
        }
          goto _L5
_L7:
        if (j <= 0) goto _L9; else goto _L8
_L8:
        int i1 = i + 1;
        i = i1;
        if (i1 >= chunks.size())
        {
            return -1;
        }
          goto _L5
_L9:
        i = 0;
_L14:
        if (i >= chunks.size()) goto _L11; else goto _L10
_L10:
        if (((List)chunks.get(i)).isEmpty()) goto _L13; else goto _L12
_L12:
        int j1 = i;
_L16:
        i = j1;
        if (j1 == -1)
        {
            return -1;
        }
          goto _L5
_L13:
        i++;
          goto _L14
_L5:
        j1 = j;
        j = i;
        i = j1;
_L17:
        j1 = j;
        j = i;
        i = j1;
          goto _L15
_L2:
        return -1;
_L11:
        j1 = -1;
          goto _L16
        i = -1;
          goto _L17
    }

    public int size()
    {
        return count;
    }
}
