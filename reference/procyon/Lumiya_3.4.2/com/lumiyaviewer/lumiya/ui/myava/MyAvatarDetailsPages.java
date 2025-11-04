// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

enum MyAvatarDetailsPages
{
    pageBalance("pageBalance", 3, -1), 
    pageBlockList("pageBlockList", 2, 2131296384), 
    pageOutfits("pageOutfits", 1, 2131296710), 
    pageProfile("pageProfile", 0, 2131296711);
    
    private final int titleResource;
    
    private MyAvatarDetailsPages(final String name, final int ordinal, final int titleResource) {
        this.titleResource = titleResource;
    }
    
    public int getTitleResource() {
        return this.titleResource;
    }
}
