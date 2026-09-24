import { describe, expect, it, vi } from "vitest";
import { MockViewerClient, TauriViewerClient, WebViewerClient } from "./index";
import type { ViewerEvent } from "../../viewer-types/src/index";

describe("ViewerClient adapters", () => {
  it("keeps the mock deterministic", async () => {
    const client = new MockViewerClient();
    await client.execute({ type: "chat.send", payload: { body: "hello" } });
    expect(client.commands).toEqual([{ type: "chat.send", payload: { body: "hello" } }]);
  });

  it("projects browser login through the wire contract", async () => {
    const events: ViewerEvent[] = [];
    const client = new WebViewerClient({
      login: vi.fn().mockResolvedValue({ agentId: "a", sessionId: "s", regionName: "Ahern" }),
      logout: vi.fn(), sendChat: vi.fn(), touchObject: vi.fn(), subscribe: () => () => undefined,
    });
    client.subscribe((event) => events.push(event));
    await client.execute({ type: "session.login", payload: { grid: "second-life", loginUri: "https://login.agni.lindenlab.com/cgi-bin/login.cgi", username: "Test Resident", password: "secret" } });
    expect(events.map(({ type }) => type)).toEqual(["session.connecting", "session.connected"]);
    expect(JSON.parse(JSON.stringify(events[1]))).toEqual({ type: "session.connected", payload: { agentId: "a", sessionId: "s", regionName: "Ahern" } });
  });

  it("serializes a command through Tauri without leaking another API", async () => {
    const invoke = vi.fn().mockResolvedValue(undefined);
    const client = await TauriViewerClient.create({ invoke, listen: vi.fn().mockResolvedValue(() => undefined) });
    await client.execute({ type: "object.touch", payload: { objectId: "prim", face: 2 } });
    expect(invoke).toHaveBeenCalledWith("viewer_execute", { command: { type: "object.touch", payload: { objectId: "prim", face: 2 } } });
  });
});
