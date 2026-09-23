
/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_DEV_SERVER_URL?: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
interface Window {
  linkpointDesktop?: {
    allowLoginEndpoint(url: string): Promise<boolean>;
    request(request: { url: string; method?: string; headers?: Record<string, string>; body?: string }): Promise<{
      ok: boolean;
      status: number;
      statusText: string;
      text: string;
      headers: Record<string, string>;
    }>;
    connectViewer(request: { loginUrl: string; username: string; password: string; start?: string }): Promise<Record<string, any>>;
    sendChat(request: { message: string; channel?: number; type?: number }): Promise<void>;
    disconnectViewer(): Promise<void>;
    onViewerEvent(listener: (event: { type: string; data: any }) => void): () => void;
  };
}
