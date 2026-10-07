// @vitest-environment jsdom
import { act } from "react";
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import DecorativeIcon from "../../components/DecorativeIcon.jsx";
import Icon from "../../components/Icon.jsx";
import IconButton from "../../components/IconButton.jsx";
import AccessibleAvatar from "../../components/AccessibleAvatar.jsx";
import ContactAvatar from "../../components/ContactAvatar.jsx";
import Header from "../../components/Header.jsx";
import RailNav from "../../components/RailNav.jsx";
import { click, mountScreen, unmount, type Mounted } from "./ui-helpers";

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: Mounted | null = null;

beforeAll(() => {
  vi.spyOn(console, "log").mockImplementation(() => undefined);
});

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.restoreAllMocks();
});

describe("A11y Icon & Avatar Primitives", () => {
  describe("Icon primitive", () => {
    it("defaults to aria-hidden='true' when no accessible name is provided", async () => {
      mounted = await mountScreen(() => <Icon name="user" size={18} />);
      const svg = mounted.host.querySelector("svg, span");
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute("aria-hidden")).toBe("true");
      expect(svg?.getAttribute("role")).toBeNull();
    });

    it("sets role='img' and renders <title> element when title prop is provided", async () => {
      mounted = await mountScreen(() => <Icon name="user" title="User Profile" />);
      const svg = mounted.host.querySelector("svg");
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute("role")).toBe("img");
      expect(svg?.hasAttribute("aria-hidden")).toBe(false);
      const titleEl = svg?.querySelector("title");
      expect(titleEl).toBeTruthy();
      expect(titleEl?.textContent).toBe("User Profile");
    });

    it("sets role='img' and aria-label when aria-label prop is provided", async () => {
      mounted = await mountScreen(() => <Icon name="user" aria-label="User Profile" />);
      const svg = mounted.host.querySelector("svg");
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute("role")).toBe("img");
      expect(svg?.getAttribute("aria-label")).toBe("User Profile");
      expect(svg?.hasAttribute("aria-hidden")).toBe(false);
    });

    it("sets role='img' and aria-labelledby when aria-labelledby prop is provided", async () => {
      mounted = await mountScreen(() => <Icon name="user" aria-labelledby="icon-label-id" />);
      const svg = mounted.host.querySelector("svg");
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute("role")).toBe("img");
      expect(svg?.getAttribute("aria-labelledby")).toBe("icon-label-id");
      expect(svg?.hasAttribute("aria-hidden")).toBe(false);
    });

    it("allows overriding aria-hidden explicitly even when accessible name is present", async () => {
      mounted = await mountScreen(() => <Icon name="user" aria-label="User Profile" aria-hidden="true" />);
      const svg = mounted.host.querySelector("svg");
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute("aria-hidden")).toBe("true");
    });

    it("fallback span defaults to aria-hidden='true' when icon name is missing or unknown", async () => {
      mounted = await mountScreen(() => <Icon name="nonexistent-icon-xyz" />);
      const span = mounted.host.querySelector("span");
      expect(span).toBeTruthy();
      expect(span?.getAttribute("aria-hidden")).toBe("true");
    });
  });

  describe("DecorativeIcon", () => {
    it("always renders aria-hidden='true' to prevent exposure to screen readers", async () => {
      mounted = await mountScreen(() => <DecorativeIcon name="user" size={20} className="custom-icon" />);
      const svg = mounted.host.querySelector("svg, span");
      expect(svg).toBeTruthy();
      expect(svg?.getAttribute("aria-hidden")).toBe("true");
    });
  });

  describe("IconButton", () => {
    it("renders native <button type='button'> with mandatory aria-label", async () => {
      const handleClick = vi.fn();
      mounted = await mountScreen(() => (
        <IconButton icon="search" label="Search friends" onClick={handleClick} size={16} buttonSize={36} />
      ));

      const button = mounted.host.querySelector("button");
      expect(button).toBeTruthy();
      expect(button?.getAttribute("type")).toBe("button");
      expect(button?.getAttribute("aria-label")).toBe("Search friends");

      // Internal decorative icon has aria-hidden="true"
      const icon = button?.querySelector("svg, span");
      expect(icon).toBeTruthy();
      expect(icon?.getAttribute("aria-hidden")).toBe("true");

      await click(button);
      expect(handleClick).toHaveBeenCalledTimes(1);
    });

    it("issues a console warning if mandatory label prop is omitted", async () => {
      const warnSpy = vi.spyOn(console, "warn").mockImplementation(() => undefined);
      mounted = await mountScreen(() => <IconButton icon="settings" onClick={() => {}} />);

      expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('[IconButton] Missing required label prop for icon "settings".'));
    });
  });

  describe("AccessibleAvatar", () => {
    it("renders initials span with aria-hidden='true' in decorative mode", async () => {
      mounted = await mountScreen(() => <AccessibleAvatar name="John Resident" size={40} decorative={true} />);
      const span = mounted.host.querySelector("span");
      expect(span).toBeTruthy();
      expect(span?.getAttribute("aria-hidden")).toBe("true");
      expect(span?.textContent).toBe("JR");
    });

    it("renders photo img with alt='' and aria-hidden='true' in decorative mode", async () => {
      mounted = await mountScreen(() => (
        <AccessibleAvatar name="Jane Resident" photo="data:image/png;base64,fake" size={40} decorative={true} />
      ));
      const img = mounted.host.querySelector("img");
      expect(img).toBeTruthy();
      expect(img?.getAttribute("alt")).toBe("");
      expect(img?.getAttribute("aria-hidden")).toBe("true");
    });

    it("renders standalone initials span with role='img' and aria-label when decorative={false}", async () => {
      mounted = await mountScreen(() => (
        <AccessibleAvatar name="Alice Resident" size={40} decorative={false} label="Alice Resident's avatar" />
      ));
      const span = mounted.host.querySelector("span");
      expect(span).toBeTruthy();
      expect(span?.getAttribute("role")).toBe("img");
      expect(span?.getAttribute("aria-label")).toBe("Alice Resident's avatar");
      expect(span?.hasAttribute("aria-hidden")).toBe(false);
    });

    it("renders standalone photo img with alt text when decorative={false}", async () => {
      mounted = await mountScreen(() => (
        <AccessibleAvatar name="Bob Resident" photo="data:image/png;base64,fake" size={40} decorative={false} />
      ));
      const img = mounted.host.querySelector("img");
      expect(img).toBeTruthy();
      expect(img?.getAttribute("alt")).toBe("Bob Resident");
      expect(img?.hasAttribute("aria-hidden")).toBe(false);
    });

    it("ContactAvatar delegates to AccessibleAvatar", async () => {
      mounted = await mountScreen(() => <ContactAvatar name="Carol Resident" size={32} />);
      const span = mounted.host.querySelector("span");
      expect(span).toBeTruthy();
      expect(span?.getAttribute("aria-hidden")).toBe("true");
      expect(span?.textContent).toBe("CR");
    });
  });

  describe("Header & RailNav component integration", () => {
    it("renders header action controls using IconButton primitives on Friends screen", async () => {
      mounted = await mountScreen(() => {
        // Render Header inside AppProvider context configured for Friends screen
        return <Header />;
      });

      // Switch screen to Friends via AppContext
      await act(async () => {
        mounted?.ctx.current.actions.setScreen("Friends");
      });

      const iconButtons = mounted.host.querySelectorAll("button[aria-label='CONTACTS'], button[aria-label='ADD FRIEND'], button[aria-label='SEARCH']");
      expect(iconButtons.length).toBe(3);
      iconButtons.forEach((btn) => {
        expect(btn.getAttribute("type")).toBe("button");
        const icon = btn.querySelector("svg, span");
        expect(icon?.getAttribute("aria-hidden")).toBe("true");
      });
    });

    it("ensures Header back and toggle button inner icons default to aria-hidden='true'", async () => {
      mounted = await mountScreen(() => <Header />);

      await act(async () => {
        mounted?.ctx.current.actions.setScreen("Settings");
      });

      const backButton = mounted.host.querySelector("button[title='Back to Chat'], button[title='Back to Login']");
      expect(backButton).toBeTruthy();
      const backIcon = backButton?.querySelector("svg, span");
      expect(backIcon?.getAttribute("aria-hidden")).toBe("true");

      const tilesButton = mounted.host.querySelector("button[title='Toggle Multi-Window Workspace Engine']");
      expect(tilesButton).toBeTruthy();
      const tilesIcon = tilesButton?.querySelector("svg, span");
      expect(tilesIcon?.getAttribute("aria-hidden")).toBe("true");
    });

    it("ensures RailNav tab inner icons default to aria-hidden='true'", async () => {
      mounted = await mountScreen(() => <RailNav />);

      await act(async () => {
        mounted?.ctx.current.actions.setLayout("rules");
        mounted?.ctx.current.actions.setDevice("tab");
        mounted?.ctx.current.actions.setScreen("Chat");
      });

      const tabs = mounted.host.querySelectorAll('[role="tab"]');
      expect(tabs.length).toBeGreaterThan(0);

      tabs.forEach((tab) => {
        const icon = tab.querySelector("svg, span");
        expect(icon).toBeTruthy();
        expect(icon?.getAttribute("aria-hidden")).toBe("true");
      });
    });
  });
});
