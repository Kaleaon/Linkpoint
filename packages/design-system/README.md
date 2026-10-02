# `@linkpoint/design-system`

This package provides shared visual primitives, tokens, and theme definitions for Linkpoint Next, aligned with the **[Linkpoint Design Language](https://github.com/Kaleaon/linkpoint-design)** common reference frame.

- Theme JSON files in `themes/` are synchronized from `linkpoint-design/docs/` and conform to the app-neutral **Ktheme** schema.
- Community theme contributions live in `ktheme-pr/themes/community/` and follow the upstream contribution pipeline documented in `ktheme-pr/README.md`.
- Token mapping supports 24 Ktheme palette packs across four aesthetic families (Terminal & Neon, Console & Amber, Metal & Jewel, Daylight) and six interchangeable layout packs.

Do not edit synchronized JSON here without making the equivalent upstream design-source change in `linkpoint-design`. Components, accessibility behavior, and typed token adapters belong in `src/`.
