export type GridKind = "second-life" | "opensim";

export interface LoginRequest {
  readonly grid: GridKind;
  readonly loginUri: string;
  readonly username: string;
  readonly password: string;
  readonly start?: string;
}

export interface SessionSnapshot {
  readonly agentId: string;
  readonly sessionId: string;
  readonly regionName: string;
}

export interface ChatMessage {
  readonly id: string;
  readonly fromId: string;
  readonly fromName: string;
  readonly body: string;
  readonly timestamp: string;
}

export interface SceneEntity {
  readonly id: string;
  readonly parentId?: string;
  readonly position: readonly [number, number, number];
}

export type ViewerCommand =
  | { readonly type: "session.login"; readonly payload: LoginRequest }
  | { readonly type: "session.logout" }
  | { readonly type: "chat.send"; readonly payload: { readonly body: string } }
  | { readonly type: "object.touch"; readonly payload: { readonly objectId: string; readonly face?: number } };

export type ViewerEvent =
  | { readonly type: "session.connecting" }
  | { readonly type: "session.connected"; readonly payload: SessionSnapshot }
  | { readonly type: "session.disconnected"; readonly payload: { readonly reason: string } }
  | { readonly type: "chat.received"; readonly payload: ChatMessage }
  | { readonly type: "scene.snapshot"; readonly payload: readonly SceneEntity[] }
  | { readonly type: "scene.upsert"; readonly payload: SceneEntity }
  | { readonly type: "scene.remove"; readonly payload: { readonly id: string } };
