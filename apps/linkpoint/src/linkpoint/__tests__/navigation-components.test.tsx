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
  });

  describe("RailNav accessibility & interaction", () => {
    it("renders tabs with role='tab', tabIndex={0}, aria-selected, and supports navigation on click and keyboard keydown", async () => {
      setViewportWidth(1000);
      mounted = await mountScreen(() => <NavShellTestContainer layout="rules" initialScreen="Chat" width={1000} />);
      const tabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Navigation rail"] [role="tab"]');
      expect(tabs.length).toBeGreaterThan(0);

      const activeTab = [...tabs].find((t) => t.getAttribute("aria-selected") === "true");
      expect(activeTab).toBeTruthy();

      const friendsTab = [...tabs].find((t) => t.getAttribute("aria-label")?.includes("FRIENDS") || t.textContent?.includes("FRIENDS"));
      expect(friendsTab).toBeTruthy();
      expect(friendsTab!.tabIndex).toBe(0);

      // Mouse click switching
      await click(friendsTab!);
      expect(mounted.ctx.current.state.screen).toBe("Friends");

      // Keyboard keydown switching with Enter key
      const mapTab = [...tabs].find((t) => t.getAttribute("aria-label")?.includes("MAP") || t.textContent?.includes("MAP"));
      expect(mapTab).toBeTruthy();
      await act(async () => {
        mapTab!.dispatchEvent(new KeyboardEvent("keydown", { key: "Enter", code: "Enter", keyCode: 13, bubbles: true }));
      });
      expect(mounted.ctx.current.state.screen).toBe("Map");
    });
  });

  describe("BottomTabs accessibility & interaction", () => {
    it("renders tabs with role='tab', tabIndex={0}, and supports click and Space key selection", async () => {
      setViewportWidth(412);
      mounted = await mountScreen(() => <NavShellTestContainer layout="glass" initialScreen="Chat" width={412} />);
      const tabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Bottom navigation tabs"] [role="tab"]');
      expect(tabs.length).toBeGreaterThan(0);

      const mapTab = [...tabs].find((t) => t.getAttribute("aria-label")?.includes("MAP") || t.textContent?.includes("MAP"));
      expect(mapTab).toBeTruthy();
      expect(mapTab!.tabIndex).toBe(0);

      // Mouse click switching
      await click(mapTab!);
      expect(mounted.ctx.current.state.screen).toBe("Map");

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
    it("renders sub-navigation tabs for Chat screen and allows switching sub-views", async () => {
      mounted = await mountScreen(() => <SubNavTestContainer initialScreen="Chat" />);
      const subTabs = mounted.host.querySelectorAll<HTMLElement>('[role="tablist"][aria-label="Sub navigation tabs"] [role="tab"]');
      expect(subTabs.length).toBeGreaterThan(0);

      const groupTab = [...subTabs].find((t) => t.textContent?.includes("GROUP"));
      if (groupTab) {
        await click(groupTab);
        expect(mounted.ctx.current.state.tabs?.Chat).toBe("GROUP");
      }
    });
  });
});
