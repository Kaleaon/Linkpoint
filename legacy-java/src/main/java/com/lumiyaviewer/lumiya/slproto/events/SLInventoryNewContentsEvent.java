package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

/* loaded from: classes.dex */
public class SLInventoryNewContentsEvent {
    public int newFolderCount = 0;
    public int newItemCount = 0;
    public UUID firstParentUUID = null;
    public String firstItemName = null;
    public boolean firstIsFolder = false;

    public void AddItem(boolean z, UUID uuid, String str) {
        if (z) {
            this.newFolderCount++;
        } else {
            this.newItemCount++;
        }
        if (this.firstParentUUID == null) {
            this.firstIsFolder = z;
            this.firstParentUUID = uuid;
            this.firstItemName = str;
        } else if (z && (!this.firstIsFolder)) {
            this.firstIsFolder = z;
            this.firstParentUUID = uuid;
            this.firstItemName = str;
        }
    }

    public boolean isEmpty() {
        return this.newFolderCount == 0 && this.newItemCount == 0;
    }
}
