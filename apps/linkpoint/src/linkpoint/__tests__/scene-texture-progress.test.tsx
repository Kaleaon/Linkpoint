// @vitest-environment jsdom
import { beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import { act } from "react";
import { app } from "../app";
import { Scene3D } from "../scene-3d";
import { World3DActionBar } from "../../screens/World3D.jsx";
import { mountScreen, unmount, type Mounted } from "./ui-helpers";

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: Mounted | null = null;

beforeAll(() => {
  vi.spyOn(console, "log").mockImplementation(() => undefined);
});

describe("Scene3D Texture Progress & World3DActionBar", () => {
  it("emits scene:texture-progress events from Scene3D", () => {
    const mockGraphics: any = {
      createTexture: vi.fn().mockReturnValue("tex_mock_1"),
    };
    const mockCamera: any = {};
    const scene = new Scene3D(mockGraphics, mockCamera);

    let capturedProgress: any = null;
    scene.on("scene:texture-progress", (p) => {
      capturedProgress = p;
    });

    scene.addAssetTexture("asset_101", 64, 64, new Uint8Array(64 * 64 * 4));

    expect(capturedProgress).not.toBeNull();
    expect(capturedProgress.decoded).toBe(1);
    expect(capturedProgress.stage).toBe("Decoded");
  });

  it("World3DActionBar renders live texture progress when emitted", async () => {
    (app.auth as any).isLoggedIn = () => true;

    mounted = await mountScreen(() => <World3DActionBar />);
    expect(mounted.host.textContent).toContain("CONNECTED");

    await act(async () => {
      app.world.emit("scene:texture-progress", {
        pending: 2,
        decoded: 5,
        total: 7,
        activeProgress: 71,
        stage: "Decompressing",
      });
    });

    expect(mounted.host.textContent).toContain("TEXTURES DECODED: 5/7 (71%)");

    unmount(mounted);
    mounted = null;
  });
});
