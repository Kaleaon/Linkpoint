/**
 * @linkpoint/design-system root export.
 */

export * from "./tokens/index.js";
export * from "./css/index.js";
export * from "./react/index.js";

import artDeco from "../themes/art-deco.json";
import artNouveau from "../themes/art-nouveau.json";
import auroraGlassNight from "../themes/aurora-glass-night.json";
import burgundyRoseGold from "../themes/burgundy-rose-gold.json";
import calmClinical from "../themes/calm-clinical.json";
import charcoalChampagne from "../themes/charcoal-champagne.json";
import cleverferretGold from "../themes/cleverferret-gold.json";
import deepPurplePlatinum from "../themes/deep-purple-platinum.json";
import emeraldSilver from "../themes/emerald-silver.json";
import firestorm from "../themes/firestorm.json";
import forestCopper from "../themes/forest-copper.json";
import frutigerAero from "../themes/frutiger-aero.json";
import inkTerminalModern from "../themes/ink-terminal-modern.json";
import lcars from "../themes/lcars.json";
import linkpointDefault from "../themes/linkpoint-default.json";
import midnightAmber from "../themes/midnight-amber.json";
import navyGold from "../themes/navy-gold.json";
import neoNoirNeon from "../themes/neo-noir-neon.json";
import obsidianCrimson from "../themes/obsidian-crimson.json";
import paperInk from "../themes/paper-ink.json";
import roseGold from "../themes/rose-gold.json";
import royalBronze from "../themes/royal-bronze.json";
import royalSilver from "../themes/royal-silver.json";
import slClassic from "../themes/sl-classic.json";
import slateCyan from "../themes/slate-cyan.json";
import slateGunmetal from "../themes/slate-gunmetal.json";
import solarpunkCivic from "../themes/solarpunk-civic.json";
import windowsPhoneMetro from "../themes/windows-phone-metro.json";

const THEME_MAP: Record<string, unknown> = {
  "art-deco": artDeco,
  "art-nouveau": artNouveau,
  "aurora-glass-night": auroraGlassNight,
  "burgundy-rose-gold": burgundyRoseGold,
  "calm-clinical": calmClinical,
  "charcoal-champagne": charcoalChampagne,
  "cleverferret-gold": cleverferretGold,
  "deep-purple-platinum": deepPurplePlatinum,
  "emerald-silver": emeraldSilver,
  "firestorm": firestorm,
  "forest-copper": forestCopper,
  "frutiger-aero": frutigerAero,
  "ink-terminal-modern": inkTerminalModern,
  "lcars": lcars,
  "linkpoint-default": linkpointDefault,
  "midnight-amber": midnightAmber,
  "navy-gold": navyGold,
  "neo-noir-neon": neoNoirNeon,
  "obsidian-crimson": obsidianCrimson,
  "paper-ink": paperInk,
  "rose-gold": roseGold,
  "royal-bronze": royalBronze,
  "royal-silver": royalSilver,
  "sl-classic": slClassic,
  "slate-cyan": slateCyan,
  "slate-gunmetal": slateGunmetal,
  "solarpunk-civic": solarpunkCivic,
  "windows-phone-metro": windowsPhoneMetro
};

export async function loadTheme(name: import("./tokens/index.js").ThemeName): Promise<unknown> {
  const theme = THEME_MAP[name];
  if (theme) {
    return theme;
  }
  throw new Error(`Unknown theme: ${name}`);
}
