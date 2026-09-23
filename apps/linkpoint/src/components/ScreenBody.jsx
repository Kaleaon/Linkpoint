import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import Header from "./Header.jsx";
import Icon from "./Icon.jsx";
import Chat from "../screens/Chat.jsx";
import Inventory from "../screens/Inventory.jsx";
import World3D from "../screens/World3D.jsx";
import Login from "../screens/Login.jsx";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES } from "../theme/palettes.js";
import { FriendsScreen, GenericInventoryScreen, GroupsScreen, MapScreen, MuteListScreen, NoticesScreen, RadarScreen, SearchScreen } from "../screens/LiveScreens.jsx";
import { AccountsScreen, DiagnosticsScreen, GridsScreen, MediaScreen, NotecardsScreen, ParcelScreen, TeleportScreen, TransactionsScreen } from "../screens/LumiyaTools.jsx";

export default function ScreenBody() {
  const { state } = useApp();
  const { scr } = useTheme();
  return (
    <div style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column" }}>
      {scr !== "Login" ? <Header /> : null}
      {scr === "Login" ? <Login /> : null}
      {scr === "Chat" ? <Chat /> : null}
      {scr === "Inventory" ? <Inventory /> : null}
      {scr === "Friends" ? <FriendsScreen /> : null}
      {scr === "Radar" ? <RadarScreen /> : null}
      {scr === "Map" ? <MapScreen /> : null}
      {scr === "3D View" ? <World3D /> : null}
      {scr === "Groups" ? <GroupsScreen /> : null}
      {scr === "Notices" ? <NoticesScreen /> : null}
      {scr === "Mute List" ? <MuteListScreen /> : null}
      {scr === "Outfits" ? <GenericInventoryScreen kind="wearable" /> : null}
      {scr === "Objects" ? <GenericInventoryScreen kind="object" /> : null}
      {scr === "Teleport" ? <TeleportScreen /> : null}
      {scr === "Parcel" ? <ParcelScreen /> : null}
      {scr === "Transactions" ? <TransactionsScreen /> : null}
      {scr === "Notecards" ? <NotecardsScreen /> : null}
      {scr === "Media" ? <MediaScreen /> : null}
      {scr === "Accounts" ? <AccountsScreen /> : null}
      {scr === "Grids" ? <GridsScreen /> : null}
      {scr === "Diagnostics" ? <DiagnosticsScreen /> : null}
      {scr === "Profile" ? <Profile /> : null}
      {scr === "Settings" ? <Settings /> : null}
      {scr === "Search" ? <SearchScreen /> : null}
    </div>
  );
}

function Profile() {
  const user = app.auth.user;
  return user ? (
    <div className="honest-empty"><Icon name="user" size={34} /><h2>{user.fullName}</h2><p>{user.grid}</p></div>
  ) : <Unavailable icon="user" title="Profile" message="Connect to a grid to view your profile." />;
}

function Settings() {
  const { state, actions } = useApp();
  const notificationsEnabled = app.preferences.get("notifications", "enabled") !== false;
  const logout = async () => {
    if (app.auth.isLoggedIn()) await app.auth.logout();
    actions.setScreen("Login");
  };
  return (
    <div className="settings-screen">
      <section><h2>Layout</h2><div className="choice-grid">{Object.entries(LAYOUTS).map(([key, item]) => <button className={state.layout === key ? "selected" : ""} key={key} onClick={() => actions.setLayout(key)}>{item.name}</button>)}</div></section>
      <section><h2>Colour</h2><div className="choice-grid">{Object.entries(PALETTES).map(([key, item]) => <button className={state.palette === key ? "selected" : ""} key={key} onClick={() => actions.setPalette(key)}>{item.name}</button>)}</div></section>
      <section><h2>Notifications</h2><label className="setting-toggle"><input type="checkbox" defaultChecked={notificationsEnabled} onChange={(event) => app.preferences.set("notifications", "enabled", event.target.checked)} /> Enable viewer notifications</label></section>
      <section><button className="danger-action" onClick={() => void logout()}>{app.auth.isLoggedIn() ? "Log out" : "Return to login"}</button></section>
    </div>
  );
}

function Unavailable({ icon, title, message }) {
  return <div className="honest-empty"><Icon name={icon} size={30} /><h2>{title}</h2><p>{message}</p></div>;
}
