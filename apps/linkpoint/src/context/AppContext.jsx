import { createContext, useContext } from "react";
import { useAppState } from "../hooks/useAppState.js";

const AppContext = createContext(null);

export function AppProvider({ children }) {
  const value = useAppState();
  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useApp() {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error("useApp must be used inside <AppProvider>");
  return ctx;
}
