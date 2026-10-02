import { afterEach, beforeAll, describe, expect, it } from 'vitest';
import { createElement, useEffect } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { act } from 'react';
import { AppProvider, useApp } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import DeviceFrame from '../../components/DeviceFrame.jsx';
import { app } from '../app';
import { LAYOUTS } from '../../theme/layouts.js';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

// Names the navigation uses for the screens a resident would go to from the 3D view.
const EXIT_LABELS = /^(chat|friends|people|radar|nearby|map|inv|inventory|more|settings)$|^go to /i;

function Setup({ layout }: { layout: string }) {
  const { actions } = useApp() as any;
  useEffect(() => { actions.setLayout(layout); actions.setScreen('3D View'); }, []);
  return createElement(DeviceFrame as any);
}

async function renderIn3D(layout: string, width: number) {
  Object.defineProperty(window, 'innerWidth', { value: width, configurable: true });
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root: Root = createRoot(host);
  await act(async () => {
    root.render(createElement(AppProvider as any, null, createElement(ThemeProvider as any, null, createElement(Setup, { layout }))));
  });
  await act(async () => { await new Promise((resolve) => setTimeout(resolve, 20)); });
  return { host, root };
}

const exitControls = (host: HTMLElement) =>
  [...host.querySelectorAll<HTMLElement>('[aria-label], div, button, span')]
    .map((el) => (el.getAttribute('aria-label') || (el.children.length === 0 ? el.textContent : '') || '').trim())
    .filter((label) => EXIT_LABELS.test(label));

describe('leaving the 3D view', () => {
  let mounted: { host: HTMLElement; root: Root } | null = null;
  beforeAll(() => { (app.auth as any).isLoggedIn = () => true; });
  afterEach(async () => {
    if (mounted) { await act(async () => mounted!.root.unmount()); mounted.host.remove(); mounted = null; }
  });

  for (const [deviceName, width] of [['phone', 412], ['tablet', 1000]] as const) {
    for (const layout of Object.keys(LAYOUTS)) {
      it(`${layout} layout on a ${deviceName} keeps navigation on the 3D View`, async () => {
        mounted = await renderIn3D(layout, width);
        const found = exitControls(mounted.host);
        expect(found.length, `no way out of the 3D view in ${layout}/${deviceName}`).toBeGreaterThanOrEqual(3);
      });
    }
  }
});
