// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from "vitest";
import { act } from "react";
import { app } from "../app";
import { ParcelScreen, parseParcelFlags } from "../../screens/LumiyaTools.jsx";
import { mountScreen, unmount, type Mounted } from "./ui-helpers.tsx";

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: Mounted | null = null;

describe("ParcelScreen Component & Reactive World Store", () => {
  beforeEach(() => {
    if (mounted) {
      unmount(mounted);
      mounted = null;
    }
    app.world.region = null;
  });

  it("parses parcel flags bitmask into human-readable permission labels", () => {
    // 1029 = 1024 (Voice enabled) + 4 (Build allowed) + 1 (Fly enabled)
    const flags = parseParcelFlags(1029);
    expect(flags).toContain("Fly enabled");
    expect(flags).toContain("Build allowed");
    expect(flags).toContain("Voice enabled");
    expect(flags).toContain("Scripts restricted");
  });

  it("renders empty state when no parcel properties exist", async () => {
    mounted = await mountScreen(() => <ParcelScreen />);
    expect(mounted.host.textContent).toContain("No parcel-properties message has been received yet for this region.");
  });

  it("renders land details, ownership fallbacks, prim meter, and flags when parcel is set", async () => {
    app.world.region = {
      name: "Da Boom",
      parcel: {
        id: 101,
        name: "Linden Public Park",
        description: "Community plaza and rest spot",
        area: 4096,
        ownerId: null,
        groupId: null,
        maxPrims: 1875,
        totalPrims: 1240,
        musicUrl: "https://stream.example/radio.mp3",
        parcelFlags: 1029,
      },
    };

    mounted = await mountScreen(() => <ParcelScreen />);

    expect(mounted.host.textContent).toContain("Linden Public Park");
    expect(mounted.host.textContent).toContain("Da Boom · 4096 sq.m.");
    expect(mounted.host.textContent).toContain("Community plaza and rest spot");
    expect(mounted.host.textContent).toContain("Linden Public / Public Land");
    expect(mounted.host.textContent).toContain("No Group Affinity");
    expect(mounted.host.textContent).toContain("1240 / 1875");
    expect(mounted.host.textContent).toContain("66% prim capacity used · 635 prims available");
    expect(mounted.host.textContent).toContain("Voice enabled");
    expect(mounted.host.textContent).toContain("Fly enabled");
    expect(mounted.host.textContent).toContain("Region Audio Stream");
  });

  it("updates automatically when parcel_changed events fire on crossing parcel boundaries", async () => {
    app.world.region = {
      name: "Ahern",
      parcel: {
        id: 1,
        name: "Welcome Area",
        description: "Initial landing parcel",
        area: 1024,
        maxPrims: 468,
        totalPrims: 100,
        musicUrl: "",
        parcelFlags: 1,
      },
    };

    mounted = await mountScreen(() => <ParcelScreen />);
    expect(mounted.host.textContent).toContain("Welcome Area");

    // Simulate avatar crossing boundary to new parcel
    await act(async () => {
      app.world.emit("parcel_changed", {
        id: 2,
        name: "Club Neon",
        description: "Music venue and lounge",
        area: 2048,
        ownerId: "22222222-2222-2222-2222-222222222222",
        groupId: "33333333-3333-3333-3333-333333333333",
        maxPrims: 937,
        totalPrims: 500,
        musicUrl: "https://club.example/stream",
        parcelFlags: 1025,
      });
    });

    expect(mounted.host.textContent).toContain("Club Neon");
    expect(mounted.host.textContent).toContain("Music venue and lounge");
    expect(mounted.host.textContent).toContain("22222222-2222-2222-2222-222222222222");
    expect(mounted.host.textContent).toContain("33333333-3333-3333-3333-333333333333");
    expect(mounted.host.textContent).toContain("500 / 937");
    expect(mounted.host.textContent).toContain("53% prim capacity used · 437 prims available");
  });

  it("allows playing and pausing region audio streams", async () => {
    app.world.region = {
      name: "Club Region",
      parcel: {
        id: 5,
        name: "Club Stream",
        description: "DJ Stream",
        area: 2048,
        musicUrl: "https://dj.example/live",
      },
    };

    mounted = await mountScreen(() => <ParcelScreen />);
    const playBtn = Array.from(mounted.host.querySelectorAll("button")).find((btn) => btn.textContent === "Play");
    expect(playBtn).toBeDefined();

    await act(async () => {
      playBtn?.click();
    });

    const pauseBtn = Array.from(mounted.host.querySelectorAll("button")).find((btn) => btn.textContent === "Pause");
    expect(pauseBtn).toBeDefined();

    await act(async () => {
      pauseBtn?.click();
    });

    const newPlayBtn = Array.from(mounted.host.querySelectorAll("button")).find((btn) => btn.textContent === "Play");
    expect(newPlayBtn).toBeDefined();
  });
});
