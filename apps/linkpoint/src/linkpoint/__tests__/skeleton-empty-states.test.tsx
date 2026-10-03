// @vitest-environment jsdom
import { afterEach, describe, expect, it, vi } from "vitest";
import { act } from "react";
import ListSkeletonLoader from "../../components/ListSkeletonLoader.jsx";
import GuidedEmptyState from "../../components/GuidedEmptyState.jsx";
import { buttonByText, click, mountScreen, unmount, type Mounted } from "./ui-helpers";

let mounted: Mounted | null = null;

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
});

describe("ListSkeletonLoader and GuidedEmptyState components", () => {
  it("renders ListSkeletonLoader with specified row count and accessible attributes", async () => {
    const Component = () => <ListSkeletonLoader count={4} variant="inventory" />;
    mounted = await mountScreen(Component);

    const loader = mounted.host.querySelector(".skeleton-loader");
    expect(loader).not.toBeNull();
    expect(loader?.getAttribute("role")).toBe("status");
    expect(loader?.getAttribute("aria-busy")).toBe("true");

    const rows = mounted.host.querySelectorAll(".skeleton-row");
    expect(rows.length).toBe(4);
  });

  it("renders GuidedEmptyState with title, message, and actionable buttons", async () => {
    const handlePrimary = vi.fn();
    const handleSecondary = vi.fn();

    const Component = () => (
      <GuidedEmptyState
        title="No inventory loaded"
        description="Your inventory is empty."
        actionLabel="Reload Inventory"
        onAction={handlePrimary}
        secondaryActionLabel="Open Marketplace"
        onSecondaryAction={handleSecondary}
      />
    );

    mounted = await mountScreen(Component);

    expect(mounted.host.textContent).toContain("No inventory loaded");
    expect(mounted.host.textContent).toContain("Your inventory is empty.");

    const primaryBtn = buttonByText(mounted.host, "Reload Inventory");
    const secondaryBtn = buttonByText(mounted.host, "Open Marketplace");

    expect(primaryBtn).toBeDefined();
    expect(secondaryBtn).toBeDefined();

    await click(primaryBtn);
    expect(handlePrimary).toHaveBeenCalledTimes(1);

    await click(secondaryBtn);
    expect(handleSecondary).toHaveBeenCalledTimes(1);
  });

  it("suppresses onboarding buttons for search zero results in GuidedEmptyState", async () => {
    const Component = () => (
      <GuidedEmptyState
        title="No search results"
        description='No items match "xyz".'
        isSearch={true}
        actionLabel="Should Not Appear"
        onAction={() => {}}
      />
    );

    mounted = await mountScreen(Component);

    expect(mounted.host.textContent).toContain("No search results");
    expect(mounted.host.textContent).toContain('No items match "xyz".');
    expect(buttonByText(mounted.host, "Should Not Appear")).toBeUndefined();
  });
});
