import type { LoginRequest, ViewerCommand, ViewerEvent } from "../../viewer-types/src/index";

export type Unsubscribe = () => void;
export type ViewerEventListener = (event: ViewerEvent) => void;

/** The only simulator/runtime boundary exposed to application code. */
export interface ViewerClient {
  execute(command: ViewerCommand): Promise<void>;
  subscribe(listener: ViewerEventListener): Unsubscribe;
  dispose?(): Promise<void> | void;
}

abstract class EventClient implements ViewerClient {
  readonly #listeners = new Set<ViewerEventListener>();

  abstract execute(command: ViewerCommand): Promise<void>;

  subscribe(listener: ViewerEventListener): Unsubscribe {
    this.#listeners.add(listener);
    return () => this.#listeners.delete(listener);
  }

  protected emit(event: ViewerEvent): void {
    for (const listener of this.#listeners) listener(event);
  }
}

/** Deterministic client for screen development and contract tests. */
export class MockViewerClient extends EventClient {
  readonly commands: ViewerCommand[] = [];

  async execute(command: ViewerCommand): Promise<void> {
    this.commands.push(command);
  }

  emit(event: ViewerEvent): void {
    super.emit(event);
  }
}

/** Browser transport deliberately supplied by the app to avoid package cycles. */
export interface WebViewerTransport {
  login(request: LoginRequest): Promise<{ agentId: string; sessionId: string; regionName: string }>;
  logout(): Promise<void>;
  sendChat(body: string): Promise<void>;
  touchObject(objectId: string, face?: number): Promise<void>;
  subscribe(listener: ViewerEventListener): Unsubscribe;
}

export interface WebViewerClientOptions {
  now?: () => number;
  random?: () => number;
  maxAttempts?: number;
  messageTtlMs?: number;
  tokenTtlMs?: number;
  baseDelayMs?: number;
  maxDelayMs?: number;
}

export interface QueuedCommand {
  command: ViewerCommand;
  timestamp: number;
}

/** Production browser adapter with auto-reconnection, token caching, and request queueing. */
export class WebViewerClient extends EventClient {
  readonly #transport: WebViewerTransport;
  readonly #unsubscribe: Unsubscribe;
  readonly #now: () => number;
  readonly #random: () => number;
  readonly #maxAttempts: number;
  readonly #messageTtlMs: number;
  readonly #tokenTtlMs: number;
  readonly #baseDelayMs: number;
  readonly #maxDelayMs: number;

  #loginRequest: LoginRequest | null = null;
  #sessionSnapshot: { agentId: string; sessionId: string; regionName: string } | null = null;
  #connectionState: "disconnected" | "connecting" | "connected" | "reconnecting" = "disconnected";
  #online: boolean = true;
  #offlineStartTimestamp: number | null = null;
  #reconnectAttempts: number = 0;
  #reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  #outboundQueue: QueuedCommand[] = [];

  constructor(transport: WebViewerTransport, options: WebViewerClientOptions = {}) {
    super();
    this.#transport = transport;
    this.#unsubscribe = transport.subscribe((event) => this.emit(event));
    this.#now = options.now ?? (() => Date.now());
    this.#random = options.random ?? Math.random;
    this.#maxAttempts = options.maxAttempts ?? 5;
    this.#messageTtlMs = options.messageTtlMs ?? 60_000;
    this.#tokenTtlMs = options.tokenTtlMs ?? 15 * 60_000;
    this.#baseDelayMs = options.baseDelayMs ?? 1000;
    this.#maxDelayMs = options.maxDelayMs ?? 30_000;
  }

  get connectionState() {
    return this.#connectionState;
  }

  get outboundQueueLength() {
    this.#purgeExpiredQueue();
    return this.#outboundQueue.length;
  }

  get cachedLoginRequest() {
    return this.#loginRequest;
  }

  async execute(command: ViewerCommand): Promise<void> {
    switch (command.type) {
      case "session.login": {
        this.#clearReconnectTimer();
        this.#loginRequest = command.payload;
        this.#connectionState = "connecting";
        this.emit({ type: "session.connecting" });
        try {
          const snapshot = await this.#transport.login(command.payload);
          this.#sessionSnapshot = snapshot;
          this.#connectionState = "connected";
          this.#reconnectAttempts = 0;
          this.#offlineStartTimestamp = null;
          this.emit({ type: "session.connected", payload: snapshot });
          await this.#flushOutboundQueue();
        } catch (error) {
          this.#loginRequest = null;
          this.#sessionSnapshot = null;
          this.#connectionState = "disconnected";
          this.emit({ type: "session.error", payload: publicError(error) });
          throw error;
        }
        return;
      }
      case "session.logout": {
        this.#clearReconnectTimer();
        this.#loginRequest = null;
        this.#sessionSnapshot = null;
        this.#outboundQueue = [];
        this.#offlineStartTimestamp = null;
        this.#reconnectAttempts = 0;
        this.#connectionState = "disconnected";
        await this.#transport.logout();
        this.emit({ type: "session.disconnected", payload: { reason: "logout requested" } });
        return;
      }
      case "session.reconnect": {
        await this.reconnect();
        return;
      }
      case "chat.send": {
        if (this.#connectionState === "connected") {
          try {
            await this.#transport.sendChat(command.payload.body);
          } catch (error) {
            this.#enqueueCommand(command);
            this.#handleTransportDrop("chat send failed");
          }
        } else {
          this.#enqueueCommand(command);
        }
        return;
      }
      case "object.touch": {
        if (this.#connectionState === "connected") {
          try {
            await this.#transport.touchObject(command.payload.objectId, command.payload.face);
          } catch (error) {
            this.#enqueueCommand(command);
            this.#handleTransportDrop("object touch failed");
          }
        } else {
          this.#enqueueCommand(command);
        }
        return;
      }
      case "lifecycle.suspend": {
        if (this.#offlineStartTimestamp === null) {
          this.#offlineStartTimestamp = this.#now();
        }
        this.#clearReconnectTimer();
        if (this.#connectionState === "connected") {
          this.#connectionState = "reconnecting";
          this.emit({ type: "session.reconnecting", payload: { attempt: this.#reconnectAttempts || 1 } });
        }
        return;
      }
      case "lifecycle.resume": {
        if (this.#isTokenExpired()) {
          this.#expireToken();
          return;
        }
        if (this.#online && (this.#connectionState === "reconnecting" || (this.#connectionState === "disconnected" && this.#loginRequest !== null))) {
          await this.reconnect();
        }
        return;
      }
      case "network.changed": {
        const { online } = command.payload;
        this.#online = online;
        if (!online) {
          if (this.#offlineStartTimestamp === null) {
            this.#offlineStartTimestamp = this.#now();
          }
          this.#clearReconnectTimer();
          if (this.#connectionState === "connected") {
            this.#connectionState = "reconnecting";
            this.#reconnectAttempts = 1;
            this.emit({ type: "session.reconnecting", payload: { attempt: 1 } });
            this.emit({ type: "session.disconnected", payload: { reason: "network unavailable" } });
          }
        } else {
          if (this.#isTokenExpired()) {
            this.#expireToken();
            return;
          }
          if (this.#connectionState === "reconnecting" || (this.#connectionState === "disconnected" && this.#loginRequest !== null)) {
            this.#clearReconnectTimer();
            await this.reconnect();
          }
        }
        return;
      }
    }
  }

  async reconnect(): Promise<void> {
    this.#clearReconnectTimer();
    if (!this.#online) {
      this.#connectionState = "reconnecting";
      this.emit({ type: "session.disconnected", payload: { reason: "network unavailable" } });
      return;
    }

    if (this.#isTokenExpired()) {
      this.#expireToken();
      return;
    }

    if (!this.#loginRequest) {
      this.#connectionState = "disconnected";
      const err = new Error("No cached login credentials available");
      this.emit({ type: "session.error", payload: publicError(err) });
      throw err;
    }

    this.#reconnectAttempts++;
    if (this.#reconnectAttempts > this.#maxAttempts) {
      this.#connectionState = "disconnected";
      this.#loginRequest = null;
      this.#sessionSnapshot = null;
      this.#outboundQueue = [];
      const errorPayload = {
        code: "max-retries-exceeded",
        message: "Maximum reconnection attempts reached. Please retry or return to main menu.",
      };
      this.emit({ type: "session.error", payload: errorPayload });
      this.emit({ type: "session.disconnected", payload: { reason: "maximum reconnect attempts exceeded" } });
      return;
    }

    this.#connectionState = "reconnecting";
    this.emit({ type: "session.reconnecting", payload: { attempt: this.#reconnectAttempts } });

    try {
      const snapshot = await this.#transport.login(this.#loginRequest);
      this.#sessionSnapshot = snapshot;
      this.#connectionState = "connected";
      this.#reconnectAttempts = 0;
      this.#offlineStartTimestamp = null;
      this.emit({ type: "session.connected", payload: snapshot });
      await this.#flushOutboundQueue();
    } catch (error) {
      if (this.#reconnectAttempts < this.#maxAttempts) {
        const delay = this.#calculateBackoffDelay(this.#reconnectAttempts);
        this.#reconnectTimer = setTimeout(() => {
          void this.reconnect();
        }, delay);
      } else {
        this.#connectionState = "disconnected";
        this.#sessionSnapshot = null;
        this.#outboundQueue = [];
        this.emit({
          type: "session.error",
          payload: {
            code: "max-retries-exceeded",
            message: "Maximum reconnection attempts reached. Please retry or return to main menu.",
          },
        });
        this.emit({ type: "session.disconnected", payload: { reason: "maximum reconnect attempts exceeded" } });
      }
    }
  }

  #isTokenExpired(): boolean {
    if (this.#offlineStartTimestamp === null) return false;
    return this.#now() - this.#offlineStartTimestamp > this.#tokenTtlMs;
  }

  #expireToken(): void {
    this.#clearReconnectTimer();
    this.#loginRequest = null;
    this.#sessionSnapshot = null;
    this.#outboundQueue = [];
    this.#connectionState = "disconnected";
    this.#offlineStartTimestamp = null;
    this.#reconnectAttempts = 0;
    this.emit({
      type: "session.error",
      payload: {
        code: "token-expired",
        message: "Cached session token expired after 15 minutes offline. Please re-authenticate.",
      },
    });
    this.emit({ type: "session.disconnected", payload: { reason: "session token expired" } });
  }

  #handleTransportDrop(reason: string): void {
    if (this.#connectionState === "connected") {
      this.#connectionState = "reconnecting";
      if (this.#offlineStartTimestamp === null) {
        this.#offlineStartTimestamp = this.#now();
      }
      this.emit({ type: "session.disconnected", payload: { reason } });
      void this.reconnect();
    }
  }

  #enqueueCommand(command: ViewerCommand): void {
    this.#purgeExpiredQueue();
    this.#outboundQueue.push({ command, timestamp: this.#now() });
  }

  #purgeExpiredQueue(): void {
    const now = this.#now();
    this.#outboundQueue = this.#outboundQueue.filter((item) => now - item.timestamp <= this.#messageTtlMs);
  }

  async #flushOutboundQueue(): Promise<void> {
    this.#purgeExpiredQueue();
    const queue = [...this.#outboundQueue];
    this.#outboundQueue = [];
    for (const item of queue) {
      if (this.#connectionState !== "connected") {
        this.#outboundQueue.unshift(item);
        break;
      }
      try {
        if (item.command.type === "chat.send") {
          await this.#transport.sendChat(item.command.payload.body);
        } else if (item.command.type === "object.touch") {
          await this.#transport.touchObject(item.command.payload.objectId, item.command.payload.face);
        }
      } catch (error) {
        this.#outboundQueue.unshift(item);
        this.#handleTransportDrop("flush failed");
        break;
      }
    }
  }

  #calculateBackoffDelay(attempt: number): number {
    const exponent = Math.min(attempt - 1, 5);
    const base = this.#baseDelayMs * Math.pow(2, exponent);
    const capped = Math.min(this.#maxDelayMs, base);
    const jitterFactor = 0.8 + this.#random() * 0.4;
    return Math.floor(capped * jitterFactor);
  }

  #clearReconnectTimer(): void {
    if (this.#reconnectTimer !== null) {
      clearTimeout(this.#reconnectTimer);
      this.#reconnectTimer = null;
    }
  }

  dispose(): void {
    this.#clearReconnectTimer();
    this.#unsubscribe();
  }
}

export interface TauriBindings {
  invoke<T>(command: string, args?: Record<string, unknown>): Promise<T>;
  listen<T>(event: string, handler: (event: { payload: T }) => void): Promise<Unsubscribe>;
}

/** Tauri adapter using only the narrow command/event surface. */
export class TauriViewerClient extends EventClient {
  readonly #bindings: TauriBindings;
  #unsubscribe: Unsubscribe | undefined;

  private constructor(bindings: TauriBindings) {
    super();
    this.#bindings = bindings;
  }

  static async create(bindings: TauriBindings): Promise<TauriViewerClient> {
    const client = new TauriViewerClient(bindings);
    client.#unsubscribe = await bindings.listen<ViewerEvent>("viewer-event", ({ payload }) => client.emit(payload));
    return client;
  }

  async execute(command: ViewerCommand): Promise<void> {
    await this.#bindings.invoke("viewer_execute", { command });
  }

  dispose(): void {
    this.#unsubscribe?.();
    this.#unsubscribe = undefined;
  }
}

function publicError(error: unknown): { code: string; message: string } {
  if (error instanceof Error) return { code: "runtime-error", message: error.message };
  return { code: "runtime-error", message: "The viewer operation failed." };
}
