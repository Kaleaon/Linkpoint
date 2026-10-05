// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import React, { act, createElement } from 'react';
import { createRoot } from 'react-dom/client';
import StartLocationCombobox, { saveRecentLocation, clearRecentLocations } from '../../components/StartLocationCombobox.jsx';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

const TEST_STORAGE_KEY = 'test_start_locations_a11y_key';

async function renderCombobox(props: any = {}) {
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
          createElement(StartLocationCombobox, { storageKey: TEST_STORAGE_KEY, ...props })
        )
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

describe('StartLocationCombobox Accessibility & Keyboard Navigation', () => {
  beforeEach(() => {
    clearRecentLocations(TEST_STORAGE_KEY);
  });

  afterEach(() => {
    clearRecentLocations(TEST_STORAGE_KEY);
  });

  it('renders input element with standard WAI-ARIA combobox attributes', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    expect(input).not.toBeNull();
    expect(input.getAttribute('aria-expanded')).toBe('false');
    expect(input.getAttribute('aria-haspopup')).toBe('listbox');
    expect(input.getAttribute('aria-autocomplete')).toBe('list');
    expect(input.getAttribute('aria-controls')).toBeTruthy();
    expect(input.getAttribute('aria-activedescendant')).toBeNull();

    await cleanup();
  });

  it('opens dropdown on focus and connects aria-controls to listbox ID', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    await act(async () => {
      input.focus();
    });

    expect(input.getAttribute('aria-expanded')).toBe('true');

    const listboxId = input.getAttribute('aria-controls');
    expect(listboxId).toBeTruthy();
    const listbox = host.querySelector(`ul[id="${listboxId}"]`);
    expect(listbox).not.toBeNull();
    expect(listbox!.getAttribute('role')).toBe('listbox');

    await cleanup();
  });

  it('renders selectable options with role="option" and unique IDs', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    await act(async () => {
      input.focus();
    });

    const options = host.querySelectorAll('li[role="option"]');
    expect(options.length).toBe(2); // standardOptions: last, home

    const id0 = options[0].getAttribute('id');
    const id1 = options[1].getAttribute('id');
    expect(id0).toBeTruthy();
    expect(id1).toBeTruthy();
    expect(id0).not.toBe(id1);

    await cleanup();
  });

  it('handles keyboard navigation with ArrowDown and ArrowUp', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    // Press ArrowDown when closed opens listbox and sets activeIndex = 0
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    });

    expect(input.getAttribute('aria-expanded')).toBe('true');
    const options = host.querySelectorAll('li[role="option"]');
    const opt0Id = options[0].getAttribute('id');
    expect(input.getAttribute('aria-activedescendant')).toBe(opt0Id);
    expect(options[0].getAttribute('aria-selected')).toBe('true');

    // ArrowDown again moves activeIndex to 1
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    });

    const opt1Id = options[1].getAttribute('id');
    expect(input.getAttribute('aria-activedescendant')).toBe(opt1Id);
    expect(options[1].getAttribute('aria-selected')).toBe('true');

    // ArrowUp moves back to 0
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowUp', bubbles: true }));
    });

    expect(input.getAttribute('aria-activedescendant')).toBe(opt0Id);

    await cleanup();
  });

  it('supports Home and End keys for jumping focus', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    await act(async () => {
      input.focus();
    });

    // Press End key -> jump to last option (index 1)
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'End', bubbles: true }));
    });

    const options = host.querySelectorAll('li[role="option"]');
    expect(input.getAttribute('aria-activedescendant')).toBe(options[1].getAttribute('id'));

    // Press Home key -> jump to first option (index 0)
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Home', bubbles: true }));
    });

    expect(input.getAttribute('aria-activedescendant')).toBe(options[0].getAttribute('id'));

    await cleanup();
  });

  it('selects option on Enter key and closes dropdown', async () => {
    const handleChange = vi.fn();
    const { host, cleanup } = await renderCombobox({ onChange: handleChange });
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    // Open dropdown and move to option 1 ("home")
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    });
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    });

    // Press Enter to select option 1
    const enterEvent = new KeyboardEvent('keydown', { key: 'Enter', bubbles: true, cancelable: true });
    await act(async () => {
      input.dispatchEvent(enterEvent);
    });

    expect(handleChange).toHaveBeenCalledWith('home');
    expect(input.getAttribute('aria-expanded')).toBe('false');

    await cleanup();
  });

  it('closes dropdown and resets activeIndex on Escape key', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    });
    expect(input.getAttribute('aria-expanded')).toBe('true');

    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
    });

    expect(input.getAttribute('aria-expanded')).toBe('false');
    expect(input.getAttribute('aria-activedescendant')).toBeNull();

    await cleanup();
  });

  it('renders recent locations header with role="presentation" and recent items', async () => {
    saveRecentLocation('Ahern', TEST_STORAGE_KEY);
    saveRecentLocation('Morris', TEST_STORAGE_KEY);

    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    await act(async () => {
      input.focus();
    });

    const header = host.querySelector('li[role="presentation"]');
    expect(header).not.toBeNull();
    expect(header!.textContent).toBeTruthy();

    const options = host.querySelectorAll('li[role="option"]');
    expect(options.length).toBe(4); // 2 standard + 2 recents

    // Navigate to 4th option (Morris)
    await act(async () => {
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'End', bubbles: true }));
    });

    expect(input.getAttribute('aria-activedescendant')).toBe(options[3].getAttribute('id'));

    await cleanup();
  });

  it('synchronizes mouse enter with activeIndex', async () => {
    const { host, cleanup } = await renderCombobox();
    const input = host.querySelector('input[role="combobox"]') as HTMLInputElement;

    await act(async () => {
      input.focus();
    });

    const options = host.querySelectorAll('li[role="option"]');
    await act(async () => {
      options[1].dispatchEvent(new MouseEvent('mouseover', { bubbles: true }));
      options[1].dispatchEvent(new MouseEvent('mouseenter', { bubbles: true }));
    });

    expect(input.getAttribute('aria-activedescendant')).toBe(options[1].getAttribute('id'));

    await cleanup();
  });
});
