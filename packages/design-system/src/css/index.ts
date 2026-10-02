/**
 * Utility functions for generating and applying CSS custom properties from design tokens.
 *
 * Subpath export: @linkpoint/design-system/css
 * Zero runtime dependencies on React or DOM global objects at import time.
 */

export type TokenInput = Record<string, any>;

/**
 * Maps a design token dictionary into standard CSS custom properties.
 * E.g., maps `pri` to `--color-pri` and `--pri`, `rs` to `--radius-rs` and `--rs`, `pad` to `--spacing-pad` and `--pad`.
 */
export function generateCssVariables(tokens: TokenInput): Record<string, string> {
  const vars: Record<string, string> = {};
  if (!tokens || typeof tokens !== "object") return vars;

  const colorKeys = new Set([
    "bg", "surf", "surf2", "ink", "ink2", "pri", "onpri", "priC", "onpriC",
    "sec", "onsec", "sec2", "bdg", "onbdg", "info", "outv", "ok", "err", "warn",
    "sky1", "sky2", "gnd", "gnd2"
  ]);
  const radiusKeys = new Set(["rs", "rl", "rp", "navr"]);
  const spacingKeys = new Set(["pad", "gap"]);

  for (const [key, val] of Object.entries(tokens)) {
    if (val == null) continue;
    const strVal = String(val);

    if (key.startsWith("--")) {
      vars[key] = strVal;
      continue;
    }

    // Direct mapping for backward compatibility
    vars[`--${key}`] = strVal;

    // Categorical custom properties as required by design specification
    if (colorKeys.has(key)) {
      vars[`--color-${key}`] = strVal;
    } else if (radiusKeys.has(key)) {
      vars[`--radius-${key}`] = strVal;
    } else if (spacingKeys.has(key)) {
      vars[`--spacing-${key}`] = strVal;
    } else if (key === "font") {
      vars["--font-body"] = strVal;
    } else if (key === "dfont") {
      vars["--font-display"] = strVal;
    } else if (key === "tls") {
      vars["--tracking-tls"] = strVal;
    }
  }

  return vars;
}

/**
 * Converts a design token dictionary into a valid CSS rule block string.
 */
export function themeToCssString(tokens: TokenInput, selector = ":root"): string {
  const vars = generateCssVariables(tokens);
  const rules = Object.entries(vars)
    .map(([k, v]) => `  ${k}: ${v};`)
    .join("\n");
  return `${selector} {\n${rules}\n}`;
}

/**
 * Safely injects generated CSS custom properties into a DOM element's style.
 */
export function applyCssVariables(
  tokens: TokenInput,
  element?: { style: { setProperty(name: string, value: string): void } } | null
): void {
  const target = element ?? (typeof document !== "undefined" ? document.documentElement : null);
  if (!target || !target.style || typeof target.style.setProperty !== "function") return;
  const vars = generateCssVariables(tokens);
  for (const [key, val] of Object.entries(vars)) {
    target.style.setProperty(key, val);
  }
}
