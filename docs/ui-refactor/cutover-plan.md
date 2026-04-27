# UI Refactor Cutover Plan

Target milestone: **`2026.09`** (Compose-first UI cutover).

This plan tracks when each legacy Activity/Fragment/Adapter can be removed, and what evidence is required before deleting legacy wiring.

## 1) Legacy Activity / Adapter removal conditions

| Legacy component | Compose parity required before removal | Additional removal condition |
|---|---|---|
| `InventoryActivity` + `InventoryFragment` + `InventoryAdapter` | `InventoryScreen` supports folder drill-down, item details, notecard open, landmark teleport flow | No references to `R.layout.activity_inventory` or fragment container remain |
| `FriendsActivity` + `FriendsListFragment` + `FriendsAdapter` | `FriendsScreen` supports list rendering, online state, profile/actions dialogs | No references to `R.layout.activity_friends` remain |
| `GroupsActivity` + `GroupsAdapter` | `GroupsScreen` supports list rendering + group actions parity | No references to `R.layout.activity_groups` remain |
| `NearbyPeopleActivity` + `NearbyPeopleFragment` + `NearbyPeopleAdapter` | `NearbyPeopleScreen` supports nearby list + actions parity | No references to `R.layout.activity_nearby_people` remain |
| `ChatActivity` + `ChatAdapter` | `ChatScreen` supports message history, send flow, typing presence parity | No references to `R.layout.activity_chat` remain |
| `RadarActivity` + `RadarView` | `RadarCompose` supports range, heading, blip list parity | No references to `R.layout.activity_radar` and `item_radar_blip` remain |
| `MapActivity` | `MapScreen` supports zoom, pan, search, home/my-location parity | No references to `R.layout.activity_map` remain |
| `SearchActivity` | `SearchScreen` supports people/place/group tabs + query actions parity | No references to `R.layout.activity_search` remain |

## 2) Legacy entry-point deprecation policy

For every legacy Activity/Fragment/Adapter still in service:

1. Add `@Deprecated` with explicit replacement (`ReplaceWith(...)`) when possible.
2. Add target removal milestone in class KDoc (`Removal target: 2026.09`).
3. Block new callsites from being added after deprecation (CI static check).

## 3) Dead navigation/resource cleanup (performed incrementally)

Completed in this iteration:

- Removed the unused Build placeholder navigation entry from the manifest.
- Removed unused Build placeholder resources:
  - `activity_build.xml`
  - `panel_build_content.xml`
  - `panel_build_features.xml`
  - `panel_build_general.xml`
  - `panel_build_object.xml`
  - `panel_build_texture.xml`

## 4) Static search workflow for unused legacy layouts/adapters

Run this before each cleanup PR:

```bash
python - <<'PY'
import os,re,glob
layout_dir='Linkpoint/src/main/res/layout'
all_layouts=[os.path.splitext(os.path.basename(f))[0] for f in glob.glob(layout_dir+'/*.xml')]
refs=set()
for root,_,files in os.walk('Linkpoint/src/main'):
    for fn in files:
        if fn.endswith(('.kt','.java','.xml')):
            p=os.path.join(root,fn)
            txt=open(p,errors='ignore').read()
            refs.update(re.findall(r'R\\.layout\\.([A-Za-z0-9_]+)',txt))
            refs.update(re.findall(r'@layout/([A-Za-z0-9_]+)',txt))
for name in sorted(n for n in all_layouts if n not in refs):
    print(name)
PY
```

Then verify each candidate with `rg -n "<name>" Linkpoint/src/main`.

## 5) Bridge-flag collapse and Compose default

- Bridge strategy is now **Compose-first**.
- Legacy Activity bridge remains only for temporary compatibility while parity is closed.
- No new UI feature flags should be added for View-based entry points; all new navigation should be Compose route driven.
