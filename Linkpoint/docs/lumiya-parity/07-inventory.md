# Segment 07 — Inventory

**Priority:** High. The 2026-04-25 debug report shows
`Folders Cached: 5479 / Items Cached: 0` — the folder hierarchy fetched but
the per-folder item descent is broken. This breaks every flow that depends
on a wearable, a landmark, or a notecard.

References: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/`,
`slproto/modules/inventory/`, `dao/InventoryEntry*`,
`orm/InventoryDB.java`, `orm/InventoryEntryList.java`.

---

## 1. The dual-path model

Lumiya fetches inventory via two parallel paths. Both must produce
**byte-for-byte equivalent** in-memory representations (Lumiya-Redux
conformance requirement).

| Path | Trigger | Lumiya owner |
|---|---|---|
| HTTP cap | preferred — `FetchInventory2`, `FetchInventoryDescendents2`, `FetchLib2` | `slproto/modules/inventory/` HTTP fetcher |
| UDP | fallback — `FetchInventoryReply`, `InventoryDescendents`, `BulkUpdateInventory` | `slproto/messages/Fetch*`, `Inventory*` |

The debug report's running session has all three caps available
(`FetchInventory2`, `FetchInventoryDescendents2`, `FetchLib2`) — so the cap
path should be working. Yet 0 items.

---

## 2. Folder ↔ item invariants

`InventoryEntry` (and its DAO) carries:

- `item_id` (UUID) — primary key for items
- `folder_id` (UUID) — primary key for folders; same UUID space
- `parent_id` (UUID) — points to containing folder
- `inv_type` (int) — semantic type (texture / sound / landmark / notecard /
  …)
- `asset_type` (int) — backing asset type
- `name`, `description`
- `permissions`: `base_mask`, `owner_mask`, `group_mask`, `everyone_mask`,
  `next_mask` (uint32 each)
- `sale_type`, `sale_price`
- `creation_date`
- `creator_id`, `owner_id`, `last_owner_id`
- `flags`

The 13 system folders (Textures, Sounds, Animations, Landmarks, Notecards,
Scripts, Calling Cards, Gestures, Body Parts, Clothing, Objects, Trash,
Lost and Found, Outfits) have well-known asset types and are auto-created
by the sim. Linkpoint reports `System Folders: 25`, which is plausible for
modern SL (BOM-era plus per-account specials).

---

## 3. Likely causes for "0 items" given 5479 folders

Investigation order:

1. Are descendents actually being fetched? `Currently Loading: false` says
   no fetch in flight. Either the fetch was never triggered, or it
   completed without populating items.
2. Is the cap-path response being parsed? Add a log at the LLSD-decode site
   that counts items per response.
3. Is the DAO `put()` being called for items? Add a log at the persistence
   write site.
4. Is there a schema mismatch — items written to a different table or
   column set?
5. Is the in-memory cache being read from the DAO, or only from the
   sim-fetched live stream? If the DAO write fails silently, the cache
   stays empty.

| ID | Item |
|---|---|
| L07-A | Add per-response item count logging on cap-path fetcher |
| L07-B | Add per-write item count logging on DAO write |
| L07-C | Add a pre-flight check at app startup: "if folders >0 and items =0 for any non-empty folder, force a refetch" |
| L07-D | UI never shows "0 items" without first attempting a fetch |

---

## 4. Fetch ordering and lazy loading

Lumiya fetches the folder tree shallowly first (root + system folders),
then descends on user demand:

1. Login → fetch root folder skeleton
2. User opens "Clothing" → fetch its descendents
3. User opens an outfit → fetch *its* descendents
4. Specific items needed for a wearable bake → fetch by item-id

Don't try to pull every item on login — a heavy-inventory user has 50k+
items. The 5479 folders alone took meaningful time on this session.

| ID | Item |
|---|---|
| L07-E | Lazy-load policy: descend a folder only when a UI surface requests it OR when a wearable bake needs it |
| L07-F | Background prefetch for "currently worn" outfit + Trash + Inbox |
| L07-G | Cache invalidation: server pushes `BulkUpdateInventory`; consume it and update the in-memory + DAO entries |

---

## 5. Inventory mutation operations

| Operation | UDP | Cap |
|---|---|---|
| Move item | `MoveInventoryItem` | (cap-only on modern SL) |
| Copy item | `CopyInventoryItem` | n/a |
| Remove item | `RemoveInventoryItem` | n/a |
| Update item | `UpdateInventoryItem` | n/a |
| Update create item | `UpdateCreateInventoryItem` | (server pushes after create) |
| Create folder | `CreateInventoryFolder` | n/a |
| Remove folder | `RemoveInventoryFolder` | n/a |

All reliable. All carry full `AgentData` (segment 04).

---

## 6. Wearables and outfits

Lumiya `slproto/avatar/` plus `OutfitManager` UI:

- Outfit = folder under "Outfits"
- "Wear outfit" = remove all current attachments + body parts + clothing,
  then attach/wear each item in the outfit folder
- Pair: `AgentIsNowWearing` (reliable) → server bake response →
  `AvatarAppearance`

Original worklist item #6 flagged a placeholder return path in
`OutfitManager`. That fix lives here.

| ID | Item |
|---|---|
| L07-H | `OutfitManager` returns authoritative simulator state, not a placeholder |
| L07-I | "Wear outfit" workflow exercises every step end-to-end with reliable messages |
| L07-J | BOM body parts visible in the outfit editor |

---

## 7. Concrete work items

| ID | Item |
|---|---|
| L07-A through L07-J above | |
| L07-K | Dual-path equivalence test (HTTP vs UDP fetch produces identical `InventoryEntry`) — covers segment 04's L04-F |
| L07-L | Persistence schema audit: verify DAO column types match the protocol semantics in segment 04 §4 |

---

## 8. Cross-references

- Segment 04 — protocol correctness (LLSD round-trip, permission masks)
- Segment 06 — assets pipeline (item.assetID → texture / mesh / sound / etc.)
- Segment 12 — persistence (DAO migration to Room)
