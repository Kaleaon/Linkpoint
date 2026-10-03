import { Utils } from './utils';
import { failureFromResponseBody } from './login-failure';
import { rateLimitedFetch } from './rate-limited-fetch';

const READ_ONLY_CALLS = new Set([
  'fetchAnimation', 'getBalance', 'getDiagnostics', 'getFriends', 'getGroups', 'getInventory',
  'getMapBlocks', 'getSceneObjects', 'getSceneSnapshot', 'getTransactionHistory', 'searchDir',
]);

export interface SLBridgeConnectParams {
  loginUrl: string;
  username: string;
  password: string;
  start?: string;
  mfaToken?: string;
  mfaHash?: string;
}

/** The desktop app's preload API, when running inside it. */
const desktop = () => (typeof window !== 'undefined' ? window.linkpointDesktop : undefined);

/**
 * The single transport between the interface and the viewer session in core/. Inside the desktop
 * app calls go over IPC; in a browser (or the mobile shell) they go to the web server over HTTP and
 * server-sent events. Callers never see the difference: they `call(method, params)` and listen for
 * events, and the same methods run on the same `ViewerSession` either way.
 */
export class SLBridge extends Utils.EventEmitter {
  public sessionId: string | null = null;
  public connected: boolean = false;
  private eventSource: EventSource | null = null;
  private removeNativeListener: (() => void) | null = null;
  private pendingReads = new Map<string, Promise<any>>();

  private async failure(response: Response, fallback: string) {
    const err = await response.json().catch(() => ({ error: fallback }));
    return failureFromResponseBody(err) || new Error(err.error || err.message || `${fallback} (HTTP ${response.status})`);
  }

  private begin(data: any) {
    this.sessionId = String(data.sessionId ?? data.session_id ?? '');
    this.connected = true;
    if (desktop()) {
      this.removeNativeListener?.();
      this.removeNativeListener = desktop()!.onViewerEvent(({ type, data: payload }) => this.emit(type, payload));
    } else {
      this.startEventStream(this.sessionId);
    }
    return data;
  }

  async connect(params: SLBridgeConnectParams) {
    this.disconnect();
    const native = desktop();
    if (native) {
      // Grid endpoints must be vetted by the main process before it will talk to them.
      await native.allowLoginEndpoint(params.loginUrl);
      // Subscribe first: the session streams scene data while the login is still completing.
      this.removeNativeListener = native.onViewerEvent(({ type, data }) => this.emit(type, data));
      try {
        return this.begin(await native.connectViewer(params));
      } catch (error) {
        this.removeNativeListener?.();
        this.removeNativeListener = null;
        throw error;
      }
    }

    const response = await fetch('/api/sl/connect', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    });
    if (!response.ok) throw await this.failure(response, 'Second Life connection failed');
    return this.begin(await response.json());
  }

  /** Server-held credentials; only the web server can have them. */
  async checkAutoLoginStatus(): Promise<{ available: boolean; username: string; grid: string }> {
    if (desktop()) return { available: false, username: '', grid: 'agni' };
    try {
      const response = await fetch('/api/sl/auto-login-status');
      if (!response.ok) return { available: false, username: '', grid: 'agni' };
      return await response.json();
    } catch {
      return { available: false, username: '', grid: 'agni' };
    }
  }

  async autoLogin(start?: string) {
    this.disconnect();
    const response = await fetch('/api/sl/auto-login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ start }),
    });
    if (!response.ok) throw await this.failure(response, 'Auto-login failed');
    return this.begin(await response.json());
  }

  private startEventStream(sessionId: string) {
    this.eventSource?.close();
    const es = new EventSource(`/api/sl/events?sessionId=${encodeURIComponent(sessionId)}`);
    this.eventSource = es;
    es.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data);
        if (payload?.type) this.emit(payload.type, payload.data);
      } catch (err) {
        console.warn('[SL Bridge] Error parsing event:', err);
      }
    };
    es.onerror = () => {
      console.warn('[SL Bridge] EventSource disconnected');
    };
  }

  /** Run one viewer-session method (see core/viewer-api.cjs) and return its result, or throw its message. */
  async call<T = any>(method: string, params?: Record<string, unknown>): Promise<T> {
    if (!this.connected) throw new Error('Not connected to Second Life');
    const native = desktop();
    if (native) return native.call(method, params) as Promise<T>;
    if (!this.sessionId) throw new Error('Not connected to Second Life');
    const request = async () => {
      const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: this.sessionId, method, params }),
      };
      // Never replay chat, payments, movement, or other mutations. Read calls can be throttled,
      // coalesced and retried without applying an action twice.
      const response = READ_ONLY_CALLS.has(method)
        ? await rateLimitedFetch('/api/sl/call', options)
        : await fetch('/api/sl/call', options);
      const data = await response.json().catch(() => ({}));
      if (!response.ok) throw new Error(data.error || data.message || `Request failed (HTTP ${response.status})`);
      return data as T;
    };
    if (!READ_ONLY_CALLS.has(method)) return request();
    const key = JSON.stringify([this.sessionId, method, params || null]);
    const existing = this.pendingReads.get(key);
    if (existing) return existing as Promise<T>;
    const pending = request().finally(() => this.pendingReads.delete(key));
    this.pendingReads.set(key, pending);
    return pending;
  }

  teleport(params: { destination?: string; region?: string; x?: number; y?: number; z?: number }) {
    return this.call<{ requested: { region: string; x: number; y: number; z: number }; message: string }>('teleport', params);
  }
  respondScriptDialog(params: { id: string; buttonIndex?: number; text?: string }) {
    return this.call<{ answered: boolean }>('respondScriptDialog', params);
  }
  acceptLure(params: { id: string }) { return this.call<{ accepted: boolean; message: string }>('acceptLure', params); }
  acceptInventoryOffer(params: { id: string }) { return this.call<{ accepted: boolean }>('acceptInventoryOffer', params); }
  acceptGroupInvite(params: { id: string }) { return this.call<{ accepted: boolean }>('acceptGroupInvite', params); }
  dismissInteraction(params: { id: string }) { return this.call<{ dismissed: boolean }>('dismissInteraction', params); }
  touchObject(params: { id?: string; localId?: number; face?: number; uv?: number[]; st?: number[]; position?: number[] }) {
    return this.call<{ touched: string | number }>('touchObject', params);
  }
  sit(params: { id?: string } = {}) { return this.call<{ sitting: string }>('sit', params); }
  stand() { return this.call<{ standing: boolean }>('stand'); }
  setMovement(params: { forward?: number; right?: number; up?: number; turn?: number; run?: boolean }) {
    return this.call<{ moving: boolean }>('setMovement', params);
  }
  getBalance() { return this.call<{ balance: number; currencySymbol?: string; currency_symbol?: string; isZeroCurrency?: boolean; is_zero_currency?: boolean }>('getBalance'); }
  payObject(params: { targetId?: string; id?: string; objectId?: string; amount: number; description?: string; targetName?: string; currencySymbol?: string; isZeroCurrency?: boolean }) {
    return this.call<{ paid: boolean; targetId: string; amount: number; description: string; transaction?: any }>('payObject', params);
  }
  payAvatar(params: { targetId?: string; id?: string; avatarId?: string; amount: number; description?: string; targetName?: string; currencySymbol?: string; isZeroCurrency?: boolean }) {
    return this.call<{ paid: boolean; targetId: string; amount: number; description: string; transaction?: any }>('payAvatar', params);
  }
  getTransactionHistory() {
    return this.call<{ balance: number; currencySymbol?: string; currency_symbol?: string; isZeroCurrency?: boolean; is_zero_currency?: boolean; transactions: any[] }>('getTransactionHistory');
  }

  async sendChat(message: string, channel = 0, type = 1) { await this.call('sendChat', { message, channel, type }); }
  async sendInstantMessage(recipientId: string, message: string) { await this.call('sendInstantMessage', { recipientId, message }); }
  async sendGroupMessage(groupId: string, message: string) { await this.call('sendGroupMessage', { groupId, message }); }
  async sendFriendRequest(recipientId: string, message?: string) { await this.call('sendFriendRequest', { recipientId, message }); }

  /** A resident's public profile picture as base64, or null when they have none. */
  async fetchProfilePhoto(name: string, full = false): Promise<{ photoBytes: string | null; contentType?: string }> {
    if (!this.connected) throw new Error('Not connected to Second Life');
    const native = desktop();
    if (native) return native.fetchProfilePhoto({ name, full });
    const query = `sessionId=${encodeURIComponent(this.sessionId || '')}&name=${encodeURIComponent(name)}${full ? '&size=full' : ''}`;
    const response = await rateLimitedFetch(`/api/sl/avatar/photo?${query}`);
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(data.error || `Request failed (HTTP ${response.status})`);
    return data;
  }

  async fetchFriends() { return this.connected ? this.call<any[]>('getFriends') : []; }
  async fetchGroups() { return this.connected ? this.call<any[]>('getGroups') : []; }
  async fetchInventory(folderId?: string) {
    return this.connected ? this.call('getInventory', folderId ? { folderId } : {}) : { folders: [], items: [] };
  }
  async fetchScene() {
    if (!this.connected) return [];
    try { return await this.call<any[]>('getSceneObjects'); } catch { return []; }
  }
  async fetchSceneSnapshot(): Promise<{ objects: any[]; assets: any[] }> {
    if (!this.connected) return { objects: [], assets: [] };
    try {
      const snapshot = await this.call<{ objects: any[]; assets: any[] }>('getSceneSnapshot');
      return snapshot || { objects: [], assets: [] };
    } catch {
      return { objects: await this.fetchScene(), assets: [] };
    }
  }
  async fetchDiagnostics() {
    if (!this.connected) return null;
    try { return await this.call('getDiagnostics'); } catch { return null; }
  }
  /** Directory search capabilities across grid categories ('people', 'groups', 'places'). */
  async searchDir(params: { category: string; query: string; start?: number }): Promise<{
    results: Array<{
      id: string;
      name?: string;
      displayName?: string;
      username?: string;
      firstName?: string;
      lastName?: string;
      group?: string;
      online?: boolean;
      members?: number;
      description?: string;
      dwell?: number;
      forSale?: boolean;
      type: string;
      simName?: string;
    }>;
    hasMore?: boolean;
  }> {
    if (!this.connected) {
      throw new Error('Not connected to Second Life');
    }
    return this.call('searchDir', params);
  }

  fetchAnimation(id: string): Promise<{ id: string; data: string }> { return this.call('fetchAnimation', { id }); }
  voiceProvision(sdp: string, parcelLocalId?: number) { return this.call<any>('voiceProvision', { sdp, parcelLocalId }); }
  voiceSignal(viewerSession: string, candidates?: RTCIceCandidateInit[], completed = false) { return this.call('voiceSignal', { viewerSession, candidates, completed }); }
  voiceLogout(viewerSession: string) { return this.call('voiceLogout', { viewerSession }); }

  disconnect() {
    this.eventSource?.close();
    this.eventSource = null;
    this.removeNativeListener?.();
    this.removeNativeListener = null;

    const wasConnected = this.connected;
    const sid = this.sessionId;
    this.sessionId = null;
    this.connected = false;
    this.pendingReads.clear();
    if (wasConnected) {
      const native = desktop();
      if (native) void native.disconnectViewer().catch(() => {});
      else if (sid) {
        fetch('/api/sl/disconnect', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ sessionId: sid }),
        }).catch(() => {});
      }
    }
    this.emit('disconnected', { message: 'Disconnected' });
  }
}

export const slBridge = new SLBridge();
