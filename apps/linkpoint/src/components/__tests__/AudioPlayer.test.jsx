// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import { createElement, act } from "react";
import { createRoot } from "react-dom/client";
import { AudioPlayer } from "../AudioPlayer.jsx";

globalThis.IS_REACT_ACT_ENVIRONMENT = true;

let mounted = null;

async function mount(Component, props = {}) {
  const host = document.createElement("div");
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(createElement(Component, props));
  });
  mounted = { host, root };
  return host;
}

beforeAll(() => {
  if (!window.HTMLMediaElement.prototype.play) {
    window.HTMLMediaElement.prototype.play = vi.fn().mockImplementation(() => Promise.resolve());
  }
  if (!window.HTMLMediaElement.prototype.pause) {
    window.HTMLMediaElement.prototype.pause = vi.fn().mockImplementation(() => {});
  }
});

beforeEach(() => {
  vi.spyOn(window.HTMLMediaElement.prototype, "play").mockImplementation(function () {
    this.dispatchEvent(new Event("play"));
    return Promise.resolve();
  });
  vi.spyOn(window.HTMLMediaElement.prototype, "pause").mockImplementation(function () {
    this.dispatchEvent(new Event("pause"));
  });
});

afterEach(async () => {
  if (mounted) {
    await act(async () => mounted.root.unmount());
    mounted.host.remove();
    mounted = null;
  }
  vi.restoreAllMocks();
});

describe("AudioPlayer Component", () => {
  it("renders audio element without autoPlay enabled", async () => {
    const host = await mount(AudioPlayer, { src: "https://example.com/stream.mp3" });
    const audio = host.querySelector("audio");

    expect(audio).not.toBeNull();
    expect(audio.autoplay).toBe(false);
    expect(audio.getAttribute("autoplay")).toBeNull();
  });

  it("toggles play and pause state and updates aria-label", async () => {
    const host = await mount(AudioPlayer, { src: "https://example.com/stream.mp3" });
    const playBtn = host.querySelector("button[aria-label='Play audio stream']");

    expect(playBtn).not.toBeNull();
    expect(playBtn.textContent).toBe("Play");

    await act(async () => {
      playBtn.click();
    });

    const pauseBtn = host.querySelector("button[aria-label='Pause audio stream']");
    expect(pauseBtn).not.toBeNull();
    expect(pauseBtn.textContent).toBe("Pause");

    await act(async () => {
      pauseBtn.click();
    });

    const newPlayBtn = host.querySelector("button[aria-label='Play audio stream']");
    expect(newPlayBtn).not.toBeNull();
    expect(newPlayBtn.textContent).toBe("Play");
  });

  it("handles volume slider changes and updates aria-valuenow", async () => {
    const host = await mount(AudioPlayer, { src: "https://example.com/stream.mp3" });
    const slider = host.querySelector("input[type='range']");

    expect(slider).not.toBeNull();
    expect(slider.getAttribute("aria-label")).toBe("Volume");
    expect(slider.getAttribute("aria-valuenow")).toBe("100");

    await act(async () => {
      const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, "value").set;
      setter.call(slider, "0.5");
      slider.dispatchEvent(new Event("input", { bubbles: true }));
      slider.dispatchEvent(new Event("change", { bubbles: true }));
    });

    expect(slider.getAttribute("aria-valuenow")).toBe("50");
  });

  it("toggles mute and unmute and updates aria-pressed and restores volume", async () => {
    const host = await mount(AudioPlayer, { src: "https://example.com/stream.mp3" });
    const muteBtn = host.querySelector("button[aria-label='Mute audio stream']");
    const slider = host.querySelector("input[type='range']");

    expect(muteBtn).not.toBeNull();
    expect(muteBtn.getAttribute("aria-pressed")).toBe("false");
    expect(slider.getAttribute("aria-valuenow")).toBe("100");

    await act(async () => {
      muteBtn.click();
    });

    const unmuteBtn = host.querySelector("button[aria-label='Unmute audio stream']");
    expect(unmuteBtn).not.toBeNull();
    expect(unmuteBtn.getAttribute("aria-pressed")).toBe("true");
    expect(slider.getAttribute("aria-valuenow")).toBe("0");

    await act(async () => {
      unmuteBtn.click();
    });

    const restoredMuteBtn = host.querySelector("button[aria-label='Mute audio stream']");
    expect(restoredMuteBtn).not.toBeNull();
    expect(restoredMuteBtn.getAttribute("aria-pressed")).toBe("false");
    expect(slider.getAttribute("aria-valuenow")).toBe("100");
  });

  it("invokes onError callback when audio element fires an error", async () => {
    const onError = vi.fn();
    const host = await mount(AudioPlayer, { src: "https://example.com/invalid.mp3", onError });
    const audio = host.querySelector("audio");

    expect(audio).not.toBeNull();

    await act(async () => {
      audio.dispatchEvent(new Event("error"));
    });

    expect(onError).toHaveBeenCalledTimes(1);
  });
});
