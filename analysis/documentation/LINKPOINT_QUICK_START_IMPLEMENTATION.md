# Linkpoint Quick Start Implementation Guide

## Overview

This guide provides step-by-step instructions for implementing the highest priority features in Linkpoint: Inventory, Objects, Chat, Avatar, and Voice (WebRTC).

---

## Phase 1: Inventory System (Priority 1)

### Step 1: Create Inventory Data Models

**File:** `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryFolder.kt`

```kotlin
package com.linkpoint.inventory

data class InventoryFolder(
    val folderID: UUID,
    val parentFolderID: UUID,
    val name: String,
    val type: FolderType,
    val version: Long = 0,
    val preferredType: Int = -1
)

enum class FolderType {
    ROOT,
    TEXTURE,
    SOUND,
    CALLING_CARD,
    LANDMARK,
    CLOTHING,
    OBJECT,
    NOTECARD,
    CATEGORY,
    ROOT_INVENTORY,
    TRASH,
    LOST_AND_FOUND,
    ANIMATION,
    GESTURE,
    SNAPSHOT,
    ATTACHMENT,
    WIDGET,
    ENSEMBLE,
    BODY_PART,
    MARKETPLACE_LISTING
}
```

**File:** `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryItem.kt`

```kotlin
package com.linkpoint.inventory

data class InventoryItem(
    val itemID: UUID,
    val assetID: UUID,
    val folderID: UUID,
    val name: String,
    val type: ItemType,
    val description: String = "",
    val permissions: Permissions = Permissions(),
    val saleInfo: SaleInfo? = null,
    val creationDate: Long = 0,
    val flags: Int = 0
)

data class Permissions(
    val ownerID: UUID = UUID.randomUUID(),
    val ownerMask: Int = 0,
    val groupMask: Int = 0,
    val everyoneMask: Int = 0,
    val nextOwnerMask: Int = 0
)

data class SaleInfo(
    val salePrice: Int = 0,
    val saleType: SaleType = SaleType.NOT
)

enum class ItemType {
    TEXTURE,
    SOUND,
    CALLING_CARD,
    LANDMARK,
    CLOTHING,
    OBJECT,
    NOTECARD,
    CATEGORY,
    ROOT_INVENTORY,
    SCRIPT,
    BODYPART,
    ANIMATION,
    GESTURE,
    SNAPSHOT,
    ATTACHMENT,
    WIDGET,
    ENSEMBLE,
    MARKETPLACE_LISTING,
    UNKNOWN
}

enum class SaleType {
    NOT,
    ORIGINAL,
    COPY,
    CONTENTS
}
```

### Step 2: Implement Inventory Manager

**File:** `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt`

```kotlin
package com.linkpoint.inventory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InventoryManager {
    private val folders = ConcurrentHashMap