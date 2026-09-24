import { TauriViewerClient, WebViewerClient, type ViewerClient } from "@linkpoint/viewer-client";
import { LegacyWebTransport } from "./web-transport";

export async function createViewerClient(): Promise<ViewerClient> {
  if (import.meta.env.VITE_VIEWER_RUNTIME === "tauri") {
    const [{ invoke }, { listen }] = await Promise.all([
      import("@tauri-apps/api/core"),
      import("@tauri-apps/api/event"),
    ]);
    return TauriViewerClient.create({ invoke, listen });
  }
  return new WebViewerClient(new LegacyWebTransport());
}
