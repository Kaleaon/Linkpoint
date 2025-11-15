package com.lumiyaviewer.lumiya.ui.settings

import com.lumiyaviewer.lumiya.R

enum SettingsPage {
    PageConnection(R.xml.preferences_connection, R.string.prefs_category_connection),
    PageAppearance(R.xml.preferences_appearance, R.string.prefs_category_appearance),
    PageChat(R.xml.preferences_chat, R.string.prefs_category_chat),
    PageNotifications(R.xml.preferences_notifications, R.string.prefs_category_notifications),
    Page3D(R.xml.preferences_3d, R.string.prefs_category_3d),
    PageRLV(R.xml.preferences_rlv, R.string.prefs_category_rlv),
    PageCache(R.xml.preferences_cache, R.string.prefs_category_cache)
    
    private Int pageResourceId
    private Int pageTitle

    private SettingsPage(Int i, Int i2) {
        this.pageResourceId = i
        this.pageTitle = i2
    }

    Int getPageResourceId() {
        return this.pageResourceId
    }

    Int getPageTitle() {
        return this.pageTitle
    }
}
