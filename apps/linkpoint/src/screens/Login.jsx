import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import "../i18n.js";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import Icon from "../components/Icon.jsx";
import StartLocationCombobox, { saveRecentLocation } from "../components/StartLocationCombobox.jsx";
import slActions from "../../core/sl-actions.cjs";

export default function Login() {
  const { t } = useTranslation();
  const { state, actions } = useApp();
  const { V, t: typography } = useTheme();
  const [username, setUsername] = useState(app.auth.credentials?.username || "");
  const [password, setPassword] = useState("");
  const [start, setStart] = useState("last");
  const [remember, setRemember] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  // Set when the grid asks for a multi-factor code (reason "mfa_challenge").
  const [mfa, setMfa] = useState({ needed: false, token: "", message: "" });
  const [autoLogin, setAutoLogin] = useState(null);
  const grids = actions.allGrids().filter((grid) => !["offline", "gemini"].includes(grid.key));

  useEffect(() => {
    const saved = app.auth.credentials;
    if (saved?.username) setUsername(saved.username);
    const savedGrid = grids.find((grid) => grid.key === saved?.grid || grid.host === saved?.grid);
    if (savedGrid) actions.setLoginGrid(savedGrid.key);
    let active = true;
    void app.protocol.checkAutoLoginStatus?.().then((status) => {
      if (active && status?.available) setAutoLogin(status);
    }).catch(() => {});
    return () => { active = false; };
  }, []);

  const connect = async (event) => {
    event.preventDefault();
    if (!username.trim() || !password) { setError(t("login_error_empty_credentials")); return; }
    const grid = grids.find((item) => item.key === state.loginGrid);
    if (!grid) { setError(t("login_error_invalid_grid")); return; }

    try {
      const normalizeStart = slActions?.normalizeStart || slActions?.default?.normalizeStart;
      if (typeof normalizeStart === "function") {
        normalizeStart(start);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : t("login_error_rejected"));
      return;
    }

    setBusy(true); setError("");
    try {
      if (grid.key.startsWith("custom-") && window.linkpointDesktop?.allowLoginEndpoint) await window.linkpointDesktop.allowLoginEndpoint(grid.host);
      await app.auth.login(grid.key.startsWith("custom-") ? grid.host : grid.key, username.trim(), password, remember, start, mfa.token.trim());
      saveRecentLocation(start);
      setPassword(""); setMfa({ needed: false, token: "", message: "" }); actions.setScreen("Chat");
    } catch (reason) {
      const details = reason?.details;
      if (details?.mfaRequired) {
        // Keep the name and password so the resident only has to type the code.
        const rejected = details.reason === "mfa_failure" || Boolean(mfa.token);
        setMfa({ needed: true, token: "", message: details.message });
        setError(rejected ? details.message : "");
      } else {
        setError(reason instanceof Error ? reason.message : t("login_error_rejected"));
      }
    } finally { setBusy(false); }
  };

  const connectSavedSession = async () => {
    try {
      const normalizeStart = slActions?.normalizeStart || slActions?.default?.normalizeStart;
      if (typeof normalizeStart === "function") {
        normalizeStart(start);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : t("login_error_rejected"));
      return;
    }

    setBusy(true); setError("");
    try {
      await app.auth.autoLogin(start);
      saveRecentLocation(start);
      actions.setScreen("Chat");
    }
    catch (reason) { setError(reason instanceof Error ? reason.message : t("login_error_saved_credentials")); }
    finally { setBusy(false); }
  };

  const control = { width: "100%", minHeight: 44, boxSizing: "border-box", padding: "0 11px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.bg, color: V.ink, font: `400 15px/1.3 ${typography.font}` };
  const label = { font: `600 10px/1 ${typography.font}`, letterSpacing: ".16em", color: V.pri };

  return <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "24px 16px" }}>
    <form onSubmit={connect} style={{ maxWidth: 460, margin: "0 auto", display: "grid", gap: 15 }}>
      <header style={{ textAlign: "center" }}><Icon name="box" size={28} /><h1 style={{ color: V.pri, font: `700 28px/1.1 ${typography.dfont}`, letterSpacing: ".12em" }}>{t("app_name").toUpperCase()}</h1><p style={{ color: V.ink2 }}>{t("login_header_subtitle")}</p></header>
      <label style={label}>{t("login_label_grid")}<select value={state.loginGrid} onChange={(event) => actions.setLoginGrid(event.target.value)} style={{ ...control, display: "block", marginTop: 6 }}>{grids.map((grid) => <option key={grid.key} value={grid.key}>{grid.label}</option>)}</select></label>
      <label style={label}>{t("login_label_avatar_name")}<input autoComplete="username" value={username} onChange={(event) => setUsername(event.target.value)} placeholder={t("login_placeholder_avatar_name")} style={{ ...control, display: "block", marginTop: 6 }} /></label>
      <label style={label}>{t("login_label_password")}<input type="password" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} placeholder={t("login_placeholder_password")} style={{ ...control, display: "block", marginTop: 6 }} /></label>
      {mfa.needed ? <label style={label}>{t("login_label_mfa")}<input autoFocus inputMode="numeric" autoComplete="one-time-code" maxLength={12} value={mfa.token} onChange={(event) => setMfa({ ...mfa, token: event.target.value })} placeholder={t("login_placeholder_mfa")} aria-describedby="mfa-help" style={{ ...control, display: "block", marginTop: 6, letterSpacing: ".3em" }} /><small id="mfa-help" style={{ display: "block", marginTop: 6, color: V.ink2, font: `400 11px/1.4 ${typography.font}`, letterSpacing: 0 }}>{mfa.message}</small></label> : null}
      <label style={label}>{t("login_label_start_location")}<StartLocationCombobox value={start} onChange={setStart} /></label>
      <label style={{ color: V.ink2, fontSize: 12 }}><input type="checkbox" checked={remember} onChange={(event) => setRemember(event.target.checked)} /> {t("login_remember_me")}</label>
      {error ? <div role="alert" style={{ color: V.err }}>{error}</div> : null}
      <button type="submit" disabled={busy} style={{ minHeight: 48, border: 0, borderRadius: V.rs, background: V.pri, color: V.onpri, fontWeight: 700 }}>{busy ? t("login_button_connecting") : mfa.needed ? t("login_button_mfa") : t("login_button_connect")}</button>
      {autoLogin ? <button type="button" disabled={busy} onClick={() => void connectSavedSession()} style={{ minHeight: 44, border: `1px solid ${V.pri}`, borderRadius: V.rs, background: V.priC, color: V.pri }}>{t("login_button_server_credentials")}{autoLogin.username ? ` · ${autoLogin.username}` : ""}</button> : null}
      <button type="button" onClick={actions.openAddGrid} style={{ minHeight: 40, border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.ink }}>{t("login_button_add_custom_grid")}</button>
      {state.addGrid ? <section className="runtime-card"><label style={label}>{t("login_label_grid_name")}<input value={state.addGridName} onChange={(e) => actions.setAddGridName(e.target.value)} style={{ ...control, display: "block", margin: "6px 0 10px" }} /></label><label style={label}>{t("login_label_login_uri")}<input value={state.addGridHost} onChange={(e) => actions.setAddGridHost(e.target.value)} placeholder={t("login_placeholder_login_uri")} style={{ ...control, display: "block", margin: "6px 0 10px" }} /></label><button type="button" onClick={actions.saveCustomGrid}>{t("login_button_save_grid")}</button> <button type="button" onClick={actions.cancelAddGrid}>{t("login_button_cancel")}</button></section> : null}
    </form>
  </div>;
}

