/**
 * SLMessageHandler.kt
 *
 * Kotlin translation of the decompiled SLMessageHandler Java class from the
 * Lumiya Second Life viewer. This base class provides default handler methods
 * for all Second Life protocol messages. Subclasses can override individual
 * handlers to implement custom message processing.
 *
 * Original source: decompiled from classes.dex
 */
package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage

open class SLMessageHandler {

    open fun DefaultMessageHandler(sLMessage: SLMessage) {
    }

    open fun HandleAbortXfer(abortXfer: AbortXfer) {
        DefaultMessageHandler(abortXfer)
    }

    open fun HandleAcceptCallingCard(acceptCallingCard: AcceptCallingCard) {
        DefaultMessageHandler(acceptCallingCard)
    }

    open fun HandleAcceptFriendship(acceptFriendship: AcceptFriendship) {
        DefaultMessageHandler(acceptFriendship)
    }

    open fun HandleActivateGestures(activateGestures: ActivateGestures) {
        DefaultMessageHandler(activateGestures)
    }

    open fun HandleActivateGroup(activateGroup: ActivateGroup) {
        DefaultMessageHandler(activateGroup)
    }

    open fun HandleAddCircuitCode(addCircuitCode: AddCircuitCode) {
        DefaultMessageHandler(addCircuitCode)
    }

    open fun HandleAgentAlertMessage(agentAlertMessage: AgentAlertMessage) {
        DefaultMessageHandler(agentAlertMessage)
    }

    open fun HandleAgentAnimation(agentAnimation: AgentAnimation) {
        DefaultMessageHandler(agentAnimation)
    }

    open fun HandleAgentCachedTexture(agentCachedTexture: AgentCachedTexture) {
        DefaultMessageHandler(agentCachedTexture)
    }

    open fun HandleAgentCachedTextureResponse(agentCachedTextureResponse: AgentCachedTextureResponse) {
        DefaultMessageHandler(agentCachedTextureResponse)
    }

    open fun HandleAgentDataUpdate(agentDataUpdate: AgentDataUpdate) {
        DefaultMessageHandler(agentDataUpdate)
    }

    open fun HandleAgentDataUpdateRequest(agentDataUpdateRequest: AgentDataUpdateRequest) {
        DefaultMessageHandler(agentDataUpdateRequest)
    }

    open fun HandleAgentDropGroup(agentDropGroup: AgentDropGroup) {
        DefaultMessageHandler(agentDropGroup)
    }

    open fun HandleAgentFOV(agentFOV: AgentFOV) {
        DefaultMessageHandler(agentFOV)
    }

    open fun HandleAgentGroupDataUpdate(agentGroupDataUpdate: AgentGroupDataUpdate) {
        DefaultMessageHandler(agentGroupDataUpdate)
    }

    open fun HandleAgentHeightWidth(agentHeightWidth: AgentHeightWidth) {
        DefaultMessageHandler(agentHeightWidth)
    }

    open fun HandleAgentIsNowWearing(agentIsNowWearing: AgentIsNowWearing) {
        DefaultMessageHandler(agentIsNowWearing)
    }

    open fun HandleAgentMovementComplete(agentMovementComplete: AgentMovementComplete) {
        DefaultMessageHandler(agentMovementComplete)
    }

    open fun HandleAgentPause(agentPause: AgentPause) {
        DefaultMessageHandler(agentPause)
    }

    open fun HandleAgentQuitCopy(agentQuitCopy: AgentQuitCopy) {
        DefaultMessageHandler(agentQuitCopy)
    }

    open fun HandleAgentRequestSit(agentRequestSit: AgentRequestSit) {
        DefaultMessageHandler(agentRequestSit)
    }

    open fun HandleAgentResume(agentResume: AgentResume) {
        DefaultMessageHandler(agentResume)
    }

    open fun HandleAgentSetAppearance(agentSetAppearance: AgentSetAppearance) {
        DefaultMessageHandler(agentSetAppearance)
    }

    open fun HandleAgentSit(agentSit: AgentSit) {
        DefaultMessageHandler(agentSit)
    }

    open fun HandleAgentThrottle(agentThrottle: AgentThrottle) {
        DefaultMessageHandler(agentThrottle)
    }

    open fun HandleAgentUpdate(agentUpdate: AgentUpdate) {
        DefaultMessageHandler(agentUpdate)
    }

    open fun HandleAgentWearablesRequest(agentWearablesRequest: AgentWearablesRequest) {
        DefaultMessageHandler(agentWearablesRequest)
    }

    open fun HandleAgentWearablesUpdate(agentWearablesUpdate: AgentWearablesUpdate) {
        DefaultMessageHandler(agentWearablesUpdate)
    }

    open fun HandleAlertMessage(alertMessage: AlertMessage) {
        DefaultMessageHandler(alertMessage)
    }

    open fun HandleAssetUploadComplete(assetUploadComplete: AssetUploadComplete) {
        DefaultMessageHandler(assetUploadComplete)
    }

    open fun HandleAssetUploadRequest(assetUploadRequest: AssetUploadRequest) {
        DefaultMessageHandler(assetUploadRequest)
    }

    open fun HandleAtomicPassObject(atomicPassObject: AtomicPassObject) {
        DefaultMessageHandler(atomicPassObject)
    }

    open fun HandleAttachedSound(attachedSound: AttachedSound) {
        DefaultMessageHandler(attachedSound)
    }

    open fun HandleAttachedSoundGainChange(attachedSoundGainChange: AttachedSoundGainChange) {
        DefaultMessageHandler(attachedSoundGainChange)
    }

    open fun HandleAvatarAnimation(avatarAnimation: AvatarAnimation) {
        DefaultMessageHandler(avatarAnimation)
    }

    open fun HandleAvatarAppearance(avatarAppearance: AvatarAppearance) {
        DefaultMessageHandler(avatarAppearance)
    }

    open fun HandleAvatarClassifiedReply(avatarClassifiedReply: AvatarClassifiedReply) {
        DefaultMessageHandler(avatarClassifiedReply)
    }

    open fun HandleAvatarGroupsReply(avatarGroupsReply: AvatarGroupsReply) {
        DefaultMessageHandler(avatarGroupsReply)
    }

    open fun HandleAvatarInterestsReply(avatarInterestsReply: AvatarInterestsReply) {
        DefaultMessageHandler(avatarInterestsReply)
    }

    open fun HandleAvatarInterestsUpdate(avatarInterestsUpdate: AvatarInterestsUpdate) {
        DefaultMessageHandler(avatarInterestsUpdate)
    }

    open fun HandleAvatarNotesReply(avatarNotesReply: AvatarNotesReply) {
        DefaultMessageHandler(avatarNotesReply)
    }

    open fun HandleAvatarNotesUpdate(avatarNotesUpdate: AvatarNotesUpdate) {
        DefaultMessageHandler(avatarNotesUpdate)
    }

    open fun HandleAvatarPickerReply(avatarPickerReply: AvatarPickerReply) {
        DefaultMessageHandler(avatarPickerReply)
    }

    open fun HandleAvatarPickerRequest(avatarPickerRequest: AvatarPickerRequest) {
        DefaultMessageHandler(avatarPickerRequest)
    }

    open fun HandleAvatarPickerRequestBackend(avatarPickerRequestBackend: AvatarPickerRequestBackend) {
        DefaultMessageHandler(avatarPickerRequestBackend)
    }

    open fun HandleAvatarPicksReply(avatarPicksReply: AvatarPicksReply) {
        DefaultMessageHandler(avatarPicksReply)
    }

    open fun HandleAvatarPropertiesReply(avatarPropertiesReply: AvatarPropertiesReply) {
        DefaultMessageHandler(avatarPropertiesReply)
    }

    open fun HandleAvatarPropertiesRequest(avatarPropertiesRequest: AvatarPropertiesRequest) {
        DefaultMessageHandler(avatarPropertiesRequest)
    }

    open fun HandleAvatarPropertiesRequestBackend(avatarPropertiesRequestBackend: AvatarPropertiesRequestBackend) {
        DefaultMessageHandler(avatarPropertiesRequestBackend)
    }

    open fun HandleAvatarPropertiesUpdate(avatarPropertiesUpdate: AvatarPropertiesUpdate) {
        DefaultMessageHandler(avatarPropertiesUpdate)
    }

    open fun HandleAvatarSitResponse(avatarSitResponse: AvatarSitResponse) {
        DefaultMessageHandler(avatarSitResponse)
    }

    open fun HandleAvatarTextureUpdate(avatarTextureUpdate: AvatarTextureUpdate) {
        DefaultMessageHandler(avatarTextureUpdate)
    }

    open fun HandleBulkUpdateInventory(bulkUpdateInventory: BulkUpdateInventory) {
        DefaultMessageHandler(bulkUpdateInventory)
    }

    open fun HandleBuyObjectInventory(buyObjectInventory: BuyObjectInventory) {
        DefaultMessageHandler(buyObjectInventory)
    }

    open fun HandleCameraConstraint(cameraConstraint: CameraConstraint) {
        DefaultMessageHandler(cameraConstraint)
    }

    open fun HandleCancelAuction(cancelAuction: CancelAuction) {
        DefaultMessageHandler(cancelAuction)
    }

    open fun HandleChangeInventoryItemFlags(changeInventoryItemFlags: ChangeInventoryItemFlags) {
        DefaultMessageHandler(changeInventoryItemFlags)
    }

    open fun HandleChangeUserRights(changeUserRights: ChangeUserRights) {
        DefaultMessageHandler(changeUserRights)
    }

    open fun HandleChatFromSimulator(chatFromSimulator: ChatFromSimulator) {
        DefaultMessageHandler(chatFromSimulator)
    }

    open fun HandleChatFromViewer(chatFromViewer: ChatFromViewer) {
        DefaultMessageHandler(chatFromViewer)
    }

    open fun HandleChatPass(chatPass: ChatPass) {
        DefaultMessageHandler(chatPass)
    }

    open fun HandleCheckParcelAuctions(checkParcelAuctions: CheckParcelAuctions) {
        DefaultMessageHandler(checkParcelAuctions)
    }

    open fun HandleCheckParcelSales(checkParcelSales: CheckParcelSales) {
        DefaultMessageHandler(checkParcelSales)
    }

    open fun HandleChildAgentAlive(childAgentAlive: ChildAgentAlive) {
        DefaultMessageHandler(childAgentAlive)
    }

    open fun HandleChildAgentDying(childAgentDying: ChildAgentDying) {
        DefaultMessageHandler(childAgentDying)
    }

    open fun HandleChildAgentPositionUpdate(childAgentPositionUpdate: ChildAgentPositionUpdate) {
        DefaultMessageHandler(childAgentPositionUpdate)
    }

    open fun HandleChildAgentUnknown(childAgentUnknown: ChildAgentUnknown) {
        DefaultMessageHandler(childAgentUnknown)
    }

    open fun HandleChildAgentUpdate(childAgentUpdate: ChildAgentUpdate) {
        DefaultMessageHandler(childAgentUpdate)
    }

    open fun HandleClassifiedDelete(classifiedDelete: ClassifiedDelete) {
        DefaultMessageHandler(classifiedDelete)
    }

    open fun HandleClassifiedGodDelete(classifiedGodDelete: ClassifiedGodDelete) {
        DefaultMessageHandler(classifiedGodDelete)
    }

    open fun HandleClassifiedInfoReply(classifiedInfoReply: ClassifiedInfoReply) {
        DefaultMessageHandler(classifiedInfoReply)
    }

    open fun HandleClassifiedInfoRequest(classifiedInfoRequest: ClassifiedInfoRequest) {
        DefaultMessageHandler(classifiedInfoRequest)
    }

    open fun HandleClassifiedInfoUpdate(classifiedInfoUpdate: ClassifiedInfoUpdate) {
        DefaultMessageHandler(classifiedInfoUpdate)
    }

    open fun HandleClearFollowCamProperties(clearFollowCamProperties: ClearFollowCamProperties) {
        DefaultMessageHandler(clearFollowCamProperties)
    }

    open fun HandleCloseCircuit(closeCircuit: CloseCircuit) {
        DefaultMessageHandler(closeCircuit)
    }

    open fun HandleCoarseLocationUpdate(coarseLocationUpdate: CoarseLocationUpdate) {
        DefaultMessageHandler(coarseLocationUpdate)
    }

    open fun HandleCompleteAgentMovement(completeAgentMovement: CompleteAgentMovement) {
        DefaultMessageHandler(completeAgentMovement)
    }

    open fun HandleCompleteAuction(completeAuction: CompleteAuction) {
        DefaultMessageHandler(completeAuction)
    }

    open fun HandleCompletePingCheck(completePingCheck: CompletePingCheck) {
        DefaultMessageHandler(completePingCheck)
    }

    open fun HandleConfirmAuctionStart(confirmAuctionStart: ConfirmAuctionStart) {
        DefaultMessageHandler(confirmAuctionStart)
    }

    open fun HandleConfirmEnableSimulator(confirmEnableSimulator: ConfirmEnableSimulator) {
        DefaultMessageHandler(confirmEnableSimulator)
    }

    open fun HandleConfirmXferPacket(confirmXferPacket: ConfirmXferPacket) {
        DefaultMessageHandler(confirmXferPacket)
    }

    open fun HandleCopyInventoryFromNotecard(copyInventoryFromNotecard: CopyInventoryFromNotecard) {
        DefaultMessageHandler(copyInventoryFromNotecard)
    }

    open fun HandleCopyInventoryItem(copyInventoryItem: CopyInventoryItem) {
        DefaultMessageHandler(copyInventoryItem)
    }

    open fun HandleCreateGroupReply(createGroupReply: CreateGroupReply) {
        DefaultMessageHandler(createGroupReply)
    }

    open fun HandleCreateGroupRequest(createGroupRequest: CreateGroupRequest) {
        DefaultMessageHandler(createGroupRequest)
    }

    open fun HandleCreateInventoryFolder(createInventoryFolder: CreateInventoryFolder) {
        DefaultMessageHandler(createInventoryFolder)
    }

    open fun HandleCreateInventoryItem(createInventoryItem: CreateInventoryItem) {
        DefaultMessageHandler(createInventoryItem)
    }

    open fun HandleCreateLandmarkForEvent(createLandmarkForEvent: CreateLandmarkForEvent) {
        DefaultMessageHandler(createLandmarkForEvent)
    }

    open fun HandleCreateNewOutfitAttachments(createNewOutfitAttachments: CreateNewOutfitAttachments) {
        DefaultMessageHandler(createNewOutfitAttachments)
    }

    open fun HandleCreateTrustedCircuit(createTrustedCircuit: CreateTrustedCircuit) {
        DefaultMessageHandler(createTrustedCircuit)
    }

    open fun HandleCrossedRegion(crossedRegion: CrossedRegion) {
        DefaultMessageHandler(crossedRegion)
    }

    open fun HandleDataHomeLocationReply(dataHomeLocationReply: DataHomeLocationReply) {
        DefaultMessageHandler(dataHomeLocationReply)
    }

    open fun HandleDataHomeLocationRequest(dataHomeLocationRequest: DataHomeLocationRequest) {
        DefaultMessageHandler(dataHomeLocationRequest)
    }

    open fun HandleDataServerLogout(dataServerLogout: DataServerLogout) {
        DefaultMessageHandler(dataServerLogout)
    }

    open fun HandleDeRezAck(deRezAck: DeRezAck) {
        DefaultMessageHandler(deRezAck)
    }

    open fun HandleDeRezObject(deRezObject: DeRezObject) {
        DefaultMessageHandler(deRezObject)
    }

    open fun HandleDeactivateGestures(deactivateGestures: DeactivateGestures) {
        DefaultMessageHandler(deactivateGestures)
    }

    open fun HandleDeclineCallingCard(declineCallingCard: DeclineCallingCard) {
        DefaultMessageHandler(declineCallingCard)
    }

    open fun HandleDeclineFriendship(declineFriendship: DeclineFriendship) {
        DefaultMessageHandler(declineFriendship)
    }

    open fun HandleDenyTrustedCircuit(denyTrustedCircuit: DenyTrustedCircuit) {
        DefaultMessageHandler(denyTrustedCircuit)
    }

    open fun HandleDerezContainer(derezContainer: DerezContainer) {
        DefaultMessageHandler(derezContainer)
    }

    open fun HandleDetachAttachmentIntoInv(detachAttachmentIntoInv: DetachAttachmentIntoInv) {
        DefaultMessageHandler(detachAttachmentIntoInv)
    }

    open fun HandleDirClassifiedQuery(dirClassifiedQuery: DirClassifiedQuery) {
        DefaultMessageHandler(dirClassifiedQuery)
    }

    open fun HandleDirClassifiedQueryBackend(dirClassifiedQueryBackend: DirClassifiedQueryBackend) {
        DefaultMessageHandler(dirClassifiedQueryBackend)
    }

    open fun HandleDirClassifiedReply(dirClassifiedReply: DirClassifiedReply) {
        DefaultMessageHandler(dirClassifiedReply)
    }

    open fun HandleDirEventsReply(dirEventsReply: DirEventsReply) {
        DefaultMessageHandler(dirEventsReply)
    }

    open fun HandleDirFindQuery(dirFindQuery: DirFindQuery) {
        DefaultMessageHandler(dirFindQuery)
    }

    open fun HandleDirFindQueryBackend(dirFindQueryBackend: DirFindQueryBackend) {
        DefaultMessageHandler(dirFindQueryBackend)
    }

    open fun HandleDirGroupsReply(dirGroupsReply: DirGroupsReply) {
        DefaultMessageHandler(dirGroupsReply)
    }

    open fun HandleDirLandQuery(dirLandQuery: DirLandQuery) {
        DefaultMessageHandler(dirLandQuery)
    }

    open fun HandleDirLandQueryBackend(dirLandQueryBackend: DirLandQueryBackend) {
        DefaultMessageHandler(dirLandQueryBackend)
    }

    open fun HandleDirLandReply(dirLandReply: DirLandReply) {
        DefaultMessageHandler(dirLandReply)
    }

    open fun HandleDirPeopleReply(dirPeopleReply: DirPeopleReply) {
        DefaultMessageHandler(dirPeopleReply)
    }

    open fun HandleDirPlacesQuery(dirPlacesQuery: DirPlacesQuery) {
        DefaultMessageHandler(dirPlacesQuery)
    }

    open fun HandleDirPlacesQueryBackend(dirPlacesQueryBackend: DirPlacesQueryBackend) {
        DefaultMessageHandler(dirPlacesQueryBackend)
    }

    open fun HandleDirPlacesReply(dirPlacesReply: DirPlacesReply) {
        DefaultMessageHandler(dirPlacesReply)
    }

    open fun HandleDirPopularQuery(dirPopularQuery: DirPopularQuery) {
        DefaultMessageHandler(dirPopularQuery)
    }

    open fun HandleDirPopularQueryBackend(dirPopularQueryBackend: DirPopularQueryBackend) {
        DefaultMessageHandler(dirPopularQueryBackend)
    }

    open fun HandleDirPopularReply(dirPopularReply: DirPopularReply) {
        DefaultMessageHandler(dirPopularReply)
    }

    open fun HandleDisableSimulator(disableSimulator: DisableSimulator) {
        DefaultMessageHandler(disableSimulator)
    }

    open fun HandleEconomyData(economyData: EconomyData) {
        DefaultMessageHandler(economyData)
    }

    open fun HandleEconomyDataRequest(economyDataRequest: EconomyDataRequest) {
        DefaultMessageHandler(economyDataRequest)
    }

    open fun HandleEdgeDataPacket(edgeDataPacket: EdgeDataPacket) {
        DefaultMessageHandler(edgeDataPacket)
    }

    open fun HandleEjectGroupMemberReply(ejectGroupMemberReply: EjectGroupMemberReply) {
        DefaultMessageHandler(ejectGroupMemberReply)
    }

    open fun HandleEjectGroupMemberRequest(ejectGroupMemberRequest: EjectGroupMemberRequest) {
        DefaultMessageHandler(ejectGroupMemberRequest)
    }

    open fun HandleEjectUser(ejectUser: EjectUser) {
        DefaultMessageHandler(ejectUser)
    }

    open fun HandleEmailMessageReply(emailMessageReply: EmailMessageReply) {
        DefaultMessageHandler(emailMessageReply)
    }

    open fun HandleEmailMessageRequest(emailMessageRequest: EmailMessageRequest) {
        DefaultMessageHandler(emailMessageRequest)
    }

    open fun HandleEnableSimulator(enableSimulator: EnableSimulator) {
        DefaultMessageHandler(enableSimulator)
    }

    open fun HandleError(error: Error) {
        DefaultMessageHandler(error)
    }

    open fun HandleEstateCovenantReply(estateCovenantReply: EstateCovenantReply) {
        DefaultMessageHandler(estateCovenantReply)
    }

    open fun HandleEstateCovenantRequest(estateCovenantRequest: EstateCovenantRequest) {
        DefaultMessageHandler(estateCovenantRequest)
    }

    open fun HandleEstateOwnerMessage(estateOwnerMessage: EstateOwnerMessage) {
        DefaultMessageHandler(estateOwnerMessage)
    }

    open fun HandleEventGodDelete(eventGodDelete: EventGodDelete) {
        DefaultMessageHandler(eventGodDelete)
    }

    open fun HandleEventInfoReply(eventInfoReply: EventInfoReply) {
        DefaultMessageHandler(eventInfoReply)
    }

    open fun HandleEventInfoRequest(eventInfoRequest: EventInfoRequest) {
        DefaultMessageHandler(eventInfoRequest)
    }

    open fun HandleEventLocationReply(eventLocationReply: EventLocationReply) {
        DefaultMessageHandler(eventLocationReply)
    }

    open fun HandleEventLocationRequest(eventLocationRequest: EventLocationRequest) {
        DefaultMessageHandler(eventLocationRequest)
    }

    open fun HandleEventNotificationAddRequest(eventNotificationAddRequest: EventNotificationAddRequest) {
        DefaultMessageHandler(eventNotificationAddRequest)
    }

    open fun HandleEventNotificationRemoveRequest(eventNotificationRemoveRequest: EventNotificationRemoveRequest) {
        DefaultMessageHandler(eventNotificationRemoveRequest)
    }

    open fun HandleFeatureDisabled(featureDisabled: FeatureDisabled) {
        DefaultMessageHandler(featureDisabled)
    }

    open fun HandleFetchInventory(fetchInventory: FetchInventory) {
        DefaultMessageHandler(fetchInventory)
    }

    open fun HandleFetchInventoryDescendents(fetchInventoryDescendents: FetchInventoryDescendents) {
        DefaultMessageHandler(fetchInventoryDescendents)
    }

    open fun HandleFetchInventoryReply(fetchInventoryReply: FetchInventoryReply) {
        DefaultMessageHandler(fetchInventoryReply)
    }

    open fun HandleFindAgent(findAgent: FindAgent) {
        DefaultMessageHandler(findAgent)
    }

    open fun HandleForceObjectSelect(forceObjectSelect: ForceObjectSelect) {
        DefaultMessageHandler(forceObjectSelect)
    }

    open fun HandleForceScriptControlRelease(forceScriptControlRelease: ForceScriptControlRelease) {
        DefaultMessageHandler(forceScriptControlRelease)
    }

    open fun HandleFormFriendship(formFriendship: FormFriendship) {
        DefaultMessageHandler(formFriendship)
    }

    open fun HandleFreezeUser(freezeUser: FreezeUser) {
        DefaultMessageHandler(freezeUser)
    }

    open fun HandleGenericMessage(genericMessage: GenericMessage) {
        DefaultMessageHandler(genericMessage)
    }

    open fun HandleGetScriptRunning(getScriptRunning: GetScriptRunning) {
        DefaultMessageHandler(getScriptRunning)
    }

    open fun HandleGodKickUser(godKickUser: GodKickUser) {
        DefaultMessageHandler(godKickUser)
    }

    open fun HandleGodUpdateRegionInfo(godUpdateRegionInfo: GodUpdateRegionInfo) {
        DefaultMessageHandler(godUpdateRegionInfo)
    }

    open fun HandleGodlikeMessage(godlikeMessage: GodlikeMessage) {
        DefaultMessageHandler(godlikeMessage)
    }

    open fun HandleGrantGodlikePowers(grantGodlikePowers: GrantGodlikePowers) {
        DefaultMessageHandler(grantGodlikePowers)
    }

    open fun HandleGrantUserRights(grantUserRights: GrantUserRights) {
        DefaultMessageHandler(grantUserRights)
    }

    open fun HandleGroupAccountDetailsReply(groupAccountDetailsReply: GroupAccountDetailsReply) {
        DefaultMessageHandler(groupAccountDetailsReply)
    }

    open fun HandleGroupAccountDetailsRequest(groupAccountDetailsRequest: GroupAccountDetailsRequest) {
        DefaultMessageHandler(groupAccountDetailsRequest)
    }

    open fun HandleGroupAccountSummaryReply(groupAccountSummaryReply: GroupAccountSummaryReply) {
        DefaultMessageHandler(groupAccountSummaryReply)
    }

    open fun HandleGroupAccountSummaryRequest(groupAccountSummaryRequest: GroupAccountSummaryRequest) {
        DefaultMessageHandler(groupAccountSummaryRequest)
    }

    open fun HandleGroupAccountTransactionsReply(groupAccountTransactionsReply: GroupAccountTransactionsReply) {
        DefaultMessageHandler(groupAccountTransactionsReply)
    }

    open fun HandleGroupAccountTransactionsRequest(groupAccountTransactionsRequest: GroupAccountTransactionsRequest) {
        DefaultMessageHandler(groupAccountTransactionsRequest)
    }

    open fun HandleGroupActiveProposalItemReply(groupActiveProposalItemReply: GroupActiveProposalItemReply) {
        DefaultMessageHandler(groupActiveProposalItemReply)
    }

    open fun HandleGroupActiveProposalsRequest(groupActiveProposalsRequest: GroupActiveProposalsRequest) {
        DefaultMessageHandler(groupActiveProposalsRequest)
    }

    open fun HandleGroupDataUpdate(groupDataUpdate: GroupDataUpdate) {
        DefaultMessageHandler(groupDataUpdate)
    }

    open fun HandleGroupMembersReply(groupMembersReply: GroupMembersReply) {
        DefaultMessageHandler(groupMembersReply)
    }

    open fun HandleGroupMembersRequest(groupMembersRequest: GroupMembersRequest) {
        DefaultMessageHandler(groupMembersRequest)
    }

    open fun HandleGroupNoticeAdd(groupNoticeAdd: GroupNoticeAdd) {
        DefaultMessageHandler(groupNoticeAdd)
    }

    open fun HandleGroupNoticeRequest(groupNoticeRequest: GroupNoticeRequest) {
        DefaultMessageHandler(groupNoticeRequest)
    }

    open fun HandleGroupNoticesListReply(groupNoticesListReply: GroupNoticesListReply) {
        DefaultMessageHandler(groupNoticesListReply)
    }

    open fun HandleGroupNoticesListRequest(groupNoticesListRequest: GroupNoticesListRequest) {
        DefaultMessageHandler(groupNoticesListRequest)
    }

    open fun HandleGroupProfileReply(groupProfileReply: GroupProfileReply) {
        DefaultMessageHandler(groupProfileReply)
    }

    open fun HandleGroupProfileRequest(groupProfileRequest: GroupProfileRequest) {
        DefaultMessageHandler(groupProfileRequest)
    }

    open fun HandleGroupProposalBallot(groupProposalBallot: GroupProposalBallot) {
        DefaultMessageHandler(groupProposalBallot)
    }

    open fun HandleGroupRoleChanges(groupRoleChanges: GroupRoleChanges) {
        DefaultMessageHandler(groupRoleChanges)
    }

    open fun HandleGroupRoleDataReply(groupRoleDataReply: GroupRoleDataReply) {
        DefaultMessageHandler(groupRoleDataReply)
    }

    open fun HandleGroupRoleDataRequest(groupRoleDataRequest: GroupRoleDataRequest) {
        DefaultMessageHandler(groupRoleDataRequest)
    }

    open fun HandleGroupRoleMembersReply(groupRoleMembersReply: GroupRoleMembersReply) {
        DefaultMessageHandler(groupRoleMembersReply)
    }

    open fun HandleGroupRoleMembersRequest(groupRoleMembersRequest: GroupRoleMembersRequest) {
        DefaultMessageHandler(groupRoleMembersRequest)
    }

    open fun HandleGroupRoleUpdate(groupRoleUpdate: GroupRoleUpdate) {
        DefaultMessageHandler(groupRoleUpdate)
    }

    open fun HandleGroupTitleUpdate(groupTitleUpdate: GroupTitleUpdate) {
        DefaultMessageHandler(groupTitleUpdate)
    }

    open fun HandleGroupTitlesReply(groupTitlesReply: GroupTitlesReply) {
        DefaultMessageHandler(groupTitlesReply)
    }

    open fun HandleGroupTitlesRequest(groupTitlesRequest: GroupTitlesRequest) {
        DefaultMessageHandler(groupTitlesRequest)
    }

    open fun HandleGroupVoteHistoryItemReply(groupVoteHistoryItemReply: GroupVoteHistoryItemReply) {
        DefaultMessageHandler(groupVoteHistoryItemReply)
    }

    open fun HandleGroupVoteHistoryRequest(groupVoteHistoryRequest: GroupVoteHistoryRequest) {
        DefaultMessageHandler(groupVoteHistoryRequest)
    }

    open fun HandleHealthMessage(healthMessage: HealthMessage) {
        DefaultMessageHandler(healthMessage)
    }

    open fun HandleImageData(imageData: ImageData) {
        DefaultMessageHandler(imageData)
    }

    open fun HandleImageNotInDatabase(imageNotInDatabase: ImageNotInDatabase) {
        DefaultMessageHandler(imageNotInDatabase)
    }

    open fun HandleImagePacket(imagePacket: ImagePacket) {
        DefaultMessageHandler(imagePacket)
    }

    open fun HandleImprovedInstantMessage(improvedInstantMessage: ImprovedInstantMessage) {
        DefaultMessageHandler(improvedInstantMessage)
    }

    open fun HandleImprovedTerseObjectUpdate(improvedTerseObjectUpdate: ImprovedTerseObjectUpdate) {
        DefaultMessageHandler(improvedTerseObjectUpdate)
    }

    open fun HandleInitiateDownload(initiateDownload: InitiateDownload) {
        DefaultMessageHandler(initiateDownload)
    }

    open fun HandleInternalScriptMail(internalScriptMail: InternalScriptMail) {
        DefaultMessageHandler(internalScriptMail)
    }

    open fun HandleInventoryAssetResponse(inventoryAssetResponse: InventoryAssetResponse) {
        DefaultMessageHandler(inventoryAssetResponse)
    }

    open fun HandleInventoryDescendents(inventoryDescendents: InventoryDescendents) {
        DefaultMessageHandler(inventoryDescendents)
    }

    open fun HandleInviteGroupRequest(inviteGroupRequest: InviteGroupRequest) {
        DefaultMessageHandler(inviteGroupRequest)
    }

    open fun HandleInviteGroupResponse(inviteGroupResponse: InviteGroupResponse) {
        DefaultMessageHandler(inviteGroupResponse)
    }

    open fun HandleJoinGroupReply(joinGroupReply: JoinGroupReply) {
        DefaultMessageHandler(joinGroupReply)
    }

    open fun HandleJoinGroupRequest(joinGroupRequest: JoinGroupRequest) {
        DefaultMessageHandler(joinGroupRequest)
    }

    open fun HandleKickUser(kickUser: KickUser) {
        DefaultMessageHandler(kickUser)
    }

    open fun HandleKickUserAck(kickUserAck: KickUserAck) {
        DefaultMessageHandler(kickUserAck)
    }

    open fun HandleKillChildAgents(killChildAgents: KillChildAgents) {
        DefaultMessageHandler(killChildAgents)
    }

    open fun HandleKillObject(killObject: KillObject) {
        DefaultMessageHandler(killObject)
    }

    open fun HandleLandStatReply(landStatReply: LandStatReply) {
        DefaultMessageHandler(landStatReply)
    }

    open fun HandleLandStatRequest(landStatRequest: LandStatRequest) {
        DefaultMessageHandler(landStatRequest)
    }

    open fun HandleLayerData(layerData: LayerData) {
        DefaultMessageHandler(layerData)
    }

    open fun HandleLeaveGroupReply(leaveGroupReply: LeaveGroupReply) {
        DefaultMessageHandler(leaveGroupReply)
    }

    open fun HandleLeaveGroupRequest(leaveGroupRequest: LeaveGroupRequest) {
        DefaultMessageHandler(leaveGroupRequest)
    }

    open fun HandleLinkInventoryItem(linkInventoryItem: LinkInventoryItem) {
        DefaultMessageHandler(linkInventoryItem)
    }

    open fun HandleLiveHelpGroupReply(liveHelpGroupReply: LiveHelpGroupReply) {
        DefaultMessageHandler(liveHelpGroupReply)
    }

    open fun HandleLiveHelpGroupRequest(liveHelpGroupRequest: LiveHelpGroupRequest) {
        DefaultMessageHandler(liveHelpGroupRequest)
    }

    open fun HandleLoadURL(loadURL: LoadURL) {
        DefaultMessageHandler(loadURL)
    }

    open fun HandleLogDwellTime(logDwellTime: LogDwellTime) {
        DefaultMessageHandler(logDwellTime)
    }

    open fun HandleLogFailedMoneyTransaction(logFailedMoneyTransaction: LogFailedMoneyTransaction) {
        DefaultMessageHandler(logFailedMoneyTransaction)
    }

    open fun HandleLogParcelChanges(logParcelChanges: LogParcelChanges) {
        DefaultMessageHandler(logParcelChanges)
    }

    open fun HandleLogTextMessage(logTextMessage: LogTextMessage) {
        DefaultMessageHandler(logTextMessage)
    }

    open fun HandleLogoutReply(logoutReply: LogoutReply) {
        DefaultMessageHandler(logoutReply)
    }

    open fun HandleLogoutRequest(logoutRequest: LogoutRequest) {
        DefaultMessageHandler(logoutRequest)
    }

    open fun HandleMapBlockReply(mapBlockReply: MapBlockReply) {
        DefaultMessageHandler(mapBlockReply)
    }

    open fun HandleMapBlockRequest(mapBlockRequest: MapBlockRequest) {
        DefaultMessageHandler(mapBlockRequest)
    }

    open fun HandleMapItemReply(mapItemReply: MapItemReply) {
        DefaultMessageHandler(mapItemReply)
    }

    open fun HandleMapItemRequest(mapItemRequest: MapItemRequest) {
        DefaultMessageHandler(mapItemRequest)
    }

    open fun HandleMapLayerReply(mapLayerReply: MapLayerReply) {
        DefaultMessageHandler(mapLayerReply)
    }

    open fun HandleMapLayerRequest(mapLayerRequest: MapLayerRequest) {
        DefaultMessageHandler(mapLayerRequest)
    }

    open fun HandleMapNameRequest(mapNameRequest: MapNameRequest) {
        DefaultMessageHandler(mapNameRequest)
    }

    open fun HandleMeanCollisionAlert(meanCollisionAlert: MeanCollisionAlert) {
        DefaultMessageHandler(meanCollisionAlert)
    }

    open fun HandleMergeParcel(mergeParcel: MergeParcel) {
        DefaultMessageHandler(mergeParcel)
    }

    open fun HandleModifyLand(modifyLand: ModifyLand) {
        DefaultMessageHandler(modifyLand)
    }

    open fun HandleMoneyBalanceReply(moneyBalanceReply: MoneyBalanceReply) {
        DefaultMessageHandler(moneyBalanceReply)
    }

    open fun HandleMoneyBalanceRequest(moneyBalanceRequest: MoneyBalanceRequest) {
        DefaultMessageHandler(moneyBalanceRequest)
    }

    open fun HandleMoneyTransferBackend(moneyTransferBackend: MoneyTransferBackend) {
        DefaultMessageHandler(moneyTransferBackend)
    }

    open fun HandleMoneyTransferRequest(moneyTransferRequest: MoneyTransferRequest) {
        DefaultMessageHandler(moneyTransferRequest)
    }

    open fun HandleMoveInventoryFolder(moveInventoryFolder: MoveInventoryFolder) {
        DefaultMessageHandler(moveInventoryFolder)
    }

    open fun HandleMoveInventoryItem(moveInventoryItem: MoveInventoryItem) {
        DefaultMessageHandler(moveInventoryItem)
    }

    open fun HandleMoveTaskInventory(moveTaskInventory: MoveTaskInventory) {
        DefaultMessageHandler(moveTaskInventory)
    }

    open fun HandleMultipleObjectUpdate(multipleObjectUpdate: MultipleObjectUpdate) {
        DefaultMessageHandler(multipleObjectUpdate)
    }

    open fun HandleMuteListRequest(muteListRequest: MuteListRequest) {
        DefaultMessageHandler(muteListRequest)
    }

    open fun HandleMuteListUpdate(muteListUpdate: MuteListUpdate) {
        DefaultMessageHandler(muteListUpdate)
    }

    open fun HandleNameValuePair(nameValuePair: NameValuePair) {
        DefaultMessageHandler(nameValuePair)
    }

    open fun HandleNearestLandingRegionReply(nearestLandingRegionReply: NearestLandingRegionReply) {
        DefaultMessageHandler(nearestLandingRegionReply)
    }

    open fun HandleNearestLandingRegionRequest(nearestLandingRegionRequest: NearestLandingRegionRequest) {
        DefaultMessageHandler(nearestLandingRegionRequest)
    }

    open fun HandleNearestLandingRegionUpdated(nearestLandingRegionUpdated: NearestLandingRegionUpdated) {
        DefaultMessageHandler(nearestLandingRegionUpdated)
    }

    open fun HandleNeighborList(neighborList: NeighborList) {
        DefaultMessageHandler(neighborList)
    }

    open fun HandleNetTest(netTest: NetTest) {
        DefaultMessageHandler(netTest)
    }

    open fun HandleObjectAdd(objectAdd: ObjectAdd) {
        DefaultMessageHandler(objectAdd)
    }

    open fun HandleObjectAttach(objectAttach: ObjectAttach) {
        DefaultMessageHandler(objectAttach)
    }

    open fun HandleObjectBuy(objectBuy: ObjectBuy) {
        DefaultMessageHandler(objectBuy)
    }

    open fun HandleObjectCategory(objectCategory: ObjectCategory) {
        DefaultMessageHandler(objectCategory)
    }

    open fun HandleObjectClickAction(objectClickAction: ObjectClickAction) {
        DefaultMessageHandler(objectClickAction)
    }

    open fun HandleObjectDeGrab(objectDeGrab: ObjectDeGrab) {
        DefaultMessageHandler(objectDeGrab)
    }

    open fun HandleObjectDelete(objectDelete: ObjectDelete) {
        DefaultMessageHandler(objectDelete)
    }

    open fun HandleObjectDelink(objectDelink: ObjectDelink) {
        DefaultMessageHandler(objectDelink)
    }

    open fun HandleObjectDescription(objectDescription: ObjectDescription) {
        DefaultMessageHandler(objectDescription)
    }

    open fun HandleObjectDeselect(objectDeselect: ObjectDeselect) {
        DefaultMessageHandler(objectDeselect)
    }

    open fun HandleObjectDetach(objectDetach: ObjectDetach) {
        DefaultMessageHandler(objectDetach)
    }

    open fun HandleObjectDrop(objectDrop: ObjectDrop) {
        DefaultMessageHandler(objectDrop)
    }

    open fun HandleObjectDuplicate(objectDuplicate: ObjectDuplicate) {
        DefaultMessageHandler(objectDuplicate)
    }

    open fun HandleObjectDuplicateOnRay(objectDuplicateOnRay: ObjectDuplicateOnRay) {
        DefaultMessageHandler(objectDuplicateOnRay)
    }

    open fun HandleObjectExportSelected(objectExportSelected: ObjectExportSelected) {
        DefaultMessageHandler(objectExportSelected)
    }

    open fun HandleObjectExtraParams(objectExtraParams: ObjectExtraParams) {
        DefaultMessageHandler(objectExtraParams)
    }

    open fun HandleObjectFlagUpdate(objectFlagUpdate: ObjectFlagUpdate) {
        DefaultMessageHandler(objectFlagUpdate)
    }

    open fun HandleObjectGrab(objectGrab: ObjectGrab) {
        DefaultMessageHandler(objectGrab)
    }

    open fun HandleObjectGrabUpdate(objectGrabUpdate: ObjectGrabUpdate) {
        DefaultMessageHandler(objectGrabUpdate)
    }

    open fun HandleObjectGroup(objectGroup: ObjectGroup) {
        DefaultMessageHandler(objectGroup)
    }

    open fun HandleObjectImage(objectImage: ObjectImage) {
        DefaultMessageHandler(objectImage)
    }

    open fun HandleObjectIncludeInSearch(objectIncludeInSearch: ObjectIncludeInSearch) {
        DefaultMessageHandler(objectIncludeInSearch)
    }

    open fun HandleObjectLink(objectLink: ObjectLink) {
        DefaultMessageHandler(objectLink)
    }

    open fun HandleObjectMaterial(objectMaterial: ObjectMaterial) {
        DefaultMessageHandler(objectMaterial)
    }

    open fun HandleObjectName(objectName: ObjectName) {
        DefaultMessageHandler(objectName)
    }

    open fun HandleObjectOwner(objectOwner: ObjectOwner) {
        DefaultMessageHandler(objectOwner)
    }

    open fun HandleObjectPermissions(objectPermissions: ObjectPermissions) {
        DefaultMessageHandler(objectPermissions)
    }

    open fun HandleObjectPosition(objectPosition: ObjectPosition) {
        DefaultMessageHandler(objectPosition)
    }

    open fun HandleObjectProperties(objectProperties: ObjectProperties) {
        DefaultMessageHandler(objectProperties)
    }

    open fun HandleObjectPropertiesFamily(objectPropertiesFamily: ObjectPropertiesFamily) {
        DefaultMessageHandler(objectPropertiesFamily)
    }

    open fun HandleObjectRotation(objectRotation: ObjectRotation) {
        DefaultMessageHandler(objectRotation)
    }

    open fun HandleObjectSaleInfo(objectSaleInfo: ObjectSaleInfo) {
        DefaultMessageHandler(objectSaleInfo)
    }

    open fun HandleObjectScale(objectScale: ObjectScale) {
        DefaultMessageHandler(objectScale)
    }

    open fun HandleObjectSelect(objectSelect: ObjectSelect) {
        DefaultMessageHandler(objectSelect)
    }

    open fun HandleObjectShape(objectShape: ObjectShape) {
        DefaultMessageHandler(objectShape)
    }

    open fun HandleObjectSpinStart(objectSpinStart: ObjectSpinStart) {
        DefaultMessageHandler(objectSpinStart)
    }

    open fun HandleObjectSpinStop(objectSpinStop: ObjectSpinStop) {
        DefaultMessageHandler(objectSpinStop)
    }

    open fun HandleObjectSpinUpdate(objectSpinUpdate: ObjectSpinUpdate) {
        DefaultMessageHandler(objectSpinUpdate)
    }

    open fun HandleObjectUpdate(objectUpdate: ObjectUpdate) {
        DefaultMessageHandler(objectUpdate)
    }

    open fun HandleObjectUpdateCached(objectUpdateCached: ObjectUpdateCached) {
        DefaultMessageHandler(objectUpdateCached)
    }

    open fun HandleObjectUpdateCompressed(objectUpdateCompressed: ObjectUpdateCompressed) {
        DefaultMessageHandler(objectUpdateCompressed)
    }

    open fun HandleOfferCallingCard(offerCallingCard: OfferCallingCard) {
        DefaultMessageHandler(offerCallingCard)
    }

    open fun HandleOfflineNotification(offlineNotification: OfflineNotification) {
        DefaultMessageHandler(offlineNotification)
    }

    open fun HandleOnlineNotification(onlineNotification: OnlineNotification) {
        DefaultMessageHandler(onlineNotification)
    }

    open fun HandleOpenCircuit(openCircuit: OpenCircuit) {
        DefaultMessageHandler(openCircuit)
    }

    open fun HandlePacketAck(packetAck: PacketAck) {
        DefaultMessageHandler(packetAck)
    }

    open fun HandleParcelAccessListReply(parcelAccessListReply: ParcelAccessListReply) {
        DefaultMessageHandler(parcelAccessListReply)
    }

    open fun HandleParcelAccessListRequest(parcelAccessListRequest: ParcelAccessListRequest) {
        DefaultMessageHandler(parcelAccessListRequest)
    }

    open fun HandleParcelAccessListUpdate(parcelAccessListUpdate: ParcelAccessListUpdate) {
        DefaultMessageHandler(parcelAccessListUpdate)
    }

    open fun HandleParcelAuctions(parcelAuctions: ParcelAuctions) {
        DefaultMessageHandler(parcelAuctions)
    }

    open fun HandleParcelBuy(parcelBuy: ParcelBuy) {
        DefaultMessageHandler(parcelBuy)
    }

    open fun HandleParcelBuyPass(parcelBuyPass: ParcelBuyPass) {
        DefaultMessageHandler(parcelBuyPass)
    }

    open fun HandleParcelClaim(parcelClaim: ParcelClaim) {
        DefaultMessageHandler(parcelClaim)
    }

    open fun HandleParcelDeedToGroup(parcelDeedToGroup: ParcelDeedToGroup) {
        DefaultMessageHandler(parcelDeedToGroup)
    }

    open fun HandleParcelDisableObjects(parcelDisableObjects: ParcelDisableObjects) {
        DefaultMessageHandler(parcelDisableObjects)
    }

    open fun HandleParcelDivide(parcelDivide: ParcelDivide) {
        DefaultMessageHandler(parcelDivide)
    }

    open fun HandleParcelDwellReply(parcelDwellReply: ParcelDwellReply) {
        DefaultMessageHandler(parcelDwellReply)
    }

    open fun HandleParcelDwellRequest(parcelDwellRequest: ParcelDwellRequest) {
        DefaultMessageHandler(parcelDwellRequest)
    }

    open fun HandleParcelGodForceOwner(parcelGodForceOwner: ParcelGodForceOwner) {
        DefaultMessageHandler(parcelGodForceOwner)
    }

    open fun HandleParcelGodMarkAsContent(parcelGodMarkAsContent: ParcelGodMarkAsContent) {
        DefaultMessageHandler(parcelGodMarkAsContent)
    }

    open fun HandleParcelInfoReply(parcelInfoReply: ParcelInfoReply) {
        DefaultMessageHandler(parcelInfoReply)
    }

    open fun HandleParcelInfoRequest(parcelInfoRequest: ParcelInfoRequest) {
        DefaultMessageHandler(parcelInfoRequest)
    }

    open fun HandleParcelJoin(parcelJoin: ParcelJoin) {
        DefaultMessageHandler(parcelJoin)
    }

    open fun HandleParcelMediaCommandMessage(parcelMediaCommandMessage: ParcelMediaCommandMessage) {
        DefaultMessageHandler(parcelMediaCommandMessage)
    }

    open fun HandleParcelMediaUpdate(parcelMediaUpdate: ParcelMediaUpdate) {
        DefaultMessageHandler(parcelMediaUpdate)
    }

    open fun HandleParcelObjectOwnersReply(parcelObjectOwnersReply: ParcelObjectOwnersReply) {
        DefaultMessageHandler(parcelObjectOwnersReply)
    }

    open fun HandleParcelObjectOwnersRequest(parcelObjectOwnersRequest: ParcelObjectOwnersRequest) {
        DefaultMessageHandler(parcelObjectOwnersRequest)
    }

    open fun HandleParcelOverlay(parcelOverlay: ParcelOverlay) {
        DefaultMessageHandler(parcelOverlay)
    }

    open fun HandleParcelProperties(parcelProperties: ParcelProperties) {
        DefaultMessageHandler(parcelProperties)
    }

    open fun HandleParcelPropertiesRequest(parcelPropertiesRequest: ParcelPropertiesRequest) {
        DefaultMessageHandler(parcelPropertiesRequest)
    }

    open fun HandleParcelPropertiesRequestByID(parcelPropertiesRequestByID: ParcelPropertiesRequestByID) {
        DefaultMessageHandler(parcelPropertiesRequestByID)
    }

    open fun HandleParcelPropertiesUpdate(parcelPropertiesUpdate: ParcelPropertiesUpdate) {
        DefaultMessageHandler(parcelPropertiesUpdate)
    }

    open fun HandleParcelReclaim(parcelReclaim: ParcelReclaim) {
        DefaultMessageHandler(parcelReclaim)
    }

    open fun HandleParcelRelease(parcelRelease: ParcelRelease) {
        DefaultMessageHandler(parcelRelease)
    }

    open fun HandleParcelRename(parcelRename: ParcelRename) {
        DefaultMessageHandler(parcelRename)
    }

    open fun HandleParcelReturnObjects(parcelReturnObjects: ParcelReturnObjects) {
        DefaultMessageHandler(parcelReturnObjects)
    }

    open fun HandleParcelSales(parcelSales: ParcelSales) {
        DefaultMessageHandler(parcelSales)
    }

    open fun HandleParcelSelectObjects(parcelSelectObjects: ParcelSelectObjects) {
        DefaultMessageHandler(parcelSelectObjects)
    }

    open fun HandleParcelSetOtherCleanTime(parcelSetOtherCleanTime: ParcelSetOtherCleanTime) {
        DefaultMessageHandler(parcelSetOtherCleanTime)
    }

    open fun HandlePayPriceReply(payPriceReply: PayPriceReply) {
        DefaultMessageHandler(payPriceReply)
    }

    open fun HandlePickDelete(pickDelete: PickDelete) {
        DefaultMessageHandler(pickDelete)
    }

    open fun HandlePickGodDelete(pickGodDelete: PickGodDelete) {
        DefaultMessageHandler(pickGodDelete)
    }

    open fun HandlePickInfoReply(pickInfoReply: PickInfoReply) {
        DefaultMessageHandler(pickInfoReply)
    }

    open fun HandlePickInfoUpdate(pickInfoUpdate: PickInfoUpdate) {
        DefaultMessageHandler(pickInfoUpdate)
    }

    open fun HandlePlacesQuery(placesQuery: PlacesQuery) {
        DefaultMessageHandler(placesQuery)
    }

    open fun HandlePlacesReply(placesReply: PlacesReply) {
        DefaultMessageHandler(placesReply)
    }

    open fun HandlePreloadSound(preloadSound: PreloadSound) {
        DefaultMessageHandler(preloadSound)
    }

    open fun HandlePurgeInventoryDescendents(purgeInventoryDescendents: PurgeInventoryDescendents) {
        DefaultMessageHandler(purgeInventoryDescendents)
    }

    open fun HandleRebakeAvatarTextures(rebakeAvatarTextures: RebakeAvatarTextures) {
        DefaultMessageHandler(rebakeAvatarTextures)
    }

    open fun HandleRedo(redo: Redo) {
        DefaultMessageHandler(redo)
    }

    open fun HandleRegionHandleRequest(regionHandleRequest: RegionHandleRequest) {
        DefaultMessageHandler(regionHandleRequest)
    }

    open fun HandleRegionHandshake(regionHandshake: RegionHandshake) {
        DefaultMessageHandler(regionHandshake)
    }

    open fun HandleRegionHandshakeReply(regionHandshakeReply: RegionHandshakeReply) {
        DefaultMessageHandler(regionHandshakeReply)
    }

    open fun HandleRegionIDAndHandleReply(regionIDAndHandleReply: RegionIDAndHandleReply) {
        DefaultMessageHandler(regionIDAndHandleReply)
    }

    open fun HandleRegionInfo(regionInfo: RegionInfo) {
        DefaultMessageHandler(regionInfo)
    }

    open fun HandleRegionPresenceRequestByHandle(regionPresenceRequestByHandle: RegionPresenceRequestByHandle) {
        DefaultMessageHandler(regionPresenceRequestByHandle)
    }

    open fun HandleRegionPresenceRequestByRegionID(regionPresenceRequestByRegionID: RegionPresenceRequestByRegionID) {
        DefaultMessageHandler(regionPresenceRequestByRegionID)
    }

    open fun HandleRegionPresenceResponse(regionPresenceResponse: RegionPresenceResponse) {
        DefaultMessageHandler(regionPresenceResponse)
    }

    open fun HandleRemoveAttachment(removeAttachment: RemoveAttachment) {
        DefaultMessageHandler(removeAttachment)
    }

    open fun HandleRemoveInventoryFolder(removeInventoryFolder: RemoveInventoryFolder) {
        DefaultMessageHandler(removeInventoryFolder)
    }

    open fun HandleRemoveInventoryItem(removeInventoryItem: RemoveInventoryItem) {
        DefaultMessageHandler(removeInventoryItem)
    }

    open fun HandleRemoveInventoryObjects(removeInventoryObjects: RemoveInventoryObjects) {
        DefaultMessageHandler(removeInventoryObjects)
    }

    open fun HandleRemoveMuteListEntry(removeMuteListEntry: RemoveMuteListEntry) {
        DefaultMessageHandler(removeMuteListEntry)
    }

    open fun HandleRemoveNameValuePair(removeNameValuePair: RemoveNameValuePair) {
        DefaultMessageHandler(removeNameValuePair)
    }

    open fun HandleRemoveParcel(removeParcel: RemoveParcel) {
        DefaultMessageHandler(removeParcel)
    }

    open fun HandleRemoveTaskInventory(removeTaskInventory: RemoveTaskInventory) {
        DefaultMessageHandler(removeTaskInventory)
    }

    open fun HandleReplyTaskInventory(replyTaskInventory: ReplyTaskInventory) {
        DefaultMessageHandler(replyTaskInventory)
    }

    open fun HandleReportAutosaveCrash(reportAutosaveCrash: ReportAutosaveCrash) {
        DefaultMessageHandler(reportAutosaveCrash)
    }

    open fun HandleRequestGodlikePowers(requestGodlikePowers: RequestGodlikePowers) {
        DefaultMessageHandler(requestGodlikePowers)
    }

    open fun HandleRequestImage(requestImage: RequestImage) {
        DefaultMessageHandler(requestImage)
    }

    open fun HandleRequestInventoryAsset(requestInventoryAsset: RequestInventoryAsset) {
        DefaultMessageHandler(requestInventoryAsset)
    }

    open fun HandleRequestMultipleObjects(requestMultipleObjects: RequestMultipleObjects) {
        DefaultMessageHandler(requestMultipleObjects)
    }

    open fun HandleRequestObjectPropertiesFamily(requestObjectPropertiesFamily: RequestObjectPropertiesFamily) {
        DefaultMessageHandler(requestObjectPropertiesFamily)
    }

    open fun HandleRequestParcelTransfer(requestParcelTransfer: RequestParcelTransfer) {
        DefaultMessageHandler(requestParcelTransfer)
    }

    open fun HandleRequestPayPrice(requestPayPrice: RequestPayPrice) {
        DefaultMessageHandler(requestPayPrice)
    }

    open fun HandleRequestRegionInfo(requestRegionInfo: RequestRegionInfo) {
        DefaultMessageHandler(requestRegionInfo)
    }

    open fun HandleRequestTaskInventory(requestTaskInventory: RequestTaskInventory) {
        DefaultMessageHandler(requestTaskInventory)
    }

    open fun HandleRequestTrustedCircuit(requestTrustedCircuit: RequestTrustedCircuit) {
        DefaultMessageHandler(requestTrustedCircuit)
    }

    open fun HandleRequestXfer(requestXfer: RequestXfer) {
        DefaultMessageHandler(requestXfer)
    }

    open fun HandleRetrieveInstantMessages(retrieveInstantMessages: RetrieveInstantMessages) {
        DefaultMessageHandler(retrieveInstantMessages)
    }

    open fun HandleRevokePermissions(revokePermissions: RevokePermissions) {
        DefaultMessageHandler(revokePermissions)
    }

    open fun HandleRezMultipleAttachmentsFromInv(rezMultipleAttachmentsFromInv: RezMultipleAttachmentsFromInv) {
        DefaultMessageHandler(rezMultipleAttachmentsFromInv)
    }

    open fun HandleRezObject(rezObject: RezObject) {
        DefaultMessageHandler(rezObject)
    }

    open fun HandleRezObjectFromNotecard(rezObjectFromNotecard: RezObjectFromNotecard) {
        DefaultMessageHandler(rezObjectFromNotecard)
    }

    open fun HandleRezRestoreToWorld(rezRestoreToWorld: RezRestoreToWorld) {
        DefaultMessageHandler(rezRestoreToWorld)
    }

    open fun HandleRezScript(rezScript: RezScript) {
        DefaultMessageHandler(rezScript)
    }

    open fun HandleRezSingleAttachmentFromInv(rezSingleAttachmentFromInv: RezSingleAttachmentFromInv) {
        DefaultMessageHandler(rezSingleAttachmentFromInv)
    }

    open fun HandleRoutedMoneyBalanceReply(routedMoneyBalanceReply: RoutedMoneyBalanceReply) {
        DefaultMessageHandler(routedMoneyBalanceReply)
    }

    open fun HandleRpcChannelReply(rpcChannelReply: RpcChannelReply) {
        DefaultMessageHandler(rpcChannelReply)
    }

    open fun HandleRpcChannelRequest(rpcChannelRequest: RpcChannelRequest) {
        DefaultMessageHandler(rpcChannelRequest)
    }

    open fun HandleRpcScriptReplyInbound(rpcScriptReplyInbound: RpcScriptReplyInbound) {
        DefaultMessageHandler(rpcScriptReplyInbound)
    }

    open fun HandleRpcScriptRequestInbound(rpcScriptRequestInbound: RpcScriptRequestInbound) {
        DefaultMessageHandler(rpcScriptRequestInbound)
    }

    open fun HandleRpcScriptRequestInboundForward(rpcScriptRequestInboundForward: RpcScriptRequestInboundForward) {
        DefaultMessageHandler(rpcScriptRequestInboundForward)
    }

    open fun HandleSaveAssetIntoInventory(saveAssetIntoInventory: SaveAssetIntoInventory) {
        DefaultMessageHandler(saveAssetIntoInventory)
    }

    open fun HandleScriptAnswerYes(scriptAnswerYes: ScriptAnswerYes) {
        DefaultMessageHandler(scriptAnswerYes)
    }

    open fun HandleScriptControlChange(scriptControlChange: ScriptControlChange) {
        DefaultMessageHandler(scriptControlChange)
    }

    open fun HandleScriptDataReply(scriptDataReply: ScriptDataReply) {
        DefaultMessageHandler(scriptDataReply)
    }

    open fun HandleScriptDataRequest(scriptDataRequest: ScriptDataRequest) {
        DefaultMessageHandler(scriptDataRequest)
    }

    open fun HandleScriptDialog(scriptDialog: ScriptDialog) {
        DefaultMessageHandler(scriptDialog)
    }

    open fun HandleScriptDialogReply(scriptDialogReply: ScriptDialogReply) {
        DefaultMessageHandler(scriptDialogReply)
    }

    open fun HandleScriptMailRegistration(scriptMailRegistration: ScriptMailRegistration) {
        DefaultMessageHandler(scriptMailRegistration)
    }

    open fun HandleScriptQuestion(scriptQuestion: ScriptQuestion) {
        DefaultMessageHandler(scriptQuestion)
    }

    open fun HandleScriptReset(scriptReset: ScriptReset) {
        DefaultMessageHandler(scriptReset)
    }

    open fun HandleScriptRunningReply(scriptRunningReply: ScriptRunningReply) {
        DefaultMessageHandler(scriptRunningReply)
    }

    open fun HandleScriptSensorReply(scriptSensorReply: ScriptSensorReply) {
        DefaultMessageHandler(scriptSensorReply)
    }

    open fun HandleScriptSensorRequest(scriptSensorRequest: ScriptSensorRequest) {
        DefaultMessageHandler(scriptSensorRequest)
    }

    open fun HandleScriptTeleportRequest(scriptTeleportRequest: ScriptTeleportRequest) {
        DefaultMessageHandler(scriptTeleportRequest)
    }

    open fun HandleSendPostcard(sendPostcard: SendPostcard) {
        DefaultMessageHandler(sendPostcard)
    }

    open fun HandleSendXferPacket(sendXferPacket: SendXferPacket) {
        DefaultMessageHandler(sendXferPacket)
    }

    open fun HandleSetAlwaysRun(setAlwaysRun: SetAlwaysRun) {
        DefaultMessageHandler(setAlwaysRun)
    }

    open fun HandleSetCPURatio(setCPURatio: SetCPURatio) {
        DefaultMessageHandler(setCPURatio)
    }

    open fun HandleSetFollowCamProperties(setFollowCamProperties: SetFollowCamProperties) {
        DefaultMessageHandler(setFollowCamProperties)
    }

    open fun HandleSetGroupAcceptNotices(setGroupAcceptNotices: SetGroupAcceptNotices) {
        DefaultMessageHandler(setGroupAcceptNotices)
    }

    open fun HandleSetGroupContribution(setGroupContribution: SetGroupContribution) {
        DefaultMessageHandler(setGroupContribution)
    }

    open fun HandleSetScriptRunning(setScriptRunning: SetScriptRunning) {
        DefaultMessageHandler(setScriptRunning)
    }

    open fun HandleSetSimPresenceInDatabase(setSimPresenceInDatabase: SetSimPresenceInDatabase) {
        DefaultMessageHandler(setSimPresenceInDatabase)
    }

    open fun HandleSetSimStatusInDatabase(setSimStatusInDatabase: SetSimStatusInDatabase) {
        DefaultMessageHandler(setSimStatusInDatabase)
    }

    open fun HandleSetStartLocation(setStartLocation: SetStartLocation) {
        DefaultMessageHandler(setStartLocation)
    }

    open fun HandleSetStartLocationRequest(setStartLocationRequest: SetStartLocationRequest) {
        DefaultMessageHandler(setStartLocationRequest)
    }

    open fun HandleSimCrashed(simCrashed: SimCrashed) {
        DefaultMessageHandler(simCrashed)
    }

    open fun HandleSimStats(simStats: SimStats) {
        DefaultMessageHandler(simStats)
    }

    open fun HandleSimStatus(simStatus: SimStatus) {
        DefaultMessageHandler(simStatus)
    }

    open fun HandleSimWideDeletes(simWideDeletes: SimWideDeletes) {
        DefaultMessageHandler(simWideDeletes)
    }

    open fun HandleSimulatorLoad(simulatorLoad: SimulatorLoad) {
        DefaultMessageHandler(simulatorLoad)
    }

    open fun HandleSimulatorMapUpdate(simulatorMapUpdate: SimulatorMapUpdate) {
        DefaultMessageHandler(simulatorMapUpdate)
    }

    open fun HandleSimulatorPresentAtLocation(simulatorPresentAtLocation: SimulatorPresentAtLocation) {
        DefaultMessageHandler(simulatorPresentAtLocation)
    }

    open fun HandleSimulatorReady(simulatorReady: SimulatorReady) {
        DefaultMessageHandler(simulatorReady)
    }

    open fun HandleSimulatorSetMap(simulatorSetMap: SimulatorSetMap) {
        DefaultMessageHandler(simulatorSetMap)
    }

    open fun HandleSimulatorShutdownRequest(simulatorShutdownRequest: SimulatorShutdownRequest) {
        DefaultMessageHandler(simulatorShutdownRequest)
    }

    open fun HandleSimulatorViewerTimeMessage(simulatorViewerTimeMessage: SimulatorViewerTimeMessage) {
        DefaultMessageHandler(simulatorViewerTimeMessage)
    }

    open fun HandleSoundTrigger(soundTrigger: SoundTrigger) {
        DefaultMessageHandler(soundTrigger)
    }

    open fun HandleStartAuction(startAuction: StartAuction) {
        DefaultMessageHandler(startAuction)
    }

    open fun HandleStartGroupProposal(startGroupProposal: StartGroupProposal) {
        DefaultMessageHandler(startGroupProposal)
    }

    open fun HandleStartLure(startLure: StartLure) {
        DefaultMessageHandler(startLure)
    }

    open fun HandleStartPingCheck(startPingCheck: StartPingCheck) {
        DefaultMessageHandler(startPingCheck)
    }

    open fun HandleStateSave(stateSave: StateSave) {
        DefaultMessageHandler(stateSave)
    }

    open fun HandleSubscribeLoad(subscribeLoad: SubscribeLoad) {
        DefaultMessageHandler(subscribeLoad)
    }

    open fun HandleSystemKickUser(systemKickUser: SystemKickUser) {
        DefaultMessageHandler(systemKickUser)
    }

    open fun HandleSystemMessage(systemMessage: SystemMessage) {
        DefaultMessageHandler(systemMessage)
    }

    open fun HandleTallyVotes(tallyVotes: TallyVotes) {
        DefaultMessageHandler(tallyVotes)
    }

    open fun HandleTelehubInfo(telehubInfo: TelehubInfo) {
        DefaultMessageHandler(telehubInfo)
    }

    open fun HandleTeleportCancel(teleportCancel: TeleportCancel) {
        DefaultMessageHandler(teleportCancel)
    }

    open fun HandleTeleportFailed(teleportFailed: TeleportFailed) {
        DefaultMessageHandler(teleportFailed)
    }

    open fun HandleTeleportFinish(teleportFinish: TeleportFinish) {
        DefaultMessageHandler(teleportFinish)
    }

    open fun HandleTeleportLandingStatusChanged(teleportLandingStatusChanged: TeleportLandingStatusChanged) {
        DefaultMessageHandler(teleportLandingStatusChanged)
    }

    open fun HandleTeleportLandmarkRequest(teleportLandmarkRequest: TeleportLandmarkRequest) {
        DefaultMessageHandler(teleportLandmarkRequest)
    }

    open fun HandleTeleportLocal(teleportLocal: TeleportLocal) {
        DefaultMessageHandler(teleportLocal)
    }

    open fun HandleTeleportLocationRequest(teleportLocationRequest: TeleportLocationRequest) {
        DefaultMessageHandler(teleportLocationRequest)
    }

    open fun HandleTeleportLureRequest(teleportLureRequest: TeleportLureRequest) {
        DefaultMessageHandler(teleportLureRequest)
    }

    open fun HandleTeleportProgress(teleportProgress: TeleportProgress) {
        DefaultMessageHandler(teleportProgress)
    }

    open fun HandleTeleportRequest(teleportRequest: TeleportRequest) {
        DefaultMessageHandler(teleportRequest)
    }

    open fun HandleTeleportStart(teleportStart: TeleportStart) {
        DefaultMessageHandler(teleportStart)
    }

    open fun HandleTerminateFriendship(terminateFriendship: TerminateFriendship) {
        DefaultMessageHandler(terminateFriendship)
    }

    open fun HandleTestMessage(testMessage: TestMessage) {
        DefaultMessageHandler(testMessage)
    }

    open fun HandleTrackAgent(trackAgent: TrackAgent) {
        DefaultMessageHandler(trackAgent)
    }

    open fun HandleTransferAbort(transferAbort: TransferAbort) {
        DefaultMessageHandler(transferAbort)
    }

    open fun HandleTransferInfo(transferInfo: TransferInfo) {
        DefaultMessageHandler(transferInfo)
    }

    open fun HandleTransferInventory(transferInventory: TransferInventory) {
        DefaultMessageHandler(transferInventory)
    }

    open fun HandleTransferInventoryAck(transferInventoryAck: TransferInventoryAck) {
        DefaultMessageHandler(transferInventoryAck)
    }

    open fun HandleTransferPacket(transferPacket: TransferPacket) {
        DefaultMessageHandler(transferPacket)
    }

    open fun HandleTransferRequest(transferRequest: TransferRequest) {
        DefaultMessageHandler(transferRequest)
    }

    open fun HandleUUIDGroupNameReply(uUIDGroupNameReply: UUIDGroupNameReply) {
        DefaultMessageHandler(uUIDGroupNameReply)
    }

    open fun HandleUUIDGroupNameRequest(uUIDGroupNameRequest: UUIDGroupNameRequest) {
        DefaultMessageHandler(uUIDGroupNameRequest)
    }

    open fun HandleUUIDNameReply(uUIDNameReply: UUIDNameReply) {
        DefaultMessageHandler(uUIDNameReply)
    }

    open fun HandleUUIDNameRequest(uUIDNameRequest: UUIDNameRequest) {
        DefaultMessageHandler(uUIDNameRequest)
    }

    open fun HandleUndo(undo: Undo) {
        DefaultMessageHandler(undo)
    }

    open fun HandleUndoLand(undoLand: UndoLand) {
        DefaultMessageHandler(undoLand)
    }

    open fun HandleUnsubscribeLoad(unsubscribeLoad: UnsubscribeLoad) {
        DefaultMessageHandler(unsubscribeLoad)
    }

    open fun HandleUpdateAttachment(updateAttachment: UpdateAttachment) {
        DefaultMessageHandler(updateAttachment)
    }

    open fun HandleUpdateCreateInventoryItem(updateCreateInventoryItem: UpdateCreateInventoryItem) {
        DefaultMessageHandler(updateCreateInventoryItem)
    }

    open fun HandleUpdateGroupInfo(updateGroupInfo: UpdateGroupInfo) {
        DefaultMessageHandler(updateGroupInfo)
    }

    open fun HandleUpdateInventoryFolder(updateInventoryFolder: UpdateInventoryFolder) {
        DefaultMessageHandler(updateInventoryFolder)
    }

    open fun HandleUpdateInventoryItem(updateInventoryItem: UpdateInventoryItem) {
        DefaultMessageHandler(updateInventoryItem)
    }

    open fun HandleUpdateMuteListEntry(updateMuteListEntry: UpdateMuteListEntry) {
        DefaultMessageHandler(updateMuteListEntry)
    }

    open fun HandleUpdateParcel(updateParcel: UpdateParcel) {
        DefaultMessageHandler(updateParcel)
    }

    open fun HandleUpdateSimulator(updateSimulator: UpdateSimulator) {
        DefaultMessageHandler(updateSimulator)
    }

    open fun HandleUpdateTaskInventory(updateTaskInventory: UpdateTaskInventory) {
        DefaultMessageHandler(updateTaskInventory)
    }

    open fun HandleUpdateUserInfo(updateUserInfo: UpdateUserInfo) {
        DefaultMessageHandler(updateUserInfo)
    }

    open fun HandleUseCachedMuteList(useCachedMuteList: UseCachedMuteList) {
        DefaultMessageHandler(useCachedMuteList)
    }

    open fun HandleUseCircuitCode(useCircuitCode: UseCircuitCode) {
        DefaultMessageHandler(useCircuitCode)
    }

    open fun HandleUserInfoReply(userInfoReply: UserInfoReply) {
        DefaultMessageHandler(userInfoReply)
    }

    open fun HandleUserInfoRequest(userInfoRequest: UserInfoRequest) {
        DefaultMessageHandler(userInfoRequest)
    }

    open fun HandleUserReport(userReport: UserReport) {
        DefaultMessageHandler(userReport)
    }

    open fun HandleUserReportInternal(userReportInternal: UserReportInternal) {
        DefaultMessageHandler(userReportInternal)
    }

    open fun HandleVelocityInterpolateOff(velocityInterpolateOff: VelocityInterpolateOff) {
        DefaultMessageHandler(velocityInterpolateOff)
    }

    open fun HandleVelocityInterpolateOn(velocityInterpolateOn: VelocityInterpolateOn) {
        DefaultMessageHandler(velocityInterpolateOn)
    }

    open fun HandleViewerEffect(viewerEffect: ViewerEffect) {
        DefaultMessageHandler(viewerEffect)
    }

    open fun HandleViewerFrozenMessage(viewerFrozenMessage: ViewerFrozenMessage) {
        DefaultMessageHandler(viewerFrozenMessage)
    }

    open fun HandleViewerStartAuction(viewerStartAuction: ViewerStartAuction) {
        DefaultMessageHandler(viewerStartAuction)
    }

    open fun HandleViewerStats(viewerStats: ViewerStats) {
        DefaultMessageHandler(viewerStats)
    }
}
