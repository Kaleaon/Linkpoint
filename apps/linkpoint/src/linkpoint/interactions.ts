/**
 * Requests the simulator wants the user to answer: script dialogs (llDialog,
 * llTextBox) and teleport lures. The backend keeps the original events and
 * sends a plain description with an opaque id; answers go back by that id.
 *
 * Nothing here is invented: an entry exists only because the grid sent it, and
 * it leaves the list only when answered, dismissed, or the session ends.
 */

import { Utils } from './utils';
import type { SLConnectionFull } from './sl-connection-full';

export interface ScriptDialogRequest {
  kind: 'script-dialog';
  id: string;
  receivedAt: number;
  objectId: string | null;
  objectName: string;
  ownerName: string;
  message: string;
  channel: number;
  imageId: string | null;
  buttons: string[];
  /** True when a button was the `!!llTextBox!!` marker: the dialog asks for typed text. */
  textBox: boolean;
  textBoxIndex: number;
}

export interface LureRequest {
  kind: 'lure';
  id: string;
  receivedAt: number;
  fromId: string | null;
  fromName: string;
  message: string;
  regionId: string | null;
  position: [number, number, number] | null;
  gridX: number | null;
  gridY: number | null;
}

export type Interaction = ScriptDialogRequest | LureRequest;

/** The button label a script uses (llTextBox) to ask for typed text instead of a choice. */
export const TEXT_BOX_MARKER = '!!llTextBox!!';

/** Pending requests kept in the UI; the oldest are dropped past this. */
export const MAX_INTERACTIONS = 25;

const errorMessage = (error: unknown) => (error instanceof Error ? error.message : String(error || 'Request failed'));

export class InteractionsManager extends Utils.EventEmitter {
  private list: Interaction[] = [];
  private busyIds = new Set<string>();

  constructor(private protocol: SLConnectionFull) {
    super();
  }

  init() {
    this.protocol.on('script_dialog', (data: any) => this.add('script-dialog', data));
    this.protocol.on('lure', (data: any) => this.add('lure', data));
    this.protocol.on('disconnected', () => this.clear());
    this.protocol.on('connection_failed', () => this.clear());
  }

  /** Pending requests, oldest first. */
  get items(): readonly Interaction[] {
    return this.list;
  }

  /** The request to show now: the oldest unanswered one. */
  get current(): Interaction | null {
    return this.list[0] || null;
  }

  isBusy(id: string) {
    return this.busyIds.has(id);
  }

  private add(kind: Interaction['kind'], data: any) {
    if (!data || typeof data.id !== 'string' || !data.id) return;
    if (this.list.some((item) => item.id === data.id)) return;
    const base = { id: data.id, receivedAt: Number.isFinite(data.receivedAt) ? data.receivedAt : Date.now() };
    const item: Interaction = kind === 'script-dialog'
      ? {
          ...base, kind,
          objectId: data.objectId ?? null,
          objectName: String(data.objectName || ''),
          ownerName: String(data.ownerName || ''),
          message: String(data.message || ''),
          channel: Number.isFinite(data.channel) ? data.channel : 0,
          imageId: data.imageId ?? null,
          buttons: Array.isArray(data.buttons) ? data.buttons.map(String) : [],
          textBox: Boolean(data.textBox),
          textBoxIndex: Number.isInteger(data.textBoxIndex) ? data.textBoxIndex : -1,
        }
      : {
          ...base, kind,
          fromId: data.fromId ?? null,
          fromName: String(data.fromName || ''),
          message: String(data.message || ''),
          regionId: data.regionId ?? null,
          position: Array.isArray(data.position) && data.position.length === 3 ? (data.position as [number, number, number]) : null,
          gridX: Number.isFinite(data.gridX) ? data.gridX : null,
          gridY: Number.isFinite(data.gridY) ? data.gridY : null,
        };
    this.list.push(item);
    while (this.list.length > MAX_INTERACTIONS) this.list.shift();
    this.changed();
    this.emit('interaction_received', item);
  }

  private remove(id: string) {
    const before = this.list.length;
    this.list = this.list.filter((item) => item.id !== id);
    if (this.list.length !== before) this.changed();
  }

  private changed() {
    this.emit('interactions_changed', this.list);
  }

  clear() {
    this.busyIds.clear();
    if (!this.list.length) return;
    this.list = [];
    this.changed();
  }

  /**
   * Run a server call for one request. The request stays in the list on failure so the
   * user can retry, and the failure is reported through `interaction_failed`.
   */
  private async run<T>(id: string, call: () => Promise<T>): Promise<T | null> {
    if (this.busyIds.has(id)) return null;
    this.busyIds.add(id);
    this.changed();
    try {
      const result = await call();
      this.busyIds.delete(id);
      this.remove(id);
      return result;
    } catch (error) {
      this.busyIds.delete(id);
      this.changed();
      this.emit('interaction_failed', { id, message: errorMessage(error) });
      return null;
    }
  }

  /** Answer a button dialog. Returns whether the grid acknowledged. */
  async answerButton(id: string, buttonIndex: number) {
    const result = await this.run(id, () => this.protocol.respondScriptDialog({ id, buttonIndex }));
    return Boolean(result);
  }

  /** Answer a text box dialog with the typed text. */
  async answerText(id: string, text: string) {
    const result = await this.run(id, () => this.protocol.respondScriptDialog({ id, text }));
    return Boolean(result);
  }

  /** Accept a teleport lure. Emits `lure_accepted` with the grid's message once teleported. */
  async acceptLure(id: string) {
    const lure = this.list.find((item) => item.id === id && item.kind === 'lure') as LureRequest | undefined;
    const result = await this.run(id, () => this.protocol.acceptLure(id));
    if (result && lure) this.emit('lure_accepted', { lure, message: result.message });
    return Boolean(result);
  }

  /** Dismiss locally. The grid is not told: the client library cannot decline a lure. */
  async dismiss(id: string) {
    this.remove(id);
    this.busyIds.delete(id);
    try { await this.protocol.dismissInteraction(id); } catch { /* the server may already have dropped it */ }
  }
}
