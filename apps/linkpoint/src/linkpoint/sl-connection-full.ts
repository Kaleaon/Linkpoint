/**
 * Linkpoint PWA - Complete SL Connection Implementation
 */

import { toLoginFailure } from './login-failure';
import { Utils } from './utils';
import { SLProtocol } from './sl-protocol-real';
import { LLSD } from './llsd';
import { corsHandler } from './cors-handler';
import { slBridge } from './sl-bridge';

export class SLConnectionFull extends Utils.EventEmitter {
  public state: string = 'IDLE'; // IDLE, AUTHENTICATING, CONNECTING, CONNECTED
  public connected: boolean = false;
  public authReply: any = null;
  public agentId: string | null = null;
  public sessionId: string | null = null;
  public circuitCode: number | null = null;
  public simAddress: string | null = null;
  public simPort: number | null = null;
  public seedCapability: string | null = null;
  public capabilities: Record<string, string> = {};

  public isHypergrid: boolean = false;
  public foreignGridUri: string | null = null;
  public homeGridUri: string | null = null;

  public inventoryRoot: string | null = null;
  public eventQueueRunning: boolean = false;
  private lastEventId: number | null = null;
  private eventQueueTimer: ReturnType<typeof setTimeout> | null = null;
  private eventQueueFailures = 0;

  // Real-Time Second Life Telemetry & Diagnostics. Every figure is unknown
  // (null / empty) until the simulator or login reply supplies it; nothing here
  // is a placeholder that could be mistaken for a measurement.
  private diagnostics = {
    connected: false,
    latencyMs: null as number | null,
    packetLossPct: null as number | null,
    packetsIn: null as number | null,
    packetsOut: null as number | null,
    lastPacketTimestamp: null as number | null,
    simName: '',
    simAddress: '',
    simPort: null as number | null,
    circuitCode: null as number | null,
    agentId: '',
  };

  public getDiagnostics() {
    const isConn = this.connected || slBridge.connected;
    return {
      ...this.diagnostics,
      connected: isConn,
      agentId: this.agentId || this.diagnostics.agentId || (typeof window !== 'undefined' && (window as any).app?.auth?.user?.id) || '',
      simName: this.authReply?.sim_name || (typeof window !== 'undefined' && (window as any).app?.world?.regionName) || this.diagnostics.simName,
      // Prefer what the login reply actually told us over the last polled snapshot.
      simAddress: this.simAddress || this.diagnostics.simAddress,
      simPort: this.simPort || this.diagnostics.simPort,
      circuitCode: this.circuitCode || this.diagnostics.circuitCode,
    };
  }

  public async fetchDiagnostics() {
    if (slBridge.connected) {
      try {
        const live = await slBridge.fetchDiagnostics();
        if (live) {
          this.diagnostics = { ...this.diagnostics, ...live };
          this.emit('diagnostics_updated', this.getDiagnostics());
          return this.getDiagnostics();
        }
      } catch (err) {
        console.warn('[SL Connection] fetchDiagnostics warning:', err);
      }
    }
    return this.getDiagnostics();
  }

  constructor() {
    super();
  }

  private resetConnectionState() {
    this.connected = false;
    this.balance = null;
    this.authReply = null;
    this.agentId = null;
    this.sessionId = null;
    this.circuitCode = null;
    this.simAddress = null;
    this.simPort = null;
    this.seedCapability = null;
    this.capabilities = {};
    this.inventoryRoot = null;
    this.eventQueueRunning = false;
    this.lastEventId = null;
    if (this.eventQueueTimer) clearTimeout(this.eventQueueTimer);
    this.eventQueueTimer = null;
    this.eventQueueFailures = 0;
  }

  /** Route every viewer-session event to the interface. The one mapping for desktop, web and mobile. */
  private attachBridge() {
    slBridge.removeAllListeners();
    const forward = (from: string, to: string, wrap: (data: any) => any = (data) => data) => slBridge.on(from, (data: any) => this.emit(to, wrap(data)));
    forward('chat', 'ChatFromSimulator');
    forward('im', 'ChatFromSimulator', (data) => ({ ...data, chatType: 'im' }));
    forward('group-chat', 'ChatFromSimulator', (data) => ({ ...data, chatType: 'group', type: 'group' }));
    forward('group-notice', 'group_notice');
    forward('friend-status', 'friend_status');
    forward('friend-request', 'friend_request');
    forward('friend-response', 'friend_response');
    forward('friend-remove', 'friend_remove');
    forward('script-dialog', 'script_dialog');
    forward('lure', 'lure');
    forward('parcel-properties', 'ParcelProperties', (data) => ({ parcelData: data }));
    forward('coarse-avatar', 'CoarseAvatarUpdate');
    forward('mute-list-update', 'MuteListUpdate');
    forward('use-cached-mute-list', 'UseCachedMuteList');
    forward('update-mute-list-entry', 'UpdateMuteListEntry');
    forward('remove-mute-list-entry', 'RemoveMuteListEntry');
    for (const name of ['avatar_presence', 'avatar-presence']) {
      slBridge.on(name, (data: any) => {
        this.emit('avatar_presence', data);
        this.emit('avatar-presence', data);
      });
    }
    // Everything else the session announces is scene data: objects, assets, textures, terrain, environment...
    for (const type of ['object-add', 'object-update', 'object-remove', 'asset-ready', 'asset-error', 'animations', 'texture-ready', 'material-ready', 'world-data', 'environment', 'terrain']) {
      forward(type, `scene:${type}`);
    }
    slBridge.on('disconnected', (data: any) => {
      this.connected = false;
      this.setState('IDLE');
      this.emit('disconnected', data);
    });
  }

  private async finishLogin(loginResult: any) {
    this.authReply = loginResult;
    this.agentId = String(loginResult.agent_id);
    // The session runs the circuit for us, so the interface learns only what the login reply says.
    this.sessionId = String(loginResult.sessionId ?? loginResult.session_id);
    this.circuitCode = Number(loginResult.circuit_code) || null;
    this.inventoryRoot = loginResult.inventory_root || null;
    this.simAddress = null;
    this.simPort = null;
    this.setState('CONNECTED');
    this.connected = true;

    try {
      const friendsList = await slBridge.fetchFriends();
      if (Array.isArray(friendsList) && friendsList.length > 0) this.emit('friends_loaded', friendsList);
    } catch (fErr) {
      console.warn('[SL Connection] fetchFriends warning:', fErr);
    }

    this.emit('connected', loginResult);
    return loginResult;
  }

  async connect(gridId: string, username: string, password: string, startLocation: string = 'last', mfa: { token?: string; hash?: string } = {}) {
    this.resetConnectionState();
    this.setState('AUTHENTICATING');

    try {
      if (gridId === 'gemini' || gridId === 'offline' || gridId === 'local') {
        throw new Error('Synthetic grid sessions have been removed; select a live Second Life or OpenSim endpoint');
      }
      const loginUrl = SLProtocol.getLoginUrl(gridId) || gridId;
      if (!loginUrl) throw new Error('Invalid or insecure grid selected');
      this.attachBridge();
      const loginResult = await slBridge.connect({ loginUrl, username, password, start: startLocation, mfaToken: mfa.token, mfaHash: mfa.hash });
      return await this.finishLogin(loginResult);
    } catch (error) {
      this.resetConnectionState();
      this.setState('IDLE');
      const failure = toLoginFailure(error);
      this.emit('connection_failed', failure);
      throw failure;
    }
  }

  /** Log in with credentials held by the web server. */
  async autoLogin(startLocation: string = 'last') {
    try {
      this.setState('AUTHENTICATING');
      this.attachBridge();
      return await this.finishLogin(await slBridge.autoLogin(startLocation));
    } catch (error) {
      this.resetConnectionState();
      this.setState('IDLE');
      this.emit('connection_failed', error);
      throw error;
    }
  }

  async fetchCapabilities() {
    try {
      const capsToRequest = ['EventQueueGet', 'FetchInventoryDescendents2', 'ChatSessionRequest', 'GetDisplayNames', 'AgentInventoryService', 'AgentInventoryService3'];
      const response = await corsHandler.makeRequest(this.seedCapability!, {
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
        body: LLSD.buildXML(capsToRequest)
      });

      if (response && response.ok) {
        const text = await response.text();
        this.capabilities = LLSD.parseXML(text);
        this.emit('capabilities_ready', this.capabilities);
      }
    } catch (error) {
      console.error('Failed to fetch capabilities:', error);
    }
  }

  startEventQueue() {
    if (this.eventQueueRunning) return;
    this.eventQueueRunning = true;
    this.pollEventQueue();
  }

  async pollEventQueue() {
    if (!this.eventQueueRunning) return;

    try {
      const url = this.capabilities.EventQueueGet;
      const body = this.lastEventId
        ? LLSD.buildXML({ ack: this.lastEventId, done: false })
        : LLSD.buildXML({ done: false });

      const response = await corsHandler.makeRequest(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
        body
      });

      if (response && response.ok) {
        const text = await response.text();
        const data = LLSD.parseXML(text);

        if (data && data.events) {
          data.events.forEach((event: any) => this.handleEvent(event));
          if (data.id) this.lastEventId = data.id;
        }
        this.eventQueueFailures = 0;
      } else {
        this.eventQueueFailures++;
      }
    } catch (error) {
      console.error('Event queue error:', error);
      this.eventQueueFailures++;
    }

    if (this.eventQueueRunning) {
      if (this.eventQueueFailures >= 5) {
        this.eventQueueRunning = false;
        this.emit('event_queue_failed');
        return;
      }
      const delay = Math.min(30_000, 1_000 * 2 ** this.eventQueueFailures);
      this.eventQueueTimer = setTimeout(() => this.pollEventQueue(), delay);
    }
  }

  handleEvent(event: any) {
    console.log('Event:', event.message, event.body);
    const body = event.body ?? event.data ?? event;
    this.emit(event.message, body);
    if (event.message?.includes('_')) {
      this.emit(event.message.replace(/_/g, '-'), body);
    } else if (event.message?.includes('-')) {
      this.emit(event.message.replace(/-/g, '_'), body);
    }
    const lower = String(event.message || '').toLowerCase();
    if (lower === 'avatar_presence' || lower === 'avatarpresence' || lower === 'avatar-presence') {
      this.emit('avatar_presence', body);
      this.emit('avatar-presence', body);
    }
  }

  // ---- viewer actions -----------------------------------------------------
  // Both the desktop app and the web server run the same validated actions.
  private requireConnected() {
    if (!this.connected && !slBridge.connected) throw new Error('Not connected to a grid');
  }

  /** Teleport to "secondlife://Region/x/y/z", a map URL, "Region/x/y/z", or Hypergrid URI (e.g. hg.osgrid.org:8002:RegionName). */
  async teleportTo(destination: string) {
    this.requireConnected();
    const result = await slBridge.teleport({ destination });
    if (result?.isHypergrid || result?.requested?.isHypergrid) {
      this.isHypergrid = true;
      this.foreignGridUri = result.requested?.gridUri || null;
    } else {
      this.isHypergrid = false;
      this.foreignGridUri = null;
    }
    this.emit('teleport_requested', result);
    return result;
  }

  /** A resident's public profile picture (base64), or null when they have none. */
  async fetchProfilePhoto(name: string, full = false) {
    this.requireConnected();
    return slBridge.fetchProfilePhoto(name, full);
  }

  /** Answer a script dialog: pass a button index, or `text` for a text box. */
  async respondScriptDialog(request: { id: string; buttonIndex?: number; text?: string }) {
    this.requireConnected();
    return slBridge.respondScriptDialog(request);
  }

  /** Accept a teleport lure. Resolves when the grid reports the teleport result. */
  async acceptLure(id: string) {
    this.requireConnected();
    return slBridge.acceptLure({ id });
  }

  /** Forget an interaction on the server. Nothing is sent to the grid. */
  async dismissInteraction(id: string) {
    this.requireConnected();
    return slBridge.dismissInteraction({ id });
  }

  /** Touch an object by id. Face and texture coordinates are sent only when known. */
  async touchObject(target: { id?: string; localId?: number; face?: number; uv?: number[]; st?: number[]; position?: number[] }) {
    this.requireConnected();
    return slBridge.touchObject(target);
  }

  async sit(id?: string) {
    this.requireConnected();
    return slBridge.sit({ id });
  }

  async stand() {
    this.requireConnected();
    return slBridge.stand();
  }

  /** L$ balance, or null when the grid has not answered. Never a guess. */
  public balance: number | null = null;

  async refreshBalance(): Promise<number | null> {
    if (!this.connected && !slBridge.connected) { this.balance = null; return null; }
    try {
      const { balance } = await slBridge.getBalance();
      this.balance = Number.isFinite(balance) ? balance : null;
    } catch (error) {
      console.warn('[SL Connection] balance unavailable:', error);
      this.balance = null;
    }
    this.emit('balance_updated', this.balance);
    return this.balance;
  }

  async payObject(params: { objectId?: string; targetId?: string; id?: string; amount?: number; price?: number; description?: string }) {
    this.requireConnected();
    const result = await slBridge.payObject(params);
    if (result && typeof result.balance === 'number' && Number.isFinite(result.balance)) {
      this.balance = result.balance;
      this.emit('balance_updated', this.balance);
    } else {
      void this.refreshBalance();
    }
    return result;
  }

  async sendChat(message: string, channel: number = 0, type: number = 1) {
    if (!this.connected) throw new Error('Not connected to a grid');
    await slBridge.sendChat(message, channel, type);
  }

  async sendInstantMessage(recipientId: string, message: string) {
    if (!this.connected) throw new Error('Not connected to a grid');
    await slBridge.sendInstantMessage(recipientId, message);
  }

  /** Download an animation asset (custom/uploaded animations) as raw bytes. */
  async fetchAnimation(id: string): Promise<Uint8Array> {
    if (!this.connected) throw new Error('Not connected to a grid');
    const reply = await slBridge.fetchAnimation(id);
    if (!reply?.data) throw new Error('Animation downloads are unavailable on this connection');
    const binary = atob(reply.data);
    return Uint8Array.from(binary, (character) => character.charCodeAt(0));
  }

  // A group message is never spoken in the region: it goes to the group's chat session or fails.
  async sendGroupMessage(groupId: string, message: string) {
    if (!this.connected) throw new Error('Not connected to a grid');
    await slBridge.sendGroupMessage(groupId, message);
  }

  async sendFriendRequest(recipientId: string, message?: string) {
    if (!this.connected) throw new Error('Not connected to a grid');
    await slBridge.sendFriendRequest(recipientId, message);
  }

  async fetchFriends() {
    if (!this.connected) return [];
    return slBridge.fetchFriends();
  }

  getCapability(name: string) {
    return this.capabilities[name];
  }
  private setState(newState: string) {
    this.state = newState;
    this.emit('state_changed', newState);
  }

  async logout() {
    this.eventQueueRunning = false;
    if (this.eventQueueTimer) clearTimeout(this.eventQueueTimer);
    this.eventQueueTimer = null;
    slBridge.disconnect();
    this.connected = false;
    this.setState('IDLE');
    this.emit('disconnected');
  }
}
