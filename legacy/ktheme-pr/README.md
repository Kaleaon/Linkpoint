# Linkpoint theme contributions

Four themes generated from the Linkpoint 2.0 mobile Second Life UI design exploration. Each is a stand-alone Ktheme JSON conforming to the `Theme` schema and ready to drop into `themes/community/` (or promote to `themes/examples/`).

| File | Theme ID | Vibe |
| --- | --- | --- |
| `stargate-sg1.json` | `stargate-sg1` | Iris-bronze + event-horizon teal · gate-room console |
| `stargate-atlantis.json` | `stargate-atlantis` | Atlantean blue glass + bronze · Art Deco geometry · Wright prairie banding |
| `lcars-tng.json` | `lcars-tng` | Warm TNG amber + lilac · strict pill geometry · uppercase Antonio |
| `metro-cyan.json` | `metro-cyan` | Black field + vivid cyan tiles · Segoe Light · lowercase chrome |

## How to PR

```bash
git clone https://github.com/Kaleaon/Ktheme.git
cd Ktheme
mkdir -p themes/community
cp /path/to/this/folder/themes/community/*.json themes/community/
git checkout -b add-linkpoint-themes
git add themes/community/*.json
git commit -m "Add Linkpoint contribution: SG-1, Atlantis, LCARS-TNG, Metro Cyan"
git push origin add-linkpoint-themes
# open PR on github.com/Kaleaon/Ktheme
```

## Validation

Each file matches the schema used by `themes/examples/*.json` (notably `lcars.json`, `windows-phone-metro.json`, `aurora-glass-night.json`):
- `metadata` block with `id`, `name`, `description`, `author`, `version`, `tags`, timestamps
- Full Material Design 3 `colorScheme` (primary/secondary/tertiary + containers + on-* + surface variants + outline + inverse)
- `effects` (metallic, shadows, blur, shimmer, gradients, overlays — only the relevant ones)
- Optional `typography` and `adaptation` blocks for layout/icons/componentOverrides

Run `engine.validateTheme(theme)` after import; the contrast guardrails should pass without warnings.

## Notes per theme

- **Stargate SG-1** — uses the standard `metallic.GOLD` variant with secondary teal `#4FE0FF` as a screen-blend overlay.
- **Stargate Atlantis** — combines `metallic.BRONZE` with a custom `gradients.stops` array that paints two thin horizontal Wright-style prairie bands at 42–43% and 56–57% of the surface. Ships a `displayFontFamily` override for Cinzel.
- **LCARS-TNG** — palette variant of the existing `lcars` preset; warmer amber and lilac shifted toward TNG-era panels. Geometry rules unchanged (rail/pill).
- **Metro Cyan** — sibling to `windows-phone-metro` with a stronger cyan accent and pure-black field for higher punch.
