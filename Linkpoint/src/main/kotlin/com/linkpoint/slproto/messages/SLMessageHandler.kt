package com.linkpoint.slproto.messages
import java.util.*

import com.linkpoint.slproto.SLMessage

class SLMessageHandler {
    public Unit DefaultMessageHandler(SLMessage sLMessage) {
    }

    public Unit HandleAbortXfer(AbortXfer abortXfer) {
        DefaultMessageHandler(abortXfer)
    }

    public Unit HandleAcceptCallingCard(AcceptCallingCard acceptCallingCard) {
        DefaultMessageHandler(acceptCallingCard)
    }

    public Unit HandleAcceptFriendship(AcceptFriendship acceptFriendship) {
        DefaultMessageHandler(acceptFriendship)
    }

    public Unit HandleActivateGestures(ActivateGestures activateGestures) {
        DefaultMessageHandler(activateGestures)
    }

    public Unit HandleActivateGroup(ActivateGroup activateGroup) {
        DefaultMessageHandler(activateGroup)
    }

    public Unit HandleAddCircuitCode(AddCircuitCode addCircuitCode) {
        DefaultMessageHandler(addCircuitCode)
    }

    public Unit HandleAgentAlertMessage(AgentAlertMessage agentAlertMessage) {
        DefaultMessageHandler(agentAlertMessage)
    }

    public Unit HandleAgentAnimation(AgentAnimation agentAnimation) {
        DefaultMessageHandler(agentAnimation)
    }

    public Unit HandleAgentCachedTexture(AgentCachedTexture agentCachedTexture) {
        DefaultMessageHandler(agentCachedTexture)
    }

    public Unit HandleAgentCachedTextureResponse(AgentCachedTextureResponse agentCachedTextureResponse) {
        DefaultMessageHandler(agentCachedTextureResponse)
    }

    public Unit HandleAgentDataUpdate(AgentDataUpdate agentDataUpdate) {
        DefaultMessageHandler(agentDataUpdate)
    }

    public Unit HandleAgentDataUpdateRequest(AgentDataUpdateRequest agentDataUpdateRequest) {
        DefaultMessageHandler(agentDataUpdateRequest)
    }

    public Unit HandleAgentDropGroup(AgentDropGroup agentDropGroup) {
        DefaultMessageHandler(agentDropGroup)
    }

    public Unit HandleAgentFOV(AgentFOV agentFOV) {
        DefaultMessageHandler(agentFOV)
    }

    public Unit HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentGroupDataUpdate) {
        DefaultMessageHandler(agentGroupDataUpdate)
    }

    public Unit HandleAgentHeightWidth(AgentHeightWidth agentHeightWidth) {
        DefaultMessageHandler(agentHeightWidth)
    }

    public Unit HandleAgentIsNowWearing(AgentIsNowWearing agentIsNowWearing) {
        DefaultMessageHandler(agentIsNowWearing)
    }

    public Unit HandleAgentMovementComplete(AgentMovementComplete agentMovementComplete) {
        DefaultMessageHandler(agentMovementComplete)
    }

    public Unit HandleAgentPause(AgentPause agentPause) {
        DefaultMessageHandler(agentPause)
    }

    public Unit HandleAgentQuitCopy(AgentQuitCopy agentQuitCopy) {
        DefaultMessageHandler(agentQuitCopy)
    }

    public Unit HandleAgentRequestSit(AgentRequestSit agentRequestSit) {
        DefaultMessageHandler(agentRequestSit)
    }

    public Unit HandleAgentResume(AgentResume agentResume) {
        DefaultMessageHandler(agentResume)
    }

    public Unit HandleAgentSetAppearance(AgentSetAppearance agentSetAppearance) {
        DefaultMessageHandler(agentSetAppearance)
    }

    public Unit HandleAgentSit(AgentSit agentSit) {
        DefaultMessageHandler(agentSit)
    }

    public Unit HandleAgentThrottle(AgentThrottle agentThrottle) {
        DefaultMessageHandler(agentThrottle)
    }

    public Unit HandleAgentUpdate(AgentUpdate agentUpdate) {
        DefaultMessageHandler(agentUpdate)
    }

    public Unit HandleAgentWearablesRequest(AgentWearablesRequest agentWearablesRequest) {
        DefaultMessageHandler(agentWearablesRequest)
    }

    public Unit HandleAgentWearablesUpdate(AgentWearablesUpdate agentWearablesUpdate) {
        DefaultMessageHandler(agentWearablesUpdate)
    }

    public Unit HandleAlertMessage(AlertMessage alertMessage) {
        DefaultMessageHandler(alertMessage)
    }

    public Unit HandleAssetUploadComplete(AssetUploadComplete assetUploadComplete) {
        DefaultMessageHandler(assetUploadComplete)
    }

    public Unit HandleAssetUploadRequest(AssetUploadRequest assetUploadRequest) {
        DefaultMessageHandler(assetUploadRequest)
    }

    public Unit HandleAtomicPassObject(AtomicPassObject atomicPassObject) {
        DefaultMessageHandler(atomicPassObject)
    }

    public Unit HandleAttachedSound(AttachedSound attachedSound) {
        DefaultMessageHandler(attachedSound)
    }

    public Unit HandleAttachedSoundGainChange(AttachedSoundGainChange attachedSoundGainChange) {
        DefaultMessageHandler(attachedSoundGainChange)
    }

    public Unit HandleAvatarAnimation(AvatarAnimation avatarAnimation) {
        DefaultMessageHandler(avatarAnimation)
    }

    public Unit HandleAvatarAppearance(AvatarAppearance avatarAppearance) {
        DefaultMessageHandler(avatarAppearance)
    }

    public Unit HandleAvatarClassifiedReply(AvatarClassifiedReply avatarClassifiedReply) {
        DefaultMessageHandler(avatarClassifiedReply)
    }

    public Unit HandleAvatarGroupsReply(AvatarGroupsReply avatarGroupsReply) {
        DefaultMessageHandler(avatarGroupsReply)
    }

    public Unit HandleAvatarInterestsReply(AvatarInterestsReply avatarInterestsReply) {
        DefaultMessageHandler(avatarInterestsReply)
    }

    public Unit HandleAvatarInterestsUpdate(AvatarInterestsUpdate avatarInterestsUpdate) {
        DefaultMessageHandler(avatarInterestsUpdate)
    }

    public Unit HandleAvatarNotesReply(AvatarNotesReply avatarNotesReply) {
        DefaultMessageHandler(avatarNotesReply)
    }

    public Unit HandleAvatarNotesUpdate(AvatarNotesUpdate avatarNotesUpdate) {
        DefaultMessageHandler(avatarNotesUpdate)
    }

    public Unit HandleAvatarPickerReply(AvatarPickerReply avatarPickerReply) {
        DefaultMessageHandler(avatarPickerReply)
    }

    public Unit HandleAvatarPickerRequest(AvatarPickerRequest avatarPickerRequest) {
        DefaultMessageHandler(avatarPickerRequest)
    }

    public Unit HandleAvatarPickerRequestBackend(AvatarPickerRequestBackend avatarPickerRequestBackend) {
        DefaultMessageHandler(avatarPickerRequestBackend)
    }

    public Unit HandleAvatarPicksReply(AvatarPicksReply avatarPicksReply) {
        DefaultMessageHandler(avatarPicksReply)
    }

    public Unit HandleAvatarPropertiesReply(AvatarPropertiesReply avatarPropertiesReply) {
        DefaultMessageHandler(avatarPropertiesReply)
    }

    public Unit HandleAvatarPropertiesRequest(AvatarPropertiesRequest avatarPropertiesRequest) {
        DefaultMessageHandler(avatarPropertiesRequest)
    }

    public Unit HandleAvatarPropertiesRequestBackend(AvatarPropertiesRequestBackend avatarPropertiesRequestBackend) {
        DefaultMessageHandler(avatarPropertiesRequestBackend)
    }

    public Unit HandleAvatarPropertiesUpdate(AvatarPropertiesUpdate avatarPropertiesUpdate) {
        DefaultMessageHandler(avatarPropertiesUpdate)
    }

    public Unit HandleAvatarSitResponse(AvatarSitResponse avatarSitResponse) {
        DefaultMessageHandler(avatarSitResponse)
    }

    public Unit HandleAvatarTextureUpdate(AvatarTextureUpdate avatarTextureUpdate) {
        DefaultMessageHandler(avatarTextureUpdate)
    }

    public Unit HandleBulkUpdateInventory(BulkUpdateInventory bulkUpdateInventory) {
        DefaultMessageHandler(bulkUpdateInventory)
    }

    public Unit HandleBuyObjectInventory(BuyObjectInventory buyObjectInventory) {
        DefaultMessageHandler(buyObjectInventory)
    }

    public Unit HandleCameraConstraint(CameraConstraint cameraConstraint) {
        DefaultMessageHandler(cameraConstraint)
    }

    public Unit HandleCancelAuction(CancelAuction cancelAuction) {
        DefaultMessageHandler(cancelAuction)
    }

    public Unit HandleChangeInventoryItemFlags(ChangeInventoryItemFlags changeInventoryItemFlags) {
        DefaultMessageHandler(changeInventoryItemFlags)
    }

    public Unit HandleChangeUserRights(ChangeUserRights changeUserRights) {
        DefaultMessageHandler(changeUserRights)
    }

    public Unit HandleChatFromSimulator(ChatFromSimulator chatFromSimulator) {
        DefaultMessageHandler(chatFromSimulator)
    }

    public Unit HandleChatFromViewer(ChatFromViewer chatFromViewer) {
        DefaultMessageHandler(chatFromViewer)
    }

    public Unit HandleChatPass(ChatPass chatPass) {
        DefaultMessageHandler(chatPass)
    }

    public Unit HandleCheckParcelAuctions(CheckParcelAuctions checkParcelAuctions) {
        DefaultMessageHandler(checkParcelAuctions)
    }

    public Unit HandleCheckParcelSales(CheckParcelSales checkParcelSales) {
        DefaultMessageHandler(checkParcelSales)
    }

    public Unit HandleChildAgentAlive(ChildAgentAlive childAgentAlive) {
        DefaultMessageHandler(childAgentAlive)
    }

    public Unit HandleChildAgentDying(ChildAgentDying childAgentDying) {
        DefaultMessageHandler(childAgentDying)
    }

    public Unit HandleChildAgentPositionUpdate(ChildAgentPositionUpdate childAgentPositionUpdate) {
        DefaultMessageHandler(childAgentPositionUpdate)
    }

    public Unit HandleChildAgentUnknown(ChildAgentUnknown childAgentUnknown) {
        DefaultMessageHandler(childAgentUnknown)
    }

    public Unit HandleChildAgentUpdate(ChildAgentUpdate childAgentUpdate) {
        DefaultMessageHandler(childAgentUpdate)
    }

    public Unit HandleClassifiedDelete(ClassifiedDelete classifiedDelete) {
        DefaultMessageHandler(classifiedDelete)
    }

    public Unit HandleClassifiedGodDelete(ClassifiedGodDelete classifiedGodDelete) {
        DefaultMessageHandler(classifiedGodDelete)
    }

    public Unit HandleClassifiedInfoReply(ClassifiedInfoReply classifiedInfoReply) {
        DefaultMessageHandler(classifiedInfoReply)
    }

    public Unit HandleClassifiedInfoRequest(ClassifiedInfoRequest classifiedInfoRequest) {
        DefaultMessageHandler(classifiedInfoRequest)
    }

    public Unit HandleClassifiedInfoUpdate(ClassifiedInfoUpdate classifiedInfoUpdate) {
        DefaultMessageHandler(classifiedInfoUpdate)
    }

    public Unit HandleClearFollowCamProperties(ClearFollowCamProperties clearFollowCamProperties) {
        DefaultMessageHandler(clearFollowCamProperties)
    }

    public Unit HandleCloseCircuit(CloseCircuit closeCircuit) {
        DefaultMessageHandler(closeCircuit)
    }

    public Unit HandleCoarseLocationUpdate(CoarseLocationUpdate coarseLocationUpdate) {
        DefaultMessageHandler(coarseLocationUpdate)
    }

    public Unit HandleCompleteAgentMovement(CompleteAgentMovement completeAgentMovement) {
        DefaultMessageHandler(completeAgentMovement)
    }

    public Unit HandleCompleteAuction(CompleteAuction completeAuction) {
        DefaultMessageHandler(completeAuction)
    }

    public Unit HandleCompletePingCheck(CompletePingCheck completePingCheck) {
        DefaultMessageHandler(completePingCheck)
    }

    public Unit HandleConfirmAuctionStart(ConfirmAuctionStart confirmAuctionStart) {
        DefaultMessageHandler(confirmAuctionStart)
    }

    public Unit HandleConfirmEnableSimulator(ConfirmEnableSimulator confirmEnableSimulator) {
        DefaultMessageHandler(confirmEnableSimulator)
    }

    public Unit HandleConfirmXferPacket(ConfirmXferPacket confirmXferPacket) {
        DefaultMessageHandler(confirmXferPacket)
    }

    public Unit HandleCopyInventoryFromNotecard(CopyInventoryFromNotecard copyInventoryFromNotecard) {
        DefaultMessageHandler(copyInventoryFromNotecard)
    }

    public Unit HandleCopyInventoryItem(CopyInventoryItem copyInventoryItem) {
        DefaultMessageHandler(copyInventoryItem)
    }

    public Unit HandleCreateGroupReply(CreateGroupReply createGroupReply) {
        DefaultMessageHandler(createGroupReply)
    }

    public Unit HandleCreateGroupRequest(CreateGroupRequest createGroupRequest) {
        DefaultMessageHandler(createGroupRequest)
    }

    public Unit HandleCreateInventoryFolder(CreateInventoryFolder createInventoryFolder) {
        DefaultMessageHandler(createInventoryFolder)
    }

    public Unit HandleCreateInventoryItem(CreateInventoryItem createInventoryItem) {
        DefaultMessageHandler(createInventoryItem)
    }

    public Unit HandleCreateLandmarkForEvent(CreateLandmarkForEvent createLandmarkForEvent) {
        DefaultMessageHandler(createLandmarkForEvent)
    }

    public Unit HandleCreateNewOutfitAttachments(CreateNewOutfitAttachments createNewOutfitAttachments) {
        DefaultMessageHandler(createNewOutfitAttachments)
    }

    public Unit HandleCreateTrustedCircuit(CreateTrustedCircuit createTrustedCircuit) {
        DefaultMessageHandler(createTrustedCircuit)
    }

    public Unit HandleCrossedRegion(CrossedRegion crossedRegion) {
        DefaultMessageHandler(crossedRegion)
    }

    public Unit HandleDataHomeLocationReply(DataHomeLocationReply dataHomeLocationReply) {
        DefaultMessageHandler(dataHomeLocationReply)
    }

    public Unit HandleDataHomeLocationRequest(DataHomeLocationRequest dataHomeLocationRequest) {
        DefaultMessageHandler(dataHomeLocationRequest)
    }

    public Unit HandleDataServerLogout(DataServerLogout dataServerLogout) {
        DefaultMessageHandler(dataServerLogout)
    }

    public Unit HandleDeRezAck(DeRezAck deRezAck) {
        DefaultMessageHandler(deRezAck)
    }

    public Unit HandleDeRezObject(DeRezObject deRezObject) {
        DefaultMessageHandler(deRezObject)
    }

    public Unit HandleDeactivateGestures(DeactivateGestures deactivateGestures) {
        DefaultMessageHandler(deactivateGestures)
    }

    public Unit HandleDeclineCallingCard(DeclineCallingCard declineCallingCard) {
        DefaultMessageHandler(declineCallingCard)
    }

    public Unit HandleDeclineFriendship(DeclineFriendship declineFriendship) {
        DefaultMessageHandler(declineFriendship)
    }

    public Unit HandleDenyTrustedCircuit(DenyTrustedCircuit denyTrustedCircuit) {
        DefaultMessageHandler(denyTrustedCircuit)
    }

    public Unit HandleDerezContainer(DerezContainer derezContainer) {
        DefaultMessageHandler(derezContainer)
    }

    public Unit HandleDetachAttachmentIntoInv(DetachAttachmentIntoInv detachAttachmentIntoInv) {
        DefaultMessageHandler(detachAttachmentIntoInv)
    }

    public Unit HandleDirClassifiedQuery(DirClassifiedQuery dirClassifiedQuery) {
        DefaultMessageHandler(dirClassifiedQuery)
    }

    public Unit HandleDirClassifiedQueryBackend(DirClassifiedQueryBackend dirClassifiedQueryBackend) {
        DefaultMessageHandler(dirClassifiedQueryBackend)
    }

    public Unit HandleDirClassifiedReply(DirClassifiedReply dirClassifiedReply) {
        DefaultMessageHandler(dirClassifiedReply)
    }

    public Unit HandleDirEventsReply(DirEventsReply dirEventsReply) {
        DefaultMessageHandler(dirEventsReply)
    }

    public Unit HandleDirFindQuery(DirFindQuery dirFindQuery) {
        DefaultMessageHandler(dirFindQuery)
    }

    public Unit HandleDirFindQueryBackend(DirFindQueryBackend dirFindQueryBackend) {
        DefaultMessageHandler(dirFindQueryBackend)
    }

    public Unit HandleDirGroupsReply(DirGroupsReply dirGroupsReply) {
        DefaultMessageHandler(dirGroupsReply)
    }

    public Unit HandleDirLandQuery(DirLandQuery dirLandQuery) {
        DefaultMessageHandler(dirLandQuery)
    }

    public Unit HandleDirLandQueryBackend(DirLandQueryBackend dirLandQueryBackend) {
        DefaultMessageHandler(dirLandQueryBackend)
    }

    public Unit HandleDirLandReply(DirLandReply dirLandReply) {
        DefaultMessageHandler(dirLandReply)
    }

    public Unit HandleDirPeopleReply(DirPeopleReply dirPeopleReply) {
        DefaultMessageHandler(dirPeopleReply)
    }

    public Unit HandleDirPlacesQuery(DirPlacesQuery dirPlacesQuery) {
        DefaultMessageHandler(dirPlacesQuery)
    }

    public Unit HandleDirPlacesQueryBackend(DirPlacesQueryBackend dirPlacesQueryBackend) {
        DefaultMessageHandler(dirPlacesQueryBackend)
    }

    public Unit HandleDirPlacesReply(DirPlacesReply dirPlacesReply) {
        DefaultMessageHandler(dirPlacesReply)
    }

    public Unit HandleDirPopularQuery(DirPopularQuery dirPopularQuery) {
        DefaultMessageHandler(dirPopularQuery)
    }

    public Unit HandleDirPopularQueryBackend(DirPopularQueryBackend dirPopularQueryBackend) {
        DefaultMessageHandler(dirPopularQueryBackend)
    }

    public Unit HandleDirPopularReply(DirPopularReply dirPopularReply) {
        DefaultMessageHandler(dirPopularReply)
    }

    public Unit HandleDisableSimulator(DisableSimulator disableSimulator) {
        DefaultMessageHandler(disableSimulator)
    }

    public Unit HandleEconomyData(EconomyData economyData) {
        DefaultMessageHandler(economyData)
    }

    public Unit HandleEconomyDataRequest(EconomyDataRequest economyDataRequest) {
        DefaultMessageHandler(economyDataRequest)
    }

    public Unit HandleEdgeDataPacket(EdgeDataPacket edgeDataPacket) {
        DefaultMessageHandler(edgeDataPacket)
    }

    public Unit HandleEjectGroupMemberReply(EjectGroupMemberReply ejectGroupMemberReply) {
        DefaultMessageHandler(ejectGroupMemberReply)
    }

    public Unit HandleEjectGroupMemberRequest(EjectGroupMemberRequest ejectGroupMemberRequest) {
        DefaultMessageHandler(ejectGroupMemberRequest)
    }

    public Unit HandleEjectUser(EjectUser ejectUser) {
        DefaultMessageHandler(ejectUser)
    }

    public Unit HandleEmailMessageReply(EmailMessageReply emailMessageReply) {
        DefaultMessageHandler(emailMessageReply)
    }

    public Unit HandleEmailMessageRequest(EmailMessageRequest emailMessageRequest) {
        DefaultMessageHandler(emailMessageRequest)
    }

    public Unit HandleEnableSimulator(EnableSimulator enableSimulator) {
        DefaultMessageHandler(enableSimulator)
    }

    public Unit HandleError(Error error) {
        DefaultMessageHandler(error)
    }

    public Unit HandleEstateCovenantReply(EstateCovenantReply estateCovenantReply) {
        DefaultMessageHandler(estateCovenantReply)
    }

    public Unit HandleEstateCovenantRequest(EstateCovenantRequest estateCovenantRequest) {
        DefaultMessageHandler(estateCovenantRequest)
    }

    public Unit HandleEstateOwnerMessage(EstateOwnerMessage estateOwnerMessage) {
        DefaultMessageHandler(estateOwnerMessage)
    }

    public Unit HandleEventGodDelete(EventGodDelete eventGodDelete) {
        DefaultMessageHandler(eventGodDelete)
    }

    public Unit HandleEventInfoReply(EventInfoReply eventInfoReply) {
        DefaultMessageHandler(eventInfoReply)
    }

    public Unit HandleEventInfoRequest(EventInfoRequest eventInfoRequest) {
        DefaultMessageHandler(eventInfoRequest)
    }

    public Unit HandleEventLocationReply(EventLocationReply eventLocationReply) {
        DefaultMessageHandler(eventLocationReply)
    }

    public Unit HandleEventLocationRequest(EventLocationRequest eventLocationRequest) {
        DefaultMessageHandler(eventLocationRequest)
    }

    public Unit HandleEventNotificationAddRequest(EventNotificationAddRequest eventNotificationAddRequest) {
        DefaultMessageHandler(eventNotificationAddRequest)
    }

    public Unit HandleEventNotificationRemoveRequest(EventNotificationRemoveRequest eventNotificationRemoveRequest) {
        DefaultMessageHandler(eventNotificationRemoveRequest)
    }

    public Unit HandleFeatureDisabled(FeatureDisabled featureDisabled) {
        DefaultMessageHandler(featureDisabled)
    }

    public Unit HandleFetchInventory(FetchInventory fetchInventory) {
        DefaultMessageHandler(fetchInventory)
    }

    public Unit HandleFetchInventoryDescendents(FetchInventoryDescendents fetchInventoryDescendents) {
        DefaultMessageHandler(fetchInventoryDescendents)
    }

    public Unit HandleFetchInventoryReply(FetchInventoryReply fetchInventoryReply) {
        DefaultMessageHandler(fetchInventoryReply)
    }

    public Unit HandleFindAgent(FindAgent findAgent) {
        DefaultMessageHandler(findAgent)
    }

    public Unit HandleForceObjectSelect(ForceObjectSelect forceObjectSelect) {
        DefaultMessageHandler(forceObjectSelect)
    }

    public Unit HandleForceScriptControlRelease(ForceScriptControlRelease forceScriptControlRelease) {
        DefaultMessageHandler(forceScriptControlRelease)
    }

    public Unit HandleFormFriendship(FormFriendship formFriendship) {
        DefaultMessageHandler(formFriendship)
    }

    public Unit HandleFreezeUser(FreezeUser freezeUser) {
        DefaultMessageHandler(freezeUser)
    }

    public Unit HandleGenericMessage(GenericMessage genericMessage) {
        DefaultMessageHandler(genericMessage)
    }

    public Unit HandleGetScriptRunning(GetScriptRunning getScriptRunning) {
        DefaultMessageHandler(getScriptRunning)
    }

    public Unit HandleGodKickUser(GodKickUser godKickUser) {
        DefaultMessageHandler(godKickUser)
    }

    public Unit HandleGodUpdateRegionInfo(GodUpdateRegionInfo godUpdateRegionInfo) {
        DefaultMessageHandler(godUpdateRegionInfo)
    }

    public Unit HandleGodlikeMessage(GodlikeMessage godlikeMessage) {
        DefaultMessageHandler(godlikeMessage)
    }

    public Unit HandleGrantGodlikePowers(GrantGodlikePowers grantGodlikePowers) {
        DefaultMessageHandler(grantGodlikePowers)
    }

    public Unit HandleGrantUserRights(GrantUserRights grantUserRights) {
        DefaultMessageHandler(grantUserRights)
    }

    public Unit HandleGroupAccountDetailsReply(GroupAccountDetailsReply groupAccountDetailsReply) {
        DefaultMessageHandler(groupAccountDetailsReply)
    }

    public Unit HandleGroupAccountDetailsRequest(GroupAccountDetailsRequest groupAccountDetailsRequest) {
        DefaultMessageHandler(groupAccountDetailsRequest)
    }

    public Unit HandleGroupAccountSummaryReply(GroupAccountSummaryReply groupAccountSummaryReply) {
        DefaultMessageHandler(groupAccountSummaryReply)
    }

    public Unit HandleGroupAccountSummaryRequest(GroupAccountSummaryRequest groupAccountSummaryRequest) {
        DefaultMessageHandler(groupAccountSummaryRequest)
    }

    public Unit HandleGroupAccountTransactionsReply(GroupAccountTransactionsReply groupAccountTransactionsReply) {
        DefaultMessageHandler(groupAccountTransactionsReply)
    }

    public Unit HandleGroupAccountTransactionsRequest(GroupAccountTransactionsRequest groupAccountTransactionsRequest) {
        DefaultMessageHandler(groupAccountTransactionsRequest)
    }

    public Unit HandleGroupActiveProposalItemReply(GroupActiveProposalItemReply groupActiveProposalItemReply) {
        DefaultMessageHandler(groupActiveProposalItemReply)
    }

    public Unit HandleGroupActiveProposalsRequest(GroupActiveProposalsRequest groupActiveProposalsRequest) {
        DefaultMessageHandler(groupActiveProposalsRequest)
    }

    public Unit HandleGroupDataUpdate(GroupDataUpdate groupDataUpdate) {
        DefaultMessageHandler(groupDataUpdate)
    }

    public Unit HandleGroupMembersReply(GroupMembersReply groupMembersReply) {
        DefaultMessageHandler(groupMembersReply)
    }

    public Unit HandleGroupMembersRequest(GroupMembersRequest groupMembersRequest) {
        DefaultMessageHandler(groupMembersRequest)
    }

    public Unit HandleGroupNoticeAdd(GroupNoticeAdd groupNoticeAdd) {
        DefaultMessageHandler(groupNoticeAdd)
    }

    public Unit HandleGroupNoticeRequest(GroupNoticeRequest groupNoticeRequest) {
        DefaultMessageHandler(groupNoticeRequest)
    }

    public Unit HandleGroupNoticesListReply(GroupNoticesListReply groupNoticesListReply) {
        DefaultMessageHandler(groupNoticesListReply)
    }

    public Unit HandleGroupNoticesListRequest(GroupNoticesListRequest groupNoticesListRequest) {
        DefaultMessageHandler(groupNoticesListRequest)
    }

    public Unit HandleGroupProfileReply(GroupProfileReply groupProfileReply) {
        DefaultMessageHandler(groupProfileReply)
    }

    public Unit HandleGroupProfileRequest(GroupProfileRequest groupProfileRequest) {
        DefaultMessageHandler(groupProfileRequest)
    }

    public Unit HandleGroupProposalBallot(GroupProposalBallot groupProposalBallot) {
        DefaultMessageHandler(groupProposalBallot)
    }

    public Unit HandleGroupRoleChanges(GroupRoleChanges groupRoleChanges) {
        DefaultMessageHandler(groupRoleChanges)
    }

    public Unit HandleGroupRoleDataReply(GroupRoleDataReply groupRoleDataReply) {
        DefaultMessageHandler(groupRoleDataReply)
    }

    public Unit HandleGroupRoleDataRequest(GroupRoleDataRequest groupRoleDataRequest) {
        DefaultMessageHandler(groupRoleDataRequest)
    }

    public Unit HandleGroupRoleMembersReply(GroupRoleMembersReply groupRoleMembersReply) {
        DefaultMessageHandler(groupRoleMembersReply)
    }

    public Unit HandleGroupRoleMembersRequest(GroupRoleMembersRequest groupRoleMembersRequest) {
        DefaultMessageHandler(groupRoleMembersRequest)
    }

    public Unit HandleGroupRoleUpdate(GroupRoleUpdate groupRoleUpdate) {
        DefaultMessageHandler(groupRoleUpdate)
    }

    public Unit HandleGroupTitleUpdate(GroupTitleUpdate groupTitleUpdate) {
        DefaultMessageHandler(groupTitleUpdate)
    }

    public Unit HandleGroupTitlesReply(GroupTitlesReply groupTitlesReply) {
        DefaultMessageHandler(groupTitlesReply)
    }

    public Unit HandleGroupTitlesRequest(GroupTitlesRequest groupTitlesRequest) {
        DefaultMessageHandler(groupTitlesRequest)
    }

    public Unit HandleGroupVoteHistoryItemReply(GroupVoteHistoryItemReply groupVoteHistoryItemReply) {
        DefaultMessageHandler(groupVoteHistoryItemReply)
    }

    public Unit HandleGroupVoteHistoryRequest(GroupVoteHistoryRequest groupVoteHistoryRequest) {
        DefaultMessageHandler(groupVoteHistoryRequest)
    }

    public Unit HandleHealthMessage(HealthMessage healthMessage) {
        DefaultMessageHandler(healthMessage)
    }

    public Unit HandleImageData(ImageData imageData) {
        DefaultMessageHandler(imageData)
    }

    public Unit HandleImageNotInDatabase(ImageNotInDatabase imageNotInDatabase) {
        DefaultMessageHandler(imageNotInDatabase)
    }

    public Unit HandleImagePacket(ImagePacket imagePacket) {
        DefaultMessageHandler(imagePacket)
    }

    public Unit HandleImprovedInstantMessage(ImprovedInstantMessage improvedInstantMessage) {
        DefaultMessageHandler(improvedInstantMessage)
    }

    public Unit HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        DefaultMessageHandler(improvedTerseObjectUpdate)
    }

    public Unit HandleInitiateDownload(InitiateDownload initiateDownload) {
        DefaultMessageHandler(initiateDownload)
    }

    public Unit HandleInternalScriptMail(InternalScriptMail internalScriptMail) {
        DefaultMessageHandler(internalScriptMail)
    }

    public Unit HandleInventoryAssetResponse(InventoryAssetResponse inventoryAssetResponse) {
        DefaultMessageHandler(inventoryAssetResponse)
    }

    public Unit HandleInventoryDescendents(InventoryDescendents inventoryDescendents) {
        DefaultMessageHandler(inventoryDescendents)
    }

    public Unit HandleInviteGroupRequest(InviteGroupRequest inviteGroupRequest) {
        DefaultMessageHandler(inviteGroupRequest)
    }

    public Unit HandleInviteGroupResponse(InviteGroupResponse inviteGroupResponse) {
        DefaultMessageHandler(inviteGroupResponse)
    }

    public Unit HandleJoinGroupReply(JoinGroupReply joinGroupReply) {
        DefaultMessageHandler(joinGroupReply)
    }

    public Unit HandleJoinGroupRequest(JoinGroupRequest joinGroupRequest) {
        DefaultMessageHandler(joinGroupRequest)
    }

    public Unit HandleKickUser(KickUser kickUser) {
        DefaultMessageHandler(kickUser)
    }

    public Unit HandleKickUserAck(KickUserAck kickUserAck) {
        DefaultMessageHandler(kickUserAck)
    }

    public Unit HandleKillChildAgents(KillChildAgents killChildAgents) {
        DefaultMessageHandler(killChildAgents)
    }

    public Unit HandleKillObject(KillObject killObject) {
        DefaultMessageHandler(killObject)
    }

    public Unit HandleLandStatReply(LandStatReply landStatReply) {
        DefaultMessageHandler(landStatReply)
    }

    public Unit HandleLandStatRequest(LandStatRequest landStatRequest) {
        DefaultMessageHandler(landStatRequest)
    }

    public Unit HandleLayerData(LayerData layerData) {
        DefaultMessageHandler(layerData)
    }

    public Unit HandleLeaveGroupReply(LeaveGroupReply leaveGroupReply) {
        DefaultMessageHandler(leaveGroupReply)
    }

    public Unit HandleLeaveGroupRequest(LeaveGroupRequest leaveGroupRequest) {
        DefaultMessageHandler(leaveGroupRequest)
    }

    public Unit HandleLinkInventoryItem(LinkInventoryItem linkInventoryItem) {
        DefaultMessageHandler(linkInventoryItem)
    }

    public Unit HandleLiveHelpGroupReply(LiveHelpGroupReply liveHelpGroupReply) {
        DefaultMessageHandler(liveHelpGroupReply)
    }

    public Unit HandleLiveHelpGroupRequest(LiveHelpGroupRequest liveHelpGroupRequest) {
        DefaultMessageHandler(liveHelpGroupRequest)
    }

    public Unit HandleLoadURL(LoadURL loadURL) {
        DefaultMessageHandler(loadURL)
    }

    public Unit HandleLogDwellTime(LogDwellTime logDwellTime) {
        DefaultMessageHandler(logDwellTime)
    }

    public Unit HandleLogFailedMoneyTransaction(LogFailedMoneyTransaction logFailedMoneyTransaction) {
        DefaultMessageHandler(logFailedMoneyTransaction)
    }

    public Unit HandleLogParcelChanges(LogParcelChanges logParcelChanges) {
        DefaultMessageHandler(logParcelChanges)
    }

    public Unit HandleLogTextMessage(LogTextMessage logTextMessage) {
        DefaultMessageHandler(logTextMessage)
    }

    public Unit HandleLogoutReply(LogoutReply logoutReply) {
        DefaultMessageHandler(logoutReply)
    }

    public Unit HandleLogoutRequest(LogoutRequest logoutRequest) {
        DefaultMessageHandler(logoutRequest)
    }

    public Unit HandleMapBlockReply(MapBlockReply mapBlockReply) {
        DefaultMessageHandler(mapBlockReply)
    }

    public Unit HandleMapBlockRequest(MapBlockRequest mapBlockRequest) {
        DefaultMessageHandler(mapBlockRequest)
    }

    public Unit HandleMapItemReply(MapItemReply mapItemReply) {
        DefaultMessageHandler(mapItemReply)
    }

    public Unit HandleMapItemRequest(MapItemRequest mapItemRequest) {
        DefaultMessageHandler(mapItemRequest)
    }

    public Unit HandleMapLayerReply(MapLayerReply mapLayerReply) {
        DefaultMessageHandler(mapLayerReply)
    }

    public Unit HandleMapLayerRequest(MapLayerRequest mapLayerRequest) {
        DefaultMessageHandler(mapLayerRequest)
    }

    public Unit HandleMapNameRequest(MapNameRequest mapNameRequest) {
        DefaultMessageHandler(mapNameRequest)
    }

    public Unit HandleMeanCollisionAlert(MeanCollisionAlert meanCollisionAlert) {
        DefaultMessageHandler(meanCollisionAlert)
    }

    public Unit HandleMergeParcel(MergeParcel mergeParcel) {
        DefaultMessageHandler(mergeParcel)
    }

    public Unit HandleModifyLand(ModifyLand modifyLand) {
        DefaultMessageHandler(modifyLand)
    }

    public Unit HandleMoneyBalanceReply(MoneyBalanceReply moneyBalanceReply) {
        DefaultMessageHandler(moneyBalanceReply)
    }

    public Unit HandleMoneyBalanceRequest(MoneyBalanceRequest moneyBalanceRequest) {
        DefaultMessageHandler(moneyBalanceRequest)
    }

    public Unit HandleMoneyTransferBackend(MoneyTransferBackend moneyTransferBackend) {
        DefaultMessageHandler(moneyTransferBackend)
    }

    public Unit HandleMoneyTransferRequest(MoneyTransferRequest moneyTransferRequest) {
        DefaultMessageHandler(moneyTransferRequest)
    }

    public Unit HandleMoveInventoryFolder(MoveInventoryFolder moveInventoryFolder) {
        DefaultMessageHandler(moveInventoryFolder)
    }

    public Unit HandleMoveInventoryItem(MoveInventoryItem moveInventoryItem) {
        DefaultMessageHandler(moveInventoryItem)
    }

    public Unit HandleMoveTaskInventory(MoveTaskInventory moveTaskInventory) {
        DefaultMessageHandler(moveTaskInventory)
    }

    public Unit HandleMultipleObjectUpdate(MultipleObjectUpdate multipleObjectUpdate) {
        DefaultMessageHandler(multipleObjectUpdate)
    }

    public Unit HandleMuteListRequest(MuteListRequest muteListRequest) {
        DefaultMessageHandler(muteListRequest)
    }

    public Unit HandleMuteListUpdate(MuteListUpdate muteListUpdate) {
        DefaultMessageHandler(muteListUpdate)
    }

    public Unit HandleNameValuePair(NameValuePair nameValuePair) {
        DefaultMessageHandler(nameValuePair)
    }

    public Unit HandleNearestLandingRegionReply(NearestLandingRegionReply nearestLandingRegionReply) {
        DefaultMessageHandler(nearestLandingRegionReply)
    }

    public Unit HandleNearestLandingRegionRequest(NearestLandingRegionRequest nearestLandingRegionRequest) {
        DefaultMessageHandler(nearestLandingRegionRequest)
    }

    public Unit HandleNearestLandingRegionUpdated(NearestLandingRegionUpdated nearestLandingRegionUpdated) {
        DefaultMessageHandler(nearestLandingRegionUpdated)
    }

    public Unit HandleNeighborList(NeighborList neighborList) {
        DefaultMessageHandler(neighborList)
    }

    public Unit HandleNetTest(NetTest netTest) {
        DefaultMessageHandler(netTest)
    }

    public Unit HandleObjectAdd(ObjectAdd objectAdd) {
        DefaultMessageHandler(objectAdd)
    }

    public Unit HandleObjectAttach(ObjectAttach objectAttach) {
        DefaultMessageHandler(objectAttach)
    }

    public Unit HandleObjectBuy(ObjectBuy objectBuy) {
        DefaultMessageHandler(objectBuy)
    }

    public Unit HandleObjectCategory(ObjectCategory objectCategory) {
        DefaultMessageHandler(objectCategory)
    }

    public Unit HandleObjectClickAction(ObjectClickAction objectClickAction) {
        DefaultMessageHandler(objectClickAction)
    }

    public Unit HandleObjectDeGrab(ObjectDeGrab objectDeGrab) {
        DefaultMessageHandler(objectDeGrab)
    }

    public Unit HandleObjectDelete(ObjectDelete objectDelete) {
        DefaultMessageHandler(objectDelete)
    }

    public Unit HandleObjectDelink(ObjectDelink objectDelink) {
        DefaultMessageHandler(objectDelink)
    }

    public Unit HandleObjectDescription(ObjectDescription objectDescription) {
        DefaultMessageHandler(objectDescription)
    }

    public Unit HandleObjectDeselect(ObjectDeselect objectDeselect) {
        DefaultMessageHandler(objectDeselect)
    }

    public Unit HandleObjectDetach(ObjectDetach objectDetach) {
        DefaultMessageHandler(objectDetach)
    }

    public Unit HandleObjectDrop(ObjectDrop objectDrop) {
        DefaultMessageHandler(objectDrop)
    }

    public Unit HandleObjectDuplicate(ObjectDuplicate objectDuplicate) {
        DefaultMessageHandler(objectDuplicate)
    }

    public Unit HandleObjectDuplicateOnRay(ObjectDuplicateOnRay objectDuplicateOnRay) {
        DefaultMessageHandler(objectDuplicateOnRay)
    }

    public Unit HandleObjectExportSelected(ObjectExportSelected objectExportSelected) {
        DefaultMessageHandler(objectExportSelected)
    }

    public Unit HandleObjectExtraParams(ObjectExtraParams objectExtraParams) {
        DefaultMessageHandler(objectExtraParams)
    }

    public Unit HandleObjectFlagUpdate(ObjectFlagUpdate objectFlagUpdate) {
        DefaultMessageHandler(objectFlagUpdate)
    }

    public Unit HandleObjectGrab(ObjectGrab objectGrab) {
        DefaultMessageHandler(objectGrab)
    }

    public Unit HandleObjectGrabUpdate(ObjectGrabUpdate objectGrabUpdate) {
        DefaultMessageHandler(objectGrabUpdate)
    }

    public Unit HandleObjectGroup(ObjectGroup objectGroup) {
        DefaultMessageHandler(objectGroup)
    }

    public Unit HandleObjectImage(ObjectImage objectImage) {
        DefaultMessageHandler(objectImage)
    }

    public Unit HandleObjectIncludeInSearch(ObjectIncludeInSearch objectIncludeInSearch) {
        DefaultMessageHandler(objectIncludeInSearch)
    }

    public Unit HandleObjectLink(ObjectLink objectLink) {
        DefaultMessageHandler(objectLink)
    }

    public Unit HandleObjectMaterial(ObjectMaterial objectMaterial) {
        DefaultMessageHandler(objectMaterial)
    }

    public Unit HandleObjectName(ObjectName objectName) {
        DefaultMessageHandler(objectName)
    }

    public Unit HandleObjectOwner(ObjectOwner objectOwner) {
        DefaultMessageHandler(objectOwner)
    }

    public Unit HandleObjectPermissions(ObjectPermissions objectPermissions) {
        DefaultMessageHandler(objectPermissions)
    }

    public Unit HandleObjectPosition(ObjectPosition objectPosition) {
        DefaultMessageHandler(objectPosition)
    }

    public Unit HandleObjectProperties(ObjectProperties objectProperties) {
        DefaultMessageHandler(objectProperties)
    }

    public Unit HandleObjectPropertiesFamily(ObjectPropertiesFamily objectPropertiesFamily) {
        DefaultMessageHandler(objectPropertiesFamily)
    }

    public Unit HandleObjectRotation(ObjectRotation objectRotation) {
        DefaultMessageHandler(objectRotation)
    }

    public Unit HandleObjectSaleInfo(ObjectSaleInfo objectSaleInfo) {
        DefaultMessageHandler(objectSaleInfo)
    }

    public Unit HandleObjectScale(ObjectScale objectScale) {
        DefaultMessageHandler(objectScale)
    }

    public Unit HandleObjectSelect(ObjectSelect objectSelect) {
        DefaultMessageHandler(objectSelect)
    }

    public Unit HandleObjectShape(ObjectShape objectShape) {
        DefaultMessageHandler(objectShape)
    }

    public Unit HandleObjectSpinStart(ObjectSpinStart objectSpinStart) {
        DefaultMessageHandler(objectSpinStart)
    }

    public Unit HandleObjectSpinStop(ObjectSpinStop objectSpinStop) {
        DefaultMessageHandler(objectSpinStop)
    }

    public Unit HandleObjectSpinUpdate(ObjectSpinUpdate objectSpinUpdate) {
        DefaultMessageHandler(objectSpinUpdate)
    }

    public Unit HandleObjectUpdate(ObjectUpdate objectUpdate) {
        DefaultMessageHandler(objectUpdate)
    }

    public Unit HandleObjectUpdateCached(ObjectUpdateCached objectUpdateCached) {
        DefaultMessageHandler(objectUpdateCached)
    }

    public Unit HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
        DefaultMessageHandler(objectUpdateCompressed)
    }

    public Unit HandleOfferCallingCard(OfferCallingCard offerCallingCard) {
        DefaultMessageHandler(offerCallingCard)
    }

    public Unit HandleOfflineNotification(OfflineNotification offlineNotification) {
        DefaultMessageHandler(offlineNotification)
    }

    public Unit HandleOnlineNotification(OnlineNotification onlineNotification) {
        DefaultMessageHandler(onlineNotification)
    }

    public Unit HandleOpenCircuit(OpenCircuit openCircuit) {
        DefaultMessageHandler(openCircuit)
    }

    public Unit HandlePacketAck(PacketAck packetAck) {
        DefaultMessageHandler(packetAck)
    }

    public Unit HandleParcelAccessListReply(ParcelAccessListReply parcelAccessListReply) {
        DefaultMessageHandler(parcelAccessListReply)
    }

    public Unit HandleParcelAccessListRequest(ParcelAccessListRequest parcelAccessListRequest) {
        DefaultMessageHandler(parcelAccessListRequest)
    }

    public Unit HandleParcelAccessListUpdate(ParcelAccessListUpdate parcelAccessListUpdate) {
        DefaultMessageHandler(parcelAccessListUpdate)
    }

    public Unit HandleParcelAuctions(ParcelAuctions parcelAuctions) {
        DefaultMessageHandler(parcelAuctions)
    }

    public Unit HandleParcelBuy(ParcelBuy parcelBuy) {
        DefaultMessageHandler(parcelBuy)
    }

    public Unit HandleParcelBuyPass(ParcelBuyPass parcelBuyPass) {
        DefaultMessageHandler(parcelBuyPass)
    }

    public Unit HandleParcelClaim(ParcelClaim parcelClaim) {
        DefaultMessageHandler(parcelClaim)
    }

    public Unit HandleParcelDeedToGroup(ParcelDeedToGroup parcelDeedToGroup) {
        DefaultMessageHandler(parcelDeedToGroup)
    }

    public Unit HandleParcelDisableObjects(ParcelDisableObjects parcelDisableObjects) {
        DefaultMessageHandler(parcelDisableObjects)
    }

    public Unit HandleParcelDivide(ParcelDivide parcelDivide) {
        DefaultMessageHandler(parcelDivide)
    }

    public Unit HandleParcelDwellReply(ParcelDwellReply parcelDwellReply) {
        DefaultMessageHandler(parcelDwellReply)
    }

    public Unit HandleParcelDwellRequest(ParcelDwellRequest parcelDwellRequest) {
        DefaultMessageHandler(parcelDwellRequest)
    }

    public Unit HandleParcelGodForceOwner(ParcelGodForceOwner parcelGodForceOwner) {
        DefaultMessageHandler(parcelGodForceOwner)
    }

    public Unit HandleParcelGodMarkAsContent(ParcelGodMarkAsContent parcelGodMarkAsContent) {
        DefaultMessageHandler(parcelGodMarkAsContent)
    }

    public Unit HandleParcelInfoReply(ParcelInfoReply parcelInfoReply) {
        DefaultMessageHandler(parcelInfoReply)
    }

    public Unit HandleParcelInfoRequest(ParcelInfoRequest parcelInfoRequest) {
        DefaultMessageHandler(parcelInfoRequest)
    }

    public Unit HandleParcelJoin(ParcelJoin parcelJoin) {
        DefaultMessageHandler(parcelJoin)
    }

    public Unit HandleParcelMediaCommandMessage(ParcelMediaCommandMessage parcelMediaCommandMessage) {
        DefaultMessageHandler(parcelMediaCommandMessage)
    }

    public Unit HandleParcelMediaUpdate(ParcelMediaUpdate parcelMediaUpdate) {
        DefaultMessageHandler(parcelMediaUpdate)
    }

    public Unit HandleParcelObjectOwnersReply(ParcelObjectOwnersReply parcelObjectOwnersReply) {
        DefaultMessageHandler(parcelObjectOwnersReply)
    }

    public Unit HandleParcelObjectOwnersRequest(ParcelObjectOwnersRequest parcelObjectOwnersRequest) {
        DefaultMessageHandler(parcelObjectOwnersRequest)
    }

    public Unit HandleParcelOverlay(ParcelOverlay parcelOverlay) {
        DefaultMessageHandler(parcelOverlay)
    }

    public Unit HandleParcelProperties(ParcelProperties parcelProperties) {
        DefaultMessageHandler(parcelProperties)
    }

    public Unit HandleParcelPropertiesRequest(ParcelPropertiesRequest parcelPropertiesRequest) {
        DefaultMessageHandler(parcelPropertiesRequest)
    }

    public Unit HandleParcelPropertiesRequestByID(ParcelPropertiesRequestByID parcelPropertiesRequestByID) {
        DefaultMessageHandler(parcelPropertiesRequestByID)
    }

    public Unit HandleParcelPropertiesUpdate(ParcelPropertiesUpdate parcelPropertiesUpdate) {
        DefaultMessageHandler(parcelPropertiesUpdate)
    }

    public Unit HandleParcelReclaim(ParcelReclaim parcelReclaim) {
        DefaultMessageHandler(parcelReclaim)
    }

    public Unit HandleParcelRelease(ParcelRelease parcelRelease) {
        DefaultMessageHandler(parcelRelease)
    }

    public Unit HandleParcelRename(ParcelRename parcelRename) {
        DefaultMessageHandler(parcelRename)
    }

    public Unit HandleParcelReturnObjects(ParcelReturnObjects parcelReturnObjects) {
        DefaultMessageHandler(parcelReturnObjects)
    }

    public Unit HandleParcelSales(ParcelSales parcelSales) {
        DefaultMessageHandler(parcelSales)
    }

    public Unit HandleParcelSelectObjects(ParcelSelectObjects parcelSelectObjects) {
        DefaultMessageHandler(parcelSelectObjects)
    }

    public Unit HandleParcelSetOtherCleanTime(ParcelSetOtherCleanTime parcelSetOtherCleanTime) {
        DefaultMessageHandler(parcelSetOtherCleanTime)
    }

    public Unit HandlePayPriceReply(PayPriceReply payPriceReply) {
        DefaultMessageHandler(payPriceReply)
    }

    public Unit HandlePickDelete(PickDelete pickDelete) {
        DefaultMessageHandler(pickDelete)
    }

    public Unit HandlePickGodDelete(PickGodDelete pickGodDelete) {
        DefaultMessageHandler(pickGodDelete)
    }

    public Unit HandlePickInfoReply(PickInfoReply pickInfoReply) {
        DefaultMessageHandler(pickInfoReply)
    }

    public Unit HandlePickInfoUpdate(PickInfoUpdate pickInfoUpdate) {
        DefaultMessageHandler(pickInfoUpdate)
    }

    public Unit HandlePlacesQuery(PlacesQuery placesQuery) {
        DefaultMessageHandler(placesQuery)
    }

    public Unit HandlePlacesReply(PlacesReply placesReply) {
        DefaultMessageHandler(placesReply)
    }

    public Unit HandlePreloadSound(PreloadSound preloadSound) {
        DefaultMessageHandler(preloadSound)
    }

    public Unit HandlePurgeInventoryDescendents(PurgeInventoryDescendents purgeInventoryDescendents) {
        DefaultMessageHandler(purgeInventoryDescendents)
    }

    public Unit HandleRebakeAvatarTextures(RebakeAvatarTextures rebakeAvatarTextures) {
        DefaultMessageHandler(rebakeAvatarTextures)
    }

    public Unit HandleRedo(Redo redo) {
        DefaultMessageHandler(redo)
    }

    public Unit HandleRegionHandleRequest(RegionHandleRequest regionHandleRequest) {
        DefaultMessageHandler(regionHandleRequest)
    }

    public Unit HandleRegionHandshake(RegionHandshake regionHandshake) {
        DefaultMessageHandler(regionHandshake)
    }

    public Unit HandleRegionHandshakeReply(RegionHandshakeReply regionHandshakeReply) {
        DefaultMessageHandler(regionHandshakeReply)
    }

    public Unit HandleRegionIDAndHandleReply(RegionIDAndHandleReply regionIDAndHandleReply) {
        DefaultMessageHandler(regionIDAndHandleReply)
    }

    public Unit HandleRegionInfo(RegionInfo regionInfo) {
        DefaultMessageHandler(regionInfo)
    }

    public Unit HandleRegionPresenceRequestByHandle(RegionPresenceRequestByHandle regionPresenceRequestByHandle) {
        DefaultMessageHandler(regionPresenceRequestByHandle)
    }

    public Unit HandleRegionPresenceRequestByRegionID(RegionPresenceRequestByRegionID regionPresenceRequestByRegionID) {
        DefaultMessageHandler(regionPresenceRequestByRegionID)
    }

    public Unit HandleRegionPresenceResponse(RegionPresenceResponse regionPresenceResponse) {
        DefaultMessageHandler(regionPresenceResponse)
    }

    public Unit HandleRemoveAttachment(RemoveAttachment removeAttachment) {
        DefaultMessageHandler(removeAttachment)
    }

    public Unit HandleRemoveInventoryFolder(RemoveInventoryFolder removeInventoryFolder) {
        DefaultMessageHandler(removeInventoryFolder)
    }

    public Unit HandleRemoveInventoryItem(RemoveInventoryItem removeInventoryItem) {
        DefaultMessageHandler(removeInventoryItem)
    }

    public Unit HandleRemoveInventoryObjects(RemoveInventoryObjects removeInventoryObjects) {
        DefaultMessageHandler(removeInventoryObjects)
    }

    public Unit HandleRemoveMuteListEntry(RemoveMuteListEntry removeMuteListEntry) {
        DefaultMessageHandler(removeMuteListEntry)
    }

    public Unit HandleRemoveNameValuePair(RemoveNameValuePair removeNameValuePair) {
        DefaultMessageHandler(removeNameValuePair)
    }

    public Unit HandleRemoveParcel(RemoveParcel removeParcel) {
        DefaultMessageHandler(removeParcel)
    }

    public Unit HandleRemoveTaskInventory(RemoveTaskInventory removeTaskInventory) {
        DefaultMessageHandler(removeTaskInventory)
    }

    public Unit HandleReplyTaskInventory(ReplyTaskInventory replyTaskInventory) {
        DefaultMessageHandler(replyTaskInventory)
    }

    public Unit HandleReportAutosaveCrash(ReportAutosaveCrash reportAutosaveCrash) {
        DefaultMessageHandler(reportAutosaveCrash)
    }

    public Unit HandleRequestGodlikePowers(RequestGodlikePowers requestGodlikePowers) {
        DefaultMessageHandler(requestGodlikePowers)
    }

    public Unit HandleRequestImage(RequestImage requestImage) {
        DefaultMessageHandler(requestImage)
    }

    public Unit HandleRequestInventoryAsset(RequestInventoryAsset requestInventoryAsset) {
        DefaultMessageHandler(requestInventoryAsset)
    }

    public Unit HandleRequestMultipleObjects(RequestMultipleObjects requestMultipleObjects) {
        DefaultMessageHandler(requestMultipleObjects)
    }

    public Unit HandleRequestObjectPropertiesFamily(RequestObjectPropertiesFamily requestObjectPropertiesFamily) {
        DefaultMessageHandler(requestObjectPropertiesFamily)
    }

    public Unit HandleRequestParcelTransfer(RequestParcelTransfer requestParcelTransfer) {
        DefaultMessageHandler(requestParcelTransfer)
    }

    public Unit HandleRequestPayPrice(RequestPayPrice requestPayPrice) {
        DefaultMessageHandler(requestPayPrice)
    }

    public Unit HandleRequestRegionInfo(RequestRegionInfo requestRegionInfo) {
        DefaultMessageHandler(requestRegionInfo)
    }

    public Unit HandleRequestTaskInventory(RequestTaskInventory requestTaskInventory) {
        DefaultMessageHandler(requestTaskInventory)
    }

    public Unit HandleRequestTrustedCircuit(RequestTrustedCircuit requestTrustedCircuit) {
        DefaultMessageHandler(requestTrustedCircuit)
    }

    public Unit HandleRequestXfer(RequestXfer requestXfer) {
        DefaultMessageHandler(requestXfer)
    }

    public Unit HandleRetrieveInstantMessages(RetrieveInstantMessages retrieveInstantMessages) {
        DefaultMessageHandler(retrieveInstantMessages)
    }

    public Unit HandleRevokePermissions(RevokePermissions revokePermissions) {
        DefaultMessageHandler(revokePermissions)
    }

    public Unit HandleRezMultipleAttachmentsFromInv(RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv) {
        DefaultMessageHandler(rezMultipleAttachmentsFromInv)
    }

    public Unit HandleRezObject(RezObject rezObject) {
        DefaultMessageHandler(rezObject)
    }

    public Unit HandleRezObjectFromNotecard(RezObjectFromNotecard rezObjectFromNotecard) {
        DefaultMessageHandler(rezObjectFromNotecard)
    }

    public Unit HandleRezRestoreToWorld(RezRestoreToWorld rezRestoreToWorld) {
        DefaultMessageHandler(rezRestoreToWorld)
    }

    public Unit HandleRezScript(RezScript rezScript) {
        DefaultMessageHandler(rezScript)
    }

    public Unit HandleRezSingleAttachmentFromInv(RezSingleAttachmentFromInv rezSingleAttachmentFromInv) {
        DefaultMessageHandler(rezSingleAttachmentFromInv)
    }

    public Unit HandleRoutedMoneyBalanceReply(RoutedMoneyBalanceReply routedMoneyBalanceReply) {
        DefaultMessageHandler(routedMoneyBalanceReply)
    }

    public Unit HandleRpcChannelReply(RpcChannelReply rpcChannelReply) {
        DefaultMessageHandler(rpcChannelReply)
    }

    public Unit HandleRpcChannelRequest(RpcChannelRequest rpcChannelRequest) {
        DefaultMessageHandler(rpcChannelRequest)
    }

    public Unit HandleRpcScriptReplyInbound(RpcScriptReplyInbound rpcScriptReplyInbound) {
        DefaultMessageHandler(rpcScriptReplyInbound)
    }

    public Unit HandleRpcScriptRequestInbound(RpcScriptRequestInbound rpcScriptRequestInbound) {
        DefaultMessageHandler(rpcScriptRequestInbound)
    }

    public Unit HandleRpcScriptRequestInboundForward(RpcScriptRequestInboundForward rpcScriptRequestInboundForward) {
        DefaultMessageHandler(rpcScriptRequestInboundForward)
    }

    public Unit HandleSaveAssetIntoInventory(SaveAssetIntoInventory saveAssetIntoInventory) {
        DefaultMessageHandler(saveAssetIntoInventory)
    }

    public Unit HandleScriptAnswerYes(ScriptAnswerYes scriptAnswerYes) {
        DefaultMessageHandler(scriptAnswerYes)
    }

    public Unit HandleScriptControlChange(ScriptControlChange scriptControlChange) {
        DefaultMessageHandler(scriptControlChange)
    }

    public Unit HandleScriptDataReply(ScriptDataReply scriptDataReply) {
        DefaultMessageHandler(scriptDataReply)
    }

    public Unit HandleScriptDataRequest(ScriptDataRequest scriptDataRequest) {
        DefaultMessageHandler(scriptDataRequest)
    }

    public Unit HandleScriptDialog(ScriptDialog scriptDialog) {
        DefaultMessageHandler(scriptDialog)
    }

    public Unit HandleScriptDialogReply(ScriptDialogReply scriptDialogReply) {
        DefaultMessageHandler(scriptDialogReply)
    }

    public Unit HandleScriptMailRegistration(ScriptMailRegistration scriptMailRegistration) {
        DefaultMessageHandler(scriptMailRegistration)
    }

    public Unit HandleScriptQuestion(ScriptQuestion scriptQuestion) {
        DefaultMessageHandler(scriptQuestion)
    }

    public Unit HandleScriptReset(ScriptReset scriptReset) {
        DefaultMessageHandler(scriptReset)
    }

    public Unit HandleScriptRunningReply(ScriptRunningReply scriptRunningReply) {
        DefaultMessageHandler(scriptRunningReply)
    }

    public Unit HandleScriptSensorReply(ScriptSensorReply scriptSensorReply) {
        DefaultMessageHandler(scriptSensorReply)
    }

    public Unit HandleScriptSensorRequest(ScriptSensorRequest scriptSensorRequest) {
        DefaultMessageHandler(scriptSensorRequest)
    }

    public Unit HandleScriptTeleportRequest(ScriptTeleportRequest scriptTeleportRequest) {
        DefaultMessageHandler(scriptTeleportRequest)
    }

    public Unit HandleSendPostcard(SendPostcard sendPostcard) {
        DefaultMessageHandler(sendPostcard)
    }

    public Unit HandleSendXferPacket(SendXferPacket sendXferPacket) {
        DefaultMessageHandler(sendXferPacket)
    }

    public Unit HandleSetAlwaysRun(SetAlwaysRun setAlwaysRun) {
        DefaultMessageHandler(setAlwaysRun)
    }

    public Unit HandleSetCPURatio(SetCPURatio setCPURatio) {
        DefaultMessageHandler(setCPURatio)
    }

    public Unit HandleSetFollowCamProperties(SetFollowCamProperties setFollowCamProperties) {
        DefaultMessageHandler(setFollowCamProperties)
    }

    public Unit HandleSetGroupAcceptNotices(SetGroupAcceptNotices setGroupAcceptNotices) {
        DefaultMessageHandler(setGroupAcceptNotices)
    }

    public Unit HandleSetGroupContribution(SetGroupContribution setGroupContribution) {
        DefaultMessageHandler(setGroupContribution)
    }

    public Unit HandleSetScriptRunning(SetScriptRunning setScriptRunning) {
        DefaultMessageHandler(setScriptRunning)
    }

    public Unit HandleSetSimPresenceInDatabase(SetSimPresenceInDatabase setSimPresenceInDatabase) {
        DefaultMessageHandler(setSimPresenceInDatabase)
    }

    public Unit HandleSetSimStatusInDatabase(SetSimStatusInDatabase setSimStatusInDatabase) {
        DefaultMessageHandler(setSimStatusInDatabase)
    }

    public Unit HandleSetStartLocation(SetStartLocation setStartLocation) {
        DefaultMessageHandler(setStartLocation)
    }

    public Unit HandleSetStartLocationRequest(SetStartLocationRequest setStartLocationRequest) {
        DefaultMessageHandler(setStartLocationRequest)
    }

    public Unit HandleSimCrashed(SimCrashed simCrashed) {
        DefaultMessageHandler(simCrashed)
    }

    public Unit HandleSimStats(SimStats simStats) {
        DefaultMessageHandler(simStats)
    }

    public Unit HandleSimStatus(SimStatus simStatus) {
        DefaultMessageHandler(simStatus)
    }

    public Unit HandleSimWideDeletes(SimWideDeletes simWideDeletes) {
        DefaultMessageHandler(simWideDeletes)
    }

    public Unit HandleSimulatorLoad(SimulatorLoad simulatorLoad) {
        DefaultMessageHandler(simulatorLoad)
    }

    public Unit HandleSimulatorMapUpdate(SimulatorMapUpdate simulatorMapUpdate) {
        DefaultMessageHandler(simulatorMapUpdate)
    }

    public Unit HandleSimulatorPresentAtLocation(SimulatorPresentAtLocation simulatorPresentAtLocation) {
        DefaultMessageHandler(simulatorPresentAtLocation)
    }

    public Unit HandleSimulatorReady(SimulatorReady simulatorReady) {
        DefaultMessageHandler(simulatorReady)
    }

    public Unit HandleSimulatorSetMap(SimulatorSetMap simulatorSetMap) {
        DefaultMessageHandler(simulatorSetMap)
    }

    public Unit HandleSimulatorShutdownRequest(SimulatorShutdownRequest simulatorShutdownRequest) {
        DefaultMessageHandler(simulatorShutdownRequest)
    }

    public Unit HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        DefaultMessageHandler(simulatorViewerTimeMessage)
    }

    public Unit HandleSoundTrigger(SoundTrigger soundTrigger) {
        DefaultMessageHandler(soundTrigger)
    }

    public Unit HandleStartAuction(StartAuction startAuction) {
        DefaultMessageHandler(startAuction)
    }

    public Unit HandleStartGroupProposal(StartGroupProposal startGroupProposal) {
        DefaultMessageHandler(startGroupProposal)
    }

    public Unit HandleStartLure(StartLure startLure) {
        DefaultMessageHandler(startLure)
    }

    public Unit HandleStartPingCheck(StartPingCheck startPingCheck) {
        DefaultMessageHandler(startPingCheck)
    }

    public Unit HandleStateSave(StateSave stateSave) {
        DefaultMessageHandler(stateSave)
    }

    public Unit HandleSubscribeLoad(SubscribeLoad subscribeLoad) {
        DefaultMessageHandler(subscribeLoad)
    }

    public Unit HandleSystemKickUser(SystemKickUser systemKickUser) {
        DefaultMessageHandler(systemKickUser)
    }

    public Unit HandleSystemMessage(SystemMessage systemMessage) {
        DefaultMessageHandler(systemMessage)
    }

    public Unit HandleTallyVotes(TallyVotes tallyVotes) {
        DefaultMessageHandler(tallyVotes)
    }

    public Unit HandleTelehubInfo(TelehubInfo telehubInfo) {
        DefaultMessageHandler(telehubInfo)
    }

    public Unit HandleTeleportCancel(TeleportCancel teleportCancel) {
        DefaultMessageHandler(teleportCancel)
    }

    public Unit HandleTeleportFailed(TeleportFailed teleportFailed) {
        DefaultMessageHandler(teleportFailed)
    }

    public Unit HandleTeleportFinish(TeleportFinish teleportFinish) {
        DefaultMessageHandler(teleportFinish)
    }

    public Unit HandleTeleportLandingStatusChanged(TeleportLandingStatusChanged teleportLandingStatusChanged) {
        DefaultMessageHandler(teleportLandingStatusChanged)
    }

    public Unit HandleTeleportLandmarkRequest(TeleportLandmarkRequest teleportLandmarkRequest) {
        DefaultMessageHandler(teleportLandmarkRequest)
    }

    public Unit HandleTeleportLocal(TeleportLocal teleportLocal) {
        DefaultMessageHandler(teleportLocal)
    }

    public Unit HandleTeleportLocationRequest(TeleportLocationRequest teleportLocationRequest) {
        DefaultMessageHandler(teleportLocationRequest)
    }

    public Unit HandleTeleportLureRequest(TeleportLureRequest teleportLureRequest) {
        DefaultMessageHandler(teleportLureRequest)
    }

    public Unit HandleTeleportProgress(TeleportProgress teleportProgress) {
        DefaultMessageHandler(teleportProgress)
    }

    public Unit HandleTeleportRequest(TeleportRequest teleportRequest) {
        DefaultMessageHandler(teleportRequest)
    }

    public Unit HandleTeleportStart(TeleportStart teleportStart) {
        DefaultMessageHandler(teleportStart)
    }

    public Unit HandleTerminateFriendship(TerminateFriendship terminateFriendship) {
        DefaultMessageHandler(terminateFriendship)
    }

    public Unit HandleTestMessage(TestMessage testMessage) {
        DefaultMessageHandler(testMessage)
    }

    public Unit HandleTrackAgent(TrackAgent trackAgent) {
        DefaultMessageHandler(trackAgent)
    }

    public Unit HandleTransferAbort(TransferAbort transferAbort) {
        DefaultMessageHandler(transferAbort)
    }

    public Unit HandleTransferInfo(TransferInfo transferInfo) {
        DefaultMessageHandler(transferInfo)
    }

    public Unit HandleTransferInventory(TransferInventory transferInventory) {
        DefaultMessageHandler(transferInventory)
    }

    public Unit HandleTransferInventoryAck(TransferInventoryAck transferInventoryAck) {
        DefaultMessageHandler(transferInventoryAck)
    }

    public Unit HandleTransferPacket(TransferPacket transferPacket) {
        DefaultMessageHandler(transferPacket)
    }

    public Unit HandleTransferRequest(TransferRequest transferRequest) {
        DefaultMessageHandler(transferRequest)
    }

    public Unit HandleUUIDGroupNameReply(UUIDGroupNameReply uUIDGroupNameReply) {
        DefaultMessageHandler(uUIDGroupNameReply)
    }

    public Unit HandleUUIDGroupNameRequest(UUIDGroupNameRequest uUIDGroupNameRequest) {
        DefaultMessageHandler(uUIDGroupNameRequest)
    }

    public Unit HandleUUIDNameReply(UUIDNameReply uUIDNameReply) {
        DefaultMessageHandler(uUIDNameReply)
    }

    public Unit HandleUUIDNameRequest(UUIDNameRequest uUIDNameRequest) {
        DefaultMessageHandler(uUIDNameRequest)
    }

    public Unit HandleUndo(Undo undo) {
        DefaultMessageHandler(undo)
    }

    public Unit HandleUndoLand(UndoLand undoLand) {
        DefaultMessageHandler(undoLand)
    }

    public Unit HandleUnsubscribeLoad(UnsubscribeLoad unsubscribeLoad) {
        DefaultMessageHandler(unsubscribeLoad)
    }

    public Unit HandleUpdateAttachment(UpdateAttachment updateAttachment) {
        DefaultMessageHandler(updateAttachment)
    }

    public Unit HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updateCreateInventoryItem) {
        DefaultMessageHandler(updateCreateInventoryItem)
    }

    public Unit HandleUpdateGroupInfo(UpdateGroupInfo updateGroupInfo) {
        DefaultMessageHandler(updateGroupInfo)
    }

    public Unit HandleUpdateInventoryFolder(UpdateInventoryFolder updateInventoryFolder) {
        DefaultMessageHandler(updateInventoryFolder)
    }

    public Unit HandleUpdateInventoryItem(UpdateInventoryItem updateInventoryItem) {
        DefaultMessageHandler(updateInventoryItem)
    }

    public Unit HandleUpdateMuteListEntry(UpdateMuteListEntry updateMuteListEntry) {
        DefaultMessageHandler(updateMuteListEntry)
    }

    public Unit HandleUpdateParcel(UpdateParcel updateParcel) {
        DefaultMessageHandler(updateParcel)
    }

    public Unit HandleUpdateSimulator(UpdateSimulator updateSimulator) {
        DefaultMessageHandler(updateSimulator)
    }

    public Unit HandleUpdateTaskInventory(UpdateTaskInventory updateTaskInventory) {
        DefaultMessageHandler(updateTaskInventory)
    }

    public Unit HandleUpdateUserInfo(UpdateUserInfo updateUserInfo) {
        DefaultMessageHandler(updateUserInfo)
    }

    public Unit HandleUseCachedMuteList(UseCachedMuteList useCachedMuteList) {
        DefaultMessageHandler(useCachedMuteList)
    }

    public Unit HandleUseCircuitCode(UseCircuitCode useCircuitCode) {
        DefaultMessageHandler(useCircuitCode)
    }

    public Unit HandleUserInfoReply(UserInfoReply userInfoReply) {
        DefaultMessageHandler(userInfoReply)
    }

    public Unit HandleUserInfoRequest(UserInfoRequest userInfoRequest) {
        DefaultMessageHandler(userInfoRequest)
    }

    public Unit HandleUserReport(UserReport userReport) {
        DefaultMessageHandler(userReport)
    }

    public Unit HandleUserReportInternal(UserReportInternal userReportInternal) {
        DefaultMessageHandler(userReportInternal)
    }

    public Unit HandleVelocityInterpolateOff(VelocityInterpolateOff velocityInterpolateOff) {
        DefaultMessageHandler(velocityInterpolateOff)
    }

    public Unit HandleVelocityInterpolateOn(VelocityInterpolateOn velocityInterpolateOn) {
        DefaultMessageHandler(velocityInterpolateOn)
    }

    public Unit HandleViewerEffect(ViewerEffect viewerEffect) {
        DefaultMessageHandler(viewerEffect)
    }

    public Unit HandleViewerFrozenMessage(ViewerFrozenMessage viewerFrozenMessage) {
        DefaultMessageHandler(viewerFrozenMessage)
    }

    public Unit HandleViewerStartAuction(ViewerStartAuction viewerStartAuction) {
        DefaultMessageHandler(viewerStartAuction)
    }

    public Unit HandleViewerStats(ViewerStats viewerStats) {
        DefaultMessageHandler(viewerStats)
    }
}
