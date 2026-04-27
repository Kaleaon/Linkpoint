# Segment 12 — Persistence (DAO/ORM) and Eventing

**Priority:** Medium. Two cross-cutting concerns: how state lives between
sessions, and how subsystems talk to each other.

References: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/dao/`,
`com/lumiyaviewer/lumiya/orm/`, `com/lumiyaviewer/lumiya/eventbus/`,
`com/lumiyaviewer/lumiya/react/`.

---

## 1. Persistence

### 1.1 Lumiya's DAOs (greenDAO 2.x)

| Entity | Purpose |
|---|---|
| `ChatMessage` | local chat history |
| `Chatter` | known avatars (UUID, name, last-seen) |
| `Friend` | friends list with rights |
| `User` | account record |
| `UserName` | UUID → legacy username cache |
| `UserPic` | avatar thumbnail cache |
| `CachedAsset` | asset metadata (UUID, type, size, last-fetched) |
| `CachedResponse` | HTTP response cache |
| `MoneyTransaction` | L$ tx history |
| `MuteListCachedData` | mute list |
| `SearchGridResult` | search result cache |
| `GroupMember`, `GroupRoleMember` | group membership |
| `InventoryEntry` | (via `InventoryDB`) per-folder/item |

`InventoryDB` does bulk batched writes (`MAX_UPDATES_PER_TRANSACTION = 16`).

### 1.2 Linkpoint direction

Lumiya-Redux is migrating greenDAO → Room. Linkpoint should already be on
Room or another modern persistence. Audit:

| ID | Item |
|---|---|
| L12-A | Verify all 13 entity types have a Linkpoint analogue |
| L12-B | Disk asset cache (segment 06's L06-O) — must persist across launches |
| L12-C | Schema migrations: when bumping a DB version, ship a migration; never wipe-on-upgrade |
| L12-D | Bulk write transactions for inventory descent (Lumiya batches at 16 updates/tx) |

### 1.3 Schema correctness

From segment 04 §4: inventory persistence must preserve permission masks,
sale type, sale price, asset/inv types, parent/item identity. Audit each
column type against the protocol byte layout.

---

## 2. Eventing

### 2.1 Lumiya's EventBus

`eventbus/EventBus` wraps Guava's `EventBus`. Pattern:

- Publishers call `eventBus.publish(event)`.
- Subscribers register with `@EventHandler`-annotated methods.
- `EventRateLimiter` debounces high-frequency events.
- Single-threaded delivery semantics — all handlers run on the event-bus
  thread.

Major events:

| Event | Source | Subscribers |
|---|---|---|
| `SLConnectionStateChangedEvent` | `SLGridConnection` | UI status indicator, service notification |
| `SLLoginResultEvent` | `SLGridConnection` | login screen, world view |
| `SLDisconnectEvent` | `SLGridConnection` | UI alert |
| `SLReconnectingEvent(attempt)` | `SLGridConnection.Reconnect()` | UI "Reconnecting…" pill |
| `SLChatTextEvent` (and all subclasses) | chat handlers | chat UI |
| `SLTeleportResultEvent(success, msg)` | teleport request callbacks | UI |
| `SLConnectionStateChangedEvent` | state machine | UI |

### 2.2 Reactive subscription system (`react/`)

Distinct from `eventbus/`. Lumiya's `react/` is custom — not RxJava. Used
for **data subscriptions** (vs. discrete events):

- `SubscriptionData<K, V>` — observe per-key updates (e.g., per-avatar
  display name, per-object position)
- `SubscriptionSingleDataPool<V>` — observe a single value (e.g., agent's
  L$ balance, current parcel)
- `Subscribable`, `Subscription`, `Pool` — the primitives
- `UIThreadExecutor`, `AsyncRequestHandler`, `RequestHandler` — threading

This is the data-flow backbone for the Compose-equivalent UI in Lumiya.
Linkpoint with Compose has `Flow`/`StateFlow`/`SharedFlow`; the
**discipline** is what to copy: fine-grained per-key subscriptions, not
"observe the whole world".

| ID | Item |
|---|---|
| L12-E | Audit Linkpoint state flows: are they per-key (good) or whole-collection (bad)? |
| L12-F | Add rate-limiting to high-frequency event types (avatar position updates, parcel-overlay updates) |
| L12-G | Ensure background work uses dedicated dispatchers, not `Dispatchers.Main` |

---

## 3. Concrete work items

| ID | Item |
|---|---|
| L12-A through L12-G above | |
| L12-H | Diagnostics: surface DAO write counts, query counts, and event-bus event counts in the debug report |
| L12-I | Debug toggle: log every event-bus publish (volume can be huge — gate behind a setting) |

---

## 4. Cross-references

- Segment 01 — `SLReconnectingEvent` lives here
- Segment 04 — schema correctness for inventory/permissions
- Segment 06 — disk asset cache
- Segment 07 — inventory DAO
- Segment 08 — chat history DAO
