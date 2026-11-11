package com.linkpoint.protocol.messages

import com.linkpoint.LinkpointApp
import com.linkpoint.linden.llmessage.Deprecation
import com.linkpoint.linden.llmessage.Encoding
import com.linkpoint.linden.llmessage.Frequency
import com.linkpoint.linden.llmessage.MessageTemplate
import com.linkpoint.linden.llmessage.Trust

/**
 * Explicit classification of message_template.msg entries for protocol conformance checks.
 */
object MessageTemplateCatalog {
    private val _templates: MutableMap<String, MessageTemplate> = linkedMapOf()

    private fun add(
        name: String,
        frequency: Frequency = Frequency.LOW,
        messageNumber: UInt = 0u,
        trust: Trust = Trust.NOTRUST,
        encoding: Encoding = Encoding.ZEROCODED,
        deprecation: Deprecation = Deprecation.NOT_DEPRECATED,
    ) {
        _templates[name] = MessageTemplate(name, frequency, messageNumber, trust, encoding, deprecation)
    }

    init {
        // ── Active messages ───────────────────────────────────────────────
        // Frequency and messageNumber are placeholders (0u / LOW); exact
        // values are populated when message_template.msg parsing is added.
        add("AbortXfer")
        add("AcceptCallingCard")
        add("AcceptFriendship")
        add("ActivateGestures")
        add("ActivateGroup")
        add("AddCircuitCode")
        add("AgentAlertMessage")
        add("AgentAnimation")
        add("AgentCachedTexture")
        add("AgentCachedTextureResponse")
        add("AgentDataUpdate")
        add("AgentDataUpdateRequest")
        add("AgentFOV")
        add("AgentHeightWidth")
        add("AgentIsNowWearing")
        add("AgentMovementComplete")
        add("AgentPause")
        add("AgentQuitCopy")
        add("AgentRequestSit")
        add("AgentResume")
        add("AgentSetAppearance")
        add("AgentSit")
        add("AgentThrottle")
        add("AgentUpdate")
        add("AgentWearablesRequest")
        add("AgentWearablesUpdate")
        add("AlertMessage")
        add("AssetUploadComplete")
        add("AssetUploadRequest")
        add("AtomicPassObject")
        add("AttachedSound")
        add("AttachedSoundGainChange")
        add("AvatarAnimation")
        add("AvatarAppearance")
        add("AvatarClassifiedReply")
        add("AvatarGroupsReply")
        add("AvatarInterestsReply")
        add("AvatarInterestsUpdate")
        add("AvatarNotesReply")
        add("AvatarNotesUpdate")
        add("AvatarPickerReply")
        add("AvatarPickerRequest")
        add("AvatarPickerRequestBackend")
        add("AvatarPicksReply")
        add("AvatarPropertiesReply")
        add("AvatarPropertiesRequest")
        add("AvatarPropertiesRequestBackend")
        add("AvatarPropertiesUpdate")
        add("AvatarSitResponse")
        add("AvatarTextureUpdate")
        add("BulkUpdateInventory")
        add("BuyObjectInventory")
        add("CameraConstraint")
        add("CancelAuction")
        add("ChangeInventoryItemFlags")
        add("ChangeUserRights")
        add("ChatFromSimulator")
        add("ChatFromViewer")
        add("ChatPass")
        add("CheckParcelAuctions")
        add("CheckParcelSales")
        add("ChildAgentAlive")
        add("ChildAgentDying")
        add("ChildAgentPositionUpdate")
        add("ChildAgentUnknown")
        add("ChildAgentUpdate")
        add("ClassifiedDelete")
        add("ClassifiedGodDelete")
        add("ClassifiedInfoReply")
        add("ClassifiedInfoRequest")
        add("ClassifiedInfoUpdate")
        add("ClearFollowCamProperties")
        add("CoarseLocationUpdate")
        add("CompleteAgentMovement")
        add("CompleteAuction")
        add("CompletePingCheck")
        add("ConfirmAuctionStart")
        add("ConfirmEnableSimulator")
        add("ConfirmXferPacket")
        add("CopyInventoryItem")
        add("CreateGroupReply")
        add("CreateGroupRequest")
        add("CreateInventoryFolder")
        add("CreateInventoryItem")
        add("CreateLandmarkForEvent")
        add("CreateNewOutfitAttachments")
        add("CreateTrustedCircuit")
        add("CrossedRegion")
        add("DataHomeLocationReply")
        add("DataHomeLocationRequest")
        add("DataServerLogout")
        add("DeRezAck")
        add("DeRezObject")
        add("DeactivateGestures")
        add("DeclineCallingCard")
        add("DeclineFriendship")
        add("DenyTrustedCircuit")
        add("DerezContainer")
        add("DetachAttachmentIntoInv")
        add("DirClassifiedQuery")
        add("DirClassifiedQueryBackend")
        add("DirClassifiedReply")
        add("DirEventsReply")
        add("DirFindQuery")
        add("DirFindQueryBackend")
        add("DirGroupsReply")
        add("DirLandQuery")
        add("DirLandQueryBackend")
        add("DirPeopleReply")
        add("DirPlacesQuery")
        add("DirPlacesQueryBackend")
        add("DirPlacesReply")
        add("DisableSimulator")
        add("EconomyData")
        add("EconomyDataRequest")
        add("EdgeDataPacket")
        add("EjectGroupMemberReply")
        add("EjectGroupMemberRequest")
        add("EjectUser")
        add("EmailMessageReply")
        add("EmailMessageRequest")
        add("EnableSimulator")
        add("Error")
        add("EstateCovenantReply")
        add("EstateCovenantRequest")
        add("EstateOwnerMessage")
        add("EventGodDelete")
        add("EventInfoReply")
        add("EventInfoRequest")
        add("EventLocationReply")
        add("EventLocationRequest")
        add("EventNotificationAddRequest")
        add("EventNotificationRemoveRequest")
        add("FeatureDisabled")
        add("FetchInventory")
        add("FetchInventoryDescendents")
        add("FetchInventoryReply")
        add("FindAgent")
        add("ForceObjectSelect")
        add("ForceScriptControlRelease")
        add("FormFriendship")
        add("FreezeUser")
        add("GenericMessage")
        add("GetScriptRunning")
        add("GodKickUser")
        add("GodUpdateRegionInfo")
        add("GodlikeMessage")
        add("GrantGodlikePowers")
        add("GrantUserRights")
        add("GroupAccountDetailsReply")
        add("GroupAccountDetailsRequest")
        add("GroupAccountSummaryReply")
        add("GroupAccountSummaryRequest")
        add("GroupAccountTransactionsReply")
        add("GroupAccountTransactionsRequest")
        add("GroupActiveProposalItemReply")
        add("GroupActiveProposalsRequest")
        add("GroupDataUpdate")
        add("GroupMembersReply")
        add("GroupMembersRequest")
        add("GroupNoticeAdd")
        add("GroupNoticeRequest")
        add("GroupNoticesListReply")
        add("GroupNoticesListRequest")
        add("GroupProfileReply")
        add("GroupProfileRequest")
        add("GroupRoleChanges")
        add("GroupRoleDataReply")
        add("GroupRoleDataRequest")
        add("GroupRoleMembersReply")
        add("GroupRoleMembersRequest")
        add("GroupRoleUpdate")
        add("GroupTitleUpdate")
        add("GroupTitlesReply")
        add("GroupTitlesRequest")
        add("GroupVoteHistoryItemReply")
        add("GroupVoteHistoryRequest")
        add("HealthMessage")
        add("ImageData")
        add("ImageNotInDatabase")
        add("ImagePacket")
        add("ImprovedInstantMessage")
        add("ImprovedTerseObjectUpdate")
        add("InitiateDownload")
        add("InternalScriptMail")
        add("InventoryAssetResponse")
        add("InventoryDescendents")
        add("InviteGroupRequest")
        add("InviteGroupResponse")
        add("JoinGroupReply")
        add("JoinGroupRequest")
        add("KickUser")
        add("KickUserAck")
        add("KillChildAgents")
        add("KillObject")
        add("LandStatRequest")
        add("LayerData")
        add("LeaveGroupReply")
        add("LeaveGroupRequest")
        add("LinkInventoryItem")
        add("LiveHelpGroupReply")
        add("LiveHelpGroupRequest")
        add("LoadURL")
        add("LogDwellTime")
        add("LogFailedMoneyTransaction")
        add("LogParcelChanges")
        add("LogTextMessage")
        add("LogoutReply")
        add("LogoutRequest")
        add("MapBlockReply")
        add("MapBlockRequest")
        add("MapItemReply")
        add("MapItemRequest")
        add("MapLayerReply")
        add("MapLayerRequest")
        add("MapNameRequest")
        add("MeanCollisionAlert")
        add("MergeParcel")
        add("ModifyLand")
        add("MoneyBalanceReply")
        add("MoneyBalanceRequest")
        add("MoneyTransferBackend")
        add("MoneyTransferRequest")
        add("MoveInventoryFolder")
        add("MoveInventoryItem")
        add("MoveTaskInventory")
        add("MultipleObjectUpdate")
        add("MuteListRequest")
        add("MuteListUpdate")
        add("NameValuePair")
        add("NearestLandingRegionReply")
        add("NearestLandingRegionRequest")
        add("NearestLandingRegionUpdated")
        add("NeighborList")
        add("NetTest")
        add("ObjectAdd")
        add("ObjectAttach")
        add("ObjectBuy")
        add("ObjectCategory")
        add("ObjectClickAction")
        add("ObjectDeGrab")
        add("ObjectDelete")
        add("ObjectDelink")
        add("ObjectDeselect")
        add("ObjectDescription")
        add("ObjectDetach")
        add("ObjectDrop")
        add("ObjectDuplicate")
        add("ObjectDuplicateOnRay")
        add("ObjectExportSelected")
        add("ObjectExtraParams")
        add("ObjectFlagUpdate")
        add("ObjectGrab")
        add("ObjectGrabUpdate")
        add("ObjectGroup")
        add("ObjectImage")
        add("ObjectIncludeInSearch")
        add("ObjectLink")
        add("ObjectMaterial")
        add("ObjectName")
        add("ObjectOwner")
        add("ObjectPermissions")
        add("ObjectProperties")
        add("ObjectPropertiesFamily")
        add("ObjectRotation")
        add("ObjectSaleInfo")
        add("ObjectSelect")
        add("ObjectShape")
        add("ObjectSpinStart")
        add("ObjectSpinStop")
        add("ObjectSpinUpdate")
        add("ObjectUpdate")
        add("ObjectUpdateCached")
        add("ObjectUpdateCompressed")
        add("OfferCallingCard")
        add("OfflineNotification")
        add("OnlineNotification")
        add("ParcelAccessListReply")
        add("ParcelAccessListRequest")
        add("ParcelAccessListUpdate")
        add("ParcelAuctions")
        add("ParcelBuy")
        add("ParcelBuyPass")
        add("ParcelClaim")
        add("ParcelDeedToGroup")
        add("ParcelDisableObjects")
        add("ParcelDivide")
        add("ParcelDwellReply")
        add("ParcelDwellRequest")
        add("ParcelGodForceOwner")
        add("ParcelGodMarkAsContent")
        add("ParcelInfoReply")
        add("ParcelInfoRequest")
        add("ParcelJoin")
        add("ParcelMediaCommandMessage")
        add("ParcelMediaUpdate")
        add("ParcelObjectOwnersRequest")
        add("ParcelOverlay")
        add("ParcelProperties")
        add("ParcelPropertiesRequest")
        add("ParcelPropertiesRequestByID")
        add("ParcelPropertiesUpdate")
        add("ParcelReclaim")
        add("ParcelRelease")
        add("ParcelRename")
        add("ParcelReturnObjects")
        add("ParcelSales")
        add("ParcelSelectObjects")
        add("ParcelSetOtherCleanTime")
        add("PayPriceReply")
        add("PickDelete")
        add("PickGodDelete")
        add("PickInfoReply")
        add("PickInfoUpdate")
        add("PlacesQuery")
        add("PreloadSound")
        add("PurgeInventoryDescendents")
        add("RebakeAvatarTextures")
        add("Redo")
        add("RegionHandleRequest")
        add("RegionHandshake")
        add("RegionHandshakeReply")
        add("RegionIDAndHandleReply")
        add("RegionInfo")
        add("RegionPresenceRequestByHandle")
        add("RegionPresenceRequestByRegionID")
        add("RegionPresenceResponse")
        add("RemoveAttachment")
        add("RemoveInventoryFolder")
        add("RemoveInventoryItem")
        add("RemoveInventoryObjects")
        add("RemoveMuteListEntry")
        add("RemoveNameValuePair")
        add("RemoveParcel")
        add("RemoveTaskInventory")
        add("ReplyTaskInventory")
        add("ReportAutosaveCrash")
        add("RequestGodlikePowers")
        add("RequestImage")
        add("RequestInventoryAsset")
        add("RequestMultipleObjects")
        add("RequestObjectPropertiesFamily")
        add("RequestParcelTransfer")
        add("RequestPayPrice")
        add("RequestRegionInfo")
        add("RequestTaskInventory")
        add("RequestTrustedCircuit")
        add("RequestXfer")
        add("RetrieveInstantMessages")
        add("RevokePermissions")
        add("RezMultipleAttachmentsFromInv")
        add("RezObject")
        add("RezObjectFromNotecard")
        add("RezScript")
        add("RezSingleAttachmentFromInv")
        add("RoutedMoneyBalanceReply")
        add("RpcChannelReply")
        add("RpcChannelRequest")
        add("RpcScriptReplyInbound")
        add("RpcScriptRequestInbound")
        add("SaveAssetIntoInventory")
        add("ScriptAnswerYes")
        add("ScriptControlChange")
        add("ScriptDataReply")
        add("ScriptDataRequest")
        add("ScriptDialog")
        add("ScriptDialogReply")
        add("ScriptMailRegistration")
        add("ScriptQuestion")
        add("ScriptReset")
        add("ScriptSensorReply")
        add("ScriptSensorRequest")
        add("ScriptTeleportRequest")
        add("SendPostcard")
        add("SendXferPacket")
        add("SetAlwaysRun")
        add("SetCPURatio")
        add("SetFollowCamProperties")
        add("SetGroupAcceptNotices")
        add("SetGroupContribution")
        add("SetScriptRunning")
        add("SetSimPresenceInDatabase")
        add("SetSimStatusInDatabase")
        add("SetStartLocation")
        add("SetStartLocationRequest")
        add("SimCrashed")
        add("SimStats")
        add("SimStatus")
        add("SimWideDeletes")
        add("SimulatorLoad")
        add("SimulatorMapUpdate")
        add("SimulatorPresentAtLocation")
        add("SimulatorReady")
        add("SimulatorSetMap")
        add("SimulatorShutdownRequest")
        add("SimulatorViewerTimeMessage")
        add("SoundTrigger")
        add("StartAuction")
        add("StartLure")
        add("StartPingCheck")
        add("StateSave")
        add("SubscribeLoad")
        add("SystemKickUser")
        add("SystemMessage")
        add("TallyVotes")
        add("TelehubInfo")
        add("TeleportCancel")
        add("TeleportFailed")
        add("TeleportFinish")
        add("TeleportLandingStatusChanged")
        add("TeleportLandmarkRequest")
        add("TeleportLocal")
        add("TeleportLocationRequest")
        add("TeleportLureRequest")
        add("TeleportProgress")
        add("TeleportRequest")
        add("TeleportStart")
        add("TerminateFriendship")
        add("TestMessage")
        add("TrackAgent")
        add("TransferAbort")
        add("TransferInfo")
        add("TransferInventory")
        add("TransferInventoryAck")
        add("TransferPacket")
        add("TransferRequest")
        add("UUIDGroupNameReply")
        add("UUIDGroupNameRequest")
        add("UUIDNameReply")
        add("UUIDNameRequest")
        add("Undo")
        add("UndoLand")
        add("UnsubscribeLoad")
        add("UpdateAttachment")
        add("UpdateCreateInventoryItem")
        add("UpdateGroupInfo")
        add("UpdateInventoryFolder")
        add("UpdateInventoryItem")
        add("UpdateMuteListEntry")
        add("UpdateParcel")
        add("UpdateSimulator")
        add("UpdateTaskInventory")
        add("UpdateUserInfo")
        add("UseCachedMuteList")
        add("UseCircuitCode")
        add("UserInfoReply")
        add("UserInfoRequest")
        add("UserReport")
        add("UserReportInternal")
        add("VelocityInterpolateOff")
        add("VelocityInterpolateOn")
        add("ViewerEffect")
        add("ViewerFrozenMessage")
        add("ViewerStartAuction")

        // ── Deprecated messages ───────────────────────────────────────────
        add("AgentDropGroup",                  deprecation = Deprecation.DEPRECATED)
        add("AgentGroupDataUpdate",            deprecation = Deprecation.DEPRECATED)
        add("CopyInventoryFromNotecard",       deprecation = Deprecation.DEPRECATED)
        add("DirLandReply",                    deprecation = Deprecation.DEPRECATED)
        add("DirPopularQuery",                 deprecation = Deprecation.DEPRECATED)
        add("DirPopularQueryBackend",          deprecation = Deprecation.DEPRECATED)
        add("DirPopularReply",                 deprecation = Deprecation.DEPRECATED)
        add("GroupProposalBallot",             deprecation = Deprecation.DEPRECATED)
        add("LandStatReply",                   deprecation = Deprecation.DEPRECATED)
        add("ObjectPosition",                  deprecation = Deprecation.DEPRECATED)
        add("ObjectScale",                     deprecation = Deprecation.DEPRECATED)
        add("ParcelObjectOwnersReply",         deprecation = Deprecation.DEPRECATED)
        add("PlacesReply",                     deprecation = Deprecation.DEPRECATED)
        add("RezRestoreToWorld",               deprecation = Deprecation.DEPRECATED)
        add("RpcScriptRequestInboundForward",  deprecation = Deprecation.DEPRECATED)
        add("ScriptRunningReply",              deprecation = Deprecation.DEPRECATED)
        add("StartGroupProposal",              deprecation = Deprecation.DEPRECATED)
        add("ViewerStats",                     deprecation = Deprecation.DEPRECATED)
    }

    operator fun get(name: String): MessageTemplate? = _templates[name]

    fun contains(name: String): Boolean = name in _templates

    val size: Int get() = _templates.size

    val allNames: Set<String>
        get() = _templates.keys.toSet()

    val activeNames: Set<String>
        get() = _templates.values
            .filter { it.deprecation == Deprecation.NOT_DEPRECATED }
            .map { it.name }
            .toSet()

    val deprecatedNames: Set<String>
        get() = _templates.values
            .filter { it.deprecation != Deprecation.NOT_DEPRECATED }
            .map { it.name }
            .toSet()

    /** Messages currently supported by parser and/or writer paths in Linkpoint. */
    private val declaredParserOrWriterMessages: Set<String> = setOf(
        "AbortXfer",
        "AcceptFriendship",
        "ActivateGestures",
        "ActivateGroup",
        "AddCircuitCode",
        "AgentAlertMessage",
        "AgentAnimation",
        "AgentCachedTexture",
        "AgentCachedTextureResponse",
        "AgentDataUpdate",
        "AgentIsNowWearing",
        "AgentMovementComplete",
        "AgentRequestSit",
        "AgentSetAppearance",
        "AgentSit",
        "AgentThrottle",
        "AgentUpdate",
        "AgentWearablesUpdate",
        "AlertMessage",
        "AtomicPassObject",
        "AttachedSound",
        "AttachedSoundGainChange",
        "AvatarAnimation",
        "AvatarAppearance",
        "AgentPause",
        "AgentResume",
        "AgentWearablesRequest",
        "AvatarGroupsReply",
        "AvatarInterestsReply",
        "AvatarPropertiesReply",
        "AvatarPropertiesRequest",
        "AvatarSitResponse",
        "BulkUpdateInventory",
        "CameraConstraint",
        "ChangeUserRights",
        "ChatFromSimulator",
        "ChatFromViewer",
        "ChildAgentAlive",
        "ChildAgentPositionUpdate",
        "ChildAgentUpdate",
        "CoarseLocationUpdate",
        "CompleteAgentMovement",
        "CompletePingCheck",
        "ConfirmEnableSimulator",
        "ConfirmXferPacket",
        "CopyInventoryItem",
        "CreateInventoryFolder",
        "CrossedRegion",
        "DeRezObject",
        "DeactivateGestures",
        "DeclineFriendship",
        "DirClassifiedReply",
        "DirEventsReply",
        "DirGroupsReply",
        "DirPeopleReply",
        "DirPlacesReply",
        "DisableSimulator",
        "EconomyData",
        "EdgeDataPacket",
        "EnableSimulator",
        "EstateCovenantReply",
        "EstateOwnerMessage",
        "AssetUploadRequest",
        "AvatarPickerRequest",
        "AvatarPropertiesUpdate",
        "DirFindQuery",
        "DirPlacesQuery",
        "EjectGroupMemberRequest",
        "FetchInventory",
        "FetchInventoryDescendents",
        "FetchInventoryReply",
        "FindAgent",
        "GenericMessage",
        "InviteGroupRequest",
        "JoinGroupRequest",
        "LeaveGroupReply",
        "FormFriendship",
        "FreezeUser",
        "GrantUserRights",
        "GroupMembersReply",
        "GroupNoticeAdd",
        "GroupProfileReply",
        "GroupProfileRequest",
        "GroupRoleDataReply",
        "GroupRoleDataRequest",
        "GroupTitlesReply",
        "GroupTitlesRequest",
        "HealthMessage",
        "ImageData",
        "ImagePacket",
        "ImprovedInstantMessage",
        "ImprovedTerseObjectUpdate",
        "InternalScriptMail",
        "InventoryDescendents",
        "KillObject",
        "LayerData",
        "LeaveGroupRequest",
        "LoadURL",
        "LogoutReply",
        "LogoutRequest",
        "MapBlockReply",
        "MapItemReply",
        "MapLayerReply",
        "MapNameRequest",
        "MeanCollisionAlert",
        "MoneyBalanceReply",
        "MoneyBalanceRequest",
        "MoveInventoryItem",
        "MultipleObjectUpdate",
        "NeighborList",
        "ObjectAdd",
        "ObjectDeGrab",
        "ObjectDelete",
        "ObjectDelink",
        "ObjectDescription",
        "ObjectGrab",
        "ObjectLink",
        "ObjectName",
        "ObjectProperties",
        "ObjectPropertiesFamily",
        "ObjectSelect",
        "ObjectUpdate",
        "ObjectUpdateCached",
        "ObjectUpdateCompressed",
        "OfflineNotification",
        "OnlineNotification",
        "ParcelAccessListReply",
        "ParcelAccessListUpdate",
        "ParcelBuy",
        "ParcelDeedToGroup",
        "ParcelDwellReply",
        "ParcelInfoReply",
        "ParcelOverlay",
        // ParcelProperties is in deprecatedMessagesWithRationale below
        // (template marks it Deprecated).
        "ParcelPropertiesRequest",
        "ParcelPropertiesUpdate",
        "ParcelRelease",
        "ParcelReturnObjects",
        "PreloadSound",
        "RegionHandshake",
        "RegionHandshakeReply",
        "RegionInfo",
        "RemoveInventoryFolder",
        "RemoveInventoryItem",
        "RequestImage",
        "RequestMultipleObjects",
        "RequestObjectPropertiesFamily",
        "RequestPayPrice",
        "RezObject",
        "RezSingleAttachmentFromInv",
        "ScriptControlChange",
        "ScriptDialog",
        "ScriptDialogReply",
        "ScriptQuestion",
        "SendXferPacket",
        "SimCrashed",
        "SimStats",
        "SimStatus",
        "SoundTrigger",
        "StartLure",
        "StartPingCheck",
        "TeleportCancel",
        "TeleportFailed",
        "TeleportFinish",
        "TeleportLandmarkRequest",
        "TeleportLocal",
        "TeleportLocationRequest",
        "TeleportLureRequest",
        "TeleportProgress",
        "TeleportRequest",
        "TeleportStart",
        "TerminateFriendship",
        "TransferInfo",
        "TransferPacket",
        "UUIDGroupNameReply",
        "UUIDNameReply",
        "UpdateCreateInventoryItem",
        "UpdateInventoryItem",
        "UseCircuitCode",
        "ViewerEffect",
    )

    val supportedParserOrWriterMessages: Set<String> =
        declaredParserOrWriterMessages + LinkpointApp.parserSupportedMessageNamesForConformance

    /** Declared-only messages kept for parity; each entry carries an explicit rationale. */
    private val declaredOnlyBacklogMessages: Map<String, String> = mapOf(
        "AcceptCallingCard" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AgentDataUpdateRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AgentFOV" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AgentHeightWidth" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // AgentPause moved to supportedParserOrWriterMessages: writer
        // implemented in UDPConnectionFixed.sendAgentPause; lifecycle hook
        // wires it from ProcessLifecycleObserver.onStop.
        "AgentQuitCopy" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // AgentResume / AgentWearablesRequest writers implemented in
        // UDPConnectionFixed (sendAgentResume / sendAgentWearablesRequest).
        "AssetUploadComplete" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarClassifiedReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarInterestsUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarNotesReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarNotesUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarPickerReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarPickerRequestBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarPicksReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarPropertiesRequestBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "AvatarTextureUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "BuyObjectInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CancelAuction" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ChangeInventoryItemFlags" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ChatPass" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CheckParcelAuctions" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CheckParcelSales" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ChildAgentDying" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ChildAgentUnknown" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ClassifiedDelete" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ClassifiedGodDelete" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ClassifiedInfoReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ClassifiedInfoRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ClassifiedInfoUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ClearFollowCamProperties" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CompleteAuction" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ConfirmAuctionStart" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateGroupReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateGroupRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateGroupRequestExtended" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateInventoryItem" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateLandmarkForEvent" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateNewOutfitAttachments" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "CreateTrustedCircuit" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DataHomeLocationReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DataHomeLocationRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DataServerLogout" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DeRezAck" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DeclineCallingCard" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DenyTrustedCircuit" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DerezContainer" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DetachAttachmentIntoInv" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DirClassifiedQuery" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DirClassifiedQueryBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DirFindQueryBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DirLandQuery" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DirLandQueryBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "DirPlacesQueryBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EconomyDataRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EjectGroupMemberReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EjectUser" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EmailMessageReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EmailMessageRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "Error" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EstateCovenantRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventGodDelete" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventInfoReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventInfoRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventLocationReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventLocationRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventNotificationAddRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "EventNotificationRemoveRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "FeatureDisabled" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ForceObjectSelect" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ForceScriptControlRelease" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GameControlInput" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GenericStreamingMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GetScriptRunning" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GodKickUser" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GodUpdateRegionInfo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GodlikeMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GrantGodlikePowers" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupAccountDetailsReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupAccountDetailsRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupAccountSummaryReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupAccountSummaryRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupAccountTransactionsReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupAccountTransactionsRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupActiveProposalItemReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupActiveProposalsRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupDataUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupMembersRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupNoticeRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupNoticesListReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupNoticesListRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupRoleChanges" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // GroupRoleDataRequest writer implemented in
        // UDPConnectionFixed.sendGroupRoleDataRequest; reply parser in
        // GroupsManager.handleGroupRoleData.
        "GroupRoleMembersReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupRoleMembersRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupRoleUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupTitleUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // GroupTitlesRequest writer implemented in
        // UDPConnectionFixed.sendGroupTitlesRequest; reply parser in
        // GroupsManager.handleGroupTitles.
        "GroupVoteHistoryItemReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "GroupVoteHistoryRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ImageNotInDatabase" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "InitiateDownload" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "InventoryAssetResponse" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "InviteGroupResponse" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "JoinGroupReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "JoinGroupRequestExtended" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "KickUser" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "KickUserAck" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "KillChildAgents" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LandStatRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // LargeGenericMessage is in deprecatedMessagesWithRationale below
        // (template marks it UDPDeprecated).
        "LinkInventoryItem" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LiveHelpGroupReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LiveHelpGroupRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LogDwellTime" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LogFailedMoneyTransaction" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LogParcelChanges" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "LogTextMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MapBlockRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MapItemRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MapLayerRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MapNameRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MergeParcel" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ModifyLand" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MoneyTransferBackend" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MoneyTransferRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MoveInventoryFolder" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MoveTaskInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MuteListRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "MuteListUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "NameValuePair" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "NearestLandingRegionReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "NearestLandingRegionRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "NearestLandingRegionUpdated" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "NetTest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectAnimation" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectAttach" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectBuy" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectBypassModUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectCategory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectClickAction" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectDeselect" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectDetach" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectDrop" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectDuplicate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectDuplicateOnRay" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectExportSelected" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectExtraParams" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectFlagUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectGrabUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectGroup" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectImage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectIncludeInSearch" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectMaterial" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectOwner" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectPermissions" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectRotation" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectSaleInfo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectShape" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectSpinStart" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectSpinStop" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ObjectSpinUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "OfferCallingCard" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelAccessListRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelAuctions" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelBuyPass" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelClaim" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelDisableObjects" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelDivide" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelDwellRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelGodForceOwner" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelGodMarkAsContent" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelInfoRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelJoin" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelMediaCommandMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelMediaUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelObjectOwnersRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelPropertiesRequestByID" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelReclaim" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelRename" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelSales" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelSelectObjects" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ParcelSetOtherCleanTime" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PayPriceReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PickDelete" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PickGodDelete" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PickInfoReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PickInfoUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PlacesQuery" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "PurgeInventoryDescendents" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RebakeAvatarTextures" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "Redo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RegionHandleRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RegionIDAndHandleReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RegionPresenceRequestByHandle" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RegionPresenceRequestByRegionID" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RegionPresenceResponse" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RemoveAttachment" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RemoveInventoryObjects" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RemoveMuteListEntry" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RemoveNameValuePair" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RemoveParcel" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RemoveTaskInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ReplyTaskInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ReportAutosaveCrash" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestGodlikePowers" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestInventoryAsset" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestParcelTransfer" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestPayPrice" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestRegionInfo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestTaskInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestTrustedCircuit" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RequestXfer" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RetrieveIMsExtended" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RetrieveInstantMessages" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RevokePermissions" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RezMultipleAttachmentsFromInv" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RezObjectFromNotecard" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RezScript" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // RoutedMoneyBalanceReply is in deprecatedMessagesWithRationale below
        // (template marks it Deprecated).
        "RpcChannelReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RpcChannelRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RpcScriptReplyInbound" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "RpcScriptRequestInbound" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SaveAssetIntoInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ScriptAnswerYes" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // ScriptDataReply / ScriptDataRequest are in
        // deprecatedMessagesWithRationale below (template marks them
        // Deprecated).
        "ScriptMailRegistration" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ScriptReset" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ScriptSensorReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ScriptSensorRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ScriptTeleportRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SendPostcard" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetAlwaysRun" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetCPURatio" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetFollowCamProperties" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetGroupAcceptNotices" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetGroupContribution" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetScriptRunning" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        // SetSimPresenceInDatabase is in deprecatedMessagesWithRationale
        // below (template marks it Deprecated).
        "SetSimStatusInDatabase" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetStartLocation" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SetStartLocationRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimWideDeletes" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorLoad" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorMapUpdate" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorPresentAtLocation" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorReady" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorSetMap" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorShutdownRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SimulatorViewerTimeMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "StartAuction" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "StateSave" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SubscribeLoad" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SystemKickUser" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "SystemMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TallyVotes" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TelehubInfo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TeleportLandingStatusChanged" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TestMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TrackAgent" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TransferAbort" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TransferInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TransferInventoryAck" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "TransferRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UUIDGroupNameRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UUIDNameRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "Undo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UndoLand" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UnsubscribeLoad" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateAttachment" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateGroupInfo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateInventoryFolder" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateMuteListEntry" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateParcel" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateSimulator" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateTaskInventory" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UpdateUserInfo" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UseCachedMuteList" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UserInfoReply" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UserInfoRequest" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UserReport" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "UserReportInternal" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "VelocityInterpolateOff" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "VelocityInterpolateOn" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ViewerFrozenMessage" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
        "ViewerStartAuction" to "Declared for protocol ID parity; parser/writer support is not implemented yet.",
    )

    private val implementedDeclaredMessageNames: Set<String> = setOf(
        "FetchInventoryDescendents",
        "FetchInventory",
        "RequestPayPrice",
        "DirFindQuery",
        "GroupTitlesRequest",
        "MapNameRequest",
        "AgentPause",
        "AgentResume",
    )

    private const val DEFERRED_DATE = "2026-04-24"
    private const val DEFERRED_ISSUE = "https://github.com/Kaleaon/Linkpoint/issues/1734"

    private val userCriticalPrefixes = listOf("Chat", "Inventory", "Parcel", "Money", "Economy")
    private val mediumUtilityPrefixes = listOf("Group", "Dir", "Map", "Search")

    private fun classifyDeferredMessage(name: String): String = when {
        userCriticalPrefixes.any { name.startsWith(it) } -> "user-critical"
        mediumUtilityPrefixes.any { name.startsWith(it) } -> "medium-utility"
        else -> "low-priority-admin"
    }

    private fun deferredRationale(category: String): String =
        "@deferred category=$category since=$DEFERRED_DATE issue=$DEFERRED_ISSUE"

    private val deferredMessageNames: Set<String> =
        declaredOnlyBacklogMessages.keys - implementedDeclaredMessageNames

    /** Declared-only backlog split by prioritization slice for parity tracking. */
    val declaredOnlyUserCriticalMessagesWithRationale: Map<String, String> = deferredMessageNames
        .filter { classifyDeferredMessage(it) == "user-critical" }
        .sorted()
        .associateWith { deferredRationale("user-critical") }

    /** Declared-only backlog split by prioritization slice for parity tracking. */
    val declaredOnlyMediumUtilityMessagesWithRationale: Map<String, String> = deferredMessageNames
        .filter { classifyDeferredMessage(it) == "medium-utility" }
        .sorted()
        .associateWith { deferredRationale("medium-utility") }

    /** Declared-only backlog split by prioritization slice for parity tracking. */
    val declaredOnlyLowPriorityAdminMessagesWithRationale: Map<String, String> = deferredMessageNames
        .filter { classifyDeferredMessage(it) == "low-priority-admin" }
        .sorted()
        .associateWith { deferredRationale("low-priority-admin") }

    /** Declared-only messages kept for parity and explicitly deferred with dated issue linkage. */
    val declaredOnlyMessagesWithRationale: Map<String, String> =
        declaredOnlyUserCriticalMessagesWithRationale +
            declaredOnlyMediumUtilityMessagesWithRationale +
            declaredOnlyLowPriorityAdminMessagesWithRationale

    /** Deprecated template messages tracked explicitly to preserve historical parity. */
    val deprecatedMessagesWithRationale: Map<String, String> = mapOf(
        "AgentDropGroup" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "AgentGroupDataUpdate" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "CopyInventoryFromNotecard" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "DirLandReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "DirPopularQuery" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "DirPopularQueryBackend" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "DirPopularReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "GroupProposalBallot" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "LandStatReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "LargeGenericMessage" to "Marked UDPDeprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ObjectPosition" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ObjectScale" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ParcelObjectOwnersReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ParcelProperties" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "PlacesReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "RezRestoreToWorld" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "RoutedMoneyBalanceReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "RpcScriptRequestInboundForward" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ScriptDataReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ScriptDataRequest" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ScriptRunningReply" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "SetSimPresenceInDatabase" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "StartGroupProposal" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
        "ViewerStats" to "Marked Deprecated in message_template.msg; retained only for historical/protocol completeness.",
    )
}
