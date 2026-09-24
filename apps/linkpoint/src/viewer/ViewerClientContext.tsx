import { createContext, useContext, useEffect, useMemo, useReducer } from "react";
import type { ReactNode } from "react";
import type { ViewerClient } from "@linkpoint/viewer-client";
import { initialViewerState, reduceViewerEvent } from "@linkpoint/viewer-store";

const ViewerClientContext = createContext<ViewerClient | null>(null);
const ViewerStateContext = createContext(initialViewerState);

export function ViewerClientProvider({ client, children }: { client: ViewerClient; children: ReactNode }) {
  const [state, dispatch] = useReducer(reduceViewerEvent, initialViewerState);
  useEffect(() => client.subscribe(dispatch), [client]);
  useEffect(() => () => { void client.dispose?.(); }, [client]);
  const stableState = useMemo(() => state, [state]);
  return (
    <ViewerClientContext.Provider value={client}>
      <ViewerStateContext.Provider value={stableState}>{children}</ViewerStateContext.Provider>
    </ViewerClientContext.Provider>
  );
}

export function useViewerClient() {
  const client = useContext(ViewerClientContext);
  if (!client) throw new Error("useViewerClient must be used inside ViewerClientProvider");
  return client;
}

export function useViewerState() {
  return useContext(ViewerStateContext);
}
