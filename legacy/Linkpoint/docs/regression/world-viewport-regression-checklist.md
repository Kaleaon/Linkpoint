# World Viewport Regression Checklist

## Renderer context loss
- [ ] Enter world and confirm the renderer draws first frame within 3 seconds.
- [ ] Trigger background/foreground app transition twice and verify world rendering resumes.
- [ ] While connected, force GPU context loss path (developer option or renderer toggle) and verify camera state is restored after handoff.

## Surface recreation
- [ ] Lock/unlock screen while world is active and confirm no black surface remains.
- [ ] Switch renderer backend preference and verify old surface is disposed before new surface attaches.
- [ ] Confirm HUD + overlays remain visible and interactable after surface recreation.

## Orientation changes
- [ ] Rotate portrait -> landscape -> portrait and verify world surface resizes correctly each time.
- [ ] Confirm joystick, minimap, action stack, and HUD remain in expected layer order after each rotation.
- [ ] Validate no duplicate renderer instances are left running after repeated orientation flips.
