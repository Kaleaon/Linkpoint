package com.linkpoint.ui.linkpoint2

import androidx.compose.ui.graphics.Color
import com.linkpoint.ui.chat.ChatChannel
import com.linkpoint.ui.chat.ChatMessage
import com.linkpoint.ui.chat.MessageStatus
import com.linkpoint.ui.chat.MessageType
import com.linkpoint.ui.friends.FriendData
import com.linkpoint.ui.friends.FriendStatus
import com.linkpoint.ui.linkpoint2.screens.ConversationSummary
import com.linkpoint.ui.linkpoint2.screens.EventCard
import com.linkpoint.ui.linkpoint2.screens.GraphicsState
import com.linkpoint.ui.linkpoint2.screens.GridEntry
import com.linkpoint.ui.linkpoint2.screens.GroupMember
import com.linkpoint.ui.linkpoint2.screens.NotificationItem
import com.linkpoint.ui.linkpoint2.screens.NotificationKind
import com.linkpoint.ui.linkpoint2.screens.PlaceCard
import com.linkpoint.ui.linkpoint2.screens.PrivacyState
import com.linkpoint.ui.linkpoint2.screens.SavedOutfit
import com.linkpoint.ui.linkpoint2.screens.VoiceParticipant
import com.linkpoint.ui.linkpoint2.screens.WalletTransaction
import com.linkpoint.ui.linkpoint2.screens.WearableSlot
import java.util.UUID

/**
 * Sample data used by the navigation graph when no live ViewModel/Repository
 * is wired yet. These match the design's example content from
 * design/screens-*.jsx so screens render with the intended fidelity.
 *
 * Production wiring should replace these with state from the appropriate
 * repository (ChatManager, FriendsManager, EconomyManager, …).
 */
internal object L2Demo {

    val Conversations = listOf(
        ConversationSummary("c1", "Echo Nightshade", "tping now 💜", "14:30", unread = 2, online = true),
        ConversationSummary("c2", "Crystal Coast 🌊", "Aria: dj set starts at 8 SLT", "14:12", unread = 7, online = true, isGroup = true),
        ConversationSummary("c3", "Mira Ashford", "sent you the mesh body fix script", "13:48", online = true),
        ConversationSummary("c4", "Tinkerers Guild", "Voss: anyone testing the new physics?", "12:30", unread = 1, isGroup = true),
        ConversationSummary("c5", "Juno Vex", "thanks for the teleport! 💜", "Yesterday"),
        ConversationSummary("c6", "Solaris Build Team", "You: pushing the parcel updates now", "Yesterday", isGroup = true),
        ConversationSummary("c7", "Ren Volkov", "meet at the floating gardens?", "Mon"),
    )

    val ChatThread = listOf(
        ChatMessage("m1", "Echo Nightshade", "so I just got back from the Crystal Coast sim", System.currentTimeMillis() - 800_000, MessageType.NORMAL, ChatChannel.LOCAL),
        ChatMessage("m2", "Echo Nightshade", "omg the new sim is GORGEOUS, you have to come", System.currentTimeMillis() - 700_000, MessageType.NORMAL, ChatChannel.LOCAL),
        ChatMessage("m3", "You", "wait send me the slurl??", System.currentTimeMillis() - 600_000, MessageType.NORMAL, ChatChannel.LOCAL, isMine = true, status = MessageStatus.READ),
        ChatMessage("m4", "Echo Nightshade", "secondlife://Crystal Coast/186/92/24", System.currentTimeMillis() - 500_000, MessageType.NORMAL, ChatChannel.LOCAL),
        ChatMessage("m5", "You", "tping now 💜", System.currentTimeMillis() - 200_000, MessageType.NORMAL, ChatChannel.LOCAL, isMine = true, status = MessageStatus.DELIVERED),
        ChatMessage("m6", "You", "omw", System.currentTimeMillis() - 60_000, MessageType.NORMAL, ChatChannel.LOCAL, isMine = true, status = MessageStatus.READ),
    )

    val Friends = listOf(
        FriendData(UUID.randomUUID(), "Echo Nightshade", FriendStatus.ONLINE, "Bay City — Plum", canSeeMap = true),
        FriendData(UUID.randomUUID(), "Mira Ashford", FriendStatus.ONLINE, "Crystal Coast", canSeeMap = true),
        FriendData(UUID.randomUUID(), "Voss Tink", FriendStatus.AWAY, "Tinkerers Guild HQ"),
        FriendData(UUID.randomUUID(), "Juno Vex", FriendStatus.OFFLINE),
        FriendData(UUID.randomUUID(), "Ren Volkov", FriendStatus.OFFLINE),
        FriendData(UUID.randomUUID(), "Aria Dawn", FriendStatus.BUSY, "DJing"),
    )

    val WalletTransactions = listOf(
        WalletTransaction("t1", "Aria Dawn — DJ tip", "Festival ground", 250, "14:30", isIncome = true),
        WalletTransaction("t2", "Coral Bay parcel rent", "Parcel · Coral Bay", 1_000, "Yesterday", isIncome = false),
        WalletTransaction("t3", "Mesh body upgrade", "Marketplace", 1_499, "Mon", isIncome = false),
        WalletTransaction("t4", "Group dividends", "Tinkerers Guild", 320, "Sun", isIncome = true),
        WalletTransaction("t5", "Texture pack sale", "Marketplace", 750, "Sun", isIncome = true),
    )

    val Places = listOf(
        PlaceCard(
            id = "p1",
            name = "Crystal Coast",
            rating = "PG",
            traffic = 14,
            parcel = "186, 92, 24 · Beach",
            coverGradient = listOf(Color(0xFF1D4060), Color(0xFF4A7BA8), Color(0xFFC8B88A)),
        ),
        PlaceCard(
            id = "p2",
            name = "Bay City — Plum",
            rating = "Mature",
            traffic = 45,
            parcel = "128, 64, 32 · Urban",
            coverGradient = listOf(Color(0xFF1A1A2E), Color(0xFF50C8DC), Color(0xFF8C7CFF)),
        ),
        PlaceCard(
            id = "p3",
            name = "Solarpunk Civic",
            rating = "PG",
            traffic = 22,
            parcel = "64, 64, 22 · Forest",
            coverGradient = listOf(Color(0xFF1F3A14), Color(0xFF3D9C40), Color(0xFFFFB940)),
        ),
    )

    val Events = listOf(
        EventCard("e1", "Aurora DJ Set", "Crystal Coast", "Tonight 8 SLT", "Live"),
        EventCard("e2", "Build Jam — neon city", "Tinkerers Guild HQ", "Sat 2 SLT", "Build"),
        EventCard("e3", "Solarpunk meetup", "Solarpunk Civic", "Sun 5 SLT", "Social"),
    )

    val Outfits = listOf(
        SavedOutfit("o1", "Festival glam", "Aria", Color(0xFFFF8FB1)),
        SavedOutfit("o2", "Build mode", "Voss", Color(0xFF6DE8FF)),
        SavedOutfit("o3", "Forest wanderer", "Zen", Color(0xFF79D87E)),
    )

    val OutfitSlots = listOf(
        WearableSlot("Skin", "Lara — Honey", layer = 0, group = "Body"),
        WearableSlot("Shape", "Lyra v3", layer = 0, group = "Body"),
        WearableSlot("Hair", "Mira — Auburn", layer = 1, group = "Body"),
        WearableSlot("Eyes", "Aurora", layer = 1, group = "Body"),
        WearableSlot("Top", "Linen blouse", layer = 2, group = "Clothing"),
        WearableSlot("Bottom", "Wide-leg pants", layer = 2, group = "Clothing"),
        WearableSlot("Shoes", "Strapped sandals", layer = 3, group = "Clothing"),
        WearableSlot("Necklace", "Aurora pendant", layer = 4, group = "Bento Attachments"),
        WearableSlot("Ring", null, layer = 4, group = "Bento Attachments"),
    )

    val GroupMembers = listOf(
        GroupMember("g1", "Voss Tink", "Founder", true),
        GroupMember("g2", "Mira Ashford", "Officer", true),
        GroupMember("g3", "Echo Nightshade", "Member", true),
        GroupMember("g4", "Juno Vex", "Member", false),
        GroupMember("g5", "Ren Volkov", "Member", false),
    )

    val Voices = listOf(
        VoiceParticipant("v1", "Echo Nightshade", volume = 0.7f, muted = false, distanceMeters = 4, speaking = true),
        VoiceParticipant("v2", "Mira Ashford", volume = 0.4f, muted = false, distanceMeters = 12, speaking = false),
        VoiceParticipant("v3", "Voss Tink", volume = 0.0f, muted = true, distanceMeters = 18, speaking = false),
    )

    val Notifications = listOf(
        NotificationItem("n1", NotificationKind.Im, "Echo Nightshade", "tping now 💜", "1m"),
        NotificationItem("n2", NotificationKind.FriendRequest, "Aria Dawn", "wants to be friends", "5m", acceptable = true),
        NotificationItem("n3", NotificationKind.Money, "Mira Ashford paid you L$ 250", "for mesh body fix script", "10m"),
        NotificationItem("n4", NotificationKind.GroupPing, "Tinkerers Guild", "Voss: who's testing the new physics?", "20m", mentioned = true),
        NotificationItem("n5", NotificationKind.Region, "Bay City — Plum", "Region restart in 15 minutes", "30m"),
    )

    val Grids = listOf(
        GridEntry("agni", "Second Life · Agni", "https://login.agni.lindenlab.com/cgi-bin/login.cgi", builtIn = true, online = true),
        GridEntry("aditi", "Second Life · Aditi (beta)", "https://login.aditi.lindenlab.com/cgi-bin/login.cgi", builtIn = true, online = true),
        GridEntry("custom1", "OSGrid", "http://login.osgrid.org/", builtIn = false, online = true),
        GridEntry("custom2", "Kitely", "https://grid.kitely.com:8002/", builtIn = false, online = false),
    )

    val DefaultGraphics = GraphicsState()
    val DefaultPrivacy = PrivacyState()
    val BlockedUsers = listOf("Spammer Resident", "Annoying Bot")
}
