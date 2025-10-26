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

     public fun assetType(): Int {
        return this.assetType
    }

     public fun containsString(): String {
        return this.containsString
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof InventoryQuery)) {
            return false
        }
        val inventoryQuery: InventoryQuery = (InventoryQuery) obj
        if (this.folderId != null ? this.folderId.equals(inventoryQuery.folderId()) : inventoryQuery.folderId() == null) {
            if (this.containsString != null ? this.containsString.equals(inventoryQuery.containsString()) : inventoryQuery.containsString() == null) {
                if (this.includeFolders == inventoryQuery.includeFolders() && this.includeItems == inventoryQuery.includeItems() && this.newestFirst == inventoryQuery.newestFirst() && this.folderType == inventoryQuery.folderType()) {
                    return this.assetType == inventoryQuery.assetType()
                }
            }
        }
        return false
    }

     public fun folderId(): UUID {
        return this.folderId
    }

     public fun folderType(): Int {
        return this.folderType
    }

     public override fun hashCode(): Int {
        val i: Int = 0
        val i2: Int = 1231
        val hashCode: Int = ((this.folderId == null ? 0 : this.folderId.hashCode()) ^ 1000003) * 1000003
        if (this.containsString != null) {
            i = this.containsString.hashCode()
        }
        val i3: Int = ((this.includeItems ? 1231 : 1237) ^ (((this.includeFolders ? 1231 : 1237) ^ ((hashCode ^ i) * 1000003)) * 1000003)) * 1000003
        if (!this.newestFirst) {
            i2 = 1237
        }
        return ((((i3 ^ i2) * 1000003) ^ this.folderType) * 1000003) ^ this.assetType
    }

     public fun includeFolders(): Boolean {
        return this.includeFolders
    }

     public fun includeItems(): Boolean {
        return this.includeItems
    }

     public fun newestFirst(): Boolean {
        return this.newestFirst
    }

     public override fun toString(): String {
        return "InventoryQuery{folderId=" + this.folderId + ", " + "containsString=" + this.containsString + ", " + "includeFolders=" + this.includeFolders + ", " + "includeItems=" + this.includeItems + ", " + "newestFirst=" + this.newestFirst + ", " + "folderType=" + this.folderType + ", " + "assetType=" + this.assetType + "}"
    }
}
