import { createContext, useContext, useMemo } from "react";
import { useApp } from "./AppContext.jsx";
import { computeTheme } from "../theme/computeTheme.js";

const ThemeContext = createContext(null);

export function ThemeProvider({ children }) {
  const { state, actions } = useApp();
  const theme = useMemo(() => computeTheme(state, actions.cf), [state, actions.cf]);
  return <ThemeContext.Provider value={theme}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error("useTheme must be used inside <ThemeProvider>");
  return ctx;
}
