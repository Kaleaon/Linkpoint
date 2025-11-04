// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLInventoryNewContentsEvent
{
    public boolean firstIsFolder;
    public String firstItemName;
    public UUID firstParentUUID;
    public int newFolderCount;
    public int newItemCount;
    
    public SLInventoryNewContentsEvent() {
        this.newFolderCount = 0;
        this.newItemCount = 0;
        this.firstParentUUID = null;
        this.firstItemName = null;
        this.firstIsFolder = false;
    }
    
    public void AddItem(final boolean b, final UUID uuid, final String s) {
        if (b) {
            ++this.newFolderCount;
        }
        else {
            ++this.newItemCount;
        }
        if (this.firstParentUUID == null) {
            this.firstIsFolder = b;
            this.firstParentUUID = uuid;
            this.firstItemName = s;
        }
        else if (b && (this.firstIsFolder ^ true)) {
            this.firstIsFolder = b;
            this.firstParentUUID = uuid;
            this.firstItemName = s;
        }
    }
    
    public boolean isEmpty() {
        boolean b = false;
        if (this.newFolderCount == 0) {
            b = b;
            if (this.newItemCount == 0) {
                b = true;
            }
        }
        return b;
    }
}
