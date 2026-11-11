# UI Refactor Accessibility Checklist

Use this checklist during feature work and pre-release QA for Compose and XML screens.

## Motion and animation

- [ ] Verify system **Animator duration scale** (`Developer options`) is respected.
- [ ] With animator scale set to `0x`, confirm non-essential animations are disabled.
- [ ] Ensure critical feedback (loading/error/success) remains visible without animation.

## Touch targets and input

- [ ] Interactive icon/action controls meet minimum **48dp x 48dp** touch targets.
- [ ] Closely grouped controls still allow reliable single-tap selection.
- [ ] UI remains usable with one-handed reach zones where practical.

## Semantics and labels

- [ ] Navigation controls expose clear labels (Back, Open menu, Open settings, etc.).
- [ ] Overflow/menu controls include descriptive content descriptions.
- [ ] Theme picker controls (open/select/share/edit/delete) are all screen-reader discoverable.
- [ ] Decorative icons use `contentDescription = null` to reduce TalkBack noise.

## Color and contrast

- [ ] Confirm text contrast on surface/background combinations remains legible.
- [ ] Confirm text contrast on primary/accent surfaces remains legible.
- [ ] Run/observe debug contrast assertions for `onSurface/surface` and `onPrimary/primary`.

## Screen reader and focus order

- [ ] Test with TalkBack enabled for logical traversal order.
- [ ] Ensure focus does not get trapped in dialogs/menus.
- [ ] Verify dynamic content updates are announced where required.

## Manual QA pass

- [ ] Login and settings flows.
- [ ] World navigation and drawer/menu actions.
- [ ] Theme picker selection and theme management actions.
- [ ] Map/minimap core controls.
