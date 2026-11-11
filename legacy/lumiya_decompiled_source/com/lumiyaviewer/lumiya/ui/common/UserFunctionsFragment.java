package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.SLGroupInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.chat.GroupNoticeFragment;
import com.lumiyaviewer.lumiya.ui.chat.PayUserFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class UserFunctionsFragment extends ChatterReloadableFragment implements ReloadableFragment {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f378xb1d54699 = null;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedIn = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.9
        private final /* synthetic */ void $m$0(Object obj) {
            ((UserFunctionsFragment) this).m584xeb75d0e8((Boolean) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    protected final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.10
        private final /* synthetic */ void $m$0(Object obj) {
            ((UserFunctionsFragment) this).m585xeb75d0e9((CurrentLocationInfo) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m570x2680ba3d() {
        if (f378xb1d54699 != null) {
            return f378xb1d54699;
        }
        int[] iArr = new int[ChatterID.ChatterType.valuesCustom().length];
        try {
            iArr[ChatterID.ChatterType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterID.ChatterType.Local.ordinal()] = 3;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterID.ChatterType.User.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        f378xb1d54699 = iArr;
        return iArr;
    }

    private void handleEnableVoice() {
        if (VoicePluginServiceConnection.checkPluginInstalled(getContext())) {
            new AlertDialog.Builder(getContext()).setMessage(getContext().getString(R.string.enable_voice_question)).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.1
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    UserFunctionsFragment.m572x54333d46(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setCancelable(true).create().show();
        } else {
            new AlertDialog.Builder(getContext()).setTitle(R.string.enable_voice).setMessage(getContext().getString(R.string.enable_voice_plugin_message, LicenseChecker.APP_STORE_NAME)).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.7
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((UserFunctionsFragment) this).m586x54293cc7(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setCancelable(true).create().show();
        }
    }

    private void handlePlayParcelMedia() {
        StreamingMediaService.startStreamingMediaService(getContext(), this.userManager);
    }

    private void handleTeleportTo(final SLAgentCircuit sLAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        if (sLAgentCircuit != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.teleport_to_user_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.15
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((UserFunctionsFragment) this).m588x5441d035((SLAgentCircuit) sLAgentCircuit, (ChatterID.ChatterIDUser) chatterIDUser, dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.3
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            });
            builder.create().show();
        }
    }

    private void handleUserAddFriend(final SLAgentCircuit sLAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(getContext());
        textFieldDialogBuilder.setTitle(getString(R.string.offer_friendship_title));
        textFieldDialogBuilder.setDefaultText(getString(R.string.default_friendship_message));
        textFieldDialogBuilder.setOnTextEnteredListener(new TextFieldDialogBuilder.OnTextEnteredListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.12
            private final /* synthetic */ void $m$0(String str) {
                ((SLAgentCircuit) sLAgentCircuit).AddFriend(((ChatterID.ChatterIDUser) chatterIDUser).getChatterUUID(), str);
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder.OnTextEnteredListener
            public final void onTextEntered(String str) {
                $m$0(str);
            }
        });
        textFieldDialogBuilder.show();
    }

    private void handleUserCloseChat(ChatterID chatterID, boolean z) {
        UserManager userManager;
        boolean z2 = true;
        if (chatterID == null || (userManager = chatterID.getUserManager()) == null) {
            return;
        }
        ActiveChattersManager activeChattersManager = userManager.getChatterList().getActiveChattersManager();
        if (!z && chatterID.getChatterType() != ChatterID.ChatterType.Group) {
            z2 = false;
        }
        activeChattersManager.markChatterInactive(chatterID, z2);
        if (this instanceof ChatFragment) {
            FragmentActivity activity = getActivity();
            if ((activity instanceof DetailsActivity) && !((DetailsActivity) activity).closeDetailsFragment(this) && (activity instanceof ChatNewActivity)) {
                DetailsActivity.showDetails(activity, ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(ChatterID.getLocalChatterID(userManager.getUserID())));
            }
        }
    }

    private void handleUserMute(final ChatterID chatterID) {
        final UserManager userManager = this.userManager;
        if (chatterID == null || userManager == null) {
            return;
        }
        final String resolvedName = this.nameRetriever != null ? this.nameRetriever.getResolvedName() : null;
        if (resolvedName == null) {
            resolvedName = getString(R.string.name_loading_title);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.block_confirm_message, resolvedName)).setCancelable(true);
        CharSequence[] charSequenceArr = userManager.getActiveAgentCircuit() != null ? new CharSequence[]{getString(R.string.mute_action_description), getString(R.string.block_action_description)} : new CharSequence[]{getString(R.string.mute_action_description)};
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        builder.setSingleChoiceItems(charSequenceArr, 0, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.8
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((AtomicInteger) atomicInteger).set(i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.17
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((UserFunctionsFragment) this).m587x543591ca((AtomicInteger) atomicInteger, (ChatterID) chatterID, (UserManager) userManager, (String) resolvedName, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.4
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    private void handleUserOfferTeleport(UserManager userManager, final SLAgentCircuit sLAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        ParcelData parcelData;
        CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
        String str = "";
        if (currentLocationInfoSnapshot != null && (parcelData = currentLocationInfoSnapshot.parcelData()) != null) {
            str = Strings.nullToEmpty(parcelData.getName());
        }
        TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(getContext());
        textFieldDialogBuilder.setTitle(getString(R.string.offer_teleport_title));
        textFieldDialogBuilder.setDefaultText("Join me in " + str);
        textFieldDialogBuilder.setOnTextEnteredListener(new TextFieldDialogBuilder.OnTextEnteredListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.13
            private final /* synthetic */ void $m$0(String str2) {
                ((SLAgentCircuit) sLAgentCircuit).OfferTeleport(((ChatterID.ChatterIDUser) chatterIDUser).getChatterUUID(), str2);
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder.OnTextEnteredListener
            public final void onTextEntered(String str2) {
                $m$0(str2);
            }
        });
        textFieldDialogBuilder.show();
    }

    private void handleUserOpenChat(ChatterID chatterID) {
        DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(chatterID));
    }

    private void handleUserPayUser(ChatterID.ChatterIDUser chatterIDUser) {
        DetailsActivity.showEmbeddedDetails(getActivity(), PayUserFragment.class, PayUserFragment.makeSelection(chatterIDUser));
    }

    private void handleUserRemoveFriend(final SLAgentCircuit sLAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String resolvedName = this.nameRetriever != null ? this.nameRetriever.getResolvedName() : null;
        if (resolvedName == null) {
            resolvedName = getString(R.string.name_loading_title);
        }
        builder.setMessage(String.format(getString(R.string.delete_friend_title_format), resolvedName)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.11
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                UserFunctionsFragment.m579x54375b69((SLAgentCircuit) sLAgentCircuit, (ChatterID.ChatterIDUser) chatterIDUser, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.5
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    private void handleUserRequestTeleport(final SLAgentCircuit sLAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(getContext());
        textFieldDialogBuilder.setTitle(getString(R.string.request_teleport_title));
        textFieldDialogBuilder.setOnTextEnteredListener(new TextFieldDialogBuilder.OnTextEnteredListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.14
            private final /* synthetic */ void $m$0(String str) {
                ((SLAgentCircuit) sLAgentCircuit).RequestTeleport(((ChatterID.ChatterIDUser) chatterIDUser).getChatterUUID(), str);
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder.OnTextEnteredListener
            public final void onTextEntered(String str) {
                $m$0(str);
            }
        });
        textFieldDialogBuilder.show();
    }

    private void handleUserShareObject(ChatterID.ChatterIDUser chatterIDUser) {
        startActivity(InventoryActivity.makeTransferIntent(getContext(), chatterIDUser.agentUUID, chatterIDUser.getChatterUUID(), this.nameRetriever != null ? this.nameRetriever.getResolvedName() : null));
    }

    private void handleUserUnblock(final ChatterID chatterID) {
        final UserManager userManager = this.userManager;
        if (chatterID == null || userManager == null) {
            return;
        }
        final String resolvedName = this.nameRetriever != null ? this.nameRetriever.getResolvedName() : null;
        if (resolvedName == null) {
            resolvedName = getString(R.string.name_loading_title);
        }
        new AlertDialog.Builder(getContext()).setMessage(getString(R.string.unblock_confirm_message, resolvedName)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.16
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                UserFunctionsFragment.m574x543499cf((UserManager) userManager, (ChatterID) chatterID, (String) resolvedName, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks.6
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).create().show();
    }

    private void handleUserUnmute(ChatterID chatterID) {
        UserManager userManager = this.userManager;
        if (chatterID == null || userManager == null) {
            return;
        }
        userManager.getChatterList().getActiveChattersManager().unmuteChatter(chatterID);
    }

    private void handleViewLocationDetails() {
        CurrentLocationInfo currentLocationInfoSnapshot;
        ParcelData parcelData;
        if (this.userManager == null || (currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot()) == null || (parcelData = currentLocationInfoSnapshot.parcelData()) == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), ParcelPropertiesFragment.class, ParcelPropertiesFragment.makeSelection(this.userManager.getUserID(), parcelData));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_20370  reason: not valid java name */
    public static /* synthetic */ void m572x54333d46(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        GlobalOptions.getInstance().enableVoice();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_23327  reason: not valid java name */
    public static /* synthetic */ void m574x543499cf(UserManager userManager, ChatterID chatterID, String str, DialogInterface dialogInterface, int i) {
        userManager.getChatterList().getActiveChattersManager().unmuteChatter(chatterID);
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            if (chatterID instanceof ChatterID.ChatterIDUser) {
                activeAgentCircuit.getModules().muteList.Unblock(new MuteListEntry(MuteType.AGENT, ((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), str, 15));
            } else if (chatterID instanceof ChatterID.ChatterIDGroup) {
                activeAgentCircuit.getModules().muteList.Unblock(new MuteListEntry(MuteType.GROUP, ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID(), str, 15));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_29514  reason: not valid java name */
    public static /* synthetic */ void m579x54375b69(SLAgentCircuit sLAgentCircuit, ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        sLAgentCircuit.RemoveFriend(chatterIDUser.getChatterUUID());
    }

    private void performTeleportTo(SLAgentCircuit sLAgentCircuit, ChatterID.ChatterIDUser chatterIDUser) {
        if (sLAgentCircuit != null) {
            LLVector3 nearbyAgentLocation = sLAgentCircuit.getModules().minimap.getNearbyAgentLocation(chatterIDUser.getChatterUUID());
            if (nearbyAgentLocation != null) {
                if (sLAgentCircuit.TeleportToLocalPosition(nearbyAgentLocation)) {
                    new TeleportProgressDialog(getContext(), this.userManager, R.string.teleporting_progress_message).show();
                }
            } else if (sLAgentCircuit.getModules().worldMap.TeleportToAgent(chatterIDUser.getChatterUUID())) {
                new TeleportProgressDialog(getContext(), this.userManager, R.string.teleporting_progress_message).show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleStartVoice(ChatterID chatterID) {
        CurrentLocationInfo currentLocationInfoSnapshot;
        VoiceChannelInfo parcelVoiceChannel;
        UserManager userManager = chatterID != null ? chatterID.getUserManager() : null;
        SLAgentCircuit activeAgentCircuit = userManager != null ? userManager.getActiveAgentCircuit() : null;
        if (chatterID == null || userManager == null || activeAgentCircuit == null) {
            return;
        }
        if (chatterID.getChatterType() == ChatterID.ChatterType.User) {
            activeAgentCircuit.getModules().voice.userVoiceChatRequest(chatterID.getOptionalChatterUUID());
        } else if (chatterID.getChatterType() == ChatterID.ChatterType.Group) {
            activeAgentCircuit.getModules().voice.groupVoiceChatRequest(chatterID.getOptionalChatterUUID());
        } else if (chatterID.getChatterType() != ChatterID.ChatterType.Local || (currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot()) == null || (parcelVoiceChannel = currentLocationInfoSnapshot.parcelVoiceChannel()) == null) {
        } else {
            activeAgentCircuit.getModules().voice.nearbyVoiceChatRequest(parcelVoiceChannel);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleUserViewProfile(ChatterID chatterID) {
        if (chatterID == null || !chatterID.isValidUUID()) {
            return;
        }
        switch (m570x2680ba3d()[chatterID.getChatterType().ordinal()]) {
            case 1:
                DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(chatterID));
                return;
            case 2:
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(chatterID));
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isVoiceLoggedIn() {
        Boolean data = this.voiceLoggedIn.getData();
        if (data != null) {
            return data.booleanValue();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_19348  reason: not valid java name */
    public /* synthetic */ void m586x54293cc7(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        VoicePluginServiceConnection.setInstallOfferDisplayed(true);
        GlobalOptions.getInstance().enableVoice();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(LicenseChecker.VOICE_PLUGIN_URL));
        getContext().startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_25742  reason: not valid java name */
    public /* synthetic */ void m587x543591ca(AtomicInteger atomicInteger, ChatterID chatterID, UserManager userManager, String str, DialogInterface dialogInterface, int i) {
        if (atomicInteger.get() == 0) {
            handleUserCloseChat(chatterID, true);
        } else if (atomicInteger.get() == 1) {
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                if (chatterID instanceof ChatterID.ChatterIDUser) {
                    activeAgentCircuit.getModules().muteList.Block(new MuteListEntry(MuteType.AGENT, ((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), str, 15));
                } else if (chatterID instanceof ChatterID.ChatterIDGroup) {
                    activeAgentCircuit.getModules().muteList.Block(new MuteListEntry(MuteType.GROUP, ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID(), str, 15));
                }
            }
            handleUserCloseChat(chatterID, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_31539  reason: not valid java name */
    public /* synthetic */ void m588x5441d035(SLAgentCircuit sLAgentCircuit, ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        performTeleportTo(sLAgentCircuit, chatterIDUser);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.user_list_context_menu, menu);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @CallSuper
    /* renamed from: onCurrentLocationChanged */
    public void m585xeb75d0e9(CurrentLocationInfo currentLocationInfo) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }

    @EventHandler
    public void onGlobalOptionsChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        UserManager userManager = this.chatterID != null ? this.chatterID.getUserManager() : null;
        if (this.chatterID != null && userManager != null) {
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            switch (menuItem.getItemId()) {
                case R.id.item_close /* 2131755779 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) || (this.chatterID instanceof ChatterID.ChatterIDGroup)) {
                        handleUserCloseChat(this.chatterID, false);
                    }
                    return true;
                case R.id.item_unmute /* 2131755781 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) || (this.chatterID instanceof ChatterID.ChatterIDGroup)) {
                        handleUserUnmute(this.chatterID);
                    }
                    return true;
                case R.id.item_close_and_mute /* 2131755846 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) || (this.chatterID instanceof ChatterID.ChatterIDGroup)) {
                        handleUserMute(this.chatterID);
                    }
                    return true;
                case R.id.item_open_chat /* 2131755860 */:
                    handleUserOpenChat(this.chatterID);
                    return true;
                case R.id.item_view_profile /* 2131755861 */:
                    handleUserViewProfile(this.chatterID);
                    return true;
                case R.id.item_start_voice /* 2131755862 */:
                    handleStartVoice(this.chatterID);
                    return true;
                case R.id.item_location_details /* 2131755863 */:
                    handleViewLocationDetails();
                    return true;
                case R.id.item_play_parcel_media /* 2131755864 */:
                    handlePlayParcelMedia();
                    return true;
                case R.id.item_send_group_notice /* 2131755865 */:
                    DetailsActivity.showEmbeddedDetails(getActivity(), GroupNoticeFragment.class, GroupNoticeFragment.makeSelection(this.chatterID));
                    return true;
                case R.id.item_offer_teleport /* 2131755866 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleUserOfferTeleport(userManager, activeAgentCircuit, (ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_request_teleport /* 2131755867 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleUserRequestTeleport(activeAgentCircuit, (ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_teleport_to /* 2131755868 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleTeleportTo(activeAgentCircuit, (ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_pay_user /* 2131755869 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleUserPayUser((ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_share_object /* 2131755870 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleUserShareObject((ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_add_friend /* 2131755871 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleUserAddFriend(activeAgentCircuit, (ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_remove_friend /* 2131755872 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) && activeAgentCircuit != null) {
                        handleUserRemoveFriend(activeAgentCircuit, (ChatterID.ChatterIDUser) this.chatterID);
                    }
                    return true;
                case R.id.item_enable_voice /* 2131755873 */:
                    handleEnableVoice();
                    return true;
                case R.id.item_unblock /* 2131755874 */:
                    if ((this.chatterID instanceof ChatterID.ChatterIDUser) || (this.chatterID instanceof ChatterID.ChatterIDGroup)) {
                        handleUserUnblock(this.chatterID);
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // android.support.v4.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        boolean z;
        boolean z2;
        boolean z3;
        AvatarGroupList avatarGroupList;
        AvatarGroupList.AvatarGroupEntry avatarGroupEntry;
        super.onPrepareOptionsMenu(menu);
        int[] iArr = {R.id.item_open_chat, R.id.item_view_profile, R.id.item_location_details, R.id.item_play_parcel_media, R.id.item_send_group_notice, R.id.item_offer_teleport, R.id.item_request_teleport, R.id.item_teleport_to, R.id.item_pay_user, R.id.item_share_object, R.id.item_add_friend, R.id.item_remove_friend, R.id.item_close, R.id.item_close_and_mute, R.id.item_unmute, R.id.item_unblock, R.id.item_start_voice, R.id.item_enable_voice};
        UserManager userManager = this.chatterID != null ? this.chatterID.getUserManager() : null;
        if (this.chatterID == null || userManager == null) {
            for (int i : iArr) {
                MenuItem findItem = menu.findItem(i);
                if (findItem != null) {
                    findItem.setVisible(false);
                }
            }
            return;
        }
        Debug.Printf("UserMenu: item type %s", this.chatterID.getChatterType());
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        CurrentLocationInfo data = this.currentLocationInfo.getData();
        boolean z4 = activeAgentCircuit != null;
        boolean z5 = this.chatterID instanceof ChatterID.ChatterIDLocal;
        boolean z6 = this.chatterID instanceof ChatterID.ChatterIDUser;
        boolean z7 = this.chatterID instanceof ChatterID.ChatterIDGroup;
        boolean z8 = false;
        boolean z9 = false;
        boolean z10 = false;
        boolean voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled();
        boolean isVoiceLoggedIn = isVoiceLoggedIn();
        boolean z11 = false;
        boolean isPluginSupported = !voiceEnabled ? VoicePluginServiceConnection.isPluginSupported() : false;
        if (!z5 || data == null) {
            z = isPluginSupported;
            z2 = false;
        } else {
            ParcelData parcelData = data.parcelData();
            if (parcelData != null) {
                z9 = true;
                z10 = !Strings.isNullOrEmpty(parcelData.getMediaURL());
            }
            z11 = voiceEnabled && isVoiceLoggedIn && data.parcelVoiceChannel() != null;
            if (!isPluginSupported || data.parcelVoiceChannel() == null) {
                z = false;
                z2 = z9;
            } else {
                z = true;
                z2 = z9;
            }
        }
        if (z7 || z6) {
            z11 = voiceEnabled ? isVoiceLoggedIn : false;
        }
        Friend friend = z6 ? userManager.getChatterList().getFriendManager().getFriend(this.chatterID.getOptionalChatterUUID()) : null;
        boolean z12 = false;
        if (z6 || z7) {
            Chatter chatter = userManager.getChatterList().getActiveChattersManager().getChatter(this.chatterID);
            z12 = chatter != null ? chatter.getActive() : false;
            boolean muted = chatter != null ? chatter.getMuted() : false;
            if (activeAgentCircuit != null) {
                z8 = activeAgentCircuit.getModules().muteList.isMuted(this.chatterID.getOptionalChatterUUID(), z7 ? MuteType.GROUP : MuteType.AGENT);
                z3 = muted;
            } else {
                z8 = false;
                z3 = muted;
            }
        } else {
            z3 = false;
        }
        boolean z13 = z6 && friend != null;
        boolean isInstance = ChatFragment.class.isInstance(this);
        boolean isInstance2 = z6 ? UserProfileFragment.class.isInstance(this) : z7 ? GroupProfileFragment.class.isInstance(this) : false;
        boolean canTeleportToLocation = (z6 && z4) ? activeAgentCircuit.getModules().rlvController.canTeleportToLocation() : false;
        boolean z14 = false;
        if (z6 && z4 && canTeleportToLocation) {
            z14 = activeAgentCircuit.getModules().minimap.getNearbyAgentLocation(this.chatterID.getOptionalChatterUUID()) != null;
            if (friend != null) {
                z14 |= (friend.getRightsHas() & 2) != 0;
            }
        }
        boolean z15 = z7 ? (!z4 || (avatarGroupList = userManager.getChatterList().getGroupManager().getAvatarGroupList()) == null || (avatarGroupEntry = avatarGroupList.Groups.get(this.chatterID.getOptionalChatterUUID())) == null || (avatarGroupEntry.GroupPowers & SLGroupInfo.GP_NOTICES_SEND) == 0) ? false : true : false;
        int i2 = 0;
        int length = iArr.length;
        while (true) {
            int i3 = i2;
            if (i3 >= length) {
                return;
            }
            int i4 = iArr[i3];
            MenuItem findItem2 = menu.findItem(i4);
            if (findItem2 != null) {
                switch (i4) {
                    case R.id.item_close /* 2131755779 */:
                        findItem2.setVisible(((z6 || z7) && isInstance) ? z12 : false);
                        continue;
                    case R.id.item_unmute /* 2131755781 */:
                        findItem2.setVisible(((z6 || z7) && z3) ? !z8 : false);
                        continue;
                    case R.id.item_close_and_mute /* 2131755846 */:
                        findItem2.setVisible(((z6 || z7) && isInstance && z12) ? !(z3 ? z8 : false) : false);
                        continue;
                    case R.id.item_open_chat /* 2131755860 */:
                        findItem2.setVisible((z6 || z7) ? !isInstance : false);
                        continue;
                    case R.id.item_view_profile /* 2131755861 */:
                        findItem2.setVisible((z6 || z7) ? !isInstance2 : false);
                        continue;
                    case R.id.item_start_voice /* 2131755862 */:
                        findItem2.setVisible(z4 ? z11 : false);
                        continue;
                    case R.id.item_location_details /* 2131755863 */:
                        findItem2.setVisible(z5 ? z2 : false);
                        continue;
                    case R.id.item_play_parcel_media /* 2131755864 */:
                        findItem2.setVisible((z5 && z2) ? z10 : false);
                        continue;
                    case R.id.item_send_group_notice /* 2131755865 */:
                        findItem2.setVisible((z7 && z4) ? z15 : false);
                        continue;
                    case R.id.item_offer_teleport /* 2131755866 */:
                        findItem2.setVisible(z6 ? z4 : false);
                        continue;
                    case R.id.item_request_teleport /* 2131755867 */:
                        findItem2.setVisible(z6 ? z4 : false);
                        continue;
                    case R.id.item_teleport_to /* 2131755868 */:
                        findItem2.setVisible((z6 && z4 && canTeleportToLocation) ? z14 : false);
                        continue;
                    case R.id.item_pay_user /* 2131755869 */:
                        findItem2.setVisible(z6 ? z4 : false);
                        continue;
                    case R.id.item_share_object /* 2131755870 */:
                        findItem2.setVisible(z6 ? z4 : false);
                        continue;
                    case R.id.item_add_friend /* 2131755871 */:
                        findItem2.setVisible((z6 && z4) ? !z13 : false);
                        continue;
                    case R.id.item_remove_friend /* 2131755872 */:
                        findItem2.setVisible((z6 && z4) ? z13 : false);
                        continue;
                    case R.id.item_enable_voice /* 2131755873 */:
                        findItem2.setVisible(z);
                        continue;
                    case R.id.item_unblock /* 2131755874 */:
                        findItem2.setVisible(((z6 || z7) && z8) ? z4 : false);
                        continue;
                }
            }
            i2 = i3 + 1;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment, com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        UserManager userManager = this.chatterID != null ? this.chatterID.getUserManager() : null;
        if (userManager != null) {
            this.voiceLoggedIn.subscribe(userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        } else {
            this.voiceLoggedIn.unsubscribe();
            this.currentLocationInfo.unsubscribe();
        }
        EventBus.getInstance().subscribe(this);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        this.voiceLoggedIn.unsubscribe();
        this.currentLocationInfo.unsubscribe();
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @CallSuper
    /* renamed from: onVoiceLoginStatusChanged */
    public void m584xeb75d0e8(Boolean bool) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }
}
