// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            AbortXfer, AcceptCallingCard, AcceptFriendship, ActivateGestures, 
//            ActivateGroup, AddCircuitCode, AgentAlertMessage, AgentAnimation, 
//            AgentCachedTexture, AgentCachedTextureResponse, AgentDataUpdate, AgentDataUpdateRequest, 
//            AgentDropGroup, AgentFOV, AgentGroupDataUpdate, AgentHeightWidth, 
//            AgentIsNowWearing, AgentMovementComplete, AgentPause, AgentQuitCopy, 
//            AgentRequestSit, AgentResume, AgentSetAppearance, AgentSit, 
//            AgentThrottle, AgentUpdate, AgentWearablesRequest, AgentWearablesUpdate, 
//            AlertMessage, AssetUploadComplete, AssetUploadRequest, AtomicPassObject, 
//            AttachedSound, AttachedSoundGainChange, AvatarAnimation, AvatarAppearance, 
//            AvatarClassifiedReply, AvatarGroupsReply, AvatarInterestsReply, AvatarInterestsUpdate, 
//            AvatarNotesReply, AvatarNotesUpdate, AvatarPickerReply, AvatarPickerRequest, 
//            AvatarPickerRequestBackend, AvatarPicksReply, AvatarPropertiesReply, AvatarPropertiesRequest, 
//            AvatarPropertiesRequestBackend, AvatarPropertiesUpdate, AvatarSitResponse, AvatarTextureUpdate, 
//            BulkUpdateInventory, BuyObjectInventory, CameraConstraint, CancelAuction, 
//            ChangeInventoryItemFlags, ChangeUserRights, ChatFromSimulator, ChatFromViewer, 
//            ChatPass, CheckParcelAuctions, CheckParcelSales, ChildAgentAlive, 
//            ChildAgentDying, ChildAgentPositionUpdate, ChildAgentUnknown, ChildAgentUpdate, 
//            ClassifiedDelete, ClassifiedGodDelete, ClassifiedInfoReply, ClassifiedInfoRequest, 
//            ClassifiedInfoUpdate, ClearFollowCamProperties, CloseCircuit, CoarseLocationUpdate, 
//            CompleteAgentMovement, CompleteAuction, CompletePingCheck, ConfirmAuctionStart, 
//            ConfirmEnableSimulator, ConfirmXferPacket, CopyInventoryFromNotecard, CopyInventoryItem, 
//            CreateGroupReply, CreateGroupRequest, CreateInventoryFolder, CreateInventoryItem, 
//            CreateLandmarkForEvent, CreateNewOutfitAttachments, CreateTrustedCircuit, CrossedRegion, 
//            DataHomeLocationReply, DataHomeLocationRequest, DataServerLogout, DeRezAck, 
//            DeRezObject, DeactivateGestures, DeclineCallingCard, DeclineFriendship, 
//            DenyTrustedCircuit, DerezContainer, DetachAttachmentIntoInv, DirClassifiedQuery, 
//            DirClassifiedQueryBackend, DirClassifiedReply, DirEventsReply, DirFindQuery, 
//            DirFindQueryBackend, DirGroupsReply, DirLandQuery, DirLandQueryBackend, 
//            DirLandReply, DirPeopleReply, DirPlacesQuery, DirPlacesQueryBackend, 
//            DirPlacesReply, DirPopularQuery, DirPopularQueryBackend, DirPopularReply, 
//            DisableSimulator, EconomyData, EconomyDataRequest, EdgeDataPacket, 
//            EjectGroupMemberReply, EjectGroupMemberRequest, EjectUser, EmailMessageReply, 
//            EmailMessageRequest, EnableSimulator, Error, EstateCovenantReply, 
//            EstateCovenantRequest, EstateOwnerMessage, EventGodDelete, EventInfoReply, 
//            EventInfoRequest, EventLocationReply, EventLocationRequest, EventNotificationAddRequest, 
//            EventNotificationRemoveRequest, FeatureDisabled, FetchInventory, FetchInventoryDescendents, 
//            FetchInventoryReply, FindAgent, ForceObjectSelect, ForceScriptControlRelease, 
//            FormFriendship, FreezeUser, GenericMessage, GetScriptRunning, 
//            GodKickUser, GodUpdateRegionInfo, GodlikeMessage, GrantGodlikePowers, 
//            GrantUserRights, GroupAccountDetailsReply, GroupAccountDetailsRequest, GroupAccountSummaryReply, 
//            GroupAccountSummaryRequest, GroupAccountTransactionsReply, GroupAccountTransactionsRequest, GroupActiveProposalItemReply, 
//            GroupActiveProposalsRequest, GroupDataUpdate, GroupMembersReply, GroupMembersRequest, 
//            GroupNoticeAdd, GroupNoticeRequest, GroupNoticesListReply, GroupNoticesListRequest, 
//            GroupProfileReply, GroupProfileRequest, GroupProposalBallot, GroupRoleChanges, 
//            GroupRoleDataReply, GroupRoleDataRequest, GroupRoleMembersReply, GroupRoleMembersRequest, 
//            GroupRoleUpdate, GroupTitleUpdate, GroupTitlesReply, GroupTitlesRequest, 
//            GroupVoteHistoryItemReply, GroupVoteHistoryRequest, HealthMessage, ImageData, 
//            ImageNotInDatabase, ImagePacket, ImprovedInstantMessage, ImprovedTerseObjectUpdate, 
//            InitiateDownload, InternalScriptMail, InventoryAssetResponse, InventoryDescendents, 
//            InviteGroupRequest, InviteGroupResponse, JoinGroupReply, JoinGroupRequest, 
//            KickUser, KickUserAck, KillChildAgents, KillObject, 
//            LandStatReply, LandStatRequest, LayerData, LeaveGroupReply, 
//            LeaveGroupRequest, LinkInventoryItem, LiveHelpGroupReply, LiveHelpGroupRequest, 
//            LoadURL, LogDwellTime, LogFailedMoneyTransaction, LogParcelChanges, 
//            LogTextMessage, LogoutReply, LogoutRequest, MapBlockReply, 
//            MapBlockRequest, MapItemReply, MapItemRequest, MapLayerReply, 
//            MapLayerRequest, MapNameRequest, MeanCollisionAlert, MergeParcel, 
//            ModifyLand, MoneyBalanceReply, MoneyBalanceRequest, MoneyTransferBackend, 
//            MoneyTransferRequest, MoveInventoryFolder, MoveInventoryItem, MoveTaskInventory, 
//            MultipleObjectUpdate, MuteListRequest, MuteListUpdate, NameValuePair, 
//            NearestLandingRegionReply, NearestLandingRegionRequest, NearestLandingRegionUpdated, NeighborList, 
//            NetTest, ObjectAdd, ObjectAttach, ObjectBuy, 
//            ObjectCategory, ObjectClickAction, ObjectDeGrab, ObjectDelete, 
//            ObjectDelink, ObjectDescription, ObjectDeselect, ObjectDetach, 
//            ObjectDrop, ObjectDuplicate, ObjectDuplicateOnRay, ObjectExportSelected, 
//            ObjectExtraParams, ObjectFlagUpdate, ObjectGrab, ObjectGrabUpdate, 
//            ObjectGroup, ObjectImage, ObjectIncludeInSearch, ObjectLink, 
//            ObjectMaterial, ObjectName, ObjectOwner, ObjectPermissions, 
//            ObjectPosition, ObjectProperties, ObjectPropertiesFamily, ObjectRotation, 
//            ObjectSaleInfo, ObjectScale, ObjectSelect, ObjectShape, 
//            ObjectSpinStart, ObjectSpinStop, ObjectSpinUpdate, ObjectUpdate, 
//            ObjectUpdateCached, ObjectUpdateCompressed, OfferCallingCard, OfflineNotification, 
//            OnlineNotification, OpenCircuit, PacketAck, ParcelAccessListReply, 
//            ParcelAccessListRequest, ParcelAccessListUpdate, ParcelAuctions, ParcelBuy, 
//            ParcelBuyPass, ParcelClaim, ParcelDeedToGroup, ParcelDisableObjects, 
//            ParcelDivide, ParcelDwellReply, ParcelDwellRequest, ParcelGodForceOwner, 
//            ParcelGodMarkAsContent, ParcelInfoReply, ParcelInfoRequest, ParcelJoin, 
//            ParcelMediaCommandMessage, ParcelMediaUpdate, ParcelObjectOwnersReply, ParcelObjectOwnersRequest, 
//            ParcelOverlay, ParcelProperties, ParcelPropertiesRequest, ParcelPropertiesRequestByID, 
//            ParcelPropertiesUpdate, ParcelReclaim, ParcelRelease, ParcelRename, 
//            ParcelReturnObjects, ParcelSales, ParcelSelectObjects, ParcelSetOtherCleanTime, 
//            PayPriceReply, PickDelete, PickGodDelete, PickInfoReply, 
//            PickInfoUpdate, PlacesQuery, PlacesReply, PreloadSound, 
//            PurgeInventoryDescendents, RebakeAvatarTextures, Redo, RegionHandleRequest, 
//            RegionHandshake, RegionHandshakeReply, RegionIDAndHandleReply, RegionInfo, 
//            RegionPresenceRequestByHandle, RegionPresenceRequestByRegionID, RegionPresenceResponse, RemoveAttachment, 
//            RemoveInventoryFolder, RemoveInventoryItem, RemoveInventoryObjects, RemoveMuteListEntry, 
//            RemoveNameValuePair, RemoveParcel, RemoveTaskInventory, ReplyTaskInventory, 
//            ReportAutosaveCrash, RequestGodlikePowers, RequestImage, RequestInventoryAsset, 
//            RequestMultipleObjects, RequestObjectPropertiesFamily, RequestParcelTransfer, RequestPayPrice, 
//            RequestRegionInfo, RequestTaskInventory, RequestTrustedCircuit, RequestXfer, 
//            RetrieveInstantMessages, RevokePermissions, RezMultipleAttachmentsFromInv, RezObject, 
//            RezObjectFromNotecard, RezRestoreToWorld, RezScript, RezSingleAttachmentFromInv, 
//            RoutedMoneyBalanceReply, RpcChannelReply, RpcChannelRequest, RpcScriptReplyInbound, 
//            RpcScriptRequestInbound, RpcScriptRequestInboundForward, SaveAssetIntoInventory, ScriptAnswerYes, 
//            ScriptControlChange, ScriptDataReply, ScriptDataRequest, ScriptDialog, 
//            ScriptDialogReply, ScriptMailRegistration, ScriptQuestion, ScriptReset, 
//            ScriptRunningReply, ScriptSensorReply, ScriptSensorRequest, ScriptTeleportRequest, 
//            SendPostcard, SendXferPacket, SetAlwaysRun, SetCPURatio, 
//            SetFollowCamProperties, SetGroupAcceptNotices, SetGroupContribution, SetScriptRunning, 
//            SetSimPresenceInDatabase, SetSimStatusInDatabase, SetStartLocation, SetStartLocationRequest, 
//            SimCrashed, SimStats, SimStatus, SimWideDeletes, 
//            SimulatorLoad, SimulatorMapUpdate, SimulatorPresentAtLocation, SimulatorReady, 
//            SimulatorSetMap, SimulatorShutdownRequest, SimulatorViewerTimeMessage, SoundTrigger, 
//            StartAuction, StartGroupProposal, StartLure, StartPingCheck, 
//            StateSave, SubscribeLoad, SystemKickUser, SystemMessage, 
//            TallyVotes, TelehubInfo, TeleportCancel, TeleportFailed, 
//            TeleportFinish, TeleportLandingStatusChanged, TeleportLandmarkRequest, TeleportLocal, 
//            TeleportLocationRequest, TeleportLureRequest, TeleportProgress, TeleportRequest, 
//            TeleportStart, TerminateFriendship, TestMessage, TrackAgent, 
//            TransferAbort, TransferInfo, TransferInventory, TransferInventoryAck, 
//            TransferPacket, TransferRequest, UUIDGroupNameReply, UUIDGroupNameRequest, 
//            UUIDNameReply, UUIDNameRequest, Undo, UndoLand, 
//            UnsubscribeLoad, UpdateAttachment, UpdateCreateInventoryItem, UpdateGroupInfo, 
//            UpdateInventoryFolder, UpdateInventoryItem, UpdateMuteListEntry, UpdateParcel, 
//            UpdateSimulator, UpdateTaskInventory, UpdateUserInfo, UseCachedMuteList, 
//            UseCircuitCode, UserInfoReply, UserInfoRequest, UserReport, 
//            UserReportInternal, VelocityInterpolateOff, VelocityInterpolateOn, ViewerEffect, 
//            ViewerFrozenMessage, ViewerStartAuction, ViewerStats

public class SLMessageHandler
{

    public SLMessageHandler()
    {
    }

    public void DefaultMessageHandler(SLMessage slmessage)
    {
    }

    public void HandleAbortXfer(AbortXfer abortxfer)
    {
        DefaultMessageHandler(abortxfer);
    }

    public void HandleAcceptCallingCard(AcceptCallingCard acceptcallingcard)
    {
        DefaultMessageHandler(acceptcallingcard);
    }

    public void HandleAcceptFriendship(AcceptFriendship acceptfriendship)
    {
        DefaultMessageHandler(acceptfriendship);
    }

    public void HandleActivateGestures(ActivateGestures activategestures)
    {
        DefaultMessageHandler(activategestures);
    }

    public void HandleActivateGroup(ActivateGroup activategroup)
    {
        DefaultMessageHandler(activategroup);
    }

    public void HandleAddCircuitCode(AddCircuitCode addcircuitcode)
    {
        DefaultMessageHandler(addcircuitcode);
    }

    public void HandleAgentAlertMessage(AgentAlertMessage agentalertmessage)
    {
        DefaultMessageHandler(agentalertmessage);
    }

    public void HandleAgentAnimation(AgentAnimation agentanimation)
    {
        DefaultMessageHandler(agentanimation);
    }

    public void HandleAgentCachedTexture(AgentCachedTexture agentcachedtexture)
    {
        DefaultMessageHandler(agentcachedtexture);
    }

    public void HandleAgentCachedTextureResponse(AgentCachedTextureResponse agentcachedtextureresponse)
    {
        DefaultMessageHandler(agentcachedtextureresponse);
    }

    public void HandleAgentDataUpdate(AgentDataUpdate agentdataupdate)
    {
        DefaultMessageHandler(agentdataupdate);
    }

    public void HandleAgentDataUpdateRequest(AgentDataUpdateRequest agentdataupdaterequest)
    {
        DefaultMessageHandler(agentdataupdaterequest);
    }

    public void HandleAgentDropGroup(AgentDropGroup agentdropgroup)
    {
        DefaultMessageHandler(agentdropgroup);
    }

    public void HandleAgentFOV(AgentFOV agentfov)
    {
        DefaultMessageHandler(agentfov);
    }

    public void HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentgroupdataupdate)
    {
        DefaultMessageHandler(agentgroupdataupdate);
    }

    public void HandleAgentHeightWidth(AgentHeightWidth agentheightwidth)
    {
        DefaultMessageHandler(agentheightwidth);
    }

    public void HandleAgentIsNowWearing(AgentIsNowWearing agentisnowwearing)
    {
        DefaultMessageHandler(agentisnowwearing);
    }

    public void HandleAgentMovementComplete(AgentMovementComplete agentmovementcomplete)
    {
        DefaultMessageHandler(agentmovementcomplete);
    }

    public void HandleAgentPause(AgentPause agentpause)
    {
        DefaultMessageHandler(agentpause);
    }

    public void HandleAgentQuitCopy(AgentQuitCopy agentquitcopy)
    {
        DefaultMessageHandler(agentquitcopy);
    }

    public void HandleAgentRequestSit(AgentRequestSit agentrequestsit)
    {
        DefaultMessageHandler(agentrequestsit);
    }

    public void HandleAgentResume(AgentResume agentresume)
    {
        DefaultMessageHandler(agentresume);
    }

    public void HandleAgentSetAppearance(AgentSetAppearance agentsetappearance)
    {
        DefaultMessageHandler(agentsetappearance);
    }

    public void HandleAgentSit(AgentSit agentsit)
    {
        DefaultMessageHandler(agentsit);
    }

    public void HandleAgentThrottle(AgentThrottle agentthrottle)
    {
        DefaultMessageHandler(agentthrottle);
    }

    public void HandleAgentUpdate(AgentUpdate agentupdate)
    {
        DefaultMessageHandler(agentupdate);
    }

    public void HandleAgentWearablesRequest(AgentWearablesRequest agentwearablesrequest)
    {
        DefaultMessageHandler(agentwearablesrequest);
    }

    public void HandleAgentWearablesUpdate(AgentWearablesUpdate agentwearablesupdate)
    {
        DefaultMessageHandler(agentwearablesupdate);
    }

    public void HandleAlertMessage(AlertMessage alertmessage)
    {
        DefaultMessageHandler(alertmessage);
    }

    public void HandleAssetUploadComplete(AssetUploadComplete assetuploadcomplete)
    {
        DefaultMessageHandler(assetuploadcomplete);
    }

    public void HandleAssetUploadRequest(AssetUploadRequest assetuploadrequest)
    {
        DefaultMessageHandler(assetuploadrequest);
    }

    public void HandleAtomicPassObject(AtomicPassObject atomicpassobject)
    {
        DefaultMessageHandler(atomicpassobject);
    }

    public void HandleAttachedSound(AttachedSound attachedsound)
    {
        DefaultMessageHandler(attachedsound);
    }

    public void HandleAttachedSoundGainChange(AttachedSoundGainChange attachedsoundgainchange)
    {
        DefaultMessageHandler(attachedsoundgainchange);
    }

    public void HandleAvatarAnimation(AvatarAnimation avataranimation)
    {
        DefaultMessageHandler(avataranimation);
    }

    public void HandleAvatarAppearance(AvatarAppearance avatarappearance)
    {
        DefaultMessageHandler(avatarappearance);
    }

    public void HandleAvatarClassifiedReply(AvatarClassifiedReply avatarclassifiedreply)
    {
        DefaultMessageHandler(avatarclassifiedreply);
    }

    public void HandleAvatarGroupsReply(AvatarGroupsReply avatargroupsreply)
    {
        DefaultMessageHandler(avatargroupsreply);
    }

    public void HandleAvatarInterestsReply(AvatarInterestsReply avatarinterestsreply)
    {
        DefaultMessageHandler(avatarinterestsreply);
    }

    public void HandleAvatarInterestsUpdate(AvatarInterestsUpdate avatarinterestsupdate)
    {
        DefaultMessageHandler(avatarinterestsupdate);
    }

    public void HandleAvatarNotesReply(AvatarNotesReply avatarnotesreply)
    {
        DefaultMessageHandler(avatarnotesreply);
    }

    public void HandleAvatarNotesUpdate(AvatarNotesUpdate avatarnotesupdate)
    {
        DefaultMessageHandler(avatarnotesupdate);
    }

    public void HandleAvatarPickerReply(AvatarPickerReply avatarpickerreply)
    {
        DefaultMessageHandler(avatarpickerreply);
    }

    public void HandleAvatarPickerRequest(AvatarPickerRequest avatarpickerrequest)
    {
        DefaultMessageHandler(avatarpickerrequest);
    }

    public void HandleAvatarPickerRequestBackend(AvatarPickerRequestBackend avatarpickerrequestbackend)
    {
        DefaultMessageHandler(avatarpickerrequestbackend);
    }

    public void HandleAvatarPicksReply(AvatarPicksReply avatarpicksreply)
    {
        DefaultMessageHandler(avatarpicksreply);
    }

    public void HandleAvatarPropertiesReply(AvatarPropertiesReply avatarpropertiesreply)
    {
        DefaultMessageHandler(avatarpropertiesreply);
    }

    public void HandleAvatarPropertiesRequest(AvatarPropertiesRequest avatarpropertiesrequest)
    {
        DefaultMessageHandler(avatarpropertiesrequest);
    }

    public void HandleAvatarPropertiesRequestBackend(AvatarPropertiesRequestBackend avatarpropertiesrequestbackend)
    {
        DefaultMessageHandler(avatarpropertiesrequestbackend);
    }

    public void HandleAvatarPropertiesUpdate(AvatarPropertiesUpdate avatarpropertiesupdate)
    {
        DefaultMessageHandler(avatarpropertiesupdate);
    }

    public void HandleAvatarSitResponse(AvatarSitResponse avatarsitresponse)
    {
        DefaultMessageHandler(avatarsitresponse);
    }

    public void HandleAvatarTextureUpdate(AvatarTextureUpdate avatartextureupdate)
    {
        DefaultMessageHandler(avatartextureupdate);
    }

    public void HandleBulkUpdateInventory(BulkUpdateInventory bulkupdateinventory)
    {
        DefaultMessageHandler(bulkupdateinventory);
    }

    public void HandleBuyObjectInventory(BuyObjectInventory buyobjectinventory)
    {
        DefaultMessageHandler(buyobjectinventory);
    }

    public void HandleCameraConstraint(CameraConstraint cameraconstraint)
    {
        DefaultMessageHandler(cameraconstraint);
    }

    public void HandleCancelAuction(CancelAuction cancelauction)
    {
        DefaultMessageHandler(cancelauction);
    }

    public void HandleChangeInventoryItemFlags(ChangeInventoryItemFlags changeinventoryitemflags)
    {
        DefaultMessageHandler(changeinventoryitemflags);
    }

    public void HandleChangeUserRights(ChangeUserRights changeuserrights)
    {
        DefaultMessageHandler(changeuserrights);
    }

    public void HandleChatFromSimulator(ChatFromSimulator chatfromsimulator)
    {
        DefaultMessageHandler(chatfromsimulator);
    }

    public void HandleChatFromViewer(ChatFromViewer chatfromviewer)
    {
        DefaultMessageHandler(chatfromviewer);
    }

    public void HandleChatPass(ChatPass chatpass)
    {
        DefaultMessageHandler(chatpass);
    }

    public void HandleCheckParcelAuctions(CheckParcelAuctions checkparcelauctions)
    {
        DefaultMessageHandler(checkparcelauctions);
    }

    public void HandleCheckParcelSales(CheckParcelSales checkparcelsales)
    {
        DefaultMessageHandler(checkparcelsales);
    }

    public void HandleChildAgentAlive(ChildAgentAlive childagentalive)
    {
        DefaultMessageHandler(childagentalive);
    }

    public void HandleChildAgentDying(ChildAgentDying childagentdying)
    {
        DefaultMessageHandler(childagentdying);
    }

    public void HandleChildAgentPositionUpdate(ChildAgentPositionUpdate childagentpositionupdate)
    {
        DefaultMessageHandler(childagentpositionupdate);
    }

    public void HandleChildAgentUnknown(ChildAgentUnknown childagentunknown)
    {
        DefaultMessageHandler(childagentunknown);
    }

    public void HandleChildAgentUpdate(ChildAgentUpdate childagentupdate)
    {
        DefaultMessageHandler(childagentupdate);
    }

    public void HandleClassifiedDelete(ClassifiedDelete classifieddelete)
    {
        DefaultMessageHandler(classifieddelete);
    }

    public void HandleClassifiedGodDelete(ClassifiedGodDelete classifiedgoddelete)
    {
        DefaultMessageHandler(classifiedgoddelete);
    }

    public void HandleClassifiedInfoReply(ClassifiedInfoReply classifiedinforeply)
    {
        DefaultMessageHandler(classifiedinforeply);
    }

    public void HandleClassifiedInfoRequest(ClassifiedInfoRequest classifiedinforequest)
    {
        DefaultMessageHandler(classifiedinforequest);
    }

    public void HandleClassifiedInfoUpdate(ClassifiedInfoUpdate classifiedinfoupdate)
    {
        DefaultMessageHandler(classifiedinfoupdate);
    }

    public void HandleClearFollowCamProperties(ClearFollowCamProperties clearfollowcamproperties)
    {
        DefaultMessageHandler(clearfollowcamproperties);
    }

    public void HandleCloseCircuit(CloseCircuit closecircuit)
    {
        DefaultMessageHandler(closecircuit);
    }

    public void HandleCoarseLocationUpdate(CoarseLocationUpdate coarselocationupdate)
    {
        DefaultMessageHandler(coarselocationupdate);
    }

    public void HandleCompleteAgentMovement(CompleteAgentMovement completeagentmovement)
    {
        DefaultMessageHandler(completeagentmovement);
    }

    public void HandleCompleteAuction(CompleteAuction completeauction)
    {
        DefaultMessageHandler(completeauction);
    }

    public void HandleCompletePingCheck(CompletePingCheck completepingcheck)
    {
        DefaultMessageHandler(completepingcheck);
    }

    public void HandleConfirmAuctionStart(ConfirmAuctionStart confirmauctionstart)
    {
        DefaultMessageHandler(confirmauctionstart);
    }

    public void HandleConfirmEnableSimulator(ConfirmEnableSimulator confirmenablesimulator)
    {
        DefaultMessageHandler(confirmenablesimulator);
    }

    public void HandleConfirmXferPacket(ConfirmXferPacket confirmxferpacket)
    {
        DefaultMessageHandler(confirmxferpacket);
    }

    public void HandleCopyInventoryFromNotecard(CopyInventoryFromNotecard copyinventoryfromnotecard)
    {
        DefaultMessageHandler(copyinventoryfromnotecard);
    }

    public void HandleCopyInventoryItem(CopyInventoryItem copyinventoryitem)
    {
        DefaultMessageHandler(copyinventoryitem);
    }

    public void HandleCreateGroupReply(CreateGroupReply creategroupreply)
    {
        DefaultMessageHandler(creategroupreply);
    }

    public void HandleCreateGroupRequest(CreateGroupRequest creategrouprequest)
    {
        DefaultMessageHandler(creategrouprequest);
    }

    public void HandleCreateInventoryFolder(CreateInventoryFolder createinventoryfolder)
    {
        DefaultMessageHandler(createinventoryfolder);
    }

    public void HandleCreateInventoryItem(CreateInventoryItem createinventoryitem)
    {
        DefaultMessageHandler(createinventoryitem);
    }

    public void HandleCreateLandmarkForEvent(CreateLandmarkForEvent createlandmarkforevent)
    {
        DefaultMessageHandler(createlandmarkforevent);
    }

    public void HandleCreateNewOutfitAttachments(CreateNewOutfitAttachments createnewoutfitattachments)
    {
        DefaultMessageHandler(createnewoutfitattachments);
    }

    public void HandleCreateTrustedCircuit(CreateTrustedCircuit createtrustedcircuit)
    {
        DefaultMessageHandler(createtrustedcircuit);
    }

    public void HandleCrossedRegion(CrossedRegion crossedregion)
    {
        DefaultMessageHandler(crossedregion);
    }

    public void HandleDataHomeLocationReply(DataHomeLocationReply datahomelocationreply)
    {
        DefaultMessageHandler(datahomelocationreply);
    }

    public void HandleDataHomeLocationRequest(DataHomeLocationRequest datahomelocationrequest)
    {
        DefaultMessageHandler(datahomelocationrequest);
    }

    public void HandleDataServerLogout(DataServerLogout dataserverlogout)
    {
        DefaultMessageHandler(dataserverlogout);
    }

    public void HandleDeRezAck(DeRezAck derezack)
    {
        DefaultMessageHandler(derezack);
    }

    public void HandleDeRezObject(DeRezObject derezobject)
    {
        DefaultMessageHandler(derezobject);
    }

    public void HandleDeactivateGestures(DeactivateGestures deactivategestures)
    {
        DefaultMessageHandler(deactivategestures);
    }

    public void HandleDeclineCallingCard(DeclineCallingCard declinecallingcard)
    {
        DefaultMessageHandler(declinecallingcard);
    }

    public void HandleDeclineFriendship(DeclineFriendship declinefriendship)
    {
        DefaultMessageHandler(declinefriendship);
    }

    public void HandleDenyTrustedCircuit(DenyTrustedCircuit denytrustedcircuit)
    {
        DefaultMessageHandler(denytrustedcircuit);
    }

    public void HandleDerezContainer(DerezContainer derezcontainer)
    {
        DefaultMessageHandler(derezcontainer);
    }

    public void HandleDetachAttachmentIntoInv(DetachAttachmentIntoInv detachattachmentintoinv)
    {
        DefaultMessageHandler(detachattachmentintoinv);
    }

    public void HandleDirClassifiedQuery(DirClassifiedQuery dirclassifiedquery)
    {
        DefaultMessageHandler(dirclassifiedquery);
    }

    public void HandleDirClassifiedQueryBackend(DirClassifiedQueryBackend dirclassifiedquerybackend)
    {
        DefaultMessageHandler(dirclassifiedquerybackend);
    }

    public void HandleDirClassifiedReply(DirClassifiedReply dirclassifiedreply)
    {
        DefaultMessageHandler(dirclassifiedreply);
    }

    public void HandleDirEventsReply(DirEventsReply direventsreply)
    {
        DefaultMessageHandler(direventsreply);
    }

    public void HandleDirFindQuery(DirFindQuery dirfindquery)
    {
        DefaultMessageHandler(dirfindquery);
    }

    public void HandleDirFindQueryBackend(DirFindQueryBackend dirfindquerybackend)
    {
        DefaultMessageHandler(dirfindquerybackend);
    }

    public void HandleDirGroupsReply(DirGroupsReply dirgroupsreply)
    {
        DefaultMessageHandler(dirgroupsreply);
    }

    public void HandleDirLandQuery(DirLandQuery dirlandquery)
    {
        DefaultMessageHandler(dirlandquery);
    }

    public void HandleDirLandQueryBackend(DirLandQueryBackend dirlandquerybackend)
    {
        DefaultMessageHandler(dirlandquerybackend);
    }

    public void HandleDirLandReply(DirLandReply dirlandreply)
    {
        DefaultMessageHandler(dirlandreply);
    }

    public void HandleDirPeopleReply(DirPeopleReply dirpeoplereply)
    {
        DefaultMessageHandler(dirpeoplereply);
    }

    public void HandleDirPlacesQuery(DirPlacesQuery dirplacesquery)
    {
        DefaultMessageHandler(dirplacesquery);
    }

    public void HandleDirPlacesQueryBackend(DirPlacesQueryBackend dirplacesquerybackend)
    {
        DefaultMessageHandler(dirplacesquerybackend);
    }

    public void HandleDirPlacesReply(DirPlacesReply dirplacesreply)
    {
        DefaultMessageHandler(dirplacesreply);
    }

    public void HandleDirPopularQuery(DirPopularQuery dirpopularquery)
    {
        DefaultMessageHandler(dirpopularquery);
    }

    public void HandleDirPopularQueryBackend(DirPopularQueryBackend dirpopularquerybackend)
    {
        DefaultMessageHandler(dirpopularquerybackend);
    }

    public void HandleDirPopularReply(DirPopularReply dirpopularreply)
    {
        DefaultMessageHandler(dirpopularreply);
    }

    public void HandleDisableSimulator(DisableSimulator disablesimulator)
    {
        DefaultMessageHandler(disablesimulator);
    }

    public void HandleEconomyData(EconomyData economydata)
    {
        DefaultMessageHandler(economydata);
    }

    public void HandleEconomyDataRequest(EconomyDataRequest economydatarequest)
    {
        DefaultMessageHandler(economydatarequest);
    }

    public void HandleEdgeDataPacket(EdgeDataPacket edgedatapacket)
    {
        DefaultMessageHandler(edgedatapacket);
    }

    public void HandleEjectGroupMemberReply(EjectGroupMemberReply ejectgroupmemberreply)
    {
        DefaultMessageHandler(ejectgroupmemberreply);
    }

    public void HandleEjectGroupMemberRequest(EjectGroupMemberRequest ejectgroupmemberrequest)
    {
        DefaultMessageHandler(ejectgroupmemberrequest);
    }

    public void HandleEjectUser(EjectUser ejectuser)
    {
        DefaultMessageHandler(ejectuser);
    }

    public void HandleEmailMessageReply(EmailMessageReply emailmessagereply)
    {
        DefaultMessageHandler(emailmessagereply);
    }

    public void HandleEmailMessageRequest(EmailMessageRequest emailmessagerequest)
    {
        DefaultMessageHandler(emailmessagerequest);
    }

    public void HandleEnableSimulator(EnableSimulator enablesimulator)
    {
        DefaultMessageHandler(enablesimulator);
    }

    public void HandleError(Error error)
    {
        DefaultMessageHandler(error);
    }

    public void HandleEstateCovenantReply(EstateCovenantReply estatecovenantreply)
    {
        DefaultMessageHandler(estatecovenantreply);
    }

    public void HandleEstateCovenantRequest(EstateCovenantRequest estatecovenantrequest)
    {
        DefaultMessageHandler(estatecovenantrequest);
    }

    public void HandleEstateOwnerMessage(EstateOwnerMessage estateownermessage)
    {
        DefaultMessageHandler(estateownermessage);
    }

    public void HandleEventGodDelete(EventGodDelete eventgoddelete)
    {
        DefaultMessageHandler(eventgoddelete);
    }

    public void HandleEventInfoReply(EventInfoReply eventinforeply)
    {
        DefaultMessageHandler(eventinforeply);
    }

    public void HandleEventInfoRequest(EventInfoRequest eventinforequest)
    {
        DefaultMessageHandler(eventinforequest);
    }

    public void HandleEventLocationReply(EventLocationReply eventlocationreply)
    {
        DefaultMessageHandler(eventlocationreply);
    }

    public void HandleEventLocationRequest(EventLocationRequest eventlocationrequest)
    {
        DefaultMessageHandler(eventlocationrequest);
    }

    public void HandleEventNotificationAddRequest(EventNotificationAddRequest eventnotificationaddrequest)
    {
        DefaultMessageHandler(eventnotificationaddrequest);
    }

    public void HandleEventNotificationRemoveRequest(EventNotificationRemoveRequest eventnotificationremoverequest)
    {
        DefaultMessageHandler(eventnotificationremoverequest);
    }

    public void HandleFeatureDisabled(FeatureDisabled featuredisabled)
    {
        DefaultMessageHandler(featuredisabled);
    }

    public void HandleFetchInventory(FetchInventory fetchinventory)
    {
        DefaultMessageHandler(fetchinventory);
    }

    public void HandleFetchInventoryDescendents(FetchInventoryDescendents fetchinventorydescendents)
    {
        DefaultMessageHandler(fetchinventorydescendents);
    }

    public void HandleFetchInventoryReply(FetchInventoryReply fetchinventoryreply)
    {
        DefaultMessageHandler(fetchinventoryreply);
    }

    public void HandleFindAgent(FindAgent findagent)
    {
        DefaultMessageHandler(findagent);
    }

    public void HandleForceObjectSelect(ForceObjectSelect forceobjectselect)
    {
        DefaultMessageHandler(forceobjectselect);
    }

    public void HandleForceScriptControlRelease(ForceScriptControlRelease forcescriptcontrolrelease)
    {
        DefaultMessageHandler(forcescriptcontrolrelease);
    }

    public void HandleFormFriendship(FormFriendship formfriendship)
    {
        DefaultMessageHandler(formfriendship);
    }

    public void HandleFreezeUser(FreezeUser freezeuser)
    {
        DefaultMessageHandler(freezeuser);
    }

    public void HandleGenericMessage(GenericMessage genericmessage)
    {
        DefaultMessageHandler(genericmessage);
    }

    public void HandleGetScriptRunning(GetScriptRunning getscriptrunning)
    {
        DefaultMessageHandler(getscriptrunning);
    }

    public void HandleGodKickUser(GodKickUser godkickuser)
    {
        DefaultMessageHandler(godkickuser);
    }

    public void HandleGodUpdateRegionInfo(GodUpdateRegionInfo godupdateregioninfo)
    {
        DefaultMessageHandler(godupdateregioninfo);
    }

    public void HandleGodlikeMessage(GodlikeMessage godlikemessage)
    {
        DefaultMessageHandler(godlikemessage);
    }

    public void HandleGrantGodlikePowers(GrantGodlikePowers grantgodlikepowers)
    {
        DefaultMessageHandler(grantgodlikepowers);
    }

    public void HandleGrantUserRights(GrantUserRights grantuserrights)
    {
        DefaultMessageHandler(grantuserrights);
    }

    public void HandleGroupAccountDetailsReply(GroupAccountDetailsReply groupaccountdetailsreply)
    {
        DefaultMessageHandler(groupaccountdetailsreply);
    }

    public void HandleGroupAccountDetailsRequest(GroupAccountDetailsRequest groupaccountdetailsrequest)
    {
        DefaultMessageHandler(groupaccountdetailsrequest);
    }

    public void HandleGroupAccountSummaryReply(GroupAccountSummaryReply groupaccountsummaryreply)
    {
        DefaultMessageHandler(groupaccountsummaryreply);
    }

    public void HandleGroupAccountSummaryRequest(GroupAccountSummaryRequest groupaccountsummaryrequest)
    {
        DefaultMessageHandler(groupaccountsummaryrequest);
    }

    public void HandleGroupAccountTransactionsReply(GroupAccountTransactionsReply groupaccounttransactionsreply)
    {
        DefaultMessageHandler(groupaccounttransactionsreply);
    }

    public void HandleGroupAccountTransactionsRequest(GroupAccountTransactionsRequest groupaccounttransactionsrequest)
    {
        DefaultMessageHandler(groupaccounttransactionsrequest);
    }

    public void HandleGroupActiveProposalItemReply(GroupActiveProposalItemReply groupactiveproposalitemreply)
    {
        DefaultMessageHandler(groupactiveproposalitemreply);
    }

    public void HandleGroupActiveProposalsRequest(GroupActiveProposalsRequest groupactiveproposalsrequest)
    {
        DefaultMessageHandler(groupactiveproposalsrequest);
    }

    public void HandleGroupDataUpdate(GroupDataUpdate groupdataupdate)
    {
        DefaultMessageHandler(groupdataupdate);
    }

    public void HandleGroupMembersReply(GroupMembersReply groupmembersreply)
    {
        DefaultMessageHandler(groupmembersreply);
    }

    public void HandleGroupMembersRequest(GroupMembersRequest groupmembersrequest)
    {
        DefaultMessageHandler(groupmembersrequest);
    }

    public void HandleGroupNoticeAdd(GroupNoticeAdd groupnoticeadd)
    {
        DefaultMessageHandler(groupnoticeadd);
    }

    public void HandleGroupNoticeRequest(GroupNoticeRequest groupnoticerequest)
    {
        DefaultMessageHandler(groupnoticerequest);
    }

    public void HandleGroupNoticesListReply(GroupNoticesListReply groupnoticeslistreply)
    {
        DefaultMessageHandler(groupnoticeslistreply);
    }

    public void HandleGroupNoticesListRequest(GroupNoticesListRequest groupnoticeslistrequest)
    {
        DefaultMessageHandler(groupnoticeslistrequest);
    }

    public void HandleGroupProfileReply(GroupProfileReply groupprofilereply)
    {
        DefaultMessageHandler(groupprofilereply);
    }

    public void HandleGroupProfileRequest(GroupProfileRequest groupprofilerequest)
    {
        DefaultMessageHandler(groupprofilerequest);
    }

    public void HandleGroupProposalBallot(GroupProposalBallot groupproposalballot)
    {
        DefaultMessageHandler(groupproposalballot);
    }

    public void HandleGroupRoleChanges(GroupRoleChanges grouprolechanges)
    {
        DefaultMessageHandler(grouprolechanges);
    }

    public void HandleGroupRoleDataReply(GroupRoleDataReply grouproledatareply)
    {
        DefaultMessageHandler(grouproledatareply);
    }

    public void HandleGroupRoleDataRequest(GroupRoleDataRequest grouproledatarequest)
    {
        DefaultMessageHandler(grouproledatarequest);
    }

    public void HandleGroupRoleMembersReply(GroupRoleMembersReply grouprolemembersreply)
    {
        DefaultMessageHandler(grouprolemembersreply);
    }

    public void HandleGroupRoleMembersRequest(GroupRoleMembersRequest grouprolemembersrequest)
    {
        DefaultMessageHandler(grouprolemembersrequest);
    }

    public void HandleGroupRoleUpdate(GroupRoleUpdate grouproleupdate)
    {
        DefaultMessageHandler(grouproleupdate);
    }

    public void HandleGroupTitleUpdate(GroupTitleUpdate grouptitleupdate)
    {
        DefaultMessageHandler(grouptitleupdate);
    }

    public void HandleGroupTitlesReply(GroupTitlesReply grouptitlesreply)
    {
        DefaultMessageHandler(grouptitlesreply);
    }

    public void HandleGroupTitlesRequest(GroupTitlesRequest grouptitlesrequest)
    {
        DefaultMessageHandler(grouptitlesrequest);
    }

    public void HandleGroupVoteHistoryItemReply(GroupVoteHistoryItemReply groupvotehistoryitemreply)
    {
        DefaultMessageHandler(groupvotehistoryitemreply);
    }

    public void HandleGroupVoteHistoryRequest(GroupVoteHistoryRequest groupvotehistoryrequest)
    {
        DefaultMessageHandler(groupvotehistoryrequest);
    }

    public void HandleHealthMessage(HealthMessage healthmessage)
    {
        DefaultMessageHandler(healthmessage);
    }

    public void HandleImageData(ImageData imagedata)
    {
        DefaultMessageHandler(imagedata);
    }

    public void HandleImageNotInDatabase(ImageNotInDatabase imagenotindatabase)
    {
        DefaultMessageHandler(imagenotindatabase);
    }

    public void HandleImagePacket(ImagePacket imagepacket)
    {
        DefaultMessageHandler(imagepacket);
    }

    public void HandleImprovedInstantMessage(ImprovedInstantMessage improvedinstantmessage)
    {
        DefaultMessageHandler(improvedinstantmessage);
    }

    public void HandleImprovedTerseObjectUpdate(ImprovedTerseObjectUpdate improvedterseobjectupdate)
    {
        DefaultMessageHandler(improvedterseobjectupdate);
    }

    public void HandleInitiateDownload(InitiateDownload initiatedownload)
    {
        DefaultMessageHandler(initiatedownload);
    }

    public void HandleInternalScriptMail(InternalScriptMail internalscriptmail)
    {
        DefaultMessageHandler(internalscriptmail);
    }

    public void HandleInventoryAssetResponse(InventoryAssetResponse inventoryassetresponse)
    {
        DefaultMessageHandler(inventoryassetresponse);
    }

    public void HandleInventoryDescendents(InventoryDescendents inventorydescendents)
    {
        DefaultMessageHandler(inventorydescendents);
    }

    public void HandleInviteGroupRequest(InviteGroupRequest invitegrouprequest)
    {
        DefaultMessageHandler(invitegrouprequest);
    }

    public void HandleInviteGroupResponse(InviteGroupResponse invitegroupresponse)
    {
        DefaultMessageHandler(invitegroupresponse);
    }

    public void HandleJoinGroupReply(JoinGroupReply joingroupreply)
    {
        DefaultMessageHandler(joingroupreply);
    }

    public void HandleJoinGroupRequest(JoinGroupRequest joingrouprequest)
    {
        DefaultMessageHandler(joingrouprequest);
    }

    public void HandleKickUser(KickUser kickuser)
    {
        DefaultMessageHandler(kickuser);
    }

    public void HandleKickUserAck(KickUserAck kickuserack)
    {
        DefaultMessageHandler(kickuserack);
    }

    public void HandleKillChildAgents(KillChildAgents killchildagents)
    {
        DefaultMessageHandler(killchildagents);
    }

    public void HandleKillObject(KillObject killobject)
    {
        DefaultMessageHandler(killobject);
    }

    public void HandleLandStatReply(LandStatReply landstatreply)
    {
        DefaultMessageHandler(landstatreply);
    }

    public void HandleLandStatRequest(LandStatRequest landstatrequest)
    {
        DefaultMessageHandler(landstatrequest);
    }

    public void HandleLayerData(LayerData layerdata)
    {
        DefaultMessageHandler(layerdata);
    }

    public void HandleLeaveGroupReply(LeaveGroupReply leavegroupreply)
    {
        DefaultMessageHandler(leavegroupreply);
    }

    public void HandleLeaveGroupRequest(LeaveGroupRequest leavegrouprequest)
    {
        DefaultMessageHandler(leavegrouprequest);
    }

    public void HandleLinkInventoryItem(LinkInventoryItem linkinventoryitem)
    {
        DefaultMessageHandler(linkinventoryitem);
    }

    public void HandleLiveHelpGroupReply(LiveHelpGroupReply livehelpgroupreply)
    {
        DefaultMessageHandler(livehelpgroupreply);
    }

    public void HandleLiveHelpGroupRequest(LiveHelpGroupRequest livehelpgrouprequest)
    {
        DefaultMessageHandler(livehelpgrouprequest);
    }

    public void HandleLoadURL(LoadURL loadurl)
    {
        DefaultMessageHandler(loadurl);
    }

    public void HandleLogDwellTime(LogDwellTime logdwelltime)
    {
        DefaultMessageHandler(logdwelltime);
    }

    public void HandleLogFailedMoneyTransaction(LogFailedMoneyTransaction logfailedmoneytransaction)
    {
        DefaultMessageHandler(logfailedmoneytransaction);
    }

    public void HandleLogParcelChanges(LogParcelChanges logparcelchanges)
    {
        DefaultMessageHandler(logparcelchanges);
    }

    public void HandleLogTextMessage(LogTextMessage logtextmessage)
    {
        DefaultMessageHandler(logtextmessage);
    }

    public void HandleLogoutReply(LogoutReply logoutreply)
    {
        DefaultMessageHandler(logoutreply);
    }

    public void HandleLogoutRequest(LogoutRequest logoutrequest)
    {
        DefaultMessageHandler(logoutrequest);
    }

    public void HandleMapBlockReply(MapBlockReply mapblockreply)
    {
        DefaultMessageHandler(mapblockreply);
    }

    public void HandleMapBlockRequest(MapBlockRequest mapblockrequest)
    {
        DefaultMessageHandler(mapblockrequest);
    }

    public void HandleMapItemReply(MapItemReply mapitemreply)
    {
        DefaultMessageHandler(mapitemreply);
    }

    public void HandleMapItemRequest(MapItemRequest mapitemrequest)
    {
        DefaultMessageHandler(mapitemrequest);
    }

    public void HandleMapLayerReply(MapLayerReply maplayerreply)
    {
        DefaultMessageHandler(maplayerreply);
    }

    public void HandleMapLayerRequest(MapLayerRequest maplayerrequest)
    {
        DefaultMessageHandler(maplayerrequest);
    }

    public void HandleMapNameRequest(MapNameRequest mapnamerequest)
    {
        DefaultMessageHandler(mapnamerequest);
    }

    public void HandleMeanCollisionAlert(MeanCollisionAlert meancollisionalert)
    {
        DefaultMessageHandler(meancollisionalert);
    }

    public void HandleMergeParcel(MergeParcel mergeparcel)
    {
        DefaultMessageHandler(mergeparcel);
    }

    public void HandleModifyLand(ModifyLand modifyland)
    {
        DefaultMessageHandler(modifyland);
    }

    public void HandleMoneyBalanceReply(MoneyBalanceReply moneybalancereply)
    {
        DefaultMessageHandler(moneybalancereply);
    }

    public void HandleMoneyBalanceRequest(MoneyBalanceRequest moneybalancerequest)
    {
        DefaultMessageHandler(moneybalancerequest);
    }

    public void HandleMoneyTransferBackend(MoneyTransferBackend moneytransferbackend)
    {
        DefaultMessageHandler(moneytransferbackend);
    }

    public void HandleMoneyTransferRequest(MoneyTransferRequest moneytransferrequest)
    {
        DefaultMessageHandler(moneytransferrequest);
    }

    public void HandleMoveInventoryFolder(MoveInventoryFolder moveinventoryfolder)
    {
        DefaultMessageHandler(moveinventoryfolder);
    }

    public void HandleMoveInventoryItem(MoveInventoryItem moveinventoryitem)
    {
        DefaultMessageHandler(moveinventoryitem);
    }

    public void HandleMoveTaskInventory(MoveTaskInventory movetaskinventory)
    {
        DefaultMessageHandler(movetaskinventory);
    }

    public void HandleMultipleObjectUpdate(MultipleObjectUpdate multipleobjectupdate)
    {
        DefaultMessageHandler(multipleobjectupdate);
    }

    public void HandleMuteListRequest(MuteListRequest mutelistrequest)
    {
        DefaultMessageHandler(mutelistrequest);
    }

    public void HandleMuteListUpdate(MuteListUpdate mutelistupdate)
    {
        DefaultMessageHandler(mutelistupdate);
    }

    public void HandleNameValuePair(NameValuePair namevaluepair)
    {
        DefaultMessageHandler(namevaluepair);
    }

    public void HandleNearestLandingRegionReply(NearestLandingRegionReply nearestlandingregionreply)
    {
        DefaultMessageHandler(nearestlandingregionreply);
    }

    public void HandleNearestLandingRegionRequest(NearestLandingRegionRequest nearestlandingregionrequest)
    {
        DefaultMessageHandler(nearestlandingregionrequest);
    }

    public void HandleNearestLandingRegionUpdated(NearestLandingRegionUpdated nearestlandingregionupdated)
    {
        DefaultMessageHandler(nearestlandingregionupdated);
    }

    public void HandleNeighborList(NeighborList neighborlist)
    {
        DefaultMessageHandler(neighborlist);
    }

    public void HandleNetTest(NetTest nettest)
    {
        DefaultMessageHandler(nettest);
    }

    public void HandleObjectAdd(ObjectAdd objectadd)
    {
        DefaultMessageHandler(objectadd);
    }

    public void HandleObjectAttach(ObjectAttach objectattach)
    {
        DefaultMessageHandler(objectattach);
    }

    public void HandleObjectBuy(ObjectBuy objectbuy)
    {
        DefaultMessageHandler(objectbuy);
    }

    public void HandleObjectCategory(ObjectCategory objectcategory)
    {
        DefaultMessageHandler(objectcategory);
    }

    public void HandleObjectClickAction(ObjectClickAction objectclickaction)
    {
        DefaultMessageHandler(objectclickaction);
    }

    public void HandleObjectDeGrab(ObjectDeGrab objectdegrab)
    {
        DefaultMessageHandler(objectdegrab);
    }

    public void HandleObjectDelete(ObjectDelete objectdelete)
    {
        DefaultMessageHandler(objectdelete);
    }

    public void HandleObjectDelink(ObjectDelink objectdelink)
    {
        DefaultMessageHandler(objectdelink);
    }

    public void HandleObjectDescription(ObjectDescription objectdescription)
    {
        DefaultMessageHandler(objectdescription);
    }

    public void HandleObjectDeselect(ObjectDeselect objectdeselect)
    {
        DefaultMessageHandler(objectdeselect);
    }

    public void HandleObjectDetach(ObjectDetach objectdetach)
    {
        DefaultMessageHandler(objectdetach);
    }

    public void HandleObjectDrop(ObjectDrop objectdrop)
    {
        DefaultMessageHandler(objectdrop);
    }

    public void HandleObjectDuplicate(ObjectDuplicate objectduplicate)
    {
        DefaultMessageHandler(objectduplicate);
    }

    public void HandleObjectDuplicateOnRay(ObjectDuplicateOnRay objectduplicateonray)
    {
        DefaultMessageHandler(objectduplicateonray);
    }

    public void HandleObjectExportSelected(ObjectExportSelected objectexportselected)
    {
        DefaultMessageHandler(objectexportselected);
    }

    public void HandleObjectExtraParams(ObjectExtraParams objectextraparams)
    {
        DefaultMessageHandler(objectextraparams);
    }

    public void HandleObjectFlagUpdate(ObjectFlagUpdate objectflagupdate)
    {
        DefaultMessageHandler(objectflagupdate);
    }

    public void HandleObjectGrab(ObjectGrab objectgrab)
    {
        DefaultMessageHandler(objectgrab);
    }

    public void HandleObjectGrabUpdate(ObjectGrabUpdate objectgrabupdate)
    {
        DefaultMessageHandler(objectgrabupdate);
    }

    public void HandleObjectGroup(ObjectGroup objectgroup)
    {
        DefaultMessageHandler(objectgroup);
    }

    public void HandleObjectImage(ObjectImage objectimage)
    {
        DefaultMessageHandler(objectimage);
    }

    public void HandleObjectIncludeInSearch(ObjectIncludeInSearch objectincludeinsearch)
    {
        DefaultMessageHandler(objectincludeinsearch);
    }

    public void HandleObjectLink(ObjectLink objectlink)
    {
        DefaultMessageHandler(objectlink);
    }

    public void HandleObjectMaterial(ObjectMaterial objectmaterial)
    {
        DefaultMessageHandler(objectmaterial);
    }

    public void HandleObjectName(ObjectName objectname)
    {
        DefaultMessageHandler(objectname);
    }

    public void HandleObjectOwner(ObjectOwner objectowner)
    {
        DefaultMessageHandler(objectowner);
    }

    public void HandleObjectPermissions(ObjectPermissions objectpermissions)
    {
        DefaultMessageHandler(objectpermissions);
    }

    public void HandleObjectPosition(ObjectPosition objectposition)
    {
        DefaultMessageHandler(objectposition);
    }

    public void HandleObjectProperties(ObjectProperties objectproperties)
    {
        DefaultMessageHandler(objectproperties);
    }

    public void HandleObjectPropertiesFamily(ObjectPropertiesFamily objectpropertiesfamily)
    {
        DefaultMessageHandler(objectpropertiesfamily);
    }

    public void HandleObjectRotation(ObjectRotation objectrotation)
    {
        DefaultMessageHandler(objectrotation);
    }

    public void HandleObjectSaleInfo(ObjectSaleInfo objectsaleinfo)
    {
        DefaultMessageHandler(objectsaleinfo);
    }

    public void HandleObjectScale(ObjectScale objectscale)
    {
        DefaultMessageHandler(objectscale);
    }

    public void HandleObjectSelect(ObjectSelect objectselect)
    {
        DefaultMessageHandler(objectselect);
    }

    public void HandleObjectShape(ObjectShape objectshape)
    {
        DefaultMessageHandler(objectshape);
    }

    public void HandleObjectSpinStart(ObjectSpinStart objectspinstart)
    {
        DefaultMessageHandler(objectspinstart);
    }

    public void HandleObjectSpinStop(ObjectSpinStop objectspinstop)
    {
        DefaultMessageHandler(objectspinstop);
    }

    public void HandleObjectSpinUpdate(ObjectSpinUpdate objectspinupdate)
    {
        DefaultMessageHandler(objectspinupdate);
    }

    public void HandleObjectUpdate(ObjectUpdate objectupdate)
    {
        DefaultMessageHandler(objectupdate);
    }

    public void HandleObjectUpdateCached(ObjectUpdateCached objectupdatecached)
    {
        DefaultMessageHandler(objectupdatecached);
    }

    public void HandleObjectUpdateCompressed(ObjectUpdateCompressed objectupdatecompressed)
    {
        DefaultMessageHandler(objectupdatecompressed);
    }

    public void HandleOfferCallingCard(OfferCallingCard offercallingcard)
    {
        DefaultMessageHandler(offercallingcard);
    }

    public void HandleOfflineNotification(OfflineNotification offlinenotification)
    {
        DefaultMessageHandler(offlinenotification);
    }

    public void HandleOnlineNotification(OnlineNotification onlinenotification)
    {
        DefaultMessageHandler(onlinenotification);
    }

    public void HandleOpenCircuit(OpenCircuit opencircuit)
    {
        DefaultMessageHandler(opencircuit);
    }

    public void HandlePacketAck(PacketAck packetack)
    {
        DefaultMessageHandler(packetack);
    }

    public void HandleParcelAccessListReply(ParcelAccessListReply parcelaccesslistreply)
    {
        DefaultMessageHandler(parcelaccesslistreply);
    }

    public void HandleParcelAccessListRequest(ParcelAccessListRequest parcelaccesslistrequest)
    {
        DefaultMessageHandler(parcelaccesslistrequest);
    }

    public void HandleParcelAccessListUpdate(ParcelAccessListUpdate parcelaccesslistupdate)
    {
        DefaultMessageHandler(parcelaccesslistupdate);
    }

    public void HandleParcelAuctions(ParcelAuctions parcelauctions)
    {
        DefaultMessageHandler(parcelauctions);
    }

    public void HandleParcelBuy(ParcelBuy parcelbuy)
    {
        DefaultMessageHandler(parcelbuy);
    }

    public void HandleParcelBuyPass(ParcelBuyPass parcelbuypass)
    {
        DefaultMessageHandler(parcelbuypass);
    }

    public void HandleParcelClaim(ParcelClaim parcelclaim)
    {
        DefaultMessageHandler(parcelclaim);
    }

    public void HandleParcelDeedToGroup(ParcelDeedToGroup parceldeedtogroup)
    {
        DefaultMessageHandler(parceldeedtogroup);
    }

    public void HandleParcelDisableObjects(ParcelDisableObjects parceldisableobjects)
    {
        DefaultMessageHandler(parceldisableobjects);
    }

    public void HandleParcelDivide(ParcelDivide parceldivide)
    {
        DefaultMessageHandler(parceldivide);
    }

    public void HandleParcelDwellReply(ParcelDwellReply parceldwellreply)
    {
        DefaultMessageHandler(parceldwellreply);
    }

    public void HandleParcelDwellRequest(ParcelDwellRequest parceldwellrequest)
    {
        DefaultMessageHandler(parceldwellrequest);
    }

    public void HandleParcelGodForceOwner(ParcelGodForceOwner parcelgodforceowner)
    {
        DefaultMessageHandler(parcelgodforceowner);
    }

    public void HandleParcelGodMarkAsContent(ParcelGodMarkAsContent parcelgodmarkascontent)
    {
        DefaultMessageHandler(parcelgodmarkascontent);
    }

    public void HandleParcelInfoReply(ParcelInfoReply parcelinforeply)
    {
        DefaultMessageHandler(parcelinforeply);
    }

    public void HandleParcelInfoRequest(ParcelInfoRequest parcelinforequest)
    {
        DefaultMessageHandler(parcelinforequest);
    }

    public void HandleParcelJoin(ParcelJoin parceljoin)
    {
        DefaultMessageHandler(parceljoin);
    }

    public void HandleParcelMediaCommandMessage(ParcelMediaCommandMessage parcelmediacommandmessage)
    {
        DefaultMessageHandler(parcelmediacommandmessage);
    }

    public void HandleParcelMediaUpdate(ParcelMediaUpdate parcelmediaupdate)
    {
        DefaultMessageHandler(parcelmediaupdate);
    }

    public void HandleParcelObjectOwnersReply(ParcelObjectOwnersReply parcelobjectownersreply)
    {
        DefaultMessageHandler(parcelobjectownersreply);
    }

    public void HandleParcelObjectOwnersRequest(ParcelObjectOwnersRequest parcelobjectownersrequest)
    {
        DefaultMessageHandler(parcelobjectownersrequest);
    }

    public void HandleParcelOverlay(ParcelOverlay parceloverlay)
    {
        DefaultMessageHandler(parceloverlay);
    }

    public void HandleParcelProperties(ParcelProperties parcelproperties)
    {
        DefaultMessageHandler(parcelproperties);
    }

    public void HandleParcelPropertiesRequest(ParcelPropertiesRequest parcelpropertiesrequest)
    {
        DefaultMessageHandler(parcelpropertiesrequest);
    }

    public void HandleParcelPropertiesRequestByID(ParcelPropertiesRequestByID parcelpropertiesrequestbyid)
    {
        DefaultMessageHandler(parcelpropertiesrequestbyid);
    }

    public void HandleParcelPropertiesUpdate(ParcelPropertiesUpdate parcelpropertiesupdate)
    {
        DefaultMessageHandler(parcelpropertiesupdate);
    }

    public void HandleParcelReclaim(ParcelReclaim parcelreclaim)
    {
        DefaultMessageHandler(parcelreclaim);
    }

    public void HandleParcelRelease(ParcelRelease parcelrelease)
    {
        DefaultMessageHandler(parcelrelease);
    }

    public void HandleParcelRename(ParcelRename parcelrename)
    {
        DefaultMessageHandler(parcelrename);
    }

    public void HandleParcelReturnObjects(ParcelReturnObjects parcelreturnobjects)
    {
        DefaultMessageHandler(parcelreturnobjects);
    }

    public void HandleParcelSales(ParcelSales parcelsales)
    {
        DefaultMessageHandler(parcelsales);
    }

    public void HandleParcelSelectObjects(ParcelSelectObjects parcelselectobjects)
    {
        DefaultMessageHandler(parcelselectobjects);
    }

    public void HandleParcelSetOtherCleanTime(ParcelSetOtherCleanTime parcelsetothercleantime)
    {
        DefaultMessageHandler(parcelsetothercleantime);
    }

    public void HandlePayPriceReply(PayPriceReply paypricereply)
    {
        DefaultMessageHandler(paypricereply);
    }

    public void HandlePickDelete(PickDelete pickdelete)
    {
        DefaultMessageHandler(pickdelete);
    }

    public void HandlePickGodDelete(PickGodDelete pickgoddelete)
    {
        DefaultMessageHandler(pickgoddelete);
    }

    public void HandlePickInfoReply(PickInfoReply pickinforeply)
    {
        DefaultMessageHandler(pickinforeply);
    }

    public void HandlePickInfoUpdate(PickInfoUpdate pickinfoupdate)
    {
        DefaultMessageHandler(pickinfoupdate);
    }

    public void HandlePlacesQuery(PlacesQuery placesquery)
    {
        DefaultMessageHandler(placesquery);
    }

    public void HandlePlacesReply(PlacesReply placesreply)
    {
        DefaultMessageHandler(placesreply);
    }

    public void HandlePreloadSound(PreloadSound preloadsound)
    {
        DefaultMessageHandler(preloadsound);
    }

    public void HandlePurgeInventoryDescendents(PurgeInventoryDescendents purgeinventorydescendents)
    {
        DefaultMessageHandler(purgeinventorydescendents);
    }

    public void HandleRebakeAvatarTextures(RebakeAvatarTextures rebakeavatartextures)
    {
        DefaultMessageHandler(rebakeavatartextures);
    }

    public void HandleRedo(Redo redo)
    {
        DefaultMessageHandler(redo);
    }

    public void HandleRegionHandleRequest(RegionHandleRequest regionhandlerequest)
    {
        DefaultMessageHandler(regionhandlerequest);
    }

    public void HandleRegionHandshake(RegionHandshake regionhandshake)
    {
        DefaultMessageHandler(regionhandshake);
    }

    public void HandleRegionHandshakeReply(RegionHandshakeReply regionhandshakereply)
    {
        DefaultMessageHandler(regionhandshakereply);
    }

    public void HandleRegionIDAndHandleReply(RegionIDAndHandleReply regionidandhandlereply)
    {
        DefaultMessageHandler(regionidandhandlereply);
    }

    public void HandleRegionInfo(RegionInfo regioninfo)
    {
        DefaultMessageHandler(regioninfo);
    }

    public void HandleRegionPresenceRequestByHandle(RegionPresenceRequestByHandle regionpresencerequestbyhandle)
    {
        DefaultMessageHandler(regionpresencerequestbyhandle);
    }

    public void HandleRegionPresenceRequestByRegionID(RegionPresenceRequestByRegionID regionpresencerequestbyregionid)
    {
        DefaultMessageHandler(regionpresencerequestbyregionid);
    }

    public void HandleRegionPresenceResponse(RegionPresenceResponse regionpresenceresponse)
    {
        DefaultMessageHandler(regionpresenceresponse);
    }

    public void HandleRemoveAttachment(RemoveAttachment removeattachment)
    {
        DefaultMessageHandler(removeattachment);
    }

    public void HandleRemoveInventoryFolder(RemoveInventoryFolder removeinventoryfolder)
    {
        DefaultMessageHandler(removeinventoryfolder);
    }

    public void HandleRemoveInventoryItem(RemoveInventoryItem removeinventoryitem)
    {
        DefaultMessageHandler(removeinventoryitem);
    }

    public void HandleRemoveInventoryObjects(RemoveInventoryObjects removeinventoryobjects)
    {
        DefaultMessageHandler(removeinventoryobjects);
    }

    public void HandleRemoveMuteListEntry(RemoveMuteListEntry removemutelistentry)
    {
        DefaultMessageHandler(removemutelistentry);
    }

    public void HandleRemoveNameValuePair(RemoveNameValuePair removenamevaluepair)
    {
        DefaultMessageHandler(removenamevaluepair);
    }

    public void HandleRemoveParcel(RemoveParcel removeparcel)
    {
        DefaultMessageHandler(removeparcel);
    }

    public void HandleRemoveTaskInventory(RemoveTaskInventory removetaskinventory)
    {
        DefaultMessageHandler(removetaskinventory);
    }

    public void HandleReplyTaskInventory(ReplyTaskInventory replytaskinventory)
    {
        DefaultMessageHandler(replytaskinventory);
    }

    public void HandleReportAutosaveCrash(ReportAutosaveCrash reportautosavecrash)
    {
        DefaultMessageHandler(reportautosavecrash);
    }

    public void HandleRequestGodlikePowers(RequestGodlikePowers requestgodlikepowers)
    {
        DefaultMessageHandler(requestgodlikepowers);
    }

    public void HandleRequestImage(RequestImage requestimage)
    {
        DefaultMessageHandler(requestimage);
    }

    public void HandleRequestInventoryAsset(RequestInventoryAsset requestinventoryasset)
    {
        DefaultMessageHandler(requestinventoryasset);
    }

    public void HandleRequestMultipleObjects(RequestMultipleObjects requestmultipleobjects)
    {
        DefaultMessageHandler(requestmultipleobjects);
    }

    public void HandleRequestObjectPropertiesFamily(RequestObjectPropertiesFamily requestobjectpropertiesfamily)
    {
        DefaultMessageHandler(requestobjectpropertiesfamily);
    }

    public void HandleRequestParcelTransfer(RequestParcelTransfer requestparceltransfer)
    {
        DefaultMessageHandler(requestparceltransfer);
    }

    public void HandleRequestPayPrice(RequestPayPrice requestpayprice)
    {
        DefaultMessageHandler(requestpayprice);
    }

    public void HandleRequestRegionInfo(RequestRegionInfo requestregioninfo)
    {
        DefaultMessageHandler(requestregioninfo);
    }

    public void HandleRequestTaskInventory(RequestTaskInventory requesttaskinventory)
    {
        DefaultMessageHandler(requesttaskinventory);
    }

    public void HandleRequestTrustedCircuit(RequestTrustedCircuit requesttrustedcircuit)
    {
        DefaultMessageHandler(requesttrustedcircuit);
    }

    public void HandleRequestXfer(RequestXfer requestxfer)
    {
        DefaultMessageHandler(requestxfer);
    }

    public void HandleRetrieveInstantMessages(RetrieveInstantMessages retrieveinstantmessages)
    {
        DefaultMessageHandler(retrieveinstantmessages);
    }

    public void HandleRevokePermissions(RevokePermissions revokepermissions)
    {
        DefaultMessageHandler(revokepermissions);
    }

    public void HandleRezMultipleAttachmentsFromInv(RezMultipleAttachmentsFromInv rezmultipleattachmentsfrominv)
    {
        DefaultMessageHandler(rezmultipleattachmentsfrominv);
    }

    public void HandleRezObject(RezObject rezobject)
    {
        DefaultMessageHandler(rezobject);
    }

    public void HandleRezObjectFromNotecard(RezObjectFromNotecard rezobjectfromnotecard)
    {
        DefaultMessageHandler(rezobjectfromnotecard);
    }

    public void HandleRezRestoreToWorld(RezRestoreToWorld rezrestoretoworld)
    {
        DefaultMessageHandler(rezrestoretoworld);
    }

    public void HandleRezScript(RezScript rezscript)
    {
        DefaultMessageHandler(rezscript);
    }

    public void HandleRezSingleAttachmentFromInv(RezSingleAttachmentFromInv rezsingleattachmentfrominv)
    {
        DefaultMessageHandler(rezsingleattachmentfrominv);
    }

    public void HandleRoutedMoneyBalanceReply(RoutedMoneyBalanceReply routedmoneybalancereply)
    {
        DefaultMessageHandler(routedmoneybalancereply);
    }

    public void HandleRpcChannelReply(RpcChannelReply rpcchannelreply)
    {
        DefaultMessageHandler(rpcchannelreply);
    }

    public void HandleRpcChannelRequest(RpcChannelRequest rpcchannelrequest)
    {
        DefaultMessageHandler(rpcchannelrequest);
    }

    public void HandleRpcScriptReplyInbound(RpcScriptReplyInbound rpcscriptreplyinbound)
    {
        DefaultMessageHandler(rpcscriptreplyinbound);
    }

    public void HandleRpcScriptRequestInbound(RpcScriptRequestInbound rpcscriptrequestinbound)
    {
        DefaultMessageHandler(rpcscriptrequestinbound);
    }

    public void HandleRpcScriptRequestInboundForward(RpcScriptRequestInboundForward rpcscriptrequestinboundforward)
    {
        DefaultMessageHandler(rpcscriptrequestinboundforward);
    }

    public void HandleSaveAssetIntoInventory(SaveAssetIntoInventory saveassetintoinventory)
    {
        DefaultMessageHandler(saveassetintoinventory);
    }

    public void HandleScriptAnswerYes(ScriptAnswerYes scriptansweryes)
    {
        DefaultMessageHandler(scriptansweryes);
    }

    public void HandleScriptControlChange(ScriptControlChange scriptcontrolchange)
    {
        DefaultMessageHandler(scriptcontrolchange);
    }

    public void HandleScriptDataReply(ScriptDataReply scriptdatareply)
    {
        DefaultMessageHandler(scriptdatareply);
    }

    public void HandleScriptDataRequest(ScriptDataRequest scriptdatarequest)
    {
        DefaultMessageHandler(scriptdatarequest);
    }

    public void HandleScriptDialog(ScriptDialog scriptdialog)
    {
        DefaultMessageHandler(scriptdialog);
    }

    public void HandleScriptDialogReply(ScriptDialogReply scriptdialogreply)
    {
        DefaultMessageHandler(scriptdialogreply);
    }

    public void HandleScriptMailRegistration(ScriptMailRegistration scriptmailregistration)
    {
        DefaultMessageHandler(scriptmailregistration);
    }

    public void HandleScriptQuestion(ScriptQuestion scriptquestion)
    {
        DefaultMessageHandler(scriptquestion);
    }

    public void HandleScriptReset(ScriptReset scriptreset)
    {
        DefaultMessageHandler(scriptreset);
    }

    public void HandleScriptRunningReply(ScriptRunningReply scriptrunningreply)
    {
        DefaultMessageHandler(scriptrunningreply);
    }

    public void HandleScriptSensorReply(ScriptSensorReply scriptsensorreply)
    {
        DefaultMessageHandler(scriptsensorreply);
    }

    public void HandleScriptSensorRequest(ScriptSensorRequest scriptsensorrequest)
    {
        DefaultMessageHandler(scriptsensorrequest);
    }

    public void HandleScriptTeleportRequest(ScriptTeleportRequest scriptteleportrequest)
    {
        DefaultMessageHandler(scriptteleportrequest);
    }

    public void HandleSendPostcard(SendPostcard sendpostcard)
    {
        DefaultMessageHandler(sendpostcard);
    }

    public void HandleSendXferPacket(SendXferPacket sendxferpacket)
    {
        DefaultMessageHandler(sendxferpacket);
    }

    public void HandleSetAlwaysRun(SetAlwaysRun setalwaysrun)
    {
        DefaultMessageHandler(setalwaysrun);
    }

    public void HandleSetCPURatio(SetCPURatio setcpuratio)
    {
        DefaultMessageHandler(setcpuratio);
    }

    public void HandleSetFollowCamProperties(SetFollowCamProperties setfollowcamproperties)
    {
        DefaultMessageHandler(setfollowcamproperties);
    }

    public void HandleSetGroupAcceptNotices(SetGroupAcceptNotices setgroupacceptnotices)
    {
        DefaultMessageHandler(setgroupacceptnotices);
    }

    public void HandleSetGroupContribution(SetGroupContribution setgroupcontribution)
    {
        DefaultMessageHandler(setgroupcontribution);
    }

    public void HandleSetScriptRunning(SetScriptRunning setscriptrunning)
    {
        DefaultMessageHandler(setscriptrunning);
    }

    public void HandleSetSimPresenceInDatabase(SetSimPresenceInDatabase setsimpresenceindatabase)
    {
        DefaultMessageHandler(setsimpresenceindatabase);
    }

    public void HandleSetSimStatusInDatabase(SetSimStatusInDatabase setsimstatusindatabase)
    {
        DefaultMessageHandler(setsimstatusindatabase);
    }

    public void HandleSetStartLocation(SetStartLocation setstartlocation)
    {
        DefaultMessageHandler(setstartlocation);
    }

    public void HandleSetStartLocationRequest(SetStartLocationRequest setstartlocationrequest)
    {
        DefaultMessageHandler(setstartlocationrequest);
    }

    public void HandleSimCrashed(SimCrashed simcrashed)
    {
        DefaultMessageHandler(simcrashed);
    }

    public void HandleSimStats(SimStats simstats)
    {
        DefaultMessageHandler(simstats);
    }

    public void HandleSimStatus(SimStatus simstatus)
    {
        DefaultMessageHandler(simstatus);
    }

    public void HandleSimWideDeletes(SimWideDeletes simwidedeletes)
    {
        DefaultMessageHandler(simwidedeletes);
    }

    public void HandleSimulatorLoad(SimulatorLoad simulatorload)
    {
        DefaultMessageHandler(simulatorload);
    }

    public void HandleSimulatorMapUpdate(SimulatorMapUpdate simulatormapupdate)
    {
        DefaultMessageHandler(simulatormapupdate);
    }

    public void HandleSimulatorPresentAtLocation(SimulatorPresentAtLocation simulatorpresentatlocation)
    {
        DefaultMessageHandler(simulatorpresentatlocation);
    }

    public void HandleSimulatorReady(SimulatorReady simulatorready)
    {
        DefaultMessageHandler(simulatorready);
    }

    public void HandleSimulatorSetMap(SimulatorSetMap simulatorsetmap)
    {
        DefaultMessageHandler(simulatorsetmap);
    }

    public void HandleSimulatorShutdownRequest(SimulatorShutdownRequest simulatorshutdownrequest)
    {
        DefaultMessageHandler(simulatorshutdownrequest);
    }

    public void HandleSimulatorViewerTimeMessage(SimulatorViewerTimeMessage simulatorviewertimemessage)
    {
        DefaultMessageHandler(simulatorviewertimemessage);
    }

    public void HandleSoundTrigger(SoundTrigger soundtrigger)
    {
        DefaultMessageHandler(soundtrigger);
    }

    public void HandleStartAuction(StartAuction startauction)
    {
        DefaultMessageHandler(startauction);
    }

    public void HandleStartGroupProposal(StartGroupProposal startgroupproposal)
    {
        DefaultMessageHandler(startgroupproposal);
    }

    public void HandleStartLure(StartLure startlure)
    {
        DefaultMessageHandler(startlure);
    }

    public void HandleStartPingCheck(StartPingCheck startpingcheck)
    {
        DefaultMessageHandler(startpingcheck);
    }

    public void HandleStateSave(StateSave statesave)
    {
        DefaultMessageHandler(statesave);
    }

    public void HandleSubscribeLoad(SubscribeLoad subscribeload)
    {
        DefaultMessageHandler(subscribeload);
    }

    public void HandleSystemKickUser(SystemKickUser systemkickuser)
    {
        DefaultMessageHandler(systemkickuser);
    }

    public void HandleSystemMessage(SystemMessage systemmessage)
    {
        DefaultMessageHandler(systemmessage);
    }

    public void HandleTallyVotes(TallyVotes tallyvotes)
    {
        DefaultMessageHandler(tallyvotes);
    }

    public void HandleTelehubInfo(TelehubInfo telehubinfo)
    {
        DefaultMessageHandler(telehubinfo);
    }

    public void HandleTeleportCancel(TeleportCancel teleportcancel)
    {
        DefaultMessageHandler(teleportcancel);
    }

    public void HandleTeleportFailed(TeleportFailed teleportfailed)
    {
        DefaultMessageHandler(teleportfailed);
    }

    public void HandleTeleportFinish(TeleportFinish teleportfinish)
    {
        DefaultMessageHandler(teleportfinish);
    }

    public void HandleTeleportLandingStatusChanged(TeleportLandingStatusChanged teleportlandingstatuschanged)
    {
        DefaultMessageHandler(teleportlandingstatuschanged);
    }

    public void HandleTeleportLandmarkRequest(TeleportLandmarkRequest teleportlandmarkrequest)
    {
        DefaultMessageHandler(teleportlandmarkrequest);
    }

    public void HandleTeleportLocal(TeleportLocal teleportlocal)
    {
        DefaultMessageHandler(teleportlocal);
    }

    public void HandleTeleportLocationRequest(TeleportLocationRequest teleportlocationrequest)
    {
        DefaultMessageHandler(teleportlocationrequest);
    }

    public void HandleTeleportLureRequest(TeleportLureRequest teleportlurerequest)
    {
        DefaultMessageHandler(teleportlurerequest);
    }

    public void HandleTeleportProgress(TeleportProgress teleportprogress)
    {
        DefaultMessageHandler(teleportprogress);
    }

    public void HandleTeleportRequest(TeleportRequest teleportrequest)
    {
        DefaultMessageHandler(teleportrequest);
    }

    public void HandleTeleportStart(TeleportStart teleportstart)
    {
        DefaultMessageHandler(teleportstart);
    }

    public void HandleTerminateFriendship(TerminateFriendship terminatefriendship)
    {
        DefaultMessageHandler(terminatefriendship);
    }

    public void HandleTestMessage(TestMessage testmessage)
    {
        DefaultMessageHandler(testmessage);
    }

    public void HandleTrackAgent(TrackAgent trackagent)
    {
        DefaultMessageHandler(trackagent);
    }

    public void HandleTransferAbort(TransferAbort transferabort)
    {
        DefaultMessageHandler(transferabort);
    }

    public void HandleTransferInfo(TransferInfo transferinfo)
    {
        DefaultMessageHandler(transferinfo);
    }

    public void HandleTransferInventory(TransferInventory transferinventory)
    {
        DefaultMessageHandler(transferinventory);
    }

    public void HandleTransferInventoryAck(TransferInventoryAck transferinventoryack)
    {
        DefaultMessageHandler(transferinventoryack);
    }

    public void HandleTransferPacket(TransferPacket transferpacket)
    {
        DefaultMessageHandler(transferpacket);
    }

    public void HandleTransferRequest(TransferRequest transferrequest)
    {
        DefaultMessageHandler(transferrequest);
    }

    public void HandleUUIDGroupNameReply(UUIDGroupNameReply uuidgroupnamereply)
    {
        DefaultMessageHandler(uuidgroupnamereply);
    }

    public void HandleUUIDGroupNameRequest(UUIDGroupNameRequest uuidgroupnamerequest)
    {
        DefaultMessageHandler(uuidgroupnamerequest);
    }

    public void HandleUUIDNameReply(UUIDNameReply uuidnamereply)
    {
        DefaultMessageHandler(uuidnamereply);
    }

    public void HandleUUIDNameRequest(UUIDNameRequest uuidnamerequest)
    {
        DefaultMessageHandler(uuidnamerequest);
    }

    public void HandleUndo(Undo undo)
    {
        DefaultMessageHandler(undo);
    }

    public void HandleUndoLand(UndoLand undoland)
    {
        DefaultMessageHandler(undoland);
    }

    public void HandleUnsubscribeLoad(UnsubscribeLoad unsubscribeload)
    {
        DefaultMessageHandler(unsubscribeload);
    }

    public void HandleUpdateAttachment(UpdateAttachment updateattachment)
    {
        DefaultMessageHandler(updateattachment);
    }

    public void HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updatecreateinventoryitem)
    {
        DefaultMessageHandler(updatecreateinventoryitem);
    }

    public void HandleUpdateGroupInfo(UpdateGroupInfo updategroupinfo)
    {
        DefaultMessageHandler(updategroupinfo);
    }

    public void HandleUpdateInventoryFolder(UpdateInventoryFolder updateinventoryfolder)
    {
        DefaultMessageHandler(updateinventoryfolder);
    }

    public void HandleUpdateInventoryItem(UpdateInventoryItem updateinventoryitem)
    {
        DefaultMessageHandler(updateinventoryitem);
    }

    public void HandleUpdateMuteListEntry(UpdateMuteListEntry updatemutelistentry)
    {
        DefaultMessageHandler(updatemutelistentry);
    }

    public void HandleUpdateParcel(UpdateParcel updateparcel)
    {
        DefaultMessageHandler(updateparcel);
    }

    public void HandleUpdateSimulator(UpdateSimulator updatesimulator)
    {
        DefaultMessageHandler(updatesimulator);
    }

    public void HandleUpdateTaskInventory(UpdateTaskInventory updatetaskinventory)
    {
        DefaultMessageHandler(updatetaskinventory);
    }

    public void HandleUpdateUserInfo(UpdateUserInfo updateuserinfo)
    {
        DefaultMessageHandler(updateuserinfo);
    }

    public void HandleUseCachedMuteList(UseCachedMuteList usecachedmutelist)
    {
        DefaultMessageHandler(usecachedmutelist);
    }

    public void HandleUseCircuitCode(UseCircuitCode usecircuitcode)
    {
        DefaultMessageHandler(usecircuitcode);
    }

    public void HandleUserInfoReply(UserInfoReply userinforeply)
    {
        DefaultMessageHandler(userinforeply);
    }

    public void HandleUserInfoRequest(UserInfoRequest userinforequest)
    {
        DefaultMessageHandler(userinforequest);
    }

    public void HandleUserReport(UserReport userreport)
    {
        DefaultMessageHandler(userreport);
    }

    public void HandleUserReportInternal(UserReportInternal userreportinternal)
    {
        DefaultMessageHandler(userreportinternal);
    }

    public void HandleVelocityInterpolateOff(VelocityInterpolateOff velocityinterpolateoff)
    {
        DefaultMessageHandler(velocityinterpolateoff);
    }

    public void HandleVelocityInterpolateOn(VelocityInterpolateOn velocityinterpolateon)
    {
        DefaultMessageHandler(velocityinterpolateon);
    }

    public void HandleViewerEffect(ViewerEffect viewereffect)
    {
        DefaultMessageHandler(viewereffect);
    }

    public void HandleViewerFrozenMessage(ViewerFrozenMessage viewerfrozenmessage)
    {
        DefaultMessageHandler(viewerfrozenmessage);
    }

    public void HandleViewerStartAuction(ViewerStartAuction viewerstartauction)
    {
        DefaultMessageHandler(viewerstartauction);
    }

    public void HandleViewerStats(ViewerStats viewerstats)
    {
        DefaultMessageHandler(viewerstats);
    }
}
