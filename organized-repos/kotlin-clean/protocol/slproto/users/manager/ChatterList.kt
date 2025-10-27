package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.dao.DaoSession
import com.linkpoint.react.RequestFinalProcessor
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.modules.SLModules
import java.util.Collections
import java.util.EnumMap
import java.util.Map
import java.util.UUID
import javax.annotation.Nonnull

class ChatterList {
    /* access modifiers changed from: private */
    val ActiveChattersManager activeChattersManager
    private val SubscriptionPool<ChatterListType, ImmutableList<ChatterDisplayData>> chatterListPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val Map<ChatterListType, ChatterDisplayDataList> chatterLists = Collections.synchronizedMap(EnumMap(ChatterListType.class))
    private val DaoSession daoSession
    /* access modifiers changed from: private */
    val FriendManager friendManager
    /* access modifiers changed from: private */
    val GroupManager groupManager
    private val SubscriptionPool<UUID, Float> nearbyDistancePool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val OnListUpdated onNearbyListUpdated = $Lambda$vvo1Hidt87pwA0OrMywwrJjt1rU(this)
    private val SubscriptionPool<UUID, Boolean> typingUsersPool = SubscriptionPool<>()
    private val UserManager userManager

    public ChatterList(final UserManager userManager2) {
        this.userManager = userManager2
        this.daoSession = userManager2.getDaoSession()
        this.chatterListPool.setRequestOnce(true)
        this.friendManager = FriendManager(userManager2, this.daoSession, this)
        this.groupManager = GroupManager(userManager2, this.daoSession, this)
        this.activeChattersManager = ActiveChattersManager(userManager2, this.daoSession, this)
        RequestFinalProcessor<UUID, Float>(this.nearbyDistancePool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public Float processRequest(UUID uuid) throws Throwable {
                SLModules modules
                SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit()
                if (activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null) {
                    return null
                }
                return modules.minimap.getDistanceToUser(uuid)
            }
        }
        RequestFinalProcessor<UUID, Boolean>(this.typingUsersPool, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public Boolean processRequest(UUID uuid) throws Throwable {
                SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit()
                if (activeAgentCircuit != null) {
                    return activeAgentCircuit.isUserTyping(uuid)
                }
                return false
            }
        }
        RequestFinalProcessor<ChatterListType, ImmutableList<ChatterDisplayData>>(this.chatterListPool, userManager2.getDatabaseExecutor()) {

            /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues  reason: not valid java name */
            private const val /* synthetic */ IntArray f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues = null
            final /* synthetic */ IntArray $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$users$manager$ChatterListType

            /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-manager-ChatterListTypeSwitchesValues  reason: not valid java name */
            @JvmStatic
private /* synthetic */ IntArray m296getcomlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues() {
                if (f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues != null) {
                    return f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues
                }
                IntArray iArr = Int[ChatterListType.values().length]
                try {
                    iArr[ChatterListType.Active.ordinal()] = 1
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[ChatterListType.Friends.ordinal()] = 2
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[ChatterListType.FriendsOnline.ordinal()] = 3
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[ChatterListType.Groups.ordinal()] = 4
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[ChatterListType.Nearby.ordinal()] = 5
                } catch (NoSuchFieldError e5) {
                }
                f225comlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues = iArr
                return iArr
            }

            /* access modifiers changed from: protected */
            fun cancelRequest(ChatterListType chatterListType) {
                ChatterDisplayDataList chatterDisplayDataList = (ChatterDisplayDataList) ChatterList.this.chatterLists.remove(chatterListType)
                if (chatterDisplayDataList != null) {
                    chatterDisplayDataList.dispose()
                }
            }

            /* access modifiers changed from: protected */
            public ImmutableList<ChatterDisplayData> processRequest(ChatterListType chatterListType) {
                ChatterDisplayDataList chatterDisplayDataList = (ChatterDisplayDataList) ChatterList.this.chatterLists.get(chatterListType)
                if (chatterDisplayDataList == null) {
                    switch (m296getcomlumiyaviewerlumiyaslprotousersmanagerChatterListTypeSwitchesValues()[chatterListType.ordinal()]) {
                        case 1:
                            chatterDisplayDataList = ChatterList.this.activeChattersManager.getActiveChattersList()
                            break
                        case 2:
                            chatterDisplayDataList = ChatterList.this.friendManager.getFriendList()
                            break
                        case 3:
                            chatterDisplayDataList = ChatterList.this.friendManager.getFriendsOnlineList()
                            break
                        case 4:
                            chatterDisplayDataList = ChatterList.this.groupManager.getGroupList()
                            break
                        case 5:
                            chatterDisplayDataList = NearbyChattersDisplayDataList(userManager2, ChatterList.this.onNearbyListUpdated)
                            break
                    }
                    chatterDisplayDataList.requestRefresh(userManager2.getDatabaseExecutor())
                    ChatterList.this.chatterLists.put(chatterListType, chatterDisplayDataList)
                }
                return chatterDisplayDataList.getChatterList()
            }
        }
    }

    public ActiveChattersManager getActiveChattersManager() {
        return this.activeChattersManager
    }

    public Subscribable<ChatterListType, ImmutableList<ChatterDisplayData>> getChatterList() {
        return this.chatterListPool
    }

    public Subscribable<UUID, Float> getDistanceToUser() {
        return this.nearbyDistancePool
    }

    public FriendManager getFriendManager() {
        return this.friendManager
    }

    public GroupManager getGroupManager() {
        return this.groupManager
    }

    public Subscribable<UUID, Boolean> getUserTypingStatus() {
        return this.typingUsersPool
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ChatterList_4733  reason: not valid java name */
    public /* synthetic */ Unit m295lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ChatterList_4733() {
        notifyListUpdated(ChatterListType.Nearby)
    }

    /* access modifiers changed from: package-private */
    fun notifyListUpdated(ChatterListType chatterListType) {
        this.chatterListPool.requestUpdate(chatterListType)
    }

    fun updateDistanceToAllUsers() {
        this.nearbyDistancePool.requestUpdateAll()
    }

    fun updateDistanceToUser(UUID uuid) {
        this.nearbyDistancePool.requestUpdate(uuid)
    }

    fun updateList(ChatterListType chatterListType) {
        ChatterDisplayDataList chatterDisplayDataList = this.chatterLists.get(chatterListType)
        if (chatterDisplayDataList != null) {
            chatterDisplayDataList.requestRefresh(this.userManager.getDatabaseExecutor())
        }
    }

    fun updateUserTypingStatus(UUID uuid) {
        this.typingUsersPool.requestUpdate(uuid)
    }
}
