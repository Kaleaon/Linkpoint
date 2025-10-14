package com.lumiyaviewer.lumiya.slproto.messages
import java.util.*

import com.lumiyaviewer.lumiya.slproto.SLMessage

class SLMessageHandler {
    Unit DefaultMessageHandler(SLMessage sLMessage) {
    }

    Unit HandleAbortXfer(AbortXfer abortXfer) {
        DefaultMessageHandler(abortXfer)
    }

    Unit HandleAcceptCallingCard(AcceptCallingCard acceptCallingCard) {
        DefaultMessageHandler(acceptCallingCard)
    }

    Unit HandleAcceptFriendship(AcceptFriendship acceptFriendship) {
        DefaultMessageHandler(acceptFriendship)
    }

    Unit HandleActivateGestures(ActivateGestures activateGestures) {
        DefaultMessageHandler(activateGestures)
    }

    Unit HandleActivateGroup(ActivateGroup activateGroup) {
        DefaultMessageHandler(activateGroup)
    }

    Unit HandleAddCircuitCode(AddCircuitCode addCircuitCode) {
        DefaultMessageHandler(addCircuitCode)
    }

    Unit HandleAgentAlertMessage(AgentAlertMessage agentAlertMessage) {
        DefaultMessageHandler(agentAlertMessage)
    }

    Unit HandleAgentAnimation(AgentAnimation agentAnimation) {
        DefaultMessageHandler(agentAnimation)
    }

    Unit HandleAgentCachedTexture(AgentCachedTexture agentCachedTexture) {
        DefaultMessageHandler(agentCachedTexture)
    }

    Unit HandleAgentCachedTextureResponse(AgentCachedTextureResponse agentCachedTextureResponse) {
        DefaultMessageHandler(agentCachedTextureResponse)
    }

    Unit HandleAgentDataUpdate(AgentDataUpdate agentDataUpdate) {
        DefaultMessageHandler(agentDataUpdate)
    }

    Unit HandleAgentDataUpdateRequest(AgentDataUpdateRequest agentDataUpdateRequest) {
        DefaultMessageHandler(agentDataUpdateRequest)
    }

    Unit HandleAgentDropGroup(AgentDropGroup agentDropGroup) {
        DefaultMessageHandler(agentDropGroup)
    }

    Unit HandleAgentFOV(AgentFOV agentFOV) {
        DefaultMessageHandler(agentFOV)
    }

    Unit HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentGroupDataUpdate) {
        DefaultMessageHandler(agentGroupDataUpdate)
    }

    Unit HandleAgentHeightWidth(AgentHeightWidth agentHeightWidth) {
        DefaultMessageHandler(agentHeightWidth)
    }

    Unit HandleAgentIsNowWearing(AgentIsNowWearing agentIsNowWearing) {
        DefaultMessageHandler(agentIsNowWearing)
    }

    Unit HandleAgentMovementComplete(AgentMovementComplete agentMovementComplete) {
        DefaultMessageHandler(agentMovementComplete)
    }

    Unit HandleAgentPause(AgentPause agentPause) {
        DefaultMessageHandler(agentPause)
    }

    Unit HandleAgentQuitCopy(AgentQuitCopy agentQuitCopy) {
        DefaultMessageHandler(agentQuitCopy)
    }

    Unit HandleAgentRequestSit(AgentRequestSit agentRequestSit) {
        DefaultMessageHandler(agentRequestSit)
    }

    Unit HandleAgentResume(AgentResume agentResume) {
        DefaultMessageHandler(agentResume)
    }

    Unit HandleAgentSetAppearance(AgentSetAppearance agentSetAppearance) {
        DefaultMessageHandler(agentSetAppearance)
    }

    Unit HandleAgentSit(AgentSit agentSit) {
        DefaultMessageHandler(agentSit)
    }

    Unit HandleAgentThrottle(AgentThrottle agentThrottle) {
        DefaultMessageHandler(agentThrottle)
    }

    Unit HandleAgentUpdate(AgentUpdate agentUpdate) {
        DefaultMessageHandler(agentUpdate)
    }

    Unit HandleAgentWearablesRequest(AgentWearablesRequest agentWearablesRequest) {
        DefaultMessageHandler(agentWearablesRequest)
    }

    Unit HandleAgentWearablesUpdate(AgentWearablesUpdate agentWearablesUpdate) {
        DefaultMessageHandler(agentWearablesUpdate)
    }

    Unit HandleAlertMessage(AlertMessage alertMessage) {
        DefaultMessageHandler(alertMessage)
    }

    Unit HandleAssetUploadComplete(AssetUploadComplete assetUploadComplete) {
        DefaultMessageHandler(assetUploadComplete)
    }

    Unit HandleAssetUploadRequest(AssetUploadRequest assetUploadRequest) {
        DefaultMessageHandler(assetUploadRequest)
    }

    Unit HandleAtomicPassObject(AtomicPassObject atomicPassObject) {
        DefaultMessageHandler(atomicPassObject)
    }

    Unit HandleAttachedSound(AttachedSound attachedSound) {
        DefaultMessageHandler(attachedSound)
    }

    Unit HandleAttachedSoundGainChange(AttachedSoundGainChange attachedSoundGainChange) {
        DefaultMessageHandler(attachedSoundGainChange)
    }

    Unit HandleAvatarAnimation(AvatarAnimation avatarAnimation) {
        DefaultMessageHandler(avatarAnimation)
    }

    Unit HandleAvatarAppearance(AvatarAppearance avatarAppearance) {
        DefaultMessageHandler(avatarAppearance)
    }

    Unit HandleAvatarClassifiedReply(AvatarClassifiedReply avatarClassifiedReply) {
        DefaultMessageHandler(avatarClassifiedReply)
    }

    Unit HandleAvatarGroupsReply(AvatarGroupsReply avatarGroupsReply) {
        DefaultMessageHandler(avatarGroupsReply)
    }

    Unit HandleAvatarInterestsReply(AvatarInterestsReply avatarInterestsReply) {
        DefaultMessageHandler(avatarInterestsReply)
    }

    Unit HandleAvatarInterestsUpdate(AvatarInterestsUpdate avatarInterestsUpdate) {
        DefaultMessageHandler(avatarInterestsUpdate)
    }

    Unit HandleAvatarNotesReply(AvatarNotesReply avatarNotesReply) {
        DefaultMessageHandler(avatarNotesReply)
    }

    Unit HandleAvatarNotesUpdate(AvatarNotesUpdate avatarNotesUpdate) {
        DefaultMessageHandler(avatarNotesUpdate)
    }

    Unit HandleAvatarPickerReply(AvatarPickerReply avatarPickerReply) {
        DefaultMessageHandler(avatarPickerReply)
    }

    Unit HandleAvatarPickerRequest(AvatarPickerRequest avatarPickerRequest) {
        DefaultMessageHandler(avatarPickerRequest)
    }

    Unit HandleAvatarPickerRequestBackend(AvatarPickerRequestBackend avatarPickerRequestBackend) {
        DefaultMessageHandler(avatarPickerRequestBackend)
    }

    Unit HandleAvatarPicksReply(AvatarPicksReply avatarPicksReply) {
        DefaultMessageHandler(avatarPicksReply)
    }

    Unit HandleAvatarPropertiesReply(AvatarPropertiesReply avatarPropertiesReply) {
        DefaultMessageHandler(avatarPropertiesReply)
    }

    Unit HandleAvatarPropertiesRequest(AvatarPropertiesRequest avatarPropertiesRequest) {
        DefaultMessageHandler(avatarPropertiesRequest)
    }

    Unit HandleAvatarPropertiesRequestBackend(AvatarPropertiesRequestBackend avatarPropertiesRequestBackend) {
        DefaultMessageHandler(avatarPropertiesRequestBackend)
    }

    Unit HandleAvatarPropertiesUpdate(AvatarPropertiesUpdate avatarPropertiesUpdate) {
        DefaultMessageHandler(avatarPropertiesUpdate)
    }

    Unit HandleAvatarSitResponse(AvatarSitResponse avatarSitResponse) {
        DefaultMessageHandler(avatarSitResponse)
    }

    Unit HandleAvatarTextureUpdate(AvatarTextureUpdate avatarTextureUpdate) {
        DefaultMessageHandler(avatarTextureUpdate)
    }

    Unit HandleBulkUpdateInventory(BulkUpdateInventory bulkUpdateInventory) {
        DefaultMessageHandler(bulkUpdateInventory)
    }

    Unit HandleBuyObjectInventory(BuyObjectInventory buyObjectInventory) {
        DefaultMessageHandler(buyObjectInventory)
    }

    Unit HandleCameraConstraint(CameraConstraint cameraConstraint) {
        DefaultMessageHandler(cameraConstraint)
    }

    Unit HandleCancelAuction(CancelAuction cancelAuction) {
        DefaultMessageHandler(cancelAuction)
    }

    Unit HandleChangeInventoryItemFlags(ChangeInventoryItemFlags changeInventoryItemFlags) {
        DefaultMessageHandler(changeInventoryItemFlags)
    }

    Unit HandleChangeUserRights(ChangeUserRights changeUserRights) {
        DefaultMessageHandler(changeUserRights)
    }

    Unit HandleChatFromSimulator(ChatFromSimulator chatFromSimulator) {
        DefaultMessageHandler(chatFromSimulator)
    }

    Unit HandleChatFromViewer(ChatFromViewer chatFromViewer) {
        DefaultMessageHandler(chatFromViewer)
    }

    Unit HandleChatPass(ChatPass chatPass) {
        DefaultMessageHandler(chatPass)
    }

    Unit HandleCheckParcelAuctions(CheckParcelAuctions checkParcelAuctions) {
        DefaultMessageHandler(checkParcelAuctions)
    }

    Unit HandleCheckParcelSales(CheckParcelSales checkParcelSales) {
        DefaultMessageHandler(checkParcelSales)
    }

    Unit HandleChildAgentAlive(ChildAgentAlive childAgentAlive) {
        DefaultMessageHandler(childAgentAlive)
    }

    Unit HandleChildAgentDying(ChildAgentDying childAgentDying) {
        DefaultMessageHandler(childAgentDying)
    }

    Unit HandleChildAgentPositionUpdate(ChildAgentPositionUpdate childAgentPositionUpdate) {
        DefaultMessageHandler(childAgentPositionUpdate)
    }

    Unit HandleChildAgentUnknown(ChildAgentUnknown childAgentUnknown) {
        DefaultMessageHandler(childAgentUnknown)
    }

    Unit HandleChildAgentUpdate(ChildAgentUpdate childAgentUpdate) {
        DefaultMessageHandler(childAgentUpdate)
    }

    Unit HandleClassifiedDelete(ClassifiedDelete classifiedDelete) {
        DefaultMessageHandler(classifiedDelete)
    }

    Unit HandleClassifiedGodDelete(ClassifiedGodDelete classifiedGodDelete) {
        DefaultMessageHandler(classifiedGodDelete)
    }

    Unit HandleClassifiedInfoReply(ClassifiedInfoReply classifiedInfoReply) {
        DefaultMessageHandler(classifiedInfoReply)
    }

    Unit HandleClassifiedInfoRequest(ClassifiedInfoRequest classifiedInfoRequest) {
        DefaultMessageHandler(classifiedInfoRequest)
    }

    Unit HandleClassifiedInfoUpdate(ClassifiedInfoUpdate classifiedInfoUpdate) {
        DefaultMessageHandler(classifiedInfoUpdate)
    }

    Unit HandleClearFollowCamProperties(ClearFollowCamProperties clearFollowCamProperties) {
        DefaultMessageHandler(clearFollowCamProperties)
    }

    Unit HandleCloseCircuit(CloseCircuit closeCircuit) {
        DefaultMessageHandler(closeCircuit)
    }

    Unit HandleCoarseLocationUpdate(CoarseLocationUpdate coarseLocationUpdate) {
        DefaultMessageHandler(coarseLocationUpdate)
    }

    Unit HandleCompleteAgentMovement(CompleteAgentMovement completeAgentMovement) {
        DefaultMessageHandler(completeAgentMovement)
    }

    Unit HandleCompleteAuction(CompleteAuction completeAuction) {
        DefaultMessageHandler(completeAuction)
    }

    Unit HandleCompletePingCheck(CompletePingCheck completePingCheck) {
        DefaultMessageHandler(completePingCheck)
    }

    Unit HandleConfirmAuctionStart(ConfirmAuctionStart confirmAuctionStart) {
        DefaultMessageHandler(confirmAuctionStart)
    }

    Unit HandleConfirmEnableSimulator(ConfirmEnableSimulator confirmEnableSimulator) {
        DefaultMessageHandler(confirmEnableSimulator)
    }

    Unit HandleConfirmXferPacket(ConfirmXferPacket confirmXferPacket) {
        DefaultMessageHandler(confirmXferPacket)
    }

    Unit HandleCopyInventoryFromNotecard(CopyInventoryFromNotecard copyInventoryFromNotecard) {
        DefaultMessageHandler(copyInventoryFromNotecard)
    }

    Unit HandleCopyInventoryItem(CopyInventoryItem copyInventoryItem) {
        DefaultMessageHandler(copyInventoryItem)
    }

    Unit HandleCreateGroupReply(CreateGroupReply createGroupReply) {
        DefaultMessageHandler(createGroupReply)
    }

    Unit HandleCreateGroupRequest(CreateGroupRequest createGroupRequest) {
        DefaultMessageHandler(createGroupRequest)
    }

    Unit HandleCreateInventoryFolder(CreateInventoryFolder createInventoryFolder) {
        DefaultMessageHandler(createInventoryFolder)
    }

    Unit HandleCreateInventoryItem(CreateInventoryItem createInventoryItem) {
        DefaultMessageHandler(createInventoryItem)
    }

    Unit HandleCreateLandmarkForEvent(CreateLandmarkForEvent createLandmarkForEvent) {
        DefaultMessageHandler(createLandmarkForEvent)
    }

    Unit HandleCreateNewOutfitAttachments(CreateNewOutfitAttachments createNewOutfitAttachments) {
        DefaultMessageHandler(createNewOutfitAttachments)
    }

    Unit HandleCreateTrustedCircuit(CreateTrustedCircuit createTrustedCircuit) {
        DefaultMessageHandler(createTrustedCircuit)
    }

    Unit HandleCrossedRegion(CrossedRegion crossedRegion) {
        DefaultMessageHandler(crossedRegion)
    }

    Unit HandleDataHomeLocationReply(DataHomeLocationReply dataHomeLocationReply) {
        DefaultMessageHandler(dataHomeLocationReply)
    }

    Unit HandleDataHomeLocationRequest(DataHomeLocationRequest dataHomeLocationRequest) {
        DefaultMessageHandler(dataHomeLocationRequest)
    }

    Unit HandleDataServerLogout(DataServerLogout dataServerLogout) {
        DefaultMessageHandler(dataServerLogout)
    }

    Unit HandleDeRezAck(DeRezAck deRezAck) {
        DefaultMessageHandler(deRezAck)
    }

    Unit HandleDeRezObject(DeRezObject deRezObject) {
        DefaultMessageHandler(deRezObject)
    }

    Unit HandleDeactivateGestures(DeactivateGestures deactivateGestures) {
        DefaultMessageHandler(deactivateGestures)
    }

    Unit HandleDeclineCallingCard(DeclineCallingCard declineCallingCard) {
        DefaultMessageHandler(declineCallingCard)
    }

    Unit HandleDeclineFriendship(DeclineFriendship declineFriendship) {
        DefaultMessageHandler(declineFriendship)
    }

    Unit HandleDenyTrustedCircuit(DenyTrustedCircuit denyTrustedCircuit) {
        DefaultMessageHandler(denyTrustedCircuit)
    }

    Unit HandleDerezContainer(DerezContainer derezContainer) {
        DefaultMessageHandler(derezContainer)
    }

    Unit HandleDetachAttachmentIntoInv(DetachAttachmentIntoInv detachAttachmentIntoInv) {
        DefaultMessageHandler(detachAttachmentIntoInv)
    }

    Unit HandleDirClassifiedQuery(DirClassifiedQuery dirClassifiedQuery) {
        DefaultMessageHandler(dirClassifiedQuery)
    }

    Unit HandleDirClassifiedQueryBackend(DirClassifiedQueryBackend dirClassifiedQueryBackend) {
        DefaultMessageHandler(dirClassifiedQueryBackend)
    }

    Unit HandleDirClassifiedReply(DirClassifiedReply dirClassifiedReply) {
        DefaultMessageHandler(dirClassifiedReply)
    }

    Unit HandleDirEventsReply(DirEventsReply dirEventsReply) {
        DefaultMessageHandler(dirEventsReply)
    }

    Unit HandleDirFindQuery(DirFindQuery dirFindQuery) {
        DefaultMessageHandler(dirFindQuery)
    }

    Unit HandleDirFindQueryBackend(DirFindQueryBackend dirFindQueryBackend) {
        DefaultMessageHandler(dirFindQueryBackend)
    }

    Unit HandleDirGroupsReply(DirGroupsReply dirGroupsReply) {
        DefaultMessageHandler(dirGroupsReply)
    }

    Unit HandleDirLandQuery(DirLandQuery dirLandQuery) {
        DefaultMessageHandler(dirLandQuery)
    }

    Unit HandleDirLandQueryBackend(DirLandQueryBackend dirLandQueryBackend) {
        DefaultMessageHandler(dirLandQueryBackend)
    }

    Unit HandleDirLandReply(DirLandReply dirLandReply) {
        DefaultMessageHandler(dirLandReply)
    }

    Unit HandleDirPeopleReply(DirPeopleReply dirPeopleReply) {
        DefaultMessageHandler(dirPeopleReply)
    }

    Unit HandleDirPlacesQuery(DirPlacesQuery dirPlacesQuery) {
        DefaultMessageHandler(dirPlacesQuery)
    }

    Unit HandleDirPlacesQueryBackend(DirPlacesQueryBackend dirPlacesQueryBackend) {
        DefaultMessageHandler(dirPlacesQueryBackend)
    }

    Unit HandleDirPlacesReply(DirPlacesReply dirPlacesReply) {
        DefaultMessageHandler(dirPlacesReply)
    }

    Unit HandleDirPopularQuery(DirPopularQuery dirPopularQuery) {
        DefaultMessageHandler(dirPopularQuery)
    }

    Unit HandleDirPopularQueryBackend(DirPopularQueryBackend dirPopularQueryBackend) {
        DefaultMessageHandler(dirPopularQueryBackend)
    }

    Unit HandleDirPopularReply(DirPopularReply dirPopularReply) {
        DefaultMessageHandler(dirPopularReply)
    }

    Unit HandleDisableSimulator(DisableSimulator disableSimulator) {
        DefaultMessageHandler(disableSimulator)
    }

    Unit HandleEconomyData(EconomyData economyData) {
        DefaultMessageHandler(economyData)
    }

    Unit HandleEconomyDataRequest(EconomyDataRequest economyDataRequest) {
        DefaultMessageHandler(economyDataRequest)
    }

    Unit HandleEdgeDataPacket(EdgeDataPacket edgeDataPacket) {
        DefaultMessageHandler(edgeDataPacket)
    }

    Unit HandleEjectGroupMemberReply(EjectGroupMemberReply ejectGroupMemberReply) {
        DefaultMessageHandler(ejectGroupMemberReply)
    }

    Unit HandleEjectGroupMemberRequest(EjectGroupMemberRequest ejectGroupMemberRequest) {
        DefaultMessageHandler(ejectGroupMemberRequest)
    }

    Unit HandleEjectUser(EjectUser ejectUser) {
        DefaultMessageHandler(ejectUser)
    }

    Unit HandleEmailMessageReply(EmailMessageReply emailMessageReply) {
        DefaultMessageHandler(emailMessageReply)
    }

    Unit HandleEmailMessageRequest(EmailMessageRequest emailMessageRequest) {
        DefaultMessageHandler(emailMessageRequest)
    }

    Unit HandleEnableSimulator(EnableSimulator enableSimulator) {
        DefaultMessageHandler(enableSimulator)
    }

    Unit HandleError(Error error) {
        DefaultMessageHandler(error)
    }

    Unit HandleEstateCovenantReply(EstateCovenantReply estateCovenantReply) {
        DefaultMessageHandler(estateCovenantReply)
    }

    Unit HandleEstateCovenantRequest(EstateCovenantRequest estateCovenantRequest) {
        DefaultMessageHandler(estateCovenantRequest)
    }

    Unit HandleEstateOwnerMessage(EstateOwnerMessage estateOwnerMessage) {
        DefaultMessageHandler(estateOwnerMessage)
    }

    Unit HandleEventGodDelete(EventGodDelete eventGodDelete) {
        DefaultMessageHandler(eventGodDelete)
    }

    Unit HandleEventInfoReply(EventInfoReply eventInfoReply) {
        DefaultMessageHandler(eventInfoReply)
    }

    Unit HandleEventInfoRequest(EventInfoRequest eventInfoRequest) {
        DefaultMessageHandler(eventInfoRequest)
    }

    Unit HandleEventLocationReply(EventLocationReply eventLocationReply) {
        DefaultMessageHandler(eventLocationReply)
    }

    Unit HandleEventLocationRequest(EventLocationRequest eventLocationRequest) {
        DefaultMessageHandler(eventLocationRequest)
    }

    Unit HandleEventNotificationAddRequest(EventNotificationAddRequest eventNotificationAddRequest) {
        DefaultMessageHandler(eventNotificationAddRequest)
    }

    Unit HandleEventNotificationRemoveRequest(EventNotificationRemoveRequest eventNotificationRemoveRequest) {
        DefaultMessageHandler(eventNotificationRemoveRequest)
    }

    Unit HandleFeatureDisabled(FeatureDisabled featureDisabled) {
        DefaultMessageHandler(featureDisabled)
    }

    Unit HandleFetchInventory(FetchInventory fetchInventory) {
        DefaultMessageHandler(fetchInventory)
    }

    Unit HandleFetchInventoryDescendents(FetchInventoryDescendents fetchInventoryDescendents) {
        DefaultMessageHandler(fetchInventoryDescendents)
    }

    Unit HandleFetchInventoryReply(FetchInventoryReply fetchInventoryReply) {
        DefaultMessageHandler(fetchInventoryReply)
    }

    Unit HandleFindAgent(FindAgent findAgent) {
        DefaultMessageHandler(findAgent)
    }

    Unit HandleForceObjectSelect(ForceObjectSelect forceObjectSelect) {
        DefaultMessageHandler(forceObjectSelect)
    }

    Unit HandleForceScriptControlRelease(ForceScriptControlRelease forceScriptControlRelease) {
        DefaultMessageHandler(forceScriptControlRelease)
    }

    Unit HandleFormFriendship(FormFriendship formFriendship) {
        DefaultMessageHandler(formFriendship)
    }

    Unit HandleFreezeUser(FreezeUser freezeUser) {
        DefaultMessageHandler(freezeUser)
    }

    Unit HandleGenericMessage(GenericMessage genericMessage) {
        DefaultMessageHandler(genericMessage)
    }

    Unit HandleGetScriptRunning(GetScriptRunning getScriptRunning) {
        DefaultMessageHandler(getScriptRunning)
    }

    Unit HandleGodKickUser(GodKickUser godKickUser) {
        DefaultMessageHandler(godKickUser)
    }

    Unit HandleGodUpdateRegionInfo(GodUpdateRegionInfo godUpdateRegionInfo) {
        DefaultMessageHandler(godUpdateRegionInfo)
    }

    Unit HandleGodlikeMessage(GodlikeMessage godlikeMessage) {
        DefaultMessageHandler(godlikeMessage)
    }

    Unit HandleGrantGodlikePowers(GrantGodlikePowers grantGodlikePowers) {
        DefaultMessageHandler(grantGodlikePowers)
    }

    Unit HandleGrantUserRights(GrantUserRights grantUserRights) {
        DefaultMessageHandler(grantUserRights)
    }

    Unit HandleGroupAccountDetailsReply(GroupAccountDetailsReply groupAccountDetailsReply) {
        DefaultMessageHandler(groupAccountDetailsReply)
    }

    Unit HandleGroupAccountDetailsRequest(GroupAccountDetailsRequest groupAccountDetailsRequest) {
        DefaultMessageHandler(groupAccountDetailsRequest)
    }

    Unit HandleGroupAccountSummaryReply(GroupAccountSummaryReply groupAccountSummaryReply) {
        DefaultMessageHandler(groupAccountSummaryReply)
    }

    Unit HandleGroupAccountSummaryRequest(GroupAccountSummaryRequest groupAccountSummaryRequest) {
        DefaultMessageHandler(groupAccountSummaryRequest)
    }

    Unit HandleGroupAccountTransactionsReply(GroupAccountTransactionsReply groupAccountTransactionsReply) {
        DefaultMessageHandler(groupAccountTransactionsReply)
    }

    Unit HandleGroupAccountTransactionsRequest(GroupAccountTransactionsRequest groupAccountTransactionsRequest) {
        DefaultMessageHandler(groupAccountTransactionsRequest)
    }

    Unit HandleGroupActiveProposalItemReply(GroupActiveProposalItemReply groupActiveProposalItemReply) {
        DefaultMessageHandler(groupActiveProposalItemReply)
    }

    Unit HandleGroupActiveProposalsRequest(GroupActiveProposalsRequest groupActiveProposalsRequest) {
        DefaultMessageHandler(groupActiveProposalsRequest)
    }

    Unit HandleGroupDataUpdate(GroupDataUpdate groupDataUpdate) {
        DefaultMessageHandler(groupDataUpdate)
    }

    Unit HandleGroupMembersReply(GroupMembersReply groupMembersReply) {
        DefaultMessageHandler(groupMembersReply)
    }

    Unit HandleGroupMembersRequest(GroupMembersRequest groupMembersRequest) {
        DefaultMessageHandler(groupMembersRequest)
    }

    Unit HandleGroupNoticeAdd(GroupNoticeAdd groupNoticeAdd) {
        DefaultMessageHandler(groupNoticeAdd)
    }

    Unit HandleGroupNoticeRequest(GroupNoticeRequest groupNoticeRequest) {
        DefaultMessageHandler(groupNoticeRequest)
    }

    Unit HandleGroupNoticesListReply(GroupNoticesListReply groupNoticesListReply) {
        DefaultMessageHandler(groupNoticesListReply)
    }

    Unit HandleGroupNoticesListRequest(GroupNoticesListRequest groupNoticesListRequest) {
        DefaultMessageHandler(groupNoticesListRequest)
    }

    Unit HandleGroupProfileReply(GroupProfileReply groupProfileReply) {
        DefaultMessageHandler(groupProfileReply)
    }

    Unit HandleGroupProfileRequest(GroupProfileRequest groupProfileRequest) {
        DefaultMessageHandler(groupProfileRequest)
    }

    Unit HandleGroupProposalBallot(GroupProposalBallot groupProposalBallot) {
        DefaultMessageHandler(groupProposalBallot)
    }

    Unit HandleGroupRoleChanges(GroupRoleChanges groupRoleChanges) {
        DefaultMessageHandler(groupRoleChanges)
    }

    Unit HandleGroupRoleDataReply(GroupRoleDataReply groupRoleDataReply) {
        DefaultMessageHandler(groupRoleDataReply)
    }

    Unit HandleGroupRoleDataRequest(GroupRoleDataRequest groupRoleDataRequest) {
        DefaultMessageHandler(groupRoleDataRequest)
    }

    Unit HandleGroupRoleMembersReply(GroupRoleMembersReply groupRoleMembersReply) {
        DefaultMessageHandler(groupRoleMembersReply)
    }

    Unit HandleGroupRoleMembersRequest(GroupRoleMembersRequest groupRoleMembersRequest) {
        DefaultMessageHandler(groupRoleMembersRequest)
    }

    Unit HandleGroupRoleUpdate(GroupRoleUpdate groupRoleUpdate) {
        DefaultMessageHandler(groupRoleUpdate)
    }

    Unit HandleGroupTitleUpdate(GroupTitleUpdate groupTitleUpdate) {
        DefaultMessageHandler(groupTitleUpdate)
    }

    Unit HandleGroupTitlesReply(GroupTitlesReply groupTitlesReply) {
        DefaultMessageHandler(groupTitlesReply)
    }

    Unit HandleGroupTitlesRequest(GroupTitlesRequest groupTitlesRequest) {
        DefaultMessageHandler(groupTitlesRequest)
    }

    Unit HandleGroupVoteHistoryItemReply(GroupVoteHistoryItemReply groupVoteHistoryItemReply) {
        DefaultMessageHandler(groupVoteHistoryItemReply)
    }

    Unit HandleGroupVoteHistoryRequest(GroupVoteHistoryRequest groupVoteHistoryRequest) {
        DefaultMessageHandler(groupVoteHistoryRequest)
    }

    Unit HandleHealthMessage(HealthMessage healthMessage) {
        DefaultMessageHandler(healthMessage)
    }

    Unit HandleImageData(ImageData imageData) {
        DefaultMessageHandler(imageData)
    }

    Unit HandleImageNotInDatabase(ImageNotInDatabase imageNotInDatabase) {
        DefaultMessageHandler(imageNotInDatabase)
    }

    Unit HandleImagePacket(ImagePacket imagePacket) {
        DefaultMessageHandler(imagePacket)
    }

    Unit HandleImprovedInstantMessage(ImprovedInstantMessage improvedInstantMessage) {
        DefaultMessageHandler(improvedInstantMessage)
    }

    Unit HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        DefaultMessageHandler(improvedTerseObjectUpdate)
    }

    Unit HandleInitiateDownload(InitiateDownload initiateDownload) {
        DefaultMessageHandler(initiateDownload)
    }

    Unit HandleInternalScriptMail(InternalScriptMail internalScriptMail) {
        DefaultMessageHandler(internalScriptMail)
    }

    Unit HandleInventoryAssetResponse(InventoryAssetResponse inventoryAssetResponse) {
        DefaultMessageHandler(inventoryAssetResponse)
    }

    Unit HandleInventoryDescendents(InventoryDescendents inventoryDescendents) {
        DefaultMessageHandler(inventoryDescendents)
    }

    Unit HandleInviteGroupRequest(InviteGroupRequest inviteGroupRequest) {
        DefaultMessageHandler(inviteGroupRequest)
    }

    Unit HandleInviteGroupResponse(InviteGroupResponse inviteGroupResponse) {
        DefaultMessageHandler(inviteGroupResponse)
    }

    Unit HandleJoinGroupReply(JoinGroupReply joinGroupReply) {
        DefaultMessageHandler(joinGroupReply)
    }

    Unit HandleJoinGroupRequest(JoinGroupRequest joinGroupRequest) {
        DefaultMessageHandler(joinGroupRequest)
    }

    Unit HandleKickUser(KickUser kickUser) {
        DefaultMessageHandler(kickUser)
    }

    Unit HandleKickUserAck(KickUserAck kickUserAck) {
        DefaultMessageHandler(kickUserAck)
    }

    Unit HandleKillChildAgents(KillChildAgents killChildAgents) {
        DefaultMessageHandler(killChildAgents)
    }

    Unit HandleKillObject(KillObject killObject) {
        DefaultMessageHandler(killObject)
    }

    Unit HandleLandStatReply(LandStatReply landStatReply) {
        DefaultMessageHandler(landStatReply)
    }

    Unit HandleLandStatRequest(LandStatRequest landStatRequest) {
        DefaultMessageHandler(landStatRequest)
    }

    Unit HandleLayerData(LayerData layerData) {
        DefaultMessageHandler(layerData)
    }

    Unit HandleLeaveGroupReply(LeaveGroupReply leaveGroupReply) {
        DefaultMessageHandler(leaveGroupReply)
    }

    Unit HandleLeaveGroupRequest(LeaveGroupRequest leaveGroupRequest) {
        DefaultMessageHandler(leaveGroupRequest)
    }

    Unit HandleLinkInventoryItem(LinkInventoryItem linkInventoryItem) {
        DefaultMessageHandler(linkInventoryItem)
    }

    Unit HandleLiveHelpGroupReply(LiveHelpGroupReply liveHelpGroupReply) {
        DefaultMessageHandler(liveHelpGroupReply)
    }

    Unit HandleLiveHelpGroupRequest(LiveHelpGroupRequest liveHelpGroupRequest) {
        DefaultMessageHandler(liveHelpGroupRequest)
    }

    Unit HandleLoadURL(LoadURL loadURL) {
        DefaultMessageHandler(loadURL)
    }

    Unit HandleLogDwellTime(LogDwellTime logDwellTime) {
        DefaultMessageHandler(logDwellTime)
    }

    Unit HandleLogFailedMoneyTransaction(LogFailedMoneyTransaction logFailedMoneyTransaction) {
        DefaultMessageHandler(logFailedMoneyTransaction)
    }

    Unit HandleLogParcelChanges(LogParcelChanges logParcelChanges) {
        DefaultMessageHandler(logParcelChanges)
    }

    Unit HandleLogTextMessage(LogTextMessage logTextMessage) {
        DefaultMessageHandler(logTextMessage)
    }

    Unit HandleLogoutReply(LogoutReply logoutReply) {
        DefaultMessageHandler(logoutReply)
    }

    Unit HandleLogoutRequest(LogoutRequest logoutRequest) {
        DefaultMessageHandler(logoutRequest)
    }

    Unit HandleMapBlockReply(MapBlockReply mapBlockReply) {
        DefaultMessageHandler(mapBlockReply)
    }

    Unit HandleMapBlockRequest(MapBlockRequest mapBlockRequest) {
        DefaultMessageHandler(mapBlockRequest)
    }

    Unit HandleMapItemReply(MapItemReply mapItemReply) {
        DefaultMessageHandler(mapItemReply)
    }

    Unit HandleMapItemRequest(MapItemRequest mapItemRequest) {
        DefaultMessageHandler(mapItemRequest)
    }

    Unit HandleMapLayerReply(MapLayerReply mapLayerReply) {
        DefaultMessageHandler(mapLayerReply)
    }

    Unit HandleMapLayerRequest(MapLayerRequest mapLayerRequest) {
        DefaultMessageHandler(mapLayerRequest)
    }

    Unit HandleMapNameRequest(MapNameRequest mapNameRequest) {
        DefaultMessageHandler(mapNameRequest)
    }

    Unit HandleMeanCollisionAlert(MeanCollisionAlert meanCollisionAlert) {
        DefaultMessageHandler(meanCollisionAlert)
    }

    Unit HandleMergeParcel(MergeParcel mergeParcel) {
        DefaultMessageHandler(mergeParcel)
    }

    Unit HandleModifyLand(ModifyLand modifyLand) {
        DefaultMessageHandler(modifyLand)
    }

    Unit HandleMoneyBalanceReply(MoneyBalanceReply moneyBalanceReply) {
        DefaultMessageHandler(moneyBalanceReply)
    }

    Unit HandleMoneyBalanceRequest(MoneyBalanceRequest moneyBalanceRequest) {
        DefaultMessageHandler(moneyBalanceRequest)
    }

    Unit HandleMoneyTransferBackend(MoneyTransferBackend moneyTransferBackend) {
        DefaultMessageHandler(moneyTransferBackend)
    }

    Unit HandleMoneyTransferRequest(MoneyTransferRequest moneyTransferRequest) {
        DefaultMessageHandler(moneyTransferRequest)
    }

    Unit HandleMoveInventoryFolder(MoveInventoryFolder moveInventoryFolder) {
        DefaultMessageHandler(moveInventoryFolder)
    }

    Unit HandleMoveInventoryItem(MoveInventoryItem moveInventoryItem) {
        DefaultMessageHandler(moveInventoryItem)
    }

    Unit HandleMoveTaskInventory(MoveTaskInventory moveTaskInventory) {
        DefaultMessageHandler(moveTaskInventory)
    }

    Unit HandleMultipleObjectUpdate(MultipleObjectUpdate multipleObjectUpdate) {
        DefaultMessageHandler(multipleObjectUpdate)
    }

    Unit HandleMuteListRequest(MuteListRequest muteListRequest) {
        DefaultMessageHandler(muteListRequest)
    }

    Unit HandleMuteListUpdate(MuteListUpdate muteListUpdate) {
        DefaultMessageHandler(muteListUpdate)
    }

    Unit HandleNameValuePair(NameValuePair nameValuePair) {
        DefaultMessageHandler(nameValuePair)
    }

    Unit HandleNearestLandingRegionReply(NearestLandingRegionReply nearestLandingRegionReply) {
        DefaultMessageHandler(nearestLandingRegionReply)
    }

    Unit HandleNearestLandingRegionRequest(NearestLandingRegionRequest nearestLandingRegionRequest) {
        DefaultMessageHandler(nearestLandingRegionRequest)
    }

    Unit HandleNearestLandingRegionUpdated(NearestLandingRegionUpdated nearestLandingRegionUpdated) {
        DefaultMessageHandler(nearestLandingRegionUpdated)
    }

    Unit HandleNeighborList(NeighborList neighborList) {
        DefaultMessageHandler(neighborList)
    }

    Unit HandleNetTest(NetTest netTest) {
        DefaultMessageHandler(netTest)
    }

    Unit HandleObjectAdd(ObjectAdd objectAdd) {
        DefaultMessageHandler(objectAdd)
    }

    Unit HandleObjectAttach(ObjectAttach objectAttach) {
        DefaultMessageHandler(objectAttach)
    }

    Unit HandleObjectBuy(ObjectBuy objectBuy) {
        DefaultMessageHandler(objectBuy)
    }

    Unit HandleObjectCategory(ObjectCategory objectCategory) {
        DefaultMessageHandler(objectCategory)
    }

    Unit HandleObjectClickAction(ObjectClickAction objectClickAction) {
        DefaultMessageHandler(objectClickAction)
    }

    Unit HandleObjectDeGrab(ObjectDeGrab objectDeGrab) {
        DefaultMessageHandler(objectDeGrab)
    }

    Unit HandleObjectDelete(ObjectDelete objectDelete) {
        DefaultMessageHandler(objectDelete)
    }

    Unit HandleObjectDelink(ObjectDelink objectDelink) {
        DefaultMessageHandler(objectDelink)
    }

    Unit HandleObjectDescription(ObjectDescription objectDescription) {
        DefaultMessageHandler(objectDescription)
    }

    Unit HandleObjectDeselect(ObjectDeselect objectDeselect) {
        DefaultMessageHandler(objectDeselect)
    }

    Unit HandleObjectDetach(ObjectDetach objectDetach) {
        DefaultMessageHandler(objectDetach)
    }

    Unit HandleObjectDrop(ObjectDrop objectDrop) {
        DefaultMessageHandler(objectDrop)
    }

    Unit HandleObjectDuplicate(ObjectDuplicate objectDuplicate) {
        DefaultMessageHandler(objectDuplicate)
    }

    Unit HandleObjectDuplicateOnRay(ObjectDuplicateOnRay objectDuplicateOnRay) {
        DefaultMessageHandler(objectDuplicateOnRay)
    }

    Unit HandleObjectExportSelected(ObjectExportSelected objectExportSelected) {
        DefaultMessageHandler(objectExportSelected)
    }

    Unit HandleObjectExtraParams(ObjectExtraParams objectExtraParams) {
        DefaultMessageHandler(objectExtraParams)
    }

    Unit HandleObjectFlagUpdate(ObjectFlagUpdate objectFlagUpdate) {
        DefaultMessageHandler(objectFlagUpdate)
    }

    Unit HandleObjectGrab(ObjectGrab objectGrab) {
        DefaultMessageHandler(objectGrab)
    }

    Unit HandleObjectGrabUpdate(ObjectGrabUpdate objectGrabUpdate) {
        DefaultMessageHandler(objectGrabUpdate)
    }

    Unit HandleObjectGroup(ObjectGroup objectGroup) {
        DefaultMessageHandler(objectGroup)
    }

    Unit HandleObjectImage(ObjectImage objectImage) {
        DefaultMessageHandler(objectImage)
    }

    Unit HandleObjectIncludeInSearch(ObjectIncludeInSearch objectIncludeInSearch) {
        DefaultMessageHandler(objectIncludeInSearch)
    }

    Unit HandleObjectLink(ObjectLink objectLink) {
        DefaultMessageHandler(objectLink)
    }

    Unit HandleObjectMaterial(ObjectMaterial objectMaterial) {
        DefaultMessageHandler(objectMaterial)
    }

    Unit HandleObjectName(ObjectName objectName) {
        DefaultMessageHandler(objectName)
    }

    Unit HandleObjectOwner(ObjectOwner objectOwner) {
        DefaultMessageHandler(objectOwner)
    }

    Unit HandleObjectPermissions(ObjectPermissions objectPermissions) {
        DefaultMessageHandler(objectPermissions)
    }

    Unit HandleObjectPosition(ObjectPosition objectPosition) {
        DefaultMessageHandler(objectPosition)
    }

    Unit HandleObjectProperties(ObjectProperties objectProperties) {
        DefaultMessageHandler(objectProperties)
    }

    Unit HandleObjectPropertiesFamily(ObjectPropertiesFamily objectPropertiesFamily) {
        DefaultMessageHandler(objectPropertiesFamily)
    }

    Unit HandleObjectRotation(ObjectRotation objectRotation) {
        DefaultMessageHandler(objectRotation)
    }

    Unit HandleObjectSaleInfo(ObjectSaleInfo objectSaleInfo) {
        DefaultMessageHandler(objectSaleInfo)
    }

    Unit HandleObjectScale(ObjectScale objectScale) {
        DefaultMessageHandler(objectScale)
    }

    Unit HandleObjectSelect(ObjectSelect objectSelect) {
        DefaultMessageHandler(objectSelect)
    }

    Unit HandleObjectShape(ObjectShape objectShape) {
        DefaultMessageHandler(objectShape)
    }

    Unit HandleObjectSpinStart(ObjectSpinStart objectSpinStart) {
        DefaultMessageHandler(objectSpinStart)
    }

    Unit HandleObjectSpinStop(ObjectSpinStop objectSpinStop) {
        DefaultMessageHandler(objectSpinStop)
    }

    Unit HandleObjectSpinUpdate(ObjectSpinUpdate objectSpinUpdate) {
        DefaultMessageHandler(objectSpinUpdate)
    }

    Unit HandleObjectUpdate(ObjectUpdate objectUpdate) {
        DefaultMessageHandler(objectUpdate)
    }

    Unit HandleObjectUpdateCached(ObjectUpdateCached objectUpdateCached) {
        DefaultMessageHandler(objectUpdateCached)
    }

    Unit HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
        DefaultMessageHandler(objectUpdateCompressed)
    }

    Unit HandleOfferCallingCard(OfferCallingCard offerCallingCard) {
        DefaultMessageHandler(offerCallingCard)
    }

    Unit HandleOfflineNotification(OfflineNotification offlineNotification) {
        DefaultMessageHandler(offlineNotification)
    }

    Unit HandleOnlineNotification(OnlineNotification onlineNotification) {
        DefaultMessageHandler(onlineNotification)
    }

    Unit HandleOpenCircuit(OpenCircuit openCircuit) {
        DefaultMessageHandler(openCircuit)
    }

    Unit HandlePacketAck(PacketAck packetAck) {
        DefaultMessageHandler(packetAck)
    }

    Unit HandleParcelAccessListReply(ParcelAccessListReply parcelAccessListReply) {
        DefaultMessageHandler(parcelAccessListReply)
    }

    Unit HandleParcelAccessListRequest(ParcelAccessListRequest parcelAccessListRequest) {
        DefaultMessageHandler(parcelAccessListRequest)
    }

    Unit HandleParcelAccessListUpdate(ParcelAccessListUpdate parcelAccessListUpdate) {
        DefaultMessageHandler(parcelAccessListUpdate)
    }

    Unit HandleParcelAuctions(ParcelAuctions parcelAuctions) {
        DefaultMessageHandler(parcelAuctions)
    }

    Unit HandleParcelBuy(ParcelBuy parcelBuy) {
        DefaultMessageHandler(parcelBuy)
    }

    Unit HandleParcelBuyPass(ParcelBuyPass parcelBuyPass) {
        DefaultMessageHandler(parcelBuyPass)
    }

    Unit HandleParcelClaim(ParcelClaim parcelClaim) {
        DefaultMessageHandler(parcelClaim)
    }

    Unit HandleParcelDeedToGroup(ParcelDeedToGroup parcelDeedToGroup) {
        DefaultMessageHandler(parcelDeedToGroup)
    }

    Unit HandleParcelDisableObjects(ParcelDisableObjects parcelDisableObjects) {
        DefaultMessageHandler(parcelDisableObjects)
    }

    Unit HandleParcelDivide(ParcelDivide parcelDivide) {
        DefaultMessageHandler(parcelDivide)
    }

    Unit HandleParcelDwellReply(ParcelDwellReply parcelDwellReply) {
        DefaultMessageHandler(parcelDwellReply)
    }

    Unit HandleParcelDwellRequest(ParcelDwellRequest parcelDwellRequest) {
        DefaultMessageHandler(parcelDwellRequest)
    }

    Unit HandleParcelGodForceOwner(ParcelGodForceOwner parcelGodForceOwner) {
        DefaultMessageHandler(parcelGodForceOwner)
    }

    Unit HandleParcelGodMarkAsContent(ParcelGodMarkAsContent parcelGodMarkAsContent) {
        DefaultMessageHandler(parcelGodMarkAsContent)
    }

    Unit HandleParcelInfoReply(ParcelInfoReply parcelInfoReply) {
        DefaultMessageHandler(parcelInfoReply)
    }

    Unit HandleParcelInfoRequest(ParcelInfoRequest parcelInfoRequest) {
        DefaultMessageHandler(parcelInfoRequest)
    }

    Unit HandleParcelJoin(ParcelJoin parcelJoin) {
        DefaultMessageHandler(parcelJoin)
    }

    Unit HandleParcelMediaCommandMessage(ParcelMediaCommandMessage parcelMediaCommandMessage) {
        DefaultMessageHandler(parcelMediaCommandMessage)
    }

    Unit HandleParcelMediaUpdate(ParcelMediaUpdate parcelMediaUpdate) {
        DefaultMessageHandler(parcelMediaUpdate)
    }

    Unit HandleParcelObjectOwnersReply(ParcelObjectOwnersReply parcelObjectOwnersReply) {
        DefaultMessageHandler(parcelObjectOwnersReply)
    }

    Unit HandleParcelObjectOwnersRequest(ParcelObjectOwnersRequest parcelObjectOwnersRequest) {
        DefaultMessageHandler(parcelObjectOwnersRequest)
    }

    Unit HandleParcelOverlay(ParcelOverlay parcelOverlay) {
        DefaultMessageHandler(parcelOverlay)
    }

    Unit HandleParcelProperties(ParcelProperties parcelProperties) {
        DefaultMessageHandler(parcelProperties)
    }

    Unit HandleParcelPropertiesRequest(ParcelPropertiesRequest parcelPropertiesRequest) {
        DefaultMessageHandler(parcelPropertiesRequest)
    }

    Unit HandleParcelPropertiesRequestByID(ParcelPropertiesRequestByID parcelPropertiesRequestByID) {
        DefaultMessageHandler(parcelPropertiesRequestByID)
    }

    Unit HandleParcelPropertiesUpdate(ParcelPropertiesUpdate parcelPropertiesUpdate) {
        DefaultMessageHandler(parcelPropertiesUpdate)
    }

    Unit HandleParcelReclaim(ParcelReclaim parcelReclaim) {
        DefaultMessageHandler(parcelReclaim)
    }

    Unit HandleParcelRelease(ParcelRelease parcelRelease) {
        DefaultMessageHandler(parcelRelease)
    }

    Unit HandleParcelRename(ParcelRename parcelRename) {
        DefaultMessageHandler(parcelRename)
    }

    Unit HandleParcelReturnObjects(ParcelReturnObjects parcelReturnObjects) {
        DefaultMessageHandler(parcelReturnObjects)
    }

    Unit HandleParcelSales(ParcelSales parcelSales) {
        DefaultMessageHandler(parcelSales)
    }

    Unit HandleParcelSelectObjects(ParcelSelectObjects parcelSelectObjects) {
        DefaultMessageHandler(parcelSelectObjects)
    }

    Unit HandleParcelSetOtherCleanTime(ParcelSetOtherCleanTime parcelSetOtherCleanTime) {
        DefaultMessageHandler(parcelSetOtherCleanTime)
    }

    Unit HandlePayPriceReply(PayPriceReply payPriceReply) {
        DefaultMessageHandler(payPriceReply)
    }

    Unit HandlePickDelete(PickDelete pickDelete) {
        DefaultMessageHandler(pickDelete)
    }

    Unit HandlePickGodDelete(PickGodDelete pickGodDelete) {
        DefaultMessageHandler(pickGodDelete)
    }

    Unit HandlePickInfoReply(PickInfoReply pickInfoReply) {
        DefaultMessageHandler(pickInfoReply)
    }

    Unit HandlePickInfoUpdate(PickInfoUpdate pickInfoUpdate) {
        DefaultMessageHandler(pickInfoUpdate)
    }

    Unit HandlePlacesQuery(PlacesQuery placesQuery) {
        DefaultMessageHandler(placesQuery)
    }

    Unit HandlePlacesReply(PlacesReply placesReply) {
        DefaultMessageHandler(placesReply)
    }

    Unit HandlePreloadSound(PreloadSound preloadSound) {
        DefaultMessageHandler(preloadSound)
    }

    Unit HandlePurgeInventoryDescendents(PurgeInventoryDescendents purgeInventoryDescendents) {
        DefaultMessageHandler(purgeInventoryDescendents)
    }

    Unit HandleRebakeAvatarTextures(RebakeAvatarTextures rebakeAvatarTextures) {
        DefaultMessageHandler(rebakeAvatarTextures)
    }

    Unit HandleRedo(Redo redo) {
        DefaultMessageHandler(redo)
    }

    Unit HandleRegionHandleRequest(RegionHandleRequest regionHandleRequest) {
        DefaultMessageHandler(regionHandleRequest)
    }

    Unit HandleRegionHandshake(RegionHandshake regionHandshake) {
        DefaultMessageHandler(regionHandshake)
    }

    Unit HandleRegionHandshakeReply(RegionHandshakeReply regionHandshakeReply) {
        DefaultMessageHandler(regionHandshakeReply)
    }

    Unit HandleRegionIDAndHandleReply(RegionIDAndHandleReply regionIDAndHandleReply) {
        DefaultMessageHandler(regionIDAndHandleReply)
    }

    Unit HandleRegionInfo(RegionInfo regionInfo) {
        DefaultMessageHandler(regionInfo)
    }

    Unit HandleRegionPresenceRequestByHandle(RegionPresenceRequestByHandle regionPresenceRequestByHandle) {
        DefaultMessageHandler(regionPresenceRequestByHandle)
    }

    Unit HandleRegionPresenceRequestByRegionID(RegionPresenceRequestByRegionID regionPresenceRequestByRegionID) {
        DefaultMessageHandler(regionPresenceRequestByRegionID)
    }

    Unit HandleRegionPresenceResponse(RegionPresenceResponse regionPresenceResponse) {
        DefaultMessageHandler(regionPresenceResponse)
    }

    Unit HandleRemoveAttachment(RemoveAttachment removeAttachment) {
        DefaultMessageHandler(removeAttachment)
    }

    Unit HandleRemoveInventoryFolder(RemoveInventoryFolder removeInventoryFolder) {
        DefaultMessageHandler(removeInventoryFolder)
    }

    Unit HandleRemoveInventoryItem(RemoveInventoryItem removeInventoryItem) {
        DefaultMessageHandler(removeInventoryItem)
    }

    Unit HandleRemoveInventoryObjects(RemoveInventoryObjects removeInventoryObjects) {
        DefaultMessageHandler(removeInventoryObjects)
    }

    Unit HandleRemoveMuteListEntry(RemoveMuteListEntry removeMuteListEntry) {
        DefaultMessageHandler(removeMuteListEntry)
    }

    Unit HandleRemoveNameValuePair(RemoveNameValuePair removeNameValuePair) {
        DefaultMessageHandler(removeNameValuePair)
    }

    Unit HandleRemoveParcel(RemoveParcel removeParcel) {
        DefaultMessageHandler(removeParcel)
    }

    Unit HandleRemoveTaskInventory(RemoveTaskInventory removeTaskInventory) {
        DefaultMessageHandler(removeTaskInventory)
    }

    Unit HandleReplyTaskInventory(ReplyTaskInventory replyTaskInventory) {
        DefaultMessageHandler(replyTaskInventory)
    }

    Unit HandleReportAutosaveCrash(ReportAutosaveCrash reportAutosaveCrash) {
        DefaultMessageHandler(reportAutosaveCrash)
    }

    Unit HandleRequestGodlikePowers(RequestGodlikePowers requestGodlikePowers) {
        DefaultMessageHandler(requestGodlikePowers)
    }

    Unit HandleRequestImage(RequestImage requestImage) {
        DefaultMessageHandler(requestImage)
    }

    Unit HandleRequestInventoryAsset(RequestInventoryAsset requestInventoryAsset) {
        DefaultMessageHandler(requestInventoryAsset)
    }

    Unit HandleRequestMultipleObjects(RequestMultipleObjects requestMultipleObjects) {
        DefaultMessageHandler(requestMultipleObjects)
    }

    Unit HandleRequestObjectPropertiesFamily(RequestObjectPropertiesFamily requestObjectPropertiesFamily) {
        DefaultMessageHandler(requestObjectPropertiesFamily)
    }

    Unit HandleRequestParcelTransfer(RequestParcelTransfer requestParcelTransfer) {
        DefaultMessageHandler(requestParcelTransfer)
    }

    Unit HandleRequestPayPrice(RequestPayPrice requestPayPrice) {
        DefaultMessageHandler(requestPayPrice)
    }

    Unit HandleRequestRegionInfo(RequestRegionInfo requestRegionInfo) {
        DefaultMessageHandler(requestRegionInfo)
    }

    Unit HandleRequestTaskInventory(RequestTaskInventory requestTaskInventory) {
        DefaultMessageHandler(requestTaskInventory)
    }

    Unit HandleRequestTrustedCircuit(RequestTrustedCircuit requestTrustedCircuit) {
        DefaultMessageHandler(requestTrustedCircuit)
    }

    Unit HandleRequestXfer(RequestXfer requestXfer) {
        DefaultMessageHandler(requestXfer)
    }

    Unit HandleRetrieveInstantMessages(RetrieveInstantMessages retrieveInstantMessages) {
        DefaultMessageHandler(retrieveInstantMessages)
    }

    Unit HandleRevokePermissions(RevokePermissions revokePermissions) {
        DefaultMessageHandler(revokePermissions)
    }

    Unit HandleRezMultipleAttachmentsFromInv(RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv) {
        DefaultMessageHandler(rezMultipleAttachmentsFromInv)
    }

    Unit HandleRezObject(RezObject rezObject) {
        DefaultMessageHandler(rezObject)
    }

    Unit HandleRezObjectFromNotecard(RezObjectFromNotecard rezObjectFromNotecard) {
        DefaultMessageHandler(rezObjectFromNotecard)
    }

    Unit HandleRezRestoreToWorld(RezRestoreToWorld rezRestoreToWorld) {
        DefaultMessageHandler(rezRestoreToWorld)
    }

    Unit HandleRezScript(RezScript rezScript) {
        DefaultMessageHandler(rezScript)
    }

    Unit HandleRezSingleAttachmentFromInv(RezSingleAttachmentFromInv rezSingleAttachmentFromInv) {
        DefaultMessageHandler(rezSingleAttachmentFromInv)
    }

    Unit HandleRoutedMoneyBalanceReply(RoutedMoneyBalanceReply routedMoneyBalanceReply) {
        DefaultMessageHandler(routedMoneyBalanceReply)
    }

    Unit HandleRpcChannelReply(RpcChannelReply rpcChannelReply) {
        DefaultMessageHandler(rpcChannelReply)
    }

    Unit HandleRpcChannelRequest(RpcChannelRequest rpcChannelRequest) {
        DefaultMessageHandler(rpcChannelRequest)
    }

    Unit HandleRpcScriptReplyInbound(RpcScriptReplyInbound rpcScriptReplyInbound) {
        DefaultMessageHandler(rpcScriptReplyInbound)
    }

    Unit HandleRpcScriptRequestInbound(RpcScriptRequestInbound rpcScriptRequestInbound) {
        DefaultMessageHandler(rpcScriptRequestInbound)
    }

    Unit HandleRpcScriptRequestInboundForward(RpcScriptRequestInboundForward rpcScriptRequestInboundForward) {
        DefaultMessageHandler(rpcScriptRequestInboundForward)
    }

    Unit HandleSaveAssetIntoInventory(SaveAssetIntoInventory saveAssetIntoInventory) {
        DefaultMessageHandler(saveAssetIntoInventory)
    }

    Unit HandleScriptAnswerYes(ScriptAnswerYes scriptAnswerYes) {
        DefaultMessageHandler(scriptAnswerYes)
    }

    Unit HandleScriptControlChange(ScriptControlChange scriptControlChange) {
        DefaultMessageHandler(scriptControlChange)
    }

    Unit HandleScriptDataReply(ScriptDataReply scriptDataReply) {
        DefaultMessageHandler(scriptDataReply)
    }

    Unit HandleScriptDataRequest(ScriptDataRequest scriptDataRequest) {
        DefaultMessageHandler(scriptDataRequest)
    }

    Unit HandleScriptDialog(ScriptDialog scriptDialog) {
        DefaultMessageHandler(scriptDialog)
    }

    Unit HandleScriptDialogReply(ScriptDialogReply scriptDialogReply) {
        DefaultMessageHandler(scriptDialogReply)
    }

    Unit HandleScriptMailRegistration(ScriptMailRegistration scriptMailRegistration) {
        DefaultMessageHandler(scriptMailRegistration)
    }

    Unit HandleScriptQuestion(ScriptQuestion scriptQuestion) {
        DefaultMessageHandler(scriptQuestion)
    }

    Unit HandleScriptReset(ScriptReset scriptReset) {
        DefaultMessageHandler(scriptReset)
    }

    Unit HandleScriptRunningReply(ScriptRunningReply scriptRunningReply) {
        DefaultMessageHandler(scriptRunningReply)
    }

    Unit HandleScriptSensorReply(ScriptSensorReply scriptSensorReply) {
        DefaultMessageHandler(scriptSensorReply)
    }

    Unit HandleScriptSensorRequest(ScriptSensorRequest scriptSensorRequest) {
        DefaultMessageHandler(scriptSensorRequest)
    }

    Unit HandleScriptTeleportRequest(ScriptTeleportRequest scriptTeleportRequest) {
        DefaultMessageHandler(scriptTeleportRequest)
    }

    Unit HandleSendPostcard(SendPostcard sendPostcard) {
        DefaultMessageHandler(sendPostcard)
    }

    Unit HandleSendXferPacket(SendXferPacket sendXferPacket) {
        DefaultMessageHandler(sendXferPacket)
    }

    Unit HandleSetAlwaysRun(SetAlwaysRun setAlwaysRun) {
        DefaultMessageHandler(setAlwaysRun)
    }

    Unit HandleSetCPURatio(SetCPURatio setCPURatio) {
        DefaultMessageHandler(setCPURatio)
    }

    Unit HandleSetFollowCamProperties(SetFollowCamProperties setFollowCamProperties) {
        DefaultMessageHandler(setFollowCamProperties)
    }

    Unit HandleSetGroupAcceptNotices(SetGroupAcceptNotices setGroupAcceptNotices) {
        DefaultMessageHandler(setGroupAcceptNotices)
    }

    Unit HandleSetGroupContribution(SetGroupContribution setGroupContribution) {
        DefaultMessageHandler(setGroupContribution)
    }

    Unit HandleSetScriptRunning(SetScriptRunning setScriptRunning) {
        DefaultMessageHandler(setScriptRunning)
    }

    Unit HandleSetSimPresenceInDatabase(SetSimPresenceInDatabase setSimPresenceInDatabase) {
        DefaultMessageHandler(setSimPresenceInDatabase)
    }

    Unit HandleSetSimStatusInDatabase(SetSimStatusInDatabase setSimStatusInDatabase) {
        DefaultMessageHandler(setSimStatusInDatabase)
    }

    Unit HandleSetStartLocation(SetStartLocation setStartLocation) {
        DefaultMessageHandler(setStartLocation)
    }

    Unit HandleSetStartLocationRequest(SetStartLocationRequest setStartLocationRequest) {
        DefaultMessageHandler(setStartLocationRequest)
    }

    Unit HandleSimCrashed(SimCrashed simCrashed) {
        DefaultMessageHandler(simCrashed)
    }

    Unit HandleSimStats(SimStats simStats) {
        DefaultMessageHandler(simStats)
    }

    Unit HandleSimStatus(SimStatus simStatus) {
        DefaultMessageHandler(simStatus)
    }

    Unit HandleSimWideDeletes(SimWideDeletes simWideDeletes) {
        DefaultMessageHandler(simWideDeletes)
    }

    Unit HandleSimulatorLoad(SimulatorLoad simulatorLoad) {
        DefaultMessageHandler(simulatorLoad)
    }

    Unit HandleSimulatorMapUpdate(SimulatorMapUpdate simulatorMapUpdate) {
        DefaultMessageHandler(simulatorMapUpdate)
    }

    Unit HandleSimulatorPresentAtLocation(SimulatorPresentAtLocation simulatorPresentAtLocation) {
        DefaultMessageHandler(simulatorPresentAtLocation)
    }

    Unit HandleSimulatorReady(SimulatorReady simulatorReady) {
        DefaultMessageHandler(simulatorReady)
    }

    Unit HandleSimulatorSetMap(SimulatorSetMap simulatorSetMap) {
        DefaultMessageHandler(simulatorSetMap)
    }

    Unit HandleSimulatorShutdownRequest(SimulatorShutdownRequest simulatorShutdownRequest) {
        DefaultMessageHandler(simulatorShutdownRequest)
    }

    Unit HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        DefaultMessageHandler(simulatorViewerTimeMessage)
    }

    Unit HandleSoundTrigger(SoundTrigger soundTrigger) {
        DefaultMessageHandler(soundTrigger)
    }

    Unit HandleStartAuction(StartAuction startAuction) {
        DefaultMessageHandler(startAuction)
    }

    Unit HandleStartGroupProposal(StartGroupProposal startGroupProposal) {
        DefaultMessageHandler(startGroupProposal)
    }

    Unit HandleStartLure(StartLure startLure) {
        DefaultMessageHandler(startLure)
    }

    Unit HandleStartPingCheck(StartPingCheck startPingCheck) {
        DefaultMessageHandler(startPingCheck)
    }

    Unit HandleStateSave(StateSave stateSave) {
        DefaultMessageHandler(stateSave)
    }

    Unit HandleSubscribeLoad(SubscribeLoad subscribeLoad) {
        DefaultMessageHandler(subscribeLoad)
    }

    Unit HandleSystemKickUser(SystemKickUser systemKickUser) {
        DefaultMessageHandler(systemKickUser)
    }

    Unit HandleSystemMessage(SystemMessage systemMessage) {
        DefaultMessageHandler(systemMessage)
    }

    Unit HandleTallyVotes(TallyVotes tallyVotes) {
        DefaultMessageHandler(tallyVotes)
    }

    Unit HandleTelehubInfo(TelehubInfo telehubInfo) {
        DefaultMessageHandler(telehubInfo)
    }

    Unit HandleTeleportCancel(TeleportCancel teleportCancel) {
        DefaultMessageHandler(teleportCancel)
    }

    Unit HandleTeleportFailed(TeleportFailed teleportFailed) {
        DefaultMessageHandler(teleportFailed)
    }

    Unit HandleTeleportFinish(TeleportFinish teleportFinish) {
        DefaultMessageHandler(teleportFinish)
    }

    Unit HandleTeleportLandingStatusChanged(TeleportLandingStatusChanged teleportLandingStatusChanged) {
        DefaultMessageHandler(teleportLandingStatusChanged)
    }

    Unit HandleTeleportLandmarkRequest(TeleportLandmarkRequest teleportLandmarkRequest) {
        DefaultMessageHandler(teleportLandmarkRequest)
    }

    Unit HandleTeleportLocal(TeleportLocal teleportLocal) {
        DefaultMessageHandler(teleportLocal)
    }

    Unit HandleTeleportLocationRequest(TeleportLocationRequest teleportLocationRequest) {
        DefaultMessageHandler(teleportLocationRequest)
    }

    Unit HandleTeleportLureRequest(TeleportLureRequest teleportLureRequest) {
        DefaultMessageHandler(teleportLureRequest)
    }

    Unit HandleTeleportProgress(TeleportProgress teleportProgress) {
        DefaultMessageHandler(teleportProgress)
    }

    Unit HandleTeleportRequest(TeleportRequest teleportRequest) {
        DefaultMessageHandler(teleportRequest)
    }

    Unit HandleTeleportStart(TeleportStart teleportStart) {
        DefaultMessageHandler(teleportStart)
    }

    Unit HandleTerminateFriendship(TerminateFriendship terminateFriendship) {
        DefaultMessageHandler(terminateFriendship)
    }

    Unit HandleTestMessage(TestMessage testMessage) {
        DefaultMessageHandler(testMessage)
    }

    Unit HandleTrackAgent(TrackAgent trackAgent) {
        DefaultMessageHandler(trackAgent)
    }

    Unit HandleTransferAbort(TransferAbort transferAbort) {
        DefaultMessageHandler(transferAbort)
    }

    Unit HandleTransferInfo(TransferInfo transferInfo) {
        DefaultMessageHandler(transferInfo)
    }

    Unit HandleTransferInventory(TransferInventory transferInventory) {
        DefaultMessageHandler(transferInventory)
    }

    Unit HandleTransferInventoryAck(TransferInventoryAck transferInventoryAck) {
        DefaultMessageHandler(transferInventoryAck)
    }

    Unit HandleTransferPacket(TransferPacket transferPacket) {
        DefaultMessageHandler(transferPacket)
    }

    Unit HandleTransferRequest(TransferRequest transferRequest) {
        DefaultMessageHandler(transferRequest)
    }

    Unit HandleUUIDGroupNameReply(UUIDGroupNameReply uUIDGroupNameReply) {
        DefaultMessageHandler(uUIDGroupNameReply)
    }

    Unit HandleUUIDGroupNameRequest(UUIDGroupNameRequest uUIDGroupNameRequest) {
        DefaultMessageHandler(uUIDGroupNameRequest)
    }

    Unit HandleUUIDNameReply(UUIDNameReply uUIDNameReply) {
        DefaultMessageHandler(uUIDNameReply)
    }

    Unit HandleUUIDNameRequest(UUIDNameRequest uUIDNameRequest) {
        DefaultMessageHandler(uUIDNameRequest)
    }

    Unit HandleUndo(Undo undo) {
        DefaultMessageHandler(undo)
    }

    Unit HandleUndoLand(UndoLand undoLand) {
        DefaultMessageHandler(undoLand)
    }

    Unit HandleUnsubscribeLoad(UnsubscribeLoad unsubscribeLoad) {
        DefaultMessageHandler(unsubscribeLoad)
    }

    Unit HandleUpdateAttachment(UpdateAttachment updateAttachment) {
        DefaultMessageHandler(updateAttachment)
    }

    Unit HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updateCreateInventoryItem) {
        DefaultMessageHandler(updateCreateInventoryItem)
    }

    Unit HandleUpdateGroupInfo(UpdateGroupInfo updateGroupInfo) {
        DefaultMessageHandler(updateGroupInfo)
    }

    Unit HandleUpdateInventoryFolder(UpdateInventoryFolder updateInventoryFolder) {
        DefaultMessageHandler(updateInventoryFolder)
    }

    Unit HandleUpdateInventoryItem(UpdateInventoryItem updateInventoryItem) {
        DefaultMessageHandler(updateInventoryItem)
    }

    Unit HandleUpdateMuteListEntry(UpdateMuteListEntry updateMuteListEntry) {
        DefaultMessageHandler(updateMuteListEntry)
    }

    Unit HandleUpdateParcel(UpdateParcel updateParcel) {
        DefaultMessageHandler(updateParcel)
    }

    Unit HandleUpdateSimulator(UpdateSimulator updateSimulator) {
        DefaultMessageHandler(updateSimulator)
    }

    Unit HandleUpdateTaskInventory(UpdateTaskInventory updateTaskInventory) {
        DefaultMessageHandler(updateTaskInventory)
    }

    Unit HandleUpdateUserInfo(UpdateUserInfo updateUserInfo) {
        DefaultMessageHandler(updateUserInfo)
    }

    Unit HandleUseCachedMuteList(UseCachedMuteList useCachedMuteList) {
        DefaultMessageHandler(useCachedMuteList)
    }

    Unit HandleUseCircuitCode(UseCircuitCode useCircuitCode) {
        DefaultMessageHandler(useCircuitCode)
    }

    Unit HandleUserInfoReply(UserInfoReply userInfoReply) {
        DefaultMessageHandler(userInfoReply)
    }

    Unit HandleUserInfoRequest(UserInfoRequest userInfoRequest) {
        DefaultMessageHandler(userInfoRequest)
    }

    Unit HandleUserReport(UserReport userReport) {
        DefaultMessageHandler(userReport)
    }

    Unit HandleUserReportInternal(UserReportInternal userReportInternal) {
        DefaultMessageHandler(userReportInternal)
    }

    Unit HandleVelocityInterpolateOff(VelocityInterpolateOff velocityInterpolateOff) {
        DefaultMessageHandler(velocityInterpolateOff)
    }

    Unit HandleVelocityInterpolateOn(VelocityInterpolateOn velocityInterpolateOn) {
        DefaultMessageHandler(velocityInterpolateOn)
    }

    Unit HandleViewerEffect(ViewerEffect viewerEffect) {
        DefaultMessageHandler(viewerEffect)
    }

    Unit HandleViewerFrozenMessage(ViewerFrozenMessage viewerFrozenMessage) {
        DefaultMessageHandler(viewerFrozenMessage)
    }

    Unit HandleViewerStartAuction(ViewerStartAuction viewerStartAuction) {
        DefaultMessageHandler(viewerStartAuction)
    }

    Unit HandleViewerStats(ViewerStats viewerStats) {
        DefaultMessageHandler(viewerStats)
    }
}
