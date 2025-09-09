package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashSet;
import java.util.UUID;
import javax.annotation.Nullable;

public class AvatarPickerForInvite extends AvatarPickerFragment {
    private static final String GROUP_ID_KEY = "groupID";
    private static final String GROUP_LIST_KEY = "avatarGroupList";
    private static final String GROUP_PROFILE_KEY = "groupProfile";
    private static final String GROUP_ROLES = "groupRoles";
    private static final String GROUP_TITLES_KEY = "groupTitles";

    private static class RoleEntry {
        final UUID roleID;
        final String roleTitle;

        private RoleEntry(UUID uuid, String str) {
            this.roleID = uuid;
            this.roleTitle = str;
        }

        /* synthetic */ RoleEntry(UUID uuid, String str, RoleEntry roleEntry) {
            this(uuid, str);
        }

        public String toString() {
            return this.roleTitle;
        }
    }

    private UUID getGroupID() {
        return UUID.fromString(getArguments().getString(GROUP_ID_KEY));
    }

    public static Bundle makeArguments(UUID uuid, UUID uuid2, GroupProfileReply groupProfileReply, AvatarGroupList avatarGroupList, GroupTitlesReply groupTitlesReply, GroupRoleDataReply groupRoleDataReply) {
        Bundle makeFragmentArguments = ActivityUtils.makeFragmentArguments(uuid, (Bundle) null);
        makeFragmentArguments.putString(GROUP_ID_KEY, uuid2.toString());
        makeFragmentArguments.putParcelable(GROUP_PROFILE_KEY, groupProfileReply);
        makeFragmentArguments.putSerializable(GROUP_LIST_KEY, avatarGroupList);
        makeFragmentArguments.putParcelable(GROUP_TITLES_KEY, groupTitlesReply);
        makeFragmentArguments.putParcelable(GROUP_ROLES, groupRoleDataReply);
        return makeFragmentArguments;
    }

    /* access modifiers changed from: protected */
    public void createExtraView(LayoutInflater layoutInflater, FrameLayout frameLayout) {
        layoutInflater.inflate(R.layout.invite_role_picker_extra, frameLayout);
    }

    public String getTitle() {
        return getString(R.string.invite_selector_title);
    }

    /* access modifiers changed from: protected */
    public void onAvatarSelected(ChatterID chatterID, @Nullable String str) {
        UserManager userManager;
        SLAgentCircuit activeAgentCircuit;
        View view = getView();
        if (view != null) {
            Object selectedItem = ((Spinner) view.findViewById(R.id.role_picker_spinner)).getSelectedItem();
            if ((selectedItem instanceof RoleEntry) && (userManager = chatterID.getUserManager()) != null && (chatterID instanceof ChatterID.ChatterIDUser) && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
                activeAgentCircuit.getModules().groupManager.SendGroupInvite(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), getGroupID(), ((RoleEntry) selectedItem).roleID);
                Toast.makeText(getContext(), R.string.group_invitation_sent, 1).show();
                FragmentActivity activity = getActivity();
                if (activity instanceof DetailsActivity) {
                    ((DetailsActivity) activity).closeDetailsFragment(this);
                }
            }
        }
    }

    public void onStart() {
        boolean z;
        boolean z2;
        boolean z3;
        AvatarGroupList.AvatarGroupEntry avatarGroupEntry;
        super.onStart();
        GroupProfileReply groupProfileReply = (GroupProfileReply) getArguments().getParcelable(GROUP_PROFILE_KEY);
        GroupTitlesReply groupTitlesReply = (GroupTitlesReply) getArguments().getParcelable(GROUP_TITLES_KEY);
        AvatarGroupList avatarGroupList = (AvatarGroupList) getArguments().getSerializable(GROUP_LIST_KEY);
        GroupRoleDataReply groupRoleDataReply = (GroupRoleDataReply) getArguments().getParcelable(GROUP_ROLES);
        boolean z4 = false;
        HashSet hashSet = new HashSet();
        if (groupTitlesReply == null || groupProfileReply == null) {
            z = false;
        } else {
            for (GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                hashSet.add(groupData.RoleID);
                z4 = groupData.RoleID.equals(groupProfileReply.GroupData_Field.OwnerRole) ? true : z4;
            }
            z = z4;
        }
        if (avatarGroupList == null || (avatarGroupEntry = avatarGroupList.Groups.get(getGroupID())) == null) {
            z2 = false;
            z3 = false;
        } else {
            boolean z5 = (avatarGroupEntry.GroupPowers & 256) != 0;
            if ((avatarGroupEntry.GroupPowers & 128) != 0) {
                z3 = z5;
                z2 = true;
            } else {
                z3 = z5;
                z2 = false;
            }
        }
        ImmutableList.Builder builder = new ImmutableList.Builder();
        if (!(groupRoleDataReply == null || groupProfileReply == null)) {
            for (GroupRoleDataReply.RoleData roleData : groupRoleDataReply.RoleData_Fields) {
                if ((z || (z3 && (roleData.RoleID.equals(groupProfileReply.GroupData_Field.OwnerRole) ^ true)) || roleData.RoleID.equals(UUIDPool.ZeroUUID)) ? true : z2 ? hashSet.contains(roleData.RoleID) : false) {
                    builder.add((Object) new RoleEntry(roleData.RoleID, SLMessage.stringFromVariableOEM(roleData.Title), (RoleEntry) null));
                }
            }
        }
        ImmutableList build = builder.build();
        View view = getView();
        if (view != null) {
            ((Spinner) view.findViewById(R.id.role_picker_spinner)).setAdapter(new ArrayAdapter(getContext(), 17367049, build));
        }
    }
}
