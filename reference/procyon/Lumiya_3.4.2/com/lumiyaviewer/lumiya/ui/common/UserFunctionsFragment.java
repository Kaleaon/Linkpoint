// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.ui.chat.GroupNoticeFragment;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import android.support.annotation.CallSuper;
import android.view.MenuInflater;
import android.view.Menu;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.GlobalOptions;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.chat.PayUserFragment;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.google.common.base.Strings;
import java.util.concurrent.atomic.AtomicInteger;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import android.content.Context;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;

public abstract class UserFunctionsFragment extends ChatterReloadableFragment implements ReloadableFragment
{
    protected final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedIn;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues() {
        if (UserFunctionsFragment.-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues != null) {
            return UserFunctionsFragment.-com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = new int[ChatterID.ChatterType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterID.ChatterType.Group.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterID.ChatterType.Local.ordinal()] = 3;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues[ChatterID.ChatterType.User.ordinal()] = 2;
                        return -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    public UserFunctionsFragment() {
        this.voiceLoggedIn = new SubscriptionData<SubscriptionSingleKey, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$9(this));
        this.currentLocationInfo = new SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(UIThreadExecutor.getInstance(), new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$10(this));
    }
    
    private void handleEnableVoice() {
        if (!VoicePluginServiceConnection.checkPluginInstalled(this.getContext())) {
            new AlertDialog.Builder(this.getContext()).setTitle(2131296531).setMessage(this.getContext().getString(2131296532, new Object[] { "Google Play" })).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$7(this)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks()).setCancelable(true).create().show();
        }
        else {
            new AlertDialog.Builder(this.getContext()).setMessage(this.getContext().getString(2131296533)).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$1()).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$2()).setCancelable(true).create().show();
        }
    }
    
    private void handlePlayParcelMedia() {
        StreamingMediaService.startStreamingMediaService(this.getContext(), this.userManager);
    }
    
    private void handleTeleportTo(final SLAgentCircuit slAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        if (slAgentCircuit != null) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this.getActivity());
            alertDialog$Builder.setMessage((CharSequence)this.getString(2131297101)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$15(this, slAgentCircuit, chatterIDUser)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$3());
            alertDialog$Builder.create().show();
        }
    }
    
    private void handleUserAddFriend(final SLAgentCircuit slAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        final TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(this.getContext());
        textFieldDialogBuilder.setTitle(this.getString(2131296846));
        textFieldDialogBuilder.setDefaultText(this.getString(2131296472));
        textFieldDialogBuilder.setOnTextEnteredListener((TextFieldDialogBuilder.OnTextEnteredListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$12(slAgentCircuit, chatterIDUser));
        textFieldDialogBuilder.show();
    }
    
    private void handleUserCloseChat(final ChatterID chatterID, final boolean b) {
        final boolean b2 = true;
        if (chatterID != null) {
            final UserManager userManager = chatterID.getUserManager();
            if (userManager != null) {
                final ActiveChattersManager activeChattersManager = userManager.getChatterList().getActiveChattersManager();
                boolean b3 = b2;
                if (!b) {
                    b3 = (chatterID.getChatterType() == ChatterID.ChatterType.Group && b2);
                }
                activeChattersManager.markChatterInactive(chatterID, b3);
                if (this instanceof ChatFragment) {
                    final FragmentActivity activity = this.getActivity();
                    if (activity instanceof DetailsActivity && !((DetailsActivity)activity).closeDetailsFragment(this) && activity instanceof ChatNewActivity) {
                        DetailsActivity.showDetails(activity, ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(ChatterID.getLocalChatterID(userManager.getUserID())));
                    }
                }
            }
        }
    }
    
    private void handleUserMute(final ChatterID chatterID) {
        String s = null;
        final UserManager userManager = this.userManager;
        if (chatterID != null && userManager != null) {
            if (this.nameRetriever != null) {
                s = this.nameRetriever.getResolvedName();
            }
            if (s == null) {
                s = this.getString(2131296712);
            }
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setTitle((CharSequence)this.getString(2131296383, s)).setCancelable(true);
            CharSequence[] array;
            if (userManager.getActiveAgentCircuit() != null) {
                array = new CharSequence[] { this.getString(2131296703), this.getString(2131296382) };
            }
            else {
                array = new CharSequence[] { this.getString(2131296703) };
            }
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            alertDialog$Builder.setSingleChoiceItems(array, 0, (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$8(atomicInteger));
            alertDialog$Builder.setPositiveButton((CharSequence)"OK", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$17(this, atomicInteger, chatterID, userManager, s));
            alertDialog$Builder.setNegativeButton((CharSequence)"Cancel", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$4());
            alertDialog$Builder.create().show();
        }
    }
    
    private void handleUserOfferTeleport(final UserManager userManager, final SLAgentCircuit slAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        final CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
        String nullToEmpty = "";
        if (currentLocationInfoSnapshot != null) {
            final ParcelData parcelData = currentLocationInfoSnapshot.parcelData();
            nullToEmpty = nullToEmpty;
            if (parcelData != null) {
                nullToEmpty = Strings.nullToEmpty(parcelData.getName());
            }
        }
        final TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(this.getContext());
        textFieldDialogBuilder.setTitle(this.getString(2131296847));
        textFieldDialogBuilder.setDefaultText("Join me in " + nullToEmpty);
        textFieldDialogBuilder.setOnTextEnteredListener((TextFieldDialogBuilder.OnTextEnteredListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$13(slAgentCircuit, chatterIDUser));
        textFieldDialogBuilder.show();
    }
    
    private void handleUserOpenChat(final ChatterID chatterID) {
        DetailsActivity.showDetails(this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(chatterID));
    }
    
    private void handleUserPayUser(final ChatterID.ChatterIDUser chatterIDUser) {
        DetailsActivity.showEmbeddedDetails(this.getActivity(), PayUserFragment.class, ChatterFragment.makeSelection(chatterIDUser));
    }
    
    private void handleUserRemoveFriend(final SLAgentCircuit slAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        String s = null;
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
        if (this.nameRetriever != null) {
            s = this.nameRetriever.getResolvedName();
        }
        if (s == null) {
            s = this.getString(2131296712);
        }
        alertDialog$Builder.setMessage((CharSequence)String.format(this.getString(2131296476), s)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$11(slAgentCircuit, chatterIDUser)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$5());
        alertDialog$Builder.create().show();
    }
    
    private void handleUserRequestTeleport(final SLAgentCircuit slAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        final TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(this.getContext());
        textFieldDialogBuilder.setTitle(this.getString(2131296933));
        textFieldDialogBuilder.setOnTextEnteredListener((TextFieldDialogBuilder.OnTextEnteredListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$14(slAgentCircuit, chatterIDUser));
        textFieldDialogBuilder.show();
    }
    
    private void handleUserShareObject(final ChatterID.ChatterIDUser chatterIDUser) {
        String resolvedName = null;
        if (this.nameRetriever != null) {
            resolvedName = this.nameRetriever.getResolvedName();
        }
        this.startActivity(InventoryActivity.makeTransferIntent(this.getContext(), chatterIDUser.agentUUID, chatterIDUser.getChatterUUID(), resolvedName));
    }
    
    private void handleUserUnblock(final ChatterID chatterID) {
        String s = null;
        final UserManager userManager = this.userManager;
        if (chatterID != null && userManager != null) {
            if (this.nameRetriever != null) {
                s = this.nameRetriever.getResolvedName();
            }
            if (s == null) {
                s = this.getString(2131296712);
            }
            new AlertDialog$Builder(this.getContext()).setMessage((CharSequence)this.getString(2131297120, s)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$16(userManager, chatterID, s)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$6()).create().show();
        }
    }
    
    private void handleUserUnmute(final ChatterID chatterID) {
        final UserManager userManager = this.userManager;
        if (chatterID != null && userManager != null) {
            userManager.getChatterList().getActiveChattersManager().unmuteChatter(chatterID);
        }
    }
    
    private void handleViewLocationDetails() {
        if (this.userManager != null) {
            final CurrentLocationInfo currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot();
            if (currentLocationInfoSnapshot != null) {
                final ParcelData parcelData = currentLocationInfoSnapshot.parcelData();
                if (parcelData != null) {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), ParcelPropertiesFragment.class, ParcelPropertiesFragment.makeSelection(this.userManager.getUserID(), parcelData));
                }
            }
        }
    }
    
    private void performTeleportTo(final SLAgentCircuit slAgentCircuit, final ChatterID.ChatterIDUser chatterIDUser) {
        if (slAgentCircuit != null) {
            final LLVector3 nearbyAgentLocation = slAgentCircuit.getModules().minimap.getNearbyAgentLocation(chatterIDUser.getChatterUUID());
            if (nearbyAgentLocation != null) {
                if (slAgentCircuit.TeleportToLocalPosition(nearbyAgentLocation)) {
                    new TeleportProgressDialog(this.getContext(), this.userManager, 2131297104).show();
                }
            }
            else if (slAgentCircuit.getModules().worldMap.TeleportToAgent(chatterIDUser.getChatterUUID())) {
                new TeleportProgressDialog(this.getContext(), this.userManager, 2131297104).show();
            }
        }
    }
    
    protected void handleStartVoice(final ChatterID chatterID) {
        SLAgentCircuit activeAgentCircuit = null;
        UserManager userManager;
        if (chatterID != null) {
            userManager = chatterID.getUserManager();
        }
        else {
            userManager = null;
        }
        if (userManager != null) {
            activeAgentCircuit = userManager.getActiveAgentCircuit();
        }
        if (chatterID != null && userManager != null && activeAgentCircuit != null) {
            if (chatterID.getChatterType() == ChatterID.ChatterType.User) {
                activeAgentCircuit.getModules().voice.userVoiceChatRequest(chatterID.getOptionalChatterUUID());
            }
            else if (chatterID.getChatterType() == ChatterID.ChatterType.Group) {
                activeAgentCircuit.getModules().voice.groupVoiceChatRequest(chatterID.getOptionalChatterUUID());
            }
            else if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
                final CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
                if (currentLocationInfoSnapshot != null) {
                    final VoiceChannelInfo parcelVoiceChannel = currentLocationInfoSnapshot.parcelVoiceChannel();
                    if (parcelVoiceChannel != null) {
                        activeAgentCircuit.getModules().voice.nearbyVoiceChatRequest(parcelVoiceChannel);
                    }
                }
            }
        }
    }
    
    protected void handleUserViewProfile(final ChatterID chatterID) {
        if (chatterID != null && chatterID.isValidUUID()) {
            switch (-getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues()[chatterID.getChatterType().ordinal()]) {
                case 2: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(chatterID));
                    break;
                }
                case 1: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), GroupProfileFragment.class, ChatterFragment.makeSelection(chatterID));
                    break;
                }
            }
        }
    }
    
    protected boolean isVoiceLoggedIn() {
        final Boolean b = this.voiceLoggedIn.getData();
        return b != null && b;
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886114, menu);
    }
    
    @CallSuper
    protected void onCurrentLocationChanged(final CurrentLocationInfo currentLocationInfo) {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }
    
    @EventHandler
    public void onGlobalOptionsChanged(final GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        UserManager userManager;
        if (this.chatterID != null) {
            userManager = this.chatterID.getUserManager();
        }
        else {
            userManager = null;
        }
        if (this.chatterID != null && userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            switch (menuItem.getItemId()) {
                case 2131755860: {
                    this.handleUserOpenChat(this.chatterID);
                    return true;
                }
                case 2131755861: {
                    this.handleUserViewProfile(this.chatterID);
                    return true;
                }
                case 2131755863: {
                    this.handleViewLocationDetails();
                    return true;
                }
                case 2131755864: {
                    this.handlePlayParcelMedia();
                    return true;
                }
                case 2131755865: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), GroupNoticeFragment.class, ChatterFragment.makeSelection(this.chatterID));
                    return true;
                }
                case 2131755866: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleUserOfferTeleport(userManager, activeAgentCircuit, (ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755867: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleUserRequestTeleport(activeAgentCircuit, (ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755868: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleTeleportTo(activeAgentCircuit, (ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755869: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleUserPayUser((ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755870: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleUserShareObject((ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755871: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleUserAddFriend(activeAgentCircuit, (ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755872: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser && activeAgentCircuit != null) {
                        this.handleUserRemoveFriend(activeAgentCircuit, (ChatterID.ChatterIDUser)this.chatterID);
                    }
                    return true;
                }
                case 2131755779: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser || this.chatterID instanceof ChatterID.ChatterIDGroup) {
                        this.handleUserCloseChat(this.chatterID, false);
                    }
                    return true;
                }
                case 2131755846: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser || this.chatterID instanceof ChatterID.ChatterIDGroup) {
                        this.handleUserMute(this.chatterID);
                    }
                    return true;
                }
                case 2131755781: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser || this.chatterID instanceof ChatterID.ChatterIDGroup) {
                        this.handleUserUnmute(this.chatterID);
                    }
                    return true;
                }
                case 2131755874: {
                    if (this.chatterID instanceof ChatterID.ChatterIDUser || this.chatterID instanceof ChatterID.ChatterIDGroup) {
                        this.handleUserUnblock(this.chatterID);
                    }
                    return true;
                }
                case 2131755862: {
                    this.handleStartVoice(this.chatterID);
                    return true;
                }
                case 2131755873: {
                    this.handleEnableVoice();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
    
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final int[] array2;
        final int[] array = array2 = new int[18];
        array2[0] = 2131755860;
        array2[1] = 2131755861;
        array2[2] = 2131755863;
        array2[3] = 2131755864;
        array2[4] = 2131755865;
        array2[5] = 2131755866;
        array2[6] = 2131755867;
        array2[7] = 2131755868;
        array2[8] = 2131755869;
        array2[9] = 2131755870;
        array2[10] = 2131755871;
        array2[11] = 2131755872;
        array2[12] = 2131755779;
        array2[13] = 2131755846;
        array2[14] = 2131755781;
        array2[15] = 2131755874;
        array2[16] = 2131755862;
        array2[17] = 2131755873;
        UserManager userManager;
        if (this.chatterID != null) {
            userManager = this.chatterID.getUserManager();
        }
        else {
            userManager = null;
        }
        if (this.chatterID != null && userManager != null) {
            Debug.Printf("UserMenu: item type %s", this.chatterID.getChatterType());
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            final CurrentLocationInfo currentLocationInfo = this.currentLocationInfo.getData();
            final boolean b = activeAgentCircuit != null;
            final boolean b2 = this.chatterID instanceof ChatterID.ChatterIDLocal;
            final boolean b3 = this.chatterID instanceof ChatterID.ChatterIDUser;
            final boolean b4 = this.chatterID instanceof ChatterID.ChatterIDGroup;
            final int n = 0;
            boolean b5 = false;
            boolean b6 = false;
            final boolean b7 = false;
            final boolean voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled();
            final boolean voiceLoggedIn = this.isVoiceLoggedIn();
            final boolean b8 = false;
            final boolean b9 = !voiceEnabled && VoicePluginServiceConnection.isPluginSupported();
            boolean visible;
            boolean b11;
            if (b2 && currentLocationInfo != null) {
                final ParcelData parcelData = currentLocationInfo.parcelData();
                b6 = b7;
                if (parcelData != null) {
                    b5 = true;
                    b6 = (Strings.isNullOrEmpty(parcelData.getMediaURL()) ^ true);
                }
                final boolean b10 = voiceEnabled && voiceLoggedIn && currentLocationInfo.parcelVoiceChannel() != null;
                if (b9 && currentLocationInfo.parcelVoiceChannel() != null) {
                    visible = true;
                    b11 = b10;
                }
                else {
                    visible = false;
                    b11 = b10;
                }
            }
            else {
                visible = b9;
                b5 = false;
                b11 = b8;
            }
            if (b4 || b3) {
                b11 = (voiceEnabled && voiceLoggedIn);
            }
            Friend friend;
            if (b3) {
                friend = userManager.getChatterList().getFriendManager().getFriend(this.chatterID.getOptionalChatterUUID());
            }
            else {
                friend = null;
            }
            boolean b12 = false;
            int n2;
            int n3;
            if (b3 || b4) {
                final Chatter chatter = userManager.getChatterList().getActiveChattersManager().getChatter(this.chatterID);
                b12 = (chatter != null && chatter.getActive());
                final boolean b13 = chatter != null && chatter.getMuted();
                if (activeAgentCircuit != null) {
                    final SLMuteList muteList = activeAgentCircuit.getModules().muteList;
                    final UUID optionalChatterUUID = this.chatterID.getOptionalChatterUUID();
                    MuteType muteType;
                    if (b4) {
                        muteType = MuteType.GROUP;
                    }
                    else {
                        muteType = MuteType.AGENT;
                    }
                    final boolean muted = muteList.isMuted(optionalChatterUUID, muteType);
                    n2 = (b13 ? 1 : 0);
                    n3 = (muted ? 1 : 0);
                }
                else {
                    final int n4 = 0;
                    n2 = (b13 ? 1 : 0);
                    n3 = n4;
                }
            }
            else {
                n2 = 0;
                n3 = n;
            }
            final boolean b14 = b3 && friend != null;
            final boolean instance = ChatFragment.class.isInstance(this);
            boolean instance2;
            if (b3) {
                instance2 = UserProfileFragment.class.isInstance(this);
            }
            else {
                instance2 = (b4 && GroupProfileFragment.class.isInstance(this));
            }
            final boolean b15 = b3 && b && activeAgentCircuit.getModules().rlvController.canTeleportToLocation();
            boolean b17;
            final boolean b16 = b17 = false;
            if (b3) {
                b17 = b16;
                if (b) {
                    b17 = b16;
                    if (b15) {
                        b17 = (activeAgentCircuit.getModules().minimap.getNearbyAgentLocation(this.chatterID.getOptionalChatterUUID()) != null);
                        if (friend != null) {
                            b17 |= ((friend.getRightsHas() & 0x2) != 0x0);
                        }
                    }
                }
            }
            boolean b18 = false;
            Label_0705: {
                if (b4) {
                    if (b) {
                        final AvatarGroupList avatarGroupList = userManager.getChatterList().getGroupManager().getAvatarGroupList();
                        if (avatarGroupList != null) {
                            final AvatarGroupList.AvatarGroupEntry avatarGroupEntry = avatarGroupList.Groups.get(this.chatterID.getOptionalChatterUUID());
                            if (avatarGroupEntry != null && (avatarGroupEntry.GroupPowers & 0x40000000000L) != 0x0L) {
                                b18 = true;
                                break Label_0705;
                            }
                        }
                    }
                    b18 = false;
                }
                else {
                    b18 = false;
                }
            }
            for (final int n5 : array) {
                final MenuItem item = menu.findItem(n5);
                if (item != null) {
                    switch (n5) {
                        case 2131755860: {
                            item.setVisible((b3 || b4) && (instance ^ true));
                            break;
                        }
                        case 2131755861: {
                            boolean visible2;
                            if (b3 || b4) {
                                visible2 = (instance2 ^ true);
                            }
                            else {
                                visible2 = false;
                            }
                            item.setVisible(visible2);
                            break;
                        }
                        case 2131755863: {
                            item.setVisible(b2 && b5);
                            break;
                        }
                        case 2131755864: {
                            item.setVisible(b2 && b5 && b6);
                            break;
                        }
                        case 2131755865: {
                            item.setVisible(b4 && b && b18);
                            break;
                        }
                        case 2131755866: {
                            item.setVisible(b3 && b);
                            break;
                        }
                        case 2131755867: {
                            item.setVisible(b3 && b);
                            break;
                        }
                        case 2131755868: {
                            item.setVisible(b3 && b && b15 && b17);
                            break;
                        }
                        case 2131755869: {
                            item.setVisible(b3 && b);
                            break;
                        }
                        case 2131755870: {
                            item.setVisible(b3 && b);
                            break;
                        }
                        case 2131755871: {
                            item.setVisible(b3 && b && (b14 ^ true));
                            break;
                        }
                        case 2131755872: {
                            item.setVisible(b3 && b && b14);
                            break;
                        }
                        case 2131755779: {
                            item.setVisible((b3 || b4) && instance && b12);
                            break;
                        }
                        case 2131755846: {
                            int visible3;
                            if ((b3 || b4) && instance && b12) {
                                int n6;
                                if (n2 != 0) {
                                    n6 = n3;
                                }
                                else {
                                    n6 = 0;
                                }
                                visible3 = (n6 ^ 0x1);
                            }
                            else {
                                visible3 = 0;
                            }
                            item.setVisible((boolean)(visible3 != 0));
                            break;
                        }
                        case 2131755781: {
                            int visible4;
                            if ((b3 || b4) && n2 != 0) {
                                visible4 = (n3 ^ 0x1);
                            }
                            else {
                                visible4 = 0;
                            }
                            item.setVisible((boolean)(visible4 != 0));
                            break;
                        }
                        case 2131755874: {
                            item.setVisible((b3 || b4) && n3 != 0 && b);
                            break;
                        }
                        case 2131755862: {
                            item.setVisible(b && b11);
                            break;
                        }
                        case 2131755873: {
                            item.setVisible(visible);
                            break;
                        }
                    }
                }
            }
        }
        else {
            for (int j = 0; j < array.length; ++j) {
                final MenuItem item2 = menu.findItem(array[j]);
                if (item2 != null) {
                    item2.setVisible(false);
                }
            }
        }
    }
    
    @Override
    public void onStart() {
        UserManager userManager = null;
        super.onStart();
        if (this.chatterID != null) {
            userManager = this.chatterID.getUserManager();
        }
        if (userManager != null) {
            this.voiceLoggedIn.subscribe(userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        }
        else {
            this.voiceLoggedIn.unsubscribe();
            this.currentLocationInfo.unsubscribe();
        }
        EventBus.getInstance().subscribe(this);
    }
    
    @Override
    public void onStop() {
        this.voiceLoggedIn.unsubscribe();
        this.currentLocationInfo.unsubscribe();
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }
    
    @CallSuper
    protected void onVoiceLoginStatusChanged(final Boolean b) {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }
}
