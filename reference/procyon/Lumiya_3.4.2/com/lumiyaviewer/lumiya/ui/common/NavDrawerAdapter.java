// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import java.util.UUID;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.login.LogoutDialog;
import com.lumiyaviewer.lumiya.ui.settings.SettingsActivity;
import com.lumiyaviewer.lumiya.ui.search.SearchGridActivity;
import com.lumiyaviewer.lumiya.ui.myava.MyAvatarActivity;
import android.app.Activity;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.objects.ObjectListNewActivity;
import com.lumiyaviewer.lumiya.ui.render.WorldViewActivity;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ArrayAdapter;

class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem> implements AdapterView$OnItemClickListener
{
    private static NavDrawerItem[] items;
    
    static {
        NavDrawerAdapter.items = new NavDrawerItem[] { (NavDrawerItem)new NavDrawerActivityItem(2131755881, 2130772019, 2131296714, ChatNewActivity.class), (NavDrawerItem)new NavDrawerActivityItem(2131755847, 2130772053, 2131296713, WorldViewActivity.class), (NavDrawerItem)new NavDrawerActivityItem(2131755848, 2130772028, 2131296719, ObjectListNewActivity.class), (NavDrawerItem)new NavDrawerActivityItem(2131755785, 2130772015, 2131296715, InventoryActivity.class), (NavDrawerItem)new NavDrawerActivityItem(2131755849, 2130772021, 2131296717, MinimapActivity.class), (NavDrawerItem)new NavDrawerItem(2131755850, 2130772013, 2131296724) {
                @Override
                public void onClick(final Context context) {
                    if (context instanceof Activity) {
                        TeleportHomeDialog.show((Activity)context);
                    }
                }
            }, (NavDrawerItem)new NavDrawerActivityItem(2131755851, 2130771995, 2131296718, MyAvatarActivity.class), (NavDrawerItem)new NavDrawerActivityItem(2131755852, 2130772035, 2131296721, SearchGridActivity.class), (NavDrawerItem)new NavDrawerActivityItem(2131755787, 2130772037, 2131296722, SettingsActivity.class), (NavDrawerItem)new NavDrawerItem(2131755857, 2130772041, 2131296723) {
                @Override
                public void onClick(final Context context) {
                    if (context instanceof Activity) {
                        LogoutDialog.show((Activity)context);
                    }
                }
            } };
    }
    
    NavDrawerAdapter(final Context context) {
        super(context, 2130968676, (Object[])NavDrawerAdapter.items);
    }
    
    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        final NavDrawerItem navDrawerItem = (NavDrawerItem)this.getItem(n);
        if (navDrawerItem == null) {
            return null;
        }
        View inflate;
        if ((inflate = view) == null) {
            inflate = ((LayoutInflater)this.getContext().getSystemService("layout_inflater")).inflate(2130968676, viewGroup, false);
        }
        final TypedValue typedValue = new TypedValue();
        this.getContext().getTheme().resolveAttribute(navDrawerItem.iconId, typedValue, true);
        ((TextView)inflate.findViewById(2131755501)).setText((CharSequence)this.getContext().getString(navDrawerItem.labelId));
        ((ImageView)inflate.findViewById(2131755500)).setImageResource(typedValue.resourceId);
        return inflate;
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final NavDrawerItem navDrawerItem = (NavDrawerItem)this.getItem(n);
        if (navDrawerItem != null) {
            navDrawerItem.onClick(adapterView.getContext());
        }
    }
    
    private static class NavDrawerActivityItem extends NavDrawerItem
    {
        final Class<?> activityClass;
        
        NavDrawerActivityItem(final int n, final int n2, final int n3, final Class<?> activityClass) {
            super(n, n2, n3);
            this.activityClass = activityClass;
        }
        
        @Override
        public void onClick(final Context context) {
            final Intent intent = new Intent(context, (Class)this.activityClass);
            intent.addFlags(131072);
            if (context instanceof Activity) {
                final UUID activeAgentID = ActivityUtils.getActiveAgentID(((Activity)context).getIntent());
                if (activeAgentID != null) {
                    intent.putExtra("activeAgentUUID", activeAgentID.toString());
                }
            }
            context.startActivity(intent);
        }
    }
    
    static class NavDrawerItem
    {
        final int iconId;
        final int itemId;
        final int labelId;
        
        NavDrawerItem(final int itemId, final int iconId, final int labelId) {
            this.itemId = itemId;
            this.iconId = iconId;
            this.labelId = labelId;
        }
        
        public void onClick(final Context context) {
        }
    }
}
