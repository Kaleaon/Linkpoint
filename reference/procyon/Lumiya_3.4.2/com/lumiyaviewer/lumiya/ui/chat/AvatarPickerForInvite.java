// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import java.util.Iterator;
import android.widget.SpinnerAdapter;
import java.util.List;
import android.widget.ArrayAdapter;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.View;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.widget.Toast;
import android.widget.Spinner;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.LayoutInflater;
import java.io.Serializable;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment;

public class AvatarPickerForInvite extends AvatarPickerFragment
{
    private static final String GROUP_ID_KEY = "groupID";
    private static final String GROUP_LIST_KEY = "avatarGroupList";
    private static final String GROUP_PROFILE_KEY = "groupProfile";
    private static final String GROUP_ROLES = "groupRoles";
    private static final String GROUP_TITLES_KEY = "groupTitles";
    
    private UUID getGroupID() {
        return UUID.fromString(this.getArguments().getString("groupID"));
    }
    
    public static Bundle makeArguments(final UUID uuid, final UUID uuid2, final GroupProfileReply groupProfileReply, final AvatarGroupList list, final GroupTitlesReply groupTitlesReply, final GroupRoleDataReply groupRoleDataReply) {
        final Bundle fragmentArguments = ActivityUtils.makeFragmentArguments(uuid, null);
        fragmentArguments.putString("groupID", uuid2.toString());
        fragmentArguments.putParcelable("groupProfile", (Parcelable)groupProfileReply);
        fragmentArguments.putSerializable("avatarGroupList", (Serializable)list);
        fragmentArguments.putParcelable("groupTitles", (Parcelable)groupTitlesReply);
        fragmentArguments.putParcelable("groupRoles", (Parcelable)groupRoleDataReply);
        return fragmentArguments;
    }
    
    @Override
    protected void createExtraView(final LayoutInflater layoutInflater, final FrameLayout frameLayout) {
        layoutInflater.inflate(2130968661, (ViewGroup)frameLayout);
    }
    
    @Override
    public String getTitle() {
        return this.getString(2131296639);
    }
    
    @Override
    protected void onAvatarSelected(final ChatterID chatterID, @Nullable final String s) {
        final View view = this.getView();
        if (view != null) {
            final Object selectedItem = ((Spinner)view.findViewById(2131755459)).getSelectedItem();
            if (selectedItem instanceof RoleEntry) {
                final UserManager userManager = chatterID.getUserManager();
                if (userManager != null && chatterID instanceof ChatterID.ChatterIDUser) {
                    final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                    if (activeAgentCircuit != null) {
                        activeAgentCircuit.getModules().groupManager.SendGroupInvite(((ChatterID.ChatterIDUser)chatterID).getChatterUUID(), this.getGroupID(), ((RoleEntry)selectedItem).roleID);
                        Toast.makeText(this.getContext(), 2131296573, 1).show();
                        final FragmentActivity activity = this.getActivity();
                        if (activity instanceof DetailsActivity) {
                            ((DetailsActivity)activity).closeDetailsFragment(this);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final GroupProfileReply groupProfileReply = (GroupProfileReply)this.getArguments().getParcelable("groupProfile");
        final GroupTitlesReply groupTitlesReply = (GroupTitlesReply)this.getArguments().getParcelable("groupTitles");
        final AvatarGroupList list = (AvatarGroupList)this.getArguments().getSerializable("avatarGroupList");
        final GroupRoleDataReply groupRoleDataReply = (GroupRoleDataReply)this.getArguments().getParcelable("groupRoles");
        boolean b = false;
        final HashSet set = new HashSet();
        boolean b2;
        if (groupTitlesReply != null && groupProfileReply != null) {
            for (final GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                set.add(groupData.RoleID);
                if (groupData.RoleID.equals(groupProfileReply.GroupData_Field.OwnerRole)) {
                    b = true;
                }
            }
            b2 = b;
        }
        else {
            b2 = false;
        }
        while (true) {
            Label_0428: {
                if (list == null) {
                    break Label_0428;
                }
                final AvatarGroupList.AvatarGroupEntry avatarGroupEntry = list.Groups.get(this.getGroupID());
                if (avatarGroupEntry == null) {
                    break Label_0428;
                }
                int n;
                if ((avatarGroupEntry.GroupPowers & 0x100L) != 0x0L) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                int n3;
                int n4;
                if ((avatarGroupEntry.GroupPowers & 0x80L) != 0x0L) {
                    final int n2 = 1;
                    n3 = n;
                    n4 = n2;
                }
                else {
                    n3 = n;
                    n4 = 0;
                }
                final ImmutableList.Builder<RoleEntry> builder = new ImmutableList.Builder<RoleEntry>();
                if (groupRoleDataReply != null && groupProfileReply != null) {
                    for (final GroupRoleDataReply.RoleData roleData : groupRoleDataReply.RoleData_Fields) {
                        if (b2 || (n3 != 0 && (roleData.RoleID.equals(groupProfileReply.GroupData_Field.OwnerRole) ^ true)) || roleData.RoleID.equals(UUIDPool.ZeroUUID) || (n4 != 0 && set.contains(roleData.RoleID))) {
                            builder.add(new RoleEntry(roleData.RoleID, SLMessage.stringFromVariableOEM(roleData.Title), null));
                        }
                    }
                }
                final ImmutableList<RoleEntry> build = builder.build();
                final View view = this.getView();
                if (view != null) {
                    ((Spinner)view.findViewById(2131755459)).setAdapter((SpinnerAdapter)new ArrayAdapter(this.getContext(), 17367049, (List)build));
                }
                return;
            }
            int n4 = 0;
            int n3 = 0;
            continue;
        }
    }
    
    private static class RoleEntry
    {
        final UUID roleID;
        final String roleTitle;
        
        private RoleEntry(final UUID roleID, final String roleTitle) {
            this.roleID = roleID;
            this.roleTitle = roleTitle;
        }
        
        @Override
        public String toString() {
            return this.roleTitle;
        }
    }
}
