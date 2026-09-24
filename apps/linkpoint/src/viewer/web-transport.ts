import type { ChatMessage, LoginRequest, ViewerEvent } from "@linkpoint/viewer-types";
import type { Unsubscribe, ViewerEventListener, WebViewerTransport } from "@linkpoint/viewer-client";
import { app, type LinkpointApp } from "../linkpoint/app";

/** Isolates the imported browser protocol while it is replaced by Rust/Tauri. */
export class LegacyWebTransport implements WebViewerTransport {
  readonly #app: LinkpointApp;
  readonly #listeners = new Set<ViewerEventListener>();
  #initialized: Promise<void>;

  constructor(runtime: LinkpointApp = app) {
    this.#app = runtime;
    this.#initialized = runtime.init();
    runtime.chat.on("message_received", (message: unknown) => this.#chat(message));
    runtime.chat.on("message_sent", (message: unknown) => this.#chat(message));
    runtime.protocol.on("disconnected", (reason: unknown) => this.#emit({
      type: "session.disconnected",
      payload: { reason: typeof reason === "string" ? reason : "connection closed" },
    }));
    runtime.world.on("objects_changed", (objects: unknown[]) => this.#emit({
      type: "scene.snapshot",
      payload: objects.map(toSceneEntity),
    }));
  }

  async login(request: LoginRequest) {
    await this.#initialized;
    const grid = request.grid === "opensim" ? request.loginUri : gridKey(request.loginUri);
    await this.#app.auth.login(grid, request.username, request.password, true, request.start ?? "last");
    return {
      agentId: this.#app.protocol.agentId ?? "",
      sessionId: this.#app.protocol.sessionId ?? "",
      regionName: String(this.#app.protocol.authReply?.sim_name ?? this.#app.protocol.authReply?.region_name ?? "Unknown region"),
    };
  }

  async logout() {
    await this.#app.auth.logout();
  }

  async sendChat(body: string) {
    await this.#app.chat.sendMessage(body);
  }

  async touchObject(objectId: string, face?: number) {
    const touch = (this.#app.protocol as unknown as { touchObject?: (id: string, face?: number) => Promise<void> }).touchObject;
    if (!touch) throw new Error("Object interaction is unavailable in the browser runtime");
    await touch.call(this.#app.protocol, objectId, face);
  }

  subscribe(listener: ViewerEventListener): Unsubscribe {
    this.#listeners.add(listener);
    return () => this.#listeners.delete(listener);
  }

  #emit(event: ViewerEvent) {
    for (const listener of this.#listeners) listener(event);
  }

  #chat(value: unknown) {
    const message = value as Record<string, unknown>;
    const payload: ChatMessage = {
      id: String(message.id ?? crypto.randomUUID()),
      fromId: String(message.senderId ?? ""),
      fromName: String(message.sender ?? "Unknown"),
      body: String(message.text ?? ""),
      timestamp: new Date(Number(message.timestamp ?? Date.now())).toISOString(),
    };
    this.#emit({ type: "chat.received", payload });
  }
}

function gridKey(uri: string) {
  if (uri.includes("login.aditi.lindenlab.com")) return "aditi";
  return "agni";
}

function toSceneEntity(value: unknown) {
  const object = value as Record<string, unknown>;
  const position = Array.isArray(object.position) ? object.position : [0, 0, 0];
  return {
    id: String(object.id ?? object.localId ?? "unknown"),
    parentId: object.parentId == null ? undefined : String(object.parentId),
    position: [Number(position[0] ?? 0), Number(position[1] ?? 0), Number(position[2] ?? 0)] as const,
  };
}
