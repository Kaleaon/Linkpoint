// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import java.util.Collections;
import java.util.EnumMap;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import javax.annotation.Nonnull;

public class ChatterList
{
    @Nonnull
    private final ActiveChattersManager activeChattersManager;
    private final SubscriptionPool<ChatterListType, ImmutableList<ChatterDisplayData>> chatterListPool;
    private final Map<ChatterListType, ChatterDisplayDataList> chatterLists;
    @Nonnull
    private final DaoSession daoSession;
    @Nonnull
    private final FriendManager friendManager;
    @Nonnull
    private final GroupManager groupManager;
    private final SubscriptionPool<UUID, Float> nearbyDistancePool;
    private final OnListUpdated onNearbyListUpdated;
    private final SubscriptionPool<UUID, Boolean> typingUsersPool;
    @Nonnull
    private final UserManager userManager;
    
    public ChatterList(@Nonnull final UserManager userManager) {
        this.chatterListPool = new SubscriptionPool<ChatterListType, ImmutableList<ChatterDisplayData>>();
        this.chatterLists = Collections.synchronizedMap(new EnumMap<ChatterListType, ChatterDisplayDataList>(ChatterListType.class));
        this.nearbyDistancePool = new SubscriptionPool<UUID, Float>();
        this.typingUsersPool = new SubscriptionPool<UUID, Boolean>();
        this.onNearbyListUpdated = new _$Lambda$vvo1Hidt87pwA0OrMywwrJjt1rU(this);
        this.userManager = userManager;
        this.daoSession = userManager.getDaoSession();
        this.chatterListPool.setRequestOnce(true);
        this.friendManager = new FriendManager(userManager, this.daoSession, this);
        this.groupManager = new GroupManager(userManager, this.daoSession, this);
        this.activeChattersManager = new ActiveChattersManager(userManager, this.daoSession, this);
        new RequestFinalProcessor<UUID, Float>(this.nearbyDistancePool, userManager.getDatabaseExecutor()) {
            @Override
            protected Float processRequest(@Nonnull final UUID uuid) throws Throwable {
                final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    final SLModules modules = activeAgentCircuit.getModules();
                    if (modules != null) {
                        return modules.minimap.getDistanceToUser(uuid);
                    }
                }
                return null;
            }
        };
        new RequestFinalProcessor<UUID, Boolean>(this.typingUsersPool, userManager.getDatabaseExecutor()) {
            @Override
            protected Boolean processRequest(@Nonnull final UUID uuid) throws Throwable {
                final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    return activeAgentCircuit.isUserTyping(uuid);
                }
                return false;
            }
        };
        new RequestFinalProcessor<ChatterListType, ImmutableList<ChatterDisplayData>>(this.chatterListPool, userManager.getDatabaseExecutor()) {
            private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues() {
                if (ChatterList$3.-com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues != null) {
                    return ChatterList$3.-com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues;
                }
                int[] -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues = new int[ChatterListType.values().length];
                while (true) {
                    try {
                        -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues[ChatterListType.Active.ordinal()] = 1;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues[ChatterListType.Friends.ordinal()] = 2;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues[ChatterListType.FriendsOnline.ordinal()] = 3;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues[ChatterListType.Groups.ordinal()] = 4;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues[ChatterListType.Nearby.ordinal()] = 5;
                                        return -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues;
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError2) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError3) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError4) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError5) {
                        continue;
                    }
                    break;
                }
            }
            
            @Override
            protected void cancelRequest(@Nonnull final ChatterListType chatterListType) {
                final ChatterDisplayDataList list = ChatterList.this.chatterLists.remove(chatterListType);
                if (list != null) {
                    list.dispose();
                }
            }
            
            @Override
            protected ImmutableList<ChatterDisplayData> processRequest(@Nonnull final ChatterListType chatterListType) {
                ChatterDisplayDataList list2;
                final ChatterDisplayDataList list = list2 = ChatterList.this.chatterLists.get(chatterListType);
                if (list == null) {
                    switch (-getcom-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues()[chatterListType.ordinal()]) {
                        default: {
                            list2 = list;
                            break;
                        }
                        case 1: {
                            list2 = ChatterList.this.activeChattersManager.getActiveChattersList();
                            break;
                        }
                        case 2: {
                            list2 = ChatterList.this.friendManager.getFriendList();
                            break;
                        }
                        case 3: {
                            list2 = ChatterList.this.friendManager.getFriendsOnlineList();
                            break;
                        }
                        case 4: {
                            list2 = ChatterList.this.groupManager.getGroupList();
                            break;
                        }
                        case 5: {
                            list2 = new NearbyChattersDisplayDataList(userManager, ChatterList.this.onNearbyListUpdated);
                            break;
                        }
                    }
                    list2.requestRefresh(userManager.getDatabaseExecutor());
                    ChatterList.this.chatterLists.put(chatterListType, list2);
                }
                return list2.getChatterList();
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
    
    void notifyListUpdated(final ChatterListType chatterListType) {
        this.chatterListPool.requestUpdate(chatterListType);
    }
    
    public void updateDistanceToAllUsers() {
        this.nearbyDistancePool.requestUpdateAll();
    }
    
    public void updateDistanceToUser(final UUID uuid) {
        this.nearbyDistancePool.requestUpdate(uuid);
    }
    
    public void updateList(final ChatterListType chatterListType) {
        final ChatterDisplayDataList list = this.chatterLists.get(chatterListType);
        if (list != null) {
            list.requestRefresh(this.userManager.getDatabaseExecutor());
        }
    }
    
    public void updateUserTypingStatus(final UUID uuid) {
        this.typingUsersPool.requestUpdate(uuid);
    }
}
