import { describe, it, expect } from "vitest";

// Test subpath imports directly
import {
  LAYOUTS,
  PALETTES,
  DEVICES,
  themeNames,
  computeThemeTokens,
  ensureMinContrast,
  contrastRatio,
  meetsAA,
  inkOn
} from "../tokens/index.js";

import {
  generateCssVariables,
  themeToCssString,
  applyCssVariables
} from "../css/index.js";

import {
  LayoutProvider,
  useThemeTokens,
  useLayoutMode,
  Card,
  ConsoleFrame,
  RailNav,
  TileNav,
  BottomTabs,
  DeviceFrame
} from "../react/index.js";

import {
  LayoutProvider as LayoutProviderLayout,
  ConsoleFrame as ConsoleFrameLayout
} from "../layout/index.js";

import { loadTheme } from "../index.js";

describe("@linkpoint/design-system subpath exports & functional requirements", () => {
  it("Requirement 1: package exports tokens, css, layout, react, and root", () => {
    expect(LAYOUTS).toBeDefined();
    expect(generateCssVariables).toBeDefined();
    expect(LayoutProvider).toBeDefined();
    expect(LayoutProviderLayout).toBe(LayoutProvider);
    expect(ConsoleFrameLayout).toBe(ConsoleFrame);
  });

  it("Requirement 2: ./tokens exports typed registries and embedded WCAG contrast logic", () => {
    expect(LAYOUTS.terminal).toBeDefined();
    expect(PALETTES.ink).toBeDefined();
    expect(DEVICES.ios).toBeDefined();
    expect(themeNames.length).toBeGreaterThan(0);

    // WCAG contrast enforcement check
    const bg = "#0A1112";
    const fg = "#6CFF9A";
    const ratio = contrastRatio(fg, bg);
    expect(ratio).toBeGreaterThanOrEqual(4.5);
    expect(meetsAA(fg, bg)).toBe(true);

    const ink = inkOn(bg, ["#6CFF9A"]);
    expect(ink).toBeDefined();

    const lowContrastFg = "#112222";
    const adjusted = ensureMinContrast(lowContrastFg, bg, 4.5);
    expect(contrastRatio(adjusted, bg)).toBeGreaterThanOrEqual(4.5);

    const tokens = computeThemeTokens("terminal", "ink");
    expect(tokens.bg).toBe("#0A1112");
    expect(tokens.pri).toBe("#6CFF9A");
  });

  it("Requirement 3: ./css supplies generateCssVariables and themeToCssString mapping theme tokens", () => {
    const tokens = computeThemeTokens("terminal", "ink");
    const vars = generateCssVariables(tokens);

    expect(vars["--color-pri"]).toBe("#6CFF9A");
    expect(vars["--radius-rs"]).toBe("4px");
    expect(vars["--spacing-pad"]).toBe("12px");
    expect(vars["--font-body"]).toBeDefined();

    const cssString = themeToCssString(tokens, ":root");
    expect(cssString).toContain(":root {");
    expect(cssString).toContain("--color-pri: #6CFF9A;");
    expect(cssString).toContain("--radius-rs: 4px;");
    expect(cssString).toContain("--spacing-pad: 12px;");

    // applyCssVariables should safely handle non-DOM environment without throwing
    expect(() => applyCssVariables(tokens)).not.toThrow();
  });

  it("Requirement 4: ./layout and ./react export UI primitives and hooks", () => {
    expect(Card).toBeDefined();
    expect(ConsoleFrame).toBeDefined();
    expect(RailNav).toBeDefined();
    expect(TileNav).toBeDefined();
    expect(BottomTabs).toBeDefined();
    expect(DeviceFrame).toBeDefined();
    expect(useThemeTokens).toBeDefined();
    expect(useLayoutMode).toBeDefined();
  });

  it("Constraint: theme loader compatibility", async () => {
    const artDecoTheme = await loadTheme("art-deco");
    expect(artDecoTheme).toBeDefined();
  });
});
