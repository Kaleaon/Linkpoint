// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import javax.annotation.Nullable;
import java.util.UUID;

final class AutoValue_InventoryQuery extends InventoryQuery
{
    private final int assetType;
    private final String containsString;
    private final UUID folderId;
    private final int folderType;
    private final boolean includeFolders;
    private final boolean includeItems;
    private final boolean newestFirst;
    
    AutoValue_InventoryQuery(@Nullable final UUID folderId, @Nullable final String containsString, final boolean includeFolders, final boolean includeItems, final boolean newestFirst, final int folderType, final int assetType) {
        this.folderId = folderId;
        this.containsString = containsString;
        this.includeFolders = includeFolders;
        this.includeItems = includeItems;
        this.newestFirst = newestFirst;
        this.folderType = folderType;
        this.assetType = assetType;
    }
    
    @Override
    public int assetType() {
        return this.assetType;
    }
    
    @Nullable
    @Override
    public String containsString() {
        return this.containsString;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof InventoryQuery) {
            final InventoryQuery inventoryQuery = (InventoryQuery)o;
            if (this.folderId == null) {
                if (inventoryQuery.folderId() != null) {
                    return false;
                }
            }
            else if (!this.folderId.equals(inventoryQuery.folderId())) {
                return false;
            }
            if (this.containsString == null) {
                if (inventoryQuery.containsString() != null) {
                    return false;
                }
            }
            else if (!this.containsString.equals(inventoryQuery.containsString())) {
                return false;
            }
            if (this.includeFolders != inventoryQuery.includeFolders() || this.includeItems != inventoryQuery.includeItems() || this.newestFirst != inventoryQuery.newestFirst() || this.folderType != inventoryQuery.folderType()) {
                return false;
            }
            if (this.assetType != inventoryQuery.assetType()) {
                b = false;
            }
            return b;
            b = false;
            return b;
        }
        return false;
    }
    
    @Nullable
    @Override
    public UUID folderId() {
        return this.folderId;
    }
    
    @Override
    public int folderType() {
        return this.folderType;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int n = 1231;
        int hashCode2;
        if (this.folderId == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = this.folderId.hashCode();
        }
        if (this.containsString != null) {
            hashCode = this.containsString.hashCode();
        }
        int n2;
        if (this.includeFolders) {
            n2 = 1231;
        }
        else {
            n2 = 1237;
        }
        int n3;
        if (this.includeItems) {
            n3 = 1231;
        }
        else {
            n3 = 1237;
        }
        if (!this.newestFirst) {
            n = 1237;
        }
        return (((n3 ^ (n2 ^ ((hashCode2 ^ 0xF4243) * 1000003 ^ hashCode) * 1000003) * 1000003) * 1000003 ^ n) * 1000003 ^ this.folderType) * 1000003 ^ this.assetType;
    }
    
    @Override
    public boolean includeFolders() {
        return this.includeFolders;
    }
    
    @Override
    public boolean includeItems() {
        return this.includeItems;
    }
    
    @Override
    public boolean newestFirst() {
        return this.newestFirst;
    }
    
    @Override
    public String toString() {
        return "InventoryQuery{folderId=" + this.folderId + ", " + "containsString=" + this.containsString + ", " + "includeFolders=" + this.includeFolders + ", " + "includeItems=" + this.includeItems + ", " + "newestFirst=" + this.newestFirst + ", " + "folderType=" + this.folderType + ", " + "assetType=" + this.assetType + "}";
    }
}
