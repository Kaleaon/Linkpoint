import { afterEach, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider, useApp } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import SystemDialog from '../../components/SystemDialog.jsx';
import { DIALOGS } from '../../theme/dialogs.js';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

// Mock DIALOGS for testing
DIALOGS['llDialog'] = {
  icon: 'box',
  kind: 'SCRIPTED OBJECT',
  title: 'Vendor — Sunset Lamp v3',
  body: 'Touch a swatch to preview.',
  buttons: [
    { label: 'BUY L$250', primary: true },
    { label: 'OFFER', dim: true },
  ],
};

function DialogTrigger() {
  const { actions } = useApp();
  return (
    <button id="trigger-btn" onClick={() => actions.setDialog('llDialog')}>
      Open Dialog
    </button>
  );
}

let mounted: { host: HTMLElement; root: Root } | null = null;

async function mount() {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(
      createElement(
        AppProvider as any,
        null,
        createElement(
          ThemeProvider as any,
          null,
          createElement('div', null, createElement(DialogTrigger), createElement(SystemDialog as any))
        )
      )
    );
  });
  mounted = { host, root };
  return host;
}

afterEach(async () => {
  if (mounted) {
    await act(async () => mounted!.root.unmount());
    mounted.host.remove();
    mounted = null;
  }
  document.body.innerHTML = '';
  vi.restoreAllMocks();
});

describe('SystemDialog', () => {
  it('renders nothing when state.dialog is null', async () => {
    const host = await mount();
    expect(host.querySelector('[role="dialog"]')).toBeNull();
  });

  it('renders with correct ARIA attributes and semantic button elements', async () => {
    const host = await mount();
    const trigger = host.querySelector('#trigger-btn') as HTMLElement;
    trigger.focus();
    expect(document.activeElement).toBe(trigger);

    // Open SystemDialog
    await act(async () => {
      trigger.click();
    });

    const dialog = host.querySelector('[role="dialog"]');
    expect(dialog).not.toBeNull();
    expect(dialog?.getAttribute('aria-modal')).toBe('true');
    expect(dialog?.getAttribute('aria-labelledby')).toBe('system-dialog-title');
    expect(dialog?.getAttribute('aria-describedby')).toBe('system-dialog-body');

    expect(host.querySelector('#system-dialog-title')?.textContent).toBe('Vendor — Sunset Lamp v3');
    expect(host.querySelector('#system-dialog-body')?.textContent).toBe('Touch a swatch to preview.');

    // Buttons should all be standard <button> elements
    const buttons = Array.from(dialog?.querySelectorAll('button') || []);
    expect(buttons.length).toBe(3); // 1 Close button + 2 Action buttons
    expect(buttons[0].textContent?.trim()).toBe('CLOSE');
    expect(buttons[1].textContent?.trim()).toBe('BUY L$250');
    expect(buttons[2].textContent?.trim()).toBe('OFFER');

    // Focus trapped on first focusable button
    expect(document.activeElement).toBe(buttons[0]);
  });

  it('closes dialog on Escape key and restores focus to trigger element', async () => {
    const host = await mount();
    const trigger = host.querySelector('#trigger-btn') as HTMLElement;
    trigger.focus();

    await act(async () => {
      trigger.click();
    });

    expect(host.querySelector('[role="dialog"]')).not.toBeNull();

    // Press Escape
    await act(async () => {
      const event = new KeyboardEvent('keydown', { key: 'Escape', bubbles: true, cancelable: true });
      window.dispatchEvent(event);
    });

    expect(host.querySelector('[role="dialog"]')).toBeNull();
    expect(document.activeElement).toBe(trigger);
  });

  it('closes dialog on action button click and restores focus', async () => {
    const host = await mount();
    const trigger = host.querySelector('#trigger-btn') as HTMLElement;
    trigger.focus();

    await act(async () => {
      trigger.click();
    });

    const actionBtn = host.querySelectorAll('[role="dialog"] button')[1] as HTMLElement;
    await act(async () => {
      actionBtn.click();
    });

    expect(host.querySelector('[role="dialog"]')).toBeNull();
    expect(document.activeElement).toBe(trigger);
  });
});
