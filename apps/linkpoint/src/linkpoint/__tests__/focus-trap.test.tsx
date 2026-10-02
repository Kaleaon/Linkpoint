import { afterEach, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import FocusTrap from '../../components/FocusTrap.jsx';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: { host: HTMLElement; root: Root } | null = null;

async function mount(ui: React.ReactNode) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(ui as any);
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

describe('FocusTrap', () => {
  it('automatically focuses the first focusable element on mount', async () => {
    const host = await mount(
      createElement(
        FocusTrap,
        null,
        createElement('button', { id: 'btn1' }, 'Button 1'),
        createElement('button', { id: 'btn2' }, 'Button 2')
      )
    );

    const btn1 = host.querySelector('#btn1') as HTMLElement;
    expect(document.activeElement).toBe(btn1);
  });

  it('traps Tab navigation to loop focus from last to first element', async () => {
    const host = await mount(
      createElement(
        FocusTrap,
        null,
        createElement('button', { id: 'btn1' }, 'Button 1'),
        createElement('button', { id: 'btn2' }, 'Button 2')
      )
    );

    const btn1 = host.querySelector('#btn1') as HTMLElement;
    const btn2 = host.querySelector('#btn2') as HTMLElement;

    btn2.focus();
    expect(document.activeElement).toBe(btn2);

    // Press Tab from btn2 (last element)
    await act(async () => {
      const event = new KeyboardEvent('keydown', { key: 'Tab', bubbles: true, cancelable: true });
      window.dispatchEvent(event);
    });

    expect(document.activeElement).toBe(btn1);
  });

  it('traps Shift+Tab navigation to loop focus from first to last element', async () => {
    const host = await mount(
      createElement(
        FocusTrap,
        null,
        createElement('button', { id: 'btn1' }, 'Button 1'),
        createElement('button', { id: 'btn2' }, 'Button 2')
      )
    );

    const btn1 = host.querySelector('#btn1') as HTMLElement;
    const btn2 = host.querySelector('#btn2') as HTMLElement;

    btn1.focus();
    expect(document.activeElement).toBe(btn1);

    // Press Shift+Tab from btn1 (first element)
    await act(async () => {
      const event = new KeyboardEvent('keydown', { key: 'Tab', shiftKey: true, bubbles: true, cancelable: true });
      window.dispatchEvent(event);
    });

    expect(document.activeElement).toBe(btn2);
  });

  it('triggers onEscape when Escape key is pressed', async () => {
    const onEscape = vi.fn();
    await mount(
      createElement(
        FocusTrap,
        { onEscape },
        createElement('button', { id: 'btn1' }, 'Button 1')
      )
    );

    await act(async () => {
      const event = new KeyboardEvent('keydown', { key: 'Escape', bubbles: true, cancelable: true });
      window.dispatchEvent(event);
    });

    expect(onEscape).toHaveBeenCalledTimes(1);
  });

  it('restores focus to previously focused element when unmounted', async () => {
    const outerButton = document.createElement('button');
    outerButton.id = 'outer';
    document.body.appendChild(outerButton);
    outerButton.focus();
    expect(document.activeElement).toBe(outerButton);

    const host = await mount(
      createElement(
        FocusTrap,
        null,
        createElement('button', { id: 'inner' }, 'Inner Button')
      )
    );

    const inner = host.querySelector('#inner') as HTMLElement;
    expect(document.activeElement).toBe(inner);

    // Unmount FocusTrap
    await act(async () => {
      mounted!.root.unmount();
    });
    mounted = null;

    expect(document.activeElement).toBe(outerButton);
  });
});
