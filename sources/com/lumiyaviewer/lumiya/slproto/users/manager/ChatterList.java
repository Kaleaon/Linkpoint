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

public class ChatterList {
    /* access modifiers changed from: private */
    @Nonnull
    public final ActiveChattersManager activeChattersManager;
    private final SubscriptionPool<ChatterListType, ImmutableList<ChatterDisplayData>> chatterListPool = new SubscriptionPool<>();
    /* access modifiers changed from: private */
    public final Map<ChatterListType, ChatterDisplayDataList> chatterLists = Collections.synchronizedMap(new EnumMap(ChatterListType.class));
    @Nonnull
    private final DaoSession daoSession;
    /* access modifiers changed from: private */
    @Nonnull
    public final FriendManager friendManager;
    /* access modifiers changed from: private */
    @Nonnull
    public final GroupManager groupManager;
    private final SubscriptionPool<UUID, Float> nearbyDistancePool = new SubscriptionPool<>();
    /* access modifiers changed from: private */
    public final OnListUpdated onNearbyListUpdated = new $Lambda$vvo1Hidt87pwA0OrMywwrJjt1rU(this);
    private final SubscriptionPool<UUID, Boolean> typingUsersPool = new SubscriptionPool<>();
    @Nonnull
    private final UserManager userManager;

    public ChatterList(@Nonnull final UserManager userManager2) {
        this.userManager = userManager2;
        this.daoSession = userManager2.getDaoSession();
        this.chatterListPool.setRequestOnce(true);
        this.friendManager = new FriendManager(userManager2, this.daoSession, this);
        this.groupManager = new GroupManager(userManager2, this.daoSession, this);
        this.activeChattersManager = new ActiveChattersManager(userManager2, this.daoSession, this);
        new RequestFinalProcessor<UUID, Float>(this.nearbyDistancePool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public Float processRequest(@Nonnull UUID uuid) throws Throwable {
                SLModules modules;
                SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit();
                if (activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null) {
                    return null;
                }
                return modules.minimap.getDistanceToUser(uuid);
            }
        };
        new RequestFinalProcessor<UUID, Boolean>(this.typingUsersPool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public Boolean processRequest(@Nonnull UUID uuid) throws Throwable {
                SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    return activeAgentCircuit.isUserTyping(uuid);
                }
                return false;
            }
        };
        new RequestFinalProcessor<ChatterListType, ImmutableList<ChatterDisplayData>>(this.chatterListPool, userManager2.getDatabaseExecutor()) {

            /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues  reason: not valid java name */
            private static final /* synthetic */ int[] f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues = null;
            final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$users$manager$ChatterListType;

            /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues  reason: not valid java name */
            private static /* synthetic */ int[] m296getcomlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues() {
                if (f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues != null) {
                    return f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues;
                }
                int[] iArr = new int[ChatterListType.values().length];
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
                f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues = iArr;
                return iArr;
            }

            /* access modifiers changed from: protected */
            public void cancelRequest(@Nonnull ChatterListType chatterListType) {
                ChatterDisplayDataList chatterDisplayDataList = (ChatterDisplayDataList) ChatterList.this.chatterLists.remove(chatterListType);
                if (chatterDisplayDataList != null) {
                    chatterDisplayDataList.dispose();
                }
            }

            /* access modifiers changed from: protected */
            public ImmutableList<ChatterDisplayData> processRequest(@Nonnull ChatterListType chatterListType) {
                ChatterDisplayDataList chatterDisplayDataList = (ChatterDisplayDataList) ChatterList.this.chatterLists.get(chatterListType);
                if (chatterDisplayDataList == null) {
                    switch (m296getcomlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues()[chatterListType.ordinal()]) {
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
                            chatterDisplayDataList = new NearbyChattersDisplayDataList(userManager2, ChatterList.this.onNearbyListUpdated);
                            break;
                    }
                    chatterDisplayDataList.requestRefresh(userManager2.getDatabaseExecutor());
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

    /* synthetic */ void notifyNearbyListUpdate() {
        notifyListUpdated(ChatterListType.Nearby);
    }

    /* access modifiers changed from: package-private */
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
