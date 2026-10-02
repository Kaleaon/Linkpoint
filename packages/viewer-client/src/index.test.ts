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

  it("reconnects automatically using cached credentials when network returns", async () => {
    const events: ViewerEvent[] = [];
    const login = vi.fn().mockResolvedValue({ agentId: "a", sessionId: "s", regionName: "Ahern" });
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const client = new WebViewerClient({
      login, logout: vi.fn(), sendChat, touchObject: vi.fn(), subscribe: () => () => undefined,
    });
    client.subscribe((e) => events.push(e));

    const loginPayload = { grid: "second-life" as const, loginUri: "https://login.agni.lindenlab.com/cgi-bin/login.cgi", username: "Test Resident", password: "secret" };
    await client.execute({ type: "session.login", payload: loginPayload });
    expect(client.connectionState).toBe("connected");

    // Network drops
    await client.execute({ type: "network.changed", payload: { online: false } });
    expect(client.connectionState).toBe("reconnecting");

    // Network restored within 3 seconds
    await client.execute({ type: "network.changed", payload: { online: true } });
    expect(client.connectionState).toBe("connected");
    expect(login).toHaveBeenCalledTimes(2);
    expect(login).toHaveBeenLastCalledWith(loginPayload);
  });

  it("queues outbound messages while offline and flushes them in order upon reconnect", async () => {
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const touchObject = vi.fn().mockResolvedValue(undefined);
    const login = vi.fn().mockResolvedValue({ agentId: "a", sessionId: "s", regionName: "Ahern" });

    const client = new WebViewerClient({
      login, logout: vi.fn(), sendChat, touchObject, subscribe: () => () => undefined,
    });

    const loginPayload = { grid: "second-life" as const, loginUri: "https://login.agni.lindenlab.com/cgi-bin/login.cgi", username: "Test Resident", password: "secret" };
    await client.execute({ type: "session.login", payload: loginPayload });

    // Transition offline
    await client.execute({ type: "network.changed", payload: { online: false } });

    // Queue messages while offline
    await client.execute({ type: "chat.send", payload: { body: "First offline message" } });
    await client.execute({ type: "object.touch", payload: { objectId: "prim-1" } });
    await client.execute({ type: "chat.send", payload: { body: "Second offline message" } });

    expect(client.outboundQueueLength).toBe(3);
    expect(sendChat).not.toHaveBeenCalled();

    // Network restored -> reconnect & flush
    await client.execute({ type: "network.changed", payload: { online: true } });

    expect(client.connectionState).toBe("connected");
    expect(client.outboundQueueLength).toBe(0);
    expect(sendChat).toHaveBeenNthCalledWith(1, "First offline message");
    expect(touchObject).toHaveBeenNthCalledWith(1, "prim-1", undefined);
    expect(sendChat).toHaveBeenNthCalledWith(2, "Second offline message");
  });

  it("discards queued messages older than 60 seconds", async () => {
    let mockTime = 1_000_000;
    const sendChat = vi.fn().mockResolvedValue(undefined);
    const login = vi.fn().mockResolvedValue({ agentId: "a", sessionId: "s", regionName: "Ahern" });

    const client = new WebViewerClient(
      { login, logout: vi.fn(), sendChat, touchObject: vi.fn(), subscribe: () => () => undefined },
      { now: () => mockTime, messageTtlMs: 60_000 }
    );

    await client.execute({ type: "session.login", payload: { grid: "second-life", loginUri: "https://login.example/cgi-bin/login.cgi", username: "u", password: "p" } });
    await client.execute({ type: "network.changed", payload: { online: false } });

    // Send first chat at t=1,000,000
    await client.execute({ type: "chat.send", payload: { body: "Stale message" } });

    // Advance time by 61 seconds (61,000 ms)
    mockTime += 61_000;

    // Send second chat at t=1,061,000
    await client.execute({ type: "chat.send", payload: { body: "Fresh message" } });

    // Reconnect
    await client.execute({ type: "network.changed", payload: { online: true } });

    expect(sendChat).toHaveBeenCalledTimes(1);
    expect(sendChat).toHaveBeenCalledWith("Fresh message");
  });

  it("expires cached session tokens after 15 minutes of continuous offline state", async () => {
    let mockTime = 1_000_000;
    const events: ViewerEvent[] = [];
    const login = vi.fn().mockResolvedValue({ agentId: "a", sessionId: "s", regionName: "Ahern" });

    const client = new WebViewerClient(
      { login, logout: vi.fn(), sendChat: vi.fn(), touchObject: vi.fn(), subscribe: () => () => undefined },
      { now: () => mockTime, tokenTtlMs: 15 * 60_000 }
    );
    client.subscribe((e) => events.push(e));

    await client.execute({ type: "session.login", payload: { grid: "second-life", loginUri: "https://login.example/cgi-bin/login.cgi", username: "u", password: "p" } });

    // Go offline at t=1,000,000
    await client.execute({ type: "network.changed", payload: { online: false } });

    // Advance time by 15 minutes and 1 second (901,000 ms)
    mockTime += 901_000;

    // Network returns after token expired
    await client.execute({ type: "network.changed", payload: { online: true } });

    expect(client.connectionState).toBe("disconnected");
    expect(client.cachedLoginRequest).toBeNull();
    const errorEvent = events.find((e) => e.type === "session.error") as { type: "session.error"; payload: { code: string; message: string } } | undefined;
    expect(errorEvent?.payload.code).toBe("token-expired");
  });

  it("triggers max retry limits and emits max-retries-exceeded error", async () => {
    const events: ViewerEvent[] = [];
    const login = vi.fn().mockRejectedValue(new Error("Network handshake failed"));

    const client = new WebViewerClient(
      { login, logout: vi.fn(), sendChat: vi.fn(), touchObject: vi.fn(), subscribe: () => () => undefined },
      { maxAttempts: 3 }
    );
    client.subscribe((e) => events.push(e));

    // Manually trigger reconnect attempts up to max
    const loginPayload = { grid: "second-life" as const, loginUri: "https://login.example/cgi-bin/login.cgi", username: "u", password: "p" };
    // First login succeeds initially then fails on reconnect
    login.mockResolvedValueOnce({ agentId: "a", sessionId: "s", regionName: "Ahern" });
    await client.execute({ type: "session.login", payload: loginPayload });

    await client.reconnect(); // Attempt 1 fails
    await client.reconnect(); // Attempt 2 fails
    await client.reconnect(); // Attempt 3 fails
    await client.reconnect(); // Attempt 4 > maxAttempts (3)

    expect(client.connectionState).toBe("disconnected");
    const errorEvent = events.find((e) => e.type === "session.error" && e.payload.code === "max-retries-exceeded");
    expect(errorEvent).toBeDefined();
  });
});
