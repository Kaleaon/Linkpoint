import { useEffect } from "react";
import { AppProvider, useApp } from "./context/AppContext.jsx";
import { ThemeProvider, useTheme } from "./context/ThemeContext.jsx";
import { AnnouncerProvider } from "./context/AnnouncerContext.jsx";
import Shell from "./components/Shell.jsx";
import SystemDialog from "./components/SystemDialog.jsx";
import Toast from "./components/Toast.jsx";
import ErrorBoundary from "./components/ErrorBoundary.jsx";
import type { ViewerClient } from "@linkpoint/viewer-client";
import { ViewerClientProvider } from "./viewer/ViewerClientContext";

function Viewer() {
  const { state, actions } = useApp();
  const { V, t } = useTheme();

  useEffect(() => {
    const selectLayout = () => {
      const width = window.innerWidth;
      actions.setDevice(width >= 1180 ? "desk" : width >= 760 ? "tab" : width >= 400 ? "and" : "ios");
    };
    selectLayout();
    window.addEventListener("resize", selectLayout);
    return () => window.removeEventListener("resize", selectLayout);
  }, [actions.setDevice]);

  return (
    <main
      className={`viewer-app layout-${state.layout} palette-${state.palette}`}
      style={{
        background: V.bg,
        color: V.ink,
        fontFamily: t.font,
      }}
    >
      <div className="viewer-workspace">
        <Shell />
        <SystemDialog />
        <Toast />
      </div>
    </main>
  );
}

export default function App({ client }: { client: ViewerClient }) {
  return (
    <ErrorBoundary label="Linkpoint">
      <AnnouncerProvider>
        <ViewerClientProvider client={client}>
          <AppProvider>
            <ThemeProvider>
              <Viewer />
            </ThemeProvider>
          </AppProvider>
        </ViewerClientProvider>
      </AnnouncerProvider>
    </ErrorBoundary>
  );
}
