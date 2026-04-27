// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.login.LogoutDialog;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapActivity;
import com.lumiyaviewer.lumiya.ui.myava.MyAvatarActivity;
import com.lumiyaviewer.lumiya.ui.objects.ObjectListNewActivity;
import com.lumiyaviewer.lumiya.ui.render.WorldViewActivity;
import com.lumiyaviewer.lumiya.ui.search.SearchGridActivity;
import com.lumiyaviewer.lumiya.ui.settings.SettingsActivity;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ActivityUtils, TeleportHomeDialog

class NavDrawerAdapter extends ArrayAdapter
    implements android.widget.AdapterView.OnItemClickListener
{
    private static class NavDrawerActivityItem extends NavDrawerItem
    {

        final Class activityClass;

        public void onClick(Context context)
        {
            Intent intent = new Intent(context, activityClass);
            intent.addFlags(0x20000);
            if (context instanceof Activity)
            {
                UUID uuid = ActivityUtils.getActiveAgentID(((Activity)context).getIntent());
                if (uuid != null)
                {
                    intent.putExtra("activeAgentUUID", uuid.toString());
                }
            }
            context.startActivity(intent);
        }

        NavDrawerActivityItem(int i, int j, int k, Class class1)
        {
            super(i, j, k);
            activityClass = class1;
        }
    }

    static class NavDrawerItem
    {

        final int iconId;
        final int itemId;
        final int labelId;

        public void onClick(Context context)
        {
        }

        NavDrawerItem(int i, int j, int k)
        {
            itemId = i;
            iconId = j;
            labelId = k;
        }
    }


    private static NavDrawerItem items[] = {
        new NavDrawerActivityItem(0x7f100369, 0x7f010033, 0x7f0901ca, com/lumiyaviewer/lumiya/ui/chat/ChatNewActivity), new NavDrawerActivityItem(0x7f100347, 0x7f010055, 0x7f0901c9, com/lumiyaviewer/lumiya/ui/render/WorldViewActivity), new NavDrawerActivityItem(0x7f100348, 0x7f01003c, 0x7f0901cf, com/lumiyaviewer/lumiya/ui/objects/ObjectListNewActivity), new NavDrawerActivityItem(0x7f100309, 0x7f01002f, 0x7f0901cb, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity), new NavDrawerActivityItem(0x7f100349, 0x7f010035, 0x7f0901cd, com/lumiyaviewer/lumiya/ui/minimap/MinimapActivity), new NavDrawerItem(0x7f10034a, 0x7f01002d, 0x7f0901d4) {

            public void onClick(Context context)
            {
                if (context instanceof Activity)
                {
                    TeleportHomeDialog.show((Activity)context);
                }
            }

        }, new NavDrawerActivityItem(0x7f10034b, 0x7f01001b, 0x7f0901ce, com/lumiyaviewer/lumiya/ui/myava/MyAvatarActivity), new NavDrawerActivityItem(0x7f10034c, 0x7f010043, 0x7f0901d1, com/lumiyaviewer/lumiya/ui/search/SearchGridActivity), new NavDrawerActivityItem(0x7f10030b, 0x7f010045, 0x7f0901d2, com/lumiyaviewer/lumiya/ui/settings/SettingsActivity), new NavDrawerItem(0x7f100351, 0x7f010049, 0x7f0901d3) {

            public void onClick(Context context)
            {
                if (context instanceof Activity)
                {
                    LogoutDialog.show((Activity)context);
                }
            }

        }
    };

    NavDrawerAdapter(Context context)
    {
        super(context, 0x7f040064, items);
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        NavDrawerItem navdraweritem = (NavDrawerItem)getItem(i);
        if (navdraweritem == null)
        {
            return null;
        }
        View view1 = view;
        if (view == null)
        {
            view1 = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(0x7f040064, viewgroup, false);
        }
        view = new TypedValue();
        getContext().getTheme().resolveAttribute(navdraweritem.iconId, view, true);
        ((TextView)view1.findViewById(0x7f1001ed)).setText(getContext().getString(navdraweritem.labelId));
        ((ImageView)view1.findViewById(0x7f1001ec)).setImageResource(((TypedValue) (view)).resourceId);
        return view1;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        view = (NavDrawerItem)getItem(i);
        if (view != null)
        {
            view.onClick(adapterview.getContext());
        }
    }

}
