package com.linkpoint.ui.settings

import com.linkpoint.R

enum class SettingsPage(val pageResourceId: Int, val pageTitle: Int) {
    PageConnection(R.xml.preferences_connection, R.string.prefs_category_connection),
    PageAppearance(R.xml.preferences_appearance, R.string.prefs_category_appearance),
    PageChat(R.xml.preferences_chat, R.string.prefs_category_chat),
    PageNotifications(R.xml.preferences_notifications, R.string.prefs_category_notifications),
    Page3D(R.xml.preferences_3d, R.string.prefs_category_3d),
    PageRLV(R.xml.preferences_rlv, R.string.prefs_category_rlv),
    PageCache(R.xml.preferences_cache, R.string.prefs_category_cache)
}