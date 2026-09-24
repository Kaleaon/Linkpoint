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

/** Production browser adapter. Browser limitations remain inside its transport. */
export class WebViewerClient extends EventClient {
  readonly #transport: WebViewerTransport;
  readonly #unsubscribe: Unsubscribe;

  constructor(transport: WebViewerTransport) {
    super();
    this.#transport = transport;
    this.#unsubscribe = transport.subscribe((event) => this.emit(event));
  }

  async execute(command: ViewerCommand): Promise<void> {
    switch (command.type) {
      case "session.login": {
        this.emit({ type: "session.connecting" });
        try {
          const snapshot = await this.#transport.login(command.payload);
          this.emit({ type: "session.connected", payload: snapshot });
        } catch (error) {
          this.emit({ type: "session.error", payload: publicError(error) });
          throw error;
        }
        return;
      }
      case "session.logout":
        await this.#transport.logout();
        this.emit({ type: "session.disconnected", payload: { reason: "logout requested" } });
        return;
      case "session.reconnect":
        throw new Error("Browser reconnect requires a fresh login");
      case "chat.send":
        await this.#transport.sendChat(command.payload.body);
        return;
      case "object.touch":
        await this.#transport.touchObject(command.payload.objectId, command.payload.face);
        return;
      case "lifecycle.suspend":
      case "lifecycle.resume":
      case "network.changed":
        return;
    }
  }

  dispose(): void {
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
