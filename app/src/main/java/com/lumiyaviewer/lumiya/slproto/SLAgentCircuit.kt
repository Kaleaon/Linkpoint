package com.lumiyaviewer.lumiya.slproto

import android.annotation.SuppressLint
import com.google.common.base.Objects
import com.google.common.logging.nano.Vr.VREvent.VrCore.ErrorCode
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GridConnectionService
import com.lumiyaviewer.lumiya.dao.UserName
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.eventbus.EventRateLimiter
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.ICapsEventHandler
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.chat.SLChatBalanceChangedEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByYouEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestedEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatOnlineOfflineEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.events.SLObjectPayInfoEvent
import com.lumiyaviewer.lumiya.slproto.events.SLRegionInfoChangedEvent
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship
import com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship.FolderData
import com.lumiyaviewer.lumiya.slproto.messages.AgentFOV
import com.lumiyaviewer.lumiya.slproto.messages.AgentMovementComplete
import com.lumiyaviewer.lumiya.slproto.messages.AgentPause
import com.lumiyaviewer.lumiya.slproto.messages.AgentResume
import com.lumiyaviewer.lumiya.slproto.messages.AlertMessage
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance
import com.lumiyaviewer.lumiya.slproto.messages.AvatarInterestsReply
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromViewer
import com.lumiyaviewer.lumiya.slproto.messages.CompleteAgentMovement
import com.lumiyaviewer.lumiya.slproto.messages.DeRezObject
import com.lumiyaviewer.lumiya.slproto.messages.DeRezObject.AgentBlock
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage
import com.lumiyaviewer.lumiya.slproto.messages.EstateOwnerMessage.ParamList
import com.lumiyaviewer.lumiya.slproto.messages.GenericMessage
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate
import com.lumiyaviewer.lumiya.slproto.messages.KillObject
import com.lumiyaviewer.lumiya.slproto.messages.LayerData
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL
import com.lumiyaviewer.lumiya.slproto.messages.LogoutRequest
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy
import com.lumiyaviewer.lumiya.slproto.messages.ObjectBuy.AgentData
import com.lumiyaviewer.lumiya.slproto.messages.ObjectDeGrab
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab
import com.lumiyaviewer.lumiya.slproto.messages.ObjectGrab.SurfaceInfo
import com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect
import com.lumiyaviewer.lumiya.slproto.messages.ObjectSelect.ObjectData
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCached
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed
import com.lumiyaviewer.lumiya.slproto.messages.OfflineNotification
import com.lumiyaviewer.lumiya.slproto.messages.OnlineNotification
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply
import com.lumiyaviewer.lumiya.slproto.messages.PayPriceReply.ButtonData
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshakeReply
import com.lumiyaviewer.lumiya.slproto.messages.RequestMultipleObjects
import com.lumiyaviewer.lumiya.slproto.messages.RequestPayPrice
import com.lumiyaviewer.lumiya.slproto.messages.RetrieveInstantMessages
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog.Buttons
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialogReply
import com.lumiyaviewer.lumiya.slproto.messages.SimulatorViewerTimeMessage
import com.lumiyaviewer.lumiya.slproto.messages.StartLure
import com.lumiyaviewer.lumiya.slproto.messages.StartLure.TargetData
import com.lumiyaviewer.lumiya.slproto.messages.TeleportFailed
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLandmarkRequest
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLocal
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLocationRequest
import com.lumiyaviewer.lumiya.slproto.messages.TeleportLureRequest
import com.lumiyaviewer.lumiya.slproto.messages.TeleportProgress
import com.lumiyaviewer.lumiya.slproto.messages.TeleportStart
import com.lumiyaviewer.lumiya.slproto.messages.TerminateFriendship
import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData
import com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser
import com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList
import java.util.Collections
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

data class SLAgentCircuit(
    var agentNameSubscription: Subscription,
    var agentPaused: Boolean = false,
    var doingObjectSelection: Boolean = false,
    var isEstateManager: Boolean = false,
    var lastObjectSelection: Long = 0,
    var lastPauseId: Int = 0,
    var lastVisibleActivities: Long = 0,
    var pendingGroupMessages: List<ImprovedInstantMessage> = new LinkedList(),
    var regionHandle: Long = 0,
    var regionID: UUID = null,
    var regionName: String = null,
    var teleportRequestSent: Boolean = false
)
