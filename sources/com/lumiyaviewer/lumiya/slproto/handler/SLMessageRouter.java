// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.handler;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.handler:
//            SLMessageHandler, SLEventQueueMessageHandler

public class SLMessageRouter
{
    private static class HandlerInfo
    {

        private final Method method;
        private final WeakReference subscriber;

        static WeakReference _2D_get0(HandlerInfo handlerinfo)
        {
            return handlerinfo.subscriber;
        }

        public void invoke(Object obj)
        {
            Object obj1;
            try
            {
                obj1 = subscriber.get();
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                ((IllegalArgumentException) (obj)).printStackTrace();
                return;
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                ((IllegalAccessException) (obj)).printStackTrace();
                return;
            }
            catch (InvocationTargetException invocationtargetexception)
            {
                Debug.Log((new StringBuilder()).append("InvocationTargetException in handler for ").append(obj.getClass().getSimpleName()).toString());
                obj = invocationtargetexception.getCause();
                if (obj != null)
                {
                    ((Throwable) (obj)).printStackTrace();
                    return;
                } else
                {
                    invocationtargetexception.printStackTrace();
                    return;
                }
            }
            if (obj1 == null)
            {
                break MISSING_BLOCK_LABEL_29;
            }
            method.invoke(obj1, new Object[] {
                obj
            });
        }

        public HandlerInfo(Method method1, Object obj)
        {
            method = method1;
            subscriber = new WeakReference(obj);
        }
    }

    private static class HandlerList extends LinkedList
    {

        public void deleteAll(Object obj)
        {
            LinkedList linkedlist = new LinkedList();
            Iterator iterator = iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                HandlerInfo handlerinfo = (HandlerInfo)iterator.next();
                Object obj1 = HandlerInfo._2D_get0(handlerinfo).get();
                if (obj1 == null || obj1 == obj)
                {
                    linkedlist.add(handlerinfo);
                }
            } while (true);
            removeAll(linkedlist);
        }

        public void invokeAll(Object obj)
        {
            for (Iterator iterator = iterator(); iterator.hasNext(); ((HandlerInfo)iterator.next()).invoke(obj)) { }
        }

        private HandlerList()
        {
        }

        HandlerList(HandlerList handlerlist)
        {
            this();
        }
    }


    private Map eventQueueMessageHandlers;
    private Map messageHandlers;

    public SLMessageRouter()
    {
        messageHandlers = new HashMap();
        eventQueueMessageHandlers = new HashMap();
    }

    public boolean handleEventQueueMessage(com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType capseventtype, LLSDNode llsdnode)
    {
        this;
        JVM INSTR monitorenter ;
        capseventtype = (HandlerList)eventQueueMessageHandlers.get(capseventtype);
        if (capseventtype == null)
        {
            break MISSING_BLOCK_LABEL_29;
        }
        capseventtype.invokeAll(llsdnode);
        this;
        JVM INSTR monitorexit ;
        return true;
        this;
        JVM INSTR monitorexit ;
        return false;
        capseventtype;
        throw capseventtype;
    }

    public boolean handleMessage(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        HandlerList handlerlist = (HandlerList)messageHandlers.get(obj.getClass());
        if (handlerlist == null)
        {
            break MISSING_BLOCK_LABEL_32;
        }
        handlerlist.invokeAll(obj);
        this;
        JVM INSTR monitorexit ;
        return true;
        this;
        JVM INSTR monitorexit ;
        return false;
        obj;
        throw obj;
    }

    public void registerHandler(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        Method amethod[];
        int j;
        amethod = obj.getClass().getMethods();
        j = amethod.length;
        int i = 0;
_L3:
        if (i >= j) goto _L2; else goto _L1
_L1:
        Object obj2 = amethod[i];
        Class aclass[];
        if ((SLMessageHandler)((Method) (obj2)).getAnnotation(com/lumiyaviewer/lumiya/slproto/handler/SLMessageHandler) == null)
        {
            break MISSING_BLOCK_LABEL_140;
        }
        aclass = ((Method) (obj2)).getParameterTypes();
        if (aclass.length != 1)
        {
            throw new IllegalArgumentException("SLMessageHandler methods must specify a single SLMessage paramter.");
        }
        break MISSING_BLOCK_LABEL_73;
        obj;
        this;
        JVM INSTR monitorexit ;
        throw obj;
        Object obj3 = aclass[0];
        HandlerList handlerlist;
        HandlerInfo handlerinfo;
        handlerinfo = new HandlerInfo(((Method) (obj2)), obj);
        handlerlist = (HandlerList)messageHandlers.get(obj3);
        Object obj1;
        obj1 = handlerlist;
        if (handlerlist != null)
        {
            break MISSING_BLOCK_LABEL_133;
        }
        obj1 = new HandlerList(null);
        messageHandlers.put(obj3, obj1);
        ((HandlerList) (obj1)).add(handlerinfo);
        obj1 = (SLEventQueueMessageHandler)((Method) (obj2)).getAnnotation(com/lumiyaviewer/lumiya/slproto/handler/SLEventQueueMessageHandler);
        if (obj1 == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        if (((Method) (obj2)).getParameterTypes().length != 1)
        {
            throw new IllegalArgumentException("SLMessageHandler methods must specify a single LLSDNode paramter.");
        }
        obj3 = ((SLEventQueueMessageHandler) (obj1)).eventName();
        obj2 = new HandlerInfo(((Method) (obj2)), obj);
        handlerlist = (HandlerList)eventQueueMessageHandlers.get(obj3);
        obj1 = handlerlist;
        if (handlerlist != null)
        {
            break MISSING_BLOCK_LABEL_238;
        }
        obj1 = new HandlerList(null);
        eventQueueMessageHandlers.put(obj3, obj1);
        ((HandlerList) (obj1)).add(obj2);
        i++;
          goto _L3
_L2:
    }

    public void unregisterHandler(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        for (Iterator iterator = messageHandlers.values().iterator(); iterator.hasNext(); ((HandlerList)iterator.next()).deleteAll(obj)) { }
        break MISSING_BLOCK_LABEL_47;
        obj;
        throw obj;
        for (Iterator iterator1 = eventQueueMessageHandlers.values().iterator(); iterator1.hasNext(); ((HandlerList)iterator1.next()).deleteAll(obj)) { }
        this;
        JVM INSTR monitorexit ;
    }
}
