// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupRolesProfileTab

private class <init> extends BaseAdapter
{

    private GroupRoleDataReply data;
    private GroupProfileReply groupProfile;
    final GroupRolesProfileTab this$0;
    private Map titlesByRole;

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

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        View view1;
        boolean flag;
        flag = true;
        view1 = view;
        if (view == null)
        {
            view1 = LayoutInflater.from(getContext()).inflate(0x7f040047, viewgroup, false);
        }
        view = getItem(i);
        if (view == null) goto _L2; else goto _L1
_L1:
        i = ((com.lumiyaviewer.lumiya.slproto.messages.getItem) (view)).getItem;
        if (((com.lumiyaviewer.lumiya.slproto.messages.getItem) (view)).getItem.equals(UUIDPool.ZeroUUID) && groupProfile != null)
        {
            i = groupProfile.GroupData_Field.rshipCount;
        }
        ((TextView)view1.findViewById(0x7f10017d)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.rshipCount) (view)).rshipCount));
        ((TextView)view1.findViewById(0x7f10017e)).setText(getResources().getQuantityString(0x7f110001, i, new Object[] {
            Integer.valueOf(i)
        }));
        if (titlesByRole == null) goto _L4; else goto _L3
_L3:
        view = (com.lumiyaviewer.lumiya.slproto.messages.titlesByRole)titlesByRole.get(((com.lumiyaviewer.lumiya.slproto.messages.titlesByRole) (view)).titlesByRole);
        if (view == null) goto _L4; else goto _L5
_L5:
        boolean flag1;
        flag1 = ((com.lumiyaviewer.lumiya.slproto.messages.titlesByRole) (view)).titlesByRole;
        i = 1;
_L7:
        view = view1.findViewById(0x7f10017c);
        if (i != 0)
        {
            i = 0;
        } else
        {
            i = 4;
        }
        view.setVisibility(i);
        view = (TextView)view1.findViewById(0x7f10017d);
        if (flag1)
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 0;
        }
        view.setTypeface(null, i);
_L2:
        return view1;
_L4:
        flag1 = false;
        i = 0;
        if (true) goto _L7; else goto _L6
_L6:
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public void setData(GroupRoleDataReply grouproledatareply, GroupTitlesReply grouptitlesreply, GroupProfileReply groupprofilereply)
    {
        data = grouproledatareply;
        if (grouptitlesreply != null)
        {
            titlesByRole = new HashMap();
            for (grouproledatareply = grouptitlesreply.GroupData_Fields.iterator(); grouproledatareply.hasNext(); titlesByRole.put(((com.lumiyaviewer.lumiya.slproto.messages.titlesByRole) (grouptitlesreply)).titlesByRole, grouptitlesreply))
            {
                grouptitlesreply = (com.lumiyaviewer.lumiya.slproto.messages.titlesByRole)grouproledatareply.next();
            }

        } else
        {
            titlesByRole = null;
        }
        groupProfile = groupprofilereply;
        notifyDataSetInvalidated();
    }

    private ()
    {
        this$0 = GroupRolesProfileTab.this;
        super();
        groupProfile = null;
        data = null;
        titlesByRole = null;
    }

    titlesByRole(titlesByRole titlesbyrole)
    {
        this();
    }
}
