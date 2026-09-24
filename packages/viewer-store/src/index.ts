import type { ChatMessage, SceneEntity, SessionSnapshot, ViewerEvent } from "../../viewer-types/src/index";

export interface ViewerState {
  readonly connection: "disconnected" | "connecting" | "connected" | "reconnecting";
  readonly session?: SessionSnapshot;
  readonly disconnectReason?: string;
  readonly error?: { readonly code: string; readonly message: string };
  readonly chat: readonly ChatMessage[];
  readonly scene: Readonly<Record<string, SceneEntity>>;
}

export const initialViewerState: ViewerState = {
  connection: "disconnected",
  chat: [],
  scene: {},
};

export function reduceViewerEvent(state: ViewerState, event: ViewerEvent): ViewerState {
  switch (event.type) {
    case "session.connecting":
      return { ...state, connection: "connecting", disconnectReason: undefined, error: undefined };
    case "session.connected":
      return { ...state, connection: "connected", session: event.payload, disconnectReason: undefined };
    case "session.disconnected":
      return { ...state, connection: "disconnected", session: undefined, disconnectReason: event.payload.reason, scene: {} };
    case "session.reconnecting":
      return { ...state, connection: "reconnecting", disconnectReason: `Reconnect attempt ${event.payload.attempt}` };
    case "session.error":
      return { ...state, connection: "disconnected", session: undefined, error: event.payload };
    case "chat.received":
      return { ...state, chat: [...state.chat, event.payload] };
    case "scene.snapshot":
      return { ...state, scene: Object.fromEntries(event.payload.map((entity) => [entity.id, entity])) };
    case "scene.upsert":
      return { ...state, scene: { ...state.scene, [event.payload.id]: event.payload } };
    case "scene.remove": {
      const { [event.payload.id]: removed, ...scene } = state.scene;
      void removed;
      return { ...state, scene };
    }
  }
}
