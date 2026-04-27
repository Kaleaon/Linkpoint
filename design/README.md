# Linkpoint 2.0 — Design Prototype

Interactive HTML prototype for the Linkpoint mobile client, covering 36 screens across 26 themes.

## Quick start

Open `Linkpoint 2.0.html` in any modern browser. No build step required — JSX is transpiled in-browser via Babel Standalone.

Use the **tweaks panel** (gear icon, or send `__activate_edit_mode` postMessage) to switch themes, density, and accent colors live.

## File map

| File | Role |
|------|------|
| `Linkpoint 2.0.html` | Entry point — loads all scripts, defines artboard layout |
| `styles.css` | All CSS custom-property theme tokens (26 themes) + component styles |
| `primitives.jsx` | Shared atoms: `Avatar`, `StatusBar`, `NavPill`, `TopBar`, `IconBtn`, icons |
| `design-canvas.jsx` | Pan/zoom canvas, `DCSection`, `DCArtboard`, `DCPostIt` |
| `tweaks-panel.jsx` | Live theme/density/accent controls |
| `android-frame.jsx` | Phone shell frame used by artboards |
| `screens-1.jsx` | Home, World Map, Chat List, Chat Detail, Inventory |
| `screens-2.jsx` | Explore, Notifications, Settings, Search, World Detail |
| `screens-3.jsx` | Marketplace, Build Mode, Friends List, Group Detail, Events |
| `screens-4.jsx` | Radar, Mini-map, Economy, Script Editor, Voice |
| `screens-5.jsx` | Profile, Media, Texture Preview |
| `screens-states.jsx` | Loading, Error, Empty, Onboarding, Permissions screens |
| `screens-extra.jsx` | Avatar editor, Picker sheet, Gesture tutorial + additional screens |

## Theme → Android mapping

Theme tokens in `styles.css` follow the same naming convention as the KTheme system in `Linkpoint/src/main/appres/values/themes.xml`. Each `[data-theme="X"]` block corresponds to a `Theme.Linkpoint.X` Material3 theme variant.

Community theme definitions for the new themes live in `../ktheme-pr/themes/community/`.

## Notable themes

- **lcars** — Authentic TNG LCARS chrome: full-height orange left rail, Antonio font, bottom elbow connector
- **metro** — Windows Phone 7 / Zune panorama: Segoe UI weight 100, flat tiles, underline inputs
- **stargate-atlantis**, **stargate-sg1** — defined in `ktheme-pr/themes/community/`
