package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.AvatarPickerForInvite;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class GroupMainProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AgentDataUpdate> agentDataUpdate = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.12
        private final /* synthetic */ void $m$0(Object obj) {
            ((GroupMainProfileTab) this).m447xa875b11a((SLAgentCircuit) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupProfile, this.myGroupList, this.agentDataUpdate, this.groupRoles).withDataChangedListener(this).withOptionalLoadables(this.groupTitles);
    private ChatterNameRetriever founderNameRetriever = null;
    private final ChatterNameRetriever.OnChatterNameUpdated onFounderNameReady = new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.13
        private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
            ((GroupMainProfileTab) this).m458xd3b61247(chatterNameRetriever);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
        public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
            $m$0(chatterNameRetriever);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onActiveGroupCheckboxClicked, reason: merged with bridge method [inline-methods] */
    public void m450xa875b11d(View view) {
        try {
            if (((CheckBox) view).isChecked()) {
                this.agentCircuit.get().getModules().groupManager.ActivateGroup(this.groupProfile.get().GroupData_Field.GroupID);
            } else {
                this.agentCircuit.get().getModules().groupManager.ActivateGroup(UUIDPool.ZeroUUID);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAgentCircuit, reason: merged with bridge method [inline-methods] */
    public void m447xa875b11a(SLAgentCircuit sLAgentCircuit) {
        View view = getView();
        if (view != null) {
            for (int i : new int[]{R.id.show_in_profile_checkbox, R.id.active_group_checkbox, R.id.group_profile_contribution_button, R.id.group_join_button, R.id.group_leave_button, R.id.group_invite_button, R.id.group_change_role_button}) {
                view.findViewById(i).setEnabled(this.agentCircuit.hasData());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onChangeRoleClicked, reason: merged with bridge method [inline-methods] */
    public void m452xa875b11f(View view) {
        int i = 0;
        try {
            this.agentCircuit.assertHasData();
            final UUID uuid = this.groupProfile.get().GroupData_Field.GroupID;
            CharSequence[] charSequenceArr = new CharSequence[this.groupTitles.get().GroupData_Fields.size()];
            int i2 = 0;
            while (i < this.groupTitles.get().GroupData_Fields.size()) {
                charSequenceArr[i] = SLMessage.stringFromVariableOEM(this.groupTitles.get().GroupData_Fields.get(i).Title);
                int i3 = this.groupTitles.get().GroupData_Fields.get(i).Selected ? i : i2;
                i++;
                i2 = i3;
            }
            final AtomicInteger atomicInteger = new AtomicInteger(i2);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.select_group_role_title).setSingleChoiceItems(charSequenceArr, i2, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i4) {
                    ((AtomicInteger) atomicInteger).set(i4);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    $m$0(dialogInterface, i4);
                }
            }).setPositiveButton(R.string.set_role_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.17
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i4) {
                    ((GroupMainProfileTab) this).m457xd3a7efa5((AtomicInteger) atomicInteger, (UUID) uuid, dialogInterface, i4);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    $m$0(dialogInterface, i4);
                }
            });
            builder.create().show();
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onContributeLandClicked, reason: merged with bridge method [inline-methods] */
    public void m453xa875b120(View view) {
        try {
            this.agentCircuit.assertHasData();
            AvatarGroupList.AvatarGroupEntry avatarGroupEntry = this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
            if (avatarGroupEntry != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.set_land_contribution_title);
                final View inflate = LayoutInflater.from(getContext()).inflate(R.layout.contribute_land_dialog_box_layout, (ViewGroup) getView(), false);
                builder.setView(inflate);
                ((EditText) inflate.findViewById(R.id.land_contribution_value)).setText(getString(R.string.generic_int_format, Integer.valueOf(avatarGroupEntry.Contribution)));
                builder.setPositiveButton(R.string.set_land_contribution_ok_button, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.14
                    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                        ((GroupMainProfileTab) this).m461xf19a4b2((View) inflate, dialogInterface, i);
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        $m$0(dialogInterface, i);
                    }
                });
                builder.create().show();
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onCopyGroupKeyClicked, reason: merged with bridge method [inline-methods] */
    public void m449xa875b11c(View view) {
        if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
            String uuid = ((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID().toString();
            if (Build.VERSION.SDK_INT < 11) {
                ((ClipboardManager) getActivity().getSystemService("clipboard")).setText(uuid);
            } else {
                ((android.content.ClipboardManager) getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Group key", uuid));
            }
            Toast.makeText(getActivity(), "Group key copied to clipboard", 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onInviteClicked, reason: merged with bridge method [inline-methods] */
    public void m456xa875b123(View view) {
        UUID uuid;
        AvatarGroupList.AvatarGroupEntry avatarGroupEntry;
        try {
            if (this.chatterID == null || !this.agentCircuit.hasData() || (avatarGroupEntry = this.myGroupList.get().Groups.get((uuid = this.groupProfile.get().GroupData_Field.GroupID))) == null || (avatarGroupEntry.GroupPowers & 2) == 0) {
                return;
            }
            DetailsActivity.showEmbeddedDetails(getActivity(), AvatarPickerForInvite.class, AvatarPickerForInvite.makeArguments(this.chatterID.agentUUID, uuid, this.groupProfile.get(), this.myGroupList.get(), this.groupTitles.get(), this.groupRoles.get()));
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onJoinClicked, reason: merged with bridge method [inline-methods] */
    public void m454xa875b121(View view) {
        try {
            this.agentCircuit.assertHasData();
            final UUID uuid = this.groupProfile.get().GroupData_Field.GroupID;
            if (this.myGroupList.get().Groups.get(uuid) == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.join_group_dialog_title);
                if (this.groupProfile.get().GroupData_Field.MembershipFee == 0) {
                    builder.setMessage(R.string.join_group_question_message_free);
                } else {
                    builder.setMessage(getString(R.string.join_group_question_message_not_free, Integer.valueOf(this.groupProfile.get().GroupData_Field.MembershipFee)));
                }
                builder.setPositiveButton(R.string.yes_join_button, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.15
                    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                        ((GroupMainProfileTab) this).m460xf191a6b((UUID) uuid, dialogInterface, i);
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        $m$0(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms
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
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onLeaveClicked, reason: merged with bridge method [inline-methods] */
    public void m455xa875b122(View view) {
        try {
            this.agentCircuit.assertHasData();
            final UUID uuid = this.groupProfile.get().GroupData_Field.GroupID;
            if (this.myGroupList.get().Groups.get(uuid) != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.leave_group_confirm);
                builder.setPositiveButton(R.string.yes_leave_button, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.16
                    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                        ((GroupMainProfileTab) this).m459xf184433((UUID) uuid, dialogInterface, i);
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        $m$0(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.1
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
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onShowInProfileCheckboxClicked, reason: merged with bridge method [inline-methods] */
    public void m451xa875b11e(View view) {
        try {
            AvatarGroupList.AvatarGroupEntry avatarGroupEntry = this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
            if (avatarGroupEntry != null) {
                this.agentCircuit.get().getModules().groupManager.SetGroupOptions(this.groupProfile.get().GroupData_Field.GroupID, avatarGroupEntry.AcceptNotices, ((CheckBox) view).isChecked());
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onViewProfileClicked, reason: merged with bridge method [inline-methods] */
    public void m448xa875b11b(View view) {
        UUID uuid;
        GroupProfileReply data = this.groupProfile.getData();
        if (data == null || this.chatterID == null || (uuid = data.GroupData_Field.FounderID) == null || !(!uuid.equals(UUIDPool.ZeroUUID)) || this.chatterID == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.chatterID.agentUUID, uuid)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_11438, reason: not valid java name */
    public /* synthetic */ void m457xd3a7efa5(AtomicInteger atomicInteger, UUID uuid, DialogInterface dialogInterface, int i) {
        int i2 = atomicInteger.get();
        if (i2 >= 0) {
            try {
                if (i2 < this.groupTitles.get().GroupData_Fields.size()) {
                    this.agentCircuit.get().getModules().groupManager.SetGroupRole(uuid, this.groupTitles.get().GroupData_Fields.get(i2).RoleID);
                }
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_21725, reason: not valid java name */
    public /* synthetic */ void m458xd3b61247(ChatterNameRetriever chatterNameRetriever) {
        View view = getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.group_founder_name)).setText(chatterNameRetriever.getResolvedName());
            ((ChatterPicView) view.findViewById(R.id.group_founder_pic)).setChatterID(chatterNameRetriever.chatterID, chatterNameRetriever.getResolvedName());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_6577, reason: not valid java name */
    public /* synthetic */ void m459xf184433(UUID uuid, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.RequestLeaveGroup(uuid);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_8098, reason: not valid java name */
    public /* synthetic */ void m460xf191a6b(UUID uuid, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.RequestJoinGroup(uuid);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_9645, reason: not valid java name */
    public /* synthetic */ void m461xf19a4b2(View view, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.SetGroupContribution(this.groupProfile.get().GroupData_Field.GroupID, Integer.parseInt(((EditText) view.findViewById(R.id.land_contribution_value)).getText().toString()));
        } catch (Exception e) {
            Debug.Warning(e);
        }
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_profile_tab_main, viewGroup, false);
        ((ImageAssetView) inflate.findViewById(R.id.group_pic_view)).setAlignTop(true);
        ((ImageAssetView) inflate.findViewById(R.id.group_pic_view)).setVerticalFit(true);
        inflate.findViewById(R.id.group_profile_view_founder_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.3
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m448xa875b11b(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.button_copy_group_key).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.4
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m449xa875b11c(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.active_group_checkbox).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.5
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m450xa875b11d(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.show_in_profile_checkbox).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.6
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m451xa875b11e(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.group_change_role_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.7
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m452xa875b11f(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.group_profile_contribution_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.8
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m453xa875b120(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.group_join_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.9
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m454xa875b121(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.group_leave_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.10
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m455xa875b122(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        inflate.findViewById(R.id.group_invite_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms.11
            private final /* synthetic */ void $m$0(View view) {
                ((GroupMainProfileTab) this).m456xa875b123(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        View view = getView();
        try {
            AvatarGroupList.AvatarGroupEntry avatarGroupEntry = this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
            if (avatarGroupEntry != null && Strings.isNullOrEmpty(avatarGroupEntry.GroupTitle) && !this.groupTitles.isSubscribed()) {
                this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupProfile.get().GroupData_Field.GroupID);
            }
            if (view != null) {
                ((ImageAssetView) view.findViewById(R.id.group_pic_view)).setAssetID(this.groupProfile.get().GroupData_Field.InsigniaID);
                String stringFromVariableUTF = SLMessage.stringFromVariableUTF(this.groupProfile.get().GroupData_Field.Charter);
                view.findViewById(R.id.group_profile_charter_card_view).setVisibility(Strings.isNullOrEmpty(stringFromVariableUTF) ? 8 : 0);
                ((TextView) view.findViewById(R.id.group_profile_charter_text)).setText(stringFromVariableUTF);
                ((TextView) view.findViewById(R.id.text_profile_member_count)).setText(getString(R.string.group_member_count_format, Integer.valueOf(this.groupProfile.get().GroupData_Field.GroupMembershipCount)));
                ((TextView) view.findViewById(R.id.text_profile_join_fee)).setText(this.groupProfile.get().GroupData_Field.MembershipFee == 0 ? getString(R.string.group_free_to_join) : getString(R.string.group_join_fee_format, Integer.valueOf(this.groupProfile.get().GroupData_Field.MembershipFee)));
                ((TextView) view.findViewById(R.id.text_profile_open_enrollment)).setText(this.groupProfile.get().GroupData_Field.OpenEnrollment ? getString(R.string.group_open_enrollment) : getString(R.string.group_closed_enrollment));
                ((TextView) view.findViewById(R.id.text_profile_show_in_search)).setText(this.groupProfile.get().GroupData_Field.AllowPublish ? this.groupProfile.get().GroupData_Field.MaturePublish ? getString(R.string.group_visible_mature) : getString(R.string.group_visible_public) : getString(R.string.group_visible_none));
                if (this.founderNameRetriever != null) {
                    this.founderNameRetriever.dispose();
                    this.founderNameRetriever = null;
                }
                UUID uuid = this.groupProfile.get().GroupData_Field.FounderID;
                Debug.Printf("GroupProfile: founderID = %s", uuid);
                if (uuid == null || !(!uuid.equals(UUIDPool.ZeroUUID)) || this.chatterID == null) {
                    view.findViewById(R.id.group_founder_card_view).setVisibility(8);
                    ((ChatterPicView) view.findViewById(R.id.group_founder_pic)).setChatterID(null, null);
                } else {
                    ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.chatterID.agentUUID, uuid);
                    view.findViewById(R.id.group_founder_card_view).setVisibility(0);
                    this.founderNameRetriever = new ChatterNameRetriever(userChatterID, this.onFounderNameReady, UIThreadExecutor.getInstance());
                }
                if (avatarGroupEntry == null) {
                    ((TextView) view.findViewById(R.id.group_membership_caption)).setText(R.string.membership_no_member_caption);
                    view.findViewById(R.id.group_join_button).setVisibility(this.groupProfile.get().GroupData_Field.OpenEnrollment ? 0 : 8);
                    view.findViewById(R.id.group_leave_button).setVisibility(8);
                    view.findViewById(R.id.group_invite_button).setVisibility(8);
                    view.findViewById(R.id.group_change_role_button).setVisibility(8);
                    view.findViewById(R.id.group_membership_role).setVisibility(8);
                    view.findViewById(R.id.membership_settings_card_view).setVisibility(8);
                    return;
                }
                ((TextView) view.findViewById(R.id.group_membership_caption)).setText(R.string.membership_caption);
                view.findViewById(R.id.group_join_button).setVisibility(8);
                view.findViewById(R.id.group_leave_button).setVisibility(0);
                view.findViewById(R.id.group_invite_button).setVisibility(((avatarGroupEntry.GroupPowers & 2) > 0L ? 1 : ((avatarGroupEntry.GroupPowers & 2) == 0L ? 0 : -1)) != 0 ? 0 : 8);
                view.findViewById(R.id.group_change_role_button).setVisibility(0);
                view.findViewById(R.id.group_membership_role).setVisibility(0);
                String str = avatarGroupEntry.GroupTitle;
                if (Strings.isNullOrEmpty(str) && this.groupTitles.hasData()) {
                    Iterator<T> it = this.groupTitles.get().GroupData_Fields.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        GroupTitlesReply.GroupData groupData = (GroupTitlesReply.GroupData) it.next();
                        if (groupData.Selected) {
                            str = SLMessage.stringFromVariableOEM(groupData.Title);
                            break;
                        }
                    }
                }
                ((TextView) view.findViewById(R.id.group_membership_role)).setText(str);
                view.findViewById(R.id.membership_settings_card_view).setVisibility(0);
                ((TextView) view.findViewById(R.id.land_contribution_text)).setText(avatarGroupEntry.Contribution != 0 ? getString(R.string.format_land_contribution, Integer.valueOf(avatarGroupEntry.Contribution)) : getString(R.string.no_land_contributed));
                ((CheckBox) view.findViewById(R.id.show_in_profile_checkbox)).setChecked(avatarGroupEntry.ListInProfile);
                ((CheckBox) view.findViewById(R.id.active_group_checkbox)).setChecked(this.agentDataUpdate.get().AgentData_Field.ActiveGroupID.equals(this.groupProfile.get().GroupData_Field.GroupID));
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        View view = getView();
        this.loadableMonitor.unsubscribeAll();
        this.agentCircuit.unsubscribe();
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDGroup)) {
            if (view != null) {
                ((TextView) view.findViewById(R.id.text_profile_group_key)).setText("");
                return;
            }
            return;
        }
        UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
        if (view != null) {
            ((TextView) view.findViewById(R.id.text_profile_group_key)).setText(chatterUUID.toString());
        }
        this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
        this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
        this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        this.agentDataUpdate.subscribe(this.userManager.getAgentDataUpdates().getPool(), chatterID.agentUUID);
        this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
    }
}
