package com.lumiyaviewer.lumiya.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity
import com.lumiyaviewer.lumiya.ui.login.LogoutDialog
import com.lumiyaviewer.lumiya.ui.minimap.MinimapActivity
import com.lumiyaviewer.lumiya.ui.myava.MyAvatarActivity
import com.lumiyaviewer.lumiya.ui.objects.ObjectListNewActivity
import com.lumiyaviewer.lumiya.ui.render.WorldViewActivity
import com.lumiyaviewer.lumiya.ui.search.SearchGridActivity
import com.lumiyaviewer.lumiya.ui.settings.SettingsActivity
import java.util.UUID

class NavDrawerAdapter : ArrayAdapter<NavDrawerItem> : AdapterView.OnItemClickListener {
    private NavDrawerItem[] items = {NavDrawerActivityItem(R.id.item_chat, R.attr.MenuIconLocalChatThemed, R.string.nav_chat, ChatNewActivity.class), NavDrawerActivityItem(R.id.item_3d_view, R.attr.MenuIconWorldViewThemed, R.string.nav_3d_view, WorldViewActivity.class), NavDrawerActivityItem(R.id.item_objects, R.attr.MenuIconObjectsThemed, R.string.nav_objects, ObjectListNewActivity.class), NavDrawerActivityItem(R.id.item_inventory, R.attr.MenuIconInventoryThemed, R.string.nav_inventory, InventoryActivity.class), NavDrawerActivityItem(R.id.item_minimap, R.attr.MenuIconMinimapThemed, R.string.nav_minimap, MinimapActivity.class), NavDrawerItem(R.id.item_teleport_home, R.attr.MenuIconHomeThemed, R.string.nav_teleport_home) {
        Unit onClick(Context context) {
            if (context instanceof Activity) {
                TeleportHomeDialog.show((Activity) context)
            }
        }
    }, NavDrawerActivityItem(R.id.item_my_avatar, R.attr.MenuIconCardThemed, R.string.nav_my_avatar, MyAvatarActivity.class), NavDrawerActivityItem(R.id.item_people_search, R.attr.MenuIconSearchThemed, R.string.nav_search, SearchGridActivity.class), NavDrawerActivityItem(R.id.item_settings, R.attr.MenuIconSettingsThemed, R.string.nav_settings, SettingsActivity.class), NavDrawerItem(R.id.item_signout, R.attr.MenuIconSignOffThemed, R.string.nav_signout) {
        Unit onClick(Context context) {
            if (context instanceof Activity) {
                LogoutDialog.show((Activity) context)
            }
        }
    }}

    private class NavDrawerActivityItem : NavDrawerItem {
        Class<?> activityClass

        NavDrawerActivityItem(Int i, Int i2, Int i3, Class<?> cls) {
            super(i, i2, i3)
            this.activityClass = cls
        }

        Unit onClick(Context context) {
            UUID activeAgentID
            Intent intent = Intent(context, this.activityClass)
            intent.addFlags(131072)
            if ((context instanceof Activity) && (activeAgentID = ActivityUtils.getActiveAgentID(((Activity) context).getIntent())) != null) {
                intent.putExtra("activeAgentUUID", activeAgentID.toString())
            }
            context.startActivity(intent)
        }
    }

    class NavDrawerItem {
        Int iconId
        Int itemId
        Int labelId

        NavDrawerItem(Int i, Int i2, Int i3) {
            this.itemId = i
            this.iconId = i2
            this.labelId = i3
        }

        Unit onClick(Context context) {
        }
    }

    NavDrawerAdapter(Context context) {
        super(context, R.layout.nav_drawer_list_item, items)
    }

    View getView(Int i, View view, ViewGroup viewGroup) {
        NavDrawerItem navDrawerItem = (NavDrawerItem) getItem(i)
        if (navDrawerItem == null) {
            return null
        }
        if (view == null) {
            view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.nav_drawer_list_item, viewGroup, false)
        }
        TypedValue typedValue = TypedValue()
        getContext().getTheme().resolveAttribute(navDrawerItem.iconId, typedValue, true)
        ((TextView) view.findViewById(R.id.navDrawerItemName)).setText(getContext().getString(navDrawerItem.labelId))
        ((ImageView) view.findViewById(R.id.navDrawerItemIcon)).setImageResource(typedValue.resourceId)
        return view
    }

    Unit onItemClick(AdapterView<?> adapterView, View view, Int i, Long j) {
        NavDrawerItem navDrawerItem = (NavDrawerItem) getItem(i)
        if (navDrawerItem != null) {
            navDrawerItem.onClick(adapterView.getContext())
        }
    }
}
