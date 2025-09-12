// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

public class ObjectsManager
{
    public static class ObjectDisplayList
    {

        public final boolean isLoading;
        public final ImmutableList objects;

        public ObjectDisplayList(ImmutableList immutablelist, boolean flag)
        {
            objects = immutablelist;
            isLoading = flag;
        }
    }

    public static class ObjectDoesNotExistException extends Exception
    {

        private final int localID;
        private final UUID uuid;

        public int getLocalID()
        {
            return localID;
        }

        private ObjectDoesNotExistException(int i)
        {
            localID = i;
            uuid = null;
        }

        ObjectDoesNotExistException(int i, ObjectDoesNotExistException objectdoesnotexistexception)
        {
            this(i);
        }

        private ObjectDoesNotExistException(UUID uuid1)
        {
            localID = 0;
            uuid = uuid1;
        }

        ObjectDoesNotExistException(UUID uuid1, ObjectDoesNotExistException objectdoesnotexistexception)
        {
            this(uuid1);
        }
    }


    private SLObjectFilterInfo filterInfo;
    private final Object filterLock = new Object();
    private final SubscriptionPool myAvatarStatePool = new SubscriptionPool();
    private final MultipleChatterNameRetriever nameRetriever;
    private final SubscriptionPool objectDisplayListPool = new SubscriptionPool();
    private final SubscriptionPool objectProfilePool = new SubscriptionPool();
    private final SimpleRequestHandler objectProfileRequestHandler = new SimpleRequestHandler() {

        final ObjectsManager this$0;

        void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$2_3438(SLAgentCircuit slagentcircuit, Integer integer)
        {
            slagentcircuit = slagentcircuit.getObjectProfile(integer.intValue());
            if (slagentcircuit != null)
            {
                ObjectsManager._2D_get4(ObjectsManager.this).onResultData(integer, slagentcircuit);
                return;
            } else
            {
                ObjectsManager._2D_get4(ObjectsManager.this).onResultError(integer, new ObjectDoesNotExistException(integer.intValue(), null));
                return;
            }
        }

        public void onRequest(Integer integer)
        {
            SLAgentCircuit slagentcircuit = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
            if (slagentcircuit != null)
            {
                slagentcircuit.execute(new _2D_.Lambda.n3FxEEuksYOCADj00lseQFiZ3z4._cls1(this, slagentcircuit, integer));
                return;
            } else
            {
                ObjectsManager._2D_get4(ObjectsManager.this).onResultError(integer, new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException());
                return;
            }
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((Integer)obj);
        }

            
            {
                this$0 = ObjectsManager.this;
                super();
            }
    };
    private final com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated = new _2D_.Lambda.n3FxEEuksYOCADj00lseQFiZ3z4(this);
    private final AtomicReference parcelInfo = new AtomicReference(null);
    private final SubscriptionSingleDataPool runningAnimationsPool = new SubscriptionSingleDataPool();
    private final SubscriptionPool taskInventoryPool = new SubscriptionPool();
    private final SubscriptionPool touchableObjectsPool = new SubscriptionPool();
    private final SimpleRequestHandler touchableObjectsRequestHandler = new SimpleRequestHandler() {

        final ObjectsManager this$0;

        void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$3_4319(SLAgentCircuit slagentcircuit, UUID uuid)
        {
            slagentcircuit = slagentcircuit.getGridConnection().parcelInfo.getUserTouchableObjects(slagentcircuit, uuid);
            if (slagentcircuit != null)
            {
                ObjectsManager._2D_get6(ObjectsManager.this).onResultData(uuid, slagentcircuit);
                return;
            } else
            {
                ObjectsManager._2D_get6(ObjectsManager.this).onResultError(uuid, new ObjectDoesNotExistException(uuid, null));
                return;
            }
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((UUID)obj);
        }

        public void onRequest(UUID uuid)
        {
            SLAgentCircuit slagentcircuit = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
            if (slagentcircuit != null)
            {
                slagentcircuit.execute(new _2D_.Lambda.n3FxEEuksYOCADj00lseQFiZ3z4._cls2(this, slagentcircuit, uuid));
                return;
            } else
            {
                ObjectsManager._2D_get6(ObjectsManager.this).onResultError(uuid, new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException());
                return;
            }
        }

            
            {
                this$0 = ObjectsManager.this;
                super();
            }
    };
    private final Runnable updateObjectListRunnable = new Runnable() {

        final ObjectsManager this$0;

        public void run()
        {
            Object obj = ObjectsManager._2D_get1(ObjectsManager.this);
            obj;
            JVM INSTR monitorenter ;
            SLObjectFilterInfo slobjectfilterinfo = ObjectsManager._2D_get0(ObjectsManager.this);
            obj;
            JVM INSTR monitorexit ;
            obj = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
            Exception exception;
            SLParcelInfo slparcelinfo;
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules().avatarControl.getAgentPosition();
            } else
            {
                obj = null;
            }
            if (obj != null)
            {
                obj = ((AgentPosition) (obj)).getImmutablePosition();
            } else
            {
                obj = null;
            }
            slparcelinfo = (SLParcelInfo)ObjectsManager._2D_get5(ObjectsManager.this).get();
            Debug.Printf("ObjectList: updating object list, parcelInfo %s, agentPosVector %s", new Object[] {
                slparcelinfo, obj
            });
            if (slparcelinfo != null && obj != null)
            {
                obj = slparcelinfo.getDisplayObjects(((com.lumiyaviewer.lumiya.slproto.types.ImmutableVector) (obj)), slobjectfilterinfo, ObjectsManager._2D_get2(ObjectsManager.this));
            } else
            {
                obj = new ObjectDisplayList(ImmutableList.of(), false);
            }
            ObjectsManager._2D_get3(ObjectsManager.this).onResultData(SubscriptionSingleKey.Value, obj);
            return;
            exception;
            throw exception;
        }

            
            {
                this$0 = ObjectsManager.this;
                super();
            }
    };
    private final SimpleRequestHandler updateRequestHandler = new SimpleRequestHandler() {

        final ObjectsManager this$0;

        public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
        {
            subscriptionsinglekey = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
            if (subscriptionsinglekey != null)
            {
                subscriptionsinglekey.execute(ObjectsManager._2D_get7(ObjectsManager.this));
                return;
            } else
            {
                ObjectsManager._2D_get3(ObjectsManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException());
                return;
            }
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((SubscriptionSingleKey)obj);
        }

        public void onRequestCancelled(SubscriptionSingleKey subscriptionsinglekey)
        {
            ObjectsManager._2D_get2(ObjectsManager.this).clearChatters();
        }

        public volatile void onRequestCancelled(Object obj)
        {
            onRequestCancelled((SubscriptionSingleKey)obj);
        }

            
            {
                this$0 = ObjectsManager.this;
                super();
            }
    };
    private final UserManager userManager;

    static SLObjectFilterInfo _2D_get0(ObjectsManager objectsmanager)
    {
        return objectsmanager.filterInfo;
    }

    static Object _2D_get1(ObjectsManager objectsmanager)
    {
        return objectsmanager.filterLock;
    }

    static MultipleChatterNameRetriever _2D_get2(ObjectsManager objectsmanager)
    {
        return objectsmanager.nameRetriever;
    }

    static SubscriptionPool _2D_get3(ObjectsManager objectsmanager)
    {
        return objectsmanager.objectDisplayListPool;
    }

    static SubscriptionPool _2D_get4(ObjectsManager objectsmanager)
    {
        return objectsmanager.objectProfilePool;
    }

    static AtomicReference _2D_get5(ObjectsManager objectsmanager)
    {
        return objectsmanager.parcelInfo;
    }

    static SubscriptionPool _2D_get6(ObjectsManager objectsmanager)
    {
        return objectsmanager.touchableObjectsPool;
    }

    static Runnable _2D_get7(ObjectsManager objectsmanager)
    {
        return objectsmanager.updateObjectListRunnable;
    }

    static UserManager _2D_get8(ObjectsManager objectsmanager)
    {
        return objectsmanager.userManager;
    }

    ObjectsManager(UserManager usermanager)
    {
        filterInfo = SLObjectFilterInfo.create();
        userManager = usermanager;
        nameRetriever = new MultipleChatterNameRetriever(usermanager.getUserID(), onChatterNameUpdated, null);
        objectDisplayListPool.attachRequestHandler(updateRequestHandler);
        objectProfilePool.attachRequestHandler(objectProfileRequestHandler);
        touchableObjectsPool.attachRequestHandler(touchableObjectsRequestHandler);
    }

    public void clearParcelInfo(SLParcelInfo slparcelinfo)
    {
        parcelInfo.compareAndSet(slparcelinfo, null);
    }

    public Subscribable getObjectDisplayList()
    {
        return objectDisplayListPool;
    }

    public Subscribable getObjectProfile()
    {
        return objectProfilePool;
    }

    public Subscribable getObjectTaskInventory()
    {
        return taskInventoryPool;
    }

    public RequestSource getTaskInventoryRequestSource()
    {
        return taskInventoryPool;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager_2320(MultipleChatterNameRetriever multiplechatternameretriever)
    {
        requestObjectListUpdate();
    }

    public SubscriptionPool myAvatarState()
    {
        return myAvatarStatePool;
    }

    public void requestObjectListUpdate()
    {
        objectDisplayListPool.requestUpdate(SubscriptionSingleKey.Value);
    }

    public void requestObjectProfileUpdate(int i)
    {
        objectProfilePool.requestUpdate(Integer.valueOf(i));
    }

    public void requestTaskInventoryUpdate(int i)
    {
        taskInventoryPool.requestUpdate(Integer.valueOf(i));
    }

    public void requestTouchableChildrenUpdate(UUID uuid)
    {
        touchableObjectsPool.requestUpdate(uuid);
    }

    public SubscriptionSingleDataPool runningAnimations()
    {
        return runningAnimationsPool;
    }

    public void setFilter(SLObjectFilterInfo slobjectfilterinfo)
    {
        boolean flag = false;
        Object obj = filterLock;
        obj;
        JVM INSTR monitorenter ;
        if (filterInfo.equals(slobjectfilterinfo))
        {
            break MISSING_BLOCK_LABEL_27;
        }
        filterInfo = slobjectfilterinfo;
        flag = true;
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            requestObjectListUpdate();
        }
        return;
        slobjectfilterinfo;
        throw slobjectfilterinfo;
    }

    public void setParcelInfo(SLParcelInfo slparcelinfo)
    {
        parcelInfo.set(slparcelinfo);
        requestObjectListUpdate();
    }

    public Subscribable touchableObjects()
    {
        return touchableObjectsPool;
    }
}
