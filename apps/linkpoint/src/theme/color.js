// Ported verbatim from the static `lum`/`ratio` helpers and the `ink()` instance
// method on the mockup's Component class. Contrast-aware ink: a static
// "is this token dark" map cannot be right across 24 independent colour packs,
// so we resolve the actual background and pick whichever candidate ink scores
// highest against it (WCAG contrast ratio).

export function lum(c) {
  const m = String(c).trim().match(/^#?([0-9a-f]{3}|[0-9a-f]{6})$/i);
  let r, g, b;
  if (m) {
    let x = m[1];
    if (x.length === 3) x = x[0] + x[0] + x[1] + x[1] + x[2] + x[2];
    r = parseInt(x.slice(0, 2), 16);
    g = parseInt(x.slice(2, 4), 16);
    b = parseInt(x.slice(4, 6), 16);
  } else {
    const n = String(c).match(/\d+(\.\d+)?/g);
    if (!n || n.length < 3) return 0.5;
    r = +n[0];
    g = +n[1];
    b = +n[2];
  }
  const f = (v) => {
    v /= 255;
    return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
  };
  return 0.2126 * f(r) + 0.7152 * f(g) + 0.0722 * f(b);
}

export function ratio(a, b) {
  const la = lum(a),
    lb = lum(b);
  return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
}

export function pickInk(bg, candidates) {
  const list = (candidates || []).concat(["#FFFFFF", "#000000"]).filter(Boolean);
  let best = list[0],
    score = -1;
  for (const c of list) {
    const r = ratio(bg, c);
    if (r > score) {
      score = r;
      best = c;
    }
  }
  return best;
}
