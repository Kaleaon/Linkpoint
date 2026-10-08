# Segment 04 — Protocol Correctness (Identity Blocks, LLSD, Conformance)

**Priority:** Critical for any mutating operation. Operations that "send" but
silently fail are worse than ones that error visibly.

This segment expands item #1 from the original worklist (placeholder
agent/session/group IDs) and adds LLSD-correctness items that come from the
Lumiya-Redux conformance doc.

---

## 1. AgentData / AgentID / SessionID block

Every authoritative message in the SL protocol carries an `AgentData` block
(or equivalent) with at minimum `AgentID` (UUID) and `SessionID` (UUID).
The sim **discards messages with a mismatched or null SessionID** without
notifying the client. This is the most common cause of "looks like it sent
but nothing happened" bugs.

### 1.1 Lumiya pattern

In every Lumiya message handler, the agent/session IDs are pulled from the
circuit, not from a viewer-wide singleton:

```java
// SLAgentCircuit, e.g. line 1828-1829
teleportLocationRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
teleportLocationRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
```

Reasoning: if a teleport drops you onto a different sim, the new circuit has
a different `circuitInfo`. Pulling from a global agent singleton can race
with teleport handoff and pack stale IDs.

### 1.2 Linkpoint audit

Files flagged in the original worklist:

- `ObjectManager` — multiple object operations write zeroed `AgentData`
- `ParcelManager` — placeholder IDs/strings/transaction values

Audit task: grep for `UUID.randomUUID()`, `UUID(0L, 0L)`, `EMPTY_UUID`, and
`UUID.fromString("00000000-…")` across `protocol/`, `objects/`, `parcels/`,
`groups/`, `inventory/`, `economy/`, `chat/`, `messaging/`. Every hit in a
*sent* message is a bug.

---

## 2. Other identity blocks to verify

| Block | Used in | Source of truth |
|---|---|---|
| `GroupID` | group operations, deeded parcels, group IM, money transfers to groups | `circuitInfo.activeGroup` (set by `ActivateGroup`) |
| `TransactionID` | money transfers, inventory create | fresh `UUID.randomUUID()` per transaction; record in pending-tx map |
| `CircuitCode` | `UseCircuitCode`, `AgentThrottle`, `ChildAgentUpdate*` | `circuitInfo.circuitCode` |
| `ObjectLocalID` (uint32) | `ObjectGrab`, `ObjectSelect`, `ObjectName`, `MultipleObjectUpdate` | `ObjectManager` per-region cache; **resets across regions** |
| `RegionHandle` (uint64) | teleport, object property family, parcel info | `circuitInfo.regionHandle` |
| `ParcelLocalID` | parcel mutators | `ParcelManager` per-region cache |

---

## 3. LLSD round-trip correctness

From `Lumiya-Redux/docs/protocol_migration_conformance.md` (verbatim):

- **Node type distinctions**: undefined ≠ empty; scalar and container types
  must remain semantically distinct.
- **Streaming parse ordering**: preserve map and array order as emitted.
- **Round-trip lossless encoding**: UUIDs, binary blobs, and ISO8601 dates
  must survive serialization-deserialization without loss.
- **Format determinism**: XML/binary/notation selection must be explicit.

### 3.1 Test fixtures

Lumiya-Redux ships fixtures under `recovered/reference/`. Linkpoint should
mirror them under `src/test/resources/protocol/llsd/` and run round-trip
tests on every PR touching `slproto/llsd/`.

### 3.2 Pitfalls observed in Java/Kotlin LLSD ports

| Pitfall | Symptom | Fix |
|---|---|---|
| Using `Map<String, Object>` for LLSD maps | iteration order non-deterministic | use `LinkedHashMap` |
| Treating empty string as undefined | downstream type confusion | distinct `Undef` sentinel |
| Java `Date` for ISO8601 | timezone loss on round-trip | use `Instant`, serialize with `DateTimeFormatter.ISO_INSTANT` |
| `UUID.toString().replace("-","")` in some places | inconsistent serialization | always full hyphenated form for LLSD XML, raw bytes for binary |
| `Base64` with line breaks in binary blobs | parser breakage | use `Base64.NO_WRAP` |

---

## 4. Inventory model invariants (from conformance doc)

For inventory:

- `item_id`, `parent_id`, `inv_type`, `asset_type` must match upstream byte
  layout exactly.
- `base_mask`, `owner_mask`, `group_mask`, `everyone_mask`, `next_mask`
  preserved on every read/write.
- `sale_type`, `sale_price` round-trip cleanly.
- Both UDP path (`FetchInventoryReply`, `InventoryDescendents`,
  `BulkUpdateInventory`) and HTTP cap path (`FetchInventory2`,
  `FetchInventoryDescendents2`, `FetchLib2`) **must produce equivalent
  inventory objects** for the same source content. Add a parity test that
  fetches one folder via both paths and asserts equality.

---

## 5. Concrete work items

| ID | Item |
|---|---|
| L04-A | Sweep `ObjectManager` and replace every placeholder `AgentData` with `circuitInfo.agentID` / `circuitInfo.sessionID` |
| L04-B | Sweep `ParcelManager` for placeholder IDs / strings / transaction values |
| L04-C | Sweep `groups/`, `economy/`, `inventory/`, `chat/`, `messaging/` for `UUID.randomUUID()` calls in send paths; document each one as either "fresh tx ID" or "bug" |
| L04-D | Add a lint-style test that fails if any sent message contains zero UUIDs in `AgentData_Field.AgentID` or `AgentData_Field.SessionID` |
| L04-E | Mirror Lumiya-Redux LLSD test fixtures under `src/test/resources/protocol/llsd/`; add round-trip tests for each format |
| L04-F | Inventory dual-path equivalence test (segment 07 owns the action item; correctness lives here) |
| L04-G | Document any deliberate deviations in `tools/protocol/message_template_mismatches.txt` |

---

## 6. Cross-references

- Segment 01 — reliable packets (a reliable message with a wrong SessionID
  is silently ACKed by the sim then ignored — appears to succeed)
- Segment 03 — message parity
- Segment 07 — inventory dual-path
