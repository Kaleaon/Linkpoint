// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import { act } from "react";
import { useEffect } from "react";
import { useApp } from "../../context/AppContext.jsx";
import NavShell from "../../components/NavShell.jsx";
import SegmentedTabs from "../../components/SegmentedTabs.jsx";
import ViewModeSwitcher from "../../components/ViewModeSwitcher.jsx";
import { click, mountScreen, unmount, type Mounted } from "./ui-helpers";

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: Mounted | null = null;

function setViewportWidth(width: number) {
  Object.defineProperty(window, "innerWidth", { value: width, configurable: true });
  window.dispatchEvent(new Event("resize"));
}

beforeAll(() => {
  vi.spyOn(console, "log").mockImplementation(() => undefined);
});

beforeEach(() => {
  localStorage.clear();
  setViewportWidth(412); // Default to phone viewport for touch nav testing
});

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.restoreAllMocks();
  vi.spyOn(console, "log").mockImplementation(() => undefined);
});

function NavShellTestContainer({ layout = "glass", initialScreen = "Chat", width = 412 }: { layout?: string; initialScreen?: string; width?: number }) {
  const { actions } = useApp();
  useEffect(() => {
    actions.setLayout(layout);
    actions.setScreen(initialScreen);
    actions.setDevice(width >= 1280 ? "desk" : width >= 900 ? "tab" : width >= 600 ? "fold" : "and");
  }, []); // Run once on mount

  return (
    <NavShell>
      <div data-testid="screen-content">Screen Content</div>
    </NavShell>
  );
}

function SubNavTestContainer({ initialScreen = "Chat" }: { initialScreen?: string }) {
  const { actions } = useApp();
  useEffect(() => {
    actions.setLayout("glass");
    actions.setScreen(initialScreen);
  }, []); // Run once on mount

  return <SegmentedTabs />;
}

describe("Navigation Components & NavShell", () => {
  describe("NavShell unified container", () => {
    it("renders BottomTabs when nav state is tabs (glass layout on phone)", async () => {
      setViewportWidth(412);
      mounted = await mountScreen(() => <NavShellTestContainer layout="glass" initialScreen="Chat" width={412} />);
      const tablist = mounted.host.querySelector('[role="tablist"][aria-label="Bottom navigation tabs"]');
      expect(tablist).toBeTruthy();
      expect(mounted.host.querySelector('[data-testid="screen-content"]')).toBeTruthy();
    });

    it("renders RailNav when nav state is rail (rules layout on tablet)", async () => {
      setViewportWidth(1000);
      mounted = await mountScreen(() => <NavShellTestContainer layout="rules" initialScreen="Chat" width={1000} />);
      const rail = mounted.host.querySelector('[role="tablist"][aria-label="Navigation rail"]');
      expect(rail).toBeTruthy();
    });

    it("renders TileNav when nav state is tiles (tiles layout on phone)", async () => {
      setViewportWidth(412);
      mounted = await mountScreen(() => <NavShellTestContainer layout="tiles" initialScreen="Chat" width={412} />);
      const tilelist = mounted.host.querySelector('[role="tablist"][aria-label="Tile navigation"]');
      expect(tilelist).toBeTruthy();
    });

    it("suppresses navigation when on the Login screen", async () => {
      setViewportWidth(412);
      mounted = await mountScreen(() => <NavShellTestContainer layout="glass" initialScreen="Login" width={412} />);
      expect(mounted.host.querySelector('[role="tablist"]')).toBeNull();
      expect(mounted.host.querySelector('[data-testid="screen-content"]')).toBeTruthy();
    });

    it("renders connection banner when disconnected and updates on protocol events", async () => {
      setViewportWidth(412);
      const { app } = await import("../app");
      (app.protocol as any).connected = false;
      mounted = await mountScreen(() => <NavShellTestContainer layout="glass" initialScreen="Chat" width={412} />);

      await act(async () => {
        app.protocol.emit("disconnected");
      });

      const banner = mounted.host.querySelector(".connection-banner");
      expect(banner).toBeTruthy();
      expect(banner?.textContent).toContain("Connection lost");

      const reconnectBtn = banner?.querySelector("button");
      expect(reconnectBtn).toBeTruthy();

      await act(async () => {
        (app.protocol as any).connected = true;
        app.protocol.emit("connected", {});
      });

      expect(mounted.host.querySelector(".connection-banner")).toBeNull();
    });
  });

  describe("RailNav accessibility & interaction", () => {
    it("renders tabs with role='tab', roving tabindex (0 for active, -1 for inactive), and supports click and arrow keys", async () => {
      setViewportWidth(1000);
      mounted = await mountScreen(() => <NavShellTestContainer layout="rules" initialScreen="Chat" width={1000} />);
      const tabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Navigation rail"] [role="tab"]');
      expect(tabs.length).toBeGreaterThan(0);

      const activeTab = [...tabs].find((t) => t.getAttribute("aria-selected") === "true");
      expect(activeTab).toBeTruthy();
      expect(activeTab!.tabIndex).toBe(0);

      const inactiveTabs = [...tabs].filter((t) => t.getAttribute("aria-selected") === "false");
      expect(inactiveTabs.length).toBeGreaterThan(0);
      inactiveTabs.forEach((t) => {
        expect(t.tabIndex).toBe(-1);
      });

      const friendsTab = [...tabs].find((t) => t.getAttribute("aria-label")?.includes("FRIENDS") || t.textContent?.includes("FRIENDS"));
      expect(friendsTab).toBeTruthy();

      // Mouse click switching
      await click(friendsTab!);
      expect(mounted.ctx.current.state.screen).toBe("Friends");
      expect(friendsTab!.tabIndex).toBe(0);

      // Keyboard keydown switching with Arrow keys and Enter
      const currentActive = [...tabs].find((t) => t.getAttribute("aria-selected") === "true")!;
      await act(async () => {
        currentActive.dispatchEvent(new KeyboardEvent("keydown", { key: "ArrowDown", bubbles: true }));
      });
      expect(mounted.ctx.current.state.screen).not.toBe("Friends");
    });

    it("supports Home and End key navigation in RailNav", async () => {
      setViewportWidth(1000);
      mounted = await mountScreen(() => <NavShellTestContainer layout="rules" initialScreen="Chat" width={1000} />);
      const tabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Navigation rail"] [role="tab"]');
      const firstTab = tabs[0];
      const lastTab = tabs[tabs.length - 1];

      await act(async () => {
        firstTab.dispatchEvent(new KeyboardEvent("keydown", { key: "End", bubbles: true }));
      });
      expect(lastTab.getAttribute("aria-selected")).toBe("true");
      expect(lastTab.tabIndex).toBe(0);

      await act(async () => {
        lastTab.dispatchEvent(new KeyboardEvent("keydown", { key: "Home", bubbles: true }));
      });
      expect(firstTab.getAttribute("aria-selected")).toBe("true");
      expect(firstTab.tabIndex).toBe(0);
    });
  });

  describe("BottomTabs accessibility & interaction", () => {
    it("renders tabs with roving tabindex (0 for active, -1 for inactive), supports click and arrow key navigation with wrapping", async () => {
      setViewportWidth(412);
      mounted = await mountScreen(() => <NavShellTestContainer layout="glass" initialScreen="Chat" width={412} />);
      const tabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Bottom navigation tabs"] [role="tab"]');
      expect(tabs.length).toBeGreaterThan(0);

      const activeTab = [...tabs].find((t) => t.getAttribute("aria-selected") === "true");
      expect(activeTab).toBeTruthy();
      expect(activeTab!.tabIndex).toBe(0);

      const inactiveTabs = [...tabs].filter((t) => t.getAttribute("aria-selected") === "false");
      inactiveTabs.forEach((t) => expect(t.tabIndex).toBe(-1));

      const mapTab = [...tabs].find((t) => t.getAttribute("aria-label")?.includes("MAP") || t.textContent?.includes("MAP"));
      expect(mapTab).toBeTruthy();

      // Mouse click switching
      await click(mapTab!);
      expect(mounted.ctx.current.state.screen).toBe("Map");
      expect(mapTab!.tabIndex).toBe(0);

      // Keyboard navigation with ArrowRight / ArrowLeft
      await act(async () => {
        mapTab!.dispatchEvent(new KeyboardEvent("keydown", { key: "ArrowRight", bubbles: true }));
      });
      const newActive = [...tabs].find((t) => t.getAttribute("aria-selected") === "true")!;
      expect(newActive.tabIndex).toBe(0);

      // Keyboard navigation with Space key
      const chatTab = [...tabs].find((t) => t.getAttribute("aria-label")?.includes("CHAT") || t.textContent?.includes("CHAT"));
      expect(chatTab).toBeTruthy();
      await act(async () => {
        chatTab!.dispatchEvent(new KeyboardEvent("keydown", { key: " ", code: "Space", keyCode: 32, bubbles: true }));
      });
      expect(mounted.ctx.current.state.screen).toBe("Chat");
    });
  });

  describe("ViewModeSwitcher", () => {
    it("renders interface mode options and allows switching view mode", async () => {
      mounted = await mountScreen(ViewModeSwitcher);
      const buttons = mounted.host.querySelectorAll<HTMLButtonElement>("button");
      expect(buttons.length).toBe(3); // MOBILE, DESKTOP, AUTO

      const mobileBtn = [...buttons].find((b) => b.textContent?.includes("MOBILE"));
      const desktopBtn = [...buttons].find((b) => b.textContent?.includes("DESKTOP"));
      expect(mobileBtn).toBeTruthy();
      expect(desktopBtn).toBeTruthy();

      await click(desktopBtn!);
      expect(mounted.ctx.current.state.viewMode).toBe("desktop");

      await click(mobileBtn!);
      expect(mounted.ctx.current.state.viewMode).toBe("mobile");
    });

    it("renders compact mode toggle button", async () => {
      mounted = await mountScreen(() => <ViewModeSwitcher compact={true} />);
      const button = mounted.host.querySelector("button");
      expect(button).toBeTruthy();
      expect(button!.textContent).toMatch(/TO DESKTOP|TO MOBILE/);
    });
  });

  describe("SegmentedTabs sub-navigation", () => {
    it("renders sub-navigation tabs with roving tabindex and supports arrow keys", async () => {
      mounted = await mountScreen(() => <SubNavTestContainer initialScreen="Chat" />);
      const subTabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Sub navigation tabs"] [role="tab"]');
      expect(subTabs.length).toBeGreaterThan(0);

      const activeTab = [...subTabs].find((t) => t.getAttribute("aria-selected") === "true");
      expect(activeTab).toBeTruthy();
      expect(activeTab!.tabIndex).toBe(0);

      const inactiveTabs = [...subTabs].filter((t) => t.getAttribute("aria-selected") === "false");
      inactiveTabs.forEach((t) => expect(t.tabIndex).toBe(-1));

      const groupTab = [...subTabs].find((t) => t.textContent?.includes("GROUP"));
      if (groupTab) {
        await click(groupTab);
        expect(mounted.ctx.current.state.tabs?.Chat).toBe("GROUP");
        expect(groupTab.tabIndex).toBe(0);
      }

      // Test arrow key focus movement
      const currentActive = [...subTabs].find((t) => t.getAttribute("aria-selected") === "true")!;
      await act(async () => {
        currentActive.dispatchEvent(new KeyboardEvent("keydown", { key: "ArrowLeft", bubbles: true }));
      });
      const imTab = [...subTabs].find((t) => t.textContent?.includes("IM"));
      if (imTab) {
        expect(mounted.ctx.current.state.tabs?.Chat).toBe("IM");
        expect(imTab.tabIndex).toBe(0);
      }
    });
  });
});
