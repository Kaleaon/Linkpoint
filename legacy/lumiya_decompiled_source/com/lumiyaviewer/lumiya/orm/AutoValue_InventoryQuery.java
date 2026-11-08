package com.lumiyaviewer.lumiya.orm;

import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
final class AutoValue_InventoryQuery extends InventoryQuery {
    private final int assetType;
    private final String containsString;
    private final UUID folderId;
    private final int folderType;
    private final boolean includeFolders;
    private final boolean includeItems;
    private final boolean newestFirst;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_InventoryQuery(@Nullable UUID uuid, @Nullable String str, boolean z, boolean z2, boolean z3, int i, int i2) {
        this.folderId = uuid;
        this.containsString = str;
        this.includeFolders = z;
        this.includeItems = z2;
        this.newestFirst = z3;
        this.folderType = i;
        this.assetType = i2;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    public int assetType() {
        return this.assetType;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    @Nullable
    public String containsString() {
        return this.containsString;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InventoryQuery) {
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
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    @Nullable
    public UUID folderId() {
        return this.folderId;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    public int folderType() {
        return this.folderType;
    }

    public int hashCode() {
        return (((((((this.includeItems ? 1231 : 1237) ^ (((this.includeFolders ? 1231 : 1237) ^ (((((this.folderId == null ? 0 : this.folderId.hashCode()) ^ 1000003) * 1000003) ^ (this.containsString != null ? this.containsString.hashCode() : 0)) * 1000003)) * 1000003)) * 1000003) ^ (this.newestFirst ? 1231 : 1237)) * 1000003) ^ this.folderType) * 1000003) ^ this.assetType;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    public boolean includeFolders() {
        return this.includeFolders;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    public boolean includeItems() {
        return this.includeItems;
    }

    @Override // com.lumiyaviewer.lumiya.orm.InventoryQuery
    public boolean newestFirst() {
        return this.newestFirst;
    }

    public String toString() {
        return "InventoryQuery{folderId=" + this.folderId + ", containsString=" + this.containsString + ", includeFolders=" + this.includeFolders + ", includeItems=" + this.includeItems + ", newestFirst=" + this.newestFirst + ", folderType=" + this.folderType + ", assetType=" + this.assetType + "}";
    }
}
