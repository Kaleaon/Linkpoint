package com.linkpoint.slproto.messages
import java.util.*

import com.linkpoint.slproto.SLMessage

class SLMessageHandler {
    fun DefaultMessageHandler(sLMessage: SLMessage) {
    }

    fun HandleAbortXfer(abortXfer: AbortXfer) {
        DefaultMessageHandler(abortXfer)
    }

    fun HandleAcceptCallingCard(acceptCallingCard: AcceptCallingCard) {
        DefaultMessageHandler(acceptCallingCard)
    }

    fun HandleAcceptFriendship(acceptFriendship: AcceptFriendship) {
        DefaultMessageHandler(acceptFriendship)
    }

    fun HandleActivateGestures(activateGestures: ActivateGestures) {
        DefaultMessageHandler(activateGestures)
    }

    fun HandleActivateGroup(activateGroup: ActivateGroup) {
        DefaultMessageHandler(activateGroup)
    }

    fun HandleAddCircuitCode(addCircuitCode: AddCircuitCode) {
        DefaultMessageHandler(addCircuitCode)
    }

    fun HandleAgentAlertMessage(agentAlertMessage: AgentAlertMessage) {
        DefaultMessageHandler(agentAlertMessage)
    }

    fun HandleAgentAnimation(agentAnimation: AgentAnimation) {
        DefaultMessageHandler(agentAnimation)
    }

    fun HandleAgentCachedTexture(agentCachedTexture: AgentCachedTexture) {
        DefaultMessageHandler(agentCachedTexture)
    }

    fun HandleAgentCachedTextureResponse(agentCachedTextureResponse: AgentCachedTextureResponse) {
        DefaultMessageHandler(agentCachedTextureResponse)
    }

    fun HandleAgentDataUpdate(agentDataUpdate: AgentDataUpdate) {
        DefaultMessageHandler(agentDataUpdate)
    }

    fun HandleAgentDataUpdateRequest(agentDataUpdateRequest: AgentDataUpdateRequest) {
        DefaultMessageHandler(agentDataUpdateRequest)
    }

    fun HandleAgentDropGroup(agentDropGroup: AgentDropGroup) {
        DefaultMessageHandler(agentDropGroup)
    }

    fun HandleAgentFOV(agentFOV: AgentFOV) {
        DefaultMessageHandler(agentFOV)
    }

    fun HandleAgentGroupDataUpdate(agentGroupDataUpdate: AgentGroupDataUpdate) {
        DefaultMessageHandler(agentGroupDataUpdate)
    }

    fun HandleAgentHeightWidth(agentHeightWidth: AgentHeightWidth) {
        DefaultMessageHandler(agentHeightWidth)
    }

    fun HandleAgentIsNowWearing(agentIsNowWearing: AgentIsNowWearing) {
        DefaultMessageHandler(agentIsNowWearing)
    }

    fun HandleAgentMovementComplete(agentMovementComplete: AgentMovementComplete) {
        DefaultMessageHandler(agentMovementComplete)
    }

    fun HandleAgentPause(agentPause: AgentPause) {
        DefaultMessageHandler(agentPause)
    }

    fun HandleAgentQuitCopy(agentQuitCopy: AgentQuitCopy) {
        DefaultMessageHandler(agentQuitCopy)
    }

    fun HandleAgentRequestSit(agentRequestSit: AgentRequestSit) {
        DefaultMessageHandler(agentRequestSit)
    }

    fun HandleAgentResume(agentResume: AgentResume) {
        DefaultMessageHandler(agentResume)
    }

    fun HandleAgentSetAppearance(agentSetAppearance: AgentSetAppearance) {
        DefaultMessageHandler(agentSetAppearance)
    }

    fun HandleAgentSit(agentSit: AgentSit) {
        DefaultMessageHandler(agentSit)
    }

    fun HandleAgentThrottle(agentThrottle: AgentThrottle) {
        DefaultMessageHandler(agentThrottle)
    }

    fun HandleAgentUpdate(agentUpdate: AgentUpdate) {
        DefaultMessageHandler(agentUpdate)
    }

    fun HandleAgentWearablesRequest(agentWearablesRequest: AgentWearablesRequest) {
        DefaultMessageHandler(agentWearablesRequest)
    }

    fun HandleAgentWearablesUpdate(agentWearablesUpdate: AgentWearablesUpdate) {
        DefaultMessageHandler(agentWearablesUpdate)
    }

    fun HandleAlertMessage(alertMessage: AlertMessage) {
        DefaultMessageHandler(alertMessage)
    }

    fun HandleAssetUploadComplete(assetUploadComplete: AssetUploadComplete) {
        DefaultMessageHandler(assetUploadComplete)
    }

    fun HandleAssetUploadRequest(assetUploadRequest: AssetUploadRequest) {
        DefaultMessageHandler(assetUploadRequest)
    }

    fun HandleAtomicPassObject(atomicPassObject: AtomicPassObject) {
        DefaultMessageHandler(atomicPassObject)
    }

    fun HandleAttachedSound(attachedSound: AttachedSound) {
        DefaultMessageHandler(attachedSound)
    }

    fun HandleAttachedSoundGainChange(attachedSoundGainChange: AttachedSoundGainChange) {
        DefaultMessageHandler(attachedSoundGainChange)
    }

    fun HandleAvatarAnimation(avatarAnimation: AvatarAnimation) {
        DefaultMessageHandler(avatarAnimation)
    }

    fun HandleAvatarAppearance(avatarAppearance: AvatarAppearance) {
        DefaultMessageHandler(avatarAppearance)
    }

    fun HandleAvatarClassifiedReply(avatarClassifiedReply: AvatarClassifiedReply) {
        DefaultMessageHandler(avatarClassifiedReply)
    }

    fun HandleAvatarGroupsReply(avatarGroupsReply: AvatarGroupsReply) {
        DefaultMessageHandler(avatarGroupsReply)
    }

    fun HandleAvatarInterestsReply(avatarInterestsReply: AvatarInterestsReply) {
        DefaultMessageHandler(avatarInterestsReply)
    }

    fun HandleAvatarInterestsUpdate(avatarInterestsUpdate: AvatarInterestsUpdate) {
        DefaultMessageHandler(avatarInterestsUpdate)
    }

    fun HandleAvatarNotesReply(avatarNotesReply: AvatarNotesReply) {
        DefaultMessageHandler(avatarNotesReply)
    }

    fun HandleAvatarNotesUpdate(avatarNotesUpdate: AvatarNotesUpdate) {
        DefaultMessageHandler(avatarNotesUpdate)
    }

    fun HandleAvatarPickerReply(avatarPickerReply: AvatarPickerReply) {
        DefaultMessageHandler(avatarPickerReply)
    }

    fun HandleAvatarPickerRequest(avatarPickerRequest: AvatarPickerRequest) {
        DefaultMessageHandler(avatarPickerRequest)
    }

    fun HandleAvatarPickerRequestBackend(avatarPickerRequestBackend: AvatarPickerRequestBackend) {
        DefaultMessageHandler(avatarPickerRequestBackend)
    }

    fun HandleAvatarPicksReply(avatarPicksReply: AvatarPicksReply) {
        DefaultMessageHandler(avatarPicksReply)
    }

    fun HandleAvatarPropertiesReply(avatarPropertiesReply: AvatarPropertiesReply) {
        DefaultMessageHandler(avatarPropertiesReply)
    }

    fun HandleAvatarPropertiesRequest(avatarPropertiesRequest: AvatarPropertiesRequest) {
        DefaultMessageHandler(avatarPropertiesRequest)
    }

    fun HandleAvatarPropertiesRequestBackend(avatarPropertiesRequestBackend: AvatarPropertiesRequestBackend) {
        DefaultMessageHandler(avatarPropertiesRequestBackend)
    }

    fun HandleAvatarPropertiesUpdate(avatarPropertiesUpdate: AvatarPropertiesUpdate) {
        DefaultMessageHandler(avatarPropertiesUpdate)
    }

    fun HandleAvatarSitResponse(avatarSitResponse: AvatarSitResponse) {
        DefaultMessageHandler(avatarSitResponse)
    }

    fun HandleAvatarTextureUpdate(avatarTextureUpdate: AvatarTextureUpdate) {
        DefaultMessageHandler(avatarTextureUpdate)
    }

    fun HandleBulkUpdateInventory(bulkUpdateInventory: BulkUpdateInventory) {
        DefaultMessageHandler(bulkUpdateInventory)
    }

    fun HandleBuyObjectInventory(buyObjectInventory: BuyObjectInventory) {
        DefaultMessageHandler(buyObjectInventory)
    }

    fun HandleCameraConstraint(cameraConstraint: CameraConstraint) {
        DefaultMessageHandler(cameraConstraint)
    }

    fun HandleCancelAuction(cancelAuction: CancelAuction) {
        DefaultMessageHandler(cancelAuction)
    }

    fun HandleChangeInventoryItemFlags(changeInventoryItemFlags: ChangeInventoryItemFlags) {
        DefaultMessageHandler(changeInventoryItemFlags)
    }

    fun HandleChangeUserRights(changeUserRights: ChangeUserRights) {
        DefaultMessageHandler(changeUserRights)
    }

    fun HandleChatFromSimulator(chatFromSimulator: ChatFromSimulator) {
        DefaultMessageHandler(chatFromSimulator)
    }

    fun HandleChatFromViewer(chatFromViewer: ChatFromViewer) {
        DefaultMessageHandler(chatFromViewer)
    }

    fun HandleChatPass(chatPass: ChatPass) {
        DefaultMessageHandler(chatPass)
    }

    fun HandleCheckParcelAuctions(checkParcelAuctions: CheckParcelAuctions) {
        DefaultMessageHandler(checkParcelAuctions)
    }

    fun HandleCheckParcelSales(checkParcelSales: CheckParcelSales) {
        DefaultMessageHandler(checkParcelSales)
    }

    fun HandleChildAgentAlive(childAgentAlive: ChildAgentAlive) {
        DefaultMessageHandler(childAgentAlive)
    }

    fun HandleChildAgentDying(childAgentDying: ChildAgentDying) {
        DefaultMessageHandler(childAgentDying)
    }

    fun HandleChildAgentPositionUpdate(childAgentPositionUpdate: ChildAgentPositionUpdate) {
        DefaultMessageHandler(childAgentPositionUpdate)
    }

    fun HandleChildAgentUnknown(childAgentUnknown: ChildAgentUnknown) {
        DefaultMessageHandler(childAgentUnknown)
    }

    fun HandleChildAgentUpdate(childAgentUpdate: ChildAgentUpdate) {
        DefaultMessageHandler(childAgentUpdate)
    }

    fun HandleClassifiedDelete(classifiedDelete: ClassifiedDelete) {
        DefaultMessageHandler(classifiedDelete)
    }

    fun HandleClassifiedGodDelete(classifiedGodDelete: ClassifiedGodDelete) {
        DefaultMessageHandler(classifiedGodDelete)
    }

    fun HandleClassifiedInfoReply(classifiedInfoReply: ClassifiedInfoReply) {
        DefaultMessageHandler(classifiedInfoReply)
    }

    fun HandleClassifiedInfoRequest(classifiedInfoRequest: ClassifiedInfoRequest) {
        DefaultMessageHandler(classifiedInfoRequest)
    }

    fun HandleClassifiedInfoUpdate(classifiedInfoUpdate: ClassifiedInfoUpdate) {
        DefaultMessageHandler(classifiedInfoUpdate)
    }

    fun HandleClearFollowCamProperties(clearFollowCamProperties: ClearFollowCamProperties) {
        DefaultMessageHandler(clearFollowCamProperties)
    }

    fun HandleCloseCircuit(closeCircuit: CloseCircuit) {
        DefaultMessageHandler(closeCircuit)
    }

    fun HandleCoarseLocationUpdate(coarseLocationUpdate: CoarseLocationUpdate) {
        DefaultMessageHandler(coarseLocationUpdate)
    }

    fun HandleCompleteAgentMovement(completeAgentMovement: CompleteAgentMovement) {
        DefaultMessageHandler(completeAgentMovement)
    }

    fun HandleCompleteAuction(completeAuction: CompleteAuction) {
        DefaultMessageHandler(completeAuction)
    }

    fun HandleCompletePingCheck(completePingCheck: CompletePingCheck) {
        DefaultMessageHandler(completePingCheck)
    }

    fun HandleConfirmAuctionStart(confirmAuctionStart: ConfirmAuctionStart) {
        DefaultMessageHandler(confirmAuctionStart)
    }

    fun HandleConfirmEnableSimulator(confirmEnableSimulator: ConfirmEnableSimulator) {
        DefaultMessageHandler(confirmEnableSimulator)
    }

    fun HandleConfirmXferPacket(confirmXferPacket: ConfirmXferPacket) {
        DefaultMessageHandler(confirmXferPacket)
    }

    fun HandleCopyInventoryFromNotecard(copyInventoryFromNotecard: CopyInventoryFromNotecard) {
        DefaultMessageHandler(copyInventoryFromNotecard)
    }

    fun HandleCopyInventoryItem(copyInventoryItem: CopyInventoryItem) {
        DefaultMessageHandler(copyInventoryItem)
    }

    fun HandleCreateGroupReply(createGroupReply: CreateGroupReply) {
        DefaultMessageHandler(createGroupReply)
    }

    fun HandleCreateGroupRequest(createGroupRequest: CreateGroupRequest) {
        DefaultMessageHandler(createGroupRequest)
    }

    fun HandleCreateInventoryFolder(createInventoryFolder: CreateInventoryFolder) {
        DefaultMessageHandler(createInventoryFolder)
    }

    fun HandleCreateInventoryItem(createInventoryItem: CreateInventoryItem) {
        DefaultMessageHandler(createInventoryItem)
    }

    fun HandleCreateLandmarkForEvent(createLandmarkForEvent: CreateLandmarkForEvent) {
        DefaultMessageHandler(createLandmarkForEvent)
    }

    fun HandleCreateNewOutfitAttachments(createNewOutfitAttachments: CreateNewOutfitAttachments) {
        DefaultMessageHandler(createNewOutfitAttachments)
    }

    fun HandleCreateTrustedCircuit(createTrustedCircuit: CreateTrustedCircuit) {
        DefaultMessageHandler(createTrustedCircuit)
    }

    fun HandleCrossedRegion(crossedRegion: CrossedRegion) {
        DefaultMessageHandler(crossedRegion)
    }

    fun HandleDataHomeLocationReply(dataHomeLocationReply: DataHomeLocationReply) {
        DefaultMessageHandler(dataHomeLocationReply)
    }

    fun HandleDataHomeLocationRequest(dataHomeLocationRequest: DataHomeLocationRequest) {
        DefaultMessageHandler(dataHomeLocationRequest)
    }

    fun HandleDataServerLogout(dataServerLogout: DataServerLogout) {
        DefaultMessageHandler(dataServerLogout)
    }

    fun HandleDeRezAck(deRezAck: DeRezAck) {
        DefaultMessageHandler(deRezAck)
    }

    fun HandleDeRezObject(deRezObject: DeRezObject) {
        DefaultMessageHandler(deRezObject)
    }

    fun HandleDeactivateGestures(deactivateGestures: DeactivateGestures) {
        DefaultMessageHandler(deactivateGestures)
    }

    fun HandleDeclineCallingCard(declineCallingCard: DeclineCallingCard) {
        DefaultMessageHandler(declineCallingCard)
    }

    fun HandleDeclineFriendship(declineFriendship: DeclineFriendship) {
        DefaultMessageHandler(declineFriendship)
    }

    fun HandleDenyTrustedCircuit(denyTrustedCircuit: DenyTrustedCircuit) {
        DefaultMessageHandler(denyTrustedCircuit)
    }

    fun HandleDerezContainer(derezContainer: DerezContainer) {
        DefaultMessageHandler(derezContainer)
    }

    fun HandleDetachAttachmentIntoInv(detachAttachmentIntoInv: DetachAttachmentIntoInv) {
        DefaultMessageHandler(detachAttachmentIntoInv)
    }

    fun HandleDirClassifiedQuery(dirClassifiedQuery: DirClassifiedQuery) {
        DefaultMessageHandler(dirClassifiedQuery)
    }

    fun HandleDirClassifiedQueryBackend(dirClassifiedQueryBackend: DirClassifiedQueryBackend) {
        DefaultMessageHandler(dirClassifiedQueryBackend)
    }

    fun HandleDirClassifiedReply(dirClassifiedReply: DirClassifiedReply) {
        DefaultMessageHandler(dirClassifiedReply)
    }

    fun HandleDirEventsReply(dirEventsReply: DirEventsReply) {
        DefaultMessageHandler(dirEventsReply)
    }

    fun HandleDirFindQuery(dirFindQuery: DirFindQuery) {
        DefaultMessageHandler(dirFindQuery)
    }

    fun HandleDirFindQueryBackend(dirFindQueryBackend: DirFindQueryBackend) {
        DefaultMessageHandler(dirFindQueryBackend)
    }

    fun HandleDirGroupsReply(dirGroupsReply: DirGroupsReply) {
        DefaultMessageHandler(dirGroupsReply)
    }

    fun HandleDirLandQuery(dirLandQuery: DirLandQuery) {
        DefaultMessageHandler(dirLandQuery)
    }

    fun HandleDirLandQueryBackend(dirLandQueryBackend: DirLandQueryBackend) {
        DefaultMessageHandler(dirLandQueryBackend)
    }

    fun HandleDirLandReply(dirLandReply: DirLandReply) {
        DefaultMessageHandler(dirLandReply)
    }

    fun HandleDirPeopleReply(dirPeopleReply: DirPeopleReply) {
        DefaultMessageHandler(dirPeopleReply)
    }

    fun HandleDirPlacesQuery(dirPlacesQuery: DirPlacesQuery) {
        DefaultMessageHandler(dirPlacesQuery)
    }

    fun HandleDirPlacesQueryBackend(dirPlacesQueryBackend: DirPlacesQueryBackend) {
        DefaultMessageHandler(dirPlacesQueryBackend)
    }

    fun HandleDirPlacesReply(dirPlacesReply: DirPlacesReply) {
        DefaultMessageHandler(dirPlacesReply)
    }

    fun HandleDirPopularQuery(dirPopularQuery: DirPopularQuery) {
        DefaultMessageHandler(dirPopularQuery)
    }

    fun HandleDirPopularQueryBackend(dirPopularQueryBackend: DirPopularQueryBackend) {
        DefaultMessageHandler(dirPopularQueryBackend)
    }

    fun HandleDirPopularReply(dirPopularReply: DirPopularReply) {
        DefaultMessageHandler(dirPopularReply)
    }

    fun HandleDisableSimulator(disableSimulator: DisableSimulator) {
        DefaultMessageHandler(disableSimulator)
    }

    fun HandleEconomyData(economyData: EconomyData) {
        DefaultMessageHandler(economyData)
    }

    fun HandleEconomyDataRequest(economyDataRequest: EconomyDataRequest) {
        DefaultMessageHandler(economyDataRequest)
    }

    fun HandleEdgeDataPacket(edgeDataPacket: EdgeDataPacket) {
        DefaultMessageHandler(edgeDataPacket)
    }

    fun HandleEjectGroupMemberReply(ejectGroupMemberReply: EjectGroupMemberReply) {
        DefaultMessageHandler(ejectGroupMemberReply)
    }

    fun HandleEjectGroupMemberRequest(ejectGroupMemberRequest: EjectGroupMemberRequest) {
        DefaultMessageHandler(ejectGroupMemberRequest)
    }

    fun HandleEjectUser(ejectUser: EjectUser) {
        DefaultMessageHandler(ejectUser)
    }

    fun HandleEmailMessageReply(emailMessageReply: EmailMessageReply) {
        DefaultMessageHandler(emailMessageReply)
    }

    fun HandleEmailMessageRequest(emailMessageRequest: EmailMessageRequest) {
        DefaultMessageHandler(emailMessageRequest)
    }

    fun HandleEnableSimulator(enableSimulator: EnableSimulator) {
        DefaultMessageHandler(enableSimulator)
    }

    fun HandleError(error: Error) {
        DefaultMessageHandler(error)
    }

    fun HandleEstateCovenantReply(estateCovenantReply: EstateCovenantReply) {
        DefaultMessageHandler(estateCovenantReply)
    }

    fun HandleEstateCovenantRequest(estateCovenantRequest: EstateCovenantRequest) {
        DefaultMessageHandler(estateCovenantRequest)
    }

    fun HandleEstateOwnerMessage(estateOwnerMessage: EstateOwnerMessage) {
        DefaultMessageHandler(estateOwnerMessage)
    }

    fun HandleEventGodDelete(eventGodDelete: EventGodDelete) {
        DefaultMessageHandler(eventGodDelete)
    }

    fun HandleEventInfoReply(eventInfoReply: EventInfoReply) {
        DefaultMessageHandler(eventInfoReply)
    }

    fun HandleEventInfoRequest(eventInfoRequest: EventInfoRequest) {
        DefaultMessageHandler(eventInfoRequest)
    }

    fun HandleEventLocationReply(eventLocationReply: EventLocationReply) {
        DefaultMessageHandler(eventLocationReply)
    }

    fun HandleEventLocationRequest(eventLocationRequest: EventLocationRequest) {
        DefaultMessageHandler(eventLocationRequest)
    }

    fun HandleEventNotificationAddRequest(eventNotificationAddRequest: EventNotificationAddRequest) {
        DefaultMessageHandler(eventNotificationAddRequest)
    }

    fun HandleEventNotificationRemoveRequest(eventNotificationRemoveRequest: EventNotificationRemoveRequest) {
        DefaultMessageHandler(eventNotificationRemoveRequest)
    }

    fun HandleFeatureDisabled(featureDisabled: FeatureDisabled) {
        DefaultMessageHandler(featureDisabled)
    }

    fun HandleFetchInventory(fetchInventory: FetchInventory) {
        DefaultMessageHandler(fetchInventory)
    }

    fun HandleFetchInventoryDescendents(fetchInventoryDescendents: FetchInventoryDescendents) {
        DefaultMessageHandler(fetchInventoryDescendents)
    }

    fun HandleFetchInventoryReply(fetchInventoryReply: FetchInventoryReply) {
        DefaultMessageHandler(fetchInventoryReply)
    }

    fun HandleFindAgent(findAgent: FindAgent) {
        DefaultMessageHandler(findAgent)
    }

    fun HandleForceObjectSelect(forceObjectSelect: ForceObjectSelect) {
        DefaultMessageHandler(forceObjectSelect)
    }

    fun HandleForceScriptControlRelease(forceScriptControlRelease: ForceScriptControlRelease) {
        DefaultMessageHandler(forceScriptControlRelease)
    }

    fun HandleFormFriendship(formFriendship: FormFriendship) {
        DefaultMessageHandler(formFriendship)
    }

    fun HandleFreezeUser(freezeUser: FreezeUser) {
        DefaultMessageHandler(freezeUser)
    }

    fun HandleGenericMessage(genericMessage: GenericMessage) {
        DefaultMessageHandler(genericMessage)
    }

    fun HandleGetScriptRunning(getScriptRunning: GetScriptRunning) {
        DefaultMessageHandler(getScriptRunning)
    }

    fun HandleGodKickUser(godKickUser: GodKickUser) {
        DefaultMessageHandler(godKickUser)
    }

    fun HandleGodUpdateRegionInfo(godUpdateRegionInfo: GodUpdateRegionInfo) {
        DefaultMessageHandler(godUpdateRegionInfo)
    }

    fun HandleGodlikeMessage(godlikeMessage: GodlikeMessage) {
        DefaultMessageHandler(godlikeMessage)
    }

    fun HandleGrantGodlikePowers(grantGodlikePowers: GrantGodlikePowers) {
        DefaultMessageHandler(grantGodlikePowers)
    }

    fun HandleGrantUserRights(grantUserRights: GrantUserRights) {
        DefaultMessageHandler(grantUserRights)
    }

    fun HandleGroupAccountDetailsReply(groupAccountDetailsReply: GroupAccountDetailsReply) {
        DefaultMessageHandler(groupAccountDetailsReply)
    }

    fun HandleGroupAccountDetailsRequest(groupAccountDetailsRequest: GroupAccountDetailsRequest) {
        DefaultMessageHandler(groupAccountDetailsRequest)
    }

    fun HandleGroupAccountSummaryReply(groupAccountSummaryReply: GroupAccountSummaryReply) {
        DefaultMessageHandler(groupAccountSummaryReply)
    }

    fun HandleGroupAccountSummaryRequest(groupAccountSummaryRequest: GroupAccountSummaryRequest) {
        DefaultMessageHandler(groupAccountSummaryRequest)
    }

    fun HandleGroupAccountTransactionsReply(groupAccountTransactionsReply: GroupAccountTransactionsReply) {
        DefaultMessageHandler(groupAccountTransactionsReply)
    }

    fun HandleGroupAccountTransactionsRequest(groupAccountTransactionsRequest: GroupAccountTransactionsRequest) {
        DefaultMessageHandler(groupAccountTransactionsRequest)
    }

    fun HandleGroupActiveProposalItemReply(groupActiveProposalItemReply: GroupActiveProposalItemReply) {
        DefaultMessageHandler(groupActiveProposalItemReply)
    }

    fun HandleGroupActiveProposalsRequest(groupActiveProposalsRequest: GroupActiveProposalsRequest) {
        DefaultMessageHandler(groupActiveProposalsRequest)
    }

    fun HandleGroupDataUpdate(groupDataUpdate: GroupDataUpdate) {
        DefaultMessageHandler(groupDataUpdate)
    }

    fun HandleGroupMembersReply(groupMembersReply: GroupMembersReply) {
        DefaultMessageHandler(groupMembersReply)
    }

    fun HandleGroupMembersRequest(groupMembersRequest: GroupMembersRequest) {
        DefaultMessageHandler(groupMembersRequest)
    }

    fun HandleGroupNoticeAdd(groupNoticeAdd: GroupNoticeAdd) {
        DefaultMessageHandler(groupNoticeAdd)
    }

    fun HandleGroupNoticeRequest(groupNoticeRequest: GroupNoticeRequest) {
        DefaultMessageHandler(groupNoticeRequest)
    }

    fun HandleGroupNoticesListReply(groupNoticesListReply: GroupNoticesListReply) {
        DefaultMessageHandler(groupNoticesListReply)
    }

    fun HandleGroupNoticesListRequest(groupNoticesListRequest: GroupNoticesListRequest) {
        DefaultMessageHandler(groupNoticesListRequest)
    }

    fun HandleGroupProfileReply(groupProfileReply: GroupProfileReply) {
        DefaultMessageHandler(groupProfileReply)
    }

    fun HandleGroupProfileRequest(groupProfileRequest: GroupProfileRequest) {
        DefaultMessageHandler(groupProfileRequest)
    }

    fun HandleGroupProposalBallot(groupProposalBallot: GroupProposalBallot) {
        DefaultMessageHandler(groupProposalBallot)
    }

    fun HandleGroupRoleChanges(groupRoleChanges: GroupRoleChanges) {
        DefaultMessageHandler(groupRoleChanges)
    }

    fun HandleGroupRoleDataReply(groupRoleDataReply: GroupRoleDataReply) {
        DefaultMessageHandler(groupRoleDataReply)
    }

    fun HandleGroupRoleDataRequest(groupRoleDataRequest: GroupRoleDataRequest) {
        DefaultMessageHandler(groupRoleDataRequest)
    }

    fun HandleGroupRoleMembersReply(groupRoleMembersReply: GroupRoleMembersReply) {
        DefaultMessageHandler(groupRoleMembersReply)
    }

    fun HandleGroupRoleMembersRequest(groupRoleMembersRequest: GroupRoleMembersRequest) {
        DefaultMessageHandler(groupRoleMembersRequest)
    }

    fun HandleGroupRoleUpdate(groupRoleUpdate: GroupRoleUpdate) {
        DefaultMessageHandler(groupRoleUpdate)
    }

    fun HandleGroupTitleUpdate(groupTitleUpdate: GroupTitleUpdate) {
        DefaultMessageHandler(groupTitleUpdate)
    }

    fun HandleGroupTitlesReply(groupTitlesReply: GroupTitlesReply) {
        DefaultMessageHandler(groupTitlesReply)
    }

    fun HandleGroupTitlesRequest(groupTitlesRequest: GroupTitlesRequest) {
        DefaultMessageHandler(groupTitlesRequest)
    }

    fun HandleGroupVoteHistoryItemReply(groupVoteHistoryItemReply: GroupVoteHistoryItemReply) {
        DefaultMessageHandler(groupVoteHistoryItemReply)
    }

    fun HandleGroupVoteHistoryRequest(groupVoteHistoryRequest: GroupVoteHistoryRequest) {
        DefaultMessageHandler(groupVoteHistoryRequest)
    }

    fun HandleHealthMessage(healthMessage: HealthMessage) {
        DefaultMessageHandler(healthMessage)
    }

    fun HandleImageData(imageData: ImageData) {
        DefaultMessageHandler(imageData)
    }

    fun HandleImageNotInDatabase(imageNotInDatabase: ImageNotInDatabase) {
        DefaultMessageHandler(imageNotInDatabase)
    }

    fun HandleImagePacket(imagePacket: ImagePacket) {
        DefaultMessageHandler(imagePacket)
    }

    fun HandleImprovedInstantMessage(improvedInstantMessage: ImprovedInstantMessage) {
        DefaultMessageHandler(improvedInstantMessage)
    }

    fun HandleImprovedTerseObjectUpdate(improvedTerseObjectUpdate: ImprovedTerseObjectUpdate) {
        DefaultMessageHandler(improvedTerseObjectUpdate)
    }

    fun HandleInitiateDownload(initiateDownload: InitiateDownload) {
        DefaultMessageHandler(initiateDownload)
    }

    fun HandleInternalScriptMail(internalScriptMail: InternalScriptMail) {
        DefaultMessageHandler(internalScriptMail)
    }

    fun HandleInventoryAssetResponse(inventoryAssetResponse: InventoryAssetResponse) {
        DefaultMessageHandler(inventoryAssetResponse)
    }

    fun HandleInventoryDescendents(inventoryDescendents: InventoryDescendents) {
        DefaultMessageHandler(inventoryDescendents)
    }

    fun HandleInviteGroupRequest(inviteGroupRequest: InviteGroupRequest) {
        DefaultMessageHandler(inviteGroupRequest)
    }

    fun HandleInviteGroupResponse(inviteGroupResponse: InviteGroupResponse) {
        DefaultMessageHandler(inviteGroupResponse)
    }

    fun HandleJoinGroupReply(joinGroupReply: JoinGroupReply) {
        DefaultMessageHandler(joinGroupReply)
    }

    fun HandleJoinGroupRequest(joinGroupRequest: JoinGroupRequest) {
        DefaultMessageHandler(joinGroupRequest)
    }

    fun HandleKickUser(kickUser: KickUser) {
        DefaultMessageHandler(kickUser)
    }

    fun HandleKickUserAck(kickUserAck: KickUserAck) {
        DefaultMessageHandler(kickUserAck)
    }

    fun HandleKillChildAgents(killChildAgents: KillChildAgents) {
        DefaultMessageHandler(killChildAgents)
    }

    fun HandleKillObject(killObject: KillObject) {
        DefaultMessageHandler(killObject)
    }

    fun HandleLandStatReply(landStatReply: LandStatReply) {
        DefaultMessageHandler(landStatReply)
    }

    fun HandleLandStatRequest(landStatRequest: LandStatRequest) {
        DefaultMessageHandler(landStatRequest)
    }

    fun HandleLayerData(layerData: LayerData) {
        DefaultMessageHandler(layerData)
    }

    fun HandleLeaveGroupReply(leaveGroupReply: LeaveGroupReply) {
        DefaultMessageHandler(leaveGroupReply)
    }

    fun HandleLeaveGroupRequest(leaveGroupRequest: LeaveGroupRequest) {
        DefaultMessageHandler(leaveGroupRequest)
    }

    fun HandleLinkInventoryItem(linkInventoryItem: LinkInventoryItem) {
        DefaultMessageHandler(linkInventoryItem)
    }

    fun HandleLiveHelpGroupReply(liveHelpGroupReply: LiveHelpGroupReply) {
        DefaultMessageHandler(liveHelpGroupReply)
    }

    fun HandleLiveHelpGroupRequest(liveHelpGroupRequest: LiveHelpGroupRequest) {
        DefaultMessageHandler(liveHelpGroupRequest)
    }

    fun HandleLoadURL(loadURL: LoadURL) {
        DefaultMessageHandler(loadURL)
    }

    fun HandleLogDwellTime(logDwellTime: LogDwellTime) {
        DefaultMessageHandler(logDwellTime)
    }

    fun HandleLogFailedMoneyTransaction(logFailedMoneyTransaction: LogFailedMoneyTransaction) {
        DefaultMessageHandler(logFailedMoneyTransaction)
    }

    fun HandleLogParcelChanges(logParcelChanges: LogParcelChanges) {
        DefaultMessageHandler(logParcelChanges)
    }

    fun HandleLogTextMessage(logTextMessage: LogTextMessage) {
        DefaultMessageHandler(logTextMessage)
    }

    fun HandleLogoutReply(logoutReply: LogoutReply) {
        DefaultMessageHandler(logoutReply)
    }

    fun HandleLogoutRequest(logoutRequest: LogoutRequest) {
        DefaultMessageHandler(logoutRequest)
    }

    fun HandleMapBlockReply(mapBlockReply: MapBlockReply) {
        DefaultMessageHandler(mapBlockReply)
    }

    fun HandleMapBlockRequest(mapBlockRequest: MapBlockRequest) {
        DefaultMessageHandler(mapBlockRequest)
    }

    fun HandleMapItemReply(mapItemReply: MapItemReply) {
        DefaultMessageHandler(mapItemReply)
    }

    fun HandleMapItemRequest(mapItemRequest: MapItemRequest) {
        DefaultMessageHandler(mapItemRequest)
    }

    fun HandleMapLayerReply(mapLayerReply: MapLayerReply) {
        DefaultMessageHandler(mapLayerReply)
    }

    fun HandleMapLayerRequest(mapLayerRequest: MapLayerRequest) {
        DefaultMessageHandler(mapLayerRequest)
    }

    fun HandleMapNameRequest(mapNameRequest: MapNameRequest) {
        DefaultMessageHandler(mapNameRequest)
    }

    fun HandleMeanCollisionAlert(meanCollisionAlert: MeanCollisionAlert) {
        DefaultMessageHandler(meanCollisionAlert)
    }

    fun HandleMergeParcel(mergeParcel: MergeParcel) {
        DefaultMessageHandler(mergeParcel)
    }

    fun HandleModifyLand(modifyLand: ModifyLand) {
        DefaultMessageHandler(modifyLand)
    }

    fun HandleMoneyBalanceReply(moneyBalanceReply: MoneyBalanceReply) {
        DefaultMessageHandler(moneyBalanceReply)
    }

    fun HandleMoneyBalanceRequest(moneyBalanceRequest: MoneyBalanceRequest) {
        DefaultMessageHandler(moneyBalanceRequest)
    }

    fun HandleMoneyTransferBackend(moneyTransferBackend: MoneyTransferBackend) {
        DefaultMessageHandler(moneyTransferBackend)
    }

    fun HandleMoneyTransferRequest(moneyTransferRequest: MoneyTransferRequest) {
        DefaultMessageHandler(moneyTransferRequest)
    }

    fun HandleMoveInventoryFolder(moveInventoryFolder: MoveInventoryFolder) {
        DefaultMessageHandler(moveInventoryFolder)
    }

    fun HandleMoveInventoryItem(moveInventoryItem: MoveInventoryItem) {
        DefaultMessageHandler(moveInventoryItem)
    }

    fun HandleMoveTaskInventory(moveTaskInventory: MoveTaskInventory) {
        DefaultMessageHandler(moveTaskInventory)
    }

    fun HandleMultipleObjectUpdate(multipleObjectUpdate: MultipleObjectUpdate) {
        DefaultMessageHandler(multipleObjectUpdate)
    }

    fun HandleMuteListRequest(muteListRequest: MuteListRequest) {
        DefaultMessageHandler(muteListRequest)
    }

    fun HandleMuteListUpdate(muteListUpdate: MuteListUpdate) {
        DefaultMessageHandler(muteListUpdate)
    }

    fun HandleNameValuePair(nameValuePair: NameValuePair) {
        DefaultMessageHandler(nameValuePair)
    }

    fun HandleNearestLandingRegionReply(nearestLandingRegionReply: NearestLandingRegionReply) {
        DefaultMessageHandler(nearestLandingRegionReply)
    }

    fun HandleNearestLandingRegionRequest(nearestLandingRegionRequest: NearestLandingRegionRequest) {
        DefaultMessageHandler(nearestLandingRegionRequest)
    }

    fun HandleNearestLandingRegionUpdated(nearestLandingRegionUpdated: NearestLandingRegionUpdated) {
        DefaultMessageHandler(nearestLandingRegionUpdated)
    }

    fun HandleNeighborList(neighborList: NeighborList) {
        DefaultMessageHandler(neighborList)
    }

    fun HandleNetTest(netTest: NetTest) {
        DefaultMessageHandler(netTest)
    }

    fun HandleObjectAdd(objectAdd: ObjectAdd) {
        DefaultMessageHandler(objectAdd)
    }

    fun HandleObjectAttach(objectAttach: ObjectAttach) {
        DefaultMessageHandler(objectAttach)
    }

    fun HandleObjectBuy(objectBuy: ObjectBuy) {
        DefaultMessageHandler(objectBuy)
    }

    fun HandleObjectCategory(objectCategory: ObjectCategory) {
        DefaultMessageHandler(objectCategory)
    }

    fun HandleObjectClickAction(objectClickAction: ObjectClickAction) {
        DefaultMessageHandler(objectClickAction)
    }

    fun HandleObjectDeGrab(objectDeGrab: ObjectDeGrab) {
        DefaultMessageHandler(objectDeGrab)
    }

    fun HandleObjectDelete(objectDelete: ObjectDelete) {
        DefaultMessageHandler(objectDelete)
    }

    fun HandleObjectDelink(objectDelink: ObjectDelink) {
        DefaultMessageHandler(objectDelink)
    }

    fun HandleObjectDescription(objectDescription: ObjectDescription) {
        DefaultMessageHandler(objectDescription)
    }

    fun HandleObjectDeselect(objectDeselect: ObjectDeselect) {
        DefaultMessageHandler(objectDeselect)
    }

    fun HandleObjectDetach(objectDetach: ObjectDetach) {
        DefaultMessageHandler(objectDetach)
    }

    fun HandleObjectDrop(objectDrop: ObjectDrop) {
        DefaultMessageHandler(objectDrop)
    }

    fun HandleObjectDuplicate(objectDuplicate: ObjectDuplicate) {
        DefaultMessageHandler(objectDuplicate)
    }

    fun HandleObjectDuplicateOnRay(objectDuplicateOnRay: ObjectDuplicateOnRay) {
        DefaultMessageHandler(objectDuplicateOnRay)
    }

    fun HandleObjectExportSelected(objectExportSelected: ObjectExportSelected) {
        DefaultMessageHandler(objectExportSelected)
    }

    fun HandleObjectExtraParams(objectExtraParams: ObjectExtraParams) {
        DefaultMessageHandler(objectExtraParams)
    }

    fun HandleObjectFlagUpdate(objectFlagUpdate: ObjectFlagUpdate) {
        DefaultMessageHandler(objectFlagUpdate)
    }

    fun HandleObjectGrab(objectGrab: ObjectGrab) {
        DefaultMessageHandler(objectGrab)
    }

    fun HandleObjectGrabUpdate(objectGrabUpdate: ObjectGrabUpdate) {
        DefaultMessageHandler(objectGrabUpdate)
    }

    fun HandleObjectGroup(objectGroup: ObjectGroup) {
        DefaultMessageHandler(objectGroup)
    }

    fun HandleObjectImage(objectImage: ObjectImage) {
        DefaultMessageHandler(objectImage)
    }

    fun HandleObjectIncludeInSearch(objectIncludeInSearch: ObjectIncludeInSearch) {
        DefaultMessageHandler(objectIncludeInSearch)
    }

    fun HandleObjectLink(objectLink: ObjectLink) {
        DefaultMessageHandler(objectLink)
    }

    fun HandleObjectMaterial(objectMaterial: ObjectMaterial) {
        DefaultMessageHandler(objectMaterial)
    }

    fun HandleObjectName(objectName: ObjectName) {
        DefaultMessageHandler(objectName)
    }

    fun HandleObjectOwner(objectOwner: ObjectOwner) {
        DefaultMessageHandler(objectOwner)
    }

    fun HandleObjectPermissions(objectPermissions: ObjectPermissions) {
        DefaultMessageHandler(objectPermissions)
    }

    fun HandleObjectPosition(objectPosition: ObjectPosition) {
        DefaultMessageHandler(objectPosition)
    }

    fun HandleObjectProperties(objectProperties: ObjectProperties) {
        DefaultMessageHandler(objectProperties)
    }

    fun HandleObjectPropertiesFamily(objectPropertiesFamily: ObjectPropertiesFamily) {
        DefaultMessageHandler(objectPropertiesFamily)
    }

    fun HandleObjectRotation(objectRotation: ObjectRotation) {
        DefaultMessageHandler(objectRotation)
    }

    fun HandleObjectSaleInfo(objectSaleInfo: ObjectSaleInfo) {
        DefaultMessageHandler(objectSaleInfo)
    }

    fun HandleObjectScale(objectScale: ObjectScale) {
        DefaultMessageHandler(objectScale)
    }

    fun HandleObjectSelect(objectSelect: ObjectSelect) {
        DefaultMessageHandler(objectSelect)
    }

    fun HandleObjectShape(objectShape: ObjectShape) {
        DefaultMessageHandler(objectShape)
    }

    fun HandleObjectSpinStart(objectSpinStart: ObjectSpinStart) {
        DefaultMessageHandler(objectSpinStart)
    }

    fun HandleObjectSpinStop(objectSpinStop: ObjectSpinStop) {
        DefaultMessageHandler(objectSpinStop)
    }

    fun HandleObjectSpinUpdate(objectSpinUpdate: ObjectSpinUpdate) {
        DefaultMessageHandler(objectSpinUpdate)
    }

    fun HandleObjectUpdate(objectUpdate: ObjectUpdate) {
        DefaultMessageHandler(objectUpdate)
    }

    fun HandleObjectUpdateCached(objectUpdateCached: ObjectUpdateCached) {
        DefaultMessageHandler(objectUpdateCached)
    }

    fun HandleObjectUpdateCompressed(objectUpdateCompressed: ObjectUpdateCompressed) {
        DefaultMessageHandler(objectUpdateCompressed)
    }

    fun HandleOfferCallingCard(offerCallingCard: OfferCallingCard) {
        DefaultMessageHandler(offerCallingCard)
    }

    fun HandleOfflineNotification(offlineNotification: OfflineNotification) {
        DefaultMessageHandler(offlineNotification)
    }

    fun HandleOnlineNotification(onlineNotification: OnlineNotification) {
        DefaultMessageHandler(onlineNotification)
    }

    fun HandleOpenCircuit(openCircuit: OpenCircuit) {
        DefaultMessageHandler(openCircuit)
    }

    fun HandlePacketAck(packetAck: PacketAck) {
        DefaultMessageHandler(packetAck)
    }

    fun HandleParcelAccessListReply(parcelAccessListReply: ParcelAccessListReply) {
        DefaultMessageHandler(parcelAccessListReply)
    }

    fun HandleParcelAccessListRequest(parcelAccessListRequest: ParcelAccessListRequest) {
        DefaultMessageHandler(parcelAccessListRequest)
    }

    fun HandleParcelAccessListUpdate(parcelAccessListUpdate: ParcelAccessListUpdate) {
        DefaultMessageHandler(parcelAccessListUpdate)
    }

    fun HandleParcelAuctions(parcelAuctions: ParcelAuctions) {
        DefaultMessageHandler(parcelAuctions)
    }

    fun HandleParcelBuy(parcelBuy: ParcelBuy) {
        DefaultMessageHandler(parcelBuy)
    }

    fun HandleParcelBuyPass(parcelBuyPass: ParcelBuyPass) {
        DefaultMessageHandler(parcelBuyPass)
    }

    fun HandleParcelClaim(parcelClaim: ParcelClaim) {
        DefaultMessageHandler(parcelClaim)
    }

    fun HandleParcelDeedToGroup(parcelDeedToGroup: ParcelDeedToGroup) {
        DefaultMessageHandler(parcelDeedToGroup)
    }

    fun HandleParcelDisableObjects(parcelDisableObjects: ParcelDisableObjects) {
        DefaultMessageHandler(parcelDisableObjects)
    }

    fun HandleParcelDivide(parcelDivide: ParcelDivide) {
        DefaultMessageHandler(parcelDivide)
    }

    fun HandleParcelDwellReply(parcelDwellReply: ParcelDwellReply) {
        DefaultMessageHandler(parcelDwellReply)
    }

    fun HandleParcelDwellRequest(parcelDwellRequest: ParcelDwellRequest) {
        DefaultMessageHandler(parcelDwellRequest)
    }

    fun HandleParcelGodForceOwner(parcelGodForceOwner: ParcelGodForceOwner) {
        DefaultMessageHandler(parcelGodForceOwner)
    }

    fun HandleParcelGodMarkAsContent(parcelGodMarkAsContent: ParcelGodMarkAsContent) {
        DefaultMessageHandler(parcelGodMarkAsContent)
    }

    fun HandleParcelInfoReply(parcelInfoReply: ParcelInfoReply) {
        DefaultMessageHandler(parcelInfoReply)
    }

    fun HandleParcelInfoRequest(parcelInfoRequest: ParcelInfoRequest) {
        DefaultMessageHandler(parcelInfoRequest)
    }

    fun HandleParcelJoin(parcelJoin: ParcelJoin) {
        DefaultMessageHandler(parcelJoin)
    }

    fun HandleParcelMediaCommandMessage(parcelMediaCommandMessage: ParcelMediaCommandMessage) {
        DefaultMessageHandler(parcelMediaCommandMessage)
    }

    fun HandleParcelMediaUpdate(parcelMediaUpdate: ParcelMediaUpdate) {
        DefaultMessageHandler(parcelMediaUpdate)
    }

    fun HandleParcelObjectOwnersReply(parcelObjectOwnersReply: ParcelObjectOwnersReply) {
        DefaultMessageHandler(parcelObjectOwnersReply)
    }

    fun HandleParcelObjectOwnersRequest(parcelObjectOwnersRequest: ParcelObjectOwnersRequest) {
        DefaultMessageHandler(parcelObjectOwnersRequest)
    }

    fun HandleParcelOverlay(parcelOverlay: ParcelOverlay) {
        DefaultMessageHandler(parcelOverlay)
    }

    fun HandleParcelProperties(parcelProperties: ParcelProperties) {
        DefaultMessageHandler(parcelProperties)
    }

    fun HandleParcelPropertiesRequest(parcelPropertiesRequest: ParcelPropertiesRequest) {
        DefaultMessageHandler(parcelPropertiesRequest)
    }

    fun HandleParcelPropertiesRequestByID(parcelPropertiesRequestByID: ParcelPropertiesRequestByID) {
        DefaultMessageHandler(parcelPropertiesRequestByID)
    }

    fun HandleParcelPropertiesUpdate(parcelPropertiesUpdate: ParcelPropertiesUpdate) {
        DefaultMessageHandler(parcelPropertiesUpdate)
    }

    fun HandleParcelReclaim(parcelReclaim: ParcelReclaim) {
        DefaultMessageHandler(parcelReclaim)
    }

    fun HandleParcelRelease(parcelRelease: ParcelRelease) {
        DefaultMessageHandler(parcelRelease)
    }

    fun HandleParcelRename(parcelRename: ParcelRename) {
        DefaultMessageHandler(parcelRename)
    }

    fun HandleParcelReturnObjects(parcelReturnObjects: ParcelReturnObjects) {
        DefaultMessageHandler(parcelReturnObjects)
    }

    fun HandleParcelSales(parcelSales: ParcelSales) {
        DefaultMessageHandler(parcelSales)
    }

    fun HandleParcelSelectObjects(parcelSelectObjects: ParcelSelectObjects) {
        DefaultMessageHandler(parcelSelectObjects)
    }

    fun HandleParcelSetOtherCleanTime(parcelSetOtherCleanTime: ParcelSetOtherCleanTime) {
        DefaultMessageHandler(parcelSetOtherCleanTime)
    }

    fun HandlePayPriceReply(payPriceReply: PayPriceReply) {
        DefaultMessageHandler(payPriceReply)
    }

    fun HandlePickDelete(pickDelete: PickDelete) {
        DefaultMessageHandler(pickDelete)
    }

    fun HandlePickGodDelete(pickGodDelete: PickGodDelete) {
        DefaultMessageHandler(pickGodDelete)
    }

    fun HandlePickInfoReply(pickInfoReply: PickInfoReply) {
        DefaultMessageHandler(pickInfoReply)
    }

    fun HandlePickInfoUpdate(pickInfoUpdate: PickInfoUpdate) {
        DefaultMessageHandler(pickInfoUpdate)
    }

    fun HandlePlacesQuery(placesQuery: PlacesQuery) {
        DefaultMessageHandler(placesQuery)
    }

    fun HandlePlacesReply(placesReply: PlacesReply) {
        DefaultMessageHandler(placesReply)
    }

    fun HandlePreloadSound(preloadSound: PreloadSound) {
        DefaultMessageHandler(preloadSound)
    }

    fun HandlePurgeInventoryDescendents(purgeInventoryDescendents: PurgeInventoryDescendents) {
        DefaultMessageHandler(purgeInventoryDescendents)
    }

    fun HandleRebakeAvatarTextures(rebakeAvatarTextures: RebakeAvatarTextures) {
        DefaultMessageHandler(rebakeAvatarTextures)
    }

    fun HandleRedo(redo: Redo) {
        DefaultMessageHandler(redo)
    }

    fun HandleRegionHandleRequest(regionHandleRequest: RegionHandleRequest) {
        DefaultMessageHandler(regionHandleRequest)
    }

    fun HandleRegionHandshake(regionHandshake: RegionHandshake) {
        DefaultMessageHandler(regionHandshake)
    }

    fun HandleRegionHandshakeReply(regionHandshakeReply: RegionHandshakeReply) {
        DefaultMessageHandler(regionHandshakeReply)
    }

    fun HandleRegionIDAndHandleReply(regionIDAndHandleReply: RegionIDAndHandleReply) {
        DefaultMessageHandler(regionIDAndHandleReply)
    }

    fun HandleRegionInfo(regionInfo: RegionInfo) {
        DefaultMessageHandler(regionInfo)
    }

    fun HandleRegionPresenceRequestByHandle(regionPresenceRequestByHandle: RegionPresenceRequestByHandle) {
        DefaultMessageHandler(regionPresenceRequestByHandle)
    }

    fun HandleRegionPresenceRequestByRegionID(regionPresenceRequestByRegionID: RegionPresenceRequestByRegionID) {
        DefaultMessageHandler(regionPresenceRequestByRegionID)
    }

    fun HandleRegionPresenceResponse(regionPresenceResponse: RegionPresenceResponse) {
        DefaultMessageHandler(regionPresenceResponse)
    }

    fun HandleRemoveAttachment(removeAttachment: RemoveAttachment) {
        DefaultMessageHandler(removeAttachment)
    }

    fun HandleRemoveInventoryFolder(removeInventoryFolder: RemoveInventoryFolder) {
        DefaultMessageHandler(removeInventoryFolder)
    }

    fun HandleRemoveInventoryItem(removeInventoryItem: RemoveInventoryItem) {
        DefaultMessageHandler(removeInventoryItem)
    }

    fun HandleRemoveInventoryObjects(removeInventoryObjects: RemoveInventoryObjects) {
        DefaultMessageHandler(removeInventoryObjects)
    }

    fun HandleRemoveMuteListEntry(removeMuteListEntry: RemoveMuteListEntry) {
        DefaultMessageHandler(removeMuteListEntry)
    }

    fun HandleRemoveNameValuePair(removeNameValuePair: RemoveNameValuePair) {
        DefaultMessageHandler(removeNameValuePair)
    }

    fun HandleRemoveParcel(removeParcel: RemoveParcel) {
        DefaultMessageHandler(removeParcel)
    }

    fun HandleRemoveTaskInventory(removeTaskInventory: RemoveTaskInventory) {
        DefaultMessageHandler(removeTaskInventory)
    }

    fun HandleReplyTaskInventory(replyTaskInventory: ReplyTaskInventory) {
        DefaultMessageHandler(replyTaskInventory)
    }

    fun HandleReportAutosaveCrash(reportAutosaveCrash: ReportAutosaveCrash) {
        DefaultMessageHandler(reportAutosaveCrash)
    }

    fun HandleRequestGodlikePowers(requestGodlikePowers: RequestGodlikePowers) {
        DefaultMessageHandler(requestGodlikePowers)
    }

    fun HandleRequestImage(requestImage: RequestImage) {
        DefaultMessageHandler(requestImage)
    }

    fun HandleRequestInventoryAsset(requestInventoryAsset: RequestInventoryAsset) {
        DefaultMessageHandler(requestInventoryAsset)
    }

    fun HandleRequestMultipleObjects(requestMultipleObjects: RequestMultipleObjects) {
        DefaultMessageHandler(requestMultipleObjects)
    }

    fun HandleRequestObjectPropertiesFamily(requestObjectPropertiesFamily: RequestObjectPropertiesFamily) {
        DefaultMessageHandler(requestObjectPropertiesFamily)
    }

    fun HandleRequestParcelTransfer(requestParcelTransfer: RequestParcelTransfer) {
        DefaultMessageHandler(requestParcelTransfer)
    }

    fun HandleRequestPayPrice(requestPayPrice: RequestPayPrice) {
        DefaultMessageHandler(requestPayPrice)
    }

    fun HandleRequestRegionInfo(requestRegionInfo: RequestRegionInfo) {
        DefaultMessageHandler(requestRegionInfo)
    }

    fun HandleRequestTaskInventory(requestTaskInventory: RequestTaskInventory) {
        DefaultMessageHandler(requestTaskInventory)
    }

    fun HandleRequestTrustedCircuit(requestTrustedCircuit: RequestTrustedCircuit) {
        DefaultMessageHandler(requestTrustedCircuit)
    }

    fun HandleRequestXfer(requestXfer: RequestXfer) {
        DefaultMessageHandler(requestXfer)
    }

    fun HandleRetrieveInstantMessages(retrieveInstantMessages: RetrieveInstantMessages) {
        DefaultMessageHandler(retrieveInstantMessages)
    }

    fun HandleRevokePermissions(revokePermissions: RevokePermissions) {
        DefaultMessageHandler(revokePermissions)
    }

    fun HandleRezMultipleAttachmentsFromInv(rezMultipleAttachmentsFromInv: RezMultipleAttachmentsFromInv) {
        DefaultMessageHandler(rezMultipleAttachmentsFromInv)
    }

    fun HandleRezObject(rezObject: RezObject) {
        DefaultMessageHandler(rezObject)
    }

    fun HandleRezObjectFromNotecard(rezObjectFromNotecard: RezObjectFromNotecard) {
        DefaultMessageHandler(rezObjectFromNotecard)
    }

    fun HandleRezRestoreToWorld(rezRestoreToWorld: RezRestoreToWorld) {
        DefaultMessageHandler(rezRestoreToWorld)
    }

    fun HandleRezScript(rezScript: RezScript) {
        DefaultMessageHandler(rezScript)
    }

    fun HandleRezSingleAttachmentFromInv(rezSingleAttachmentFromInv: RezSingleAttachmentFromInv) {
        DefaultMessageHandler(rezSingleAttachmentFromInv)
    }

    fun HandleRoutedMoneyBalanceReply(routedMoneyBalanceReply: RoutedMoneyBalanceReply) {
        DefaultMessageHandler(routedMoneyBalanceReply)
    }

    fun HandleRpcChannelReply(rpcChannelReply: RpcChannelReply) {
        DefaultMessageHandler(rpcChannelReply)
    }

    fun HandleRpcChannelRequest(rpcChannelRequest: RpcChannelRequest) {
        DefaultMessageHandler(rpcChannelRequest)
    }

    fun HandleRpcScriptReplyInbound(rpcScriptReplyInbound: RpcScriptReplyInbound) {
        DefaultMessageHandler(rpcScriptReplyInbound)
    }

    fun HandleRpcScriptRequestInbound(rpcScriptRequestInbound: RpcScriptRequestInbound) {
        DefaultMessageHandler(rpcScriptRequestInbound)
    }

    fun HandleRpcScriptRequestInboundForward(rpcScriptRequestInboundForward: RpcScriptRequestInboundForward) {
        DefaultMessageHandler(rpcScriptRequestInboundForward)
    }

    fun HandleSaveAssetIntoInventory(saveAssetIntoInventory: SaveAssetIntoInventory) {
        DefaultMessageHandler(saveAssetIntoInventory)
    }

    fun HandleScriptAnswerYes(scriptAnswerYes: ScriptAnswerYes) {
        DefaultMessageHandler(scriptAnswerYes)
    }

    fun HandleScriptControlChange(scriptControlChange: ScriptControlChange) {
        DefaultMessageHandler(scriptControlChange)
    }

    fun HandleScriptDataReply(scriptDataReply: ScriptDataReply) {
        DefaultMessageHandler(scriptDataReply)
    }

    fun HandleScriptDataRequest(scriptDataRequest: ScriptDataRequest) {
        DefaultMessageHandler(scriptDataRequest)
    }

    fun HandleScriptDialog(scriptDialog: ScriptDialog) {
        DefaultMessageHandler(scriptDialog)
    }

    fun HandleScriptDialogReply(scriptDialogReply: ScriptDialogReply) {
        DefaultMessageHandler(scriptDialogReply)
    }

    fun HandleScriptMailRegistration(scriptMailRegistration: ScriptMailRegistration) {
        DefaultMessageHandler(scriptMailRegistration)
    }

    fun HandleScriptQuestion(scriptQuestion: ScriptQuestion) {
        DefaultMessageHandler(scriptQuestion)
    }

    fun HandleScriptReset(scriptReset: ScriptReset) {
        DefaultMessageHandler(scriptReset)
    }

    fun HandleScriptRunningReply(scriptRunningReply: ScriptRunningReply) {
        DefaultMessageHandler(scriptRunningReply)
    }

    fun HandleScriptSensorReply(scriptSensorReply: ScriptSensorReply) {
        DefaultMessageHandler(scriptSensorReply)
    }

    fun HandleScriptSensorRequest(scriptSensorRequest: ScriptSensorRequest) {
        DefaultMessageHandler(scriptSensorRequest)
    }

    fun HandleScriptTeleportRequest(scriptTeleportRequest: ScriptTeleportRequest) {
        DefaultMessageHandler(scriptTeleportRequest)
    }

    fun HandleSendPostcard(sendPostcard: SendPostcard) {
        DefaultMessageHandler(sendPostcard)
    }

    fun HandleSendXferPacket(sendXferPacket: SendXferPacket) {
        DefaultMessageHandler(sendXferPacket)
    }

    fun HandleSetAlwaysRun(setAlwaysRun: SetAlwaysRun) {
        DefaultMessageHandler(setAlwaysRun)
    }

    fun HandleSetCPURatio(setCPURatio: SetCPURatio) {
        DefaultMessageHandler(setCPURatio)
    }

    fun HandleSetFollowCamProperties(setFollowCamProperties: SetFollowCamProperties) {
        DefaultMessageHandler(setFollowCamProperties)
    }

    fun HandleSetGroupAcceptNotices(setGroupAcceptNotices: SetGroupAcceptNotices) {
        DefaultMessageHandler(setGroupAcceptNotices)
    }

    fun HandleSetGroupContribution(setGroupContribution: SetGroupContribution) {
        DefaultMessageHandler(setGroupContribution)
    }

    fun HandleSetScriptRunning(setScriptRunning: SetScriptRunning) {
        DefaultMessageHandler(setScriptRunning)
    }

    fun HandleSetSimPresenceInDatabase(setSimPresenceInDatabase: SetSimPresenceInDatabase) {
        DefaultMessageHandler(setSimPresenceInDatabase)
    }

    fun HandleSetSimStatusInDatabase(setSimStatusInDatabase: SetSimStatusInDatabase) {
        DefaultMessageHandler(setSimStatusInDatabase)
    }

    fun HandleSetStartLocation(setStartLocation: SetStartLocation) {
        DefaultMessageHandler(setStartLocation)
    }

    fun HandleSetStartLocationRequest(setStartLocationRequest: SetStartLocationRequest) {
        DefaultMessageHandler(setStartLocationRequest)
    }

    fun HandleSimCrashed(simCrashed: SimCrashed) {
        DefaultMessageHandler(simCrashed)
    }

    fun HandleSimStats(simStats: SimStats) {
        DefaultMessageHandler(simStats)
    }

    fun HandleSimStatus(simStatus: SimStatus) {
        DefaultMessageHandler(simStatus)
    }

    fun HandleSimWideDeletes(simWideDeletes: SimWideDeletes) {
        DefaultMessageHandler(simWideDeletes)
    }

    fun HandleSimulatorLoad(simulatorLoad: SimulatorLoad) {
        DefaultMessageHandler(simulatorLoad)
    }

    fun HandleSimulatorMapUpdate(simulatorMapUpdate: SimulatorMapUpdate) {
        DefaultMessageHandler(simulatorMapUpdate)
    }

    fun HandleSimulatorPresentAtLocation(simulatorPresentAtLocation: SimulatorPresentAtLocation) {
        DefaultMessageHandler(simulatorPresentAtLocation)
    }

    fun HandleSimulatorReady(simulatorReady: SimulatorReady) {
        DefaultMessageHandler(simulatorReady)
    }

    fun HandleSimulatorSetMap(simulatorSetMap: SimulatorSetMap) {
        DefaultMessageHandler(simulatorSetMap)
    }

    fun HandleSimulatorShutdownRequest(simulatorShutdownRequest: SimulatorShutdownRequest) {
        DefaultMessageHandler(simulatorShutdownRequest)
    }

    fun HandleSimulatorViewerTimeMessage(simulatorViewerTimeMessage: SimulatorViewerTimeMessage) {
        DefaultMessageHandler(simulatorViewerTimeMessage)
    }

    fun HandleSoundTrigger(soundTrigger: SoundTrigger) {
        DefaultMessageHandler(soundTrigger)
    }

    fun HandleStartAuction(startAuction: StartAuction) {
        DefaultMessageHandler(startAuction)
    }

    fun HandleStartGroupProposal(startGroupProposal: StartGroupProposal) {
        DefaultMessageHandler(startGroupProposal)
    }

    fun HandleStartLure(startLure: StartLure) {
        DefaultMessageHandler(startLure)
    }

    fun HandleStartPingCheck(startPingCheck: StartPingCheck) {
        DefaultMessageHandler(startPingCheck)
    }

    fun HandleStateSave(stateSave: StateSave) {
        DefaultMessageHandler(stateSave)
    }

    fun HandleSubscribeLoad(subscribeLoad: SubscribeLoad) {
        DefaultMessageHandler(subscribeLoad)
    }

    fun HandleSystemKickUser(systemKickUser: SystemKickUser) {
        DefaultMessageHandler(systemKickUser)
    }

    fun HandleSystemMessage(systemMessage: SystemMessage) {
        DefaultMessageHandler(systemMessage)
    }

    fun HandleTallyVotes(tallyVotes: TallyVotes) {
        DefaultMessageHandler(tallyVotes)
    }

    fun HandleTelehubInfo(telehubInfo: TelehubInfo) {
        DefaultMessageHandler(telehubInfo)
    }

    fun HandleTeleportCancel(teleportCancel: TeleportCancel) {
        DefaultMessageHandler(teleportCancel)
    }

    fun HandleTeleportFailed(teleportFailed: TeleportFailed) {
        DefaultMessageHandler(teleportFailed)
    }

    fun HandleTeleportFinish(teleportFinish: TeleportFinish) {
        DefaultMessageHandler(teleportFinish)
    }

    fun HandleTeleportLandingStatusChanged(teleportLandingStatusChanged: TeleportLandingStatusChanged) {
        DefaultMessageHandler(teleportLandingStatusChanged)
    }

    fun HandleTeleportLandmarkRequest(teleportLandmarkRequest: TeleportLandmarkRequest) {
        DefaultMessageHandler(teleportLandmarkRequest)
    }

    fun HandleTeleportLocal(teleportLocal: TeleportLocal) {
        DefaultMessageHandler(teleportLocal)
    }

    fun HandleTeleportLocationRequest(teleportLocationRequest: TeleportLocationRequest) {
        DefaultMessageHandler(teleportLocationRequest)
    }

    fun HandleTeleportLureRequest(teleportLureRequest: TeleportLureRequest) {
        DefaultMessageHandler(teleportLureRequest)
    }

    fun HandleTeleportProgress(teleportProgress: TeleportProgress) {
        DefaultMessageHandler(teleportProgress)
    }

    fun HandleTeleportRequest(teleportRequest: TeleportRequest) {
        DefaultMessageHandler(teleportRequest)
    }

    fun HandleTeleportStart(teleportStart: TeleportStart) {
        DefaultMessageHandler(teleportStart)
    }

    fun HandleTerminateFriendship(terminateFriendship: TerminateFriendship) {
        DefaultMessageHandler(terminateFriendship)
    }

    fun HandleTestMessage(testMessage: TestMessage) {
        DefaultMessageHandler(testMessage)
    }

    fun HandleTrackAgent(trackAgent: TrackAgent) {
        DefaultMessageHandler(trackAgent)
    }

    fun HandleTransferAbort(transferAbort: TransferAbort) {
        DefaultMessageHandler(transferAbort)
    }

    fun HandleTransferInfo(transferInfo: TransferInfo) {
        DefaultMessageHandler(transferInfo)
    }

    fun HandleTransferInventory(transferInventory: TransferInventory) {
        DefaultMessageHandler(transferInventory)
    }

    fun HandleTransferInventoryAck(transferInventoryAck: TransferInventoryAck) {
        DefaultMessageHandler(transferInventoryAck)
    }

    fun HandleTransferPacket(transferPacket: TransferPacket) {
        DefaultMessageHandler(transferPacket)
    }

    fun HandleTransferRequest(transferRequest: TransferRequest) {
        DefaultMessageHandler(transferRequest)
    }

    fun HandleUUIDGroupNameReply(uUIDGroupNameReply: UUIDGroupNameReply) {
        DefaultMessageHandler(uUIDGroupNameReply)
    }

    fun HandleUUIDGroupNameRequest(uUIDGroupNameRequest: UUIDGroupNameRequest) {
        DefaultMessageHandler(uUIDGroupNameRequest)
    }

    fun HandleUUIDNameReply(uUIDNameReply: UUIDNameReply) {
        DefaultMessageHandler(uUIDNameReply)
    }

    fun HandleUUIDNameRequest(uUIDNameRequest: UUIDNameRequest) {
        DefaultMessageHandler(uUIDNameRequest)
    }

    fun HandleUndo(undo: Undo) {
        DefaultMessageHandler(undo)
    }

    fun HandleUndoLand(undoLand: UndoLand) {
        DefaultMessageHandler(undoLand)
    }

    fun HandleUnsubscribeLoad(unsubscribeLoad: UnsubscribeLoad) {
        DefaultMessageHandler(unsubscribeLoad)
    }

    fun HandleUpdateAttachment(updateAttachment: UpdateAttachment) {
        DefaultMessageHandler(updateAttachment)
    }

    fun HandleUpdateCreateInventoryItem(updateCreateInventoryItem: UpdateCreateInventoryItem) {
        DefaultMessageHandler(updateCreateInventoryItem)
    }

    fun HandleUpdateGroupInfo(updateGroupInfo: UpdateGroupInfo) {
        DefaultMessageHandler(updateGroupInfo)
    }

    fun HandleUpdateInventoryFolder(updateInventoryFolder: UpdateInventoryFolder) {
        DefaultMessageHandler(updateInventoryFolder)
    }

    fun HandleUpdateInventoryItem(updateInventoryItem: UpdateInventoryItem) {
        DefaultMessageHandler(updateInventoryItem)
    }

    fun HandleUpdateMuteListEntry(updateMuteListEntry: UpdateMuteListEntry) {
        DefaultMessageHandler(updateMuteListEntry)
    }

    fun HandleUpdateParcel(updateParcel: UpdateParcel) {
        DefaultMessageHandler(updateParcel)
    }

    fun HandleUpdateSimulator(updateSimulator: UpdateSimulator) {
        DefaultMessageHandler(updateSimulator)
    }

    fun HandleUpdateTaskInventory(updateTaskInventory: UpdateTaskInventory) {
        DefaultMessageHandler(updateTaskInventory)
    }

    fun HandleUpdateUserInfo(updateUserInfo: UpdateUserInfo) {
        DefaultMessageHandler(updateUserInfo)
    }

    fun HandleUseCachedMuteList(useCachedMuteList: UseCachedMuteList) {
        DefaultMessageHandler(useCachedMuteList)
    }

    fun HandleUseCircuitCode(useCircuitCode: UseCircuitCode) {
        DefaultMessageHandler(useCircuitCode)
    }

    fun HandleUserInfoReply(userInfoReply: UserInfoReply) {
        DefaultMessageHandler(userInfoReply)
    }

    fun HandleUserInfoRequest(userInfoRequest: UserInfoRequest) {
        DefaultMessageHandler(userInfoRequest)
    }

    fun HandleUserReport(userReport: UserReport) {
        DefaultMessageHandler(userReport)
    }

    fun HandleUserReportInternal(userReportInternal: UserReportInternal) {
        DefaultMessageHandler(userReportInternal)
    }

    fun HandleVelocityInterpolateOff(velocityInterpolateOff: VelocityInterpolateOff) {
        DefaultMessageHandler(velocityInterpolateOff)
    }

    fun HandleVelocityInterpolateOn(velocityInterpolateOn: VelocityInterpolateOn) {
        DefaultMessageHandler(velocityInterpolateOn)
    }

    fun HandleViewerEffect(viewerEffect: ViewerEffect) {
        DefaultMessageHandler(viewerEffect)
    }

    fun HandleViewerFrozenMessage(viewerFrozenMessage: ViewerFrozenMessage) {
        DefaultMessageHandler(viewerFrozenMessage)
    }

    fun HandleViewerStartAuction(viewerStartAuction: ViewerStartAuction) {
        DefaultMessageHandler(viewerStartAuction)
    }

    fun HandleViewerStats(viewerStats: ViewerStats) {
        DefaultMessageHandler(viewerStats)
    }
}
