// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class AvatarPickerForInvite extends AvatarPickerFragment
{
    private static class RoleEntry
    {

        final UUID roleID;
        final String roleTitle;

        public String toString()
        {
            return roleTitle;
        }

        private RoleEntry(UUID uuid, String s)
        {
            roleID = uuid;
            roleTitle = s;
        }

        RoleEntry(UUID uuid, String s, RoleEntry roleentry)
        {
            this(uuid, s);
        }
    }


    private static final String GROUP_ID_KEY = "groupID";
    private static final String GROUP_LIST_KEY = "avatarGroupList";
    private static final String GROUP_PROFILE_KEY = "groupProfile";
    private static final String GROUP_ROLES = "groupRoles";
    private static final String GROUP_TITLES_KEY = "groupTitles";

    public AvatarPickerForInvite()
    {
    }

    private UUID getGroupID()
    {
        return UUID.fromString(getArguments().getString("groupID"));
    }

    public static Bundle makeArguments(UUID uuid, UUID uuid1, GroupProfileReply groupprofilereply, AvatarGroupList avatargrouplist, GroupTitlesReply grouptitlesreply, GroupRoleDataReply grouproledatareply)
    {
        uuid = ActivityUtils.makeFragmentArguments(uuid, null);
        uuid.putString("groupID", uuid1.toString());
        uuid.putParcelable("groupProfile", groupprofilereply);
        uuid.putSerializable("avatarGroupList", avatargrouplist);
        uuid.putParcelable("groupTitles", grouptitlesreply);
        uuid.putParcelable("groupRoles", grouproledatareply);
        return uuid;
    }

    protected void createExtraView(LayoutInflater layoutinflater, FrameLayout framelayout)
    {
        layoutinflater.inflate(0x7f040055, framelayout);
    }

    public String getTitle()
    {
        return getString(0x7f09017f);
    }

    protected void onAvatarSelected(ChatterID chatterid, String s)
    {
        s = getView();
        if (s != null)
        {
            s = ((String) (((Spinner)s.findViewById(0x7f1001c3)).getSelectedItem()));
            if (s instanceof RoleEntry)
            {
                Object obj = chatterid.getUserManager();
                if (obj != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
                {
                    obj = ((UserManager) (obj)).getActiveAgentCircuit();
                    if (obj != null)
                    {
                        ((SLAgentCircuit) (obj)).getModules().groupManager.SendGroupInvite(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID(), getGroupID(), ((RoleEntry)s).roleID);
                        Toast.makeText(getContext(), 0x7f09013d, 1).show();
                        chatterid = getActivity();
                        if (chatterid instanceof DetailsActivity)
                        {
                            ((DetailsActivity)chatterid).closeDetailsFragment(this);
                        }
                    }
                }
            }
        }
    }

    public void onStart()
    {
        Object obj;
        Object obj1;
        Object obj2;
        Object obj3;
        Object obj4;
        boolean flag;
        super.onStart();
        obj = (GroupProfileReply)getArguments().getParcelable("groupProfile");
        obj4 = (GroupTitlesReply)getArguments().getParcelable("groupTitles");
        obj3 = (AvatarGroupList)getArguments().getSerializable("avatarGroupList");
        obj2 = (GroupRoleDataReply)getArguments().getParcelable("groupRoles");
        flag = false;
        obj1 = new HashSet();
        if (obj4 == null || obj == null) goto _L2; else goto _L1
_L1:
        obj4 = ((GroupTitlesReply) (obj4)).GroupData_Fields.iterator();
        do
        {
            if (!((Iterator) (obj4)).hasNext())
            {
                break;
            }
            com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData groupdata = (com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)((Iterator) (obj4)).next();
            ((Set) (obj1)).add(groupdata.RoleID);
            if (groupdata.RoleID.equals(((GroupProfileReply) (obj)).GroupData_Field.OwnerRole))
            {
                flag = true;
            }
        } while (true);
          goto _L3
_L2:
        boolean flag2 = false;
_L9:
        if (obj3 == null) goto _L5; else goto _L4
_L4:
        obj3 = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList) (obj3)).Groups.get(getGroupID());
        if (obj3 == null) goto _L5; else goto _L6
_L6:
        boolean flag1;
        com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData roledata;
        if ((((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (obj3)).GroupPowers & 256L) != 0L)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if ((((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (obj3)).GroupPowers & 128L) != 0L)
        {
            boolean flag3 = true;
            flag1 = flag;
            flag = flag3;
        } else
        {
            flag1 = flag;
            flag = false;
        }
_L7:
        obj3 = new com.google.common.collect.ImmutableList.Builder();
        if (obj2 != null && obj != null)
        {
            obj2 = ((GroupRoleDataReply) (obj2)).RoleData_Fields.iterator();
            do
            {
                if (!((Iterator) (obj2)).hasNext())
                {
                    break;
                }
                roledata = (com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData)((Iterator) (obj2)).next();
                boolean flag4;
                if (!flag2 && (!flag1 || !(roledata.RoleID.equals(((GroupProfileReply) (obj)).GroupData_Field.OwnerRole) ^ true)) && !roledata.RoleID.equals(UUIDPool.ZeroUUID))
                {
                    if (flag)
                    {
                        flag4 = ((Set) (obj1)).contains(roledata.RoleID);
                    } else
                    {
                        flag4 = false;
                    }
                } else
                {
                    flag4 = true;
                }
                if (flag4)
                {
                    ((com.google.common.collect.ImmutableList.Builder) (obj3)).add(new RoleEntry(roledata.RoleID, SLMessage.stringFromVariableOEM(roledata.Title), null));
                }
            } while (true);
        }
        obj1 = ((com.google.common.collect.ImmutableList.Builder) (obj3)).build();
        obj = getView();
        if (obj != null)
        {
            obj1 = new ArrayAdapter(getContext(), 0x1090009, ((java.util.List) (obj1)));
            ((Spinner)((View) (obj)).findViewById(0x7f1001c3)).setAdapter(((android.widget.SpinnerAdapter) (obj1)));
        }
        return;
_L5:
        flag = false;
        flag1 = false;
        if (true) goto _L7; else goto _L3
_L3:
        flag2 = flag;
        if (true) goto _L9; else goto _L8
_L8:
    }
}
