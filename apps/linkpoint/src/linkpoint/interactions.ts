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

export interface PaymentRequest {
  kind: 'payment';
  id: string;
  receivedAt: number;
  objectId: string;
  objectName: string;
  sellerName: string;
  sellerId?: string | null;
  price: number;
  currency?: string;
}

export type Interaction = ScriptDialogRequest | LureRequest | PaymentRequest;

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
    this.protocol.on('payment_request', (data: any) => this.add('payment', data));
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
      : kind === 'lure'
      ? {
          ...base, kind,
          fromId: data.fromId ?? null,
          fromName: String(data.fromName || ''),
          message: String(data.message || ''),
          regionId: data.regionId ?? null,
          position: Array.isArray(data.position) && data.position.length === 3 ? (data.position as [number, number, number]) : null,
          gridX: Number.isFinite(data.gridX) ? data.gridX : null,
          gridY: Number.isFinite(data.gridY) ? data.gridY : null,
        }
      : {
          ...base, kind: 'payment',
          objectId: String(data.objectId || data.targetId || '00000000-0000-0000-0000-000000000000'),
          objectName: String(data.objectName || data.name || 'Vendor Item'),
          sellerName: String(data.sellerName || data.ownerName || 'Simulator Resident'),
          sellerId: data.sellerId || data.ownerId || null,
          price: Math.max(0, Number.isFinite(Number(data.price ?? data.amount)) ? Number(data.price ?? data.amount) : 0),
          currency: String(data.currency || 'L$'),
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

  /** Intercept a payment request before dispatching payment packets, opening the confirmation modal dialog. */
  requestPayment(data: { objectId: string; objectName?: string; sellerName?: string; sellerId?: string; price: number; currency?: string }) {
    const id = Utils.generateUUID();
    const payload = {
      id,
      receivedAt: Date.now(),
      objectId: data.objectId,
      objectName: data.objectName || 'Vendor Item',
      sellerName: data.sellerName || 'Simulator Resident',
      sellerId: data.sellerId || null,
      price: Math.max(0, Math.floor(Number(data.price) || 0)),
      currency: data.currency || 'L$',
    };
    this.add('payment', payload);
    void this.protocol.refreshBalance();
    return id;
  }

  /** Confirm a payment interaction, sending payment packets after balance verification. */
  async confirmPayment(id: string) {
    const payment = this.list.find((item) => item.id === id && item.kind === 'payment') as PaymentRequest | undefined;
    if (!payment) {
      this.emit('interaction_failed', { id, message: 'Payment request not found or expired' });
      return false;
    }

    const currentBalance = this.protocol.balance !== null ? this.protocol.balance : await this.protocol.refreshBalance();
    if (currentBalance !== null && currentBalance < payment.price) {
      const msg = `Insufficient funds: Required L$ ${payment.price}, current balance is L$ ${currentBalance}`;
      this.emit('interaction_failed', { id, message: msg });
      throw new Error(msg);
    }

    const result = await this.run(id, async () => {
      return this.protocol.payObject({
        objectId: payment.objectId,
        amount: payment.price,
        targetId: payment.sellerId || undefined,
        description: `Payment for ${payment.objectName}`,
      });
    });

    if (result) {
      const updatedBalance = this.protocol.balance;
      this.emit('payment_completed', {
        payment,
        newBalance: updatedBalance,
        message: `Paid L$ ${payment.price} for "${payment.objectName}". Updated balance: L$ ${updatedBalance ?? 'N/A'}`,
      });
      return true;
    }
    return false;
  }

  /** Cancel a payment request locally without sending any payment packets. */
  async cancelPayment(id: string) {
    await this.dismiss(id);
  }

  /** Dismiss locally. The grid is not told: the client library cannot decline a lure. */
  async dismiss(id: string) {
    this.remove(id);
    this.busyIds.delete(id);
    try { await this.protocol.dismissInteraction(id); } catch { /* the server may already have dropped it */ }
  }
}
