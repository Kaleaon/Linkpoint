// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { act, useEffect } from "react";
import { AnnouncerProvider, useAnnouncer } from "../AnnouncerContext.jsx";
import Toast from "../../components/Toast.jsx";
import Radar from "../../screens/Radar.jsx";
import Chat from "../../screens/Chat.jsx";
import { app } from "../../linkpoint/app";
import { mountScreen, unmount, type Mounted } from "../../linkpoint/__tests__/ui-helpers";

let mounted: Mounted | null = null;

beforeEach(() => {
  vi.useFakeTimers();
  app.chat.messages = [];
  if (app.world) {
    app.world.nearbyUsers = [];
    app.world.objects = [];
  }
});

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.useRealTimers();
});

describe("AnnouncerContext & Live Region Announcer", () => {
  it("renders persistent polite and assertive live regions in the DOM tree", async () => {
    function TestComponent() {
      return <div>Test</div>;
    }

    mounted = await mountScreen(TestComponent);

    const statusEl = mounted.host.querySelector('[role="status"]');
    const alertEl = mounted.host.querySelector('[role="alert"]');

    expect(statusEl).not.toBeNull();
    expect(alertEl).not.toBeNull();
    expect(statusEl?.getAttribute("aria-live")).toBe("polite");
    expect(alertEl?.getAttribute("aria-live")).toBe("assertive");
  });

  it("announces polite and assertive messages to persistent live regions", async () => {
    function AnnounceTrigger() {
      const { announce } = useAnnouncer();
      return (
        <div>
          <button onClick={() => announce("Polite notice", "polite")}>Polite</button>
          <button onClick={() => announce("Assertive warning", "assertive")}>Assertive</button>
        </div>
      );
    }

    mounted = await mountScreen(AnnounceTrigger);

    const politeBtn = mounted.host.querySelector("button:nth-child(1)") as HTMLButtonElement;
    const assertiveBtn = mounted.host.querySelector("button:nth-child(2)") as HTMLButtonElement;

    // Trigger polite announcement
    await act(async () => {
      politeBtn.click();
      vi.advanceTimersByTime(100);
    });

    const statusEl = mounted.host.querySelector('.sr-only-announcer [role="status"]');
    expect(statusEl?.textContent).toBe("Polite notice");

    // Trigger assertive announcement
    await act(async () => {
      assertiveBtn.click();
      vi.advanceTimersByTime(100);
    });

    const alertEl = mounted.host.querySelector('.sr-only-announcer [role="alert"]');
    expect(alertEl?.textContent).toBe("Assertive warning");
  });

  it("queues and debounces rapid consecutive status updates to prevent speech truncation", async () => {
    function RapidAnnouncer() {
      const { announce } = useAnnouncer();
      useEffect(() => {
        announce("First message", "polite");
        announce("Second message", "polite");
      }, [announce]);
      return <div>Rapid test</div>;
    }

    mounted = await mountScreen(RapidAnnouncer);

    const statusEl = mounted.host.querySelector('.sr-only-announcer [role="status"]');

    // First message processed
    await act(async () => {
      vi.advanceTimersByTime(100);
    });
    expect(statusEl?.textContent).toBe("First message");

    // Advance timer past interval so second message in queue processes
    await act(async () => {
      vi.advanceTimersByTime(1000);
    });
    expect(statusEl?.textContent).toBe("Second message");
  });

  it("does not shift keyboard focus when status updates are dispatched", async () => {
    function FocusTester() {
      const { announce } = useAnnouncer();
      return (
        <div>
          <button id="focus-target" onClick={() => announce("Background event", "polite")}>
            Focus Target
          </button>
        </div>
      );
    }

    mounted = await mountScreen(FocusTester);

    const button = mounted.host.querySelector("#focus-target") as HTMLButtonElement;
    button.focus();
    expect(document.activeElement).toBe(button);

    await act(async () => {
      button.click();
      vi.advanceTimersByTime(100);
    });

    // Active element must remain unchanged
    expect(document.activeElement).toBe(button);
  });

  it("Toast component keeps persistent role='status' live region mounted when empty and visible", async () => {
    mounted = await mountScreen(Toast);

    const toastContainer = mounted.host.querySelector('[role="status"]');
    expect(toastContainer).not.toBeNull();
    expect(toastContainer?.textContent).toBe("");

    // Set toast message via app context
    await act(async () => {
      mounted!.ctx.current.actions.notify("Teleport completed");
      vi.advanceTimersByTime(50);
    });

    expect(toastContainer?.textContent).toBe("Teleport completed");

    // Wait for toast to dismiss
    await act(async () => {
      vi.advanceTimersByTime(2500);
    });

    // Container must remain in DOM after toast disappears
    expect(mounted.host.querySelector('[role="status"]')).not.toBeNull();
    expect(mounted.host.querySelector('[role="status"]')?.textContent).toBe("");
  });

  it("Radar screen announces when new residents enter proximity range", async () => {
    mounted = await mountScreen(Radar);

    // Simulate new user entering nearby users range
    await act(async () => {
      app.world.nearbyUsers = [{ id: "user-101", name: "Kaleaon Resident", distance: 15 }];
      app.world.emit("nearby_changed", app.world.nearbyUsers);
    });

    await act(async () => {
      vi.advanceTimersByTime(200);
    });

    const statusEl = mounted.host.querySelector('.sr-only-announcer [role="status"]');
    expect(statusEl?.textContent).toBe("Kaleaon Resident entered proximity range");
  });

  it("Chat screen announces incoming direct messages and notices through central announcer", async () => {
    mounted = await mountScreen(Chat);

    // Simulate incoming direct message from another resident
    await act(async () => {
      app.chat.addMessage({
        id: "msg-101",
        sender: "Tester Avatar",
        senderId: "avatar-99",
        text: "Hello from the grid!",
        type: "im",
        timestamp: Date.now(),
      });
      app.chat.emit("message_received", { id: "msg-101" });
    });

    await act(async () => {
      vi.advanceTimersByTime(200);
    });

    const statusEl = mounted.host.querySelector('.sr-only-announcer [role="status"]');
    expect(statusEl?.textContent).toBe("Direct message from Tester Avatar: Hello from the grid!");

    // Verify transcript section no longer has ad-hoc aria-live attribute
    const transcriptSection = mounted.host.querySelector("section");
    expect(transcriptSection?.getAttribute("aria-live")).toBeNull();
  });
});
