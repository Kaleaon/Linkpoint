package com.linkpoint.ui.linkpoint2.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.linkpoint.LinkpointApp
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.friends.FriendData
import com.linkpoint.ui.friends.FriendStatus
import com.linkpoint.ui.friends.FriendsScreen
import com.linkpoint.ui.groups.GroupData
import com.linkpoint.ui.groups.GroupsScreen
import com.linkpoint.ui.inventory.InventoryItemData
import com.linkpoint.ui.inventory.InventoryScreen
import com.linkpoint.ui.linkpoint2.screens.EventCard
import com.linkpoint.ui.linkpoint2.screens.GraphicsSettingsScreen
import com.linkpoint.ui.linkpoint2.screens.GraphicsState
import com.linkpoint.ui.linkpoint2.screens.GridEntry
import com.linkpoint.ui.linkpoint2.screens.GridManagementScreen
import com.linkpoint.ui.linkpoint2.screens.GroupMember
import com.linkpoint.ui.linkpoint2.screens.GroupProfileScreen
import com.linkpoint.ui.linkpoint2.screens.NotificationItem
import com.linkpoint.ui.linkpoint2.screens.NotificationKind
import com.linkpoint.ui.linkpoint2.screens.NotificationsScreen
import com.linkpoint.ui.linkpoint2.screens.PlaceCard
import com.linkpoint.ui.linkpoint2.screens.PlaceDetailScreen
import com.linkpoint.ui.linkpoint2.screens.PlacesScreen
import com.linkpoint.ui.linkpoint2.screens.PrivacySettingsScreen
import com.linkpoint.ui.linkpoint2.screens.PrivacyState
import com.linkpoint.ui.linkpoint2.screens.SavedOutfit
import com.linkpoint.ui.linkpoint2.screens.VoiceDeepScreen
import com.linkpoint.ui.linkpoint2.screens.VoiceParticipant as UiVoiceParticipant
import com.linkpoint.ui.linkpoint2.screens.WalletScreen
import com.linkpoint.ui.linkpoint2.screens.WalletTransaction
import com.linkpoint.ui.linkpoint2.screens.WearableSlot
import com.linkpoint.ui.linkpoint2.screens.OutfitComposerScreen
import com.linkpoint.ui.linkpoint2.screens.OutfitPickerScreen
import com.linkpoint.ui.linkpoint2.screens.EventsScreen
import com.linkpoint.ui.map.MapScreen
import com.linkpoint.ui.minimap.MinimapScreen
import com.linkpoint.ui.people.NearbyPeopleFilter
import com.linkpoint.ui.people.NearbyPeopleScreen
import com.linkpoint.ui.people.NearbyPerson
import com.linkpoint.ui.profile.ProfileData
import com.linkpoint.ui.profile.ProfileScreen
import com.linkpoint.ui.search.ComposeSearchResult
import com.linkpoint.ui.search.SearchScreen
import com.linkpoint.ui.teleport.TeleportHistoryEntry
import com.linkpoint.ui.teleport.TeleportHistoryScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * "Real-backend" L2 navigation routes.
 *
 * The original [com.linkpoint.ui.linkpoint2.Linkpoint2NavGraph] wired every
 * post-login surface to placeholder demo data (`L2Demo`), with no-op send /
 * tap / refresh handlers. To the user that looked like a working app
 * populated with sample residents, but every action silently dropped on the
 * floor and no live state was ever shown — chat, IM, friends, inventory,
 * groups, wallet, teleport history, notifications and grid management were
 * all fake.
 *
 * Each route in this file observes the appropriate [LinkpointApp] manager
 * and routes user actions back through it. When a manager isn't initialised
 * yet (pre-login) or a feature doesn't have a backend implementation, the
 * route renders an honest empty state via [UiLoadState.Empty] rather than
 * lying to the user.
 */

private fun managerOrNull(block: () -> Unit): Boolean = try { block(); true } catch (_: Throwable) { false }

@Composable
fun L2FriendsRoute(
    onNavigateBack: () -> Unit,
    onOpenIM: (FriendData) -> Unit,
    onViewProfile: (FriendData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()

    if (app == null || !app.isFriendsManagerInitialized()) {
        FriendsScreen(
            friends = emptyList(),
            uiLoadState = UiLoadState.Empty(
                title = "No friend list yet",
                message = "Friends populate after login from the simulator's buddy list.",
            ),
            onRetry = {},
            onNavigateBack = onNavigateBack,
            onOpenIM = onOpenIM,
            onTeleportTo = {},
            onViewProfile = onViewProfile,
            onRemoveFriend = {},
            onAddFriend = {},
            modifier = modifier,
        )
        return
    }

    val onlineSet by app.friendsManager.onlineFriends.collectAsState()
    var raw by remember { mutableStateOf(app.friendsManager.getAllFriends()) }
    LaunchedEffect(Unit) {
        app.friendsManager.friendsFlow.collect { raw = app.friendsManager.getAllFriends() }
    }

    val friends = raw.map { f ->
        val isOnline = f.agentId in onlineSet || f.isOnline
        FriendData(
            id = f.agentId,
            name = f.name,
            status = if (isOnline) FriendStatus.ONLINE else FriendStatus.OFFLINE,
            location = null,
            canSeeOnline = f.canSeeOnline,
            canSeeMap = f.canTrack,
            canModifyObjects = f.canModifyObjects,
        )
    }.sortedWith(compareByDescending<FriendData> { it.status == FriendStatus.ONLINE }.thenBy { it.name })

    FriendsScreen(
        friends = friends,
        uiLoadState = if (friends.isEmpty()) {
            UiLoadState.Empty(
                title = "No friends yet",
                message = "Add friends from a profile or use the Add Friend dialog.",
            )
        } else UiLoadState.Content,
        onRetry = { raw = app.friendsManager.getAllFriends() },
        onNavigateBack = onNavigateBack,
        onOpenIM = onOpenIM,
        onTeleportTo = { friend ->
            scope.launch { app.friendsManager.teleportTo(friend.id) }
        },
        onViewProfile = onViewProfile,
        onRemoveFriend = { friend ->
            scope.launch { app.friendsManager.removeFriend(friend.id) }
        },
        onAddFriend = {
            // Add-friend UX needs name input — handled by AddFriendDialog
            // which is currently a separate Activity. Wiring the dialog into
            // the Compose graph is a follow-up.
        },
        modifier = modifier,
    )
}

@Composable
fun L2NearbyPeopleRoute(
    onNavigateBack: () -> Unit,
    onSendIM: (NearbyPerson) -> Unit,
    onViewProfile: (NearbyPerson) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    var filter by remember { mutableStateOf(NearbyPeopleFilter.ALL) }
    var refreshTick by remember { mutableStateOf(0) }

    if (app == null || !app.isAvatarManagerInitialized()) {
        NearbyPeopleScreen(
            people = emptyList(),
            selectedFilter = filter,
            onFilterChange = { filter = it },
            isLoading = false,
            emptyMessage = "Connect to a region to see who's nearby.",
            onRefresh = { refreshTick++ },
            onSendIM = onSendIM,
            onAddFriend = {},
            onViewProfile = onViewProfile,
            onNavigateBack = onNavigateBack,
            modifier = modifier,
        )
        return
    }

    // Re-derive on each refresh tick. Avatars stream in via the UDP path so
    // a manual refresh is mostly cosmetic; we rely on the user reopening
    // the screen to pick up scene changes.
    val people = remember(refreshTick) {
        val me = app.avatarManager.getMyAvatar()
        val pos = me?.position ?: com.linkpoint.protocol.types.LLVector3.zero()
        val friendIds: Set<UUID> = if (app.isFriendsManagerInitialized()) {
            app.friendsManager.getAllFriends().map { it.agentId }.toSet()
        } else emptySet()
        val nameLookup: (UUID) -> String = { id ->
            val cached = runCatching { app.displayNameManager.getCachedDisplayName(id) }.getOrNull()
            cached?.displayName?.takeIf { it.isNotBlank() }
                ?: cached?.username
                ?: "Resident ${id.toString().take(8)}"
        }
        app.avatarManager.getNearbyAvatars(pos, 256f)
            .filter { me == null || it.agentId != me.agentId }
            .map { av ->
                val dx = av.position.x - pos.x
                val dy = av.position.y - pos.y
                val dz = av.position.z - pos.z
                NearbyPerson(
                    id = av.agentId,
                    name = nameLookup(av.agentId),
                    distance = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz),
                    isFriend = av.agentId in friendIds,
                )
            }
            .let { list ->
                when (filter) {
                    NearbyPeopleFilter.ALL -> list
                    NearbyPeopleFilter.FRIENDS -> list.filter { it.isFriend }
                    NearbyPeopleFilter.STRANGERS -> list.filter { !it.isFriend }
                }
            }
            .sortedBy { it.distance }
    }

    NearbyPeopleScreen(
        people = people,
        selectedFilter = filter,
        onFilterChange = { filter = it },
        isLoading = false,
        emptyMessage = "No other residents detected within 256 m.",
        onRefresh = { refreshTick++ },
        onSendIM = onSendIM,
        onAddFriend = { person ->
            if (app.isFriendsManagerInitialized()) {
                app.applicationScope.launch { app.friendsManager.offerFriendship(person.id) }
            }
        },
        onViewProfile = onViewProfile,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
fun L2InventoryRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    if (app == null || !app.isInventoryManagerInitialized()) {
        InventoryScreen(
            items = emptyList(),
            breadcrumb = listOf("My Inventory"),
            uiLoadState = UiLoadState.Empty(
                title = "Inventory not connected",
                message = "Log in to see your items.",
            ),
            onRetry = {},
            onItemClick = {},
            onNavigateBack = onNavigateBack,
            onNavigateUp = {},
            modifier = modifier,
        )
        return
    }
    val isLoading by app.inventoryManager.isLoading.collectAsState()
    InventoryScreen(
        items = emptyList<InventoryItemData>(),
        breadcrumb = listOf("My Inventory"),
        uiLoadState = if (isLoading) UiLoadState.Loading() else UiLoadState.Empty(
            title = "Inventory loading",
            message = "AISv3 fetch is in progress. Folder rendering is wired in a follow-up.",
        ),
        onRetry = { app.applicationScope.launch { app.inventoryManager.warmFetch() } },
        onItemClick = {},
        onNavigateBack = onNavigateBack,
        onNavigateUp = {},
        modifier = modifier,
    )
}

@Composable
fun L2GroupsRoute(
    onNavigateBack: () -> Unit,
    onOpenGroupChat: (GroupData) -> Unit,
    onViewGroupInfo: (GroupData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()

    if (app == null || !app.isGroupsManagerInitialized()) {
        GroupsScreen(
            myGroups = emptyList(),
            searchResults = emptyList(),
            onNavigateBack = onNavigateBack,
            onOpenGroupChat = onOpenGroupChat,
            onViewGroupInfo = onViewGroupInfo,
            onSetActiveGroup = {},
            onLeaveGroup = {},
            onJoinGroup = {},
            onSearch = {},
            modifier = modifier,
        )
        return
    }

    val activeGroup by app.groupsManager.activeGroup.collectAsState()
    var refreshTick by remember { mutableStateOf(0) }
    val raw = remember(refreshTick) { app.groupsManager.getAllGroups() }

    LaunchedEffect(Unit) {
        app.groupsManager.groupEvents.collect { refreshTick++ }
    }

    val myGroups = raw.map { g ->
        GroupData(
            id = g.groupId,
            name = g.name,
            memberCount = g.memberCount,
            isActive = g.groupId == activeGroup,
            charter = g.charter,
            isOpen = g.isOpenEnrollment,
            contribution = g.contribution,
        )
    }

    GroupsScreen(
        myGroups = myGroups,
        searchResults = emptyList(),
        onNavigateBack = onNavigateBack,
        onOpenGroupChat = onOpenGroupChat,
        onViewGroupInfo = onViewGroupInfo,
        onSetActiveGroup = { g -> scope.launch { app.groupsManager.setActiveGroup(g.id) } },
        onLeaveGroup = { g -> scope.launch { app.groupsManager.leaveGroup(g.id) } },
        onJoinGroup = { /* requires GroupsManager.joinGroup; not yet exposed */ },
        onSearch = { /* SearchManager.searchGroups path is wired via SEARCH route */ },
        modifier = modifier,
    )
}

@Composable
fun L2GroupProfileRoute(
    groupId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val gid = runCatching { UUID.fromString(groupId) }.getOrNull()
    val group = if (app != null && app.isGroupsManagerInitialized() && gid != null) {
        app.groupsManager.getAllGroups().firstOrNull { it.groupId == gid }
    } else null

    GroupProfileScreen(
        groupTag = group?.name?.take(4)?.uppercase() ?: groupId.take(4).uppercase(),
        groupName = group?.name ?: "Unknown group",
        description = group?.charter ?: "",
        membersOnline = 0,
        membersTotal = group?.memberCount ?: 0,
        members = emptyList<GroupMember>(),
        role = "Member",
        onBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
fun L2WalletRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()

    if (app == null) {
        WalletScreen(
            balanceLinden = 0L,
            usdEquivalent = 0.0,
            weeklyIn = 0L,
            weeklyOut = 0L,
            transactions = emptyList(),
            onBack = onBack,
            onSend = {},
            onRequest = {},
            onBuy = {},
            onRefresh = {},
            modifier = modifier,
        )
        return
    }

    // EconomyManager is constructed during managerInit but doesn't have an
    // explicit isInitialized checker. Try-catch guards the lateinit access.
    val economyAvailable = managerOrNull { app.economyManager }
    val balance: Int = if (economyAvailable) {
        val b by app.economyManager.balance.collectAsState()
        b
    } else 0

    // L$/USD pulled from Linden Lab's published LindeX feed (15-minute
    // cache). Falls back to a 250:1 estimate if the feed hasn't loaded
    // yet so the UI doesn't show 0.00 USD on first open.
    val lindex by app.liveDataFeedClient.lindex.collectAsState()
    LaunchedEffect(Unit) { app.liveDataFeedClient.fetchLindex() }

    val transactions = remember { mutableStateListOf<WalletTransaction>() }
    LaunchedEffect(economyAvailable) {
        if (!economyAvailable) return@LaunchedEffect
        app.economyManager.transactionEvents.collect { tx ->
            // Project the backend TransactionEvent onto the UI model. The
            // backend type carries amount/payer/payee/description but the
            // exact field names vary by event subclass — toString() is a
            // safe stringification until a per-subtype mapping is wired.
            transactions.add(
                0,
                WalletTransaction(
                    id = UUID.randomUUID().toString(),
                    title = tx.toString(),
                    subtitle = "",
                    amountLinden = 0L,
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    isIncome = true,
                )
            )
        }
    }

    WalletScreen(
        balanceLinden = balance.toLong(),
        usdEquivalent = lindex?.lindenToUsd(balance.toLong()) ?: (balance / 250.0),
        weeklyIn = transactions.filter { it.isIncome }.sumOf { it.amountLinden },
        weeklyOut = transactions.filter { !it.isIncome }.sumOf { it.amountLinden },
        transactions = transactions,
        onBack = onBack,
        onSend = { /* requires recipient picker — not yet implemented */ },
        onRequest = { /* L$ request flow is not implemented */ },
        onBuy = { /* L$ purchase requires LindenLab web checkout */ },
        onRefresh = {
            if (economyAvailable) scope.launch { app.economyManager.requestBalance() }
            scope.launch { app.liveDataFeedClient.fetchLindex(force = true) }
        },
        modifier = modifier,
    )
}

@Composable
fun L2NotificationsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()

    val notifAvailable = app != null && app.isNotificationManagerInitialized()

    // Per-session events (IM offers, friendship offers, money transfers,
    // system messages) come from NotificationManager. Public grid-status
    // incidents (maintenance, deployments, outages) come from the LL
    // status RSS. We merge both so the inbox is populated whether or
    // not a session is live.
    val sessionItems = remember { mutableStateListOf<NotificationItem>() }
    LaunchedEffect(notifAvailable) {
        if (!notifAvailable) return@LaunchedEffect
        sessionItems.clear()
        app!!.notificationManager.getAllNotifications().forEach { sessionItems.add(it.toUi()) }
        app.notificationManager.notificationEvents.collect {
            sessionItems.clear()
            app.notificationManager.getAllNotifications().forEach { sessionItems.add(it.toUi()) }
        }
    }

    val incidents by (app?.liveDataFeedClient?.incidents
        ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList<com.linkpoint.network.feeds.StatusIncident>()))
        .collectAsState()
    val dismissed = remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(Unit) {
        app?.let { scope.launch { it.liveDataFeedClient.fetchStatusIncidents() } }
    }

    val statusItems = incidents
        .filter { "rss:${it.title}" !in dismissed.value }
        .take(30)
        .map { incident ->
            NotificationItem(
                id = "rss:${incident.title}",
                kind = NotificationKind.Region,
                title = "Grid status",
                body = incident.title,
                timestamp = formatAgo(incident.publishedAtMs),
                acceptable = false,
                mentioned = false,
            )
        }

    val items = sessionItems.filter { it.id !in dismissed.value } + statusItems

    NotificationsScreen(
        items = items,
        onBack = onBack,
        onClearAll = {
            if (notifAvailable) app!!.notificationManager.clearAll()
            sessionItems.clear()
            dismissed.value = dismissed.value + incidents.map { "rss:${it.title}" }
        },
        onAccept = { item ->
            if (notifAvailable && !item.id.startsWith("rss:")) {
                runCatching { UUID.fromString(item.id) }.getOrNull()?.let {
                    app!!.notificationManager.markAsRead(it)
                }
            }
            dismissed.value = dismissed.value + item.id
            sessionItems.removeAll { it.id == item.id }
        },
        onDecline = { item ->
            if (notifAvailable && !item.id.startsWith("rss:")) {
                runCatching { UUID.fromString(item.id) }.getOrNull()?.let {
                    app!!.notificationManager.markAsRead(it)
                }
            }
            dismissed.value = dismissed.value + item.id
            sessionItems.removeAll { it.id == item.id }
        },
        onTap = {},
        modifier = modifier,
    )
}

private fun com.linkpoint.notifications.SLNotification.toUi(): NotificationItem {
    val kind = when (type) {
        com.linkpoint.notifications.NotificationType.INSTANT_MESSAGE -> NotificationKind.Im
        com.linkpoint.notifications.NotificationType.GROUP_CHAT,
        com.linkpoint.notifications.NotificationType.GROUP_NOTICE -> NotificationKind.GroupPing
        com.linkpoint.notifications.NotificationType.FRIENDSHIP_OFFER -> NotificationKind.FriendRequest
        com.linkpoint.notifications.NotificationType.TRANSACTION -> NotificationKind.Money
        com.linkpoint.notifications.NotificationType.SYSTEM,
        com.linkpoint.notifications.NotificationType.TELEPORT_OFFER -> NotificationKind.Region
        com.linkpoint.notifications.NotificationType.SCRIPT_DIALOG,
        com.linkpoint.notifications.NotificationType.PERMISSION_REQUEST -> NotificationKind.Generic
    }
    return NotificationItem(
        id = id.toString(),
        kind = kind,
        title = title,
        body = message,
        timestamp = formatAgo(timestamp),
        acceptable = type == com.linkpoint.notifications.NotificationType.FRIENDSHIP_OFFER ||
            type == com.linkpoint.notifications.NotificationType.TELEPORT_OFFER,
        mentioned = false,
    )
}

private fun formatAgo(epochMs: Long): String {
    if (epochMs <= 0L) return ""
    val ageMs = System.currentTimeMillis() - epochMs
    return when {
        ageMs < 0L -> "scheduled"
        ageMs < 60_000L -> "just now"
        ageMs < 3_600_000L -> "${ageMs / 60_000L}m"
        ageMs < 86_400_000L -> "${ageMs / 3_600_000L}h"
        else -> "${ageMs / 86_400_000L}d"
    }
}

@Composable
fun L2TeleportHistoryRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()

    if (app == null) {
        TeleportHistoryScreen(
            history = emptyList(),
            onTeleportTo = {},
            onDeleteEntry = {},
            onClearHistory = {},
            onNavigateBack = onNavigateBack,
            modifier = modifier,
        )
        return
    }

    val rawHistory by app.sessionManager.teleportHistory.collectAsState()
    val history = rawHistory.mapIndexed { idx, e ->
        TeleportHistoryEntry(
            id = "${e.regionName}/${e.x}/${e.y}/${e.z}/$idx",
            regionName = e.regionName,
            position = "${e.x}, ${e.y}, ${e.z}",
            timestamp = e.timestamp,
            slurl = e.toSLURL(),
        )
    }.reversed()

    TeleportHistoryScreen(
        history = history,
        onTeleportTo = { entry ->
            if (app.isTeleportManagerInitialized()) {
                scope.launch { app.teleportManager.teleportToSLURL(entry.slurl) }
            }
        },
        onDeleteEntry = { /* SessionManager.teleportHistory has no delete API today */ },
        onClearHistory = { /* SessionManager.teleportHistory has no clear API today */ },
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
fun L2GridManagementRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val grids: List<GridEntry> = if (app == null) emptyList() else {
        app.gridManager.getAvailableGrids().map { g ->
            GridEntry(
                id = g.id,
                name = g.name,
                loginUrl = g.loginUri,
                builtIn = g.id == "agni" || g.id == "aditi",
                online = true,
            )
        }
    }
    GridManagementScreen(
        grids = grids,
        onBack = onBack,
        onAdd = { /* requires URI input — not yet wired */ },
        onEdit = { _ -> /* in-place edit dialog not yet wired */ },
        onDelete = { entry ->
            if (app != null && !entry.builtIn) {
                runCatching { app.gridManager.removeCustomGrid(entry.id) }
            }
        },
        modifier = modifier,
    )
}

@Composable
fun L2VoiceDeepRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()
    val available = app != null && managerOrNull { app.voiceManager }

    val participants: List<UiVoiceParticipant> = if (!available) emptyList() else {
        val map by app!!.voiceManager.participants.collectAsState()
        map.values.map { p ->
            UiVoiceParticipant(
                id = p.agentId.toString(),
                name = p.displayName,
                volume = p.volume,
                muted = p.isSelfMuted || p.isMutedByModerator,
                distanceMeters = 0,
                speaking = p.isSpeaking,
            )
        }
    }

    VoiceDeepScreen(
        selfName = app?.sessionManager?.getAvatarName()?.ifBlank { "You" } ?: "You",
        participants = participants,
        onBack = onBack,
        onTogglePtt = {
            if (available) app!!.voiceManager.toggleMute()
        },
        onMuteParticipant = { p ->
            if (available) {
                runCatching { UUID.fromString(p.id) }.getOrNull()?.let { id ->
                    scope.launch { app!!.voiceManager.muteParticipant(id) }
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
fun L2ProfileRoute(
    userId: String,
    isMe: Boolean,
    onNavigateBack: () -> Unit,
    onSendIM: () -> Unit,
    onTeleportToMe: () -> Unit,
    onEditMyProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()
    val targetId = if (isMe) app?.sessionManager?.getAgentId() else runCatching { UUID.fromString(userId) }.getOrNull()

    var profile by remember(targetId) { mutableStateOf<com.linkpoint.world.AvatarProfile?>(null) }
    LaunchedEffect(targetId) {
        if (targetId != null && app != null && app.isProfileManagerInitialized()) {
            profile = runCatching { app.profileManager.getAvatarProfile(targetId) }.getOrNull()
        }
    }

    val data = ProfileData(
        id = targetId ?: UUID(0L, 0L),
        displayName = profile?.displayName
            ?: (if (isMe) app?.sessionManager?.getAvatarName()?.ifBlank { "You" } ?: "You" else "Loading…"),
        username = profile?.userName ?: "",
        aboutText = profile?.aboutText ?: "",
        firstLifeText = profile?.firstLifeText ?: "",
        bornDate = null,
        partner = null,
        groups = profile?.memberOf ?: emptyList(),
        webUrl = null,
        isOnline = isMe || (app?.isFriendsManagerInitialized() == true && targetId != null && targetId in app.friendsManager.onlineFriends.value),
        location = null,
        isFriend = targetId != null && app?.isFriendsManagerInitialized() == true &&
            app.friendsManager.getAllFriends().any { it.agentId == targetId },
        isOwnProfile = isMe,
    )

    ProfileScreen(
        profile = data,
        onNavigateBack = onNavigateBack,
        onSendIM = { onSendIM() },
        onAddFriend = {
            if (app != null && app.isFriendsManagerInitialized() && targetId != null) {
                scope.launch { app.friendsManager.offerFriendship(targetId) }
            }
        },
        onTeleportTo = { onTeleportToMe() },
        onEditProfile = { onEditMyProfile() },
        onOpenWeb = {},
        modifier = modifier,
    )
}

@Composable
fun L2MapRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()
    val region = app?.sessionManager?.currentRegion?.collectAsState()?.value
    val pos = app?.avatarManager?.takeIf { app.isAvatarManagerInitialized() }
        ?.getMyAvatar()?.position
    MapScreen(
        currentRegion = region?.name ?: "Unknown region",
        currentPosition = if (pos != null) Offset(pos.x, pos.y) else Offset(128f, 128f),
        uiLoadState = if (region == null) UiLoadState.Empty(
            title = "Not in a region",
            message = "The world map populates after login.",
        ) else UiLoadState.Content,
        onRetry = {},
        onNavigateBack = onNavigateBack,
        onTeleportTo = { /* requires region picker — not yet wired */ },
        onTeleportHome = {
            if (app != null && app.isTeleportManagerInitialized()) {
                scope.launch { app.teleportManager.teleportHome() }
            }
        },
        onSearch = {},
        modifier = modifier,
    )
}

@Composable
fun L2MinimapRoute(
    onNavigateBack: () -> Unit,
    onOpenWorldMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val region = app?.sessionManager?.currentRegion?.collectAsState()?.value
    val me = app?.avatarManager?.takeIf { app.isAvatarManagerInitialized() }?.getMyAvatar()
    val pos = me?.position
    MinimapScreen(
        regionName = region?.name ?: "Unknown",
        avatarPosition = if (pos != null) Offset(pos.x, pos.y) else Offset(128f, 128f),
        avatarHeading = 0f,
        markers = emptyList(),
        onNavigateBack = onNavigateBack,
        onOpenWorldMap = onOpenWorldMap,
        onMarkerTapped = {},
        modifier = modifier,
    )
}

@Composable
fun L2SearchRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()
    var results by remember { mutableStateOf<List<ComposeSearchResult>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    SearchScreen(
        results = results,
        uiLoadState = if (loading) UiLoadState.Loading() else UiLoadState.Content,
        onRetry = {},
        onSearch = { query, category ->
            if (app == null || query.isBlank()) return@SearchScreen
            val available = managerOrNull { app.searchManager }
            if (!available) return@SearchScreen
            loading = true
            scope.launch {
                results = try {
                    when (category) {
                        com.linkpoint.ui.search.SearchCategory.PEOPLE ->
                            app.searchManager.searchPeople(query).results.map { p ->
                                ComposeSearchResult.PersonResult(
                                    id = p.agentId,
                                    name = p.displayName,
                                    description = p.userName,
                                    isOnline = p.isOnline,
                                )
                            }
                        com.linkpoint.ui.search.SearchCategory.PLACES ->
                            app.searchManager.searchPlaces(query).results.map { pl ->
                                ComposeSearchResult.PlaceResult(
                                    id = pl.parcelId,
                                    name = pl.name,
                                    description = pl.description,
                                    traffic = pl.traffic.toInt(),
                                    slurl = "secondlife://${pl.region}/${pl.location}",
                                )
                            }
                        com.linkpoint.ui.search.SearchCategory.GROUPS ->
                            app.searchManager.searchGroups(query).results.map { g ->
                                ComposeSearchResult.GroupResult(
                                    id = g.groupId,
                                    name = g.name,
                                    description = g.charter,
                                    memberCount = g.memberCount,
                                    isOpen = g.isOpen,
                                )
                            }
                        com.linkpoint.ui.search.SearchCategory.EVENTS ->
                            app.searchManager.searchEvents(query).results.map { e ->
                                ComposeSearchResult.EventResult(
                                    id = UUID(0L, e.eventId.toLong()),
                                    name = e.name,
                                    description = e.description,
                                )
                            }
                    }
                } catch (_: Exception) {
                    emptyList()
                }
                loading = false
            }
        },
        onResultClick = {},
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
fun L2PlacesRoute(
    onBack: () -> Unit,
    onSelect: (PlaceCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()
    var places by remember { mutableStateOf<List<PlaceCard>>(emptyList()) }
    LaunchedEffect(Unit) {
        if (app == null || !managerOrNull { app.searchManager }) return@LaunchedEffect
        scope.launch {
            places = try {
                // No "popular places" API exposed on SearchManager; default
                // to events as a reasonable proxy until DirFindQuery
                // featured-destinations is wired.
                app.searchManager.searchPlaces("").results.map { pl ->
                    PlaceCard(
                        id = pl.parcelId.toString(),
                        name = pl.name,
                        rating = pl.category,
                        traffic = pl.traffic.toInt(),
                        parcel = "${pl.location} · ${pl.region}",
                        coverGradient = listOf(
                            androidx.compose.ui.graphics.Color(0xFF1D4060),
                            androidx.compose.ui.graphics.Color(0xFF4A7BA8),
                        ),
                    )
                }
            } catch (_: Exception) { emptyList() }
        }
    }
    PlacesScreen(
        places = places,
        onBack = onBack,
        onSelect = onSelect,
        modifier = modifier,
    )
}

@Composable
fun L2PlaceDetailRoute(
    placeId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Without persisted PlaceCard repository or DirParcelInfo round-trip,
    // we honour the placeId but render a stub card. Future: wire to
    // ParcelManager.requestParcelInfo(parcelId).
    PlaceDetailScreen(
        place = PlaceCard(
            id = placeId ?: "unknown",
            name = "Unknown place",
            rating = "PG",
            traffic = 0,
            parcel = "",
            coverGradient = listOf(
                androidx.compose.ui.graphics.Color(0xFF1D4060),
                androidx.compose.ui.graphics.Color(0xFF4A7BA8),
            ),
        ),
        description = "Place details require a ParcelInfo round-trip; not yet wired.",
        whoIsHere = emptyList(),
        onBack = onBack,
        onTeleport = {},
        onSave = {},
        onShare = {},
        modifier = modifier,
    )
}

@Composable
fun L2EventsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val scope = rememberCoroutineScope()
    var events by remember { mutableStateOf<List<EventCard>>(emptyList()) }
    LaunchedEffect(Unit) {
        if (app == null || !managerOrNull { app.searchManager }) return@LaunchedEffect
        scope.launch {
            events = try {
                app.searchManager.searchEvents("").results.map { e ->
                    EventCard(
                        id = e.eventId.toString(),
                        title = e.name,
                        region = e.region,
                        time = SimpleDateFormat("EEE h a", Locale.getDefault())
                            .format(Date(e.dateUtc * 1000)),
                        category = e.category,
                    )
                }
            } catch (_: Exception) { emptyList() }
        }
    }
    EventsScreen(events = events, onBack = onBack, onSelect = {}, modifier = modifier)
}

@Composable
fun L2OutfitPickerRoute(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // OutfitManager exposes currentOutfit (List<UUID>) but no "saved
    // outfits" repository yet. Render an honest empty state until the
    // outfit-folder discovery path is implemented.
    OutfitPickerScreen(
        outfits = emptyList<SavedOutfit>(),
        onBack = onBack,
        onWear = {},
        onEdit = { _ -> onEdit() },
        modifier = modifier,
    )
}

@Composable
fun L2OutfitComposerRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    OutfitComposerScreen(
        avatarName = app?.sessionManager?.getAvatarName()?.ifBlank { "You" } ?: "You",
        slots = emptyList<WearableSlot>(),
        onBack = onBack,
        onSave = {},
        onWear = {},
        onTap = {},
        modifier = modifier,
    )
}

@Composable
fun L2GraphicsSettingsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Graphics state is currently ephemeral — there's no GraphicsPreferences
    // store wired up. The screen still functions for UI exploration but
    // changes are not persisted. Honest behaviour > fake persistence.
    var state by remember { mutableStateOf(GraphicsState()) }
    GraphicsSettingsScreen(
        initial = state,
        onBack = onBack,
        onChange = { state = it },
        modifier = modifier,
    )
}

@Composable
fun L2PrivacySettingsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    var state by remember { mutableStateOf(PrivacyState()) }

    val muteAvailable = app != null && managerOrNull { app.muteManager }
    // Recompose whenever the mute list changes so unblock actions reflect
    // immediately. chat.MuteManager exposes a `muteListChanged` epoch
    // StateFlow but no list flow, so we read the snapshot list and key
    // it on the epoch.
    val muteEpoch: Long = if (!muteAvailable) 0L else {
        val v by app!!.muteManager.muteListChanged.collectAsState()
        v
    }
    val muteList: List<com.linkpoint.chat.MuteManager.MuteEntry> =
        if (!muteAvailable) emptyList() else remember(muteEpoch) {
            app!!.muteManager.getAllMutes()
        }

    PrivacySettingsScreen(
        initial = state,
        blockedUsers = muteList.map { it.name },
        onBack = onBack,
        onUnblock = { name ->
            if (muteAvailable) {
                muteList.firstOrNull { it.name == name }?.let { entry ->
                    app!!.muteManager.unmute(entry.id)
                }
            }
        },
        onChange = { state = it },
        modifier = modifier,
    )
}
