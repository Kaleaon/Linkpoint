package com.linkpoint.orm

import java.util.UUID
import javax.annotation.Nullable

final class AutoValue_InventoryQuery : InventoryQuery() {
    private val Int assetType
    private val String containsString
    private val UUID folderId
    private val Int folderType
    private val Boolean includeFolders
    private val Boolean includeItems
    private val Boolean newestFirst

    AutoValue_InventoryQuery(UUID uuid, String str, Boolean z, Boolean z2, Boolean z3, Int i, Int i2) {
        this.folderId = uuid
        this.containsString = str
        this.includeFolders = z
        this.includeItems = z2
        this.newestFirst = z3
        this.folderType = i
        this.assetType = i2
    }

    public Int assetType() {
        return this.assetType
    }

    public String containsString() {
        return this.containsString
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof InventoryQuery)) {
            return false
        }
        InventoryQuery inventoryQuery = (InventoryQuery) obj
        if (this.folderId != null ? this.folderId.equals(inventoryQuery.folderId()) : inventoryQuery.folderId() == null) {
            if (this.containsString != null ? this.containsString.equals(inventoryQuery.containsString()) : inventoryQuery.containsString() == null) {
                if (this.includeFolders == inventoryQuery.includeFolders() && this.includeItems == inventoryQuery.includeItems() && this.newestFirst == inventoryQuery.newestFirst() && this.folderType == inventoryQuery.folderType()) {
                    return this.assetType == inventoryQuery.assetType()
                }
            }
        }
        return false
    }

    public UUID folderId() {
        return this.folderId
    }

    public Int folderType() {
        return this.folderType
    }

    public Int hashCode() {
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

    public Boolean includeFolders() {
        return this.includeFolders
    }

    public Boolean includeItems() {
        return this.includeItems
    }

    public Boolean newestFirst() {
        return this.newestFirst
    }

    public String toString() {
        return "InventoryQuery{folderId=" + this.folderId + ", " + "containsString=" + this.containsString + ", " + "includeFolders=" + this.includeFolders + ", " + "includeItems=" + this.includeItems + ", " + "newestFirst=" + this.newestFirst + ", " + "folderType=" + this.folderType + ", " + "assetType=" + this.assetType + "}"
    }
}
