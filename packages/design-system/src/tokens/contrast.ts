/**
 * Contrast helpers and WCAG 2.2 contrast enforcement logic.
 * Zero runtime dependencies on React or DOM global objects.
 */

/** Relative luminance, per WCAG 2.1 / 2.2. Accepts #rgb, #rrggbb, or rgb()/rgba(). */
export function luminance(color: string): number {
  if (!color) return 0.5;
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
 * Pure black and white are always appended as a backstop.
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

function parseRgb(color: string): [number, number, number] {
  const hex = String(color).trim().match(/^#?([0-9a-f]{3}|[0-9a-f]{6})$/i);
  if (hex) {
    let x = hex[1];
    if (x.length === 3) x = x[0] + x[0] + x[1] + x[1] + x[2] + x[2];
    return [
      parseInt(x.slice(0, 2), 16),
      parseInt(x.slice(2, 4), 16),
      parseInt(x.slice(4, 6), 16)
    ];
  }
  const n = String(color).match(/\d+(\.\d+)?/g);
  if (n && n.length >= 3) {
    return [+n[0], +n[1], +n[2]];
  }
  return [128, 128, 128];
}

function rgbToHsl(r: number, g: number, b: number): [number, number, number] {
  r /= 255;
  g /= 255;
  b /= 255;
  const max = Math.max(r, g, b);
  const min = Math.min(r, g, b);
  let h = 0;
  let s = 0;
  const l = (max + min) / 2;

  if (max !== min) {
    const d = max - min;
    s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
    switch (max) {
      case r:
        h = (g - b) / d + (g < b ? 6 : 0);
        break;
      case g:
        h = (b - r) / d + 2;
        break;
      case b:
        h = (r - g) / d + 4;
        break;
    }
    h /= 6;
  }

  return [h * 360, s * 100, l * 100];
}

function hslToHex(h: number, s: number, l: number): string {
  h = (h % 360 + 360) % 360;
  s = Math.max(0, Math.min(100, s)) / 100;
  l = Math.max(0, Math.min(100, l)) / 100;

  const c = (1 - Math.abs(2 * l - 1)) * s;
  const x = c * (1 - Math.abs(((h / 60) % 2) - 1));
  const m = l - c / 2;

  let r = 0;
  let g = 0;
  let b = 0;

  if (0 <= h && h < 60) {
    r = c; g = x; b = 0;
  } else if (60 <= h && h < 120) {
    r = x; g = c; b = 0;
  } else if (120 <= h && h < 180) {
    r = 0; g = c; b = x;
  } else if (180 <= h && h < 240) {
    r = 0; g = x; b = c;
  } else if (240 <= h && h < 300) {
    r = x; g = 0; b = c;
  } else if (300 <= h && h < 360) {
    r = c; g = 0; b = x;
  }

  const toHex = (v: number) => {
    const hex = Math.round((v + m) * 255).toString(16);
    return hex.length === 1 ? "0" + hex : hex;
  };

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
}

/**
 * Adjust lightness of a foreground color while preserving its hue identity to guarantee
 * a minimum contrast ratio against a background surface (WCAG 2.2 Criterion 1.4.3 & 1.4.11).
 */
export function ensureMinContrast(fg: string, bg: string, minRatio = 4.5): string {
  if (!fg || !bg) return fg;
  if (contrastRatio(fg, bg) >= minRatio) {
    return fg;
  }

  const bgLum = luminance(bg);
  const fgLum = luminance(fg);
  const [r, g, b] = parseRgb(fg);
  const [h, s, l] = rgbToHsl(r, g, b);

  const shiftUp = bgLum < 0.5 || fgLum >= bgLum;

  let bestColor = fg;
  let maxRatio = contrastRatio(fg, bg);

  const step = shiftUp ? 1 : -1;
  let currentL = l;

  for (let i = 0; i < 100; i++) {
    currentL += step * 1;
    if (currentL < 0 || currentL > 100) break;

    const candidate = hslToHex(h, s, currentL);
    const ratio = contrastRatio(candidate, bg);

    if (ratio > maxRatio) {
      maxRatio = ratio;
      bestColor = candidate;
    }

    if (ratio >= minRatio) {
      return candidate;
    }
  }

  const altStep = -step;
  currentL = l;

  for (let i = 0; i < 100; i++) {
    currentL += altStep * 1;
    if (currentL < 0 || currentL > 100) break;

    const candidate = hslToHex(h, s, currentL);
    const ratio = contrastRatio(candidate, bg);

    if (ratio > maxRatio) {
      maxRatio = ratio;
      bestColor = candidate;
    }

    if (ratio >= minRatio) {
      return candidate;
    }
  }

  if (maxRatio < minRatio) {
    const peakL = shiftUp ? 98 : 2;
    for (let currentS = s; currentS >= 0; currentS -= 5) {
      const candidate = hslToHex(h, currentS, peakL);
      const ratio = contrastRatio(candidate, bg);
      if (ratio > maxRatio) {
        maxRatio = ratio;
        bestColor = candidate;
      }
      if (ratio >= minRatio) {
        return candidate;
      }
    }
  }

  return bestColor;
}
