// @vitest-environment jsdom
import { afterEach, describe, expect, it, vi } from 'vitest';
import React, { act, createElement } from 'react';
import { createRoot } from 'react-dom/client';
import Toggle from '../../components/Toggle.jsx';
import Card from '../../components/Card.jsx';
import Settings from '../../screens/Settings.jsx';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import { mountScreen, unmount, type Mounted } from './ui-helpers';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

vi.mock('../../services/google.ts', () => ({
  loadGoogle: async () => ({
    auth: { signOutGoogle: vi.fn(), getGoogleToken: () => null, signInWithGoogle: vi.fn() },
    contacts: {},
    calendar: {},
  }),
}));

async function renderComponent(element: React.ReactElement) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(
      createElement(
        AppProvider as any,
        null,
        createElement(ThemeProvider as any, null, element)
      )
    );
  });
  return {
    host,
    cleanup: async () => {
      await act(async () => {
        root.unmount();
      });
      host.remove();
    },
  };
}

describe('Toggle accessibility props', () => {
  it('renders with ariaLabel prop', async () => {
    const { host, cleanup } = await renderComponent(<Toggle on={true} ariaLabel="Enable dark mode" />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-label')).toBe('Enable dark mode');
    await cleanup();
  });

  it('renders with aria-label prop', async () => {
    const { host, cleanup } = await renderComponent(<Toggle on={false} aria-label="Enable notifications" />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-label')).toBe('Enable notifications');
    await cleanup();
  });

  it('renders with ariaLabelledBy prop', async () => {
    const { host, cleanup } = await renderComponent(<Toggle on={true} ariaLabelledBy="label-id" />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-labelledby')).toBe('label-id');
    await cleanup();
  });

  it('renders with aria-labelledby prop', async () => {
    const { host, cleanup } = await renderComponent(<Toggle on={false} aria-labelledby="label-id-2" />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-labelledby')).toBe('label-id-2');
    await cleanup();
  });

  it('Card passes title as accessible name to Toggle', async () => {
    const cardData = {
      title: 'Sound Effects',
      toggle: true,
      on: true,
      togglePick: vi.fn(),
    };
    const { host, cleanup } = await renderComponent(<Card c={cardData} />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-label')).toBe('Sound Effects');
    await cleanup();
  });

  it('Card uses explicit ariaLabel if provided over title', async () => {
    const cardData = {
      title: 'Sound Effects',
      ariaLabel: 'Custom Sound Effects Toggle',
      toggle: true,
      on: true,
      togglePick: vi.fn(),
    };
    const { host, cleanup } = await renderComponent(<Card c={cardData} />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-label')).toBe('Custom Sound Effects Toggle');
    await cleanup();
  });

  it('Card passes ariaLabelledBy if provided', async () => {
    const cardData = {
      title: 'Sound Effects',
      ariaLabelledBy: 'custom-heading-id',
      toggle: true,
      on: true,
      togglePick: vi.fn(),
    };
    const { host, cleanup } = await renderComponent(<Card c={cardData} />);
    const toggleEl = host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-labelledby')).toBe('custom-heading-id');
    await cleanup();
  });
});

describe('Settings accessible toggle', () => {
  let mounted: Mounted | null = null;

  afterEach(async () => {
    await unmount(mounted);
    mounted = null;
  });

  it('passes aria-labelledby directly to switch role element in Settings', async () => {
    mounted = await mountScreen(Settings);
    const toggleEl = mounted.host.querySelector('[role="switch"]');
    expect(toggleEl).not.toBeNull();
    expect(toggleEl!.getAttribute('aria-labelledby')).toBe('google-label');
    const parentSpan = toggleEl!.parentElement;
    expect(parentSpan?.getAttribute('aria-labelledby')).toBeNull();
  });
});
