import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import Login from '../../screens/Login.jsx';
import Search from '../../screens/Search.jsx';
import DiagnosticsPanel from '../../screens/DiagnosticsPanel.jsx';
import { app } from '../app';
import { LoginFailure } from '../login-failure';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: { host: HTMLElement; root: Root } | null = null;

async function mount(Component: any) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(
      createElement(
        AppProvider as any,
        null,
        createElement(ThemeProvider as any, null, createElement(Component))
      )
    );
  });
  mounted = { host, root };
  return host;
}

const setInputValue = async (el: HTMLInputElement, value: string) => {
  await act(async () => {
    const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value')!.set!;
    setter.call(el, value);
    el.dispatchEvent(new Event('input', { bubbles: true }));
  });
};

const submitForm = async (host: HTMLElement) => {
  await act(async () => {
    host.querySelector('form')!.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    await new Promise((r) => setTimeout(r, 10));
  });
};

beforeAll(() => {
  (app.protocol as any).checkAutoLoginStatus = async () => ({ available: false });
  if (!app.friends?.getFriends) {
    (app.friends as any) = {
      getFriends: () => [],
      on: () => {},
      off: () => {},
    };
  }
  if (!app.world?.getNearbyUsers) {
    (app.world as any) = {
      ...app.world,
      getNearbyUsers: () => [],
      on: () => {},
      off: () => {},
    };
  }
});

afterEach(async () => {
  if (mounted) {
    await act(async () => mounted!.root.unmount());
    mounted.host.remove();
    mounted = null;
  }
  vi.restoreAllMocks();
});

describe('Accessible Form Controls and ARIA Error Linkage', () => {
  describe('Login.jsx accessible form controls', () => {
    it('associates form labels explicitly with inputs via htmlFor and id pairs', async () => {
      const host = await mount(Login);

      // Grid selection
      const gridSelect = host.querySelector<HTMLSelectElement>('#login-grid-select');
      expect(gridSelect).not.toBeNull();
      const gridLabel = host.querySelector<HTMLLabelElement>('label[for="login-grid-select"]');
      expect(gridLabel).not.toBeNull();

      // Username
      const usernameInput = host.querySelector<HTMLInputElement>('#login-username');
      expect(usernameInput).not.toBeNull();
      const usernameLabel = host.querySelector<HTMLLabelElement>('label[for="login-username"]');
      expect(usernameLabel).not.toBeNull();

      // Password
      const passwordInput = host.querySelector<HTMLInputElement>('#login-password');
      expect(passwordInput).not.toBeNull();
      const passwordLabel = host.querySelector<HTMLLabelElement>('label[for="login-password"]');
      expect(passwordLabel).not.toBeNull();

      // Start location
      const startSelect = host.querySelector<HTMLSelectElement>('#login-start-location');
      expect(startSelect).not.toBeNull();
      const startLabel = host.querySelector<HTMLLabelElement>('label[for="login-start-location"]');
      expect(startLabel).not.toBeNull();

      // Remember me
      const rememberInput = host.querySelector<HTMLInputElement>('#login-remember-me');
      expect(rememberInput).not.toBeNull();
      const rememberLabel = host.querySelector<HTMLLabelElement>('label[for="login-remember-me"]');
      expect(rememberLabel).not.toBeNull();
    });

    it('sets aria-invalid and aria-describedby linking to login-error-message on error', async () => {
      vi.spyOn(app.auth, 'login').mockRejectedValueOnce(
        new LoginFailure({
          reason: 'key',
          code: 'bad_credentials',
          mfaRequired: false,
          message: 'The name or password is incorrect.',
        })
      );

      const host = await mount(Login);
      const usernameInput = host.querySelector<HTMLInputElement>('#login-username')!;
      const passwordInput = host.querySelector<HTMLInputElement>('#login-password')!;

      // Initially no error
      expect(usernameInput.getAttribute('aria-invalid')).toBe('false');
      expect(usernameInput.getAttribute('aria-describedby')).toBeNull();
      expect(host.querySelector('#login-error-message')).toBeNull();

      await setInputValue(usernameInput, 'jane');
      await setInputValue(passwordInput, 'wrongpass');
      await submitForm(host);

      // Error shown with id="login-error-message"
      const errorMsg = host.querySelector<HTMLElement>('#login-error-message');
      expect(errorMsg).not.toBeNull();
      expect(errorMsg?.getAttribute('role')).toBe('alert');
      expect(errorMsg?.textContent).toBe('The name or password is incorrect.');

      // Inputs reflect aria-invalid="true" and point to login-error-message
      expect(usernameInput.getAttribute('aria-invalid')).toBe('true');
      expect(usernameInput.getAttribute('aria-describedby')).toBe('login-error-message');
      expect(passwordInput.getAttribute('aria-invalid')).toBe('true');
      expect(passwordInput.getAttribute('aria-describedby')).toBe('login-error-message');
    });

    it('links MFA input to error message and help text when MFA challenge fails', async () => {
      const challenge = new LoginFailure({
        reason: 'mfa_challenge',
        code: 'mfa_required',
        mfaRequired: true,
        message: 'Enter code from authenticator app.',
      });
      const rejected = new LoginFailure({
        reason: 'mfa_failure',
        code: 'mfa_failed',
        mfaRequired: true,
        message: 'The multi-factor code was not accepted.',
      });

      vi.spyOn(app.auth, 'login').mockRejectedValueOnce(challenge).mockRejectedValueOnce(rejected);

      const host = await mount(Login);
      const usernameInput = host.querySelector<HTMLInputElement>('#login-username')!;
      const passwordInput = host.querySelector<HTMLInputElement>('#login-password')!;

      await setInputValue(usernameInput, 'jane');
      await setInputValue(passwordInput, 'secret');
      await submitForm(host);

      const mfaInput = host.querySelector<HTMLInputElement>('#login-mfa-token');
      expect(mfaInput).not.toBeNull();
      const mfaLabel = host.querySelector<HTMLLabelElement>('label[for="login-mfa-token"]');
      expect(mfaLabel).not.toBeNull();
      expect(mfaInput?.getAttribute('aria-describedby')).toBe('mfa-help');

      await setInputValue(mfaInput!, '000000');
      await submitForm(host);

      const mfaInputAfterError = host.querySelector<HTMLInputElement>('#login-mfa-token')!;
      expect(mfaInputAfterError.getAttribute('aria-invalid')).toBe('true');
      expect(mfaInputAfterError.getAttribute('aria-describedby')).toBe('login-error-message mfa-help');
    });

    it('associates custom grid modal input labels when addGrid modal is opened', async () => {
      const host = await mount(Login);
      const addGridBtn = [...host.querySelectorAll('button')].find((b) => b.textContent?.includes('CUSTOM') || b.textContent?.includes('grid'));
      expect(addGridBtn).not.toBeUndefined();

      await act(async () => {
        addGridBtn!.click();
      });

      const gridNameInput = host.querySelector<HTMLInputElement>('#login-add-grid-name');
      expect(gridNameInput).not.toBeNull();
      const gridNameLabel = host.querySelector<HTMLLabelElement>('label[for="login-add-grid-name"]');
      expect(gridNameLabel).not.toBeNull();

      const gridHostInput = host.querySelector<HTMLInputElement>('#login-add-grid-host');
      expect(gridHostInput).not.toBeNull();
      const gridHostLabel = host.querySelector<HTMLLabelElement>('label[for="login-add-grid-host"]');
      expect(gridHostLabel).not.toBeNull();
    });
  });

  describe('Search.jsx accessible search input', () => {
    it('associates search input with label via htmlFor and id pair', async () => {
      const host = await mount(Search);
      const searchInput = host.querySelector<HTMLInputElement>('#search-resident-input');
      expect(searchInput).not.toBeNull();

      const searchLabel = host.querySelector<HTMLLabelElement>('label[for="search-resident-input"]');
      expect(searchLabel).not.toBeNull();
      expect(searchLabel?.classList.contains('sr-only')).toBe(true);
      expect(searchInput?.getAttribute('aria-label')).toBeTruthy();
    });
  });

  describe('DiagnosticsPanel.jsx interactive control accessibility', () => {
    it('provides clear aria-labels on action controls and telemetry probes', async () => {
      vi.spyOn(app.protocol, 'getDiagnostics').mockReturnValue({
        connected: true,
        latencyMs: 45,
        packetLossPct: 0.2,
        lastPacketTimestamp: Date.now(),
        simName: 'Test Sim',
        simAddress: '127.0.0.1',
        simPort: 13000,
        circuitCode: 1234,
        packetsIn: 100,
        packetsOut: 80,
      } as any);

      const host = await mount(DiagnosticsPanel);

      const pingBtn = [...host.querySelectorAll('button')].find((b) => b.textContent?.includes('PING PROBE'));
      expect(pingBtn).not.toBeUndefined();
      expect(pingBtn?.getAttribute('aria-label')).toBe('Execute simulator ping probe');

      const graphBars = host.querySelectorAll('div[role="img"]');
      expect(graphBars.length).toBeGreaterThan(0);
      expect(graphBars[0].getAttribute('aria-label')).toContain('Ping sample 1');
    });
  });
});
