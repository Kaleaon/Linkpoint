// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupMemberRolesFragment

private class <init> extends BaseAdapter
{

    private GroupRoleDataReply data;
    private final Set selectedRoles;
    final GroupMemberRolesFragment this$0;

    public int getCount()
    {
        if (data != null)
        {
            return data.RoleData_Fields.size();
        } else
        {
            return 0;
        }
    }

    public com.lumiyaviewer.lumiya.slproto.messages.data getItem(int i)
    {
        if (data != null && i >= 0 && i < data.RoleData_Fields.size())
        {
            return (com.lumiyaviewer.lumiya.slproto.messages.data)data.RoleData_Fields.get(i);
        } else
        {
            return null;
        }
    }

    public volatile Object getItem(int i)
    {
        return getItem(i);
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    public Set getSelectedRoles()
    {
        return selectedRoles;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        View view1 = view;
        if (view == null)
        {
            view1 = LayoutInflater.from(getContext()).inflate(0x7f040043, viewgroup, false);
        }
        view = getItem(i);
        if (view != null)
        {
            ((CheckedTextView)view1.findViewById(0x7f100173)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.getItem) (view)).getItem));
            viewgroup = (CheckedTextView)view1.findViewById(0x7f100173);
            boolean flag;
            if (!((com.lumiyaviewer.lumiya.slproto.messages.getItem) (view)).getItem.equals(UUIDPool.ZeroUUID))
            {
                flag = selectedRoles.contains(((com.lumiyaviewer.lumiya.slproto.messages.selectedRoles) (view)).selectedRoles);
            } else
            {
                flag = true;
            }
            viewgroup.setChecked(flag);
        }
        return view1;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public void setData(GroupRoleDataReply grouproledatareply, Set set)
    {
        data = grouproledatareply;
        selectedRoles.clear();
        if (set != null)
        {
            selectedRoles.addAll(set);
        }
        GroupMemberRolesFragment._2D_wrap1(GroupMemberRolesFragment.this);
        notifyDataSetInvalidated();
    }

    public void toggleChecked(UUID uuid)
    {
        if (uuid.equals(UUIDPool.ZeroUUID) || GroupMemberRolesFragment._2D_get4(GroupMemberRolesFragment.this) == null || GroupMemberRolesFragment._2D_get0(GroupMemberRolesFragment.this) == null) goto _L2; else goto _L1
_L1:
        long l = GroupMemberRolesFragment._2D_wrap0(GroupMemberRolesFragment.this);
        boolean flag1;
        boolean flag2;
        flag1 = ((Set)GroupMemberRolesFragment._2D_get1(GroupMemberRolesFragment.this).get()).contains(uuid);
        flag2 = selectedRoles.contains(uuid) ^ true;
        if (flag1 != flag2) goto _L4; else goto _L3
_L3:
        if (!flag2) goto _L6; else goto _L5
_L5:
        selectedRoles.add(uuid);
_L7:
        boolean flag = true;
_L13:
        if (flag)
        {
            GroupMemberRolesFragment._2D_wrap1(GroupMemberRolesFragment.this);
            notifyDataSetChanged();
        }
_L2:
        return;
_L6:
        selectedRoles.remove(uuid);
          goto _L7
_L4:
        if (flag2) goto _L9; else goto _L8
_L8:
        if ((l & 512L) == 0L) goto _L11; else goto _L10
_L10:
        flag1 = uuid.equals(((GroupProfileReply)GroupMemberRolesFragment._2D_get2(GroupMemberRolesFragment.this).get()).GroupData_Field._fld0);
        flag2 = GroupMemberRolesFragment._2D_get4(GroupMemberRolesFragment.this).getUserID().equals(GroupMemberRolesFragment._2D_get0(GroupMemberRolesFragment.this));
        if (flag1 && !flag2) goto _L11; else goto _L12
_L12:
        selectedRoles.remove(uuid);
        flag = true;
          goto _L13
_L21:
        if (!flag) goto _L11; else goto _L14
_L14:
        selectedRoles.add(uuid);
        flag = true;
          goto _L13
_L23:
        if ((l & 128L) == 0L) goto _L16; else goto _L15
_L15:
        Object obj = (GroupTitlesReply)GroupMemberRolesFragment._2D_get3(GroupMemberRolesFragment.this).getData();
        if (obj == null) goto _L16; else goto _L17
_L17:
        obj = ((GroupTitlesReply) (obj)).GroupData_Fields.iterator();
_L20:
        if (!((Iterator) (obj)).hasNext()) goto _L16; else goto _L18
_L18:
        flag1 = ((com.lumiyaviewer.lumiya.slproto.messages.this._cls0)((Iterator) (obj)).next())._fld0.equals(uuid);
        if (!flag1) goto _L20; else goto _L19
_L19:
        flag = true;
          goto _L21
_L11:
        flag = false;
          goto _L13
_L16:
        flag = false;
          goto _L21
        uuid;
        flag = false;
          goto _L13
_L9:
        if ((256L & l) == 0L) goto _L23; else goto _L22
_L22:
        flag = true;
          goto _L21
    }

    private Y()
    {
        this$0 = GroupMemberRolesFragment.this;
        super();
        data = null;
        selectedRoles = new HashSet();
    }

    selectedRoles(selectedRoles selectedroles)
    {
        this();
    }
}
