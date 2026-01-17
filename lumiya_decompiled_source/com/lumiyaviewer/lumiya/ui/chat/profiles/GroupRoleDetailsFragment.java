package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLGroupInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class GroupRoleDetailsFragment extends ChatterFragment implements LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler {
    private static final String ROLE_ID_KEY = "role_id";
    private static final ImmutableList<RolePermission> rolePermissions = ImmutableList.copyOf(new RolePermission[]{new RolePermission(2, R.string.role_gp_member_invite, null), new RolePermission(4, R.string.role_gp_member_eject, null), new RolePermission(8, R.string.role_gp_member_options, null), new RolePermission(16, R.string.role_gp_role_create, null), new RolePermission(32, R.string.role_gp_role_delete, null), new RolePermission(64, R.string.role_gp_role_properties, null), new RolePermission(128, R.string.role_gp_role_assign_member_limited, null), new RolePermission(256, R.string.role_gp_role_assign_member, null), new RolePermission(512, R.string.role_gp_role_remove_member, null), new RolePermission(1024, R.string.role_gp_role_change_actions, null), new RolePermission(2048, R.string.role_gp_group_change_identity, null), new RolePermission(4096, R.string.role_gp_land_deed, null), new RolePermission(8192, R.string.role_gp_land_release, null), new RolePermission(16384, R.string.role_gp_land_set_sale_info, null), new RolePermission(32768, R.string.role_gp_land_divide_join, null), new RolePermission(131072, R.string.role_gp_land_find_places, null), new RolePermission(262144, R.string.role_gp_land_change_identity, null), new RolePermission(524288, R.string.role_gp_land_set_landing_point, null), new RolePermission(1048576, R.string.role_gp_land_change_media, null), new RolePermission(SLGroupInfo.GP_LAND_EDIT, R.string.role_gp_land_edit, null), new RolePermission(SLGroupInfo.GP_LAND_OPTIONS, R.string.role_gp_land_options, null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_EDIT_LAND, R.string.role_gp_land_allow_edit_land, null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_FLY, R.string.role_gp_land_allow_fly, null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_CREATE, R.string.role_gp_land_allow_create, null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_LANDMARK, R.string.role_gp_land_allow_landmark, null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_SET_HOME, R.string.role_gp_land_allow_set_home, null), new RolePermission(SLGroupInfo.GP_LAND_ALLOW_HOLD_EVENT, R.string.role_gp_land_allow_hold_event, null), new RolePermission(SLGroupInfo.GP_LAND_MANAGE_ALLOWED, R.string.role_gp_land_manage_allowed, null), new RolePermission(SLGroupInfo.GP_LAND_MANAGE_BANNED, R.string.role_gp_land_manage_banned, null), new RolePermission(SLGroupInfo.GP_LAND_MANAGE_PASSES, R.string.role_gp_land_manage_passes, null), new RolePermission(SLGroupInfo.GP_LAND_ADMIN, R.string.role_gp_land_admin, null), new RolePermission(SLGroupInfo.GP_LAND_RETURN_GROUP_SET, R.string.role_gp_land_return_group_set, null), new RolePermission(SLGroupInfo.GP_LAND_RETURN_NON_GROUP, R.string.role_gp_land_return_non_group, null), new RolePermission(SLGroupInfo.GP_LAND_RETURN_GROUP_OWNED, R.string.role_gp_land_return_group_owned, null), new RolePermission(SLGroupInfo.GP_LAND_GARDENING, R.string.role_gp_land_gardening, null), new RolePermission(SLGroupInfo.GP_OBJECT_DEED, R.string.role_gp_object_deed, null), new RolePermission(SLGroupInfo.GP_OBJECT_MANIPULATE, R.string.role_gp_object_manipulate, null), new RolePermission(SLGroupInfo.GP_OBJECT_SET_SALE, R.string.role_gp_object_set_sale, null), new RolePermission(SLGroupInfo.GP_ACCOUNTING_ACCOUNTABLE, R.string.role_gp_accounting_accountable, null), new RolePermission(SLGroupInfo.GP_NOTICES_SEND, R.string.role_gp_notices_send, null), new RolePermission(SLGroupInfo.GP_NOTICES_RECEIVE, R.string.role_gp_notices_receive, null), new RolePermission(SLGroupInfo.GP_PROPOSAL_START, R.string.role_gp_proposal_start, null), new RolePermission(SLGroupInfo.GP_PROPOSAL_VOTE, R.string.role_gp_proposal_vote, null), new RolePermission(65536, R.string.role_gp_session_join, null), new RolePermission(SLGroupInfo.GP_SESSION_VOICE, R.string.role_gp_session_voice, null), new RolePermission(SLGroupInfo.GP_SESSION_MODERATOR, R.string.role_gp_session_moderator, null)});
    @Nullable
    private UUID RoleID;
    private MenuItem deleteMenuItem;
    private MenuItem undoMenuItem;
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupRoles, this.groupProfile, this.myGroupList).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this);
    private boolean hasChanged = false;
    private final TextWatcher textChangedListener = new TextWatcher() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.GroupRoleDetailsFragment.1
        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            GroupRoleDetailsFragment.this.updateUnsavedChanges();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    private final View.OnClickListener permCheckboxClickListener = new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.3
        private final /* synthetic */ void $m$0(View view) {
            ((GroupRoleDetailsFragment) this).m497x4277c5d5(view);
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            $m$0(view);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RolePermission {
        final long permMask;
        final int permName;

        private RolePermission(long j, int i) {
            this.permMask = j;
            this.permName = i;
        }

        /* synthetic */ RolePermission(long j, int i, RolePermission rolePermission) {
            this(j, i);
        }
    }

    private boolean anyChanges() {
        String str;
        String str2;
        long j;
        String str3;
        View view = getView();
        if (view != null) {
            if (this.RoleID == null) {
                str = "";
                str2 = "";
                j = getDefaultPowers();
                str3 = "";
            } else {
                GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
                if (selectedRoleData == null) {
                    return false;
                }
                String stringFromVariableOEM = SLMessage.stringFromVariableOEM(selectedRoleData.Name);
                String stringFromVariableOEM2 = SLMessage.stringFromVariableOEM(selectedRoleData.Title);
                String stringFromVariableOEM3 = SLMessage.stringFromVariableOEM(selectedRoleData.Description);
                str = stringFromVariableOEM;
                str2 = stringFromVariableOEM2;
                j = selectedRoleData.Powers;
                str3 = stringFromVariableOEM3;
            }
            return (Objects.equal(str, ((TextView) view.findViewById(R.id.role_name_edit)).getText().toString()) && Objects.equal(str2, ((TextView) view.findViewById(R.id.role_title_edit)).getText().toString()) && Objects.equal(str3, ((TextView) view.findViewById(R.id.role_description_edit)).getText().toString()) && j == getSelectedPowers(j, (ViewGroup) view.findViewById(R.id.role_permission_list_layout))) ? false : true;
        }
        return false;
    }

    private void askForSavingChanges(final Runnable runnable) {
        View view = getView();
        if (view == null) {
            runnable.run();
            return;
        }
        GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
        long defaultPowers = selectedRoleData == null ? getDefaultPowers() : selectedRoleData.Powers;
        final String charSequence = ((TextView) view.findViewById(R.id.role_name_edit)).getText().toString();
        final String charSequence2 = ((TextView) view.findViewById(R.id.role_title_edit)).getText().toString();
        final String charSequence3 = ((TextView) view.findViewById(R.id.role_description_edit)).getText().toString();
        final long selectedPowers = getSelectedPowers(defaultPowers, (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.save_changes_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.6
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((GroupRoleDetailsFragment) this).m500x4287a0c8((String) charSequence, (String) charSequence2, (String) charSequence3, selectedPowers, (Runnable) runnable, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                GroupRoleDetailsFragment.m496x4288106b((Runnable) runnable, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    private void confirmDeleteRole() {
        if ((getMyGroupPowers() & 32) != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.delete_role_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((GroupRoleDetailsFragment) this).m498x4286a828(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E
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

    private void createPermEntries(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        for (RolePermission rolePermission : rolePermissions) {
            View inflate = layoutInflater.inflate(R.layout.group_role_perm_item, viewGroup, false);
            ((CheckedTextView) inflate).setText(rolePermission.permName);
            inflate.setTag(R.id.perm_checkbox_mask, Long.valueOf(rolePermission.permMask));
            inflate.setEnabled(true);
            inflate.setClickable(true);
            inflate.setOnClickListener(this.permCheckboxClickListener);
            viewGroup.addView(inflate);
        }
    }

    private long getDefaultPowers() {
        return 134283266L;
    }

    private int getMemberCount() {
        GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
        if (selectedRoleData != null) {
            if (selectedRoleData.RoleID.equals(UUIDPool.ZeroUUID)) {
                GroupProfileReply data = this.groupProfile.getData();
                if (data != null) {
                    return data.GroupData_Field.GroupMembershipCount;
                }
                return 0;
            }
            return selectedRoleData.Members;
        }
        return 0;
    }

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
        } catch (SubscriptionData.DataNotReadyException e) {
            return null;
        }
    }

    private long getMyGroupPowers() {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0L;
    }

    private long getSelectedPowers(long j, ViewGroup viewGroup) {
        long j2;
        int childCount = viewGroup.getChildCount();
        int i = 0;
        long j3 = j;
        while (i < childCount) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CheckedTextView) {
                Object tag = childAt.getTag(R.id.perm_checkbox_mask);
                if (tag instanceof Long) {
                    long longValue = ((Long) tag).longValue();
                    j2 = ((CheckedTextView) childAt).isChecked() ? j3 | longValue : (~longValue) & j3;
                    i++;
                    j3 = j2;
                }
            }
            j2 = j3;
            i++;
            j3 = j2;
        }
        return j3;
    }

    @Nullable
    private GroupRoleDataReply.RoleData getSelectedRoleData() {
        if (this.RoleID != null) {
            try {
                for (GroupRoleDataReply.RoleData roleData : this.groupRoles.get().RoleData_Fields) {
                    if (Objects.equal(roleData.RoleID, this.RoleID)) {
                        return roleData;
                    }
                }
                return null;
            } catch (SubscriptionData.DataNotReadyException e) {
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_26404  reason: not valid java name */
    public static /* synthetic */ void m496x4288106b(Runnable runnable, DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        runnable.run();
    }

    public static Bundle makeSelection(ChatterID chatterID, @Nullable UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(ROLE_ID_KEY, uuid.toString());
        }
        return makeSelection;
    }

    private void setLoadedValues() {
        GroupRoleDataReply.RoleData selectedRoleData = getSelectedRoleData();
        long myGroupPowers = getMyGroupPowers();
        boolean z = (64 & myGroupPowers) != 0;
        boolean equal = Objects.equal(this.RoleID, UUIDPool.ZeroUUID);
        GroupProfileReply data = this.groupProfile.getData();
        boolean equal2 = data != null ? Objects.equal(data.GroupData_Field.OwnerRole, this.RoleID) : false;
        if (this.deleteMenuItem != null) {
            this.deleteMenuItem.setVisible((this.RoleID == null || (myGroupPowers & 32) == 0 || !(equal2 ^ true)) ? false : !equal);
        }
        View view = getView();
        if (view != null) {
            if (selectedRoleData != null) {
                int memberCount = getMemberCount();
                ((EditText) view.findViewById(R.id.role_name_edit)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Name));
                ((TextView) view.findViewById(R.id.role_name_view)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Name));
                ((EditText) view.findViewById(R.id.role_title_edit)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Title));
                ((TextView) view.findViewById(R.id.role_title_view)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Title));
                ((EditText) view.findViewById(R.id.role_description_edit)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Description));
                ((TextView) view.findViewById(R.id.role_description_view)).setText(SLMessage.stringFromVariableOEM(selectedRoleData.Description));
                ((TextView) view.findViewById(R.id.role_member_count)).setText(getResources().getQuantityString(R.plurals.members, memberCount, Integer.valueOf(memberCount)));
                setPermissionCheckboxes(selectedRoleData.Powers, (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
                view.findViewById(R.id.group_role_members_card_view).setVisibility(0);
            } else {
                view.findViewById(R.id.group_role_members_card_view).setVisibility(8);
                ((EditText) view.findViewById(R.id.role_name_edit)).setText("");
                ((EditText) view.findViewById(R.id.role_title_edit)).setText("");
                ((EditText) view.findViewById(R.id.role_description_edit)).setText("");
                ((TextView) view.findViewById(R.id.role_name_view)).setText("");
                ((TextView) view.findViewById(R.id.role_title_view)).setText("");
                ((TextView) view.findViewById(R.id.role_description_view)).setText("");
                ((TextView) view.findViewById(R.id.role_member_count)).setText("");
                setPermissionCheckboxes(getDefaultPowers(), (ViewGroup) view.findViewById(R.id.role_permission_list_layout));
            }
            view.findViewById(R.id.role_name_edit).setVisibility(z ? 0 : 8);
            view.findViewById(R.id.role_name_view).setVisibility(!z ? 0 : 8);
            view.findViewById(R.id.role_title_edit).setVisibility(z ? 0 : 8);
            view.findViewById(R.id.role_title_view).setVisibility(!z ? 0 : 8);
            view.findViewById(R.id.role_description_edit).setVisibility(z ? 0 : 8);
            view.findViewById(R.id.role_description_view).setVisibility(z ? 8 : 0);
        }
        updateUnsavedChanges();
    }

    private void setPermissionCheckboxes(long j, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CheckedTextView) {
                Object tag = childAt.getTag(R.id.perm_checkbox_mask);
                if (tag instanceof Long) {
                    ((CheckedTextView) childAt).setChecked((((Long) tag).longValue() & j) != 0);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUnsavedChanges() {
        boolean anyChanges = anyChanges();
        if (anyChanges != this.hasChanged) {
            this.hasChanged = anyChanges;
            if (this.undoMenuItem != null) {
                this.undoMenuItem.setVisible(this.hasChanged);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_11905  reason: not valid java name */
    public /* synthetic */ void m497x4277c5d5(View view) {
        long myGroupPowers = getMyGroupPowers();
        GroupProfileReply data = this.groupProfile.getData();
        if ((myGroupPowers & 1024) != 0 ? !(data != null ? Objects.equal(data.GroupData_Field.OwnerRole, this.RoleID) : false) : false) {
            if (view instanceof Checkable) {
                ((Checkable) view).toggle();
            }
            updateUnsavedChanges();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_23112  reason: not valid java name */
    public /* synthetic */ void m498x4286a828(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            if ((this.chatterID instanceof ChatterID.ChatterIDGroup) && this.RoleID != null) {
                this.agentCircuit.get().getModules().groupManager.DeleteRole(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.RoleID);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_24146  reason: not valid java name */
    public /* synthetic */ void m499x42871ce8() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_25587  reason: not valid java name */
    public /* synthetic */ void m500x4287a0c8(String str, String str2, String str3, long j, Runnable runnable, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
                this.agentCircuit.get().getModules().groupManager.SetRoleProperties(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.RoleID, str, str2, str3, j);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleDetailsFragment_9928  reason: not valid java name */
    public /* synthetic */ void m501x12acc68f(View view) {
        if (this.RoleID != null) {
            if (this.RoleID.equals(UUIDPool.ZeroUUID)) {
                DetailsActivity.showEmbeddedDetails(getActivity(), GroupMembersProfileTab.class, GroupMembersProfileTab.makeSelection(this.chatterID));
            } else {
                DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleMembersFragment.class, GroupRoleMembersFragment.makeSelection(this.chatterID, this.RoleID));
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.BackButtonHandler
    public boolean onBackButtonPressed() {
        if (anyChanges()) {
            askForSavingChanges(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.5
                private final /* synthetic */ void $m$0() {
                    ((GroupRoleDetailsFragment) this).m499x42871ce8();
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
            return true;
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.group_role_edit_menu, menu);
        this.undoMenuItem = menu.findItem(R.id.item_undo);
        this.undoMenuItem.setVisible(this.hasChanged);
        this.deleteMenuItem = menu.findItem(R.id.item_delete_role);
        this.deleteMenuItem.setVisible(false);
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_role_details_layout, viewGroup, false);
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        ((TextView) inflate.findViewById(R.id.role_name_edit)).addTextChangedListener(this.textChangedListener);
        ((TextView) inflate.findViewById(R.id.role_title_edit)).addTextChangedListener(this.textChangedListener);
        ((TextView) inflate.findViewById(R.id.role_description_edit)).addTextChangedListener(this.textChangedListener);
        createPermEntries(layoutInflater, (ViewGroup) inflate.findViewById(R.id.role_permission_list_layout));
        inflate.findViewById(R.id.button_view_role_members).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$oqvWEi5fLgnwnCXV95inckWtW-E.4
            private final /* synthetic */ void $m$0(View view) {
                ((GroupRoleDetailsFragment) this).m501x12acc68f(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        setLoadedValues();
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_undo /* 2131755778 */:
                setLoadedValues();
                return true;
            case R.id.item_delete_role /* 2131755788 */:
                confirmDeleteRole();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (getArguments().containsKey(ROLE_ID_KEY)) {
            this.RoleID = UUIDPool.getUUID(getArguments().getString(ROLE_ID_KEY));
        } else {
            this.RoleID = null;
        }
        Debug.Printf("Group role details: new role = %s", this.RoleID);
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDGroup)) {
            return;
        }
        UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
        this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
        this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
        this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
    }
}
