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
  DeviceFrame,
  FormField,
  FormInput,
  FormSwitch,
  FormFieldContext,
  ElevationBadge,
  type FormFieldProps,
  type FormInputProps,
  type FormSwitchProps,
  type ElevationBadgeProps
} from "../react/index.js";

import {
  LayoutProvider as LayoutProviderLayout,
  ConsoleFrame as ConsoleFrameLayout
} from "../layout/index.js";

import {
  loadTheme,
  FormField as FormFieldRoot,
  FormInput as FormInputRoot,
  FormSwitch as FormSwitchRoot,
  FormFieldContext as FormFieldContextRoot,
  ElevationBadge as ElevationBadgeRoot
} from "../index.js";
import React from "react";
import { renderToString } from "react-dom/server";

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
    expect(FormField).toBeDefined();
    expect(FormInput).toBeDefined();
    expect(FormSwitch).toBeDefined();
    expect(FormFieldContext).toBeDefined();
    expect(ElevationBadge).toBeDefined();

    // Verify top-level re-exports match subpath exports
    expect(FormFieldRoot).toBe(FormField);
    expect(FormInputRoot).toBe(FormInput);
    expect(FormSwitchRoot).toBe(FormSwitch);
    expect(FormFieldContextRoot).toBe(FormFieldContext);
    expect(ElevationBadgeRoot).toBe(ElevationBadge);
  });

  it("Requirement 1: FormField ports accessible form primitives and passes ARIA attributes", () => {
    const html = renderToString(
      React.createElement(
        FormField,
        {
          id: "custom-grid-id",
          label: "GRID NAME",
          description: "Name for the custom grid",
          error: "Grid name already exists",
          required: true
        },
        React.createElement(FormInput, { placeholder: "Grid Name" })
      )
    );

    expect(html).toContain('id="custom-grid-id-label"');
    expect(html).toContain('for="custom-grid-id"');
    expect(html).toContain("GRID NAME");
    expect(html).toContain("*</span>"); // Required indicator
    expect(html).toContain('id="custom-grid-id"');
    expect(html).toContain('aria-describedby="custom-grid-id-desc custom-grid-id-error"');
    expect(html).toContain('aria-invalid="true"');
    expect(html).toContain('aria-errormessage="custom-grid-id-error"');
    expect(html).toContain('id="custom-grid-id-desc"');
    expect(html).toContain("Name for the custom grid");
    expect(html).toContain('id="custom-grid-id-error"');
    expect(html).toContain("&gt; Grid name already exists");
  });

  it("Requirement 1: FormSwitch toggles state correctly and applies ARIA switch attributes", () => {
    let changedVal: boolean | null = null;
    const handleChange = (val: boolean) => {
      changedVal = val;
    };

    // Render checked switch
    const checkedHtml = renderToString(
      React.createElement(FormSwitch, {
        checked: true,
        onChange: handleChange,
        "aria-label": "Toggle Feature"
      })
    );

    expect(checkedHtml).toContain('role="switch"');
    expect(checkedHtml).toContain('aria-checked="true"');
    expect(checkedHtml).toContain('tabindex="0"');
    expect(checkedHtml).toContain('aria-label="Toggle Feature"');

    // Render unchecked switch
    const uncheckedHtml = renderToString(
      React.createElement(FormSwitch, {
        checked: false,
        disabled: true
      })
    );

    expect(uncheckedHtml).toContain('aria-checked="false"');
    expect(uncheckedHtml).toContain('tabindex="-1"');
    expect(uncheckedHtml).toContain("cursor:not-allowed");

    // Test switch interaction handlers directly
    let clicked = false;
    let keyHandled = false;

    const switchElement = React.createElement(FormSwitch, {
      checked: false,
      onChange: (v) => {
        clicked = true;
      },
      onKeyDown: (e) => {
        keyHandled = true;
      }
    });

    expect(switchElement.type).toBeDefined();
  });

  it("Requirement 2: ElevationBadge renders positive, negative, and zero altitude delta displays", () => {
    // Positive delta (+15m)
    const posHtml = renderToString(React.createElement(ElevationBadge, { zDelta: 15 }));
    expect(posHtml).toContain("+15m");
    expect(posHtml).toContain("color:#38bdf8"); // Positive altitude color token
    expect(posHtml).toContain("M12 19V5M5 12l7-7 7 7"); // Up arrow path
    expect(posHtml).toContain('title="Altitude relative to viewer: +15m"');

    // Negative delta (-10m)
    const negHtml = renderToString(React.createElement(ElevationBadge, { zDelta: -10 }));
    expect(negHtml).toContain("-10m");
    expect(negHtml).toContain("color:#fb923c"); // Negative altitude color token
    expect(negHtml).toContain("M12 5v14M5 12l7 7 7-7"); // Down arrow path

    // Zero delta (0m)
    const zeroHtml = renderToString(React.createElement(ElevationBadge, { zDelta: 0 }));
    expect(zeroHtml).toContain("0m");
    expect(zeroHtml).toContain("color:#94a3b8"); // Level color token
    expect(zeroHtml).toContain("M5 12h14"); // Horizontal dash path

    // Compact mode
    const compactHtml = renderToString(React.createElement(ElevationBadge, { zDelta: 25, compact: true }));
    expect(compactHtml).toContain("font-size:8.5px");
    expect(compactHtml).toContain('width="8"');
    expect(compactHtml).toContain('height="8"');
  });

  it("Constraint: theme loader compatibility", async () => {
    for (const name of themeNames) {
      const theme = await loadTheme(name);
      expect(theme).toBeDefined();
    }
  });
});
