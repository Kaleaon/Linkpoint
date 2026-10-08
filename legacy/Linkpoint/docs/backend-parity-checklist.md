# Backend Parity Checklist

This checklist defines **must-pass scenarios** for render backend parity validation (Filament and Lumiya).

## Pass Criteria

- Each scenario is executed on both backends.
- No crash, ANR, or frozen frame loop.
- World remains responsive to camera/movement input after scenario completion.
- Diagnostics show stable frame progression and no unbounded restart loops.

## Must-Pass Scenarios

- [ ] **Login spawn**
  - Fresh login, initial region handshake, world appears with terrain + nearby content.

- [ ] **Teleport**
  - Teleport between regions (different simulator), verify scene teardown/reload and resumed rendering.

- [ ] **Crowded region**
  - Enter a high-avatar/high-prim region and verify avatar + object rendering continuity.

- [ ] **Mesh-heavy region**
  - Visit a mesh-dense location and validate geometry/material streaming without renderer instability.

- [ ] **HUD overlay**
  - Enable visible HUD elements, verify overlay rendering and interaction while world continues updating.

- [ ] **Panel open/close**
  - Repeatedly open/close in-world panels (inventory/chat/settings overlays) and confirm draw pause/resume behavior is correct.

- [ ] **Background/foreground cycle**
  - Send app to background, return to foreground, verify swapchain/context recovery and continuous rendering.

## Suggested Evidence to Capture Per Scenario

- Active backend and frame-time percentiles from diagnostics.
- Visible entity counts (avatars/prims/terrain patches).
- Texture success/failure rates.
- Swapchain/context restart counters before/after scenario.
