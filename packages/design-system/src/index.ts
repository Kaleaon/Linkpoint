/** Theme names synchronized from Kaleaon/linkpoint-design. */
export const themeNames = [
  "art-deco", "art-nouveau", "aurora-glass-night", "burgundy-rose-gold",
  "calm-clinical", "charcoal-champagne", "deep-purple-platinum",
  "emerald-silver", "forest-copper", "frutiger-aero", "ink-terminal-modern",
  "lcars", "midnight-amber", "navy-gold", "neo-noir-neon",
  "obsidian-crimson", "paper-ink", "rose-gold", "royal-bronze",
  "royal-silver", "slate-cyan", "slate-gunmetal", "solarpunk-civic",
  "windows-phone-metro",
] as const;

export type ThemeName = (typeof themeNames)[number];

export async function loadTheme(name: ThemeName): Promise<unknown> {
  return (await import(`../themes/${name}.json`)).default;
}
