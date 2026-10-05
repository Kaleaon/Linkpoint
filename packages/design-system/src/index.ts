/**
 * @linkpoint/design-system root export.
 */

export * from "./tokens/index.js";
export * from "./css/index.js";
export * from "./react/index.js";

const THEME_IMPORTS: Record<string, () => Promise<any>> = {
  "art-deco": () => import("../themes/art-deco.json"),
  "art-nouveau": () => import("../themes/art-nouveau.json"),
  "aurora-glass-night": () => import("../themes/aurora-glass-night.json"),
  "burgundy-rose-gold": () => import("../themes/burgundy-rose-gold.json"),
  "calm-clinical": () => import("../themes/calm-clinical.json"),
  "charcoal-champagne": () => import("../themes/charcoal-champagne.json"),
  "cleverferret-gold": () => import("../themes/cleverferret-gold.json"),
  "deep-purple-platinum": () => import("../themes/deep-purple-platinum.json"),
  "emerald-silver": () => import("../themes/emerald-silver.json"),
  "firestorm": () => import("../themes/firestorm.json"),
  "forest-copper": () => import("../themes/forest-copper.json"),
  "frutiger-aero": () => import("../themes/frutiger-aero.json"),
  "ink-terminal-modern": () => import("../themes/ink-terminal-modern.json"),
  "lcars": () => import("../themes/lcars.json"),
  "linkpoint-default": () => import("../themes/linkpoint-default.json"),
  "midnight-amber": () => import("../themes/midnight-amber.json"),
  "navy-gold": () => import("../themes/navy-gold.json"),
  "neo-noir-neon": () => import("../themes/neo-noir-neon.json"),
  "obsidian-crimson": () => import("../themes/obsidian-crimson.json"),
  "paper-ink": () => import("../themes/paper-ink.json"),
  "rose-gold": () => import("../themes/rose-gold.json"),
  "royal-bronze": () => import("../themes/royal-bronze.json"),
  "royal-silver": () => import("../themes/royal-silver.json"),
  "sl-classic": () => import("../themes/sl-classic.json"),
  "slate-cyan": () => import("../themes/slate-cyan.json"),
  "slate-gunmetal": () => import("../themes/slate-gunmetal.json"),
  "solarpunk-civic": () => import("../themes/solarpunk-civic.json"),
  "windows-phone-metro": () => import("../themes/windows-phone-metro.json")
};

export async function loadTheme(name: import("./tokens/index.js").ThemeName): Promise<unknown> {
  const globModules: Record<string, () => Promise<any>> =
    typeof import.meta !== "undefined" && typeof (import.meta as any).glob === "function"
      ? (import.meta as any).glob("../themes/*.json")
      : {};

  const matchedKey = Object.keys(globModules).find(
    (k) => k.replace(/\\/g, "/").endsWith(`/${name}.json`) || k === `../themes/${name}.json`
  );

  if (matchedKey && globModules[matchedKey]) {
    const mod = await globModules[matchedKey]();
    return mod.default || mod;
  }

  if (THEME_IMPORTS[name]) {
    const mod = await THEME_IMPORTS[name]();
    return mod.default || mod;
  }

  throw new Error(`Unknown theme: ${name}`);
}
