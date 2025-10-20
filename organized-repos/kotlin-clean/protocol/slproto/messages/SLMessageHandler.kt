package com.linkpoint.slproto.messages
import java.util.*

import com.linkpoint.slproto.SLMessage

class SLMessageHandler {
    fun DefaultMessageHandler(SLMessage sLMessage) {
    }

    fun HandleAbortXfer(AbortXfer abortXfer) {
        DefaultMessageHandler(abortXfer)
    }

    fun HandleAcceptCallingCard(AcceptCallingCard acceptCallingCard) {
        DefaultMessageHandler(acceptCallingCard)
    }

    fun HandleAcceptFriendship(AcceptFriendship acceptFriendship) {
        DefaultMessageHandler(acceptFriendship)
    }

    fun HandleActivateGestures(ActivateGestures activateGestures) {
        DefaultMessageHandler(activateGestures)
    }

    fun HandleActivateGroup(ActivateGroup activateGroup) {
        DefaultMessageHandler(activateGroup)
    }

    fun HandleAddCircuitCode(AddCircuitCode addCircuitCode) {
        DefaultMessageHandler(addCircuitCode)
    }

    fun HandleAgentAlertMessage(AgentAlertMessage agentAlertMessage) {
        DefaultMessageHandler(agentAlertMessage)
    }

    fun HandleAgentAnimation(AgentAnimation agentAnimation) {
        DefaultMessageHandler(agentAnimation)
    }

    fun HandleAgentCachedTexture(AgentCachedTexture agentCachedTexture) {
        DefaultMessageHandler(agentCachedTexture)
    }

    fun HandleAgentCachedTextureResponse(AgentCachedTextureResponse agentCachedTextureResponse) {
        DefaultMessageHandler(agentCachedTextureResponse)
    }

    fun HandleAgentDataUpdate(AgentDataUpdate agentDataUpdate) {
        DefaultMessageHandler(agentDataUpdate)
    }

    fun HandleAgentDataUpdateRequest(AgentDataUpdateRequest agentDataUpdateRequest) {
        DefaultMessageHandler(agentDataUpdateRequest)
    }

    fun HandleAgentDropGroup(AgentDropGroup agentDropGroup) {
        DefaultMessageHandler(agentDropGroup)
    }

    fun HandleAgentFOV(AgentFOV agentFOV) {
        DefaultMessageHandler(agentFOV)
    }

    fun HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentGroupDataUpdate) {
        DefaultMessageHandler(agentGroupDataUpdate)
    }

    fun HandleAgentHeightWidth(AgentHeightWidth agentHeightWidth) {
        DefaultMessageHandler(agentHeightWidth)
    }

    fun HandleAgentIsNowWearing(AgentIsNowWearing agentIsNowWearing) {
        DefaultMessageHandler(agentIsNowWearing)
    }

    fun HandleAgentMovementComplete(AgentMovementComplete agentMovementComplete) {
        DefaultMessageHandler(agentMovementComplete)
    }

    fun HandleAgentPause(AgentPause agentPause) {
        DefaultMessageHandler(agentPause)
    }

    fun HandleAgentQuitCopy(AgentQuitCopy agentQuitCopy) {
        DefaultMessageHandler(agentQuitCopy)
    }

    fun HandleAgentRequestSit(AgentRequestSit agentRequestSit) {
        DefaultMessageHandler(agentRequestSit)
    }

    fun HandleAgentResume(AgentResume agentResume) {
        DefaultMessageHandler(agentResume)
    }

    fun HandleAgentSetAppearance(AgentSetAppearance agentSetAppearance) {
        DefaultMessageHandler(agentSetAppearance)
    }

    fun HandleAgentSit(AgentSit agentSit) {
        DefaultMessageHandler(agentSit)
    }

    fun HandleAgentThrottle(AgentThrottle agentThrottle) {
        DefaultMessageHandler(agentThrottle)
    }

    fun HandleAgentUpdate(AgentUpdate agentUpdate) {
        DefaultMessageHandler(agentUpdate)
    }

    fun HandleAgentWearablesRequest(AgentWearablesRequest agentWearablesRequest) {
        DefaultMessageHandler(agentWearablesRequest)
    }

    fun HandleAgentWearablesUpdate(AgentWearablesUpdate agentWearablesUpdate) {
        DefaultMessageHandler(agentWearablesUpdate)
    }

    fun HandleAlertMessage(AlertMessage alertMessage) {
        DefaultMessageHandler(alertMessage)
    }

    fun HandleAssetUploadComplete(AssetUploadComplete assetUploadComplete) {
        DefaultMessageHandler(assetUploadComplete)
    }

    fun HandleAssetUploadRequest(AssetUploadRequest assetUploadRequest) {
        DefaultMessageHandler(assetUploadRequest)
    }

    fun HandleAtomicPassObject(AtomicPassObject atomicPassObject) {
        DefaultMessageHandler(atomicPassObject)
    }

    fun HandleAttachedSound(AttachedSound attachedSound) {
        DefaultMessageHandler(attachedSound)
    }

    fun HandleAttachedSoundGainChange(AttachedSoundGainChange attachedSoundGainChange) {
        DefaultMessageHandler(attachedSoundGainChange)
    }

    fun HandleAvatarAnimation(AvatarAnimation avatarAnimation) {
        DefaultMessageHandler(avatarAnimation)
    }

    fun HandleAvatarAppearance(AvatarAppearance avatarAppearance) {
        DefaultMessageHandler(avatarAppearance)
    }

    fun HandleAvatarClassifiedReply(AvatarClassifiedReply avatarClassifiedReply) {
        DefaultMessageHandler(avatarClassifiedReply)
    }

    fun HandleAvatarGroupsReply(AvatarGroupsReply avatarGroupsReply) {
        DefaultMessageHandler(avatarGroupsReply)
    }

    fun HandleAvatarInterestsReply(AvatarInterestsReply avatarInterestsReply) {
        DefaultMessageHandler(avatarInterestsReply)
    }

    fun HandleAvatarInterestsUpdate(AvatarInterestsUpdate avatarInterestsUpdate) {
        DefaultMessageHandler(avatarInterestsUpdate)
    }

    fun HandleAvatarNotesReply(AvatarNotesReply avatarNotesReply) {
        DefaultMessageHandler(avatarNotesReply)
    }

    fun HandleAvatarNotesUpdate(AvatarNotesUpdate avatarNotesUpdate) {
        DefaultMessageHandler(avatarNotesUpdate)
    }

    fun HandleAvatarPickerReply(AvatarPickerReply avatarPickerReply) {
        DefaultMessageHandler(avatarPickerReply)
    }

    fun HandleAvatarPickerRequest(AvatarPickerRequest avatarPickerRequest) {
        DefaultMessageHandler(avatarPickerRequest)
    }

    fun HandleAvatarPickerRequestBackend(AvatarPickerRequestBackend avatarPickerRequestBackend) {
        DefaultMessageHandler(avatarPickerRequestBackend)
    }

    fun HandleAvatarPicksReply(AvatarPicksReply avatarPicksReply) {
        DefaultMessageHandler(avatarPicksReply)
    }

    fun HandleAvatarPropertiesReply(AvatarPropertiesReply avatarPropertiesReply) {
        DefaultMessageHandler(avatarPropertiesReply)
    }

    fun HandleAvatarPropertiesRequest(AvatarPropertiesRequest avatarPropertiesRequest) {
        DefaultMessageHandler(avatarPropertiesRequest)
    }

    fun HandleAvatarPropertiesRequestBackend(AvatarPropertiesRequestBackend avatarPropertiesRequestBackend) {
        DefaultMessageHandler(avatarPropertiesRequestBackend)
    }

    fun HandleAvatarPropertiesUpdate(AvatarPropertiesUpdate avatarPropertiesUpdate) {
        DefaultMessageHandler(avatarPropertiesUpdate)
    }

    fun HandleAvatarSitResponse(AvatarSitResponse avatarSitResponse) {
        DefaultMessageHandler(avatarSitResponse)
    }

    fun HandleAvatarTextureUpdate(AvatarTextureUpdate avatarTextureUpdate) {
        DefaultMessageHandler(avatarTextureUpdate)
    }

    fun HandleBulkUpdateInventory(BulkUpdateInventory bulkUpdateInventory) {
        DefaultMessageHandler(bulkUpdateInventory)
    }

    fun HandleBuyObjectInventory(BuyObjectInventory buyObjectInventory) {
        DefaultMessageHandler(buyObjectInventory)
    }

    fun HandleCameraConstraint(CameraConstraint cameraConstraint) {
        DefaultMessageHandler(cameraConstraint)
    }

    fun HandleCancelAuction(CancelAuction cancelAuction) {
        DefaultMessageHandler(cancelAuction)
    }

    fun HandleChangeInventoryItemFlags(ChangeInventoryItemFlags changeInventoryItemFlags) {
        DefaultMessageHandler(changeInventoryItemFlags)
    }

    fun HandleChangeUserRights(ChangeUserRights changeUserRights) {
        DefaultMessageHandler(changeUserRights)
    }

    fun HandleChatFromSimulator(ChatFromSimulator chatFromSimulator) {
        DefaultMessageHandler(chatFromSimulator)
    }

    fun HandleChatFromViewer(ChatFromViewer chatFromViewer) {
        DefaultMessageHandler(chatFromViewer)
    }

    fun HandleChatPass(ChatPass chatPass) {
        DefaultMessageHandler(chatPass)
    }

    fun HandleCheckParcelAuctions(CheckParcelAuctions checkParcelAuctions) {
        DefaultMessageHandler(checkParcelAuctions)
    }

    fun HandleCheckParcelSales(CheckParcelSales checkParcelSales) {
        DefaultMessageHandler(checkParcelSales)
    }

    fun HandleChildAgentAlive(ChildAgentAlive childAgentAlive) {
        DefaultMessageHandler(childAgentAlive)
    }

    fun HandleChildAgentDying(ChildAgentDying childAgentDying) {
        DefaultMessageHandler(childAgentDying)
    }

    fun HandleChildAgentPositionUpdate(ChildAgentPositionUpdate childAgentPositionUpdate) {
        DefaultMessageHandler(childAgentPositionUpdate)
    }

    fun HandleChildAgentUnknown(ChildAgentUnknown childAgentUnknown) {
        DefaultMessageHandler(childAgentUnknown)
    }

    fun HandleChildAgentUpdate(ChildAgentUpdate childAgentUpdate) {
        DefaultMessageHandler(childAgentUpdate)
    }

    fun HandleClassifiedDelete(ClassifiedDelete classifiedDelete) {
        DefaultMessageHandler(classifiedDelete)
    }

    fun HandleClassifiedGodDelete(ClassifiedGodDelete classifiedGodDelete) {
        DefaultMessageHandler(classifiedGodDelete)
    }

    fun HandleClassifiedInfoReply(ClassifiedInfoReply classifiedInfoReply) {
        DefaultMessageHandler(classifiedInfoReply)
    }

    fun HandleClassifiedInfoRequest(ClassifiedInfoRequest classifiedInfoRequest) {
        DefaultMessageHandler(classifiedInfoRequest)
    }

    fun HandleClassifiedInfoUpdate(ClassifiedInfoUpdate classifiedInfoUpdate) {
        DefaultMessageHandler(classifiedInfoUpdate)
    }

    fun HandleClearFollowCamProperties(ClearFollowCamProperties clearFollowCamProperties) {
        DefaultMessageHandler(clearFollowCamProperties)
    }

    fun HandleCloseCircuit(CloseCircuit closeCircuit) {
        DefaultMessageHandler(closeCircuit)
    }

    fun HandleCoarseLocationUpdate(CoarseLocationUpdate coarseLocationUpdate) {
        DefaultMessageHandler(coarseLocationUpdate)
    }

    fun HandleCompleteAgentMovement(CompleteAgentMovement completeAgentMovement) {
        DefaultMessageHandler(completeAgentMovement)
    }

    fun HandleCompleteAuction(CompleteAuction completeAuction) {
        DefaultMessageHandler(completeAuction)
    }

    fun HandleCompletePingCheck(CompletePingCheck completePingCheck) {
        DefaultMessageHandler(completePingCheck)
    }

    fun HandleConfirmAuctionStart(ConfirmAuctionStart confirmAuctionStart) {
        DefaultMessageHandler(confirmAuctionStart)
    }

    fun HandleConfirmEnableSimulator(ConfirmEnableSimulator confirmEnableSimulator) {
        DefaultMessageHandler(confirmEnableSimulator)
    }

    fun HandleConfirmXferPacket(ConfirmXferPacket confirmXferPacket) {
        DefaultMessageHandler(confirmXferPacket)
    }

    fun HandleCopyInventoryFromNotecard(CopyInventoryFromNotecard copyInventoryFromNotecard) {
        DefaultMessageHandler(copyInventoryFromNotecard)
    }

    fun HandleCopyInventoryItem(CopyInventoryItem copyInventoryItem) {
        DefaultMessageHandler(copyInventoryItem)
    }

    fun HandleCreateGroupReply(CreateGroupReply createGroupReply) {
        DefaultMessageHandler(createGroupReply)
    }

    fun HandleCreateGroupRequest(CreateGroupRequest createGroupRequest) {
        DefaultMessageHandler(createGroupRequest)
    }

    fun HandleCreateInventoryFolder(CreateInventoryFolder createInventoryFolder) {
        DefaultMessageHandler(createInventoryFolder)
    }

    fun HandleCreateInventoryItem(CreateInventoryItem createInventoryItem) {
        DefaultMessageHandler(createInventoryItem)
    }

    fun HandleCreateLandmarkForEvent(CreateLandmarkForEvent createLandmarkForEvent) {
        DefaultMessageHandler(createLandmarkForEvent)
    }

    fun HandleCreateNewOutfitAttachments(CreateNewOutfitAttachments createNewOutfitAttachments) {
        DefaultMessageHandler(createNewOutfitAttachments)
    }

    fun HandleCreateTrustedCircuit(CreateTrustedCircuit createTrustedCircuit) {
        DefaultMessageHandler(createTrustedCircuit)
    }

    fun HandleCrossedRegion(CrossedRegion crossedRegion) {
        DefaultMessageHandler(crossedRegion)
    }

    fun HandleDataHomeLocationReply(DataHomeLocationReply dataHomeLocationReply) {
        DefaultMessageHandler(dataHomeLocationReply)
    }

    fun HandleDataHomeLocationRequest(DataHomeLocationRequest dataHomeLocationRequest) {
        DefaultMessageHandler(dataHomeLocationRequest)
    }

    fun HandleDataServerLogout(DataServerLogout dataServerLogout) {
        DefaultMessageHandler(dataServerLogout)
    }

    fun HandleDeRezAck(DeRezAck deRezAck) {
        DefaultMessageHandler(deRezAck)
    }

    fun HandleDeRezObject(DeRezObject deRezObject) {
        DefaultMessageHandler(deRezObject)
    }

    fun HandleDeactivateGestures(DeactivateGestures deactivateGestures) {
        DefaultMessageHandler(deactivateGestures)
    }

    fun HandleDeclineCallingCard(DeclineCallingCard declineCallingCard) {
        DefaultMessageHandler(declineCallingCard)
    }

    fun HandleDeclineFriendship(DeclineFriendship declineFriendship) {
        DefaultMessageHandler(declineFriendship)
    }

    fun HandleDenyTrustedCircuit(DenyTrustedCircuit denyTrustedCircuit) {
        DefaultMessageHandler(denyTrustedCircuit)
    }

    fun HandleDerezContainer(DerezContainer derezContainer) {
        DefaultMessageHandler(derezContainer)
    }

    fun HandleDetachAttachmentIntoInv(DetachAttachmentIntoInv detachAttachmentIntoInv) {
        DefaultMessageHandler(detachAttachmentIntoInv)
    }

    fun HandleDirClassifiedQuery(DirClassifiedQuery dirClassifiedQuery) {
        DefaultMessageHandler(dirClassifiedQuery)
    }

    fun HandleDirClassifiedQueryBackend(DirClassifiedQueryBackend dirClassifiedQueryBackend) {
        DefaultMessageHandler(dirClassifiedQueryBackend)
    }

    fun HandleDirClassifiedReply(DirClassifiedReply dirClassifiedReply) {
        DefaultMessageHandler(dirClassifiedReply)
    }

    fun HandleDirEventsReply(DirEventsReply dirEventsReply) {
        DefaultMessageHandler(dirEventsReply)
    }

    fun HandleDirFindQuery(DirFindQuery dirFindQuery) {
        DefaultMessageHandler(dirFindQuery)
    }

    fun HandleDirFindQueryBackend(DirFindQueryBackend dirFindQueryBackend) {
        DefaultMessageHandler(dirFindQueryBackend)
    }

    fun HandleDirGroupsReply(DirGroupsReply dirGroupsReply) {
        DefaultMessageHandler(dirGroupsReply)
    }

    fun HandleDirLandQuery(DirLandQuery dirLandQuery) {
        DefaultMessageHandler(dirLandQuery)
    }

    fun HandleDirLandQueryBackend(DirLandQueryBackend dirLandQueryBackend) {
        DefaultMessageHandler(dirLandQueryBackend)
    }

    fun HandleDirLandReply(DirLandReply dirLandReply) {
        DefaultMessageHandler(dirLandReply)
    }

    fun HandleDirPeopleReply(DirPeopleReply dirPeopleReply) {
        DefaultMessageHandler(dirPeopleReply)
    }

    fun HandleDirPlacesQuery(DirPlacesQuery dirPlacesQuery) {
        DefaultMessageHandler(dirPlacesQuery)
    }

    fun HandleDirPlacesQueryBackend(DirPlacesQueryBackend dirPlacesQueryBackend) {
        DefaultMessageHandler(dirPlacesQueryBackend)
    }

    fun HandleDirPlacesReply(DirPlacesReply dirPlacesReply) {
        DefaultMessageHandler(dirPlacesReply)
    }

    fun HandleDirPopularQuery(DirPopularQuery dirPopularQuery) {
        DefaultMessageHandler(dirPopularQuery)
    }

    fun HandleDirPopularQueryBackend(DirPopularQueryBackend dirPopularQueryBackend) {
        DefaultMessageHandler(dirPopularQueryBackend)
    }

    fun HandleDirPopularReply(DirPopularReply dirPopularReply) {
        DefaultMessageHandler(dirPopularReply)
    }

    fun HandleDisableSimulator(DisableSimulator disableSimulator) {
        DefaultMessageHandler(disableSimulator)
    }

    fun HandleEconomyData(EconomyData economyData) {
        DefaultMessageHandler(economyData)
    }

    fun HandleEconomyDataRequest(EconomyDataRequest economyDataRequest) {
        DefaultMessageHandler(economyDataRequest)
    }

    fun HandleEdgeDataPacket(EdgeDataPacket edgeDataPacket) {
        DefaultMessageHandler(edgeDataPacket)
    }

    fun HandleEjectGroupMemberReply(EjectGroupMemberReply ejectGroupMemberReply) {
        DefaultMessageHandler(ejectGroupMemberReply)
    }

    fun HandleEjectGroupMemberRequest(EjectGroupMemberRequest ejectGroupMemberRequest) {
        DefaultMessageHandler(ejectGroupMemberRequest)
    }

    fun HandleEjectUser(EjectUser ejectUser) {
        DefaultMessageHandler(ejectUser)
    }

    fun HandleEmailMessageReply(EmailMessageReply emailMessageReply) {
        DefaultMessageHandler(emailMessageReply)
    }

    fun HandleEmailMessageRequest(EmailMessageRequest emailMessageRequest) {
        DefaultMessageHandler(emailMessageRequest)
    }

    fun HandleEnableSimulator(EnableSimulator enableSimulator) {
        DefaultMessageHandler(enableSimulator)
    }

    fun HandleError(Error error) {
        DefaultMessageHandler(error)
    }

    fun HandleEstateCovenantReply(EstateCovenantReply estateCovenantReply) {
        DefaultMessageHandler(estateCovenantReply)
    }

    fun HandleEstateCovenantRequest(EstateCovenantRequest estateCovenantRequest) {
        DefaultMessageHandler(estateCovenantRequest)
    }

    fun HandleEstateOwnerMessage(EstateOwnerMessage estateOwnerMessage) {
        DefaultMessageHandler(estateOwnerMessage)
    }

    fun HandleEventGodDelete(EventGodDelete eventGodDelete) {
        DefaultMessageHandler(eventGodDelete)
    }

    fun HandleEventInfoReply(EventInfoReply eventInfoReply) {
        DefaultMessageHandler(eventInfoReply)
    }

    fun HandleEventInfoRequest(EventInfoRequest eventInfoRequest) {
        DefaultMessageHandler(eventInfoRequest)
    }

    fun HandleEventLocationReply(EventLocationReply eventLocationReply) {
        DefaultMessageHandler(eventLocationReply)
    }

    fun HandleEventLocationRequest(EventLocationRequest eventLocationRequest) {
        DefaultMessageHandler(eventLocationRequest)
    }

    fun HandleEventNotificationAddRequest(EventNotificationAddRequest eventNotificationAddRequest) {
        DefaultMessageHandler(eventNotificationAddRequest)
    }

    fun HandleEventNotificationRemoveRequest(EventNotificationRemoveRequest eventNotificationRemoveRequest) {
        DefaultMessageHandler(eventNotificationRemoveRequest)
    }

    fun HandleFeatureDisabled(FeatureDisabled featureDisabled) {
        DefaultMessageHandler(featureDisabled)
    }

    fun HandleFetchInventory(FetchInventory fetchInventory) {
        DefaultMessageHandler(fetchInventory)
    }

    fun HandleFetchInventoryDescendents(FetchInventoryDescendents fetchInventoryDescendents) {
        DefaultMessageHandler(fetchInventoryDescendents)
    }

    fun HandleFetchInventoryReply(FetchInventoryReply fetchInventoryReply) {
        DefaultMessageHandler(fetchInventoryReply)
    }

    fun HandleFindAgent(FindAgent findAgent) {
        DefaultMessageHandler(findAgent)
    }

    fun HandleForceObjectSelect(ForceObjectSelect forceObjectSelect) {
        DefaultMessageHandler(forceObjectSelect)
    }

    fun HandleForceScriptControlRelease(ForceScriptControlRelease forceScriptControlRelease) {
        DefaultMessageHandler(forceScriptControlRelease)
    }

    fun HandleFormFriendship(FormFriendship formFriendship) {
        DefaultMessageHandler(formFriendship)
    }

    fun HandleFreezeUser(FreezeUser freezeUser) {
        DefaultMessageHandler(freezeUser)
    }

    fun HandleGenericMessage(GenericMessage genericMessage) {
        DefaultMessageHandler(genericMessage)
    }

    fun HandleGetScriptRunning(GetScriptRunning getScriptRunning) {
        DefaultMessageHandler(getScriptRunning)
    }

    fun HandleGodKickUser(GodKickUser godKickUser) {
        DefaultMessageHandler(godKickUser)
    }

    fun HandleGodUpdateRegionInfo(GodUpdateRegionInfo godUpdateRegionInfo) {
        DefaultMessageHandler(godUpdateRegionInfo)
    }

    fun HandleGodlikeMessage(GodlikeMessage godlikeMessage) {
        DefaultMessageHandler(godlikeMessage)
    }

    fun HandleGrantGodlikePowers(GrantGodlikePowers grantGodlikePowers) {
        DefaultMessageHandler(grantGodlikePowers)
    }

    fun HandleGrantUserRights(GrantUserRights grantUserRights) {
        DefaultMessageHandler(grantUserRights)
    }

    fun HandleGroupAccountDetailsReply(GroupAccountDetailsReply groupAccountDetailsReply) {
        DefaultMessageHandler(groupAccountDetailsReply)
    }

    fun HandleGroupAccountDetailsRequest(GroupAccountDetailsRequest groupAccountDetailsRequest) {
        DefaultMessageHandler(groupAccountDetailsRequest)
    }

    fun HandleGroupAccountSummaryReply(GroupAccountSummaryReply groupAccountSummaryReply) {
        DefaultMessageHandler(groupAccountSummaryReply)
    }

    fun HandleGroupAccountSummaryRequest(GroupAccountSummaryRequest groupAccountSummaryRequest) {
        DefaultMessageHandler(groupAccountSummaryRequest)
    }

    fun HandleGroupAccountTransactionsReply(GroupAccountTransactionsReply groupAccountTransactionsReply) {
        DefaultMessageHandler(groupAccountTransactionsReply)
    }

    fun HandleGroupAccountTransactionsRequest(GroupAccountTransactionsRequest groupAccountTransactionsRequest) {
        DefaultMessageHandler(groupAccountTransactionsRequest)
    }

    fun HandleGroupActiveProposalItemReply(GroupActiveProposalItemReply groupActiveProposalItemReply) {
        DefaultMessageHandler(groupActiveProposalItemReply)
    }

    fun HandleGroupActiveProposalsRequest(GroupActiveProposalsRequest groupActiveProposalsRequest) {
        DefaultMessageHandler(groupActiveProposalsRequest)
    }

    fun HandleGroupDataUpdate(GroupDataUpdate groupDataUpdate) {
        DefaultMessageHandler(groupDataUpdate)
    }

    fun HandleGroupMembersReply(GroupMembersReply groupMembersReply) {
        DefaultMessageHandler(groupMembersReply)
    }

    fun HandleGroupMembersRequest(GroupMembersRequest groupMembersRequest) {
        DefaultMessageHandler(groupMembersRequest)
    }

    fun HandleGroupNoticeAdd(GroupNoticeAdd groupNoticeAdd) {
        DefaultMessageHandler(groupNoticeAdd)
    }

    fun HandleGroupNoticeRequest(GroupNoticeRequest groupNoticeRequest) {
        DefaultMessageHandler(groupNoticeRequest)
    }

    fun HandleGroupNoticesListReply(GroupNoticesListReply groupNoticesListReply) {
        DefaultMessageHandler(groupNoticesListReply)
    }

    fun HandleGroupNoticesListRequest(GroupNoticesListRequest groupNoticesListRequest) {
        DefaultMessageHandler(groupNoticesListRequest)
    }

    fun HandleGroupProfileReply(GroupProfileReply groupProfileReply) {
        DefaultMessageHandler(groupProfileReply)
    }

    fun HandleGroupProfileRequest(GroupProfileRequest groupProfileRequest) {
        DefaultMessageHandler(groupProfileRequest)
    }

    fun HandleGroupProposalBallot(GroupProposalBallot groupProposalBallot) {
        DefaultMessageHandler(groupProposalBallot)
    }

    fun HandleGroupRoleChanges(GroupRoleChanges groupRoleChanges) {
        DefaultMessageHandler(groupRoleChanges)
    }

    fun HandleGroupRoleDataReply(GroupRoleDataReply groupRoleDataReply) {
        DefaultMessageHandler(groupRoleDataReply)
    }

    fun HandleGroupRoleDataRequest(GroupRoleDataRequest groupRoleDataRequest) {
        DefaultMessageHandler(groupRoleDataRequest)
    }

    fun HandleGroupRoleMembersReply(GroupRoleMembersReply groupRoleMembersReply) {
        DefaultMessageHandler(groupRoleMembersReply)
    }

    fun HandleGroupRoleMembersRequest(GroupRoleMembersRequest groupRoleMembersRequest) {
        DefaultMessageHandler(groupRoleMembersRequest)
    }

    fun HandleGroupRoleUpdate(GroupRoleUpdate groupRoleUpdate) {
        DefaultMessageHandler(groupRoleUpdate)
    }

    fun HandleGroupTitleUpdate(GroupTitleUpdate groupTitleUpdate) {
        DefaultMessageHandler(groupTitleUpdate)
    }

    fun HandleGroupTitlesReply(GroupTitlesReply groupTitlesReply) {
        DefaultMessageHandler(groupTitlesReply)
    }

    fun HandleGroupTitlesRequest(GroupTitlesRequest groupTitlesRequest) {
        DefaultMessageHandler(groupTitlesRequest)
    }

    fun HandleGroupVoteHistoryItemReply(GroupVoteHistoryItemReply groupVoteHistoryItemReply) {
        DefaultMessageHandler(groupVoteHistoryItemReply)
    }

    fun HandleGroupVoteHistoryRequest(GroupVoteHistoryRequest groupVoteHistoryRequest) {
        DefaultMessageHandler(groupVoteHistoryRequest)
    }

    fun HandleHealthMessage(HealthMessage healthMessage) {
        DefaultMessageHandler(healthMessage)
    }

    fun HandleImageData(ImageData imageData) {
        DefaultMessageHandler(imageData)
    }

    fun HandleImageNotInDatabase(ImageNotInDatabase imageNotInDatabase) {
        DefaultMessageHandler(imageNotInDatabase)
    }

    fun HandleImagePacket(ImagePacket imagePacket) {
        DefaultMessageHandler(imagePacket)
    }

    fun HandleImprovedInstantMessage(ImprovedInstantMessage improvedInstantMessage) {
        DefaultMessageHandler(improvedInstantMessage)
    }

    fun HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedTerseObjectUpdate) {
        DefaultMessageHandler(improvedTerseObjectUpdate)
    }

    fun HandleInitiateDownload(InitiateDownload initiateDownload) {
        DefaultMessageHandler(initiateDownload)
    }

    fun HandleInternalScriptMail(InternalScriptMail internalScriptMail) {
        DefaultMessageHandler(internalScriptMail)
    }

    fun HandleInventoryAssetResponse(InventoryAssetResponse inventoryAssetResponse) {
        DefaultMessageHandler(inventoryAssetResponse)
    }

    fun HandleInventoryDescendents(InventoryDescendents inventoryDescendents) {
        DefaultMessageHandler(inventoryDescendents)
    }

    fun HandleInviteGroupRequest(InviteGroupRequest inviteGroupRequest) {
        DefaultMessageHandler(inviteGroupRequest)
    }

    fun HandleInviteGroupResponse(InviteGroupResponse inviteGroupResponse) {
        DefaultMessageHandler(inviteGroupResponse)
    }

    fun HandleJoinGroupReply(JoinGroupReply joinGroupReply) {
        DefaultMessageHandler(joinGroupReply)
    }

    fun HandleJoinGroupRequest(JoinGroupRequest joinGroupRequest) {
        DefaultMessageHandler(joinGroupRequest)
    }

    fun HandleKickUser(KickUser kickUser) {
        DefaultMessageHandler(kickUser)
    }

    fun HandleKickUserAck(KickUserAck kickUserAck) {
        DefaultMessageHandler(kickUserAck)
    }

    fun HandleKillChildAgents(KillChildAgents killChildAgents) {
        DefaultMessageHandler(killChildAgents)
    }

    fun HandleKillObject(KillObject killObject) {
        DefaultMessageHandler(killObject)
    }

    fun HandleLandStatReply(LandStatReply landStatReply) {
        DefaultMessageHandler(landStatReply)
    }

    fun HandleLandStatRequest(LandStatRequest landStatRequest) {
        DefaultMessageHandler(landStatRequest)
    }

    fun HandleLayerData(LayerData layerData) {
        DefaultMessageHandler(layerData)
    }

    fun HandleLeaveGroupReply(LeaveGroupReply leaveGroupReply) {
        DefaultMessageHandler(leaveGroupReply)
    }

    fun HandleLeaveGroupRequest(LeaveGroupRequest leaveGroupRequest) {
        DefaultMessageHandler(leaveGroupRequest)
    }

    fun HandleLinkInventoryItem(LinkInventoryItem linkInventoryItem) {
        DefaultMessageHandler(linkInventoryItem)
    }

    fun HandleLiveHelpGroupReply(LiveHelpGroupReply liveHelpGroupReply) {
        DefaultMessageHandler(liveHelpGroupReply)
    }

    fun HandleLiveHelpGroupRequest(LiveHelpGroupRequest liveHelpGroupRequest) {
        DefaultMessageHandler(liveHelpGroupRequest)
    }

    fun HandleLoadURL(LoadURL loadURL) {
        DefaultMessageHandler(loadURL)
    }

    fun HandleLogDwellTime(LogDwellTime logDwellTime) {
        DefaultMessageHandler(logDwellTime)
    }

    fun HandleLogFailedMoneyTransaction(LogFailedMoneyTransaction logFailedMoneyTransaction) {
        DefaultMessageHandler(logFailedMoneyTransaction)
    }

    fun HandleLogParcelChanges(LogParcelChanges logParcelChanges) {
        DefaultMessageHandler(logParcelChanges)
    }

    fun HandleLogTextMessage(LogTextMessage logTextMessage) {
        DefaultMessageHandler(logTextMessage)
    }

    fun HandleLogoutReply(LogoutReply logoutReply) {
        DefaultMessageHandler(logoutReply)
    }

    fun HandleLogoutRequest(LogoutRequest logoutRequest) {
        DefaultMessageHandler(logoutRequest)
    }

    fun HandleMapBlockReply(MapBlockReply mapBlockReply) {
        DefaultMessageHandler(mapBlockReply)
    }

    fun HandleMapBlockRequest(MapBlockRequest mapBlockRequest) {
        DefaultMessageHandler(mapBlockRequest)
    }

    fun HandleMapItemReply(MapItemReply mapItemReply) {
        DefaultMessageHandler(mapItemReply)
    }

    fun HandleMapItemRequest(MapItemRequest mapItemRequest) {
        DefaultMessageHandler(mapItemRequest)
    }

    fun HandleMapLayerReply(MapLayerReply mapLayerReply) {
        DefaultMessageHandler(mapLayerReply)
    }

    fun HandleMapLayerRequest(MapLayerRequest mapLayerRequest) {
        DefaultMessageHandler(mapLayerRequest)
    }

    fun HandleMapNameRequest(MapNameRequest mapNameRequest) {
        DefaultMessageHandler(mapNameRequest)
    }

    fun HandleMeanCollisionAlert(MeanCollisionAlert meanCollisionAlert) {
        DefaultMessageHandler(meanCollisionAlert)
    }

    fun HandleMergeParcel(MergeParcel mergeParcel) {
        DefaultMessageHandler(mergeParcel)
    }

    fun HandleModifyLand(ModifyLand modifyLand) {
        DefaultMessageHandler(modifyLand)
    }

    fun HandleMoneyBalanceReply(MoneyBalanceReply moneyBalanceReply) {
        DefaultMessageHandler(moneyBalanceReply)
    }

    fun HandleMoneyBalanceRequest(MoneyBalanceRequest moneyBalanceRequest) {
        DefaultMessageHandler(moneyBalanceRequest)
    }

    fun HandleMoneyTransferBackend(MoneyTransferBackend moneyTransferBackend) {
        DefaultMessageHandler(moneyTransferBackend)
    }

    fun HandleMoneyTransferRequest(MoneyTransferRequest moneyTransferRequest) {
        DefaultMessageHandler(moneyTransferRequest)
    }

    fun HandleMoveInventoryFolder(MoveInventoryFolder moveInventoryFolder) {
        DefaultMessageHandler(moveInventoryFolder)
    }

    fun HandleMoveInventoryItem(MoveInventoryItem moveInventoryItem) {
        DefaultMessageHandler(moveInventoryItem)
    }

    fun HandleMoveTaskInventory(MoveTaskInventory moveTaskInventory) {
        DefaultMessageHandler(moveTaskInventory)
    }

    fun HandleMultipleObjectUpdate(MultipleObjectUpdate multipleObjectUpdate) {
        DefaultMessageHandler(multipleObjectUpdate)
    }

    fun HandleMuteListRequest(MuteListRequest muteListRequest) {
        DefaultMessageHandler(muteListRequest)
    }

    fun HandleMuteListUpdate(MuteListUpdate muteListUpdate) {
        DefaultMessageHandler(muteListUpdate)
    }

    fun HandleNameValuePair(NameValuePair nameValuePair) {
        DefaultMessageHandler(nameValuePair)
    }

    fun HandleNearestLandingRegionReply(NearestLandingRegionReply nearestLandingRegionReply) {
        DefaultMessageHandler(nearestLandingRegionReply)
    }

    fun HandleNearestLandingRegionRequest(NearestLandingRegionRequest nearestLandingRegionRequest) {
        DefaultMessageHandler(nearestLandingRegionRequest)
    }

    fun HandleNearestLandingRegionUpdated(NearestLandingRegionUpdated nearestLandingRegionUpdated) {
        DefaultMessageHandler(nearestLandingRegionUpdated)
    }

    fun HandleNeighborList(NeighborList neighborList) {
        DefaultMessageHandler(neighborList)
    }

    fun HandleNetTest(NetTest netTest) {
        DefaultMessageHandler(netTest)
    }

    fun HandleObjectAdd(ObjectAdd objectAdd) {
        DefaultMessageHandler(objectAdd)
    }

    fun HandleObjectAttach(ObjectAttach objectAttach) {
        DefaultMessageHandler(objectAttach)
    }

    fun HandleObjectBuy(ObjectBuy objectBuy) {
        DefaultMessageHandler(objectBuy)
    }

    fun HandleObjectCategory(ObjectCategory objectCategory) {
        DefaultMessageHandler(objectCategory)
    }

    fun HandleObjectClickAction(ObjectClickAction objectClickAction) {
        DefaultMessageHandler(objectClickAction)
    }

    fun HandleObjectDeGrab(ObjectDeGrab objectDeGrab) {
        DefaultMessageHandler(objectDeGrab)
    }

    fun HandleObjectDelete(ObjectDelete objectDelete) {
        DefaultMessageHandler(objectDelete)
    }

    fun HandleObjectDelink(ObjectDelink objectDelink) {
        DefaultMessageHandler(objectDelink)
    }

    fun HandleObjectDescription(ObjectDescription objectDescription) {
        DefaultMessageHandler(objectDescription)
    }

    fun HandleObjectDeselect(ObjectDeselect objectDeselect) {
        DefaultMessageHandler(objectDeselect)
    }

    fun HandleObjectDetach(ObjectDetach objectDetach) {
        DefaultMessageHandler(objectDetach)
    }

    fun HandleObjectDrop(ObjectDrop objectDrop) {
        DefaultMessageHandler(objectDrop)
    }

    fun HandleObjectDuplicate(ObjectDuplicate objectDuplicate) {
        DefaultMessageHandler(objectDuplicate)
    }

    fun HandleObjectDuplicateOnRay(ObjectDuplicateOnRay objectDuplicateOnRay) {
        DefaultMessageHandler(objectDuplicateOnRay)
    }

    fun HandleObjectExportSelected(ObjectExportSelected objectExportSelected) {
        DefaultMessageHandler(objectExportSelected)
    }

    fun HandleObjectExtraParams(ObjectExtraParams objectExtraParams) {
        DefaultMessageHandler(objectExtraParams)
    }

    fun HandleObjectFlagUpdate(ObjectFlagUpdate objectFlagUpdate) {
        DefaultMessageHandler(objectFlagUpdate)
    }

    fun HandleObjectGrab(ObjectGrab objectGrab) {
        DefaultMessageHandler(objectGrab)
    }

    fun HandleObjectGrabUpdate(ObjectGrabUpdate objectGrabUpdate) {
        DefaultMessageHandler(objectGrabUpdate)
    }

    fun HandleObjectGroup(ObjectGroup objectGroup) {
        DefaultMessageHandler(objectGroup)
    }

    fun HandleObjectImage(ObjectImage objectImage) {
        DefaultMessageHandler(objectImage)
    }

    fun HandleObjectIncludeInSearch(ObjectIncludeInSearch objectIncludeInSearch) {
        DefaultMessageHandler(objectIncludeInSearch)
    }

    fun HandleObjectLink(ObjectLink objectLink) {
        DefaultMessageHandler(objectLink)
    }

    fun HandleObjectMaterial(ObjectMaterial objectMaterial) {
        DefaultMessageHandler(objectMaterial)
    }

    fun HandleObjectName(ObjectName objectName) {
        DefaultMessageHandler(objectName)
    }

    fun HandleObjectOwner(ObjectOwner objectOwner) {
        DefaultMessageHandler(objectOwner)
    }

    fun HandleObjectPermissions(ObjectPermissions objectPermissions) {
        DefaultMessageHandler(objectPermissions)
    }

    fun HandleObjectPosition(ObjectPosition objectPosition) {
        DefaultMessageHandler(objectPosition)
    }

    fun HandleObjectProperties(ObjectProperties objectProperties) {
        DefaultMessageHandler(objectProperties)
    }

    fun HandleObjectPropertiesFamily(ObjectPropertiesFamily objectPropertiesFamily) {
        DefaultMessageHandler(objectPropertiesFamily)
    }

    fun HandleObjectRotation(ObjectRotation objectRotation) {
        DefaultMessageHandler(objectRotation)
    }

    fun HandleObjectSaleInfo(ObjectSaleInfo objectSaleInfo) {
        DefaultMessageHandler(objectSaleInfo)
    }

    fun HandleObjectScale(ObjectScale objectScale) {
        DefaultMessageHandler(objectScale)
    }

    fun HandleObjectSelect(ObjectSelect objectSelect) {
        DefaultMessageHandler(objectSelect)
    }

    fun HandleObjectShape(ObjectShape objectShape) {
        DefaultMessageHandler(objectShape)
    }

    fun HandleObjectSpinStart(ObjectSpinStart objectSpinStart) {
        DefaultMessageHandler(objectSpinStart)
    }

    fun HandleObjectSpinStop(ObjectSpinStop objectSpinStop) {
        DefaultMessageHandler(objectSpinStop)
    }

    fun HandleObjectSpinUpdate(ObjectSpinUpdate objectSpinUpdate) {
        DefaultMessageHandler(objectSpinUpdate)
    }

    fun HandleObjectUpdate(ObjectUpdate objectUpdate) {
        DefaultMessageHandler(objectUpdate)
    }

    fun HandleObjectUpdateCached(ObjectUpdateCached objectUpdateCached) {
        DefaultMessageHandler(objectUpdateCached)
    }

    fun HandleObjectUpdateCompressed(ObjectUpdateCompressed objectUpdateCompressed) {
        DefaultMessageHandler(objectUpdateCompressed)
    }

    fun HandleOfferCallingCard(OfferCallingCard offerCallingCard) {
        DefaultMessageHandler(offerCallingCard)
    }

    fun HandleOfflineNotification(OfflineNotification offlineNotification) {
        DefaultMessageHandler(offlineNotification)
    }

    fun HandleOnlineNotification(OnlineNotification onlineNotification) {
        DefaultMessageHandler(onlineNotification)
    }

    fun HandleOpenCircuit(OpenCircuit openCircuit) {
        DefaultMessageHandler(openCircuit)
    }

    fun HandlePacketAck(PacketAck packetAck) {
        DefaultMessageHandler(packetAck)
    }

    fun HandleParcelAccessListReply(ParcelAccessListReply parcelAccessListReply) {
        DefaultMessageHandler(parcelAccessListReply)
    }

    fun HandleParcelAccessListRequest(ParcelAccessListRequest parcelAccessListRequest) {
        DefaultMessageHandler(parcelAccessListRequest)
    }

    fun HandleParcelAccessListUpdate(ParcelAccessListUpdate parcelAccessListUpdate) {
        DefaultMessageHandler(parcelAccessListUpdate)
    }

    fun HandleParcelAuctions(ParcelAuctions parcelAuctions) {
        DefaultMessageHandler(parcelAuctions)
    }

    fun HandleParcelBuy(ParcelBuy parcelBuy) {
        DefaultMessageHandler(parcelBuy)
    }

    fun HandleParcelBuyPass(ParcelBuyPass parcelBuyPass) {
        DefaultMessageHandler(parcelBuyPass)
    }

    fun HandleParcelClaim(ParcelClaim parcelClaim) {
        DefaultMessageHandler(parcelClaim)
    }

    fun HandleParcelDeedToGroup(ParcelDeedToGroup parcelDeedToGroup) {
        DefaultMessageHandler(parcelDeedToGroup)
    }

    fun HandleParcelDisableObjects(ParcelDisableObjects parcelDisableObjects) {
        DefaultMessageHandler(parcelDisableObjects)
    }

    fun HandleParcelDivide(ParcelDivide parcelDivide) {
        DefaultMessageHandler(parcelDivide)
    }

    fun HandleParcelDwellReply(ParcelDwellReply parcelDwellReply) {
        DefaultMessageHandler(parcelDwellReply)
    }

    fun HandleParcelDwellRequest(ParcelDwellRequest parcelDwellRequest) {
        DefaultMessageHandler(parcelDwellRequest)
    }

    fun HandleParcelGodForceOwner(ParcelGodForceOwner parcelGodForceOwner) {
        DefaultMessageHandler(parcelGodForceOwner)
    }

    fun HandleParcelGodMarkAsContent(ParcelGodMarkAsContent parcelGodMarkAsContent) {
        DefaultMessageHandler(parcelGodMarkAsContent)
    }

    fun HandleParcelInfoReply(ParcelInfoReply parcelInfoReply) {
        DefaultMessageHandler(parcelInfoReply)
    }

    fun HandleParcelInfoRequest(ParcelInfoRequest parcelInfoRequest) {
        DefaultMessageHandler(parcelInfoRequest)
    }

    fun HandleParcelJoin(ParcelJoin parcelJoin) {
        DefaultMessageHandler(parcelJoin)
    }

    fun HandleParcelMediaCommandMessage(ParcelMediaCommandMessage parcelMediaCommandMessage) {
        DefaultMessageHandler(parcelMediaCommandMessage)
    }

    fun HandleParcelMediaUpdate(ParcelMediaUpdate parcelMediaUpdate) {
        DefaultMessageHandler(parcelMediaUpdate)
    }

    fun HandleParcelObjectOwnersReply(ParcelObjectOwnersReply parcelObjectOwnersReply) {
        DefaultMessageHandler(parcelObjectOwnersReply)
    }

    fun HandleParcelObjectOwnersRequest(ParcelObjectOwnersRequest parcelObjectOwnersRequest) {
        DefaultMessageHandler(parcelObjectOwnersRequest)
    }

    fun HandleParcelOverlay(ParcelOverlay parcelOverlay) {
        DefaultMessageHandler(parcelOverlay)
    }

    fun HandleParcelProperties(ParcelProperties parcelProperties) {
        DefaultMessageHandler(parcelProperties)
    }

    fun HandleParcelPropertiesRequest(ParcelPropertiesRequest parcelPropertiesRequest) {
        DefaultMessageHandler(parcelPropertiesRequest)
    }

    fun HandleParcelPropertiesRequestByID(ParcelPropertiesRequestByID parcelPropertiesRequestByID) {
        DefaultMessageHandler(parcelPropertiesRequestByID)
    }

    fun HandleParcelPropertiesUpdate(ParcelPropertiesUpdate parcelPropertiesUpdate) {
        DefaultMessageHandler(parcelPropertiesUpdate)
    }

    fun HandleParcelReclaim(ParcelReclaim parcelReclaim) {
        DefaultMessageHandler(parcelReclaim)
    }

    fun HandleParcelRelease(ParcelRelease parcelRelease) {
        DefaultMessageHandler(parcelRelease)
    }

    fun HandleParcelRename(ParcelRename parcelRename) {
        DefaultMessageHandler(parcelRename)
    }

    fun HandleParcelReturnObjects(ParcelReturnObjects parcelReturnObjects) {
        DefaultMessageHandler(parcelReturnObjects)
    }

    fun HandleParcelSales(ParcelSales parcelSales) {
        DefaultMessageHandler(parcelSales)
    }

    fun HandleParcelSelectObjects(ParcelSelectObjects parcelSelectObjects) {
        DefaultMessageHandler(parcelSelectObjects)
    }

    fun HandleParcelSetOtherCleanTime(ParcelSetOtherCleanTime parcelSetOtherCleanTime) {
        DefaultMessageHandler(parcelSetOtherCleanTime)
    }

    fun HandlePayPriceReply(PayPriceReply payPriceReply) {
        DefaultMessageHandler(payPriceReply)
    }

    fun HandlePickDelete(PickDelete pickDelete) {
        DefaultMessageHandler(pickDelete)
    }

    fun HandlePickGodDelete(PickGodDelete pickGodDelete) {
        DefaultMessageHandler(pickGodDelete)
    }

    fun HandlePickInfoReply(PickInfoReply pickInfoReply) {
        DefaultMessageHandler(pickInfoReply)
    }

    fun HandlePickInfoUpdate(PickInfoUpdate pickInfoUpdate) {
        DefaultMessageHandler(pickInfoUpdate)
    }

    fun HandlePlacesQuery(PlacesQuery placesQuery) {
        DefaultMessageHandler(placesQuery)
    }

    fun HandlePlacesReply(PlacesReply placesReply) {
        DefaultMessageHandler(placesReply)
    }

    fun HandlePreloadSound(PreloadSound preloadSound) {
        DefaultMessageHandler(preloadSound)
    }

    fun HandlePurgeInventoryDescendents(PurgeInventoryDescendents purgeInventoryDescendents) {
        DefaultMessageHandler(purgeInventoryDescendents)
    }

    fun HandleRebakeAvatarTextures(RebakeAvatarTextures rebakeAvatarTextures) {
        DefaultMessageHandler(rebakeAvatarTextures)
    }

    fun HandleRedo(Redo redo) {
        DefaultMessageHandler(redo)
    }

    fun HandleRegionHandleRequest(RegionHandleRequest regionHandleRequest) {
        DefaultMessageHandler(regionHandleRequest)
    }

    fun HandleRegionHandshake(RegionHandshake regionHandshake) {
        DefaultMessageHandler(regionHandshake)
    }

    fun HandleRegionHandshakeReply(RegionHandshakeReply regionHandshakeReply) {
        DefaultMessageHandler(regionHandshakeReply)
    }

    fun HandleRegionIDAndHandleReply(RegionIDAndHandleReply regionIDAndHandleReply) {
        DefaultMessageHandler(regionIDAndHandleReply)
    }

    fun HandleRegionInfo(RegionInfo regionInfo) {
        DefaultMessageHandler(regionInfo)
    }

    fun HandleRegionPresenceRequestByHandle(RegionPresenceRequestByHandle regionPresenceRequestByHandle) {
        DefaultMessageHandler(regionPresenceRequestByHandle)
    }

    fun HandleRegionPresenceRequestByRegionID(RegionPresenceRequestByRegionID regionPresenceRequestByRegionID) {
        DefaultMessageHandler(regionPresenceRequestByRegionID)
    }

    fun HandleRegionPresenceResponse(RegionPresenceResponse regionPresenceResponse) {
        DefaultMessageHandler(regionPresenceResponse)
    }

    fun HandleRemoveAttachment(RemoveAttachment removeAttachment) {
        DefaultMessageHandler(removeAttachment)
    }

    fun HandleRemoveInventoryFolder(RemoveInventoryFolder removeInventoryFolder) {
        DefaultMessageHandler(removeInventoryFolder)
    }

    fun HandleRemoveInventoryItem(RemoveInventoryItem removeInventoryItem) {
        DefaultMessageHandler(removeInventoryItem)
    }

    fun HandleRemoveInventoryObjects(RemoveInventoryObjects removeInventoryObjects) {
        DefaultMessageHandler(removeInventoryObjects)
    }

    fun HandleRemoveMuteListEntry(RemoveMuteListEntry removeMuteListEntry) {
        DefaultMessageHandler(removeMuteListEntry)
    }

    fun HandleRemoveNameValuePair(RemoveNameValuePair removeNameValuePair) {
        DefaultMessageHandler(removeNameValuePair)
    }

    fun HandleRemoveParcel(RemoveParcel removeParcel) {
        DefaultMessageHandler(removeParcel)
    }

    fun HandleRemoveTaskInventory(RemoveTaskInventory removeTaskInventory) {
        DefaultMessageHandler(removeTaskInventory)
    }

    fun HandleReplyTaskInventory(ReplyTaskInventory replyTaskInventory) {
        DefaultMessageHandler(replyTaskInventory)
    }

    fun HandleReportAutosaveCrash(ReportAutosaveCrash reportAutosaveCrash) {
        DefaultMessageHandler(reportAutosaveCrash)
    }

    fun HandleRequestGodlikePowers(RequestGodlikePowers requestGodlikePowers) {
        DefaultMessageHandler(requestGodlikePowers)
    }

    fun HandleRequestImage(RequestImage requestImage) {
        DefaultMessageHandler(requestImage)
    }

    fun HandleRequestInventoryAsset(RequestInventoryAsset requestInventoryAsset) {
        DefaultMessageHandler(requestInventoryAsset)
    }

    fun HandleRequestMultipleObjects(RequestMultipleObjects requestMultipleObjects) {
        DefaultMessageHandler(requestMultipleObjects)
    }

    fun HandleRequestObjectPropertiesFamily(RequestObjectPropertiesFamily requestObjectPropertiesFamily) {
        DefaultMessageHandler(requestObjectPropertiesFamily)
    }

    fun HandleRequestParcelTransfer(RequestParcelTransfer requestParcelTransfer) {
        DefaultMessageHandler(requestParcelTransfer)
    }

    fun HandleRequestPayPrice(RequestPayPrice requestPayPrice) {
        DefaultMessageHandler(requestPayPrice)
    }

    fun HandleRequestRegionInfo(RequestRegionInfo requestRegionInfo) {
        DefaultMessageHandler(requestRegionInfo)
    }

    fun HandleRequestTaskInventory(RequestTaskInventory requestTaskInventory) {
        DefaultMessageHandler(requestTaskInventory)
    }

    fun HandleRequestTrustedCircuit(RequestTrustedCircuit requestTrustedCircuit) {
        DefaultMessageHandler(requestTrustedCircuit)
    }

    fun HandleRequestXfer(RequestXfer requestXfer) {
        DefaultMessageHandler(requestXfer)
    }

    fun HandleRetrieveInstantMessages(RetrieveInstantMessages retrieveInstantMessages) {
        DefaultMessageHandler(retrieveInstantMessages)
    }

    fun HandleRevokePermissions(RevokePermissions revokePermissions) {
        DefaultMessageHandler(revokePermissions)
    }

    fun HandleRezMultipleAttachmentsFromInv(RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv) {
        DefaultMessageHandler(rezMultipleAttachmentsFromInv)
    }

    fun HandleRezObject(RezObject rezObject) {
        DefaultMessageHandler(rezObject)
    }

    fun HandleRezObjectFromNotecard(RezObjectFromNotecard rezObjectFromNotecard) {
        DefaultMessageHandler(rezObjectFromNotecard)
    }

    fun HandleRezRestoreToWorld(RezRestoreToWorld rezRestoreToWorld) {
        DefaultMessageHandler(rezRestoreToWorld)
    }

    fun HandleRezScript(RezScript rezScript) {
        DefaultMessageHandler(rezScript)
    }

    fun HandleRezSingleAttachmentFromInv(RezSingleAttachmentFromInv rezSingleAttachmentFromInv) {
        DefaultMessageHandler(rezSingleAttachmentFromInv)
    }

    fun HandleRoutedMoneyBalanceReply(RoutedMoneyBalanceReply routedMoneyBalanceReply) {
        DefaultMessageHandler(routedMoneyBalanceReply)
    }

    fun HandleRpcChannelReply(RpcChannelReply rpcChannelReply) {
        DefaultMessageHandler(rpcChannelReply)
    }

    fun HandleRpcChannelRequest(RpcChannelRequest rpcChannelRequest) {
        DefaultMessageHandler(rpcChannelRequest)
    }

    fun HandleRpcScriptReplyInbound(RpcScriptReplyInbound rpcScriptReplyInbound) {
        DefaultMessageHandler(rpcScriptReplyInbound)
    }

    fun HandleRpcScriptRequestInbound(RpcScriptRequestInbound rpcScriptRequestInbound) {
        DefaultMessageHandler(rpcScriptRequestInbound)
    }

    fun HandleRpcScriptRequestInboundForward(RpcScriptRequestInboundForward rpcScriptRequestInboundForward) {
        DefaultMessageHandler(rpcScriptRequestInboundForward)
    }

    fun HandleSaveAssetIntoInventory(SaveAssetIntoInventory saveAssetIntoInventory) {
        DefaultMessageHandler(saveAssetIntoInventory)
    }

    fun HandleScriptAnswerYes(ScriptAnswerYes scriptAnswerYes) {
        DefaultMessageHandler(scriptAnswerYes)
    }

    fun HandleScriptControlChange(ScriptControlChange scriptControlChange) {
        DefaultMessageHandler(scriptControlChange)
    }

    fun HandleScriptDataReply(ScriptDataReply scriptDataReply) {
        DefaultMessageHandler(scriptDataReply)
    }

    fun HandleScriptDataRequest(ScriptDataRequest scriptDataRequest) {
        DefaultMessageHandler(scriptDataRequest)
    }

    fun HandleScriptDialog(ScriptDialog scriptDialog) {
        DefaultMessageHandler(scriptDialog)
    }

    fun HandleScriptDialogReply(ScriptDialogReply scriptDialogReply) {
        DefaultMessageHandler(scriptDialogReply)
    }

    fun HandleScriptMailRegistration(ScriptMailRegistration scriptMailRegistration) {
        DefaultMessageHandler(scriptMailRegistration)
    }

    fun HandleScriptQuestion(ScriptQuestion scriptQuestion) {
        DefaultMessageHandler(scriptQuestion)
    }

    fun HandleScriptReset(ScriptReset scriptReset) {
        DefaultMessageHandler(scriptReset)
    }

    fun HandleScriptRunningReply(ScriptRunningReply scriptRunningReply) {
        DefaultMessageHandler(scriptRunningReply)
    }

    fun HandleScriptSensorReply(ScriptSensorReply scriptSensorReply) {
        DefaultMessageHandler(scriptSensorReply)
    }

    fun HandleScriptSensorRequest(ScriptSensorRequest scriptSensorRequest) {
        DefaultMessageHandler(scriptSensorRequest)
    }

    fun HandleScriptTeleportRequest(ScriptTeleportRequest scriptTeleportRequest) {
        DefaultMessageHandler(scriptTeleportRequest)
    }

    fun HandleSendPostcard(SendPostcard sendPostcard) {
        DefaultMessageHandler(sendPostcard)
    }

    fun HandleSendXferPacket(SendXferPacket sendXferPacket) {
        DefaultMessageHandler(sendXferPacket)
    }

    fun HandleSetAlwaysRun(SetAlwaysRun setAlwaysRun) {
        DefaultMessageHandler(setAlwaysRun)
    }

    fun HandleSetCPURatio(SetCPURatio setCPURatio) {
        DefaultMessageHandler(setCPURatio)
    }

    fun HandleSetFollowCamProperties(SetFollowCamProperties setFollowCamProperties) {
        DefaultMessageHandler(setFollowCamProperties)
    }

    fun HandleSetGroupAcceptNotices(SetGroupAcceptNotices setGroupAcceptNotices) {
        DefaultMessageHandler(setGroupAcceptNotices)
    }

    fun HandleSetGroupContribution(SetGroupContribution setGroupContribution) {
        DefaultMessageHandler(setGroupContribution)
    }

    fun HandleSetScriptRunning(SetScriptRunning setScriptRunning) {
        DefaultMessageHandler(setScriptRunning)
    }

    fun HandleSetSimPresenceInDatabase(SetSimPresenceInDatabase setSimPresenceInDatabase) {
        DefaultMessageHandler(setSimPresenceInDatabase)
    }

    fun HandleSetSimStatusInDatabase(SetSimStatusInDatabase setSimStatusInDatabase) {
        DefaultMessageHandler(setSimStatusInDatabase)
    }

    fun HandleSetStartLocation(SetStartLocation setStartLocation) {
        DefaultMessageHandler(setStartLocation)
    }

    fun HandleSetStartLocationRequest(SetStartLocationRequest setStartLocationRequest) {
        DefaultMessageHandler(setStartLocationRequest)
    }

    fun HandleSimCrashed(SimCrashed simCrashed) {
        DefaultMessageHandler(simCrashed)
    }

    fun HandleSimStats(SimStats simStats) {
        DefaultMessageHandler(simStats)
    }

    fun HandleSimStatus(SimStatus simStatus) {
        DefaultMessageHandler(simStatus)
    }

    fun HandleSimWideDeletes(SimWideDeletes simWideDeletes) {
        DefaultMessageHandler(simWideDeletes)
    }

    fun HandleSimulatorLoad(SimulatorLoad simulatorLoad) {
        DefaultMessageHandler(simulatorLoad)
    }

    fun HandleSimulatorMapUpdate(SimulatorMapUpdate simulatorMapUpdate) {
        DefaultMessageHandler(simulatorMapUpdate)
    }

    fun HandleSimulatorPresentAtLocation(SimulatorPresentAtLocation simulatorPresentAtLocation) {
        DefaultMessageHandler(simulatorPresentAtLocation)
    }

    fun HandleSimulatorReady(SimulatorReady simulatorReady) {
        DefaultMessageHandler(simulatorReady)
    }

    fun HandleSimulatorSetMap(SimulatorSetMap simulatorSetMap) {
        DefaultMessageHandler(simulatorSetMap)
    }

    fun HandleSimulatorShutdownRequest(SimulatorShutdownRequest simulatorShutdownRequest) {
        DefaultMessageHandler(simulatorShutdownRequest)
    }

    fun HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorViewerTimeMessage) {
        DefaultMessageHandler(simulatorViewerTimeMessage)
    }

    fun HandleSoundTrigger(SoundTrigger soundTrigger) {
        DefaultMessageHandler(soundTrigger)
    }

    fun HandleStartAuction(StartAuction startAuction) {
        DefaultMessageHandler(startAuction)
    }

    fun HandleStartGroupProposal(StartGroupProposal startGroupProposal) {
        DefaultMessageHandler(startGroupProposal)
    }

    fun HandleStartLure(StartLure startLure) {
        DefaultMessageHandler(startLure)
    }

    fun HandleStartPingCheck(StartPingCheck startPingCheck) {
        DefaultMessageHandler(startPingCheck)
    }

    fun HandleStateSave(StateSave stateSave) {
        DefaultMessageHandler(stateSave)
    }

    fun HandleSubscribeLoad(SubscribeLoad subscribeLoad) {
        DefaultMessageHandler(subscribeLoad)
    }

    fun HandleSystemKickUser(SystemKickUser systemKickUser) {
        DefaultMessageHandler(systemKickUser)
    }

    fun HandleSystemMessage(SystemMessage systemMessage) {
        DefaultMessageHandler(systemMessage)
    }

    fun HandleTallyVotes(TallyVotes tallyVotes) {
        DefaultMessageHandler(tallyVotes)
    }

    fun HandleTelehubInfo(TelehubInfo telehubInfo) {
        DefaultMessageHandler(telehubInfo)
    }

    fun HandleTeleportCancel(TeleportCancel teleportCancel) {
        DefaultMessageHandler(teleportCancel)
    }

    fun HandleTeleportFailed(TeleportFailed teleportFailed) {
        DefaultMessageHandler(teleportFailed)
    }

    fun HandleTeleportFinish(TeleportFinish teleportFinish) {
        DefaultMessageHandler(teleportFinish)
    }

    fun HandleTeleportLandingStatusChanged(TeleportLandingStatusChanged teleportLandingStatusChanged) {
        DefaultMessageHandler(teleportLandingStatusChanged)
    }

    fun HandleTeleportLandmarkRequest(TeleportLandmarkRequest teleportLandmarkRequest) {
        DefaultMessageHandler(teleportLandmarkRequest)
    }

    fun HandleTeleportLocal(TeleportLocal teleportLocal) {
        DefaultMessageHandler(teleportLocal)
    }

    fun HandleTeleportLocationRequest(TeleportLocationRequest teleportLocationRequest) {
        DefaultMessageHandler(teleportLocationRequest)
    }

    fun HandleTeleportLureRequest(TeleportLureRequest teleportLureRequest) {
        DefaultMessageHandler(teleportLureRequest)
    }

    fun HandleTeleportProgress(TeleportProgress teleportProgress) {
        DefaultMessageHandler(teleportProgress)
    }

    fun HandleTeleportRequest(TeleportRequest teleportRequest) {
        DefaultMessageHandler(teleportRequest)
    }

    fun HandleTeleportStart(TeleportStart teleportStart) {
        DefaultMessageHandler(teleportStart)
    }

    fun HandleTerminateFriendship(TerminateFriendship terminateFriendship) {
        DefaultMessageHandler(terminateFriendship)
    }

    fun HandleTestMessage(TestMessage testMessage) {
        DefaultMessageHandler(testMessage)
    }

    fun HandleTrackAgent(TrackAgent trackAgent) {
        DefaultMessageHandler(trackAgent)
    }

    fun HandleTransferAbort(TransferAbort transferAbort) {
        DefaultMessageHandler(transferAbort)
    }

    fun HandleTransferInfo(TransferInfo transferInfo) {
        DefaultMessageHandler(transferInfo)
    }

    fun HandleTransferInventory(TransferInventory transferInventory) {
        DefaultMessageHandler(transferInventory)
    }

    fun HandleTransferInventoryAck(TransferInventoryAck transferInventoryAck) {
        DefaultMessageHandler(transferInventoryAck)
    }

    fun HandleTransferPacket(TransferPacket transferPacket) {
        DefaultMessageHandler(transferPacket)
    }

    fun HandleTransferRequest(TransferRequest transferRequest) {
        DefaultMessageHandler(transferRequest)
    }

    fun HandleUUIDGroupNameReply(UUIDGroupNameReply uUIDGroupNameReply) {
        DefaultMessageHandler(uUIDGroupNameReply)
    }

    fun HandleUUIDGroupNameRequest(UUIDGroupNameRequest uUIDGroupNameRequest) {
        DefaultMessageHandler(uUIDGroupNameRequest)
    }

    fun HandleUUIDNameReply(UUIDNameReply uUIDNameReply) {
        DefaultMessageHandler(uUIDNameReply)
    }

    fun HandleUUIDNameRequest(UUIDNameRequest uUIDNameRequest) {
        DefaultMessageHandler(uUIDNameRequest)
    }

    fun HandleUndo(Undo undo) {
        DefaultMessageHandler(undo)
    }

    fun HandleUndoLand(UndoLand undoLand) {
        DefaultMessageHandler(undoLand)
    }

    fun HandleUnsubscribeLoad(UnsubscribeLoad unsubscribeLoad) {
        DefaultMessageHandler(unsubscribeLoad)
    }

    fun HandleUpdateAttachment(UpdateAttachment updateAttachment) {
        DefaultMessageHandler(updateAttachment)
    }

    fun HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updateCreateInventoryItem) {
        DefaultMessageHandler(updateCreateInventoryItem)
    }

    fun HandleUpdateGroupInfo(UpdateGroupInfo updateGroupInfo) {
        DefaultMessageHandler(updateGroupInfo)
    }

    fun HandleUpdateInventoryFolder(UpdateInventoryFolder updateInventoryFolder) {
        DefaultMessageHandler(updateInventoryFolder)
    }

    fun HandleUpdateInventoryItem(UpdateInventoryItem updateInventoryItem) {
        DefaultMessageHandler(updateInventoryItem)
    }

    fun HandleUpdateMuteListEntry(UpdateMuteListEntry updateMuteListEntry) {
        DefaultMessageHandler(updateMuteListEntry)
    }

    fun HandleUpdateParcel(UpdateParcel updateParcel) {
        DefaultMessageHandler(updateParcel)
    }

    fun HandleUpdateSimulator(UpdateSimulator updateSimulator) {
        DefaultMessageHandler(updateSimulator)
    }

    fun HandleUpdateTaskInventory(UpdateTaskInventory updateTaskInventory) {
        DefaultMessageHandler(updateTaskInventory)
    }

    fun HandleUpdateUserInfo(UpdateUserInfo updateUserInfo) {
        DefaultMessageHandler(updateUserInfo)
    }

    fun HandleUseCachedMuteList(UseCachedMuteList useCachedMuteList) {
        DefaultMessageHandler(useCachedMuteList)
    }

    fun HandleUseCircuitCode(UseCircuitCode useCircuitCode) {
        DefaultMessageHandler(useCircuitCode)
    }

    fun HandleUserInfoReply(UserInfoReply userInfoReply) {
        DefaultMessageHandler(userInfoReply)
    }

    fun HandleUserInfoRequest(UserInfoRequest userInfoRequest) {
        DefaultMessageHandler(userInfoRequest)
    }

    fun HandleUserReport(UserReport userReport) {
        DefaultMessageHandler(userReport)
    }

    fun HandleUserReportInternal(UserReportInternal userReportInternal) {
        DefaultMessageHandler(userReportInternal)
    }

    fun HandleVelocityInterpolateOff(VelocityInterpolateOff velocityInterpolateOff) {
        DefaultMessageHandler(velocityInterpolateOff)
    }

    fun HandleVelocityInterpolateOn(VelocityInterpolateOn velocityInterpolateOn) {
        DefaultMessageHandler(velocityInterpolateOn)
    }

    fun HandleViewerEffect(ViewerEffect viewerEffect) {
        DefaultMessageHandler(viewerEffect)
    }

    fun HandleViewerFrozenMessage(ViewerFrozenMessage viewerFrozenMessage) {
        DefaultMessageHandler(viewerFrozenMessage)
    }

    fun HandleViewerStartAuction(ViewerStartAuction viewerStartAuction) {
        DefaultMessageHandler(viewerStartAuction)
    }

    fun HandleViewerStats(ViewerStats viewerStats) {
        DefaultMessageHandler(viewerStats)
    }
}
