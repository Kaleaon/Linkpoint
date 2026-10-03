import { describe, expect, it } from "vitest";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { GENERATED_TOKENS } from "../tokens/tokens.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const jsonPath = path.resolve(__dirname, "../tokens/tokens.json");

describe("Generated Tokens Integrity & Cross-Platform Parity", () => {
  it("should match tokens.json values exactly", () => {
    const rawData = fs.readFileSync(jsonPath, "utf-8");
    const jsonTokens = JSON.parse(rawData);

    expect(GENERATED_TOKENS.color.status.online).toBe(jsonTokens.color.status.online.value);
    expect(GENERATED_TOKENS.color.status.offline).toBe(jsonTokens.color.status.offline.value);
    expect(GENERATED_TOKENS.color.status.degraded).toBe(jsonTokens.color.status.degraded.value);
    expect(GENERATED_TOKENS.color.status.unknown).toBe(jsonTokens.color.status.unknown.value);

    expect(GENERATED_TOKENS.color.editor.background).toBe(jsonTokens.color.editor.background.value);
    expect(GENERATED_TOKENS.color.editor.appbarBackground).toBe(jsonTokens.color.editor.appbarBackground.value);
    expect(GENERATED_TOKENS.color.editor.panelBackground).toBe(jsonTokens.color.editor.panelBackground.value);
    expect(GENERATED_TOKENS.color.editor.divider).toBe(jsonTokens.color.editor.divider.value);
    expect(GENERATED_TOKENS.color.editor.text).toBe(jsonTokens.color.editor.text.value);

    expect(GENERATED_TOKENS.color.scene.background).toBe(jsonTokens.color.scene.background.value);
    expect(GENERATED_TOKENS.color.scene.shadow).toBe(jsonTokens.color.scene.shadow.value);

    expect(GENERATED_TOKENS.color.aurora.color1).toBe(jsonTokens.color.aurora.color1.value);
    expect(GENERATED_TOKENS.color.glass.background).toBe(jsonTokens.color.glass.background.value);
  });
});
