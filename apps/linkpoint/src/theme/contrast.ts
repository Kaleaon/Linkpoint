/**
 * Contrast helpers.
 *
 * A static "is this token dark" map cannot be right across twenty-four
 * independent colour packs, so ink is resolved against the actual background:
 * score every candidate and take the best. This is what keeps a Paper & Ink
 * label legible on the same component that has to work on Obsidian Crimson.
 */

/** Relative luminance, per WCAG 2.1. Accepts #rgb, #rrggbb, or rgb()/rgba(). */
export function luminance(color: string): number {
  const hex = String(color).trim().match(/^#?([0-9a-f]{3}|[0-9a-f]{6})$/i);
  let r: number;
  let g: number;
  let b: number;

  if (hex) {
    let x = hex[1];
    if (x.length === 3) x = x[0] + x[0] + x[1] + x[1] + x[2] + x[2];
    r = parseInt(x.slice(0, 2), 16);
    g = parseInt(x.slice(2, 4), 16);
    b = parseInt(x.slice(4, 6), 16);
  } else {
    const n = String(color).match(/\d+(\.\d+)?/g);
    if (!n || n.length < 3) return 0.5;
    r = +n[0];
    g = +n[1];
    b = +n[2];
  }

  const f = (v: number) => {
    const c = v / 255;
    return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
  };
  return 0.2126 * f(r) + 0.7152 * f(g) + 0.0722 * f(b);
}

/** WCAG contrast ratio between two colours, 1:1 to 21:1. */
export function contrastRatio(a: string, b: string): number {
  const la = luminance(a);
  const lb = luminance(b);
  return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
}

/**
 * Pick the most legible ink for a background from a candidate list.
 * Pure black and white are always appended as a backstop, so this never
 * returns something unreadable even for a palette that supplies nothing usable.
 */
export function inkOn(background: string, candidates: (string | undefined)[] = []): string {
  const list = candidates.concat(["#FFFFFF", "#000000"]).filter(Boolean) as string[];
  let best = list[0];
  let score = -1;
  for (const c of list) {
    const r = contrastRatio(background, c);
    if (r > score) {
      score = r;
      best = c;
    }
  }
  return best;
}

/** True when a foreground/background pair clears WCAG AA for body text. */
export function meetsAA(foreground: string, background: string, large = false): boolean {
  return contrastRatio(foreground, background) >= (large ? 3 : 4.5);
}
