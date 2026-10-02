import { useEffect, useState } from "react";
import { app } from "../linkpoint/app.ts";

// Whether the user has switched on the optional Google integration in Settings.
export default function useGoogleEnabled() {
  const [enabled, setEnabled] = useState(() => app.preferences.isGoogleEnabled());
  useEffect(() => {
    const onChange = ({ category, key }) => { if (category === "integrations" && key === "google") setEnabled(app.preferences.isGoogleEnabled()); };
    app.preferences.on("preference_changed", onChange);
    setEnabled(app.preferences.isGoogleEnabled());
    return () => app.preferences.off("preference_changed", onChange);
  }, []);
  return enabled;
}
