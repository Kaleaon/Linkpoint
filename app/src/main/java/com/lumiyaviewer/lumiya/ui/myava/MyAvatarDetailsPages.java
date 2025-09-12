package com.lumiyaviewer.lumiya.ui.myava;

import com.lumiyaviewer.lumiya.R;

enum MyAvatarDetailsPages {
    pageProfile(R.string.my_profile_fragment_title),
    pageOutfits(R.string.my_outfits_fragment_title),
    pageBlockList(R.string.block_list_fragment_title),
    pageBalance(-1);
    
    private final int titleResource;

    private MyAvatarDetailsPages(int i) {
        this.titleResource = i;
    }

    public int getTitleResource() {
        return this.titleResource;
    }
}
