import { useRef, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { EDITABLE_THEME_TOKENS } from "../theme/customTheme.js";

export default function ThemeStudio() {
  const { state, actions } = useApp();
  const [open, setOpen] = useState(false);
  const fileRef = useRef(null);
  const theme = state.customTheme;

  const importFile = async (event) => {
    const file = event.target.files?.[0];
    if (file) await actions.importTheme(await file.text());
    event.target.value = "";
  };

  return (
    <div className="theme-studio">
      <button className="theme-studio-trigger" onClick={() => setOpen(!open)} aria-expanded={open}>
        <span className="theme-studio-icon">◈</span>
        <span><strong>THEME STUDIO</strong><small>{theme ? theme.name : "Make this palette your own"}</small></span>
        <span aria-hidden="true">{open ? "−" : "+"}</span>
      </button>
      {open && (
        <div className="theme-studio-body">
          <label className="theme-name">THEME NAME
            <input value={theme.name} maxLength={48} onChange={(e) => actions.renameTheme(e.target.value)} />
          </label>
          <div className="theme-colors">
            {EDITABLE_THEME_TOKENS.map(([key, label]) => (
              <label className="color-field" key={key}>
                <span>{label}</span>
                <span className="color-control">
                  <input type="color" value={theme.colors[key]} onChange={(e) => actions.setThemeColor(key, e.target.value)} aria-label={`${label} color`} />
                  <code>{theme.colors[key].toUpperCase()}</code>
                </span>
              </label>
            ))}
          </div>
          <div className="theme-actions">
            <button onClick={actions.saveTheme}>SAVE TO DEVICE</button>
            <button onClick={actions.shareTheme}>COPY SHARE LINK</button>
            <button onClick={actions.downloadTheme}>EXPORT JSON</button>
            <button onClick={() => fileRef.current?.click()}>IMPORT</button>
            <button className="theme-reset" onClick={actions.resetTheme}>RESET</button>
          </div>
          <input ref={fileRef} className="visually-hidden" type="file" accept="application/json,.json" onChange={importFile} />
          <p className="theme-hint">Changes preview instantly. Save keeps them in this browser; a link or JSON file can be shared with anyone.</p>
        </div>
      )}
    </div>
  );
}
