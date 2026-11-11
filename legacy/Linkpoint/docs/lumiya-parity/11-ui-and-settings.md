# Segment 11 — UI Surface & Settings

**Priority:** Medium. Lumiya ships 17 Activities, 469 XML layouts, and 11
preference screens. Linkpoint is broadly Compose-first and has more
flexibility, but the **surface coverage** must be at parity for Lumiya
users to feel at home.

Reference: `lumiya_extracted/AndroidManifest.xml`, `lumiya_extracted/res/`,
`lumiya_decompiled_source/com/lumiyaviewer/lumiya/ui/`.

---

## 1. Activities Lumiya declares

| Activity | Purpose |
|---|---|
| `LoginActivity` | XMLRPC login UI |
| `TOSActivity` | Terms-of-service acceptance |
| `WorldViewActivity` | 3D scene |
| `CardboardActivity` + `CardboardTransitionActivity` | VR stereo |
| `MinimapActivity` | 2D region map |
| `ChatNewActivity` | local + IM + group chat tabs |
| `InventoryActivity` | inventory browser |
| `ObjectListNewActivity` | in-world object list |
| `NotecardEditActivity` | read/write notecards |
| `SearchGridActivity` | grid search |
| `MyAvatarActivity` | avatar editor |
| `StreamingMediaActivity` | parcel music player |
| `ManageGridsActivity` | OpenSim + LL grids |
| `ManageAccountsActivity` | alts |
| `SettingsActivity` | settings root |
| `WhatsNewActivity` | release notes |
| `TeleportSLURLActivity` | `secondlife://` deep-link handler |

Linkpoint already has equivalents for most. The audit is: for each Lumiya
activity, identify the Linkpoint screen that subsumes it, or note that we
don't ship it yet.

---

## 2. Settings panels

Lumiya ships 11 preference XML files. Linkpoint should mirror the same
groupings:

| Lumiya panel | Settings |
|---|---|
| `preferences_chat` | timestamps, legacy names, voice on/off, auto-response |
| `preferences_3d` | quality, draw distance |
| `preferences_3d_advanced` | FXAA, cloud rendering, fixed-daylight hour |
| `preferences_3d_perf` | mesh LOD, texture quality, hover-text toggles |
| `preferences_appearance` | theme, font scale |
| `preferences_connection` | auto-reconnect, max reconnect attempts, **bandwidth budget**, **timeout multiplier** |
| `preferences_notifications` | master notifications enable |
| `preferences_notifications_group` | group-IM notifications |
| `preferences_notifications_im` | private IM notifications |
| `preferences_notifications_local` | local-chat notifications |
| `preferences_cache` | location, max size, clear |
| `preferences_rlv` | RLV enable, version reply text |

### 2.1 Settings expansions Linkpoint should make

(These don't exist in Lumiya 3.4.2 but are warranted for cellular):

- `preferences_connection`:
  - "Bandwidth budget" — sets the `AgentThrottle` total
  - "Adaptive bandwidth on cellular" — auto-reduces budget when network is poor
  - "Receive-idle threshold" (advanced; default 10 s)
  - "Reconnect pre-sleep" (advanced; default 3 s)
  - "Pause asset fetches when poor network" toggle
- `preferences_3d_perf`:
  - "Texture memory budget" (default 64 MB) — segment 05 / 06
  - "Mesh memory budget"
- `preferences_notifications`:
  - "Show 'Reconnecting…' notification during recovery"

---

## 3. Deep links

Lumiya `TeleportSLURLActivity` handles:

- `secondlife://Region%20Name/x/y/z` — direct teleport
- `http://maps.secondlife.com/secondlife/Region/x/y/z` — web SLURL
- `http://` (other) — open in external browser

| ID | Item |
|---|---|
| L11-A | Linkpoint declares both intent filters |
| L11-B | URL parser handles encoded region names, optional coordinates, optional SLURL flags |
| L11-C | If not logged in: route to `LoginActivity` with the SLURL stashed for post-login auto-teleport |
| L11-D | If logged in to a different grid: warn before re-login |

---

## 4. Notification channels (Android 8+)

Lumiya predates this requirement; Linkpoint must implement properly:

| Channel | Importance | Purpose |
|---|---|---|
| `connection` | LOW | persistent foreground-service notification ("Connected to <Region>") |
| `chat_local` | DEFAULT | local-chat mentions |
| `chat_im` | HIGH | private IM (if user opted in) |
| `chat_group` | DEFAULT | group IM |
| `friends` | DEFAULT | online/offline notifications |
| `inventory_offer` | HIGH | item offers |
| `voice_call` | HIGH (with sound) | incoming voice |

Per-channel: sound, LED color, vibration pattern. Lumiya has these in
`GridConnectionService`; map each `NotificationType` enum value to a
channel.

---

## 5. Multi-account UI

Lumiya supports multiple simultaneous logins via
`UserManager.getUserManager(uuid)`. UI is `ManageAccountsActivity`.
Linkpoint's debug report shows a single connection — verify multi-account
is wired before promising it.

| ID | Item |
|---|---|
| L11-E | Account-list UI |
| L11-F | Swap "active" account in WorldView; the rest stay logged in in the background |
| L11-G | Per-account chat/IM/inventory state isolation |

---

## 6. Multi-grid UI

`ManageGridsActivity`: list of grids with login URI, helper URI, name. SL
agni and aditi presets, plus user-added OpenSim grids.

| ID | Item |
|---|---|
| L11-H | Grid manager UI matches Lumiya's affordances |
| L11-I | Per-grid login defaults persist |
| L11-J | TOS acceptance per grid |

---

## 7. Concrete work items

See L11-A through L11-J above. The largest gap is likely the **settings
panel granularity** — many of these panels exist in Linkpoint as a single
flat screen.

---

## 8. Cross-references

- Segment 02 — settings entries for cellular tuning
- Segment 09 — voice-related settings
- Segment 10 — RLV settings
