# React-Linkpoint synchronization

The active application under `apps/linkpoint` incorporates the browser viewer
from `Kaleaon/React-Linkpoint`. The current import is based on upstream commit
`c1308fe272c3ed6dab5ec49b41755d8d244a1821`.

## Imported coverage

- The complete upstream Vitest suite lives under `apps/linkpoint/src`, including
  the viewer, screen, offline-grid, LLSD, and Google integration tests.
- Runtime modules needed by those tests are colocated under `src/linkpoint`,
  `src/services`, `core`, and `electron`.
- Viewer resources are stored in `public/anims`, `public/avatar`, `materials`,
  and `src/assets/windlight`. Binary viewer resources are checked in as base64 text and decoded at runtime so pull-request tooling can review every tracked file.
- Repository scanners and desktop/material helpers are under
  `apps/linkpoint/scripts`.

The root `npm run check:web` command type-checks, tests, and builds this imported
surface alongside the shared Linkpoint packages. Keep Linkpoint's
`ViewerClient` boundary in `App.tsx` and `src/viewer` when synchronizing future
upstream revisions.
