package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ObjectsManager {
    private final MultipleChatterNameRetriever nameRetriever;
    private final UserManager userManager;
    private final AtomicReference<SLParcelInfo> parcelInfo = new AtomicReference<>(null);
    private final Object filterLock = new Object();
    private SLObjectFilterInfo filterInfo = SLObjectFilterInfo.create();
    private final MultipleChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated = new MultipleChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4
        private final /* synthetic */ void $m$0(MultipleChatterNameRetriever multipleChatterNameRetriever) {
            ((ObjectsManager) this).m358x8e849dac(multipleChatterNameRetriever);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever.OnChatterNameUpdated
        public final void onChatterNameUpdated(MultipleChatterNameRetriever multipleChatterNameRetriever) {
            $m$0(multipleChatterNameRetriever);
        }
    };
    private final SimpleRequestHandler<SubscriptionSingleKey> updateRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.1
        @Override // com.lumiyaviewer.lumiya.react.RequestHandler
        public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(ObjectsManager.this.updateObjectListRunnable);
            } else {
                ObjectsManager.this.objectDisplayListPool.onResultError(SubscriptionSingleKey.Value, new SLGridConnection.NotConnectedException());
            }
        }

        @Override // com.lumiyaviewer.lumiya.react.SimpleRequestHandler, com.lumiyaviewer.lumiya.react.RequestHandler
        public void onRequestCancelled(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            ObjectsManager.this.nameRetriever.clearChatters();
        }
    };
    private final SimpleRequestHandler<Integer> objectProfileRequestHandler = new AnonymousClass2();
    private final SimpleRequestHandler<UUID> touchableObjectsRequestHandler = new AnonymousClass3();
    private final Runnable updateObjectListRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.4
        @Override // java.lang.Runnable
        public void run() {
            SLObjectFilterInfo sLObjectFilterInfo;
            synchronized (ObjectsManager.this.filterLock) {
                sLObjectFilterInfo = ObjectsManager.this.filterInfo;
            }
            SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
            AgentPosition agentPosition = activeAgentCircuit != null ? activeAgentCircuit.getModules().avatarControl.getAgentPosition() : null;
            ImmutableVector immutablePosition = agentPosition != null ? agentPosition.getImmutablePosition() : null;
            SLParcelInfo sLParcelInfo = (SLParcelInfo) ObjectsManager.this.parcelInfo.get();
            Debug.Printf("ObjectList: updating object list, parcelInfo %s, agentPosVector %s", sLParcelInfo, immutablePosition);
            ObjectsManager.this.objectDisplayListPool.onResultData(SubscriptionSingleKey.Value, (sLParcelInfo == null || immutablePosition == null) ? new ObjectDisplayList(ImmutableList.of(), false) : sLParcelInfo.getDisplayObjects(immutablePosition, sLObjectFilterInfo, ObjectsManager.this.nameRetriever));
        }
    };
    private final SubscriptionPool<SubscriptionSingleKey, ObjectDisplayList> objectDisplayListPool = new SubscriptionPool<>();
    private final SubscriptionPool<Integer, SLObjectProfileData> objectProfilePool = new SubscriptionPool<>();
    private final SubscriptionPool<Integer, SLTaskInventory> taskInventoryPool = new SubscriptionPool<>();
    private final SubscriptionPool<SubscriptionSingleKey, MyAvatarState> myAvatarStatePool = new SubscriptionPool<>();
    private final SubscriptionSingleDataPool<ImmutableSet<UUID>> runningAnimationsPool = new SubscriptionSingleDataPool<>();
    private final SubscriptionPool<UUID, ImmutableList<SLObjectInfo>> touchableObjectsPool = new SubscriptionPool<>();

    /* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager$2  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass2 extends SimpleRequestHandler<Integer> {
        AnonymousClass2() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$2_3438  reason: not valid java name */
        public /* synthetic */ void m359xd130cea5(SLAgentCircuit sLAgentCircuit, Integer num) {
            SLObjectProfileData objectProfile = sLAgentCircuit.getObjectProfile(num.intValue());
            if (objectProfile != null) {
                ObjectsManager.this.objectProfilePool.onResultData(num, objectProfile);
            } else {
                ObjectsManager.this.objectProfilePool.onResultError(num, new ObjectDoesNotExistException(num.intValue(), (ObjectDoesNotExistException) null));
            }
        }

        @Override // com.lumiyaviewer.lumiya.react.RequestHandler
        public void onRequest(@Nonnull final Integer num) {
            final SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4.1
                    private final /* synthetic */ void $m$0() {
                        ((ObjectsManager.AnonymousClass2) this).m359xd130cea5((SLAgentCircuit) activeAgentCircuit, (Integer) num);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                ObjectsManager.this.objectProfilePool.onResultError(num, new SLGridConnection.NotConnectedException());
            }
        }
    }

    /* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager$3  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass3 extends SimpleRequestHandler<UUID> {
        AnonymousClass3() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$3_4319  reason: not valid java name */
        public /* synthetic */ void m360xd2e617a5(SLAgentCircuit sLAgentCircuit, UUID uuid) {
            ImmutableList<SLObjectInfo> userTouchableObjects = sLAgentCircuit.getGridConnection().parcelInfo.getUserTouchableObjects(sLAgentCircuit, uuid);
            if (userTouchableObjects != null) {
                ObjectsManager.this.touchableObjectsPool.onResultData(uuid, userTouchableObjects);
            } else {
                ObjectsManager.this.touchableObjectsPool.onResultError(uuid, new ObjectDoesNotExistException(uuid, (ObjectDoesNotExistException) null));
            }
        }

        @Override // com.lumiyaviewer.lumiya.react.RequestHandler
        public void onRequest(@Nonnull final UUID uuid) {
            final SLAgentCircuit activeAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4.2
                    private final /* synthetic */ void $m$0() {
                        ((ObjectsManager.AnonymousClass3) this).m360xd2e617a5((SLAgentCircuit) activeAgentCircuit, (UUID) uuid);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                ObjectsManager.this.touchableObjectsPool.onResultError(uuid, new SLGridConnection.NotConnectedException());
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ObjectDisplayList {
        public final boolean isLoading;
        public final ImmutableList<SLObjectDisplayInfo> objects;

        public ObjectDisplayList(ImmutableList<SLObjectDisplayInfo> immutableList, boolean z) {
            this.objects = immutableList;
            this.isLoading = z;
        }
    }

    /* loaded from: classes.dex */
    public static class ObjectDoesNotExistException extends Exception {
        private final int localID;
        @Nullable
        private final UUID uuid;

        private ObjectDoesNotExistException(int i) {
            this.localID = i;
            this.uuid = null;
        }

        /* synthetic */ ObjectDoesNotExistException(int i, ObjectDoesNotExistException objectDoesNotExistException) {
            this(i);
        }

        private ObjectDoesNotExistException(@Nullable UUID uuid) {
            this.localID = 0;
            this.uuid = uuid;
        }

        /* synthetic */ ObjectDoesNotExistException(UUID uuid, ObjectDoesNotExistException objectDoesNotExistException) {
            this(uuid);
        }

        public int getLocalID() {
            return this.localID;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ObjectsManager(UserManager userManager) {
        this.userManager = userManager;
        this.nameRetriever = new MultipleChatterNameRetriever(userManager.getUserID(), this.onChatterNameUpdated, null);
        this.objectDisplayListPool.attachRequestHandler(this.updateRequestHandler);
        this.objectProfilePool.attachRequestHandler(this.objectProfileRequestHandler);
        this.touchableObjectsPool.attachRequestHandler(this.touchableObjectsRequestHandler);
    }

    public void clearParcelInfo(@Nullable SLParcelInfo sLParcelInfo) {
        this.parcelInfo.compareAndSet(sLParcelInfo, null);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager_2320  reason: not valid java name */
    public /* synthetic */ void m358x8e849dac(MultipleChatterNameRetriever multipleChatterNameRetriever) {
        requestObjectListUpdate();
    }

    public SubscriptionPool<SubscriptionSingleKey, MyAvatarState> myAvatarState() {
        return this.myAvatarStatePool;
    }

    public void requestObjectListUpdate() {
        this.objectDisplayListPool.requestUpdate(SubscriptionSingleKey.Value);
    }

    public void requestObjectProfileUpdate(int i) {
        this.objectProfilePool.requestUpdate(Integer.valueOf(i));
    }

    public void requestTaskInventoryUpdate(int i) {
        this.taskInventoryPool.requestUpdate(Integer.valueOf(i));
    }

    public void requestTouchableChildrenUpdate(UUID uuid) {
        this.touchableObjectsPool.requestUpdate(uuid);
    }

    public SubscriptionSingleDataPool<ImmutableSet<UUID>> runningAnimations() {
        return this.runningAnimationsPool;
    }

    public void setFilter(SLObjectFilterInfo sLObjectFilterInfo) {
        boolean z = false;
        synchronized (this.filterLock) {
            if (!this.filterInfo.equals(sLObjectFilterInfo)) {
                this.filterInfo = sLObjectFilterInfo;
                z = true;
            }
        }
        if (z) {
            requestObjectListUpdate();
        }
    }

    public void setParcelInfo(@Nullable SLParcelInfo sLParcelInfo) {
        this.parcelInfo.set(sLParcelInfo);
        requestObjectListUpdate();
    }

    public Subscribable<UUID, ImmutableList<SLObjectInfo>> touchableObjects() {
        return this.touchableObjectsPool;
    }
}
