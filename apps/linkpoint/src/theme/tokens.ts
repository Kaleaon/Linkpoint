/**
 * Linkpoint design tokens.
 *
 * Ported from the "Second Life Mobile Viewer" design canvas: six layout packs
 * (geometry, navigation model, type, density) crossed with twenty-four colour
 * packs. Any colour pack drops into any layout, so the viewer ships 144 skins
 * from one token set.
 *
 * Layout owns shape; palette owns colour. Nothing else may hard-code either.
 */

/** Navigation model a layout pack asks for before the form factor is considered. */
export type NavModel = "TABS" | "SWEEP" | "TILES" | "RAIL";

/** Resolved navigation model, after the form factor has had its say. */
export type NavMode = "tabs" | "sweep" | "tiles" | "rail";

/** Card treatment: how a list card carries its edge and its accent. */
export type CardLook = "box" | "flat" | "soft" | "rule" | "quiet" | "cap";

/** Screen header grammar. */
export type HeadLook = "stack" | "sweep" | "pivot" | "rule" | "editorial";

/** Segmented-control treatment. */
export type SegLook = "fill" | "pivot" | "text";

/** Shape and spacing scale. */
export interface LayoutScale {
  /** Small radius: chips, buttons, inputs. */
  rs: string;
  /** Large radius: sheets and dialogs. */
  rl: string;
  /** Panel radius: cards. */
  rp: string;
  /** Navigation item radius. */
  navr: string;
  /** Base row padding. */
  pad: string;
  /** Title letter-spacing. */
  tls: string;
}

export interface LayoutLook {
  card: CardLook;
  head: HeadLook;
  seg: SegLook;
  /** Whether the pack uses filter chips at all. */
  chips: boolean;
  /** Gap between cards in a list. */
  gap: string;
  /** Optional reading measure for editorial packs. */
  measure: string | null;
}

export interface LayoutPack {
  name: string;
  nav: NavModel;
  /** Body font stack. */
  font: string;
  /** Display font stack, for titles and navigation. */
  dfont: string;
  s: LayoutScale;
  look: LayoutLook;
  note: string;
}

/** The colour roles every palette must fill. Names match the design canvas. */
export interface PaletteColors {
  bg: string; surf: string; surf2: string;
  ink: string; ink2: string;
  pri: string; onpri: string; priC: string; onpriC: string;
  sec: string; onsec: string; sec2: string;
  bdg: string; onbdg: string;
  info: string; outv: string;
  ok: string; err: string; warn: string;
  /** Horizon gradient for the 3D view: sky top, sky bottom, ground, ground far. */
  sky1: string; sky2: string; gnd: string; gnd2: string;
}

export interface PalettePack {
  name: string;
  note: string;
  /** Light-first packs invert scrim and shadow handling. */
  light: boolean;
  c: PaletteColors;
}

export interface DevicePack {
  name: string;
  dims: string;
  w: number;
  h: number;
  /** Split packs get a list+detail pane and a rail. */
  split: boolean;
  notch: "island" | "hole" | "none";
}

export const LAYOUTS: Record<string, LayoutPack> = {
  terminal: {
    name: "Ink Terminal",
    nav: "TABS",
    font: '"JetBrains Mono","IBM Plex Mono",monospace',
    dfont: '"JetBrains Mono",monospace',
    s: { rs: "4px", rl: "8px", rp: "4px", navr: "4px", pad: "12px", tls: ".26em" },
    look: { card: "box", head: "stack", seg: "fill", chips: true, gap: "9px", measure: null },
    note: "Dense terminal grid: mono type, 4px corners, bottom tabs, '>' status lines, 0.26em tracking on titles."
  },
  sweep: {
    name: "Sweep Console",
    nav: "SWEEP",
    font: '"Antonio","Jost",sans-serif',
    dfont: '"Antonio",sans-serif',
    s: { rs: "999px", rl: "28px", rp: "22px", navr: "0 999px 999px 0", pad: "10px", tls: ".16em" },
    look: { card: "cap", head: "sweep", seg: "fill", chips: true, gap: "6px", measure: null },
    note: "LCARS-rule console: the swept elbow is the frame, rail segments ARE the buttons, thickness changes at every turn, rounded caps terminate bars, title-bar grammar (cap → bar → title → end cap), one font at three sizes, two text colours, fills only."
  },
  tiles: {
    name: "Metro Tiles",
    nav: "TILES",
    font: '"Open Sans","Nunito Sans",sans-serif',
    dfont: '"Open Sans",sans-serif',
    s: { rs: "0px", rl: "0px", rp: "0px", navr: "0px", pad: "14px", tls: "0em" },
    look: { card: "flat", head: "pivot", seg: "pivot", chips: false, gap: "2px", measure: null },
    note: "Metro: zero radius, light-weight pivot titles, spacious 1.25 scale, lowercase tile nav."
  },
  glass: {
    name: "Aero Glass",
    nav: "TABS",
    font: '"Nunito Sans",sans-serif',
    dfont: '"Nunito Sans",sans-serif',
    s: { rs: "12px", rl: "18px", rp: "16px", navr: "12px", pad: "14px", tls: ".06em" },
    look: { card: "soft", head: "stack", seg: "fill", chips: true, gap: "11px", measure: null },
    note: "Glossy Aero: 12-18px rounding, comfortable 1.1 spacing, soft panels, rounded duotone icons."
  },
  rules: {
    name: "Rule & Rail",
    nav: "RAIL",
    font: '"Jost",system-ui,sans-serif',
    dfont: '"Jost",sans-serif',
    s: { rs: "0px", rl: "0px", rp: "0px", navr: "0px", pad: "11px", tls: ".35em" },
    look: { card: "rule", head: "rule", seg: "text", chips: false, gap: "0px", measure: null },
    note: "Deco discipline: hairline rules instead of fills, sharp corners, 0.35em tracking, compact scale, rail nav."
  },
  press: {
    name: "Press",
    nav: "TABS",
    font: '"Source Serif 4",Georgia,serif',
    dfont: '"Jost",sans-serif',
    s: { rs: "2px", rl: "6px", rp: "3px", navr: "2px", pad: "14px", tls: ".1em" },
    look: { card: "quiet", head: "editorial", seg: "text", chips: false, gap: "0px", measure: "58ch" },
    note: "Editorial: serif body, 2px corners, generous leading — the legibility layout for sunlight and large-type mode."
  }
};

export const PALETTES: Record<string, PalettePack> = {
  ink: {
    name: "Ink Terminal",
    note: "ink-terminal-modern, phosphor green on near-black",
    light: false,
    c: {
      bg: "#0A1112",
      surf: "#101A1C",
      surf2: "#1B2A2D",
      ink: "#D7F5E6",
      ink2: "#A7C8BC",
      pri: "#6CFF9A",
      onpri: "#0A1112",
      priC: "#1F6640",
      onpriC: "#D7F5E6",
      sec: "#3E4E5E",
      onsec: "#D7F5E6",
      sec2: "#8AD0B0",
      bdg: "#8AD0B0",
      onbdg: "#0A1112",
      info: "#8AD0B0",
      outv: "#365047",
      ok: "#6CFF9A",
      err: "#CF6679",
      warn: "#FFC98A",
      sky1: "#1c4a5c",
      sky2: "#12333a",
      gnd: "#10241d",
      gnd2: "#0a1112"
    }
  },
  lcars: {
    name: "LCARS Amber",
    note: "lcars, amber + violet + mauve on aubergine",
    light: false,
    c: {
      bg: "#120C1C",
      surf: "#1C132A",
      surf2: "#3D1F5C",
      ink: "#F3E9FF",
      ink2: "#D0B3E6",
      pri: "#F2A65A",
      onpri: "#120C1C",
      priC: "#CC7A2B",
      onpriC: "#120C1C",
      sec: "#A485F7",
      onsec: "#120C1C",
      sec2: "#C5678D",
      bdg: "#A485F7",
      onbdg: "#120C1C",
      info: "#C5678D",
      outv: "#4D2F5C",
      ok: "#F2A65A",
      err: "#CF6679",
      warn: "#FFC46B",
      sky1: "#3D1F5C",
      sky2: "#241338",
      gnd: "#1C132A",
      gnd2: "#120C1C"
    }
  },
  metro: {
    name: "Metro Cyan",
    note: "windows-phone-metro, flat cyan on deep blue",
    light: false,
    c: {
      bg: "#001A33",
      surf: "#002448",
      surf2: "#3D4854",
      ink: "#F0F8FF",
      ink2: "#B8CAD6",
      pri: "#00AEEF",
      onpri: "#001A33",
      priC: "#0070ca",
      onpriC: "#F0F8FF",
      sec: "#2D89EF",
      onsec: "#001A33",
      sec2: "#00AEEF",
      bdg: "#2D89EF",
      onbdg: "#001A33",
      info: "#00AEEF",
      outv: "#4D5A66",
      ok: "#00AEEF",
      err: "#CF6679",
      warn: "#FFC300",
      sky1: "#0a3a63",
      sky2: "#04263f",
      gnd: "#062033",
      gnd2: "#001a33"
    }
  },
  aero: {
    name: "Frutiger Aero",
    note: "frutiger-aero, sky and nature, light-first",
    light: true,
    c: {
      bg: "#EAF7FF",
      surf: "#F7FCFF",
      surf2: "#DDF1FF",
      ink: "#173A52",
      ink2: "#34566E",
      pri: "#39B6F0",
      onpri: "#173A52",
      priC: "#A9E6FF",
      onpriC: "#173A52",
      sec: "#79D87E",
      onsec: "#173A52",
      sec2: "#0A6FA0",
      bdg: "#0A6FA0",
      onbdg: "#EAF7FF",
      info: "#0A6FA0",
      outv: "#A9C7DA",
      ok: "#0A6FA0",
      err: "#BA1A1A",
      warn: "#8A5A00",
      sky1: "#BEEBFF",
      sky2: "#DAF0FF",
      gnd: "#79D87E",
      gnd2: "#3f8f57"
    }
  },
  navy: {
    name: "Navy Gold",
    note: "navy-gold, metallic gold on navy",
    light: false,
    c: {
      bg: "#0A1630",
      surf: "#1A2645",
      surf2: "#2A3655",
      ink: "#E8E3D8",
      ink2: "#C9C4B9",
      pri: "#D4AF37",
      onpri: "#0A1630",
      priC: "#715f33",
      onpriC: "#E8E3D8",
      sec: "#4A90E2",
      onsec: "#0A1630",
      sec2: "#9C8970",
      bdg: "#4A90E2",
      onbdg: "#0A1630",
      info: "#4A90E2",
      outv: "#44483E",
      ok: "#D4AF37",
      err: "#CF6679",
      warn: "#E0B84C",
      sky1: "#16305c",
      sky2: "#0e1f3f",
      gnd: "#141f38",
      gnd2: "#0a1630"
    }
  },
  paper: {
    name: "Paper & Ink",
    note: "paper-ink, hueless, maximum legibility",
    light: true,
    c: {
      bg: "#F0F0EB",
      surf: "#FAF9F6",
      surf2: "#EBEAE4",
      ink: "#2C2C2C",
      ink2: "#454545",
      pri: "#2C2C2C",
      onpri: "#F0F0EB",
      priC: "#EBEAE4",
      onpriC: "#2C2C2C",
      sec: "#595959",
      onsec: "#F0F0EB",
      sec2: "#6B6B6B",
      bdg: "#2C2C2C",
      onbdg: "#F0F0EB",
      info: "#454545",
      outv: "#C9C9C9",
      ok: "#2C6B3F",
      err: "#BA1A1A",
      warn: "#8A5A00",
      sky1: "#d9d9d2",
      sky2: "#eceae3",
      gnd: "#cfcec6",
      gnd2: "#bfbeb6"
    }
  },
  deco: {
    name: "Art Deco",
    note: "art-deco, gold hairlines on ivory-black",
    light: false,
    c: {
      bg: "#0B0A0A",
      surf: "#141314",
      surf2: "#232124",
      ink: "#F3E8D0",
      ink2: "#C9BDA2",
      pri: "#D4AF37",
      onpri: "#0B0A0A",
      priC: "#977b2f",
      onpriC: "#0B0A0A",
      sec: "#F4E7CF",
      onsec: "#0B0A0A",
      sec2: "#B8A17A",
      bdg: "#B8A17A",
      onbdg: "#0B0A0A",
      info: "#B8A17A",
      outv: "#4B4332",
      ok: "#D4AF37",
      err: "#FFB4AB",
      warn: "#E0B84C",
      sky1: "#1d1a14",
      sky2: "#12100c",
      gnd: "#191712",
      gnd2: "#0b0a0a"
    }
  },
  noir: {
    name: "Neo-Noir Neon",
    note: "neo-noir-neon, violet and cyan glow",
    light: false,
    c: {
      bg: "#090A10",
      surf: "#111420",
      surf2: "#1C2130",
      ink: "#E7EAF7",
      ink2: "#B9C0D8",
      pri: "#aa43ff",
      onpri: "#090A10",
      priC: "#4B1D73",
      onpriC: "#E7EAF7",
      sec: "#00D1FF",
      onsec: "#090A10",
      sec2: "#FF3D9E",
      bdg: "#00D1FF",
      onbdg: "#090A10",
      info: "#00D1FF",
      outv: "#363D52",
      ok: "#00D1FF",
      err: "#FF6B6B",
      warn: "#FF3D9E",
      sky1: "#2a1140",
      sky2: "#12132a",
      gnd: "#141827",
      gnd2: "#090a10"
    }
  },
  emerald: {
    name: "Emerald Silver",
    note: "emerald-silver, silver on deep emerald",
    light: false,
    c: {
      bg: "#0D3B2E",
      surf: "#1A5544",
      surf2: "#2A6554",
      ink: "#E8F5E8",
      ink2: "#C9E4D9",
      pri: "#C0C0C0",
      onpri: "#0D3B2E",
      priC: "#505050",
      onpriC: "#E8F5E8",
      sec: "#50C878",
      onsec: "#0D3B2E",
      sec2: "#8BA888",
      bdg: "#50C878",
      onbdg: "#0D3B2E",
      info: "#50C878",
      outv: "#3E4E44",
      ok: "#50C878",
      err: "#CF6679",
      warn: "#E0B84C",
      sky1: "#14584a",
      sky2: "#0d3b2e",
      gnd: "#123f33",
      gnd2: "#0a2e24"
    }
  },
  amber: {
    name: "Midnight Amber",
    note: "midnight-amber, amber on midnight blue",
    light: false,
    c: {
      bg: "#0C1824",
      surf: "#15202E",
      surf2: "#253447",
      ink: "#E8EEF5",
      ink2: "#B8C5D6",
      pri: "#FFBF00",
      onpri: "#0C1824",
      priC: "#CC9900",
      onpriC: "#0C1824",
      sec: "#D4A76A",
      onsec: "#0C1824",
      sec2: "#D4A76A",
      bdg: "#D4A76A",
      onbdg: "#0C1824",
      info: "#D4A76A",
      outv: "#3D4854",
      ok: "#FFBF00",
      err: "#CF6679",
      warn: "#FFBF00",
      sky1: "#16324a",
      sky2: "#0c1824",
      gnd: "#132234",
      gnd2: "#0c1824"
    }
  },
  crimson: {
    name: "Obsidian Crimson",
    note: "obsidian-crimson, crimson on obsidian",
    light: false,
    c: {
      bg: "#0A0A0A",
      surf: "#141414",
      surf2: "#2D2D2D",
      ink: "#F5F5F5",
      ink2: "#D0D0D0",
      pri: "#DC143C",
      onpri: "#F5F5F5",
      priC: "#B00F30",
      onpriC: "#F5F5F5",
      sec: "#A8505A",
      onsec: "#F5F5F5",
      sec2: "#A8505A",
      bdg: "#A8505A",
      onbdg: "#F5F5F5",
      info: "#D0D0D0",
      outv: "#3D3D3D",
      ok: "#DC143C",
      err: "#FF6B6B",
      warn: "#DC143C",
      sky1: "#241014",
      sky2: "#12100f",
      gnd: "#1a1414",
      gnd2: "#0a0a0a"
    }
  },
  nouveau: {
    name: "Art Nouveau",
    note: "art-nouveau, organic curves, botanical accents, and decorative linework with a warm natural palette",
    light: true,
    c: {
      bg: "#F6F0E6",
      surf: "#FFF8EE",
      surf2: "#E7D8C7",
      ink: "#2C2218",
      ink2: "#584638",
      pri: "#7B5737",
      onpri: "#F6F0E6",
      priC: "#D9C2A9",
      onpriC: "#2C2218",
      sec: "#769762",
      onsec: "#2C2218",
      sec2: "#C57C52",
      bdg: "#769762",
      onbdg: "#2C2218",
      info: "#C57C52",
      outv: "#CBB8A5",
      ok: "#6B8F57",
      err: "#BA1A1A",
      warn: "#C57C52",
      sky1: "#c4af99",
      sky2: "#fbf4ea",
      gnd: "#fcf6ec",
      gnd2: "#F6F0E6"
    }
  },
  aurora: {
    name: "Aurora Glass Night",
    note: "aurora-glass-night, night-first glass aesthetic with aurora accents and disciplined blur",
    light: false,
    c: {
      bg: "#0A1224",
      surf: "#101C33",
      surf2: "#1D2B4A",
      ink: "#E7F0FF",
      ink2: "#B5C7E9",
      pri: "#6DE8FF",
      onpri: "#0A1224",
      priC: "#2A6F85",
      onpriC: "#E7F0FF",
      sec: "#8C7CFF",
      onsec: "#0A1224",
      sec2: "#7CFFD8",
      bdg: "#8C7CFF",
      onbdg: "#0A1224",
      info: "#7CFFD8",
      outv: "#334364",
      ok: "#8C7CFF",
      err: "#CF6679",
      warn: "#7CFFD8",
      sky1: "#2b4d6b",
      sky2: "#0d172c",
      gnd: "#0e192f",
      gnd2: "#0A1224"
    }
  },
  burgundy: {
    name: "Burgundy Rose Gold",
    note: "burgundy-rose-gold, rich burgundy with elegant rose gold metallic accents",
    light: false,
    c: {
      bg: "#2D0F1A",
      surf: "#3D1525",
      surf2: "#5C2A3D",
      ink: "#FFE6ED",
      ink2: "#E6C0CC",
      pri: "#B76E79",
      onpri: "#2D0F1A",
      priC: "#93575F",
      onpriC: "#FFE6ED",
      sec: "#4D1A2A",
      onsec: "#FFE6ED",
      sec2: "#C99BA5",
      bdg: "#4D1A2A",
      onbdg: "#FFE6ED",
      info: "#C99BA5",
      outv: "#6D3F4D",
      ok: "#4D1A2A",
      err: "#FFB4AB",
      warn: "#C99BA5",
      sky1: "#6c3648",
      sky2: "#351220",
      gnd: "#381322",
      gnd2: "#2D0F1A"
    }
  },
  calm: {
    name: "Calm Clinical",
    note: "calm-clinical, low-stress healthcare/admin palette with clear status readability",
    light: true,
    c: {
      bg: "#F5FAFD",
      surf: "#FFFFFF",
      surf2: "#E5EEF4",
      ink: "#1F394B",
      ink2: "#445F72",
      pri: "#38779e",
      onpri: "#F5FAFD",
      priC: "#C4DFF2",
      onpriC: "#1F394B",
      sec: "#61b48b",
      onsec: "#1F394B",
      sec2: "#7D9AB2",
      bdg: "#61b48b",
      onbdg: "#1F394B",
      info: "#7D9AB2",
      outv: "#B5C8D5",
      ok: "#4DAA7C",
      err: "#BA1A1A",
      warn: "#7D9AB2",
      sky1: "#aecadb",
      sky2: "#fafdfe",
      gnd: "#fcfefe",
      gnd2: "#F5FAFD"
    }
  },
  charcoal: {
    name: "Charcoal Champagne",
    note: "charcoal-champagne, sophisticated charcoal gray with warm champagne accents",
    light: false,
    c: {
      bg: "#1F1F1F",
      surf: "#2A2A2A",
      surf2: "#3D3D3D",
      ink: "#F5F5F5",
      ink2: "#D0D0D0",
      pri: "#F7E7CE",
      onpri: "#1F1F1F",
      priC: "#C5B8A5",
      onpriC: "#1F1F1F",
      sec: "#3D3D3D",
      onsec: "#F5F5F5",
      sec2: "#D4C4A8",
      bdg: "#3D3D3D",
      onbdg: "#F5F5F5",
      info: "#D4C4A8",
      outv: "#4D4D4D",
      ok: "#3D3D3D",
      err: "#CF6679",
      warn: "#D4C4A8",
      sky1: "#5e5c57",
      sky2: "#252525",
      gnd: "#272727",
      gnd2: "#1F1F1F"
    }
  },
  deep: {
    name: "Deep Purple Platinum",
    note: "deep-purple-platinum, deep purple background with luxurious platinum metallic accents",
    light: false,
    c: {
      bg: "#1A0F2E",
      surf: "#24153D",
      surf2: "#3D2A5C",
      ink: "#F0EBFF",
      ink2: "#D0C0E6",
      pri: "#E5E4E2",
      onpri: "#1A0F2E",
      priC: "#B8B7B5",
      onpriC: "#1A0F2E",
      sec: "#2E1A50",
      onsec: "#F0EBFF",
      sec2: "#C8BFE0",
      bdg: "#2E1A50",
      onbdg: "#F0EBFF",
      info: "#C8BFE0",
      outv: "#4D3F6D",
      ok: "#2E1A50",
      err: "#CF6679",
      warn: "#C8BFE0",
      sky1: "#5b4b74",
      sky2: "#1f1236",
      gnd: "#211339",
      gnd2: "#1A0F2E"
    }
  },
  forest: {
    name: "Forest Copper",
    note: "forest-copper, deep forest green with warm copper metallic accents",
    light: false,
    c: {
      bg: "#0D1F0D",
      surf: "#152915",
      surf2: "#2A4D2A",
      ink: "#E8F5E8",
      ink2: "#B8D9B8",
      pri: "#B87333",
      onpri: "#0D1F0D",
      priC: "#935E29",
      onpriC: "#E8F5E8",
      sec: "#1A3D1A",
      onsec: "#E8F5E8",
      sec2: "#8FA886",
      bdg: "#1A3D1A",
      onbdg: "#E8F5E8",
      info: "#8FA886",
      outv: "#3D5A3D",
      ok: "#1A3D1A",
      err: "#CF6679",
      warn: "#8FA886",
      sky1: "#44542c",
      sky2: "#112411",
      gnd: "#132613",
      gnd2: "#0D1F0D"
    }
  },
  rose: {
    name: "Rose Gold",
    note: "rose-gold, warm and elegant rose gold with burgundy undertones",
    light: false,
    c: {
      bg: "#3D1F2B",
      surf: "#4D2F3B",
      surf2: "#5D3F4B",
      ink: "#F5E5E8",
      ink2: "#E5D5D8",
      pri: "#c1818b",
      onpri: "#3D1F2B",
      priC: "#7D4A52",
      onpriC: "#F5E5E8",
      sec: "#D4A5A5",
      onsec: "#3D1F2B",
      sec2: "#C9A9A9",
      bdg: "#D4A5A5",
      onbdg: "#3D1F2B",
      info: "#C9A9A9",
      outv: "#4E3A3E",
      ok: "#D4A5A5",
      err: "#FFB4AB",
      warn: "#C9A9A9",
      sky1: "#6d4753",
      sky2: "#452733",
      gnd: "#482a36",
      gnd2: "#3D1F2B"
    }
  },
  royalb: {
    name: "Royal Bronze",
    note: "royal-bronze, regal deep purple with luxurious bronze metallic accents",
    light: false,
    c: {
      bg: "#1A0A30",
      surf: "#220D40",
      surf2: "#3D1F5C",
      ink: "#F0E6FF",
      ink2: "#D0B3E6",
      pri: "#CD7F32",
      onpri: "#1A0A30",
      priC: "#975929",
      onpriC: "#F0E6FF",
      sec: "#2D1550",
      onsec: "#F0E6FF",
      sec2: "#9B7A5F",
      bdg: "#2D1550",
      onbdg: "#F0E6FF",
      info: "#9B7A5F",
      outv: "#4D2F5C",
      ok: "#2D1550",
      err: "#CF6679",
      warn: "#9B7A5F",
      sky1: "#573054",
      sky2: "#1e0c38",
      gnd: "#200c3b",
      gnd2: "#1A0A30"
    }
  },
  royals: {
    name: "Royal Silver",
    note: "royal-silver, royal purple background with elegant silver metallic accents",
    light: false,
    c: {
      bg: "#1A1535",
      surf: "#211A40",
      surf2: "#3D2F5C",
      ink: "#F0EBFF",
      ink2: "#C8BFE6",
      pri: "#C0C0C0",
      onpri: "#1A1535",
      priC: "#9A9A9A",
      onpriC: "#1A1535",
      sec: "#2A1F50",
      onsec: "#F0EBFF",
      sec2: "#A89BC9",
      bdg: "#2A1F50",
      onbdg: "#F0EBFF",
      info: "#A89BC9",
      outv: "#4D3F66",
      ok: "#2A1F50",
      err: "#CF6679",
      warn: "#A89BC9",
      sky1: "#55496e",
      sky2: "#1e183b",
      gnd: "#1f193d",
      gnd2: "#1A1535"
    }
  },
  slatec: {
    name: "Slate Cyan",
    note: "slate-cyan, cool modern slate gray with vibrant cyan metallic accents",
    light: false,
    c: {
      bg: "#1A1F24",
      surf: "#232930",
      surf2: "#3D4854",
      ink: "#E8F0F5",
      ink2: "#B8CAD6",
      pri: "#00D9FF",
      onpri: "#1A1F24",
      priC: "#00A8CC",
      onpriC: "#1A1F24",
      sec: "#2A333D",
      onsec: "#E8F0F5",
      sec2: "#6BA5B8",
      bdg: "#2A333D",
      onbdg: "#E8F0F5",
      info: "#6BA5B8",
      outv: "#4D5A66",
      ok: "#2A333D",
      err: "#CF6679",
      warn: "#6BA5B8",
      sky1: "#326273",
      sky2: "#1f242a",
      gnd: "#20262c",
      gnd2: "#1A1F24"
    }
  },
  slateg: {
    name: "Slate Gunmetal",
    note: "slate-gunmetal, industrial slate gray with gunmetal metallic accents",
    light: false,
    c: {
      bg: "#1A2029",
      surf: "#232C38",
      surf2: "#3D4854",
      ink: "#E6ECF2",
      ink2: "#B8C5D6",
      pri: "#8F9CA8",
      onpri: "#1A2029",
      priC: "#7d8a94",
      onpriC: "#1A2029",
      sec: "#2D3844",
      onsec: "#E6ECF2",
      sec2: "#9DAAB6",
      bdg: "#2D3844",
      onbdg: "#E6ECF2",
      info: "#9DAAB6",
      outv: "#4D5A66",
      ok: "#2D3844",
      err: "#CF6679",
      warn: "#9DAAB6",
      sky1: "#4c5763",
      sky2: "#1f2631",
      gnd: "#202834",
      gnd2: "#1A2029"
    }
  },
  solarpunk: {
    name: "Solarpunk Civic",
    note: "solarpunk-civic, optimistic civic palette with daylight greens and trust-building clarity",
    light: true,
    c: {
      bg: "#F2FBF4",
      surf: "#FBFFFC",
      surf2: "#E2F2E8",
      ink: "#1E3A27",
      ink2: "#456355",
      pri: "#38B56A",
      onpri: "#1E3A27",
      priC: "#BFEFD0",
      onpriC: "#1E3A27",
      sec: "#4FAEEA",
      onsec: "#1E3A27",
      sec2: "#F2C46C",
      bdg: "#4FAEEA",
      onbdg: "#1E3A27",
      info: "#F2C46C",
      outv: "#B3D0BF",
      ok: "#4FAEEA",
      err: "#BA1A1A",
      warn: "#F2C46C",
      sky1: "#acdec0",
      sky2: "#f7fdf8",
      gnd: "#f8fefa",
      gnd2: "#F2FBF4"
    }
  }
};

/** Colour packs grouped by family — twenty-four is too many for a flat list. */
export const PALETTE_FAMILIES: { name: string; keys: string[] }[] = [
  {
    name: "TERMINAL & NEON",
    keys: ["ink", "noir", "metro", "aurora", "slatec"]
  },
  {
    name: "CONSOLE & AMBER",
    keys: ["lcars", "amber", "royalb", "forest", "crimson"]
  },
  {
    name: "METAL & JEWEL",
    keys: ["navy", "deco", "emerald", "royals", "deep", "charcoal", "slateg", "rose", "burgundy"]
  },
  {
    name: "DAYLIGHT",
    keys: ["aero", "paper", "nouveau", "calm", "solarpunk"]
  }
];

export const DEVICES: Record<string, DevicePack> = {
  ios:  { name: "iPhone 15 Pro", dims: "393×852", w: 393, h: 852, split: false, notch: "island" },
  and:  { name: "Pixel 8", dims: "412×892", w: 412, h: 892, split: false, notch: "hole" },
  tab:  { name: 'Tablet 12.9" landscape', dims: "1194×834", w: 1194, h: 834, split: true, notch: "none" },
  fold: { name: "Foldable, unfolded", dims: "840×880", w: 840, h: 880, split: true, notch: "hole" },
};

export type LayoutKey = keyof typeof LAYOUTS & string;
export type PaletteKey = keyof typeof PALETTES & string;
export type DeviceKey = keyof typeof DEVICES & string;
