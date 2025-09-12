// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.minimap:
//            NearbyPeopleMinimapFragment

private class setHasStableIds extends android.support.v7.widget.rAdapter
{

    private ImmutableList chatters;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private long nextStableId;
    private int selectedPosition;
    private UUID selectedUUID;
    private final Map stableIds = new HashMap();
    final NearbyPeopleMinimapFragment this$0;
    private final UserManager userManager;

    public int getItemCount()
    {
        return chatters.size();
    }

    public long getItemId(int i)
    {
        if (i >= 0 && i < chatters.size())
        {
            Object obj = ((ChatterDisplayData)chatters.get(i)).chatterID.getOptionalChatterUUID();
            if (obj != null)
            {
                obj = (Long)stableIds.get(obj);
                if (obj != null)
                {
                    return ((Long) (obj)).longValue();
                }
            }
        }
        return -1L;
    }

    public volatile void onBindViewHolder(android.support.v7.widget.rAdapter radapter, int i)
    {
        onBindViewHolder((onBindViewHolder)radapter, i);
    }

    public void onBindViewHolder(onBindViewHolder onbindviewholder, int i)
    {
        boolean flag = false;
        if (i >= 0 && i < chatters.size())
        {
            Context context1 = context;
            LayoutInflater layoutinflater = layoutInflater;
            UserManager usermanager = userManager;
            ChatterDisplayData chatterdisplaydata = (ChatterDisplayData)chatters.get(i);
            if (i == selectedPosition)
            {
                flag = true;
            }
            onbindviewholder.oData(context1, layoutinflater, usermanager, chatterdisplaydata, flag);
        }
    }

    public volatile android.support.v7.widget.rAdapter onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return onCreateViewHolder(viewgroup, i);
    }

    public onCreateViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        viewgroup = layoutInflater.inflate(0x7f04005f, viewgroup, false);
        return new >(NearbyPeopleMinimapFragment.this, viewgroup);
    }

    public void setChatters(ImmutableList immutablelist)
    {
        if (immutablelist == null)
        {
            immutablelist = ImmutableList.of();
        }
        chatters = immutablelist;
        selectedPosition = -1;
        immutablelist = new HashSet();
        for (int i = 0; i < chatters.size(); i++)
        {
            UUID uuid = ((ChatterDisplayData)chatters.get(i)).chatterID.getOptionalChatterUUID();
            if (uuid == null)
            {
                continue;
            }
            immutablelist.add(uuid);
            if (!stableIds.containsKey(uuid))
            {
                stableIds.put(uuid, Long.valueOf(nextStableId));
                nextStableId = nextStableId + 1L;
            }
            if (Objects.equal(uuid, selectedUUID))
            {
                selectedPosition = i;
            }
        }

        stableIds.keySet().retainAll(immutablelist);
        notifyDataSetChanged();
    }

    public void setSelected(UUID uuid)
    {
        int i;
        selectedUUID = uuid;
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_93;
        }
        i = 0;
_L3:
        UUID uuid1;
        if (i >= chatters.size())
        {
            break MISSING_BLOCK_LABEL_93;
        }
        uuid1 = ((ChatterDisplayData)chatters.get(i)).chatterID.getOptionalChatterUUID();
        if (uuid1 == null || !Objects.equal(uuid, uuid1)) goto _L2; else goto _L1
_L1:
        if (i != selectedPosition)
        {
            int j = selectedPosition;
            selectedPosition = i;
            notifyItemChanged(selectedPosition);
            notifyItemChanged(j);
        }
        return;
_L2:
        i++;
          goto _L3
        i = -1;
          goto _L1
    }

    (Context context1, UserManager usermanager)
    {
        this$0 = NearbyPeopleMinimapFragment.this;
        super();
        nextStableId = 1L;
        chatters = ImmutableList.of();
        selectedPosition = -1;
        context = context1;
        userManager = usermanager;
        layoutInflater = LayoutInflater.from(context1);
        setHasStableIds(true);
    }
}
