import { useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import LinkpointLogo from "../components/LinkpointLogo.jsx";

export default function Login() {
  const { state, actions } = useApp();
  const { V, t } = useTheme();
  const [username, setUsername] = useState(app.auth.credentials?.username || "");
  const [password, setPassword] = useState("");
  const [start, setStart] = useState("last");
  const [remember, setRemember] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const grids = actions.allGrids();

  const submit = async (event) => {
    event.preventDefault();
    if (!username.trim() || !password) return setError("Enter both an avatar name and password.");
    const grid = grids.find((item) => item.key === state.loginGrid);
    if (!grid) return setError("Select a valid grid.");
    setBusy(true);
    setError("");
    try {
      if (grid.key.startsWith("custom-") && window.linkpointDesktop?.allowLoginEndpoint) {
        await window.linkpointDesktop.allowLoginEndpoint(grid.host);
      }
      await app.auth.login(grid.key.startsWith("custom-") ? grid.host : grid.key, username.trim(), password, remember, start);
      setPassword("");
      actions.setScreen("Chat");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Login failed. Check the grid, credentials, and proxy configuration.");
    } finally {
      setBusy(false);
    }
  };

  const control = { width: "100%", minHeight: 44, boxSizing: "border-box", padding: "0 11px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.bg, color: V.ink, font: `400 16px/1.3 ${t.font}` };
  const label = { font: `600 10px/1 ${t.font}`, letterSpacing: ".16em", color: V.pri };

  return (
    <div style={{ flex: 1, overflowY: "auto", padding: 20 }}>
      <form onSubmit={submit} style={{ maxWidth: 440, margin: "0 auto", display: "grid", gap: 14 }}>
        <header style={{ textAlign: "center" }}>
          <LinkpointLogo width="min(100%, 240px)" height="auto" />
          <h1 style={{ margin: "-20px 0 0", color: V.pri, font: `700 25px/1.2 ${t.dfont}` }}>YOUR WORLD, EVERYWHERE</h1>
          <p style={{ color: V.ink2 }}>A modern, independent viewer for Second Life and OpenSimulator.</p>
        </header>
        {error && <div role="alert" style={{ color: V.err, border: `1px solid ${V.err}`, padding: 10 }}>{error}</div>}
        {state.toast && <div role="status" style={{ color: V.info, border: `1px solid ${V.info}`, padding: 10 }}>{state.toast}</div>}
        <label style={label}>GRID<select aria-label="Grid" value={state.loginGrid} onChange={(e) => actions.setLoginGrid(e.target.value)} style={{ ...control, display: "block", marginTop: 6 }}>{grids.map((g) => <option key={g.key} value={g.key}>{g.label}</option>)}</select></label>
        {!state.addGrid && <button type="button" onClick={actions.openAddGrid} style={{ ...control, cursor: "pointer", color: V.pri }}>ADD OPENSIM GRID</button>}
        {state.addGrid && <fieldset style={{ border: `1px solid ${V.outv}`, padding: 10, display: "grid", gap: 8 }}><legend style={label}>CUSTOM GRID</legend><input aria-label="Custom grid name" placeholder="Grid name" value={state.addGridName} onChange={(e) => actions.setAddGridName(e.target.value)} style={control} /><input aria-label="Custom grid login URI" placeholder="https://login.example.org/" value={state.addGridHost} onChange={(e) => actions.setAddGridHost(e.target.value)} style={control} /><div style={{ display: "flex", gap: 8 }}><button type="button" onClick={actions.cancelAddGrid}>Cancel</button><button type="button" onClick={actions.saveCustomGrid}>Save grid</button></div></fieldset>}
        <label style={label}>AVATAR NAME<input aria-label="Avatar name" autoComplete="username" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="First Last or username" style={{ ...control, display: "block", marginTop: 6 }} /></label>
        <label style={label}>PASSWORD<input aria-label="Password" type="password" autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} style={{ ...control, display: "block", marginTop: 6 }} /></label>
        <label style={label}>START AT<select aria-label="Start location" value={start} onChange={(e) => setStart(e.target.value)} style={{ ...control, display: "block", marginTop: 6 }}><option value="last">Last location</option><option value="home">Home</option></select></label>
        <label style={{ color: V.ink2, font: `400 12px/1.4 ${t.font}` }}><input type="checkbox" checked={remember} onChange={(e) => setRemember(e.target.checked)} /> Remember avatar name and grid (password is never stored)</label>
        <button type="submit" disabled={busy} style={{ minHeight: 48, border: 0, borderRadius: V.rs, background: V.pri, color: V.onpri, fontWeight: 700, cursor: busy ? "wait" : "pointer" }}>{busy ? "CONNECTING…" : "LOG IN"}</button>
      </form>
    </div>
  );
}
