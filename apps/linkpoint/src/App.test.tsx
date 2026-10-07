import { afterEach, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import App from './App';
import { AppProvider, useApp } from './context/AppContext.jsx';
import { ThemeProvider } from './context/ThemeContext.jsx';
import SystemDialog from './components/SystemDialog.jsx';
import { DIALOGS, Z_INDEX } from './theme/dialogs.js';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

// Mock dialog entry for testing
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

function TestDialogController() {
  const { actions } = useApp();
  return (
    <button id="open-dialog-btn" onClick={() => actions.setDialog('llDialog')}>
      Open System Dialog
    </button>
  );
}

let mounted: { host: HTMLElement; root: Root } | null = null;

async function mountApp() {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(createElement(App));
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

describe('App Root Layout & Z-Index Stack Tokens', () => {
  it('defines standardized z-index tokens in theme dialogs with correct hierarchy', () => {
    expect(Z_INDEX).toBeDefined();
    expect(Z_INDEX.DRAWER).toBe(40);
    expect(Z_INDEX.NAV_BANNER).toBe(999);
    expect(Z_INDEX.SYSTEM_DIALOG).toBe(10000);

    expect(Z_INDEX.SYSTEM_DIALOG).toBeGreaterThan(Z_INDEX.NAV_BANNER);
    expect(Z_INDEX.NAV_BANNER).toBeGreaterThan(Z_INDEX.DRAWER);
  });

  it('renders SystemDialog outside viewer-workspace as a direct sibling under main.viewer-app', async () => {
    const host = await mountApp();
    const mainApp = host.querySelector('main.viewer-app');
    expect(mainApp).not.toBeNull();

    const workspace = mainApp?.querySelector('.viewer-workspace');
    expect(workspace).not.toBeNull();

    // Verify SystemDialog is NOT nested inside .viewer-workspace
    const nestedDialogInWorkspace = workspace?.querySelector('[role="dialog"]');
    expect(nestedDialogInWorkspace).toBeNull();

    // Verify workspace is a direct child of main.viewer-app
    expect(workspace?.parentElement).toBe(mainApp);
  });

  it('renders active SystemDialog with top z-index precedence (10000)', async () => {
    const host = document.createElement('div');
    document.body.appendChild(host);
    const root = createRoot(host);

    await act(async () => {
      root.render(
        <AppProvider>
          <ThemeProvider>
            <main className="viewer-app">
              <div className="viewer-workspace" />
              <SystemDialog />
              <TestDialogController />
            </main>
          </ThemeProvider>
        </AppProvider>
      );
    });

    const triggerBtn = host.querySelector('#open-dialog-btn') as HTMLButtonElement;
    expect(triggerBtn).not.toBeNull();

    await act(async () => {
      triggerBtn.click();
    });

    const dialog = host.querySelector('[role="dialog"]');
    expect(dialog).not.toBeNull();

    // The FocusTrap wrapper around the dialog should be a direct sibling of .viewer-workspace
    const focusTrapContainer = dialog?.parentElement as HTMLElement | null;
    expect(focusTrapContainer).not.toBeNull();
    expect(focusTrapContainer?.parentElement?.className).toContain('viewer-app');

    // Confirm that focusTrapContainer is NOT inside .viewer-workspace
    const workspace = host.querySelector('.viewer-workspace');
    expect(workspace?.contains(focusTrapContainer!)).toBe(false);

    // Verify z-index value matches Z_INDEX.SYSTEM_DIALOG (10000)
    expect(focusTrapContainer?.style.zIndex).toBe('10000');

    await act(async () => root.unmount());
    host.remove();
  });
});
