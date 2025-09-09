package com.lumiyaviewer.lumiya.orm;

import java.util.UUID;
import javax.annotation.Nullable;

final class AutoValue_InventoryQuery extends InventoryQuery {
    private final int assetType;
    private final String containsString;
    private final UUID folderId;
    private final int folderType;
    private final boolean includeFolders;
    private final boolean includeItems;
    private final boolean newestFirst;

    AutoValue_InventoryQuery(@Nullable UUID uuid, @Nullable String str, boolean z, boolean z2, boolean z3, int i, int i2) {
        this.folderId = uuid;
        this.containsString = str;
        this.includeFolders = z;
        this.includeItems = z2;
        this.newestFirst = z3;
        this.folderType = i;
        this.assetType = i2;
    }

    public int assetType() {
        return this.assetType;
    }

    @Nullable
    public String containsString() {
        return this.containsString;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof InventoryQuery)) {
            return false;
        }
        InventoryQuery inventoryQuery = (InventoryQuery) obj;
        if (this.folderId != null ? this.folderId.equals(inventoryQuery.folderId()) : inventoryQuery.folderId() == null) {
            if (this.containsString != null ? this.containsString.equals(inventoryQuery.containsString()) : inventoryQuery.containsString() == null) {
                if (this.includeFolders == inventoryQuery.includeFolders() && this.includeItems == inventoryQuery.includeItems() && this.newestFirst == inventoryQuery.newestFirst() && this.folderType == inventoryQuery.folderType()) {
                    return this.assetType == inventoryQuery.assetType();
                }
            }
        }
        return false;
    }

    @Nullable
    public UUID folderId() {
        return this.folderId;
    }

    public int folderType() {
        return this.folderType;
    }

    public int hashCode() {
        int i = 0;
        int i2 = 1231;
        int hashCode = ((this.folderId == null ? 0 : this.folderId.hashCode()) ^ 1000003) * 1000003;
        if (this.containsString != null) {
            i = this.containsString.hashCode();
        }
        int i3 = ((this.includeItems ? 1231 : 1237) ^ (((this.includeFolders ? 1231 : 1237) ^ ((hashCode ^ i) * 1000003)) * 1000003)) * 1000003;
        if (!this.newestFirst) {
            i2 = 1237;
        }
        return ((((i3 ^ i2) * 1000003) ^ this.folderType) * 1000003) ^ this.assetType;
    }

    public boolean includeFolders() {
        return this.includeFolders;
    }

    public boolean includeItems() {
        return this.includeItems;
    }

    public boolean newestFirst() {
        return this.newestFirst;
    }

    public String toString() {
        return "InventoryQuery{folderId=" + this.folderId + ", " + "containsString=" + this.containsString + ", " + "includeFolders=" + this.includeFolders + ", " + "includeItems=" + this.includeItems + ", " + "newestFirst=" + this.newestFirst + ", " + "folderType=" + this.folderType + ", " + "assetType=" + this.assetType + "}";
    }
}
