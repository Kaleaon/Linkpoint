import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import Login from '../../screens/Login.jsx';
import { app } from '../app';
import { LoginFailure } from '../login-failure';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

const challenge = new LoginFailure({ reason: 'mfa_challenge', code: 'mfa_required', mfaRequired: true, message: 'Enter the code from your authenticator app.' });
const rejected = new LoginFailure({ reason: 'mfa_failure', code: 'mfa_failed', mfaRequired: true, message: 'The multi-factor code was not accepted.' });

let mounted: { host: HTMLElement; root: Root } | null = null;
async function mount() {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => { root.render(createElement(AppProvider as any, null, createElement(ThemeProvider as any, null, createElement(Login as any)))); });
  mounted = { host, root };
  return host;
}
const set = async (el: HTMLInputElement, value: string) => {
  await act(async () => {
    const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value')!.set!;
    setter.call(el, value);
    el.dispatchEvent(new Event('input', { bubbles: true }));
  });
};
const submit = async (host: HTMLElement) => { await act(async () => { host.querySelector('form')!.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true })); await new Promise((r) => setTimeout(r, 10)); }); };
const codeField = (host: HTMLElement) => host.querySelector<HTMLInputElement>('input[autocomplete="one-time-code"]');

beforeAll(() => { (app.protocol as any).checkAutoLoginStatus = async () => ({ available: false }); });
afterEach(async () => { if (mounted) { await act(async () => mounted!.root.unmount()); mounted.host.remove(); mounted = null; } vi.restoreAllMocks(); });

describe('Login screen multi-factor flow', () => {
  it('asks for the code after a challenge, keeps the name and password, then sends the code', async () => {
    const login = vi.spyOn(app.auth, 'login').mockRejectedValueOnce(challenge).mockResolvedValueOnce({} as any);
    const host = await mount();
    expect(codeField(host)).toBeNull();

    const inputs = host.querySelectorAll<HTMLInputElement>('input');
    const name = [...inputs].find((i) => i.autocomplete === 'username')!;
    const password = [...inputs].find((i) => i.type === 'password')!;
    await set(name, 'jane doe');
    await set(password, 'secret');
    await submit(host);

    const field = codeField(host)!;
    expect(field).not.toBeNull();
    expect(host.textContent).toContain('Enter the code from your authenticator app.');
    expect(host.textContent).toContain('VERIFY AND CONNECT');
    expect(host.querySelector('[role="alert"]')).toBeNull(); // a first challenge is a prompt, not an error
    expect(password.value).toBe('secret');
    expect(login.mock.calls[0][5]).toBe(''); // no code on the first attempt

    await set(field, '123456');
    await submit(host);
    expect(login).toHaveBeenCalledTimes(2);
    expect(login.mock.calls[1][1]).toBe('jane doe');
    expect(login.mock.calls[1][2]).toBe('secret');
    expect(login.mock.calls[1][5]).toBe('123456');
    expect(codeField(host)).toBeNull(); // success clears the prompt
  });

  it('shows the grid\'s message when the code is rejected and asks again', async () => {
    vi.spyOn(app.auth, 'login').mockRejectedValueOnce(challenge).mockRejectedValueOnce(rejected);
    const host = await mount();
    const inputs = host.querySelectorAll<HTMLInputElement>('input');
    await set([...inputs].find((i) => i.autocomplete === 'username')!, 'jane');
    await set([...inputs].find((i) => i.type === 'password')!, 'secret');
    await submit(host);
    await set(codeField(host)!, '000000');
    await submit(host);
    expect(host.querySelector('[role="alert"]')?.textContent).toBe('The multi-factor code was not accepted.');
    expect(codeField(host)).not.toBeNull();
    expect(codeField(host)!.value).toBe(''); // cleared so the next code is typed fresh
  });

  it('shows ordinary failures as errors without asking for a code', async () => {
    vi.spyOn(app.auth, 'login').mockRejectedValueOnce(new LoginFailure({ reason: 'key', code: 'bad_credentials', mfaRequired: false, message: 'The name or password is incorrect.' }));
    const host = await mount();
    const inputs = host.querySelectorAll<HTMLInputElement>('input');
    await set([...inputs].find((i) => i.autocomplete === 'username')!, 'jane');
    await set([...inputs].find((i) => i.type === 'password')!, 'bad');
    await submit(host);
    expect(host.querySelector('[role="alert"]')?.textContent).toBe('The name or password is incorrect.');
    expect(codeField(host)).toBeNull();
  });
});
