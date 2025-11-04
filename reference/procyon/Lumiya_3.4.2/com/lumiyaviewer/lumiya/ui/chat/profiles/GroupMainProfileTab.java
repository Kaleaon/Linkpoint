// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.google.common.base.Strings;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import android.os.Bundle;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.chat.AvatarPickerForInvite;
import android.widget.Toast;
import android.content.ClipData;
import android.text.ClipboardManager;
import android.os.Build$VERSION;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.EditText;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.widget.CheckBox;
import android.view.View;
import android.content.DialogInterface;
import java.util.concurrent.atomic.AtomicInteger;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class GroupMainProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final SubscriptionData<UUID, AgentDataUpdate> agentDataUpdate;
    private ChatterNameRetriever founderNameRetriever;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile;
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles;
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    private final OnChatterNameUpdated onFounderNameReady;
    
    public GroupMainProfileTab() {
        this.groupProfile = new SubscriptionData<UUID, GroupProfileReply>(UIThreadExecutor.getInstance());
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.groupTitles = new SubscriptionData<UUID, GroupTitlesReply>(UIThreadExecutor.getInstance());
        this.agentDataUpdate = new SubscriptionData<UUID, AgentDataUpdate>(UIThreadExecutor.getInstance());
        this.groupRoles = new SubscriptionData<UUID, GroupRoleDataReply>(UIThreadExecutor.getInstance());
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$12(this));
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.groupProfile, this.myGroupList, this.agentDataUpdate, this.groupRoles }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this).withOptionalLoadables(this.groupTitles);
        this.founderNameRetriever = null;
        this.onFounderNameReady = new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$13(this);
    }
    
    private void onActiveGroupCheckboxClicked(final View view) {
        try {
            if (((CheckBox)view).isChecked()) {
                this.agentCircuit.get().getModules().groupManager.ActivateGroup(this.groupProfile.get().GroupData_Field.GroupID);
            }
            else {
                this.agentCircuit.get().getModules().groupManager.ActivateGroup(UUIDPool.ZeroUUID);
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        final View view = this.getView();
        if (view != null) {
            final int[] array2;
            final int[] array = array2 = new int[7];
            array2[0] = 2131755410;
            array2[1] = 2131755411;
            array2[2] = 2131755412;
            array2[3] = 2131755404;
            array2[4] = 2131755405;
            array2[5] = 2131755406;
            array2[6] = 2131755407;
            for (int i = 0; i < array.length; ++i) {
                view.findViewById(array[i]).setEnabled(this.agentCircuit.hasData());
            }
        }
    }
    
    private void onChangeRoleClicked(final View view) {
        while (true) {
            int i = 0;
            while (true) {
                Label_0232: {
                    try {
                        this.agentCircuit.assertHasData();
                        final UUID groupID = this.groupProfile.get().GroupData_Field.GroupID;
                        final CharSequence[] array = new CharSequence[this.groupTitles.get().GroupData_Fields.size()];
                        int initialValue = 0;
                        while (i < this.groupTitles.get().GroupData_Fields.size()) {
                            array[i] = SLMessage.stringFromVariableOEM(((GroupTitlesReply.GroupData)this.groupTitles.get().GroupData_Fields.get(i)).Title);
                            if (!((GroupTitlesReply.GroupData)this.groupTitles.get().GroupData_Fields.get(i)).Selected) {
                                break Label_0232;
                            }
                            initialValue = i;
                            ++i;
                        }
                        final AtomicInteger atomicInteger = new AtomicInteger(initialValue);
                        final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                        builder.setTitle(2131297010).setSingleChoiceItems(array, initialValue, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$2(atomicInteger)).setPositiveButton(2131297025, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$17(this, atomicInteger, groupID));
                        builder.create().show();
                        return;
                    }
                    catch (final SubscriptionData.DataNotReadyException ex) {
                        Debug.Warning(ex);
                        return;
                    }
                }
                continue;
            }
        }
    }
    
    private void onContributeLandClicked(final View view) {
        try {
            this.agentCircuit.assertHasData();
            final AvatarGroupList.AvatarGroupEntry avatarGroupEntry = this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
            if (avatarGroupEntry != null) {
                final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                builder.setTitle(2131297023);
                final View inflate = LayoutInflater.from(this.getContext()).inflate(2130968623, (ViewGroup)this.getView(), false);
                builder.setView(inflate);
                ((EditText)inflate.findViewById(2131755339)).setText((CharSequence)this.getString(2131296558, avatarGroupEntry.Contribution));
                builder.setPositiveButton(2131297022, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$14(this, inflate));
                builder.create().show();
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onCopyGroupKeyClicked(final View view) {
        if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
            final String string = ((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID().toString();
            if (Build$VERSION.SDK_INT < 11) {
                ((ClipboardManager)this.getActivity().getSystemService("clipboard")).setText((CharSequence)string);
            }
            else {
                ((android.content.ClipboardManager)this.getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText((CharSequence)"Group key", (CharSequence)string));
            }
            Toast.makeText((Context)this.getActivity(), (CharSequence)"Group key copied to clipboard", 0).show();
        }
    }
    
    private void onInviteClicked(final View view) {
        try {
            if (this.chatterID != null && this.agentCircuit.hasData()) {
                final UUID groupID = this.groupProfile.get().GroupData_Field.GroupID;
                final AvatarGroupList.AvatarGroupEntry avatarGroupEntry = this.myGroupList.get().Groups.get(groupID);
                if (avatarGroupEntry != null && (avatarGroupEntry.GroupPowers & 0x2L) != 0x0L) {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), AvatarPickerForInvite.class, AvatarPickerForInvite.makeArguments(this.chatterID.agentUUID, groupID, this.groupProfile.get(), this.myGroupList.get(), this.groupTitles.get(), this.groupRoles.get()));
                }
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onJoinClicked(final View view) {
        try {
            this.agentCircuit.assertHasData();
            final UUID groupID = this.groupProfile.get().GroupData_Field.GroupID;
            if (this.myGroupList.get().Groups.get(groupID) == null) {
                final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                builder.setTitle(2131296644);
                if (this.groupProfile.get().GroupData_Field.MembershipFee == 0) {
                    builder.setMessage(2131296650);
                }
                else {
                    builder.setMessage(this.getString(2131296651, this.groupProfile.get().GroupData_Field.MembershipFee));
                }
                builder.setPositiveButton(2131297166, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$15(this, groupID));
                builder.setNegativeButton(2131296424, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms());
                builder.create().show();
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onLeaveClicked(final View view) {
        try {
            this.agentCircuit.assertHasData();
            final UUID groupID = this.groupProfile.get().GroupData_Field.GroupID;
            if (this.myGroupList.get().Groups.get(groupID) != null) {
                final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                builder.setTitle(2131296655);
                builder.setPositiveButton(2131297167, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$16(this, groupID));
                builder.setNegativeButton(2131296424, (DialogInterface$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$1());
                builder.create().show();
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onShowInProfileCheckboxClicked(final View view) {
        try {
            final AvatarGroupList.AvatarGroupEntry avatarGroupEntry = this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
            if (avatarGroupEntry != null) {
                this.agentCircuit.get().getModules().groupManager.SetGroupOptions(this.groupProfile.get().GroupData_Field.GroupID, avatarGroupEntry.AcceptNotices, ((CheckBox)view).isChecked());
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onViewProfileClicked(final View view) {
        final GroupProfileReply groupProfileReply = this.groupProfile.getData();
        if (groupProfileReply != null && this.chatterID != null) {
            final UUID founderID = groupProfileReply.GroupData_Field.FounderID;
            if (founderID != null && (founderID.equals(UUIDPool.ZeroUUID) ^ true) && this.chatterID != null) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.chatterID.agentUUID, founderID)));
            }
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968649, viewGroup, false);
        ((ImageAssetView)inflate.findViewById(2131755395)).setAlignTop(true);
        ((ImageAssetView)inflate.findViewById(2131755395)).setVerticalFit(true);
        inflate.findViewById(2131755416).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$3(this));
        inflate.findViewById(2131755421).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$4(this));
        inflate.findViewById(2131755411).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$5(this));
        inflate.findViewById(2131755410).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$6(this));
        inflate.findViewById(2131755407).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$7(this));
        inflate.findViewById(2131755412).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$8(this));
        inflate.findViewById(2131755404).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$9(this));
        inflate.findViewById(2131755405).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$10(this));
        inflate.findViewById(2131755406).setOnClickListener((View$OnClickListener)new _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$11(this));
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296593));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        int n;
        View view;
        AvatarGroupList.AvatarGroupEntry avatarGroupEntry;
        String stringFromVariableUTF;
        View viewById;
        int visibility;
        TextView textView;
        String text;
        TextView textView2;
        String text2;
        String text3;
        UUID founderID;
        ChatterID.ChatterIDUser userChatterID;
        int n2;
        View viewById2;
        int visibility2;
        String text4;
        String s;
        Iterator<Object> iterator;
        GroupTitlesReply.GroupData groupData;
        TextView textView3;
        String text5;
        View viewById3;
        int visibility3;
        Label_0599_Outer:Label_0791_Outer:
        while (true) {
            n = 0;
            view = this.getView();
            Label_1007: {
                while (true) {
                Label_0995:
                    while (true) {
                    Label_0988:
                        while (true) {
                            Label_0982: {
                                try {
                                    avatarGroupEntry = (AvatarGroupList.AvatarGroupEntry)this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
                                    if (avatarGroupEntry != null && Strings.isNullOrEmpty(avatarGroupEntry.GroupTitle) && !this.groupTitles.isSubscribed()) {
                                        this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupProfile.get().GroupData_Field.GroupID);
                                    }
                                    if (view != null) {
                                        ((ImageAssetView)view.findViewById(2131755395)).setAssetID(this.groupProfile.get().GroupData_Field.InsigniaID);
                                        stringFromVariableUTF = SLMessage.stringFromVariableUTF(this.groupProfile.get().GroupData_Field.Charter);
                                        viewById = view.findViewById(2131755417);
                                        if (Strings.isNullOrEmpty(stringFromVariableUTF)) {
                                            visibility = 8;
                                        }
                                        else {
                                            visibility = 0;
                                        }
                                        viewById.setVisibility(visibility);
                                        ((TextView)view.findViewById(2131755418)).setText((CharSequence)stringFromVariableUTF);
                                        ((TextView)view.findViewById(2131755397)).setText((CharSequence)this.getString(2131296578, this.groupProfile.get().GroupData_Field.GroupMembershipCount));
                                        textView = (TextView)view.findViewById(2131755398);
                                        if (this.groupProfile.get().GroupData_Field.MembershipFee == 0) {
                                            text = this.getString(2131296572);
                                        }
                                        else {
                                            text = this.getString(2131296575, this.groupProfile.get().GroupData_Field.MembershipFee);
                                        }
                                        textView.setText((CharSequence)text);
                                        textView2 = (TextView)view.findViewById(2131755399);
                                        if (this.groupProfile.get().GroupData_Field.OpenEnrollment) {
                                            text2 = this.getString(2131296590);
                                        }
                                        else {
                                            text2 = this.getString(2131296571);
                                        }
                                        textView2.setText((CharSequence)text2);
                                        if (this.groupProfile.get().GroupData_Field.AllowPublish) {
                                            if (this.groupProfile.get().GroupData_Field.MaturePublish) {
                                                text3 = this.getString(2131296597);
                                            }
                                            else {
                                                text3 = this.getString(2131296599);
                                            }
                                        }
                                        else {
                                            text3 = this.getString(2131296598);
                                        }
                                        ((TextView)view.findViewById(2131755400)).setText((CharSequence)text3);
                                        if (this.founderNameRetriever != null) {
                                            this.founderNameRetriever.dispose();
                                            this.founderNameRetriever = null;
                                        }
                                        founderID = this.groupProfile.get().GroupData_Field.FounderID;
                                        Debug.Printf("GroupProfile: founderID = %s", founderID);
                                        if (founderID != null && (founderID.equals(UUIDPool.ZeroUUID) ^ true) && this.chatterID != null) {
                                            userChatterID = ChatterID.getUserChatterID(this.chatterID.agentUUID, founderID);
                                            view.findViewById(2131755413).setVisibility(0);
                                            this.founderNameRetriever = new ChatterNameRetriever(userChatterID, this.onFounderNameReady, UIThreadExecutor.getInstance());
                                        }
                                        else {
                                            view.findViewById(2131755413).setVisibility(8);
                                            ((ChatterPicView)view.findViewById(2131755415)).setChatterID(null, null);
                                        }
                                        if (avatarGroupEntry == null) {
                                            break Label_1007;
                                        }
                                        ((TextView)view.findViewById(2131755402)).setText(2131296677);
                                        view.findViewById(2131755404).setVisibility(8);
                                        view.findViewById(2131755405).setVisibility(0);
                                        if ((avatarGroupEntry.GroupPowers & 0x2L) == 0x0L) {
                                            break Label_0982;
                                        }
                                        n2 = 1;
                                        viewById2 = view.findViewById(2131755406);
                                        if (n2 == 0) {
                                            break Label_0988;
                                        }
                                        visibility2 = n;
                                        viewById2.setVisibility(visibility2);
                                        view.findViewById(2131755407).setVisibility(0);
                                        view.findViewById(2131755403).setVisibility(0);
                                        s = (text4 = avatarGroupEntry.GroupTitle);
                                        Label_0723: {
                                            if (Strings.isNullOrEmpty(s)) {
                                                text4 = s;
                                                if (this.groupTitles.hasData()) {
                                                    iterator = this.groupTitles.get().GroupData_Fields.iterator();
                                                    do {
                                                        text4 = s;
                                                        if (!iterator.hasNext()) {
                                                            break Label_0723;
                                                        }
                                                        groupData = iterator.next();
                                                    } while (!groupData.Selected);
                                                    text4 = SLMessage.stringFromVariableOEM(groupData.Title);
                                                }
                                            }
                                        }
                                        ((TextView)view.findViewById(2131755403)).setText((CharSequence)text4);
                                        view.findViewById(2131755408).setVisibility(0);
                                        textView3 = (TextView)view.findViewById(2131755409);
                                        if (avatarGroupEntry.Contribution == 0) {
                                            break Label_0995;
                                        }
                                        text5 = this.getString(2131296547, avatarGroupEntry.Contribution);
                                        textView3.setText((CharSequence)text5);
                                        ((CheckBox)view.findViewById(2131755410)).setChecked(avatarGroupEntry.ListInProfile);
                                        ((CheckBox)view.findViewById(2131755411)).setChecked(this.agentDataUpdate.get().AgentData_Field.ActiveGroupID.equals(this.groupProfile.get().GroupData_Field.GroupID));
                                    }
                                    return;
                                }
                                catch (final SubscriptionData.DataNotReadyException ex) {
                                    Debug.Warning(ex);
                                    return;
                                }
                            }
                            n2 = 0;
                            continue Label_0599_Outer;
                        }
                        visibility2 = 8;
                        continue Label_0791_Outer;
                    }
                    text5 = this.getString(2131296745);
                    continue;
                }
            }
            ((TextView)view.findViewById(2131755402)).setText(2131296682);
            viewById3 = view.findViewById(2131755404);
            if (this.groupProfile.get().GroupData_Field.OpenEnrollment) {
                visibility3 = 0;
            }
            else {
                visibility3 = 8;
            }
            viewById3.setVisibility(visibility3);
            view.findViewById(2131755405).setVisibility(8);
            view.findViewById(2131755406).setVisibility(8);
            view.findViewById(2131755407).setVisibility(8);
            view.findViewById(2131755403).setVisibility(8);
            view.findViewById(2131755408).setVisibility(8);
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        final View view = this.getView();
        this.loadableMonitor.unsubscribeAll();
        this.agentCircuit.unsubscribe();
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            if (view != null) {
                ((TextView)view.findViewById(2131755420)).setText((CharSequence)chatterUUID.toString());
            }
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.agentDataUpdate.subscribe(this.userManager.getAgentDataUpdates().getPool(), chatterID.agentUUID);
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
        }
        else if (view != null) {
            ((TextView)view.findViewById(2131755420)).setText((CharSequence)"");
        }
    }
}
