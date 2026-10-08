package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class ChatterList {
    @Nonnull
    private final ActiveChattersManager activeChattersManager;
    @Nonnull
    private final DaoSession daoSession;
    @Nonnull
    private final FriendManager friendManager;
    @Nonnull
    private final GroupManager groupManager;
    @Nonnull
    private final UserManager userManager;
    private final SubscriptionPool<ChatterListType, ImmutableList<ChatterDisplayData>> chatterListPool = new SubscriptionPool<>();
    private final Map<ChatterListType, ChatterDisplayDataList> chatterLists = Collections.synchronizedMap(new EnumMap(ChatterListType.class));
    private final SubscriptionPool<UUID, Float> nearbyDistancePool = new SubscriptionPool<>();
    private final SubscriptionPool<UUID, Boolean> typingUsersPool = new SubscriptionPool<>();
    private final OnListUpdated onNearbyListUpdated = new OnListUpdated() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$vvo1Hidt87pwA0OrMywwrJjt1rU
        private final /* synthetic */ void $m$0() {
            ((ChatterList) this).m306xfc0863d4();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.manager.OnListUpdated
        public final void onListUpdated() {
            $m$0();
        }
    };

    public ChatterList(@Nonnull final UserManager userManager) {
        this.userManager = userManager;
        this.daoSession = userManager.getDaoSession();
        this.chatterListPool.setRequestOnce(true);
        this.friendManager = new FriendManager(userManager, this.daoSession, this);
        this.groupManager = new GroupManager(userManager, this.daoSession, this);
        this.activeChattersManager = new ActiveChattersManager(userManager, this.daoSession, this);
        new RequestFinalProcessor<UUID, Float>(this.nearbyDistancePool, userManager.getDatabaseExecutor()) { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestFinalProcessor
            public Float processRequest(@Nonnull UUID uuid) throws Throwable {
                SLModules modules;
                SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null) {
                    return null;
                }
                return modules.minimap.getDistanceToUser(uuid);
            }
        };
        new RequestFinalProcessor<UUID, Boolean>(this.typingUsersPool, userManager.getDatabaseExecutor()) { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestFinalProcessor
            public Boolean processRequest(@Nonnull UUID uuid) throws Throwable {
                SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    return activeAgentCircuit.isUserTyping(uuid);
                }
                return false;
            }
        };
        new RequestFinalProcessor<ChatterListType, ImmutableList<ChatterDisplayData>>(this.chatterListPool, userManager.getDatabaseExecutor()) { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList.3

            /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues  reason: not valid java name */
            private static final /* synthetic */ int[] f225x521388d7 = null;
            final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$users$manager$ChatterListType;

            /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues  reason: not valid java name */
            private static /* synthetic */ int[] m307x49b0a37b() {
                if (f225x521388d7 != null) {
                    return f225x521388d7;
                }
                int[] iArr = new int[ChatterListType.valuesCustom().length];
                try {
                    iArr[ChatterListType.Active.ordinal()] = 1;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[ChatterListType.Friends.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[ChatterListType.FriendsOnline.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[ChatterListType.Groups.ordinal()] = 4;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[ChatterListType.Nearby.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                f225x521388d7 = iArr;
                return iArr;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestFinalProcessor
            public void cancelRequest(@Nonnull ChatterListType chatterListType) {
                ChatterDisplayDataList chatterDisplayDataList = (ChatterDisplayDataList) ChatterList.this.chatterLists.remove(chatterListType);
                if (chatterDisplayDataList != null) {
                    chatterDisplayDataList.dispose();
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.lumiyaviewer.lumiya.react.RequestFinalProcessor
            public ImmutableList<ChatterDisplayData> processRequest(@Nonnull ChatterListType chatterListType) {
                ChatterDisplayDataList chatterDisplayDataList = (ChatterDisplayDataList) ChatterList.this.chatterLists.get(chatterListType);
                if (chatterDisplayDataList == null) {
                    switch (m307x49b0a37b()[chatterListType.ordinal()]) {
                        case 1:
                            chatterDisplayDataList = ChatterList.this.activeChattersManager.getActiveChattersList();
                            break;
                        case 2:
                            chatterDisplayDataList = ChatterList.this.friendManager.getFriendList();
                            break;
                        case 3:
                            chatterDisplayDataList = ChatterList.this.friendManager.getFriendsOnlineList();
                            break;
                        case 4:
                            chatterDisplayDataList = ChatterList.this.groupManager.getGroupList();
                            break;
                        case 5:
                            chatterDisplayDataList = new NearbyChattersDisplayDataList(userManager, ChatterList.this.onNearbyListUpdated);
                            break;
                    }
                    chatterDisplayDataList.requestRefresh(userManager.getDatabaseExecutor());
                    ChatterList.this.chatterLists.put(chatterListType, chatterDisplayDataList);
                }
                return chatterDisplayDataList.getChatterList();
            }
        };
    }

    @Nonnull
    public ActiveChattersManager getActiveChattersManager() {
        return this.activeChattersManager;
    }

    public Subscribable<ChatterListType, ImmutableList<ChatterDisplayData>> getChatterList() {
        return this.chatterListPool;
    }

    public Subscribable<UUID, Float> getDistanceToUser() {
        return this.nearbyDistancePool;
    }

    @Nonnull
    public FriendManager getFriendManager() {
        return this.friendManager;
    }

    @Nonnull
    public GroupManager getGroupManager() {
        return this.groupManager;
    }

    public Subscribable<UUID, Boolean> getUserTypingStatus() {
        return this.typingUsersPool;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ChatterList_4733  reason: not valid java name */
    public /* synthetic */ void m306xfc0863d4() {
        notifyListUpdated(ChatterListType.Nearby);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyListUpdated(ChatterListType chatterListType) {
        this.chatterListPool.requestUpdate(chatterListType);
    }

    public void updateDistanceToAllUsers() {
        this.nearbyDistancePool.requestUpdateAll();
    }

    public void updateDistanceToUser(UUID uuid) {
        this.nearbyDistancePool.requestUpdate(uuid);
    }

    public void updateList(ChatterListType chatterListType) {
        ChatterDisplayDataList chatterDisplayDataList = this.chatterLists.get(chatterListType);
        if (chatterDisplayDataList != null) {
            chatterDisplayDataList.requestRefresh(this.userManager.getDatabaseExecutor());
        }
    }

    public void updateUserTypingStatus(UUID uuid) {
        this.typingUsersPool.requestUpdate(uuid);
    }
}
