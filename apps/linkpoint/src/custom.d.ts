
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
    connectViewer(request: { loginUrl: string; username: string; password: string; start?: string; mfaToken?: string; mfaHash?: string }): Promise<Record<string, any>>;
    /** Any viewer-session operation by name; the allow-list is core/viewer-api.cjs. */
    call(method: string, params?: Record<string, unknown>): Promise<any>;
    fetchProfilePhoto(request: { name: string; full?: boolean }): Promise<{ photoBytes: string | null; contentType?: string }>;
    disconnectViewer(): Promise<void>;
    onViewerEvent(listener: (event: { type: string; data: any }) => void): () => void;
  };
}
