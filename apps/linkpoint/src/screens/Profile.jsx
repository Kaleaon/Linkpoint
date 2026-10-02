import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";

export default function Profile() {
  const { V, t } = useTheme();
  const user = app.auth.user;
  if (!user) return <div className="honest-empty"><Icon name="user" size={30} /><h2>Profile</h2><p>Connect to a grid to view your resident profile.</p></div>;
  return <div className="tool-page"><article className="runtime-card" style={{ borderColor: V.outv, background: V.surf }}>
    <Icon name="user" size={34} style={{ color: V.pri }} />
    <h2 style={{ font: `600 20px/1.3 ${t.dfont}` }}>{user.fullName}</h2>
    <dl><dt>Grid</dt><dd>{user.grid}</dd><dt>Agent ID</dt><dd>{user.id || "Not supplied"}</dd></dl>
  </article></div>;
}
