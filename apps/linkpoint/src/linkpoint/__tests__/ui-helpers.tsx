import { act, createElement } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider, useApp } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

export interface Mounted { host: HTMLElement; root: Root; ctx: { current: any } }

/** Mount a component inside the real providers. `ctx.current` exposes the app context (actions, state). */
export async function mountScreen(component: any): Promise<Mounted> {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  const ctx = { current: null as any };
  const Probe = () => { ctx.current = useApp(); return null; };
  await act(async () => {
    root.render(createElement(AppProvider as any, null, createElement(ThemeProvider as any, null, createElement('div', null, createElement(Probe), createElement(component)))));
  });
  return { host, root, ctx };
}

export async function unmount(mounted: Mounted | null) {
  if (!mounted) return;
  await act(async () => mounted.root.unmount());
  mounted.host.remove();
}

export const flush = () => act(async () => { await new Promise((resolve) => setTimeout(resolve, 0)); });
export const click = async (el: Element | null | undefined) => { if (!el) throw new Error('element not found to click'); await act(async () => { (el as HTMLElement).click(); }); };
export const buttonByText = (host: HTMLElement, text: string | RegExp) =>
  [...host.querySelectorAll('button')].find((b) => (typeof text === 'string' ? (b.textContent || '').trim() === text : text.test(b.textContent || ''))) as HTMLButtonElement | undefined;

/** Type into a React-controlled input or textarea. */
export async function typeInto(el: HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement, value: string) {
  await act(async () => {
    const proto = el instanceof HTMLTextAreaElement ? HTMLTextAreaElement.prototype : el instanceof HTMLSelectElement ? HTMLSelectElement.prototype : HTMLInputElement.prototype;
    Object.getOwnPropertyDescriptor(proto, 'value')!.set!.call(el, value);
    el.dispatchEvent(new Event(el instanceof HTMLSelectElement ? 'change' : 'input', { bubbles: true }));
  });
}
