import { describe, expect, it } from "vitest";
import { initialViewerState, reduceViewerEvent } from "./index";

describe("reduceViewerEvent", () => {
  it("projects session and scene events without mutating prior state", () => {
    const connected = reduceViewerEvent(initialViewerState, {
      type: "session.connected",
      payload: { agentId: "agent", sessionId: "session", regionName: "Ahern" },
    });
    const populated = reduceViewerEvent(connected, {
      type: "scene.upsert",
      payload: { id: "prim-1", position: [1, 2, 3] },
    });

    expect(connected.connection).toBe("connected");
    expect(connected.scene).toEqual({});
    expect(populated.scene["prim-1"]?.position).toEqual([1, 2, 3]);
  });

  it("clears transient world state on disconnect", () => {
    const state = reduceViewerEvent(
      { ...initialViewerState, scene: { prim: { id: "prim", position: [0, 0, 0] } } },
      { type: "session.disconnected", payload: { reason: "network" } },
    );
    expect(state.scene).toEqual({});
    expect(state.disconnectReason).toBe("network");
  });
});
