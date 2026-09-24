import { describe, expect, it } from "vitest";
import { getSceneBounds } from "./index";

describe("getSceneBounds", () => {
  it("provides a useful default for an empty region", () => {
    expect(getSceneBounds([])).toEqual({ center: [128, 128, 0], radius: 32 });
  });

  it("includes entity scale when framing a scene", () => {
    const bounds = getSceneBounds([
      { id: "a", position: [0, 0, 0], scale: [2, 2, 2] },
      { id: "b", position: [10, 4, 2], scale: [4, 2, 2] },
    ]);
    expect(bounds.center).toEqual([5.5, 2, 1]);
    expect(bounds.radius).toBeGreaterThanOrEqual(8);
  });
});
