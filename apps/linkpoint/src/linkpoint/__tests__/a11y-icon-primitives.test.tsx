// @vitest-environment jsdom
import { act } from "react";
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import DecorativeIcon from "../../components/DecorativeIcon.jsx";
import IconButton from "../../components/IconButton.jsx";
import AccessibleAvatar from "../../components/AccessibleAvatar.jsx";
import ContactAvatar from "../../components/ContactAvatar.jsx";
import Header from "../../components/Header.jsx";
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

  describe("Header component migration", () => {
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
  });
});
