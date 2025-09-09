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
import com.lumiyaviewer.lumiya.R;
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

class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem> implements AdapterView.OnItemClickListener {
    private static NavDrawerItem[] items = {new NavDrawerActivityItem(R.id.item_chat, R.attr.MenuIconLocalChatThemed, R.string.nav_chat, ChatNewActivity.class), new NavDrawerActivityItem(R.id.item_3d_view, R.attr.MenuIconWorldViewThemed, R.string.nav_3d_view, WorldViewActivity.class), new NavDrawerActivityItem(R.id.item_objects, R.attr.MenuIconObjectsThemed, R.string.nav_objects, ObjectListNewActivity.class), new NavDrawerActivityItem(R.id.item_inventory, R.attr.MenuIconInventoryThemed, R.string.nav_inventory, InventoryActivity.class), new NavDrawerActivityItem(R.id.item_minimap, R.attr.MenuIconMinimapThemed, R.string.nav_minimap, MinimapActivity.class), new NavDrawerItem(R.id.item_teleport_home, R.attr.MenuIconHomeThemed, R.string.nav_teleport_home) {
        public void onClick(Context context) {
            if (context instanceof Activity) {
                TeleportHomeDialog.show((Activity) context);
            }
        }
    }, new NavDrawerActivityItem(R.id.item_my_avatar, R.attr.MenuIconCardThemed, R.string.nav_my_avatar, MyAvatarActivity.class), new NavDrawerActivityItem(R.id.item_people_search, R.attr.MenuIconSearchThemed, R.string.nav_search, SearchGridActivity.class), new NavDrawerActivityItem(R.id.item_settings, R.attr.MenuIconSettingsThemed, R.string.nav_settings, SettingsActivity.class), new NavDrawerItem(R.id.item_signout, R.attr.MenuIconSignOffThemed, R.string.nav_signout) {
        public void onClick(Context context) {
            if (context instanceof Activity) {
                LogoutDialog.show((Activity) context);
            }
        }
    }};

    private static class NavDrawerActivityItem extends NavDrawerItem {
        final Class<?> activityClass;

        NavDrawerActivityItem(int i, int i2, int i3, Class<?> cls) {
            super(i, i2, i3);
            this.activityClass = cls;
        }

        public void onClick(Context context) {
            UUID activeAgentID;
            Intent intent = new Intent(context, this.activityClass);
            intent.addFlags(131072);
            if ((context instanceof Activity) && (activeAgentID = ActivityUtils.getActiveAgentID(((Activity) context).getIntent())) != null) {
                intent.putExtra("activeAgentUUID", activeAgentID.toString());
            }
            context.startActivity(intent);
        }
    }

    static class NavDrawerItem {
        final int iconId;
        final int itemId;
        final int labelId;

        NavDrawerItem(int i, int i2, int i3) {
            this.itemId = i;
            this.iconId = i2;
            this.labelId = i3;
        }

        public void onClick(Context context) {
        }
    }

    NavDrawerAdapter(Context context) {
        super(context, R.layout.nav_drawer_list_item, items);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        NavDrawerItem navDrawerItem = (NavDrawerItem) getItem(i);
        if (navDrawerItem == null) {
            return null;
        }
        if (view == null) {
            view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.nav_drawer_list_item, viewGroup, false);
        }
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(navDrawerItem.iconId, typedValue, true);
        ((TextView) view.findViewById(R.id.navDrawerItemName)).setText(getContext().getString(navDrawerItem.labelId));
        ((ImageView) view.findViewById(R.id.navDrawerItemIcon)).setImageResource(typedValue.resourceId);
        return view;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        NavDrawerItem navDrawerItem = (NavDrawerItem) getItem(i);
        if (navDrawerItem != null) {
            navDrawerItem.onClick(adapterView.getContext());
        }
    }
}
