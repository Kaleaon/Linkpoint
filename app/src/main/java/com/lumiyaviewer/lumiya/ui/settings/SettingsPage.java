package com.lumiyaviewer.lumiya.ui.settings;

import com.lumiyaviewer.lumiya.R;

public enum SettingsPage {
    PageConnection(R.xml.preferences_connection, R.string.prefs_category_connection),
    PageAppearance(R.xml.preferences_appearance, R.string.prefs_category_appearance),
    PageChat(R.xml.preferences_chat, R.string.prefs_category_chat),
    PageNotifications(R.xml.preferences_notifications, R.string.prefs_category_notifications),
    Page3D(R.xml.preferences_3d, R.string.prefs_category_3d),
    PageRLV(R.xml.preferences_rlv, R.string.prefs_category_rlv),
    PageCache(R.xml.preferences_cache, R.string.prefs_category_cache);
    
    private final int pageResourceId;
    private final int pageTitle;

    private SettingsPage(int i, int i2) {
        this.pageResourceId = i;
        this.pageTitle = i2;
    }

    public int getPageResourceId() {
        return this.pageResourceId;
    }

    public int getPageTitle() {
        return this.pageTitle;
    }
}
