// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import java.util.UUID;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;

public class ObjectsManager
{
    private SLObjectFilterInfo filterInfo;
    private final Object filterLock;
    private final SubscriptionPool<SubscriptionSingleKey, MyAvatarState> myAvatarStatePool;
    private final MultipleChatterNameRetriever nameRetriever;
    private final SubscriptionPool<SubscriptionSingleKey, ObjectDisplayList> objectDisplayListPool;
    private final SubscriptionPool<Integer, SLObjectProfileData> objectProfilePool;
    private final SimpleRequestHandler<Integer> objectProfileRequestHandler;
    private final MultipleChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated;
    private final AtomicReference<SLParcelInfo> parcelInfo;
    private final SubscriptionSingleDataPool<ImmutableSet<UUID>> runningAnimationsPool;
    private final SubscriptionPool<Integer, SLTaskInventory> taskInventoryPool;
    private final SubscriptionPool<UUID, ImmutableList<SLObjectInfo>> touchableObjectsPool;
    private final SimpleRequestHandler<UUID> touchableObjectsRequestHandler;
    private final Runnable updateObjectListRunnable;
    private final SimpleRequestHandler<SubscriptionSingleKey> updateRequestHandler;
    private final UserManager userManager;
    
    ObjectsManager(final UserManager userManager) {
        this.parcelInfo = new AtomicReference<SLParcelInfo>(null);
        this.filterLock = new Object();
        this.filterInfo = SLObjectFilterInfo.create();
        this.onChatterNameUpdated = new _$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4(this);
        this.updateRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                final SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    activeAgentCircuit.execute(ObjectsManager.this.updateObjectListRunnable);
                }
                else {
                    ObjectsManager.this.objectDisplayListPool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
                }
            }
            
            @Override
            public void onRequestCancelled(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                ObjectsManager.this.nameRetriever.clearChatters();
            }
        };
        this.objectProfileRequestHandler = new SimpleRequestHandler<Integer>() {
            @Override
            public void onRequest(@Nonnull final Integer n) {
                final SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    activeAgentCircuit.execute(new _$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4$1(this, activeAgentCircuit, n));
                }
                else {
                    ObjectsManager.this.objectProfilePool.onResultError(n, new SLGridConnection.NotConnectedException());
                }
            }
        };
        this.touchableObjectsRequestHandler = new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                final SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    activeAgentCircuit.execute(new _$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4$2(this, activeAgentCircuit, uuid));
                }
                else {
                    ObjectsManager.this.touchableObjectsPool.onResultError(uuid, new SLGridConnection.NotConnectedException());
                }
            }
        };
        this.updateObjectListRunnable = new Runnable() {
            @Override
            public void run() {
                Object o;
                SLObjectFilterInfo -get0;
                SLParcelInfo slParcelInfo;
                Label_0055_Outer:Label_0108_Outer:
                while (true) {
                    o = ObjectsManager.this.filterLock;
                    while (true) {
                    Label_0138:
                        while (true) {
                        Label_0133:
                            while (true) {
                                synchronized (o) {
                                    -get0 = ObjectsManager.this.filterInfo;
                                    monitorexit(o);
                                    o = ObjectsManager.this.userManager.getActiveAgentCircuit();
                                    if (o != null) {
                                        o = ((SLAgentCircuit)o).getModules().avatarControl.getAgentPosition();
                                        if (o == null) {
                                            break Label_0133;
                                        }
                                        o = ((AgentPosition)o).getImmutablePosition();
                                        slParcelInfo = ObjectsManager.this.parcelInfo.get();
                                        Debug.Printf("ObjectList: updating object list, parcelInfo %s, agentPosVector %s", slParcelInfo, o);
                                        if (slParcelInfo != null && o != null) {
                                            o = slParcelInfo.getDisplayObjects((ImmutableVector)o, -get0, ObjectsManager.this.nameRetriever);
                                            ObjectsManager.this.objectDisplayListPool.onResultData(SubscriptionSingleKey.Value, o);
                                            return;
                                        }
                                        break Label_0138;
                                    }
                                }
                                o = null;
                                continue Label_0055_Outer;
                            }
                            o = null;
                            continue Label_0108_Outer;
                        }
                        o = new ObjectDisplayList(ImmutableList.of(), false);
                        continue;
                    }
                }
            }
        };
        this.objectDisplayListPool = new SubscriptionPool<SubscriptionSingleKey, ObjectDisplayList>();
        this.objectProfilePool = new SubscriptionPool<Integer, SLObjectProfileData>();
        this.taskInventoryPool = new SubscriptionPool<Integer, SLTaskInventory>();
        this.myAvatarStatePool = new SubscriptionPool<SubscriptionSingleKey, MyAvatarState>();
        this.runningAnimationsPool = new SubscriptionSingleDataPool<ImmutableSet<UUID>>();
        this.touchableObjectsPool = new SubscriptionPool<UUID, ImmutableList<SLObjectInfo>>();
        this.userManager = userManager;
        this.nameRetriever = new MultipleChatterNameRetriever(userManager.getUserID(), this.onChatterNameUpdated, null);
        this.objectDisplayListPool.attachRequestHandler(this.updateRequestHandler);
        this.objectProfilePool.attachRequestHandler(this.objectProfileRequestHandler);
        this.touchableObjectsPool.attachRequestHandler(this.touchableObjectsRequestHandler);
    }
    
    public void clearParcelInfo(@Nullable final SLParcelInfo expectedValue) {
        this.parcelInfo.compareAndSet(expectedValue, null);
    }
    
    public Subscribable<SubscriptionSingleKey, ObjectDisplayList> getObjectDisplayList() {
        return this.objectDisplayListPool;
    }
    
    public Subscribable<Integer, SLObjectProfileData> getObjectProfile() {
        return this.objectProfilePool;
    }
    
    public Subscribable<Integer, SLTaskInventory> getObjectTaskInventory() {
        return this.taskInventoryPool;
    }
    
    public RequestSource<Integer, SLTaskInventory> getTaskInventoryRequestSource() {
        return this.taskInventoryPool;
    }
    
    public SubscriptionPool<SubscriptionSingleKey, MyAvatarState> myAvatarState() {
        return this.myAvatarStatePool;
    }
    
    public void requestObjectListUpdate() {
        this.objectDisplayListPool.requestUpdate(SubscriptionSingleKey.Value);
    }
    
    public void requestObjectProfileUpdate(final int i) {
        this.objectProfilePool.requestUpdate(i);
    }
    
    public void requestTaskInventoryUpdate(final int i) {
        this.taskInventoryPool.requestUpdate(i);
    }
    
    public void requestTouchableChildrenUpdate(final UUID uuid) {
        this.touchableObjectsPool.requestUpdate(uuid);
    }
    
    public SubscriptionSingleDataPool<ImmutableSet<UUID>> runningAnimations() {
        return this.runningAnimationsPool;
    }
    
    public void setFilter(final SLObjectFilterInfo slObjectFilterInfo) {
        boolean b = false;
        synchronized (this.filterLock) {
            if (!this.filterInfo.equals(slObjectFilterInfo)) {
                this.filterInfo = slObjectFilterInfo;
                b = true;
            }
            monitorexit(this.filterLock);
            if (b) {
                this.requestObjectListUpdate();
            }
        }
    }
    
    public void setParcelInfo(@Nullable final SLParcelInfo newValue) {
        this.parcelInfo.set(newValue);
        this.requestObjectListUpdate();
    }
    
    public Subscribable<UUID, ImmutableList<SLObjectInfo>> touchableObjects() {
        return this.touchableObjectsPool;
    }
    
    public static class ObjectDisplayList
    {
        public final boolean isLoading;
        public final ImmutableList<SLObjectDisplayInfo> objects;
        
        public ObjectDisplayList(final ImmutableList<SLObjectDisplayInfo> objects, final boolean isLoading) {
            this.objects = objects;
            this.isLoading = isLoading;
        }
    }
    
    public static class ObjectDoesNotExistException extends Exception
    {
        private final int localID;
        @Nullable
        private final UUID uuid;
        
        private ObjectDoesNotExistException(final int localID) {
            this.localID = localID;
            this.uuid = null;
        }
        
        private ObjectDoesNotExistException(@Nullable final UUID uuid) {
            this.localID = 0;
            this.uuid = uuid;
        }
        
        public int getLocalID() {
            return this.localID;
        }
    }
}
