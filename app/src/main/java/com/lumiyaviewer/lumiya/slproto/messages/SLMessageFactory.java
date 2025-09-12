package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SLMessageFactory {
    public static SLMessage CreateByID(int i) {
        switch (i) {
            case -65535:
                return new TestMessage();
            case -65534:
                return new AddCircuitCode();
            case -65533:
                return new UseCircuitCode();
            case -65532:
                return new AvatarTextureUpdate();
            case -65531:
                return new SimulatorMapUpdate();
            case -65530:
                return new SimulatorSetMap();
            case -65529:
                return new SubscribeLoad();
            case -65528:
                return new UnsubscribeLoad();
            case -65527:
                return new SimulatorReady();
            case -65526:
                return new TelehubInfo();
            case -65525:
                return new SimulatorPresentAtLocation();
            case -65524:
                return new SimulatorLoad();
            case -65523:
                return new SimulatorShutdownRequest();
            case -65522:
                return new RegionPresenceRequestByRegionID();
            case -65521:
                return new RegionPresenceRequestByHandle();
            case -65520:
                return new RegionPresenceResponse();
            case -65519:
                return new UpdateSimulator();
            case -65518:
                return new LogDwellTime();
            case -65517:
                return new FeatureDisabled();
            case -65516:
                return new LogFailedMoneyTransaction();
            case -65515:
                return new UserReportInternal();
            case -65514:
                return new SetSimStatusInDatabase();
            case -65513:
                return new SetSimPresenceInDatabase();
            case -65512:
                return new EconomyDataRequest();
            case -65511:
                return new EconomyData();
            case -65510:
                return new AvatarPickerRequest();
            case -65509:
                return new AvatarPickerRequestBackend();
            case -65508:
                return new AvatarPickerReply();
            case -65507:
                return new PlacesQuery();
            case -65506:
                return new PlacesReply();
            case -65505:
                return new DirFindQuery();
            case -65504:
                return new DirFindQueryBackend();
            case -65503:
                return new DirPlacesQuery();
            case -65502:
                return new DirPlacesQueryBackend();
            case -65501:
                return new DirPlacesReply();
            case -65500:
                return new DirPeopleReply();
            case -65499:
                return new DirEventsReply();
            case -65498:
                return new DirGroupsReply();
            case -65497:
                return new DirClassifiedQuery();
            case -65496:
                return new DirClassifiedQueryBackend();
            case -65495:
                return new DirClassifiedReply();
            case -65494:
                return new AvatarClassifiedReply();
            case -65493:
                return new ClassifiedInfoRequest();
            case -65492:
                return new ClassifiedInfoReply();
            case -65491:
                return new ClassifiedInfoUpdate();
            case -65490:
                return new ClassifiedDelete();
            case -65489:
                return new ClassifiedGodDelete();
            case -65488:
                return new DirLandQuery();
            case -65487:
                return new DirLandQueryBackend();
            case -65486:
                return new DirLandReply();
            case -65485:
                return new DirPopularQuery();
            case -65484:
                return new DirPopularQueryBackend();
            case -65483:
                return new DirPopularReply();
            case -65482:
                return new ParcelInfoRequest();
            case -65481:
                return new ParcelInfoReply();
            case -65480:
                return new ParcelObjectOwnersRequest();
            case -65479:
                return new ParcelObjectOwnersReply();
            case -65478:
                return new GroupNoticesListRequest();
            case -65477:
                return new GroupNoticesListReply();
            case -65476:
                return new GroupNoticeRequest();
            case -65475:
                return new GroupNoticeAdd();
            case -65474:
                return new TeleportRequest();
            case -65473:
                return new TeleportLocationRequest();
            case -65472:
                return new TeleportLocal();
            case -65471:
                return new TeleportLandmarkRequest();
            case -65470:
                return new TeleportProgress();
            case -65469:
                return new DataHomeLocationRequest();
            case -65468:
                return new DataHomeLocationReply();
            case -65467:
                return new TeleportFinish();
            case -65466:
                return new StartLure();
            case -65465:
                return new TeleportLureRequest();
            case -65464:
                return new TeleportCancel();
            case -65463:
                return new TeleportStart();
            case -65462:
                return new TeleportFailed();
            case -65461:
                return new Undo();
            case -65460:
                return new Redo();
            case -65459:
                return new UndoLand();
            case -65458:
                return new AgentPause();
            case -65457:
                return new AgentResume();
            case -65456:
                return new ChatFromViewer();
            case -65455:
                return new AgentThrottle();
            case -65454:
                return new AgentFOV();
            case -65453:
                return new AgentHeightWidth();
            case -65452:
                return new AgentSetAppearance();
            case -65451:
                return new AgentQuitCopy();
            case -65450:
                return new ImageNotInDatabase();
            case -65449:
                return new RebakeAvatarTextures();
            case -65448:
                return new SetAlwaysRun();
            case -65447:
                return new ObjectDelete();
            case -65446:
                return new ObjectDuplicate();
            case -65445:
                return new ObjectDuplicateOnRay();
            case -65444:
                return new ObjectScale();
            case -65443:
                return new ObjectRotation();
            case -65442:
                return new ObjectFlagUpdate();
            case -65441:
                return new ObjectClickAction();
            case -65440:
                return new ObjectImage();
            case -65439:
                return new ObjectMaterial();
            case -65438:
                return new ObjectShape();
            case -65437:
                return new ObjectExtraParams();
            case -65436:
                return new ObjectOwner();
            case -65435:
                return new ObjectGroup();
            case -65434:
                return new ObjectBuy();
            case -65433:
                return new BuyObjectInventory();
            case -65432:
                return new DerezContainer();
            case -65431:
                return new ObjectPermissions();
            case -65430:
                return new ObjectSaleInfo();
            case -65429:
                return new ObjectName();
            case -65428:
                return new ObjectDescription();
            case -65427:
                return new ObjectCategory();
            case -65426:
                return new ObjectSelect();
            case -65425:
                return new ObjectDeselect();
            case -65424:
                return new ObjectAttach();
            case -65423:
                return new ObjectDetach();
            case -65422:
                return new ObjectDrop();
            case -65421:
                return new ObjectLink();
            case -65420:
                return new ObjectDelink();
            case -65419:
                return new ObjectGrab();
            case -65418:
                return new ObjectGrabUpdate();
            case -65417:
                return new ObjectDeGrab();
            case -65416:
                return new ObjectSpinStart();
            case -65415:
                return new ObjectSpinUpdate();
            case -65414:
                return new ObjectSpinStop();
            case -65413:
                return new ObjectExportSelected();
            case -65412:
                return new ModifyLand();
            case -65411:
                return new VelocityInterpolateOn();
            case -65410:
                return new VelocityInterpolateOff();
            case -65409:
                return new StateSave();
            case -65408:
                return new ReportAutosaveCrash();
            case -65407:
                return new SimWideDeletes();
            case -65406:
                return new TrackAgent();
            case -65405:
                return new ViewerStats();
            case -65404:
                return new ScriptAnswerYes();
            case -65403:
                return new UserReport();
            case -65402:
                return new AlertMessage();
            case -65401:
                return new AgentAlertMessage();
            case -65400:
                return new MeanCollisionAlert();
            case -65399:
                return new ViewerFrozenMessage();
            case -65398:
                return new HealthMessage();
            case -65397:
                return new ChatFromSimulator();
            case -65396:
                return new SimStats();
            case -65395:
                return new RequestRegionInfo();
            case -65394:
                return new RegionInfo();
            case -65393:
                return new GodUpdateRegionInfo();
            case -65392:
                return new NearestLandingRegionRequest();
            case -65391:
                return new NearestLandingRegionReply();
            case -65390:
                return new NearestLandingRegionUpdated();
            case -65389:
                return new TeleportLandingStatusChanged();
            case -65388:
                return new RegionHandshake();
            case -65387:
                return new RegionHandshakeReply();
            case -65386:
                return new SimulatorViewerTimeMessage();
            case -65385:
                return new EnableSimulator();
            case -65384:
                return new DisableSimulator();
            case -65383:
                return new TransferRequest();
            case -65382:
                return new TransferInfo();
            case -65381:
                return new TransferAbort();
            case -65380:
                return new RequestXfer();
            case -65379:
                return new AbortXfer();
            case -65378:
                return new AvatarAppearance();
            case -65377:
                return new SetFollowCamProperties();
            case -65376:
                return new ClearFollowCamProperties();
            case -65375:
                return new RequestPayPrice();
            case -65374:
                return new PayPriceReply();
            case -65373:
                return new KickUser();
            case -65372:
                return new KickUserAck();
            case -65371:
                return new GodKickUser();
            case -65370:
                return new SystemKickUser();
            case -65369:
                return new EjectUser();
            case -65368:
                return new FreezeUser();
            case -65367:
                return new AvatarPropertiesRequest();
            case -65366:
                return new AvatarPropertiesRequestBackend();
            case -65365:
                return new AvatarPropertiesReply();
            case -65364:
                return new AvatarInterestsReply();
            case -65363:
                return new AvatarGroupsReply();
            case -65362:
                return new AvatarPropertiesUpdate();
            case -65361:
                return new AvatarInterestsUpdate();
            case -65360:
                return new AvatarNotesReply();
            case -65359:
                return new AvatarNotesUpdate();
            case -65358:
                return new AvatarPicksReply();
            case -65357:
                return new EventInfoRequest();
            case -65356:
                return new EventInfoReply();
            case -65355:
                return new EventNotificationAddRequest();
            case -65354:
                return new EventNotificationRemoveRequest();
            case -65353:
                return new EventGodDelete();
            case -65352:
                return new PickInfoReply();
            case -65351:
                return new PickInfoUpdate();
            case -65350:
                return new PickDelete();
            case -65349:
                return new PickGodDelete();
            case -65348:
                return new ScriptQuestion();
            case -65347:
                return new ScriptControlChange();
            case -65346:
                return new ScriptDialog();
            case -65345:
                return new ScriptDialogReply();
            case -65344:
                return new ForceScriptControlRelease();
            case -65343:
                return new RevokePermissions();
            case -65342:
                return new LoadURL();
            case -65341:
                return new ScriptTeleportRequest();
            case -65340:
                return new ParcelOverlay();
            case -65339:
                return new ParcelPropertiesRequestByID();
            case -65338:
                return new ParcelPropertiesUpdate();
            case -65337:
                return new ParcelReturnObjects();
            case -65336:
                return new ParcelSetOtherCleanTime();
            case -65335:
                return new ParcelDisableObjects();
            case -65334:
                return new ParcelSelectObjects();
            case -65333:
                return new EstateCovenantRequest();
            case -65332:
                return new EstateCovenantReply();
            case -65331:
                return new ForceObjectSelect();
            case -65330:
                return new ParcelBuyPass();
            case -65329:
                return new ParcelDeedToGroup();
            case -65328:
                return new ParcelReclaim();
            case -65327:
                return new ParcelClaim();
            case -65326:
                return new ParcelJoin();
            case -65325:
                return new ParcelDivide();
            case -65324:
                return new ParcelRelease();
            case -65323:
                return new ParcelBuy();
            case -65322:
                return new ParcelGodForceOwner();
            case -65321:
                return new ParcelAccessListRequest();
            case -65320:
                return new ParcelAccessListReply();
            case -65319:
                return new ParcelAccessListUpdate();
            case -65318:
                return new ParcelDwellRequest();
            case -65317:
                return new ParcelDwellReply();
            case -65316:
                return new RequestParcelTransfer();
            case -65315:
                return new UpdateParcel();
            case -65314:
                return new RemoveParcel();
            case -65313:
                return new MergeParcel();
            case -65312:
                return new LogParcelChanges();
            case -65311:
                return new CheckParcelSales();
            case -65310:
                return new ParcelSales();
            case -65309:
                return new ParcelGodMarkAsContent();
            case -65308:
                return new ViewerStartAuction();
            case -65307:
                return new StartAuction();
            case -65306:
                return new ConfirmAuctionStart();
            case -65305:
                return new CompleteAuction();
            case -65304:
                return new CancelAuction();
            case -65303:
                return new CheckParcelAuctions();
            case -65302:
                return new ParcelAuctions();
            case -65301:
                return new UUIDNameRequest();
            case -65300:
                return new UUIDNameReply();
            case -65299:
                return new UUIDGroupNameRequest();
            case -65298:
                return new UUIDGroupNameReply();
            case -65297:
                return new ChatPass();
            case -65296:
                return new ChildAgentDying();
            case -65295:
                return new ChildAgentUnknown();
            case -65294:
                return new KillChildAgents();
            case -65293:
                return new GetScriptRunning();
            case -65292:
                return new ScriptRunningReply();
            case -65291:
                return new SetScriptRunning();
            case -65290:
                return new ScriptReset();
            case -65289:
                return new ScriptSensorRequest();
            case -65288:
                return new ScriptSensorReply();
            case -65287:
                return new CompleteAgentMovement();
            case -65286:
                return new AgentMovementComplete();
            case -65285:
                return new DataServerLogout();
            case -65284:
                return new LogoutRequest();
            case -65283:
                return new LogoutReply();
            case -65282:
                return new ImprovedInstantMessage();
            case -65281:
                return new RetrieveInstantMessages();
            case -65280:
                return new FindAgent();
            case -65279:
                return new RequestGodlikePowers();
            case -65278:
                return new GrantGodlikePowers();
            case -65277:
                return new GodlikeMessage();
            case -65276:
                return new EstateOwnerMessage();
            case -65275:
                return new GenericMessage();
            case -65274:
                return new MuteListRequest();
            case -65273:
                return new UpdateMuteListEntry();
            case -65272:
                return new RemoveMuteListEntry();
            case -65271:
                return new CopyInventoryFromNotecard();
            case -65270:
                return new UpdateInventoryItem();
            case -65269:
                return new UpdateCreateInventoryItem();
            case -65268:
                return new MoveInventoryItem();
            case -65267:
                return new CopyInventoryItem();
            case -65266:
                return new RemoveInventoryItem();
            case -65265:
                return new ChangeInventoryItemFlags();
            case -65264:
                return new SaveAssetIntoInventory();
            case -65263:
                return new CreateInventoryFolder();
            case -65262:
                return new UpdateInventoryFolder();
            case -65261:
                return new MoveInventoryFolder();
            case -65260:
                return new RemoveInventoryFolder();
            case -65259:
                return new FetchInventoryDescendents();
            case -65258:
                return new InventoryDescendents();
            case -65257:
                return new FetchInventory();
            case -65256:
                return new FetchInventoryReply();
            case -65255:
                return new BulkUpdateInventory();
            case -65254:
                return new RequestInventoryAsset();
            case -65253:
                return new InventoryAssetResponse();
            case -65252:
                return new RemoveInventoryObjects();
            case -65251:
                return new PurgeInventoryDescendents();
            case -65250:
                return new UpdateTaskInventory();
            case -65249:
                return new RemoveTaskInventory();
            case -65248:
                return new MoveTaskInventory();
            case -65247:
                return new RequestTaskInventory();
            case -65246:
                return new ReplyTaskInventory();
            case -65245:
                return new DeRezObject();
            case -65244:
                return new DeRezAck();
            case -65243:
                return new RezObject();
            case -65242:
                return new RezObjectFromNotecard();
            case -65241:
                return new TransferInventory();
            case -65240:
                return new TransferInventoryAck();
            case -65239:
                return new AcceptFriendship();
            case -65238:
                return new DeclineFriendship();
            case -65237:
                return new FormFriendship();
            case -65236:
                return new TerminateFriendship();
            case -65235:
                return new OfferCallingCard();
            case -65234:
                return new AcceptCallingCard();
            case -65233:
                return new DeclineCallingCard();
            case -65232:
                return new RezScript();
            case -65231:
                return new CreateInventoryItem();
            case -65230:
                return new CreateLandmarkForEvent();
            case -65229:
                return new EventLocationRequest();
            case -65228:
                return new EventLocationReply();
            case -65227:
                return new RegionHandleRequest();
            case -65226:
                return new RegionIDAndHandleReply();
            case -65225:
                return new MoneyTransferRequest();
            case -65224:
                return new MoneyTransferBackend();
            case -65223:
                return new MoneyBalanceRequest();
            case -65222:
                return new MoneyBalanceReply();
            case -65221:
                return new RoutedMoneyBalanceReply();
            case -65220:
                return new ActivateGestures();
            case -65219:
                return new DeactivateGestures();
            case -65218:
                return new MuteListUpdate();
            case -65217:
                return new UseCachedMuteList();
            case -65216:
                return new GrantUserRights();
            case -65215:
                return new ChangeUserRights();
            case -65214:
                return new OnlineNotification();
            case -65213:
                return new OfflineNotification();
            case -65212:
                return new SetStartLocationRequest();
            case -65211:
                return new SetStartLocation();
            case -65210:
                return new NetTest();
            case -65209:
                return new SetCPURatio();
            case -65208:
                return new SimCrashed();
            case -65207:
                return new NameValuePair();
            case -65206:
                return new RemoveNameValuePair();
            case -65205:
                return new UpdateAttachment();
            case -65204:
                return new RemoveAttachment();
            case -65203:
                return new AssetUploadRequest();
            case -65202:
                return new AssetUploadComplete();
            case -65201:
                return new EmailMessageRequest();
            case -65200:
                return new EmailMessageReply();
            case -65199:
                return new ScriptDataRequest();
            case -65198:
                return new ScriptDataReply();
            case -65197:
                return new CreateGroupRequest();
            case -65196:
                return new CreateGroupReply();
            case -65195:
                return new UpdateGroupInfo();
            case -65194:
                return new GroupRoleChanges();
            case -65193:
                return new JoinGroupRequest();
            case -65192:
                return new JoinGroupReply();
            case -65191:
                return new EjectGroupMemberRequest();
            case -65190:
                return new EjectGroupMemberReply();
            case -65189:
                return new LeaveGroupRequest();
            case -65188:
                return new LeaveGroupReply();
            case -65187:
                return new InviteGroupRequest();
            case -65186:
                return new InviteGroupResponse();
            case -65185:
                return new GroupProfileRequest();
            case -65184:
                return new GroupProfileReply();
            case -65183:
                return new GroupAccountSummaryRequest();
            case -65182:
                return new GroupAccountSummaryReply();
            case -65181:
                return new GroupAccountDetailsRequest();
            case -65180:
                return new GroupAccountDetailsReply();
            case -65179:
                return new GroupAccountTransactionsRequest();
            case -65178:
                return new GroupAccountTransactionsReply();
            case -65177:
                return new GroupActiveProposalsRequest();
            case -65176:
                return new GroupActiveProposalItemReply();
            case -65175:
                return new GroupVoteHistoryRequest();
            case -65174:
                return new GroupVoteHistoryItemReply();
            case -65173:
                return new StartGroupProposal();
            case -65172:
                return new GroupProposalBallot();
            case -65171:
                return new TallyVotes();
            case -65170:
                return new GroupMembersRequest();
            case -65169:
                return new GroupMembersReply();
            case -65168:
                return new ActivateGroup();
            case -65167:
                return new SetGroupContribution();
            case -65166:
                return new SetGroupAcceptNotices();
            case -65165:
                return new GroupRoleDataRequest();
            case -65164:
                return new GroupRoleDataReply();
            case -65163:
                return new GroupRoleMembersRequest();
            case -65162:
                return new GroupRoleMembersReply();
            case -65161:
                return new GroupTitlesRequest();
            case -65160:
                return new GroupTitlesReply();
            case -65159:
                return new GroupTitleUpdate();
            case -65158:
                return new GroupRoleUpdate();
            case -65157:
                return new LiveHelpGroupRequest();
            case -65156:
                return new LiveHelpGroupReply();
            case -65155:
                return new AgentWearablesRequest();
            case -65154:
                return new AgentWearablesUpdate();
            case -65153:
                return new AgentIsNowWearing();
            case -65152:
                return new AgentCachedTexture();
            case -65151:
                return new AgentCachedTextureResponse();
            case -65150:
                return new AgentDataUpdateRequest();
            case -65149:
                return new AgentDataUpdate();
            case -65148:
                return new GroupDataUpdate();
            case -65147:
                return new AgentGroupDataUpdate();
            case -65146:
                return new AgentDropGroup();
            case -65145:
                return new LogTextMessage();
            case -65144:
                return new CreateTrustedCircuit();
            case -65143:
                return new DenyTrustedCircuit();
            case -65142:
                return new RequestTrustedCircuit();
            case -65141:
                return new RezSingleAttachmentFromInv();
            case -65140:
                return new RezMultipleAttachmentsFromInv();
            case -65139:
                return new DetachAttachmentIntoInv();
            case -65138:
                return new CreateNewOutfitAttachments();
            case -65137:
                return new UserInfoRequest();
            case -65136:
                return new UserInfoReply();
            case -65135:
                return new UpdateUserInfo();
            case -65134:
                return new ParcelRename();
            case -65133:
                return new InitiateDownload();
            case -65132:
                return new SystemMessage();
            case -65131:
                return new MapLayerRequest();
            case -65130:
                return new MapLayerReply();
            case -65129:
                return new MapBlockRequest();
            case -65128:
                return new MapNameRequest();
            case -65127:
                return new MapBlockReply();
            case -65126:
                return new MapItemRequest();
            case -65125:
                return new MapItemReply();
            case -65124:
                return new SendPostcard();
            case -65123:
                return new RpcChannelRequest();
            case -65122:
                return new RpcChannelReply();
            case -65121:
                return new RpcScriptRequestInbound();
            case -65120:
                return new RpcScriptRequestInboundForward();
            case -65119:
                return new RpcScriptReplyInbound();
            case -65118:
                return new ScriptMailRegistration();
            case -65117:
                return new ParcelMediaCommandMessage();
            case -65116:
                return new ParcelMediaUpdate();
            case -65115:
                return new LandStatRequest();
            case -65114:
                return new LandStatReply();
            case -65113:
                return new Error();
            case -65112:
                return new ObjectIncludeInSearch();
            case -65111:
                return new RezRestoreToWorld();
            case -65110:
                return new LinkInventoryItem();
            case -5:
                return new PacketAck();
            case -4:
                return new OpenCircuit();
            case -3:
                return new CloseCircuit();
            case 1:
                return new StartPingCheck();
            case 2:
                return new CompletePingCheck();
            case 3:
                return new NeighborList();
            case 4:
                return new AgentUpdate();
            case 5:
                return new AgentAnimation();
            case 6:
                return new AgentRequestSit();
            case 7:
                return new AgentSit();
            case 8:
                return new RequestImage();
            case 9:
                return new ImageData();
            case 10:
                return new ImagePacket();
            case 11:
                return new LayerData();
            case 12:
                return new ObjectUpdate();
            case 13:
                return new ObjectUpdateCompressed();
            case 14:
                return new ObjectUpdateCached();
            case 15:
                return new ImprovedTerseObjectUpdate();
            case 16:
                return new KillObject();
            case 17:
                return new TransferPacket();
            case 18:
                return new SendXferPacket();
            case 19:
                return new ConfirmXferPacket();
            case 20:
                return new AvatarAnimation();
            case 21:
                return new AvatarSitResponse();
            case 22:
                return new CameraConstraint();
            case 23:
                return new ParcelProperties();
            case 24:
                return new EdgeDataPacket();
            case 25:
                return new ChildAgentUpdate();
            case 26:
                return new ChildAgentAlive();
            case 27:
                return new ChildAgentPositionUpdate();
            case 28:
                return new AtomicPassObject();
            case 29:
                return new SoundTrigger();
            case 65281:
                return new ObjectAdd();
            case 65282:
                return new MultipleObjectUpdate();
            case 65283:
                return new RequestMultipleObjects();
            case 65284:
                return new ObjectPosition();
            case 65285:
                return new RequestObjectPropertiesFamily();
            case 65286:
                return new CoarseLocationUpdate();
            case 65287:
                return new CrossedRegion();
            case 65288:
                return new ConfirmEnableSimulator();
            case 65289:
                return new ObjectProperties();
            case 65290:
                return new ObjectPropertiesFamily();
            case 65291:
                return new ParcelPropertiesRequest();
            case 65292:
                return new SimStatus();
            case 65293:
                return new AttachedSound();
            case 65294:
                return new AttachedSoundGainChange();
            case 65295:
                return new PreloadSound();
            case 65296:
                return new InternalScriptMail();
            case 65297:
                return new ViewerEffect();
            default:
                return null;
        }
    }
}
