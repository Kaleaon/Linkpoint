package com.linkpoint.orm

import java.util.UUID
import androidx.annotation.Nullable

class AutoValue_InventoryQuery : InventoryQuery {
    private Int assetType
    private String containsString
    private UUID folderId
    private Int folderType
    private Boolean includeFolders
    private Boolean includeItems
    private Boolean newestFirst

    AutoValue_InventoryQuery(@Nullable UUID uuid, @Nullable String str, Boolean z, Boolean z2, Boolean z3, Int i, Int i2) {
        this.folderId = uuid
        this.containsString = str
        this.includeFolders = z
        this.includeItems = z2
        this.newestFirst = z3
        this.folderType = i
        this.assetType = i2
    }

    fun assetType(): Int {
        return this.assetType
    }

    @Nullable
    fun containsString(): String {
        return this.containsString
    }

    fun equals(obj: Any): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof InventoryQuery)) {
            return false
        }
        InventoryQuery inventoryQuery = (InventoryQuery) obj
        if (this.folderId != null ? this.folderId == inventoryQuery.folderId() : inventoryQuery.folderId() == null) {
            if (this.containsString != null ? this.containsString == inventoryQuery.containsString() : inventoryQuery.containsString() == null) {
                if (this.includeFolders == inventoryQuery.includeFolders() && this.includeItems == inventoryQuery.includeItems() && this.newestFirst == inventoryQuery.newestFirst() && this.folderType == inventoryQuery.folderType()) {
                    return this.assetType == inventoryQuery.assetType()
                }
            }
        }
        return false
    }

    @Nullable
    fun folderId(): UUID {
        return this.folderId
    }

    fun folderType(): Int {
        return this.folderType
    }

    fun hashCode(): Int {
        Int i = 0
        Int i2 = 1231
        Int hashCode = ((this.folderId == null ? 0 : this.folderId.hashCode()) ^ 1000003) * 1000003
        if (this.containsString != null) {
            i = this.containsString.hashCode()
        }
        Int i3 = ((this.includeItems ? 1231 : 1237) ^ (((this.includeFolders ? 1231 : 1237) ^ ((hashCode ^ i) * 1000003)) * 1000003)) * 1000003
        if (!this.newestFirst) {
            i2 = 1237
        }
        return ((((i3 ^ i2) * 1000003) ^ this.folderType) * 1000003) ^ this.assetType
    }

    fun includeFolders(): Boolean {
        return this.includeFolders
    }

    fun includeItems(): Boolean {
        return this.includeItems
    }

    fun newestFirst(): Boolean {
        return this.newestFirst
    }

    fun toString(): String {
        return "InventoryQuery{folderId=" + this.folderId + ", " + "containsString=" + this.containsString + ", " + "includeFolders=" + this.includeFolders + ", " + "includeItems=" + this.includeItems + ", " + "newestFirst=" + this.newestFirst + ", " + "folderType=" + this.folderType + ", " + "assetType=" + this.assetType + "}"
    }
}
