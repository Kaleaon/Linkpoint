// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.wlist;

import com.google.common.collect.Lists;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.Identifiable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.utils.wlist:
//            ChunkedList

public class ChunkedListLoader extends AbstractList
    implements ChunkedList.ChunkFactory, RandomAccess
{
    public static interface EventListener
    {

        public abstract Executor getListEventsExecutor();

        public abstract void onListItemAddedAtEnd();

        public abstract void onListItemChanged(int i);

        public abstract void onListItemsAdded(int i, int j);

        public abstract void onListItemsRemoved(int i, int j);

        public abstract void onListReloaded();
    }

    protected static class LoadResult
    {

        public final List entries;
        final long fromId;
        final boolean hasMore;

        public LoadResult(List list, boolean flag, long l)
        {
            entries = list;
            hasMore = flag;
            fromId = l;
        }
    }


    private final Queue addedElements = new ConcurrentLinkedQueue();
    private final Comparator chatMessageComparator = new _2D_.Lambda.QDlX9uefQr1Wq8gtt1O6M2wUNME();
    private final Executor executor;
    private boolean hasAbove;
    private boolean hasBelow;
    private ChunkedList items;
    private final EventListener listener;
    private final Executor listenerExecutor;
    private LoadResult loadAboveResult;
    private long loadAboveTopmostId;
    private boolean loadAboveWanted;
    private long loadBelowLastId;
    private LoadResult loadBelowResult;
    private boolean loadBelowWanted;
    private final Runnable loadMoreData = new Runnable() {

        final ChunkedListLoader this$0;

        public void run()
        {
            boolean flag3;
            flag3 = true;
            Debug.Printf("ChatView: processing loadMoreData(), reloadRequested %b", new Object[] {
                Boolean.valueOf(ChunkedListLoader._2D_get14(ChunkedListLoader.this).get())
            });
            ChunkedListLoader._2D_get11(ChunkedListLoader.this).set(false);
            Object obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            if (!ChunkedListLoader._2D_get7(ChunkedListLoader.this) || ChunkedListLoader._2D_get5(ChunkedListLoader.this) != null) goto _L2; else goto _L1
_L1:
            boolean flag1 = ChunkedListLoader._2D_get14(ChunkedListLoader.this).get() ^ true;
_L5:
            long l = ChunkedListLoader._2D_get6(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorexit ;
            LoadResult loadresult;
            if (!flag1)
            {
                break MISSING_BLOCK_LABEL_313;
            }
            loadresult = loadInBackground(ChunkedListLoader._2D_get17(ChunkedListLoader.this), l, false);
            obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            ChunkedListLoader._2D_set2(ChunkedListLoader.this, loadresult);
            obj;
            JVM INSTR monitorexit ;
            flag1 = true;
_L7:
            obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            if (!ChunkedListLoader._2D_get10(ChunkedListLoader.this) || ChunkedListLoader._2D_get9(ChunkedListLoader.this) != null) goto _L4; else goto _L3
_L3:
            boolean flag2 = ChunkedListLoader._2D_get14(ChunkedListLoader.this).get() ^ true;
_L6:
            l = ChunkedListLoader._2D_get8(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorexit ;
            if (!flag2)
            {
                break MISSING_BLOCK_LABEL_239;
            }
            loadresult = loadInBackground(ChunkedListLoader._2D_get17(ChunkedListLoader.this), l, true);
            obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            ChunkedListLoader._2D_set4(ChunkedListLoader.this, loadresult);
            obj;
            JVM INSTR monitorexit ;
            flag1 = true;
            if (ChunkedListLoader._2D_get14(ChunkedListLoader.this).getAndSet(false))
            {
                ChunkedListLoader._2D_get13(ChunkedListLoader.this).set(true);
                flag1 = flag3;
            }
            if (flag1)
            {
                ChunkedListLoader._2D_wrap0(ChunkedListLoader.this);
            }
            return;
_L2:
            flag1 = false;
              goto _L5
            Exception exception;
            exception;
            throw exception;
            exception;
            throw exception;
_L4:
            flag2 = false;
              goto _L6
            exception;
            throw exception;
            exception;
            throw exception;
            flag1 = false;
              goto _L7
        }

            
            {
                this$0 = ChunkedListLoader.this;
                super();
            }
    };
    private final AtomicBoolean loadRequested = new AtomicBoolean();
    private final Object lock = new Object();
    private final Runnable processUpdate = new Runnable() {

        final ChunkedListLoader this$0;

        public void run()
        {
            ChunkedListLoader._2D_get15(ChunkedListLoader.this).set(false);
            Debug.Printf("ChatView: processUpdate, reloadAccepted: %b", new Object[] {
                Boolean.valueOf(ChunkedListLoader._2D_get13(ChunkedListLoader.this).get())
            });
            if (!ChunkedListLoader._2D_get13(ChunkedListLoader.this).getAndSet(false)) goto _L2; else goto _L1
_L1:
            Object obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            ChunkedListLoader._2D_set3(ChunkedListLoader.this, false);
            ChunkedListLoader._2D_set2(ChunkedListLoader.this, null);
            ChunkedListLoader._2D_set4(ChunkedListLoader.this, null);
            ChunkedListLoader._2D_set5(ChunkedListLoader.this, false);
            obj;
            JVM INSTR monitorexit ;
            ChunkedListLoader._2D_set0(ChunkedListLoader.this, true);
            ChunkedListLoader._2D_set1(ChunkedListLoader.this, true);
            ChunkedListLoader._2D_get0(ChunkedListLoader.this).clear();
            ChunkedListLoader._2D_get3(ChunkedListLoader.this).clear();
            ChunkedListLoader._2D_get4(ChunkedListLoader.this).onListReloaded();
_L4:
            return;
            Exception exception;
            exception;
            throw exception;
_L2:
            obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            Object obj1 = ChunkedListLoader._2D_get5(ChunkedListLoader.this);
            if (obj1 == null)
            {
                break MISSING_BLOCK_LABEL_196;
            }
            ChunkedListLoader._2D_set2(ChunkedListLoader.this, null);
            ChunkedListLoader._2D_set3(ChunkedListLoader.this, false);
            obj;
            JVM INSTR monitorexit ;
            Exception exception1;
            Iterator iterator;
            int j;
            int l;
            boolean flag1;
            int i1;
            long l1;
            if (obj1 != null)
            {
                int k = ((LoadResult) (obj1)).entries.size() + 0;
                ChunkedListLoader._2D_get3(ChunkedListLoader.this).addChunkAtStart(Lists.reverse(((LoadResult) (obj1)).entries));
                ChunkedListLoader._2D_set0(ChunkedListLoader.this, ((LoadResult) (obj1)).hasMore);
                j = k;
                if (((LoadResult) (obj1)).fromId == 0x7fffffffffffffffL)
                {
                    ChunkedListLoader._2D_set1(ChunkedListLoader.this, false);
                    j = k;
                }
            } else
            {
                j = 0;
            }
            if (j != 0)
            {
                ChunkedListLoader._2D_get4(ChunkedListLoader.this).onListItemsAdded(0, j);
            }
            i1 = ChunkedListLoader._2D_get3(ChunkedListLoader.this).size();
            obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
            obj;
            JVM INSTR monitorenter ;
            obj1 = ChunkedListLoader._2D_get9(ChunkedListLoader.this);
            if (obj1 == null)
            {
                break MISSING_BLOCK_LABEL_344;
            }
            ChunkedListLoader._2D_set4(ChunkedListLoader.this, null);
            ChunkedListLoader._2D_set5(ChunkedListLoader.this, false);
            obj;
            JVM INSTR monitorexit ;
            if (obj1 != null)
            {
                l = ((LoadResult) (obj1)).entries.size();
                ChunkedListLoader._2D_get3(ChunkedListLoader.this).addChunkAtEnd(((LoadResult) (obj1)).entries);
                ChunkedListLoader._2D_set1(ChunkedListLoader.this, ((LoadResult) (obj1)).hasMore);
                j = l;
                if (((LoadResult) (obj1)).fromId == 0L)
                {
                    ChunkedListLoader._2D_set0(ChunkedListLoader.this, false);
                    j = l;
                }
            } else
            {
                j = 0;
            }
            flag1 = false;
            do
            {
                obj = (Identifiable)ChunkedListLoader._2D_get0(ChunkedListLoader.this).poll();
                if (obj == null)
                {
                    break;
                }
                if (ChunkedListLoader._2D_get3(ChunkedListLoader.this).size() > 0)
                {
                    l1 = ((Long)((Identifiable)ChunkedListLoader._2D_get3(ChunkedListLoader.this).get(ChunkedListLoader._2D_get3(ChunkedListLoader.this).size() - 1)).getId()).longValue();
                } else
                {
                    l1 = -1L;
                }
                Debug.Printf("ChatView: added element: id %d, lastId %d, hasBelow %b", new Object[] {
                    ((Identifiable) (obj)).getId(), Long.valueOf(l1), Boolean.valueOf(ChunkedListLoader._2D_get2(ChunkedListLoader.this))
                });
                if (!ChunkedListLoader._2D_get2(ChunkedListLoader.this) && ((Long)((Identifiable) (obj)).getId()).longValue() > l1)
                {
                    ChunkedListLoader._2D_get3(ChunkedListLoader.this).addElement(obj, ChunkedListLoader._2D_get17(ChunkedListLoader.this), ChunkedListLoader.this);
                    flag1 = true;
                    j++;
                }
            } while (true);
            break MISSING_BLOCK_LABEL_609;
            exception1;
            throw exception1;
            exception1;
            throw exception1;
            if (j != 0)
            {
                ChunkedListLoader._2D_get4(ChunkedListLoader.this).onListItemsAdded(i1, j);
            }
            if (flag1)
            {
                ChunkedListLoader._2D_get4(ChunkedListLoader.this).onListItemAddedAtEnd();
            }
_L5:
            exception1 = ((Exception) (ChunkedListLoader._2D_get12(ChunkedListLoader.this)));
            exception1;
            JVM INSTR monitorenter ;
            iterator = ChunkedListLoader._2D_get16(ChunkedListLoader.this).entrySet().iterator();
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_778;
            }
            obj = (Identifiable)((java.util.Map.Entry)iterator.next()).getValue();
            iterator.remove();
_L6:
            exception1;
            JVM INSTR monitorexit ;
            if (obj == null) goto _L4; else goto _L3
_L3:
            j = ChunkedListLoader._2D_get3(ChunkedListLoader.this).replaceElement(obj, ChunkedListLoader._2D_get1(ChunkedListLoader.this));
            Debug.Printf("ChunkedListLoader: replace: replacedIndex is %d", new Object[] {
                Integer.valueOf(j)
            });
            if (j >= 0)
            {
                ChunkedListLoader._2D_get4(ChunkedListLoader.this).onListItemChanged(j);
            }
              goto _L5
            obj;
            throw obj;
            obj = null;
              goto _L6
        }

            
            {
                this$0 = ChunkedListLoader.this;
                super();
            }
    };
    private final AtomicBoolean reloadAccepted = new AtomicBoolean();
    private final AtomicBoolean reloadRequested = new AtomicBoolean();
    private final boolean startFromStart;
    private final AtomicBoolean updatePosted = new AtomicBoolean();
    private final Map updatedElements = new HashMap();
    private final int windowSize;

    static Queue _2D_get0(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.addedElements;
    }

    static Comparator _2D_get1(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.chatMessageComparator;
    }

    static boolean _2D_get10(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadBelowWanted;
    }

    static AtomicBoolean _2D_get11(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadRequested;
    }

    static Object _2D_get12(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.lock;
    }

    static AtomicBoolean _2D_get13(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.reloadAccepted;
    }

    static AtomicBoolean _2D_get14(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.reloadRequested;
    }

    static AtomicBoolean _2D_get15(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.updatePosted;
    }

    static Map _2D_get16(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.updatedElements;
    }

    static int _2D_get17(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.windowSize;
    }

    static boolean _2D_get2(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.hasBelow;
    }

    static ChunkedList _2D_get3(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.items;
    }

    static EventListener _2D_get4(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.listener;
    }

    static LoadResult _2D_get5(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadAboveResult;
    }

    static long _2D_get6(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadAboveTopmostId;
    }

    static boolean _2D_get7(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadAboveWanted;
    }

    static long _2D_get8(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadBelowLastId;
    }

    static LoadResult _2D_get9(ChunkedListLoader chunkedlistloader)
    {
        return chunkedlistloader.loadBelowResult;
    }

    static boolean _2D_set0(ChunkedListLoader chunkedlistloader, boolean flag)
    {
        chunkedlistloader.hasAbove = flag;
        return flag;
    }

    static boolean _2D_set1(ChunkedListLoader chunkedlistloader, boolean flag)
    {
        chunkedlistloader.hasBelow = flag;
        return flag;
    }

    static LoadResult _2D_set2(ChunkedListLoader chunkedlistloader, LoadResult loadresult)
    {
        chunkedlistloader.loadAboveResult = loadresult;
        return loadresult;
    }

    static boolean _2D_set3(ChunkedListLoader chunkedlistloader, boolean flag)
    {
        chunkedlistloader.loadAboveWanted = flag;
        return flag;
    }

    static LoadResult _2D_set4(ChunkedListLoader chunkedlistloader, LoadResult loadresult)
    {
        chunkedlistloader.loadBelowResult = loadresult;
        return loadresult;
    }

    static boolean _2D_set5(ChunkedListLoader chunkedlistloader, boolean flag)
    {
        chunkedlistloader.loadBelowWanted = flag;
        return flag;
    }

    static void _2D_wrap0(ChunkedListLoader chunkedlistloader)
    {
        chunkedlistloader.postUpdate();
    }

    public ChunkedListLoader(int i, Executor executor1, boolean flag, EventListener eventlistener)
    {
        loadAboveWanted = false;
        loadAboveResult = null;
        loadBelowWanted = false;
        loadBelowResult = null;
        windowSize = i;
        executor = executor1;
        startFromStart = flag;
        listener = eventlistener;
        listenerExecutor = eventlistener.getListEventsExecutor();
        items = new ChunkedList();
        hasAbove = true;
        hasBelow = true;
    }

    static int lambda$_2D_com_lumiyaviewer_lumiya_utils_wlist_ChunkedListLoader_13028(Identifiable identifiable, Identifiable identifiable1)
    {
        return Long.signum(((Long)identifiable.getId()).longValue() - ((Long)identifiable1.getId()).longValue());
    }

    private void postUpdate()
    {
        if (updatePosted.compareAndSet(false, true))
        {
            Debug.Printf("ChatView: requesting processUpdate ()", new Object[0]);
            listenerExecutor.execute(processUpdate);
            return;
        } else
        {
            Debug.Printf("ChatView: processUpdate () already requested", new Object[0]);
            return;
        }
    }

    public void addElement(Identifiable identifiable)
    {
        Debug.Printf("ChatView: addElement: adding element with id %d", new Object[] {
            identifiable.getId()
        });
        addedElements.add(identifiable);
        postUpdate();
    }

    public List createEmptyChunk()
    {
        return new ArrayList(windowSize);
    }

    public Identifiable get(int i)
    {
        return (Identifiable)items.get(i);
    }

    public volatile Object get(int i)
    {
        return get(i);
    }

    public boolean hasMoreItemsAtBottom()
    {
        return hasBelow;
    }

    protected LoadResult loadInBackground(int i, long l, boolean flag)
    {
        return new LoadResult(new ArrayList(0), false, l);
    }

    public void reload()
    {
        reloadRequested.set(true);
        if (loadRequested.compareAndSet(false, true))
        {
            executor.execute(loadMoreData);
        }
    }

    public void setVisibleRange(int i, int j)
    {
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        int k = items.size();
        Object obj;
        Object obj1;
        if (!loadAboveWanted && loadAboveResult == null)
        {
            obj = "yes";
        } else
        {
            obj = "no";
        }
        if (!loadBelowWanted && loadBelowResult == null)
        {
            obj1 = "yes";
        } else
        {
            obj1 = "no";
        }
        Debug.Printf("ChatView: new visible range %d, %d size %d above possible %s below possible %s", new Object[] {
            Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), obj, obj1
        });
        obj2;
        JVM INSTR monitorexit ;
        if (items.size() <= 0) goto _L2; else goto _L1
_L1:
        if (i > 0 || !hasAbove) goto _L4; else goto _L3
_L3:
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (loadAboveWanted || loadAboveResult != null) goto _L6; else goto _L5
_L5:
        loadAboveTopmostId = ((Long)((Identifiable)items.get(0)).getId()).longValue();
        loadAboveWanted = true;
        Debug.Printf("ChatView: requesting load above id %d", new Object[] {
            Long.valueOf(loadAboveTopmostId)
        });
        k = 1;
_L13:
        obj;
        JVM INSTR monitorexit ;
_L14:
        if (j < items.size() - 1 || !hasBelow) goto _L8; else goto _L7
_L7:
        obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        i = k;
        obj = obj1;
        if (loadBelowWanted)
        {
            break MISSING_BLOCK_LABEL_316;
        }
        i = k;
        obj = obj1;
        if (loadBelowResult != null)
        {
            break MISSING_BLOCK_LABEL_316;
        }
        loadBelowLastId = ((Long)((Identifiable)items.get(items.size() - 1)).getId()).longValue();
        loadBelowWanted = true;
        Debug.Printf("ChatView: requesting load below id %d", new Object[] {
            Long.valueOf(loadBelowLastId)
        });
        i = 1;
        obj = obj1;
_L24:
        obj;
        JVM INSTR monitorexit ;
_L17:
        if (i == 0) goto _L10; else goto _L9
_L9:
        if (!loadRequested.compareAndSet(false, true)) goto _L12; else goto _L11
_L11:
        Debug.Printf("ChatView: requesting loadMoreData ()", new Object[0]);
        executor.execute(loadMoreData);
_L10:
        return;
        obj;
        throw obj;
_L6:
        k = 0;
          goto _L13
        obj1;
        throw obj1;
_L4:
        if (i <= 0)
        {
            break MISSING_BLOCK_LABEL_784;
        }
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (loadAboveWanted)
        {
            break MISSING_BLOCK_LABEL_466;
        }
        obj1 = loadAboveResult;
        if (obj1 != null)
        {
            break MISSING_BLOCK_LABEL_466;
        }
        k = 1;
_L15:
        obj;
        JVM INSTR monitorexit ;
        if (!k)
        {
            break MISSING_BLOCK_LABEL_784;
        }
        i = items.removeElementsBefore(i);
        if (i == 0)
        {
            break MISSING_BLOCK_LABEL_784;
        }
        hasAbove = true;
        listener.onListItemsRemoved(0, i);
        k = 0;
          goto _L14
        k = 0;
          goto _L15
        obj1;
        throw obj1;
        obj;
        throw obj;
_L8:
        i = k;
        if (j < 0) goto _L17; else goto _L16
_L16:
        i = k;
        if (j >= items.size() - 1) goto _L17; else goto _L18
_L18:
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (loadBelowWanted)
        {
            break MISSING_BLOCK_LABEL_596;
        }
        obj1 = loadBelowResult;
        int l;
        if (obj1 != null)
        {
            break MISSING_BLOCK_LABEL_596;
        }
        l = 1;
_L19:
        obj;
        JVM INSTR monitorexit ;
        i = k;
        if (l != 0)
        {
            l = items.size();
            j = items.removeElementsAfter(j);
            i = k;
            if (j != 0)
            {
                hasBelow = true;
                listener.onListItemsRemoved(l - j, j);
                i = k;
            }
        }
          goto _L17
        l = 0;
          goto _L19
        Exception exception;
        exception;
        throw exception;
_L2:
        if (!startFromStart) goto _L21; else goto _L20
_L20:
        if (!hasBelow) goto _L23; else goto _L22
_L22:
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (loadBelowWanted || loadBelowResult != null)
        {
            break MISSING_BLOCK_LABEL_679;
        }
        loadBelowLastId = 0L;
        loadBelowWanted = true;
        Debug.Printf("ChatView: requesting load below id %d", new Object[] {
            Long.valueOf(loadBelowLastId)
        });
        i = 1;
          goto _L24
        i = 0;
          goto _L24
        exception;
        throw exception;
_L21:
        if (!hasAbove) goto _L23; else goto _L25
_L25:
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (loadAboveWanted || loadAboveResult != null)
        {
            break MISSING_BLOCK_LABEL_756;
        }
        loadAboveTopmostId = 0x7fffffffffffffffL;
        loadAboveWanted = true;
        Debug.Printf("ChatView: requesting load above id %d", new Object[] {
            Long.valueOf(loadAboveTopmostId)
        });
        i = 1;
          goto _L24
        i = 0;
          goto _L24
        exception;
        throw exception;
_L12:
        Debug.Printf("ChatView: loadMoreData() already requested", new Object[0]);
        return;
_L23:
        i = 0;
          goto _L17
        k = 0;
          goto _L14
    }

    public int size()
    {
        return items.size();
    }

    public void updateElement(Identifiable identifiable)
    {
        Debug.Printf("ChatView: addElement: updated element with id %d", new Object[] {
            identifiable.getId()
        });
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        updatedElements.put((Long)identifiable.getId(), identifiable);
        obj;
        JVM INSTR monitorexit ;
        postUpdate();
        return;
        identifiable;
        throw identifiable;
    }
}
