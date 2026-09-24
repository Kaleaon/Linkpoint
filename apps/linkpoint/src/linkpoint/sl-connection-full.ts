/**
 * Linkpoint PWA - Complete SL Connection Implementation
 */

import { Utils } from './utils';
import { SLProtocol } from './sl-protocol-real';
import { LLSD } from './llsd';
import { corsHandler } from './cors-handler';

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

  public inventoryRoot: string | null = null;
  public eventQueueRunning: boolean = false;
  private lastEventId: number | null = null;
  private eventQueueTimer: ReturnType<typeof setTimeout> | null = null;
  private eventQueueFailures = 0;

  constructor() {
    super();
  }

  private resetConnectionState() {
    this.connected = false;
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

  async connect(gridId: string, username: string, password: string, startLocation: string = 'last') {
    this.resetConnectionState();
    this.setState('AUTHENTICATING');

    try {
      const protocol = new SLProtocol();
      const loginResult = await protocol.login(gridId, username, password, startLocation);

      this.authReply = loginResult;
      this.agentId = loginResult.agent_id;
      this.sessionId = loginResult.session_id;
      this.circuitCode = parseInt(loginResult.circuit_code);
      this.simAddress = loginResult.sim_ip;
      this.simPort = parseInt(loginResult.sim_port);
      this.seedCapability = loginResult.seed_capability;
      this.inventoryRoot = loginResult['inventory-root']?.[0]?.folder_id || null;

      if (this.seedCapability) {
        await this.fetchCapabilities();
      }

      this.setState('CONNECTING');
      // In browser, we simulate circuit establishment via capabilities

      if (this.capabilities.EventQueueGet) {
        this.startEventQueue();
      }

      this.setState('CONNECTED');
      this.connected = true;
      this.emit('connected', loginResult);
      return loginResult;

    } catch (error) {
      this.resetConnectionState();
      this.setState('IDLE');
      this.emit('connection_failed', error);
      throw error;
    }
  }

  async fetchCapabilities() {
    try {
      const capsToRequest = ['EventQueueGet', 'FetchInventoryDescendents2', 'ChatSessionRequest', 'GetDisplayNames'];
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
    this.emit(event.message, event.body);
  }

  async sendChat(message: string, channel: number = 0, type: number = 1) {
    if (!this.connected) throw new Error('Not connected to a grid');
    if (!this.capabilities.ChatSessionRequest) throw new Error('This grid did not provide a chat capability');

    try {
      await corsHandler.makeRequest(this.capabilities.ChatSessionRequest, {
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
        body: LLSD.buildXML({ message, channel, type })
      });
    } catch (error) {
      console.error('Failed to send chat:', error);
      throw error;
    }
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
    this.connected = false;
    this.setState('IDLE');
    this.emit('disconnected');
  }
}
