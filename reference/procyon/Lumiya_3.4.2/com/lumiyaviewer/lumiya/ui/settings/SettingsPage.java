// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

public enum SettingsPage
{
    Page3D("Page3D", 4, 2131165187, 2131296894), 
    PageAppearance("PageAppearance", 1, 2131165190, 2131296896), 
    PageCache("PageCache", 6, 2131165191, 2131296897), 
    PageChat("PageChat", 2, 2131165192, 2131296898), 
    PageConnection("PageConnection", 0, 2131165193, 2131296899), 
    PageNotifications("PageNotifications", 3, 2131165194, 2131296900), 
    PageRLV("PageRLV", 5, 2131165198, 2131296904);
    
    private final int pageResourceId;
    private final int pageTitle;
    
    private SettingsPage(final String name, final int ordinal, final int pageResourceId, final int pageTitle) {
        this.pageResourceId = pageResourceId;
        this.pageTitle = pageTitle;
    }
    
    public int getPageResourceId() {
        return this.pageResourceId;
    }
    
    public int getPageTitle() {
        return this.pageTitle;
    }
}
