// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.reqset;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.utils.reqset:
//            RequestCompleteListener

class WeakRequestSet
{

    private final Object lock = new Object();
    private final Map requests = new HashMap();

    WeakRequestSet()
    {
    }

    boolean addRequest(Object obj, Object obj1)
    {
        boolean flag1 = true;
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        Object obj3 = (Set)requests.get(obj);
        if (obj3 != null) goto _L2; else goto _L1
_L1:
        obj3 = new HashSet();
        ((Set) (obj3)).add(new WeakReference(obj1));
        requests.put(obj, obj3);
_L6:
        obj2;
        JVM INSTR monitorexit ;
        return flag1;
_L2:
        obj = ((Set) (obj3)).iterator();
        boolean flag = false;
_L4:
        WeakReference weakreference;
        if (!((Iterator) (obj)).hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        weakreference = (WeakReference)((Iterator) (obj)).next();
        if (weakreference.get() == null)
        {
            ((Iterator) (obj)).remove();
            continue; /* Loop/switch isn't completed */
        }
        if (weakreference.get() == obj1)
        {
            flag = true;
        }
        if (true) goto _L4; else goto _L3
_L3:
        if (flag)
        {
            break MISSING_BLOCK_LABEL_165;
        }
        ((Set) (obj3)).add(new WeakReference(obj1));
        continue; /* Loop/switch isn't completed */
        obj;
        throw obj;
        flag1 = false;
        if (true) goto _L6; else goto _L5
_L5:
    }

    void completeRequest(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        Set set = (Set)requests.remove(obj);
        obj1;
        JVM INSTR monitorexit ;
        obj1 = set.iterator();
        do
        {
            if (!((Iterator) (obj1)).hasNext())
            {
                break;
            }
            Object obj2 = ((WeakReference)((Iterator) (obj1)).next()).get();
            if (obj2 != null && (obj2 instanceof RequestCompleteListener))
            {
                ((RequestCompleteListener)obj2).onRequestComplete(obj);
            }
        } while (true);
        break MISSING_BLOCK_LABEL_81;
        obj;
        throw obj;
    }

    Object getRequest()
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        Object obj = requests.entrySet().iterator();
_L2:
        java.util.Map.Entry entry;
        if (((Iterator) (obj)).hasNext())
        {
            entry = (java.util.Map.Entry)((Iterator) (obj)).next();
            Iterator iterator = ((Set)entry.getValue()).iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                if (((WeakReference)iterator.next()).get() == null)
                {
                    iterator.remove();
                }
            } while (true);
            break MISSING_BLOCK_LABEL_98;
        }
        break; /* Loop/switch isn't completed */
        obj;
        throw obj;
label0:
        {
            if (!((Set)entry.getValue()).isEmpty())
            {
                break label0;
            }
            ((Iterator) (obj)).remove();
        }
        if (true) goto _L2; else goto _L1
        obj = entry.getKey();
_L4:
        obj1;
        JVM INSTR monitorexit ;
        return obj;
_L1:
        obj = null;
        if (true) goto _L4; else goto _L3
_L3:
    }
}
